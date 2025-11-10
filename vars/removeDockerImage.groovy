def call(String imageName, String tag) {
    echo "Removing local Docker image ${imageName}:${tag}"
    sh "docker rmi -f ${imageName}:${tag} || true"
}

