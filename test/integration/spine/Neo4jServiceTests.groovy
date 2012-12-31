package spine

import groovyx.net.http.RESTClient
import static org.junit.Assert.*
import org.junit.*

class Neo4jServiceTests {

	def Neo4jService neo4jService

	@Before
	void setUp() {
		neo4jService.baseUrl = 'http://localhost:8484/db/'
		neo4jService.rest = new RESTClient(neo4jService.baseUrl)
		neo4jService.deleteAll()
	}

	@After
	void tearDown() {
		// Tear down logic here
	}
	
	/*
	 * General
	 */
	
	@Test
	void testCypherQuery() {
		def query = "START n=node(0) RETURN n"
		def result = neo4jService.doCypherQuery(query)
		def rootNode = neo4jService.bindNode(result.data[0][0])
		assert rootNode.id == 0
	}
	
	@Test 
	void testCypherQueryWithParameters() {
		def query = "START n=node({id}) RETURN n"
		def params = [id:0]
		def result = neo4jService.doCypherQuery(query, params)
		def rootNode = neo4jService.bindNode(result.data[0][0])
		assert rootNode.id == 0
	}
	
	@Test
	void testDeleteAll() {
		neo4jService.deleteAll()
		assert neo4jService.countNodes() == 1 // There should remains the node 0
		assert neo4jService.countRelationships() == 0
		assert neo4jService.listNodeIndexes().size() == 0
		assert neo4jService.listRelationshipIndexes().size() == 0
		
		// The root node (node(0)) should not be deleted
		def rootNode = neo4jService.getNode(0)
		assert rootNode.id == 0
	}
	
	@Test 
	void testCountNodes() {
		neo4jService.deleteAll()
		assert neo4jService.countNodes() == 1 // The node 0 should remains
		neo4jService.persistNode(new GraphNode())
		assert neo4jService.countNodes() == 2
	}
	
	@Test 
	void testCountRelationships() {
		neo4jService.deleteAll()
		assert neo4jService.countRelationships() == 0
		
		// Create a relationship
		def node1 = new GraphNode()
		def node2 = new GraphNode()
		neo4jService.persistNode(node1)
		neo4jService.persistNode(node2)
		
		neo4jService.persistRelationship(new GraphRelationship(node1, node2, 'CONNECT'))
		assert neo4jService.countRelationships() == 1
	}

	/*
	 * Nodes
	 */

	@Test
	void testCreateNode() {
		def node = new GraphNode()
		neo4jService.persistNode(node)
		assert node.id != null
	}

	@Test
	void testCreateNodeWithProperties() {
		def node = new GraphNode()
		node.email = 'test@test.com'
		neo4jService.persistNode(node)

		def node2 = neo4jService.getNode(node.id)
		assert node2.email.equals(node.email)
	}
	
	@Test
	void testUpdateNode() {
		def node = new GraphNode()
		node.email = 'test@test.com'
		neo4jService.persistNode(node)
		node.email = 'pouet@pouet.com'
		neo4jService.persistNode(node)

		def node2 = neo4jService.getNode(node.id)
		assert node2.email.equals(node.email)
	}

	@Test
	void testGetNode() {
		testCreateNodeWithProperties() // It's the same principle
	}

	@Test
	void getNonExistentNode() {
		neo4jService.deleteAll()
		try {
			def node = neo4jService.getNode(123)
			assert false
		}
		catch (Exception e) {
			assert true
		}
	}
	
	@Test
	void testDeleteNode() {
		def nbNodes = neo4jService.countNodes()
		def node = new GraphNode()
		neo4jService.persistNode(node)
		neo4jService.deleteNode(node)
		assert node.id == null
		assert nbNodes == neo4jService.countNodes()
	}
	
	@Test
	void testDeleteNodeWithRelationships() {
		def node1 = new GraphNode()
		def node2 = new GraphNode()
		neo4jService.persistNode(node1)
		neo4jService.persistNode(node2)
		
		def rel = new GraphRelationship(node1, node2, 'CONNECT')
		neo4jService.persistRelationship(rel)
		
		try {
			neo4jService.deleteNode(node1)
			assert false
		}
		catch (Exception e) {
			assert true
		}
	}

	/*
	 * Node properties
	 */

	/*
	 * Relationships
	 */
	
