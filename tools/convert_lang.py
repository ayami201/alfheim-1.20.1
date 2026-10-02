#!/usr/bin/env python3
"""Переводы автора: .lang (1.7.10) → .json (1.20.1) (ROADMAP, КТ-1).

Читает legacy/src/main/resources/assets/<alfheim|botania>/lang/<en_US|ru_RU|zh_CN>.lang и пишет
src/main/resources/assets/<…>/lang/<en_us|ru_ru|zh_cn>.json.

Разбор — как у LanguageMap 1.7.10: пустые строки и строки с «#» в начале пропускаются, строка делится
по первому «=», строка без «=» пропускается, ключ и значение не обрезаются, у повторного ключа
остаётся последнее значение. Значения переносятся как есть: числовые форматы (%d, %.1f) и 1.7.10, и
1.20.1 при загрузке сами заменяют на %s.

Ключи:
- переименовываются по разделу "lang" файла src/main/resources/alfheim/legacy_ids.json (SPEC, Р-5);
- не переносятся ключи из REMOVED ниже — вещей, которых в порту нет.

Запуск из корня репозитория:
    python3 tools/convert_lang.py           записать .json
    python3 tools/convert_lang.py --check   сверить .json с .lang (CI): число ключей = ключи автора
                                            минус удалённые, переименования применены, файлы не правлены руками
"""
import fnmatch
import json
import os
import sys

NAMESPACES = ["alfheim", "botania"]
LANGS = ["en_US", "ru_RU", "zh_CN"]
SRC = "legacy/src/main/resources/assets/%s/lang/%s.lang"
DST = "src/main/resources/assets/%s/lang/%s.json"
LEGACY_IDS = "src/main/resources/alfheim/legacy_ids.json"

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
]


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
        if key in renames:
            renamed.append(key)
            key = renames[key]
        out[key] = value
    return out, removed, renamed


def dump(data):
    return json.dumps(data, ensure_ascii=False, indent="\t") + "\n"


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
                if len(json.loads(current)) != len(entries) - len(removed):
                    errors.append("%s: ключей %d, ожидалось %d" % (dst, len(json.loads(current)), len(entries) - len(removed)))
            else:
                os.makedirs(os.path.dirname(dst), exist_ok=True)
                with open(dst, "w", encoding="utf-8") as f:
                    f.write(text)
            print("%-45s у автора %4d, удалено %3d, переименовано %3d, в .json %4d" % (dst, len(entries), len(removed), len(renamed), len(out)))
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
