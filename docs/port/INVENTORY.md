# Опись исходников автора

Каждый файл кода автора и каждая папка ресурсов → контрольная точка (КТ) и статус.
Опись составлена в КТ-0 от точки отсчёта `fd34c141` (релиз 67). Дальше её правит
каждая сессия: перенесла файл — поставила «перенесено» и путь в `src/`; файл не
подошёл к своей КТ — перенесла в другую и написала почему в примечании.

**Статусы:** `ждёт` — ещё в `legacy/`; `перенесено` — лежит в `src/`;
`выпало (причина)` — не переносится; `WIP — стадия 2` — незаконченное автором,
остаётся в `legacy/` до стадии 2 (SPEC, п. 6).

**КТ** — из [ROADMAP.md](ROADMAP.md). `—` — у файла нет КТ (выпал или WIP).
`по HOOKS.md` — файл с врезками в разные механики: каждая врезка переносится в КТ
своей механики, учёт — в [HOOKS.md](HOOKS.md).

Распределение по КТ предварительное: точно оно выясняется при переносе. Блок и
его блок-сущность, рендер и модель идут в одну КТ — в ту, где их механика.
Рецепты, переводы и тексты лексикона переносятся вместе с вещью, к которой относятся.

## Сверка

```bash
python3 tools/check_inventory.py
```

Скрипт берёт список файлов `*.kt` и `*.java` из `src/main/java` и `src/api` в коммите
`fd34c141` и проверяет, что в описи ровно по одной строке на каждый (1 153), что
статусы допустимы и что «перенесено» стоит ровно у тех файлов, которых больше нет в
`legacy/`. Без истории git (мелкий клон) он сверяет с деревом `legacy/` + перенесённые.
Запускается в CI.

## Сводка

| КТ | Файлов | Строк | ждёт | перенесено | выпало | WIP — стадия 2 |
|---|---:|---:|---:|---:|---:|---:|
| КТ-0 | 2 | 141 |  | 2 |  |  |
| КТ-1 | 30 | 5 631 |  | 30 |  |  |
| КТ-2 | 222 | 19 082 | 212 | 10 |  |  |
| КТ-3 | 215 | 21 193 | 215 |  |  |  |
| КТ-4 | 177 | 24 578 | 177 |  |  |  |
| КТ-5 | 43 | 4 930 | 43 |  |  |  |
| КТ-6 | 107 | 12 508 | 107 |  |  |  |
| КТ-7 | 139 | 10 888 | 139 |  |  |  |
| КТ-8 | 95 | 13 066 | 95 |  |  |  |
| КТ-9 | 19 | 2 559 | 19 |  |  |  |
| КТ-10 | 24 | 1 775 | 24 |  |  |  |
| по HOOKS.md | 8 | 3 773 | 8 |  |  |  |
| — | 72 | 4 519 |  |  | 60 | 12 |
| **всего** | **1153** | **124 643** | **1049** | **32** | **60** | **12** |

«Строк» — строки исходников автора вместе с пустыми и комментариями.

## Код

### `legacy/src/api/java/Reika/ChromatiCraft/Registry/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `ExtraChromaIDs.java` | 8 | — | выпало (часть ModdedDimensionsIntegration про мод не из сборки, SPEC п. 7) | заглушка API |

### `legacy/src/api/java/appeng/spatial/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `StorageWorldProvider.java` | 7 | — | выпало (часть ModdedDimensionsIntegration про мод не из сборки, SPEC п. 7) | заглушка API |

### `legacy/src/api/java/cofh/api/energy/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `IEnergyContainerItem.java` | 46 | — | выпало (Thermal Foundation: мод отсутствует на 1.20.1) | заглушка API CoFH |

### `legacy/src/api/java/cofh/asmhooks/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `HooksCore.java` | 3 | — | выпало (Thermal Foundation: мод отсутствует на 1.20.1) | заглушка API CoFH |

### `legacy/src/api/java/cofh/core/item/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `IEqualityOverrideItem.java` | 8 | — | выпало (Thermal Foundation: мод отсутствует на 1.20.1) | заглушка API CoFH |

### `legacy/src/api/java/cofh/core/render/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `IconRegistry.java` | 7 | — | выпало (Thermal Foundation: мод отсутствует на 1.20.1) | заглушка API CoFH |

### `legacy/src/api/java/cofh/thermalfoundation/fluid/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `TFFluids.java` | 3 | — | выпало (Thermal Foundation: мод отсутствует на 1.20.1) | заглушка API CoFH |

### `legacy/src/api/java/com/emoniph/witchery/util/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `Config.java` | 8 | — | выпало (часть ModdedDimensionsIntegration про мод не из сборки, SPEC п. 7) | заглушка API |

### `legacy/src/api/java/com/gildedgames/the_aether/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `AetherConfig.java` | 10 | КТ-10 | ждёт | заглушка; в 1.20.1 — настоящий API Aether |

### `legacy/src/api/java/com/meteor/extrabotany/api/hugetools/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `HugeItemRenderer.java` | 3 | КТ-10 | ждёт | заглушка API ExtraBotany 1.7.10; врезки правили рендер её предметов. В 1.20.1 — проверка с ExtraBotany: Reburn (SPEC п. 7) |

### `legacy/src/api/java/com/meteor/extrabotany/client/render/item/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `GunRenderer.java` | 3 | КТ-10 | ждёт | заглушка API ExtraBotany 1.7.10; врезки правили рендер её предметов. В 1.20.1 — проверка с ExtraBotany: Reburn (SPEC п. 7) |

### `legacy/src/api/java/com/rwtema/extrautils/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `ExtraUtils.java` | 5 | — | выпало (часть ModdedDimensionsIntegration про мод не из сборки, SPEC п. 7) | заглушка API |

### `legacy/src/api/java/com/teammetallurgy/atum/handler/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `AtumConfig.java` | 5 | — | выпало (часть ModdedDimensionsIntegration про мод не из сборки, SPEC п. 7) | заглушка API |

### `legacy/src/api/java/com/yurtmod/main/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `Config.java` | 5 | — | выпало (часть ModdedDimensionsIntegration про мод не из сборки, SPEC п. 7) | заглушка API |

### `legacy/src/api/java/ec3/utils/cfg/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `Config.java` | 5 | — | выпало (часть ModdedDimensionsIntegration про мод не из сборки, SPEC п. 7) | заглушка API |

### `legacy/src/api/java/erebus/core/handler/configs/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `ConfigHandler.java` | 6 | — | выпало (часть ModdedDimensionsIntegration про мод не из сборки, SPEC п. 7) | заглушка API |

### `legacy/src/api/java/ic2/core/crop/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `TileEntityCrop.java` | 10 | — | выпало (IC2: мод отсутствует на 1.20.1, SPEC п. 7) | заглушка API; грядки IC2 для `ItemTerraHoe` |

### `legacy/src/api/java/lumien/randomthings/Configuration/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `Settings.java` | 5 | — | выпало (часть ModdedDimensionsIntegration про мод не из сборки, SPEC п. 7) | заглушка API |

### `legacy/src/api/java/net/gtn/dimensionalpocket/common/lib/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `Reference.java` | 5 | — | выпало (часть ModdedDimensionsIntegration про мод не из сборки, SPEC п. 7) | заглушка API |

### `legacy/src/api/java/org/dave/CompactMachines/handler/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `ConfigurationHandler.java` | 5 | — | выпало (часть ModdedDimensionsIntegration про мод не из сборки, SPEC п. 7) | заглушка API |

### `legacy/src/api/java/org/dimdev/dimdoors/config/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `DDProperties.java` | 9 | — | выпало (Dimensional Doors: нет в сборке, SPEC п. 7) | заглушка API; Лимб для `ItemTankMask` |

### `legacy/src/api/java/org/dimdev/dimdoors/core/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `DDTeleporter.java` | 11 | — | выпало (Dimensional Doors: нет в сборке, SPEC п. 7) | заглушка API; Лимб для `ItemTankMask` |

### `legacy/src/api/java/org/dimdev/dimdoors/util/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `Point4D.java` | 5 | — | выпало (Dimensional Doors: нет в сборке, SPEC п. 7) | заглушка API; Лимб для `ItemTankMask` |

### `legacy/src/api/java/org/dimdev/dimdoors/world/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `LimboProvider.java` | 12 | — | выпало (Dimensional Doors: нет в сборке, SPEC п. 7) | заглушка API; Лимб для `ItemTankMask` |

### `legacy/src/api/java/thebetweenlands/utils/confighandler/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `ConfigHandler.java` | 5 | — | выпало (часть ModdedDimensionsIntegration про мод не из сборки, SPEC п. 7) | заглушка API |

### `legacy/src/api/java/twilightforest/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `TwilightForestMod.java` | 5 | КТ-10 | ждёт | заглушка; в 1.20.1 — настоящий API Twilight Forest |

### `legacy/src/main/java/alfheim/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `AlfheimCore.kt` | 135 | КТ-0 | перенесено | → `src/main/java/alfheim/AlfheimCore.kt` |
| `ToDoList.kt` | 29 | — | WIP — стадия 2 | список дел автора, весь закомментирован |

### `legacy/src/main/java/alfheim/api/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `AlfheimAPI.kt` | 377 | КТ-1 | перенесено | → `src/main/java/alfheim/api/AlfheimAPI.kt`; работают редкости, «розовость», топливо, веса руд; остальное закомментировано до КТ своих механик |
| `ModInfo.kt` | 6 | КТ-0 | перенесено | → `src/main/java/alfheim/api/ModInfo.kt` |
| `package-info.java` | 4 | — | выпало (аннотация `@API` FML 1.7.10, в 1.20.1 аналога нет) |  |

### `legacy/src/main/java/alfheim/api/block/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `IHourglassTrigger.kt` | 14 | КТ-3 | ждёт |  |

### `legacy/src/main/java/alfheim/api/block/tile/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `SubTileAnomalyBase.kt` | 132 | КТ-3 | ждёт |  |

### `legacy/src/main/java/alfheim/api/boss/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `IBossWithName.kt` | 16 | КТ-8 | ждёт |  |

### `legacy/src/main/java/alfheim/api/crafting/recipe/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `RecipeBarrel.kt` | 19 | КТ-3 | ждёт |  |
| `RecipeManaInfuser.kt` | 20 | КТ-3 | ждёт |  |
| `RecipeTreeCrafting.kt` | 56 | КТ-3 | ждёт |  |
| `TunerIncantation.kt` | 74 | КТ-3 | ждёт |  |

### `legacy/src/main/java/alfheim/api/entity/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `EnumRace.kt` | 83 | КТ-7 | ждёт |  |
| `IAlfheimMob.kt` | 9 | КТ-5 | ждёт |  |
| `Interfaces.kt` | 51 | КТ-5 | ждёт |  |

### `legacy/src/main/java/alfheim/api/event/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `AlfheimModeChangedEvent.kt` | 5 | КТ-7 | ждёт |  |
| `AttackEntityEventPost.kt` | 8 | КТ-1 | перенесено | → `src/main/java/alfheim/api/event/AttackEntityEventPost.kt` |
| `PlayerChangedRaceEvent.kt` | 7 | КТ-7 | ждёт |  |
| `PlayerInteractAdequateEvent.kt` | 32 | КТ-1 | перенесено | → `src/main/java/alfheim/api/event/PlayerInteractAdequateEvent.kt` |
| `SpellCastEvent.kt` | 13 | КТ-7 | ждёт |  |
| `TimeStopCheckEvent.kt` | 18 | КТ-7 | ждёт |  |

### `legacy/src/main/java/alfheim/api/item/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `ColorOverrideHelper.kt` | 51 | КТ-4 | ждёт |  |
| `IDoubleBoundItem.kt` | 221 | КТ-4 | ждёт |  |
| `Interfaces.kt` | 99 | КТ-4 | ждёт |  |
| `IPriestColorOverride.kt` | 8 | КТ-4 | ждёт |  |
| `ThrowableCollidingItem.kt` | 12 | КТ-2 | ждёт |  |

### `legacy/src/main/java/alfheim/api/item/equipment/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `IElementalItem.kt` | 23 | КТ-4 | ждёт |  |

### `legacy/src/main/java/alfheim/api/item/equipment/bauble/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `IManaDiscountBauble.kt` | 20 | КТ-4 | ждёт |  |

### `legacy/src/main/java/alfheim/api/lib/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `LibOreDict.kt` | 71 | КТ-2 | ждёт |  |
| `LibRenderIDs.kt` | 33 | КТ-2 | ждёт |  |
| `LibResourceLocations.kt` | 318 | КТ-1 | перенесено | → `src/main/java/alfheim/api/lib/LibResourceLocations.kt`; пути через `legacyPath`; анимированные текстуры (`ResourceLocationAnimated`) — в КТ своих моделей |
| `LibShaderIDs.kt` | 14 | КТ-2 | ждёт |  |

### `legacy/src/main/java/alfheim/api/network/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `AlfheimPacket.kt` | 19 | КТ-1 | перенесено | → `src/main/java/alfheim/api/network/AlfheimPacket.kt` |

### `legacy/src/main/java/alfheim/api/spell/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `ITimeStopSpecific.kt` | 22 | КТ-7 | ждёт |  |
| `SpellBase.kt` | 111 | КТ-7 | ждёт |  |

### `legacy/src/main/java/alfheim/api/trees/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `IIridescentSaplingVariant.kt` | 15 | КТ-2 | ждёт |  |
| `IridescentSaplingBaseVariant.kt` | 44 | КТ-2 | ждёт |  |

### `legacy/src/main/java/alfheim/api/world/domain/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `Domain.kt` | 51 | КТ-6 | ждёт |  |

### `legacy/src/main/java/alfheim/client/core/handler/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `CardinalSystemClient.kt` | 153 | КТ-7 | ждёт |  |
| `EventHandlerClient.kt` | 325 | КТ-1 | перенесено | → `src/main/java/alfheim/client/core/handler/EventHandlerClient.kt`; общий обработчик: подписан на шину, его методы раскомментирует КТ своей механики |
| `HUDCorporeaRat.kt` | 47 | КТ-3 | ждёт |  |
| `KeyBindingHandlerClient.kt` | 353 | КТ-7 | ждёт |  |

### `legacy/src/main/java/alfheim/client/core/helper/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `IconHelper.kt` | 34 | КТ-2 | ждёт | иконки 1.7.10 → модели; вероятно, выпадет при переносе блоков |
| `InterpolatedIconHelper.kt` | 40 | КТ-2 | ждёт | иконки 1.7.10 → модели; вероятно, выпадет при переносе блоков |

### `legacy/src/main/java/alfheim/client/core/proxy/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `ClientProxy.kt` | 447 | КТ-1 | перенесено | → `src/main/java/alfheim/client/core/proxy/ClientProxy.kt`; регистрация рендера — в КТ своих вещей, режимы и клавиши — КТ-7 |

### `legacy/src/main/java/alfheim/client/core/util/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `AlfheimBotaniaModifiersClient.kt` | 12 | КТ-3 | ждёт |  |

### `legacy/src/main/java/alfheim/client/gui/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `GUIBanner.kt` | 66 | КТ-8 | ждёт | сезонные события |
| `GUIConfig.kt` | 9 | — | выпало (экран настроек: в Forge 1.20.1 встроенного нет, настройки в игре показывает мод Configured) | решение владельца: настройки — в файлах `config/Alfheim/*.toml`, в игре — через Configured, если он есть в сборке |
| `GUIDeathTimer.kt` | 77 | КТ-7 | ждёт |  |
| `GUIEditGaiaButton.kt` | 43 | КТ-3 | ждёт |  |
| `GUIFactory.kt` | 12 | — | выпало (экран настроек: в Forge 1.20.1 встроенного нет, настройки в игре показывает мод Configured) | см. `GUIConfig.kt` |
| `GUIParty.kt` | 693 | КТ-7 | ждёт |  |
| `GUIRace.kt` | 64 | КТ-7 | ждёт |  |
| `GUIScreenOverlay.kt` | 74 | КТ-6 | ждёт | холод Нифльхейма |
| `GUISheerCold.kt` | 35 | КТ-6 | ждёт | холод Нифльхейма |
| `GUISpells.kt` | 276 | КТ-7 | ждёт |  |
| `ItemsRemainingRenderHandler.kt` | 126 | КТ-4 | ждёт |  |

### `legacy/src/main/java/alfheim/client/integration/nei/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `NEIAlfheimConfig.kt` | 53 | КТ-10 | ждёт | NEI → JEI |

### `legacy/src/main/java/alfheim/client/integration/nei/recipes/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `RecipeHandlerAdvancedPlate.kt` | 101 | КТ-10 | ждёт | NEI → JEI |
| `RecipeHandlerAlphirine.kt` | 103 | КТ-10 | ждёт | NEI → JEI |
| `RecipeHandlerManaInfuser.kt` | 47 | КТ-10 | ждёт | NEI → JEI |
| `RecipeHandlerTradePortal.kt` | 107 | КТ-10 | ждёт | NEI → JEI |
| `RecipeHandlerTreeCrafting.kt` | 111 | КТ-10 | ждёт | NEI → JEI |

### `legacy/src/main/java/alfheim/client/lib/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `LibResourceLocationsActual.kt` | 29 | КТ-1 | перенесено | → `src/main/java/alfheim/client/lib/LibResourceLocationsActual.kt`; старый пилон — КТ-3, lexica — КТ-7, плащи — КТ-4 |

### `legacy/src/main/java/alfheim/client/model/armor/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `ModelArmorVolcano.kt` | 540 | КТ-4 | ждёт |  |
| `ModelBelt.kt` | 5 | КТ-4 | ждёт |  |
| `ModelElementalArmor.kt` | 274 | КТ-4 | ждёт |  |
| `ModelElvoriumArmor.kt` | 123 | КТ-4 | ждёт |  |
| `ModelFenrirArmor.kt` | 266 | КТ-4 | ждёт |  |
| `ModelSnowArmor.kt` | 500 | КТ-4 | ждёт |  |

