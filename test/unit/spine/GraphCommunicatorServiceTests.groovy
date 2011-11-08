package spine

import grails.test.*

class GraphCommunicatorServiceTests extends GrailsUnitTestCase {
    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testSomething() {

    }
	
	void testneoDelete() {
		def n = new GraphCommunicatorService()
		def node = n.neoPost('/db/data/node', ['name' : 'Testuser'])
		def result = n.neoDelete(node.self)
		assert result
	}
	
	void testneoDelete2() {
		def n = new GraphCommunicatorService()
		def result = n.neoDelete('["http://localhost:7474/db/data/relationship/63"]')
		assert result
	}
}
