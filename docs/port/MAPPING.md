# Соответствия 1.7.10 → 1.20.1

Зачем этот файл: одинаковый код автора должен переноситься одинаково в любой
сессии. Соответствие, которое понадобилось больше одного раза, записывается
сюда.

Имена 1.20.1 — официальные (Mojang), как в MDK. Строки без ✓ — предварительные:
при первом использовании их сверяют с исходниками 1.20.1, Forge 47.4.23 или
Botania 456 и ставят ✓. Если оказалось иначе — строку исправляют.

## Общие приёмы

| Приём | Когда | Пример |
|---|---|---|
| Тип подключается под именем 1.7.10 | класс в 1.20.1 только переименован, а строк автора с ним много | `import net.minecraft.world.entity.player.Player as EntityPlayer`, `import net.minecraft.server.level.ServerPlayer as EntityPlayerMP` — с пометкой `// PORT:` у импорта |
| Имена полей 1.7.10 — частными свойствами внизу класса | поле только переименовано, смысл тот же | `private val Entity.posX get() = x` в `Vector3` |
| Ветка, которой нужна механика другой КТ, — `Unit` и закомментированный код автора под ней | `when` по перечислению должен остаться полным | ветки пакетов `Message1d`, `Message2d` |
| Блок `/* PORT: по мере надобности … */` | функция библиотеки ASJCore ещё не нужна порту | `Extensions.kt`, `ASJUtilities.kt` |
| `// PORT-FIX:` над исправленной строкой, строка автора закомментирована под ней | техническая ошибка автора (CLAUDE.md, правило 5) | `Vector3.isInside` |

## Имена и metadata

| Было | Стало | Примечание |
|---|---|---|
| `alfheim:DomainDoor` | `alfheim:domain_door` | snake_case от имени автора (SPEC, Р-5) |
| metadata = разные вещи | отдельные блоки/предметы; id — имя, под которым вариант был у автора, в snake_case: `alfStorage` + 4 → `alf_storage4` | ✓ так вариант звали ключ перевода (`tile.alfStorage4.name`) и текстура (`alfStorage4.png`); в коде — массив блоков, номер варианта — индекс: `alfStorage = Array(6) { BlockAlfStorage(it) }`, `ItemStack(alfStorage, 1, 4)` → `ItemStack(alfStorage[4])` |
| metadata = состояние | свойство BlockState | поворот, рост, «включён» |
| любое старое имя + meta | запись в `src/generated/resources/alfheim/legacy_ids.json` | ✓ один источник для построек, lang, лексикона, тестов; строит генерация данных |
| `GameRegistry.registerBlock` / `registerItem` и др. в preInit | `DeferredRegister` из `alfheim.port.registry.AlfheimRegisters` | ✓ реестры регистрируются на шине мода в конструкторе |
| `GameRegistry.registerBlock(block, itemClass, name)` в коде автора | `alfheim.port.legacy.GameRegistry` с той же сигнатурой | ✓ блок попадает в событие регистрации через `LegacyRegistration`; реестры автора (`AlfheimBlocks`, `AlfheimFluffBlocks`) создаются внутри события регистрации блоков, предметы-блоки — в событии предметов |
| `CreativeTabs("Name")` | `CreativeModeTab` из `AlfheimRegisters.CREATIVE_MODE_TABS`, id — имя в snake_case | ✓ `setNoTitle` → `hideTitle`, `backgroundImageName` → `withBackgroundLocation` (путь строчными), `hasSearchBar` → `withSearchBar`, `displayAllReleventItems` → `displayItems` |
| `setCreativeTab(AlfheimTab)` | ничего | в 1.20.1 вещь не знает своей вкладки: список вещей — в `AlfheimTab.displayAllReleventItems` |

## legacy_ids.json

Файл `src/generated/resources/alfheim/legacy_ids.json`, читает `alfheim.port.registry.LegacyIds`,
сверяет GameTest `PortRegistryTest`. Строит генерация данных (`alfheim.port.data.LegacyIdsProvider`) по всему,
что зарегистрировано через `LegacyRegistration`; руками не правится.

| Раздел | Ключ | Значение |
|---|---|---|
| `blocks`, `items`, `entities` | старое имя в реестре 1.7.10, `modid:name` | новый id; если вещь различалась metadata — объект `{"0": …, "1": …}`, ключ `"*"` — любая metadata |
| | | к id блока можно дописать свойства состояния: `"alfheim:alt_wood[axis=y]"` |
| `lang` | старый ключ перевода | новый ключ (`block.alfheim.<id>`, `item.alfheim.<id>`, `effect.alfheim.<id>`); применяет `tools/convert_lang.py`. Если у вариантов было одно имя 1.7.10 (`tile.alfheim:irisWood.name`) — список новых ключей, текст получает каждый |

Старый ключ предмета-блока: `tile.` + имя из `setBlockName`; `ItemBlockMetaName` дописывал номер варианта,
`ItemBlockLeavesMod` — приставку `alfheim:` (`tile.alfheim:ElvenSand.name`). В 1.20.1 у предмета-блока ключ блока,
поэтому старый ключ предмета переименовывается в ключ блока. Имя зелья — ключ из `setPotionName`
(`alfheim.potion.whiteWine`) → ключ эффекта (`effect.alfheim.white_wine`); ключи `….postfix` остаются как были.

Пример: `"blocks": {"alfheim:altWood1": {"0": "alfheim:alt_wood1_…", "*": "…"}}`.

Блок 1.7.10, у которого в 1.20.1 нет своего блока, записывается как состояние другого: двойная плита
`"alfheim:LivingCobble0SlabFull": "alfheim:living_cobble0_slab[type=double]"` (`LegacyRegistration.alias`).
Metadata поворота и половины (лестницы, плиты, столбы) — состояние: его переводит загрузчик построек (КТ-6).

## Переводы

| Было | Стало | Примечание |
|---|---|---|
| `assets/<ns>/lang/en_US.lang` | `assets/<ns>/lang/en_us.json` | ✓ собирает `tools/convert_lang.py` из `.lang` в `legacy/`; руками `.json` не правится |
| значения с `%d`, `%.1f` | как есть | ✓ и 1.7.10, и 1.20.1 при загрузке заменяют их на `%s` |
| ключи удалённых вещей | не переносятся | ✓ список с причинами — `REMOVED` в `tools/convert_lang.py` |
| ключ предмета Botania `ItemBlockMod`, `ItemBlockModSlab` (`tile.botania:…`) | ключ блока | ✓ `ItemBlockMod` 1.7.10 менял `tile.` на `tile.botania:` — так эти ключи записаны в переводах Alfheim; ключи двойных плит (`…SlabFull`) не переносятся: своего предмета у двойной плиты нет |
| `StatCollector.translateToLocal` / `translateToLocalFormatted` | `alfheim.port.legacy.StatCollector` там, где автору нужен готовый текст на этой стороне; `Component.translatable` — для текста, который уходит игроку | ✓ прослойка поверх `Language.getInstance()`: на сервере — английский, как в 1.7.10; при ошибке формата — «Format error: …». Ключи те же, пока их не переименует `legacy_ids.json` |

## Ресурсы

| Было | Стало | Примечание |
|---|---|---|
| путь ресурса автора: `textures/model/item/AkashicRecordsCube.png` | `legacyPath(path)` из `alfheim.port.legacy`: `textures/model/item/akashic_records_cube.png` | ✓ в 1.20.1 в пути только строчные буквы, цифры и `_-./`. Каждая часть пути — в snake_case, как имена в реестре, расширение — строчными. Файлы из `legacy/` переносятся под этими именами |
| `ResourceLocationIL(…)` (LibResourceLocations) | как есть | ✓ конструктор сам применяет `legacyPath` |
| `ResourceLocation(modid, path)` с путём автора | `ResourceLocation(modid, legacyPath(path))` | ✓ |
| ванильные `textures/blocks/…`, `textures/items/…` | `textures/block/…`, `textures/item/…` | ✓ |
| текстуры блоков и предметов автора `textures/blocks/…`, `textures/items/…` | те же папки, имена файлов в snake_case; в модели — `alfheim:blocks/elven_sand` | ✓ атлас блоков 1.20.1 берёт только `block/` и `item/`, папки автора добавляет `assets/minecraft/atlases/blocks.json`. Переносит `tools/move_legacy.py` |
| `.png.mcmeta` с `"interpolate": true` (`InterpolatedIconHelper`) | как есть | ✓ плавную анимацию 1.20.1 рисует сама |
| `vazkii.botania.client.lib.LibResources` | `vazkii.botania.client.lib.ResourcesLib` | ✓ константы — полные пути `botania:…`; «розовый» пилон 1.7.10 — `MODEL_PYLON_GAIA` |
| `ResourceLocationAnimated` (ASJCore) | переносится вместе с первой моделью, которой он нужен | строки автора с ним закомментированы с номером КТ |
| `EnumHelper.addRarity(name, EnumChatFormatting, displayName)` | `Rarity.create(name, ChatFormatting)` (Forge) | ✓ отображаемого имени у редкости в 1.20.1 нет; у автора оно нигде не выводилось |
| `MinecraftForge.MC_VERSION` | `MCPVersion.getMCVersion()` | ✓ |
| `sounds.json`: звук без приставки (`"fenrir/attack"`) | `"alfheim:fenrir/attack"` | ✓ 1.7.10 искал такой звук в пространстве имён файла, 1.20.1 — в `minecraft` |
| звук по имени (`"alfheim:quad"`), категория из `sounds.json` | событие `AlfheimSounds.events["quad"]`, категория `AlfheimSounds.source("quad")` | ✓ события регистрируются по `sounds.json`; поле `category` 1.20.1 не читает, громкость выбирает код через `SoundSource` |

## Мир, блоки, блок-сущности

| Было | Стало | Примечание |
|---|---|---|
| `World` | `Level` | |
| `world.isRemote` | `level.isClientSide` или `isRemote` прослойки | ✓ |
| `world.getBlock(x, y, z)`, `setBlock`, `getTileEntity`, `isAirBlock` | прослойка `alfheim.port.legacy` | ✓ только где смысл тот же (SPEC, Р-4); список — раздел «Прослойка `alfheim.port.legacy`» |
| `world.getBlockMetadata(x, y, z)` | свойство BlockState | без прослойки: смысл у каждого блока свой |
| `Block.registerBlockIcons` / `getIcon` / `IIcon` | модели и состояния блоков через datagen | ✓ метод закомментирован в классе блока, рядом с ним — пометка, где его модель (раздел «Блоки и предметы») |
| `Block.onBlockActivated` | `use` | |
| `updateTick` | `tick` / `randomTick` | |
| `onNeighborBlockChange` | `neighborChanged` | |
| `onBlockAdded` | `onPlace` | ✓ 1.20.1 зовёт его и при смене состояния того же блока: код автора, который сверяет metadata, просто ничего не меняет |
| `getLightValue(world, x, y, z)` — свечение по metadata | свечение состояний (`lightEmission`), записанное с создания блока по тому же правилу | ✓ `BlockColoredLamp`: свойство `power` |
| `breakBlock` | `onRemove` | |
| `getDrops` | таблица лута (datagen) | |
| `TileEntity` | `BlockEntity` + `BlockEntityType` | |
| `updateEntity` | `BlockEntityTicker` | |
| `readFromNBT` / `writeToNBT` | `load` / `saveAdditional` | |
| `getDescriptionPacket` / `onDataPacket` | `getUpdatePacket` / `getUpdateTag` / `onDataPacket` | |
| `AxisAlignedBB`, `MathHelper`, `MovingObjectPosition` | `AABB`, `Mth`, `HitResult` | |
| `block.material.isLiquid` | `state.liquid()` | ✓ |
| `Blocks.water` и `flowing_water`, `lava` и `flowing_lava` | `Blocks.WATER`, `Blocks.LAVA` | ✓ стоячая и текучая жидкость — один блок, уровень — свойство состояния: сравнения автора «стоячая — текучая» сводятся к одному блоку |
| `IFluidBlock.canDrain(world, x, y, z)`, `drain(world, x, y, z, true)` | `canDrain(level, pos)`, `drain(level, pos, FluidAction.EXECUTE)` | ✓ |
| `block.getBlockHardness(world, x, y, z)`, `block.canPlaceBlockAt(world, x, y, z)` | `state.getDestroySpeed(level, pos)`, `state.canSurvive(level, pos)` | ✓ |
| `ITileEntityProvider` | `EntityBlock` | ✓ |
| `te.writeToNBT(nbt)`; `TileEntity.createAndLoadEntity(nbt)` + `xCoord` / `yCoord` / `zCoord` + `world.setTileEntity(x, y, z, te)` | `be.saveWithFullMetadata()`; `BlockEntity.loadStatic(pos, state, nbt)` + `level.setBlockEntity(be)` | ✓ блок-сущность 1.20.1 создаётся сразу в своей точке |
| «точки нет» — `y = -1` в NBT предмета | `Int.MIN_VALUE` (или нет записи) | ✓ мир 1.20.1 бывает ниже нуля, блок на −1 — обычный; так же у секстанта Botania 1.20.1 |

