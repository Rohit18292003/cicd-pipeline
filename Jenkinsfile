
pipeline {
    agent any

    tools {
        jdk 'JDK21'
        maven 'Maven3'
    }

    stages {

        stage('Checkout') {
            steps {
                echo 'Getting source code from GitHub'
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo 'Compiling Spring Boot application'
                bat 'mvn clean compile'
            }
        }

        stage('Test') {
            steps {
                echo 'Running unit tests'
                bat 'mvn test'
            }
        }

        stage('Package') {
            steps {
                echo 'Creating JAR file'
                bat 'mvn package -DskipTests'
            }
        }

        stage('Archive') {
            steps {
                archiveArtifacts artifacts: 'target/*.jar',
                                 fingerprint: true
            }
        }

        stage('Docker Build') {
            steps {
                bat '''
                    docker build ^
                    -t rohitbirajdardocker/cicd-springboot-app:%BUILD_NUMBER% ^
                    -t rohitbirajdardocker/cicd-springboot-app:latest .
                '''
            }
        }

        stage('Docker Push') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'dockerhub-credentials',
                        usernameVariable: 'DOCKERHUB_USER',
                        passwordVariable: 'DOCKERHUB_TOKEN'
                    )
                ]) {
                    bat 'echo %DOCKERHUB_TOKEN%| docker login -u %DOCKERHUB_USER% --password-stdin'

                    try {
                        bat 'docker push rohitbirajdardocker/cicd-springboot-app:%BUILD_NUMBER%'
                        bat 'docker push rohitbirajdardocker/cicd-springboot-app:latest'
                    } finally {
                        bat 'docker logout'
                    }
                }
            }
        }

        stage('Docker Deploy') {
            steps {
                script {
                    bat(
                        returnStatus: true,
                        script: 'docker rm -f cicd-app'
                    )

                    bat '''
                        docker pull rohitbirajdardocker/cicd-springboot-app:latest

                        docker run -d --restart unless-stopped ^
                        -p 8081:8080 ^
                        --name cicd-app ^
                        rohitbirajdardocker/cicd-springboot-app:latest
                    '''
                }
            }
        }
    }

    post {
        success {
            echo 'BUILD SUCCESSFUL!'
        }

        failure {
            echo 'BUILD FAILED! Check console output.'
        }

        always {
            echo 'Pipeline execution completed.'
        }
    }
}
