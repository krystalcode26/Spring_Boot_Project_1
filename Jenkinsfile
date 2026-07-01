pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'krystalyang/ems'
        DOCKER_PLATFORM = 'linux/amd64'
        EC2_USER = 'ec2-user'
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timeout(time: 30, unit: 'MINUTES')
        disableConcurrentBuilds()
    }

    triggers {
        // GitHub webhook: push events on dev branch → Build when a change is pushed to GitHub
        githubPush()
    }

    stages {
        stage('Checkout') {
            when {
                branch 'dev'
            }
            steps {
                checkout scm
            }
        }

        stage('Test') {
            when {
                branch 'dev'
            }
            steps {
                sh 'chmod +x mvnw'
                sh './mvnw clean test'
            }
        }

        stage('Docker Build & Push') {
            when {
                branch 'dev'
            }
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    sh '''
                        echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                        docker build --platform ${DOCKER_PLATFORM} \
                          -t ${DOCKER_IMAGE}:latest \
                          -t ${DOCKER_IMAGE}:${GIT_COMMIT} .
                        docker push ${DOCKER_IMAGE}:latest
                        docker push ${DOCKER_IMAGE}:${GIT_COMMIT}
                    '''
                }
            }
        }

        stage('Deploy to EC2') {
            when {
                branch 'dev'
            }
            steps {
                script {
                    if (env.JENKINS_IN_DOCKER == 'true') {
                        sh '''
                            COMPOSE_FILE="${COMPOSE_DIR}/docker-compose.yml"
                            docker compose -f "$COMPOSE_FILE" pull app
                            docker compose -f "$COMPOSE_FILE" up -d app
                            sleep 15
                            curl -sf http://localhost:8088/actuator/health | head -c 500 \
                              || echo "Health check failed — see: docker compose -f $COMPOSE_FILE logs app --tail 30"
                        '''
                    } else {
                        sshagent(credentials: ['ec2-ssh-key']) {
                            sh '''
                                ssh -o StrictHostKeyChecking=no ${EC2_USER}@${EC2_HOST} \
                                  'bash -s' < scripts/ec2-deploy.sh
                            '''
                        }
                    }
                }
            }
        }
    }

    post {
        success {
            echo 'Deployed to EC2 successfully.'
        }
        failure {
            echo 'Pipeline failed — check logs above.'
        }
        always {
            sh 'docker logout || true'
        }
    }
}
