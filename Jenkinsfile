
pipeline {

    agent any

    tools {
        maven 'Maven-3.9.9'
    }

    options {
        timestamps()
        timeout(time: 20, unit: 'MINUTES')
    }

    stages {

        stage('Checkout') {
            steps {
                echo 'Downloading source code...'
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo 'Building Spring Boot application...'
                sh 'mvn -B -ntp clean compile'
            }
        }

        stage('Test') {
            steps {
                echo 'Running unit tests...'
                sh 'mvn -B -ntp test'
            }
        }

        stage('Package') {
            steps {
                echo 'Generating executable JAR...'
                sh 'mvn -B -ntp package -DskipTests'
            }
        }

        stage('Archive JAR') {
            steps {
                archiveArtifacts(
                    artifacts: 'target/*.jar',
                    fingerprint: true,
                    allowEmptyArchive: false
                )
            }
        }
    }

    post {
        always {
            junit(
                testResults: 'target/surefire-reports/*.xml',
                allowEmptyResults: true
            )
        }

        success {
            echo 'CI PIPELINE SUCCESS!'
            echo 'Java JAR generated successfully.'
        }

        failure {
            echo 'CI PIPELINE FAILED!'
            echo 'Check Jenkins console output.'
        }
    }
}
