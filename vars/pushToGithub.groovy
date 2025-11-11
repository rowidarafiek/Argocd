def call() {
    withCredentials([usernamePassword(credentialsId: env.GIT_CREDS, usernameVariable: 'GIT_USER', passwordVariable: 'GIT_PASS')]) {
        sh """
            git config user.name "rowidarafiek"
            git config user.email "rowidarafiek@domain.com"

            git checkout -B ${env.BRANCH_NAME}
            git add ${env.DEPLOYMENT_FILE}

            if git diff --staged --quiet; then
                echo "No changes to commit"
            else
                git commit -m "${env.COMMIT_MESSAGE}"
                git push https://${GIT_USER}:${GIT_PASS}@github.com/rowidarafiek/Argocd.git HEAD:${env.BRANCH_NAME}
                echo "Changes pushed to GitHub"
            fi
        """
    }
}
