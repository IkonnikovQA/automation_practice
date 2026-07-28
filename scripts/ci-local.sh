#!/usr/bin/env bash
set -euo pipefail

stage="${1:-all}"

run_quality() {
  mvn -B spotless:check
  mvn -B checkstyle:check
}

run_build() {
  docker compose build
}

run_api_smoke() {
  docker compose --profile api run --rm api-smoke
}

run_ui_smoke() {
  docker compose --profile ui up --abort-on-container-exit --exit-code-from ui-smoke ui-smoke
}

cleanup() {
  docker compose --profile ui down -v || true
}

case "$stage" in
  quality)
    run_quality
    ;;
  build)
    run_build
    ;;
  api-smoke)
    run_api_smoke
    ;;
  ui-smoke)
    run_ui_smoke
    cleanup
    ;;
  all)
    run_quality
    run_build
    run_api_smoke
    run_ui_smoke
    cleanup
    ;;
  *)
    echo "Unknown stage: $stage"
    echo "Usage: ./scripts/ci-local.sh [quality|build|api-smoke|ui-smoke|all]"
    exit 1
    ;;
esac
