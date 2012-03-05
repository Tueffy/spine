package spine

import java.lang.ref.ReferenceQueue.Null;

import grails.converters.JSON
import org.codehaus.groovy.grails.commons.*
import spine.SmtpService 

class NetworkController {
	
	def config = ConfigurationHolder.config
	
	def beforeInterceptor = [action:this.&checkUser,except:[]]

	def spineService
	def smtpService

	/**
	 * 
	 * 
	 */
	def index = {
				
		// Get the user
		User user = new User()
		if(params.user !=null)
			user.email = params.user			
		else
			user.email = session.user
		
		user = spineService.getUser(user.email);
		user.tags = [];
		user.tags = spineService.getUserTags(user);
				
		def filter = params.filter
				
		// Get user network (get the first page)
		def n = null
		n  = spineService.getUserNetwork(user, filter , 0, config.network.itemsPerPage)
		for ( i in n ) {
				def userFromList = new User()
				userFromList.email = i.email
				def tags = spineService.getUserTags(userFromList)
				i.tags = tags
		}
		
		//Get stastistics for tags and badges
		def badges = spineService.getBadges(user);
		def hotTags = spineService.getHotTags();
		
		//Top 5 hot tags
		hotTags= [hotTags[1], hotTags[2], hotTags[3], hotTags[4], hotTags[5]]		
				
		[param : params.filter, user : user, neighbours : n, badges: badges, hotTags: hotTags]
	}
	
	/**
	 * 
	 */
	def ajaxAutoComplete = {
		def results = spineService.autocompleteTags(params.filter)
		[ tags: results ]
	}
	
	/**
	 * 
	 */
	def ajaxPage = {
		
		// Get the user
		User user = new User()
		if(params.user !=null)
			user.email = params.user
		else
			user.email = session.user
		
		// Then get the page, according to the configured pagination
		if(params.page == null) params.page = 1
		int offset = params.page.toInteger() - 1
		offset *= config.network.itemsPerPage
		def n  = spineService.getUserNetwork(user, params.filter, offset, config.network.itemsPerPage)
		
		render( template: "inc/page", model: [neighbours: n]);
		
	}
	
	/**
	 * 
	 */
	def linkProperties = {
		
		def allProperties = spineService.getProperties('*')
		[param : allProperties, user : session.user]
	}
	
	/**
	 * 
	 */
	def connectPeople = {
		
		log.debug "Connect: ${params}"
		
		if ( (params.sourcePerson != null) &&  (params.targetPerson != null) && (params.linkProps != null) ) {
			def result = spineService.connectPeople(params.sourcePerson, params.targetPerson, params.linkProps)
			[param : 'Successfully connected', user : session.user]
		}else if ((params.sourcePerson2 != null) &&  (params.targetPerson2 != null)){
			def result = spineService.disconnectPeople(params.sourcePerson2, params.targetPerson2)
			[param : result, user : session.user]
		}
		
	}
	
	/**
	 * 
	 */
	def filterGraph = {
		
		log.debug "Filter ${params.filterProperty}"
		
		redirect(controller:'network', action:'index', params : ['filter':params.filterProperty])
		
	} 
	
	/**
	 * Callback used by visualisation
	 * 
	 */
	def graphJSON = {
				
		//TODO Jure, merge filter and properties
		log.debug "Filters used for rendering: " + params.filter.toString().tokenize(',') + params.userID.toString()
		
		def edges = spineService.getUserEdges(params.userID.toString(), params.filter.toString().tokenize(','), 2)
		render (text:spineService.getGraphJSON(edges, session.username), contentType:"application/json", encoding:"UTF-8")
		
	}
	
	/**
	 * 
	 */
	def graphEdgesJSON = {
		
		def sourceNode = spineService.findNodeByName(params.source)
		def targetNode = spineService.findNodeByName(params.target)
		
		log.debug "Source node ${sourceNode}"
		log.debug "Target node ${targetNode}"
		
		def edges = spineService.getAllEdges(sourceNode[0],targetNode[0])
		
		log.debug "Edges: ${edges}"
		//edges = ["Test","test"]
		render (text:spineService.getGraphEdgesJSON(edges), contentType:"application/json", encoding:"UTF-8")
		
	}
	
