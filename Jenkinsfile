pipeline {
  agent any

  options {
    timestamps()
    disableConcurrentBuilds()
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Quality') {
      steps {
        sh 'mvn -B spotless:check'
        sh 'mvn -B checkstyle:check'
      }
    }

    stage('Build Docker Image') {
      steps {
        sh 'docker compose build'
      }
    }

    stage('API Smoke') {
      steps {
        sh 'docker compose --profile api run --rm api-smoke'
      }
      post {
        always {
          sh 'mkdir -p artifacts/api'
          sh 'cp -r target/surefire-reports artifacts/api/ || true'
          sh 'cp -r target/allure-results artifacts/api/ || true'
        }
      }
    }

    stage('UI Smoke') {
      steps {
        sh 'docker compose --profile ui up --abort-on-container-exit --exit-code-from ui-smoke ui-smoke'
      }
      post {
        always {
          sh 'docker compose --profile ui down -v || true'
          sh 'mkdir -p artifacts/ui'
          sh 'cp -r target/surefire-reports artifacts/ui/ || true'
          sh 'cp -r target/allure-results artifacts/ui/ || true'
        }
      }
    }
  }

  post {
    always {
      archiveArtifacts artifacts: 'artifacts/**', allowEmptyArchive: true
      junit testResults: 'target/surefire-reports/*.xml', allowEmptyResults: true
    }
  }
}
