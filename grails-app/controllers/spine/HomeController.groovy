package spine

class HomeController {
//small change
	def index = {
		if (session.user != null) {
			redirect (controller : 'network' , action : 'index')
		}
	}

	def about = {
			redirect (controller : 'home' , action : 'about')
	}	
	

	def how = {
			redirect (controller : 'home' , action : 'howitworks')
	}

	def doSignup = {
		session.email = params.email
		log.debug session.email
		
		redirect (controller : 'user', action : 'register', params:[ email: params.email])
	}

}
