package spine

import grails.converters.JSON
import org.codehaus.groovy.grails.commons.*


class NetworkController {
	
	def config = ConfigurationHolder.config
	
	def beforeInterceptor = [action:this.&checkUser,except:[]]

	def spineService

	def ajaxAutoComplete = {
		println "test.."
		//def test = networkService.getProps()
		//println "New props function: " + test
		println "auto complete executed with: " + params.filter
		def inputText = params.filter
		def allProperties = spineService.getProperties('*')
		//lookup properties with filter
		def choices = '<ul>' //ajax list
		allProperties.each {
			def choice = it.substring(0,Math.min(inputText.length(),it.length()))
			println 'Choice is: ' + choice
			if (choice.compareToIgnoreCase(inputText) == 0) {
				//println it
				choices = choices + '<li>' + it + '</li>'
			}
		}
		choices = choices + '</ul>'
		render choices
	}
			
	
	
	def index = {
		// Get the user
		User user = new User()
		if(params.user !=null)
			user.email = params.user			
		else
			user.email = session.user
		
		user = spineService.getUser(user.email)
			
		// Get user network (get the first page)
		def n = null
		n  = spineService.getUserNetwork(user, null, 0, config.network.itemsPerPage)
		for ( i in n ) {
				def userFromList = new User()
				userFromList.email = i.email
				def tags = spineService.getUserTags(userFromList)
				i.tags = tags
				println i
		}
		
		
		//println params.user
		/*
		def neighbourParameters = ['userCenter' : session.user, 'filter' : params.filter]
		def n  = spineService.getNeighbours(neighbourParameters)
		
		def allusers = []
		n.each {
			def neighbour = spineService.getPropertiesByEmail(it.key)
			neighbour["distance"] = it.value
			allusers.add(neighbour)
		}
		*/
		//println allusers
		[param : params.filter, user : user, neighbours : n]
		//[param : params.filter, user : session.user]
	}
	
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
		def n  = spineService.getUserNetwork(user, null, offset, config.network.itemsPerPage)
		render( template: "page", model: [neighbours: n]);
	}
	
	def linkProperties = {
		def allProperties = spineService.getProperties('*')
		[param : allProperties, user : session.user]
	}
	
	def connectPeople = {
		println "Connect: " + params
		if ( (params.sourcePerson != null) &&  (params.targetPerson != null) && (params.linkProps != null) ) {
			def result = spineService.connectPeople(params.sourcePerson, params.targetPerson, params.linkProps)
			[param : 'Successfully connected', user : session.user]
		}else if ((params.sourcePerson2 != null) &&  (params.targetPerson2 != null)){
			def result = spineService.disconnectPeople(params.sourcePerson2, params.targetPerson2)
			[param : result, user : session.user]
		}
	}
	
	def filterGraph = {
		//println params.filterProperty
		redirect(controller:'network', action:'index', params : ['filter':params.filterProperty])
	} 
	
	def graphJSON = { //callback used by visualisation
		//TODO Jure, merge filter and properties
		println "Filters used for rendering: " + params.filter.toString().tokenize(',') + params.userID.toString()
		def edges = spineService.getUserEdges(params.userID.toString(), params.filter.toString().tokenize(','), 2)
		render (text:spineService.getGraphJSON(edges, session.username), contentType:"application/json", encoding:"UTF-8")
	}
	
	def graphEdgesJSON = {
		println params.source + params.target
		
		def sourceNode = spineService.findNodeByName(params.source)
		def targetNode = spineService.findNodeByName(params.target)
		println "Source1 " + sourceNode
		println "Source1 " + targetNode
		def edges = spineService.getAllEdges(sourceNode[0],targetNode[0])
		println edges
		//edges = ["Test","test"]
		render (text:spineService.getGraphEdgesJSON(edges), contentType:"application/json", encoding:"UTF-8")
		
	}
	
	def importGraph = {
		def String fileContent
		if (params.edgesFile != null) {
			println "Loading Edges file now.."
			def f = request.getFile('edgesFile')
			spineService.importEdges(f.getFileItem().getString())
		}
		if (params.nodesFile != null) {
			println "Loading Nodes file now.."
			def f = request.getFile('nodesFile')
			spineService.importNodes(f.getFileItem().getString())
		}
		redirect(controller:'network', action:'index')
	}

	// need to be shifted to the user service
	def checkUser() {
		if(!session.user) {
			// i.e. user not logged in
			redirect(controller:'user',action:'login')
			return false
		}
	}
	 
	
	/**
	 * 
	 * @author Thomas M. Michelbach, Christian Tueffers, Ingmar Mueller, Jure Zakotnik
	 */
	def getUser  = {
		
		def user = new User()
		user.email = params.id
	
		//@TODO: Optimize with direct call to getUser
		def n = null
		n  = spineService.getUserNetwork(user, null, 0, 20)
		
		
		for ( i in n ) {
				def email1 = params.id;
				def email2 = i.email;
				if(email1.equalsIgnoreCase(email2)){					
					user = i
					println "TEST"
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
		
		println tags
		
		render tags as JSON
	}	
	
	/**
	 * 
	 * 
	 * @author Thomas M. Michelbach, Christian Tueffers, Ingmar Mueller, Jure Zakotnik
	 */
	def removeTag = {
		def user = new User()
		user.email = params.user
		
		//println params.user
		//println params.tag
		
		def tags = spineService.removeTag(session.user, user, params.tag)
		render user as JSON
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
		println params.e
		
		//spineService.addTag(session.user, params.id, params.e)
		
		render user as JSON
	}

	
	/**
	*
	*
	* @author Thomas M. Michelbach, Christian Tueffers, Ingmar Mueller, Jure Zakotnik
	*/
   def getUserStatistics = {
	   
	   def statistics = [badgesNumber : "10", tagsNumber: "50"]
	   
	   render statistics as JSON
   }
   
}

