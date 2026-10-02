#!/usr/bin/env python3
"""Сверка docs/port/HOOKS.md с врезками автора на точке отсчёта fd34c141 (ROADMAP, КТ-0).

Проверяет:
1. у каждой аннотации @Hook, @HookReplacer, @HookField, @SuperWrapper в
   src/main/java точки отсчёта есть ровно одна строка с тем же местом
   (файл:строка), лишних строк нет;
2. у каждого класса, который правит AlfheimClassTransformer, есть строка в
   разделе «Трансформер»;
3. строк «Добавленные интерфейсы» столько же, сколько вызовов
   registerAdditionalInterface, строк «Закомментированные» — сколько
   закомментированных @Hook;
4. статусы допустимы, сводная таблица совпадает с таблицами.
Печатает, из чего складываются 428 строк с `@Hook` из ROADMAP.

Нужен коммит fd34c141. В мелком клоне:
    git fetch --depth=1 origin fd34c141cb9d4466735a0e034d6a2fb2401cc177

Запуск из корня репозитория: python3 tools/check_hooks.py [--summary]
--summary печатает сводную таблицу, пересчитанную по строкам.
"""
import collections
import re
import subprocess
import sys

REF = "fd34c141cb9d4466735a0e034d6a2fb2401cc177"
HOOKS = "docs/port/HOOKS.md"
ANN = re.compile(r"@(Hook|HookReplacer|HookField|SuperWrapper)\b(?![.\w])")
PREFIX = {"Hook": "H", "HookReplacer": "R", "HookField": "F", "SuperWrapper": "S"}
STATUS = re.compile(r"^(ждёт|перенесено|WIP — стадия 2|не нужна \(.+\)|выпало \(.+\))")
ORDER = ["ждёт", "перенесено", "не нужна", "выпало", "WIP — стадия 2"]
TRANSFORMER = "src/main/java/alfheim/common/core/asm/transformer/AlfheimClassTransformer.kt"


def git(*args):
    return subprocess.run(["git", *args], capture_output=True, text=True)


def strip_comments(text):
    """Комментарии -> пробелы, переводы строк сохраняются (номера строк не сдвигаются)."""
    out, i, in_str = [], 0, False
    while i < len(text):
        c = text[i]
        if in_str:
            out.append(c)
            if c == "\\" and i + 1 < len(text):
                out.append(text[i + 1])
                i += 2
                continue
            if c == '"':
                in_str = False
            i += 1
        elif text.startswith("//", i):
            j = text.find("\n", i)
            j = len(text) if j < 0 else j
            out.append(" " * (j - i))
            i = j
        elif text.startswith("/*", i):
            j = text.find("*/", i + 2)
            j = len(text) if j < 0 else j + 2
            out.append("".join(ch if ch == "\n" else " " for ch in text[i:j]))
            i = j
        else:
            if c == '"':
                in_str = True
            out.append(c)
            i += 1
    return "".join(out)


def status_key(status):
    return status.split(" (")[0]