	@Test
	void testCreateRelationship() {
		def node1 = new GraphNode()
		def node2 = new GraphNode()
		neo4jService.persistNode(node1)
		neo4jService.persistNode(node2)
		
		def rel = new GraphRelationship(node1, node2, 'CONNECT')
		neo4jService.persistRelationship(rel)
		assert rel.id != null
	}
	
	@Test
	void testCreateRelationshipWithProperties() {
		def node1 = new GraphNode()
		def node2 = new GraphNode()
		neo4jService.persistNode(node1)
		neo4jService.persistNode(node2)
		
		def rel = new GraphRelationship(node1, node2, 'CONNECT')
		rel.score = 0.5
		neo4jService.persistRelationship(rel)
		
		def rel2 = neo4jService.getRelationship(rel.id)
		assert rel2.score == rel.score
	}
	
	@Test
	void testUpdateRelationship() {
		def node1 = new GraphNode()
		def node2 = new GraphNode()
		neo4jService.persistNode(node1)
		neo4jService.persistNode(node2)
		
		def rel = new GraphRelationship(node1, node2, 'CONNECT')
		rel.score = 0.5
		neo4jService.persistRelationship(rel)
		rel.score = 0.7
		neo4jService.persistRelationship(rel)
		
		def rel2 = neo4jService.getRelationship(rel.id)
		assert rel2.score == rel.score
	}
	
	@Test
	void testGetAllRelationships() {
		def node1 = new GraphNode()
		def node2 = new GraphNode()
		neo4jService.persistNode(node1)
		neo4jService.persistNode(node2)
		
		def rel1 = new GraphRelationship(node1, node2, 'CONNECT')
		def rel2 = new GraphRelationship(node2, node1, 'CONNECT')
		neo4jService.persistRelationship(rel1)
		neo4jService.persistRelationship(rel2)
		
		def rels1 = neo4jService.getAllRelationships(node1)
		assert rels1.size() == 2
		def rels2 = neo4jService.getAllRelationships(node1)
		assert rels2.size() == 2
	} 
	
	@Test
	void testGetAllRelationshipsOnLonelyNode() {
		def node = new GraphNode()
		neo4jService.persistNode(node)
		
		def rels = neo4jService.getAllRelationships(node)
		assert rels.size() == 0
	}
	
	@Test
	void testGetIncomingRelationships() {
		def node1 = new GraphNode()
		def node2 = new GraphNode()
		neo4jService.persistNode(node1)
		neo4jService.persistNode(node2)
		
		def rel1 = new GraphRelationship(node1, node2, 'CONNECT')
		def rel2 = new GraphRelationship(node2, node1, 'CONNECT')
		neo4jService.persistRelationship(rel1)
		neo4jService.persistRelationship(rel2)
		
		def rels1 = neo4jService.getIncomingRelationships(node1)
		assert rels1.size() == 1
		def rels2 = neo4jService.getIncomingRelationships(node2)
		assert rels2.size() == 1
	}
	
	@Test
	void testGetOutgoingRelationship() {
		def node1 = new GraphNode()
		def node2 = new GraphNode()
		neo4jService.persistNode(node1)
		neo4jService.persistNode(node2)
		
		def rel1 = new GraphRelationship(node1, node2, 'CONNECT')
		def rel2 = new GraphRelationship(node2, node1, 'CONNECT')
		neo4jService.persistRelationship(rel1)
		neo4jService.persistRelationship(rel2)
		
		def rels1 = neo4jService.getOutgoingRelationships(node1)
		assert rels1.size() == 1
		def rels2 = neo4jService.getOutgoingRelationships(node2)
		assert rels2.size() == 1
	}
	
	@Test
	void testGetRelationship() {
		testCreateRelationshipWithProperties() // It's the same principle
	}
	
	void testGetRelationshipBetween() {
		// Create two nodes
		def startNode = new GraphNode()
		neo4jService.persistNode(startNode)
		def endNode = new GraphNode()
		neo4jService.persistNode(endNode)
		
		// Create a relationship between the two nodes
		def relationship = new GraphRelationship(startNode, endNode, 'CONNECT')
		neo4jService.persistRelationship(relationship)
		
		// Get the relationship between startNode and endNode
		def List<GraphRelationship> foundRelationships = neo4jService.getRelationshipsBetween(startNode, endNode)
		assert foundRelationships.size() == 1
		assert foundRelationships[0].id == relationship.id
		
		// Get the relationship between startNode and endNode (but add a filtering by type)
		foundRelationships = neo4jService.getRelationshipsBetween(startNode, endNode, 'CONNECT')
		assert foundRelationships.size() == 1
		assert foundRelationships[0].id == relationship.id
	}
	
