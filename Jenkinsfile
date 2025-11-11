@Library('shared-library') _

pipeline {
    agent { label 'new-agent' }

    environment {  
        IMAGE_NAME = "rowidarafiek/app"
        IMAGE_TAG = "25"
        DOCKER_CREDS = 'dockerhub-cred'
        GIT_CREDS = 'github-cred'
        BRANCH_NAME = 'stag'
        COMMIT_MESSAGE = "Automated update from Jenkins ${IMAGE_TAG}"
    }

    stages {
        stage('Check Workspace') {
            steps {
                sh 'pwd'
                sh 'ls -l'
            }
        }

        stage('Run Unit Tests') {
            steps { script { unitTests() } }
        }

        stage('Build the Application') {
            steps { script { buildApp() } }
        }

        stage('Verify Build Artifact') {
            steps {
                sh 'ls -l target/'
            }
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

