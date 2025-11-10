def call(String imageName, String buildNumber) {
    echo "Removing local Docker images..."
    sh """
        docker rmi ${imageName}:${buildNumber} || true
        docker rmi ${imageName}:latest || true
        echo "Local images removed"
    """
}
