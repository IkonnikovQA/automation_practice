# RUNBOOK

Операционный гайд по диагностике и восстановлению упавших CI-прогонов в `automation_practice`.

## 1. Упал job `quality`

### Симптомы
- падает `mvn verify -DskipTests`
- падает `spotless:check`
- падает `checkstyle:check`
- не проходят правила Enforcer по версии Java/Maven

### Действия
1. Локально:
   - `mvn -B spotless:apply`
   - `mvn -B verify -DskipTests`
2. Закоммитить правки форматирования/стиля.
3. Перезапустить workflow.

## 2. Упал job `api-smoke`

### Симптомы
- упали ассерты в `AuthApiTest` / `BookingApiTest`
- сетевые ошибки / проблемы скачивания Maven-плагинов

### Действия
1. Открыть артефакт `surefire-reports-api-smoke` и найти упавший тест.
2. Перепрогнать локально:
   - `mvn -B -pl api-tests -am clean test -Psmoke-api`
3. Если причина внешняя (`403`, сеть, нестабильность sandbox) — перезапустить workflow и сравнить с diagnostics.

## 3. Упал job `ui-smoke`

### Симптомы
- ошибки сессии/старта Selenium
- таймауты или не найдены элементы

### Действия
1. Проверить логи Selenium service в workflow.
2. Открыть:
   - `surefire-reports-ui-smoke`
   - `allure-results-ui-smoke`
3. Локально (с Docker):
   - `docker compose --profile ui up --abort-on-container-exit --exit-code-from ui-smoke ui-smoke`
4. При флаках смотреть `ui-smoke-diagnostics` (один rerun).

## 4. Упал job `regression`

### Симптомы
- падения API и/или UI в полном прогоне
- нестабильность публичных sandbox

### Действия
1. Сравнить Surefire API vs UI артефакты.
2. Проверить, что Selenium service healthy и в команде передан `ui.remote.url`.
3. Перепрогнать только упавший модуль:
   - API: `mvn -B -pl api-tests -am test`
   - UI: `mvn -B -pl ui-tests -am test -Dui.remote.url=...`
4. Известные особенности sandbox зафиксированы в `docs/api-coverage.md`.

## 5. Проблемы публикации Allure Pages

### Симптомы
- `allure-report` не деплоит / пустой отчёт
- нет артефактов `allure-results-regression-*`

### Действия
1. Убедиться, что `regression` завершился и загрузил Allure-артефакты.
2. Проверить права workflow: `pages: write`, `id-token: write`.
3. Локально собрать отчёт из скачанных results:
   - `allure generate <results> --clean -o allure-report`

## 6. Быстрый чеклист перед эскалацией

- [ ] Локально зелёный `mvn -B verify -DskipTests`
- [ ] Локально воспроизведён упавший тест
- [ ] Это баг продукта, а не инфраструктура (см. Allure categories)
- [ ] Обновлены README / `docs/api-coverage.md`, если менялось покрытие
