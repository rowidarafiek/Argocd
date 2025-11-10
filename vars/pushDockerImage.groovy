def call(String imageName, String tag, String creds) {
    echo "Pushing Docker image ${imageName}:${tag}"
    withCredentials([usernamePassword(credentialsId: creds, usernameVariable: 'USER', passwordVariable: 'PASS')]) {
        sh """
            echo $PASS | docker login -u $USER --password-stdin
            docker push ${imageName}:${tag}
        """
    }
}

