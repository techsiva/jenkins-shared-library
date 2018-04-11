def call(body) {

	def config = [:]
	body.resolveStrategy = Closure.DELEGATE_FIRST
	body.delegate = config
	body()

	node {
		// Clean workspace before doing anything
		deleteDir()

		try {
			stages {
				stage ('Compile Stage') {   
					steps {
			        	sh "echo 'building ${config.projectName} ...'"
						withMaven(maven : 'maven_3_5_0') {
							sh 'mvn clean compile'
						}
					}
				}
				stage ('Testing Stage') {
					steps {
						withMaven(maven : 'maven_3_5_0') {
							sh 'mvn test'
						}
					}
				}
				stage ('Deployment Stage') {
					steps {
						withMaven(maven : 'maven_3_5_0') {
							sh 'mvn deploy'
						}
					}
				}
			}       
				post {
				success {
					mail to:"ruban.yuvaraj@gmail.com", subject: "SUCCESSFUL: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'", body: "SUCCESSFUL: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'"
				}
				failure {
					mail to:"ruban.yuvaraj@gmail.com", subject: "FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'", body: "FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'"
				}
			}
		} catch (err) {
			currentBuild.result = 'FAILED'
			throw err
		}
	}
}