def main():
    if git("cat-file", "-e", REF + "^{commit}").returncode:
        print("Нет коммита точки отсчёта. Выполните:\n  git fetch --depth=1 origin " + REF)
        return 2

    files = [f for f in git("ls-tree", "-r", "--name-only", REF, "--", "src/main/java").stdout.splitlines()
             if f.endswith((".kt", ".java"))]
    found = {}          # (файл, строка) -> вид
    naive = collections.Counter()
    commented = 0
    interfaces = 0
    for f in files:
        raw = git("show", "%s:%s" % (REF, f)).stdout
        code = strip_comments(raw)
        for m in ANN.finditer(code):
            found[("legacy/" + f, code.count("\n", 0, m.start()) + 1)] = m.group(1)
        for line in raw.split("\n"):
            if "@Hook" in line and not line.strip().startswith("//"):
                for tok in re.findall(r"@Hook[\w.]*", line):
                    naive[tok] += 1
            if re.match(r"^\s*//.*@Hook\b(?![.\w])", line):
                commented += 1
        interfaces += len(re.findall(r"\bregisterAdditionalInterface\(\"", code))

    transformer = strip_comments(git("show", "%s:%s" % (REF, TRANSFORMER)).stdout)
    start = transformer.index("return when (transformedName)")
    when = transformer[start:start + re.search(r"\belse\s+->", transformer[start:]).start()]
    classes = sorted({c.split(".")[-1] for c in re.findall(r'"([\w.$]+)"', when)})

    errors = []
    rows = collections.defaultdict(list)
    section = None
    summary_table = {}
    for n, line in enumerate(open(HOOKS, encoding="utf-8"), 1):
        line = line.rstrip("\n")
        if line.startswith("## "):
            section = line[3:]
            continue
        if not line.startswith("| "):
            continue
        cells = [c.strip() for c in re.split(r"(?<!\\)\|", line.strip())[1:-1]]
        if section == "Сводка" and re.fullmatch(r"КТ-\d+|—", cells[0]):
            summary_table[cells[0]] = cells[1:]
            continue
        m = re.match(r"^([HRFSTIC])-\d+$", cells[0])
        if not m:
            continue
        kind = m.group(1)
        rows[kind].append((n, cells))

    seen = {}
    counts = collections.Counter()
    for kind in "HRFS":
        for n, cells in rows[kind]:
            loc = re.fullmatch(r"`(legacy/[^`]+):(\d+)`", cells[1])
            if not loc:
                errors.append("строка %d: не разобрано место %s" % (n, cells[1]))
                continue
            key = (loc.group(1), int(loc.group(2)))
            if key in seen:
                errors.append("строка %d: место %s:%d уже в строке %d" % (n, key[0], key[1], seen[key]))
            seen[key] = n
            if key not in found:
                errors.append("строка %d: на %s:%d нет врезки" % (n, key[0], key[1]))
            elif PREFIX[found[key]] != kind:
                errors.append("строка %d: на %s:%d @%s, а ID %s-" % (n, key[0], key[1], found[key], kind))
            kt, status = cells[5], cells[7]
            if not STATUS.match(status):
                errors.append("строка %d: недопустимый статус «%s»" % (n, status))
            counts[(kt, status_key(status))] += 1
    for key in sorted(set(found) - set(seen)):
        errors.append("нет строки для @%s на %s:%d" % (found[key], key[0], key[1]))

    t_classes = " ".join(cells[1] for _, cells in rows["T"])
    for c in classes:
        if c not in t_classes:
            errors.append("трансформер: нет строки для класса %s" % c)
    for kind, idx_kt, idx_st in (("T", 4, 6), ("I", 5, 7), ("C", None, 3)):
        for n, cells in rows[kind]:
            status = cells[idx_st]
            if not STATUS.match(status):
                errors.append("строка %d: недопустимый статус «%s»" % (n, status))
            counts[(cells[idx_kt] if idx_kt is not None else "—", status_key(status))] += 1
    if len(rows["I"]) != interfaces:
        errors.append("интерфейсов в таблице %d, вызовов registerAdditionalInterface %d" % (len(rows["I"]), interfaces))
    if len(rows["C"]) != commented:
        errors.append("закомментированных в таблице %d, у автора %d" % (len(rows["C"]), commented))

    kts = sorted({k for k, _ in counts}, key=lambda k: (k == "—", int(k[3:]) if k.startswith("КТ-") else 99))
    table = {}
    for k in kts:
        table[k] = [str(sum(counts[(k, s)] for s in ORDER))] + [str(counts[(k, s)] or "") for s in ORDER]
    if table != summary_table:
        errors.append("сводная таблица не совпадает со строками: python3 tools/check_hooks.py --summary")

    by_kind = collections.Counter(found.values())
    print("Точка отсчёта %s:" % REF[:8])
    print("  @Hook %d, @HookReplacer %d, @HookField %d, @SuperWrapper %d" % (
        by_kind["Hook"], by_kind["HookReplacer"], by_kind["HookField"], by_kind["SuperWrapper"]))
    print("  строк с `@Hook` (как в ROADMAP): %d = %s" % (
        sum(naive.values()), " + ".join("%d %s" % (v, k) for k, v in sorted(naive.items()))))
    print("  классов в трансформере %d, добавленных интерфейсов %d, закомментированных @Hook %d" % (
        len(classes), interfaces, commented))
    print("HOOKS.md: H %d, R %d, F %d, S %d, T %d, I %d, C %d" % tuple(len(rows[k]) for k in "HRFSTIC"))

    if "--summary" in sys.argv:
        print("\n| КТ | Всего | " + " | ".join(ORDER) + " |")
        print("|---|---:|" + "---:|" * len(ORDER))
        for k in kts:
            print("| %s | %s |" % (k, " | ".join(table[k])))
        total = [sum(counts[(k, s)] for k in kts) for s in ORDER]
        print("| **всего** | **%d** | %s |" % (sum(total), " | ".join("**%d**" % t for t in total)))

    if errors:
        print("\nОШИБКИ (%d):" % len(errors))
        for e in errors[:100]:
            print("  " + e)
        return 1
    print("OK: врезки сходятся")
    return 0


if __name__ == "__main__":
    sys.exit(main())
