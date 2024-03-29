package spine

import grails.plugin.mail.MailService
import groovy.text.SimpleTemplateEngine


class SmtpService {
	
	grails.plugin.mail.MailService mailService
	def grailsApplication
	
	private sendAccountActivationMail(String toMail, String fromMail){
		
		mailService.sendMail {
			//multipart true
			to toMail
			from fromMail
			println "Sending email: " + fromMail + " to " + toMail
			subject "Your Spine Account activation email"
			body(
				view: "/emails/account-activation",
				//plugin:"email-confirmation",
				model:[fromAddress:'team@spine-it.com', id: toMail]
			)			
		    //attachBytes 'spine-logo.gif','image/gif', new File(  "./web-app/images/emails/spine-logo.gif").readBytes()
		  }
	 }
	
	private sendFeedbackMail(String title, String message, String fromMail, String toMail){
		
		mailService.sendMail {
			//multipart true
			to toMail
			from toMail
			cc fromMail
			subject title
			body message
		}
	}

	private sendAccountRemovalMail(String toMail, String fromMail){
		
		mailService.sendMail {
			//multipart true
			to toMail
			from fromMail
			subject "Hello John"
			body(
				view: "/emails/account-removal",
				//plugin:"email-confirmation",
				model:[fromAddress:'team@spine-it.com', id:'111']
			)
			//attachBytes 'spine-logo.gif','image/gif', new File(  "./web-app/images/emails/spine-logo.gif").readBytes()
		  }
	 }
	
	private sendNewsMail(String toMail, String fromMail){
		
		mailService.sendMail {
			//multipart true
			to toMail
			from fromMail
			subject "Hello John"
			body(
				view: "/emails/news-weekly",
				//plugin:"email-confirmation",
				model:[fromAddress:'team@spine-it.com', id:'111']
			)
			//attachBytes 'spine-logo.gif','image/gif', new File(  "./web-app/images/emails/spine-logo.gif").readBytes()
		  }
	 }
	
	private sendPasswordRecoveryMail(String toMail, String fromMail){
		
		mailService.sendMail {
			//multipart true
			to toMail
			from fromMail
			subject "Hello John"
			body(
				view: "/emails/password-recovery",
				//plugin:"email-confirmation",
				model:[fromAddress:'team@spine-it.com', id: toMail]
			)
			//attachBytes 'spine-logo.gif','image/gif', new File(  "./web-app/images/emails/spine-logo.gif").readBytes()
		  }
	 }
	
	private sendUserInvitationMail(String toMail, String fromMail){
		
		mailService.sendMail {
			//multipart true
			to toMail
			from fromMail
			subject "Hello John"
			body(
				view: "/emails/user-invitation",
				//plugin:"email-confirmation",
				model:[fromAddress:'team@spine-it.com', id:'111']
			)
			//attachBytes 'spine-logo.gif','image/gif', new File(  "./web-app/images/emails/spine-logo.gif").readBytes()
		  }
	 }
}