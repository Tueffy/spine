package spine

import java.util.regex.*

//import com.sun.org.apache.xalan.internal.xsltc.compiler.CeilingCall;

class NetworkService {
    static transactional = false

    def graphCommunicatorService
	def cypherPlugin = '/db/data/cypher'
	def SuperIndexService superIndexService
    //def http = new RESTClient( 'http://localhost:7575' ) //tcpmon

    /*
      *
      * Network service to provide access to nodes, relationships and properties.
      * The naming convention is CRUD - Create, Read, Update, Delete, Exists
      * All services also take care about indexing the nodes/relationships
      *
      * The services are using
      * Nodes (CRUD, search/query)
      * Relationships (CRUD, search/query)
      *
      *
      */

    // ----------------  Node related services

    //Provides all node data for a user (using email)
    /**
     * Returns all node related data for a user using email as ID
     * @param email user ID
     * @return node data
     */
    def readNode(String email) {
        def json = graphCommunicatorService.neoGet('/db/data/index/node/names/email', ['query': '"' + email + '"'])
        return json.data[0]
    }
	
	def User readUserNode(String email) {
		def json = graphCommunicatorService.neoGet('/db/data/index/node/names/email', ['query': '"' + email + '"'])
		if(json == null)
			return null
		User user = new User()
		user.bind(json[0])
		return user
	}

    /** Create node and add to index (all properties)
     * @param props
     * @return
     */
	def createNode(Map props) {
        //TODO: check if node exists already via readNode
 
		//create a timestamp for the new node and add into the props for createdAt and lastUpdated
		def now = System.currentTimeMillis()
		props['createdAt'] = now
		props['lastUpdated'] = now

		//initiate the node creation
		def newNodeRef = graphCommunicatorService.neoPost('/db/data/node', props)
        addNodeIndex(newNodeRef.self, props)
        return newNodeRef
    }

    /** Update node with new properties, the old ones remain. For this, the node needs to be removed from index and added again
     * @param email
     * @param props
     * @return
     */
    def updateNode(String email, Map props, boolean bypassLastUpdatedProperty = false) {

		if(!bypassLastUpdatedProperty) {
			//change the timestamp of lastUpdated
			def now = System.currentTimeMillis()
			props['lastUpdated'] = now
		}
		
        def json = graphCommunicatorService.neoGet('/db/data/index/node/names/email', ['query': '"' + email + '"'])
        //node URL is self
        def newProperties = json.data[0]
        //replace only the properties that are passed, but neo always requires all properties to be passed as PUT
        props.each {
            println it
            newProperties.(it.getKey()) = it.getValue()
        }
        //remove all old index entries
        removeNodeIndex(json.self[0])
		//graphCommunicatorService.neoDelete(json.self[0]) // actually we don't need to delete the node
        //add new properties to node
        graphCommunicatorService.neoPut(json.self[0] + '/properties', newProperties)
        //add new index entries
        addNodeIndex(json.self[0], newProperties)
    }


    /** Delete node and remove from index
     * @param email identifier of the node
     * @return
     */
    def deleteNode(String email) {
        def json = graphCommunicatorService.neoGet('/db/data/index/node/names/email', ['query': '"' + email + '"'])
        def nodeURL = json.self[0]
        //TODO: delete all relationships related to node before deleting node
        //remove node from index
        removeNodeIndex(email)
        //remove node itself
        graphCommunicatorService.neoDelete(nodeURL)
    }

    /** Get a neo4j URI for a email node (utility function, used only internally)
     * @param email
     * @return
     */
    def getNodeURIFromEmail(String email) {
        def json = graphCommunicatorService.neoGet('/db/data/index/node/names/email', ['query': '"' + email + '"'])
        return json.self[0]
    }

    /**
     *
     *
     * @param queryObject : Pass a map with the properties.
     * key: email, value: expression to filter for nodes, e.g. j* would result in all emails starting with j*
     * For example [email : 'jure*', lastName : 'Zakotnik']. See also unit tests for examples.
     *
     * This method is for nodes only, so it does not search any relationships
     *
     * @return Map with email identifiers
     */
    def queryNode(Map queryObject) {
        def queryResult = []
        if (queryObject == null) {
            //TODO: get traversal over all nodes in your neighbourhood
        } else {
            //go through all keys in the query map and create a valid lucene query
            def queryString = ''
            queryObject.each {
                queryString = queryString + it.key + ':' + it.value + ' AND '
            }
            //dirty hack, remove the last AND.. (in groovy maps, there are some nice collapsing functions for this...
            queryString = queryString.substring(0, queryString.size() - 5)
            //println 'Query string: ' + queryString
            def json = graphCommunicatorService.neoGet('/db/data/index/node/names', ['query': (queryString)])
            //println 'Query JSON result: ' + json
            json.each {
                queryResult.add(it.data.email)
            }
        }
        return queryResult
    }
	
