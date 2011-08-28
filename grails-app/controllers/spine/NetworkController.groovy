package spine


class NetworkController {

	def networkService

	def ajaxAutoComplete = {
		println "auto complete.."
	}
			
	def index = {
		//println params.filter
		[param : params.filter]
	}
	
	def linkProperties = {
		def allProperties = networkService.getProperties()
		[param : allProperties]
	}
	
	def connectPeople = {
		println "Connect: " + params
		if ( (params.sourcePerson != null) &&  (params.targetPerson != null) && (params.linkProps != null) ) {
			def result = networkService.connectPeople(params.sourcePerson, params.targetPerson, params.linkProps)
			[param : 'Successfully connected']
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
		render (text:networkService.getGraphJSON(edges), contentType:"application/json", encoding:"UTF-8")
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
}
