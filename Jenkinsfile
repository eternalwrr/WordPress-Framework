pipeline {
    agent any
    options {
        timestamps()
        skipDefaultCheckout(true)
    }
    parameters {
        choice(
            name: 'BROWSER',
            choices: ['chrome', 'firefox'],
            description: 'Select browser for test execution'
        )
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
                bat 'echo Selected browser: %BROWSER%'
            }
        }
        stage('Run Tests') {
            steps {
                bat "mvn -B clean test -Dbrowser=${params.BROWSER}"
            }
        }
    }
    post {
        always {
            junit(
                testResults: '**/target/surefire-reports/*.xml',
                allowEmptyResults: true
            )

            allure(
                includeProperties: false,
                jdk: '',
                results: [[path: 'target/allure-results']]
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