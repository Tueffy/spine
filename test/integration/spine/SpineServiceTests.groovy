package spine

import groovyx.net.http.RESTClient
import static org.junit.Assert.*
import org.junit.*

import spine.exception.graphdb.RelationshipNotFoundException;

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
class SpineServiceTests {

	def grailsApplication
	def SpineService spineService
	def TestDataImporterService testDataImporterService
	def test = false
	
	void setUp() {
		spineService.neo4jService.baseUrl = grailsApplication.config.neo4j.test.baseUrl
		spineService.neo4jService.rest = new RESTClient(spineService.neo4jService.baseUrl)
		testDataImporterService.spineService.neo4jService.baseUrl = grailsApplication.config.neo4j.test.baseUrl
		testDataImporterService.spineService.neo4jService.rest = new RESTClient(spineService.neo4jService.baseUrl)
		resetDB()
	}

	void tearDown() {
		
	}
	
	/**
	 * Import data used for these tests
	 */
	void importTestData() {
		spineService.neo4jService.deleteAll()
		def users = testDataImporterService.importAndPersistsUsers("test/data/users.csv")
		testDataImporterService.importAndPeristRelationships("test/data/relationships.csv", users)
	}
	
	/**
	 * Create a new user with dummy information
	 */
	def GraphNode createNewDummyUser() {
		// Create and persist an user into database
		def GraphNode user = new GraphNode()
		user.firstname = "Andrew"
		user.lastname = "Tesla"
		user.email = "test@test.com"
		user.salt = "qwerty"
		user.password = spineService.hashPassword("password", user.salt)
		user.city = "Frankfurt"
		user.country = "Germany"
		user.birthdate = "1980-01-01"
		spineService.addUser(user)
		return user
	}
	
	/**
	 * Reset the database and reimport test data to have it clean for the tests
	 */
	void resetDB() {
		spineService.neo4jService.deleteAll() // Delete everything
		spineService.neo4jService.createNodeIndex(spineService.userIndex) // Create the user index
		importTestData() // Import test data
	}
	
	/**
	 * 
	 */
	void testImportData() {
		importTestData()
		spineService.neo4jService.countNodes() == 5
		spineService.neo4jService.countRelationships() == 3
		
		def GraphNode user1 = spineService.getUserByEmail("christian.tueffers@techbank.com")
		assert user1 != null
		assert user1.firstname == "Christian"
		assert user1.city == "Frankfurt"
		assert user1.country == "Germany"
		
		def GraphNode user2 = spineService.getUserByEmail("paul-julien.vauthier@techbank.com")
		assert user2 != null
		assert user2.firstname == "Paul-Julien"
		assert user2.city == "Lille"
		assert user2.country == "France"
		
		try {
			def GraphRelationship relationship = spineService.getDirectConnectionBetweenUsers(user1, user2)
			assert relationship != null
			assert relationship.data.containsKey(spineService.normalizeTag("Java"))
		}
		catch (RelationshipNotFoundException e) {
			fail("Relation between ${user1.email} and ${user2.email} not found. ")
		}
	}
	
	void testHashPassword() {
		def password = "password"
		def salt = "qwerty"
		def expected = "e2ccc167664d622742b4685e645e8144f09ac7ce24d0f5af8de14c801c68abc94ff20501d958225647a429a0efd302e53bf73256268a27516f70aa5b0471c2b2"
		assert expected == spineService.hashPassword(password, salt)
	}
	
	void testGenerateSalt() {
		String salt = spineService.generateSalt()
		assert salt.size() == 6
	}
	
	void testAddUser() {
		// Create new user
		def GraphNode user = createNewDummyUser() 
		
		// Retrieve the user from DB by ID
		def foundUser = spineService.getUserById(user.id)
		assert foundUser.id == user.id
		assert foundUser.email == "test@test.com"
		
		// Retrieve the user from DB by index
		foundUser = spineService.getUserByEmail("test@test.com")
		assert foundUser != null
		assert foundUser.id == user.id
		assert foundUser.email == user.email
		assert foundUser.email == "test@test.com"
		test = true
	}
	
	void testAuthenticateUser() {
		def GraphNode user = createNewDummyUser()
		
		def GraphNode authenticatedUser = spineService.authenticateUser("test@test.com", "password")
		assert authenticatedUser != null
		assert authenticatedUser.email == "test@test.com"
	}
	
	void testUpdateUser() {
		def GraphNode user = createNewDummyUser()
		
		user.city = "Munich"
		spineService.updateUser(user)
		
		def GraphNode updatedUser = spineService.getUserByEmail("test@test.com")
		assert updatedUser != null
		assert updatedUser.id == user.id
		assert updatedUser.city == "Munich"
	}
	
	void testRefreshUser() {
		def GraphNode user = createNewDummyUser()
		def GraphNode toBeRefreshedUser = new GraphNode()
		toBeRefreshedUser.id = user.id
		spineService.refreshUser(toBeRefreshedUser)
		assert toBeRefreshedUser.email == user.email
		assert toBeRefreshedUser.city == user.city
	}
	