### `legacy/src/main/java/alfheim/client/model/block/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `ModelBarrel.kt` | 410 | КТ-3 | ждёт |  |
| `ModelManaReflector.kt` | 105 | КТ-3 | ждёт |  |
| `ModelSimpleAnyavil.kt` | 35 | КТ-3 | ждёт |  |
| `ModelSimpleManaAccelerator.kt` | 137 | КТ-3 | ждёт |  |
| `ModelSpreaderFrame.kt` | 113 | КТ-3 | ждёт |  |
| `ModelYggFlower.kt` | 109 | КТ-3 | ждёт |  |

### `legacy/src/main/java/alfheim/client/model/entity/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `ModelBipedEyes.kt` | 44 | КТ-5 | ждёт |  |
| `ModelBipedGlowing.kt` | 26 | КТ-5 | ждёт |  |
| `ModelButterfly.kt` | 76 | КТ-5 | ждёт |  |
| `ModelEntityElf.kt` | 215 | КТ-5 | ждёт |  |
| `ModelEntityFenrir.kt` | 232 | КТ-8 | ждёт |  |
| `ModelEntityFlowerBud.kt` | 279 | КТ-5 | ждёт |  |
| `ModelEntityFlugel.kt` | 256 | КТ-8 | ждёт |  |
| `ModelEntityFrozenViking.kt` | 98 | КТ-5 | ждёт |  |
| `ModelEntityJellyfish.kt` | 141 | КТ-5 | ждёт |  |
| `ModelEntityLolicorn.kt` | 426 | КТ-5 | ждёт |  |
| `ModelEntityPrimalBoss.kt` | 181 | КТ-8 | ждёт |  |
| `ModelEntityRook.kt` | 553 | КТ-8 | ждёт |  |
| `ModelEntitySleipnir.kt` | 489 | КТ-5 | ждёт |  |
| `ModelEntitySurtr.kt` | 493 | КТ-8 | ждёт |  |
| `ModelEntityThrym.kt` | 719 | КТ-8 | ждёт |  |
| `ModelEntityVenusHumanTrap.kt` | 177 | КТ-5 | ждёт |  |
| `ModelIcicle.kt` | 46 | КТ-8 | ждёт |  |
| `ModelNekomimi.kt` | 104 | КТ-7 | ждёт |  |
| `ModelRollingMelon.kt` | 27 | КТ-8 | ждёт |  |
| `ModelSnowSprite.kt` | 31 | КТ-5 | ждёт |  |
| `ModelSubspaceSpear.kt` | 161 | КТ-4 | ждёт |  |

### `legacy/src/main/java/alfheim/client/model/item/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `ModelCreatorStaff.kt` | 253 | КТ-4 | ждёт |  |
| `ModelSurtrSword.kt` | 461 | КТ-4 | ждёт |  |
| `ModelThrymAxe.kt` | 593 | КТ-4 | ждёт |  |

### `legacy/src/main/java/alfheim/client/render/block/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `RenderBlockAlfheimPylons.kt` | 31 | КТ-3 | ждёт |  |
| `RenderBlockAlfheimThaumOre.kt` | 69 | — | выпало (Thaumcraft: мод отсутствует на 1.20.1) |  |
| `RenderBlockAnomalyHarvester.kt` | 27 | КТ-3 | ждёт |  |
| `RenderBlockAnyavil.kt` | 30 | КТ-3 | ждёт |  |
| `RenderBlockBarrel.kt` | 26 | КТ-3 | ждёт |  |
| `RenderBlockChair.kt` | 102 | КТ-3 | ждёт |  |
| `RenderBlockColoredDoubleGrass.kt` | 56 | КТ-2 | ждёт |  |
| `RenderBlockComposite.kt` | 179 | КТ-3 | ждёт |  |
| `RenderBlockDomainLobby.kt` | 32 | КТ-6 | ждёт |  |
| `RenderBlockDoubleBlock.kt` | 42 | КТ-3 | ждёт |  |
| `RenderBlockDoubleCamo.kt` | 78 | КТ-3 | ждёт |  |
| `RenderBlockFloodlight.kt` | 25 | КТ-3 | ждёт |  |
| `RenderBlockGrapeGreen.kt` | 20 | КТ-2 | ждёт |  |
| `RenderBlockGrapeRedPlanted.kt` | 93 | КТ-2 | ждёт |  |
| `RenderBlockHopper.kt` | 110 | КТ-3 | ждёт |  |
| `RenderBlockManaAccelerator.kt` | 26 | КТ-3 | ждёт |  |
| `RenderBlockManaReflector.kt` | 27 | КТ-3 | ждёт |  |
| `RenderBlockManaTuner.kt` | 25 | КТ-3 | ждёт |  |
| `RenderBlockNidhoggTooth.kt` | 123 | КТ-2 | ждёт |  |
| `RenderBlockNiflheimSet.kt` | 82 | КТ-2 | ждёт |  |
| `RenderBlockOnyx.kt` | 12 | КТ-2 | ждёт |  |
| `RenderBlockPowerStone.kt` | 30 | КТ-3 | ждёт |  |
| `RenderBlockShrinePanel.kt` | 362 | КТ-2 | ждёт |  |
| `RenderBlockSpire.kt` | 28 | КТ-3 | ждёт |  |
| `RenderBlockTable.kt` | 35 | КТ-3 | ждёт |  |
| `RenderBlockWorldTree.kt` | 32 | КТ-3 | ждёт |  |
| `RenderSimpleDoubleBlock.kt` | 638 | КТ-3 | ждёт |  |

### `legacy/src/main/java/alfheim/client/render/entity/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `RenderBooba.kt` | 119 | КТ-4 | ждёт |  |
| `RenderContributors.kt` | 275 | КТ-7 | ждёт |  |
| `RenderEntityAlfheimPixie.kt` | 50 | КТ-5 | ждёт |  |
| `RenderEntityBlackBolt.kt` | 99 | КТ-8 | ждёт |  |
| `RenderEntityButterfly.kt` | 54 | КТ-5 | ждёт |  |
| `RenderEntityDedMoroz.kt` | 21 | КТ-8 | ждёт |  |
| `RenderEntityDriftingMine.kt` | 52 | КТ-7 | ждёт |  |
| `RenderEntityElementalSlime.kt` | 17 | КТ-5 | ждёт |  |
| `RenderEntityElf.kt` | 11 | КТ-5 | ждёт |  |
| `RenderEntityElvenChakram.kt` | 49 | КТ-4 | ждёт |  |
| `RenderEntityFallingHang.kt` | 35 | КТ-5 | ждёт |  |
| `RenderEntityFenrir.kt` | 71 | КТ-8 | ждёт |  |
| `RenderEntityFenrirDome.kt` | 46 | КТ-8 | ждёт |  |
| `RenderEntityFenrirSlash.kt` | 43 | КТ-8 | ждёт |  |
| `RenderEntityFenrirStorm.kt` | 84 | КТ-7 | ждёт |  |
| `RenderEntityFloatingIsland.kt` | 102 | КТ-6 | ждёт |  |
| `RenderEntityFlowerBud.kt` | 18 | КТ-5 | ждёт |  |
| `RenderEntityFlugel.kt` | 24 | КТ-8 | ждёт |  |
| `RenderEntityFrozenViking.kt` | 10 | КТ-5 | ждёт |  |
| `RenderEntityGleipnir.kt` | 100 | КТ-4 | ждёт |  |
| `RenderEntityGravityTrap.kt` | 69 | КТ-7 | ждёт |  |
| `RenderEntityHarp.kt` | 44 | КТ-7 | ждёт |  |
| `RenderEntityIcicle.kt` | 48 | КТ-8 | ждёт |  |
| `RenderEntityJellyfish.kt` | 41 | КТ-5 | ждёт |  |
| `RenderEntityLeftHand.kt` | 134 | КТ-8 | ждёт |  |
| `RenderEntityLightningMark.kt` | 44 | КТ-7 | ждёт |  |
| `RenderEntityLolicorn.kt` | 54 | КТ-5 | ждёт |  |
| `RenderEntityManaCreeper.kt` | 60 | КТ-5 | ждёт |  |
| `RenderEntityMjolnir.kt` | 53 | КТ-4 | ждёт |  |
| `RenderEntityMortar.kt` | 28 | КТ-7 | ждёт |  |
| `RenderEntityMuspelheimSun.kt` | 18 | КТ-8 | ждёт |  |
| `RenderEntityMuspelheimSunSlash.kt` | 72 | КТ-8 | ждёт |  |
| `RenderEntityMuspelson.kt` | 24 | КТ-8 | ждёт |  |
| `RenderEntityPrimalMark.kt` | 97 | КТ-8 | ждёт |  |
| `RenderEntityResonance.kt` | 51 | КТ-4 | ждёт |  |
| `RenderEntityRollingMelon.kt` | 23 | КТ-8 | ждёт |  |
| `RenderEntityRook.kt` | 19 | КТ-8 | ждёт |  |
| `RenderEntitySniceBall.kt` | 54 | КТ-8 | ждёт |  |
| `RenderEntitySnowSprite.kt` | 59 | КТ-5 | ждёт |  |
| `RenderEntitySubspace.kt` | 33 | КТ-4 | ждёт |  |
| `RenderEntitySubspaceSpear.kt` | 53 | КТ-4 | ждёт |  |
| `RenderEntitySurtr.kt` | 213 | КТ-8 | ждёт |  |
| `RenderEntityThrownItem.kt` | 58 | КТ-2 | ждёт |  |
| `RenderEntityThrownPotion.kt` | 67 | КТ-2 | ждёт |  |
| `RenderEntityThrym.kt` | 161 | КТ-8 | ждёт |  |
| `RenderEntityVenusHumanTrap.kt` | 10 | КТ-5 | ждёт |  |
| `RenderEntityWarBanner.kt` | 28 | КТ-4 | ждёт |  |
| `RenderEntityWindBlade.kt` | 71 | КТ-7 | ждёт |  |
| `RenderFakeLightning.kt` | 97 | КТ-2 | ждёт |  |
| `RenderWings.kt` | 152 | КТ-7 | ждёт |  |
| `ShadedObjectHaloPlane.kt` | 37 | КТ-8 | ждёт |  |

### `legacy/src/main/java/alfheim/client/render/item/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `RenderEntityItemImmortal.kt` | 283 | КТ-4 | ждёт |  |
| `RenderFloatingFlowerRainbowItem.kt` | 29 | КТ-3 | ждёт |  |
| `RenderItemAkashicRecords.kt` | 172 | КТ-4 | ждёт |  |
| `RenderItemAnomaly.kt` | 90 | КТ-3 | ждёт |  |
| `RenderItemDoubleCamo.kt` | 62 | КТ-3 | ждёт |  |
| `RenderItemFenrirClaws.kt` | 96 | КТ-4 | ждёт |  |
| `RenderItemFlugelHead.kt` | 76 | КТ-8 | ждёт |  |
| `RenderItemManaReflector.kt` | 41 | КТ-3 | ждёт |  |
| `RenderItemMjolnir.kt` | 77 | КТ-4 | ждёт |  |
| `RenderItemOrgans.kt` | 10 | КТ-8 | ждёт |  |
| `RenderItemRoyalStaff.kt` | 67 | КТ-4 | ждёт |  |
| `RenderItemSnowSword.kt` | 118 | КТ-4 | ждёт |  |
| `RenderItemSurtrSword.kt` | 38 | КТ-4 | ждёт |  |
| `RenderItemThrymAxe.kt` | 50 | КТ-4 | ждёт |  |
| `RenderItemYggFlower.kt` | 40 | КТ-3 | ждёт |  |
| `RenderMoonBow.kt` | 78 | КТ-4 | ждёт |  |

### `legacy/src/main/java/alfheim/client/render/particle/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `EntityBloodFx.kt` | 109 | КТ-2 | ждёт |  |
| `EntityFeatherFx.kt` | 117 | КТ-7 | ждёт |  |
| `EntityFXSmoke.kt` | 69 | КТ-2 | ждёт |  |
| `EntityTornadoFX.kt` | 68 | КТ-8 | ждёт |  |
| `EntityVoxelFX.kt` | 123 | КТ-2 | ждёт |  |

### `legacy/src/main/java/alfheim/client/render/tile/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `MultipassRenderer.kt` | 82 | КТ-3 | ждёт |  |
| `RenderStar.kt` | 44 | КТ-3 | ждёт |  |
| `RenderTileAlfheimPortal.kt` | 62 | КТ-6 | ждёт |  |
| `RenderTileAlfheimPylons.kt` | 292 | КТ-3 | ждёт |  |
| `RenderTileAnimatedTorch.kt` | 66 | КТ-3 | ждёт |  |
| `RenderTileAnomaly.kt` | 71 | КТ-3 | ждёт |  |
| `RenderTileAnomalyHarvester.kt` | 72 | КТ-3 | ждёт |  |
| `RenderTileAnyavil.kt` | 49 | КТ-3 | ждёт |  |
| `RenderTileBarrel.kt` | 36 | КТ-3 | ждёт |  |
| `RenderTileDomainLobby.kt` | 83 | КТ-6 | ждёт |  |
| `RenderTileFloodLight.kt` | 101 | КТ-3 | ждёт |  |
| `RenderTileGaiaButton.kt` | 61 | КТ-3 | ждёт |  |
| `RenderTileHeadFlugel.kt` | 58 | КТ-8 | ждёт |  |
| `RenderTileHeadMiku.kt` | 58 | КТ-8 | ждёт |  |
| `RenderTileIcyGeyser.kt` | 56 | КТ-6 | ждёт |  |
| `RenderTileItemDisplay.kt` | 96 | КТ-3 | ждёт |  |
| `RenderTileItemFrame.kt` | 96 | КТ-3 | ждёт |  |
| `RenderTileManaAccelerator.kt` | 78 | КТ-3 | ждёт |  |
| `RenderTileManaReflector.kt` | 84 | КТ-3 | ждёт |  |
| `RenderTileManaTuner.kt` | 131 | КТ-3 | ждёт |  |
| `RenderTilePowerStone.kt` | 36 | КТ-3 | ждёт |  |
| `RenderTileRaceSelector.kt` | 179 | КТ-7 | ждёт |  |
| `RenderTileSpire.kt` | 42 | КТ-3 | ждёт |  |
| `RenderTileTradePortal.kt` | 93 | КТ-3 | ждёт |  |
| `RenderTileTreeBerry.kt` | 50 | КТ-2 | ждёт |  |
| `RenderTileWorldTree.kt` | 63 | КТ-3 | ждёт |  |
| `RenderTileYggFlower.kt` | 30 | КТ-3 | ждёт |  |

### `legacy/src/main/java/alfheim/client/render/world/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `AstrolabePreviewHandler.kt` | 74 | КТ-4 | ждёт |  |
| `DummyRenderHandler.kt` | 9 | КТ-6 | ждёт | нужен только мирам Хельхейма, Нифльхейма и доменов; в 1.20.1 небо и погоду рисует `DimensionSpecialEffects`, переносится с ними |
| `FenrirVisualEffectsRenderer.kt` | 56 | КТ-8 | ждёт |  |
| `SkyRendererAlfheim.kt` | 358 | КТ-6 | ждёт |  |
| `SkyRendererDomains.kt` | 80 | КТ-6 | ждёт |  |
| `SpellVisualizations.kt` | 182 | КТ-7 | ждёт |  |
| `VisualEffectHandlerClient.kt` | 544 | КТ-1 | перенесено | → `src/main/java/alfheim/client/render/world/VisualEffectHandlerClient.kt`; список эффектов работает, сами эффекты закомментированы до КТ тех, кто их шлёт |
| `WeatherRendererAlfheim.kt` | 144 | КТ-6 | ждёт |  |
| `WeatherRendererNiflheim.kt` | 98 | КТ-6 | ждёт |  |

### `legacy/src/main/java/alfheim/client/sound/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `EntityBoundMovingSound.kt` | 40 | КТ-8 | ждёт | музыка и звуки боссов |

### `legacy/src/main/java/alfheim/common/achievement/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `AlfheimAchievements.kt` | 127 | КТ-10 | ждёт | достижения → advancements |

