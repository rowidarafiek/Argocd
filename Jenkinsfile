@Library('shared-lib') _

pipeline {
    agent { label 'linux-docker' }

    environment {
        IMAGE_NAME = 'rowidarafiek/app'
    }

    stages {

        stage('Clone Repository') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'github-pat', usernameVariable: 'GIT_USER', passwordVariable: 'GIT_PASS')]) {
                    sh '''
                        echo "========== Cloning Repository =========="
                        rm -rf Argocd
                        git clone https://${GIT_USER}:${GIT_PASS}@github.com/rowidarafiek/Argocd.git
                        cd Argocd
                        git checkout main
                    '''
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                sh '''
                    echo "========== Building Docker Image =========="
                    docker build -t ${IMAGE_NAME}:${BUILD_NUMBER} .
                '''
            }
        }

        stage('Push Docker Image') {
            steps {
                 sh 'mvn clean package -DskipTests'
                withCredentials([usernamePassword(credentialsId: 'dockerhub-cred', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh '''
                        echo "========== Pushing Docker Image =========="
                        echo "${DOCKER_PASS}" | docker login -u "${DOCKER_USER}" --password-stdin
                        docker push ${IMAGE_NAME}:${BUILD_NUMBER}
                    '''
                }
            }
        }

        stage('Update Deployment Manifest') {
            steps {
                dir('Argocd') {
                    withCredentials([usernamePassword(credentialsId: 'github-pat', usernameVariable: 'GIT_USER', passwordVariable: 'GIT_PASS')]) {
                        sh '''
                            echo "========== Updating Kubernetes Manifest =========="
                            git fetch origin main
                            git checkout main
                            git reset --hard origin/main

                            sed -i "s|image:.*|image: rowidarafiek/app:${BUILD_NUMBER}|" deployment.yaml

                            git config user.name "rowidarafiek"
                            git config user.email "jenkins@cicd.local"
                            git add deployment.yaml
                            git commit -m "Update image to ${BUILD_NUMBER}" || true

                            git pull origin main --rebase
                            git push https://${GIT_USER}:${GIT_PASS}@github.com/rowidarafiek/Argocd.git main
                        '''
                    }
                }
            }
        }

        stage('Validate ArgoCD Deployment') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'argocd-cred', usernameVariable: 'ARGO_USER', passwordVariable: 'ARGO_PASS')]) {
                    sh '''
                        echo "========== Validating ArgoCD Deployment =========="
                        if ! command -v argocd >/dev/null 2>&1; then
                            echo "ArgoCD CLI not found. Please install it on the Jenkins agent."
                            exit 1
                        fi

                        argocd login 192.168.126.129:32443 --username ${ARGO_USER} --password ${ARGO_PASS} --insecure
                        argocd app sync myapp
                        argocd app wait myapp --health --timeout 180
                    '''
                }
            }
        }
    }

    post {
        success {
            echo "✅ Deployment Successful!"
        }
        failure {
            echo "❌ Pipeline Failed!"
            cleanWs()
        }
    }
}

