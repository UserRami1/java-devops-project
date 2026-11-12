
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

        stage('Deploy to Nexus') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'nexus-credentials',
                        usernameVariable: 'NEXUS_USER',
                        passwordVariable: 'NEXUS_PASSWORD'
                    )
                ]) {
                    sh '''
                        set +x

                        cat > settings.xml <<EOF
<settings>
    <servers>
        <server>
            <id>nexus</id>
            <username>${NEXUS_USER}</username>
            <password>${NEXUS_PASSWORD}</password>
        </server>
    </servers>
</settings>
EOF

                        trap 'rm -f settings.xml' EXIT

                        echo "Deploying artifact to Nexus..."

                        mvn -B -ntp -s settings.xml deploy -DskipTests
                    '''
                }
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
            echo 'Artifact deployed to Nexus!'
        }

        failure {
            echo 'CI PIPELINE FAILED!'
            echo 'Check Jenkins console output.'
        }
    }
}
