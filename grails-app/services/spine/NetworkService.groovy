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
	
	/**
	 * uses the cypher plugin to retrieve the neighbours using offset and limit
	 * 
	 * @param email
	 * @param offset
	 * @param limit
	 * @return
	 */
	def readNodeViaCypher(String email, int offset, int limit) {

		def json = graphcomm.neoPost('/db/data/ext/CypherPlugin/graphdb/execute_query', '{"query": "start n=node:names(email={SP_user}) match (n)-[:connect*1..5]->(x) return x skip '+offset+' limit '+limit+'","params": {"SP_user":"'+email+'"}}')
		def resultNodes = []
		json.data.each {
			//println "Elem: "+it[0].data
			resultNodes.add(it[0].data)
		}

		return resultNodes
	}
	
	/*
	 * 
	 * Network service to provide access to nodes, relationships and properties.
	 * The naming convention is CRUD - Create, Read, Update, Delete, Exists 
	 * 
	 */
	
	//Provides all data for a user
	def readNode(String email) {
		def json = graphcomm.neoGet('/db/data/index/node/names/email', ['query' : '"'+email+'"'])
		return json.data[0]
	}
	
	def createNode(Map props) {
		//TODO: check if node exists already via readNode
		def newNodeRef = graphcomm.neoPost('/db/data/node', props)
		addNodeIndex(newNodeRef.self,props)
		return newNodeRef
	}
	
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
	
	def deleteNode(String email) {
		def json = graphcomm.neoGet('/db/data/index/node/names/email', ['query' : '"'+email+'"'])
		def nodeURL = json.self[0]
		//TODO: delete all relationships related to node before deleting node
		//remove node from index
		removeNodeIndex(email)
		//remove node itself
		graphcomm.neoDelete(nodeURL)
	}
	
	def existNode(String email) {
		
	}
	
	/**
	 * 
	 * @param queryObject: Pass a map with the properties.
	 * key: email, value: expression to filter for nodes, e.g. j* would result in all emails starting with j* 
	 * @return
	 */
	def queryNode(Map queryObject) {
		def json = graphcomm.neoGet('/db/data/index/node/names/email', ['query' : queryObject.email])
		def resultEmails = []
		json.each {
			resultEmails.add(it.data.email)
		}
		return resultEmails
	}
	
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
	
	def removeNodeIndex(String email) {
		//DELETE /index/node/my_nodes/123
		def indexPath = '/db/data/index/node/names/'+email
		graphcomm.neoDelete(indexPath)
		
	}
	
	
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
	
	/* merged, deprecated
	 * def findNodeByName(String name) {
		def json = graphcomm.neoGet('/db/data/index/node/names/name', ['query' : '"'+name+'"'])
		return json.self
	}
	*/
	
	/* merged, deprecated
	 * def findNodeByEmail(String email) {
		def json = graphcomm.neoGet('/db/data/index/node/names/email', ['query' : '"'+email+'"'])
		return json.self
	}*/
	
	/* merged, deprecated
	def getPropertiesByEmail(String email) {
		println 'Searching node by email..'
		def node = findNodeByEmail(email)
		def json = graphcomm.neoGet(node)
		println json.data
		return json.data
	}
	*/
	
	/* merged, deprecated
	def getAllNodes() {
		def json = graphcomm.neoGet('/db/data/index/node/names/name', ['query' : '*'])
		return json.self
	}*/
	
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
	
	def getPropsForEdge (List edges) {
		def props = []
		edges.each {
			def json = graphcomm.neoGet(it + '/properties')
			json.each { props.add(it.key) }
		}
		return props
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
	
	/*
	def findNameByNode(String node) {
		def json = graphcomm.neoGet(node)
		return json.data.email
	}*/
/*
	def getTargets(String node) {
		def json = graphcomm.neoGet(node+'/relationships/out')
		return json.end
	}*/
	
	
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
	
	def exists(String nameUser)
	{
		List result = findNodeByName(nameUser)
		if (result.size() == 0) { 
			return false
		} else {
			return true
		}
	}*/
	
	/*
	def createNode(HashMap props) {		
		def newNodeRef = graphcomm.neoPost('/db/data/node', props)
		//put name into index
		def indexPath = '/db/data/index/node/names/'
		
		//put last name into index
		def postBody = ['value' : props.lastName, 'key' : 'name', 'uri' : newNodeRef.self]
		graphcomm.neoPost(indexPath, postBody)
		//put email into index
		postBody = ['value' : props.email, 'key' : 'email', 'uri' : newNodeRef.self]
		graphcomm.neoPost(indexPath, postBody)
	}*/
	
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
		
    def importEdges(String file) {
		def input = file.splitEachLine("\t") {fields ->
			createEdge(fields)
		}
		println "Edges loaded!"
	}
	
	def importNodes(String file) {
		//per node from file, create a hashmap with properties, which are imported
		def input = file.splitEachLine("\t") {fields ->
			println fields
			def props = [:]
			props['firstName'] = fields[0]
			props['lastName'] = fields[1]
			props['email'] = fields[2]
			props['password'] = fields[3]
			props['city'] = fields[4]
			props['country'] = fields[5]
			props['image'] = fields[6]
			createNode(props)
		}
		println "Nodes created!"
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
