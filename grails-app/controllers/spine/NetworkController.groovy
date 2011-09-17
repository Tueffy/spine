package spine


class NetworkController {
	def beforeInterceptor = [action:this.&checkUser,except:[]]

	def networkService

	
	
	def ajaxAutoComplete = {
		println "auto complete.."
	}
			
	def index = {
		//println params.filter
		[param : params.filter, user : session.user]
	}
	
	def linkProperties = {
		def allProperties = networkService.getProperties()
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
}
