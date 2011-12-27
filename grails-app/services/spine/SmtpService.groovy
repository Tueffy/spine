package spine

import grails.plugin.mail.MailService;


class SmtpService {
	
	grails.plugin.mail.MailService mailService 
	
	private sendRegistrationEmail(String toMail, String fromMail){
		mailService.sendMail {
			//multipart true
			to toMail
			from fromMail
			subject "Hello John"
			body(
				view: "/emails/account-registration",
				model:[fromAddress:'t.michelbach@gmail.com']
			)			
		    //attachBytes 'spine-logo.gif','image/gif', new File(  "./web-app/images/emails/spine-logo.gif").readBytes()
		  }
	 }
}