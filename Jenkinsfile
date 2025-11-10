@Library('shared-library') _

pipeline {
    agent { label 'linux-docker' }

    environment {
        IMAGE_NAME = "rowidarafiek/app"
        IMAGE_TAG  = "${env.BUILD_NUMBER}"
        DOCKER_CREDS = 'dockerhub-cred'
        GIT_BRANCH  = "${env.BRANCH_NAME}"
        GIT_COMMIT_MESSAGE = "Automated update from Jenkins ${env.BUILD_NUMBER}"
    }

    stages {
        stage('Run Unit Tests') {
            steps {
                unitTests()
            }
        }

        stage('Build the Application') {
            steps {
                buildApp()
            }
        }

        stage('Build Docker Image') {
            steps {
                buildDockerImage(IMAGE_NAME, IMAGE_TAG)
            }
        }

        stage('Push Docker Image to Registry') {
            steps {
                pushDockerImage(IMAGE_NAME, IMAGE_TAG)
            }
        }

        stage('Update Deployment YAML') {
            steps {
                updateDeploymentYaml(IMAGE_NAME, IMAGE_TAG)
            }
        }

        stage('Push Changes to GitHub') {
            steps {
                pushToGithub(GIT_BRANCH, GIT_COMMIT_MESSAGE)
            }
        }

        stage('Remove Local Docker Image') {
            steps {
                removeDockerImage(IMAGE_NAME, IMAGE_TAG)
            }
        }
    }

    post {
        always {
            echo 'Pipeline completed'
        }
        success {
            echo 'Pipeline completed successfully'
        }
        failure {
            echo 'Pipeline completed with failure'
        }
    }
}

