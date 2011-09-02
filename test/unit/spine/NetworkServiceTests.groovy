package spine

import grails.test.*

class NetworkServiceTests extends GrailsUnitTestCase {
    protected void setUp() {
        super.setUp()
		
		
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testFindNodeByEmail() {
		def n = new NetworkService()
		def node = n.findNodeByEmail('jure@zakotnik.de')
		assert node
    }
}
