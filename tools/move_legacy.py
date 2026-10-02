#!/usr/bin/env python3
"""Перенос файлов автора из legacy/ в src/ одним `git mv` (CLAUDE.md, правило 3).

Код ложится по тому же пути:
    legacy/src/main/java/<путь>                 → src/main/java/<путь>
    legacy/asjcore/src/main/java/<путь>         → src/main/java/<путь>
Ресурсы — под именами 1.20.1 (MAPPING.md, «Ресурсы»): каждая часть пути в snake_case, расширение строчными,
как `legacyPath` в alfheim.port.legacy и имена в реестре:
    legacy/src/main/resources/assets/alfheim/textures/blocks/ElvenSand.png
                                                → src/main/resources/assets/alfheim/textures/blocks/elven_sand.png

Запуск из корня репозитория: python3 tools/move_legacy.py <файлы в legacy/…>
Содержимое файлов не меняется: коммит с переносом — только `git mv`.
"""
import os
import re
import subprocess
import sys

CODE_ROOTS = ["legacy/src/main/java/", "legacy/asjcore/src/main/java/"]
ASSETS_ROOT = "legacy/src/main/resources/assets/"


def snake_case(name):
    """Как AlfheimRegisters.snakeCase"""
    name = re.sub(r"([a-z0-9])([A-Z])", r"\1_\2", name)
    name = re.sub(r"([A-Z]+)([A-Z][a-z])", r"\1_\2", name)
    return name.lower()


def legacy_path(path):
    """Как legacyPath в alfheim.port.legacy"""
    parts = []
    for part in path.split("/"):
        dot = part.rfind(".")
        parts.append(snake_case(part[:dot]) + part[dot:].lower() if dot > 0 else snake_case(part))
    return "/".join(parts)


def target(path):
    for root in CODE_ROOTS:
        if path.startswith(root):
            return "src/main/java/" + path[len(root):]
    if path.startswith(ASSETS_ROOT):
        namespace, rest = path[len(ASSETS_ROOT):].split("/", 1)
        return "src/main/resources/assets/%s/%s" % (namespace, legacy_path(rest))
    raise SystemExit("не знаю, куда переносить %s" % path)


def main():
    if len(sys.argv) < 2:
        print(__doc__)
        return 1
    for path in sys.argv[1:]:
        dst = target(path)
        if os.path.exists(dst):
            raise SystemExit("%s уже существует" % dst)
        os.makedirs(os.path.dirname(dst), exist_ok=True)
        subprocess.run(["git", "mv", path, dst], check=True)
        print("%s → %s" % (path, dst))
    return 0


if __name__ == "__main__":
    sys.exit(main())
