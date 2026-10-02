# Соответствия 1.7.10 → 1.20.1

Зачем этот файл: одинаковый код автора должен переноситься одинаково в любой
сессии. Соответствие, которое понадобилось больше одного раза, записывается
сюда.

Имена 1.20.1 — официальные (Mojang), как в MDK. Строки без ✓ — предварительные:
при первом использовании их сверяют с исходниками 1.20.1, Forge 47.4.23 или
Botania 456 и ставят ✓. Если оказалось иначе — строку исправляют.

## Имена и metadata

| Было | Стало | Примечание |
|---|---|---|
| `alfheim:DomainDoor` | `alfheim:domain_door` | snake_case от имени автора (SPEC, Р-5) |
| metadata = разные вещи | отдельные блоки/предметы `<имя>_<вариант>` | имена вариантов — из кода и lang автора |
| metadata = состояние | свойство BlockState | поворот, рост, «включён» |
| любое старое имя + meta | запись в `src/main/resources/alfheim/legacy_ids.json` | один источник для построек, lang, лексикона, тестов |

## Мир, блоки, блок-сущности

| Было | Стало | Примечание |
|---|---|---|
| `World` | `Level` | |
| `world.isRemote` | `level.isClientSide` | |
| `world.getBlock(x, y, z)`, `setBlock`, `getTileEntity`, `isAirBlock` | прослойка `alfheim.port.legacy` | только где смысл тот же (SPEC, Р-4) |
| `world.getBlockMetadata(x, y, z)` | свойство BlockState | без прослойки: смысл у каждого блока свой |
| `Block.registerBlockIcons` / `getIcon` / `IIcon` | модели и состояния блоков через datagen | метод удаляется |
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

## Предметы, сущности, эффекты

| Было | Стало | Примечание |
|---|---|---|
| `onItemRightClick` / `onItemUse` / `onUpdate` / `addInformation` | `use` / `useOn` / `inventoryTick` / `appendHoverText` | |
| `stack.stackTagCompound`, `NBTTagCompound` | `stack.getTag()` / `getOrCreateTag()`, `CompoundTag` | `ItemNBTHelper` автора сохраняется поверх |
| `EntityPlayer`, `EntityLivingBase` | `Player`, `LivingEntity` | |
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
| `@Mod(modid, dependencies, useMetadata, guiFactory, modLanguageAdapter)` | `@Mod(MODID)` на `object` + `META-INF/mods.toml` | ✓ зависимости и описание — в `mods.toml`, язык — `modLoader="kotlinforforge"`; экран настроек (`guiFactory`) — КТ-1 |
| `@EventHandler` + `FMLConstructionEvent`, `FMLPreInitializationEvent` | вызов из `init {}` объекта мода | ✓ регистрация (DeferredRegister) и конфиг в 1.20.1 возможны только в конструкторе мода |
| `@EventHandler` + `FMLInitializationEvent` | `FMLCommonSetupEvent` на шине мода | ✓ идёт параллельно с другими модами: непотокобезопасное — в `event.enqueueWork {}` |
| `@EventHandler` + `FMLPostInitializationEvent` | `InterModProcessEvent` на шине мода | ✓ последнее событие загрузки, когда все моды прошли setup |
| `@EventHandler` + `FMLLoadCompleteEvent` | `FMLLoadCompleteEvent` на шине мода | |
| `@EventHandler` + `FMLServerStartingEvent` | `ServerStartingEvent` на шине Forge | ✓ |
| `event.registerServerCommand(...)` | `RegisterCommandsEvent` на шине Forge | команды регистрируются до `ServerStartingEvent` |
| подписка методов `@EventHandler` | `MOD_BUS` / `FORGE_BUS` из `thedarkcolour.kotlinforforge.forge`, `addListener(EventPriority.NORMAL, false, Event::class.java, ::метод)` | ✓ форма с явным классом события; имена методов автора сохраняются |
| `@KotlinProxy` (ASJ) | `DistExecutor` | классы прокси автора сохраняются (SPEC, Р-11) |
| `@Metadata ModMetadata` | `ModList.get().getModContainerById(MODID).get().modInfo` (`IModInfo`) | версия — `ArtifactVersion`, имя — `displayName` |
| `Loader.isModLoaded("Mod")` | `ModList.get().isLoaded("mod")` | ✓ id модов в 1.20.1 — строчными |
| `MinecraftServer.getServer()` | `ServerLifecycleHooks.getCurrentServer()` | ✓ |
| `saveHandler.worldDirectory` | `server.getWorldPath(LevelResource.ROOT).toAbsolutePath().normalize()` | ✓ `ROOT` даёт путь с «.» на конце |

## События, сеть, конфиг, команды

| Было | Стало | Примечание |
|---|---|---|
| `eventForge()` / `eventFML()` (asjlib) | `MinecraftForge.EVENT_BUS.register(...)` | события жизненного цикла мода — на шине мода |
| `cpw.mods.fml…SubscribeEvent` | `net.minecraftforge.eventbus.api.SubscribeEvent` | |
| `SimpleNetworkWrapper`, `IMessage` | `SimpleChannel`, encoder / decoder / handler | порядок полей — как у автора |
| `Configuration` | `ForgeConfigSpec` | SPEC, Р-12 |
| `CommandBase` | Brigadier, `RegisterCommandsEvent` | имена и аргументы — как у автора |
| `ClientRegistry.registerKeyBinding` | `RegisterKeyMappingsEvent` | |
| Ore Dictionary | теги | |
| достижения (`Achievement`) | advancements через datagen | |
| `StatCollector.translateToLocal` | `I18n.get` (клиент) / `Component.translatable` | |
| `ChatComponentText`, `EnumChatFormatting` | `Component.literal`, `ChatFormatting` | |

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

Опции, потерявшие смысл в 1.20.1 (SPEC, п. 4). Заполняется в КТ-1.

| Опция автора | Почему удалена |
|---|---|