### `legacy/src/main/java/alfheim/common/block/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `AlfheimBlocks.kt` | 830 | КТ-2 | перенесено | → `src/main/java/alfheim/common/block/AlfheimBlocks.kt`; блоки, которых ещё нет, закомментированы с `// PORT: КТ-n` и включаются вместе со своей КТ |
| `AlfheimFluffBlocks.kt` | 282 | КТ-2 | перенесено | → `src/main/java/alfheim/common/block/AlfheimFluffBlocks.kt`; то же |
| `BlockAiryVirus.kt` | 61 | КТ-2 | ждёт |  |
| `BlockAlfheimPortal.kt` | 80 | КТ-6 | ждёт | портал в Альфхейм |
| `BlockAlfheimPylon.kt` | 61 | КТ-3 | ждёт | с блок-сущностью |
| `BlockAlfheimSlabs.kt` | 90 | КТ-2 | ждёт |  |
| `BlockAlfStorage.kt` | 67 | КТ-2 | перенесено | → `src/main/java/alfheim/common/block/BlockAlfStorage.kt` |
| `BlockAmplifier.kt` | 25 | КТ-2 | ждёт |  |
| `BlockAnimatedTorch.kt` | 83 | КТ-3 | ждёт | с блок-сущностью |
| `BlockAnomaly.kt` | 123 | КТ-3 | ждёт | с блок-сущностью |
| `BlockAnomalyHarvester.kt` | 126 | КТ-3 | ждёт | с блок-сущностью |
| `BlockAnyavil.kt` | 141 | КТ-3 | ждёт | с блок-сущностью |
| `BlockBarrel.kt` | 115 | КТ-3 | ждёт | с блок-сущностью |
| `BlockBarrier.kt` | 51 | КТ-2 | ждёт |  |
| `BlockBottomlessChest.kt` | 61 | КТ-3 | ждёт | с блок-сущностью |
| `BlockChair.kt` | 83 | КТ-3 | ждёт | с блок-сущностью |
| `BlockComposite.kt` | 317 | КТ-3 | ждёт | с блок-сущностью |
| `BlockCracklingStar.kt` | 135 | КТ-3 | ждёт | с блок-сущностью |
| `BlockCurtainPlacer.kt` | 66 | КТ-3 | ждёт | с блок-сущностью |
| `BlockDirtDissolvable.kt` | 31 | КТ-2 | ждёт |  |
| `BlockDomainDoor.kt` | 69 | КТ-6 | ждёт | вход в Домены |
| `BlockDoubleBlock.kt` | 191 | КТ-3 | ждёт | с блок-сущностью |
| `BlockDoubleCamo.kt` | 220 | КТ-3 | ждёт | основа маскирующихся блоков с блок-сущностью |
| `BlockDreamSapling.kt` | 87 | КТ-2 | ждёт |  |
| `BlockDwarfLantern.kt` | 44 | КТ-2 | ждёт |  |
| `BlockElvenOre.kt` | 58 | КТ-2 | ждёт |  |
| `BlockElvenSand.kt` | 18 | КТ-2 | перенесено | → `src/main/java/alfheim/common/block/BlockElvenSand.kt` |
| `BlockElvenSandstone.kt` | 53 | КТ-2 | перенесено | → `src/main/java/alfheim/common/block/BlockElvenSandstone.kt` |
| `BlockEnderActuator.kt` | 58 | КТ-3 | ждёт | с блок-сущностью |
| `BlockFloodLight.kt` | 28 | КТ-3 | ждёт | WIP автора (`.WIP()`): переносится как есть, с меткой [WIP] (SPEC п. 6) |
| `BlockFunnel.kt` | 229 | КТ-3 | ждёт | с блок-сущностью |
| `BlockGaiaButton.kt` | 96 | КТ-3 | ждёт | с блок-сущностью |
| `BlockGrapeRed.kt` | 82 | КТ-2 | ждёт |  |
| `BlockGrapeRedPlanted.kt` | 134 | КТ-2 | ждёт |  |
| `BlockGrapeWhite.kt` | 102 | КТ-2 | ждёт |  |
| `BlockHang.kt` | 61 | КТ-2 | ждёт |  |
| `BlockHeadFlugel.kt` | 47 | КТ-8 | ждёт | трофей босса |
| `BlockHeadMiku.kt` | 48 | КТ-8 | ждёт | трофей босса |
| `BlockIcicle.kt` | 19 | КТ-2 | ждёт |  |
| `BlockIcyGeyser.kt` | 24 | КТ-6 | ждёт | гейзер Нифльхейма |
| `BlockItemDisplay.kt` | 136 | КТ-3 | ждёт | с блок-сущностью |
| `BlockItemFrame.kt` | 277 | КТ-3 | ждёт | WIP автора (`.WIP()`): переносится как есть, с меткой [WIP] (SPEC п. 6) |
| `BlockKindling.kt` | 57 | КТ-2 | ждёт |  |
| `BlockKudzuVine.kt` | 497 | КТ-3 | ждёт | с блок-сущностью |
| `BlockLivingCobble.kt` | 39 | КТ-2 | перенесено | → `src/main/java/alfheim/common/block/BlockLivingCobble.kt` |
| `BlockLivingMountain.kt` | 30 | КТ-2 | ждёт |  |
| `BlockLootbox.kt` | 260 | КТ-6 | ждёт | летающие острова |
| `BlockManaIce.kt` | 20 | КТ-2 | перенесено | → `src/main/java/alfheim/common/block/BlockManaIce.kt` |
| `BlockModTrapDoor.kt` | 39 | КТ-2 | ждёт |  |
| `BlockNidhoggTooth.kt` | 53 | КТ-2 | ждёт |  |
| `BlockNiflheim.kt` | 112 | КТ-2 | ждёт |  |
| `BlockNiflheimIce.kt` | 128 | КТ-2 | ждёт |  |
| `BlockNiflheimPortal.kt` | 160 | КТ-6 | ждёт | портал в Нифльхейм |
| `BlockOnyx.kt` | 11 | КТ-2 | ждёт | WIP автора (`.WIP()`): переносится как есть, с меткой [WIP] (SPEC п. 6) |
| `BlockPaneMeta.kt` | 46 | КТ-2 | ждёт |  |
| `BlockPatternLexicon.kt` | 63 | КТ-2 | перенесено | → `src/main/java/alfheim/common/block/BlockPatternLexicon.kt` |
| `BlockPowerStone.kt` | 56 | КТ-3 | ждёт | с блок-сущностью |
| `BlockRaceSelector.kt` | 167 | КТ-7 | ждёт | выбор расы |
| `BlockRealityAnchor.kt` | 18 | КТ-3 | ждёт | с блок-сущностью |
| `BlockRealmPowerCollector.kt` | 61 | КТ-2 | ждёт |  |
| `BlockRedFlame.kt` | 91 | КТ-2 | ждёт |  |
| `BlockRedstoneAttractor.kt` | 30 | КТ-2 | ждёт | WIP автора (`.WIP()`): переносится как есть, с меткой [WIP] (SPEC п. 6) |
| `BlockRedstoneRelay.kt` | 25 | КТ-3 | ждёт | с блок-сущностью |
| `BlockRedStringObserver.kt` | 39 | КТ-3 | ждёт | с блок-сущностью |
| `BlockRedStringWatcher.kt` | 53 | КТ-3 | ждёт | с блок-сущностью |
| `BlockRift.kt` | 22 | КТ-4 | ждёт | вместе с инструментами разлома (`ItemRiftPick`, `ItemRiftSword`) |
| `BlockSadOakLeaves.kt` | 71 | КТ-2 | ждёт |  |
| `BlockSecretGlass.kt` | 56 | КТ-3 | ждёт | с блок-сущностью |
| `BlockShrineGlass.kt` | 36 | КТ-2 | ждёт |  |
| `BlockShrinePillar.kt` | 33 | КТ-2 | ждёт |  |
| `BlockSnakeBody.kt` | 204 | КТ-2 | ждёт |  |
| `BlockSnakeObject.kt` | 32 | КТ-2 | ждёт |  |
| `BlockSnowGrass.kt` | 117 | КТ-2 | ждёт |  |
| `BlockSnowLayer.kt` | 122 | КТ-2 | ждёт |  |
| `BlockSpire.kt` | 23 | КТ-3 | ждёт | с блок-сущностью |
| `BlockStalactite.kt` | 16 | КТ-2 | ждёт |  |
| `BlockStalagmite.kt` | 16 | КТ-2 | ждёт |  |
| `BlockStar.kt` | 96 | КТ-3 | ждёт | с блок-сущностью |
| `BlockSubspacian.kt` | 79 | КТ-2 | ждёт |  |
| `BlockTable.kt` | 18 | КТ-3 | ждёт | с блок-сущностью |
| `BlockTradePortal.kt` | 51 | КТ-3 | ждёт | с блок-сущностью |
| `BlockTreeBerry.kt` | 122 | КТ-2 | ждёт | ягоды магических деревьев; блок-сущность простая, переносится с деревьями |
| `BlockYggFlower.kt` | 36 | КТ-3 | ждёт | с блок-сущностью |

### `legacy/src/main/java/alfheim/common/block/alt/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `BlockAltLeaves.kt` | 148 | КТ-2 | ждёт |  |
| `BlockAltPlanks.kt` | 120 | КТ-2 | ждёт |  |
| `BlockAltWood.kt` | 140 | КТ-2 | ждёт |  |
| `BlockAltWoodPartials.kt` | 96 | КТ-2 | ждёт |  |
| `BlockYggDecor.kt` | 31 | КТ-2 | ждёт |  |

### `legacy/src/main/java/alfheim/common/block/base/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `BlockContainerMod.kt` | 55 | КТ-2 | ждёт |  |
| `BlockLeavesMod.kt` | 209 | КТ-2 | ждёт |  |
| `BlockMod.kt` | 49 | КТ-2 | перенесено | → `src/main/java/alfheim/common/block/base/BlockMod.kt` |
| `BlockModRotatedPillar.kt` | 92 | КТ-2 | ждёт |  |
| `BlockRainbowManaFlame.kt` | 85 | КТ-3 | ждёт | с блок-сущностью |
| `BlockSlabMod.kt` | 53 | КТ-2 | ждёт |  |
| `BlockStairsMod.kt` | 31 | КТ-2 | ждёт |  |
| `IDoublePlant.kt` | 13 | КТ-2 | ждёт |  |
| `IMultipassRenderer.kt` | 10 | КТ-3 | ждёт | основа маскирующихся блоков с блок-сущностью |

### `legacy/src/main/java/alfheim/common/block/colored/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `BlockAuroraDirt.kt` | 161 | КТ-2 | ждёт |  |
| `BlockAuroraLeaves.kt` | 38 | КТ-2 | ждёт |  |
| `BlockAuroraPlanks.kt` | 64 | КТ-2 | ждёт |  |
| `BlockAuroraWood.kt` | 52 | КТ-2 | ждёт |  |
| `BlockAuroraWoodPartials.kt` | 54 | КТ-2 | ждёт |  |
| `BlockColoredDirt.kt` | 125 | КТ-2 | ждёт |  |
| `BlockColoredDoubleGrass.kt` | 177 | КТ-2 | ждёт |  |
| `BlockColoredGrass.kt` | 100 | КТ-2 | ждёт |  |
| `BlockColoredLamp.kt` | 90 | КТ-2 | ждёт |  |
| `BlockColoredLeaves.kt` | 65 | КТ-2 | ждёт |  |
| `BlockColoredPlanks.kt` | 106 | КТ-2 | ждёт |  |
| `BlockColoredSapling.kt` | 155 | КТ-2 | ждёт |  |
| `BlockColoredWood.kt` | 87 | КТ-2 | ждёт |  |
| `BlockColoredWoodSlab.kt` | 49 | КТ-2 | ждёт |  |
| `BlockColoredWoodStairs.kt` | 35 | КТ-2 | ждёт |  |
| `BlockFloatingFlowerRainbow.kt` | 32 | КТ-3 | ждёт | с блок-сущностью |

### `legacy/src/main/java/alfheim/common/block/colored/rainbow/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `BlockRainbowDirt.kt` | 92 | КТ-2 | ждёт |  |
| `BlockRainbowDoubleFlower.kt` | 162 | КТ-2 | ждёт |  |
| `BlockRainbowDoubleGrass.kt` | 185 | КТ-2 | ждёт |  |
| `BlockRainbowGrass.kt` | 199 | КТ-2 | ждёт |  |
| `BlockRainbowLeaves.kt` | 35 | КТ-2 | ждёт |  |
| `BlockRainbowMushroom.kt` | 106 | КТ-2 | ждёт |  |
| `BlockRainbowPlanks.kt` | 74 | КТ-2 | ждёт |  |
| `BlockRainbowWood.kt` | 51 | КТ-2 | ждёт |  |
| `BlockRainbowWoodPartials.kt` | 42 | КТ-2 | ждёт |  |
| `BlockShimmerQuartz.kt` | 93 | КТ-2 | ждёт |  |
| `BlockSoftStorage.kt` | 61 | КТ-2 | ждёт |  |

### `legacy/src/main/java/alfheim/common/block/compat/thaumcraft/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `BlockAlfheimThaumOre.kt` | 104 | — | выпало (Thaumcraft: мод отсутствует на 1.20.1) |  |

### `legacy/src/main/java/alfheim/common/block/corporea/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `BlockCorporeaAutocrafter.kt` | 218 | КТ-3 | ждёт |  |
| `BlockCorporeaInjector.kt` | 24 | КТ-3 | ждёт |  |
| `BlockCorporeaRat.kt` | 22 | КТ-3 | ждёт |  |
| `BlockCorporeaSparkBase.kt` | 34 | КТ-3 | ждёт |  |

### `legacy/src/main/java/alfheim/common/block/fluid/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `BlockManaFluid.kt` | 103 | КТ-2 | ждёт |  |

### `legacy/src/main/java/alfheim/common/block/magtrees/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `BlockMagicLeaves.kt` | 24 | КТ-2 | ждёт |  |
| `BlockTunedSapling.kt` | 67 | КТ-2 | ждёт |  |

### `legacy/src/main/java/alfheim/common/block/magtrees/barrier/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `BlockBarrierLeaves.kt` | 20 | КТ-2 | ждёт |  |
| `BlockBarrierPlanks.kt` | 37 | КТ-2 | ждёт |  |
| `BlockBarrierSapling.kt` | 21 | КТ-2 | ждёт |  |
| `BlockBarrierWood.kt` | 59 | КТ-2 | ждёт |  |
| `BlockBarrierWoodPartials.kt` | 25 | КТ-2 | ждёт |  |

### `legacy/src/main/java/alfheim/common/block/magtrees/calico/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `BlockCalicoLeaves.kt` | 24 | КТ-2 | ждёт |  |
| `BlockCalicoPlanks.kt` | 39 | КТ-2 | ждёт |  |
| `BlockCalicoSapling.kt` | 25 | КТ-2 | ждёт |  |
| `BlockCalicoWood.kt` | 56 | КТ-2 | ждёт |  |
| `BlockCalicoWoodPartials.kt` | 30 | КТ-2 | ждёт |  |
| `EventHandlerCalico.kt` | 50 | КТ-2 | ждёт |  |
| `IExplosionDampener.kt` | 11 | КТ-2 | ждёт |  |

### `legacy/src/main/java/alfheim/common/block/magtrees/circuit/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `BlockCircuitLeaves.kt` | 50 | КТ-2 | ждёт |  |
| `BlockCircuitPlanks.kt` | 66 | КТ-2 | ждёт |  |
| `BlockCircuitSapling.kt` | 37 | КТ-2 | ждёт |  |
| `BlockCircuitWood.kt` | 77 | КТ-2 | ждёт |  |
| `BlockCircuitWoodPartials.kt` | 79 | КТ-2 | ждёт |  |
| `ICircuitBlock.kt` | 20 | КТ-2 | ждёт |  |

### `legacy/src/main/java/alfheim/common/block/magtrees/lightning/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `BlockLightningLeaves.kt` | 19 | КТ-2 | ждёт |  |
| `BlockLightningPlanks.kt` | 58 | КТ-2 | ждёт |  |
| `BlockLightningSapling.kt` | 20 | КТ-2 | ждёт |  |
| `BlockLightningWood.kt` | 68 | КТ-2 | ждёт |  |
| `BlockLightningWoodPartials.kt` | 25 | КТ-2 | ждёт |  |

### `legacy/src/main/java/alfheim/common/block/magtrees/nether/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `BlockNetherLeaves.kt` | 26 | КТ-2 | ждёт |  |
| `BlockNetherPlanks.kt` | 68 | КТ-2 | ждёт |  |
| `BlockNetherSapling.kt` | 33 | КТ-2 | ждёт |  |
| `BlockNetherWood.kt` | 73 | КТ-2 | ждёт |  |
| `BlockNetherWoodPartials.kt` | 52 | КТ-2 | ждёт |  |

### `legacy/src/main/java/alfheim/common/block/magtrees/sealing/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `BlockSealingLeaves.kt` | 27 | КТ-2 | ждёт |  |
| `BlockSealingPlanks.kt` | 34 | КТ-2 | ждёт |  |
| `BlockSealingSapling.kt` | 28 | КТ-2 | ждёт |  |
| `BlockSealingWood.kt` | 58 | КТ-2 | ждёт |  |
| `BlockSealingWoodPartials.kt` | 42 | КТ-2 | ждёт |  |
| `EventHandlerSealingOak.kt` | 67 | КТ-2 | ждёт |  |
| `ISoundSilencer.kt` | 37 | КТ-2 | ждёт |  |

### `legacy/src/main/java/alfheim/common/block/mana/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `BlockManaAccelerator.kt` | 95 | КТ-3 | ждёт |  |
| `BlockManaInfuser.kt` | 71 | КТ-3 | ждёт |  |
| `BlockManaReflector.kt` | 162 | КТ-3 | ждёт |  |
| `BlockManaTuner.kt` | 112 | КТ-3 | ждёт |  |
| `BlockTreeCrafter.kt` | 54 | КТ-3 | ждёт |  |
| `BlockWorldTree.kt` | 72 | КТ-3 | ждёт |  |

### `legacy/src/main/java/alfheim/common/block/schema/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `BlockSchemaAnnihilator.kt` | 55 | КТ-6 | ждёт | инструменты построек автора |
| `BlockSchemaContoller.kt` | 51 | КТ-6 | ждёт | инструменты построек автора |
| `BlockSchemaFiller.kt` | 28 | КТ-6 | ждёт | инструменты построек автора |
| `BlockSchemaGenerator.kt` | 38 | КТ-6 | ждёт | инструменты построек автора |
| `BlockSchemaMarker.kt` | 27 | КТ-6 | ждёт | инструменты построек автора |

