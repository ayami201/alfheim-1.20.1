# Задачи порта

Живой файл: агент обновляет его в каждой сессии, владелец читает его первым.

**Текущая КТ:** КТ-1 — Фундамент ([ROADMAP.md](ROADMAP.md)). **Статус:** в работе.

КТ-0 принята 02.10.2026 (PR «КТ-0 готова», ayami201/alfheim-1.20.1#1).

## КТ-1 — Фундамент

- [ ] Конфиг: `AlfheimConfigHandler`, `AlfheimPreConfigHandler` → ForgeConfigSpec (SPEC, Р-12); опции, потерявшие смысл, — в MAPPING.md; сверка опций с автором скриптом в CI
- [ ] ASJCore: `ASJUtilities`, `Extensions`, `ItemNBTHelper`, `math` — то, что нужно КТ-1 и КТ-2
- [ ] Прослойка `alfheim.port.legacy` — минимум для КТ-2 (SPEC, Р-4)
- [ ] Сеть: канал `SimpleChannel` и регистрация пакетов автора (SPEC, Р-11)
- [ ] Регистрация: DeferredRegister, правило имён, `src/main/resources/alfheim/legacy_ids.json` (SPEC, Р-5)
- [ ] Вкладка творческого режима `AlfheimTab`
- [ ] `core/util`, `core/helper`, нужное из `api/`, прокси, общие обработчики событий — без частей, которым нужны механики следующих КТ
- [ ] Переводы `.lang` → `.json` (en_us, ru_ru, zh_cn и строки Alfheim для Botania) скриптом; сверка числа ключей в CI
- [ ] GameTest регистрации
- [ ] Опыт: клиент без экрана в облаке (Xvfb + программный OpenGL), не больше одной сессии
- [ ] PR «КТ-1 готова»

## КТ-0 — Каркас

- [x] Перенести дерево автора в `legacy/` одним коммитом (только `git mv`) — `f6ad8c4`, 3 538 файлов, все R100
- [x] Снимок ASJCore → `legacy/asjcore/`, хэш → `legacy/asjcore/SNAPSHOT.md` — коммит `7265f5dc` («1.7.0.2 Release»)
- [x] Сборка Forge 1.20.1-47.4.23: Java 17, Kotlin 2.2.21, KFF 4.12.0, Botania 456, Patchouli 85, Curios 5.14.1, Mixin
- [x] `AlfheimCore.kt` и `api/ModInfo.kt`: перенос + правка (`ModInfo.kt` переносится без правок)
- [x] `mods.toml` с авторством
- [x] `.gitignore` под новую раскладку
- [x] CI: `build` + `runGameTestServer` (+ сверка описи и врезок, проверка лога сервера)
- [x] `INVENTORY.md` — сверка числом с `legacy/src/main/java`: 1 153 из 1 153, `python3 tools/check_inventory.py`
- [x] `HOOKS.md` — сверка числом с `@Hook` в `legacy`: 428 строк с `@Hook` разобраны, `python3 tools/check_hooks.py`
- [x] `README.md` порта
- [x] PR в `port/1.20.1` с отчётом

## Как проверять (для любой сессии)

```bash
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
./gradlew build
./gradlew runGameTestServer && python3 tools/check_server_log.py
python3 tools/check_inventory.py && python3 tools/check_hooks.py
```

## Найдено

Отступления от оригинала (`// PORT:`, `// PORT-FIX:`), расхождения в
поведении, всё, что пригодится на стадии 2. Формат: дата — где — что — почему.

- 02.10.2026 — `AlfheimCore.kt` — `preInit` автора вызывается из конструктора мода (`init {}`), `init` — на `FMLCommonSetupEvent`, `postInit` — на `InterModProcessEvent`, `starting` — на `ServerStartingEvent`. `construct` и `postPostInit` закомментированы целиком: в них только Thaumcraft — в 1.20.1 нет событий FML 1.7.10, а регистрация и конфиг возможны только в конструкторе. Соответствие — в MAPPING.md.
- 02.10.2026 — `AlfheimCore.kt` — флаги `MineTweakerLoaded`, `TravellersGearLoaded` всегда `false` (моды выпали, SPEC п. 7); `NEILoaded`, `TiCLoaded`, `TwilightForestLoaded` пока `false`, проверка вернётся в КТ-10 вместе с интеграциями — чтобы код, перенесённый раньше, не шёл в непортированные ветки интеграций.
- 02.10.2026 — `AlfheimCore.kt` — `stupidMode` включается, если загружен мод с id `avaritia` (у автора — `Avaritia`). В 1.20.1 есть только неофициальные порты Avaritia; если в сборке её нет, режим выключен, как у автора без Avaritia.
- 02.10.2026 — `mods.toml` — `authors="AlexSocol, ChatGPT"`: так в `authorList` оригинального `mcmod.info`. Поле `credits` — `credits.txt` автора целиком, подставляется при сборке.
- 02.10.2026 — `alfheim.mixins.json` — без строки `"refmap"`: пока миксинов нет, карта ссылок не создаётся, и Mixin писал в лог предупреждение. С первым миксином строку вернуть (написано в HOOKS.md).
- 02.10.2026 — HOOKS.md — число 428 из ROADMAP — это строки с `@Hook` (`grep`). Врезок `@Hook` 388, `@HookReplacer` 32, `@HookField` 3; ещё 4 — пометки параметров `@Hook.ReturnValue`, 1 — объявление группы `@HookReplacer.CreateHRG`. Кроме них учтены 4 `@SuperWrapper`, 20 преобразований трансформера в 19 классах, 11 добавленных интерфейсов и 7 закомментированных автором `@Hook`.
- 02.10.2026 — опись и врезки — у автора есть интеграции, которых не было в SPEC п. 7: ExtraBotany (2 врезки в её рендер), Dimensional Doors (`ItemTankMask`, телепорт в Лимб), IC2 (`ItemTerraHoe`, грядки), Avaritia (`stupidMode`), Witchery (исправление чужого рендера в трансформере). Решено: Dimensional Doors, IC2 и Witchery выпадают, проверка Avaritia переносится, врезки ExtraBotany переносятся в КТ-10 и проверяются с ExtraBotany: Reburn (журнал решений, SPEC п. 7). Сами `ItemTankMask` и `ItemTerraHoe` остаются; без этих модов они работают, как у автора без них: маска отправляет в Хельхейм, мотыга пропускает грядки IC2.
- 02.10.2026 — опись — вещи Alfheim, взятые автором из ExtraBotany (по `credits.txt` — копьё `ItemSpearSubspace`, лук `ItemMoonlightBow`, `ItemMultibauble` и рецепт очистки реликвий `RecipeCleanRelic`; линзы `lensPush`, `lensSmelt`, `lensSuperconductor`, `lensTrack` — `ItemLensExtender.kt`), — собственные вещи Alfheim, а не интеграция. Они переносятся в своих КТ и без ExtraBotany: Reburn. Если она будет в сборке, у игрока окажутся похожие предметы из обоих модов.
- 02.10.2026 — `RagnarokHandler.kt` — в списке существ Рагнарёка есть имена из других модов 1.7.10 (`primitivemobs`, `Thaumcraft`, `witchery`). Решать при переносе в КТ-8: таких существ в сборке нет.
- 02.10.2026 — опись — 19 врезок `ElementalDamageAdapter` и 2 интерфейса нацелены на классы Thaumcraft (жезлы, големы, виспы и др.) — выпадают вместе с Thaumcraft.
- 02.10.2026 — опись — узоры знамён `assets/minecraft/textures/entity/banner` (18 флагов) — часть интеграции с Et Futurum, выпадают вместе с ней. В 1.20.1 знамёна есть в ванили: узоры можно вернуть как ванильные на стадии 2.
- 02.10.2026 — опись — папки автора `unused` (27 текстур блоков, 23 текстуры предметов, 1 модель) в игре не используются: «WIP — стадия 2».
- 02.10.2026 — опись — пост-шейдер `assets/minecraft/shaders` (`depth`, `sobel`): прямых ссылок в коде не найдено, отнесён к КТ-7 до выяснения.
- 02.10.2026 — опись — сущности `EntityFenrirDome`, `EntityFenrirSlash`, `EntityTornado` зарегистрированы, но используются только незарегистрированным WIP `EntityFenrirNew`. По SPEC п. 6 переносятся как зарегистрированные (КТ-8).
- 02.10.2026 — `gradle/wrapper/gradle-wrapper.jar` — единственный новый jar в git: без него не работает `./gradlew`, которого требует CLAUDE.md. Это стандартная обёртка Gradle 8.14.3; CI сверяет её с контрольными суммами Gradle (`gradle/actions/setup-gradle`). Исключение записано в CLAUDE.md.
- 02.10.2026 — правила — `move:`-коммит файла кода в `src/` ломает компиляцию до следующего `port:`-коммита: код 1.7.10 не собирается на 1.20.1. В КТ-0 обошлось: файлы перенесены и поправлены до появления сборки. Исключение записано в CLAUDE.md, раздел «Проверка».

## Журнал решений

| Дата | Решение | Почему | Как откатить |
|---|---|---|---|
| 02.10.2026 | Первая редакция ТЗ, контрольных точек и правил порта | Старт проекта | — |
| 02.10.2026 | Список «выпадает заранее» (SPEC, п. 7) утверждён владельцем; Tinkers' Construct, Twilight Forest и Aether остаются | Все три будут в сборке владельца | Вернуть Tinkers в список п. 7 |
| 02.10.2026 | Снимок ASJCore — коммит `7265f5dc` («1.7.0.2 Release») | Его исходники совпадают с `libs/src/1.7.10-ASJCore-1.7.0.2-deobf-sources.jar` с точностью до концов строк; подробности — `legacy/asjcore/SNAPSHOT.md` | Взять другой коммит и пересоздать папку |
| 02.10.2026 | Обёртка Gradle 8.14.3 (в MDK — 8.8) | Та же версия, что установлена в облаке: облако и CI собирают одинаково; ForgeGradle 6 с ней работает | `distributionUrl` в `gradle/wrapper/gradle-wrapper.properties` |
| 02.10.2026 | Версия мода `67-port.N`, N — номер последней принятой КТ | По jar видно, от какого релиза автора идёт порт и до какой КТ он дошёл | `mod_version` в `gradle.properties` |
| 02.10.2026 | GameTest-ы — в отдельном наборе исходников `src/gametest` | Тесты запускаются в CI, но не попадают в jar для игроков | Перенести в `src/main` и убрать `sourceSets.gametest` из `build.gradle` |
| 02.10.2026 | Опись и врезки сверяются скриптами `tools/check_inventory.py` и `tools/check_hooks.py` в CI | Сверка числом (ROADMAP, КТ-0) повторяется на каждом PR, а не один раз | Убрать шаги из `.github/workflows/build.yml` |
| 02.10.2026 | Скрипты порта — в `tools/` | Не код мода и не документ; КТ-1 добавит туда скрипт переводов | Перенести в `docs/port/tools/` |
| 02.10.2026 | Интеграции с Dimensional Doors, IC2 и Witchery не переносятся (SPEC п. 7); проверка Avaritia (`stupidMode`) переносится | Эти моды существуют только для 1.7.10; без них код автора эти ветки не выполняет, игрок ничего не теряет. Проверка Avaritia — одна строка автора и без Avaritia ничего не делает | Вернуть строки в INVENTORY.md и HOOKS.md в «ждёт», убрать абзац из SPEC п. 7 |
| 02.10.2026 | Интеграция автора с ExtraBotany (2 врезки в рендер её предметов) переносится в КТ-10 и проверяется с ExtraBotany: Reburn (SPEC п. 7) | ExtraBotany: Reburn — версия ExtraBotany для Forge 1.20.1, её можно поставить в сборку | Вернуть ExtraBotany в абзац «не переносятся» SPEC п. 7, строки в INVENTORY.md и HOOKS.md — в «выпало» |
| 02.10.2026 | Коммит `move:` с кодом — исключение из правила «сборка зелёная после каждого коммита» (CLAUDE.md) | Перенос и правка — разные коммиты (правило 3), а код 1.7.10 на 1.20.1 не компилируется | Вернуть прежнюю строку в CLAUDE.md |
| 02.10.2026 | `gradle/wrapper/gradle-wrapper.jar` — исключение из правила «новые jar-файлы не попадают в git» (CLAUDE.md) | Без него не работает `./gradlew` | Удалить файл и собирать установленным `gradle` |
| 02.10.2026 | Формат отчётов: в PR и документах — изменения чек-листом, итоговые решения, найденное и проверка; без раздела «Ждёт меня», вопросов и переписки (CLAUDE.md, «Отчёт») | Документы и PR читают люди, которые не видели переписки | Вернуть прежний раздел «Отчёт» в CLAUDE.md |

## Вопросы к владельцу

Пока нет.
