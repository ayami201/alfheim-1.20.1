#!/usr/bin/env python3
# Пишет пустую структуру 1x1x1 (воздух) в формате NBT для GameTest, Minecraft 1.20.1 (DataVersion 3465).
# Так сделан src/gametest/resources/data/alfheim/structures/empty.nbt:
#   python3 tools/make_empty_structure.py src/gametest/resources/data/alfheim/structures/empty.nbt
import gzip, struct, sys
def s(x): b = x.encode('utf-8'); return struct.pack('>H', len(b)) + b
def tag(t, name, payload): return bytes([t]) + s(name) + payload
def int_list(v): return bytes([3]) + struct.pack('>i', len(v)) + b''.join(struct.pack('>i', i) for i in v)
def compound(*items): return b''.join(items) + b'\x00'
def comp_list(cs): return bytes([10]) + struct.pack('>i', len(cs)) + b''.join(cs)
root = compound(
    tag(3, 'DataVersion', struct.pack('>i', 3465)),
    tag(9, 'size', int_list([1, 1, 1])),
    tag(9, 'palette', comp_list([compound(tag(8, 'Name', s('minecraft:air')))])),
    tag(9, 'blocks', comp_list([compound(tag(9, 'pos', int_list([0, 0, 0])), tag(3, 'state', struct.pack('>i', 0)))])),
    tag(9, 'entities', bytes([0]) + struct.pack('>i', 0)),
)
data = tag(10, '', root)
with open(sys.argv[1], 'wb') as f:
    f.write(gzip.compress(data, mtime=0))
