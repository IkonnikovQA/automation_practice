param(
  [ValidateSet("quality", "build", "api-smoke", "ui-smoke", "all")]
  [string]$Stage = "all"
)

$ErrorActionPreference = "Stop"

function Run-Quality {
  mvn -B spotless:check
  if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

  mvn -B checkstyle:check
  if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
}

function Run-Build {
  docker compose build
  if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
}

function Run-ApiSmoke {
  docker compose --profile api run --rm api-smoke
  if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
}

function Run-UiSmoke {
  docker compose --profile ui up --abort-on-container-exit --exit-code-from ui-smoke ui-smoke
  if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
}

function Cleanup-Docker {
  docker compose --profile ui down -v
}

switch ($Stage) {
  "quality" {
    Run-Quality
  }
  "build" {
    Run-Build
  }
  "api-smoke" {
    Run-ApiSmoke
  }
  "ui-smoke" {
    try {
      Run-UiSmoke
    }
    finally {
      Cleanup-Docker
    }
  }
  "all" {
    try {
      Run-Quality
      Run-Build
      Run-ApiSmoke
      Run-UiSmoke
    }
    finally {
      Cleanup-Docker
    }
  }
}