	def queryRelationship(Map queryObject) 
	{
		def queryResult = []
		// Turn the queryObject into a valid lucene query
		def queryString = ''
		queryObject.each {
			queryString = queryString + it.key + ':' + it.value + ' AND '
		}
		//dirty hack, remove the last AND.. (in groovy maps, there are some nice collapsing functions for this...
		queryString = queryString.substring(0, queryString.size() - 5)
		def json = graphCommunicatorService.neoGet('/db/data/index/relationship/edges', ['query': (queryString)])
		return json
	}
	
	def NetworkedUser queryUserInNetworkContext(String contextUserEmail, String targetEmail)
	{
		def NetworkedUser networkedUser = new NetworkedUser()
		
		def query = 
			'start ' +
				'n = node:names(email={SP_user}), ' +
				'm = node:names(email={SP_target_user}) ' + 
			'match ' + 
				'p = shortestPath(n-[:connect*..5]->m) ' + 
			'return m, relationships(p) ' 

		// Execute the query
		def cypherPlugin = '/db/data/ext/CypherPlugin/graphdb/execute_query'
		def json = graphCommunicatorService.neoPost(cypherPlugin, '{"query": "'+ query +'", "params": {"SP_user":"' + contextUserEmail + '", "SP_target_user":"' + targetEmail + '"}}')
		if(!json.data || json.data.size() == 0) // not in the user network: extended network 
		{
			query =
			'start ' +
				'm = node:names(email={SP_target_user}) ' +
			'return m '
			json = graphCommunicatorService.neoPost(cypherPlugin, '{"query": "'+ query +'", "params": {"SP_user":"' + contextUserEmail + '", "SP_target_user":"' + targetEmail + '"}}')
		}
		
		println json;
		if(!json.data || json.data.size() == 0)
			return null
		
		json = json.data[0] // The query gives only one result back
		
		// json[0] => m
		// json[1] => p ; only if poeple are directly connected
		networkedUser.user = new User()
		networkedUser.user.bind(json[0])
		if(json[1] == null) 
			networkedUser.distance = 0;
		else 
		{
			networkedUser.distance = json[1].size()
			if(networkedUser.distance == 1) // direct tags if path length == 1
			networkedUser.bindDirectTags(json[1][0]) // json[1][0] => the 1st relationship of the path p
		}
		
		return networkedUser
	}
	
	def String parseSearchQueryIntoLuceneQuery(String query) {
		def List<String> tokenizedQuery = query.tokenize(" ")
		def operators = ['AND', 'OR']
		def luceneQuery = ''
		def lastWordWasOperator = false
		tokenizedQuery.each {
			if(lastWordWasOperator || (!lastWordWasOperator && !operators.contains(it)))
			{
				// Default implicit operator :
				if(!lastWordWasOperator && !operators.contains(it) && luceneQuery != '')
					luceneQuery += ' OR '
				
				luceneQuery += '(' +
					'tag : ' + it.toLowerCase() + ' OR ' +
					'badge : ' + it.toLowerCase() + ' OR ' +
					'email : ' + it.toLowerCase() + ' OR ' +
					'firstname : ' + it.toLowerCase() + ' OR ' +
					'lastname : ' + it.toLowerCase() + ' OR ' +
					'city : ' + it.toLowerCase() + '' +
					')'
				lastWordWasOperator = false
			}
			else
			{
				luceneQuery += ' ' + it + ' '
				lastWordWasOperator = true
			}
		}
		return luceneQuery
	}

