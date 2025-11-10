def call(String imageName, String buildNumber) {
    echo "Scanning Docker image ${imageName}:${buildNumber}"
    // Add your scanning command here, e.g.,
    // sh "docker scan ${imageName}:${buildNumber}"
}

