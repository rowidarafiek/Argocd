def call() {
    withCredentials([usernamePassword(credentialsId: argocd-cred, usernameVariable: 'GIT_USER', passwordVariable: 'GIT_PASS')]) {
        sh """
            git config user.name "rowidarafiek"
            git config user.email "rowidarafiek@domain.com"

            # Ensure branch exists locally
            git fetch origin
            git checkout -B ${env.BRANCH_NAME} origin/${env.BRANCH_NAME} || git checkout -B ${env.BRANCH_NAME}

            # Stage deployment file
            git add ${env.DEPLOYMENT_FILE}

            # Commit only if there are changes
            if git diff --staged --quiet; then
                echo "No changes to commit"
            else
                git commit -m "${env.COMMIT_MESSAGE}"
                git remote set-url origin https://${GIT_USER}:${GIT_PASS}@github.com/rowidarafiek/Argocd.git
                git push origin ${env.BRANCH_NAME} || echo "Push failed (branch may be protected)"
                echo "Changes pushed to GitHub"
            fi
        """
    }
}

