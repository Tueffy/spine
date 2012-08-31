package spine

class HomeController {

	SpineService spineService
	
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

		// check if user already exists
		println "Do check if user exists already!"
		def user = new User();
		user = spineService.getUser(params.email);

		if(user != null)
		{
			flash['message'] = "This email address is already registered!"
			redirect(controller:'home',action:'index')
			return
		}

		session.email = params.email
		
		redirect (controller : 'user', action : 'register', params:[ email: params.email])
	}

}
