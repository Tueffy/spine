package spine

import java.nio.charset.Charset
import java.util.regex.Matcher
import java.util.regex.Pattern

import org.springframework.beans.factory.InitializingBean

import spine.exception.graphdb.GraphDbException
import spine.exception.graphdb.RelationshipNotFoundException

import groovyx.net.http.RESTClient
import static groovyx.net.http.ContentType.*

class Neo4jService implements InitializingBean {

	static transactional = false
	def grailsApplication
	def String baseUrl = "http://localhost:7474/db/"
	def RESTClient rest
	
	def Neo4jService() {
		
	}
	
	/**
	 * 
	 */
	def void afterPropertiesSet() throws Exception {
		if(grailsApplication.config.neo4j.test.baseUrl)
			baseUrl = grailsApplication.config.neo4j.test.baseUrl
		rest = new RESTClient(baseUrl)
	};
	
	/*
	 * General
	 */
	
	/**
	 * Execute a given Cypher query with parameters. 
	 * @param query
	 * @param params
	 * @return
	 */
	def doCypherQuery(String query, Map params = [:]) {
		
		// Build the request Map
		def request = [
			path: "data/cypher",
			requestContentType: JSON,
			body: [
				"query": query,
				"params": params
			]
		]
		
		def response = rest.post(request)
		if(response.status != 200)
			throw new Exception("Cypher query failed")
		return response.data
	}
	
	/**
	 * Delete all the nodes and the relationships from the database excepts the
	 * root node. 
	 * @return
	 */
	def deleteAll() {
		
		// Delete everything excepts things related to node(0)
		def query = """
			START n=node(*) 
			MATCH n-[r?]->m 
			WHERE id(n) <> 0 
			DELETE r, n 
		"""
		doCypherQuery(query)
		
		query = """
			START r=relationship(*) 
			DELETE r 
		"""
		doCypherQuery(query)
		
		// Delete the indexes
		def nodeIndexes = listNodeIndexes()
		def relationshipIndexes = listRelationshipIndexes()
		nodeIndexes.each {
			deleteNodeIndex(it)
		} 
		relationshipIndexes.each {
			deleteRelationshipIndex(it)
		}
	}

	/**
	 * Extract the node or relationship ID from its URI
	 * @param URI
	 * @return
	 */
	def Long extractIdFromURI(String URI) {
		Pattern pattern = Pattern.compile('(.*)/([0-9]+)/?')
		Matcher matcher = pattern.matcher(URI)
		matcher.matches()
		if(matcher.groupCount() > 0)
			return matcher.group(2).toLong()
		else
			throw new Exception("Failed to extract ID from URI")
	}

	

	
		
	/*
	 * Node
	 */
	
	/**
	 *
	 * @param id
	 * @return
	 */
	def GraphNode getNode(Long id) {
		def response = rest.get(path: "data/node/${id}")
		if(response.status != 200)
			throw new Exception("Node not found")
		return bindNode(response.data)
	}

	def String getNodeURI(GraphNode node) {
		if(node.id == null)
			throw new Exception("Impossible to generate the URI: the node has not been persisted")
		return baseUrl + "data/node/" + node.id
	}
	
	/**
	 *
	 * @param node
	 * @return
	 */
	def persistNode(GraphNode node) {
		if(node.id) // Update
		{
			def response = rest.put(path: "data/node/${node.id}/properties",
					body: node.data,
					requestContentType : URLENC )
			if(response.status != 204)
				throw new Exception("An error occured while updating a node")
		}
		else // Insert new
		{
			def response = rest.post(	path: 'data/node',
					body: node.data,
					requestContentType : URLENC )
			if(response.status != 201)
				throw new Exception("Creation of node failed")
			node.id = extractIdFromURI(response.data.self)
		}
	}
	
