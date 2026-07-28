# Changelog

Все заметные изменения проекта фиксируются в этом файле.

Формат основан на [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
версионирование фреймворка — [Semantic Versioning](https://semver.org/).

## [Unreleased]

### Added

- API-покрытие: `depositpaid=false`, multi-field `PATCH`, double-delete, негативы create payload
- `docs/api-coverage.md` — карта endpoint → тесты
- `CONTRIBUTING.md` и этот changelog
- Selenium service для GitHub Actions job `regression`
- публикация Allure Pages на каждый push в `main`
- Spotless / Checkstyle / Enforcer привязаны к Maven `verify`
- русскоязычные docs, `@DisplayName`, Allure Story/Step и categories

### Changed

- workflow переименован в `.github/workflows/ci.yml`
- Selenium-образ закреплён на `selenium/standalone-chrome:4.27.0`
- quality CI job: `mvn verify -DskipTests`
- документация и пользовательские описания тестов переведены на русский

## [1.0.0-SNAPSHOT] - 2026-07

### Added

- multi-module Maven (`test-core`, `api-tests`, `ui-tests`)
- наборы Restful Booker API и SauceDemo UI
- GitHub Actions, Jenkins, Docker Compose, Allure Pages, hooks, RUNBOOK
