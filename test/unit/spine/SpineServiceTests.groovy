package spine

import grails.test.*

class SpineServiceTests extends GrailsUnitTestCase {

	def s = new SpineService()
	
    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

	void testLoginUser1() {
		def success = s.loginUser('christian.tueffers@techbank.com', 'manage')
		assert success.lastname == 'Tueffers'
		assert success.firstname == 'Christian'
		assert success.country == 'Germany'
		assert success.city == 'Frankfurt'
		assert success.email == 'christian.tueffers@techbank.com'
		assert success.imagepath == 'christian.tueffers@techbank.com.jpg'
	}
	
	void testLoginUser2() {
		
		def failure = s.loginUser('christian.tueffers@techbank.com', 'password')
		assert failure == null
		
	}

	void testLoginUser3() {
		
		def notexist = s.loginUser('christian.tueffers@techbank.com', 'password')
		assert notexist == null
	}
	
	void testAddTag(){
		
		def test = s.addTag('jure.zakotnik@techbank.com','ingmar.mueller@techbank.com','zCloud zJava')
		assert test == null
	}
	
	void testGetUserNetwork1() {
		def user = new User()
		user.email = 'christian.tueffers@techbank.com'
		def result = s.getUserNetwork(user, null, 0, null)
		
		assert result != null
	}

}