### `legacy/src/main/java/alfheim/common/block/tile/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `TileAlfheimPortal.kt` | 339 | КТ-6 | ждёт | портал в Альфхейм |
| `TileAlfheimPylon.kt` | 179 | КТ-3 | ждёт |  |
| `TileAnimatedTorch.kt` | 197 | КТ-3 | ждёт |  |
| `TileAnomaly.kt` | 98 | КТ-3 | ждёт |  |
| `TileAnomalyHarvester.kt` | 499 | КТ-3 | ждёт |  |
| `TileAnyavil.kt` | 191 | КТ-3 | ждёт |  |
| `TileBarrel.kt` | 132 | КТ-3 | ждёт |  |
| `TileBottomlessChest.kt` | 49 | КТ-3 | ждёт |  |
| `TileChair.kt` | 56 | КТ-3 | ждёт |  |
| `TileComposite.kt` | 82 | КТ-3 | ждёт |  |
| `TileCracklingStar.kt` | 104 | КТ-3 | ждёт |  |
| `TileCurtainPlacer.kt` | 136 | КТ-3 | ждёт |  |
| `TileDomainLobby.kt` | 187 | КТ-6 | ждёт | вход в Домены |
| `TileDoubleBlock.kt` | 11 | КТ-3 | ждёт |  |
| `TileDoubleCamo.kt` | 113 | КТ-3 | ждёт |  |
| `TileEnderActuator.kt` | 55 | КТ-3 | ждёт |  |
| `TileFloatingFlowerRainbow.kt` | 28 | КТ-3 | ждёт |  |
| `TileFloodLight.kt` | 17 | КТ-3 | ждёт | WIP автора (`.WIP()`): переносится как есть, с меткой [WIP] (SPEC п. 6) |
| `TileGaiaButton.kt` | 25 | КТ-3 | ждёт |  |
| `TileHeadFlugel.kt` | 5 | КТ-8 | ждёт | трофей босса |
| `TileHeadMiku.kt` | 5 | КТ-8 | ждёт | трофей босса |
| `TileIcyGeyser.kt` | 108 | КТ-6 | ждёт | гейзер Нифльхейма |
| `TileItemDisplay.kt` | 71 | КТ-3 | ждёт |  |
| `TileItemFrame.kt` | 65 | КТ-3 | ждёт | WIP автора (`.WIP()`): переносится как есть, с меткой [WIP] (SPEC п. 6) |
| `TileKudzuVine.kt` | 144 | КТ-3 | ждёт |  |
| `TileLightningTreeTop.kt` | 54 | КТ-2 | ждёт | блок-сущность магического дерева, переносится с деревом |
| `TileLivingwoodFunnel.kt` | 431 | КТ-3 | ждёт |  |
| `TileManaAccelerator.kt` | 118 | КТ-3 | ждёт |  |
| `TileManaInfuser.kt` | 373 | КТ-3 | ждёт |  |
| `TileManaReflector.kt` | 9 | КТ-3 | ждёт |  |
| `TileManaTuner.kt` | 198 | КТ-3 | ждёт |  |
| `TilePowerStone.kt` | 97 | КТ-3 | ждёт |  |
| `TileRaceSelector.kt` | 113 | КТ-7 | ждёт | выбор расы |
| `TileRainbowManaFlame.kt` | 96 | КТ-3 | ждёт |  |
| `TileRealityAnchor.kt` | 103 | КТ-3 | ждёт |  |
| `TileRedstoneRelay.kt` | 58 | КТ-3 | ждёт |  |
| `TileRedStringObserver.kt` | 7 | КТ-3 | ждёт |  |
| `TileRedStringWatcher.kt` | 78 | КТ-3 | ждёт |  |
| `TileRift.kt` | 203 | КТ-4 | ждёт | вместе с инструментами разлома (`ItemRiftPick`, `ItemRiftSword`) |
| `TileSchemaAnnihilator.kt` | 18 | КТ-6 | ждёт | инструменты построек автора |
| `TileSchemaController.kt` | 406 | КТ-6 | ждёт | инструменты построек автора |
| `TileSecretGlass.kt` | 12 | КТ-3 | ждёт |  |
| `TileSpire.kt` | 29 | КТ-3 | ждёт |  |
| `TileStar.kt` | 37 | КТ-3 | ждёт |  |
| `TileTable.kt` | 3 | КТ-3 | ждёт |  |
| `TileTradePortal.kt` | 288 | КТ-3 | ждёт |  |
| `TileTreeBerry.kt` | 11 | КТ-2 | ждёт | ягоды магических деревьев; блок-сущность простая, переносится с деревьями |
| `TileTreeCook.kt` | 32 | КТ-2 | ждёт | блок-сущность магического дерева, переносится с деревом |
| `TileTreeCrafter.kt` | 418 | КТ-3 | ждёт |  |
| `TileTreeWind.kt` | 61 | КТ-2 | ждёт | блок-сущность магического дерева, переносится с деревом |
| `TileVafthrudnirSoul.kt` | 166 | КТ-6 | ждёт | загадки Вафтруднира в Хельхейме |
| `TileWorldTree.kt` | 166 | КТ-3 | ждёт |  |
| `TileYggFlower.kt` | 55 | КТ-3 | ждёт |  |

### `legacy/src/main/java/alfheim/common/block/tile/corporea/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `TileCorporeaAutocrafter.kt` | 392 | КТ-3 | ждёт |  |
| `TileCorporeaInjector.kt` | 29 | КТ-3 | ждёт |  |
| `TileCorporeaRat.kt` | 116 | КТ-3 | ждёт |  |
| `TileCorporeaSparkBase.kt` | 22 | КТ-3 | ждёт |  |

### `legacy/src/main/java/alfheim/common/block/tile/sub/anomaly/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `SubTileAntigrav.kt` | 40 | КТ-3 | ждёт |  |
| `SubTileGravity.kt` | 74 | КТ-3 | ждёт |  |
| `SubTileKiller.kt` | 32 | КТ-3 | ждёт |  |
| `SubTileLightning.kt` | 185 | КТ-3 | ждёт |  |
| `SubTileManaTornado.kt` | 65 | КТ-3 | ждёт |  |
| `SubTileManaVoid.kt` | 64 | КТ-3 | ждёт |  |
| `SubTileSpeedUp.kt` | 62 | КТ-3 | ждёт |  |
| `SubTileWarp.kt` | 246 | КТ-3 | ждёт |  |

### `legacy/src/main/java/alfheim/common/block/tile/sub/flower/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `AlfheimSignature.kt` | 44 | КТ-3 | ждёт |  |
| `SubTileAquapanthus.kt` | 45 | КТ-3 | ждёт |  |
| `SubTileBudOfYggdrasil.kt` | 83 | КТ-3 | ждёт |  |
| `SubTileCrysanthermum.kt` | 164 | КТ-3 | ждёт |  |
| `SubTileGourmaryllisHard.kt` | 122 | КТ-3 | ждёт |  |
| `SubTileGourmaryllisUltra.kt` | 186 | КТ-3 | ждёт |  |
| `SubTileOrechidAlfarem.kt` | 30 | КТ-3 | ждёт |  |
| `SubTileOrechidEndium.kt` | 52 | КТ-3 | ждёт |  |
| `SubTilePetronia.kt` | 127 | КТ-3 | ждёт |  |
| `SubTileRattlerose.kt` | 274 | КТ-3 | ждёт |  |
| `SubTileStormFlower.kt` | 72 | КТ-3 | ждёт |  |
| `SubTileTradescantia.kt` | 334 | КТ-3 | ждёт |  |
| `SubTileWeatherFlower.kt` | 84 | КТ-3 | ждёт |  |
| `SubTileWitherAconite.kt` | 90 | КТ-3 | ждёт |  |

### `legacy/src/main/java/alfheim/common/compat/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `AngelicaCompat.kt` | 104 | — | выпало (Angelica: мод отсутствует на 1.20.1) |  |
| `TransformableHookReplacerHandler.kt` | 97 | по HOOKS.md | ждёт | врезки переносятся по одной вместе со своей механикой (SPEC Р-6) |

### `legacy/src/main/java/alfheim/common/core/asm/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `AlfheimHookLoader.kt` | 91 | — | выпало (загрузчик HookLib; сами врезки — в HOOKS.md (SPEC Р-6)) |  |

### `legacy/src/main/java/alfheim/common/core/asm/hook/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `AlfheimFieldHookHandler.java` | 17 | по HOOKS.md | ждёт | врезки переносятся по одной вместе со своей механикой (SPEC Р-6) |
| `AlfheimHookHandler.kt` | 2333 | по HOOKS.md | ждёт | врезки переносятся по одной вместе со своей механикой (SPEC Р-6) |
| `AlfheimHPHooks.kt` | 55 | КТ-7 | ждёт | врезки — в HOOKS.md |
| `Botania18AndUpBackport.kt` | 269 | по HOOKS.md | ждёт | врезки переносятся по одной вместе со своей механикой (SPEC Р-6) |
| `ElementalDamageAdapter.kt` | 414 | КТ-4 | ждёт | врезки — в HOOKS.md |

### `legacy/src/main/java/alfheim/common/core/asm/hook/extender/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `FlowerBagExtender.kt` | 268 | КТ-3 | ждёт | расширитель Botania; врезки — в HOOKS.md |
| `FurnaceExtender.kt` | 125 | КТ-3 | ждёт | расширитель Botania; врезки — в HOOKS.md |
| `ItemAuraRingExtender.kt` | 42 | КТ-3 | ждёт | расширитель Botania; врезки — в HOOKS.md |
| `ItemLensExtender.kt` | 121 | КТ-3 | ждёт | расширитель Botania; врезки — в HOOKS.md |
| `ItemTwigWandExtender.kt` | 80 | КТ-3 | ждёт | расширитель Botania; врезки — в HOOKS.md |
| `LensPaintExtender.kt` | 217 | КТ-3 | ждёт | расширитель Botania; врезки — в HOOKS.md |
| `LightRelayExtender.kt` | 161 | КТ-3 | ждёт | расширитель Botania; врезки — в HOOKS.md |
| `ManaSpreaderExtender.kt` | 210 | КТ-3 | ждёт | расширитель Botania; врезки — в HOOKS.md |
| `PureDaisyExtender.kt` | 52 | КТ-3 | ждёт | расширитель Botania; врезки — в HOOKS.md |
| `QuartzExtender.kt` | 99 | КТ-3 | ждёт | расширитель Botania; врезки — в HOOKS.md |
| `RelicHooks.kt` | 246 | КТ-4 | ждёт | врезки — в HOOKS.md |
| `SparkExtender.kt` | 154 | КТ-3 | ждёт | расширитель Botania; врезки — в HOOKS.md |

### `legacy/src/main/java/alfheim/common/core/asm/hook/fixes/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `BotaniaGlowingRenderFixes.kt` | 78 | КТ-4 | ждёт | врезки — в HOOKS.md |
| `CorporeaInputFix.kt` | 61 | КТ-4 | ждёт | врезки — в HOOKS.md |
| `FlightTiaraFix.kt` | 143 | КТ-4 | ждёт | врезки — в HOOKS.md |
| `GodAttributesHooks.kt` | 204 | КТ-4 | ждёт | врезки — в HOOKS.md |
| `RecipeAncientWillsFix.kt` | 73 | КТ-4 | ждёт | врезки — в HOOKS.md |

### `legacy/src/main/java/alfheim/common/core/asm/hook/integration/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `BotaniaVisDiscountHooks.kt` | 51 | — | выпало (Thaumcraft: мод отсутствует на 1.20.1) | врезки — в HOOKS.md |
| `RedstoneRodHookHandler.kt` | 26 | КТ-4 | ждёт | врезки — в HOOKS.md |
| `TGHandlerBotaniaAdapterHooks.kt` | 82 | — | выпало (Travellers Gear: мод отсутствует на 1.20.1) | врезки — в HOOKS.md |
| `TraitFairySpawner.kt` | 25 | КТ-10 | ждёт | Tinkers 3; врезка — в HOOKS.md |

### `legacy/src/main/java/alfheim/common/core/asm/hook/replacer/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `HookReplacerHandler.java` | 155 | по HOOKS.md | ждёт | врезки переносятся по одной вместе со своей механикой (SPEC Р-6) |
| `HookReplacerHandler.kt` | 258 | по HOOKS.md | ждёт | врезки переносятся по одной вместе со своей механикой (SPEC Р-6) |

### `legacy/src/main/java/alfheim/common/core/asm/superwrapper/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `SuperWrapperHandler.kt` | 36 | по HOOKS.md | ждёт | врезки переносятся по одной вместе со своей механикой (SPEC Р-6) |

### `legacy/src/main/java/alfheim/common/core/asm/transformer/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `AlfheimClassTransformer.kt` | 608 | по HOOKS.md | ждёт | врезки переносятся по одной вместе со своей механикой (SPEC Р-6) |

### `legacy/src/main/java/alfheim/common/core/command/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `CommandAlfheim.kt` | 194 | КТ-7 | ждёт |  |
| `CommandDebug.kt` | 79 | КТ-7 | ждёт |  |
| `CommandMTSpellInfo.kt` | 24 | — | выпало (MineTweaker: мод отсутствует на 1.20.1) |  |

### `legacy/src/main/java/alfheim/common/core/handler/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `AlfheimAchievementHandler.kt` | 83 | КТ-10 | ждёт | достижения → advancements |
| `AlfheimConfigHandler.kt` | 613 | КТ-1 | перенесено | → `src/main/java/alfheim/common/core/handler/AlfheimConfigHandler.kt`; удалённые опции — MAPPING.md |
| `AlfheimPreConfigHandler.kt` | 25 | КТ-1 | перенесено | → `src/main/java/alfheim/common/core/handler/AlfheimPreConfigHandler.kt` |
| `CardinalSystem.kt` | 1173 | КТ-7 | ждёт |  |
| `ChunkLoadingHandler.kt` | 129 | КТ-6 | ждёт |  |
| `DispenserHandlers.kt` | 174 | КТ-2 | ждёт |  |
| `ESMHandlers.kt` | 391 | КТ-7 | ждёт |  |
| `EventHandler.kt` | 488 | КТ-1 | перенесено | → `src/main/java/alfheim/common/core/handler/EventHandler.kt`; общий обработчик: работают новости при входе и две особенности участников, остальное раскомментирует КТ своей механики |
| `EventHandlerSummer.kt` | 38 | КТ-8 | ждёт | сезонные события |
| `EventHandlerWinter.kt` | 22 | КТ-8 | ждёт | сезонные события |
| `HilarityHandler.kt` | 222 | КТ-4 | ждёт | обращается к sessionserver Mojang (SPEC п. 8) |
| `KeyBindingHandler.kt` | 91 | КТ-7 | ждёт |  |
| `SheerColdHandler.kt` | 225 | КТ-6 | ждёт |  |
| `SoulRestructuringHandler.kt` | 146 | КТ-3 | ждёт |  |
| `TimeHandler.kt` | 20 | КТ-1 | перенесено | → `src/main/java/alfheim/common/core/handler/TimeHandler.kt`; без правок |
| `TradingGiftsHandler.kt` | 135 | КТ-3 | ждёт |  |
| `VisualEffectHandler.kt` | 22 | КТ-1 | перенесено | → `src/main/java/alfheim/common/core/handler/VisualEffectHandler.kt` |
| `WorkInProgressItemsHandler.kt` | 33 | КТ-2 | ждёт | метка [WIP] для вещей автора |

### `legacy/src/main/java/alfheim/common/core/handler/ragnarok/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `RagnarokEmblemCraftHandler.kt` | 136 | КТ-8 | ждёт |  |
| `RagnarokHandler.kt` | 1164 | КТ-8 | ждёт |  |

### `legacy/src/main/java/alfheim/common/core/helper/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `ContributorsPrivacyHelper.kt` | 151 | КТ-1 | перенесено | → `src/main/java/alfheim/common/core/helper/ContributorsPrivacyHelper.kt`; обращается к Bitbucket автора (SPEC п. 8) |
| `CorporeaAdvancedHelper.kt` | 109 | КТ-3 | ждёт |  |
| `DiceDropsHelper.kt` | 70 | КТ-4 | ждёт |  |
| `ElementalDamageHelper.kt` | 319 | КТ-4 | ждёт |  |
| `ElvenFlightHelper.kt` | 73 | КТ-7 | ждёт |  |
| `RotateGenerationHelper.kt` | 175 | КТ-6 | ждёт |  |

### `legacy/src/main/java/alfheim/common/core/proxy/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `CommonProxy.kt` | 110 | КТ-1 | перенесено | → `src/main/java/alfheim/common/core/proxy/CommonProxy.kt`; вызовы будущих КТ закомментированы с номером КТ |

### `legacy/src/main/java/alfheim/common/core/registry/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `AlfheimRegistry.kt` | 611 | КТ-1 | перенесено | → `src/main/java/alfheim/common/core/registry/AlfheimRegistry.kt`; работают веса руд для цветов, остальное раскомментирует КТ своих вещей |

### `legacy/src/main/java/alfheim/common/core/util/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `AlfheimTab.kt` | 785 | КТ-1 | перенесено | → `src/main/java/alfheim/common/core/util/AlfheimTab.kt`; строки списка вкладки ждут КТ своих вещей |
| `DamageSourceSpell.kt` | 149 | КТ-7 | ждёт |  |
| `InfoLoader.kt` | 82 | КТ-1 | перенесено | → `src/main/java/alfheim/common/core/util/InfoLoader.kt`; обращается к Bitbucket автора (SPEC п. 8) |

### `legacy/src/main/java/alfheim/common/crafting/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `CraftingExtensions.kt` | 15 | КТ-2 | ждёт |  |

### `legacy/src/main/java/alfheim/common/crafting/recipe/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `AlfheimRecipes.kt` | 3222 | КТ-2 | ждёт | переносится по одному рецепту; рецепты вещей других КТ — вместе с ними (SPEC Р-9) |
| `RecipePureDaisyExclusion.kt` | 19 | КТ-3 | ждёт |  |
| `RecipePureDaisyMeta.kt` | 19 | КТ-3 | ждёт |  |
| `RecipeRuneAltarFull.kt` | 9 | КТ-3 | ждёт |  |
| `ShapedOreRecipeLearnable.kt` | 37 | КТ-7 | ждёт | рецепт по знаниям `CardinalSystem` |
| `TunerIncantationIO.kt` | 6 | КТ-3 | ждёт |  |

