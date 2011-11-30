package spine

import grails.converters.JSON

class UserController {

	def spineService

	/**
	 * nothing displayed in case of login
	 */
	def login = {
	}

	
	/**
	 * in case of registration, pre-fill email address
	 */
	def register = {
		
		//to do: delete email via session, but to differently
		[tmp_email : session.email]
	}


	/**
	 * login user and create the user object for the logged in user
	 */
	def doLogin = {
		
		// call the spine service to validate login
		def loggedInUser = spineService.loginUser(params.email, params.password)
		
		// if login successful then send JSON user to page, otherwise show error message
		if (loggedInUser != null) {
			session.user = loggedInUser
			
			//[user : loggedInUser as JSON]
			//println user
			redirect(controller:'network',action:'index')
		}
		else {
			flash['message'] = "Invalid user/password combination"
			redirect(controller:'user',action:'login')
		}
	}

	
	/**
	 * register user calls spine service to create a user without any tag yet
	 */
	def doRegister = {	
		
		// create map with parameters
		def userparams = ['firstName' : params.firstName, 'lastName' : params.lastName, 'city' : params.city, 'country' : params.country, 'email' : params.email, 'password' : params.password, 'image' : params.email+".jpg"]
		
		// call the spine service and depending on success either forward to login page or keep on register page
		if (spineService.createNewUser(userparams, null) != null) {
			flash['message'] = "New user has been created"
			redirect(controller:'user', action:'login')
		}
		else {
			// TO DO: better error handling, detailed information on what has failed
			flash['message'] = "User creation failed"
			redirect(controller:'user', action:'register')
		}
	}

	
	/**
	 * logout deletes the session object and redirects to the login page
	 */
	def doLogout = 	{
		session.user = null
		redirect(controller:'user', action:'login')
	}
	
	/**
	 * while registering user can upload a picture of himself throw AJAX
	 */
	def doAjaxPictureUpload = {
		def d = request.getFile('picture')
	}
	
	/**
	 * crop the image uploaded by the user throw AJAX 
	 */
	def doAjaxPictureCropping = {
//		def i
		// Image loaded, we want to crop it now
		
	}
}
