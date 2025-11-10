def call(String imageRepo, String imageTag, String namespace) {
    def image = "${imageRepo}:${imageTag}"
    sh "kubectl apply -n ${namespace} -f k8s/deployment.yaml"
    sh "kubectl set image deployment/my-app my-app=${image} -n ${namespace}"
}

