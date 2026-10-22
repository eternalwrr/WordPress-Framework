pipeline {
    agent any
    options {
        timestamps()
        skipDefaultCheckout(true)
    }
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Check Environment') {
            steps {
                bat 'java -version'
                bat 'mvn -version'
            }
        }
        stage('Run Tests') {
            steps {
                bat 'mvn -B clean test'
            }
        }
    }
    post {
        always {
            junit(
                testResults: '**/target/surefire-reports/*.xml',
                allowEmptyResults: true
            )
            archiveArtifacts(
                artifacts: '**/target/allure-results/**',
                allowEmptyArchive: true
            )
        }
        success {
            echo 'All tests passed successfully!'
        }
        failure {
            echo 'Pipeline failed. Check the console output.'
        }
    }
}
