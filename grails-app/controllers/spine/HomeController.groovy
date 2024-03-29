package spine

class HomeController {

	SpineService spineService
	SmtpService smtpService
	
	def index = {
		if (session.user != null) {
			redirect (controller : 'network' , action : 'index')
		}
	}

	def about = {
		redirect (controller : 'home' , action : 'about')
	}	

	def feedback = {
		redirect (controller : 'home' , action : 'feedback')
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

	def doFeedback = {
		
		// check if user already exists
		smtpService.sendFeedbackMail(params.subject, params.message, params.email, "team@spine-it.com")
		flash['message'] = "Message has been sent!"
		redirect (controller : 'home', action : 'index')
	}
		
}
