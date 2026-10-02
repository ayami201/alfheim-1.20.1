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
| `lang` | старый ключ перевода | новый ключ (`block.alfheim.<id>`, `item.alfheim.<id>`); применяет `tools/convert_lang.py` |

Старый ключ предмета-блока: `tile.` + имя из `setBlockName`; `ItemBlockMetaName` дописывал номер варианта,
`ItemBlockLeavesMod` — приставку `alfheim:` (`tile.alfheim:ElvenSand.name`). В 1.20.1 у предмета-блока ключ блока,
поэтому старый ключ предмета переименовывается в ключ блока.

Пример: `"blocks": {"alfheim:altWood1": {"0": "alfheim:alt_wood1_…", "*": "…"}}`.

## Переводы

| Было | Стало | Примечание |
|---|---|---|
| `assets/<ns>/lang/en_US.lang` | `assets/<ns>/lang/en_us.json` | ✓ собирает `tools/convert_lang.py` из `.lang` в `legacy/`; руками `.json` не правится |
| значения с `%d`, `%.1f` | как есть | ✓ и 1.7.10, и 1.20.1 при загрузке заменяют их на `%s` |
| ключи удалённых вещей | не переносятся | ✓ список с причинами — `REMOVED` в `tools/convert_lang.py` |
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
| `breakBlock` | `onRemove` | |
| `getDrops` | таблица лута (datagen) | |
| `TileEntity` | `BlockEntity` + `BlockEntityType` | |
| `updateEntity` | `BlockEntityTicker` | |
| `readFromNBT` / `writeToNBT` | `load` / `saveAdditional` | |
| `getDescriptionPacket` / `onDataPacket` | `getUpdatePacket` / `getUpdateTag` / `onDataPacket` | |
| `AxisAlignedBB`, `MathHelper`, `MovingObjectPosition` | `AABB`, `Mth`, `HitResult` | |

## Блоки и предметы

Блоки автора наследуют базовые классы порта `alfheim.port.legacy.Block1710` (`Block` 1.7.10) и
`BlockFalling1710` (`BlockFalling`). Они принимают материал 1.7.10, а сеттеры 1.7.10 работают после создания
блока, как в 1.7.10: `init {}` блоков и цепочки сеттеров автора не меняются. Проверяет GameTest `PortBlocksTest`.

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

## Ore Dictionary

`regOreDict` автора остаётся: `registerOre` прослойки записывает пары «имя — вещь», генерация данных
(`alfheim.port.data.OreDictTags`) превращает их в теги — один и тот же для предмета и для блока: в 1.7.10 по
Ore Dictionary искали и вещи, и блоки. Имя без строки в таблице роняет генерацию данных.

| Имя 1.7.10 | Тег 1.20.1 | Примечание |
|---|---|---|
| `sand` | `forge:sand` | ✓ |

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

## Предметы, сущности, эффекты

| Было | Стало | Примечание |
|---|---|---|
| `onItemRightClick` / `onItemUse` / `onUpdate` / `addInformation` | `use` / `useOn` / `inventoryTick` / `appendHoverText` | |
| `stack.stackTagCompound`, `NBTTagCompound` | `stack.getTag()` / `getOrCreateTag()`, `CompoundTag` | `ItemNBTHelper` автора сохраняется поверх |
| `EntityPlayer`, `EntityLivingBase` | `Player`, `LivingEntity` | |
| `isSneaking`, `heldItem`, `riddenByEntity` | `isShiftKeyDown`, `mainHandItem` (пустой стек вместо `null`), `firstPassenger` | ✓ |
| `mountEntity(entity)` | `startRiding(entity, true)` | ✓ 1.7.10 сажал без проверок, кроме кольца из всадников; `force = true` — так же |
| `EntityTameable.isTamed`, `func_152115_b(uuidString)` | `isTame`, `setOwnerUUID(uuid)` | ✓ |
| `Items.stick`, `Blocks.grass` и др. | `Items.STICK`, `Blocks.GRASS_BLOCK` и др. | поля ванилы в 1.20.1 — заглавными; имя проверять по смыслу |
| `World` в сигнатурах автора | `import net.minecraft.world.level.Level as World` | ✓ |
| `entityInit` + `DataWatcher` | `defineSynchedData` + `SynchedEntityData` | |
| `readEntityFromNBT` / `writeEntityToNBT` | `readAdditionalSaveData` / `addAdditionalSaveData` | |
| `onUpdate` (сущность) | `tick` | |
| `applyEntityAttributes` | атрибуты в `EntityAttributeCreationEvent` | |
| `EntityRegistry.registerModEntity` + яйцо | `EntityType` + `ForgeSpawnEggItem` | цвета яиц — авторские |
| `IExtendedEntityProperties` | Capability Forge | SPEC, Р-11 |
| `WorldSavedData` | `SavedData` | |
| `Potion` / `PotionEffect` | `MobEffect` / `MobEffectInstance` | номера зелий из конфига удаляются |
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
| `SimpleNetworkWrapper`, `IMessage` | `SimpleChannel`; `ASJPacket.toBytes` / `fromBytes` / `create`, обработчик `AlfheimPacket.onMessage` | ✓ поля пишет `ASJPacket`, как coremod `ASJPacketCompleter`: свои поля класса пакета, не static и не final, в порядке объявления, после `toCustomBytes`. Обработка — в основном потоке (`enqueueWork`) |
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
| объект без методов `@SubscribeEvent` на шине | как есть | ✓ шина Forge 1.20.1 принимает его молча — общий обработчик подписывается, даже если его методы ещё ждут свою КТ |