    /**
     * Uses the cypher plugin to retrieve the neighbours using offset and limit. The result includes the start node of the query itself.
     *
     * @param email id of the person to start the query
     * @param offset pagination value
     * @param limit max of how many neighbours are searched
     * @return Network
     */
	def queryForNeighbourNodes(String email, int offset, int limit, String filter = null, boolean extended_search = true)
	{
		// TODO : The function is becomming too complex, must be splitted into more atomic functions
		// TODO : Refectoring - refactor the way search work to make it less complicated and more maintainable and to get more performances
		
		// Vars initialization
		def network = new Network()
		network.offset = offset
		network.limit = limit
		network.filter = filter
		
		/*-------------------------------------------------------------
		 * 
		 * 	LUCENE QUERY : Which we will use to query the super index
		 * 
		 * ----------------------------------------------------------- */
		def luceneQuery = ''
		def isFiltered = (filter != null && filter.trim() != '')
		if(isFiltered) 
			luceneQuery = parseSearchQueryIntoLuceneQuery(filter)
		else 
			luceneQuery = '*:* '
		
		
		/*-------------------------------------------------------------
		 * 
		 * 	FIRST QUERY : Get results from the User Network
		 * 
		 * ----------------------------------------------------------- */
		// Build the query
		def query = 'start ' +
						'n = node:names(email={SP_user}) '
		if(isFiltered) {
			query += ", m = node:super_index('" + luceneQuery + "') "
		}
		query += 	'match ' 
		if(!isFiltered)
			query += 'n-[:connect*1..5]->m, '
		query += 	'p = shortestPath(n-[:connect*..5]->m) ' + 
						' ' +
					'where m <> n ' +
					'return ' +
						'distinct m, length(p), relationships(p) ' +
					'order by length(p) ' +
					'skip ' + offset + ' ' +
					'limit ' + limit + ' '
		
//		print("\n\n\n" + query + "\n\n\n");
		
		// Execute the query
		def cypherPlugin = '/db/data/ext/CypherPlugin/graphdb/execute_query'
		def json = graphCommunicatorService.neoPost(cypherPlugin, '{"query": "'+ query +'", "params": {"SP_user":"' + email + '"}}')
		
		// Get results
//		return json.data[0]
		json.data.each {
			
			// it[0] => m
			// it[1] => length(p)
			// it[2] => relationships(p)
			
			def networkedUser = new NetworkedUser(new User());
			networkedUser.user.bind(it[0])
			networkedUser.distance = it[1]
			if(networkedUser.distance == 1)
				networkedUser.bindDirectTags(it[2][0]) // it[2][0] => the first relationship of the path
			network.networkedUsers.add(networkedUser)
		}
		
		
		/*-------------------------------------------------------------
		*
		* 	COUNTING : How many people in the user network 
		* 			   match the query ? 
		*
		* ----------------------------------------------------------- */
		int firstQueryNbTotalResults = 0;
		query = 'start ' +
					'n = node:names(email={SP_user}) '
		if(isFiltered) {
			query += ", m = node:super_index('" + luceneQuery + "') "
		}
		query += 'match '
		if(!isFiltered)
			query += 'n-[:connect*1..5]->m, '
		query += 'p = shortestPath(n-[:connect*..5]->m) ' +
				'where m <> n ' + 
				'return ' +
					'count(distinct m) as nb '
		json = graphCommunicatorService.neoPost(cypherPlugin, '{"query": "'+ query +'", "params": {"SP_user":"' + email + '"}}')
		if(!json || !json.data || json.data.size() == 0)
			firstQueryNbTotalResults = 0;
		else
			firstQueryNbTotalResults = (int) json.data[0][0]
		network.networkSize = firstQueryNbTotalResults
			
		
		/*-------------------------------------------------------------
		*
		* 	SECOND QUERY : Get results from the whole Spine Network 
		*
		* ----------------------------------------------------------- */
		// We only execute the second query if there no more (or not enough) 
		// result to get from the first one.  
		// AND the extended_search parameter must be set to true
		if(extended_search && network.networkedUsers.size() < limit && isFiltered)
		{
			int newOffset = offset - firstQueryNbTotalResults;
			if(newOffset < 0)
			newOffset = 0
			int newLimit = limit - network.networkedUsers.size()
			
			// Build the second query
			query = 'start ' +
						'n = node:names(email={SP_user}), '
			if(isFiltered) {
				query += " m = node:super_index('" + luceneQuery + "') "
			}
			query += 
					'match ' + 
						'n-[r?:connect*1..5]->m ' + 
					'where ' +
						'r is null AND m <> n ' +
					'return ' +
						'distinct m, count(*) AS nbResults ' +
					'skip ' + newOffset + ' ' +
					'limit ' + newLimit + ' '
					
			
//			log.info(query)
			
			// Execute the query
			cypherPlugin = '/db/data/ext/CypherPlugin/graphdb/execute_query'
			json = graphCommunicatorService.neoPost(cypherPlugin, '{"query": "'+ query +'", "params": {"SP_user":"' + email + '"}}')
			
			// Get results
			json.data.each {
				
				// it[0] => m
				// it[1] = nbResults
				
				def networkedUser = new NetworkedUser(new User())
				networkedUser.user.bind(it[0])
				network.networkedUsers.add(networkedUser)
			}
		}
		
		
		return network
	}

