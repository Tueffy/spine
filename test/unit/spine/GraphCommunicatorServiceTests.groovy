package spine

import grails.test.*

class GraphCommunicatorServiceTests extends GrailsUnitTestCase {
    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }
	
	void testneoDelete() {
		def n = new GraphCommunicatorService()
		def node = n.neoPost('/db/data/node', ['name' : 'Testuser'])
		def result = n.neoDelete(node.self)
		assert result
	}
}
