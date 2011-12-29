package spine

import java.security.MessageDigest

class ImportDataService {

    static transactional = false
    def NetworkService networkService
	def GraphCommunicatorService graphCommunicatorService
	 

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
            props['freeText'] = fields[7]
			props['company'] = fields[8]
			props['department'] = fields[9]
			props['jobTitle'] = fields[10]
			props['gender'] = fields[11]
			props['birthday'] = fields[12]
			props['phone'] = fields[13]
			props['mobile'] = fields[14]
			props['status'] = fields[15]
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
		
		// Check integrity of users we use for testing purpose
		results['nodesChecksum'] = []
		def mainUsers = ['jure.zakotnik@techbank.com', 
			'christian.tueffers@techbank.com', 
			'ingmar.mueller@techbank.com', 
			'monika.hoppe@techbank.com']
		mainUsers.eachWithIndex () {
			it, i -> results['nodesChecksum'][i] = networkService.readNode(it)
		}
		
		println results
		return results
	}
}
