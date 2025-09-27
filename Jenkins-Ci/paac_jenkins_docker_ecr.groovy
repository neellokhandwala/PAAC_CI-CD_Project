pipeline {
    agent any
    tools {
        maven "MAVEN3.9"
        jdk "JDK17"
    }

    environment {
        AWS_DEFAULT_REGION = "ap-south-1"
        imageName = "965638719568.dkr.ecr.ap-south-1.amazonaws.com/cicdpro"
        cluster = "cicd_pro_cluster"
        service = "cicd_pro-service"
    }

    stages {
        stage('Fetch Code') {
            steps {
                git branch: 'docker', url: 'https://github.com/hkhcoder/vprofile-project.git'
            }
        }

        stage('Build') {
            steps {
                timeout(time: 10, unit: 'MINUTES') {
                    sh 'mvn clean install -DskipTests -B -V'
                }
            }
            post {
                success {
                    archiveArtifacts artifacts: '**/target/*.war'
                }
            }
        }

        stage('Unit Tests') {
            steps {
                script {
                    def mvnHome = tool 'MAVEN3.9'
                    def status = sh(script: "${mvnHome}/bin/mvn test", returnStatus: true)
                    if (status != 0) {
                        error "Unit tests failed."
                    }
                }
            }
        }

        stage('Checkstyle Analysis') {
            steps {
                sh 'mvn checkstyle:checkstyle'
                sh '''
                if grep -q "<error " target/checkstyle-result.xml; then
                    echo "Checkstyle violations found! Warning only."
                else
                    echo "No Checkstyle violations."
                fi
                '''
            }
        }

        stage('Build App Image') {
            steps {
                script {
                    dockerImage = docker.build("${imageName}:${BUILD_NUMBER}", "./Docker-files/app/multistage/")
                }
            }
        }

        stage('Upload App Image') {
            steps {
                withAWS(credentials: 'aws creds', region: 'ap-south-1') {
                    sh """
                    aws ecr get-login-password --region ${AWS_DEFAULT_REGION} | \
                    docker login --username AWS --password-stdin 965638719568.dkr.ecr.ap-south-1.amazonaws.com

                    docker push ${imageName}:${BUILD_NUMBER}
                    """
                    sh "docker tag ${imageName}:${BUILD_NUMBER} ${imageName}:latest"
                    sh "docker push ${imageName}:latest"
                }
            }
        }

        stage('Remove Local Images') {
            steps {
                sh 'docker image prune -f || echo "Warning: prune failed"'
            }
        }

        stage('Deploy to ECS') {
            steps {
                withAWS(credentials: 'aws creds', region: 'ap-south-1') {
                    sh "aws ecs update-service --cluster ${cluster} --service ${service} --force-new-deployment --region ${AWS_DEFAULT_REGION}"
                }
            }
        }
    }

    post {
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed. Check logs.'
        }
    }
}