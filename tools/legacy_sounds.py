#!/usr/bin/env python3
"""Таблица звуков 1.7.10 → 1.20.1 для прослойки порта (SPEC, Р-4; MAPPING.md, «Ресурсы»).

Автор проигрывает звуки по имени 1.7.10: "random.fizz", "botania:enchanterBlock". В 1.20.1 звуки ванилы и
Botania переименованы, а громкость выбирает категория (SoundSource), которую в 1.7.10 задавал sounds.json.

Скрипт собирает имена звуков ванилы и Botania из кода автора (строки в legacy/ и src/) и для каждого находит
событие 1.20.1, которое играет те же аудиофайлы, — игрок слышит тот же звук. Категория берётся из sounds.json
1.7.10. Если те же файлы играют несколько событий, выбирается то, чьё имя ближе по смыслу; ничьи решает
OVERRIDES ниже. Звуки Alfheim в таблицу не входят: их события — те же имена (AlfheimSounds).

Источники:
- sounds.json ванилы 1.7.10 и 1.20.1 — с серверов Mojang (как у лаунчера), кэш в build/legacy_sounds/;
- sounds.json Botania r1.8-249 — legacy/libs/, Botania 1.20.1-456 — из кэша Gradle (после ./gradlew build).

Запуск из корня репозитория:
    python3 tools/legacy_sounds.py           записать src/main/resources/alfheim/legacy_sounds.json
    python3 tools/legacy_sounds.py --check   сверить файл с тем, что получилось бы сейчас
"""
import glob
import json
import os
import re
import sys
import urllib.request
import zipfile

OUT = "src/main/resources/alfheim/legacy_sounds.json"
CACHE = "build/legacy_sounds"
MANIFEST = "https://piston-meta.mojang.com/mc/game/version_manifest_v2.json"
BOTANIA_OLD = "legacy/libs/Botania r1.8-249-deobf-src.jar"
BOTANIA_NEW = os.path.expanduser("~/.gradle/caches/modules-2/files-2.1/vazkii.botania/Botania/1.20.1-456-FORGE/*/Botania-1.20.1-456-FORGE.jar")
CODE = ["legacy/src/main/java", "src/main/java"]

# Ничьи: те же файлы играют несколько событий 1.20.1, по имени не различить. Имя 1.7.10 → событие и почему
OVERRIDES = {
    "botania:enchanterBlock": ("botania:enchanter_form", "в Botania 1.7.10 звучал, когда собирался зачарователь"),
    "dig.grass": ("minecraft:block.grass.break", "dig — звук ломания блока; у автора — смерть мухоловки"),
    "mob.zombie.step": ("minecraft:entity.zombie.step", "шаги зомби; у автора — шаги ледяного викинга"),
    "random.bow": ("minecraft:entity.arrow.shoot", "выстрел из лука; у автора — луки, чакрам и выстрелы викинга"),
    "random.click": ("minecraft:block.stone_button.click_on", "щелчок кнопки; у автора — кнопка Гайи и резонатор"),
    "random.explode": ("minecraft:entity.generic.explode", "обычный взрыв"),
    "random.fizz": ("minecraft:block.fire.extinguish", "шипение огня или лавы — так random.fizz использовала ванила 1.7.10"),
    "random.pop": ("minecraft:entity.item.pickup", "подбор предмета; у автора — подбор неуничтожимого предмета"),
}


def fetch(url, name):
    os.makedirs(CACHE, exist_ok=True)
    path = os.path.join(CACHE, name)
    if not os.path.exists(path):
        with urllib.request.urlopen(url, timeout=60) as r, open(path, "wb") as f:
            f.write(r.read())
    return json.load(open(path, encoding="utf-8"))


def vanilla_sounds(version):
    manifest = fetch(MANIFEST, "version_manifest_v2.json")
    v = next(x for x in manifest["versions"] if x["id"] == version)
    index = fetch(fetch(v["url"], version + ".json")["assetIndex"]["url"], version + "-index.json")
    h = index["objects"]["minecraft/sounds.json"]["hash"]
    return fetch("https://resources.download.minecraft.net/%s/%s" % (h[:2], h), version + "-sounds.json")


def jar_json(path, entry):
    with zipfile.ZipFile(path) as z:
        return json.loads(z.read(entry))