### `legacy/src/main/java/alfheim/common/crafting/recipe/barrel/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `RecipeBeer.kt` | 236 | КТ-3 | ждёт |  |
| `RecipeWine.kt` | 216 | КТ-3 | ждёт |  |
| `RecipeWineWhite.kt` | 35 | КТ-3 | ждёт |  |

### `legacy/src/main/java/alfheim/common/crafting/recipe/tuner/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `IncantationElementalSlimeGrowth.kt` | 16 | КТ-3 | ждёт |  |
| `IncantationEquipmentElementalTuning.kt` | 128 | КТ-3 | ждёт |  |
| `IncantationThaumWandOvercharge.kt` | 129 | — | выпало (Thaumcraft: мод отсутствует на 1.20.1) |  |

### `legacy/src/main/java/alfheim/common/crafting/recipe/workbench/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `RecipeAesirCloak.kt` | 44 | КТ-4 | ждёт |  |
| `RecipeCleanRelic.kt` | 66 | КТ-4 | ждёт |  |
| `RecipeClearLoki.kt` | 54 | КТ-4 | ждёт |  |
| `RecipeElvenWeed.kt` | 59 | КТ-2 | ждёт |  |
| `RecipeHelmRevealingAlfheim.kt` | 59 | — | выпало (Thaumcraft: мод отсутствует на 1.20.1) | очки Thaumcraft + шлем |
| `RecipeLensPurification.kt` | 42 | КТ-3 | ждёт |  |
| `RecipeLensSplit.kt` | 29 | КТ-3 | ждёт |  |
| `RecipeLootInterceptor.kt` | 53 | КТ-4 | ждёт |  |
| `RecipeLootInterceptorClear.kt` | 53 | КТ-4 | ждёт |  |
| `RecipeRainbowLensDye.kt` | 75 | КТ-3 | ждёт |  |
| `RecipeResonatorTipping.kt` | 50 | КТ-4 | ждёт |  |
| `RecipeRingDyes.kt` | 104 | КТ-4 | ждёт |  |
| `RecipeSaveIvy.kt` | 97 | КТ-2 | ждёт |  |
| `RecipeSpecialFloatingFlower.kt` | 51 | КТ-3 | ждёт |  |
| `RecipeStencil.kt` | 104 | КТ-2 | ждёт |  |
| `RecipeThrowablePotion.kt` | 55 | КТ-2 | ждёт |  |

### `legacy/src/main/java/alfheim/common/entity/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `EntityAlfheimPixie.kt` | 142 | КТ-5 | ждёт |  |
| `EntityBlackBolt.kt` | 91 | КТ-8 | ждёт |  |
| `EntityButterfly.kt` | 196 | КТ-5 | ждёт |  |
| `EntityCharge.kt` | 53 | КТ-8 | ждёт |  |
| `EntityEarthquake.kt` | 158 | КТ-8 | ждёт |  |
| `EntityEarthquakeFracture.kt` | 91 | КТ-8 | ждёт |  |
| `EntityElementalSlime.kt` | 103 | КТ-5 | ждёт |  |
| `EntityElf.kt` | 525 | КТ-5 | ждёт |  |
| `EntityElvenChakram.kt` | 245 | КТ-4 | ждёт |  |
| `EntityFallingHang.kt` | 69 | КТ-5 | ждёт |  |
| `EntityFenrirDome.kt` | 103 | КТ-8 | ждёт | зарегистрирована, но используется только незарегистрированным `EntityFenrirNew`; переносится как зарегистрированная (SPEC п. 6) |
| `EntityFenrirSlash.kt` | 51 | КТ-8 | ждёт | зарегистрирована, но используется только незарегистрированным `EntityFenrirNew`; переносится как зарегистрированная (SPEC п. 6) |
| `EntityFireAura.kt` | 47 | КТ-4 | ждёт |  |
| `EntityFireSpirit.kt` | 465 | КТ-8 | ждёт |  |
| `EntityFireTornado.kt` | 86 | КТ-8 | ждёт |  |
| `EntityFlowerBud.kt` | 123 | КТ-5 | ждёт |  |
| `EntityFracturedSpaceCollector.kt` | 216 | КТ-4 | ждёт |  |
| `EntityFrozenViking.kt` | 106 | КТ-5 | ждёт |  |
| `EntityGleipnir.kt` | 71 | КТ-4 | ждёт |  |
| `EntityIcicle.kt` | 178 | КТ-8 | ждёт |  |
| `EntityJellyfish.kt` | 183 | КТ-5 | ждёт |  |
| `EntityLightningMark.kt` | 50 | КТ-7 | ждёт |  |
| `EntityLolicorn.kt` | 328 | КТ-5 | ждёт |  |
| `EntityMagicArrow.kt` | 217 | КТ-4 | ждёт |  |
| `EntityMeteor.kt` | 166 | КТ-8 | ждёт |  |
| `EntityMjolnir.kt` | 137 | КТ-4 | ждёт |  |
| `EntityMuspelheimSun.kt` | 98 | КТ-8 | ждёт |  |
| `EntityMuspelheimSunSlash.kt` | 82 | КТ-8 | ждёт |  |
| `EntityMuspelson.kt` | 176 | КТ-8 | ждёт |  |
| `EntityPrimalBossChunkAttack.kt` | 159 | КТ-8 | ждёт |  |
| `EntityPrimalMark.kt` | 94 | КТ-8 | ждёт |  |
| `EntityResonance.kt` | 249 | КТ-4 | ждёт |  |
| `EntityRift.kt` | 227 | КТ-4 | ждёт |  |
| `EntityRollingMelon.kt` | 107 | КТ-8 | ждёт |  |
| `EntitySniceBall.kt` | 66 | КТ-8 | ждёт |  |
| `EntitySnowSprite.kt` | 160 | КТ-5 | ждёт |  |
| `EntitySubspace.kt` | 167 | КТ-4 | ждёт |  |
| `EntitySubspaceSpear.kt` | 111 | КТ-4 | ждёт |  |
| `EntityThrowableCopy_1.12.2.kt` | 19 | КТ-2 | ждёт |  |
| `EntityThrowableItem.kt` | 52 | КТ-2 | ждёт |  |
| `EntityThrownPotion.kt` | 96 | КТ-2 | ждёт |  |
| `EntityTornado.kt` | 85 | КТ-8 | ждёт | зарегистрирована, но используется только незарегистрированным `EntityFenrirNew`; переносится как зарегистрированная (SPEC п. 6) |
| `EntityVenusHumanTrap.kt` | 145 | КТ-5 | ждёт |  |
| `EntityVoidCreeper.kt` | 37 | КТ-5 | ждёт |  |
| `EntityWarBanner.kt` | 42 | КТ-4 | ждёт |  |
| `FakeLightning.kt` | 52 | КТ-2 | ждёт |  |

### `legacy/src/main/java/alfheim/common/entity/ai/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `AIAttackOnIntersect.kt` | 46 | КТ-5 | ждёт |  |
| `EntityAICreeperAvoidPooka.kt` | 90 | КТ-7 | ждёт | криперы избегают расы Пука |
| `EntityAIFleeOnLowHP.kt` | 49 | КТ-5 | ждёт |  |
| `EntityAIHurtByTargetNotLowHP.kt` | 15 | КТ-5 | ждёт |  |
| `EntityAINearestAttackableTargetNotLowHP.kt` | 23 | КТ-5 | ждёт |  |

### `legacy/src/main/java/alfheim/common/entity/ai/elf/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `EntityAIElfHurtByTarget.kt` | 48 | КТ-5 | ждёт |  |
| `EntityElfDialogLogic.kt` | 185 | — | WIP — стадия 2 | диалоги эльфов: вызов отключён автором (FIXME «crashes in jar»), у PRAETOR и PRIEST — `TODO()` (SPEC п. 6) |
| `EntityElfJunkmanLogic.kt` | 40 | — | WIP — стадия 2 | торговля эльфа-старьёвщика: нигде не вызывается |

### `legacy/src/main/java/alfheim/common/entity/boss/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `EntityDedMoroz.kt` | 231 | КТ-8 | ждёт |  |
| `EntityFenrir.kt` | 507 | КТ-8 | ждёт |  |
| `EntityFenrirNew.kt` | 243 | — | WIP — стадия 2 | новый Фенрир автора: не зарегистрирован (SPEC п. 6) |
| `EntityFlugel.kt` | 1123 | КТ-8 | ждёт |  |
| `EntityRook.kt` | 205 | КТ-8 | ждёт | WIP-босс автора: зарегистрирован, вызывается командой; переносится как есть (SPEC п. 6) |
| `IForceKill.kt` | 5 | КТ-8 | ждёт |  |

### `legacy/src/main/java/alfheim/common/entity/boss/ai/fenrir/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `EntityAIFenrirDashAtTarget.kt` | 40 | КТ-8 | ждёт |  |
| `EntityAIFenrirLeapAtTarget.kt` | 63 | КТ-8 | ждёт |  |

### `legacy/src/main/java/alfheim/common/entity/boss/ai/fenrirnew/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `EntityAIFenrirBite.kt` | 33 | — | WIP — стадия 2 | ИИ нового Фенрира, не зарегистрирован (SPEC п. 6) |
| `EntityAIFenrirHit.kt` | 36 | — | WIP — стадия 2 | ИИ нового Фенрира, не зарегистрирован (SPEC п. 6) |
| `EntityAIFenrirJump.kt` | 45 | — | WIP — стадия 2 | ИИ нового Фенрира, не зарегистрирован (SPEC п. 6) |
| `EntityAIFenrirOnslaught.kt` | 87 | — | WIP — стадия 2 | ИИ нового Фенрира, не зарегистрирован (SPEC п. 6) |
| `EntityAIFenrirSkillBase.kt` | 64 | — | WIP — стадия 2 | ИИ нового Фенрира, не зарегистрирован (SPEC п. 6) |
| `EntityAIFenrirSlash.kt` | 39 | — | WIP — стадия 2 | ИИ нового Фенрира, не зарегистрирован (SPEC п. 6) |
| `EntityAIFenrirTailSwipe.kt` | 36 | — | WIP — стадия 2 | ИИ нового Фенрира, не зарегистрирован (SPEC п. 6) |
| `EntityAIFenrirTornado.kt` | 33 | — | WIP — стадия 2 | ИИ нового Фенрира, не зарегистрирован (SPEC п. 6) |

### `legacy/src/main/java/alfheim/common/entity/boss/ai/flugel/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `AIBase.kt` | 30 | КТ-8 | ждёт |  |
| `AIChase.kt` | 70 | КТ-8 | ждёт |  |
| `AIDeathray.kt` | 96 | КТ-8 | ждёт |  |
| `AIEnergy.kt` | 29 | КТ-8 | ждёт |  |
| `AIInvul.kt` | 26 | КТ-8 | ждёт |  |
| `AILightning.kt` | 46 | КТ-8 | ждёт |  |
| `AIRays.kt` | 58 | КТ-8 | ждёт |  |
| `AIRegen.kt` | 27 | КТ-8 | ждёт |  |
| `AITask.kt` | 25 | КТ-8 | ждёт |  |
| `AITeleport.kt` | 17 | КТ-8 | ждёт |  |
| `AIWait.kt` | 18 | КТ-8 | ждёт |  |

### `legacy/src/main/java/alfheim/common/entity/boss/primal/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `EntityPrimalBoss.kt` | 625 | КТ-8 | ждёт |  |
| `EntitySurtr.kt` | 265 | КТ-8 | ждёт |  |
| `EntityThrym.kt` | 194 | КТ-8 | ждёт |  |

### `legacy/src/main/java/alfheim/common/entity/boss/primal/ai/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `PrimalAIRangedAttack.kt` | 58 | КТ-8 | ждёт |  |
| `PrimalAISelectTarget.kt` | 53 | КТ-8 | ждёт |  |
| `PrimalAISpinning.kt` | 40 | КТ-8 | ждёт |  |
| `PrimalAISuperSmash.kt` | 45 | КТ-8 | ждёт |  |

### `legacy/src/main/java/alfheim/common/entity/boss/primal/ai/surtr/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `SurtrAISecondStageStart.kt` | 54 | КТ-8 | ждёт |  |
| `SurtrAIThirdStageStart.kt` | 26 | КТ-8 | ждёт |  |

### `legacy/src/main/java/alfheim/common/entity/boss/primal/ai/thrym/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `ThrymAISecondStageStart.kt` | 77 | КТ-8 | ждёт |  |
| `ThrymAIThirdStageStart.kt` | 175 | КТ-8 | ждёт |  |

### `legacy/src/main/java/alfheim/common/entity/item/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `EntityItemImmortal.kt` | 161 | КТ-4 | ждёт | реликвии не исчезают на земле |
| `EntityItemImmortalRelic.kt` | 45 | КТ-4 | ждёт | реликвии не исчезают на земле |
| `IImmortalHandledItem.kt` | 6 | КТ-4 | ждёт | реликвии не исчезают на земле |

### `legacy/src/main/java/alfheim/common/entity/spell/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `EntitySpellAcidMyst.kt` | 64 | КТ-7 | ждёт |  |
| `EntitySpellAquaStream.kt` | 64 | КТ-7 | ждёт |  |
| `EntitySpellDarkness.kt` | 59 | КТ-7 | ждёт |  |
| `EntitySpellDriftingMine.kt` | 129 | КТ-7 | ждёт |  |
| `EntitySpellFenrirStorm.kt` | 96 | КТ-7 | ждёт |  |
| `EntitySpellFireball.kt` | 216 | КТ-7 | ждёт |  |
| `EntitySpellFirestar.kt` | 100 | КТ-7 | ждёт |  |
| `EntitySpellFirewall.kt` | 85 | КТ-7 | ждёт |  |
| `EntitySpellGravityTrap.kt` | 82 | КТ-7 | ждёт |  |
| `EntitySpellHarp.kt` | 78 | КТ-7 | ждёт |  |
| `EntitySpellIsaacMissile.kt` | 138 | КТ-7 | ждёт |  |
| `EntitySpellLeafStorm.kt` | 56 | КТ-7 | ждёт |  |
| `EntitySpellMortar.kt` | 134 | КТ-7 | ждёт |  |
| `EntitySpellNoteshot.kt` | 65 | КТ-7 | ждёт |  |
| `EntitySpellWindBlade.kt` | 105 | КТ-7 | ждёт |  |

### `legacy/src/main/java/alfheim/common/floatingisland/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `EntityFloatingIsland.kt` | 447 | КТ-6 | ждёт |  |
| `FloatingIslandBlockAccess.kt` | 232 | КТ-6 | ждёт |  |
| `FloatingIslandGenerator.kt` | 396 | КТ-6 | ждёт |  |
| `FloatingIslandInteractionHandler.kt` | 144 | КТ-6 | ждёт |  |
| `FloatingIslandPathfinder.kt` | 274 | КТ-6 | ждёт |  |

### `legacy/src/main/java/alfheim/common/integration/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `ModdedDimensionsIntegration.kt` | 61 | КТ-10 | ждёт | части Twilight Forest и Aether; части остальных модов выпадают (SPEC п. 7) |
| `ThermalFoundationIntegration.kt` | 46 | — | выпало (Thermal Foundation: мод отсутствует на 1.20.1) |  |

### `legacy/src/main/java/alfheim/common/integration/etfuturum/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `EtFuturumAlfheimConfig.kt` | 41 | — | выпало (Et Futurum: мод отсутствует на 1.20.1) |  |

### `legacy/src/main/java/alfheim/common/integration/minetweaker/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `MinetweakerAlfheimConfig.kt` | 48 | — | выпало (MineTweaker: мод отсутствует на 1.20.1) |  |
| `MTHandlerGeneral.kt` | 92 | — | выпало (MineTweaker: мод отсутствует на 1.20.1) |  |
| `MTHandlerManaInfuser.kt` | 78 | — | выпало (MineTweaker: мод отсутствует на 1.20.1) |  |
| `MTHandlerManaTuner.kt` | 54 | — | выпало (MineTweaker: мод отсутствует на 1.20.1) |  |
| `MTHandlerMobSpawn.kt` | 50 | — | выпало (MineTweaker: мод отсутствует на 1.20.1) |  |
| `MTHandlerSpells.kt` | 161 | — | выпало (MineTweaker: мод отсутствует на 1.20.1) |  |
| `MTHandlerSuffuser.kt` | 84 | — | выпало (MineTweaker: мод отсутствует на 1.20.1) |  |

### `legacy/src/main/java/alfheim/common/integration/multipart/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `MultipartAlfheimConfig.kt` | 84 | — | выпало (Forge Multipart: мод отсутствует на 1.20.1) |  |

### `legacy/src/main/java/alfheim/common/integration/thaumcraft/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `TCHandlerAspects.kt` | 841 | — | выпало (Thaumcraft: мод отсутствует на 1.20.1) |  |
| `ThaumcraftAlfheimConfig.kt` | 16 | — | выпало (Thaumcraft: мод отсутствует на 1.20.1) |  |
| `ThaumcraftAlfheimModule.kt` | 634 | — | выпало (Thaumcraft: мод отсутствует на 1.20.1) |  |
| `ThaumcraftSuffusionRecipes.kt` | 44 | — | выпало (Thaumcraft: мод отсутствует на 1.20.1) |  |

