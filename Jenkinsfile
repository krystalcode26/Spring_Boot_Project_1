/* Definition of CI/CD pipeline flow*/
pipeline {
    agent any

    /* Loading parameters - Here pass EC2 IP address when you try to deploy*/
    parameters {
        string(
            name: 'EC2_HOST_OVERRIDE',
            defaultValue: '',
            description: 'Optional: override the EC2 IP for this build only. Leave blank to use EC2_HOST from .env'
        )
        booleanParam(
            name: 'SONAR_ENABLED',
            defaultValue: false,
            description: 'Run SonarQube analysis and Quality Gate (requires SonarQube at http://sonarqube:9000)'
        )
    }

    /* set up environment*/
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

    /* the branch to check out*/
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Test & Coverage') {
            steps {
                sh 'chmod +x mvnw'
                sh './mvnw clean test -B'
            }
            post {
                always {
                    junit 'target/surefire-reports/**/*.xml'
                }
            }
        }

        stage('SonarQube Analysis') {
            when {
                expression { params.SONAR_ENABLED == true }
            }
            steps {
                withSonarQubeEnv('sonarqube') {
                    // Reuse JaCoCo from Test stage; Maven polls Sonar for QG (no Jenkins webhook needed)
                    sh './mvnw sonar:sonar -DskipTests -Dsonar.qualitygate.wait=true -B'
                }
            }
            post {
                failure {
                    echo 'SonarQube Quality Gate failed. Open http://localhost:9000/dashboard?id=ems-backend → Quality Gate tab for the exact condition.'
                    echo 'Common fixes: review Security Hotspots in Sonar UI, or confirm coverage exclusions in pom.xml.'
                }
            }
        }

        stage('Package') {
            steps {
                sh './mvnw package -DskipTests -B'
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }

        /* build docker image*/
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
                /* check whether the IP address there*/
                script {
                    if (!env.RESOLVED_EC2_HOST?.trim()) {
                        error('EC2_HOST is not set. Add EC2_HOST to jenkins/.env or pass EC2_HOST_OVERRIDE.')
                    }
                }
                /*SSH into EC2 - use this private key*/
                sh """
                    if [ ! -f /run/secrets/ec2-ssh-key.pem ]; then
                      echo 'SSH key not found at /run/secrets/ec2-ssh-key.pem'
                      echo 'Set SSH_KEY_PATH in jenkins/.env and restart Jenkins (docker compose up -d).'
                      exit 1
                    fi
                    install -m 400 /run/secrets/ec2-ssh-key.pem /tmp/ec2-ssh-key.pem
                    ssh -i /tmp/ec2-ssh-key.pem -o StrictHostKeyChecking=no ${EC2_USER}@${RESOLVED_EC2_HOST} '
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
                    rm -f /tmp/ec2-ssh-key.pem
                """
            }
        }
    }

    post {
        success {
            echo "Deployed ${DOCKER_IMAGE}:${DOCKER_TAG} to ${RESOLVED_EC2_HOST}"
            echo 'Ensure EC2 has GOOGLE_CLIENT_ID, GOOGLE_CLIENT_SECRET, and JWT_SECRET set for secured APIs.'
        }
        failure {
            echo 'Pipeline failed. Check the stage logs above.'
        }
        always {
            cleanWs()
        }
    }
}
