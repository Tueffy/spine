package spine

import java.util.regex.*

class NetworkService {
    static transactional = false

    def graphCommunicatorService
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

    /** Create node and add to index (all properties)
     * @param props
     * @return
     */
    def createNode(Map props) {
        //TODO: check if node exists already via readNode
        def newNodeRef = graphCommunicatorService.neoPost('/db/data/node', props)
        addNodeIndex(newNodeRef.self, props)
        return newNodeRef
    }

    /** Update node with new properties, the old ones remain. For this, the node needs to be removed from index and added again
     * @param email
     * @param props
     * @return
     */
    def updateNode(String email, Map props) {
        def json = graphCommunicatorService.neoGet('/db/data/index/node/names/email', ['query': '"' + email + '"'])
        //node URL is self
        def newProperties = json.data[0]
        //replace only the properties that are passed, but neo always requires all properties to be passed as PUT
        props.each {
            println it
            newProperties.(it.getKey()) = it.getValue()
        }
        //remove all old index entries
//        removeNodeIndex(json.self[0])
		graphCommunicatorService.neoDelete(json.self[0])
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

    /**
     * Uses the cypher plugin to retrieve the neighbours using offset and limit. The result includes the start node of the query itself.
     *
     * @param email id of the person to start the query
     * @param offset pagination value
     * @param limit max of how many neighbours are searched
     * @return The found nodes data. If no neighbours are found, returns null.
     */
	def queryForNeighbourNodes(String email, int offset, int limit, List filter = [])
	{
		// TODO : Whant there is not enough result in the user network in case of search, complete with results from the whole network
		// TODO : The function is becomming too complex, must be splitted into more atomic functions
		// TODO : Refectoring - refactor the way search work to make it less complicated and more maintenable and to get more performances
		// TODO : Pagination associated with search seems not to be working (need to propagate the search query)
		
		// Vars initialization
		def resultNodes = []
		def neighbour = [:]
		
		/*-------------------------------------------------------------
		 * 
		 * 	FIRST QUERY : Get results from the User Network
		 * 
		 * ----------------------------------------------------------- */
		// Build the query
		def query = "start n=node:names(email={SP_user}) match p=n-[:connect*1..5]->(x) "
		if(!filter.isEmpty()) {
			query += "where all(r in rels(p) WHERE "
			filter.each {
				query += "r.`" + it + "` OR "
			}
			query += "1=0) "
		}
		query += "return distinct x, min(length(p)), count(*) AS nbResults "
		query += "order by min(length(p)) skip " + offset + " limit " + limit + " "
		
		println "\n\n\n"
		println query
		println "\n\n\n"
		
		// Execute the query
		def cypherPlugin = '/db/data/ext/CypherPlugin/graphdb/execute_query'
		def json = graphCommunicatorService.neoPost(cypherPlugin, '{"query": "'+ query +'", "params": {"SP_user":"' + email + '"}}')
		
		// How many results ? 
		int firstQueryNbResults = 0
		if(json.data) firstQueryNbResults = json.data[0][2]
		
		// Get results
		json.data.each {
			neighbour = it[0].data
			neighbour['distance'] = it[1]
			println "Elem: " + neighbour
			resultNodes.add(neighbour)
		}
		
		
		
		/*-------------------------------------------------------------
		*
		* 	SECOND QUERY : Get results from the whole Spine Network 
		*
		* ----------------------------------------------------------- */
		// We only execute the second query if there no more (or not enough) 
		// result to get from the first one.  
		if(resultNodes.size() < limit && !filter.isEmpty())
		{
			int newOffset = offset / limit + 1
			int newLimit = limit - resultNodes.size()
			
			// TODO : Only support one tag search, add multi tag support
			// Check here : https://groups.google.com/forum/?hl=fr#!topic/neo4j/dWOsK6meGHs 
			
			String luceneQuery = ''
			for (i in 0..(filter.size() - 1)) 
			{
				luceneQuery += filter[i] + ':"tag" '
				if(i < filter.size() - 1) luceneQuery += 'OR '
			} // TODO : Use this for multi tag support (lucene query)
			
			query = 'start '
			query += 'r=relationship:edges('+ filter[0] +'="tag"), n=node:names(email={SP_USER})  '
			query += 'a-[r:connect]->b, a-[r2:connect*1..5]->c '
			query += 'where r2 is null '
			query += 'return distinct b '
			
			println "\n\n\n"
			println query
			println "\n\n\n"
			
			// Execute the query
			cypherPlugin = '/db/data/ext/CypherPlugin/graphdb/execute_query'
			json = graphCommunicatorService.neoPost(cypherPlugin, '{"query": "'+ query +'", "params": {"SP_user":"' + email + '"}}')
			
			// Get results
			json.data.each {
				neighbour = it[0].data
				neighbour['distance'] = it[1]
				println "Elem: " + neighbour
				resultNodes.add(neighbour)
			}
		}
		
		
		return resultNodes
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
        println json
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
        println 'relationship to be created is:' + from + ' -> ' + to
		
        def createdRelationship
        if (!relationship) {
            createdRelationship = graphCommunicatorService.neoPost(from + '/relationships', ['to': to, 'type': 'connect']).self
			// Apply tags to the newly created relationship 
			if(props.tags) {
				setProperty(['relationshipURI': createdRelationship, 'tags': props.tags])
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
        println 'Found relationship: ' + resultRelationship
        return resultRelationship
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
		
		tagList.each { allExistingTags[it] = 1 }
		
		// Update in DB
        graphCommunicatorService.neoPut(relationship + '/properties', allExistingTags)
		
        // add to index
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
		setRelationshipIndex(relationship, actualTags)
		
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
		println "relationshipURI" + relationshipURI
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
        println('Nodes to disconnect are: ' + from + ' -> ' + to + ', it is relationship with URI: ' + rel)
        //TODO first remove all properties for this relationship from the index!
        //remove from graph
        graphCommunicatorService.neoDelete(rel)
        //assert that rel does not exist
        println graphCommunicatorService.neoGet(rel)
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
