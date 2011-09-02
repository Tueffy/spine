package spine

import java.util.List;

//import groovyx.net.http.HTTPBuilder
import grails.converters.JSON
import static groovyx.net.http.ContentType.JSON
import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient

class NetworkService {
    static transactional = false
	def http = new RESTClient( 'http://localhost:7474' )
	//def http = new RESTClient( 'http://localhost:7575' ) //tcpmon
	
	def getProperties() {
		def Set props = []
		//iterate through all relationships
		try {
			//TODO not optimal to retrieve all edges, just to get the list of properties
			http.get( path: '/db/data/index/relationship/edges' , query: ['query' : '*:*'],  requestContentType: groovyx.net.http.ContentType.JSON    ) {resp, json ->
				json.data.each { props.addAll(it.keySet())}
			}
		} catch (HttpResponseException ex) {
			println 'Nothing found when filtering edges: ' + ex.toString()
			return [] //no edge found with this name, result set empty
		}
		return props.toList().sort()
	}
	
	def getNodesFromEdge(String edge) {
		def nodes = []
		http.get( path: edge,  requestContentType: groovyx.net.http.ContentType.JSON    ) {resp, json ->
			nodes.add(json.start)
			nodes.add(json.end)
		}
		return nodes
	}
	
	def findNodeByName(String name) {
		def result
		try {
			//lucene query uses " because of whitespaces in the name..
			http.get( path: '/db/data/index/node/names/name' , query: ['query' : '"'+name+'"'],  requestContentType: groovyx.net.http.ContentType.JSON    ) {resp, json ->
				result =  json
		} } catch (HttpResponseException ex) {
			return [] //no node found with this name, result set empty
		}
		return result.self
	}
	
	def getAllNodes() {
		def result
		try {
			http.get( path: '/db/data/index/node/names/name' , query: ['query' : '*'],  requestContentType: groovyx.net.http.ContentType.JSON    ) {resp, json ->
				result =  json
		} } catch (HttpResponseException ex) {
			return [] //no node found with this name, result set empty
		}
		return result.self
	}
	
	def connectPeople(String source, String target, String props) {
		//create nodes if they do not exist
		createNode(source)
		createNode(target)
		createEdge([source, target, props])
	}
	
	def getFilteredEdges (List props){
		def result
		//create query string for Lucene
		def query = props.join(':* OR ') + ':*'
		println 'Query string: ' + query
		try {
			http.get( path: '/db/data/index/relationship/edges' , query: ['query' : query],  requestContentType: groovyx.net.http.ContentType.JSON    ) {resp, json ->
				result =  json
				//println 'Query Result: '
				//println result
		} } catch (HttpResponseException ex) {
			println 'Nothing found when filtering edges'
			return [] //no edge found with this name, result set empty
		}
		return result.self
	}
	
	def findNameByNode(String node) {
		http.get( path: node,  requestContentType: groovyx.net.http.ContentType.JSON    ) {resp, json ->
			return json.data.name
		}
	}
	
	def getTargets(String node) {
		http.get( path: node+'/relationships/out',  requestContentType: groovyx.net.http.ContentType.JSON    ) {resp, json ->
			//println json.end
			return json.end
		}
	}
	
	def exists(String nameUser)
	{
		List result = findNodeByName(nameUser)
		if (result.size() == 0) { 
			return false
		} else {
			return true
		}
	}
	
	def createNode(String name) {
		if (exists(name)) {
			//println "Node already exists, will do nothing.."
			return
		}
		def newNodeRef = ""

		def postBody = ['name' : name]
		http.post( path: '/db/data/node', body: postBody, requestContentType: groovyx.net.http.ContentType.JSON    ) {resp, json ->
			newNodeRef = json
		}
		//add to lucene index with key "name"
		def indexPath = '/db/data/index/node/names/name/' + newNodeRef.data.getAt('name')
		postBody = '\"' + newNodeRef.self + '\"'
		http.post( path: indexPath, body: postBody, requestContentType: groovyx.net.http.ContentType.JSON    ) {resp, json ->
		}
				
		//println "Created new node: " + name
	}
	
