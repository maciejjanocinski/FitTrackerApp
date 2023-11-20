pipeline
 {
    agent any

    stages {
        stage('Build') {
            steps {
                script {
                    sh 'gradle clean build'
                }
            }
        }

        stage('Test') {
            steps {
                script {
                    sh 'gradle test'
                }
            }
        }
    }
 }