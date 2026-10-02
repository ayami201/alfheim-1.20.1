#!/usr/bin/env python3
"""Проверка лога сервера после `./gradlew runGameTestServer` (ROADMAP, КТ-0, «Готово, когда»).

1. Загружены моды alfheim, botania, patchouli, curios, kotlinforforge:
   в логе есть строка `PortSmokeTest: loaded <id>` от GameTest-а порта.
2. Ошибок alfheim нет: ни одна запись уровня ERROR или FATAL (вместе со
   стек-трейсом) не упоминает alfheim. Путь к репозиторию `alfheim-1.20.1`
   не считается.

Запуск: python3 tools/check_server_log.py [run/logs/latest.log]
"""
import re
import sys

MODS = ["alfheim", "botania", "patchouli", "curios", "kotlinforforge"]
ENTRY = re.compile(r"^\[[^\]]+\] \[[^\]]*/(TRACE|DEBUG|INFO|WARN|ERROR|FATAL)\]")


def main(path):
    text = open(path, encoding="utf-8", errors="replace").read()
    ok = True

    for mod in MODS:
        m = re.search(r"PortSmokeTest: loaded %s (\S+)" % re.escape(mod), text)
        if m:
            print("OK   загружен %s %s" % (mod, m.group(1)))
        else:
            print("FAIL не загружен %s" % mod)
            ok = False

    # Записи лога: строка с уровнем плюс следующие за ней строки стек-трейса
    entries, level, buf = [], None, []
    for line in text.splitlines():
        m = ENTRY.match(line)
        if m:
            if buf:
                entries.append((level, "\n".join(buf)))
            level, buf = m.group(1), [line]
        elif buf:
            buf.append(line)
    if buf:
        entries.append((level, "\n".join(buf)))

    bad = [e for lvl, e in entries
           if lvl in ("ERROR", "FATAL") and re.search(r"alfheim", e.replace("alfheim-1.20.1", ""), re.I)]
    errors = sum(1 for lvl, _ in entries if lvl in ("ERROR", "FATAL"))
    if bad:
        ok = False
        for e in bad:
            print("FAIL ошибка alfheim:\n" + e[:2000])
    else:
        print("OK   ошибок alfheim нет (всего записей ERROR/FATAL в логе: %d, все не от alfheim)" % errors)

    return 0 if ok else 1


if __name__ == "__main__":
    sys.exit(main(sys.argv[1] if len(sys.argv) > 1 else "run/logs/latest.log"))
