package spine;

import grails.test.GrailsUnitTestCase

class UserTests extends GrailsUnitTestCase {
	
	def NetworkService networkService
	def ImportDataService importDataService
	def checkDbResults
	
	protected void setUp() {
		super.setUp()
		checkDbResults = importDataService.checkDB()
	}

	/**
	 * Checks the status of the test database. This checks: number of nodes, relationships, properties, indices
	 */
	protected void tearDown() {
		super.tearDown()
		assert checkDbResults == importDataService.checkDB()
	}

	void testSortTags() {
		def User user = new User()
		user.tags = [Agile: 1, IT:5, Java: 3, Spring:1]
		def directTags = ['Java']
		
		user.sortTags(directTags)
		
		// def expected = ['Java', 'IT', 'Agile', 'Spring'] // for tags order by value reversed
		def expected = ['Java', 'Agile', 'IT', 'Spring'] // for tags ordered by key
		println user.tags
		println expected
		def i = 0
		user.tags.each {
			assert expected[i] == it.key
			i++
		}
	}
	
	void testSortTags2() {
		def User user = new User()
		user.tags = [HTML: 1, Java:1, PM:4, BI: 9, Leadership:1, Architecture: 1, DataWarehouse: 2, BigData: 1, Information: 2]
		def directTags = ['HTML', 'Java']
		
		user.sortTags(directTags)
		
		// def expected = ['HTML', 'Java', 'BI', 'PM', 'DataWarehouse', 'Information', 'Leadership', 'Architecture', 'BigData'] // for tags order by value reversed
		def expected = ['HTML', 'Java', 'Architecture', 'BI', 'PM', 'BigData', 'DataWarehouse', 'Information', 'Leadership'] // for tags ordered by key
		println user.tags.keySet()
		println expected
		def i = 0
		user.tags.each {
			assert expected[i] == it.key
			i++
		}
	}
	
	void testCreateAndDeleteUserNode() {
		// Create user
		def User user = new User()
		user.email = 'test@test.com'
		user.persist(networkService.graphCommunicatorService)
		assert user.self != null
		
		def readedNode = networkService.readNode(user.email)
		assert readedNode.email == user.email
		
		// Delete user
		user.delete(networkService.graphCommunicatorService)
		assert user.self == null
		
		readedNode = networkService.readNode(user.email)
		assert !readedNode
	}
	
}