	//creates a new user in the database
	def createNewUser(String name, String email, String password) {
		if (exists(name)) {
			//println "Node already exists, will do nothing.."
			return
		}
		def newNodeRef = ""

		def postBody = ['name' : name]
		http.post( path: '/db/data/node', body: postBody, requestContentType: groovyx.net.http.ContentType.JSON    ) {resp, json ->
			newNodeRef = json
		}
		//add to lucene index with key "name"
		def indexPath = '/db/data/index/node/names/name/' + newNodeRef.data.getAt('name')
		postBody = '\"' + newNodeRef.self + '\"'
		http.post( path: indexPath, body: postBody, requestContentType: groovyx.net.http.ContentType.JSON    ) {resp, json ->
		}
				
		//println "Created new node: " + name
	}
	
	def createEdge (List edgeProperties) {
		if (!exists(edgeProperties[0])) { //start node
			println "Source node not found, will create it.."
			createNode(edgeProperties[0])
		}
		if (!exists(edgeProperties[1])) { //end node
			println "Target node not found, will create it.."
			createNode(edgeProperties[1])
		}
		String node1 = findNodeByName(edgeProperties[0])[0]
		String node2 = findNodeByName(edgeProperties[1])[0]
		println ('Nodes to connect are: ' + node1 + '->' + node2)
		def postBody = ['to' : node2, 'type': 'connect']
		//create relationship
		def relationship
		http.post( path: node1+'/relationships', body: postBody, requestContentType: groovyx.net.http.ContentType.JSON    ) {resp, json ->
			relationship = json.self
		}
				
		//update properties with default strength 1
		def String props = edgeProperties[2] //csv separated properties
		def String[] allProperties = props.tokenize(';')
		//println 'All properties: ' + allProperties
		allProperties.each() { prop ->
			def putBody = [ (prop) : 1 ]
			http.put( path: relationship+'/properties', body: putBody, requestContentType: groovyx.net.http.ContentType.JSON  )

			//add this relationship to index with this property
			def indexPath = '/db/data/index/relationship/edges/' + prop + '/1'
			postBody = '\"' + relationship + '\"'
			//println 'Adding relationship to index with: '
			//println indexPath + '->' + relationship
			http.post( path: indexPath, body: postBody, requestContentType: groovyx.net.http.ContentType.JSON    ) {resp, json ->
			}
		}
	}
	
    def importEdges(String file) {
		def input = file.splitEachLine("\t") {fields ->
			createEdge(fields)
		}
		println "Edges loaded!"
	}
	
	def getGraphJSON(List allEdges) {
		/*TODO
		 * for each edge, extract nodes (they should not be counted twice)
		 * then create links in JSON, based on edges list and link to extracted nodes
		 */
		
		//println 'Render graph from filtered edges..'
		//format sample: '{"nodes":[{"name":"Myriel","group":1},{"name":"Napoleon","group":1},{"name":"Mlle.Baptistine","group":1},{"name":"Mme.Magloire","group":1{"name":"Brujon","group":4},{"name":"Mme.Hucheloup","group":8}],"links"[{"source":1,"target":0,"value":1},{"source":2,"target":0,"value":8},{"source":3,"target":0,"value":10},{"source":3,"target":2,"value":6},{"source":4,"target":0,"value":1},{"source":5,"target":0,"value":1]}'
		def countMapping = new HashMap() //node ID -> count, needed for force.js
		def nodes = new ArrayList()
		def links = new ArrayList()
		int count = 0
		//println 'Number of edges to render: ' + allNodes.size()
		allEdges.each {	
			getNodesFromEdge(it).each() {
				if (!countMapping.containsKey(it)) {
					nodes.add(['name':findNameByNode(it), 'group':1, 'number':count])
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
		//println converter
		return converter
	}
	
}
