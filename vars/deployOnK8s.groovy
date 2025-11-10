def call(String imageRepo, String imageTag, String namespace) {
    def image = "${imageRepo}:${imageTag}"
    sh "kubectl apply -n ${namespace} -f deployment.yaml"
sh "kubectl set image deployment/jenkins-app-deployment jenkins-app-container=${image} -n ${namespace}"

}


