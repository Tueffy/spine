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
		println params.filter.toString().tokenize(',')
		//def edges = networkService.getFilteredEdges( ['ECB','EnBW'])
		def edges = networkService.getFilteredEdges(params.filter.toString().tokenize(','))
		//println 'Filtered edges for rendering: ' + edges
		render (text:networkService.getGraphJSON(edges, session.username), contentType:"application/json", encoding:"UTF-8")
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
