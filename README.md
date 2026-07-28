# automation_practice

API automation framework for [Restful Booker](https://restful-booker.herokuapp.com/apidoc/index.html) with production-like test architecture and CI.

## Tech stack

- Java 17
- Maven
- JUnit 5
- REST Assured
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
└── tests/api/
    ├── AuthApiTest
    └── BookingApiTest

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
- Test taxonomy:
  - `@smoke`, `@regression`, `@negative`, `@contract`

## Run locally

```bash
# full regression
mvn clean test

# smoke set (fast checks)
mvn clean test -Dgroups=smoke

# style & quality checks
mvn spotless:check
mvn checkstyle:check

# allure report
mvn allure:serve
```

Default auth: `admin` / `password123` (from Restful Booker docs).

## CI workflow

GitHub Actions workflow (`.github/workflows/api-tests.yml`) includes:

- `quality` job: Spotless + Checkstyle
- `smoke` job: runs on PR and manual trigger
- `regression` job: runs on push to `main`, schedule, manual trigger
- Surefire + Allure artifacts upload for each test job

## Coverage

- Health: `GET /ping`
- Auth:
  - valid credentials
  - invalid credentials
  - malformed body
- Booking positive:
  - list bookings
  - create booking
  - get by id
  - full update (`PUT`)
  - partial update (`PATCH`)
  - delete
- Booking negative:
  - get non-existent id (`404`)
  - update without token (`403`)
  - update with invalid token (`403`)
  - delete without token (`403`)
  - invalid create payloads (`400`)

## Roadmap

- Add UI module (`ui-tests`) with Selenide
- Add API + UI hybrid flows
- Publish generated Allure report to GitHub Pages
