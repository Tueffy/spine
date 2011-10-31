package spine

class UserController {

	def networkService

	// for the login gsp
	def login = {
	}
	
	// for the register gsp
	def register = {
	}

	// login user
	def doLogin = {

		// get json with user information
		def user = networkService.getPropertiesByEmail(params.email)
		println 'Login service, recognized user: ' + user
		// validate, if password is correct, then switch to network view, 
		//otherwise remain on the site
		if (user.password == params['password']) {
			session.user = user
			session.username = user.email
			redirect(controller:'network',action:'index')
		}
		else {
			flash['message'] = "Invalid user/password combination"
			redirect(controller:'user',action:'login')
		}
	}

	//register a new user
	def doRegister = {	
		
		//validate, if email already existing
		List result = networkService.findNodeByEmail(params.email)

		//either refuse (message is not properly shown), or create the new user	
		if (result.size() > 0) {
			flash['message'] = "User already existing"
			redirect(controller:'user', action:'register')
		} 
		else {
			//TODO the service below needs to be updated to the current property structure
			//networkService.createNode(['name' : params.name, 'email' : params.email, 'password' : params.password])
			redirect(controller:'user', action:'login')
		}
	}
	
	// delete the user session object
	def doLogout = 	{
		session.user = null
		session.username = null
		redirect(controller:'user', action:'login')
	}
}
