package spine

import java.util.regex.*

class SpineService {

    static transactional = false
    def  networkService
    def badgeService
	def SuperIndexService superIndexService
	def LogService logService
	
	def static List hotTagsCache = []

	
    /**
     * take the email address and the password and verify, if exist in database and correct
     * if yes then instantiate the loggedInUser object, if not, return null
     *
     * @param email
     * @param password
     * @return user as User object
     */
    def loginUser(String email, String password) {

        def user

        // retrieve the node via email address
        def userNode = networkService.readNode(email)

        // verify if user exists and if passwords are identical
        if (userNode != null)
            if (userNode.password == password) {

                //create an instance of the user
                user = new User()

                // copy over the values from the hash map into the user object
                user.firstName = userNode.firstName
                user.lastName = userNode.lastName
				user.password = userNode.password
                user.email = userNode.email
                user.country = userNode.country
                user.city = userNode.city
                user.imagePath = userNode.image
        		user.freeText = userNode.freeText
				user.company = userNode.company
				user.department = userNode.department
				user.jobTitle = userNode.jobTitle
				user.phone = userNode.phone
				user.mobile = userNode.mobile
				user.gender = userNode.gender
				user.birthday = userNode.birthday
				user.status = userNode.status
        				
                user.tags = networkService.getIncomingTagsForNode(userNode.email)
                if (user.tags != null)
                    user.badges = badgeService.evaluateTags(user.tags)
            }

        //returns either the loggedInUser or null, if login was not successful
        return user
    }

	
    /**
     *
     * @param contextUser
     * @param filter
     * @param offset
     * @return userList of type User
     */
    def Network getUserNetwork(User contextUser, String filter, int offset, int limit) {
		
        def Network network
		network = networkService.queryForNeighbourNodes(contextUser.email, offset, limit, filter)

		// Grab tabs and badges for each user of the network
		network.networkedUsers.each {
			networkedUser -> 
			networkedUser.user.tags = networkService.getIncomingTagsForNode(networkedUser.user.email)
			networkedUser.user.badges = badgeService.evaluateTags(networkedUser.user.tags)
			networkedUser.sortTags()
		}

        return network
    }
	
	def NetworkedUser getUserInNetworkContext(User contextUser, String targetEmail) {
		def NetworkedUser networkedUser = networkService.queryUserInNetworkContext(contextUser.email, targetEmail)
		if(networkedUser != null) {
			networkedUser.user.tags = networkService.getIncomingTagsForNode(networkedUser.user.email)
			networkedUser.user.badges = badgeService.evaluateTags(networkedUser.user.tags)
			networkedUser.sortTags()
		}
		
		return networkedUser
	}

    /**
     * returns a User object based on email address
     *
     * @return User
     */
    def User getUser(String email, boolean getTags = true) {

        def User user = networkService.readUserNode(email)
		if(user == null)
			return null
			
		// get tags
		if(getTags) {
			user.tags = networkService.getIncomingTagsForNode(user.email)
			if (user.tags != null)
				user.badges = badgeService.evaluateTags(user.tags)
		}
        
        return user
    }

