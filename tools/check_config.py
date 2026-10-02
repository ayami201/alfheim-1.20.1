#!/usr/bin/env python3
"""Сверка файлов настроек порта с опциями автора (ROADMAP, КТ-1, «Готово, когда»).

Опции автора берутся из AlfheimConfigHandler.kt и AlfheimPreConfigHandler.kt на точке
отсчёта fd34c141: каждый незакомментированный вызов loadProp(КАТЕГОРИЯ, "имя", ...).
Проверяет, что после ./gradlew runGameTestServer:
1. каждая опция автора лежит в своём файле (core.cfg → core.toml, mod.cfg → mod.toml) под
   той же категорией и с тем же именем; исключения — таблица «Удалённые опции конфига» в
   docs/port/MAPPING.md и клиентские опции из ClientOptions.kt (они в client.toml, которого
   на сервере нет);
2. лишних опций в файлах нет;
3. значение в только что созданном файле — значение автора по умолчанию (начальное значение
   поля в его коде); если выражение не разобрать, опция помечается «не проверено».

Точка в имени опции (wire.overpowered) — уровень вложенности TOML: [general.wire] overpowered.

Запуск из корня репозитория: python3 tools/check_config.py [папка с файлами, по умолчанию run/config/Alfheim]
Значения по умолчанию сверяются, только если файлы созданы с нуля (в CI так и есть).
"""
import fnmatch
import os
import re
import subprocess
import sys
import tomllib

REF = "fd34c141cb9d4466735a0e034d6a2fb2401cc177"
HANDLERS = {
    "core.toml": "src/main/java/alfheim/common/core/handler/AlfheimPreConfigHandler.kt",
    "mod.toml": "src/main/java/alfheim/common/core/handler/AlfheimConfigHandler.kt",
}
CLIENT_FILE = {"mod.toml": "client.toml"}
MAPPING = "docs/port/MAPPING.md"
CLIENT_OPTIONS = "src/main/java/alfheim/port/config/ClientOptions.kt"


def author_source(path):
    out = subprocess.run(["git", "show", "%s:%s" % (REF, path)], capture_output=True, text=True)
    if out.returncode == 0:
        return out.stdout
    # мелкий клон без точки отсчёта: файл автора лежит в src/ (перенесён), закомментированные порт-строки
    # «//\t\t... = loadProp» тогда тоже опции автора
    text = open(path, encoding="utf-8").read()
    return re.sub(r"^//(\t\t\w+ = .*loadProp\()", r"\1", text, flags=re.M)


def categories(text):
    consts = {"CATEGORY_GENERAL": "general", "CATEGORY_SPLITTER": "."}
    for name, expr in re.findall(r"const val (CATEGORY_\w+) = ([^\n]+)", text):
        parts = [p.strip() for p in expr.split("+")]
        consts[name] = "".join(consts[p] if p in consts else p.strip('"') for p in parts)
    return consts


def kotlin_value(expr, fields):
    """Начальное значение поля автора → значение Python; None, если не разобрать."""
    expr = re.sub(r"\s*//.*$", "", expr).strip()
    if expr in ("true", "false"):
        return expr == "true"
    if expr == "Int.MAX_VALUE":
        return 2147483647
    if re.fullmatch(r"-?0x[0-9A-Fa-f]+", expr):
        return int(expr, 16)
    if re.fullmatch(r"-?\d+(\.toByte\(\))?", expr):
        return int(expr.replace(".toByte()", ""))
    if re.fullmatch(r"-?\d+\.\d+f?", expr):
        return float(expr.rstrip("f"))
    if re.fullmatch(r"[\d\s*+-]+", expr):
        return eval(expr)  # только цифры и арифметика
    if re.fullmatch(r'"[^"]*"', expr):
        return expr[1:-1]
    m = re.fullmatch(r"intArrayOf\(([^)]*)\)", expr)
    if m:
        return [int(x) for x in m.group(1).split(",") if x.strip()]
    m = re.fullmatch(r"arrayOf\((.*)\)", expr)
    if m:
        return re.findall(r'"([^"]*)"', m.group(1))
    if re.fullmatch(r"emptyArray(<String>)?\(\)|IntArray\(0\)", expr):
        return []
    m = re.fullmatch(r"IntArray\((\d+)\) \{ -1 \+ it \}", expr)
    if m:
        return [-1 + i for i in range(int(m.group(1)))]
    if expr in fields:
        return fields[expr]
    return None


