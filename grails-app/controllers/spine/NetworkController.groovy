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
		
		user = spineService.getUser(user.email)
		user.tags = [:]
		user.tags = spineService.getUserTags(user)
				
		def filter = params.filter
				
		// Get user network (get the first page)
		def Network network  = spineService.getUserNetwork(user, filter , 0, config.network.itemsPerPage)
		
		//Get stastistics for tags and badges
		def badges = spineService.getBadges(user);
		def hotTags = spineService.getHotTags();
		
		//Top 5 hot tags
		hotTags= [hotTags[1], hotTags[2], hotTags[3], hotTags[4], hotTags[5]]		
				
		[param : params.filter, user : user, network : network, badges: badges, hotTags: hotTags]
	}
	
	/**
	 * jQuery autocomplete compliant, returns a simple list of tags
	 */
	def ajaxAutoComplete = {
		def results = []
		def filter = params.filter ? params.filter : params.term
		render spineService.autocompleteTags(filter) as JSON
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
		
		def Network network  = spineService.getUserNetwork(user, params.filter , offset, config.network.itemsPerPage)
		
		render( template: "inc/page", model: [network: network]);
		
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
		
		def targetEmail = params.id
		def contextUser = new User()
		contextUser.email = session.user
		
		def NetworkedUser networkedUser = spineService.getUserInNetworkContext(contextUser, targetEmail)
		
		render networkedUser as JSON
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

