package spine

import java.io.File;

import org.codehaus.groovy.grails.web.context.ServletContextHolder;

import com.sun.net.httpserver.Authenticator.Success;

import grails.converters.JSON
import groovy.text.SimpleTemplateEngine
import pl.burningice.plugins.image.BurningImageService
import spine.FileService
import spine.SmtpService


class UserController {

	SpineService spineService
	BurningImageService burningImageService
	FileService fileService
	SmtpService smtpService

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
		if(params.picture != ""){
			cropUserPicture()
			userparams.image = userparams.email + "." + fileService.extractExtensionFromFileName(params.picture)
		}
		
		
		// call the spine service and depending on success either forward to login page or keep on register page
		if (spineService.createNewUser(userparams, null) != null) {
			flash['message'] = "New user has been created"
			
			smtpService.sendRegistrationEmail("alexander.michelbach@gmail.com","alexander.michelbach@gmail.com")
			
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
		if(params.email == null) params.email = session.user.email
		int minHeightWidhth = 50
		if(params.crop_w.toInteger() < minHeightWidhth || params.crop_h.toInteger() < minHeightWidhth)
			return false
		
		// Crop the picture and dealing with the tmp picture
		String inputFilename = params.picture
		String inputDir = ServletContextHolder.servletContext.getRealPath('/') + "uploads/tmp/"
		String inputFileExtension = fileService.extractExtensionFromFileName(inputFilename)
		String outputDir = ServletContextHolder.servletContext.getRealPath('/') + "images/profiles/"
		burningImageService.doWith(inputDir + inputFilename, outputDir).execute {
			it.crop(params.crop_x1.toInteger(), params.crop_y1.toInteger(), params.crop_w.toInteger(), params.crop_h.toInteger())
		}
		
		
		// Delete the old file
		Boolean oldFileDeleted
		if(session.user.imagePath != null)
		{
			File oldFileToDelete = new File(outputDir + session.user.imagePath)
			if(oldFileToDelete.exists() && !oldFileToDelete.directory)
			{
				oldFileDeleted = oldFileToDelete.delete();
				println "\nFile deleted = ${oldFileDeleted}"
			}
		}
		
		// Finalize upload by putting the cropped picture to its right place
		println "File renaming = "
		File file = new File(outputDir + inputFilename)
		String outputFileName = params.email + "." + inputFileExtension
		Boolean fileMoved = file.renameTo(new File(outputDir + outputFileName))
		println fileMoved.toString() + "\n" + outputDir + outputFileName
		
		// If moving failed, delete the tmp file
		if(!fileMoved)
			file.delete() 

		return fileMoved
	}
	
	def profile = {
		
	
		
		def user = session.user
		
		println session
		
		return [ user: session.user ]
	}
	
	def updateProfile = {
		// Get the current user
		User user = session.user
		
		// create map with parameters
		def userparams = [
			'firstName' : params.firstname,
			'lastName' : params.lastname,
			'city' : params.city,
			'country' : params.country,
			'email' : params.email,
			'password' : params.password, 
			'imagePath' : "",
			'freeText' : params.freeText ]
		
		// If an image has been sent, apply cropping
		println "picture = ${params.picture}"
		if(params.picture != "")
		{
			cropUserPicture()
			userparams.imagePath = session.user.email + "." + fileService.extractExtensionFromFileName(params.picture)
		}
		
		Boolean success = spineService.updateUserProfile(user, userparams)
		[ user: userparams ]
		
		if(success)
		{
			// Reload the user in the session
			session.user = spineService.getUser(user.email) 
			flash.message = "Profile updated ! ";
			redirect(action: "profile")
		}
		else
			render(view:"profile")
	}
	
	
	
}
