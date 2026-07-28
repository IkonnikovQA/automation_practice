#!/usr/bin/env bash
set -euo pipefail

stage="${1:-all}"

run_quality() {
  mvn -B verify -DskipTests
}

run_api_smoke() {
  mvn -B -pl api-tests -am clean test -Psmoke-api
}

run_ui_smoke() {
  mvn -B -pl ui-tests -am clean test -Psmoke-ui
}

case "$stage" in
  quality)
    run_quality
    ;;
  build)
    docker compose build
    ;;
  api-smoke)
    run_api_smoke
    ;;
  ui-smoke)
    run_ui_smoke
    ;;
  all)
    run_quality
    docker compose build
    run_api_smoke
    run_ui_smoke
    ;;
  *)
    echo "Unknown stage: $stage"
    echo "Usage: ./scripts/ci-local.sh [quality|build|api-smoke|ui-smoke|all]"
    exit 1
    ;;
esac
