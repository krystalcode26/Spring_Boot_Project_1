pipeline {
    agent any

    parameters {
        string(
            name: 'EC2_HOST_OVERRIDE',
            defaultValue: '',
            description: 'Optional: override the EC2 IP for this build only. Leave blank to use EC2_HOST from .env'
        )
    }

    environment {
        DOCKER_IMAGE = 'krystalyang/ems'
        DOCKER_TAG = "${env.BUILD_NUMBER}"
        DOCKER_PLATFORM = 'linux/amd64'
        EC2_USER = 'ec2-user'          // ec2-user for Amazon Linux, ubuntu for Ubuntu AMIs
        APP_PORT = '8088'
        CONTAINER_NAME = 'ems-backend'
        RESOLVED_EC2_HOST = "${params.EC2_HOST_OVERRIDE ?: env.EC2_HOST}"
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timeout(time: 30, unit: 'MINUTES')
        disableConcurrentBuilds()
    }

    triggers {
        githubPush()
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh 'chmod +x mvnw'
                sh './mvnw clean package -DskipTests -B'
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }

        stage('Docker Build & Push') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-credentials',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    sh '''
                        echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                        docker build --platform ${DOCKER_PLATFORM} \
                          -t "${DOCKER_IMAGE}:${DOCKER_TAG}" \
                          -t "${DOCKER_IMAGE}:latest" .
                        docker push "${DOCKER_IMAGE}:${DOCKER_TAG}"
                        docker push "${DOCKER_IMAGE}:latest"
                        docker logout
                    '''
                }
            }
        }

        stage('Deploy to EC2') {
            steps {
                script {
                    if (!env.RESOLVED_EC2_HOST?.trim()) {
                        error('EC2_HOST is not set. Add EC2_HOST to jenkins/.env or pass EC2_HOST_OVERRIDE.')
                    }
                }
                withCredentials([sshUserPrivateKey(
                    credentialsId: 'ec2-ssh-key',
                    keyFileVariable: 'SSH_KEY'
                )]) {
                    sh """
                        ssh -i "\$SSH_KEY" -o StrictHostKeyChecking=no ${EC2_USER}@${RESOLVED_EC2_HOST} '
                            set -e
                            docker pull ${DOCKER_IMAGE}:latest
                            if [ -f ~/ems-backend/docker-compose.yml ]; then
                              docker compose -f ~/ems-backend/docker-compose.yml pull app
                              docker compose -f ~/ems-backend/docker-compose.yml up -d app
                            elif [ -f ~/docker-compose.yml ]; then
                              docker compose -f ~/docker-compose.yml pull app
                              docker compose -f ~/docker-compose.yml up -d app
                            else
                              docker stop ${CONTAINER_NAME} 2>/dev/null || true
                              docker rm ${CONTAINER_NAME} 2>/dev/null || true
                              docker run -d \\
                                --name ${CONTAINER_NAME} \\
                                --restart unless-stopped \\
                                -p ${APP_PORT}:${APP_PORT} \\
                                -e SPRING_PROFILES_ACTIVE=docker \\
                                ${DOCKER_IMAGE}:latest
                            fi
                            docker image prune -f
                        '
                    """
                }
            }
        }
    }

    post {
        success {
            echo "Deployed ${DOCKER_IMAGE}:${DOCKER_TAG} to ${RESOLVED_EC2_HOST}"
        }
        failure {
            echo 'Pipeline failed. Check the stage logs above.'
        }
        always {
            cleanWs()
        }
    }
}