	/**
	 * 
	 * @param node
	 * @param key
	 * @param value
	 * @return
	 */
	def persistNodeProperty(GraphNode node, String key, String value) {
		def properties = [:]
		properties[key] = value
		persistNodeProperties(node, properties)
	}
	
	/**
	 * 
	 * @param node
	 * @param properties
	 * @return
	 */
	def persistNodeProperties(GraphNode node, Map properties) {
		if(!node.id)
			throw new GraphDbException("Cannot persist property of a non persisted node")
		def response = rest.put(path: "data/node/${node.id}/properties", 
				requestContentType: JSON, 
				body: properties)
		if(response.status != 204) 
			throw new GraphDbException("Failed to persist properties on node ${node.id}")
	}
	
	/**
	 * 
	 * @param node
	 * @param key
	 * @return
	 */
	def deleteNodeProperty(GraphNode node, String key) {
		if(!node.id)
			throw new GraphDbException("Cannot persist property of a non persisted node")
		def response = rest.delete(path: "data/node/${node.id}/properties/${key}")
		if(response.status != 204)
			throw new GraphDbException("Something went wrong during during the deletion on the property ${key} of the node ${node.id}")
	}
	
	/**
	 *
	 * @param node
	 * @return
	 */
	def deleteNode(GraphNode node) {
		if(node.id == null)
			throw new Exception("Impossible to delete a node which has not been persisted")
		def response = rest.delete(path: "data/node/${node.id}")
		if(response.status != 204) {
			if(response.status == 409) // 409: Conflicting
				throw new Exception("Deletion of the node failed because it has relationships with other nodes")
			else
				throw new Exception("Deletion of the node failed")
		}
		node.id = null
	}
	
	/**
	 *
	 * @return
	 */
	def countNodes() {
		def query = """
			START n=node(*) 
			RETURN count(n)
		"""
		def result = doCypherQuery(query)
		log.info(result)
		return result.data[0][0]
	}
	
	/**
	 *
	 * @param data
	 * @return
	 */
	def GraphNode bindNode(data) {
		def node = new GraphNode()
		node.id = extractIdFromURI(data.self)
		data.data.each {
			node.setProperty(it.key, it.value)
		}
		return node
	}
	
	
	
	
	
	/*
	 * Relationship
	 */
	
	/**
	 *
	 * @param id
	 * @return
	 */
	def GraphRelationship getRelationship(Long id) {
		def response = rest.get(path: "data/relationship/${id}")
		if(response.status != 200)
			throw new Exception("Node not found")
		def rel = bindRelationship(response.data)
	}
	
	/**
	 * Get a list of relationships between two nodes
	 * @param startNode
	 * @param endNode
	 * @param type Filter on relationships type
	 * @return 
	 */
	def List<GraphRelationship> getRelationshipsBetween(GraphNode startNode, GraphNode endNode, String type = null) {
		def query = """
		START 
			a = node({startNode}), 
			b = node({endNode}) 
		MATCH 
			a-[r]->b 
		RETURN r
		"""
		def result = doCypherQuery(query, [startNode:startNode.id, endNode: endNode.id])
		def relationships = []
		result.data.each {
			// it[0] => r
			if(type == null || type.equalsIgnoreCase(it.type)) {
				def rel = bindRelationship(it[0])
				relationships.add(rel)
			}
		}
		
		return relationships
	}
	
	/**
	 * Get a single relationship between two nodes
	 * @param startNode
	 * @param endNode
	 * @throws RelationshipNotFoundException
	 * @return
	 */
	def GraphRelationship getSingleRelationshipBetween(GraphNode startNode, GraphNode endNode, def type = null) 
	throws RelationshipNotFoundException {
		def relationships = getRelationshipsBetween(startNode, endNode, type)
		
		if(relationships.size() > 1) 
			throw new Exception("Only one relationship expected"); // TODO: Custom Exception type
		
		if(relationships.size() == 0) {
			throw new RelationshipNotFoundException("No relationship found"); 
		}
			
		return relationships[0]
	}
	
