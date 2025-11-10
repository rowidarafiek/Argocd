def call(String branchName, String commitMessage) {
    echo "Pushing changes to GitHub on branch ${branchName}"
    withCredentials([usernamePassword(
        credentialsId: 'github-cred',
        usernameVariable: 'GIT_USER',
        passwordVariable: 'GIT_PASS'
    )]) {
        sh """
            git config user.name "${GIT_USER}"
            git config user.email "jenkins@local"
            git add .
            git commit -m "${commitMessage}" || echo "No changes to commit"
            git push https://${GIT_USER}:${GIT_PASS}@github.com/rowidarafiek/jenkins.git ${branchName}
        """
    }
}

