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
src/test/java/com/qa/practice/
├── config/             # config loader + env/system overrides
├── api/
│   ├── client/         # BookingApi: endpoint wrapper methods
│   ├── models/         # DTO records
│   └── specs/          # base spec + token spec + retry filter
├── data/               # test data factory/builders
└── tests/
    ├── api/
    │   ├── AuthApiTest
    │   └── BookingApiTest
    └── ui/
        └── SauceDemoUiTest

src/test/resources/
└── schemas/            # JSON Schema contracts
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

## Run locally

```bash
# full regression
mvn clean test

# smoke set (fast checks)
mvn clean test -Dgroups=smoke

# ui only
mvn clean test -Dgroups=ui

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

Default auth: `admin` / `password123` (from Restful Booker docs).

## CI workflow

GitHub Actions workflow (`.github/workflows/api-tests.yml`) includes:

- `quality` job: Spotless + Checkstyle
- `smoke` job: runs on PR and manual trigger
- `regression` job: runs on push to `main`, schedule, manual trigger
- Surefire + Allure artifacts upload for each test job

Jenkins pipeline (`Jenkinsfile`) includes:

- `Quality` stage: Spotless + Checkstyle
- `Build Docker Image` stage: `docker compose build`
- `API Smoke` stage: dockerized smoke for `AuthApiTest` + `BookingApiTest`
- `UI Smoke` stage: dockerized smoke for `SauceDemoUiTest` with Selenium container
- archived test artifacts from API and UI stages

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
  - create booking with `additionalneeds = null`
  - get by id
  - full update (`PUT`)
  - partial update (`PATCH`)
  - delete
- Booking negative:
  - get non-existent id (`404`)
  - update without token (`403`)
  - update with invalid token (`403`)
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