    /** Returns all properties for the relationship pointing to the node referred to and their number.
     * E.g. for markus.long@techbank.com, it returns ['ITIL':3, 'Help':1, 'Operations':3, 'Desk':1, 'IT':2]
     *
     * @param email
     * @return map of relationship properties (tags) and their number
     */
    def getIncomingTagsForNode(String email) {
        //returns the incoming tags per node and their number
        def tagMap = [:]
        def json = graphCommunicatorService.neoGet('/db/data/index/node/names/email', ['query': '"' + email + '"'])
        //get incoming relationships from incoming_relationships
//		log.info(json)
        if (json.size() == 0) {
            return tagMap
        }
        json = graphCommunicatorService.neoGet(json.incoming_relationships[0])
        json.each {
            def allTagsForRelationship = it.data //this could be [Operations:1, Help Desk:1]
            allTagsForRelationship.each {
                def tag = it.key
                if (!tagMap.containsKey(tag)) { //add tag to tagmap with value
                    tagMap[tag] = 1
                } else {
                    tagMap[tag] = tagMap[tag] + 1
                }
            }
        }
        return tagMap
    }

    /**
     * Utility function to manually add all node properties (apart from password) to the index called names
     * To browse the index, use http://localhost:7474/db/data/index/node
     *
     * @param nodeRef This is the URI of the node to be added
     * @param props Properties to be added
     * @return void
     */
    def addNodeIndex(String nodeRef, Map props) {
        def indexPath = '/db/data/index/node/names/'
        def postBody = []
        //put last name into index
        postBody = ['value': props.lastName, 'key': 'lastName', 'uri': nodeRef]
        graphCommunicatorService.neoPost(indexPath, postBody)

        //put first name into index
        postBody = ['value': props.firstName, 'key': 'firstName', 'uri': nodeRef]
        graphCommunicatorService.neoPost(indexPath, postBody)

        //put email into index
        postBody = ['value': props.email, 'key': 'email', 'uri': nodeRef]
        graphCommunicatorService.neoPost(indexPath, postBody)

        //put country into index
        postBody = ['value': props.country, 'key': 'country', 'uri': nodeRef]
        graphCommunicatorService.neoPost(indexPath, postBody)

        //put city into index
        postBody = ['value': props.city, 'key': 'city', 'uri': nodeRef]
        graphCommunicatorService.neoPost(indexPath, postBody)

        //put freeText into index
        postBody = ['value': props.freeText, 'key': 'freeText', 'uri': nodeRef]
        graphCommunicatorService.neoPost(indexPath, postBody)

		//put company into index
        postBody = ['value': props.company, 'key': 'company', 'uri': nodeRef]
        graphCommunicatorService.neoPost(indexPath, postBody)

		//put department into index
        postBody = ['value': props.department, 'key': 'department', 'uri': nodeRef]
        graphCommunicatorService.neoPost(indexPath, postBody)

		//put jobTitle into index
        postBody = ['value': props.jobTitle, 'key': 'jobTitle', 'uri': nodeRef]
        graphCommunicatorService.neoPost(indexPath, postBody)

		//put gender into index
        postBody = ['value': props.gender, 'key': 'gender', 'uri': nodeRef]
        graphCommunicatorService.neoPost(indexPath, postBody)

		//put birthday into index
        postBody = ['value': props.birthday, 'key': 'birthday', 'uri': nodeRef]
        graphCommunicatorService.neoPost(indexPath, postBody)

		//put phone into index
        postBody = ['value': props.phone, 'key': 'phone', 'uri': nodeRef]
        graphCommunicatorService.neoPost(indexPath, postBody)

		//put mobile into index
        postBody = ['value': props.mobile, 'key': 'mobile', 'uri': nodeRef]
        graphCommunicatorService.neoPost(indexPath, postBody)

		//put phone into status
        postBody = ['value': props.status, 'key': 'status', 'uri': nodeRef]
        graphCommunicatorService.neoPost(indexPath, postBody)
    }

    /**
     * Removes node from index (should not be used directly)
     * @param email
     * @return void
     */
    def removeNodeIndex(String email) {
        //DELETE /index/node/my_nodes/123
        def indexPath = '/db/data/index/node/names/' + email
        graphCommunicatorService.neoDelete(indexPath)
    }

    // ----------------  Relationship related services

    /**
     * TOOD create test with create and remove
     * @param props (startNode, endNode, tags)
     * @return data of the created relationship
     */
    def createRelationship(Map props) {
        def from = getNodeURIFromEmail(props.startNode)
        def to = getNodeURIFromEmail(props.endNode)
        def relationship = readRelationship(props)[0]
        log.info('relationship to be created is:' + from + ' -> ' + to)
		
        def createdRelationship
        if (!relationship) {
            createdRelationship = graphCommunicatorService.neoPost(from + '/relationships', ['to': to, 'type': 'connect']).self
			// Apply tags to the newly created relationship 
			if(props.tags) {
				setProperty(['relationshipURI': createdRelationship, 'tags': props.tags])
				
				// We can deal with tags as String, Map, or List
				def tagList = []
				if(props.tags instanceof String)
				{
					def tokens = " ,;"
					def result = ''
					tagList = props.tags.tokenize(tokens)
				}
				else if(props.tags instanceof Map)
				{
					props.tags.each { key, value ->  tagList.add(key) }
				}
				else if (props.tags instanceof List)
				{
					tagList = props.tags
				}
				else
				{
					throw new Exception("tags type incompatible ! ")
				}
				
				tagList.each {
					superIndexService.removeTagFromIndex(it, to);
				}
			}
        }
		
        return createdRelationship
    }

