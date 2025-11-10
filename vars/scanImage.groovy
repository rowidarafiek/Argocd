def callSimple(String imageName, String buildNumber) {
    echo "Scanning Docker image: ${imageName}:${buildNumber}"
    sh """
        docker inspect ${imageName}:${buildNumber}
        echo "Image scan completed (basic inspection)"
    """
}