	void testGetSingleRelationshipBetween() {
		// Create two nodes
		def startNode = new GraphNode()
		neo4jService.persistNode(startNode)
		def endNode = new GraphNode()
		neo4jService.persistNode(endNode)
		
		// Create a relationship between the two nodes
		def relationship = new GraphRelationship(startNode, endNode, 'CONNECT')
		neo4jService.persistRelationship(relationship)
		
		// Get the single relationship between the two nodes
		def foundRelationship = neo4jService.getSingleRelationshipBetween(startNode, endNode)
		assert foundRelationship.id == relationship.id
		
		// Add the relationship of another type between the two nodes
		def relationship2 = new GraphRelationship(startNode, endNode, 'TEST')
		neo4jService.persistRelationship(relationship2)
		
		// Get single relationship on type CONNECT
		foundRelationship = neo4jService.getSingleRelationshipBetween(startNode, endNode, 'CONNECT')
		assert foundRelationship.id == relationship.id
		
		// Get single relationship between the two nodes without type filtering
		try {
			foundRelationship = neo4jService.getSingleRelationshipsBetween(startNode, endNode)
			fail("An exception was expected")
		} catch (Exception e) {
			assert true
		}
	}
	
	@Test
	void testDeleteRelationship() {
		def nbRelationships = neo4jService.countRelationships()
		
		def node1 = new GraphNode()
		def node2 = new GraphNode()
		neo4jService.persistNode(node1)
		neo4jService.persistNode(node2)
		def rel = new GraphRelationship(node1, node2, 'CONNECT')
		neo4jService.persistRelationship(rel)
		
		neo4jService.deleteRelationship(rel)
		assert rel.id == null
		assert nbRelationships == neo4jService.countRelationships()
	}

	/*
	 * Relationship properties 
	 */

	/*
	 * Node Indexes
	 */
	
	void testListCreateAndDeleteNodeIndexes() {
		neo4jService.deleteAll()
		assert neo4jService.listNodeIndexes().size() == 0
		
		// Create an index
		def index = "test_index"
		neo4jService.createNodeIndex(index)
		assert neo4jService.listNodeIndexes().size() == 1
		
		// Delete the created index
		neo4jService.deleteNodeIndex(index)
		assert neo4jService.listNodeIndexes().size() == 0
	}
	
	void testAddAndRemoveNodeFromNodeIndex() {
		neo4jService.deleteAll()
		
		// Create an index
		def index = "test_index"
		neo4jService.createNodeIndex(index)
		
		// Create a node
		def node = new GraphNode()
		node.email = "test@test.com"
		neo4jService.persistNode(node)
		
		// Add the node the index
		neo4jService.addNodeToIndex(node, index, "email", node.email)
		
		// Get the node from the index
		def foundNodes = neo4jService.findNode("email", node.email, index)
		assert foundNodes.size() == 1
		assert foundNodes[0].id == node.id
		assert foundNodes[0].email == node.email
		
		// Get the node from the index (lucen query)
		foundNodes = neo4jService.findNodeByLuceneQuery("email:${node.email}", index)
		assert foundNodes.size() == 1
		assert foundNodes[0].id == node.id
		assert foundNodes[0].email == node.email
		
		// Remove the node from the index
		neo4jService.removeNodeFromIndex(node, index)
		foundNodes = neo4jService.findNode("email", node.email, index)
		assert foundNodes.size() == 0
	}
	
	void testRemoveNodeFromIndexWithKey () {
		neo4jService.deleteAll()
		
		// Create an index
		def index = "test_index"
		neo4jService.createNodeIndex(index)
		
		// Create a node
		def node = new GraphNode()
		node.email = "test@test.com"
		neo4jService.persistNode(node)
		
		// Add the node to the index
		neo4jService.addNodeToIndex(node, index, "email", node.email)
		
		// Find the node from the index
		def foundNodes = neo4jService.findNode("email", node.email, index)
		assert foundNodes.size() == 1
		
		// Remove the node from the index
		neo4jService.removeNodeFromIndex(node, index, "email")
		
		// Try to find the node from the index
		foundNodes = neo4jService.findNode("email", node.email, index)
		assert foundNodes.size() == 0
	}
	
