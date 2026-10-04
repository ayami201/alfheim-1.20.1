#!/usr/bin/env python3
"""Переводы автора: .lang (1.7.10) → .json (1.20.1) (ROADMAP, КТ-1).

Читает legacy/src/main/resources/assets/<alfheim|botania>/lang/<en_US|ru_RU|zh_CN>.lang и пишет
src/main/resources/assets/<…>/lang/<en_us|ru_ru|zh_cn>.json. К переводам alfheim добавляются имена блоков Botania
1.7.10, которые вернул порт (BOTANIA_1710 ниже).

Разбор — как у LanguageMap 1.7.10: пустые строки и строки с «#» в начале пропускаются, строка делится
по первому «=», строка без «=» пропускается, ключ и значение не обрезаются, у повторного ключа
остаётся последнее значение. Значения переносятся как есть: числовые форматы (%d, %.1f) и 1.7.10, и
1.20.1 при загрузке сами заменяют на %s.

Ключи:
- переименовываются по разделу "lang" файла src/generated/resources/alfheim/legacy_ids.json (SPEC, Р-5;
  его строит генерация данных). Если у вариантов было одно имя (tile.alfheim:irisWood.name), новых ключей
  список — значение получает каждый;
- не переносятся ключи из REMOVED ниже — вещей, которых в порту нет.

Имена вещей: у каждого блока и предмета из legacy_ids.json есть имя на английском и русском (ROADMAP, КТ-2),
кроме тех, у которых его не было и у автора (NO_NAME_IN_ORIGINAL ниже).

Запуск из корня репозитория:
    python3 tools/convert_lang.py           записать .json
    python3 tools/convert_lang.py --check   сверить .json с .lang (CI): число ключей = ключи автора
                                            минус удалённые плюс лишние из списков новых ключей, переименования
                                            применены, файлы не правлены руками, у каждой вещи есть имя
"""
import fnmatch
import json
import os
import sys

NAMESPACES = ["alfheim", "botania"]
LANGS = ["en_US", "ru_RU", "zh_CN"]
SRC = "legacy/src/main/resources/assets/%s/lang/%s.lang"
DST = "src/main/resources/assets/%s/lang/%s.json"
LEGACY_IDS = "src/generated/resources/alfheim/legacy_ids.json"

# Ключи, которые не переносятся: шаблон fnmatch → причина
REMOVED = [
    ("tile.AlfheimThaumOre.*", "руды Thaumcraft: интеграция выпала (SPEC п. 7)"),
    ("tc.aspect.*", "аспект Thaumcraft: интеграция выпала (SPEC п. 7)"),
    ("alfheimmisc.tgtooltip", "меню Travellers Gear: интеграция выпала (SPEC п. 7)"),
    ("general.integration.thaumcraft", "категория настроек Thaumcraft: опции удалены (MAPPING.md)"),
    ("TC.*", "подписи удалённых настроек Thaumcraft (MAPPING.md)"),
    ("general.potions", "категория номеров зелий: опции удалены (MAPPING.md)"),
    ("general.elvenstory.mmo.potions", "категория номеров зелий MMO: опции удалены (MAPPING.md)"),
    ("potionID*", "подписи удалённых номеров зелий (MAPPING.md)"),
    ("dimensionID*", "подписи удалённых номеров измерений (MAPPING.md)"),
    ("niflheimBiomeIDs", "подпись удалённой настройки (MAPPING.md)"),
    ("flagIdSheepRainbow", "подпись удалённой настройки (MAPPING.md)"),
    ("floatingIslandSyncedDataInitLimit", "подпись удалённой настройки (MAPPING.md)"),
    ("oiiaId", "подпись удалённой настройки (MAPPING.md)"),
    ("elementiumClusterMeta", "подпись удалённой настройки (MAPPING.md)"),
    ("overrideCoFHCollisionCheck", "подпись удалённой настройки (MAPPING.md)"),
    ("tile.*Slab*Full*.name", "двойная плита — состояние type=double одинарной, своего предмета и имени у неё нет (MAPPING.md)"),
    ("tile.alfheim:rainbowDouble*[89].name", "верхняя половина двойного растения — состояние half нижней, своего предмета и имени у неё нет (MAPPING.md)"),
    ("tile.alfheim:rainbowDouble*1[01].name", "верхняя половина двойного растения — состояние half нижней, своего предмета и имени у неё нет (MAPPING.md)"),
    ("tile.botania:livingrock1Wall.name", "стену автора из кирпичей живого камня заменила стена Botania 1.20.1 со своим именем (решение автора, TASKS.md)"),
]

