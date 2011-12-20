package spine

class SpineService {

    static transactional = false
    def  networkService
    def badgeService

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
                user.email = userNode.email
                user.country = userNode.country
                user.city = userNode.city
                user.imagePath = userNode.image
                user.freeText = 'My biography'
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
    def getUserNetwork(User contextUser, String filter, int offset, int limit) {

        def queryReturn
        def userList = []

        // verify if a filter has been passed

        if ((filter == null) || (filter == '')) {

            // get the neighbours in batches of 20
            queryReturn = networkService.queryForNeighbourNodes(contextUser.email, offset, limit)

        }
        else {

            // first step is to tokenize the filter string
            def tokens = " ,;"
            def wordList = []
            wordList = filter.tokenize(tokens)

            println "search filter: " + wordList

            // now we need to wait until the network service queryNode is ready, as this is not the case yet, use the same service as in the if
            queryReturn = networkService.queryForNeighbourNodes(contextUser.email, offset, limit)
        }

        // loop through the list and instantiate the user objects incl. tags
        def user

        queryReturn.each {
            user = new User()
            user.firstName = it.firstName
            user.lastName = it.lastName
            user.email = it.email
            user.country = it.country
            user.city = it.city
            user.imagePath = it.image
            user.freeText = 'My biography'
            user.tags = networkService.getIncomingTagsForNode(it.email)
            user.distance = it.distance
            if (user.tags != null)
                user.badges = badgeService.evaluateTags(user.tags)

            userList.add(user)
        }

        return userList
    }

    /**
     * returns a User object based on email address
     *
     * @return
     */
    def getUser(String email) {

        // instantiate return structure
        def user = new User()

        // retrieve the properties
        def userNode = networkService.readNode(email)

        // copy over the values from the hash map into the user object
        user.firstName = userNode.firstName
        user.lastName = userNode.lastName
        user.email = userNode.email
        user.country = userNode.country
        user.city = userNode.city
        user.imagePath = userNode.image
        user.freeText = userNode.freeText
        user.tags = networkService.getIncomingTagsForNode(userNode.email)
		// TODO: does not look good, have to rethink the way we manage user in the code
		user.password = userNode.password
        if (user.tags != null)
            user.badges = badgeService.evaluateTags(user.tags)

        // add something related to distance to the current contextUser

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
        newUser.freeText = userparams.freetext

        // set over into map for call
        def userProps = ['firstName': newUser.firstName,
                'lastName': newUser.lastName,
                'city': newUser.city,
                'country': newUser.country,
                'email': newUser.email,
                'password': newUser.password,
                'image': newUser.imagePath]

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
     * Update node properties
     *
     * @param loggedInUser
     * @param properties
     * @return success
     */
    def updateUserProfile(User loggedInUser, HashMap properties) {

        def success = false
		
		def userProps = [
			'email': loggedInUser.email,
			'firstName': properties.firstName == '' ? loggedInUser.firstName : properties.firstName, 
			'lastName': properties.lastName == '' ? loggedInUser.lastName : properties.lastName,
			'city': properties.city == '' ? loggedInUser.city : properties.city,
			'country': properties.country == '' ? loggedInUser.country : properties.country,
			'imagePath': properties.imagePath == '' ? loggedInUser.imagePath : properties.imagePath,
			'freeText': properties.freeText == '' ? loggedInUser.country : properties.freeText,
			'password': properties.password == '' ? loggedInUser.password : properties.password 
			]
		
		println ""
		println ""
		println ""
		println ""
		println ""
		networkService.updateNode(loggedInUser.email, userProps)
		
		success = true
        return success
    }

    /**
     * Add a tag to an existing relationship or create a new one
     *
     * @param loggedInUser = User
     * @param targetUser = Email
     * @param taglist = space , ; separated list
     * @return success
     */
    def addTag(User loggedInUser, String targetUser, String tags) {

        // tokenize taglist, then check, if relationship exists, if yes then update, if not then create new one

        def success = new Boolean()
        def parameters = new HashMap()

        parameters.put('startNode', loggedInUser.email)
        parameters.put('endNode', targetUser)
        parameters.put('tags', tags)

        //Todo: Relationship exists?
        if (networkService.readRelationship() == null) {
            networkService.createRelationship(parameters)
        }
        networkService.setProperty(parameters)

        return success = true
    }

    /**
     * Remove a tag from relationship and delete, if non left
     *
     * @param loggedInUser
     * @param targetUser
     * @param taglist
     * @return sucess
     */
    def removeTag(User loggedInUser, User targetUser, String tag) {

        // remove tag from relationship properties; if no property left, delete relationship

        def success = new Boolean()
        def parameters = new HashMap()

        parameters.put('startNode', loggedInUser)
        parameters.put('endNode', targetUser)
        parameters.put('tag', tag)

        success = networkService.deleteProperty(parameters)


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
