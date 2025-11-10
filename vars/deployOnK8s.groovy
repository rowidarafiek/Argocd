def call(String imageRepo, String imageTag, String namespace) {
    def image = "${imageRepo}:${imageTag}"
    sh "kubectl apply -n ${namespace} -f deployment.yaml"
    sh "kubectl set image deployment/my-app my-app=${image} -n ${namespace}"
}