### `legacy/src/main/java/alfheim/common/integration/tinkersconstruct/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `TinkersConstructAlfheimConfig.kt` | 236 | КТ-10 | ждёт | Tinkers' Construct 3 |
| `TinkersConstructAlfheimModule.kt` | 147 | КТ-10 | ждёт | Tinkers' Construct 3 |

### `legacy/src/main/java/alfheim/common/integration/tinkersconstruct/modifier/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `ModManaRepair.kt` | 84 | КТ-10 | ждёт | Tinkers' Construct 3 |

### `legacy/src/main/java/alfheim/common/integration/travellersgear/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `ITravellersGearSynced.kt` | 36 | — | выпало (Travellers Gear: мод отсутствует на 1.20.1) |  |
| `TGHandlerBotaniaRenderer.kt` | 46 | — | выпало (Travellers Gear: мод отсутствует на 1.20.1) |  |
| `TravellerBaubleTooltipHandler.kt` | 35 | — | выпало (Travellers Gear: мод отсутствует на 1.20.1) |  |
| `TravellersGearAlfheimConfig.kt` | 15 | — | выпало (Travellers Gear: мод отсутствует на 1.20.1) |  |

### `legacy/src/main/java/alfheim/common/integration/waila/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `WAILAAlfheimConfig.kt` | 18 | КТ-10 | ждёт | WAILA → Jade |
| `WAILAHandlerAnyavil.kt` | 47 | КТ-10 | ждёт | WAILA → Jade |
| `WAILAHandlerManaAccelerator.kt` | 52 | КТ-10 | ждёт | WAILA → Jade |
| `WAILAHandlerTradePortal.kt` | 73 | КТ-10 | ждёт | WAILA → Jade |

### `legacy/src/main/java/alfheim/common/item/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `AlfheimItems.kt` | 399 | КТ-2 | ждёт | список предметов; предметы других КТ добавляются вместе с ними |
| `ItemArmilla.kt` | 182 | КТ-2 | ждёт |  |
| `ItemAstrolabe.kt` | 273 | КТ-4 | ждёт |  |
| `ItemCarver.kt` | 88 | КТ-3 | ждёт |  |
| `ItemChalk.kt` | 52 | КТ-2 | ждёт |  |
| `ItemColorSeeds.kt` | 258 | КТ-2 | ждёт |  |
| `ItemCorporeaRat.kt` | 104 | КТ-3 | ждёт |  |
| `ItemDeathSeed.kt` | 70 | КТ-2 | ждёт |  |
| `ItemElvenChakram.kt` | 54 | КТ-4 | ждёт |  |
| `ItemEnlighter.kt` | 80 | КТ-4 | ждёт |  |
| `ItemFenrirLoot.kt` | 268 | КТ-8 | ждёт |  |
| `ItemFireGrenade.kt` | 40 | КТ-2 | ждёт |  |
| `ItemFloatingIslandGenerator.kt` | 38 | КТ-6 | ждёт |  |
| `ItemHeadFlugel.kt` | 79 | КТ-8 | ждёт |  |
| `ItemHeadMiku.kt` | 76 | КТ-8 | ждёт |  |
| `ItemHyperBucket.kt` | 87 | КТ-2 | ждёт |  |
| `ItemIridescent.kt` | 92 | КТ-2 | ждёт |  |
| `ItemLensFlashInvisible.kt` | 187 | КТ-3 | ждёт |  |
| `ItemLootInterceptor.kt` | 96 | КТ-4 | ждёт |  |
| `ItemManaMirrorImba.kt` | 192 | КТ-4 | ждёт |  |
| `ItemManaStorage.kt` | 83 | КТ-4 | ждёт |  |
| `ItemMod.kt` | 33 | КТ-2 | ждёт |  |
| `ItemOrgans.kt` | 464 | КТ-8 | ждёт |  |
| `ItemPaperBreak.kt` | 65 | КТ-7 | ждёт |  |
| `ItemPaperRace.kt` | 123 | КТ-7 | ждёт |  |
| `ItemPeacePipe.kt` | 77 | КТ-7 | ждёт |  |
| `ItemSpawnEgg.kt` | 151 | КТ-5 | ждёт |  |
| `ItemSplashPotion.kt` | 108 | КТ-2 | ждёт |  |
| `ItemTriquetrum.kt` | 188 | КТ-2 | ждёт |  |
| `ItemWarBanner.kt` | 113 | КТ-4 | ждёт |  |
| `TheRodOfTheDebug.kt` | 80 | КТ-4 | ждёт |  |

### `legacy/src/main/java/alfheim/common/item/block/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `ItemBlockAnomaly.kt` | 81 | КТ-3 | ждёт |  |
| `ItemBlockAurora.kt` | 27 | КТ-2 | ждёт |  |
| `ItemBlockGrapeRed.kt` | 27 | КТ-2 | ждёт |  |
| `ItemBlockGrapeWhite.kt` | 45 | КТ-2 | ждёт |  |
| `ItemBlockItemFrame.kt` | 76 | КТ-3 | ждёт | WIP автора (`.WIP()`): переносится как есть, с меткой [WIP] (SPEC п. 6) |
| `ItemBlockLeavesMod.kt` | 24 | КТ-2 | перенесено | → `src/main/java/alfheim/common/item/block/ItemBlockLeavesMod.kt` |
| `ItemBlockManaReflector.kt` | 94 | КТ-3 | ждёт |  |
| `ItemBlockMetaSapling.kt` | 12 | КТ-2 | ждёт |  |
| `ItemBlockNidhoggTooth.kt` | 33 | КТ-2 | ждёт |  |
| `ItemBlockSubspacian.kt` | 34 | КТ-2 | ждёт |  |
| `ItemsGrassMod.kt` | 79 | КТ-2 | ждёт |  |
| `ItemsIridescentMod.kt` | 55 | КТ-2 | ждёт |  |
| `ItemsSlabMod.kt` | 41 | КТ-2 | ждёт |  |
| `ItemsSubtypeMod.kt` | 42 | КТ-2 | ждёт |  |
| `ItemStarPlacer.kt` | 103 | КТ-3 | ждёт |  |
| `ItemStarPlacer2.kt` | 104 | КТ-3 | ждёт |  |

### `legacy/src/main/java/alfheim/common/item/compat/thaumcraft/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `ItemAlfheimWandCap.kt` | 41 | — | выпало (Thaumcraft: мод отсутствует на 1.20.1) |  |
| `ItemAlfheimWandRod.kt` | 40 | — | выпало (Thaumcraft: мод отсутствует на 1.20.1) |  |
| `NaturalWandRodOnUpdate.kt` | 26 | — | выпало (Thaumcraft: мод отсутствует на 1.20.1) |  |
| `YggWandRodOnUpdate.kt` | 43 | — | выпало (Thaumcraft: мод отсутствует на 1.20.1) |  |

### `legacy/src/main/java/alfheim/common/item/compat/tinkersconstruct/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `ItemNaturalBucket.kt` | 140 | КТ-10 | ждёт | Tinkers' Construct 3 |
| `ItemNaturalManual.kt` | 107 | КТ-10 | ждёт | Tinkers' Construct 3 |
| `ItemNaturalMaterial.kt` | 32 | КТ-10 | ждёт | Tinkers' Construct 3 |

### `legacy/src/main/java/alfheim/common/item/creator/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `ItemRoyalStaff.kt` | 83 | КТ-4 | ждёт |  |
| `ItemTrisDagger.kt` | 205 | КТ-4 | ждёт |  |
| `ItemWireAxe.kt` | 248 | КТ-4 | ждёт |  |

### `legacy/src/main/java/alfheim/common/item/equipment/armor/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `ItemSnowArmor.kt` | 252 | КТ-4 | ждёт |  |
| `ItemVolcanoArmor.kt` | 193 | КТ-4 | ждёт |  |

### `legacy/src/main/java/alfheim/common/item/equipment/armor/elemental/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `ElementalArmor.kt` | 69 | КТ-4 | ждёт |  |
| `ItemElementalAirBoots.kt` | 64 | КТ-4 | ждёт |  |
| `ItemElementalEarthChest.kt` | 38 | КТ-4 | ждёт |  |
| `ItemElementalFireLeggings.kt` | 40 | КТ-4 | ждёт |  |
| `ItemElementalWaterHelm.kt` | 48 | КТ-4 | ждёт |  |

### `legacy/src/main/java/alfheim/common/item/equipment/armor/elvoruim/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `ItemElvoriumArmor.kt` | 149 | КТ-4 | ждёт |  |
| `ItemElvoriumHelmet.kt` | 161 | КТ-4 | ждёт |  |

### `legacy/src/main/java/alfheim/common/item/equipment/armor/fenrir/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `ItemFenrirArmor.kt` | 221 | КТ-4 | ждёт |  |
| `ItemFenrirBoots.kt` | 12 | КТ-4 | ждёт |  |
| `ItemFenrirHelmetRevealing.kt` | 68 | КТ-4 | ждёт |  |

### `legacy/src/main/java/alfheim/common/item/equipment/bauble/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `ItemAesirCloak.kt` | 36 | КТ-4 | ждёт |  |
| `ItemAesirEmblem.kt` | 128 | КТ-4 | ждёт |  |
| `ItemAttributionBauble.kt` | 153 | КТ-4 | ждёт |  |
| `ItemAuraRingAlfheim.kt` | 45 | КТ-4 | ждёт |  |
| `ItemBalanceCloak.kt` | 66 | КТ-4 | ждёт |  |
| `ItemBaubleCloak.kt` | 88 | КТ-4 | ждёт |  |
| `ItemBaubleGlove.kt` | 152 | КТ-4 | ждёт |  |
| `ItemCloudPendant.kt` | 44 | КТ-4 | ждёт |  |
| `ItemCoatOfArms.kt` | 124 | КТ-4 | ждёт |  |
| `ItemColorOverride.kt` | 100 | КТ-4 | ждёт |  |
| `ItemCreativeReachPendant.kt` | 52 | КТ-4 | ждёт |  |
| `ItemCrescentMoonAmulet.kt` | 53 | КТ-4 | ждёт |  |
| `ItemDodgeRing.kt` | 116 | КТ-4 | ждёт |  |
| `ItemElvenDisguise.kt` | 178 | КТ-4 | ждёт |  |
| `ItemFeedFlowerRing.kt` | 44 | КТ-4 | ждёт |  |
| `ItemFenrirCloak.kt` | 110 | КТ-4 | ждёт |  |
| `ItemFenrirGlove.kt` | 48 | КТ-4 | ждёт |  |
| `ItemGoddessCharm.kt` | 42 | КТ-4 | ждёт |  |
| `ItemInvisibilityCloak.kt` | 39 | КТ-4 | ждёт |  |
| `ItemManaStorageRing.kt` | 86 | КТ-4 | ждёт |  |
| `ItemManaweaveGlove.kt` | 61 | КТ-4 | ждёт |  |
| `ItemMultibauble.kt` | 50 | КТ-4 | ждёт |  |
| `ItemPendant.kt` | 68 | КТ-4 | ждёт |  |
| `ItemPriestCloak.kt` | 87 | КТ-4 | ждёт |  |
| `ItemPriestEmblem.kt` | 194 | КТ-4 | ждёт |  |
| `ItemRagnarokEmblemF.kt` | 74 | КТ-4 | ждёт |  |
| `ItemRationBelt.kt` | 59 | КТ-4 | ждёт |  |
| `ItemSerenade.kt` | 102 | КТ-4 | ждёт |  |
| `ItemSpatiotemporalRing.kt` | 55 | КТ-4 | ждёт |  |
| `ItemSpiderRing.kt` | 48 | КТ-4 | ждёт |  |
| `ItemSuperIcePendant.kt` | 33 | КТ-4 | ждёт |  |
| `ItemThinkingHand.kt` | 45 | КТ-4 | ждёт |  |
| `ItemToolBelt.kt` | 438 | КТ-4 | ждёт |  |

### `legacy/src/main/java/alfheim/common/item/equipment/bauble/faith/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `FaithHandlerHeimdall.kt` | 159 | КТ-4 | ждёт |  |
| `FaithHandlerLoki.kt` | 157 | КТ-4 | ждёт |  |
| `FaithHandlerNjord.kt` | 177 | КТ-4 | ждёт |  |
| `FaithHandlerOdin.kt` | 156 | КТ-4 | ждёт |  |
| `FaithHandlerSif.kt` | 191 | КТ-4 | ждёт |  |
| `FaithHandlerThor.kt` | 153 | КТ-4 | ждёт |  |
| `IFaithHandler.kt` | 58 | КТ-4 | ждёт |  |
| `ItemRagnarokEmblem.kt` | 610 | КТ-4 | ждёт |  |

### `legacy/src/main/java/alfheim/common/item/equipment/tool/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `ItemFenrirClaws.kt` | 121 | КТ-4 | ждёт |  |
| `ItemGaiaSlayer.kt` | 43 | КТ-4 | ждёт |  |
| `ItemLivingrockPickaxe.kt` | 20 | КТ-4 | ждёт |  |
| `ItemRealitySword.kt` | 225 | КТ-4 | ждёт |  |
| `ItemResonator.kt` | 167 | КТ-4 | ждёт |  |
| `ItemSnowSword.kt` | 60 | КТ-4 | ждёт |  |
| `ItemSoulSword.kt` | 188 | КТ-4 | ждёт |  |
| `ItemSurtrSword.kt` | 57 | КТ-4 | ждёт |  |
| `ItemThrymAxe.kt` | 58 | КТ-4 | ждёт |  |
| `ItemVolcanoMace.kt` | 77 | КТ-4 | ждёт |  |

### `legacy/src/main/java/alfheim/common/item/equipment/tool/manasteel/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `ItemManasteelHoe.kt` | 123 | КТ-4 | ждёт |  |

### `legacy/src/main/java/alfheim/common/item/equipment/tool/rift/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `ItemRiftPick.kt` | 20 | КТ-4 | ждёт | WIP автора (`.WIP()`): переносится как есть, с меткой [WIP] (SPEC п. 6) |
| `ItemRiftSword.kt` | 29 | КТ-4 | ждёт | WIP автора (`.WIP()`): переносится как есть, с меткой [WIP] (SPEC п. 6) |

### `legacy/src/main/java/alfheim/common/item/equipment/tool/terrasteel/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `ItemTerraHoe.kt` | 317 | КТ-4 | ждёт | часть с грядками IC2 выпадает (SPEC п. 7); у автора без IC2 она не работает |

### `legacy/src/main/java/alfheim/common/item/interaction/thaumcraft/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `ItemElementalWaterHelmRevealing.kt` | 45 | — | выпало (Thaumcraft: мод отсутствует на 1.20.1) |  |
| `ItemElvoriumHelmetRevealing.kt` | 36 | — | выпало (Thaumcraft: мод отсутствует на 1.20.1) |  |
| `ItemSnowHelmetRevealing.kt` | 46 | — | выпало (Thaumcraft: мод отсутствует на 1.20.1) |  |
| `ItemVolcanoHelmetRevealing.kt` | 46 | — | выпало (Thaumcraft: мод отсутствует на 1.20.1) |  |

### `legacy/src/main/java/alfheim/common/item/lens/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `LensDaisy.kt` | 94 | КТ-3 | ждёт |  |
| `LensLinkback.kt` | 45 | КТ-3 | ждёт |  |
| `LensMessenger.kt` | 14 | КТ-3 | ждёт |  |
| `LensPush.kt` | 38 | КТ-3 | ждёт |  |
| `LensSmelt.kt` | 75 | КТ-3 | ждёт |  |
| `LensSuperconductor.kt` | 45 | КТ-3 | ждёт |  |
| `LensTrack.kt` | 35 | КТ-3 | ждёт |  |
| `LensTripwire.kt` | 42 | КТ-3 | ждёт |  |
| `LensUnlink.kt` | 29 | КТ-3 | ждёт |  |

### `legacy/src/main/java/alfheim/common/item/material/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `ItemElvenFood.kt` | 213 | КТ-2 | ждёт |  |
| `ItemElvenResource.kt` | 504 | КТ-2 | ждёт |  |
| `ItemEventResource.kt` | 76 | КТ-2 | ждёт |  |
| `ItemSoulHorn.kt` | 27 | КТ-2 | ждёт |  |
| `ItemStoryToken.kt` | 54 | КТ-2 | ждёт |  |
| `ItemWiltedLotus.kt` | 80 | КТ-2 | ждёт |  |

### `legacy/src/main/java/alfheim/common/item/relic/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `ItemAkashicRecords.kt` | 291 | КТ-4 | ждёт |  |
| `ItemDaolos.kt` | 239 | КТ-4 | ждёт |  |
| `ItemExcaliber.kt` | 153 | КТ-4 | ждёт |  |
| `ItemFlugelSoul.kt` | 477 | КТ-4 | ждёт |  |
| `ItemGjallarhorn.kt` | 62 | КТ-4 | ждёт |  |
| `ItemGleipnir.kt` | 387 | КТ-4 | ждёт |  |
| `ItemGungnir.kt` | 101 | КТ-4 | ждёт |  |
| `ItemHeimdallRing.kt` | 97 | КТ-4 | ждёт |  |
| `ItemMjolnir.kt` | 251 | КТ-4 | ждёт |  |
| `ItemMoonlightBow.kt` | 428 | КТ-4 | ждёт |  |
| `ItemNjordRing.kt` | 93 | КТ-4 | ждёт |  |
| `ItemSifRing.kt` | 97 | КТ-4 | ждёт |  |
| `ItemSpearSubspace.kt` | 180 | КТ-4 | ждёт |  |
| `ItemTankMask.kt` | 269 | КТ-4 | ждёт | ветка с Лимбом Dimensional Doors выпадает (SPEC п. 7); у автора без DimDoors маска всегда отправляет в Хельхейм |

