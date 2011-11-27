package spine

class ImportDataService {

    static transactional = false
    def networkService
	def graphCommunicatorService
	 
	

    def importEdges(String file) {
        println 'Starting to connect nodes...'
        def props = [:]
        def input = file.splitEachLine("\t") {fields ->
            props['startNode'] = fields[0]
            props['endNode'] = fields[1]
            props['tags'] = fields[2]
            println 'Connecting : ' + fields[0] + ' -> ' + fields[1] + ' with ' + fields[2]
            networkService.createRelationship(props)
            networkService.setProperty(props)
        }
        println "Edges loaded!"
    }

    def importNodes(String file) {
        //per node from file, create a hashmap with properties, which are imported
        def input = file.splitEachLine("\t") {fields ->
            //println fields
            def props = [:]
            props['firstName'] = fields[0]
            props['lastName'] = fields[1]
            props['email'] = fields[2]
            props['password'] = fields[3]
            props['city'] = fields[4]
            props['country'] = fields[5]
            props['image'] = fields[6]
            props['freeText'] = 'Madness? This is Sparta!'
            networkService.createNode(props)
        }
        println "Nodes created!"
    }
	
	def checkDB() {
		def results = [:] //map with results, nodes, rels, properties, index
		
		//nodes in indices
		def json = graphCommunicatorService.neoGet('/db/data/index/node/names/email', ['query': 'email:*'])
        results['nodesEmailIndexSize'] = json.data.size
		json = graphCommunicatorService.neoGet('/db/data/index/node/names/lastName', ['query': 'lastName:*'])
		results['nodesLastNameIndexSize'] = json.data.size
		json = graphCommunicatorService.neoGet('/db/data/index/node/names/firstName', ['query': 'firstName:*'])
		results['nodesFirstNameIndexSize'] = json.data.size
		json = graphCommunicatorService.neoGet('/db/data/index/node/names/country', ['query': 'country:*'])
		results['nodesCountryIndexSize'] = json.data.size
		json = graphCommunicatorService.neoGet('/db/data/index/node/names/city', ['query': 'city:*'])
		results['nodesCityIndexSize'] = json.data.size
		json = graphCommunicatorService.neoGet('/db/data/index/node/names/freeText', ['query': 'freeText:*'])
		results['nodesFreeTextIndexSize'] = json.data.size
		
		//rels in indices
		json = graphCommunicatorService.neoGet('/db/data/index/relationship/edges', ['query': 'tag:*'])
		results['relsTagsIndexSize'] = json.data.size
		
		//total nodes via REST (should match index!)
		(1..1000).each { i->
			json = graphCommunicatorService.neoGet('/db/data/node/' + i)
		}
		
		println results
	}
}