    /**
     * Checks if a relationship between two nodes exists and returns the data
     * @param props (startNode, endNode)
     * @return Set of URIs of relationship (e.g. [http://...])
     */
    def readRelationship(Map props) {
        // start and end node with distance 1
        def from = getNodeURIFromEmail(props.startNode)
        def to = getNodeURIFromEmail(props.endNode)
        def resultRelationship = []
        //check if the start node of incoming relationships is 'from'-node
        def incomingRelationships = graphCommunicatorService.neoGet(to + '/relationships/in')
        incomingRelationships.each {
            if (it.start == from) {
                resultRelationship.add(it.self)
            }
        }
        log.info('Found relationship: ' + resultRelationship)
        return resultRelationship
    }
	
	/**
	 * Get the relationship between two users
	 * @param startEmail
	 * @param endEmail
	 * @return
	 */
	def ConnectRelationship findConnectRelationship(String startEmail, String endEmail, createIfNotFound = false) {
		def query = 
			'start ' + 
				'start_user = node:super_index(email = {SP_START_EMAIL}), ' + 
				'end_user = node:super_index(email = {SP_END_EMAIL}) ' + 
			'match start_user-[r?:connect]->end_user ' + 
			'return r, start_user, end_user'
		def json = graphCommunicatorService.neoPost(cypherPlugin, '{"query": "'+ query +'", "params": {"SP_START_EMAIL":"' + startEmail + '", "SP_END_EMAIL":"' + endEmail + '"}}')
		log.info(json)
		if(json.data.size() == 0)
			return null
		
		// Populate the relationship
		json = json.data[0] // first result
		
		// json[0] => r
		// json[1] => start_user
		// json[2] => end_user
		
		if(!json[1] ||! json[2])
		 throw new Exception("One of the user does not exists!")
		
		def ConnectRelationship connectRelationship = new ConnectRelationship()
		connectRelationship.start = new User()
		connectRelationship.start.bind(json[1])
		connectRelationship.end = new User()
		connectRelationship.end.bind(json[2])
		 
		if(json[0] && json[0] != 'null') // if json[0] is null it won't be equal to null because it's a JSONNull Object
		{
			connectRelationship.bind(json[0])
		}
		else // relationship not found ! 
		{
			if(!createIfNotFound)
				return null
			else
				connectRelationship.persist(graphCommunicatorService)
		}
		
		return connectRelationship
	}

    /**
     * Add or reset properties to a relationship. 
     * @param props (startNode, endNode, tags) or (relationshipURI, tags)
     * @return
     */
    def setProperty(Map props) {
        // tokenize tags and add to relationship if not yet exist for the later
		
		String relationship
        if(props.relationshipURI) relationship = props.relationshipURI
		else relationship = readRelationship(props)[0]
		
		// Update tag list
		def allExistingTags = [:]
		allExistingTags = graphCommunicatorService.neoGet(relationship + '/properties')
		if (allExistingTags == null) allExistingTags = [:] // no property found
		def tagList = []
		
		// We can deal with tags as String, Map, or List
		if(props.tags instanceof String) 
		{
	        def tokens = " ,;"
	        def result = ''
	        tagList = props.tags.tokenize(tokens)
		}
		else if(props.tags instanceof Map) 
		{
			props.tags.each { key, value ->  tagList.add(key) }
		}
		else if (props.tags instanceof List) 
		{
			tagList = props.tags
		}
		else 
		{
			throw new Exception("tags type incompatible ! ")
		}
		
		def userNodeURI = getNodeURIFromEmail(props.endNode)
		tagList.each { 
			allExistingTags[it] = 1
			superIndexService.addTagToIndex(it, userNodeURI); // Index each tag
		}
		
		// Update in DB
        graphCommunicatorService.neoPut(relationship + '/properties', allExistingTags)
		
        // add to index
		// TODO: At the end this index is not usefull, because of the new super index 
		def result = setRelationshipIndex(relationship, allExistingTags)
        return result.data
    }
	
