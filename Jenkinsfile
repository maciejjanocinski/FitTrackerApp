pipeline
 {
    agent any

    stages {
        stage('Build') {
            steps {
                script {
                    sh './gradlew clean build'
                }
            }
        }

        stage('Test') {
            steps {
                script {
                    sh './gradlew test'
                }
            }
        }
    }
 }