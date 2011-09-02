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

	def getProperties() {
		def Set props = []
		def json = graphcomm.neoGet('/db/data/index/relationship/edges',['query' : '*:*'])
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
	
	def findNodeByName(String name) {
		def json = graphcomm.neoGet('/db/data/index/node/names/name', ['query' : '"'+name+'"'])
		return json.self
	}
	
	def getAllNodes() {
		def json = graphcomm.neoGet('/db/data/index/node/names/name', ['query' : '*'])
		return json.self
	}
	
	def connectPeople(String source, String target, String props) {
		//create nodes if they do not exist
		createNode(source)
		createNode(target)
		createEdge([source, target, props])
	}
	
	def getFilteredEdges (List props){
		def query = props.join(':* OR ') + ':*'
		def json = graphcomm.neoGet('/db/data/index/relationship/edges', ['query' : query])
		return json.self
	}
	
	def findNameByNode(String node) {
		def json = graphcomm.neoGet(node)
		return json.data.name
	}
	
	def getTargets(String node) {
		def json = graphcomm.neoGet(node+'/relationships/out')
		return json.end
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
		if (exists(name)) { //if name exists, do nothing
			return
		}
		def newNodeRef = graphcomm.neoPost('/db/data/node', ['name' : name])
		def indexPath = '/db/data/index/node/names/name/' + newNodeRef.data.getAt('name')
		def postBody = '\"' + newNodeRef.self + '\"'
		graphcomm.neoPost(indexPath, postBody)
	}
	
	def createEdge (List edgeProperties) {
		if (!exists(edgeProperties[0])) { //start node
			createNode(edgeProperties[0])
		}
		if (!exists(edgeProperties[1])) { //end node
			createNode(edgeProperties[1])
		} 
		String node1 = findNodeByName(edgeProperties[0])[0]
		String node2 = findNodeByName(edgeProperties[1])[0]
		println ('Nodes to connect are: ' + node1 + '->' + node2)
		def postBody = ['to' : node2, 'type': 'connect']
		def relationship = graphcomm.neoPost(node1+'/relationships', ['to' : node2, 'type': 'connect']).self
				
		//update properties with default strength 1
		def String props = edgeProperties[2] //csv separated properties
		def String[] allProperties = props.tokenize(';')
		//println 'All properties: ' + allProperties
		allProperties.each() { prop ->
			graphcomm.neoPut(relationship+'/properties',  [ (prop) : 1 ] )
			//add this relationship to index with this property
			def indexPath = '/db/data/index/relationship/edges/' + prop + '/1'
			graphcomm.neoPost(indexPath, '\"' + relationship + '\"')
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