    /**
     * retrieve the list of tags of all incoming connections to a given user, incl. amount and sorted (highest amount first)
     *
     * @param user
     * @param amount
     * @return userTagMap
     */
    def getUserTags(User user) {
        def userTagMap = [:]
        // get the tags
        userTagMap = networkService.getIncomingTagsForNode(user.email)
        return userTagMap
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
        newUser.firstName = userparams.firstName
        newUser.lastName = userparams.lastName
        newUser.password = userparams.password
        newUser.email = userparams.email
        newUser.city = userparams.city
        newUser.country = userparams.country
        newUser.imagePath = userparams.image
		newUser.freeText = userparams.freeText
		newUser.company = userparams.company
		newUser.department = userparams.department
		newUser.jobTitle = userparams.jobTitle
		newUser.phone = userparams.phone
		newUser.mobile = userparams.mobile
		newUser.gender = userparams.gender
		newUser.birthday = userparams.birthday
		newUser.status = userparams.status

        // set over into map for call
        def userProps = ['firstName': newUser.firstName,
                'lastName': newUser.lastName,
                'city': newUser.city,
                'country': newUser.country,
                'email': newUser.email,
                'password': newUser.password,
                'image': newUser.imagePath,
				'freeText': newUser.freeText,
				'company' : newUser.company,
				'department' : newUser.department,
				'jobTitle' : newUser.jobTitle,
				'phone' : newUser.phone,
				'mobile' : newUser.mobile,
				'gender' : newUser.gender,
				'birthday' : newUser.birthday,
				'status' : newUser.status
				]

        // verify if node with same email does not exist already
        if (networkService.readNode(newUser.email) != null)
            success = false
        else {

            // create the node
            def userNode = networkService.createNode(userProps)

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
	 * Activate a user
	 * @param User user
	 * @return Boolean success
	 */
	def activateUser(User user)
	{
		return updateUserProfile(user, ['status' : 'active'])
	}

    /**
     * Update node properties
     *
     * @param loggedInUser
     * @param properties
     * @return Boolean success
     */
    def updateUserProfile(User loggedInUser, HashMap properties) {

        def success = false
		
        		def userProps = [
	     			'email': loggedInUser.email,
	     			'firstName': properties.firstName ? properties.firstName : loggedInUser.firstName, 
	     			'lastName': properties.lastName ? properties.lastName : loggedInUser.lastName,
	     			'city': properties.city ? properties.city : loggedInUser.city,
	     			'country': properties.country ? properties.country : loggedInUser.country,
	     			'imagePath': properties.imagePath ? properties.imagePath : loggedInUser.imagePath,
	     			'freeText': properties.freeText ? properties.freeText : loggedInUser.freeText,
	     			'password': properties.password ? properties.password : loggedInUser.password,
	     			'company': properties.company ? properties.company : loggedInUser.company,
	     			'department': properties.department ? properties.department : loggedInUser.department,
	     			'jobTitle': properties.jobTitle ? properties.jobTitle : loggedInUser.jobTitle,
	     			'phone': properties.phone ? properties.phone : loggedInUser.phone,
	     			'mobile': properties.mobile ? properties.mobile : loggedInUser.mobile,
	     			'gender': properties.gender ? properties.gender : loggedInUser.gender,
	     			'birthday': properties.birthday ? properties.birthday : loggedInUser.birthday,
	     			'status': properties.status ? properties.status : loggedInUser.status
	     			]
		
//		log.trace("UserPops : \n " + userProps.toString())
		networkService.updateNode(loggedInUser.email, userProps)
		
		success = true
        return success
    }

    /**
     * Add a tag to an existing relationship or create a new one
     *
     * @param loggedInUser = User
     * @param targetUser = Email
     * @param tag
     * @return success
     */
    def addTag(String loggedInUser, String targetUser, String tag) {
		def User fromUser = getUser(loggedInUser, false)
		def User toUser = getUser(targetUser, false)
		return addTag(fromUser, toUser, tag)
    }
	
	/**
	 * 
	 * @param loggedInUser
	 * @param targetUser
	 * @param tag
	 * @return
	 */
	def addTag(User loggedInUser, User targetUser, String tag) {
		
		if(!loggedInUser.self)
			loggedInUser = getUser(loggedInUser.email, false)
		if(!targetUser.self)
			targetUser = getUser(targetUser.email, false)
					
		def ConnectRelationship relationship = networkService.findConnectRelationship(loggedInUser.email, targetUser.email, true)
		relationship.addTag(tag)
		relationship.persist(networkService.graphCommunicatorService);
		superIndexService.addTagToIndex(tag, relationship.end.self)
		logService.addTag(tag, loggedInUser, targetUser)
		def success = relationship.self && relationship.hasTag(tag)
		return success
	}

    /**
     * Remove a tag from relationship and delete, if none left
     *
     * @param loggedInUser
     * @param targetUser
     * @param String tag
     * @return Boolean success
     */
    def removeTag(User loggedInUser, User targetUser, String tag) {
		def ConnectRelationship relationship = networkService.findConnectRelationship(loggedInUser.email, targetUser.email)
		if(!relationship || !relationship.self)
			return false
		relationship.removeTag(tag)
		superIndexService.removeTagFromIndex(tag, relationship.end.self)
		
		// Should we delete or update the relationship ? 
		if(relationship.data.size() == 0)
			relationship.delete(networkService.graphCommunicatorService)
		else
			relationship.persist(networkService.graphCommunicatorService);
			
		// Log the action
		logService.removeTag(tag, loggedInUser, targetUser)
			
		def success = !relationship.hasTag(tag)
		return success
    }

    /**
     * Get the badges based on a list of tags provided
     *
     * @param Map tags
     * @return List badgeList (List of Badge objects)
     */
    def getBadges(Map tags) {
        def badgeList = []
		badgeList = badgeService.evaluateTags(tags)
        return badgeList
    }
	
	/**
	 * Get the badges based on a user object
	 * @param User user
	 * @return List List of Badge objects
	 */
	def getBadges(User user)
	{
		def userTags = getUserTags(user)
		return getBadges(userTags)
	}
	
	/**
	* Get a list of hot tags
	*
	* @param 
	* @return List tagList
	*/
   def List getHotTags() {
	   if(hotTagsCache.isEmpty())
	   {
		   log.debug("Caching hot tags... ");
		   Map allTags = networkService.getAllProperties().sort { a,b -> b.value <=> a.value } // sort by value desc
		   allTags.each {
			   key, value ->
			   if(hotTagsCache.size() < 10) hotTagsCache.add(key)
		   }
	   }
	   else
	   		log.debug("Getting hot tags from cache... ");
	   return hotTagsCache
   }
   
   /**
    * 
    * @param String query One tag to search for (No multitag autocompletion)
    * @return List List of the matching tags : [[tag: x, number: y]]
    */
   def List autocompleteTags(String query)
   {
	   def allTheTags = networkService.getAllProperties()
	   def correspondingTags = []
	   Pattern pattern
	   Matcher matcher
	   
	   // The idea is to go throw each tags and by applying a regexp determine the corresponding tags.
	   // Search is : *query* where query is lowercased 
	   allTheTags.each {
		   tag, number -> 
		   pattern = Pattern.compile("(.*)" + query.toLowerCase() + "(.*)")
		   matcher = pattern.matcher(tag.toLowerCase())
		   if(matcher.find()) {
			   correspondingTags.add([
			   		tag: tag, 
					number: number
			   ]);
		   }
	  }
	  return correspondingTags
   }
	
	def filterRelationShip (Map queryObject)
	{
		return networkService.queryRelationship(queryObject)
	}
	
	def search
}