## Рендер

| Было | Стало | Примечание |
|---|---|---|
| `Tessellator`, `GL11` | `PoseStack`, `MultiBufferSource`, `VertexConsumer`, `RenderType`, `RenderSystem` | |
| `ISimpleBlockRenderingHandler` | JSON-модель, BakedModel или BlockEntityRenderer | SPEC, Р-13 |
| `IItemRenderer` | `BlockEntityWithoutLevelRenderer` через `IClientItemExtensions` | |
| `ModelBase` / `ModelRenderer` | `EntityModel` / `ModelPart` + `LayerDefinition` | размеры, опоры, UV — авторские |
| `RenderingRegistry.registerEntityRenderingHandler` | `EntityRenderersEvent.RegisterRenderers` | |
| `bindTileEntitySpecialRenderer` | `EntityRenderersEvent.RegisterRenderers` (`registerBlockEntityRenderer`) | |
| `ASJShaderHelper` + шейдеры автора | core shaders через `RegisterShadersEvent` + свой `RenderType` | |
| `setGlow` (asjlib) | полная яркость, `LightTexture.FULL_BRIGHT` | |
| `mc.gameSettings.particleSetting` | `mc.options.particles().get().id` | ✓ номера те же: 0 — все, 1 — меньше, 2 — минимум |
| `mc.effectRenderer.addEffect(частица)` | `mc.particleEngine.createParticle(данные, x, y, z, vx, vy, vz)` | без ограничителя частиц ванилы, как `addEffect`; `level.addParticle` ограничивает ещё раз |

## Миры

| Было | Стало | Примечание |
|---|---|---|
| `WorldProvider` + номер измерения | `dimension_type` + `dimension` в датапаке мода | высоты 0–255 (SPEC, Р-10) |
| `IChunkProvider` / `WE_ChunkProvider` | свой `ChunkGenerator` с кодеком | WorldEngine внутри |
| `BiomeGenBase` | биом в датапаке + свой `BiomeSource` | |
| `EntityRegistry.addSpawn` в чужие биомы | Forge biome modifier | веса — авторские |
| телепорт между мирами | `entity.changeDimension(level, ITeleporter)` | |

## Botania

| Было (r1.8-249) | Стало (1.20.1-456) | Примечание |
|---|---|---|
| `ModBlocks`, `ModItems` | `BotaniaBlocks`, `BotaniaItems` | |
| `IManaItem`, `IManaReceiver` | `ManaItem`, `ManaReceiver` | у предметов — через capability |
| `SubTileGenerating` / `SubTileFunctional` | `GeneratingFlowerBlockEntity` / `FunctionalFlowerBlockEntity` | |
| `BotaniaAPI.register…Recipe` | рецепты в JSON (`botania:mana_infusion`, `botania:elven_trade` и др.) через datagen | |
| лексикон (`LexiconEntry`, страницы) | Patchouli, расширение `botania:lexicon` | SPEC, Р-8 |
| Baubles | Curios, как у аксессуаров Botania 1.20.1 | |
| частицы `Botania.proxy.wispFX` / `sparkleFX` | `WispParticleData` / `SparkleParticleData` | |

## Удалённые опции конфига

Опции, потерявшие смысл в 1.20.1 (SPEC, п. 4). Строка `loadProp` в коде автора
закомментирована с пометкой `// PORT:`, поле остаётся со значением автора по
умолчанию: код, который его читает, переносится в своей КТ. Таблицу читает
`tools/check_config.py`; `*` в имени — любые символы.

| Опция автора | Почему удалена |
|---|---|
| `dimensionIDAlfheim`, `dimensionIDNiflheim`, `dimensionIDDomains`, `dimensionIDHelheim` | номера измерений: в 1.20.1 измерения задаются именем в датапаке мода (SPEC, Р-10) |
| `niflheimBiomeIDs` | номера биомов: в 1.20.1 биомы — данные с именами |
| `potionID*` (43 опции) | номера зелий: в 1.20.1 эффекты регистрируются по имени, номер выдаёт реестр, конфликтов номеров нет |
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