## Блоки и предметы

Блоки автора наследуют базовые классы порта `alfheim.port.legacy.Block1710` (`Block` 1.7.10) и
`BlockFalling1710` (`BlockFalling`). Они принимают материал 1.7.10, а сеттеры 1.7.10 работают после создания
блока, как в 1.7.10: `init {}` блоков и цепочки сеттеров автора не меняются. Блоки особой формы (лестница, плита, стена,
забор, калитка, люк, панель) — свои базовые классы порта, строки ниже. Проверяют GameTest-ы `PortBlocksTest` и `PortDecorTest`.

| Было | Стало | Примечание |
|---|---|---|
| `Block(Material.rock)` | `Block1710(Material.rock)`, материал — `alfheim.port.legacy.Material` | ✓ значения материалов сверены с кодом 1.7.10: цвет на карте, нужен ли инструмент, горит ли от лавы, заменяем ли, поршень, жидкость, нотный блок |
| материал «твёрдый» / нет | `forceSolidOn` / `forceSolidOff` | ✓ `BlockState.isSolid` 1.20.1 и есть твёрдость материала 1.7.10 |
| материал не непрозрачный (полупрозрачный или не мешает движению) | на блоке не появляются мобы, он не проводит красный камень, в нём не задыхаются | ✓ в 1.7.10 только блок из непрозрачного материала мог быть «нормальным кубом» |
| `setHardness(h)`, `setResistance(r)` | те же сеттеры: твёрдость пишется в состояния блока, взрывоустойчивость 1.20.1 = внутренняя 1.7.10 / 5 | ✓ `setHardness` поднимает внутреннюю до `h * 5`, `setResistance` ставит `r * 3`; порядок вызовов важен, как у автора. Поля состояний открыты `META-INF/accesstransformer.cfg` |
| `setLightLevel(f)` | свечение `(int) (15 * f)`, пишется в состояния | ✓ |
| `setLightOpacity(o)`; без него — 255 у непрозрачного куба, 0 у прочих | `getLightBlock` = `min(o, 15)`; при 0 свет неба проходит не ослабевая | ✓ |
| `isOpaqueCube() = false` | как есть: блок не скрывает грани соседей и не сплошной для света (`getOcclusionShape` пуст) | ✓ |
| `slipperiness`, `setStepSound`, `setTickRandomly` | как есть; 1.20.1 читает их методами блока | ✓ звук `null` (в 1.7.10 ронял игру при шаге) — звук камня |
| `setHarvestLevel(tool, level)` | теги из генерации данных: `minecraft:mineable/<tool>`; уровень 1, 2, 3 — `needs_stone_tool`, `needs_iron_tool`, `needs_diamond_tool`; 4 и выше — `forge:needs_netherite_tool` | ✓ кирка быстра и на блоках из камня, железа, наковальни, топор — из дерева и растений, как в 1.7.10. Отличие 1.20.1: кирка ниже уровня копает быстро, но блок не роняет |
| `setHarvestLevel(tool, level, meta)` | для варианта с этим номером; у блока без вариантов — для всех состояний | ✓ |
| `setCreativeTab(tab)`, `setBlockTextureName(name)` | как есть: вкладка перечисляет вещи сама, имя текстуры читает генерация моделей | ✓ |
| `registerBlockIcons` / `getIcon` / `IIcon` | закомментированы в классе блока; модели строит генерация данных (`AlfheimBlockStates`) по тому же правилу | ✓ текстура по умолчанию — та, что регистрировал базовый класс; блок со своим `getIcon` описан в `AlfheimBlockStates` рядом |
| `getIcon(world, x, y, z, side)` (иконка по координатам) | модель клиента выбирает квадраты по той же формуле (`alfheim.port.client.AlfheimModels`) | ✓ `BlockLivingCobble`, вариант 3 |
| иконка зависит от опции конфига (`newStorageTexture`) | обе модели; нужную подставляет клиент при сборке моделей | ✓ после смены опции — F3+T, как в 1.7.10 |
| `getRenderBlockPass() = 1` | `render_type` translucent в модели; не непрозрачный куб в проходе 0 — cutout | ✓ |
| `shouldSideBeRendered(…) = world.getBlock(x, y, z) != this` | `skipRendering(state, adjacent, side) = adjacent.block == this` | ✓ |
| `isBeaconBase(…) = true` | тег `minecraft:beacon_base_blocks` из генерации данных | ✓ генерация вызывает метод автора |
| лут по умолчанию (`getItemDropped` — свой предмет, `damageDropped` — вариант) | таблица `dropSelf` | ✓ при взрыве — с шансом 1 / сила взрыва, как в 1.7.10 |
| `updateTick` падающего блока (`func_149830_m`) | `tick` `FallingBlock` 1.20.1 | ✓ пыли под висящим блоком нет: блок 1.7.10 не пылил |
| `canSustainPlant(world, x, y, z, direction, plantable)` | `canSustainPlant(state, world, pos, direction, plantable)`, `EnumPlantType.Desert` → `PlantType.DESERT` | ✓ |
| `block.material === Material.water` (блок в мире) | `block === Blocks.WATER` | ✓ в 1.7.10 материал воды был только у стоячей и текущей воды — в 1.20.1 это один блок |
| `ItemBlock` автора (`ItemBlockLeavesMod`, `ItemBlockMetaName`) | наследник `BlockItem` с конструктором `(Block)` | ✓ имя предмета — имя блока |
| `getEntry` (`ILexiconable`) | закомментирован до КТ-9 | лексикон |
| `BlockStairs(source, meta)`, `BlockSlab(full, material)`, `BlockWall(block)`, `BlockFence(texture, material)`, `BlockFenceGate()`, `BlockTrapDoor(material)`, `BlockPane(texture, top, material, canDrop)` | `Stairs1710`, `Slab1710`, `Wall1710`, `Fence1710`, `FenceGate1710`, `TrapDoor1710`, `Pane1710` из `alfheim.port.legacy` — наследники блока той же формы 1.20.1 с сеттерами 1.7.10 | ✓ значения конструктора 1.7.10 те же (лестница и стена берут твёрдость, взрывоустойчивость и звук у блока-источника). Механика формы — 1.20.1: углы лестниц, плиты в двойную, высота и соединения стен, люк без подпорки, свет сквозь неполные блоки — как у блоков ванилы 1.20.1 |
| источник лестницы, плиты, стены — блок с metadata (`BlockModStairs(livingrockDark, 1, …)`) | блок варианта (`BlockModStairs(livingrockDark[1], 1, …)`), номер варианта остаётся параметром | ✓ |
| двойная плита — отдельный блок (`…SlabFull`), `getFullBlock()` | состояние `type=double` одинарной плиты: поле `…SlabFull` — та же плита, её `register()` закомментирован | ✓ роняет две плиты, при выборе колёсиком — одинарная; старое имя `…SlabFull` в `legacy_ids.json` — `…_slab[type=double]` |
| грани лестницы, плиты, стены (`getIcon` блока-источника для каждой стороны) | модели генерации данных: бок, низ, верх — как у источника; у стены свои шаблоны `template_wall_*_faces` с текстурами `side`, `top`, `bottom` | ✓ в шаблоне стены ванилы одна текстура на все грани |
| `canConnectFenceTo(world, x, y, z)` | `connectsTo(state, sideSolid, direction)` | ✓ сосед с твёрдой гранью и калитка, повёрнутая к забору, — в `super` 1.20.1 |
| `canPaneConnectTo(...)` | не переопределяется: `IronBarsBlock.attachsTo` 1.20.1 — `final` | панель и так тянется к соседу с твёрдой гранью и к стенам |
| теги формы | стена — `minecraft:walls`, забор — `minecraft:fences` (не `wooden_fences`), люк — `minecraft:trapdoors` | ✓ без них стена 1.20.1 не соединяется сама с собой, поводок не привязывается к забору; заборы ванилы к забору автора не тянутся, как в 1.7.10 |
| поворот столба в metadata (`BlockModRotatedPillar`: `meta and 12`, `onBlockPlaced`) | свойство состояния `axis`, `getStateForPlacement` — по стороне, на которую ставят | ✓ модель — как у бревна (`cube_column`, `cube_column_horizontal`) |
| `IFuelHandler`, топливо | деревянный блок порта (материал `wood`) горит 300 тиков — `alfheim.port.legacy.Fuel1710` | ✓ печь 1.7.10 проверяла материал раньше обработчиков модов; обработчик топлива предмета — в КТ своего предмета |
| `Block.stepSound` чужого блока | расширение `Block.stepSound` прослойки | ✓ у блока порта и блока Botania 1.7.10 (`Botania1710`) |
| `Block.unlocalizedName` чужого блока (`source.unlocalizedName` у плит и лестниц) | расширение `Block.unlocalizedName` прослойки | ✓ `tile.` + имя из `setBlockName` |
| `block == other`, когда у блока есть варианты metadata | `isSameBlock1710(other)` прослойки: в порту варианты — разные блоки с одним именем 1.7.10 | ✓ |
| `world.getBlockMetadata(x, y, z)` у блока-варианта | `getBlockVariant(x, y, z)` прослойки — номер варианта блока в точке | ✓ metadata-состояние (поворот, рост, половина) — в состоянии блока, его читает код блока |
| `ItemSubtypedBlockMod`, `ItemIridescentBlockMod`, `ItemSlabMod` — имя без номера варианта | ключ блока; старый ключ без номера → ключи всех блоков этого имени (`legacy_ids.json`, раздел `lang`) | ✓ текст у всех вариантов один, как в 1.7.10; цвет — в подсказке, как у автора |
| `ItemSlab` 1.7.10 (одинарную плиту на одинарную ставил предмет) | обычный предмет-блок: плиты складывает сама плита 1.20.1 | ✓ |
| `getIcon(world, x, y, z, side)` — иконка по координатам и стороне | модели вариантов; клиент выбирает грань по той же формуле (`alfheim.port.client.AlfheimModels`) | ✓ `BlockLivingCobble`, `BlockLivingMountain` и его плита |
| текстура, которую автор грузил `InterpolatedIconHelper` (Botania `InterpolatedIcon`) | `"interpolate": true` в `.png.mcmeta` | ✓ `InterpolatedIcon` сглаживал кадры всегда; у части файлов автора ключ `"interpolated"` (1.7.10 его не читал) — исправляется на `"interpolate"` при переносе. У текстуры, которую автор грузил обычной иконкой, ключ `"interpolated"` остаётся: её кадры и в 1.7.10 не сглаживались (`netherwood_twig`) |

### Предметы

Предметы автора наследуют `alfheim.port.legacy.Item1710` (`Item` 1.7.10): предмет создаётся без свойств, а
сеттеры 1.7.10 (`setMaxStackSize`, `setHasSubtypes`, `setContainerItem`…) работают после создания; 1.20.1 читает их
методами предмета. Проверяет GameTest `PortMaterialsTest`.

