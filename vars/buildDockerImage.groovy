def call() {
    echo "Building Docker image ${env.IMAGE_NAME}:${env.IMAGE_TAG}"
    sh 'if [ ! -f target/demo-0.0.1-SNAPSHOT.jar ]; then echo "JAR not found!"; exit 1; fi'
    sh "docker build -t ${env.IMAGE_NAME}:${env.IMAGE_TAG} ."
}