    /**
     * Get properties for a list of relationships
     * @param relationships URIs (you can get these from readRelationship)
     * @return list of properties for these
     */
    def getProperty(List relationships) {
        def props = []
        relationships.each {
            def json = graphCommunicatorService.neoGet(it + '/properties')
            //TODO include error handling for empty properties?
            json.each { props.add(it.key) }
        }
        return props
    }

	/**
	 * Delete the properties given in the map
	 * @param props A map with the following template : [startNode: X, endNode: X, tags: 'HTML,Java']
	 * @return
	 */
    def Map deleteProperty(Map props) {
        //delete prop on relationship and on index
		// Get the relationship we want to deal with
        def relationship = readRelationship(props)[0]
		
		// Turn props.tag (String) into a proper List
		def tagList = []
		tagList = props.tags.tokenize(" ,;") // Use " ,;" to cut tags from each others
		
		// Get the actual tags of the relationship in the database
		def actualTags = graphCommunicatorService.neoGet(relationship + '/properties')
		if(!actualTags) actualTags = [:]
		
		// Now we remove the tags specified in tagList from actualTag
		tagList.each {
			if(actualTags.containsKey(it)) actualTags.remove(it)
		}
		
		// Save the actual tags
		// We do not use the setProperty method, cause it won't delete the tags
		graphCommunicatorService.neoPut(relationship + '/properties', actualTags)
		
		// Remove removed tags from the index
		def userNodeURI = getNodeURIFromEmail(props.endNode)
		setRelationshipIndex(relationship, actualTags)
		actualTags.each {
			superIndexService.removeTagFromIndex(it.key, userNodeURI);
		}
		
		// If a relationship is left without properties, we delete it
		if(actualTags.isEmpty())
			deleteRelationship(props)
		
		return actualTags
    }
	
	/**
	 * Put a relationship in the index. (erease old data in the index about this relationship)
	 * /!\ Be careful to always update the index BEFORE actually updating a relationship 
	 * @param relationshipURI The URI of the relationship
	 * @param newProps A map which contains the new properties of the relationship to index
	 */
	def setRelationshipIndex(String relationshipURI, Map newProps)
	{
		def indexPath = '/db/data/index/relationship/edges/'
		log.info("relationshipURI" + relationshipURI)
		def relationshipID = getIdFromURI(relationshipURI)
		def result = ''
		
		// Remove old entry from index
		graphCommunicatorService.neoDelete(indexPath + relationshipID)
		
		// Recreate the entry in the index
		def requestQuery = []
		newProps.each {
			key, value -> 
			requestQuery = [ 'key' : key,
			'value' : 'tag',
			'uri' : relationshipURI ]
			result = graphCommunicatorService.neoPost(indexPath, requestQuery)
		}
		return result
	}
	
	def setRelationshipIndex(String relationshipURI, List newProps)
	{
		Map map = [:]
		newProps.each { map[it] = 'tag' }
		return setRelationshipIndex(relationshipURI, map)
	}
	
	def setRelationshipIndex(String relationshipURI, String tags)
	{
		// Turn props.tag (String) into a proper List
		def tagList = []
		tagList = tags.tokenize(" ,;") // Use " ,;" to cut tags from each others
		
		Map map = [:]
		tagList.each { map[it] = 'tag' }
		return setRelationshipIndex(relationshipURI, map)
	}
	
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

    /**
     * Returns a list of all existing properties on relationships and their number
     *
     * @return Hashmap of [property : number]
     */
    def getAllProperties() {
        //TODO go through index of edges and collect all properties. Cache this later.
        def props = [:]
        def json = graphCommunicatorService.neoGet('/db/data/index/relationship/edges', ['query': '*:*'])
        //TODO: not optimal solution, because there are too many edges returned. moreover, this can be done better using groovy magic
        json.data.each {
            def edge = it
            edge.each {
                def tag = it.key
                if (props[tag] == null) {
                    props[tag] = 1
                } else {
                    props[tag] = props[tag] + 1
                }
            }
        }
        return props
        //println 'All tags: ' + props
    }
	
	def getIncomingRelationships(User user, String tag) {
		def query = ' '
		query += " start me=node:super_index(email={email}) "
		query += ' match user-[r:connect]->me '
		if(tag)
			query += ' where has(r.`'+ tag +'`) '
		query += ' return r, user '
		
		// Execute the query
		def json = graphCommunicatorService.neoPost(cypherPlugin, '{"query": "'+ query +'", "params": {"email":"'+ user.email +'", "tag":"'+ tag +'"}}')
		
		def relationships = []
		json.data.each {
			
			// it[0] = r
			// it[1] = user
			
			// Get foreign user
			def User contact = new User()
			contact.bind(it[1])
			
			// Build the Relationship object
			def ConnectRelationship rel = new ConnectRelationship()
			rel.bind(it[0])
			rel.start = contact
			rel.end = user
			
			relationships.add(rel)
		}
		
		return relationships
	}

