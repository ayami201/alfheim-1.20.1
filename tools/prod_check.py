#!/usr/bin/env python3
"""Проверка мода на настоящем сервере Forge (docs/port/CHECKS.md, C-010; инструкция — docs/port/checks/C-010.md).

GameTest-ы идут в среде разработки: там у методов игры официальные имена. У игроков сервер и клиент
работают с именами SRG (`m_21023_`), и мод попадает к ним переименованным: миксины находят методы по
карте ссылок, преобразователь доступа открывает поля по именам SRG. Ошибку в этом тесты не видят.
Скрипт поднимает сервер с модом и проверяет командами то, что без переименования не работает:

1. сервер загружается до «Done» со всеми модами, без ошибок alfheim в логе;
2. «Танк» засчитывается как «Сопротивление» (миксин `LivingEntityMixin`, врезки H-008, H-009, поле
   `amplifier`, открытое преобразователем доступа): хаск с «Танком II» получает меньше урона;
3. «Пиво» прибавляет здоровье (`Potion1710` подменяет `addAttributeModifiers` и
   `getAttributeModifierValue`);
4. «Шампанское» снимает яд (`Potion1710` подменяет `isDurationEffectTick` и `applyEffectTick`);
5. имя зелья на сервере — перевод ключа автора; предмет мода (`alfheim:lembas`) существует;
6. существа мода: граната (`alfheim:thrown_item`) с бросившим хаском падает на другого хаска — урон
   «огненный шар» и огонь; летящее зелье (`alfheim:thrown_potion`) появляется командой.

Команды идут по RCON (удалённая консоль сервера): скрипт сам включает его в `server.properties`
(порт 25575, пароль `alfcheck`). Существа появляются в точке появления мира с меткой `alfcheck` и
удаляются в конце.

Запуск:
    python3 tools/prod_check.py --dir <папка сервера> -- <команда запуска сервера>
    python3 tools/prod_check.py --dir server -- ./run.sh nogui
    python3 tools/prod_check.py --dir server -- java @user_jvm_args.txt @libraries/net/minecraftforge/forge/1.20.1-47.4.23/win_args.txt nogui
    python3 tools/prod_check.py --dir run --cwd . -- ./gradlew runServer   (среда разработки, сверка самих проверок)

Код выхода 0 — всё прошло. Отчёт в Markdown печатается в конце.
"""
import argparse
import os
import re
import socket
import struct
import subprocess
import sys
import threading
import time

RCON_PORT = 25575
RCON_PASSWORD = "alfcheck"
START_TIMEOUT = 900
KNOWN_NETWORK = ["Unable to load news & version from official repo", "Failed to register contributors",
                 "Failed to register custom auras", "Failed to register patrons", "Failed to register custom wings"]


def set_properties(path):
    """Включить RCON в server.properties, остальное оставить"""
    wanted = {"enable-rcon": "true", "rcon.port": str(RCON_PORT), "rcon.password": RCON_PASSWORD}
    lines = open(path, encoding="utf-8").read().splitlines() if os.path.exists(path) else []
    out = []
    for line in lines:
        key = line.split("=", 1)[0]
        if key in wanted:
            line = f"{key}={wanted.pop(key)}"
        out.append(line)
    out += [f"{k}={v}" for k, v in wanted.items()]
    open(path, "w", encoding="utf-8").write("\n".join(out) + "\n")


class Rcon:
    def __init__(self, port, password):
        self.sock = socket.create_connection(("127.0.0.1", port), timeout=30)
        self.id = 0
        if self._send(3, password)[0] == -1:
            raise RuntimeError("RCON: неверный пароль")

    def _send(self, kind, body):
        self.id += 1
        data = struct.pack("<ii", self.id, kind) + body.encode("utf-8") + b"\0\0"
        self.sock.sendall(struct.pack("<i", len(data)) + data)
        size = struct.unpack("<i", self._read(4))[0]
        rid, _ = struct.unpack("<ii", self._read(8))
        text = self._read(size - 8)[:-2].decode("utf-8", "replace")
        return rid, text

    def _read(self, n):
        buf = b""
        while len(buf) < n:
            chunk = self.sock.recv(n - len(buf))
            if not chunk:
                raise RuntimeError("RCON: соединение закрыто")
            buf += chunk
        return buf

    def cmd(self, command):
        return self._send(2, command)[1]


