
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

        // 2. BUILD + UNIT TESTS + JACOCO
        stage('Build and Test') {
            steps {
                echo 'Building application and running unit tests...'
                echo 'Generating JaCoCo coverage report...'

                sh 'mvn -B -ntp clean verify'
            }
        }

        // 3. SONARQUBE ANALYSIS
        stage('SonarQube Analysis') {
            steps {
                echo 'Analyzing code quality with SonarQube...'

                withSonarQubeEnv('SonarQube') {
                    sh '''
                        mvn -B -ntp \
                        org.sonarsource.scanner.maven:sonar-maven-plugin:5.3.0.6276:sonar \
                        -Dsonar.projectKey=gestion-station-skii \
                        -Dsonar.projectName="Gestion Station Ski" \
                        -Dsonar.java.source=8 \
                        -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
                    '''
                }
            }
        }

        // 4. QUALITY GATE
        stage('SonarQube Quality Gate') {
            steps {
                echo 'Checking SonarQube Quality Gate...'

                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        // 5. ARCHIVE JAR
        stage('Archive JAR') {
            steps {
                echo 'Archiving generated Spring Boot JAR...'

                archiveArtifacts(
                    artifacts: 'target/*.jar',
                    fingerprint: true,
                    allowEmptyArchive: false
                )
            }
        }

        // 6. DEPLOY TO NEXUS
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

        // 7. BUILD DOCKER IMAGE
                // 7. BUILD DOCKER IMAGE
        stage('Docker Build') {
            steps {
                echo 'Building Docker image...'

                sh '''
                    docker build \
                        -f dockerfile \
                        -t raminasri2026/m2awi-skii:${BUILD_NUMBER} \
                        -t raminasri2026/m2awi-skii:latest \
                        .
                '''
            }
        }

        stage('Docker Push') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'dockerhub-credentials',
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_TOKEN'
                    )
                ]) {
                    sh '''
                        set +x
                        trap 'docker logout || true' EXIT

                        echo "$DOCKER_TOKEN" | docker login \
                            -u "$DOCKER_USER" --password-stdin

                        docker push raminasri2026/m2awi-skii:${BUILD_NUMBER}
                        docker push raminasri2026/m2awi-skii:latest
                    '''
                }
            }
        }

    }

    post {
        always {
            // Publish JUnit test results
            junit(
                testResults: 'target/surefire-reports/*.xml',
                allowEmptyResults: true
            )

            // Publish JaCoCo coverage metrics
            recordCoverage(
                tools: [[
                    parser: 'JACOCO',
                    pattern: 'target/site/jacoco/jacoco.xml'
                ]],
                failOnError: false
            )

            // Publish JaCoCo HTML report
            publishHTML(target: [
                reportDir: 'target/site/jacoco',
                reportFiles: 'index.html',
                reportName: 'JaCoCo Coverage Report',
                keepAll: true,
                alwaysLinkToLastBuild: true,
                allowMissing: true
            ])

            // Keep JaCoCo files as build artifacts
            archiveArtifacts(
                artifacts: 'target/site/jacoco/**',
                allowEmptyArchive: true
            )
        }

        success {
            echo 'CI PIPELINE SUCCESS!'
            echo 'Build and unit tests completed.'
            echo 'JaCoCo coverage report generated.'
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
