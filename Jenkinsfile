@Library('shared-library') _

pipeline {
    agent { label 'linux-docker' }

    environment {
        IMAGE_NAME = "rowidarafiek/app"
        DOCKER_CREDS = 'dockerhub-cred'
    }

    stages {

        stage('Clone Repository') {
            steps {
                script {
                    echo "Cloning repository..."
                    withCredentials([usernamePassword(
                        credentialsId: 'github-cred',
                        usernameVariable: 'GIT_USER',
                        passwordVariable: 'GIT_PASS'
                    )]) {
                        sh """
                            rm -rf jenkins
                            git clone https://\${GIT_USER}:\${GIT_PASS}@github.com/rowidarafiek/jenkins.git
                        """
                    }
                    echo "✅ Repository cloned successfully"
                }
            }
        }

        stage('Run Unit Tests') {
            steps {
                dir('jenkins') {
                    script {
                        UnitTest()
                        echo "✅ Tests passed"
                    }
                }
            }
        }

        stage('Build Application') {
            steps {
                dir('jenkins') {
                    script {
                        buildApp()
                        echo "✅ Build completed"
                    }
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                dir('jenkins') {
                    script {
                        def IMAGE_TAG = "${env.BRANCH_NAME}-${env.BUILD_NUMBER}".toString()
                        echo "Building image with tag: ${IMAGE_TAG}"
                        buildImage(IMAGE_NAME, IMAGE_TAG)
                        env.IMAGE_TAG = IMAGE_TAG
                        echo "✅ Image built: ${IMAGE_NAME}:${IMAGE_TAG}"
                    }
                }
            }
        }

        stage('Scan Docker Image') {
            steps {
                dir('jenkins') {
                    script {
                        def IMAGE_TAG = env.IMAGE_TAG
                        echo "Scanning image: ${IMAGE_NAME}:${IMAGE_TAG}"
                        callSimple(IMAGE_NAME, IMAGE_TAG)
                        echo "✅ Scan completed"
                    }
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                dir('jenkins') {
                    script {
                        def IMAGE_TAG = env.IMAGE_TAG
                        echo "Pushing image: ${IMAGE_NAME}:${IMAGE_TAG}"
                        pushImage(IMAGE_NAME, IMAGE_TAG, DOCKER_CREDS)
                        echo "✅ Image pushed successfully"
                    }
                }
            }
        }

        stage('Remove Local Docker Image') {
            steps {
                dir('jenkins') {
                    script {
                        def IMAGE_TAG = env.IMAGE_TAG
                        removeImage(IMAGE_NAME, IMAGE_TAG)
                        echo "✅ Local images removed"
                    }
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                dir('jenkins') {
                    script {
                        def repo = IMAGE_NAME.toString()
                        def tag = env.IMAGE_TAG
                        def ns = env.BRANCH_NAME.toString()
                        echo "Deploying ${repo}:${tag} to namespace ${ns}"
                        deployOnK8s(repo, tag, ns)
                        echo "✅ Deployed to ${ns} namespace"
                    }
                }
            }
        }
    }

    post {
        success {
            echo "✅ Pipeline completed successfully!"
            sh 'docker image prune -f || true'
        }
        failure {
            echo "❌ Pipeline failed!"
            sh 'docker image prune -f || true'
        }
        always {
            cleanWs(deleteDirs: true, disableDeferredWipeout: true, notFailBuild: true)
        }
    }
}

