pipeline
 {
    agent any 
    tools {  
      gradle '8.4'
    }   

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
