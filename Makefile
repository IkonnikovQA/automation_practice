.PHONY: help quality api-smoke-local ui-smoke-local docker-build api-smoke ui-smoke smoke-all docker-down ci-local

help:
	@echo "Available targets:"
	@echo "  quality      - Run Spotless and Checkstyle checks"
	@echo "  api-smoke-local - Run API smoke tests locally via Maven profile"
	@echo "  ui-smoke-local  - Run UI smoke tests locally via Maven profile"
	@echo "  docker-build - Build test runner image"
	@echo "  api-smoke    - Run API smoke tests in Docker"
	@echo "  ui-smoke     - Run UI smoke tests in Docker (with Selenium)"
	@echo "  smoke-all    - Run API and UI smoke sequentially"
	@echo "  docker-down  - Stop and clean Docker services/volumes"
	@echo "  ci-local     - Full local CI flow: quality + build + smokes"

quality:
	mvn -B spotless:check
	mvn -B checkstyle:check

api-smoke-local:
	mvn -B -pl api-tests -am clean test -Psmoke-api

ui-smoke-local:
	mvn -B -pl ui-tests -am clean test -Psmoke-ui

docker-build:
	docker compose build

api-smoke:
	docker compose --profile api run --rm api-smoke

ui-smoke:
	docker compose --profile ui up --abort-on-container-exit --exit-code-from ui-smoke ui-smoke

smoke-all: api-smoke ui-smoke

docker-down:
	docker compose --profile ui down -v

ci-local: quality docker-build smoke-all docker-down
