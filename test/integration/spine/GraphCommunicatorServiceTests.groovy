package spine

import grails.test.GrailsUnitTestCase
import org.apache.commons.logging.LogFactory

class GraphCommunicatorServiceTests extends GroovyTestCase {
    static transactional = false

    def graphCommunicatorService
    private static final log = LogFactory.getLog(this)

    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

	void testneoDelete() {
		def node = graphCommunicatorService.neoPost('/db/data/node', ['firstName': 'Testuser'])
		def testNode = graphCommunicatorService.neoGet(node.self)
		assert testNode
		graphCommunicatorService.neoDelete(node.self)
		testNode = graphCommunicatorService.neoGet(node.self)
		assert !testNode
	}
}
