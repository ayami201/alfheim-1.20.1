#!/usr/bin/env python3
"""Проверка миксинов в собранном jar (HOOKS.md, «Как переносить врезку»).

В игре имена методов Minecraft — SRG (`m_21023_`), а миксины порта написаны с официальными (`hasEffect`).
Переводит их карта ссылок `alfheim.refmap.json`, которую строит процессор аннотаций Mixin при сборке и
кладёт в jar. В среде разработки она не нужна (там официальные имена), поэтому GameTest-ы её отсутствие
не замечают, а в игре без неё миксин не найдёт метод и игра упадёт при запуске.

Проверяет, что в jar:
1. есть конфиг миксинов `alfheim.mixins.json` со ссылкой на карту;
2. есть сама карта, и у каждого миксина из конфига в ней есть хотя бы одна запись.

Запуск: python3 tools/check_mixins.py [путь к jar]; без пути — jar мода в build/libs/
"""
import glob
import json
import sys
import zipfile

CONFIG = "alfheim.mixins.json"


def find_jar():
    jars = [j for j in glob.glob("build/libs/*.jar") if not j.endswith(("-sources.jar", "-slim.jar"))]
    if len(jars) != 1:
        sys.exit(f"ОШИБКА: ожидался один jar мода в build/libs/, найдено: {jars}")
    return jars[0]


def main():
    path = sys.argv[1] if len(sys.argv) > 1 else find_jar()
    errors = []
    with zipfile.ZipFile(path) as jar:
        names = set(jar.namelist())
        if CONFIG not in names:
            sys.exit(f"ОШИБКА: в {path} нет {CONFIG}")
        config = json.loads(jar.read(CONFIG))
        package = config["package"].replace(".", "/")
        mixins = [m for side in ("mixins", "client", "server") for m in config.get(side, [])]
        refmap_name = config.get("refmap")
        if mixins and not refmap_name:
            errors.append(f"в {CONFIG} нет строки \"refmap\"")
        elif mixins and refmap_name not in names:
            errors.append(f"в jar нет карты ссылок {refmap_name}: процессор аннотаций Mixin её не построил")
        elif mixins:
            mappings = json.loads(jar.read(refmap_name)).get("mappings", {})
            for mixin in mixins:
                key = f"{package}/{mixin.replace('.', '/')}"
                if not mappings.get(key):
                    errors.append(f"в {refmap_name} нет записей миксина {key}")
        print(f"{path}: миксинов {len(mixins)}, карта ссылок {refmap_name or '—'}")

    if errors:
        print(f"\nОШИБКИ ({len(errors)}):")
        for e in errors:
            print("  " + e)
        sys.exit(1)
    print("OK: у каждого миксина есть записи в карте ссылок")


if __name__ == "__main__":
    main()
