@Library('shared-library') _

pipeline {
    agent { label 'new-agent' }

    environment {
        IMAGE_NAME = "rowidarafiek/app"
        IMAGE_TAG = "${env.BUILD_NUMBER}"
        DOCKER_CREDS = 'dockerhub-cred'
        GIT_CREDS = 'github-pat'
        BRANCH_NAME = "${env.BRANCH_NAME}" // Multibranch will provide dev/stag/prod
        COMMIT_MESSAGE = "Automated update from Jenkins ${IMAGE_TAG}"
        DEPLOYMENT_FILE = 'deployment.yaml'
    }

    stages {
        stage('Run Unit Tests') {
            steps { script { unitTests() } }
        }

        stage('Build Application') {
            steps { script { buildApp() } }
        }

        stage('Verify Build Artifact') {
            steps { sh 'ls -l target/' }
        }

        stage('Build Docker Image') {
            steps { script { buildDockerImage() } }
        }

        stage('Push Docker Image') {
            steps { script { pushDockerImage() } }
        }

        stage('Update Deployment YAML') {
            steps { script { updateDeploymentYaml() } }
        }

        stage('Push to GitHub') {
            steps { script { pushToGithub() } }
        }
    }

    post {
        always { echo 'Pipeline completed' }
        success { echo 'Pipeline completed successfully' }
        failure { echo 'Pipeline completed with failure' }
    }
}

