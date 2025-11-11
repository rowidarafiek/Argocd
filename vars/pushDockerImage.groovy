def call() {
    echo "Pushing Docker image ${env.IMAGE_NAME}:${env.IMAGE_TAG}"
    withCredentials([usernamePassword(credentialsId: env.DOCKER_CREDS, usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
        sh """
            echo \$DOCKER_PASS | docker login -u \$DOCKER_USER --password-stdin
            docker push ${env.IMAGE_NAME}:${env.IMAGE_TAG}
        """
    }
}
