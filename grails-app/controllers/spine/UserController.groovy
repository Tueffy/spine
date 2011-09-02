package spine

class UserController {

	def networkService

	def login = {
	}
	
	def register = {
	}

	def doLogin = {
	
		def user = User.findWhere(email:params['email'],
			password:params['password'])
		session.user = user
		if (user)
			redirect(controller:'network',action:'index')
		else
			redirect(controller:'user',action:'login')
	}
	
	def doRegister = {	
		def result = networkService.createNode([name : params.name, email : params.email, password : params.password])
		redirect(controller:'user', action:'login')
	}
	
	def doLogout = 	{
		session.user = null
		redirect(controller:'user', action:'login')
	}
}