	void testNormalizeTag() {
		assert spineService.normalizeTag("Java") == "Java" 
		assert spineService.normalizeTag("Java", true) == "java" 
		
		assert spineService.normalizeTag("Java Expert") == "Java_Expert"
		assert spineService.normalizeTag("Java Expert", true) == "java_expert"
		
		assert spineService.normalizeTag("java expert") == "java_expert"
		assert spineService.normalizeTag("java expert", true) == "java_expert"
	}
	
	/**
	 * Test adding a tag, looking for it via incoming relationships, then via index
	 * Test deleting a tag, looking for it via incoming relationships, then via index
	 */
	void testTagging() {
		def GraphNode cTueffers = spineService.getUserByEmail("christian.tueffers@techbank.com")
		assert cTueffers.id != null
		def GraphNode pjVauthier = spineService.getUserByEmail("paul-julien.vauthier@techbank.com")
		assert pjVauthier.id != null
		
		// Test adding a tag
		spineService.tagUser(cTueffers, pjVauthier, "Groovy")
		def pjVauthierTags = spineService.summarizeUserTags(pjVauthier)
		assert pjVauthierTags.size() == 2
		assert pjVauthierTags["Groovy"] == 1
		assert pjVauthierTags["Java"] == 1
		
		// Tests tag from the index
		def List<GraphNode> usersWithJavaTag = spineService.searchTagInUserIndex("Java")
		def GraphNode foundUser = usersWithJavaTag.find { it.id == pjVauthier.id }
		assert foundUser != null
		assert foundUser.id == pjVauthier.id
		
		def List<GraphNode> usersWithGroovyTag = spineService.searchTagInUserIndex("Groovy")
		foundUser = usersWithGroovyTag.find { it.id == pjVauthier.id }
		assert foundUser != null
		assert foundUser.id == pjVauthier.id
		
		// Test removing a tag
		spineService.untagUser(cTueffers, pjVauthier, "Groovy")
		pjVauthierTags = spineService.summarizeUserTags(pjVauthier)
		assert pjVauthierTags.size() == 1
		assert pjVauthierTags["Java"] == 1
		
		// Tests tag from the index
		usersWithJavaTag = spineService.searchTagInUserIndex("Java")
		foundUser = usersWithJavaTag.find { it.id == pjVauthier.id }
		assert foundUser != null
		assert foundUser.id == pjVauthier.id
		
		usersWithGroovyTag = spineService.searchTagInUserIndex("Groovy")
		foundUser = usersWithGroovyTag.find { it.id == pjVauthier.id }
		assert foundUser == null
	}
	
	void testUntagging() {
		def GraphNode cTueffers = spineService.getUserByEmail("christian.tueffers@techbank.com")
		assert cTueffers.id != null
		def GraphNode pjVauthier = spineService.getUserByEmail("paul-julien.vauthier@techbank.com")
		assert pjVauthier.id != null
		def GraphNode jZakotnik = spineService.getUserByEmail("jure.zakotnik@techbank.com")
		assert jZakotnik.id != null
		
		spineService.tagUser(cTueffers, jZakotnik, "Agile")
		spineService.tagUser(pjVauthier, jZakotnik, "Agile")
		
		spineService.untagUser(cTueffers, jZakotnik, "Agile")
		
		// We should still find jZakotnik tagged as Agile
		def List<GraphNode> usersWithAgileTag = spineService.searchTagInUserIndex("Agile")
		def GraphNode foundUser = usersWithAgileTag.find { it.id == jZakotnik.id }
		assert foundUser != null
		assert foundUser.id == jZakotnik.id
	}
	
	void testSummarizeUserTags() {
		def GraphNode cTueffers = spineService.getUserByEmail("christian.tueffers@techbank.com")
		def GraphNode pjVauthier = spineService.getUserByEmail("paul-julien.vauthier@techbank.com")
		def GraphNode jZakotnik = spineService.getUserByEmail("jure.zakotnik@techbank.com")
		def GraphNode iMuller = spineService.getUserByEmail("ingmar.muller@techbank.com")
		
		assert cTueffers.id != null
		assert pjVauthier.id != null
		assert jZakotnik.id != null
		assert iMuller.id != null
		
		def Map<String, Long> tagsToCTueffers = spineService.summarizeUserTags(cTueffers)
		def Map<String, Long> tagsToPjVauthier = spineService.summarizeUserTags(pjVauthier)
		def Map<String, Long> tagsToJZakotnik = spineService.summarizeUserTags(jZakotnik)
		def Map<String, Long> tagsToIMuller = spineService.summarizeUserTags(iMuller)
		
		assert tagsToCTueffers.size() == 0
		assert tagsToPjVauthier.size() == 1
		assert tagsToJZakotnik.size() == 1
		assert tagsToIMuller.size() == 0
		
		assert tagsToPjVauthier["Java"] == 1
		assert tagsToJZakotnik["ECB"] == 2
	}
	
	void testGetDirectConnectionBetweenUsers() {
		def GraphNode cTueffers = spineService.getUserByEmail("christian.tueffers@techbank.com")
		def GraphNode pjVauthier = spineService.getUserByEmail("paul-julien.vauthier@techbank.com")
		
		def GraphRelationship relationship = spineService.getDirectConnectionBetweenUsers(cTueffers, pjVauthier)
		assert relationship.id != null
		assert relationship.startNode.id == cTueffers.id
		assert relationship.endNode.id == pjVauthier.id
		assert relationship.data["Java"] == 1
	}

}
