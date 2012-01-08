package spine

import grails.test.GrailsUnitTestCase

class SpineServiceTests extends GrailsUnitTestCase {

    def SpineService spineService
    def u1 = new User()
    def u2 = new User()
    def u3 = new User()
    def u4 = new User()
	
	def ImportDataService importDataService
	def checkDbResults

    protected void setUp() {
        super.setUp()

        //Define Users, which can be used during the testing
        u1.email = 'markus.long@techbank.com'
        u2.email = 'jure.zakotnik@techbank.com'
        u3.email = 'christian.tueffers@techbank.com'
        u4.email = 'fero.bacak@techbank.com'

		checkDbResults = importDataService.checkDB()
    }

    /**
	* Checks the status of the test database. This checks: number of nodes, relationships, properties, indices
	*/
    protected void tearDown() {
        super.tearDown()
		assert checkDbResults == importDataService.checkDB()
    }

    // tests for loginUser

    void testLoginUser1() {

        def success = spineService.loginUser('christian.tueffers@techbank.com', 'manage')
        assert success.lastName == 'Tueffers'
        assert success.firstName == 'Christian'
        assert success.country == 'Germany'
        assert success.city == 'Frankfurt'
        assert success.email == 'christian.tueffers@techbank.com'
        assert success.imagePath == 'christian.tueffers@techbank.com.jpg'
        assert success.badges.size() == 0
    }

    void testLoginUser2() {

        def failure = spineService.loginUser('christian.tueffers@techbank.com', 'password')
        assert failure == null
    }

    void testLoginUser3() {

        def notexist = spineService.loginUser('christian.tueffers@techbank.com', 'password')
        assert notexist == null
    }

    void testLoginUser4() {

        def success = spineService.loginUser('markus.long@techbank.com', 'clojure')
        assert success.lastName == 'Long'
        assert success.firstName == 'Markus'
        assert success.country == 'Germany'
        assert success.city == 'Hamburg'
        assert success.email == 'markus.long@techbank.com'
        assert success.imagePath == 'markus.long@techbank.com.jpg'
        assert success.tags.size() == 7
        assert success.badges.size() == 2
    }

    // tests for getUserNetwork

    void testGetUserNetwork1() {

        def result = spineService.getUserNetwork(u1, '', 0, 10)
        assert result.size() == 10
    }

    void testGetUserNetwork2() {

        def result = spineService.getUserNetwork(u2, '', 0, 10)
        assert result.size() == 10
    }

    // tests for getUser

    void testGetUser1() {

        def result = spineService.getUser('markus.long@techbank.com')
        assert result != null

    }

    // tests for getUserTags

    void testGetUserTags1() {

        def output = spineService.getUserTags(u1)
        assert output == ['Help':3, 'ITIL':5, 'Operations':6, 'Desk':3, 'IT':4, 'Java':2, 'SOA':1]

    }

    void testGetUserTags2() {
        def output = spineService.getUserTags(u2)
        assert output ==  ['Agile':15, 'IT':11, 'Java':12, 'SOA':1, 'Spring':4, 'Cloud':1, 'BPM':1, 'RPG':2, 'Operations':2, 'Bielefeld':5, 'Development':1, 'Warhammer':1, 'SSL':1, 'Munich':2, 'Jax':2, '2011':2, 'Wine':1, 'Soccer':1]
    }

    void testGetUserTags3() {
        def output = spineService.getUserTags(u3)
        assert output == ['Love':1, 'ProjectX':1]
    }

    void testGetUserTags4() {

        def output = spineService.getUserTags(u4)
        assert output == [Spring: 7, Java: 9, Development: 2, IT: 1, HTML: 3, SQL: 2, SSL: 1, zCloud: 1, zJava: 1]
    }
	
	void testRemoveTag()
	{
		spineService.addTag(u1, u4, 'Office')
		def userTags = spineService.getUserTags(u4)
		assert userTags['Office'] == 1
		spineService.removeTag(u1, u4, 'Office')
		userTags = spineService.getUserTags(u4)
		assert (userTags['Office'] == null)
	}

	void testAddTag() {
        assert true // If testRemoveTag is ok, testAddTag must be ok
    }
	
	void testGetBadges()
	{
		// Calling with a Map of tags
		def userTags = spineService.getUserTags(u1)
		def userBadges = spineService.getBadges(userTags)
		assert userBadges.size() == 2
		assert userBadges[0].name == "ITIL Champ"
		assert userBadges[1].name == "The Operator"
		
		// Calling with a user object
		userBadges = spineService.getBadges(u1)
		assert userBadges.size() == 2
		assert userBadges[0].name == "ITIL Champ"
		assert userBadges[1].name == "The Operator"
	}
	
	void testAutocompleteTags()
	{
		def results = spineService.autocompleteTags('Clou')
		def expectedRestults = ['Cloud' : 6, 'zCloud' : 1]
		assert results.sort() == expectedRestults.sort()
	}
	
	void testActivateUser()
	{
		def user = spineService.getUser(u1.email)
		spineService.updateUserProfile(user, ['status' : 'inactive'])
		spineService.activateUser(user)
	} 
	
	void testGetHotTags()
	{
		def hotTags = spineService.getHotTags()
		def tagsToTest = ['IT', 'Java', 'Agile', 'Operations', 'Front', 'Office', 'ProjectX', 'Trading', 'Spring', 'Soccer']
		assert hotTags == tagsToTest
	}

}
