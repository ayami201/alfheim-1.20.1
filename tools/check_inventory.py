#!/usr/bin/env python3
"""Сверка docs/port/INVENTORY.md с исходниками автора (ROADMAP, КТ-0).

Проверяет:
1. в описи ровно по одной строке на каждый файл *.kt и *.java из src/main/java
   и src/api точки отсчёта fd34c141 — 1 153 файла, лишних строк нет;
2. статус каждой строки допустим;
3. «перенесено» стоит ровно у файлов, которых нет в legacy/, и путь в src/ из
   примечания существует; остальные файлы лежат в legacy/;
4. сумма по таблице ресурсов = числу файлов в assets на точке отсчёта;
5. сводная таблица совпадает со строками описи.

Если коммита fd34c141 нет (мелкий клон), список файлов собирается из дерева:
legacy/src + строки «перенесено».

Запуск из корня репозитория: python3 tools/check_inventory.py [--summary]
--summary печатает сводную таблицу, пересчитанную по строкам.
"""
import os
import re
import subprocess
import sys

REF = "fd34c141cb9d4466735a0e034d6a2fb2401cc177"
REF_CODE_FILES = 1153
REF_ASSET_FILES = 2344
INVENTORY = "docs/port/INVENTORY.md"
STATUSES = ("ждёт", "перенесено", "WIP — стадия 2")


def git(*args):
    return subprocess.run(["git", *args], capture_output=True, text=True)


def ref_available():
    return git("cat-file", "-e", REF + "^{commit}").returncode == 0


def ref_files(prefixes):
    out = git("ls-tree", "-r", "--name-only", REF, "--", *prefixes).stdout
    return out.splitlines()


def parse_inventory():
    rows, assets_total, summary = [], None, {}
    section, directory = None, None
    for n, line in enumerate(open(INVENTORY, encoding="utf-8"), 1):
        line = line.rstrip("\n")
        if line.startswith("## "):
            section = line[3:].strip()
            continue
        m = re.match(r"^### `legacy/(.+)/`$", line)
        if m:
            directory = m.group(1)
            continue
        if section == "Код" and line.startswith("| `"):
            cells = [c.strip() for c in line.strip("|").split("|")]
            name = cells[0].strip("`")
            rows.append((n, directory + "/" + name, cells[2], cells[3], cells[4] if len(cells) > 4 else "", int(cells[1])))
        if section == "Сводка" and line.startswith("| "):
            cells = [c.strip() for c in line.strip("|").split("|")]
            if re.fullmatch(r"КТ-\d+|—|по HOOKS\.md", cells[0]):
                summary[cells[0]] = cells[1:]
        if section == "Ресурсы":
            m = re.match(r"^\| \*\*всего\*\* \| \*\*(\d+)\*\*", line)
            if m:
                assets_total = int(m.group(1))
    return rows, assets_total, summary


def recount(rows):
    def kt_key(k):
        return (0, int(k[3:])) if k.startswith("КТ-") else (1, k == "—", k)
    statuses = ["ждёт", "перенесено", "выпало", "WIP — стадия 2"]
    table = {}
    for kt in sorted({r[2] for r in rows}, key=kt_key):
        mine = [r for r in rows if r[2] == kt]
        lines = "{:,}".format(sum(r[5] for r in mine)).replace(",", "\u202f")
        table[kt] = [str(len(mine)), lines] + [str(sum(1 for r in mine if r[3].split(" (")[0] == s) or "") for s in statuses]
    return table


def main():
    errors = []
    rows, assets_total, summary = parse_inventory()

    have_ref = ref_available()
    if have_ref:
        ref = [f for f in ref_files(["src/main/java", "src/api"]) if f.endswith((".kt", ".java"))]
        n_assets = len(ref_files(["src/main/resources/assets"]))
        source = "коммит " + REF[:8]
    else:
        ref = []
        for root in ("legacy/src/main/java", "legacy/src/api"):
            for d, _, fs in os.walk(root):
                ref += [os.path.join(d, f)[len("legacy/"):] for f in fs if f.endswith((".kt", ".java"))]
        ref += [r[1] for r in rows if r[3] == "перенесено"]
        n_assets = REF_ASSET_FILES
        source = "дерево legacy/ + перенесённые (истории git нет)"

    if len(ref) != REF_CODE_FILES:
        errors.append("файлов кода на точке отсчёта %d, ожидалось %d" % (len(ref), REF_CODE_FILES))

    seen = {}
    for n, path, kt, status, note, _ in rows:
        if path in seen:
            errors.append("строка %d: %s уже есть в строке %d" % (n, path, seen[path]))
        seen[path] = n
        if not (status in STATUSES or re.fullmatch(r"выпало \(.+\)", status)):
            errors.append("строка %d: недопустимый статус «%s»" % (n, status))
        in_legacy = os.path.exists("legacy/" + path)
        if status == "перенесено":
            if in_legacy:
                errors.append("строка %d: %s «перенесено», но файл ещё в legacy/" % (n, path))
            m = re.search(r"`(src/[^`]+)`", note)
            if not m or not os.path.exists(m.group(1)):
                errors.append("строка %d: %s «перенесено», но в примечании нет существующего пути src/…" % (n, path))
        elif not in_legacy:
            errors.append("строка %d: %s со статусом «%s», но файла нет в legacy/" % (n, path, status))

    missing = sorted(set(ref) - set(seen))
    extra = sorted(set(seen) - set(ref))
    for p in missing:
        errors.append("нет строки для %s" % p)
    for p in extra:
        errors.append("лишняя строка %s (строка %d)" % (p, seen[p]))

    if assets_total != n_assets:
        errors.append("ресурсы: в описи %s файлов, на точке отсчёта %d" % (assets_total, n_assets))

    table = recount(rows)
    if table != summary:
        errors.append("сводная таблица не совпадает со строками: python3 tools/check_inventory.py --summary")
    if "--summary" in sys.argv:
        print("| КТ | Файлов | Строк | ждёт | перенесено | выпало | WIP — стадия 2 |")
        print("|---|---:|---:|---:|---:|---:|---:|")
        for kt, cells in table.items():
            print("| %s | %s |" % (kt, " | ".join(cells)))
        print()

    counts = {}
    for _, _, _, status, _, _ in rows:
        key = status.split(" (")[0]
        counts[key] = counts.get(key, 0) + 1
    print("Источник: " + source)
    print("Файлов кода на точке отсчёта: %d, строк в описи: %d" % (len(ref), len(rows)))
    print("По статусам: " + ", ".join("%s — %d" % kv for kv in sorted(counts.items())))
    print("Файлов ресурсов: в описи %s, на точке отсчёта %d" % (assets_total, n_assets))
    if errors:
        print("\nОШИБКИ (%d):" % len(errors))
        for e in errors[:100]:
            print("  " + e)
        return 1
    print("OK: опись сходится")
    return 0


if __name__ == "__main__":
    sys.exit(main())
