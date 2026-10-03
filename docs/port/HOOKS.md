# Врезки автора

Каждая врезка автора в чужой код (Minecraft, Forge, Botania, другие моды) → КТ, решение
в 1.20.1 и статус. Врезки переносятся вместе со своей механикой (SPEC, Р-6): сначала
ищется событие Forge или API Botania 1.20.1, иначе пишется миксин в `alfheim.port.mixin`.

**Статусы:** `ждёт`; `перенесено` — в «Решении» указано событие или миксин;
`не нужна (причина)` — в 1.20.1 так уже работает или нечего исправлять;
`выпало (причина)` — цель в моде, которого нет на 1.20.1 (SPEC, п. 7);
`WIP — стадия 2` — у автора закомментирована.

**Колонки.** «Где» — файл и строка аннотации в `legacy/` (на точке отсчёта `fd34c141`).
«Цель» — класс и метод, куда врезка вставляет код. «Как» — куда и что: «в начале» /
«в конце» метода, «возвращает свой результат» (заменяет метод целиком), «выходит, если
true» (досрочно завершает метод) и т. п. Смысл врезки дописывается в «Примечание» при
переносе. «Решение в 1.20.1» заполняется при переносе: событие, миксин или «не нужна».
Распределение по КТ предварительное, как в [INVENTORY.md](INVENTORY.md).

**Как переносить врезку.** Найти цель в исходниках 1.20.1 (Minecraft — в `build/` после
сборки, Botania — артефакт `-sources`). Если механика есть в событии Forge или в API
Botania — подписаться на событие. Иначе миксин: Java-класс в `src/main/java/alfheim/port/mixin/`,
строка в `src/main/resources/alfheim.mixins.json`. Строка `"refmap": "alfheim.refmap.json"` в
`alfheim.mixins.json` нужна миксинам, чтобы найти методы Minecraft в собранном jar; её вернул
первый миксин (H-008, H-009). Карту ссылок в jar проверяет `tools/check_mixins.py` в CI: GameTest-ы
идут без неё и её отсутствия не замечают.

## Сверка

```bash
python3 tools/check_hooks.py
```

Скрипт находит на точке отсчёта каждую аннотацию врезки и проверяет, что у неё есть
строка в этом файле с тем же местом, а у каждого класса трансформера — строка в разделе
«Трансформер». Запускается в CI.

**Про число 428 из ROADMAP.** Это число незакомментированных строк, где встречается
`@Hook` (`grep`). В него входят:

| Что | Число | Раздел |
|---|---:|---|
| `@Hook` — врезки HookLib | 388 | [HookLib](#hooklib-hook) |
| `@HookReplacer` — замены участков байткода | 32 | [HookReplacer](#hookreplacer) |
| `@HookField` — поля, добавленные в чужие классы | 3 | [HookField](#hookfield) |
| `@Hook.ReturnValue` — пометка параметра врезки, не отдельная врезка | 4 | — |
| `@HookReplacer.CreateHRG` — объявление группы замен, не отдельная врезка | 1 | — |
| **итого строк с `@Hook`** | **428** | |

Строк в таблицах — по одной на врезку: 388 + 32 + 3 = **423**. Кроме них учтены врезки,
которые `grep @Hook` не находит: 4 `@SuperWrapper`, 20 преобразований трансформера (в 19 классах),
11 интерфейсов, добавленных к чужим классам, и 7 врезок, закомментированных автором.

## Сводка

| КТ | Всего | ждёт | перенесено | не нужна | выпало | WIP — стадия 2 |
|---|---:|---:|---:|---:|---:|---:|
| КТ-1 | 3 | 3 |  |  |  |  |
| КТ-2 | 20 | 16 | 3 | 1 |  |  |
| КТ-3 | 190 | 190 |  |  |  |  |
| КТ-4 | 105 | 105 |  |  |  |  |
| КТ-5 | 13 | 13 |  |  |  |  |
| КТ-6 | 14 | 14 |  |  |  |  |
| КТ-7 | 16 | 16 |  |  |  |  |
| КТ-8 | 44 | 44 |  |  |  |  |
| КТ-9 | 5 | 5 |  |  |  |  |
| КТ-10 | 5 | 5 |  |  |  |  |
| — | 50 |  |  |  | 43 | 7 |
| **всего** | **465** | **411** | **3** | **1** | **43** | **7** |

Сводку пересчитывает `tools/check_hooks.py --summary`.

## HookLib `@Hook`

Библиотека GloomyFolken HookLib из ASJCore. Цель — класс первого параметра функции
(или `targetClass`), метод — имя функции (или `targetMethod`).

| ID | Где | Цель | Как | Сторона | КТ | Решение в 1.20.1 | Статус | Примечание |
|---|---|---|---|---|---|---|---|---|
| H-001 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHPHooks.kt:14` | `EntityLivingBase#getHealth` | в конце, возвращает свой результат, читает результат метода | оба | КТ-7 |  | ждёт |  |
| H-002 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHPHooks.kt:20` | `EntityLivingBase#getMaxHealth` | в конце, возвращает свой результат, читает результат метода | оба | КТ-7 |  | ждёт |  |
| H-003 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHPHooks.kt:37` | `EntityLivingBase#setHealth` | в конце, возвращает свой результат | оба | КТ-7 |  | ждёт |  |
| H-004 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:169` | `ContainerWorkbench#<init>` | в конце, добавляет код | оба | КТ-2 |  | ждёт | игрок у верстака (`CraftingExtensions`) |
| H-005 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:175` | `AlfheimAPI#registerSpell` | в начале, выходит, если true | оба | КТ-7 |  | ждёт |  |
| H-006 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:182` | `WorldServer#createBonusChest` | в начале, выходит, если true | оба | КТ-7 |  | ждёт |  |
| H-007 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:186` | `EntityCreeper#<init>` | в конце, добавляет код | оба | КТ-7 |  | ждёт |  |
| H-008 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:192` | `EntityLivingBase#isPotionActive` | в начале, возвращает свой результат | оба | КТ-2 | миксин `alfheim.port.mixin.LivingEntityMixin` → `LivingEntity#hasEffect`, код врезки — `alfheim.port.hook.EffectHooks` | перенесено | «Танк» засчитывается как «Сопротивление»; проверяет `PortPotionsTest.tankCountsAsResistance` |
| H-009 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:199` | `EntityLivingBase#getActivePotionEffect` | в начале, возвращает свой результат | оба | КТ-2 | миксин `alfheim.port.mixin.LivingEntityMixin` → `LivingEntity#getEffect`, код врезки — `alfheim.port.hook.EffectHooks` | перенесено | сила «Танка» прибавляется к силе «Сопротивления» (у автора — к самому эффекту на существе, при каждом вызове: ошибка автора перенесена); проверяет `PortPotionsTest.tankCountsAsResistance` |
| H-010 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:212` | `EntityLiving#despawnEntity` | в начале, выходит, если true | оба | КТ-5 |  | ждёт |  |
| H-011 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:216` | `EntityDragon#attackEntityFrom` | в начале, возвращает свой результат | оба | КТ-7 |  | ждёт |  |
| H-012 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:225` | `BlockFire#updateTick` | в начале, выходит, если true | оба | КТ-8 |  | ждёт |  |
| H-013 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:234` | `BlockPortal#updateTick` | в начале, выходит, если true | оба | КТ-6 |  | ждёт |  |
| H-014 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:244` | `BlockPortal#onEntityCollidedWithBlock` | в начале, выходит, если true | оба | КТ-6 |  | ждёт |  |
| H-015 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:254` | `CommandDimTP#processCommand` | в начале, добавляет код | оба | КТ-1 |  | ждёт |  |
| H-016 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:262` | `ServerConfigurationManager#transferPlayerToDimension` | в начале, выходит, если true | оба | КТ-7 |  | ждёт |  |
| H-017 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:306` | `BlockFloatingFlower#onBlockActivated` | в начале, выходит, если true | оба | КТ-3 |  | ждёт |  |
| H-018 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:325` | `TileFloatingSpecialFlower#isOnSpecialSoil` | в начале, возвращает свой результат | оба | КТ-3 |  | ждёт |  |
| H-019 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:329` | `ManaItemHandler#requestManaExact` | в начале, выходит, если true | оба | КТ-3 |  | ждёт |  |
| H-020 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:333` | `ManaItemHandler#requestMana` | в начале, выходит, если true, результат — из `requestManaChecked` | оба | КТ-3 |  | ждёт |  |
| H-021 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:340` | `ManaItemHandler#getFullDiscountForTools` | в конце, возвращает свой результат, читает результат метода | оба | КТ-4 |  | ждёт | скидка маны от аксессуаров и расы; часть про Travellers Gear выпадает |
| H-022 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:365` | `BlockModWall#<init>` | в конце, добавляет код | оба | КТ-2 | — | не нужна (стены Botania 1.20.1 и так во вкладке Botania; стенам Alfheim автор сразу ставит вкладку Alfheim, `setCreativeTab(AlfheimTab)`) | вкладка Botania каждой стене `BlockModWall` — и стенам Botania (их конструктор ванилы клал во вкладку «Строительные блоки»), и стенам Alfheim |
| H-023 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:371` | `BotaniaCreativeTab#addBlock` | в конце, добавляет код | оба | КТ-2 | событие `BuildCreativeModeTabContentsEvent` (`alfheim.port.hook.CreativeTabHooks`) | перенесено | стена из эльфийского кварца во вкладке Botania сразу за лестницей из эльфийского кварца; проверяет `PortDecorTest.creativeTabs` |
| H-024 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:378` | `BlockSpreader#<init>` | в конце, добавляет код | оба | КТ-3 |  | ждёт |  |
| H-025 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:385` | `BlockHourglass#tickRate` | в начале, возвращает свой результат | оба | КТ-3 |  | ждёт |  |
| H-026 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:389` | `BlockHourglass#onBurstCollision` | в начале, возвращает свой результат | оба | КТ-3 |  | ждёт |  |
| H-027 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:405` | `TileHourglass#isItemValidForSlot` | в начале, возвращает свой результат | оба | КТ-3 |  | ждёт |  |
| H-028 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:437` | `TileHourglass#isItemValidForSlot` | в начале, возвращает свой результат | оба | КТ-3 |  | ждёт |  |
| H-029 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:441` | `TileHourglass#getStackItemTime` | в конце, возвращает свой результат, читает результат метода | оба | КТ-3 |  | ждёт |  |
| H-030 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:449` | `TileHourglass#getColor` | в конце, возвращает свой результат, читает результат метода | оба | КТ-3 |  | ждёт |  |
| H-031 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:461` | `TileHourglass#renderHUD` | в начале, возвращает свой результат | оба | КТ-3 |  | ждёт | автор: «Add: Hovering Hourglass HUD now shows the exact current time within the cycle Added the ability to use Mana Powder in the Hovering Hourglass to make it a counter» |
| H-032 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:501` | `TileEnchanter#writeCustomNBT` | в конце, добавляет код | оба | КТ-3 |  | ждёт | исправление NBT зачарователя Botania |
| H-033 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:507` | `TileEnchanter#readCustomNBT` | в конце, добавляет код | оба | КТ-3 |  | ждёт | исправление NBT зачарователя Botania |
| H-034 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:513` | `ItemBauble#setCosmeticItem` | в конце, добавляет код | оба | КТ-4 |  | ждёт |  |
| H-035 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:520` | `BlockCocoon#onBlockActivated` | в конце, добавляет код, результат `false` | оба | КТ-3 |  | ждёт | кокон Botania |
| H-036 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:536` | `TileCocoon#writeCustomNBT` | в конце, добавляет код | оба | КТ-3 |  | ждёт | кокон Botania |
| H-037 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:542` | `TileCocoon#readCustomNBT` | в конце, добавляет код | оба | КТ-3 |  | ждёт | кокон Botania |
| H-038 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:551` | `TileCocoon#hatch` | в начале, добавляет код | оба | КТ-3 |  | ждёт | кокон Botania |
| H-039 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:557` | `World#spawnEntityInWorld` | в конце, добавляет код, читает результат метода | оба | КТ-3 |  | ждёт | кокон Botania |
| H-040 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:568` | `Entity#moveFlying` | в конце, добавляет код | оба | КТ-7 |  | ждёт |  |
| H-041 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:578` | `EntityDoppleganger#setDead` | в начале, добавляет код | оба | КТ-8 |  | ждёт |  |
| H-042 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:594` | `EntityDoppleganger#onLivingUpdate` | в начале, добавляет код | оба | КТ-8 |  | ждёт |  |
| H-043 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:601` | `EntityDoppleganger#onLivingUpdate` | в конце, добавляет код | оба | КТ-8 |  | ждёт |  |
| H-044 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:607` | `EntityDoppleganger#spawn` | в начале, выходит, если true | оба | КТ-8 |  | ждёт |  |
| H-045 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:627` | `EntityDoppleganger#attackEntityFrom` | в начале, выходит, если true, результат `false` | оба | КТ-8 |  | ждёт |  |
| H-046 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:636` | `EntityDoppleganger#attackEntityFrom` | в начале, добавляет код | оба | КТ-8 |  | ждёт |  |
| H-047 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:644` | `EntityDoppleganger#attackEntityFrom` | в конце, добавляет код, читает результат метода | оба | КТ-8 |  | ждёт |  |
| H-048 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:652` | `EntityDoppleganger#onDeath` | в конце, добавляет код | оба | КТ-8 |  | ждёт |  |
| H-049 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:677` | `EntityDoppleganger#getNameColor` | создаёт метод, возвращает свой результат | клиент | КТ-8 |  | ждёт |  |
| H-050 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:681` | `LensFirework#collideBurst` | в начале, выходит, если true | оба | КТ-3 |  | ждёт |  |
| H-051 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:703` | `EntityManaBurst#<init>` | в конце, добавляет код | оба | КТ-3 |  | ждёт |  |
| H-052 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:710` | `CommonProxy#wispFX` | в начале, возвращает свой результат | оба | КТ-8 |  | ждёт | частицы во время боя с Гайей |
| H-053 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:727` | `CommonProxy#wispFX` | в начале, возвращает свой результат | оба | КТ-8 |  | ждёт | частицы во время боя с Гайей |
| H-054 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:741` | `CommonProxy#setEntryDataToOpen` | создаёт метод, добавляет код | оба | КТ-9 |  | ждёт |  |
| H-055 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:746` | `ClientProxy#setEntryDataToOpen` | создаёт метод, добавляет код | клиент | КТ-9 |  | ждёт |  |
| H-056 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:752` | `ItemLexicon#openBook` | в начале, добавляет код | оба | КТ-9 |  | ждёт |  |
| H-057 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:761` | `BlockGrass#updateTick` | в конце, добавляет код | оба | КТ-8 |  | ждёт |  |
| H-058 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:768` | `BlockGrass#randomDisplayTick` | создаёт метод, возвращает свой результат | оба | КТ-6 |  | ждёт |  |
| H-059 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:776` | `WorldClient#doVoidFogParticles` | в начале, добавляет код | оба | КТ-6 |  | ждёт |  |
| H-060 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:802` | `BlockSnow#updateTick` | в конце, добавляет код | оба | КТ-8 |  | ждёт |  |
| H-061 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:814` | `BlockAltGrass#updateTick` | в начале, добавляет код | оба | КТ-8 |  | ждёт |  |
| H-062 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:845` | `BlockIce#updateTick` | в начале, выходит, если true | оба | КТ-6 |  | ждёт |  |
| H-063 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:849` | `BlockIce#updateTick` | в конце, добавляет код | оба | КТ-8 |  | ждёт |  |
| H-064 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:856` | `ItemAncientWill#getSubItems` | в конце, добавляет код | оба | КТ-4 |  | ждёт | новая Воля древних |
| H-065 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:862` | `ItemAncientWill#registerIcons` | в конце, добавляет код | оба | КТ-4 |  | ждёт | новая Воля древних |
| H-066 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:868` | `ItemAncientWill#addInformation` | в начале, выходит, если true | оба | КТ-4 |  | ждёт | новая Воля древних |
| H-067 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:879` | `BlockAvatar#onBlockActivated` | в конце, добавляет код, читает результат метода | оба | КТ-3 |  | ждёт | синхронизация блок-сущности Botania |
| H-068 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:886` | `ItemDye#onItemUse` | в конце, добавляет код, читает результат метода | оба | КТ-3 |  | ждёт | синхронизация блок-сущности Botania |
| H-069 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:894` | `ItemOpenBucket#onItemRightClick` | в начале, возвращает свой результат | оба | КТ-3 |  | ждёт | fix for IFluidBlock; автор: «fix for IFluidBlock» |
| H-070 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:942` | `ItemExchangeRod#displayRemainderCounter` | в начале, возвращает свой результат | оба | КТ-4 |  | ждёт |  |
| H-071 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:953` | `ItemMissileRod#onItemRightClick` | в начале, выходит, если не null | оба | КТ-4 |  | ждёт |  |
| H-072 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:964` | `ItemsRemainingRenderHandler#set` | в начале, выходит, если true | оба | КТ-4 |  | ждёт | счётчик предметов жезлов |
| H-073 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:968` | `BlockSpreader#onBlockActivated` | в конце, добавляет код, читает результат метода | оба | КТ-3 |  | ждёт |  |
| H-074 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:977` | `EntitySpark#setDead` | в начале, выходит, если true | оба | КТ-3 |  | ждёт | автор: «dupe fix» |
| H-075 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:982` | `EntityCorporeaSpark#setDead` | в начале, выходит, если true | оба | КТ-3 |  | ждёт | автор: «dupe fix» |
| H-076 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:987` | `ItemRainbowRod#<init>` | в начале, добавляет код | оба | КТ-4 |  | ждёт | автор: «dupe fix» |
| H-077 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:993` | `ItemRainbowRod#onItemRightClick` | в начале, выходит, если не null | оба | КТ-8 |  | ждёт | жезл Бифрёста во время Рагнарёка |
| H-078 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:999` | `ItemRainbowRod#getContainerItem` | в начале, выходит, если null | оба | КТ-4 |  | ждёт |  |
| H-079 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1005` | `ItemRainbowRod#onAvatarUpdate` | в начале, возвращает свой результат | оба | КТ-4 |  | ждёт |  |
| H-080 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1062` | `BlockBellows#onBlockActivated` | в начале, возвращает свой результат | оба | КТ-4 |  | ждёт |  |
| H-081 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1070` | `TilePool#updateEntity` | в начале, добавляет код | оба | КТ-3 |  | ждёт |  |
| H-082 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1076` | `TilePool#collideEntityItem` | в начале, возвращает свой результат | оба | КТ-3 |  | ждёт |  |
| H-083 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1120` | `RecipeManaInfusion#matches` | в конце, выходит, если true, результат — из `sizeCheck`, читает результат метода | оба | КТ-3 |  | ждёт |  |
| H-084 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1132` | `TilePylon#updateEntity` | в начале, добавляет код | оба | КТ-3 |  | ждёт |  |
| H-085 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1138` | `TilePylon#updateEntity` | в конце, добавляет код | оба | КТ-3 |  | ждёт |  |
| H-086 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1150` | `ClientProxy#sparkleFX` | в начале, выходит, если true | оба | КТ-3 |  | ждёт |  |
| H-087 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1154` | `BlockSpecialFlower#getSubBlocks` | в начале, возвращает свой результат | оба | КТ-3 |  | ждёт |  |
| H-088 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1168` | `ItemGaiaHead#isValidArmor` | создаёт метод, возвращает свой результат | оба | КТ-4 |  | ждёт |  |
| H-089 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1172` | `BotaniaCreativeTab#displayAllReleventItems` | в конце, добавляет код | оба | КТ-4 |  | ждёт | `ItemThinkingHand` во вкладке Botania |
| H-090 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1178` | `SubTileEntity#onBlockPlacedBy` | в начале, добавляет код | оба | КТ-3 |  | ждёт |  |
| H-091 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1184` | `SubTileEntity#onBlockAdded` | в начале, добавляет код | оба | КТ-3 |  | ждёт |  |
| H-092 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1190` | `BlockPylon#getIcon` | в начале, возвращает свой результат | оба | КТ-3 |  | ждёт |  |
| H-093 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1195` | `RecipePureDaisy#matches` | в начале, выходит, если true, результат `false` | оба | КТ-3 |  | ждёт | рецепты Чистой маргаритки |
| H-094 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1210` | `AesirRingRecipe#matches` | в начале, возвращает свой результат | оба | КТ-4 |  | ждёт |  |
| H-095 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1240` | `HeadRecipe#getOutput` | в начале, добавляет код | оба | КТ-3 |  | ждёт | головы игроков в аптекаре |
| H-096 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1247` | `RecipeHandlerPetalApothecary#getRecipes` | в начале, возвращает свой результат | оба | КТ-10 |  | ждёт | NEI → JEI |
| H-097 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1253` | `ItemFlugelEye#onItemUse` | в начале, выходит, если true | оба | КТ-8 |  | ждёт |  |
| H-098 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1260` | `ItemInfiniteFruit#onUsingTick` | в начале, возвращает свой результат | оба | КТ-3 |  | ждёт |  |
| H-099 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1267` | `ItemBottledMana#onItemUse` | создаёт метод, возвращает свой результат | оба | КТ-3 |  | ждёт |  |
| H-100 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1298` | `ItemGaiaHead#onItemRightClick` | создаёт метод, возвращает свой результат | оба | КТ-4 |  | ждёт |  |
| H-101 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1305` | `EnchantmentHelper#getFortuneModifier` | в начале, возвращает свой результат | оба | КТ-7 |  | ждёт |  |
| H-102 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1310` | `BlockAltar#getSubBlocks` | в конце, добавляет код | оба | КТ-3 |  | ждёт | аптекарь из живого булыжника |
| H-103 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1316` | `BlockAltar#getIcon` | в начале, возвращает свой результат | оба | КТ-3 |  | ждёт | аптекарь из живого булыжника |
| H-104 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1321` | `TileAltar#collideEntityItem` | в начале, добавляет код | оба | КТ-3 |  | ждёт | аптекарь из живого булыжника |
| H-105 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1340` | `TileAltar#collideEntityItem` | в конце, добавляет код, читает результат метода | оба | КТ-3 |  | ждёт | аптекарь из живого булыжника |
| H-106 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1350` | `RenderTileAltar#renderTileEntityAt` | в начале, добавляет код | оба | КТ-3 |  | ждёт | аптекарь из живого булыжника |
| H-107 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1361` | `TextureManager#bindTexture` | в начале, выходит, если true | оба | КТ-3 |  | ждёт | аптекарь из живого булыжника |
| H-108 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1372` | `BotaniaCreativeTab#hasSearchBar` | в начале, возвращает свой результат | оба | КТ-1 |  | ждёт |  |
| H-109 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1377` | `BlockGaiaHead#getItemIconName` | создаёт метод, возвращает свой результат | клиент | КТ-4 |  | ждёт |  |
| H-110 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1382` | `ItemManaMirror#getManaPool` | в начале, возвращает свой результат | оба | КТ-3 |  | ждёт | автор: «chunkloading fix» |
| H-111 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1398` | `ItemManaMirror#getBinding` | в начале, возвращает свой результат | оба | КТ-3 |  | ждёт | автор: «chunkloading fix» |
| H-112 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1412` | `ItemManaMirror#getDamage` | в конце, возвращает свой результат, читает результат метода | оба | КТ-3 |  | ждёт |  |
| H-113 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1416` | `ItemManaMirror#getManaFractionForDisplay` | в конце, возвращает свой результат, читает результат метода | оба | КТ-3 |  | ждёт |  |
| H-114 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1420` | `ItemManaMirror#getColorFromItemStack` | в начале, возвращает свой результат | оба | КТ-3 |  | ждёт |  |
| H-115 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1427` | `ItemManaResource#canFit` | в начале, выходит, если true | оба | КТ-3 |  | ждёт |  |
| H-116 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1434` | `HUDHandler#renderManaInvBar` | в начале, выходит, если true | оба | КТ-3 |  | ждёт |  |
| H-117 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1447` | `HUDHandler#drawSimpleManaHUD` | в начале, добавляет код | оба | КТ-3 |  | ждёт |  |
| H-118 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1455` | `HUDHandler#renderManaBar` | в конце, добавляет код | оба | КТ-3 |  | ждёт |  |
| H-119 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1469` | `ItemRenderer#renderOverlays` | в начале, добавляет код | клиент | КТ-2 |  | ждёт |  |
| H-120 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1478` | `BossBarHandler#setCurrentBoss` | в начале, возвращает свой результат | оба | КТ-8 |  | ждёт |  |
| H-121 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1484` | `BossBarHandler#render` | в начале, добавляет код | оба | КТ-8 |  | ждёт |  |
| H-122 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1491` | `StatCollector#translateToLocal` | в начале, добавляет код | оба | КТ-3 |  | ждёт |  |
| H-123 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1501` | `FontRenderer#drawString` | в начале, возвращает свой результат | клиент | КТ-2 |  | ждёт |  |
| H-124 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1508` | `FontRenderer#drawStringWithShadow` | в начале, возвращает свой результат | клиент | КТ-8 |  | ждёт |  |
| H-125 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1531` | `TooltipAdditionDisplayHandler#drawManaBar` | в начале, добавляет код | оба | КТ-3 |  | ждёт |  |
| H-126 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1545` | `FXWisp#<init>` | в конце, добавляет код | оба | КТ-2 |  | ждёт | частицы-огоньки Botania |
| H-127 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1551` | `FXWisp#<clinit>` | в конце, добавляет код | оба | КТ-2 |  | ждёт | частицы-огоньки Botania |
| H-128 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1560` | `FXWisp#renderQueued` | в начале, добавляет код | оба | КТ-2 |  | ждёт | частицы-огоньки Botania |
| H-129 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1566` | `FXWisp#dispatchQueuedRenders` | в конце, добавляет код | оба | КТ-2 |  | ждёт | частицы-огоньки Botania |
| H-130 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1572` | `FXSparkle#renderQueued` | в начале, выходит, если true | оба | КТ-3 |  | ждёт |  |
| H-131 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1579` | `EntityPlayer#isInvisibleToPlayer` | в конце, возвращает свой результат, читает результат метода | клиент | КТ-7 |  | ждёт | невидимость членов группы |
| H-132 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1584` | `Entity#isInvisibleToPlayer` | в конце, возвращает свой результат, читает результат метода | клиент | КТ-7 |  | ждёт |  |
| H-133 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1596` | `MovementInputFromOptions#updatePlayerMoveState` | в начале, выходит, если true | клиент | КТ-6 |  | ждёт |  |
| H-134 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1633` | `ItemLexicon#getEntryFromForce` | в начале, добавляет код | оба | КТ-9 |  | ждёт |  |
| H-135 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1642` | `ItemLexicon#getEntryFromForce` | в конце, добавляет код, читает результат метода | оба | КТ-9 |  | ждёт |  |
| H-136 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1649` | `WorldProvider#calculateCelestialAngle` | в начале, выходит, если true, результат `0.5f` | оба | КТ-8 |  | ждёт | Рагнарёк: нет солнца и луны |
| H-137 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1654` | `RenderGlobal#renderSky` | в начале, добавляет код | клиент | КТ-8 |  | ждёт | Рагнарёк: нет солнца и луны |
| H-138 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1670` | `RenderGlobal#renderSky` | в конце, добавляет код | клиент | КТ-8 |  | ждёт | Рагнарёк: нет солнца и луны |
| H-139 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1679` | `End#addComponentParts` | в начале, выходит, если true | оба | КТ-6 |  | ждёт | вход в домен Сурта в адской крепости |
| H-140 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1797` | `ItemTerraformRod#<clinit>` | в конце, добавляет код | оба | КТ-4 |  | ждёт |  |
| H-141 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1808` | `EntitySnowball#onImpact` | в конце, добавляет код | оба | КТ-8 |  | ждёт |  |
| H-142 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1821` | `Block#getRelativeSlipperiness` | создаёт метод, возвращает свой результат | оба | КТ-4 |  | ждёт |  |
| H-143 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1831` | `BlockStem#updateTick` | в начале, добавляет код | оба | КТ-8 |  | ждёт | Hellish Vacation: арбузы-мобы |
| H-144 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1837` | `World#setBlock` | в начале, выходит, если true, результат `false` | оба | КТ-8 |  | ждёт | Hellish Vacation: арбузы-мобы |
| H-145 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1849` | `BlockStem#updateTick` | в конце, добавляет код | оба | КТ-8 |  | ждёт | Hellish Vacation: арбузы-мобы |
| H-146 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1855` | `MapGenNetherBridge#<init>` | в конце, добавляет код | оба | КТ-8 |  | ждёт |  |
| H-147 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1861` | `ItemPickaxe#func_150893_a` | в начале, выходит, если true, результат — из `getDigSpeed` | оба | КТ-2 |  | ждёт |  |
| H-148 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1868` | `World#getCollidingBoundingBoxes` | в конце, добавляет код, приоритет HIGH, читает результат метода | оба | КТ-6 |  | ждёт | летающие острова |
| H-149 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1886` | `HooksCore#getEntityCollisionBoxes` | в начале, выходит, если не null | оба | — |  | выпало (Thermal Foundation: мод отсутствует на 1.20.1) |  |
| H-150 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1892` | `World#getPathEntityToEntity` | в конце, возвращает свой результат, читает результат метода | оба | КТ-6 |  | ждёт | летающие острова |
| H-151 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1916` | `World#getEntityPathToXYZ` | в конце, возвращает свой результат, читает результат метода | оба | КТ-6 |  | ждёт | летающие острова |
| H-152 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1941` | `RenderGlobal#loadRenderers` | в начале, добавляет код | клиент | КТ-6 |  | ждёт | летающие острова |
| H-153 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1948` | `TileRuneAltar#saveLastRecipe` | в конце, добавляет код | оба | КТ-3 |  | ждёт | полный рецепт рунного алтаря |
| H-154 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1971` | `ThaumcraftCraftingManager#getObjectTags` | в начале, выходит, если true, результат `null` | оба | — |  | выпало (Thaumcraft: мод отсутствует на 1.20.1) |  |
| H-155 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1975` | `ThaumcraftCraftingManager#getBonusTags` | в начале, выходит, если true, результат `null` | оба | — |  | выпало (Thaumcraft: мод отсутствует на 1.20.1) |  |
| H-156 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1979` | `EnumEnchantmentType#canEnchantItem` | в начале, выходит, если true | оба | КТ-4 |  | ждёт |  |
| H-157 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1983` | `ItemStack#damageItem` | в начале, выходит, если true | оба | КТ-2 |  | ждёт | ивы сохранения (`RecipeSaveIvy`) |
| H-158 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1988` | `EntityAIAvoidEntity#shouldExecute` | в начале, выходит, если true, результат `false` | оба | КТ-7 |  | ждёт |  |
| H-159 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:1994` | `BiomeGenBase#getFloatTemperature` | в конце, возвращает свой результат, читает результат метода | оба | КТ-8 |  | ждёт |  |
| H-160 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:2007` | `BlockPlatform#onBlockClicked` | создаёт метод, возвращает свой результат | оба | КТ-3 |  | ждёт | абстрактная платформа Botania |
| H-161 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:2015` | `TileSpecialFlower#readCustomNBT` | в начале, добавляет код | оба | КТ-3 |  | ждёт |  |
| H-162 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:2025` | `ItemBottledMana#onUpdate` | в начале, возвращает свой результат | оба | КТ-3 |  | ждёт |  |
| H-163 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:2029` | `ItemOdinRing#fillModifiers` | в начале, возвращает свой результат | оба | КТ-4 |  | ждёт |  |
| H-164 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:2037` | `ItemAesirRing#fillModifiers` | в начале, возвращает свой результат | оба | КТ-4 |  | ждёт |  |
| H-165 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:2045` | `EntityPlayer#attackTargetEntityWithCurrentItem` | в конце, добавляет код | оба | КТ-1 |  | ждёт | событие `AttackEntityEventPost` |
| H-166 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:2051` | `HugeItemRenderer#renderItem` | в начале, добавляет код | оба | КТ-10 |  | ждёт | правка рендера предметов ExtraBotany 1.7.10 (`IItemRenderer`); в 1.20.1 — проверка с ExtraBotany: Reburn (SPEC п. 7) |
| H-167 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:2058` | `GunRenderer#renderItem` | в начале, добавляет код | оба | КТ-10 |  | ждёт | правка рендера предметов ExtraBotany 1.7.10 (`IItemRenderer`); в 1.20.1 — проверка с ExtraBotany: Reburn (SPEC п. 7) |
| H-168 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:2065` | `ItemGravityRod#setEntityMotionFromVector` | в конце, добавляет код | оба | КТ-4 |  | ждёт |  |
| H-169 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:2071` | `RenderPlayer#getColorMultiplier` | создаёт метод, возвращает свой результат | оба | КТ-7 |  | ждёт | облик участника команды автора (SPEC п. 8) |
| H-170 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:2081` | `EntityWolf#getCollarColor` | в начале, возвращает свой результат | оба | КТ-2 |  | ждёт | цвет ошейника волка |
| H-171 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:2085` | `EntityWolf#setCollarColor` | в начале, возвращает свой результат | оба | КТ-2 |  | ждёт | цвет ошейника волка |
| H-172 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:2089` | `EntityWolf#interact` | в начале, выходит, если true | оба | КТ-2 |  | ждёт | цвет ошейника волка |
| H-173 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:2111` | `TileAlfPortal#addItem` | в начале, выходит, если true | оба | КТ-3 |  | ждёт | обмен в портале эльфов |
| H-174 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:2143` | `RenderTileRuneAltar#renderTileEntityAt` | в начале, добавляет код | оба | КТ-3 |  | ждёт |  |
| H-175 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:2149` | `ContainerRepair#canInteractWith` | в начале, возвращает свой результат | оба | КТ-3 |  | ждёт | эльфийская наковальня |
| H-176 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:2158` | `SheddingHandler#onLivingUpdate` | в начале, выходит, если true | оба | КТ-3 |  | ждёт | линька животных Botania |
| H-177 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:2171` | `EntityOcelot#updateAITick` | в начале, добавляет код, необязательная | оба | КТ-5 |  | ждёт | пасхалка «oiia»: кружащиеся оцелоты |
| H-178 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:2201` | `ModelOcelot#render` | в начале, добавляет код, необязательная | клиент | КТ-5 |  | ждёт | пасхалка «oiia»: кружащиеся оцелоты |
| H-179 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:2224` | `ModelOcelot#setRotationAngles` | в начале, выходит, если true | клиент | КТ-5 |  | ждёт | пасхалка «oiia»: кружащиеся оцелоты |
| H-180 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:2248` | `ModelOcelot#render` | в конце, добавляет код, необязательная | клиент | КТ-5 |  | ждёт | пасхалка «oiia»: кружащиеся оцелоты |
| H-181 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:2259` | `BlockFire#onBlockAdded` | в начале, выходит, если true | оба | КТ-8 |  | ждёт |  |
| H-182 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:2268` | `WorldGenFire#generate` | в начале, добавляет код | оба | КТ-8 |  | ждёт | красное пламя в Аду |
| H-183 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:2277` | `WorldGenFire#generate` | в конце, добавляет код | оба | КТ-8 |  | ждёт | красное пламя в Аду |
| H-184 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:2284` | `IslandType#getColor` | создаёт метод, возвращает свой результат | оба | КТ-3 |  | ждёт |  |
| H-185 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:2288` | `EntitySkeleton#immuneToMuspel` | создаёт метод, возвращает свой результат | оба | КТ-5 |  | ждёт |  |
| H-186 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:2292` | `EntitySkeleton#immuneToNifl` | создаёт метод, возвращает свой результат | оба | КТ-5 |  | ждёт |  |
| H-187 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:2297` | `EntityAgeable#onLivingUpdate` | в конце, добавляет код | оба | КТ-5 |  | ждёт | блокировка возраста животных |
| H-188 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:2314` | `EntityAgeable#addGrowth` | в начале, выходит, если true | оба | КТ-5 |  | ждёт | блокировка возраста животных |
| H-189 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:2319` | `World#getClosestVulnerablePlayerToEntity` | в конце, возвращает свой результат, читает результат метода | оба | КТ-2 |  | ждёт |  |
| H-190 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimHookHandler.kt:2326` | `EntitySlime#createInstance` | в конце, добавляет код, читает результат метода | оба | КТ-3 |  | ждёт |  |
| H-191 | `legacy/src/main/java/alfheim/common/core/asm/hook/Botania18AndUpBackport.kt:48` | `SubTileRannuncarpus#onUpdate` | в начале, добавляет код | оба | КТ-3 |  | ждёт |  |
| H-192 | `legacy/src/main/java/alfheim/common/core/asm/hook/Botania18AndUpBackport.kt:68` | `World#getEntitiesWithinAABB` | в конце, добавляет код, читает результат метода | оба | КТ-3 |  | ждёт |  |
| H-193 | `legacy/src/main/java/alfheim/common/core/asm/hook/Botania18AndUpBackport.kt:97` | `SubTileFallenKanade#onUpdate` | в конце, добавляет код | оба | КТ-3 |  | ждёт |  |
| H-194 | `legacy/src/main/java/alfheim/common/core/asm/hook/Botania18AndUpBackport.kt:121` | `ItemGrassHorn#breakGrass` | в начале, добавляет код | оба | КТ-3 |  | ждёт | рог травы Botania |
| H-195 | `legacy/src/main/java/alfheim/common/core/asm/hook/Botania18AndUpBackport.kt:127` | `Block#isLeaves` | в начале, выходит, если true | оба | КТ-3 |  | ждёт | рог травы Botania |
| H-196 | `legacy/src/main/java/alfheim/common/core/asm/hook/Botania18AndUpBackport.kt:131` | `ItemGrassHorn#breakGrass` | в начале, добавляет код | оба | КТ-3 |  | ждёт | рог травы Botania |
| H-197 | `legacy/src/main/java/alfheim/common/core/asm/hook/Botania18AndUpBackport.kt:139` | `ItemLokiRing#onUnequipped` | в начале, возвращает свой результат | оба | КТ-4 |  | ждёт |  |
| H-198 | `legacy/src/main/java/alfheim/common/core/asm/hook/Botania18AndUpBackport.kt:161` | `SubTileNarslimmus#getMaxMana` | в начале, возвращает свой результат | оба | КТ-3 |  | ждёт |  |
| H-199 | `legacy/src/main/java/alfheim/common/core/asm/hook/Botania18AndUpBackport.kt:167` | `InternalMethodHandler#isBotaniaFlower` | в конце, возвращает свой результат, читает результат метода | оба | КТ-3 |  | ждёт |  |
| H-200 | `legacy/src/main/java/alfheim/common/core/asm/hook/Botania18AndUpBackport.kt:179` | `ClientProxy#playRecordClientSided` | в начале, возвращает свой результат | клиент | КТ-3 |  | ждёт | проигрыватель Botania |
| H-201 | `legacy/src/main/java/alfheim/common/core/asm/hook/Botania18AndUpBackport.kt:189` | `ItemGrassHorn#breakGrass` | в начале, выходит, если true | оба | КТ-3 |  | ждёт | рог травы Botania |
| H-202 | `legacy/src/main/java/alfheim/common/core/asm/hook/Botania18AndUpBackport.kt:198` | `ItemDice#onItemRightClick` | в начале, возвращает свой результат | оба | КТ-4 |  | ждёт |  |
| H-203 | `legacy/src/main/java/alfheim/common/core/asm/hook/Botania18AndUpBackport.kt:230` | `EntityMagicLandmine#onUpdate` | в начале, добавляет код | оба | КТ-8 |  | ждёт | мины Гайи |
| H-204 | `legacy/src/main/java/alfheim/common/core/asm/hook/Botania18AndUpBackport.kt:238` | `EntityPlayer#attackEntityFrom` | в начале, добавляет код | оба | КТ-8 |  | ждёт | мины Гайи |
| H-205 | `legacy/src/main/java/alfheim/common/core/asm/hook/Botania18AndUpBackport.kt:245` | `EntityMagicLandmine#onUpdate` | в конце, добавляет код | оба | КТ-8 |  | ждёт | мины Гайи |
| H-206 | `legacy/src/main/java/alfheim/common/core/asm/hook/Botania18AndUpBackport.kt:253` | `SubTileArcaneRose#onUpdate` | в начале, добавляет код | оба | КТ-3 |  | ждёт |  |
| H-207 | `legacy/src/main/java/alfheim/common/core/asm/hook/ElementalDamageAdapter.kt:50` | `DamageSource#<init>` | в конце, добавляет код | оба | КТ-4 |  | ждёт |  |
| H-208 | `legacy/src/main/java/alfheim/common/core/asm/hook/ElementalDamageAdapter.kt:56` | `DamageSource#causeIndirectMagicDamage` | в конце, возвращает свой результат, читает результат метода | оба | КТ-4 |  | ждёт |  |
| H-209 | `legacy/src/main/java/alfheim/common/core/asm/hook/ElementalDamageAdapter.kt:62` | `DamageSource#causeMobDamage` | в конце, возвращает свой результат, читает результат метода | оба | КТ-4 |  | ждёт |  |
| H-210 | `legacy/src/main/java/alfheim/common/core/asm/hook/ElementalDamageAdapter.kt:68` | `DamageSource#causePlayerDamage` | в конце, возвращает свой результат, читает результат метода | оба | КТ-4 |  | ждёт |  |
| H-211 | `legacy/src/main/java/alfheim/common/core/asm/hook/ElementalDamageAdapter.kt:74` | `DamageSource#causeThrownDamage` | в конце, возвращает свой результат, читает результат метода | оба | КТ-4 |  | ждёт |  |
| H-212 | `legacy/src/main/java/alfheim/common/core/asm/hook/ElementalDamageAdapter.kt:96` | `ItemThunderSword#hitEntity` | в начале, добавляет код | оба | КТ-4 |  | ждёт |  |
| H-213 | `legacy/src/main/java/alfheim/common/core/asm/hook/ElementalDamageAdapter.kt:103` | `ItemThunderSword#hitEntity` | в конце, добавляет код | оба | КТ-4 |  | ждёт |  |
| H-214 | `legacy/src/main/java/alfheim/common/core/asm/hook/ElementalDamageAdapter.kt:111` | `ItemTerraSword#updateBurst` | в начале, добавляет код | оба | КТ-4 |  | ждёт |  |
| H-215 | `legacy/src/main/java/alfheim/common/core/asm/hook/ElementalDamageAdapter.kt:117` | `ItemTerraSword#updateBurst` | в конце, добавляет код | оба | КТ-4 |  | ждёт |  |
| H-216 | `legacy/src/main/java/alfheim/common/core/asm/hook/ElementalDamageAdapter.kt:124` | `EntityThornChakram#onImpact` | в начале, добавляет код | оба | КТ-4 |  | ждёт |  |
| H-217 | `legacy/src/main/java/alfheim/common/core/asm/hook/ElementalDamageAdapter.kt:130` | `EntityThornChakram#onImpact` | в конце, добавляет код | оба | КТ-4 |  | ждёт |  |
| H-218 | `legacy/src/main/java/alfheim/common/core/asm/hook/ElementalDamageAdapter.kt:137` | `EntityBabylonWeapon#onUpdate` | в начале, добавляет код | оба | КТ-4 |  | ждёт |  |
| H-219 | `legacy/src/main/java/alfheim/common/core/asm/hook/ElementalDamageAdapter.kt:143` | `EntityBabylonWeapon#onUpdate` | в конце, добавляет код | оба | КТ-4 |  | ждёт |  |
| H-220 | `legacy/src/main/java/alfheim/common/core/asm/hook/ElementalDamageAdapter.kt:150` | `ItemElementalSword#attackTargetEntityWithCurrentItem` | в начале, добавляет код | оба | — |  | выпало (Thaumcraft: мод отсутствует на 1.20.1) | цель — класс Thaumcraft |
| H-221 | `legacy/src/main/java/alfheim/common/core/asm/hook/ElementalDamageAdapter.kt:156` | `ItemElementalSword#attackTargetEntityWithCurrentItem` | в конце, добавляет код | оба | — |  | выпало (Thaumcraft: мод отсутствует на 1.20.1) | цель — класс Thaumcraft |
| H-222 | `legacy/src/main/java/alfheim/common/core/asm/hook/ElementalDamageAdapter.kt:163` | `EntityDamageSource#<init>` | в конце, добавляет код | оба | КТ-4 |  | ждёт |  |
| H-223 | `legacy/src/main/java/alfheim/common/core/asm/hook/ElementalDamageAdapter.kt:181` | `EntityDamageSourceIndirect#<init>` | в конце, добавляет код | оба | КТ-4 |  | ждёт |  |
| H-224 | `legacy/src/main/java/alfheim/common/core/asm/hook/ElementalDamageAdapter.kt:208` | `ItemFocusShock#doLightningBolt` | в начале, добавляет код | оба | — |  | выпало (Thaumcraft: мод отсутствует на 1.20.1) | цель — класс Thaumcraft |
| H-225 | `legacy/src/main/java/alfheim/common/core/asm/hook/ElementalDamageAdapter.kt:214` | `ItemFocusShock#doLightningBolt` | в конце, добавляет код | оба | — |  | выпало (Thaumcraft: мод отсутствует на 1.20.1) | цель — класс Thaumcraft |
| H-226 | `legacy/src/main/java/alfheim/common/core/asm/hook/ElementalDamageAdapter.kt:221` | `EntityDedMoroz#attackEntityAsMob` | в начале, добавляет код | оба | КТ-8 |  | ждёт | стихийный урон существ Сурта и Деда Мороза |
| H-227 | `legacy/src/main/java/alfheim/common/core/asm/hook/ElementalDamageAdapter.kt:227` | `EntityDedMoroz#attackEntityAsMob` | в конце, добавляет код | оба | КТ-8 |  | ждёт | стихийный урон существ Сурта и Деда Мороза |
| H-228 | `legacy/src/main/java/alfheim/common/core/asm/hook/ElementalDamageAdapter.kt:234` | `EntityMuspelson#attackEntityAsMob` | в начале, добавляет код | оба | КТ-8 |  | ждёт | стихийный урон существ Сурта и Деда Мороза |
| H-229 | `legacy/src/main/java/alfheim/common/core/asm/hook/ElementalDamageAdapter.kt:240` | `EntityMuspelson#attackEntityAsMob` | в конце, добавляет код | оба | КТ-8 |  | ждёт | стихийный урон существ Сурта и Деда Мороза |
| H-230 | `legacy/src/main/java/alfheim/common/core/asm/hook/ElementalDamageAdapter.kt:247` | `EntityEldritchOrb#onImpact` | в начале, добавляет код | оба | — |  | выпало (Thaumcraft: мод отсутствует на 1.20.1) | цель — класс Thaumcraft |
| H-231 | `legacy/src/main/java/alfheim/common/core/asm/hook/ElementalDamageAdapter.kt:253` | `EntityEldritchOrb#onImpact` | в конце, добавляет код | оба | — |  | выпало (Thaumcraft: мод отсутствует на 1.20.1) | цель — класс Thaumcraft |
| H-232 | `legacy/src/main/java/alfheim/common/core/asm/hook/ElementalDamageAdapter.kt:260` | `EntityGolemOrb#onImpact` | в начале, добавляет код | оба | — |  | выпало (Thaumcraft: мод отсутствует на 1.20.1) | цель — класс Thaumcraft |
| H-233 | `legacy/src/main/java/alfheim/common/core/asm/hook/ElementalDamageAdapter.kt:266` | `EntityGolemOrb#onImpact` | в конце, добавляет код | оба | — |  | выпало (Thaumcraft: мод отсутствует на 1.20.1) | цель — класс Thaumcraft |
| H-234 | `legacy/src/main/java/alfheim/common/core/asm/hook/ElementalDamageAdapter.kt:273` | `EntityFireBat#attackEntity` | в начале, добавляет код | оба | — |  | выпало (Thaumcraft: мод отсутствует на 1.20.1) | цель — класс Thaumcraft |
| H-235 | `legacy/src/main/java/alfheim/common/core/asm/hook/ElementalDamageAdapter.kt:279` | `EntityFireBat#attackEntity` | в конце, добавляет код | оба | — |  | выпало (Thaumcraft: мод отсутствует на 1.20.1) | цель — класс Thaumcraft |
| H-236 | `legacy/src/main/java/alfheim/common/core/asm/hook/ElementalDamageAdapter.kt:286` | `EntityMindSpider#attackEntity` | в начале, добавляет код | оба | — |  | выпало (Thaumcraft: мод отсутствует на 1.20.1) | цель — класс Thaumcraft |
| H-237 | `legacy/src/main/java/alfheim/common/core/asm/hook/ElementalDamageAdapter.kt:292` | `EntityMindSpider#attackEntity` | в конце, добавляет код | оба | — |  | выпало (Thaumcraft: мод отсутствует на 1.20.1) | цель — класс Thaumcraft |
| H-238 | `legacy/src/main/java/alfheim/common/core/asm/hook/ElementalDamageAdapter.kt:299` | `EntityThaumicSlime#onCollideWithPlayer` | в начале, добавляет код | оба | — |  | выпало (Thaumcraft: мод отсутствует на 1.20.1) | цель — класс Thaumcraft |
| H-239 | `legacy/src/main/java/alfheim/common/core/asm/hook/ElementalDamageAdapter.kt:305` | `EntityThaumicSlime#onCollideWithPlayer` | в конце, добавляет код | оба | — |  | выпало (Thaumcraft: мод отсутствует на 1.20.1) | цель — класс Thaumcraft |
| H-240 | `legacy/src/main/java/alfheim/common/core/asm/hook/ElementalDamageAdapter.kt:312` | `EntityWisp#updateEntityActionState` | в начале, добавляет код | оба | — |  | выпало (Thaumcraft: мод отсутствует на 1.20.1) | цель — класс Thaumcraft |
| H-241 | `legacy/src/main/java/alfheim/common/core/asm/hook/ElementalDamageAdapter.kt:329` | `EntityWisp#updateEntityActionState` | в конце, добавляет код | оба | — |  | выпало (Thaumcraft: мод отсутствует на 1.20.1) | цель — класс Thaumcraft |
| H-242 | `legacy/src/main/java/alfheim/common/core/asm/hook/ElementalDamageAdapter.kt:347` | `EntityCreeper#getElements` | создаёт метод, возвращает свой результат | оба | КТ-4 |  | ждёт |  |
| H-243 | `legacy/src/main/java/alfheim/common/core/asm/hook/ElementalDamageAdapter.kt:356` | `EntitySkeleton#getElements` | создаёт метод, возвращает свой результат | оба | КТ-4 |  | ждёт |  |
| H-244 | `legacy/src/main/java/alfheim/common/core/asm/hook/ElementalDamageAdapter.kt:361` | `EntityWisp#getElements` | создаёт метод, возвращает свой результат | оба | — |  | выпало (Thaumcraft: мод отсутствует на 1.20.1) | цель — класс Thaumcraft |
| H-245 | `legacy/src/main/java/alfheim/common/core/asm/hook/ElementalDamageAdapter.kt:377` | `EntityGolemBase#getElements` | создаёт метод, возвращает свой результат | оба | — |  | выпало (Thaumcraft: мод отсутствует на 1.20.1) | цель — класс Thaumcraft |
| H-246 | `legacy/src/main/java/alfheim/common/core/asm/hook/ElementalDamageAdapter.kt:389` | `ItemRelic#damageSource` | в конце, возвращает свой результат, читает результат метода | оба | КТ-4 |  | ждёт | автор: «util» |
| H-247 | `legacy/src/main/java/alfheim/common/core/asm/hook/ElementalDamageAdapter.kt:395` | `DamageSource#setFireDamage` | в начале, добавляет код | оба | КТ-4 |  | ждёт |  |
| H-248 | `legacy/src/main/java/alfheim/common/core/asm/hook/ElementalDamageAdapter.kt:401` | `Entity#onStruckByLightning` | в начале, возвращает свой результат | оба | КТ-4 |  | ждёт |  |
| H-249 | `legacy/src/main/java/alfheim/common/core/asm/hook/ElementalDamageAdapter.kt:408` | `RenderWisp#doRender` | в конце, добавляет код | оба | — |  | выпало (Thaumcraft: мод отсутствует на 1.20.1) | цель — класс Thaumcraft; автор: «wisp elements render fix» |
| H-250 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/FlowerBagExtender.kt:38` | `ItemFlowerBag#onPickupItem` | в начале, возвращает свой результат | оба | КТ-3 |  | ждёт |  |
| H-251 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/FlowerBagExtender.kt:74` | `InventoryFlowerBag#<clinit>` | в конце, добавляет код | оба | КТ-3 |  | ждёт |  |
| H-252 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/FlowerBagExtender.kt:80` | `InventoryFlowerBag#getSizeInventory` | в начале, возвращает свой результат | оба | КТ-3 |  | ждёт |  |
| H-253 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/FlowerBagExtender.kt:86` | `GuiFlowerBag#<clinit>` | в конце, добавляет код | оба | КТ-3 |  | ждёт |  |
| H-254 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/FlowerBagExtender.kt:92` | `GuiFlowerBag#<init>` | в конце, добавляет код | оба | КТ-3 |  | ждёт |  |
| H-255 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/FlowerBagExtender.kt:98` | `GuiFlowerBag#drawGuiContainerForegroundLayer` | в начале, возвращает свой результат | оба | КТ-3 |  | ждёт |  |
| H-256 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/FlowerBagExtender.kt:106` | `GuiFlowerBag#drawGuiContainerBackgroundLayer` | в начале, возвращает свой результат | оба | КТ-3 |  | ждёт |  |
| H-257 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/FlowerBagExtender.kt:139` | `ContainerFlowerBag#<init>` | в конце, добавляет код | оба | КТ-3 |  | ждёт |  |
| H-258 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/FlowerBagExtender.kt:169` | `ContainerFlowerBag#transferStackInSlot` | в начале, возвращает свой результат | оба | КТ-3 |  | ждёт |  |
| H-259 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/FurnaceExtender.kt:26` | `BlockFurnace#damageDropped` | создаёт метод, возвращает свой результат | оба | КТ-3 |  | ждёт |  |
| H-260 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/FurnaceExtender.kt:30` | `BlockFurnace#func_149930_e` | в начале, выходит, если true | оба | КТ-3 |  | ждёт |  |
| H-261 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/FurnaceExtender.kt:62` | `RenderBlocks#renderBlockAsItem` | в начале, добавляет код | оба | КТ-3 |  | ждёт |  |
| H-262 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/FurnaceExtender.kt:71` | `BlockFurnace#getIcon` | в начале, выходит, если не null | оба | КТ-3 |  | ждёт |  |
| H-263 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/FurnaceExtender.kt:79` | `RenderBlocks#renderBlockAsItem` | в конце, добавляет код | оба | КТ-3 |  | ждёт |  |
| H-264 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/FurnaceExtender.kt:86` | `BlockFurnace#registerBlockIcons` | в начале, добавляет код | оба | КТ-3 |  | ждёт |  |
| H-265 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/FurnaceExtender.kt:98` | `BlockFurnace#onBlockPlacedBy` | в конце, добавляет код | оба | КТ-3 |  | ждёт |  |
| H-266 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/FurnaceExtender.kt:107` | `BlockFurnace#randomDisplayTick` | в начале, добавляет код | оба | КТ-3 |  | ждёт |  |
| H-267 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/FurnaceExtender.kt:116` | `World#getBlockMetadata` | в конце, возвращает свой результат, читает результат метода | оба | КТ-3 |  | ждёт |  |
| H-268 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/ItemAuraRingExtender.kt:12` | `ItemAuraRing#getMana` | создаёт метод, возвращает свой результат | оба | КТ-3 |  | ждёт |  |
| H-269 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/ItemAuraRingExtender.kt:16` | `ItemAuraRing#getMaxMana` | создаёт метод, возвращает свой результат | оба | КТ-3 |  | ждёт |  |
| H-270 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/ItemAuraRingExtender.kt:20` | `ItemAuraRing#addMana` | создаёт метод, возвращает свой результат | оба | КТ-3 |  | ждёт |  |
| H-271 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/ItemAuraRingExtender.kt:24` | `ItemAuraRing#canReceiveManaFromPool` | создаёт метод, возвращает свой результат | оба | КТ-3 |  | ждёт |  |
| H-272 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/ItemAuraRingExtender.kt:28` | `ItemAuraRing#canReceiveManaFromItem` | создаёт метод, возвращает свой результат | оба | КТ-3 |  | ждёт |  |
| H-273 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/ItemAuraRingExtender.kt:32` | `ItemAuraRing#canExportManaToPool` | создаёт метод, возвращает свой результат | оба | КТ-3 |  | ждёт |  |
| H-274 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/ItemAuraRingExtender.kt:36` | `ItemAuraRing#canExportManaToItem` | создаёт метод, возвращает свой результат | оба | КТ-3 |  | ждёт |  |
| H-275 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/ItemAuraRingExtender.kt:40` | `ItemAuraRing#isNoExport` | создаёт метод, возвращает свой результат | оба | КТ-3 |  | ждёт |  |
| H-276 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/ItemLensExtender.kt:45` | `ItemLens#<clinit>` | в конце, добавляет код | оба | КТ-3 |  | ждёт |  |
| H-277 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/ItemLensExtender.kt:54` | `ItemLens#getSubItems` | в конце, добавляет код | оба | КТ-3 |  | ждёт |  |
| H-278 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/ItemLensExtender.kt:63` | `ItemLens#registerIcons` | в конце, добавляет код | клиент | КТ-3 |  | ждёт |  |
| H-279 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/ItemLensExtender.kt:70` | `ItemLens#getIconFromDamageForRenderPass` | в конце, выходит, если не null | клиент | КТ-3 |  | ждёт |  |
| H-280 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/ItemLensExtender.kt:74` | `ItemLens#allowBurstShooting` | в начале, возвращает свой результат | оба | КТ-3 |  | ждёт |  |
| H-281 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/ItemLensExtender.kt:80` | `ItemLens#onControlledSpreaderTick` | в начале, возвращает свой результат | оба | КТ-3 |  | ждёт |  |
| H-282 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/ItemLensExtender.kt:86` | `ItemLens#onControlledSpreaderPulse` | в начале, возвращает свой результат | оба | КТ-3 |  | ждёт |  |
| H-283 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/ItemLensExtender.kt:92` | `ItemLens#getProps` | в начале, выходит, если true, результат `28` | оба | КТ-3 |  | ждёт |  |
| H-284 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/ItemLensExtender.kt:98` | `ItemLens#getUnlocalizedName` | в начале, выходит, если не null | оба | КТ-3 |  | ждёт |  |
| H-285 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/ItemLensExtender.kt:104` | `ItemLens#doesContainerItemLeaveCraftingGrid` | создаёт метод, возвращает свой результат | оба | КТ-3 |  | ждёт |  |
| H-286 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/ItemLensExtender.kt:108` | `ItemLens#hasContainerItem` | создаёт метод, возвращает свой результат | оба | КТ-3 |  | ждёт |  |
| H-287 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/ItemLensExtender.kt:112` | `ItemLens#getContainerItem` | создаёт метод, возвращает свой результат | оба | КТ-3 |  | ждёт |  |
| H-288 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/ItemTwigWandExtender.kt:45` | `ItemTwigWand#registerIcons` | в начале, возвращает свой результат | оба | КТ-3 |  | ждёт |  |
| H-289 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/ItemTwigWandExtender.kt:51` | `ItemTwigWand#getIcon` | в начале, возвращает свой результат | оба | КТ-3 |  | ждёт |  |
| H-290 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/ItemTwigWandExtender.kt:62` | `ItemTwigWand#getRenderPasses` | в начале, возвращает свой результат | оба | КТ-3 |  | ждёт |  |
| H-291 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/ItemTwigWandExtender.kt:66` | `ItemTwigWand#getSubItems` | в начале, возвращает свой результат | оба | КТ-3 |  | ждёт |  |
| H-292 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/ItemTwigWandExtender.kt:73` | `ItemTwigWand#getColorFromItemStack` | в начале, возвращает свой результат | оба | КТ-3 |  | ждёт |  |
| H-293 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/LensPaintExtender.kt:54` | `LensPaint#collideBurst` | в начале, возвращает свой результат | оба | КТ-3 |  | ждёт |  |
| H-294 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/LensPaintExtender.kt:195` | `RenderSheep#shouldRenderPass` | в конце, добавляет код, необязательная, читает результат метода | клиент | КТ-3 |  | ждёт |  |
| H-295 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/LensPaintExtender.kt:205` | `EntitySheep#writeEntityToNBT` | в конце, добавляет код, необязательная | оба | КТ-3 |  | ждёт |  |
| H-296 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/LensPaintExtender.kt:211` | `EntitySheep#readEntityFromNBT` | в конце, добавляет код, необязательная | оба | КТ-3 |  | ждёт |  |
| H-297 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/LightRelayExtender.kt:29` | `BlockLightRelay#getSubBlocks` | в конце, добавляет код | оба | КТ-3 |  | ждёт |  |
| H-298 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/LightRelayExtender.kt:35` | `BlockLightRelay#damageDropped` | в начале, возвращает свой результат | оба | КТ-3 |  | ждёт |  |
| H-299 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/LightRelayExtender.kt:39` | `BlockLightRelay#isProvidingWeakPower` | в начале, возвращает свой результат | оба | КТ-3 |  | ждёт |  |
| H-300 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/LightRelayExtender.kt:47` | `BlockLightRelay#registerBlockIcons` | в конце, добавляет код | клиент | КТ-3 |  | ждёт |  |
| H-301 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/LightRelayExtender.kt:57` | `BlockLightRelay#getIcon` | в начале, возвращает свой результат | клиент | КТ-3 |  | ждёт |  |
| H-302 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/LightRelayExtender.kt:68` | `TileLightRelay#isValidBinding` | в начале, возвращает свой результат | оба | КТ-3 |  | ждёт |  |
| H-303 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/LightRelayExtender.kt:75` | `TileLightRelay#getBinding` | в начале, возвращает свой результат | оба | КТ-3 |  | ждёт |  |
| H-304 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/LightRelayExtender.kt:110` | `RenderTileLightRelay#renderTileEntityAt` | в начале, добавляет код | клиент | КТ-3 |  | ждёт |  |
| H-305 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/LightRelayExtender.kt:124` | `RenderTileLightRelay#func_77026_a` | в начале, выходит, если true | оба | КТ-3 |  | ждёт |  |
| H-306 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/ManaSpreaderExtender.kt:49` | `BlockSpreader#registerBlockIcons` | в начале, добавляет код | оба | КТ-3 |  | ждёт |  |
| H-307 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/ManaSpreaderExtender.kt:55` | `BlockSpreader#getIcon` | в начале, выходит, если не null | оба | КТ-3 |  | ждёт |  |
| H-308 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/ManaSpreaderExtender.kt:63` | `BlockSpreader#getSubBlocks` | в конце, добавляет код | оба | КТ-3 |  | ждёт |  |
| H-309 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/ManaSpreaderExtender.kt:70` | `BlockSpreader#getEntry` | в начале, выходит, если не null | оба | КТ-3 |  | ждёт |  |
| H-310 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/ManaSpreaderExtender.kt:84` | `TileSpreader#getBurst` | в начале, добавляет код | оба | КТ-3 |  | ждёт |  |
| H-311 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/ManaSpreaderExtender.kt:93` | `BurstProperties#<init>` | в конце, добавляет код | оба | КТ-3 |  | ждёт |  |
| H-312 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/ManaSpreaderExtender.kt:115` | `TileSpreader#getMaxMana` | в начале, возвращает свой результат | оба | КТ-3 |  | ждёт |  |
| H-313 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/ManaSpreaderExtender.kt:143` | `RenderSpreader#renderInventoryBlock` | в начале, добавляет код | оба | КТ-3 |  | ждёт |  |
| H-314 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/ManaSpreaderExtender.kt:176` | `RenderTileSpreader#renderTileEntityAt` | в начале, добавляет код | оба | КТ-3 |  | ждёт |  |
| H-315 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/ManaSpreaderExtender.kt:185` | `ModelSpreader#render` | в конце, добавляет код | оба | КТ-3 |  | ждёт |  |
| H-316 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/PureDaisyExtender.kt:15` | `SubTilePureDaisy#onWanded` | создаёт метод, возвращает свой результат | оба | КТ-3 |  | ждёт |  |
| H-317 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/QuartzExtender.kt:20` | `BlockQuartz#<init>` | в конце, добавляет код | оба | КТ-3 |  | ждёт | автор: «################################ Vanilla block ################################» |
| H-318 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/QuartzExtender.kt:28` | `BlockQuartz#getIcon` | в начале, выходит, если не null | клиент | КТ-3 |  | ждёт |  |
| H-319 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/QuartzExtender.kt:33` | `BlockQuartz#getSubBlocks` | в конце, добавляет код | клиент | КТ-3 |  | ждёт |  |
| H-320 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/QuartzExtender.kt:42` | `BlockSpecialQuartz#<init>` | в конце, добавляет код | оба | КТ-3 |  | ждёт |  |
| H-321 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/QuartzExtender.kt:56` | `BlockSpecialQuartz#getNames` | в конце, возвращает свой результат, читает результат метода | оба | КТ-3 |  | ждёт |  |
| H-322 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/QuartzExtender.kt:77` | `BlockSpecialQuartz#getIcon` | в начале, выходит, если не null | клиент | КТ-3 |  | ждёт |  |
| H-323 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/QuartzExtender.kt:84` | `BlockSpecialQuartz#getSubBlocks` | в конце, добавляет код | клиент | КТ-3 |  | ждёт |  |
| H-324 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/QuartzExtender.kt:95` | `ItemBlockSpecialQuartz#getUnlocalizedName` | в начале, возвращает свой результат | оба | КТ-3 |  | ждёт |  |
| H-325 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/RelicHooks.kt:52` | `ItemRelic#getSoulbindUsernameS` | в начале, возвращает свой результат | оба | КТ-4 |  | ждёт |  |
| H-326 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/RelicHooks.kt:56` | `ItemRelic#bindToUsernameS` | в начале, возвращает свой результат | оба | КТ-4 |  | ждёт |  |
| H-327 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/RelicHooks.kt:63` | `ItemRelic#isRightPlayer` | в начале, выходит, если true | оба | КТ-4 |  | ждёт |  |
| H-328 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/RelicHooks.kt:69` | `ItemRelic#updateRelic` | в начале, возвращает свой результат | оба | КТ-4 |  | ждёт |  |
| H-329 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/RelicHooks.kt:89` | `ItemRelic#addBindInfo` | в начале, возвращает свой результат | оба | КТ-4 |  | ждёт |  |
| H-330 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/RelicHooks.kt:112` | `ItemRelicBauble#canEquip` | в начале, выходит, если true, результат `false` | оба | КТ-4 |  | ждёт |  |
| H-331 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/RelicHooks.kt:131` | `ItemNBTHelper#getNBT` | в начале, выходит, если не null | оба | КТ-4 |  | ждёт |  |
| H-332 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/RelicHooks.kt:135` | `AItemNBTHelper#getNBT` | в начале, выходит, если не null | оба | КТ-4 |  | ждёт |  |
| H-333 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/RelicHooks.kt:156` | `AItemNBTHelper#verifyExistance` | в начале, выходит, если true | оба | КТ-4 |  | ждёт |  |
| H-334 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/SparkExtender.kt:31` | `EntitySpark#entityInit` | в начале, добавляет код | оба | КТ-3 |  | ждёт |  |
| H-335 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/SparkExtender.kt:38` | `EntitySpark#getAttachedTile` | в начале, выходит, если не null | оба | КТ-3 |  | ждёт |  |
| H-336 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/SparkExtender.kt:45` | `EntitySpark#readEntityFromNBT` | в начале, добавляет код | оба | КТ-3 |  | ждёт |  |
| H-337 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/SparkExtender.kt:55` | `EntitySpark#writeEntityToNBT` | в начале, добавляет код | оба | КТ-3 |  | ждёт |  |
| H-338 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/SparkExtender.kt:62` | `TileEnchanter#attachSpark` | в начале, добавляет код | оба | КТ-3 |  | ждёт |  |
| H-339 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/SparkExtender.kt:68` | `TilePool#attachSpark` | в начале, добавляет код | оба | КТ-3 |  | ждёт |  |
| H-340 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/SparkExtender.kt:74` | `TileTerraPlate#attachSpark` | в начале, добавляет код | оба | КТ-3 |  | ждёт |  |
| H-341 | `legacy/src/main/java/alfheim/common/core/asm/hook/fixes/BotaniaGlowingRenderFixes.kt:21` | `BaubleRenderHandler#renderManaTablet` | в начале, добавляет код | клиент | КТ-4 |  | ждёт |  |
| H-342 | `legacy/src/main/java/alfheim/common/core/asm/hook/fixes/BotaniaGlowingRenderFixes.kt:29` | `BaubleRenderHandler#renderManaTablet` | в конце, добавляет код | клиент | КТ-4 |  | ждёт |  |
| H-343 | `legacy/src/main/java/alfheim/common/core/asm/hook/fixes/BotaniaGlowingRenderFixes.kt:36` | `ItemBloodPendant#onPlayerBaubleRender` | в начале, добавляет код | клиент | КТ-4 |  | ждёт |  |
| H-344 | `legacy/src/main/java/alfheim/common/core/asm/hook/fixes/BotaniaGlowingRenderFixes.kt:44` | `ItemBloodPendant#onPlayerBaubleRender` | в конце, добавляет код | клиент | КТ-4 |  | ждёт |  |
| H-345 | `legacy/src/main/java/alfheim/common/core/asm/hook/fixes/BotaniaGlowingRenderFixes.kt:51` | `RenderPixie#doRender` | в начале, добавляет код | клиент | КТ-4 |  | ждёт |  |
| H-346 | `legacy/src/main/java/alfheim/common/core/asm/hook/fixes/BotaniaGlowingRenderFixes.kt:59` | `RenderPixie#doRender` | в конце, добавляет код | клиент | КТ-4 |  | ждёт |  |
| H-347 | `legacy/src/main/java/alfheim/common/core/asm/hook/fixes/BotaniaGlowingRenderFixes.kt:66` | `ItemFlightTiara#onPlayerBaubleRender` | в начале, добавляет код | клиент | КТ-4 |  | ждёт |  |
| H-348 | `legacy/src/main/java/alfheim/common/core/asm/hook/fixes/BotaniaGlowingRenderFixes.kt:74` | `ItemFlightTiara#onPlayerBaubleRender` | в конце, добавляет код | клиент | КТ-4 |  | ждёт |  |
| H-349 | `legacy/src/main/java/alfheim/common/core/asm/hook/fixes/CorporeaInputFix.kt:18` | `NEIInputHandler#keyTyped` | в начале, возвращает свой результат | оба | КТ-10 |  | ждёт | ввод корпореи в NEI → JEI |
| H-350 | `legacy/src/main/java/alfheim/common/core/asm/hook/fixes/FlightTiaraFix.kt:29` | `ItemFlightTiara#onEquipped` | в конце, добавляет код | оба | КТ-4 |  | ждёт |  |
| H-351 | `legacy/src/main/java/alfheim/common/core/asm/hook/fixes/FlightTiaraFix.kt:83` | `ItemFlightTiara#onPlayerBaubleRender` | в начале, выходит, если true | клиент | КТ-4 |  | ждёт |  |
| H-352 | `legacy/src/main/java/alfheim/common/core/asm/hook/fixes/GodAttributesHooks.kt:34` | `BlockLiquid#addCollisionBoxesToList` | создаёт метод, возвращает свой результат | оба | КТ-4 |  | ждёт |  |
| H-353 | `legacy/src/main/java/alfheim/common/core/asm/hook/fixes/GodAttributesHooks.kt:73` | `EntityPlayer#addExhaustion` | в начале, возвращает свой результат | оба | КТ-4 |  | ждёт |  |
| H-354 | `legacy/src/main/java/alfheim/common/core/asm/hook/fixes/GodAttributesHooks.kt:85` | `ItemTerraAxe#breakOtherBlock` | в начале, возвращает свой результат | оба | КТ-4 |  | ждёт |  |
| H-355 | `legacy/src/main/java/alfheim/common/core/asm/hook/fixes/GodAttributesHooks.kt:108` | `EnchantmentHelper#func_151386_g` | в конце, возвращает свой результат, читает результат метода | оба | КТ-4 |  | ждёт |  |
| H-356 | `legacy/src/main/java/alfheim/common/core/asm/hook/fixes/GodAttributesHooks.kt:120` | `EntityBoat#onUpdate` | в начале, добавляет код | оба | КТ-4 |  | ждёт |  |
| H-357 | `legacy/src/main/java/alfheim/common/core/asm/hook/fixes/GodAttributesHooks.kt:130` | `EntityBoat#onUpdate` | в конце, добавляет код | оба | КТ-4 |  | ждёт |  |
| H-358 | `legacy/src/main/java/alfheim/common/core/asm/hook/fixes/GodAttributesHooks.kt:136` | `EntityBoat#setDead` | создаёт метод, возвращает свой результат | оба | КТ-4 |  | ждёт |  |
| H-359 | `legacy/src/main/java/alfheim/common/core/asm/hook/fixes/GodAttributesHooks.kt:142` | `EntityBoat#func_145778_a` | создаёт метод, возвращает свой результат | оба | КТ-4 |  | ждёт |  |
| H-360 | `legacy/src/main/java/alfheim/common/core/asm/hook/fixes/GodAttributesHooks.kt:148` | `ItemAesirRing#shouldHaveStepup` | создаёт метод, возвращает свой результат | оба | КТ-4 |  | ждёт |  |
| H-361 | `legacy/src/main/java/alfheim/common/core/asm/hook/fixes/GodAttributesHooks.kt:157` | `ItemTerraPick#breakOtherBlock` | в начале, добавляет код | оба | КТ-4 |  | ждёт |  |
| H-362 | `legacy/src/main/java/alfheim/common/core/asm/hook/fixes/GodAttributesHooks.kt:163` | `ItemThorRing#getThorRing` | в начале, выходит, если null | оба | КТ-4 |  | ждёт |  |
| H-363 | `legacy/src/main/java/alfheim/common/core/asm/hook/fixes/GodAttributesHooks.kt:167` | `ItemTerraPick#breakOtherBlock` | в конце, добавляет код | оба | КТ-4 |  | ждёт |  |
| H-364 | `legacy/src/main/java/alfheim/common/core/asm/hook/fixes/GodAttributesHooks.kt:175` | `ItemLokiRing#onPlayerInteract` | в начале, выходит, если true | оба | КТ-4 |  | ждёт |  |
| H-365 | `legacy/src/main/java/alfheim/common/core/asm/hook/fixes/GodAttributesHooks.kt:179` | `ItemLokiRing#breakOnAllCursors` | в начале, выходит, если true | оба | КТ-4 |  | ждёт |  |
| H-366 | `legacy/src/main/java/alfheim/common/core/asm/hook/fixes/GodAttributesHooks.kt:183` | `ItemLokiRing#getSourceWireframe` | в начале, выходит, если null | оба | КТ-4 |  | ждёт |  |
| H-367 | `legacy/src/main/java/alfheim/common/core/asm/hook/fixes/GodAttributesHooks.kt:188` | `ItemLokiRing#getWireframesToDraw` | в начале, выходит, если null | клиент | КТ-4 |  | ждёт |  |
| H-368 | `legacy/src/main/java/alfheim/common/core/asm/hook/fixes/GodAttributesHooks.kt:194` | `ItemOdinRing#onValidPlayerWornTick` | в начале, выходит, если true | оба | КТ-4 |  | ждёт |  |
| H-369 | `legacy/src/main/java/alfheim/common/core/asm/hook/fixes/GodAttributesHooks.kt:198` | `ItemOdinRing#onEquippedOrLoadedIntoWorld` | в начале, выходит, если true | оба | КТ-4 |  | ждёт |  |
| H-370 | `legacy/src/main/java/alfheim/common/core/asm/hook/fixes/GodAttributesHooks.kt:202` | `ItemOdinRing#onPlayerAttacked` | в начале, выходит, если true | оба | КТ-4 |  | ждёт |  |
| H-371 | `legacy/src/main/java/alfheim/common/core/asm/hook/fixes/RecipeAncientWillsFix.kt:18` | `AncientWillRecipe#matches` | в начале, возвращает свой результат | оба | КТ-4 |  | ждёт |  |
| H-372 | `legacy/src/main/java/alfheim/common/core/asm/hook/fixes/RecipeAncientWillsFix.kt:45` | `AncientWillRecipe#getCraftingResult` | в начале, возвращает свой результат | оба | КТ-4 |  | ждёт |  |
| H-373 | `legacy/src/main/java/alfheim/common/core/asm/hook/integration/BotaniaVisDiscountHooks.kt:17` | `ItemElementiumHelmRevealing#getVisDiscount` | создаёт метод, возвращает свой результат | оба | — |  | выпало (Thaumcraft: мод отсутствует на 1.20.1) |  |
| H-374 | `legacy/src/main/java/alfheim/common/core/asm/hook/integration/BotaniaVisDiscountHooks.kt:21` | `ItemManasteelHelmRevealing#getVisDiscount` | создаёт метод, возвращает свой результат | оба | — |  | выпало (Thaumcraft: мод отсутствует на 1.20.1) |  |
| H-375 | `legacy/src/main/java/alfheim/common/core/asm/hook/integration/BotaniaVisDiscountHooks.kt:25` | `ItemTerrasteelHelmRevealing#getVisDiscount` | создаёт метод, возвращает свой результат | оба | — |  | выпало (Thaumcraft: мод отсутствует на 1.20.1) |  |
| H-376 | `legacy/src/main/java/alfheim/common/core/asm/hook/integration/BotaniaVisDiscountHooks.kt:29` | `ItemElementiumHelmRevealing#addInformation` | создаёт метод, возвращает свой результат | оба | — |  | выпало (Thaumcraft: мод отсутствует на 1.20.1) |  |
| H-377 | `legacy/src/main/java/alfheim/common/core/asm/hook/integration/BotaniaVisDiscountHooks.kt:34` | `ItemManasteelHelmRevealing#addInformation` | создаёт метод, возвращает свой результат | оба | — |  | выпало (Thaumcraft: мод отсутствует на 1.20.1) |  |
| H-378 | `legacy/src/main/java/alfheim/common/core/asm/hook/integration/BotaniaVisDiscountHooks.kt:39` | `ItemTerrasteelHelmRevealing#addInformation` | создаёт метод, возвращает свой результат | оба | — |  | выпало (Thaumcraft: мод отсутствует на 1.20.1) |  |
| H-379 | `legacy/src/main/java/alfheim/common/core/asm/hook/integration/RedstoneRodHookHandler.kt:12` | `World#isBlockProvidingPowerTo` | в конце, возвращает свой результат, читает результат метода | оба | КТ-4 |  | ждёт |  |
| H-380 | `legacy/src/main/java/alfheim/common/core/asm/hook/integration/RedstoneRodHookHandler.kt:20` | `World#getIndirectPowerLevelTo` | в конце, возвращает свой результат, читает результат метода | оба | КТ-4 |  | ждёт |  |
| H-381 | `legacy/src/main/java/alfheim/common/core/asm/hook/integration/TGHandlerBotaniaAdapterHooks.kt:20` | `ItemHolyCloak#getBaubleType` | в начале, возвращает свой результат | оба | — |  | выпало (Travellers Gear: мод отсутствует на 1.20.1) |  |
| H-382 | `legacy/src/main/java/alfheim/common/core/asm/hook/integration/TGHandlerBotaniaAdapterHooks.kt:26` | `ItemHolyCloak#onTravelGearEquip` | создаёт метод, добавляет код | оба | — |  | выпало (Travellers Gear: мод отсутствует на 1.20.1) |  |
| H-383 | `legacy/src/main/java/alfheim/common/core/asm/hook/integration/TGHandlerBotaniaAdapterHooks.kt:32` | `ItemHolyCloak#onTravelGearTick` | создаёт метод, добавляет код | оба | — |  | выпало (Travellers Gear: мод отсутствует на 1.20.1) |  |
| H-384 | `legacy/src/main/java/alfheim/common/core/asm/hook/integration/TGHandlerBotaniaAdapterHooks.kt:38` | `ItemHolyCloak#onPlayerDamage` | в начале, выходит, если true | оба | — |  | выпало (Travellers Gear: мод отсутствует на 1.20.1) |  |
| H-385 | `legacy/src/main/java/alfheim/common/core/asm/hook/integration/TGHandlerBotaniaAdapterHooks.kt:64` | `ItemHolyCloak#addHiddenTooltip` | создаёт метод, возвращает свой результат | оба | — |  | выпало (Travellers Gear: мод отсутствует на 1.20.1) |  |
| H-386 | `legacy/src/main/java/alfheim/common/core/asm/hook/integration/TGHandlerBotaniaAdapterHooks.kt:71` | `ItemHolyCloak#getIconFromDamage` | создаёт метод, возвращает свой результат | клиент | — |  | выпало (Travellers Gear: мод отсутствует на 1.20.1) |  |
| H-387 | `legacy/src/main/java/alfheim/common/core/asm/hook/integration/TGHandlerBotaniaAdapterHooks.kt:78` | `ItemUnholyCloak#getIconFromDamage` | создаёт метод, возвращает свой результат | клиент | — |  | выпало (Travellers Gear: мод отсутствует на 1.20.1) |  |
| H-388 | `legacy/src/main/java/alfheim/common/core/asm/hook/integration/TraitFairySpawner.kt:15` | `PixieHandler#getChance` | в начале, выходит, если true, результат `0.1f` | оба | КТ-10 |  | ждёт |  |

## HookReplacer

`@HookReplacer` (KAIIIAK, в ASJCore) заменяет кусок байткода метода между `startFROM()` и
`startTO()` на код между `startTO()` и `stop()`.

| ID | Где | Цель | Как | Сторона | КТ | Решение в 1.20.1 | Статус | Примечание |
|---|---|---|---|---|---|---|---|---|
| R-01 | `legacy/src/main/java/alfheim/common/compat/TransformableHookReplacerHandler.kt:19` | `RenderWolf#shouldRenderPass` | заменяет участок байткода | оба | КТ-2 |  | ждёт | цвет ошейника волка |
| R-02 | `legacy/src/main/java/alfheim/common/compat/TransformableHookReplacerHandler.kt:40` | `LightningHandler#onRenderWorldLast` | заменяет участок байткода | оба | КТ-3 |  | ждёт | рендер молний Botania |
| R-03 | `legacy/src/main/java/alfheim/common/compat/TransformableHookReplacerHandler.kt:50` | `LightningHandler#onRenderWorldLast` | заменяет участок байткода | оба | КТ-3 |  | ждёт | рендер молний Botania |
| R-04 | `legacy/src/main/java/alfheim/common/compat/TransformableHookReplacerHandler.kt:61` | `RenderTileFloatingFlower#renderTileEntityAt` | заменяет участок байткода | оба | КТ-3 |  | ждёт | автор: «SOURCES ARE FAKE!!!» |
| R-05 | `legacy/src/main/java/alfheim/common/compat/TransformableHookReplacerHandler.kt:87` | `RenderTileFloatingFlower#renderTileEntityAt` | заменяет участок байткода, необязательная | оба | КТ-3 |  | ждёт | автор: «SOURCES ARE FAKE!!!» |
| R-06 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/ManaSpreaderExtender.kt:128` | `TileSpreader#renderHUD` | заменяет участок байткода | оба | КТ-3 |  | ждёт |  |
| R-07 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/ManaSpreaderExtender.kt:155` | `RenderTileSpreader#renderTileEntityAt` | заменяет участок байткода | оба | КТ-3 |  | ждёт |  |
| R-08 | `legacy/src/main/java/alfheim/common/core/asm/hook/replacer/HookReplacerHandler.java:35` | `ToolCommons#removeBlockWithDrops` | заменяет участок байткода | оба | КТ-4 |  | ждёт | эльфийская кирка Botania |
| R-09 | `legacy/src/main/java/alfheim/common/core/asm/hook/replacer/HookReplacerHandler.java:44` | `ItemElementiumPick#onHarvestDrops` | заменяет участок байткода | оба | КТ-4 |  | ждёт | эльфийская кирка Botania |
| R-10 | `legacy/src/main/java/alfheim/common/core/asm/hook/replacer/HookReplacerHandler.java:53` | `ItemElementiumPick#onHarvestDrops` | заменяет участок байткода | оба | КТ-4 |  | ждёт | эльфийская кирка Botania |
| R-11 | `legacy/src/main/java/alfheim/common/core/asm/hook/replacer/HookReplacerHandler.java:88` | `ItemManaResource#onPlayerInteract` | заменяет участок байткода | оба | КТ-3 |  | ждёт |  |
| R-12 | `legacy/src/main/java/alfheim/common/core/asm/hook/replacer/HookReplacerHandler.java:97` | `ItemManaResource#onPlayerInteract` | заменяет участок байткода | оба | КТ-3 |  | ждёт |  |
| R-13 | `legacy/src/main/java/alfheim/common/core/asm/hook/replacer/HookReplacerHandler.java:114` | `SheddingHandler#onLivingUpdate` | заменяет участок байткода, необязательная | оба | КТ-3 |  | ждёт | линька животных Botania |
| R-14 | `legacy/src/main/java/alfheim/common/core/asm/hook/replacer/HookReplacerHandler.java:128` | `SubTileMunchdew#onUpdate` | заменяет участок байткода | оба | КТ-3 |  | ждёт | функциональные цветы Botania |
| R-15 | `legacy/src/main/java/alfheim/common/core/asm/hook/replacer/HookReplacerHandler.java:137` | `SubTileMunchdew#onUpdate` | заменяет участок байткода | оба | КТ-3 |  | ждёт | функциональные цветы Botania |
| R-16 | `legacy/src/main/java/alfheim/common/core/asm/hook/replacer/HookReplacerHandler.java:146` | `SubTileTigerseye#onUpdate` | заменяет участок байткода | оба | КТ-3 |  | ждёт | функциональные цветы Botania |
| R-17 | `legacy/src/main/java/alfheim/common/core/asm/hook/replacer/HookReplacerHandler.kt:41` | `TileCocoon#hatch` | заменяет участок байткода | оба | КТ-3 |  | ждёт | кокон Botania |
| R-18 | `legacy/src/main/java/alfheim/common/core/asm/hook/replacer/HookReplacerHandler.kt:54` | `TileCocoon#hatch` | заменяет участок байткода | оба | КТ-3 |  | ждёт | кокон Botania |
| R-19 | `legacy/src/main/java/alfheim/common/core/asm/hook/replacer/HookReplacerHandler.kt:67` | `EntityDoppleganger#attackEntityFrom` | заменяет участок байткода | оба | КТ-8 |  | ждёт |  |
| R-20 | `legacy/src/main/java/alfheim/common/core/asm/hook/replacer/HookReplacerHandler.kt:82` | `ItemGravityRod#leftClick` | заменяет участок байткода | оба | КТ-4 |  | ждёт |  |
| R-21 | `legacy/src/main/java/alfheim/common/core/asm/hook/replacer/HookReplacerHandler.kt:96` | `SheddingHandler#getShedPattern` | заменяет участок байткода | оба | КТ-3 |  | ждёт | линька животных Botania |
| R-22 | `legacy/src/main/java/alfheim/common/core/asm/hook/replacer/HookReplacerHandler.kt:108` | `EntityAnimal#getCanSpawnHere` | заменяет участок байткода | оба | КТ-6 |  | ждёт |  |
| R-23 | `legacy/src/main/java/alfheim/common/core/asm/hook/replacer/HookReplacerHandler.kt:129` | `TFFluids#preInit` | заменяет участок байткода | оба | — |  | выпало (Thermal Foundation: мод отсутствует на 1.20.1) |  |
| R-24 | `legacy/src/main/java/alfheim/common/core/asm/hook/replacer/HookReplacerHandler.kt:138` | `BlockDynamicLiquid#updateTick` | заменяет участок байткода | оба | КТ-6 |  | ждёт |  |
| R-25 | `legacy/src/main/java/alfheim/common/core/asm/hook/replacer/HookReplacerHandler.kt:150` | `BlockLiquid#func_149805_n` | заменяет участок байткода | оба | КТ-6 |  | ждёт |  |
| R-26 | `legacy/src/main/java/alfheim/common/core/asm/hook/replacer/HookReplacerHandler.kt:162` | `ItemWaterRing#onWornTick` | заменяет участок байткода | оба | КТ-4 |  | ждёт |  |
| R-27 | `legacy/src/main/java/alfheim/common/core/asm/hook/replacer/HookReplacerHandler.kt:171` | `ItemWaterRing#onWornTick` | заменяет участок байткода | оба | КТ-4 |  | ждёт |  |
| R-28 | `legacy/src/main/java/alfheim/common/core/asm/hook/replacer/HookReplacerHandler.kt:180` | `ItemWaterRing#onUnequipped` | заменяет участок байткода | оба | КТ-4 |  | ждёт |  |
| R-29 | `legacy/src/main/java/alfheim/common/core/asm/hook/replacer/HookReplacerHandler.kt:189` | `CompositeLensRecipe#matches` | заменяет участок байткода, совпадение [2] | оба | КТ-3 |  | ждёт |  |
| R-30 | `legacy/src/main/java/alfheim/common/core/asm/hook/replacer/HookReplacerHandler.kt:209` | `SubTileNarslimmus#onUpdate` | заменяет участок байткода | оба | КТ-3 |  | ждёт |  |
| R-31 | `legacy/src/main/java/alfheim/common/core/asm/hook/replacer/HookReplacerHandler.kt:221` | `TileCraftCrate#craft` | заменяет участок байткода, группа ["craftyCrateUnclog"] | оба | КТ-3 |  | ждёт | крафтовый ящик Botania |
| R-32 | `legacy/src/main/java/alfheim/common/core/asm/hook/replacer/HookReplacerHandler.kt:232` | `TileCraftCrate#craft` | заменяет участок байткода, группа ["craftyCrateUnclog"] | оба | КТ-3 |  | ждёт | крафтовый ящик Botania |

## HookField

`@HookField` добавляет поле в чужой класс. В 1.20.1 — capability, `SynchedEntityData`
или поле через миксин-интерфейс, по месту.

| ID | Где | Цель | Как | Сторона | КТ | Решение в 1.20.1 | Статус | Примечание |
|---|---|---|---|---|---|---|---|---|
| F-01 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimFieldHookHandler.java:9` | `ContainerWorkbench` | добавляет поле `alfheim_synthetic_thePlayer: EntityPlayer` | оба | КТ-2 |  | ждёт | игрок у верстака (`CraftingExtensions`) |
| F-02 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimFieldHookHandler.java:12` | `DamageSource` | добавляет поле `alfheim_synthetic_elementalFlag: int` | оба | КТ-4 |  | ждёт | стихийный урон |
| F-03 | `legacy/src/main/java/alfheim/common/core/asm/hook/AlfheimFieldHookHandler.java:15` | `TileCocoon` | добавляет поле `alfheim_synthetic_essenceGiven: int` | оба | КТ-3 |  | ждёт | кокон Botania |

## SuperWrapper

`@SuperWrapper` (KAIIIAK, в ASJCore) даёт коду Alfheim вызвать метод родительского класса
в обход переопределения.

| ID | Где | Цель | Как | Сторона | КТ | Решение в 1.20.1 | Статус | Примечание |
|---|---|---|---|---|---|---|---|---|
| S-01 | `legacy/src/main/java/alfheim/common/core/asm/superwrapper/SuperWrapperHandler.kt:14` | `ItemManasteelArmor#addInformation` | обёртка вызова `super` | оба | КТ-4 |  | ждёт |  |
| S-02 | `legacy/src/main/java/alfheim/common/core/asm/superwrapper/SuperWrapperHandler.kt:20` | `EntityLiving#canDespawn` | обёртка вызова `super` без вызова своего метода | оба | КТ-5 |  | ждёт | исчезновение и редкий дроп мобов |
| S-03 | `legacy/src/main/java/alfheim/common/core/asm/superwrapper/SuperWrapperHandler.kt:26` | `BlockBush#canPlaceBlockOn` | обёртка вызова `super` без вызова своего метода | оба | КТ-2 |  | ждёт |  |
| S-04 | `legacy/src/main/java/alfheim/common/core/asm/superwrapper/SuperWrapperHandler.kt:32` | `EntityLivingBase#dropRareDrop` | обёртка вызова `super` без вызова своего метода | оба | КТ-5 |  | ждёт | исчезновение и редкий дроп мобов |

## Трансформер

`legacy/src/main/java/alfheim/common/core/asm/transformer/AlfheimClassTransformer.kt` —
ASM-трансформер на 607 строк. По строке на каждое преобразование.

| ID | Класс | Метод | Что меняет | КТ | Решение в 1.20.1 | Статус |
|---|---|---|---|---|---|---|
| T-01 | `RenderGlobal` | renderEntities | рендер привязанных цепью Глейпнира существ (`LeashingHandler`) | КТ-4 |  | ждёт |
| T-02 | `EntityLivingBase` | moveEntityWithHeading | скольжение: `Block.slipperiness` → `getRelativeSlipperiness(entity)` (ледяной пол брони, врезка `Block#getRelativeSlipperiness`) | КТ-4 |  | ждёт |
| T-03 | `EntityTrackerEntry` | tryStartWachingThis | вызов `PartySystem.notifySpawn` — состав группы видит существо | КТ-7 |  | ждёт |
| T-04 | `Potion` | performEffect | урон отравления — источник `DamageSourceSpell.poison` | КТ-7 |  | ждёт |
| T-05 | `ItemNugget` | registerIcons, getSubItems | самородки Thaumcraft | — |  | выпало (Thaumcraft: мод отсутствует на 1.20.1) |
| T-06 | `BaubleRenderHandler` | renderManaTablet | рендер планшета маны на поясе | КТ-4 |  | ждёт |
| T-07 | `TooltipAdditionDisplayHandler` | render | подсказки маны Botania | КТ-3 |  | ждёт |
| T-08 | `RenderHelper` | renderProgressPie | автор: «Fix for progress pie integrity on full progress» | КТ-3 |  | ждёт |
| T-09 | `TileManaFlame` | все методы, кроме getColor и writeCustomNBT | чтение поля `color` → вызов `getColor()` (радужное пламя) | КТ-3 |  | ждёт |
| T-10 | `TileSpecialFlower` | все методы и поля | константа `TAG_SUBTILE_NAME` → `SubTileEntity.TAG_TYPE` (совместимость NBT цветов) | КТ-3 |  | ждёт |
| T-11 | `EntityDoppleganger` | класс, getBossBarTextureRect | интерфейс `IBotaniaBossWithShaderAndName`, полоса здоровья Гайи | КТ-8 |  | ждёт |
| T-12 | `EntityDoppleganger` | onLivingUpdate (`changeTeleport`) | `teleportTo` → `setPosition`, высота 1.6 → 3.6 | КТ-8 |  | ждёт |
| T-13 | `ItemFlowerBag` | loadStacks | расширенный мешок цветов (`FlowerBagExtender`) | КТ-3 |  | ждёт |
| T-14 | `ItemMiningRing` | onWornTick | постоянный эффект кольца | КТ-4 |  | ждёт |
| T-15 | `ItemWaterRing` | onWornTick | постоянный эффект кольца | КТ-4 |  | ждёт |
| T-16 | `ItemLens` | поле SUBTYPES, все методы | число линз Botania + линзы Alfheim (`ItemLensExtender`) | КТ-3 |  | ждёт |
| T-17 | `ItemAesirRing` | onDropped | кольцо Асов распадается и на кольца Сив, Ньёрда, Хеймдалля | КТ-4 |  | ждёт |
| T-18 | `ItemTerraformRod` | terraform | радиус — `GodAttributesHooks.getRange` | КТ-4 |  | ждёт |
| T-19 | `LibItemNames` | <clinit> | имена линз Alfheim в списке Botania | КТ-3 |  | ждёт |
| T-20 | `ClientEvents$GUIOverlay (Witchery)` | renderHotbar | автор: «fixes for stupid coders» — исправление чужого мода | — |  | выпало (Witchery: мод отсутствует на 1.20.1) |

## Добавленные интерфейсы

`registerAdditionalInterface` (ASJCore) добавляет интерфейс к чужому классу. В 1.20.1 —
миксин-интерфейс или проверка на месте вызова.

| ID | Где | Класс | Интерфейс | Зачем | КТ | Решение в 1.20.1 | Статус |
|---|---|---|---|---|---|---|---|
| I-01 | `legacy/src/main/java/alfheim/common/core/asm/AlfheimHookLoader.kt:82` | `EntityCreeper` | `IElementalEntity` | стихии существ | КТ-4 |  | ждёт |
| I-02 | `legacy/src/main/java/alfheim/common/core/asm/AlfheimHookLoader.kt:83` | `EntitySkeleton` | `IMuspelheimEntity` | защита от жара Муспельхейма | КТ-5 |  | ждёт |
| I-03 | `legacy/src/main/java/alfheim/common/core/asm/AlfheimHookLoader.kt:84` | `EntitySkeleton` | `INiflheimEntity` | защита от холода Нифльхейма | КТ-5 |  | ждёт |
| I-04 | `legacy/src/main/java/alfheim/common/core/asm/AlfheimHookLoader.kt:85` | `EntityGolemBase (Thaumcraft)` | `IElementalEntity` |  | — |  | выпало (Thaumcraft: мод отсутствует на 1.20.1) |
| I-05 | `legacy/src/main/java/alfheim/common/core/asm/AlfheimHookLoader.kt:86` | `EntityWisp (Thaumcraft)` | `IElementalEntity` |  | — |  | выпало (Thaumcraft: мод отсутствует на 1.20.1) |
| I-06 | `legacy/src/main/java/alfheim/common/core/asm/AlfheimHookLoader.kt:87` | `ItemAuraRing` | `IManaItem` | кольцо ауры хранит ману (`ItemAuraRingExtender`) | КТ-3 |  | ждёт |
| I-07 | `legacy/src/main/java/alfheim/common/core/asm/AlfheimHookLoader.kt:88` | `ItemAesirRing` | `IStepupItem` | шаг вверх | КТ-4 |  | ждёт |
| I-08 | `legacy/src/main/java/alfheim/common/core/asm/AlfheimHookLoader.kt:89` | `EntityDoppleganger` | `IMob` | Гайя считается монстром | КТ-8 |  | ждёт |
| I-09 | `AlfheimCore.kt`, `construct` | `ItemElementiumHelmRevealing` (Botania) | `IVisDiscountGear` (Thaumcraft) | скидка вис | — |  | выпало (Thaumcraft: мод отсутствует на 1.20.1); в порте закомментировано с `// PORT:` |
| I-10 | `AlfheimCore.kt`, `construct` | `ItemManasteelHelmRevealing` (Botania) | `IVisDiscountGear` (Thaumcraft) | скидка вис | — |  | выпало (Thaumcraft: мод отсутствует на 1.20.1); в порте закомментировано с `// PORT:` |
| I-11 | `AlfheimCore.kt`, `construct` | `ItemTerrasteelHelmRevealing` (Botania) | `IVisDiscountGear` (Thaumcraft) | скидка вис | — |  | выпало (Thaumcraft: мод отсутствует на 1.20.1); в порте закомментировано с `// PORT:` |

## Закомментированные автором

Не действовали в релизе 67. Незаконченное автором «удлинение искр» (апгрейд-проводник).

| ID | Где | Строка | Статус |
|---|---|---|---|
| C-1 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/SparkExtender.kt:84` | `//	@Hook(injectOnExit = true)` | WIP — стадия 2 |
| C-2 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/SparkExtender.kt:97` | `//	@Hook(returnCondition = ReturnCondition.ALWAYS)` | WIP — стадия 2 |
| C-3 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/SparkExtender.kt:118` | `//	@Hook(returnCondition = ReturnCondition.ON_TRUE)` | WIP — стадия 2 |
| C-4 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/SparkExtender.kt:130` | `//	@Hook(injectOnExit = true)` | WIP — стадия 2 |
| C-5 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/SparkExtender.kt:138` | `//	@Hook(returnCondition = ReturnCondition.ON_NOT_NULL)` | WIP — стадия 2 |
| C-6 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/SparkExtender.kt:143` | `//	@Hook(injectOnExit = true)` | WIP — стадия 2 |
| C-7 | `legacy/src/main/java/alfheim/common/core/asm/hook/extender/SparkExtender.kt:151` | `//	@Hook(returnCondition = ReturnCondition.ON_NOT_NULL)` | WIP — стадия 2 |
