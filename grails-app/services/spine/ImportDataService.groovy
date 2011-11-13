package spine

class ImportDataService {

    static transactional = false
	def n = new NetworkService()

    def importEdges(String file) {
		println 'Starting to connect nodes...'
		def props = [:]
		def input = file.splitEachLine("\t") {fields ->
			props['startNode'] = fields[0]
			props['endNode'] = fields[1]
			props['tags'] = fields[2]
			println 'Connecting : ' + fields[0] + ' -> '+ fields[1] + ' with ' + fields[2]
			n.createRelationship(props)
			n.setProperty(props)
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
			n.createNode(props)
		}
		println "Nodes created!"
	}
}
