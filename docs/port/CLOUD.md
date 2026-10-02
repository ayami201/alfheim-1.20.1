# Запуск в облаке Claude Code

Этот файл для владельца: как настроить облачную среду, как запускать сессии и
что делать с результатом.

## 1. Среда (один раз)

claude.ai/code → выбор среды (кнопка с облаком, по умолчанию «Default») → **Add environment**.

**Name:** `Alfheim port`

**Network access:** `Custom`, галочка **Also include default list of common package managers**.
Список доменов — по одному в строке:

```
maven.minecraftforge.net
files.minecraftforge.net
libraries.minecraft.net
launchermeta.mojang.com
piston-meta.mojang.com
piston-data.mojang.com
resources.download.minecraft.net
maven.blamejared.com
maven.theillusivec4.top
thedarkcolour.github.io
repo.spongepowered.org
```

Зачем: по умолчанию облако пускает только в общие хранилища пакетов, а
Minecraft, Forge, Botania, Curios и Kotlin for Forge лежат на своих серверах.
Для КТ-10 позже понадобятся JEI, Jade, Tinkers' Construct, Twilight Forest и
Aether. JEI лежит на `maven.blamejared.com`, он уже в списке. Tinkers — на
`dvs1.progwml6.com`. Для остальных добавится `api.modrinth.com` или
`cursemaven.com` — агент скажет, какой.

**Setup script:**

```bash
#!/bin/bash
# Java 17 для Forge 1.20.1. В облаке по умолчанию стоит только Java 21.
if [ ! -d /usr/lib/jvm/java-17-openjdk-amd64 ]; then
  SUDO=""; [ "$(id -u)" -ne 0 ] && SUDO="sudo"
  $SUDO apt-get update -qq && $SUDO apt-get install -y -qq openjdk-17-jdk-headless
fi
```

Скрипт выполняется при первом запуске, потом облако помнит результат.

## 2. Настройки репозитория на GitHub (один раз)

После того как ветка `port/1.20.1` появится на GitHub:
- **Settings → General → Default branch** → `port/1.20.1`. Тогда сессии по
  умолчанию начинают с порта, и PR идут туда же.
- **Settings → General → Pull Requests** → снять **Allow squash merging**.
  Причина — в CLAUDE.md, раздел «Git».

## 3. Запуск сессии

claude.ai/code → среда `Alfheim port` → репозиторий `ayami201/alfheim-1.20.1`,
ветка `port/1.20.1` → модель → текст ниже.

Одна сессия за раз: пока PR прошлой сессии не влит, новую не запускать.

**Первая сессия (КТ-0):**

```
Я переношу мод Alfheim (автор AlexSocol, Minecraft 1.7.10) на Minecraft 1.20.1 Forge для небольшой сборки с Botania. Игрокам нужен мод с логикой и балансом автора, а не переделка. С учётом этого: выполни КТ-0 из docs/port/ROADMAP.md. Правила работы — в CLAUDE.md, технические решения — в docs/port/SPEC.md.

Готово — это когда выполнены все пункты «Готово, когда» КТ-0 и открыт PR в port/1.20.1 с отчётом.

Облачная среда: Java 17 установлена в /usr/lib/jvm, сеть открыта только для доменов из docs/port/CLOUD.md. Если для сборки не хватает домена или пакета — напиши, какого именно, и остановись: настройки среды меняет владелец.

Останавливайся и спрашивай только в случаях из раздела «Когда останавливаться» в CLAUDE.md.
```

**Следующие сессии:**

```
Продолжи порт Alfheim по docs/port/TASKS.md: возьми текущую КТ и следующий незакрытый пункт. Готово — пункты «Готово, когда» этой КТ выполнены и открыт PR с отчётом. Если КТ не помещается в сессию — закончи на целом пункте TASKS.md, обнови файл и открой PR с тем, что сделано.
```

**Модель.** На Pro модели Fable оплачиваются отдельными «usage credits».
Подарочные $100 на облако точно покрывают модели, входящие в план (Opus 5.5,
Sonnet 5.5). ТЗ написано так, что подходит и Fable, и Opus.

## 4. Когда сессия закончилась

1. Прочитай последнее сообщение сессии: там то, что нужно от тебя. В PR —
   список изменений, решения и результат проверки.
2. Дай ссылку на PR локальному Claude: «проверь PR». Он сверит изменения с оригиналом и правилами.
3. Если PR завершает КТ и в нём есть раздел «Проверка в игре» — скачай jar из
   проверки CI (вкладка **Checks** → артефакт сборки), положи в копию своей
   сборки `toru 1.20` и пройди чек-лист. Все зависимости мода (Botania,
   Patchouli, Curios, Kotlin for Forge) в сборке уже есть.
4. Влей PR кнопкой **Merge pull request** (не squash).

## 5. Известные ловушки облака

- **Скачивание Gradle.** Обёртка `./gradlew` скачивает Gradle с GitHub, а облако
  пускает на GitHub только в подключённые репозитории. Если обёртка падает с
  403, агент проверяет `gradle --version` в облаке. Если версия подходит
  ForgeGradle 6 (Gradle 8.x), он собирает установленным `gradle`. Если нет —
  пишет, какой домен-зеркало добавить в список.
- **Первая сборка в новой сессии долгая.** ForgeGradle распаковывает и
  подготавливает Minecraft 5–10 минут. Это ожидание, а не зависание.
- **Машина облака:** 4 ядра, 16 ГБ памяти, 30 ГБ диска.

Проверено в сессии КТ-0 (02.10.2026):

- **Java.** По умолчанию `java` — 21. Перед сборкой:
  `export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64`.
- **Обёртка Gradle работает.** `./gradlew` скачал Gradle 8.14.3 через прокси облака.
  Обёртка закреплена на 8.14.3 — той же версии, что установлена в облаке
  (`/opt/gradle`), поэтому запасной путь `gradle build` собирает так же.
- **429 Too Many Requests от Maven Central** при первой сборке: облако упирается в
  ограничение частоты запросов. Это не запрет. Повторить сборку с
  `--max-workers=2`: уже скачанное Gradle не качает заново.
- **В логе `runGameTestServer` две ошибки не от мода:** `Failed to load properties
  from file: server.properties` (первый запуск сервера) и `Failed to request yggdrasil
  public key … Host not in allowlist: api.minecraftservices.com` (тестовому серверу
  вход Mojang не нужен). Домен добавлять не нужно. `tools/check_server_log.py`
  проверяет, что ошибок от `alfheim` нет.