### `legacy/src/main/java/alfheim/common/item/rod/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `ItemRedstoneRod.kt` | 364 | КТ-4 | ждёт |  |
| `ItemRodBlackHole.kt` | 136 | КТ-4 | ждёт |  |
| `ItemRodClicker.kt` | 482 | КТ-4 | ждёт |  |
| `ItemRodElemental.kt` | 96 | КТ-4 | ждёт |  |
| `ItemRodFlameStar.kt` | 132 | КТ-4 | ждёт |  |
| `ItemRodGrass.kt` | 77 | КТ-4 | ждёт |  |
| `ItemRodInterdiction.kt` | 182 | КТ-4 | ждёт |  |
| `ItemRodIridescent.kt` | 197 | КТ-4 | ждёт |  |
| `ItemRodLightning.kt` | 317 | КТ-4 | ждёт |  |
| `ItemRodPortal.kt` | 145 | КТ-4 | ждёт |  |
| `ItemRodPrismatic.kt` | 91 | КТ-4 | ждёт |  |
| `ItemRodSuperExchange.kt` | 384 | КТ-4 | ждёт |  |

### `legacy/src/main/java/alfheim/common/lexicon/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `AlfheimAbyssalLexiconEntry.kt` | 32 | КТ-9 | ждёт |  |
| `AlfheimLexiconCategory.kt` | 15 | КТ-9 | ждёт |  |
| `AlfheimLexiconData.kt` | 1609 | КТ-9 | ждёт |  |
| `AlfheimLexiconEntry.kt` | 49 | КТ-9 | ждёт |  |
| `AlfheimRelicLexiconEntry.kt` | 36 | КТ-9 | ждёт |  |
| `MultiblockComponentRainbow.kt` | 45 | КТ-9 | ждёт |  |

### `legacy/src/main/java/alfheim/common/lexicon/page/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `PageFurnaceRecipe.kt` | 92 | КТ-9 | ждёт |  |
| `PageManaInfuserRecipe.kt` | 92 | КТ-9 | ждёт |  |
| `PageMultiblockHidden.kt` | 37 | КТ-9 | ждёт |  |
| `PageMultiblockLearnable.kt` | 140 | КТ-9 | ждёт |  |
| `PagePureDaisyRecipe.kt` | 70 | КТ-9 | ждёт |  |
| `PageSpell.kt` | 97 | КТ-9 | ждёт |  |
| `PageTextConditional.kt` | 9 | КТ-9 | ждёт |  |
| `PageTextLearnableAchievement.kt` | 6 | КТ-9 | ждёт |  |
| `PageTextLearnableKnowledge.kt` | 7 | КТ-9 | ждёт |  |
| `PageTreeCrafting.kt` | 57 | КТ-9 | ждёт |  |
| `PageTunerCodes.kt` | 46 | КТ-9 | ждёт |  |
| `PageTuningIORecipe.kt` | 5 | КТ-9 | ждёт |  |
| `PageTuningRecipe.kt` | 115 | КТ-9 | ждёт |  |

### `legacy/src/main/java/alfheim/common/network/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `Data.kt` | 15 | КТ-1 | перенесено | → `src/main/java/alfheim/common/network/Data.kt` |
| `NetworkService.kt` | 118 | КТ-1 | перенесено | → `src/main/java/alfheim/common/network/NetworkService.kt` |

### `legacy/src/main/java/alfheim/common/network/packet/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `Message0dC.kt` | 23 | КТ-1 | перенесено | → `src/main/java/alfheim/common/network/packet/Message0dC.kt`; ветки обработки по КТ механик закомментированы |
| `Message0dS.kt` | 93 | КТ-1 | перенесено | → `src/main/java/alfheim/common/network/packet/Message0dS.kt`; ветки обработки по КТ механик закомментированы |
| `Message1d.kt` | 80 | КТ-1 | перенесено | → `src/main/java/alfheim/common/network/packet/Message1d.kt`; ветки обработки по КТ механик закомментированы |
| `Message1l.kt` | 14 | КТ-1 | перенесено | → `src/main/java/alfheim/common/network/packet/Message1l.kt`; ветка SEED — КТ-5 |
| `Message2d.kt` | 70 | КТ-1 | перенесено | → `src/main/java/alfheim/common/network/packet/Message2d.kt`; ветки обработки по КТ механик закомментированы |
| `Message3d.kt` | 42 | КТ-1 | перенесено | → `src/main/java/alfheim/common/network/packet/Message3d.kt`; ветки обработки по КТ механик закомментированы |
| `MessageContributor.kt` | 80 | КТ-1 | перенесено | → `src/main/java/alfheim/common/network/packet/MessageContributor.kt` |
| `MessageCorporeaRequest.kt` | 22 | КТ-3 | ждёт | канал — КТ-1, пакет — вместе с механикой |
| `MessageDisguise.kt` | 55 | КТ-4 | ждёт | канал — КТ-1, пакет — вместе с механикой |
| `MessageEffect.kt` | 55 | КТ-2 | ждёт | канал — КТ-1, пакет — вместе с механикой |
| `MessageFIBlock.kt` | 21 | КТ-6 | ждёт | канал — КТ-1, пакет — вместе с механикой |
| `MessageFuckedUpServerPrecision.kt` | 18 | КТ-3 | ждёт | канал — КТ-1, пакет — вместе с механикой |
| `MessageGleipnirLeash.kt` | 22 | КТ-4 | ждёт | канал — КТ-1, пакет — вместе с механикой |
| `MessageHotSpellC.kt` | 20 | КТ-7 | ждёт | канал — КТ-1, пакет — вместе с механикой |
| `MessageHotSpellS.kt` | 12 | КТ-7 | ждёт | канал — КТ-1, пакет — вместе с механикой |
| `MessageKeyBindS.kt` | 24 | КТ-7 | ждёт | канал — КТ-1, пакет — вместе с механикой |
| `MessageNI.kt` | 69 | КТ-1 | перенесено | → `src/main/java/alfheim/common/network/packet/MessageNI.kt`; BLIZZARD — КТ-8, HEARTLOSS — КТ-7 |
| `MessageOrgans.kt` | 18 | КТ-8 | ждёт | канал — КТ-1, пакет — вместе с механикой |
| `MessageParty.kt` | 22 | КТ-7 | ждёт | канал — КТ-1, пакет — вместе с механикой |
| `MessageRaceInfo.kt` | 11 | КТ-7 | ждёт | канал — КТ-1, пакет — вместе с механикой |
| `MessageRaceSelection.kt` | 33 | КТ-7 | ждёт | канал — КТ-1, пакет — вместе с механикой |
| `MessageRedstoneSignalsSync.kt` | 26 | КТ-4 | ждёт | канал — КТ-1, пакет — вместе с механикой |
| `MessageRelicNBTSync.kt` | 32 | КТ-4 | ждёт | канал — КТ-1, пакет — вместе с механикой |
| `MessageSkinInfo.kt` | 11 | КТ-7 | ждёт | канал — КТ-1, пакет — вместе с механикой |
| `MessageSpellParams.kt` | 15 | КТ-7 | ждёт | канал — КТ-1, пакет — вместе с механикой |
| `MessageTileItem.kt` | 15 | КТ-3 | ждёт | канал — КТ-1, пакет — вместе с механикой |
| `MessageTimeStop.kt` | 23 | КТ-7 | ждёт | канал — КТ-1, пакет — вместе с механикой |
| `MessageUpdateGaiaButton.kt` | 15 | КТ-3 | ждёт | канал — КТ-1, пакет — вместе с механикой |
| `MessageVisualEffect.kt` | 21 | КТ-1 | перенесено | → `src/main/java/alfheim/common/network/packet/MessageVisualEffect.kt` |

### `legacy/src/main/java/alfheim/common/potion/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `PotionAlfheim.kt` | 40 | КТ-2 | ждёт |  |
| `PotionBeastWithin.kt` | 23 | КТ-2 | ждёт |  |
| `PotionBeer.kt` | 44 | КТ-2 | ждёт |  |
| `PotionBerserk.kt` | 24 | КТ-2 | ждёт |  |
| `PotionBleeding.kt` | 24 | КТ-2 | ждёт |  |
| `PotionButterShield.kt` | 56 | КТ-2 | ждёт |  |
| `PotionChampagne.kt` | 20 | КТ-2 | ждёт |  |
| `PotionDeathMark.kt` | 15 | КТ-2 | ждёт |  |
| `PotionEdgeLife.kt` | 18 | КТ-2 | ждёт |  |
| `PotionEternity.kt` | 103 | КТ-2 | ждёт |  |
| `PotionGoldRush.kt` | 19 | КТ-2 | ждёт |  |
| `PotionHystrix.kt` | 30 | КТ-2 | ждёт |  |
| `PotionIceLens.kt` | 25 | КТ-2 | ждёт |  |
| `PotionLeftFlame.kt` | 109 | КТ-2 | ждёт |  |
| `PotionLightningShield.kt` | 46 | КТ-2 | ждёт |  |
| `PotionManaVoid.kt` | 77 | КТ-2 | ждёт |  |
| `PotionNinja.kt` | 24 | КТ-2 | ждёт |  |
| `PotionNoclip.kt` | 82 | КТ-2 | ждёт |  |
| `PotionPriorityTarget.kt` | 121 | КТ-2 | ждёт |  |
| `PotionQuadDamage.kt` | 83 | КТ-2 | ждёт |  |
| `PotionSacrifice.kt` | 50 | КТ-2 | ждёт |  |
| `PotionShowMana.kt` | 33 | КТ-2 | ждёт |  |
| `PotionSoulburn.kt` | 67 | КТ-2 | ждёт |  |
| `PotionTank.kt` | 24 | КТ-2 | ждёт |  |
| `PotionThrow.kt` | 33 | КТ-2 | ждёт |  |
| `PotionTimeAnchor.kt` | 51 | КТ-2 | ждёт |  |
| `PotionTimeConquest.kt` | 36 | КТ-2 | ждёт |  |
| `PotionVoodooDoll.kt` | 56 | КТ-2 | ждёт |  |
| `PotionVoodooTarget.kt` | 33 | КТ-2 | ждёт |  |
| `PotionWellOLife.kt` | 15 | КТ-2 | ждёт |  |
| `PotionWhiteWine.kt` | 29 | КТ-2 | ждёт |  |

### `legacy/src/main/java/alfheim/common/potion/berries/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `PotionWTFBerry0.kt` | 36 | КТ-2 | ждёт |  |
| `PotionWTFBerry2.kt` | 22 | КТ-2 | ждёт |  |
| `PotionWTFBerry3.kt` | 22 | КТ-2 | ждёт |  |
| `PotionWTFBerry4.kt` | 30 | КТ-2 | ждёт |  |
| `PotionWTFBerry5.kt` | 32 | КТ-2 | ждёт |  |

### `legacy/src/main/java/alfheim/common/spell/darkness/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `SpellAcidMyst.kt` | 44 | КТ-7 | ждёт |  |
| `SpellDeathMark.kt` | 36 | КТ-7 | ждёт |  |
| `SpellDecay.kt` | 34 | КТ-7 | ждёт |  |
| `SpellNight.kt` | 24 | КТ-7 | ждёт |  |
| `SpellPoisonRoots.kt` | 74 | КТ-7 | ждёт |  |
| `SpellSacrifice.kt` | 31 | КТ-7 | ждёт |  |
| `SpellSwap.kt` | 62 | КТ-7 | ждёт |  |
| `SpellVoodooDoll.kt` | 34 | КТ-7 | ждёт |  |
| `SpellVoodooTarget.kt` | 33 | КТ-7 | ждёт |  |

### `legacy/src/main/java/alfheim/common/spell/earth/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `SpellGoldRush.kt` | 37 | КТ-7 | ждёт |  |
| `SpellHammerfall.kt` | 70 | КТ-7 | ждёт |  |
| `SpellMortar.kt` | 24 | КТ-7 | ждёт |  |
| `SpellNoclip.kt` | 33 | КТ-7 | ждёт |  |
| `SpellStoneSkin.kt` | 37 | КТ-7 | ждёт |  |
| `SpellTitanHit.kt` | 155 | КТ-7 | ждёт |  |
| `SpellWallWarp.kt` | 85 | КТ-7 | ждёт |  |

### `legacy/src/main/java/alfheim/common/spell/fire/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `SpellDispel.kt` | 40 | КТ-7 | ждёт |  |
| `SpellFireball.kt` | 40 | КТ-7 | ждёт |  |
| `SpellFirestar.kt` | 59 | КТ-7 | ждёт |  |
| `SpellFirewall.kt` | 51 | КТ-7 | ждёт |  |
| `SpellIgnition.kt` | 68 | КТ-7 | ждёт |  |
| `SpellSun.kt` | 31 | КТ-7 | ждёт |  |

### `legacy/src/main/java/alfheim/common/spell/illusion/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `SpellConfusion.kt` | 34 | КТ-7 | ждёт |  |
| `SpellDarkness.kt` | 26 | КТ-7 | ждёт |  |
| `SpellHollowBody.kt` | 36 | КТ-7 | ждёт |  |
| `SpellJoin.kt` | 41 | КТ-7 | ждёт |  |
| `SpellNightVision.kt` | 37 | КТ-7 | ждёт |  |
| `SpellShadowVortex.kt` | 38 | КТ-7 | ждёт |  |
| `SpellSmokeScreen.kt` | 33 | КТ-7 | ждёт |  |
| `SpellTrueSight.kt` | 36 | КТ-7 | ждёт |  |

### `legacy/src/main/java/alfheim/common/spell/nature/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `SpellAport.kt` | 59 | КТ-7 | ждёт |  |
| `SpellBeastWithin.kt` | 28 | КТ-7 | ждёт |  |
| `SpellButterflyShield.kt` | 32 | КТ-7 | ждёт |  |
| `SpellCall.kt` | 51 | КТ-7 | ждёт |  |
| `SpellDay.kt` | 24 | КТ-7 | ждёт |  |
| `SpellEdgeLife.kt` | 27 | КТ-7 | ждёт |  |
| `SpellHystrix.kt` | 32 | КТ-7 | ждёт |  |
| `SpellNineLives.kt` | 34 | КТ-7 | ждёт |  |
| `SpellUphealth.kt` | 37 | КТ-7 | ждёт |  |
| `SpellWarhood.kt` | 31 | КТ-7 | ждёт |  |

### `legacy/src/main/java/alfheim/common/spell/sound/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `SpellBattleHorn.kt` | 40 | КТ-7 | ждёт |  |
| `SpellDragonGrowl.kt` | 39 | КТ-7 | ждёт |  |
| `SpellEcho.kt` | 40 | КТ-7 | ждёт |  |
| `SpellHarp.kt` | 46 | КТ-7 | ждёт |  |
| `SpellIsaacStorm.kt` | 53 | КТ-7 | ждёт |  |
| `SpellNoteshot.kt` | 22 | КТ-7 | ждёт |  |
| `SpellOutdare.kt` | 40 | КТ-7 | ждёт |  |
| `SpellPriorityTarget.kt` | 44 | КТ-7 | ждёт |  |
| `SpellWhisper.kt` | 36 | КТ-7 | ждёт |  |

### `legacy/src/main/java/alfheim/common/spell/tech/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `SpellBlink.kt` | 104 | КТ-7 | ждёт |  |
| `SpellDriftingMine.kt` | 24 | КТ-7 | ждёт |  |
| `SpellGravityTrap.kt` | 55 | КТ-7 | ждёт |  |
| `SpellRepair.kt` | 29 | КТ-7 | ждёт |  |
| `SpellTimeAnchor.kt` | 31 | КТ-7 | ждёт |  |
| `SpellTimeConquest.kt` | 30 | КТ-7 | ждёт |  |
| `SpellTimeStop.kt` | 25 | КТ-7 | ждёт |  |

### `legacy/src/main/java/alfheim/common/spell/water/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `SpellAquaBind.kt` | 73 | КТ-7 | ждёт |  |
| `SpellAquaStream.kt` | 21 | КТ-7 | ждёт |  |
| `SpellHealing.kt` | 36 | КТ-7 | ждёт |  |
| `SpellIceLens.kt` | 25 | КТ-7 | ждёт |  |
| `SpellLiquification.kt` | 67 | КТ-7 | ждёт |  |
| `SpellPurifyingSurface.kt` | 60 | КТ-7 | ждёт |  |
| `SpellRain.kt` | 32 | КТ-7 | ждёт |  |
| `SpellResurrect.kt` | 39 | КТ-7 | ждёт |  |
| `SpellWellOLife.kt` | 36 | КТ-7 | ждёт |  |

### `legacy/src/main/java/alfheim/common/spell/wind/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `SpellBunnyHop.kt` | 35 | КТ-7 | ждёт |  |
| `SpellFenrirStorm.kt` | 22 | КТ-7 | ждёт |  |
| `SpellLeafStorm.kt` | 24 | КТ-7 | ждёт |  |
| `SpellThor.kt` | 53 | КТ-7 | ждёт |  |
| `SpellThrow.kt` | 29 | КТ-7 | ждёт |  |
| `SpellThunder.kt` | 31 | КТ-7 | ждёт |  |
| `SpellWaterBreathing.kt` | 37 | КТ-7 | ждёт |  |
| `SpellWindBlades.kt` | 27 | КТ-7 | ждёт |  |

### `legacy/src/main/java/alfheim/common/world/data/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `CustomWorldData.kt` | 136 | КТ-6 | ждёт |  |

### `legacy/src/main/java/alfheim/common/world/dim/alfheim/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `WorldProviderAlfheim.kt` | 137 | КТ-6 | ждёт |  |

