package spine

import java.util.List;

import grails.converters.JSON
import static groovyx.net.http.ContentType.JSON
import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient

class NetworkService {
	static transactional = false
	def http = new RESTClient( 'http://localhost:7474' )
	def graphcomm = new GraphCommunicatorService()
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
		def json = graphcomm.neoGet('/db/data/index/node/names/email', ['query' : '"'+email+'"'])
		return json.data[0]
	}

	/** Create node and add to index (all properties)
	 * @param props
	 * @return
	 */
	def createNode(Map props) {
		//TODO: check if node exists already via readNode
		def newNodeRef = graphcomm.neoPost('/db/data/node', props)
		addNodeIndex(newNodeRef.self,props)
		return newNodeRef
	}

	/**Update node with new properties, the old ones remain. For this, the node needs to be removed from index and added again
	 * @param email
	 * @param props
	 * @return
	 */
	def updateNode(String email, Map props) {
		def json = graphcomm.neoGet('/db/data/index/node/names/email', ['query' : '"'+email+'"'])
		//node URL is self
		def newProperties = json.data[0]
		//replace only the properties that are passed, but neo always requires all properties to be passed as PUT
		props.each {
			println it
			newProperties.(it.getKey()) = it.getValue()
		}
		//remove all old index entries
		removeNodeIndex(json.self[0])
		//add new properties to node
		graphcomm.neoPut(json.self[0]+'/properties',  newProperties)
		//add new index entries
		addNodeIndex(json.self[0], newProperties)
	}

	/** Delete node and remove from index
	 * @param email identifier of the node
	 * @return
	 */
	def deleteNode(String email) {
		def json = graphcomm.neoGet('/db/data/index/node/names/email', ['query' : '"'+email+'"'])
		def nodeURL = json.self[0]
		//TODO: delete all relationships related to node before deleting node
		//remove node from index
		removeNodeIndex(email)
		//remove node itself
		graphcomm.neoDelete(nodeURL)
	}
	
	
	

	/** Get a neo4j URI for a email node (utility function, used only internally)
	 * @param email
	 * @return
	 */
	def getNodeURIFromEmail(String email){
		def json = graphcomm.neoGet('/db/data/index/node/names/email', ['query' : '"'+email+'"'])
		return json.self[0]
	}

	/**
	 * 
	 * 
	 * @param queryObject: Pass a map with the properties.
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
			queryString = queryString.substring(0, queryString.size()-5)
			//println 'Query string: ' + queryString
			def json = graphcomm.neoGet('/db/data/index/node/names', ['query' : (queryString)])
			//println 'Query JSON result: ' + json
			json.each {
				queryResult.add(it.data.email)
			}
		}
		return queryResult
	}
	
	/**
	* Uses the cypher plugin to retrieve the neighbours using offset and limit. The result includes the start node of the query itself.
	*
	* @param email id of the person to start the query
	* @param offset pagination value
	* @param limit max of how many neighbours are searched
	* @return The found nodes data. If no neighbours are found, returns null.
	*/
   def queryForNeighbourNodes(String email, int offset, int limit) {

	   def json = graphcomm.neoPost('/db/data/ext/CypherPlugin/graphdb/execute_query', '{"query": "start n=node:names(email={SP_user}) match (n)-[:connect*1..5]->(x) return x skip '+offset+' limit '+limit+'","params": {"SP_user":"'+email+'"}}')
	   def resultNodes = []
	   json.data.each {
		   //println "Elem: "+it[0].data
		   resultNodes.add(it[0].data)
	   }
	   return resultNodes
   }

	/**Returns all properties for the relationship pointing to the node referred to and their number. 
	 * E.g. for markus.long@techbank.com, it returns ['ITIL':3, 'Help':1, 'Operations':3, 'Desk':1, 'IT':2]
	 * 
	 * @param email
	 * @return map of relationship properties (tags) and their number
	 */
	def getIncomingTagsForNode(String email)  {
		//returns the incoming tags per node and their number
		def tagMap = [:]
		def json = graphcomm.neoGet('/db/data/index/node/names/email', ['query' : '"'+email+'"'])
		//get incoming relationships from incoming_relationships
		json = graphcomm.neoGet(json.incoming_relationships[0])
		json.each {
			def allTagsForRelationship = it.data //this could be [Operations:1, Help Desk:1]
			allTagsForRelationship.each {
				def tag = it.key
				if (!tagMap.containsKey(tag)) { //add tag to tagmap with value
					tagMap[tag] = 1
				} else {
					tagMap[tag] = tagMap[tag] +1
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
		postBody = ['value' : props.lastName, 'key' : 'lastName', 'uri' : nodeRef]
		graphcomm.neoPost(indexPath, postBody)

		//put first name into index
		postBody = ['value' : props.firstName, 'key' : 'firstName', 'uri' : nodeRef]
		graphcomm.neoPost(indexPath, postBody)

		//put email into index
		postBody = ['value' : props.email, 'key' : 'email', 'uri' : nodeRef]
		graphcomm.neoPost(indexPath, postBody)

		//put country into index
		postBody = ['value' : props.email, 'key' : 'country', 'uri' : nodeRef]
		graphcomm.neoPost(indexPath, postBody)

		//put city into index
		postBody = ['value' : props.email, 'key' : 'city', 'uri' : nodeRef]
		graphcomm.neoPost(indexPath, postBody)
	}

	/** 
	 * Removes node from index (should not be used directly)
	 * @param email
	 * @return void
	 */
	def removeNodeIndex(String email) {
		//DELETE /index/node/my_nodes/123
		def indexPath = '/db/data/index/node/names/'+email
		graphcomm.neoDelete(indexPath)
	}
	
	// ----------------  Relationship related services

	/**
	 *TOOD create test with create and remove
	 * @param props (startNode, endNode, tags)
	 * @return data of the created relationship
	 */
	def createRelationship(Map props) {
		//TODO: check whether Rel exists. No: create Relationship.
		def from = getNodeURIFromEmail(props.startNode)
		def to = getNodeURIFromEmail(props.endNode)
		def relationship = readRelationship(props)
		//println 'relationship to be created is:' + from + ' -> ' + to
		def createdRelationship
		if (relationship.size() == 0) {
			createdRelationship = graphcomm.neoPost(from+'/relationships', ['to' : to, 'type': 'connect']).self[0]
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
		def incomingRelationships = graphcomm.neoGet(to + '/relationships/in')
		incomingRelationships.each {
			if (it.start == from) {
				resultRelationship.add(it.self)
			}
		}
		println 'Found relationship: ' + resultRelationship
		return resultRelationship
	}

	/**
	 *
	 * @param props (relationship URI, tags)
	 * @return
	 */
	def setProperty(Map props){
		// tokenize tags and add to relationship if not yet exist for the later
		def relationship = readRelationship(props)[0]
		def tokens = " ,;"
		def tagList = []
		def result = ''
		tagList = props.tags.tokenize(tokens)
		def allExistingTags = [:]
		allExistingTags = graphcomm.neoGet(relationship+'/properties')
		if (allExistingTags == null) { //no properties present
			allExistingTags = [:]
		}
		tagList.each {
			allExistingTags[it]=1
		}
		graphcomm.neoPut(relationship+'/properties',allExistingTags)
		// add to index
		def postBody
		def indexPath = '/db/data/index/relationship/edges/'
		allExistingTags.each {
			postBody = ['value' : it.key, 'key' : 'tag', 'uri' : relationship]
			println 'Index put relationship: ' + postBody
			result = graphcomm.neoPost(indexPath, postBody)
		}
		return result.data
	}
	
	
	/**
	 * Get properties for a list of relationships
	 * @param relationships
	 * @return list of properties for these 
	 */
	def getProperty (List relationships) {
		def props = []
		relationships.each {
			def json = graphcomm.neoGet(it + '/properties')
			json.each { props.add(it.key) }
		}
		return props
	}


	/**
	 * Remove a relationship using start and end node
	 * @param props (startNode, endNode)
	 * @return
	 */
	def deleteRelationship(Map props){
		def result = ''

		def from = readNode(props.startNode)
		def to = readNode(props.endNode)
		println ('Nodes to disconnect are: ' + from.self + ' -> ' + to.self)
		def allEdges = graphcomm.neoPost(from.self + '/paths', ['to' : to.self,'direction':'out','max_depth': 1])

		if (allEdges.size() != 0){
			def rels = allEdges.relationships
			// das muss doch besser gehen
			rels.each(){ json ->
				json.each(){ json2 ->
					graphcomm.neoDelete(json2)
				}
			}
			result = 'Disconnected'
		}
		else {result = 'Nothing to disconnect'}
	}

	def addRelationshipIndex(String relationship){

	}

	def removeRelationshipIndex(String relationship){

	}

	def deleteProperty(Map props) {
		//delete prop on relationship and on index
	}


	// *********************** old code ***************************/
	// *********************** old code ***************************/
	// *********************** old code ***************************/
	// *********************** old code ***************************/
	// *********************** old code ***************************/
	def getProperties(String filter) {
		def Set props = []
		def json = graphcomm.neoGet('/db/data/index/relationship/edges',['query' : '*:'+filter])
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
		def json = graphcomm.neoGet(edge)
		nodes.add(json.start)
		nodes.add(json.end)
		return nodes
	}
	
	
	def getAllEdges(String source, String target){
		// Source and target are node addresses
		// return List of Edges	
		def postBody = ['to' : target, 'max_depth': 1]
		println "S:" + source + "/paths" + postBody
		def json = graphcomm.neoPost(source+'/paths', postBody)
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
		def json = graphcomm.neoGet('/db/data/index/relationship/edges', ['query' : query])
		return json.self
	}
	
	def getUserEdges (String name, List props, Integer depth){
		def node = findNodeByName(name)
		def resultingEdges = []
		def postBody = ""
		postBody = ['order' : 'depth_first', 'uniqueness' : 'node_path', 'max_depth' : depth]
		def json = graphcomm.neoPost(node[0]+'/traverse/relationship', postBody)
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
		def filteredEdges = graphcomm.neoGet('/db/data/index/relationship/edges/tag/'+ parameters.filter)
		
		//2. Resultat for each nach length zu center
		println filteredEdges
		
		def lengthMap = [:]
		filteredEdges.each {  
			// Length für Center zu Node ermitteln 
			postBody = ['to' : it.end, 'algorithm' : 'shortestPath','max_depth':10]
			result = graphcomm.neoPost (centerNode+'/path/', postBody)
			println "R: " + it.end+ " " + result.length
			lengthMap.put(findNameByNode(it.end),result.length)
			}
		//3. Sort nach length und return
		return lengthMap.sort { a, b -> a.value <=> b.value }
		}
		else {
		// gib alle Kontakte aus (CT: auf 2 reduziert)
		postBody = ['order' : 'breadth_first', 'max_depth' : 2]
		def path = graphcomm.neoPost(centerNode+'/traverse/path', postBody)
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
		def relationship = graphcomm.neoPost(node1+'/relationships', ['to' : node2, 'type': 'connect']).self
				
		//update properties with default strength 1
		def String props = edgeProperties[2] //csv separated properties
		def String[] allProperties = props.tokenize(';')
		println "Property imported for edge is: " + allProperties
		def m = [:]
		//each property gets an initial 1 value assigned, i.e. the list is converted to map
		allProperties.each { m[it]=1 } 
		graphcomm.neoPut(relationship+'/properties',  m )
		allProperties.each() { prop ->
			//add this relationship to index with this property
			def indexPath = '/db/data/index/relationship/edges/'
			postBody = ['value' : prop, 'key' : 'tag', 'uri' : relationship]
			graphcomm.neoPost(indexPath, postBody)
		}
	}

	def deleteAllEdges (List edgeProperties) {
		def result = ''
		if (exists(edgeProperties[0])&& exists(edgeProperties[1])) {
				String node1 = findNodeByName(edgeProperties[0])[0]
				String node2 = findNodeByName(edgeProperties[1])[0]
				println ('Nodes to disconnect are: ' + node1 + ' -> ' + node2)
				def allEdges = graphcomm.neoPost(node1 + '/paths', ['to' : node2,'direction':'out','max_depth': 1])

				if (allEdges.size() != 0){
					def rels = allEdges.relationships
					// das muss doch besser gehen
					rels.each(){ json ->
						json.each(){ json2 ->
							graphcomm.neoDelete(json2)
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
