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
	
	void testGetPropertiesByEmail() {
		def n = new NetworkService()
		def props = n.getPropertiesByEmail('jure@zakotnik.de')
		assert props
	}

	void testDeleteAllEdges() {
		def n = new NetworkService()
		n.connectPeople("testuser1","testuser2","test123;test456")
		def props = n.deleteAllEdges(['testuser1','testuser2'])
		assert props
	}
	
	void testGetPropsForEdge() {
		def n = new NetworkService()
		def props = n.getPropsForEdge(['http://localhost:7474/db/data/relationship/33','http://localhost:7474/db/data/relationship/34'])
		println 'getPropsResult: ' + props
		assert props
	}
	
}
