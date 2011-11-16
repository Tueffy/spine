package spine

import grails.test.*

class SpineServiceTests extends GrailsUnitTestCase {

	def s = new SpineService()
	def u1 = new User()
	def u2 = new User()
	def u3 = new User()
	def u4 = new User()
	
    protected void setUp() {
	
		//Define Users, which can be used during the testing
		u1.email = 'markus.long@techbank.com'
		u2.email = 'jure.zakotnik@techbank.com'
		u3.email = 'christian.tueffers@techbank.com'
		u4.email = 'fero.bacak@techbank.com'
		
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

	// tests for loginUser
	
	void testLoginUser1() {
		
		def success = s.loginUser('christian.tueffers@techbank.com', 'manage')
		assert success.lastName == 'Tueffers'
		assert success.firstName == 'Christian'
		assert success.country == 'Germany'
		assert success.city == 'Frankfurt'
		assert success.email == 'christian.tueffers@techbank.com'
		assert success.imagePath == 'christian.tueffers@techbank.com.jpg'		
	}
	
	void testLoginUser2() {
		
		def failure = s.loginUser('christian.tueffers@techbank.com', 'password')
		assert failure == null
	}

	void testLoginUser3() {
		
		def notexist = s.loginUser('christian.tueffers@techbank.com', 'password')
		assert notexist == null
	}

	void testLoginUser4() {
		
		def success = s.loginUser('markus.long@techbank.com', 'clojure')
		assert success.lastName == 'Long'
		assert success.firstName == 'Markus'
		assert success.country == 'Germany'
		assert success.city == 'Hamburg'
		assert success.email == 'markus.long@techbank.com'
		assert success.imagePath == 'markus.long@techbank.com.jpg'
		assert success.tags.size() == 5
	}
	
	// tests for getUserNetwork
	
	void testGetUserNetwork1() {
		
		def result = s.getUserNetwork(u1, '', 0)
		assert result.size() == 12
	}

	void testGetUserNetwork2() {
		
		def result = s.getUserNetwork(u2, '', 0)
		result.each { println it}
		assert result.size() == 12		
	}


	// tests for getUser
	
	void testGetUser1() {
		
		def result = s.getUser('markus.long@techbank.com')
		println result
		assert result != null
		
	}
	
	
	// tests for getUserTags
	
	void testGetUserTags1() {
		
		def output = s.getUserTags(u1)
		assert output == ['ITIL':3, 'Help':1, 'Operations':3, 'Desk':1, 'IT':2]

	}

	void testGetUserTags2() {
		
		def output = s.getUserTags(u2)
		assert output == ['SQL':1, 'HTML':1, 'Cloud':1, 'BPM':1, 'Development':1, 'zCloud':1, 'zJava':1, 'Help':1, 'ITIL':1, 'Desk':1, 'Warhammer':1, 'Wine':1, 'Soccer':1, 'SSL':2, 'RPG':2, 'Munich':2, 'Jax':2, '2011':2, 'Operations':3, 'Spring':4, 'Bielefeld':5, 'Java':8, 'IT':9, 'Agile':11]
	}

	void testGetUserTags3() {
		
		def output = s.getUserTags(u3)
		assert output == [:]
	}

	void testGetUserTags4() {
		
		def output = s.getUserTags(u4)
		assert output == [IT:1, SQL:1, Development:2, HTML:2, Spring:6, Java:8]
	}

	
/*	void testAddTag(){
		
		def test = s.addTag(u1,'ingmar.mueller@techbank.com','zCloud zJava')
		assert test == true
	}
*/
	
}