| Было | Стало | Примечание |
|---|---|---|
| `Item()`, сеттеры, `unlocalizedName = name`, `creativeTab = tab` | `Item1710()`, те же сеттеры и свойства | ✓ `getItemStackLimit`, `getMaxDamage`, `isDamageable` (прочность есть и вариантов нет), `hasContainerItem` / `getContainerItem` (`null` — ничего не остаётся) — открытые методы `Item1710` с именами 1.7.10 |
| metadata = разные предметы (`ItemElvenResource`, `getSubItems` по номерам) | массив предметов `Array(n) { ItemX(it) }`, номер — `val meta` предмета (`LegacyItem.variant`) | ✓ id — имя варианта у автора, если у варианта своё имя (`LegacyItem.variantName`: `ElvenItems` 3 → `ElvoriumIngot` → `alfheim:elvorium_ingot`), иначе имя предмета и номер (`wiltedLotus` 1 → `wilted_lotus1`) |
| `ItemStack(item, n, meta)`, `stack.itemDamage` как вариант | `ItemStack(items[meta], n)`; `stack.meta` (ASJCore) — номер варианта предмета или блока, иначе повреждение | ✓ |
| `getItemStackDisplayName(stack)`, ключ `getUnlocalizedNameInefficiently(stack) + ".name"` | `getDescriptionId(stack)` `Item1710` — тот же ключ, переименованный по `legacy_ids.json`; `ItemMod.getName` переводит его на этой стороне (`StatCollector`), `&` → `§` | ✓ имена, которые автор собирает по NBT, — его ключи; на сервере имя английское, как в 1.7.10 |
| `getSubItems(item, tab, list)` | `Item1710.getSubItems(item, tab, list)`; вкладка автора зовёт его, как в 1.7.10 | ✓ предмет-вариант выдаёт только свои вещи |
| `registerIcons` / `getIcon(stack, pass)` / `getRenderPasses` / `IIcon` | закомментированы в классе предмета; модели строит генерация данных (`AlfheimItemModels`): проход n — слой `layer<n>` `item/generated` | ✓ текстура `alfheim:materials/RiftShardEmpty` → `alfheim:items/materials/rift_shard_empty` |
| `getColorFromItemStack(stack, pass)` | как есть; клиент красит им слой с тем же номером (`alfheim.port.client.AlfheimItemColors`) | ✓ спрашивается на каждом кадре — переливы как в 1.7.10 |
| `setFull3D()` | родитель модели `item/handheld` | |
| `hasEffect(stack, pass)` | как есть; `isFoil` = `hasEffect(stack, 0)` | ✓ |
| `addInformation(stack, player, list, adv)` | `appendHoverText(stack, level, tooltip: MutableList<Component>, flag)`; `addStringToTooltip` (ASJCore) принимает `MutableList<Component>` | ✓ |
| `onItemRightClick` / `setItemInUse` / `getMaxItemUseDuration` / `getItemUseAction` / `onEaten` | `use` / `startUsingItem(hand)` / `getUseDuration` / `getUseAnimation` (`EnumAction.bow` → `UseAnim.BOW`) / `finishUsingItem` | ✓ |
| `stack.stackSize--`, `foodStats.addStats(f, s)`, `isBadEffect` | `stack.shrink(1)`, `foodData.eat(f, s)`, `effect.category == HARMFUL` | ✓ |
| `ItemFood(heal, saturation, wolfMeat)`: `func_150905_g(stack)`, `func_150906_h(stack)`, `onEaten`, `setAlwaysEdible` | `alfheim.port.legacy.ItemFood1710`: те же методы сытости и насыщения; `onEaten` → `finishUsingItem` (минус один, `foodData.eat`, отрыжка) | ✓ сытость спрашивается у стака, как в 1.7.10; в 1.20.1 пустой стак теряет свой предмет, поэтому всё, что зависит от варианта (`stack.meta`), берётся до того, как стак уменьшен. `player.canEat` — метод 1.20.1: в творческом режиме есть можно всегда |
| `stack.displayName` | `stack.hoverName.string` | ✓ `ItemStack.getDisplayName` 1.20.1 — другой метод (имя в скобках), он закрывает свойство прослойки |
| `IFuelHandler.getBurnTime(fuel)` предмета | `getBurnTime(stack, recipeType)` предмета зовёт метод автора | ✓ |
| `commandSenderName` игрока | `gameProfile.name` | ✓ |
| `EntitySheep.fleeceColorTable` | `alfheim.port.legacy.Sheep1710.fleeceColorTable`; где строк с таблицей много — `import alfheim.port.legacy.Sheep1710 as EntitySheep` | ✓ таблица 1.7.10: цвета красителей 1.20.1 другие |
| иконка на праздник (`AlfheimCore.jingleTheBells`) | вторая модель; нужную подставляет клиент при сборке моделей (`AlfheimModels`) | ✓ |
| `getItemDropped` / `damageDropped` / `quantityDropped` блока | таблица лута из генерации данных по методам автора (`AlfheimBlockLoot`); `getItemDropped` возвращает предмет 1.20.1 | ✓ формула удачи 1.7.10 — `ApplyBonusCount.addOreBonusCount`; при взрыве каждая вещь — с шансом 1 / сила взрыва (`explosion_decay`) |
| `getExpDrop(world, meta, fortune)` | `getExpDrop(state, level, random, pos, fortune, silkTouch)` зовёт метод автора; с шёлковым касанием — 0 | ✓ так считал опыт Forge 1.7.10 (`BlockEvent.BreakEvent`) |

## Растения

Растения, листва и деревья 1.7.10 — базовые классы порта `alfheim.port.legacy` поверх растений 1.20.1 той же формы:
`Bush1710` (`BlockBush`, поверх `BushBlock`), `Sapling1710` (`BlockSapling`), `TallGrass1710` (`BlockTallGrass`),
`DoublePlant1710` (`BlockDoublePlant`, поверх `DoublePlantBlock`), `Leaves1710` (`BlockLeaves`, поверх `LeavesBlock`),
`WorldGenerator` и `WorldGenAbstractTree` (генераторы деревьев). Методы 1.7.10, которые переопределяет автор
(`updateTick`, `canBlockStay`, `checkAndDropBlock`, `func_149851_a` …), остаются у автора как были; базовый класс
вызывает их из методов 1.20.1. Проверяет GameTest `PortPlantsTest`.

| Было | Стало | Примечание |
|---|---|---|
| `BlockBush` | `Bush1710` (`BushBlock`) | ✓ стоит, пока блок снизу держит растение (`canBlockStay`: `canSustainPlant` блока снизу); без опоры — лут и воздух (флаг 2); столкновений нет; рамка 0,3–0,7 по сторонам и 0,6 в высоту; тики случайные |
| `canPlaceBlockAt` и `canBlockStay` растения | одно правило 1.20.1 — `canSurvive` (`Bush1710`: `canBlockStay` автора) | ✓ 1.7.10 при установке ещё требовал заменяемое место и `canSustainPlant` блока снизу — у растений автора это то же самое |
| `updateTick` растения | и случайный (`randomTick`), и запланированный (`tick`) тик | ✓ |
| `IGrowable`: `func_149851_a`, `func_149852_a`, `func_149853_b` | `IGrowable` прослойки (`BonemealableBlock`): `isValidBonemealTarget`, `isBonemealSuccess`, `performBonemeal` | ✓ имена методов автора те же; костная мука тратится, если удобрить можно, как в 1.7.10 |
| `BlockSapling`, бит 8 metadata («готов расти») | `Sapling1710`, свойство `stage` | ✓ рамка 0,1–0,9 и 0,8; мука срабатывает с шансом 0,45 |
| `BlockTallGrass` | `TallGrass1710` | ✓ материал лиан: заменяемая, горит; рамка 0,1–0,9 и 0,8. Лут — таблица: с ножницами — сама трава (`onSheared`), иначе с шансом 1/8 — пшеничные семена (`ForgeHooks.getGrassSeed`), удача не влияет. Семена других модов (`MinecraftForge.addGrassSeed` 1.7.10) в 1.20.1 добавляются модификаторами к таблице травы ванилы — к траве автора они не относятся. Не смещается: 1.7.10 смещал только траву ванилы |
| `BlockDoublePlant`, бит 8 metadata (верхняя половина) | `DoublePlant1710` (`DoublePlantBlock`), свойство `half` | ✓ обе половины ставит `DoublePlantBlock.placeAt` 1.20.1, ломает 1.20.1: вторая половина исчезает без лута. Заменяемы только варианты 2 и 3, как в 1.7.10; костная мука — копия растения, кроме вариантов 2 и 3 |
| смещение двойного растения при рисовании (`RenderBlocks.renderBlockDoublePlant`, `RenderBlockColoredDoubleGrass`) | `offsetFunction` состояний `DoublePlant1710` (открыт в `accesstransformer.cfg`) | ✓ ±0,15 блока по хэшу x и z — тому же, что у `Mth.getSeed`; у растений 1.20.1 — ±0,25 |
| `BlockLeaves`, бит опадания metadata (`decayBit()`) | `Leaves1710` (`LeavesBlock`), свойство `persistent` | ✓ листва, которую поставил игрок, — `persistent`, как бит опадания от `ItemBlock.getMetadata` 1.7.10. Опадает по алгоритму автора (`updateTick`) на случайном тике; расстояние до бревна (`distance`) 1.20.1 считает сама — по нему листва ванилы находит бревно через листву автора. Держит воду, как листва 1.20.1: механика формы — 1.20.1, как у плит и лестниц |
| лут листвы 1.7.10 (`getDrops`, `IShearable`) | таблица: с ножницами или шёлковым касанием — сама листва, иначе — `getItemDropped` с шансом по удаче | ✓ шансы автора (`BlockLeavesMod`), взрыв лут не уменьшает (`dropBlockAsItemWithChance` с шансом 1) |
| листва при «быстрой» графике (`setGraphicsLevel`, иконка `_opaque`) | модель листвы и модель `_opaque`; при «быстрой» графике квадраты берутся из `_opaque` (`alfheim.port.client.AlfheimModels`) | ✓ сплошной или с отсечением листву рисует 1.20.1 по той же настройке |
| `isLeaves`, `canSustainLeaves`, `isWood` | теги `minecraft:leaves` (вся листва порта — `Leaves1710`), `minecraft:logs` (Ore Dictionary `logWood`) | ✓ по этим тегам листву и брёвна находят и листва ванилы, и прослойка (`World.kt`) |
| `beginLeavesDecay` — бревно сообщает соседней листве, что пора проверить опадание | не нужен | ✓ листва 1.20.1 пересчитывает расстояние сама, когда бревно убрали; у листвы автора `beginLeavesDecay` пустой |
| `WorldGenerator.generate(world, random, x, y, z)`, `setBlockAndNotifyAdequately(…, meta)` | `WorldGenerator` прослойки; блок ставится с флагами 3 (генератор создан с `notify`) или 2 | ✓ metadata — 0: вариант — сам блок (SPEC, Р-5) |
| `TerrainGen.saplingGrowTree` | `ForgeEventFactory.blockGrowFeature`: `DENY` — дерево не растёт | ✓ |
| `onPlantGrow` (под выросшим деревом трава и пашня → земля) | `Block.onPlantGrow` прослойки; дерево ванилы на блоке порта — `onTreeGrow` блока = true | ✓ в 1.7.10 дерево меняло на землю только траву и пашню; 1.20.1 меняет всё, чего нет в `minecraft:dirt` |
| высота мира 0–255 в генераторах | `minBuildHeight`, `maxBuildHeight` мира | ✓ мир 1.20.1 — от −64 до 320 |
| `BiomeGenBase.plantFlower` (`world.getBiomeGenForCoords(x, z).plantFlower(…)`) | `Level.plantFlower(random, x, y, z)` прослойки | ✓ цветы биома 1.20.1 — его цветочные узоры генерации; ставится цветок первого, как от костной муки на траве 1.20.1 |
| цвет блока (`getRenderColor`, `colorMultiplier`) | цвет блока на клиенте (`alfheim.port.client.AlfheimBlockColors`); модели — окрашенные копии шаблонов ванилы (`alfheim.port.data.TintedTemplates`), листва — `block/leaves`, крест — `block/tinted_cross` | ✓ metadata в `colorMultiplier` — номер варианта блока; у двойного растения у обеих половин он один. Предмет-блок 1.7.10 красил объёмную модель `getRenderColor` блока по metadata предмета (`ItemBlock` прослойки) |
| `IIridescentSaplingVariant`, `addTreeVariant(soil, wood, leaves, …)` | те же имена; почва, бревно и листва — массивы блоков-вариантов, блок без вариантов — `arrayOf(блок)` | ✓ номер варианта почвы — её индекс в массиве; `getWood` и `getLeaves` отдают сам блок варианта, `getMeta` — 0 |