	/**
	 * Returns the fully qualified relationship URI base on a GraphRelationship object
	 * @param relationship
	 * @return
	 */
	def String getRelationshipURI(GraphRelationship relationship)
	{
		if(relationship.id == null)
			throw new Exception("Impossible to generate the URI: the relationship has not been persisted")
		return baseUrl + "data/relationship/" + relationship.id
	}

	
	/**
	 * Get all the relationships (incoming and outgoing) related to a GraphNode
	 * @param node
	 * @return
	 */
	def List<GraphRelationship> getAllRelationships(GraphNode node) {
		def response = rest.get(path: "data/node/${node.id}/relationships/all")
		if(response.status != 200)
			throw new Exception("Error while get node relationships")
		def rels = []
		response.data.each {
			def rel = bindRelationship(it)
			rels.add(rel)
		}
		return rels
	}
	
	/**
	 * Get all the incoming relationships of a node
	 * @param node
	 * @param type
	 * @return
	 */
	def List<GraphRelationship> getIncomingRelationships(GraphNode node, String type = null) {
		def response = rest.get(path: "data/node/${node.id}/relationships/in")
		if(response.status != 200)
			throw new Exception("Error while get node relationships")
		def rels = []
		response.data.each {
			def rel = bindRelationship(it)
			if(type == null || (type != null && rel.type == type))
				rels.add(rel)
		}
		return rels
	}
	
	/**
	 * Get all the outgoing relationships of node
	 * @param node
	 * @param type
	 * @return
	 */
	def List<GraphRelationship> getOutgoingRelationships(GraphNode node, String type = null) {
		def response = rest.get(path: "data/node/${node.id}/relationships/out")
				if(response.status != 200)
					throw new Exception("Error while get node relationships")
		def rels = []
				response.data.each {
					def rel = bindRelationship(it)
					if(type == null || (type != null && rel.type == type))
						rels.add(rel)
				}
		return rels
	}

	/**
	 * Persist a GraphRelationship object into the Neo4j data store. 
	 * @param relationship
	 * @return
	 */
	def persistRelationship(GraphRelationship relationship) {

		if(relationship.id) // Update
		{
			def response = rest.put(path: "data/relationship/${relationship.id}/properties",
					requestContentType : JSON,
					body: relationship.data )
			if(response.status != 204)
				throw new Exception("An error occured while updating a relationship")
		}
		else // Insert new
		{
			if(relationship.startNode == null)
				throw new Exception("Relationship cannot be persisted: the startNode property of the relationship is null")
			if(relationship.endNode == null)
				throw new Exception("Relationship cannot be persisted: the endNode property of the relationship is null")
			if(relationship.type == null)
				throw new Exception("Relationship cannot be persisted: the relationship type has not been set")
			if(relationship.startNode.id == null)
				throw new Exception("Relationship cannot be persisted: the startNode must be persisted first")
			if(relationship.endNode.id == null)
				throw new Exception("Relationship cannot be persisted: the endNode must be persisted first")

			def response = rest.post(	path: "data/node/"+ relationship.startNode.id +"/relationships",
					requestContentType : JSON,
					body: [ to: getNodeURI(relationship.endNode),
						type: relationship.type,
						data: relationship.data
					])
			if(response.status != 201)
				throw new Exception("Creation of relationship failed")
			relationship.id = extractIdFromURI(response.data.self)
			persistRelationship(relationship)
		}
	}
	
	/**
	 *
	 * @param relationship
	 * @param key
	 * @param value
	 * @return
	 */
	def persistRelationshipProperty(GraphRelationship relationship, String key, String value) {
		if(!relationship.id)
			throw new GraphDbException("Cannot persist property of a non persisted relationship")
		def response = rest.put(path: "data/relationship/${relationship.id}/properties/${key}",
			requestContentType: JSON,
			body: value)
		if(response.status != 204)
			throw new GraphDbException("Failed to persist property ${key} on relationship ${relationship.id}")
	}
	
