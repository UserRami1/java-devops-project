
pipeline {
    agent any

    tools {
        maven 'Maven-3.9.9'
    }

    options {
        timestamps()
        timeout(time: 30, unit: 'MINUTES')
    }

    stages {

        // 1. CHECKOUT
        stage('Checkout') {
            steps {
                echo 'Downloading source code...'
                checkout scm
            }
        }

        // 2. BUILD
        stage('Build') {
            steps {
                echo 'Building Spring Boot application...'
                sh 'mvn -B -ntp clean compile'
            }
        }

        // 3. UNIT TESTS
        stage('Test') {
            steps {
                echo 'Running unit tests...'
                sh 'mvn -B -ntp test'
            }
        }

        // 4. SONARQUBE ANALYSIS
        
stage('SonarQube Analysis') {
    steps {
        withSonarQubeEnv('SonarQube') {
            sh '''
                mvn -B -ntp org.sonarsource.scanner.maven:sonar-maven-plugin:5.3.0.6276:sonar \
                -Dsonar.projectKey=gestion-station-skii \
                -Dsonar.projectName="Gestion Station Ski" \
                -Dsonar.java.source=8 \
                -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
            '''
        }
    }
}

        // 5. QUALITY GATE
        stage('SonarQube Quality Gate') {
            steps {
                echo 'Checking SonarQube Quality Gate...'

                timeout(time: 10, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        // 6. PACKAGE
        stage('Package') {
            steps {
                echo 'Generating executable JAR...'
                sh 'mvn -B -ntp package -DskipTests'
            }
        }

        // 7. ARCHIVE JAR
        stage('Archive JAR') {
            steps {
                archiveArtifacts(
                    artifacts: 'target/*.jar',
                    fingerprint: true,
                    allowEmptyArchive: false
                )
            }
        }

        // 8. DEPLOY TO NEXUS
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
            echo 'SonarQube analysis completed.'
            echo 'Quality Gate passed.'
            echo 'Artifact deployed to Nexus!'
        }

        failure {
            echo 'CI PIPELINE FAILED!'
            echo 'Check Jenkins console output.'
        }
    }
}