	/**
	 * 
	 */
	def importGraph = {
		
		def String fileContent
		
		if (params.edgesFile != null) {
			log.debug "Loading Edges file now.."
			def f = request.getFile('edgesFile')
			spineService.importEdges(f.getFileItem().getString())
		}
		if (params.nodesFile != null) {
			log.debug "Loading Nodes file now.."
			def f = request.getFile('nodesFile')
			spineService.importNodes(f.getFileItem().getString())
		}
		
		redirect(controller:'network', action:'index')
		
	}

	/**
	 * Need to be shifted to the user service
	 * 
	 * @return
	 */
	def checkUser() {
		
		if(!session.user) {
			// i.e. user not logged in
			redirect(controller:'user',action:'login')
			return false
		}else{
			return true
		}
		
	}
	 
	
	/**
	 * 
	 * @author Thomas M. Michelbach, Christian Tueffers, Ingmar Mueller, Jure Zakotnik
	 */
	def getUser  = {
		
		def user = new User()
		//user.email = params.id
		user = spineService.getUser(params.id);
	
		//@TODO: Optimize with direct call to getUser
		def n = null
		n  = spineService.getUserNetwork(user, null, 0, 30)		
		
		log.debug "User: ${user}"
		
		for ( i in n ) {
				def email1 = params.id;
				def email2 = i.email;
				if(email1.equalsIgnoreCase(email2)){					
					user = i
					break
				}
		}		
		
		render user as JSON
		
	}
	
	/**
	 * 
	 * @author Thomas M. Michelbach, Christian Tueffers, Ingmar Mueller, Jure Zakotnik
	 */
	def getTags = {
		
		def user = new User()
		user.email = params.id				
		def tags = spineService.getUserTags(user)		
				
		render tags as JSON
		
	}	
	
	/**
	 * 
	 * 
	 * @author Thomas M. Michelbach, Christian Tueffers, Ingmar Mueller, Jure Zakotnik
	 */
	def removeTag = {
		
		def sessionUser = new User()
		sessionUser = session.user
		
		def selectedUser = new User()
		selectedUser = spineService.getUser(params.user) 
		
		def tag = params.id
		
		def tags = spineService.removeTag(sessionUser, selectedUser, tag)
		
		render sessionUser as JSON
		
	}
	
	/**
	 * 
	 * 
	 * @author Thomas M. Michelbach, Christian Tueffers, Ingmar Mueller, Jure Zakotnik
	 */
	def setTag = {
		
		def user = new User()
		user.email ="test@test.com"
		
		render user as JSON
		
	}
	
	/**
	 * 
	 * 
	 * @author Thomas M. Michelbach, Christian Tueffers, Ingmar Mueller, Jure Zakotnik
	 */
	def addTag = {
		
		def user = new User()
		
		user = session.user
		
		spineService.addTag(user.email, params.email, params.tag)
		
		//render user as JSON
		def response = [tag: params.tag]
		
		render response as JSON
	}

	
	/**
	* TODO: Check if needed. Right now feature is impleted in the index method in this controller
	*
	* @author Thomas M. Michelbach, Christian Tueffers, Ingmar Mueller, Jure Zakotnik
	*/
   def getUserStatistics = {
	   
	   def statistics = [badgesNumber : "10", tagsNumber: "50"]
	   
	   render statistics as JSON
	   
   }
   
   
   /**
   *
   *
   * @author Thomas M. Michelbach, Christian Tueffers, Ingmar Mueller, Jure Zakotnik
   */
  def inviteNewUser = {
	  
	  smtpService.sendUserInvitationMail("t.michelbach@gmail.com", "t.michelbach@gmail.com")	  
	  def status = [success : "true"]
	  
	  render status as JSON
	  
  }
   
}