    /**
     * Remove a relationship using start and end node
     * @param props (startNode, endNode)
     * @return
     */
    def deleteRelationship(Map props) {
        def result = ''
        def from = getNodeURIFromEmail(props.startNode)
        def to = getNodeURIFromEmail(props.endNode)
        def rel = readRelationship(props)[0]
        log.info('Nodes to disconnect are: ' + from + ' -> ' + to + ', it is relationship with URI: ' + rel)
        //TODO first remove all properties for this relationship from the index!
        //remove from graph
        graphCommunicatorService.neoDelete(rel)
        //assert that rel does not exist
        log.info(graphCommunicatorService.neoGet(rel))
    }
	
	def reindexRelationships() {
		def json = graphCommunicatorService.neoGet('/db/data/index/relationship/edges', ['query': '*:*'])
		json.each {
			def tags = []
			it.data.each { tags.add(it.key) }
		}
	}
	

    // *********************** old code starts here ***************************/
    // *********************** old code ***************************/
    // *********************** old code ***************************/
    // *********************** old code ***************************/
    // *********************** old code ***************************/
    /*
     def getProperties(String filter) {
         def Set props = []
         def json = graphCommunicatorService.neoGet('/db/data/index/relationship/edges',['query' : '*:'+filter])
         println json
         //TODO: not optimal solution, because there are too many edges returned
         json.data.each {
             props.addAll(it.keySet())
         }
         return props.toList().sort()
     }

     def getNodesFromEdge(String edge) {
         def nodes = []
         println edge
         def json = graphCommunicatorService.neoGet(edge)
         nodes.add(json.start)
         nodes.add(json.end)
         return nodes
     }


     def getAllEdges(String source, String target){
         // Source and target are node addresses
         // return List of Edges
         def postBody = ['to' : target, 'max_depth': 1]
         println "S:" + source + "/paths" + postBody
         def json = graphCommunicatorService.neoPost(source+'/paths', postBody)
         def rels = json.relationships
         def returnValues = []
         rels.each{
             returnValues.add(it[0])
         }
         return returnValues
     }

     def connectPeople(String source, String target, String props) {
         //create nodes if they do not exist
         createNode([name : source])
         createNode([name: target])
         createEdge([source, target, props])
     }

     def disconnectPeople(String source, String target ){
         def result = deleteAllEdges([source, target])
         return result
     }



     def getFilteredEdges (List props){
         def query = props.join(':* OR ') + ':*'
         def json = graphCommunicatorService.neoGet('/db/data/index/relationship/edges', ['query' : query])
         return json.self
     }

     def getUserEdges (String name, List props, Integer depth){
         def node = findNodeByName(name)
         def resultingEdges = []
         def postBody = ""
         postBody = ['order' : 'depth_first', 'uniqueness' : 'node_path', 'max_depth' : depth]
         def json = graphCommunicatorService.neoPost(node[0]+'/traverse/relationship', postBody)
         //filter for props edges only (TODO, could be maybe done already in neo4j instead of application level)
         json.each {
             def edge = it
             edge.data.each {
                 if ((props.size() == 0) || (props.contains(it.key))) { //if no filter, just add all edges
                     resultingEdges.add(edge.self)
                 }
                 println 'getUserEdges, another edge: ' + edge
             }
         }
         println "getUserEdges, with depth: " + resultingEdges
         return resultingEdges
         }

     def getNeighbours(HashMap parameters) {
         //parameters contain user session, and others
         def postBody, result

         println "Filters used: " + parameters.filter
         def centerNode = findNodeByEmail(parameters.userCenter.email)[0]

         if (parameters.filter != null && parameters.filter != ''){
         //1. Index nach Filter suchen
         def filteredEdges = graphCommunicatorService.neoGet('/db/data/index/relationship/edges/tag/'+ parameters.filter)

         //2. Resultat for each nach length zu center
         println filteredEdges

         def lengthMap = [:]
         filteredEdges.each {
             // Length f�r Center zu Node ermitteln
             postBody = ['to' : it.end, 'algorithm' : 'shortestPath','max_depth':10]
             result = graphCommunicatorService.neoPost (centerNode+'/path/', postBody)
             println "R: " + it.end+ " " + result.length
             lengthMap.put(findNameByNode(it.end),result.length)
             }
         //3. Sort nach length und return
         return lengthMap.sort { a, b -> a.value <=> b.value }
         }
         else {
         // gib alle Kontakte aus (CT: auf 2 reduziert)
         postBody = ['order' : 'breadth_first', 'max_depth' : 2]
         def path = graphCommunicatorService.neoPost(centerNode+'/traverse/path', postBody)
         //def result = ['center':parameters.userCenter.email , 'centerNode' : centerNode, 'path' : path]
         // result = empty Map
         result = [:]
         path.each {
             println 'Searching neighbourhood:' + findNameByNode(it.end)
             result.put(findNameByNode(it.end), it.length)
              }
         println 'Neighbourhood search finished: ' + result
         return result
         }

     }

     /*

     def createEdge (List edgeProperties) {
         String node1 = findNodeByEmail(edgeProperties[0])[0]
         String node2 = findNodeByEmail(edgeProperties[1])[0]
         println ('Nodes to connect are: ' + node1 + '->' + node2)
         def postBody = ['to' : node2, 'type': 'connect']
         def relationship = graphCommunicatorService.neoPost(node1+'/relationships', ['to' : node2, 'type': 'connect']).self

         //update properties with default strength 1
         def String props = edgeProperties[2] //csv separated properties
         def String[] allProperties = props.tokenize(';')
         println "Property imported for edge is: " + allProperties
         def m = [:]
         //each property gets an initial 1 value assigned, i.e. the list is converted to map
         allProperties.each { m[it]=1 }
         graphCommunicatorService.neoPut(relationship+'/properties',  m )
         allProperties.each() { prop ->
             //add this relationship to index with this property
             def indexPath = '/db/data/index/relationship/edges/'
             postBody = ['value' : prop, 'key' : 'tag', 'uri' : relationship]
             graphCommunicatorService.neoPost(indexPath, postBody)
         }
     }

     def deleteAllEdges (List edgeProperties) {
         def result = ''
         if (exists(edgeProperties[0])&& exists(edgeProperties[1])) {
                 String node1 = findNodeByName(edgeProperties[0])[0]
                 String node2 = findNodeByName(edgeProperties[1])[0]
                 println ('Nodes to disconnect are: ' + node1 + ' -> ' + node2)
                 def allEdges = graphCommunicatorService.neoPost(node1 + '/paths', ['to' : node2,'direction':'out','max_depth': 1])

                 if (allEdges.size() != 0){
                     def rels = allEdges.relationships
                     // das muss doch besser gehen
                     rels.each(){ json ->
                         json.each(){ json2 ->
                             graphCommunicatorService.neoDelete(json2)
                         }
                     }
                     result = 'Disconnected'
                 }
                 else {result = 'Nothing to disconnect'}
         }
     }



     def getGraphEdgesJSON(List allEdges) {

     def values = getPropsForEdge(allEdges)
     def converter = values as grails.converters.JSON
     println converter
     return converter
     }

     /*
      * not needed anymore, was used for exporting the whole graph
      *

      def getGraphJSON(List allEdges, String username) {

         //println 'Render graph from filtered edges..'
         //format sample: '{"nodes":[{"name":"Myriel","group":1},{"name":"Napoleon","group":1},{"name":"Mlle.Baptistine","group":1},{"name":"Mme.Magloire","group":1{"name":"Brujon","group":4},{"name":"Mme.Hucheloup","group":8}],"links"[{"source":1,"target":0,"value":1},{"source":2,"target":0,"value":8},{"source":3,"target":0,"value":10},{"source":3,"target":2,"value":6},{"source":4,"target":0,"value":1},{"source":5,"target":0,"value":1]}'
         def countMapping = new HashMap() //node ID -> count, needed for force.js
         def nodes = new ArrayList()
         def links = new ArrayList()
         def name

         int count = 0
         //println 'Number of edges to render: ' + allNodes.size()
         allEdges.each {
             getNodesFromEdge(it).each() {
                 if (!countMapping.containsKey(it)) {

                     //get the username
                     name = findNameByNode(it)

                     // check, if username, then highlight
                     if (name == username)
                         nodes.add(['name':name, 'group':1, 'number':count])
                     else
                         nodes.add(['name':name, 'group':2, 'number':count])

                     countMapping.putAt(it, count)
                     count = count+1
                 }
             }
         }
         //println ('Nodes to render: ' + nodes)

         //nodes list complete. add links. TODO merge with previous loop, but easier for now

         allEdges.each {
             def edgeNodes = getNodesFromEdge(it)
             links.add('source':countMapping.get(edgeNodes[0]), 'target':countMapping.get(edgeNodes[1]),'value':'1')
         }
         def jsongraph = new HashMap()
         jsongraph.put('nodes',nodes)
         jsongraph.put('links',links)
         def converter = jsongraph as grails.converters.JSON
         println converter
         return converter
     }	*/
}
