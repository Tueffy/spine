package spine

import grails.test.GrailsUnitTestCase
import org.apache.commons.logging.LogFactory

class GraphCommunicatorServiceTests extends GrailsUnitTestCase {
    static transactional = false

    private GraphCommunicatorService n
    private static final log = LogFactory.getLog(this)

    protected void setUp() {
        super.setUp()

        mockLogging(GraphCommunicatorService.class, true)
        n = new BeanCtxFactory().createAppCtx().getBean(GraphCommunicatorService.class)

    }

    protected void tearDown() {
        super.tearDown()
    }

    void testneoDelete() {

        def node = n.neoPost('/db/data/node', ['firstName': 'Testuser'])

        def testNode = n.neoGet(node.self)

        assert testNode

        n.neoDelete(node.self)

        testNode = n.neoGet(node.self)

        assert !testNode

    }
}
