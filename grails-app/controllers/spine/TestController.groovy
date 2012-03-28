package spine

class TestController {

	def GraphCommunicatorService graphCommunicatorService;
	def NetworkService networkService;
	def SuperIndexService superIndexService;
	def SpineService spineService;
	
	def index = {
		def json = graphCommunicatorService.neoGet('/db/data/index/node/names', ['query': 'email:*'])
		json.each { indexNode(it.self, it) }
		render "ok"
	}



	/**
	 * Insert / update the data about the node in the super index
	 * @param nodeID
	 * @param json
	 * @return
	 */
	def indexNode(String nodeURI, json = null) {
		if(json == null) {
			json = graphCommunicatorService.neoGet(nodeURI)
		}
		
		// Indexing tags of the node
		def tags = []
		def incomingRelationshipsJson = graphCommunicatorService.neoGet(json.incoming_relationships)
		incomingRelationshipsJson.each
		{
			it.data.each {
				if(!tags.contains(it.key))
				{
					tags.add(it.key)
				}
			}
		}
		tags.each { superIndexService.addTagToIndex(it, nodeURI) }
		
		// Indexing the badges
		if(json.data?.email)
		{
			def badges = []
			def user = spineService.getUser(json.data.email)
			badges = spineService.getBadges(user)
			badges.each {
				superIndexService.addBadgeToIndex(it.toString(), nodeURI)
			}
		}
		
		// Index user basic data
		if(json.data?.email)  // adding email
			superIndexService.addFirstNameToIndex(json.data.email, nodeURI)
		
		if(json.data?.firstName)  // adding first name
			superIndexService.addFirstNameToIndex(json.data.firstName, nodeURI)
		
		if(json.data?.lastName)  // adding last name
			superIndexService.addLastNameToIndex(json.data.lastName, nodeURI)
		
		if(json.data?.city)  // adding city
			superIndexService.addCityToIndex(json.data.city, nodeURI)
	}

	
    def oldindex = {
		networkService.reindexRelationships()
		render("ok")
//		def json = graphCommunicatorService.neoGet('/db/data/index/relationship/edges', ['query': '*:*'])
//		def String string = "";
//		json.each {
////			setRelationShipIndex(it.self, it.data)
//			string += it.toString() + "\n \n \n"
//		}
//		render(string)
	}
}