## Ore Dictionary

`regOreDict` автора остаётся: `registerOre` прослойки записывает пары «имя — вещь», генерация данных
(`alfheim.port.data.OreDictTags`) превращает их в теги — один и тот же для предмета и для блока: в 1.7.10 по
Ore Dictionary искали и вещи, и блоки. Имя без строки в таблице роняет генерацию данных.

Имя, у которого есть общий тег Forge (так материалы называют Botania 1.20.1 и другие моды), — этот тег:
`ingotX` → `forge:ingots/x`, `nuggetX` → `forge:nuggets/x`, `dustX` → `forge:dusts/x`, `oreX` → `forge:ores/x`.
Остальные — тег Alfheim по имени автора в snake_case: `essenceMuspelheim` → `alfheim:essence_muspelheim`.
Предмет, который ставит блок под своим именем (`ItemNameBlockItem`: семена, лепестки Botania), в тег блоков не
попадает: в 1.7.10 это был простой предмет.

| Имя 1.7.10 | Тег 1.20.1 | Примечание |
|---|---|---|
| `sand` | `forge:sand` | ✓ |
| `coal` | `minecraft:coals` | ✓ уголь и древесный уголь |
| `slimeball` | `forge:slimeballs` | ✓ |
| `ingotElvorium`, `ingotMauftrium`, `ingotMuspelheimPower`, `ingotNiflheimPower` | `forge:ingots/elvorium`, `…/mauftrium`, `…/muspelheim_power`, `…/niflheim_power` | ✓ |
| `nuggetElvorium`, `nuggetMauftrium` | `forge:nuggets/elvorium`, `…/mauftrium` | ✓ |
| `dustIffesal` | `forge:dusts/iffesal` | ✓ |
| `oreDragonstone`, `oreElementium`, `oreQuartzElven`, `oreGold`, `oreIffesal`, `oreLapis` | `forge:ores/dragonstone`, `…/elementium`, `…/quartz_elven`, `…/gold`, `…/iffesal`, `…/lapis` | ✓ |
| `oreGoldAlfheim`, `oreLapisAlfheim` | `alfheim:ore_gold_alfheim`, `alfheim:ore_lapis_alfheim` | ✓ |
| `essenceMuspelheim`, `essenceNiflheim`, `furFenrir`, `runePrimalA`, `runeMuspelheimA`, `runeNiflheimA`, `twigDreamwoodInfused` | `alfheim:<имя в snake_case>` | ✓ |
| `twigThunderwood`, `splinterThunderwood`, `twigNetherwood`, `splinterNetherwood`, `coalFlame` | `alfheim:<имя в snake_case>` | ✓ |
| `dyeRainbow`, `dyeFloralPowder`, `petalRainbow`, `quartzRainbow`, `petalMystic` | `alfheim:<имя в snake_case>` | ✓ в `dyeRainbow` — и блок радужного моста Botania |
| `logWood`, `plankWood`, `slabWood`, `stairWood`, `treeSapling`, `treeLeaves` | `minecraft:logs`, `minecraft:planks`, `minecraft:wooden_slabs`, `minecraft:wooden_stairs`, `minecraft:saplings`, `minecraft:leaves` | ✓ имена Forge 1.7.10 для дерева: по ним брёвна держат листву, а доски идут в рецепты ванилы (палки, верстак, сундук), как рецепты Forge 1.7.10 с `plankWood` |
| `irisWood`, `irisLeaves`, `irisDirt` и они же с цветом (`irisWoodWhite` …) | `alfheim:iris_wood`, `alfheim:iris_wood_white` … | ✓ |
| `registerOre(name, block)`, `registerOre(name, item)` | `registerOre` прослойки: вещь блока или предмета | ✓ в 1.7.10 — с любой metadata; вариант в порту — свой блок |

Имена, которые автор берёт у Forge и Botania 1.7.10, в рецептах переводит таблица `Ingredients1710.ore`
(`alfheim.port.legacy.Recipes1710`): имя Forge — общий тег Forge или ванилы 1.20.1, если он есть, иначе предмет;
имя Botania — то, что ставит на его место в свои рецепты Botania 1.20.1. Имена Botania 1.7.10 в коде автора —
`alfheim.port.legacy.botania.LibOreDict` (в Botania 1.20.1 класса нет). Имя без строки в таблице роняет генерацию
данных: строку добавляет КТ, рецептам которой имя понадобилось.

| Имя 1.7.10 | Ингредиент 1.20.1 | Примечание |
|---|---|---|
| `ingotGold`, `nuggetGold`, `ingotBrickNether`, `gemQuartz`, `gemEmerald`, `dustRedstone`, `dustGlowstone`, `blockRedstone`, `cobblestone`, `stickWood` | `forge:ingots/gold`, `forge:nuggets/gold`, `forge:ingots/nether_brick`, `forge:gems/quartz`, `forge:gems/emerald`, `forge:dusts/redstone`, `forge:dusts/glowstone`, `forge:storage_blocks/redstone`, `forge:cobblestone`, `forge:rods/wooden` | ✓ имена Forge 1.7.10 |
| `dye<Цвет>`, `blockGlass<Цвет>` | `forge:dyes/<цвет>`, `forge:glass/<цвет>` | ✓ белый краситель 1.20.1 — не костная мука, как в 1.7.10 |
| `treeSapling`, `glowstone` | `minecraft:saplings`, предмет `minecraft:glowstone` | ✓ |
| `livingwood`, `dreamwood`, `ingotManasteel`, `manaDiamond`, `ingotTerrasteel`, `ingotElvenElementium`, `elvenDragonstone`, `nuggetManasteel`, `nuggetTerrasteel`, `nuggetElvenElementium`, `powderMana`, `petal<Цвет>` | `botania:livingwood_logs`, `botania:dreamwood_logs`, `botania:manasteel_ingots`, `botania:mana_diamond_gems`, `botania:terrasteel_ingots`, `botania:elementium_ingots`, `botania:dragonstone_gems`, `botania:manasteel_nuggets`, `botania:terrasteel_nuggets`, `botania:elementium_nuggets`, `botania:mana_dusts`, `botania:petals/<цвет>` | ✓ теги Botania 1.20.1 |
| `livingrock`, `manaPearl`, `livingwoodTwig`, `dreamwoodTwig`, `eternalLifeEssence`, `redstoneRoot`, `elvenPixieDust`, `bPlaceholder`, `bRedString`, `gaiaIngot`, `bEnderAirBottle`, `manaString`, `livingRoot`, `clothManaweave`, `blockBlaze` | предметы Botania 1.20.1 | ✓ так эти вещи стоят в рецептах Botania 1.20.1 |
| `rune<Имя>B`, `mysticFlower<Цвет>`, `mysticFlower<Цвет>Double`, `quartz<Имя>` | руны, цветы и кварц Botania 1.20.1 | ✓ |
| `shardPrismarine` | `minecraft:prismarine_shard` | ✓ осколок призмарина Botania 1.7.10 в 1.20.1 — предмет ванилы |
| `dirt`, `slabCobblestone`, `powderBlaze`, `rodBlaze` | `minecraft:dirt`, `minecraft:cobblestone_slab`, `minecraft:blaze_powder`, `forge:rods/blaze` | ✓ их регистрировала Botania 1.7.10 |
| `pestleAndMortar` | — | в Botania 1.20.1 ступки нет: рецепты автора с ней ждут решения владельца (TASKS.md) |

## Рецепты

`AlfheimRecipes` автора остаётся: вызовы 1.7.10 записывают рецепты в прослойку (`alfheim.port.legacy.Recipes1710`),
генерация данных (`alfheim.port.data.AlfheimRecipeProvider`) пишет из записей JSON 1.20.1 (SPEC, Р-9). В игре код
автора не выполняется. Рецепты вещей, которых ещё нет в порту, закомментированы блоками `/* PORT: КТ-n — … */`;
рецепт включается вместе со своей вещью — вместе со строкой `BotaniaAPI.getLatestAddedRecipe(s)` за ним, если она есть.

| Было (1.7.10) | Стало (1.20.1) | Примечание |
|---|---|---|
| `addOreDictRecipe`, `addShapelessOreDictRecipe` (ASJCore), `GameRegistry.addRecipe` / `addShapedRecipe` / `addShapelessRecipe`, `CraftingManager.getInstance().recipeList.add(ShapedOreRecipe(…))` | как есть — записывают `ShapedOreRecipe` / `ShapelessOreRecipe` прослойки; JSON — `minecraft:crafting_shaped`, `minecraft:crafting_shapeless` | ✓ шаблон зеркальный, как в 1.7.10; символ шаблона без ингредиента — пустая клетка; пустые края шаблона роняют генерацию данных (1.20.1 их срезает, и рецепт складывается в любом месте сетки) |
| `GameRegistry.addSmelting(вход, выход, опыт)` | `minecraft:smelting`, 200 тиков | ✓ `Item` и `Block` без стека — любой вариант, как `WILDCARD_VALUE` в 1.7.10 |
| ингредиент `ItemStack`, `Item`, `Block` | предмет 1.20.1 | ✓ сравнение — по предмету и metadata, без NBT и числа: повреждаемый предмет — только целый (`forge:partial_nbt`, `Damage: 0`), `ItemStack(x, n, WILDCARD_VALUE)` — с любым износом; предмет мода без вариантов с заданной metadata (`ItemStack(hyperBucket, 1, i)`) — только с ней: ингредиент `alfheim:meta` (`MetaIngredient`, metadata — повреждение стака); `Block` в рецепте по шаблону — любой вариант, в рецепте без формы — вариант 0; блок или предмет с вариантами в порту — массив (`livingcobble`): «любой» — все варианты |
| имя Ore Dictionary | тег или предмет | ✓ раздел «Ore Dictionary» |
| результат `ItemStack(x, n, meta)` | `ItemStack(x[meta], n)` | ✓ раздел «Имена и metadata»; у предмета мода без вариантов metadata — повреждение (`ItemStack.meta` ASJCore): `ItemStack(hyperBucket, 1, 2)` — гиперведро уровня 2, в JSON — `"nbt": "{Damage:2}"`. Предмет Botania или ванилы с metadata — предмет 1.20.1 (разделы «Botania», «Предметы, сущности, эффекты») |
| id рецепта (в 1.7.10 его не было) | путь id результата: `alfheim:elvorium_ingot`; переплавка — `…_from_smelting`; одинаковые — `_2`, `_3` в порядке рецептов автора | ✓ |
| книга рецептов (в 1.7.10 её не было) | достижений, открывающих рецепты, нет: рецепт попадает в книгу, когда игрок его сделает | ✓ раздел книги — по результату: блок — «строительство», в печи еда — «еда» |
| особый рецепт: `addRecipe(RecipeX)` + `RecipeSorter.register("alfheim:имя", …)` | тип рецепта `alfheim:<имя в snake_case>` со своим сериализатором (`alfheim.port.registry.LegacySpecialRecipes`), JSON `{"type": "alfheim:имя"}`; рецепт верстака 1.20.1 зовёт `matches`, `getCraftingResult`, `getRecipeSize` класса автора | ✓ как особые рецепты ванилы, в книге рецептов его нет; новый особый рецепт — строка в `LegacySpecialRecipes` |
| `IRecipe`, `InventoryCrafting`, `inv.sizeInventory`, `inv[i]` | `IRecipe` прослойки (Java: `recipe.recipeOutput` читается как свойство), `CraftingContainer`, `containerSize`, `getItem` (пустой — `null`) | ✓ |
| `BotaniaAPI.getLatestAddedRecipe()`, `getLatestAddedRecipes(n)` | `alfheim.port.legacy.botania.BotaniaAPI` — последние записанные рецепты | ✓ |
| рецепт, раскладку которого занимает рецепт другого мода 1.20.1 | GameTest `PortRecipesTest.recipesDoNotOverlap` | известные пересечения — в тесте, с вопросом владельцу |

