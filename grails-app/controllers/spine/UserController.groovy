package spine

import java.io.File;

import org.codehaus.groovy.grails.web.context.ServletContextHolder;
import grails.converters.JSON
import pl.burningice.plugins.image.BurningImageService
import spine.FileService

class UserController {

	def spineService
	BurningImageService burningImageService
	FileService fileService

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
		def userparams = [
			'firstName' : params.firstName,
			'lastName' : params.lastName,
			'city' : params.city,
			'country' : params.country,
			'email' : params.email,
			'password' : params.password, 
			'image' : "",
			'freeText' : params.freetext ]
		
		// If an image has been sent, apply cropping
		if(params.picture != "")
		{
			cropUserPicture()
			userparams.image = userparams.email + "." + fileService.extractExtensionFromFileName(params.picture)
		}
		
		
		
		
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
	
	private Boolean cropUserPicture()
	{
		int minHeightWidhth = 50
		if(params.crop_w.toInteger() < minHeightWidhth || params.crop_h.toInteger() < minHeightWidhth)
			return false
		
		String inputFilename = params.picture
		String inputDir = ServletContextHolder.servletContext.getRealPath('/') + "uploads/tmp/"
		String inputFileExtension = fileService.extractExtensionFromFileName(inputFilename)
		String outputDir = ServletContextHolder.servletContext.getRealPath('/') + "images/profiles/"
		burningImageService.doWith(inputDir + inputFilename, outputDir).execute {
			it.crop(params.crop_x1.toInteger(), params.crop_y1.toInteger(), params.crop_w.toInteger(), params.crop_h.toInteger())
		}
		
		File file = new File(outputDir + inputFilename)
		String outputFileName = params.email + "." + inputFileExtension 
		fileService.renameFile(file, outputFileName) 

		return true
	}
	
}
