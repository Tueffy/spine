package spine

class SpineService {

    static transactional = false
	def networkService = new NetworkService()
	def badgeService = new BadgeService()
	
	/**
	 * take the email address and the password and verify, if exist in database and correct
	 * if yes then instantiate the loggedInUser object, if not, return null
	 * 
	 * @param email
	 * @param password
	 * @return success
	 */
	def loginUser(String email, String password) {

		def loggedInUser
		def success

		// retrieve the node via email address
		def userNode = networkService.readNode(email)

		// verify if user exists and if passwords are identical
		if (userNode != null)
			if (userNode.password == password) {
				
				//create an instance of the user
				loggedInUser = new User()
				
				// copy over the values from the hash map into the user object
				loggedInUser.firstname = userNode.firstName
				loggedInUser.lastname = userNode.lastName
				loggedInUser.email = userNode.email
				loggedInUser.city = userNode.city
				loggedInUser.country = userNode.country
				loggedInUser.imagepath = userNode.image
				loggedInUser.freetext = "Free Text"
				
			}
			
		//returns either the loggedInUser or null, if login was not successful
		return loggedInUser
	}

	
	/**
	 * 
	 * @param contextUser
	 * @param filter
	 * @param offset
	 * @param orderType
	 * @return
	 */
	def getUserNetwork(User contextUser, String filter, String offset, String orderType) {

		def userList = new HashMap()
		// search full network, using offset, orderType
		// loop over list and get all tags and all badges
		
		// (user: UserObject, tags: taglist (unique incl. ount), badgelist, distance : distance)
		
		return userList
    }
	
	/**
	 * retrieve the list of tags of all incoming connections to a given user, incl. amount and sorted (highest amount first)
	 * 
	 * @param user
	 * @param amount
	 * @return userTagList
	 */
	def getUserTags(User user, int amount) {
		
		def userTagList = new TreeMap()
		
		// getRelationships for one user and then getProperties per Relationship;
		// loop over Properies ad build the userTagList, with one entry per property and the count
		// use amount to limit what comes 
		
		return userTagList
		
	}
	
	/**
	 * create a new user in the network, which from an UI perspective only works with adding a relationship from myself and tagging it
	 * 
	 * @param userparams
	 * @param tags
	 * @return success or fail
	 */
	def createNewUser(HashMap userparams, List tags) {
		
		def newUser = new User()
		def success = false

		// copy over the values from the hash map into the user object to trigger validation
		newUser.firstname = userparams.firstName
		newUser.lastname = userparams.lastName
		newUser.password = userparams.password
		newUser.email = userparams.email
		newUser.city = userparams.city
		newUser.country = userparams.country
		newUser.imagepath = userparams.image
		newUser.freetext = "Free Text"
		
		// set over into map for call
		def userProps = ['firstName' : newUser.firstname,
							'lastName' : newUser.lastname, 
							'city' : newUser.city,
							'country' : newUser.country,
							'email' : newUser.email,
							'password' : newUser.password,
							'image' : newUser.imagepath]
		
		// verify if node with same email does not exist already
		if (networkService.readNode(newUser.email) != null)
			success = false
		else {
		
			// create the node
			def userNode = networkService.createNode(userProps)
			println userNode
			
			// create the node
			if (userNode != null) {
				
				// if a tag list has been provided, this means that a relationship should be created with these tags
				if (tags != null) {
					def currentUser = session.user
					success = setTag(currentUser, newUser, tags)
				}
				
				success = true
			}
		}
		
		return success
	}
	

	/**
	 * Update node properties
	 * 
	 * @param loggedInUser
	 * @param properties
	 * @return success
	 */
	def updateUserProfile(User loggedInUser, HashMap properties) {
	
		def success = new Boolean()
			
		// update node properties
		
		return success
	}
	
	
	/**
	 * Add a tag to an existing relationship or create a new one
	 * 
	 * @param loggedInUser
	 * @param targetUser
	 * @param taglist
	 * @return success
	 */
	def setTag(User loggedInUser, User targetUser, String taglist) {
		
		def success = new Boolean()
		// tokenize taglist, then check, if relationship exists, if yes then update, if not then create new one
		
		return success
	}
	
	/**
	 * Remove a tag from relationship and delete, if non left
	 * 
	 * @param loggedInUser
	 * @param targetUser
	 * @param taglist
	 * @return sucess
	 */
	def removeTag(User loggedInUser, User targetUser, String taglist) {
		
		def success = new Boolean()
		
		// remove tag from relationship properties; if no property left, delete relationship
		
		return success
	}
	
	
	/**
	 * Calls the method evaluateBadgeRules from the badge service based on a list of tags provided
	 * 
	 * @param user
	 * @return badgeList
	 */
	def getBadges(TreeMap tags) {
		
		def badgeList = ['javahero', 'godofhtml']
		
		// retrieve list of badges, actually just a list of imagepaths
		
		return badgeList
	}
}