# Имена блоков Botania r1.8-249, которые вернул порт (alfheim.port.legacy.botania.BotaniaBlocks1710): ключ → язык → текст.
# Тексты — из .lang Botania r1.8-249 (legacy/libs/Botania r1.8-249-deobf-src.jar, assets/botania/lang), автор Vazkii
BOTANIA_1710 = {
    "tile.botania:customBrick3.name": {"en_US": "Roof Tile", "ru_RU": "Черепица", "zh_CN": "瓦块"},
}

# Языки, на которых у каждой вещи должно быть имя (ROADMAP, КТ-2)
NAMED_LANGS = ["en_us", "ru_ru"]

# Ключи имён, которых нет у автора: ключ → язык → почему
NO_NAME_IN_ORIGINAL = {
}


def parse_lang(path):
    """Как LanguageMap.parseLangFile 1.7.10"""
    entries = {}
    with open(path, encoding="utf-8") as f:
        for line in f.read().splitlines():
            if not line or line[0] == "#":
                continue
            parts = line.split("=", 1)
            if len(parts) == 2:
                entries[parts[0]] = parts[1]
    return entries


def removed_reason(key):
    for pattern, reason in REMOVED:
        if fnmatch.fnmatchcase(key, pattern):
            return reason
    return None


def convert(entries, renames):
    out, removed, renamed = {}, [], []
    for key, value in entries.items():
        if removed_reason(key):
            removed.append(key)
            continue
        keys = [key]
        if key in renames:
            renamed.append(key)
            keys = renames[key] if isinstance(renames[key], list) else [renames[key]]
        for new in keys:
            out[new] = value
    return out, removed, renamed


def extra_keys(entries, renames):
    """Сколько ключей добавляют списки: старый ключ с n новыми даёт n − 1 лишних"""
    return sum(len(renames[key]) - 1 for key in entries if isinstance(renames.get(key), list) and not removed_reason(key))


def dump(data):
    return json.dumps(data, ensure_ascii=False, indent="\t") + "\n"


def name_keys():
    """Ключ имени каждого блока и предмета из legacy_ids.json: block.<ns>.<id> или item.<ns>.<id>"""
    with open(LEGACY_IDS, encoding="utf-8") as f:
        ids = json.load(f)

    def new_ids(section):
        out = []
        for value in ids[section].values():
            for new in (value.values() if isinstance(value, dict) else [value]):
                # блок другого мода вместо блока автора (стена Botania) — имя у него своё
                if new.startswith("alfheim:"):
                    out.append(new.split("[")[0])
        return out

    blocks = set(new_ids("blocks"))
    keys = set()
    for new in blocks:
        keys.add("block." + new.replace(":", "."))
    for new in new_ids("items"):
        keys.add(("block." if new in blocks else "item.") + new.replace(":", "."))
    return sorted(keys)


def check_names():
    errors = []
    for lang in NAMED_LANGS:
        with open(DST % ("alfheim", lang), encoding="utf-8") as f:
            names = json.load(f)
        for key in name_keys():
            if key not in names and lang not in NO_NAME_IN_ORIGINAL.get(key, {}):
                errors.append("%s: нет имени %s" % (lang, key))
    return errors


def main():
    check = "--check" in sys.argv
    with open(LEGACY_IDS, encoding="utf-8") as f:
        renames = json.load(f)["lang"]
    errors = []
    for ns in NAMESPACES:
        for lang in LANGS:
            src = SRC % (ns, lang)
            dst = DST % (ns, lang.lower())
            entries = parse_lang(src)
            if ns == "alfheim":
                entries.update({key: texts[lang] for key, texts in BOTANIA_1710.items() if lang in texts})
            out, removed, renamed = convert(entries, renames)
            text = dump(out)
            if check:
                if not os.path.exists(dst):
                    errors.append("нет файла %s" % dst)
                    continue
                with open(dst, encoding="utf-8") as f:
                    current = f.read()
                if current != text:
                    errors.append("%s не совпадает с %s после перевода: запусти python3 tools/convert_lang.py" % (dst, src))
                expected = len(entries) - len(removed) + extra_keys(entries, renames)
                if len(json.loads(current)) != expected:
                    errors.append("%s: ключей %d, ожидалось %d" % (dst, len(json.loads(current)), expected))
            else:
                os.makedirs(os.path.dirname(dst), exist_ok=True)
                with open(dst, "w", encoding="utf-8") as f:
                    f.write(text)
            print("%-45s у автора %4d, удалено %3d, переименовано %3d, в .json %4d" % (dst, len(entries), len(removed), len(renamed), len(out)))
    if check:
        errors += check_names()
    if errors:
        print("\nОШИБКИ (%d):" % len(errors))
        for e in errors:
            print("  " + e)
        return 1
    if check:
        print("OK: переводы сходятся с .lang автора")
    return 0


if __name__ == "__main__":
    sys.exit(main())
