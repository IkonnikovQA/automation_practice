# automation_practice

API automation framework for [Restful Booker](https://restful-booker.herokuapp.com/apidoc/index.html).

UI layer will be added later.

## Stack

| Layer | Tool |
|-------|------|
| Java | 17 |
| Build | Maven |
| Tests | JUnit 5 |
| API | REST Assured |
| Reports | Allure |
| Assertions | AssertJ |

## Architecture

```
src/test/java/com/qa/practice/
├── config/           # config.properties + env/system overrides
├── api/
│   ├── client/       # BookingApi
│   ├── models/       # Booking, Auth DTOs (records)
│   └── specs/        # RequestSpecification
├── data/             # TestDataFactory
└── tests/api/        # Auth + Booking tests
```

## Run

```bash
mvn clean test
mvn clean test -Dgroups=smoke
mvn allure:serve
```

## Coverage (v1)

| Endpoint | Cases |
|----------|--------|
| `GET /ping` | health |
| `POST /auth` | valid / invalid credentials |
| `GET /booking` | list ids |
| `POST /booking` | create |
| `GET /booking/{id}` | get by id |
| `PUT /booking/{id}` | full update (cookie token) |
| `PATCH /booking/{id}` | partial update |
| `DELETE /booking/{id}` | delete + 404 after |

Default auth: `admin` / `password123` (from Restful Booker docs).

## Next

- Negative cases (missing fields, unauthorized update)
- JSON Schema checks
- UI layer (separate target)
- GitHub Actions + Allure artifacts