	def testRemoveNodeFromIndexWithKeyValue() {
		neo4jService.deleteAll()
		
		// Create an index
		def index = "test_index"
		neo4jService.createNodeIndex(index)
		
		// Create a node
		def node = new GraphNode()
		node.email = "test@test.com"
		neo4jService.persistNode(node)
		
		// Add the node the index
		neo4jService.addNodeToIndex(node, index, "email", node.email)
		
		// Find the node from the index
		def foundNodes = neo4jService.findNode("email", node.email, index)
		assert foundNodes.size() == 1
		
		// Remove the node from the index
		neo4jService.removeNodeFromIndex(node, index, "email", node.email)
		
		// Try to find the node from the index
		foundNodes = neo4jService.findNode("email", node.email, index)
		assert foundNodes.size() == 0
	}
	
	/*
	 * Relationship indexes
	 */
	
	void testListCreateAndDeleteRelationshipIndexes() {
		neo4jService.deleteAll()
		assert neo4jService.listRelationshipIndexes().size() == 0
		
		// Create an index
		def index = "test_index"
		neo4jService.createRelationshipIndex(index)
		assert neo4jService.listRelationshipIndexes().size() == 1
		
		// Delete the created index
		neo4jService.deleteRelationshipIndex(index)
		assert neo4jService.listRelationshipIndexes().size() == 0
	}
	
	void testAddAndRemoveRelationshipFromNodeIndex() {
		neo4jService.deleteAll()
		
		// Create a relationship
		def GraphNode startNode = new GraphNode()
		neo4jService.persistNode(startNode)
		def GraphNode endNode = new GraphNode()
		neo4jService.persistNode(endNode)
		def GraphRelationship relationship = new GraphRelationship(startNode, endNode, 'CONNECT')
		relationship.score = 50
		neo4jService.persistRelationship(relationship)
		
		// Create an index
		def index = "test_index"
		neo4jService.createRelationshipIndex(index)
		
		// Add the relationship to the index
		neo4jService.addRelationshipToIndex(relationship, index, "score", "50")
		
		// Find the relationship from the index
		def foundRelationships = neo4jService.findRelationship("score", "50", index)
		assert foundRelationships.size() == 1
		
		// Remove the relationship from the index
		neo4jService.removeRelationshipFromIndex(relationship, index)
		foundRelationships = neo4jService.findRelationship("score", "50", index)
		assert foundRelationships.size() == 0
	}
	
	void testRemoveRelationshipFromIndexWithKey () {
		neo4jService.deleteAll()
		
		// Create a relationship
		def GraphNode startNode = new GraphNode()
		neo4jService.persistNode(startNode)
		def GraphNode endNode = new GraphNode()
		neo4jService.persistNode(endNode)
		def GraphRelationship relationship = new GraphRelationship(startNode, endNode, 'CONNECT')
		relationship.score = 50
		neo4jService.persistRelationship(relationship)
		
		// Create an index
		def index = "test_index"
		neo4jService.createRelationshipIndex(index)
		
		// Add the relationship to the index
		neo4jService.addRelationshipToIndex(relationship, index, "score", "50")
		
		// Find the relationship from the index
		def foundRelationships = neo4jService.findRelationship("score", "50", index)
		assert foundRelationships.size() == 1
		
		// Remove the relationship from the index
		neo4jService.removeRelationshipFromIndex(relationship, index, "score")
		foundRelationships = neo4jService.findRelationship("score", "50", index)
		assert foundRelationships.size() == 0
	}
	
	void testRemoveRelationshipFromIndexWithKeyValue () {
		neo4jService.deleteAll()
		
		// Create a relationship
		def GraphNode startNode = new GraphNode()
		neo4jService.persistNode(startNode)
		def GraphNode endNode = new GraphNode()
		neo4jService.persistNode(endNode)
		def GraphRelationship relationship = new GraphRelationship(startNode, endNode, 'CONNECT')
		relationship.score = 50
		neo4jService.persistRelationship(relationship)
		
		// Create an index
		def index = "test_index"
		neo4jService.createRelationshipIndex(index)
		
		// Add the relationship to the index
		neo4jService.addRelationshipToIndex(relationship, index, "score", "50")
		
		// Find the relationship from the index
		def foundRelationships = neo4jService.findRelationship("score", "50", index)
		assert foundRelationships.size() == 1
		
		// Remove the relationship from the index
		neo4jService.removeRelationshipFromIndex(relationship, index, "score", "50")
		foundRelationships = neo4jService.findRelationship("score", "50", index)
		assert foundRelationships.size() == 0
	}
	
}