## Прослойка `alfheim.port.legacy`

Функции с сигнатурами 1.7.10 поверх 1.20.1 (SPEC, Р-4). Код автора с ними остаётся как был, нужен только импорт
`alfheim.port.legacy.*`. Проверяет GameTest `PortLegacyTest`.

| Вызов 1.7.10 | Что делает в 1.20.1 | Примечание |
|---|---|---|
| `getBlock(x, y, z)`, `getTileEntity(x, y, z)`, `isAirBlock(x, y, z)` | `getBlockState(pos).block`, `getBlockEntity(pos)`, `getBlockState(pos).isAir` | у `BlockGetter` (IBlockAccess 1.7.10) |
| `setBlock(x, y, z, block)` | `setBlock(pos, block.defaultBlockState(), 3)` | metadata 0 — состояние по умолчанию |
| `setBlock(x, y, z, block, meta, flags)` | `setBlock(x, y, z, state, flags)` прослойки | состояние вместо metadata выбирает вызывающий код; младшие флаги 1, 2, 4 те же |
| `setBlockToAir(x, y, z)` | `setBlock(pos, AIR, 3)` | |
| `notifyBlocksOfNeighborChange(x, y, z, block)`, `scheduleBlockUpdate(x, y, z, block, delay)` | `updateNeighborsAt`, `scheduleTick` | |
| `spawnEntityInWorld(entity)`, `isRemote` | `addFreshEntity(entity)`, `isClientSide` | |
| `playSoundEffect(x, y, z, name, volume, pitch)`, `playSoundAtEntity(entity, name, volume, pitch)` | `playSound(null, x, y, z, событие, категория, volume, pitch)` | как в 1.7.10: на сервере — всем рядом, на клиенте — ничего |
| `playSound(x, y, z, name, volume, pitch, distanceDelay)` | `playLocalSound(...)` | звук только на этом клиенте |
| имя звука ванилы или Botania | событие из `alfheim/legacy_sounds.json` | таблицу собирает `tools/legacy_sounds.py`: событие 1.20.1 с теми же аудиофайлами и категория из sounds.json 1.7.10; ничьи решены в `OVERRIDES` скрипта. Звука нет в таблице — запустить скрипт |
| `spawnParticle(name, x, y, z, vx, vy, vz)` | `addParticle(тип, ...)` | только частицы без параметров (`LegacyParticles`); `reddust`, `mobSpell`, `iconcrack_…`, `blockcrack_…` — на месте вызова |
| `Block.toItem()`, `Item.toBlock()`, `ItemStack.block` (ASJCore) | `asItem()` (воздух → `null`), `Block.byItem` | ✓ |
| `soundTypeStone`, `soundTypeWood`, `soundTypeGrass`, `soundTypeGravel`, `soundTypeCloth`, `soundTypeGlass`, `soundTypeSnow`, `soundTypeSand`, `soundTypeMetal`, `soundTypePiston`, `soundTypeLadder`, `soundTypeAnvil` в классе блока | как есть — `SoundTypes1710` у `Block1710` и `BlockFalling1710` | ✓ `SoundType.STONE`, `WOOD`, `GRASS`, `GRAVEL`, `WOOL`, `GLASS`, `SNOW`, `SAND`, `METAL`, `STONE`, `LADDER`, `ANVIL` |
| `Block.soundTypeStone` и др. вне класса блока | `Block1710.soundTypeStone` и др. | ✓ |
| `registerOre(name, stack)` (Ore Dictionary) | `registerOre` прослойки: записывает пару, теги строит генерация данных | ✓ раздел «Ore Dictionary» |
| `hasKey`, `removeTag`, `setTag`, `getCompoundTag`, `setString`, `setInteger`, `getInteger`, `setDouble`, `setFloat`, `setBoolean` у NBT | `contains`, `remove`, `put`, `getCompound`, `putString`, `putInt`, `getInt`, `putDouble`, `putFloat`, `putBoolean` (`NBT1710`) | ✓ |
| `e.entityLiving`, `e.entityPlayer`, `e.ammount` (`LivingHurtEvent`, `LivingAttackEvent`), `HarvestCheck.success`, `source.damageType` | `entity`, `amount`, `canHarvest`, `msgId` (`Events1710`) | ✓ |
| `hurtResistantTime`, `maxHurtResistantTime`, `lastDamage` существа | `invulnerableTime`, `invulnerableDuration`, `lastHurt` (открыт преобразователем доступа) | ✓ |
| `getEntitiesWithinAABB(world, clazz, aabb)`, `getBoundingBox(…)`, `Entity.boundingBox(range)`, `AABB.expand` / `offset` (ASJCore) | `getEntitiesOfClass`, `AABB`, `inflate` / `move` | ✓ наблюдатели 1.20.1 в выборку не попадают — в 1.7.10 их не было |
| `Entity.playSoundAtEntity(name, volume, pitch)` (ASJCore) | `level().playSoundAtEntity(entity, …)` прослойки | ✓ |
| `ASJUtilities.isServer` / `isClient` | выделенный сервер — сервер; иначе — логическая сторона потока (`EffectiveSide`) | ✓ потоки загрузки Forge считают себя клиентом: без проверки выделенного сервера код загрузки на сервере шёл бы клиентскими ветками |
| `MovingObjectPosition`, `MovingObjectType`; `typeOfHit`, `hitVec`, `blockX` / `blockY` / `blockZ`, `sideHit`, `entityHit` | `HitResult` (псевдоним) и свойства с именами 1.7.10 (`Hit1710`) | ✓ сторона — номер 1.7.10, он равен `Direction.get3DDataValue` |
| `inventory[i]`, `inventory[i] = stack` (`IInventory`, ASJCore) | `Container.getItem(i)` / `setItem(i, …)` | ✓ пустой стек ↔ `null` |
| `world.provider.dimensionId`, `entity.dimension` (номер измерения) | `level.dimensionId` — id строкой (`minecraft:overworld`); обратно — `dimensionKey(id)` | ✓ номеров измерений в 1.20.1 нет: где автор хранит номер, хранится id |
| `ChunkCoordinates(x, y, z)`, `posX` / `posY` / `posZ`, `val (x, y, z) = coords` | как есть — `ChunkCoordinates` = `BlockPos` (`Coords1710`) | ✓ `BlockPos` неизменяемый: код, который меняет точку, переписывается на месте |
| `ForgeDirection` (`getOrientation`, `VALID_DIRECTIONS`, `UNKNOWN`, `offsetX` / `Y` / `Z`) | как есть — перечисление `ForgeDirection` прослойки; для API 1.20.1 — `.direction` (`Direction`, у `UNKNOWN` — `null`) | ✓ номера сторон 1.7.10 = `Direction.get3DDataValue`; остальное (`getOpposite`, `getRotation`) — по мере надобности |
| `player.capabilities.isCreativeMode` / `isFlying` / `allowFlying` / `disableDamage` | как есть — `Abilities` 1.20.1: `instabuild` / `flying` / `mayfly` / `invulnerable` (`Player1710`) | ✓ |
| `GameRegistry.findUniqueIdentifierFor(block \| item).toString()` | как есть — ключ реестра (`modid:name`) | ✓ |
| `nbt.hasNoTags()`, `world.removeTileEntity(x, y, z)` | `isEmpty`, `removeBlockEntity(pos)` | ✓ |
| `checkChunksExist(…)`, `getBlockLightValue(x, y, z)`, `getStrongestIndirectPower(x, y, z)` | `hasChunksAt`, `getMaxLocalRawBrightness`, `getBestNeighborSignal` | ✓ |
| `block.canSustainPlant(world, x, y, z, direction, plantable)`, `isLeaves`, `canSustainLeaves`, `isAir`, `isReplaceable`, `onPlantGrow`, `isNormalCube(world, x, y, z)` | у состояния в точке: `canSustainPlant`, теги `minecraft:leaves` и `minecraft:logs`, `isAir`, `canBeReplaced`, трава и пашня → земля, `isRedstoneConductor` | ✓ `isNormalCube` до 1.16 и назывался так |
| `block.canBlockStay(world, x, y, z)`, `block.canPlaceBlockAt(world, x, y, z)` у любого блока | растение порта — своё правило (`Bush1710`, `DoublePlant1710`), прочие — `canSurvive` (и заменяемое место) | ✓ |
| `world.getBiomeGenForCoords(x, z).plantFlower(world, random, x, y, z)` | `world.plantFlower(random, x, y, z)` | ✓ раздел «Растения» |
| `GameRegistry.registerFuelHandler(handler)` | `Fuel1710.handlers` | ✓ раздел «Блоки и предметы» |

## Предметы, сущности, эффекты

