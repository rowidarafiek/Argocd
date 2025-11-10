def call(String imageName, String tag) {
    echo "Updating deployment.yaml with ${imageName}:${tag}"
    sh """
        sed -i 's|image: .*|image: ${imageName}:${tag}|' k8s/deployment.yaml
    """
}

