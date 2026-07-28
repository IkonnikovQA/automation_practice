# automation_practice

API + UI automation framework for [Restful Booker](https://restful-booker.herokuapp.com/apidoc/index.html) and [SauceDemo](https://www.saucedemo.com/) with production-like test architecture and CI.

## Tech stack

- Java 17
- Maven
- JUnit 5
- REST Assured
- Selenide
- AssertJ
- Allure
- Spotless / Checkstyle / Enforcer

## Project architecture

```text
automation_practice/
├── pom.xml             # parent aggregator + dependency/plugin management
├── test-core/
│   └── pom.xml         # shared config/specs/clients/page objects/builders (test-jar)
├── api-tests/
│   └── pom.xml         # API test module (AuthApiTest, BookingApiTest)
├── ui-tests/
│   └── pom.xml         # UI test module (SauceDemoUiTest)
└── src/test/
    ├── java/com/qa/practice/
    │   ├── config/
    │   ├── api/
    │   ├── data/
    │   ├── ui/
    │   └── tests/
    └── resources/
        ├── schemas/
        ├── allure.properties
        └── categories.json
```

## Key engineering decisions

- Retry strategy for unstable public sandbox:
  - retries only for `429` and `5xx`
  - no retries for `4xx` to avoid hiding product bugs
- Contract checks:
  - JSON schema validation for auth and booking responses
- Cleanup strategy:
  - created booking ids are tracked and deleted in `@AfterEach`
- Test data strategy:
  - fluent builders for API booking payloads and UI checkout customer data
- Test taxonomy:
  - `@smoke`, `@regression`, `@negative`, `@contract`
- Allure diagnostics:
  - `environment.properties` is generated in CI for traceability
  - `categories.json` groups failures by infrastructure, contract/data, regression

## Run locally

```bash
# full regression
mvn clean test

# smoke API set (fast checks)
mvn -pl api-tests -am clean test -Psmoke-api

# smoke UI set
mvn -pl ui-tests -am clean test -Psmoke-ui

# style & quality checks
mvn spotless:check
mvn checkstyle:check

# allure report
mvn allure:serve
```

## Run in Docker

```bash
# build image
docker compose build

# API smoke in container
docker compose --profile api run --rm api-smoke

# UI smoke in containers (starts selenium + test runner)
docker compose --profile ui up --abort-on-container-exit --exit-code-from ui-smoke ui-smoke

# optional: open Selenium VNC in browser
# http://localhost:7900 (password: secret)
```

## One-command local runs

```bash
# Make targets (Linux/macOS/WSL/Git Bash)
make quality
make api-smoke
make ui-smoke
make ci-local

# Bash script
./scripts/ci-local.sh all
./scripts/ci-local.sh quality
./scripts/ci-local.sh ui-smoke
```

```powershell
# PowerShell script (Windows)
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

Enabled hooks:

- `pre-commit`: `spotless:check` + `checkstyle:check`
- `pre-push`: smoke API profile (`-pl api-tests -am -Psmoke-api`)

Default auth: `admin` / `password123` (from Restful Booker docs).

## CI workflow

GitHub Actions workflow (`.github/workflows/api-tests.yml`) includes:

- `quality` job: Spotless + Checkstyle
- `api-smoke` job: API smoke (`-Psmoke-api`) on PR and manual trigger
- `ui-smoke` job: UI smoke (`-Psmoke-ui`) on PR and manual trigger with Selenium service
- `ui-smoke-diagnostics` job: scheduled/manual UI smoke with single rerun for flaky diagnostics
- `regression` job: full regression on push to `main`, schedule, manual trigger
- `allure-report` job: publishes merged regression report to GitHub Pages
- Surefire + Allure artifacts upload for each test job

Jenkins pipeline (`Jenkinsfile`) includes:

- `Quality` stage: Spotless + Checkstyle
- `Build Docker Image` stage: `docker compose build`
- `API Smoke` stage: dockerized smoke for `AuthApiTest` + `BookingApiTest`
- `UI Smoke` stage: dockerized smoke for `SauceDemoUiTest` with Selenium container
- archived test artifacts from API and UI stages and JUnit XML publication

## Owner/component matrix

- `Platform / CI`: `.github/workflows/api-tests.yml`, `Jenkinsfile`, `docker-compose.yml` (`@IkonnikovQA`)
- `API Client & Specs`: `com.qa.practice.api.*` (`@IkonnikovQA`)
- `API Tests`: `AuthApiTest`, `BookingApiTest` (tags: `api`, `smoke`, `regression`, `negative`, `contract`)
- `UI Framework`: `SelenideSetup`, `com.qa.practice.ui.pages.*` (`@IkonnikovQA`)
- `UI Tests`: `SauceDemoUiTest` (tags: `ui`, `smoke`, `regression`, `negative`)

## Coverage

- Health: `GET /ping`
- Auth:
  - valid credentials
  - invalid credentials
- Booking positive:
  - list bookings
  - list by firstname filter
  - list by lastname filter
  - create booking
  - create booking with `totalprice = 0`
  - create booking with `additionalneeds = null`
  - get by id
  - full update (`PUT`)
  - partial update (`PATCH`)
  - delete
- Booking negative:
  - get non-existent id (`404`)
  - update without token (`403`)
  - update with invalid token (`403`)
  - partial update without token (`403`)
  - partial update with invalid token (`403`)
  - delete without token (`403`)
- UI (SauceDemo):
  - valid login
  - logout
  - login with `problem_user`
  - login mandatory fields validation
  - add product to cart
  - add two products to cart
  - remove product from cart
  - sort by price low-high
  - full checkout flow
  - checkout mandatory fields validation
  - locked out user error
  - invalid password error

## Roadmap

- Add separate UI Maven module (`ui-tests`)
- Add API + UI hybrid flows
- Publish generated Allure report to GitHub Pages
