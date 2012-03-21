package spine

/**
 * This is a first attempt of using a good index to make search work better
 *
 */

class SuperIndexService {

	static transactional = true
	
	def GraphCommunicatorService graphCommunicatorService
	def SpineService spineService

	def addNodeToSuperIndex(String key, String value, String nodeURI) {
		def indexPath = '/db/data/index/node/super_index/'
		def requestQuery = [ 'key' : key,
					'value' : value,
					'uri' : nodeURI ]
		graphCommunicatorService.neoPost(indexPath, requestQuery)
	}

	def addTagToIndex(String tag, String nodeURI) {
		addNodeToSuperIndex("tag", tag, nodeURI)
	}

	def addBadgeToIndex(String badge, String nodeURI) {
		addNodeToSuperIndex("badge", badge, nodeURI)
	}

	def addEmailToIndex(String email, String nodeURI) {
		addNodeToSuperIndex("email", email, nodeURI)
	}
	
	def addCityToIndex(String city, String nodeURI) {
		addNodeToSuperIndex("city", city, nodeURI)
	}
	
	def addFirstNameToIndex(String firstName, String nodeURI) {
		addNodeToSuperIndex("firstname", firstName, nodeURI)
	}
	
	def addLastNameToIndex(String lastName, String nodeURI) {
		addNodeToSuperIndex("lastname", lastName, nodeURI)
	}

	
	/**
	 * Insert / update the data aboute the node in the super index
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
		tags.each { addTagToIndex(it, nodeURI) }
		
		// Indexing the badges
		if(json.data?.email) 
		{
			def badges = []
			def user = spineService.getUser(json.data.email)
			badges = spineService.getBadges(user)
			badges.each {
				addBadgeToIndex(it.toString(), nodeURI)
			}
		}
		
		
		// Index user basic data
		if(json.data?.email)  // adding email
			addFirstNameToIndex(json.data.email, nodeURI)
		
		if(json.data?.firstName)  // adding first name
			addFirstNameToIndex(json.data.firstName, nodeURI)
		
		if(json.data?.lastName)  // adding last name
			addLastNameToIndex(json.data.lastName, nodeURI)
		
		if(json.data?.city)  // adding city
			addCityToIndex(json.data.city, nodeURI)
		
		
	}
	
	/**
	 * Re-index everything ! 
	 * @return
	 */
	def indexAll() {
		def json = graphCommunicatorService.neoGet('/db/data/index/node/names', ['query': 'email:*'])
		json.each { indexNode(it.self, it) }
	}
}