	/**
	 *
	 * @param relationship
	 * @param properties
	 * @return
	 */
	def persistRelationshipProperties(GraphRelationship relationship, Map properties) {
		if(!relationship.id)
			throw new GraphDbException("Cannot persist property of a non persisted relationship")
		def response = rest.put(path: "data/relationship/${relationship.id}/properties",
				requestContentType: JSON,
				body: properties)
		if(response.status != 204)
			throw new GraphDbException("Failed to persist properties on relationship ${relationship.id}")
	}
	
	/**
	 *
	 * @param relationship
	 * @param key
	 * @return
	 */
	def deleteRelationshipProperty(GraphRelationship relationship, String key) {
		if(!relationship.id)
			throw new GraphDbException("Cannot persist property of a non persisted relationship")
		def response = rest.delete(path: "data/relationship/${relationship.id}/properties/${key}")
		if(response.status != 204)
			throw new GraphDbException("Something went wrong during during the deletion on the property ${key} of the relationship ${relationship.id}")
	}

	/**
	 * Delete a relationship
	 * @param relationship
	 * @return
	 */
	def deleteRelationship(GraphRelationship relationship) {
		if(relationship.id == null)
			throw new Exception("Impossible to delete a relationship which has not been persisted")
		def response = rest.delete(path: "data/relationship/${relationship.id}")
		if(response.status != 204)
			throw new Exception("Deletion of the relationship failed")
		relationship.id = null
	}

	/**
	 * Count the number of relationships in the Neo4j database
	 * @return
	 */
	def countRelationships() {
		def query = """
			START r=relationship(*) 
			RETURN count(r)
		"""
		def result = doCypherQuery(query)
		return result.data[0][0]
	}

	/**
	 * 
	 * @param data
	 * @return
	 */
	def GraphRelationship bindRelationship(data) {
		def startNode = getNode(extractIdFromURI(data.start))
		def endNode = getNode(extractIdFromURI(data.end))
		def rel = new GraphRelationship(startNode, endNode, data.type)
		rel.id = extractIdFromURI(data.self)
		
		data.data.each {
			rel.setProperty(it.key, it.value)
		}
		
		return rel
	}
	
	
	
	
	
	/*
	 * Node Index
	 */
	
	/**
	 * Create a node index
	 * @param name
	 * @return
	 */
	def createNodeIndex(String name) {
		def response = rest.post(path: "data/index/node/", 
				requestContentType: JSON,
				body: [
					name: name
				])
		if(response.status != 201)
			throw new Exception("Creation of the index '${name}' failed")
	}
	
	/**
	 * Create a node index the index does not already exists
	 * @param name
	 * @return
	 */
	def createNodeIndexIfNotExisting(String name) {
		def indexes = listNodeIndexes()
		if(!indexes.contains(name))
			createNodeIndex(name)
	}
	
	/**
	 * Delete a node index
	 * @param name
	 * @return
	 */
	def deleteNodeIndex(String name) {
		def response = rest.delete(path: "data/index/node/" + name)
		if(response.status != 204)
			throw new Exception("Something went wrong during the deletion of the index '${name}'")
	}
	
	/**
	 * Get the list of node indexes in the database
	 * @return
	 */
	def List<String> listNodeIndexes() {
		def response = rest.get(path: "data/index/node/")
		if(![200,204].contains(response.status))
			throw new Exception("Unable to retrieve the list if node indexes")
			
		def List<String> indexes = []
		response.data.each { indexName, index ->
			indexes.add(indexName)
		}
		return indexes
	}
	