| Было | Стало | Примечание |
|---|---|---|
| `onItemRightClick` / `onItemUse` / `onUpdate` / `addInformation` | `use` / `useOn` / `inventoryTick` / `appendHoverText` | `onItemUse` предмета автора остаётся как есть: `Item1710.useOn` зовёт его (`true` — успех, рука взмахивает; `false` — `PASS`, дальше `use`, как в 1.7.10) |
| `onUsingTick(stack, player, count)` | `onUseTick(level, entity, stack, count)` | ✓ предмет может держать любое существо: код автора — после `entity as? Player ?: return` |
| `player.theItemInWorldManager.blockReachDistance` | `player.blockReach` (атрибут Forge) | ✓ дальность руки игрока: в 1.7.10 на сервере — 5 блоков в любом режиме, в 1.20.1 — 4,5, в творческом — 5 |
| `ASJUtilities.getMouseOver(entity, dist, interact)`, `getSelectedBlock(entity, dist, stopOnLiquid)`, `rayTrace(entity, dist)` | те же функции ASJCore на `ClipContext` | ✓ луч — от глаз (`eyePosition`) на обеих сторонах; `stopOnLiquid` — `ClipContext.Fluid.SOURCE_ONLY` (только источники, как в 1.7.10); промах — `null` |
| `stack.stackTagCompound`, `NBTTagCompound` | `stack.getTag()` / `getOrCreateTag()`, `CompoundTag` | `ItemNBTHelper` автора сохраняется поверх |
| `EntityPlayer`, `EntityLivingBase` | `Player`, `LivingEntity` | |
| `isSneaking`, `heldItem`, `riddenByEntity` | `isShiftKeyDown`, `mainHandItem` (пустой стек вместо `null`), `firstPassenger` | ✓ |
| `mountEntity(entity)` | `startRiding(entity, true)` | ✓ 1.7.10 сажал без проверок, кроме кольца из всадников; `force = true` — так же |
| `EntityTameable.isTamed`, `func_152115_b(uuidString)` | `isTame`, `setOwnerUUID(uuid)` | ✓ |
| `Items.stick`, `Blocks.grass` и др. | `Items.STICK`, `Blocks.GRASS_BLOCK` и др. | поля ванилы в 1.20.1 — заглавными; имя проверять по смыслу |
| `Blocks.planks` 5, `Items.dye` 4, `Items.coal` 1 | `Blocks.DARK_OAK_PLANKS`, `Items.LAPIS_LAZULI`, `Items.CHARCOAL` | ✓ вариант ванилы 1.7.10 — отдельный предмет 1.20.1 |
| `Items.skull` 3, `Items.cooked_fished` 0 | `Items.PLAYER_HEAD`, `Items.COOKED_COD` | ✓ |
| `World` в сигнатурах автора | `import net.minecraft.world.level.Level as World` | ✓ |
| `entityInit` + `DataWatcher` | `defineSynchedData` + `SynchedEntityData` | ✓ номер ячейки → ключ `SynchedEntityData.defineId(Класс::class.java, EntityDataSerializers.…)` в `companion object` класса; `addObject(n, v)` → `define(KEY, v)`, `getWatchableObject…(n)` / `updateObject(n, v)` → `get(KEY)` / `set(KEY, v)`; `setObjectWatched` не нужен |
| `EntityThrowable` (`super(world)`, `super(world, thrower)`, `onImpact`, `getThrower`, `func_70182_d` — скорость, `func_70183_g` — поправка угла, `getGravityVelocity`, `setSize`) | `alfheim.port.legacy.EntityThrowable` поверх `ThrowableProjectile`: `super(legacyType<Класс>(), world)`, остальное — имена 1.7.10 | ✓ бросок — из глаз, на 0,16 вбок и 0,1 вниз, как в 1.7.10; `setThrowableHeading` → `shoot`: разброс 1.20.1 — треугольный, той же величины. Блоки 1.20.1, которые отвечают на удар снаряда (мишень, колокол), не отвечают |
| `worldObj`, `setDead()`, `getDistanceSqToEntity(e)`, `attackEntityFrom(source, amount)`, `setFire(seconds)` | `level()`, `discard()`, `distanceToSqr(e)`, `hurt`, `setSecondsOnFire` (`Entity1710`) | ✓ |
| `posX` / `posY` / `posZ`, `motionX` / `motionY` / `motionZ`, `rotationYaw` / `rotationPitch`, `setLocationAndAngles`, `setPosition(x, y, z)`, `setThrowableHeading` | `x` … (запись — `setPos`), `deltaMovement`, `yRot` / `xRot`, `moveTo`, `setPos`, `shoot` (`Entity1710`) | ✓ `posY` — низ существа; у своего игрока на клиенте 1.7.10 он был на уровне глаз — такие места переносятся на месте вызова. `yOffset` (сдвиг рисунка) в 1.20.1 нет |
| `BlockDispenser.dispenseBehaviorRegistry.putObject(предмет, поведение)` | `DispenserBlock.registerBehavior(предмет, поведение)` | ✓ в `FMLCommonSetupEvent`, в очереди основного потока (`enqueueWork`): предметы 1.20.1 создаются позже preInit, а реестр общий для модов и не потокобезопасный |
| `IBlockSource` (`xInt`, `getWorld`, `getBlockTileEntity`), `IBehaviorDispenseItem`, `BehaviorDefaultDispenseItem.dispenseStack` | `BlockSource` (`pos`, `level`, `getEntity`), `DispenseItemBehavior`, `DefaultDispenseItemBehavior.execute` (`Dispenser1710`) | ✓ |
| `BlockDispenser.func_149937_b(blockMetadata)` (сторона раздатчика), `EnumFacing.frontOffsetX` | `blockState.getValue(DispenserBlock.FACING)`, `Direction.stepX` (`frontOffsetX` — `Coords1710`) | ✓ номера сторон те же |
| `stack.func_150996_a(предмет)` (замена предмета стака), `TileEntityDispenser.func_146019_a(stack)` | новый стак — его раздатчик кладёт в слот; `DispenserBlockEntity.addItem(stack)` | ✓ |
| `Blocks.flowing_water` с metadata 0, `Blocks.water` | `Blocks.WATER` с `LiquidBlock.LEVEL` 0 | ✓ стоячей и текучей воды 1.7.10 в 1.20.1 нет — один блок |
| `EntityDamageSourceIndirect("fireball", прямой, виновник).setFireDamage()` | `DamageSource(тип DamageTypes.FIREBALL из реестра, прямой, виновник)` | ✓ тип «огненный шар» — огненный; `damageSources().fireball` 1.20.1 принимает только огненный шар (`Fireball`) |
| `world.playAuxSFX(2002, x, y, z, metadata зелья)` (брызги зелья) | `level.levelEvent(2002, BlockPos, цвет)` | ✓ в 1.20.1 число события — цвет брызг (`MobEffects.X.color`); частицы — от середины низа блока, в 1.7.10 — от угла |
| `Potion.isInstant()`, `affectEntity(thrower, target, amplifier, health)`, `PotionEffect.getEffectName()`, `Potion.getDurationString(effect)` | `isInstantenous`, `applyInstantenousEffect(thrower, thrower, target, …)`, `descriptionId`, `MobEffectUtil.formatDuration` — в `Effects1710` и `Potion1710` | ✓ урон засчитывается бросившему, без бросившего — обычная магия, как в 1.7.10. Длительность 1.20.1 — `03:00` (в 1.7.10 — `3:00`), как у всех зелий 1.20.1 |
| `readEntityFromNBT` / `writeEntityToNBT` | `readAdditionalSaveData` / `addAdditionalSaveData` | |
| `onUpdate` (сущность) | `tick` | |
| `applyEntityAttributes` | атрибуты в `EntityAttributeCreationEvent` | |
| `EntityRegistry.registerModEntity` (`ASJUtilities.registerEntity(класс, имя, номер)`) | `LegacyRegistration.entity(класс, имя)` → `EntityType` с конструктором `(World)` в событии регистрации | ✓ слежение 8 чанков (128 блоков), обновление каждый тик, скорость — клиентам, как `registerModEntity(…, 128, 1, true)`; id — имя в snake_case (`ThrownPotion` → `alfheim:thrown_potion`); тип для `super(…)` — `legacyType<Класс>()`. Имя 1.7.10 — `alfheim.ThrownPotion`, его ключ перевода `entity.alfheim.ThrownPotion.name` → `entity.alfheim.thrown_potion` |
| яйцо существа (`registerEntity` с цветами) | `ForgeSpawnEggItem` | цвета яиц — авторские |
| `IExtendedEntityProperties` | Capability Forge | SPEC, Р-11 |
| `WorldSavedData` | `SavedData` | |
| `Potion` (зелье автора и `Potion.regeneration` и др.) | `alfheim.port.legacy.Potion1710` — наследник `MobEffect` с API 1.7.10; ванила — его поля с именами 1.7.10 | ✓ номер 1.7.10 остаётся: ванила — 1–23, зелья автора — номера по умолчанию из конфига, прочие эффекты реестра — свободные номера после регистрации (`Potion1710.assignIds`). `Potion.potionTypes[id]`, `MobEffect.id`, `PotionEffect.potionID`. Имя в реестре — из `setPotionName` без приставки, в snake_case: `alfheim.potion.whiteWine` → `alfheim:white_wine` |
| `isReady` / `performEffect` / `applyAttributesModifiersToEntity` / `removeAttributesModifiersFromEntity` / `func_111184_a` / `func_111183_a` / `func_111186_k` | те же методы `Potion1710`; 1.20.1 зовёт их вместо `isDurationEffectTick` / `applyEffectTick` / `addAttributeModifiers` / `removeAttributeModifiers` / `addAttributeModifier` / `getAttributeModifierValue` / `getAttributeModifiers` | ✓ |
| `setIconIndex`, `getStatusIconIndex` + `bindTexture(лист)` | `setIconIndex` как есть, лист — `Potion1710.iconSheet`; иконку рисует клиент (`alfheim.port.client.LegacyEffectIcons`) | ✓ клетка 18×18 листа 256×256 с высоты 198, как в 1.7.10; и в инвентаре, и в углу экрана |
| `PotionEffect(id, duration, amplifier)`, `pe.duration = …`, `pe.amplifier = …` | `MobEffectInstance` (`PotionEffect` — псевдоним прослойки, `PotionEffect(id, …)`); поля открыты преобразователем доступа | ✓ как ASJCore в 1.7.10 |
| `isPotionActive(id \| зелье)`, `getActivePotionEffect(id \| зелье)`, `addPotionEffect`, `removePotionEffect(id)`, `activePotionEffects` | `alfheim.port.legacy` (`Effects1710`): `hasEffect`, `getEffect`, `addEffect`, `removeEffect`, `activeEffects` | ✓ |
| `activePotionEffects.iterator()` + `remove()` + `onFinishedPotionEffect(pe)` | `activeEffects.toList().forEach { removeEffect(it.effect) }` | ✓ `removeEffect` и убирает эффект, и снимает его действие |
| `SharedMonsterAttributes.maxHealth` и др., `getEntityAttribute`, `applyModifier`, `AttributeModifier(uuid, name, amount, operation: Int)` | `Attributes.MAX_HEALTH` и др., `getAttribute`, `addPermanentModifier`, `AttributeModifier.Operation.fromValue` — в `Effects1710` | ✓ |
| `entityData` (NBT существа Forge) | `persistentData` | ✓ правится на месте |
| `entityId`, `uniqueID`, `getRNG()` (`rng`) | `id`, `uuid`, `random` (`RandomSource`) | ✓ `Collection.random(RandomSource)` (ASJCore) — выбор по генератору 1.20.1 |
| `ridingEntity` | `vehicle` | ✓ |
| `DamageSource` автора | тип урона (`damage_type` в датапаке) + `DamageSource` | с 1.19.4 типы урона — данные |

## Класс мода и жизненный цикл

Выбрано в КТ-0 при переносе `AlfheimCore.kt`.

| Было | Стало | Примечание |
|---|---|---|
| `@Mod(modid, dependencies, useMetadata, guiFactory, modLanguageAdapter)` | `@Mod(MODID)` на `object` + `META-INF/mods.toml` | ✓ зависимости и описание — в `mods.toml`, язык — `modLoader="kotlinforforge"`; экран настроек (`guiFactory`) не переносится: настройки в игре показывает мод Configured, если он есть в сборке |
| `@EventHandler` + `FMLConstructionEvent`, `FMLPreInitializationEvent` | вызов из `init {}` объекта мода | ✓ регистрация (DeferredRegister) и конфиг в 1.20.1 возможны только в конструкторе мода |
| `@EventHandler` + `FMLInitializationEvent` | `FMLCommonSetupEvent` на шине мода | ✓ идёт параллельно с другими модами: непотокобезопасное — в `event.enqueueWork {}` |
| `@EventHandler` + `FMLPostInitializationEvent` | `InterModProcessEvent` на шине мода | ✓ последнее событие загрузки, когда все моды прошли setup |
| `@EventHandler` + `FMLLoadCompleteEvent` | `FMLLoadCompleteEvent` на шине мода | |
| `@EventHandler` + `FMLServerStartingEvent` | `ServerStartingEvent` на шине Forge | ✓ |
| `event.registerServerCommand(...)` | `RegisterCommandsEvent` на шине Forge | команды регистрируются до `ServerStartingEvent` |
| подписка методов `@EventHandler` | `MOD_BUS` / `FORGE_BUS` из `thedarkcolour.kotlinforforge.forge`, `addListener(EventPriority.NORMAL, false, Event::class.java, ::метод)` | ✓ форма с явным классом события; имена методов автора сохраняются |
| `@KotlinProxy` (ASJ) | `DistExecutor.unsafeRunForDist({ Supplier { ClientProxy } }, { Supplier { CommonProxy() } })` | ✓ классы прокси автора сохраняются (SPEC, Р-11); на сервере `ClientProxy` не загружается (`PortProxyTest`) |
| `@Metadata ModMetadata` | `ModList.get().getModContainerById(MODID).get().modInfo` (`IModInfo`) | версия — `ArtifactVersion`, имя — `displayName` |
| `Loader.isModLoaded("Mod")` | `ModList.get().isLoaded("mod")` | ✓ id модов в 1.20.1 — строчными |
| `MinecraftServer.getServer()` | `ServerLifecycleHooks.getCurrentServer()` | ✓ |
| `saveHandler.worldDirectory` | `server.getWorldPath(LevelResource.ROOT).toAbsolutePath().normalize()` | ✓ `ROOT` даёт путь с «.» на конце |

## События, сеть, конфиг, команды

