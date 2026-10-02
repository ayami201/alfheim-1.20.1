#!/usr/bin/env python3
"""Проверка лога сервера после `./gradlew runGameTestServer` (ROADMAP, КТ-0, «Готово, когда»).

1. Загружены моды alfheim, botania, patchouli, curios, kotlinforforge:
   в логе есть строка `PortSmokeTest: loaded <id>` от GameTest-а порта.
2. Ошибок alfheim нет: ни одна запись уровня ERROR или FATAL (вместе со
   стек-трейсом) не упоминает alfheim. Путь к репозиторию `alfheim-1.20.1`
   не считается.
3. Исключение — сетевые ошибки автора из KNOWN_NETWORK (SPEC п. 8): мод при
   запуске обращается к Bitbucket автора, без сети или без файла там пишет
   ERROR и работает дальше. Такая запись выводится в отчёт, но проверку не
   валит, если её стек-трейс — сетевое исключение (или стек-трейса у автора нет).

Запуск: python3 tools/check_server_log.py [run/logs/latest.log]
"""
import re
import sys

MODS = ["alfheim", "botania", "patchouli", "curios", "kotlinforforge"]
ENTRY = re.compile(r"^\[[^\]]+\] \[[^\]]*/(TRACE|DEBUG|INFO|WARN|ERROR|FATAL)\]")
MESSAGE = re.compile(r"^\[[^\]]+\] \[[^\]]*/(?:ERROR|FATAL)\] \[ALFHEIM/[^\]]*\]: (.*)$")

# Начало сообщения автора → откуда оно
KNOWN_NETWORK = [
    ("Unable to load news & version from official repo.", "InfoLoader: новости и версия (опция notifications)"),
    ("Failed to register contributors, using default parameters", "ContributorsPrivacyHelper: hashes.txt"),
    ("Failed to register custom auras", "ContributorsPrivacyHelper: auras.txt"),
    ("Failed to register patrons", "ContributorsPrivacyHelper: patrons.txt"),
    ("Failed to register custom wings", "ContributorsPrivacyHelper: wings.txt"),
]
# Исключения «нет сети или нет файла»: HTTP 404 в Java — FileNotFoundException, ответ не XML — SAXParseException
NETWORK_EXCEPTION = re.compile(r"^(java\.net\.\w+|java\.io\.(FileNotFoundException|IOException)|javax\.net\.ssl\.\w+|org\.xml\.sax\.SAXParseException)\b")


def known_network(entry):
    """Источник, если запись — сетевая ошибка автора, иначе None"""
    lines = entry.splitlines()
    m = MESSAGE.match(lines[0])
    if not m:
        return None
    for prefix, source in KNOWN_NETWORK:
        if m.group(1).startswith(prefix):
            if len(lines) > 1 and not NETWORK_EXCEPTION.match(lines[1].strip()):
                return None
            return source
    return None


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
    known = [e for e in bad if known_network(e)]
    for e in known:
        bad.remove(e)
        print("OK   сетевая ошибка автора, SPEC п. 8 (%s): %s" % (known_network(e), e.splitlines()[1].strip() if "\n" in e else "без стек-трейса"))
    errors = sum(1 for lvl, _ in entries if lvl in ("ERROR", "FATAL")) - len(known)
    if bad:
        ok = False
        for e in bad:
            print("FAIL ошибка alfheim:\n" + e[:2000])
    else:
        print("OK   ошибок alfheim нет (всего записей ERROR/FATAL в логе: %d, все не от alfheim)" % errors)

    return 0 if ok else 1


if __name__ == "__main__":
    sys.exit(main(sys.argv[1] if len(sys.argv) > 1 else "run/logs/latest.log"))
