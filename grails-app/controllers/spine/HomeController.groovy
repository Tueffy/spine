package spine

class HomeController {
//small change
	def index = {
		if (session.user != null) {
			redirect (controller : 'network' , action : 'index')
		}
	}

	def about = {
			redirect (controller : 'home' , action : 'index')
	}

	def contact = {
			redirect (controller : 'home' , action : 'index')
	}

	def doSignup = {
		session.email = params.email
		println session.email
		redirect (controller : 'user', action : 'register')
	}

}