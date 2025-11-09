pipeline {
  agent { label 'agent-1' }
  
  environment {
    IMAGE_NAME = 'rowidarafiek/app'
    APP_REPO = 'jenkins'
    MANIFEST_REPO = 'k8s-manifests'  // Your manifest repository name
    GIT_EMAIL = 'jenkins@cicd.local'
    GIT_NAME = 'Jenkins CI/CD'
  }
  
  stages {
    stage('Clone Repository') {
      steps {
        withCredentials([usernamePassword(
          credentialsId: 'github-cred',
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
            echo "Image: ${IMAGE_NAME}:${BUILD_NUMBER}"
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
          credentialsId: 'github-cred',
          usernameVariable: 'GIT_USER',
          passwordVariable: 'GIT_PASS'
        )]) {
          sh '''
            echo "========== Updating Kubernetes Manifest =========="
            
            # Clean up any existing manifest repo
            rm -rf ${MANIFEST_REPO}
            
            # Clone manifest repository
            git clone https://$GIT_USER:$GIT_PASS@github.com/rowidarafiek/${MANIFEST_REPO}.git
            cd ${MANIFEST_REPO}
            
            # Configure git
            git config user.email "${GIT_EMAIL}"
            git config user.name "${GIT_NAME}"
            
            # Update image tag in deployment.yaml
            # This will find and replace the image line with the new tag
            sed -i "s|image: ${IMAGE_NAME}:.*|image: ${IMAGE_NAME}:${BUILD_NUMBER}|g" deployment.yaml
            
            # Alternative: if you want to update a specific pattern
            # sed -i "s|rowidarafiek/app:.*|${IMAGE_NAME}:${BUILD_NUMBER}|g" deployment.yaml
            
            echo "=== Updated deployment.yaml ==="
            cat deployment.yaml | grep -A 2 "image:"
            
            # Check if there are changes to commit
            if git diff --quiet; then
              echo "No changes detected in deployment.yaml"
            else
              echo "Changes detected, preparing to commit..."
              git add deployment.yaml
              git commit -m "Update image to ${IMAGE_NAME}:${BUILD_NUMBER} - Build #${BUILD_NUMBER}"
              echo "Manifest updated successfully ✓"
            fi
          '''
        }
      }
    }
    
    stage('Push Updates to GitHub') {
      steps {
        withCredentials([usernamePassword(
          credentialsId: 'github-cred',
          usernameVariable: 'GIT_USER',
          passwordVariable: 'GIT_PASS'
        )]) {
          sh '''
            echo "========== Pushing Changes to GitHub =========="
            cd ${MANIFEST_REPO}
            
            # Check if there are commits to push
            if git log origin/main..HEAD | grep -q .; then
              # Push changes to GitHub
              git push https://$GIT_USER:$GIT_PASS@github.com/rowidarafiek/${MANIFEST_REPO}.git main
              echo "Changes pushed to GitHub successfully ✓"
            else
              echo "No new commits to push"
            fi
          '''
        }
      }
    }
    
    stage('Validate ArgoCD Deployment') {
      steps {
        sh '''
          echo "========== Validating ArgoCD Deployment =========="
          
          # Wait for ArgoCD to detect changes
          echo "Waiting 30 seconds for ArgoCD to detect changes..."
          sleep 30
          
          # Check if kubectl is available
          if command -v kubectl &> /dev/null; then
            echo "--- ArgoCD Application Status ---"
            kubectl get application jenkins-app -n argocd 2>/dev/null || echo "ArgoCD application 'jenkins-app' not found"
            
            echo ""
            echo "--- Deployment Status ---"
            kubectl get deployment jenkins-app -n default 2>/dev/null || echo "Deployment not found yet"
            
            echo ""
            echo "--- Pods Status ---"
            kubectl get pods -n default -l app=jenkins-app 2>/dev/null || echo "No pods found yet"
            
            echo ""
            echo "--- Recent Events ---"
            kubectl get events -n default --sort-by='.lastTimestamp' | tail -10 || true
            
            echo ""
            echo "✓ ArgoCD will automatically sync and deploy the new version"
            echo "✓ New image: ${IMAGE_NAME}:${BUILD_NUMBER}"
          else
            echo "kubectl not available, skipping Kubernetes validation"
            echo "ArgoCD will still automatically sync and deploy"
          fi
        '''
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
      echo '========================================='
      echo '✅ Pipeline Completed Successfully!'
      echo '========================================='
      echo "✓ Build Number: ${BUILD_NUMBER}"
      echo "✓ Docker Image: ${IMAGE_NAME}:${BUILD_NUMBER}"
      echo "✓ Manifest Updated: deployment.yaml"
      echo "✓ Changes Pushed to GitHub"
      echo "✓ ArgoCD will deploy automatically"
      echo '========================================='
    }
    
    failure {
      echo '========================================='
      echo '❌ Pipeline Failed!'
      echo '========================================='
      echo "Build Number: ${BUILD_NUMBER}"
      echo "Please check the logs above for details"
      echo '========================================='
    }
    
    cleanup {
      echo 'Cleaning up workspace...'
      cleanWs()
    }
  }
}
