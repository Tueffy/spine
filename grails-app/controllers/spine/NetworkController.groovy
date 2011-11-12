package spine

import grails.converters.JSON


class NetworkController {
	def beforeInterceptor = [action:this.&checkUser,except:[]]

	def networkService

	def ajaxAutoComplete = {
		println "test.."
		//def test = networkService.getProps()
		//println "New props function: " + test
		println "auto complete executed with: " + params.filter
		def inputText = params.filter
		def allProperties = networkService.getProperties('*')
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
		def neighbourParameters = ['userCenter' : session.user, 'filter' : params.filter]
		def n  = networkService.getNeighbours(neighbourParameters)
		
		def allusers = []
		n.each {
			def neighbour = networkService.getPropertiesByEmail(it.key)
			neighbour["distance"] = it.value
			allusers.add(neighbour)
		}
		
		//println allusers
		[param : params.filter, user : session.user, neighbours : allusers]
	}
	
	def linkProperties = {
		def allProperties = networkService.getProperties('*')
		[param : allProperties, user : session.user]
	}
	
	def connectPeople = {
		println "Connect: " + params
		if ( (params.sourcePerson != null) &&  (params.targetPerson != null) && (params.linkProps != null) ) {
			def result = networkService.connectPeople(params.sourcePerson, params.targetPerson, params.linkProps)
			[param : 'Successfully connected', user : session.user]
		}
		else if ((params.sourcePerson2 != null) &&  (params.targetPerson2 != null)){
			def result = networkService.disconnectPeople(params.sourcePerson2, params.targetPerson2)
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
		def edges = networkService.getUserEdges(params.userID.toString(), params.filter.toString().tokenize(','), 2)
		render (text:networkService.getGraphJSON(edges, session.username), contentType:"application/json", encoding:"UTF-8")
	}
	
	def graphEdgesJSON = {
		println params.source + params.target
		
		def sourceNode = networkService.findNodeByName(params.source)
		def targetNode = networkService.findNodeByName(params.target)
		println "Source1 " + sourceNode
		println "Source1 " + targetNode
		def edges = networkService.getAllEdges(sourceNode[0],targetNode[0])
		println edges
		//edges = ["Test","test"]
		render (text:networkService.getGraphEdgesJSON(edges), contentType:"application/json", encoding:"UTF-8")
		
	}
	
	def importGraph = {
		def String fileContent
		if (params.edgesFile != null) {
			println "Loading Edges file now.."
			def f = request.getFile('edgesFile')
			networkService.importEdges(f.getFileItem().getString())
		}
		if (params.nodesFile != null) {
			println "Loading Nodes file now.."
			def f = request.getFile('nodesFile')
			networkService.importNodes(f.getFileItem().getString())
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
	
	
	def getUser = {
		def user = new User()
		user.email ="test@test.com"
		render user as JSON
	}
}
