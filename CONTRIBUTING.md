# Как контрибьютить

Краткий гайд по работе с этим портфолио-фреймворком.

## Требования

- JDK 17+
- Maven 3.9+
- Опционально: Docker Desktop (для UI smoke / compose)

## Локальный процесс

1. Один раз поставить hooks:
   - Linux/macOS/WSL: `./scripts/install-hooks.sh`
   - Windows: `.\scripts\install-hooks.ps1`
2. Форматирование и стиль:
   - `mvn -B spotless:apply`
   - `mvn -B verify -DskipTests`
3. Гонять только изменённое:
   - API: `mvn -B -pl api-tests -am "-Dtest=BookingApiTest#methodName" test`
   - UI: `mvn -B -pl ui-tests -am "-Dtest=SauceDemoUiTest#methodName" test`
4. Перед push — smoke:
   - `mvn -B -pl api-tests -am test -Psmoke-api`

## Ожидания к PR

- Один фокус на PR (покрытие, CI, docs или архитектура).
- При новых API-кейсах обновлять покрытие в `README.md` и `docs/api-coverage.md`.
- Добавлять запись в секцию `[Unreleased]` в `CHANGELOG.md`.
- На публичных sandbox предпочитать стабильные ассерты; известную flaky-поведение документировать, а не форсировать хрупкие проверки.
- Пользовательские описания (`@DisplayName`, Allure `@Story` / `@Step`) — на русском.

## Теги

| Тег | Назначение |
|---|---|
| `smoke` | Быстрый критический путь для PR-гейтов |
| `regression` | Полный набор на main / nightly |
| `negative` | Невалидный ввод / ошибки авторизации |
| `contract` | Schema / структурные проверки |
| `api` / `ui` | Фильтры набора |

## Владение

Ревьюер/владелец по умолчанию: `@IkonnikovQA` (см. `.github/CODEOWNERS`).