def author_options(text):
    """{(путь категории, имя): значение по умолчанию или None}"""
    cats = categories(text)
    fields = {}
    for name, expr in re.findall(r"^\t(?:var|val|private var) (\w+)(?:: \w+)? = (.+)$", text, re.M):
        if name not in fields:
            fields[name] = kotlin_value(expr, fields)
    options = {}
    for line in text.splitlines():
        if line.strip().startswith("//"):
            continue
        m = re.search(r'loadProp\((CATEGORY_\w+), "([\w.]+)", ([^,]+(?:\(\))?),', line)
        if not m:
            continue
        cat, name, arg = cats[m.group(1)], m.group(2), m.group(3).strip()
        arg = re.sub(r"\.(D|I)$", "", arg)
        options[(cat, name)] = kotlin_value(arg, fields)
    return options


def removed_options():
    text = open(MAPPING, encoding="utf-8").read()
    section = text.split("## Удалённые опции конфига", 1)[1].split("\n## ", 1)[0]
    names = []
    for line in section.splitlines():
        if line.startswith("| `"):
            names += re.findall(r"`([^`]+)`", line.split("|")[1])
    return names


def client_options():
    text = open(CLIENT_OPTIONS, encoding="utf-8").read()
    body = text.split("private val options", 1)[1]
    return set(re.findall(r'"(\w[\w.]*)"', body.split("fun isClient", 1)[0])) - set(CLIENT_FILE) - set(CLIENT_FILE.values())


def flatten(table, prefix=()):
    for key, value in table.items():
        if isinstance(value, dict):
            yield from flatten(value, prefix + (key,))
        else:
            yield prefix + (key,), value


def same(a, b):
    if isinstance(a, float) or isinstance(b, float):
        return isinstance(a, (int, float)) and isinstance(b, (int, float)) and abs(a - b) < 1e-9
    return a == b


def main(folder):
    errors, unchecked, checked = [], [], 0
    removed = removed_options()
    client = client_options()
    total = {"автора": 0, "удалено": 0, "в клиентском файле": 0, "в файлах": 0}

    for file_name, handler in HANDLERS.items():
        options = author_options(author_source(handler))
        path = os.path.join(folder, file_name)
        if not os.path.exists(path):
            errors.append("нет файла %s" % path)
            continue
        with open(path, "rb") as f:
            values = dict(flatten(tomllib.load(f)))
        client_values = {}
        client_path = os.path.join(folder, CLIENT_FILE.get(file_name, ""))
        if file_name in CLIENT_FILE and os.path.exists(client_path):
            with open(client_path, "rb") as f:
                client_values = dict(flatten(tomllib.load(f)))

        expected = set()
        for (cat, name), default in options.items():
            total["автора"] += 1
            key = tuple(cat.split(".")) + tuple(name.split("."))
            if any(fnmatch.fnmatchcase(name, r) for r in removed):
                total["удалено"] += 1
                if key in values:
                    errors.append("%s: удалённая опция %s.%s есть в файле" % (file_name, cat, name))
                continue
            if file_name in CLIENT_FILE and name in client:
                total["в клиентском файле"] += 1
                if key in values:
                    errors.append("%s: клиентская опция %s.%s лежит в общем файле" % (file_name, cat, name))
                if client_values and key not in client_values:
                    errors.append("%s: нет клиентской опции %s.%s" % (CLIENT_FILE[file_name], cat, name))
                continue
            expected.add(key)
            if key not in values:
                errors.append("%s: нет опции %s.%s" % (file_name, cat, name))
                continue
            total["в файлах"] += 1
            if default is None:
                unchecked.append("%s.%s" % (cat, name))
            elif not same(values[key], default):
                errors.append("%s: %s.%s = %r, у автора по умолчанию %r" % (file_name, cat, name, values[key], default))
            else:
                checked += 1
        for key in sorted(set(values) - expected):
            errors.append("%s: лишняя опция %s" % (file_name, ".".join(key)))

    unknown_client = sorted(n for n in client if not any(n == name for f, h in HANDLERS.items() if f in CLIENT_FILE for (_, name) in author_options(author_source(h))))
    for n in unknown_client:
        errors.append("ClientOptions.kt: у автора нет опции %s" % n)

    print("Опций у автора: %(автора)d; удалено: %(удалено)d; в клиентском файле: %(в клиентском файле)d; в файлах сервера: %(в файлах)d" % total)
    print("Значение по умолчанию совпадает с авторским: %d; не проверено: %d%s" % (
        checked, len(unchecked), " (" + ", ".join(unchecked) + ")" if unchecked else ""))
    if errors:
        print("\nОШИБКИ (%d):" % len(errors))
        for e in errors:
            print("  " + e)
        return 1
    print("OK: настройки сходятся с автором")
    return 0


if __name__ == "__main__":
    sys.exit(main(sys.argv[1] if len(sys.argv) > 1 else "run/config/Alfheim"))