| Было | Стало | Примечание |
|---|---|---|
| `eventForge()` / `eventFML()` (asjlib) | `MinecraftForge.EVENT_BUS.register(...)` | события жизненного цикла мода — на шине мода |
| `cpw.mods.fml…SubscribeEvent` | `net.minecraftforge.eventbus.api.SubscribeEvent` | |
| `SimpleNetworkWrapper`, `IMessage` | `SimpleChannel`; `ASJPacket.toBytes` / `fromBytes` / `create`, обработчик `AlfheimPacket.onMessage` | ✓ поля пишет `ASJPacket`, как coremod `ASJPacketCompleter`: свои поля класса пакета, не static и не final, в порядке объявления, после `toCustomBytes`. Обработка — в основном потоке (`enqueueWork`); ошибка обработчика пишется в лог (`exceptionally`), как в 1.7.10 — иначе она остаётся в `CompletableFuture` |
| регистрация пакета для обеих сторон (`Side.CLIENT` и `Side.SERVER`) | второй раз — без привязки к направлению | ✓ в `SimpleChannel` класс пакета — ключ кодека |
| `sendToDimension(packet, dimId)` | `sendToDim(packet, ResourceKey<Level>)`, `PacketDistributor.DIMENSION` | ✓ номеров измерений нет |
| `NetworkRegistry.TargetPoint(dim, x, y, z, range)` | `alfheim.port.legacy.TargetPoint(level.dimension(), x, y, z, range)` | ✓ радиус как у автора; в Forge он в квадрате, переводит `toForge()` |
| `cpw.mods.fml.relauncher.Side` | `net.minecraftforge.fml.LogicalSide` | ✓ те же `CLIENT` / `SERVER`; `@SideOnly` → `@OnlyIn(Dist.…)` |
| `player.playerNetServerHandler.kickPlayerFromServer(text)` | `player.connection.disconnect(Component.literal(text))` | ✓ |
| `commandSenderName` игрока | `gameProfile.name` | ✓ |
| `MinecraftServer.getServer().configurationManager.playerEntityList` | `ServerLifecycleHooks.getCurrentServer().playerList.players` | ✓ |
| `PlayerEvent.PlayerLoggedInEvent` / `PlayerLoggedOutEvent` (FML) `.player` | `PlayerEvent.PlayerLoggedInEvent` / `PlayerLoggedOutEvent` (Forge) `.entity` | ✓ |
| `TickEvent` (FML) | `net.minecraftforge.event.TickEvent` | ✓ на шине Forge |
| своё событие без конструктора без аргументов | как есть | ✓ шина Forge дописывает его сама, как FML 1.7.10 (`PortNetworkTest.eventsAcceptListeners`) |
| `javax.xml.bind…HexBinaryAdapter().marshal(bytes)` | `HexFormat.of().withUpperCase().formatHex(bytes)` | ✓ JAXB убран из Java 11 |
| `Configuration` | `ForgeConfigSpec` через прослойку `alfheim.port.config.Configuration` | ✓ SPEC, Р-12; подробности — раздел «Файлы конфига» |
| `CommandBase` | Brigadier, `RegisterCommandsEvent` | имена и аргументы — как у автора |
| `ClientRegistry.registerKeyBinding` | `RegisterKeyMappingsEvent` | |
| Ore Dictionary | теги | |
| достижения (`Achievement`) | advancements через datagen | |
| `ChatComponentText`, `EnumChatFormatting` | `Component.literal`, `ChatFormatting` | |
| `ChatComponentTranslation`, `IChatComponent.Serializer.func_150699_a` | `Component.translatable`, `Component.Serializer.fromJson` | ✓ |
| `ICommandSender`, `addChatMessage` | `CommandSource`, `sendSystemMessage` | ✓ `ASJUtilities.say`; имя отправителя — `Nameable.name` или «Server» |
| `EntityInteractEvent` `.entityPlayer`, `.target` | `PlayerInteractEvent.EntityInteract` `.entity`, `.target` | ✓ событие приходит для каждой руки: проверка предмета в главной руке повторяется, второй вызов ничего не меняет |
| сделать после того, как событие закончится («Post» 1.7.10, которого в 1.20.1 нет) | `server.tell(TickTask(server.tickCount) { … })` — после тика | ✓ `server.execute` на потоке сервера выполняет задачу сразу |
| `LivingPotionEvent` ASJCore: `Add.Post`, `Change.Post`, `Remove.Post` | `MobEffectEvent.Added` (`oldEffectInstance != null` — изменение), `MobEffectEvent.Remove`, `MobEffectEvent.Expired` | ✓ Forge сообщает об изменении до того, как эффект обновлён: пакет об изменении уходит в конце тика (`server.execute`). У `Remove` свой `effect` — `MobEffect`, эффект с длительностью — `effectInstance` (может быть `null`) |
| `LivingUpdateEvent`, `LivingSetAttackTargetEvent`, `EntityJoinWorldEvent` | `LivingEvent.LivingTickEvent`, `LivingChangeTargetEvent` (цель снимается в самом событии: `newTarget = null`), `EntityJoinLevelEvent` | ✓ |
| `BlockEvent.PlaceEvent`, `MultiPlaceEvent` | `BlockEvent.EntityPlaceEvent`, `EntityMultiPlaceEvent` | ✓ |
| `RenderBlockOverlayEvent`, `DrawBlockHighlightEvent` | `RenderBlockScreenEffectEvent`, `RenderHighlightEvent.Block` | ✓ |
| объект без методов `@SubscribeEvent` на шине | как есть | ✓ шина Forge 1.20.1 принимает его молча — общий обработчик подписывается, даже если его методы ещё ждут свою КТ |

## Рендер

| Было | Стало | Примечание |
|---|---|---|
| `Tessellator`, `GL11` | `PoseStack`, `MultiBufferSource`, `VertexConsumer`, `RenderType`, `RenderSystem` | |
| `ISimpleBlockRenderingHandler` | JSON-модель, BakedModel или BlockEntityRenderer | SPEC, Р-13 |
| рендер двойного растения автора (`RenderBlockColoredDoubleGrass`): крест половин с цветом нижней | окрашенные кресты половин из генерации данных; смещение по X и Z — `DoublePlant1710` | ✓ раздел «Растения» |
| `IItemRenderer` | `BlockEntityWithoutLevelRenderer` через `IClientItemExtensions` | |
| `ModelBase` / `ModelRenderer` | `EntityModel` / `ModelPart` + `LayerDefinition` | размеры, опоры, UV — авторские |
| `RenderingRegistry.registerEntityRenderingHandler` | `EntityRenderersEvent.RegisterRenderers` (`alfheim.port.client.AlfheimEntityRenderers`) | ✓ событие идёт раньше postInit, где автор регистрировал рендер, поэтому пары «существо — рендер» собраны там; строки в `ClientProxy` помечены. У каждого существа нужен рендер: без него клиент падает |
| `Render` (объект): `doRender(entity, x, y, z, yaw, partialTicks)`, `getEntityTexture` | `EntityRenderer<T>` (класс с конструктором от контекста): `render(entity, yaw, partialTicks, poseStack, buffers, light)`, `getTextureLocation` | ✓ смещение к существу уже в матрице; `GL_BLEND` — вид отрисовки `RenderType.entityTranslucent(атлас)`, без смешивания — `entityCutout`; `TextureMap.locationItemsTexture` → `InventoryMenu.BLOCK_ATLAS` |
| квадрат лицом к камере: `glRotatef(180 − playerViewY, Y)`, `glRotatef(−playerViewX, X)` | `mulPose(entityRenderDispatcher.cameraOrientation())`, `mulPose(YP(180))` | ✓ так рисует брошенный предмет ванила 1.20.1. Цвет, свет (`setBrightness(240)` → `uv2(240)`), наложение и нормаль — у каждой вершины |
| иконка предмета в рендере: `item.getIcon(stack, pass)`, `getIconFromDamage(meta)` | `alfheim.port.client.getIcon` / `getIconFromDamage` — спрайт слоя модели предмета с тем же номером | ✓ проход 0 — частица модели (слой 0) |
| `RenderGlobal.doSpawnParticle(имя, …)` из `worldAccesses` (частица, которой потом меняют цвет или скорость) | `mc.levelRenderer.addParticleInternal(данные, данные.type.overrideLimiter, …)` (открыт в `accesstransformer.cfg`) | ✓ те же правила ванилы: дальность и настройка «Частицы»; `LevelRenderer` у мира клиента один |
| `EntityFX.setRBGColorF(r, g, b)`, `multiplyVelocity(m)` | `Particle.setColor(r, g, b)`, `setPower(m)` | ✓ |
| частица `iconcrack_<id>_<meta>` | `ItemParticleOption(ParticleTypes.ITEM, ItemStack(предмет))` | ✓ рисунок — частица модели предмета (слой 0), как иконка прохода 0 в 1.7.10 |
| `bindTileEntitySpecialRenderer` | `EntityRenderersEvent.RegisterRenderers` (`registerBlockEntityRenderer`) | |
| `ASJShaderHelper` + шейдеры автора | core shaders через `RegisterShadersEvent` + свой `RenderType` | |
| `setGlow` (asjlib) | полная яркость, `LightTexture.FULL_BRIGHT` | |
| `mc.gameSettings.particleSetting` | `mc.options.particles().get().id` | ✓ номера те же: 0 — все, 1 — меньше, 2 — минимум |
| `mc.effectRenderer.addEffect(частица)` | `mc.particleEngine.createParticle(данные, x, y, z, vx, vy, vz)` | без ограничителя частиц ванилы, как `addEffect`; `level.addParticle` ограничивает ещё раз |
| `RenderWorldLastEvent` | `RenderLevelStageEvent`, этап `AFTER_LEVEL` (после мира и погоды, до руки) | ✓ **на этом этапе Forge 1.20.1 кладёт в событие матрицу проекции, а не камеры**: поворот камеры строится сам — `PoseStack()`, `mulPose(XP(camera.xRot))`, `mulPose(YP(camera.yRot + 180))`. На других этапах `poseStack` события — камера |
| `RenderManager.renderPosX` / `Y` / `Z` | `mc.gameRenderer.mainCamera.position` | ✓ |
| линии `GL_LINES` / `GL_LINE_STRIP` с `glLineWidth` | шейдер линий ванилы: `RenderSystem.setShader(GameRenderer::getRendertypeLinesShader)`, формат `POSITION_COLOR_NORMAL`, у вершины — направление отрезка, толщина — `RenderSystem.lineWidth` | ✓ шейдер позиции рисует линии в 1 пиксель; рамка блока — `LevelRenderer.renderLineBox` |
| текст в мире (`fontRenderer.drawString` в матрице мира) | `font.drawInBatch(…, Font.DisplayMode.SEE_THROUGH / NORMAL, 0, LightTexture.FULL_BRIGHT)` + `MultiBufferSource.immediate(…).endBatch()` | ✓ `SEE_THROUGH` — без проверки глубины |
| `RenderGameOverlayEvent.Post` (`ElementType.ALL`), `ScaledResolution` | `RenderGuiEvent.Post` (`guiGraphics`), `mc.window.guiScaledWidth` / `guiScaledHeight` | ✓ |
| `mc.objectMouseOver` | `mc.hitResult` | ✓ промах в 1.20.1 — `MISS`, в 1.7.10 — `null` |
| своя частица автора (`EntityFX`): `addEffect(частица)`; `particleRed` / `Green` / `Blue`, `particleMaxAge`, `particleGravity`, `prevPosX`, `interpPosX` | `Particle`: `mc.particleEngine.add(частица)`; `rCol` / `gCol` / `bCol`, `lifetime`, `gravity`, `xo`, `camera.position.x` | ✓ `renderParticle(Tessellator, …)` → `render(VertexConsumer, Camera, partialTicks)`, точки — от камеры. Свой проход рисования автора (очередь и GL после частиц) — свой `ParticleRenderType` частицы (`EntityVoxelFX.RENDER_TYPE`) |

## Миры

| Было | Стало | Примечание |
|---|---|---|
| `WorldProvider` + номер измерения | `dimension_type` + `dimension` в датапаке мода | высоты 0–255 (SPEC, Р-10) |
| `IChunkProvider` / `WE_ChunkProvider` | свой `ChunkGenerator` с кодеком | WorldEngine внутри |
| `BiomeGenBase` | биом в датапаке + свой `BiomeSource` | |
| `EntityRegistry.addSpawn` в чужие биомы | Forge biome modifier | веса — авторские |
| телепорт между мирами | `entity.changeDimension(level, ITeleporter)` | |
| `ASJUtilities.sendToDimensionWithoutPortal(entity, dim, x, y, z)` | то же, `dim` — `ResourceKey<Level>`: игрок — `ServerPlayer.teleportTo(level, …)`, прочие — `changeDimension` с телепортером в точку | ✓ игрок уходит и из Края без титров, как у автора; нет такого измерения — ничего не происходит (в 1.7.10 — падение) |

## Botania