def number(text):
    found = re.findall(r"(-?\d+(?:\.\d+)?)[bsfdL]?\s*$", text.strip())
    return float(found[0]) if found else None


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--dir", required=True, help="папка сервера (server.properties, logs)")
    parser.add_argument("--cwd", help="где выполнять команду запуска (по умолчанию — --dir)")
    parser.add_argument("command", nargs=argparse.REMAINDER)
    args = parser.parse_args()
    command = args.command[1:] if args.command[:1] == ["--"] else args.command
    if not command:
        sys.exit("нужна команда запуска сервера после --")

    set_properties(os.path.join(args.dir, "server.properties"))
    output = []
    proc = subprocess.Popen(command, cwd=args.cwd or args.dir, stdin=subprocess.PIPE, stdout=subprocess.PIPE,
                            stderr=subprocess.STDOUT, text=True, encoding="utf-8", errors="replace")

    def reader():
        for line in proc.stdout:
            output.append(line.rstrip("\n"))
            print(line, end="", flush=True)

    threading.Thread(target=reader, daemon=True).start()

    results = []

    def check(name, ok, detail):
        results.append((name, ok, detail))

    deadline = time.time() + START_TIMEOUT
    while time.time() < deadline and proc.poll() is None and not any("Done (" in l for l in output):
        time.sleep(1)
    started = any("Done (" in l for l in output)
    check("Сервер загрузился до «Done»", started, "" if started else "сервер не запустился — см. лог и crash-reports")

    if started:
        while not any("RCON running on" in l for l in output) and time.time() < deadline:
            time.sleep(1)
        rcon = Rcon(RCON_PORT, RCON_PASSWORD)
        r = rcon.cmd

        r("kill @e[tag=alfcheck]")
        r("difficulty easy")
        r("time set day")
        # площадка у точки появления: пусто, под ногами камень; хаск — 20 здоровья, не горит на солнце, без ИИ
        r("fill ~-2 ~ ~-2 ~9 ~3 ~2 minecraft:air")
        r("fill ~-2 ~-1 ~-2 ~9 ~-1 ~2 minecraft:stone")
        husks = {"alfcheck_tank": "~ ~ ~", "alfcheck_ctrl": "~2 ~ ~", "alfcheck_beer": "~4 ~ ~", "alfcheck_champ": "~6 ~ ~"}
        for tag, pos in husks.items():
            r(f'summon minecraft:husk {pos} {{NoAI:1b,PersistenceRequired:1b,Tags:["alfcheck","{tag}"]}}')

        def health(tag):
            return number(r(f"data get entity @e[tag={tag},limit=1] Health"))

        # 2. «Танк II» засчитывается как «Сопротивление II»: урон 10 → 6
        r("effect give @e[tag=alfcheck_tank,limit=1] alfheim:tank 120 1")
        before = health("alfcheck_tank"), health("alfcheck_ctrl")
        r("damage @e[tag=alfcheck_tank,limit=1] 10 minecraft:generic")
        r("damage @e[tag=alfcheck_ctrl,limit=1] 10 minecraft:generic")
        tank, ctrl = before[0] - health("alfcheck_tank"), before[1] - health("alfcheck_ctrl")
        check("«Танк» засчитывается как «Сопротивление» (H-008, H-009)", tank == 6.0 and ctrl == 10.0,
              f"урон 10: с «Танком» снято {tank} (ждём 6.0), без него {ctrl} (ждём 10.0)")

        # 3. «Пиво»: здоровье +3 и лечит на 3
        start = health("alfcheck_beer")
        r("effect give @e[tag=alfcheck_beer,limit=1] alfheim:beer 120 0")
        beer_max = number(r("attribute @e[tag=alfcheck_beer,limit=1] minecraft:generic.max_health get"))
        beer = health("alfcheck_beer")
        check("«Пиво» прибавляет здоровье", beer_max == 23.0 and beer == min(start + 3, 23.0),
              f"максимум {beer_max} (ждём 23.0), здоровье {start} → {beer} (ждём +3)")

        # 4. «Шампанское» снимает яд на следующем тике
        r("effect give @e[tag=alfcheck_champ,limit=1] minecraft:poison 120 0")
        r("effect give @e[tag=alfcheck_champ,limit=1] alfheim:champagne 120 0")
        time.sleep(2)
        effects = r("data get entity @e[tag=alfcheck_champ,limit=1] ActiveEffects")
        check("«Шампанское» снимает яд", "alfheim:champagne" in effects and "minecraft:poison" not in effects, effects)

        # 5. Имя зелья на сервере и предмет мода
        applied = r("effect give @e[tag=alfcheck_ctrl,limit=1] alfheim:white_wine 10 0")
        check("Имя зелья — перевод ключа автора", "White Wine" in applied, applied)
        r("summon minecraft:item ~ ~1 ~ {Item:{id:\"alfheim:lembas\",Count:1b},Tags:[\"alfcheck\",\"alfcheck_item\"],PickupDelay:32767}")
        item = r("data get entity @e[tag=alfcheck_item,limit=1] Item.id")
        check("Предмет мода существует (alfheim:lembas)", "alfheim:lembas" in item, item)

        # 6. Граната от бросившего (хаск «ctrl») падает на хаска: урон 3 («огненный шар») за вычетом брони хаска (2) —
        # 2,94, и огонь на 10 секунд
        r('summon minecraft:husk ~9 ~ ~ {NoAI:1b,PersistenceRequired:1b,Tags:["alfcheck","alfcheck_fire"]}')
        owner = re.search(r"\[I;\s*(-?\d+),\s*(-?\d+),\s*(-?\d+),\s*(-?\d+)\]", r("data get entity @e[tag=alfcheck_ctrl,limit=1] UUID"))
        start = health("alfcheck_fire")
        if owner:
            r('summon alfheim:thrown_item ~9 ~3 ~ {Owner:[I;%s],Motion:[0.0d,-1.0d,0.0d],Tags:["alfcheck"]}' % ",".join(owner.groups()))
        time.sleep(1)
        burnt = health("alfcheck_fire")
        fire = number(r("data get entity @e[tag=alfcheck_fire,limit=1] Fire"))
        check("Граната мода ранит и поджигает (alfheim:thrown_item)",
              owner is not None and None not in (start, burnt, fire) and start - burnt >= 2.9 and fire > 0,
              f"здоровье {start} → {burnt} (ждём −2,94 и больше), огонь {fire} тиков")
        r('summon alfheim:thrown_potion ~9 ~6 ~ {NoGravity:1b,Tags:["alfcheck","alfcheck_potion"]}')
        potion = r("execute if entity @e[type=alfheim:thrown_potion,tag=alfcheck_potion]")
        check("Летящее зелье мода существует (alfheim:thrown_potion)", "count: 1" in potion, potion)

        r("kill @e[tag=alfcheck]")
        r("stop")
    try:
        proc.wait(timeout=180)
    except subprocess.TimeoutExpired:
        proc.kill()

    # путь к папке сервера или к репозиторию порта (alfheim-1.20.1) — не упоминание мода
    paths = {os.path.abspath(p) for p in (args.dir, args.cwd or args.dir)}
    def mentions_mod(line):
        for path in paths:
            line = line.replace(path, "")
        return "alfheim" in line.lower().replace("alfheim-1.20.1", "").replace("alfheim-1-20-1", "")
    errors = [l for l in output if re.search(r"/(ERROR|FATAL)\]", l) and mentions_mod(l)
              and not any(k in l for k in KNOWN_NETWORK)]
    check("В логе нет ошибок alfheim (кроме сетевых автора, SPEC п. 8)", not errors, "\n".join(errors[:20]))

    print("\n## Отчёт prod_check.py\n")
    print("| Проверка | Итог | Подробности |\n|---|---|---|")
    for name, ok, detail in results:
        print(f"| {name} | {'OK' if ok else 'ОШИБКА'} | {detail.replace(chr(10), ' ').replace('|', '/')[:300]} |")
    sys.exit(0 if all(ok for _, ok, _ in results) else 1)


if __name__ == "__main__":
    main()
