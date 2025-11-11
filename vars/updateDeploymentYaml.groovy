def call() {
    echo "Updating deployment.yaml with image tag ${env.IMAGE_TAG}"
    sh """
        sed -i 's|image:.*|image: ${env.IMAGE_NAME}:${env.IMAGE_TAG}|' deployment.yaml
    """
}