	/**
	 * Associates a node with the given key/value pair in the given index
	 * @param node
	 * @param indexName
	 * @param key
	 * @param value
	 * @param overwritePreviousValue
	 * @return
	 */
	def addNodeToIndex(GraphNode node, String indexName, String key, String value, Boolean overwritePreviousValue = false) {
		if(overwritePreviousValue) 
			removeNodeFromIndex(node, indexName, key, value)
		
		def response = rest.post(path: "data/index/node/" + indexName, 
				requestContentType: JSON,
				body: [
					key: key, 
					value: value,
					uri: getNodeURI(node) 
				])
		if(response.status != 201)
			throw new Exception("Error while adding the node ${node.id} to the index '${indexName}'")
	}
	
	/**
	 * Remove all entries with a given node from an index
	 * @param node
	 * @param indexName
	 * @return
	 */
	def removeNodeFromIndex(GraphNode node, String indexName) {
		def response = rest.delete(path: "data/index/node/${indexName}/${node.id}")
		if(response.status != 204)
			throw new Exception("Error while removing the node ${node.id} from the index '${indexName}'")
	}
	
	/**
	 * Remove all entries with a given node and key from an index
	 * @param node
	 * @param indexName
	 * @param key
	 * @return
	 */
	def removeNodeFromIndex(GraphNode node, indexName, String key) {
		def response = rest.delete(path: "data/index/node/${indexName}/${key}/${node.id}")
		if(response.status != 204)
			throw new Exception("Error while removing the key '${key}' for the node ${node.id} from the index '${indexName}'")
	}
	
	/**
	 * Remove all entries with a given node, key and value from an index
	 * @param node
	 * @param indexName
	 * @param key
	 * @param value
	 * @return
	 */
	def removeNodeFromIndex(GraphNode node, indexName, String key, value) {
		def response = rest.delete(path: "data/index/node/${indexName}/${key}/${value}/${node.id}")
		if(response.status != 204)
			throw new Exception("Error while removing the key/value pair '${key}/${value}' for the node ${node.id} from the index '${indexName}'")
	}
	
	/**
	 * Find node by exact match
	 * @param key
	 * @param value
	 * @param indexName
	 * @return
	 */
	def List<GraphNode> findNode(String key, value, String indexName) {
		def response = rest.get(path: "data/index/node/${indexName}/${key}/${value}")
		if(response.status != 200)
			throw new Exception("Unable to retrive nodes from index '${indexName}' with key '${key}' and value '${value}'")
		
		def List<GraphNode> nodes = []
		response.data.each {
			def GraphNode node = bindNode(it)
			nodes.add(node)
		}
		return nodes
	}
	
	/**
	 * Find node by Lucene query
	 * @param query
	 * @param indexName
	 * @return
	 */
	def findNodeByLuceneQuery(String query, String indexName) {
		def response = rest.get(path: "data/index/node/${indexName}", 
				query: [
					query: query
				])
		if(![200, 404].contains(response.status))
			throw new Exception("Lucene query failed on node index '${indexName}'")
			
		def List<GraphNode> nodes = []
		response.data.each {
			def GraphNode node = bindNode(it)
			nodes.add(node)
		}
		return nodes
	}
	
	
	
	
	
	/*
	 * Relationship Index
	 */
	
	/**
	 * Create a relationship index
	 * @param name
	 * @return
	 */
	def createRelationshipIndex(String name) {
		def response = rest.post(path: "data/index/relationship/",
			requestContentType: JSON,
			body: [
				name: name
			])
		if(response.status != 201)
			throw new Exception("Creation of the index '${name}' failed")
	}
	
	/**
	 * Create a relationship index the index does not already exists
	 * @param name
	 * @return
	 */
	def createRelationshipIndexIfNotExisting(String name) {
		def indexes = listRelationshipIndexes()
		if(!indexes.contains(name))
			createRelationshipIndex(name)
	}
	
	/**
	 * Delete a relationship index
	 * @param name
	 * @return
	 */
	def deleteRelationshipIndex(String name) {
		def response = rest.delete(path: "data/index/relationship/" + name)
		if(response.status != 204)
			throw new Exception("Something went wrong during the deletion of the index '${name}'")
	}
	