### `legacy/src/main/java/alfheim/common/world/dim/alfheim/biome/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `BiomeAlfheim.kt` | 75 | КТ-6 | ждёт |  |
| `BiomeBeach.kt` | 19 | КТ-6 | ждёт |  |
| `BiomeField.kt` | 36 | КТ-6 | ждёт |  |
| `BiomeIslandForest.kt` | 40 | КТ-6 | ждёт |  |
| `BiomeMountHigh.kt` | 26 | КТ-6 | ждёт |  |
| `BiomeMountLow.kt` | 36 | КТ-6 | ждёт |  |
| `BiomeMountMid.kt` | 36 | КТ-6 | ждёт |  |
| `BiomeMountTopField.kt` | 25 | КТ-6 | ждёт |  |
| `BiomeMountTopForest.kt` | 38 | КТ-6 | ждёт |  |
| `BiomePitForest.kt` | 37 | КТ-6 | ждёт |  |
| `BiomePitGiantFlowers.kt` | 28 | КТ-6 | ждёт |  |
| `BiomeRiver.kt` | 22 | КТ-6 | ждёт |  |
| `BiomeSandbank.kt` | 19 | КТ-6 | ждёт |  |

### `legacy/src/main/java/alfheim/common/world/dim/alfheim/customgens/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `AlfheimLakeGen.kt` | 87 | КТ-6 | ждёт |  |
| `NiflheimLocationGenerator.kt` | 156 | КТ-6 | ждёт |  |
| `WorldGenAlfheim.kt` | 93 | КТ-6 | ждёт |  |
| `WorldGenAlfheimThaumcraft.kt` | 126 | КТ-6 | ждёт |  |
| `WorldGenGrapesWhiteAlfheim.kt` | 46 | КТ-6 | ждёт |  |
| `WorldGenGrass.kt` | 141 | КТ-6 | ждёт |  |
| `WorldGenIridescence.kt` | 58 | КТ-6 | ждёт |  |
| `WorldGenMelonPumpkins.kt` | 39 | КТ-6 | ждёт |  |
| `WorldGenMutatedFlowers.kt` | 55 | КТ-6 | ждёт |  |
| `WorldGenReedAlfheim.kt` | 31 | КТ-6 | ждёт |  |
| `YggdrasilGenerator.kt` | 89 | КТ-6 | ждёт |  |

### `legacy/src/main/java/alfheim/common/world/dim/alfheim/structure/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `StructureArena.kt` | 87 | КТ-6 | ждёт |  |
| `StructureDreamsTree.kt` | 574 | КТ-6 | ждёт |  |
| `StructurePortalToNiflheim.kt` | 18 | КТ-6 | ждёт |  |
| `StructureShrine.kt` | 49 | КТ-6 | ждёт |  |
| `StructureSpawnpoint.kt` | 91 | КТ-6 | ждёт |  |

### `legacy/src/main/java/alfheim/common/world/dim/domains/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `ChunkProviderDomains.kt` | 43 | КТ-6 | ждёт |  |
| `WorldChunkManagerDomains.kt` | 18 | КТ-6 | ждёт |  |
| `WorldProviderDomains.kt` | 239 | КТ-6 | ждёт |  |

### `legacy/src/main/java/alfheim/common/world/dim/domains/gen/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `FenrirDomain.kt` | 41 | КТ-6 | ждёт |  |
| `SurtrDomain.kt` | 83 | КТ-6 | ждёт |  |
| `ThrymDomain.kt` | 34 | КТ-6 | ждёт |  |

### `legacy/src/main/java/alfheim/common/world/dim/helheim/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `BiomeHelheim.kt` | 35 | КТ-6 | ждёт |  |
| `WorldProviderHelheim.kt` | 230 | КТ-6 | ждёт |  |

### `legacy/src/main/java/alfheim/common/world/dim/helheim/gen/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `WorldGenHelheim.kt` | 88 | КТ-6 | ждёт |  |

### `legacy/src/main/java/alfheim/common/world/dim/niflheim/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `ChunkManagerNiflheim.kt` | 141 | КТ-6 | ждёт |  |
| `ChunkProviderNiflheim.kt` | 691 | КТ-6 | ждёт |  |
| `WorldProviderNiflheim.kt` | 207 | КТ-6 | ждёт |  |

### `legacy/src/main/java/alfheim/common/world/dim/niflheim/biome/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `BiomeNiflheim.kt` | 41 | КТ-6 | ждёт |  |

### `legacy/src/main/java/alfheim/common/world/dim/niflheim/customgens/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `MapGenCustomCaves.kt` | 219 | КТ-6 | ждёт |  |
| `MapGenCustomRavine.kt` | 175 | КТ-6 | ждёт |  |
| `WorldGenCaveHangs.kt` | 27 | КТ-6 | ждёт |  |
| `WorldGenGigaHangs.kt` | 43 | КТ-6 | ждёт |  |
| `WorldGenGigaRoot.kt` | 23 | КТ-6 | ждёт |  |
| `WorldGenHole.kt` | 125 | КТ-6 | ждёт |  |
| `WorldGenIcePikes.kt` | 88 | КТ-6 | ждёт |  |
| `WorldGenIcyGeyser.kt` | 16 | КТ-6 | ждёт |  |
| `WorldGenIglu.kt` | 283 | КТ-6 | ждёт |  |
| `WorldGenLakes.kt` | 169 | КТ-6 | ждёт |  |
| `WorldGenNifleur.kt` | 14 | КТ-6 | ждёт |  |
| `WorldGenRibs.kt` | 41 | КТ-6 | ждёт |  |
| `WorldGenRoot.kt` | 60 | КТ-6 | ждёт |  |
| `WorldGenTentacles.kt` | 21 | КТ-6 | ждёт |  |
| `WorldGenWaterfall.kt` | 89 | КТ-6 | ждёт |  |

### `legacy/src/main/java/alfheim/common/world/dim/niflheim/structure/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `StructureGenChest.kt` | 32 | КТ-6 | ждёт |  |
| `StructureGenSpawner.kt` | 64 | КТ-6 | ждёт |  |
| `WorldGenBigDungeons.kt` | 78 | КТ-6 | ждёт |  |
| `WorldGenBridge.kt` | 146 | КТ-6 | ждёт |  |
| `WorldGenDungeons.kt` | 87 | КТ-6 | ждёт |  |
| `WorldGenRuins.kt` | 610 | КТ-6 | ждёт |  |

### `legacy/src/main/java/alfheim/common/world/gen/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `HeartWoodTreeGen.kt` | 94 | КТ-6 | ждёт |  |
| `SimpleTreeGen.kt` | 98 | КТ-6 | ждёт |  |

### `legacy/src/main/java/alfheim/common/world/mobspawn/`

| Файл | Строк | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `MobSpawnHandler.kt` | 284 | КТ-6 | ждёт |  |

## Ресурсы

Папки `legacy/src/main/resources/assets/`. «Только файлы папки» — без вложенных папок,
у которых своя строка. Сумма по таблице = числу файлов в `assets` на точке отсчёта.

| Папка | Файлов | КТ | Статус | Примечание |
|---|---:|---|---|---|
| `alfheim/dialogs/common/` | 1 | — | WIP — стадия 2 | диалоги эльфов; вызов диалогов отключён автором (SPEC п. 6) |
| `alfheim/lang/` | 3 | КТ-1 | перенесено | `.lang` остаются здесь, `src/main/resources/assets/alfheim/lang/*.json` собирает `tools/convert_lang.py` |
| `alfheim/loot/` | 3 | КТ-6 | ждёт | награды лутбоксов летающих островов (`BlockLootbox`) |
| `alfheim/model/` (только файлы папки) | 37 | по владельцу | ждёт | OBJ-модели; каждая — в КТ своего блока, предмета или существа (SPEC Р-13) |
| `alfheim/model/unused/` | 1 | — | WIP — стадия 2 | `SpireOld.obj`, в коде не используется |
| `alfheim/schemas/` | 767 | КТ-6 | ждёт | постройки (вместе с `fi/`, `flowers/`, `niflheim/`, `yggdrasil/`), без изменений (SPEC Р-10) |
| `alfheim/shaders/` | 11 | по владельцу | ждёт | шейдеры автора; каждый — в КТ эффекта, который его использует (SPEC Р-13) |
| `alfheim/sounds/` (только файлы папки) | 14 | КТ-1 | перенесено | → `src/main/resources/assets/alfheim/sounds/`; `resonatorBlast.ogg`, `resonatorFire.ogg` → `resonator_blast.ogg`, `resonator_fire.ogg` (путь — строчными) |
| `alfheim/sounds/fenrir/` | 4 | КТ-1 | перенесено | Фенрир; все звуки — вместе с `sounds.json` |
| `alfheim/sounds/horn/` | 3 | КТ-1 | перенесено | Гьяллархорн |
| `alfheim/sounds/organs/` | 4 | КТ-1 | перенесено | Флюгель |
| `alfheim/sounds/surtr/` | 18 | КТ-1 | перенесено | Сурт |
| `alfheim/sounds/thrym/` | 15 | КТ-1 | перенесено | Трим |
| `alfheim/textures/` (только файлы папки) | 2 | КТ-2 | ждёт | `rainbow.png` с анимацией |
| `alfheim/textures/banner/` | 2 | КТ-8 | ждёт | баннеры сезонных событий |
| `alfheim/textures/blocks/` (только файлы папки) | 386 | КТ-2 | ждёт | текстуры блоков других КТ переносятся вместе с блоком; перенесено 17 — вместе со своими блоками, имена в snake_case (`tools/move_legacy.py`); `alfStorage6.png` автор не использовал (вариантов у `BlockAlfStorage` 6: 0–5) |
| `alfheim/textures/blocks/decor/` | 83 | КТ-2 | ждёт | перенесено 5 (`ElvenSandstone*`) |
| `alfheim/textures/blocks/snake/` | 40 | КТ-2 | ждёт |  |
| `alfheim/textures/blocks/unused/` | 27 | — | WIP — стадия 2 | папка автора `unused`, в игре не используется |
| `alfheim/textures/environment/` | 1 | КТ-6 | ждёт | небо миров |
| `alfheim/textures/gui/` (только файлы папки) | 13 | по владельцу | ждёт | интерфейс: HUD рас и заклинаний — КТ-7, оверлеи блоков — КТ-3 |
| `alfheim/textures/gui/categories/` | 7 | КТ-9 | ждёт | лексикон |
| `alfheim/textures/gui/entries/` | 8 | КТ-9 | ждёт | лексикон |
| `alfheim/textures/gui/spells/` | 85 | КТ-7 | ждёт | иконки заклинаний |
| `alfheim/textures/items/` (только файлы папки) | 182 | КТ-2 | ждёт | текстуры предметов других КТ переносятся вместе с предметом |
| `alfheim/textures/items/coatofarms/` | 19 | КТ-4 | ждёт | `ItemCoatOfArms` |
| `alfheim/textures/items/materials/` | 72 | КТ-2 | ждёт |  |
| `alfheim/textures/items/misc/` | 17 | КТ-2 | ждёт |  |
| `alfheim/textures/items/unused/` | 23 | — | WIP — стадия 2 | папка автора `unused`, в игре не используется |
| `alfheim/textures/misc/` (только файлы папки) | 19 | по владельцу | ждёт | эффекты и оверлеи; каждый — в КТ своей механики |
| `alfheim/textures/misc/icons/` | 14 | КТ-7 | ждёт | иконки рас в HUD группы |
| `alfheim/textures/misc/particles/` | 3 | КТ-2 | ждёт | частицы |
| `alfheim/textures/model/armor/` | 43 | КТ-4 | ждёт | броня |
| `alfheim/textures/model/avatar/` | 4 | КТ-3 | ждёт | жезлы в аватаре Botania |
| `alfheim/textures/model/block/` | 56 | КТ-3 | ждёт | модели блоков с блок-сущностью |
| `alfheim/textures/model/entity/` | 116 | КТ-5 | ждёт | существа; боссы — КТ-8, крылья и облики рас — КТ-7 |
| `alfheim/textures/model/item/` | 10 | КТ-4 | ждёт | модели оружия |
| `botania/lang/` | 3 | КТ-1 | перенесено | строки лексикона Alfheim в пространстве имён Botania; `.json` собирает `tools/convert_lang.py` |
| `botania/sounds/music/` | 6 | КТ-8 | ждёт | 6 треков без указания происхождения: решение владельца в КТ-10 (SPEC п. 8) |
| `botania/textures/blocks/` | 24 | КТ-3 | ждёт | световые реле, кварц, `alt/` — расширители Botania |
| `botania/textures/gui/` | 1 | КТ-9 | ждёт | категория лексикона |
| `botania/textures/items/` | 72 | по владельцу | ждёт | аксессуары и реликвии — КТ-4, линзы — КТ-3 |
| `etfuturum/textures/model/` | 1 | — | выпало (Et Futurum: мод отсутствует на 1.20.1) |  |
| `minecraft/shaders/` | 4 | КТ-7 | ждёт | пост-шейдер `depth`; прямых ссылок в коде не найдено — уточнить при переносе |
| `minecraft/textures/blocks/` | 6 | КТ-3 | ждёт | печь из живого камня (`FurnaceExtender`), кварц |
| `minecraft/textures/entity/banner/` | 18 | — | выпало (Et Futurum: мод отсутствует на 1.20.1) | узоры знамён для Et Futurum (`EFHandlerBanners`) |
| `minecraft/textures/gui/` | 4 | КТ-1 | перенесено | `tab_Alfheim.png` → `src/main/resources/assets/alfheim/textures/gui/container/creative_inventory/tab_alfheim.png`; `tab_Alfheim_Alternate.png`, `tab_AlfheimModular.png` код автора не использует — остаются здесь; `tab_NTC.png` — вкладка Thaumcraft, выпадает |
| `thaumcraft/` | 29 | — | выпало (Thaumcraft: мод отсутствует на 1.20.1) |  |
| `tinker/` | 61 | КТ-10 | ждёт | Tinkers' Construct 3 |
| `alfheim/sounds.json` | 1 | КТ-1 | перенесено | → `src/main/resources/assets/alfheim/sounds.json`; 57 событий целиком (в JSON нет комментариев), пути звуков — с приставкой `alfheim:`; события регистрирует `alfheim.port.registry.AlfheimSounds` |
| `botania/sounds.json` | 1 | КТ-8 | ждёт | музыка боссов |
| **всего** | **2344** | | | |

Файлы в корне `legacy/src/main/resources/`:

| Файл | КТ | Статус | Примечание |
|---|---|---|---|
| `mcmod.info` | КТ-0 | перенесено | содержимое → `src/main/resources/META-INF/mods.toml` |
| `credits.txt` | КТ-0 | перенесено | → `src/main/resources/credits.txt` |
| `alfheim_logo.png` | КТ-0 | перенесено | → `src/main/resources/alfheim_logo.png` |
| `alfheim_at.cfg` | по владельцу | ждёт | access transformer 1.7.10 (открывает закрытые поля и методы игры). Строку переносит КТ кода, которому она нужна: в 1.20.1 правило пишется заново в `META-INF/accesstransformer.cfg` с именем 1.20.1 или заменяется публичным методом |

Остальное в `legacy/` (сборка 1.7.10, `libs/`, `news/`, `release/`, служебные txt,
`legacy/asjcore/`) — эталон для сверки, в порт не переносится. Части ASJCore
переносятся по мере надобности (SPEC, Р-3), список — ниже.

## ASJCore

Файлы библиотеки автора, перенесённые из `legacy/asjcore/src/main/java/` в
`src/main/java/` с тем же путём (SPEC, Р-3). Перенос и правка — разными
коммитами, как у файлов Alfheim. Функции, которые порту ещё не понадобились,
закомментированы блоками `/* PORT: по мере надобности … */`: их включает КТ,
которой они нужны, сверив смысл с 1.20.1.

| Файл | Строк | КТ | Работает | Примечание |
|---|---:|---|---|---|
| `alexsocol/asjlib/extendables/ASJConfigHandler.kt` | 98 | КТ-1 | всё | поверх `alfheim.port.config.Configuration` |
| `alexsocol/asjlib/extendables/ASJPreConfigHandler.kt` | 83 | КТ-1 | всё | то же |
| `alexsocol/asjlib/ASJUtilities.kt` | 914 | КТ-1 | лог, сторона (`isServer`/`isClient`), `chance`, `randInBounds`, поиск в коллекциях, `say`, `soundFromMaterial` (КТ-2) | |
| `alexsocol/asjlib/Extensions.kt` | 358 | КТ-1 | функции Kotlin, `clamp`/`mfloor`/`mceil`, `eventForge`/`eventFML`, `ItemStack.cooldown`, `toItem`/`toBlock`/`ItemStack.block` | `meta`, числовые `id`, `PotionEffectU`, базовые классы блоков (`extendables`) — в КТ-2 вместе с блоками: у них меняется смысл metadata |
| `alexsocol/asjlib/ExtensionsClient.kt` | 17 | КТ-1 | `mc` | |
| `alexsocol/asjlib/ArrayExt.kt` | 88 | КТ-1 | всё | без правок |
| `alexsocol/asjlib/ItemNBTHelper.kt` | 149 | КТ-1 | всё | |
| `alexsocol/asjlib/math/Vector3.kt` | 391 | КТ-1 | всё, кроме `glVertex` | |
| `alexsocol/asjlib/math/Quaternion.kt` | 94 | КТ-1 | всё | без правок |
| `alexsocol/asjlib/network/ASJPacket.kt` | 143 | КТ-1 | всё | работу coremod `ASJPacketCompleter` (запись и чтение полей пакета) делает отражение |
| `alexsocol/asjlib/extendables/block/BlockModMeta.kt` | 46 | КТ-2 | всё | вариант metadata — отдельный блок с номером `meta`; иконки — модели генерации данных |
| `alexsocol/asjlib/extendables/ItemBlockMetaName.kt` | 17 | КТ-2 | всё | номер варианта — в id блока и в ключе перевода |
