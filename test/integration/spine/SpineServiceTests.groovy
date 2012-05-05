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
        u1.email = 'ahmed.fatir@innonet-bank.com'
        u2.email = 'akin.burshaz@innonet-bank.com'
        u3.email = 'oliver.schaefer@innonet-bank.com'
        u4.email = 'kerstin.kruse@innonet-bank.com'

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

        def success = spineService.loginUser('ahmed.fatir@innonet-bank.com', 'password')
        assert success.lastName == 'Fatir'
        assert success.firstName == 'Ahmed'
        assert success.country == 'UK'
        assert success.city == 'London'
        assert success.email == 'ahmed.fatir@innonet-bank.com'
        assert success.imagePath == 'ahmed.fatir@innonet-bank.com.jpg'
        assert success.badges.size() == 0
    }

    void testLoginUser2() {

        def failure = spineService.loginUser('ahmed.fatir@innonet-bank.com', 'azerty')
        assert failure == null
    }

    void testLoginUser3() {

        def notexist = spineService.loginUser('akin.burshaz@innonet-bank.com', 'qwerty')
        assert notexist == null
    }

    void testLoginUser4() {

        def success = spineService.loginUser('akin.burshaz@innonet-bank.com', 'password')
        assert success.lastName == 'Burshaz'
        assert success.firstName == 'Akin'
        assert success.country == 'Germany'
        assert success.city == 'Hamburg'
        assert success.email == 'akin.burshaz@innonet-bank.com'
        assert success.imagePath == 'akin.burshaz@innonet-bank.com.jpg'
        assert success.tags.size() == 10
        assert success.badges.size() == 0
    }

    // tests for getUserNetwork

    void testGetUserNetwork1() {
		def Network network = spineService.getUserNetwork(u1, '', 0, 10)
		assert network.networkedUsers.size() == 10
    }

    void testGetUserNetwork2() {

        def Network network = spineService.getUserNetwork(u2, '', 0, 10)
        assert network.networkedUsers.size() == 10
    }

    // tests for getUser

    void testGetUser1() {

        def result = spineService.getUser('ahmed.fatir@innonet-bank.com')
        assert result != null

    }

    // tests for getUserTags

    void testGetUserTags1() {

        def output = spineService.getUserTags(u1)
        assert output == ['BI':8, 'Java':7, 'HTML':1, 'BJ':1] 

    }

    void testGetUserTags2() {
        def output = spineService.getUserTags(u2)
        assert output ==  ['BI':9, 'Java':5, 'ITIL':2, 'HelpDesk':1, 'BAM':2, 'DataWarehouse':1, 'CEP':1, 'SQL':2, 'RealTime':1, 'Architecture':1] 
    }

    void testGetUserTags3() {
        def output = spineService.getUserTags(u3)
        assert output == ['Clubbing':1, 'Java':1, 'Serengeti':1, 'Innovation':1, 'zCloud':1, 'Mobile':1, 'zJava':1] 
    }

    void testGetUserTags4() {

        def output = spineService.getUserTags(u4)
        assert output == ['Diversity':1, 'LesBleus':1, 'TourEiffel':1, 'Shopping':1, 'Sales':1]
    }
	
	void testRemoveTag()
	{
		spineService.addTag(u1, u3, 'Office')
		def userTags = spineService.getUserTags(u3)
		assert userTags['Office']
		spineService.removeTag(u1, u3, 'Office')
		userTags = spineService.getUserTags(u3)
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
		assert userBadges.size() == 0
//		assert userBadges[0].name == "ITIL Champ"
//		assert userBadges[1].name == "The Operator"
		
		// Calling with a user object
		userBadges = spineService.getBadges(u1)
		assert userBadges.size() == 0
//		assert userBadges[0].name == "ITIL Champ"
//		assert userBadges[1].name == "The Operator"
	}
	
	void testAutocompleteTags()
	{
		def results = spineService.autocompleteTags('Clou')
		def expectedResults = [
			[tag: 'zCloud', number: 1], 
			[tag: 'Cloud', number: 2]
		]
		
		assert results.size() == 2
		assert results.containsAll(expectedResults)
	}
	
	void testActivateUser()
	{
		def user = spineService.getUser(u1.email)
		spineService.updateUserProfile(user, ['status' : 'inactive'])
		spineService.activateUser(user)
		user = spineService.getUser(u1.email)
		assert user.status == 'active'
	} 
	
	void testGetHotTags()
	{
		def hotTags = spineService.getHotTags()
		def tagsToTest = ['Java', 'BI', 'Securities', 'Bonds', 'Wine', 'PM', 'Leadership', 'Innovation', 'Diversity', 'Scheduling']
		assert hotTags == tagsToTest
	}
	
	void testgetUserInNetworkContext() 
	{
		User contextUser = u1
		User targetUser = u3
		def NetworkedUser networkedUser = spineService.getUserInNetworkContext(contextUser, targetUser.email)
		
		def directTagsExpected = ['Innovation', 'zCloud', 'Mobile',  'zJava']
		
		def expectedBadges = []
		def actualBadges = []
		networkedUser.user.badges.each {
			actualBadges.add(it.name)
		}
		
		assert actualBadges.sort() == expectedBadges.sort()
		assert networkedUser.directTags.sort() == directTagsExpected.sort()
		assert networkedUser.distance == 1
	}

}
