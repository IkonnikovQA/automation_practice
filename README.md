# automation_practice

[![CI](https://github.com/IkonnikovQA/automation_practice/actions/workflows/ci.yml/badge.svg)](https://github.com/IkonnikovQA/automation_practice/actions/workflows/ci.yml)
[![Allure Report](https://img.shields.io/badge/Allure-Report-blue)](https://ikonnikovqa.github.io/automation_practice/#)
[![Java](https://img.shields.io/badge/Java-17-orange)](https://openjdk.org/projects/jdk/17/)
[![Maven](https://img.shields.io/badge/Maven-3.9+-C71A36)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/license-Portfolio-lightgrey)](README.md)

Фреймворк автоматизации API + UI для [Restful Booker](https://restful-booker.herokuapp.com/apidoc/index.html) и [SauceDemo](https://www.saucedemo.com/) с архитектурой и CI, близкими к боевым проектам.

## Стек

- Java 17
- Maven
- JUnit 5
- REST Assured
- Selenide
- AssertJ
- Allure
- Spotless / Checkstyle / Enforcer

## Архитектура проекта

```text
automation_practice/
├── pom.xml                 # родительский агрегатор + управление зависимостями/плагинами
├── test-core/
│   ├── pom.xml             # общий framework JAR (config/specs/clients/page objects/builders)
│   └── src/main/
│       ├── java/com/qa/practice/{config,api,data,ui}/
│       └── resources/      # config.properties, allure.properties, categories.json
├── api-tests/
│   ├── pom.xml
│   └── src/test/
│       ├── java/.../tests/api/   # AuthApiTest, BookingApiTest
│       └── resources/schemas/
├── ui-tests/
│   ├── pom.xml
│   └── src/test/java/.../tests/ui/  # SauceDemoUiTest
├── hybrid-tests/
│   ├── pom.xml
│   └── src/test/java/.../tests/hybrid/  # API → UI оркестрация
└── docs/
    └── api-coverage.md     # карта endpoint → тесты
```

## Ключевые инженерные решения

- Стратегия ретраев для нестабильного публичного sandbox:
  - повторы только при `429` и `5xx`
  - без ретраев на `4xx`, чтобы не маскировать баги продукта
- Контрактные проверки:
  - JSON Schema для auth и booking-ответов
- Стратегия очистки:
  - id созданных бронирований трекаются и удаляются в `@AfterEach`
- Тестовые данные:
  - fluent-builders для API booking и UI checkout
- Таксономия тестов:
  - `@smoke`, `@regression`, `@negative`, `@contract`
- Диагностика Allure:
  - в CI генерируется `environment.properties`
  - `categories.json` группирует падения по инфраструктуре, контракту/данным и регрессии

## Локальный запуск

```bash
# полная регрессия
mvn clean test

# smoke API (быстрые проверки)
mvn -pl api-tests -am clean test -Psmoke-api

# smoke UI
mvn -pl ui-tests -am clean test -Psmoke-ui

# smoke Hybrid (API → UI)
mvn -pl hybrid-tests -am clean test -Psmoke-hybrid

# стиль и quality-гейты (также привязаны к verify)
mvn verify -DskipTests
mvn spotless:check
mvn checkstyle:check

# Allure-отчёт
mvn allure:serve
```

Демо-учётные данные Restful Booker: `admin` / `password123` (только публичный sandbox; переопределяются через env/system properties).

## Запуск в Docker

```bash
# сборка образа
docker compose build

# API smoke в контейнере
docker compose --profile api run --rm api-smoke

# UI smoke (поднимает selenium + runner)
docker compose --profile ui up --abort-on-container-exit --exit-code-from ui-smoke ui-smoke

# Hybrid smoke (API → UI, с Selenium)
docker compose --profile hybrid up --abort-on-container-exit --exit-code-from hybrid-smoke hybrid-smoke

# опционально: Selenium VNC в браузере
# http://localhost:7900 (пароль: secret)
```

## Однокомандные локальные прогоны

```bash
# Make (Linux/macOS/WSL/Git Bash)
make quality
make api-smoke
make ui-smoke
make ci-local

# Bash-скрипт
./scripts/ci-local.sh all
./scripts/ci-local.sh quality
./scripts/ci-local.sh ui-smoke
```

```powershell
# PowerShell (Windows)
.\scripts\ci-local.ps1 -Stage all
.\scripts\ci-local.ps1 -Stage quality
.\scripts\ci-local.ps1 -Stage ui-smoke
```

## Git hooks

```bash
# Linux/macOS/WSL
./scripts/install-hooks.sh

# Windows PowerShell
.\scripts\install-hooks.ps1
```

Включённые hooks:

- `pre-commit`: `spotless:check` + `checkstyle:check`
- `pre-push`: smoke API (`-pl api-tests -am -Psmoke-api`)

## CI

GitHub Actions ([`.github/workflows/ci.yml`](.github/workflows/ci.yml)):

- `quality`: `mvn verify -DskipTests` (Spotless + Checkstyle + Enforcer)
- `api-smoke`: API smoke (`-Psmoke-api`) на PR и ручной запуск
- `ui-smoke`: UI smoke (`-Psmoke-ui`) на PR и ручной запуск с Selenium
- `ui-smoke-diagnostics`: scheduled/manual UI smoke с одним rerun для диагностики флаков
- `regression`: полная регрессия (api + ui + hybrid) на push в `main`, schedule и manual (с Selenium)
- `allure-report`: публикация отчёта на GitHub Pages при push в main / schedule / manual
- артефакты Surefire + Allure для каждого тестового job

Jenkins (`Jenkinsfile`):

- `Quality`: Spotless + Checkstyle + Enforcer через `verify`
- `Build Docker Image`: `docker compose build`
- `API Smoke`: dockerized smoke для `AuthApiTest` + `BookingApiTest`
- `UI Smoke`: dockerized smoke для `SauceDemoUiTest` с Selenium
- архивация артефактов и публикация JUnit XML

## Матрица владельцев / компонентов

- `Platform / CI`: `.github/workflows/ci.yml`, `Jenkinsfile`, `docker-compose.yml` (`@IkonnikovQA`)
- `API Client & Specs`: `com.qa.practice.api.*` (`@IkonnikovQA`)
- `API Tests`: `AuthApiTest`, `BookingApiTest` (теги: `api`, `smoke`, `regression`, `negative`, `contract`)
- `UI Framework`: `SelenideSetup`, `com.qa.practice.ui.pages.*` (`@IkonnikovQA`)
- `UI Tests`: `SauceDemoUiTest` (теги: `ui`, `smoke`, `regression`, `negative`)
- `Hybrid Tests`: `HybridApiUiSmokeTest` (теги: `hybrid`, `smoke`, `regression`)

## Владение и эксплуатация

- `CODEOWNERS`: [`.github/CODEOWNERS`](.github/CODEOWNERS)
- Операционный гайд: [`RUNBOOK.md`](RUNBOOK.md)
- Как контрибьютить: [`CONTRIBUTING.md`](CONTRIBUTING.md)
- Карта API: [`docs/api-coverage.md`](docs/api-coverage.md)
- Changelog: [`CHANGELOG.md`](CHANGELOG.md)
- Рекомендуемый процесс:
  - держать зелёными PR-проверки (`quality`, `api-smoke`, `ui-smoke`);
  - использовать `ui-smoke-diagnostics` для трендов флаков;
  - начинать разбор инцидентов с `RUNBOOK.md`.

## Покрытие

- Health: `GET /ping`
- Auth:
  - валидные credentials
  - невалидные credentials
- Booking (позитив):
  - список бронирований
  - фильтр по firstname
  - фильтр по lastname
  - создание booking
  - создание с `totalprice = 0`
  - создание с `additionalneeds = null`
  - создание с `depositpaid = false`
  - получение по id
  - полное обновление (`PUT`) + повторное чтение
  - частичное обновление (`PATCH`) firstname + повторное чтение
  - частичное обновление (`PATCH`) нескольких полей + повторное чтение
  - удаление
- Booking (негатив):
  - несуществующий id (`404`)
  - создание с пустым телом / битым JSON / неверным Content-Type
  - update без токена (`403`)
  - update с невалидным токеном (`403`)
  - partial update без токена (`403`)
  - partial update с невалидным токеном (`403`)
  - delete без токена (`403`)
  - delete с невалидным токеном (`403`)
  - повторный delete / delete несуществующего id
- UI (SauceDemo):
  - успешный логин
  - logout
  - логин `problem_user`
  - валидация обязательных полей логина
  - добавление товара в корзину
  - добавление двух товаров
  - удаление товара из корзины
  - сортировка по цене low→high
  - полный checkout
  - валидация обязательных полей checkout
  - ошибка locked out user
  - ошибка неверного пароля
- Hybrid (API → UI):
  - health API + create/get booking, затем UI login в каталог SauceDemo

## Roadmap

- Матрица пользователей SauceDemo (`performance_glitch_user`, `error_user`) + sort/product details
- Browser matrix (Chrome/Firefox) и Dependabot/CodeQL
- Visual / a11y / load (опционально, advanced)
