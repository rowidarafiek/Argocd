def call() {
    echo "Building Docker image ${env.IMAGE_NAME}:${env.IMAGE_TAG}"
    sh "docker build -t ${env.IMAGE_NAME}:${env.IMAGE_TAG} ."
}
