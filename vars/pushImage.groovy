def call(String imageName, String buildNumber, String credentialsId) {
    echo "Pushing Docker image to registry..."
    
    withCredentials([usernamePassword(
        credentialsId: credentialsId,
        usernameVariable: 'DOCKER_USER',
        passwordVariable: 'DOCKER_PASS'
    )]) {
        sh """
            echo "Logging into Docker registry..."
            echo \$DOCKER_PASS | docker login -u \$DOCKER_USER --password-stdin
            
            echo "Pushing ${imageName}:${buildNumber}..."
            docker push ${imageName}:${buildNumber}
            
            echo "Pushing ${imageName}:latest..."
            docker push ${imageName}:latest
            
            echo "Logging out..."
            docker logout
        """
    }
    
      echo "Docker images pushed successfully"
}
