@Library('shared-library') _

pipeline {
    agent { label 'linux-docker' }

    environment {
        IMAGE_NAME = "rowidarafiek/app"
        DOCKER_CREDS = "dockerhub-cred"
    }

    stages {

        stage('Clone Repo') {
            steps {
                script {
                    echo "Cloning repository"
                    withCredentials([usernamePassword(
                        credentialsId: 'github-cred',
                        usernameVariable: 'GIT_USER',
                        passwordVariable: 'GIT_PASS'
                    )]) {
                        sh """
                            rm -rf jenkins
                            git clone https://${GIT_USER}:${GIT_PASS}@github.com/rowidarafiek/jenkins.git
                        """
                    }
                }
            }
        }

        stage('Unit Tests') {
            steps {
                dir('jenkins') {
                    script {
                        unitTests()
                    }
                }
            }
        }

        stage('Build App') {
            steps {
                dir('jenkins') {
                    script {
                        buildApp()
                    }
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                dir('jenkins') {
                    script {
                        IMAGE_TAG = "${env.BRANCH_NAME}-${env.BUILD_NUMBER}"
                        buildDockerImage(IMAGE_NAME, IMAGE_TAG)
                    }
                }
            }
        }

        stage('Scan Docker Image') {
            steps {
                dir('jenkins') {
                    script {
                        scanImage(IMAGE_NAME, IMAGE_TAG)
                    }
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                dir('jenkins') {
                    script {
                        pushDockerImage(IMAGE_NAME, IMAGE_TAG, DOCKER_CREDS)
                    }
                }
            }
        }

        stage('Remove Local Image') {
            steps {
                dir('jenkins') {
                    script {
                        removeDockerImage(IMAGE_NAME, IMAGE_TAG)
                    }
                }
            }
        }
    }

    post {
        success {
            echo "Pipeline completed successfully"
            sh 'docker image prune -f || true'
        }
        failure {
            echo "Pipeline failed"
            sh 'docker image prune -f || true'
        }
        always {
            cleanWs(deleteDirs: true, disableDeferredWipeout: true)
        }
    }
}

