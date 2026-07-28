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
  mvn -B -pl api-tests -am clean test -Psmoke-api
  if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
}

function Run-UiSmoke {
  mvn -B -pl ui-tests -am clean test -Psmoke-ui
  if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
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
    Run-UiSmoke
  }
  "all" {
    Run-Quality
    Run-Build
    Run-ApiSmoke
    Run-UiSmoke
  }
}
