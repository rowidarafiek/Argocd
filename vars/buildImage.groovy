def call(String imageName, String buildNumber) {
    echo "Building Docker image: ${imageName}:${buildNumber}"
    sh """
        docker build -t ${imageName}:${buildNumber} .
        docker tag ${imageName}:${buildNumber} ${imageName}:latest
    """
    echo "Docker image built successfully"
}
