# Снимок ASJCore

Исходники библиотеки автора ASJCore — эталон для сверки при переносе частей
`alexsocol.asjlib` и `ru.vamig.worldengine` (SPEC, Р-2, Р-3). Здесь ничего не
правится.

| | |
|---|---|
| Репозиторий | https://bitbucket.org/AlexSocol/asjcore |
| Коммит | `7265f5dcee6ee429b040b9093e10d8731ff4d503` |
| Сообщение | «1.7.0.2 Release» |
| Автор, дата | AlexSocol, 03.09.2026 13:49 +0300 |
| Версия | 1.7.0.2 — та, что лежит у Alfheim в `legacy/libs/1.7.10-ASJCore-1.7.0.2-deobf.jar` |
| Лицензия | NCCPL 1.0, `LICENSE.txt` в этой папке |

## Почему этот коммит

Это коммит релиза 1.7.0.2. Его исходники сверены с
`legacy/libs/src/1.7.10-ASJCore-1.7.0.2-deobf-sources.jar` (исходники, с
которыми собирался Alfheim): все 193 файла `.kt` и `.java` из jar совпадают с
коммитом, если не считать концов строк (в jar — CRLF, в git — LF). В коммите
есть ещё 12 файлов `src/api` (заглушки чужих модов), в jar их нет.

Соседние коммиты не подходят: в `628cab1` и `674addd` («1.7.0.3 Release»)
отличаются 14 файлов, в `c4a522c` («1.7.0.1 Release») — 18. Следующий за
релизом merge `e2cc7b7` меняет только `gradle.properties`.

## Что взято

Всё дерево коммита (`git archive`), кроме:

- `build/dirtyArtifacts/forgeSrc-1.7.10-10.13.4.1614-1.7.10.jar` и
  `gradle/wrapper/gradle-wrapper.jar` — jar-файлы в git порта не попадают;
- `gradle/wrapper/gradle-wrapper.properties`, `gradlew`, `gradlew.bat`,
  `setup.bat` — обёртка сборки 1.7.10, без jar она не нужна;
- `.gitignore` — внутри порта он начал бы скрывать файлы этой папки.

Повторить снимок:

```bash
git clone https://bitbucket.org/AlexSocol/asjcore.git
cd asjcore
git archive 7265f5dcee6ee429b040b9093e10d8731ff4d503 \
  LICENSE.txt README.MD build.gradle changelog.txt gradle.properties src \
  | tar -x -C <порт>/legacy/asjcore
```
