def call(String imageName, String tag) {
    echo "Building Docker image ${imageName}:${tag}"
    sh "docker build -t ${imageName}:${tag} ."
}

