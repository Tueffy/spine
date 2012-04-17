package spine

class Network {

	User user // Whom does this network belong to ?  
	int maxLevel = 5 // Max level the network reaches
	List<NetworkedUser> networkedUsers = [] // A list of NetworkedUser
	int networkSize // The size of the user network according the maxLevel prop
	int offset = 0
	int limit
	List filter = []
	
	def bind (User user, json = null) {
		this.user = user;
		
		if(json != null) {
			
		}
	}
	
	def List toEmailList() {
		def emailList = []
		networkedUsers.each {
			emailList.add(it.user.email)
		}
		return emailList
	}
	
    static constraints = {
    }
}