| Было (r1.8-249) | Стало (1.20.1-456) | Примечание |
|---|---|---|
| `ModBlocks`, `ModFluffBlocks`, `ModItems` | `BotaniaBlocks`, `BotaniaItems` | ✓ вариант metadata — отдельный блок: `ModBlocks.livingrock`, 1 → `livingrockBrick`; `ModBlocks.livingwood`, `ModBlocks.dreamwood`: 0 (кора) → `livingwood`, `dreamwood`, 1 (доски) → `livingwoodPlanks`, `dreamwoodPlanks`; `ModFluffBlocks.elfQuartz` → `elfQuartz` |
| значения блока Botania 1.7.10 (материал, твёрдость, звук, имя, иконка), когда из него сделан блок автора | `alfheim.port.legacy.botania.Botania1710` | ✓ из кода Botania r1.8-249; текстура — Botania 1.20.1 |
| `vazkii.botania.common.block.decor.slabs.BlockModSlab`, `BlockLivingSlab`, `stairs.BlockModStairs`, `walls.BlockModWall`; `ItemBlockMod`, `ItemBlockModSlab` | те же имена в `alfheim.port.legacy.botania` поверх `Slab1710`, `Stairs1710`, `Wall1710` | ✓ в Botania 1.20.1 этих классов нет; регистрация и имена — как в r1.8-249, вкладка Botania не ставится (вкладку задаёт автор) |
| `LibResources.PREFIX_MOD` | `ResourcesLib.PREFIX_MOD` (`botania:`), в коде автора — `import … ResourcesLib as LibResources` | ✓ |
| `IManaItem`, `IManaReceiver` | `ManaItem`, `ManaReceiver` | у предметов — через capability |
| `SubTileGenerating` / `SubTileFunctional` | `GeneratingFlowerBlockEntity` / `FunctionalFlowerBlockEntity` | |
| `BotaniaAPI.register…Recipe` | рецепты в JSON (`botania:mana_infusion`, `botania:elven_trade` и др.) через datagen | |
| лексикон (`LexiconEntry`, страницы) | Patchouli, расширение `botania:lexicon` | SPEC, Р-8 |
| Baubles | Curios, как у аксессуаров Botania 1.20.1 | |
| частицы `Botania.proxy.wispFX(world, x, y, z, r, g, b, size, gravity)` / `sparkleFX(world, x, y, z, r, g, b, size, m)` | `alfheim.port.legacy.botania.Botania.proxy` → `world.addParticle(WispParticleData.wisp(size, r, g, b, 1) / SparkleParticleData.sparkle(size, r, g, b, m), …)` | ✓ «гравитация» огонька — скорость вниз; на сервере — ничего, как в 1.7.10 |
| `ItemKeepIvy.TAG_KEEP` (плющ верности) | `ResoluteIvyItem.TAG_KEEP` | ✓ тег тот же — `Botania_keepIvy` |
| `ManaItemHandler.requestManaExact(…)` и др. | `ManaItemHandler.instance().requestManaExact(…)` | ✓ |
| `vazkii.botania.common.core.helper.ItemNBTHelper` | `vazkii.botania.common.helper.ItemNBTHelper` | ✓ те же методы |
| `ToolCommons.raytraceFromEntity(world, player, fluids, dist)` | `ToolCommons.raytraceFromEntity(entity, dist, fluids)` | ✓ промах — `MISS`, а не `null` |
| структура секстанта: `MultiblockSextant` + `AnyComponent(pos, block, 0)`, `MultiblockRenderHandler.setMultiblock` / `anchor`; `Botania.proxy.removeSextantMultiblock()` | разреженная структура Patchouli (`PatchouliAPI.get().makeSparseMultiblock`, `predicateMatcher(block) { !it.isAir }`) с id `WorldshaperssSextantItem.MULTIBLOCK_ID`, показ — `Proxy.INSTANCE.showMultiblock(mb, название, точка, Rotation.NONE)`; `alfheim.port.legacy.botania.Botania.proxy.removeSextantMultiblock()` | ✓ как секстант Botania 1.20.1; с тем же id её убирает и секстант. Patchouli пишет над структурой название и долю готовых блоков |
| `ConfigHandler.boundBlockWireframe` и др. настройки клиента Botania | `BotaniaConfig.client().boundBlockWireframe()` | ✓ |
| `Botania.proxy.worldElapsedTicks` | `alfheim.port.legacy.botania.Botania.proxy.worldElapsedTicks` | ✓ на клиенте — тики игры Botania (`ClientTickHandler.ticksInGame`), на сервере — время основного мира |
| `ModItems.manaResource` 9, `ModItems.quartz` 5 | `BotaniaItems.dragonstone`, `BotaniaItems.elfQuartz` | ✓ |
| `ModItems.manaResource` 1, 5, 7, 14 | `BotaniaItems.manaPearl`, `lifeEssence`, `elementium`, `gaiaIngot` | ✓ |
| `ModItems.ancientWill` 0–5 | `alfheim.port.legacy.botania.ancientWill[i]`: `BotaniaItems.ancientWillAhrim`, `…Dharok`, `…Guthan`, `…Torag`, `…Verac`, `…Karil` | ✓ массив по номеру 1.7.10: `ItemStack(ancientWill, 1, i)` → `ItemStack(ancientWill[i])` |
| `ModBlocks.platform` 1, `ModBlocks.dreamwood` 3, 4 | `BotaniaBlocks.spectralPlatform`, `dreamwoodFramed`, `dreamwoodPatternFramed` | ✓ |
| ингредиент рецепта `ModBlocks.livingwood`, `ModBlocks.dreamwood` 0 (`ItemStack(livingwood)`) | `LIVING_WOOD`, `DREAM_WOOD` → теги `botania:livingwood_logs`, `botania:dreamwood_logs` | ✓ как в рецептах Botania 1.20.1: живое дерево чистой маргаритки в 1.20.1 — бревно, блок коры делается из 4 брёвен |
| `ModBlocks.livingrock` 4 (резные кирпичи) | `BotaniaBlocks.livingrockBrickChiseled` | ✓ |
| `ModFluffBlocks.livingrockStairs`, `livingrockBrickStairs`, `livingrockSlab`, `livingrockBrickSlab`, `livingrockWall` | `BotaniaBlocks` — те же имена | ✓ |
| `ModBlocks.mushroom` с metadata цвета | грибы Botania 1.20.1 по цвету: `BotaniaBlocks.whiteMushroom` … (`getMushroom(DyeColor)`) | ✓ |
| `ModFluffBlocks.elfQuartz` и др. кварцевые блоки | `BotaniaBlocks.elfQuartz` и др. | ✓ так же в Botania 1.20.1 называются предметы кварца — в коде автора явный импорт блока |
| `ModBlocks.customBrick` 3 (черепица) | — | в Botania 1.20.1 нет: рецепты черепицы автора ждут решения владельца (TASKS.md) |
| `ModItems.brewVial` (`ItemBrewVial`), `BotaniaAPI.fallbackBrew` | `BotaniaItems.brewVial` (`BaseBrewItem`), `BotaniaBrews.fallbackBrew` | ✓ |
| `ModItems.petal` с metadata цвета | `BotaniaItems.getPetal(DyeColor)` | ✓ |
| `ModItems.dye` (цветочная пыль) | краситель ванилы (`DyeItem.byColor`) | ✓ в Botania 1.20.1 цветочной пыли нет: лепестки дают краситель ванилы |
| `ModItems.vial` 1 (колба из альвийского стекла) | `BotaniaItems.flask` | ✓ |
| `PotionMod` (зелье Botania 1.7.10) | `alfheim.port.legacy.botania.PotionMod` поверх `Potion1710` | ✓ имя `botania.potion.<имя>`, как в r1.8-249 |
| `Botania.proxy.lightningFX(world, Vector3, Vector3, …)` | `alfheim.port.legacy.botania.Botania.proxy.lightningFX` → `Proxy.INSTANCE.lightningFX(level, Vec3, Vec3, …)` | ✓ |
| `IBrewItem`, `IBrewContainer`; `brew.getUnlocalizedName(stack)` | `BrewItem`, `BrewContainer`; `getTranslationKey(stack)` | ✓ |
| `BotaniaAPI.brewMap`, `getBrewFromKey(ключ)`, `fallbackBrew`, `brew.key`; `ModBrews` | реестр варев `BotaniaAPI.instance().brewRegistry!!` (перебор, `get(ResourceLocation)`, `getKey(brew)`), `BotaniaBrews.fallbackBrew`; `BotaniaBrews` | ✓ ключ варева в NBT вещи (`brewKey`) — id в реестре (`botania:healing`), в 1.7.10 — имя (`healing`). Запасное варево в 1.20.1 — в реестре: перебор варев его пропускает, как перебор `brewMap` 1.7.10 |
| `ModPotions` (`vazkii.botania.common.brew`) | `BotaniaMobEffects` | ✓ |
| иконки склянки Botania `vial0`, `vial1_0` | текстуры `botania:item/vial`, `botania:item/brew_vial_0` | ✓ |

## Удалённые опции конфига

Опции, потерявшие смысл в 1.20.1 (SPEC, п. 4). Строка `loadProp` в коде автора
закомментирована с пометкой `// PORT:`, поле остаётся со значением автора по
умолчанию: код, который его читает, переносится в своей КТ. Таблицу читает
`tools/check_config.py`; `*` в имени — любые символы.

| Опция автора | Почему удалена |
|---|---|
| `dimensionIDAlfheim`, `dimensionIDNiflheim`, `dimensionIDDomains`, `dimensionIDHelheim` | номера измерений: в 1.20.1 измерения задаются именем в датапаке мода (SPEC, Р-10) |
| `niflheimBiomeIDs` | номера биомов: в 1.20.1 биомы — данные с именами |
| `potionID*` (43 опции) | номера зелий: в 1.20.1 эффекты регистрируются по имени, конфликтов номеров нет; зелья автора сохраняют номера по умолчанию (`Potion1710`) |
| `flagIdSheepRainbow`, `floatingIslandSyncedDataInitLimit`, `oiiaId` | номера в DataWatcher; в 1.20.1 номера SynchedEntityData выдаёт игра |
| `TC.botaniaAspects`, `TC.tinctura`, `TC.overrideFMCaps`, `TC.treeCrafting` | интеграция с Thaumcraft и Forbidden Magic выпала (SPEC, п. 7) |
| `elementiumClusterMeta` (`core.cfg`) | metadata кластера Thaumcraft; интеграция выпала (SPEC, п. 7) |
| `overrideCoFHCollisionCheck` (`core.cfg`) | отключала врезку в CoFHCore 1.7.10; в 1.20.1 этой врезки нет |

## Файлы конфига

| Было | Стало | Примечание |
|---|---|---|
| `config/Alfheim/core.cfg` (`AlfheimPreConfigHandler`, читал coremod) | `config/Alfheim/core.toml`, читается первым в конструкторе мода | ✓ coremod в 1.20.1 нет |
| `config/Alfheim/mod.cfg` (`AlfheimConfigHandler`) | `config/Alfheim/mod.toml` | ✓ |
| — | `config/Alfheim/client.toml`: интерфейс и графика, только на клиенте | ✓ SPEC, Р-12; список — `alfheim.port.config.ClientOptions` |
| `Configuration` / `PreConfiguration` в `ASJConfigHandler` / `ASJPreConfigHandler` | прослойка `alfheim.port.config.Configuration` поверх `ForgeConfigSpec` | ✓ код автора не меняется; два прохода `readProperties`: объявление опций, затем чтение |
| категория `a.b` + опция `c` | таблица TOML `[a.b]`, ключ `c` | ✓ точка в имени опции — тоже уровень: `wire.overpowered` → `[general.wire] overpowered` |
| `setRequiresMcRestart(true)` | `worldRestart()` | ✓ в 1.20.1 у общих конфигов нет пометки «нужен перезапуск игры» |
| `OnConfigChangedEvent` (экран настроек) | `ModConfigEvent.Reloading` | ✓ Forge шлёт его, когда файл изменили во время игры |
| значение не того типа | значение по умолчанию | ✓ как в 1.7.10; границы чисел проверяет `ASJConfigHandler` автора |
