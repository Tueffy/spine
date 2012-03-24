package spine

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is a first attempt of using a good index to make search work better
 *
 */

class SuperIndexService {

	static transactional = true
	static indexPath = '/db/data/index/node/super_index/'
	def GraphCommunicatorService graphCommunicatorService

	
	/* 
	 * 
	 *  	POPULATE THE INDEX
	 * 
	 */
	
	def addNodeToSuperIndex(String key, String value, String nodeURI) {
		def requestQuery = [ 
					'key' : key,
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
	/*def indexNode(String nodeURI, json = null) {
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
	*/
	
	/**
	 * Re-index everything ! 
	 * @return
	 */
	/*
	def indexAll() {
		def json = graphCommunicatorService.neoGet('/db/data/index/node/names', ['query': 'email:*'])
		json.each { indexNode(it.self, it) }
	}*/
	
	
	
	
	
	
	
	
	
	/*
	*
	*  	REMOVE FROM THE INDEX
	*
	*/
	
	def removeNodeFromSuperIndex(String key, String value, String nodeURI) {
		graphCommunicatorService.neoDelete(indexPath + getIdFromURI(nodeURI) + '/' + key + '/' + value)
	}
	
	def removeNodeFromSuperIndex(String nodeURI)
	{
		graphCommunicatorService.neoDelete(indexPath + getIdFromURI(nodeURI))
	}
	
	def removeTagFromIndex(String tag, String nodeURI) {
		removeNodeFromSuperIndex("tag", tag, nodeURI)
	}

	def removeBadgeFromIndex(String badge, String nodeURI) {
		removeNodeFromSuperIndex("badge", badge, nodeURI)
	}

	def removeEmailFromIndex(String email, String nodeURI) {
		removeNodeFromSuperIndex("email", email, nodeURI)
	}
	
	def removeCityFromIndex(String city, String nodeURI) {
		removeNodeFromSuperIndex("city", city, nodeURI)
	}
	
	def removeFirstNameFromIndex(String firstName, String nodeURI) {
		removeNodeFromSuperIndex("firstname", firstName, nodeURI)
	}
	
	def removeLastNameFromIndex(String lastName, String nodeURI) {
		removeNodeFromSuperIndex("lastname", lastName, nodeURI)
	}
	
	
	
	
	
	// Copy of the method in NetworkService because having a property referencing
	// NetworkService made the test crash. 
	def String getIdFromURI(String URI)
	{
		Pattern pattern = Pattern.compile('(.*)/([0-9]+)/?')
		Matcher matcher = pattern.matcher(URI)
		matcher.matches()
		if(matcher.groupCount() > 0)
			return matcher.group(2)
		else
			return '-1'
	}

}
