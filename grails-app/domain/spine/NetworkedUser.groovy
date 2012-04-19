package spine

class NetworkedUser {

	User user
	int distance = 0
	List<String> directTags = []
	
	def NetworkedUser(User user = null) {
		this.user = user
	}
	
	/**
	 * 
	 * @param json : json from a neo4j relationship
	 * @return void
	 */
	def bindDirectTags(json) {
		json.data.each {
			tag, number -> 
			directTags.add(tag)
		}
	}
	
	def boolean isDirectTag(String tag) {
		return directTags.contains(tag)
	}
	
	def sortTags() {
		if(user != null) {
			user.sortTags(directTags)
		}
	}
	
    static constraints = {
    }
}