	/**
	 * Get the list of relationship indexes in the database
	 * @return
	 */
	def List<String> listRelationshipIndexes() {
		def response = rest.get(path: "data/index/relationship/")
		if(![200,204].contains(response.status))
			throw new Exception("Unable to retrieve the list if relationship indexes")
			
		def List<String> indexes = []
		response.data.each { indexName, index ->
			indexes.add(indexName)
		}
		return indexes
	}
	
	/**
	 * Associates a relationship with the given key/value pair in the given index
	 * @param node
	 * @param indexName
	 * @param key
	 * @param value
	 * @param overwritePreviousValue
	 * @return
	 */
	def addRelationshipToIndex(GraphRelationship rel, String indexName, String key, String value, Boolean overwritePreviousValue = false) {
		if(overwritePreviousValue)
			removeRelationshipFromIndex(rel, indexName, key, value)
		
		def response = rest.post(path: "data/index/relationship/" + indexName,
			requestContentType: JSON,
			body: [
				key: key,
				value: value,
				uri: getRelationshipURI(rel)
			])
		if(response.status != 201)
			throw new Exception("Error while adding the relationship ${rel.id} to the index '${indexName}'")
	}
	
	/**
	 * Remove all entries with a given relationship from an index
	 * @param rel
	 * @param indexName
	 * @return
	 */
	def removeRelationshipFromIndex(GraphRelationship rel, String indexName) {
		def response = rest.delete(path: "data/index/relationship/${indexName}/${rel.id}")
		if(response.status != 204)
			throw new Exception("Error while removing the node ${rel.id} from the index '${indexName}'")
	}
	
	/**
	 * Remove all entries with a given relationship and key from an index
	 * @param rel
	 * @param indexName
	 * @param key
	 * @return
	 */
	def removeRelationshipFromIndex(GraphRelationship rel, String indexName, String key) {
		def response = rest.delete(path: "data/index/relationship/${indexName}/${key}/${rel.id}")
		if(response.status != 204)
			throw new Exception("Error while removing the key '${key}' for the relationship ${rel.id} from the index '${indexName}'")
	}
	
	
	/**
	 * Remove all entries with a given relationship, key and value from an index
	 * @param rel
	 * @param indexName
	 * @param key
	 * @param value
	 * @return
	 */
	def removeRelationshipFromIndex(GraphRelationship rel, String indexName, String key, String value) {
		def response = rest.delete(path: "data/index/relationship/${indexName}/${key}/${value}/${rel.id}")
		if(response.status != 204)
			throw new Exception("Error while removing the key/value pair '${key}/${value}' for the relationship ${rel.id} from the index '${indexName}'")
	}
	
	/**
	 * Find relationship by exact match
	 * @param key
	 * @param value
	 * @param indexName
	 * @return
	 */
	def List<GraphRelationship> findRelationship(String key, String value, String indexName) {
		def response = rest.get(path: "data/index/relationship/${indexName}/${key}/${value}")
		if(response.status != 200)
			throw new Exception("Unable to retrive relationship from index '${indexName}' with key '${key}' and value '${value}'")
		
		def List<GraphRelationship> relationships = []
		response.data.each {
			def relationship = bindRelationship(it)
			relationships.add(relationship)
		}
		return relationships
	}
	
	/**
	 * Find node by Lucene query
	 * @param query
	 * @param indexName
	 * @return
	 */
	def List<GraphRelationship> findRelationshipByLuceneQuery(String query, String indexName) {
		def response = rest.get(path: "data/index/relationship/${indexName}",
			query: [
				query: query
			])
		if(![200, 404].contains(response.status))
			throw new Exception("Lucene query failed on relationship index '${indexName}'")
			
		def List<GraphRelationship> relationships = []
		response.data.each {
			def relationship = bindRelationship(it)
			relationships.add(relationship)
		}
		return relationships
	}
	
	
	
	
}
