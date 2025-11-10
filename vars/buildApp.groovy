def call() {
    echo "Building application"
    sh """
        chmod +x gradlew || true
        ./gradlew clean build || mvn clean package
    """
}

