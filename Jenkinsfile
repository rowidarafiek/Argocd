pipeline {
  agent { label 'linux-docker' }

  environment {
    IMAGE_NAME = 'rowidarafiek/app'
    APP_REPO = 'jenkins'
    MANIFEST_REPO = 'k8s-manifests'
    GIT_EMAIL = 'jenkins@cicd.local'
    GIT_NAME = 'Jenkins CI/CD'
    ARGOCD_SERVER = '192.168.126.129:32443'
    ARGO_APP_NAME = 'jenkins-app'
  }

  stages {
    stage('Clone Repository') {
      steps {
        withCredentials([usernamePassword(
          credentialsId: 'github-pat',   // Credential with your GitHub username + PAT
          usernameVariable: 'GIT_USER',
          passwordVariable: 'GIT_PASS'
        )]) {
          sh '''
            echo "========== Cloning Application Repository =========="
            rm -rf ${APP_REPO}
            git clone https://$GIT_USER:$GIT_PASS@github.com/rowidarafiek/${APP_REPO}.git
            echo "Repository cloned successfully ✓"
          '''
        }
      }
    }

    stage('Run Unit Tests') {
      steps {
        dir("${APP_REPO}") {
          sh '''
            echo "========== Running Unit Tests =========="
            if [ -f pom.xml ]; then
              mvn test
              echo "Tests passed ✓"
            else
              echo "No test files found, skipping..."
            fi
          '''
        }
      }
    }

    stage('Build Application') {
      steps {
        dir("${APP_REPO}") {
          sh '''
            echo "========== Building Application =========="
            if [ -f pom.xml ]; then
              mvn clean package -DskipTests
              echo "Application built successfully ✓"
            elif [ -f package.json ]; then
              npm install
              npm run build
              echo "Node.js application built successfully ✓"
            elif [ -f requirements.txt ]; then
              pip install -r requirements.txt
              echo "Python dependencies installed ✓"
            else
              echo "No build file detected, skipping..."
            fi
          '''
        }
      }
    }

    stage('Build Docker Image') {
      steps {
        dir("${APP_REPO}") {
          sh '''
            echo "========== Building Docker Image =========="
            docker build -t ${IMAGE_NAME}:${BUILD_NUMBER} .
            docker tag ${IMAGE_NAME}:${BUILD_NUMBER} ${IMAGE_NAME}:latest
            echo "Docker image built successfully ✓"
          '''
        }
      }
    }

    stage('Push Docker Image to Registry') {
      steps {
        withCredentials([usernamePassword(
          credentialsId: 'dockerhub-cred',
          usernameVariable: 'DOCKER_USER',
          passwordVariable: 'DOCKER_PASS'
        )]) {
          dir("${APP_REPO}") {
            sh '''
              echo "========== Pushing to Docker Hub =========="
              echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
              docker push ${IMAGE_NAME}:${BUILD_NUMBER}
              docker push ${IMAGE_NAME}:latest
              echo "Image pushed successfully ✓"
            '''
          }
        }
      }
    }

    stage('Delete Docker Image') {
      steps {
        sh '''
          echo "========== Cleaning Up Local Images =========="
          docker rmi ${IMAGE_NAME}:${BUILD_NUMBER} || true
          docker rmi ${IMAGE_NAME}:latest || true
          docker system prune -f
          echo "Local images deleted ✓"
        '''
      }
    }

    stage('Update Deployment Manifest') {
  steps {
    withCredentials([usernamePassword(
      credentialsId: 'github-pat',
      usernameVariable: 'GIT_USER',
      passwordVariable: 'GIT_PASS'
    )]) {
      sh '''
        echo "========== Updating Kubernetes Manifest =========="
        git fetch origin
        git checkout main || git checkout -b main
        git reset --hard origin/main
        git pull origin main --rebase || true

        sed -i "s|image:.*|image: rowidarafiek/app:${BUILD_NUMBER}|" deployment.yaml

        git config user.name "${GIT_USER}"
        git config user.email "${GIT_EMAIL}"

        git add deployment.yaml
        git commit -m "Update image to ${BUILD_NUMBER}" || echo "No changes to commit"
        git push https://$GIT_USER:$GIT_PASS@github.com/rowidarafiek/${APP_REPO}.git main
      '''
    }
  }
}

    stage('Validate ArgoCD Deployment') {
      steps {
        withCredentials([usernamePassword(
          credentialsId: 'argocd-cred',
          usernameVariable: 'ARGO_USER',
          passwordVariable: 'ARGO_PASS'
        )]) {
          sh '''
            echo "========== Validating ArgoCD Deployment =========="
            argocd login ${ARGOCD_SERVER} --username $ARGO_USER --password $ARGO_PASS --insecure
            sleep 30
            argocd app get ${ARGO_APP_NAME}
            argocd app wait ${ARGO_APP_NAME} --health --timeout 180
          '''
        }
      }
    }
  }

  post {
    always {
      sh '''
        echo "========== Cleanup =========="
        docker logout || true
        docker image prune -f || true
        rm -rf ${MANIFEST_REPO} || true
      '''
    }

    success {
      echo '✅ Pipeline Completed Successfully!'
    }

    failure {
      echo '❌ Pipeline Failed!'
    }

    cleanup {
      cleanWs()
    }
  }
}