def files(event):
    """Аудиофайлы события без пространства имён, строчными: в Botania 1.20.1 файлы переименованы в строчные"""
    out = set()
    for s in event["sounds"]:
        name = s if isinstance(s, str) else s["name"]
        if isinstance(s, dict) and s.get("type") == "event":
            continue
        out.add(name.split(":")[-1].lower())
    return frozenset(out)


def tokens(name):
    name = re.sub(r"([a-z])([A-Z])", r"\1 \2", name.split(":")[-1])
    return [t.lower() for t in re.split(r"[ ._/]+", name) if t]


def similarity(old, new):
    """Сколько слов старого имени есть в новом (слово совпадает, если общее начало от 4 букв)"""
    nt = tokens(new)
    return sum(1 for o in tokens(old) if any(o == n or (len(o) >= 4 and len(n) >= 4 and o[:4] == n[:4]) for n in nt))


def used_names(old_vanilla, old_botania):
    names = set()
    for root in CODE:
        for d, _, fs in os.walk(root):
            for f in fs:
                if not f.endswith((".kt", ".java")):
                    continue
                for s in re.findall(r'"([A-Za-z0-9_.:]+)"', open(os.path.join(d, f), encoding="utf-8", errors="replace").read()):
                    if s in old_vanilla or s.startswith("minecraft:") and s[10:] in old_vanilla:
                        names.add(s.removeprefix("minecraft:"))
                    elif s.startswith("botania:") and s[8:] in old_botania:
                        names.add(s)
    return sorted(names)


def build():
    old_vanilla, new_vanilla = vanilla_sounds("1.7.10"), vanilla_sounds("1.20.1")
    old_botania = jar_json(BOTANIA_OLD, "assets/botania/sounds.json")
    jars = glob.glob(BOTANIA_NEW)
    if not jars:
        sys.exit("нет Botania 1.20.1 в кэше Gradle: сначала ./gradlew build")
    new_botania = jar_json(jars[0], "assets/botania/sounds.json")

    table, report, errors = {}, [], []
    for name in used_names(old_vanilla, old_botania):
        if name.startswith("botania:"):
            old, new, ns = old_botania[name[8:]], new_botania, "botania"
        else:
            old, new, ns = old_vanilla[name], new_vanilla, "minecraft"
        want = files(old)
        candidates = sorted(k for k, e in new.items() if files(e) == want)
        if not candidates:
            candidates = sorted(k for k, e in new.items() if files(e) & want)
            if candidates:
                report.append("%s: файлы совпадают частично — %s" % (name, ", ".join(candidates)))
        if name in OVERRIDES:
            event = OVERRIDES[name][0]
        elif not candidates:
            errors.append("%s: в 1.20.1 нет события с файлами %s" % (name, sorted(want)))
            continue
        else:
            best = max(similarity(name, c) for c in candidates)
            top = [c for c in candidates if similarity(name, c) == best]
            if len(top) > 1:
                errors.append("%s: ничья %s — решить в OVERRIDES" % (name, ", ".join(top)))
                continue
            event = ns + ":" + top[0]
        table[name] = {"event": event, "category": old.get("category", "master")}
    return table, report, errors


def dump(table):
    data = {
        "_comment": "Звук 1.7.10 по имени → событие 1.20.1 с теми же файлами и категория автора. Собирает tools/legacy_sounds.py, руками не правится",
        "sounds": table,
    }
    return json.dumps(data, ensure_ascii=False, indent="\t") + "\n"


def main():
    table, report, errors = build()
    for line in report:
        print("  " + line)
    if errors:
        print("ОШИБКИ (%d):" % len(errors))
        for e in errors:
            print("  " + e)
        return 1
    text = dump(table)
    if "--check" in sys.argv:
        if open(OUT, encoding="utf-8").read() != text:
            print("%s не совпадает с тем, что собирает скрипт: запусти python3 tools/legacy_sounds.py" % OUT)
            return 1
        print("OK: %s совпадает (%d звуков)" % (OUT, len(table)))
        return 0
    open(OUT, "w", encoding="utf-8").write(text)
    print("%s: %d звуков" % (OUT, len(table)))
    return 0


if __name__ == "__main__":
    sys.exit(main())
