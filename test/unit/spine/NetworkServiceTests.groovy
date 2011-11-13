package spine

import grails.test.*

class NetworkServiceTests extends GrailsUnitTestCase {
    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

	
	void testQueryForNeighbourNodes() {
		def n = new NetworkService()
		def result = n.queryForNeighbourNodes('jure.zakotnik@techbank.com', 0, 10)
		def targetResultList = ['anne.brown@techbank.com',
			'falk.seibild@techbank.com',
			'markus.long@techbank.com',
			'fero.bacak@techbank.com',
			'jonas.jux@techbank.com',
			'falk.seibild@techbank.com',
			'jure.zakotnik@techbank.com',
			'petra.gerste@techbank.com',
			'matthias.zugler@techbank.com',
			'fero.bacak@techbank.com']
		def resultList = []
		result.each {
			println it.email
			resultList.add(it.email)
		}
		assert resultList.containsAll(targetResultList)
	}
	
	void testGetNodeURIFromEmail() {
		def n = new NetworkService()
		assert n.getNodeURIFromEmail('jure.zakotnik@techbank.com') == 'http://localhost:7474/db/data/node/3'
		assert n.getNodeURIFromEmail('monika.hoppe@techbank.com') == 'http://localhost:7474/db/data/node/5'
	}
	
	void testGetIncomingTagsForNode() {
		def n = new NetworkService()
		def output = n.getIncomingTagsForNode('markus.long@techbank.com')
		assert output == ['ITIL':3, 'Help':1, 'Operations':3, 'Desk':1, 'IT':2]
	}
	
	void testCreateAndDeleteNode(){
		def n = new NetworkService()
		def r = ''
		def data = [lastName:'UnitTestNode', firstName:'Node', password:'manage', country:'Germany', city:'Frankfurt', email:'unit.test@techbank.com', image:'jure.zakotnik@techbank.com.jpg']
		def output = n.createNode(data)
		println 'Create node output: ' + output
		assert n.readNode('unit.test@techbank.com')
		n.deleteNode('unit.test@techbank.com')
		assert n.readNode('unit.test@techbank.com') == null
	}
	
	void testReadNode()
	{
		def n = new NetworkService()
		def data = n.readNode('jure.zakotnik@techbank.com')
		println data
		assert data.lastName == 'Zakotnik'
		assert data.firstName == 'Jure'
		assert data.password == 'cluster'
		assert data.country == 'Germany'
		assert data.city == 'Frankfurt'
		assert data.email == 'jure.zakotnik@techbank.com'
		assert data.image == 'jure.zakotnik@techbank.com.jpg'
	}

	void testUpdateNode() {
		def n = new NetworkService()
		def r = ''
		def data = [lastName:'Zakotnik', firstName:'Jure', password:'cluster2', country:'Germany', city:'Frankfurt', email:'jure.zakotnik@techbank.com', image:'jure.zakotnik@techbank.com.jpg']
		n.updateNode('jure.zakotnik@techbank.com', data)
		r = n.readNode('jure.zakotnik@techbank.com')
		println r
		assert r.password == 'cluster2'
		n.updateNode('jure.zakotnik@techbank.com', [password:'cluster'])
		r = n.readNode('jure.zakotnik@techbank.com')
		println r
		assert r.password == 'cluster'
		
		
	}

	void testReadNodeEmpty()
	{
		def n = new NetworkService()
		def data = n.readNode('noemail.zakotnik@techbank.com')
		println data
		assert data == null
	}

	void testQueryNodeWithSingleParameter()
	{
		def n = new NetworkService()
		def queryObject = [email : 'm*']
		def data = n.queryNode(queryObject)
		println data
		assert data == ['monika.hoppe@techbank.com', 
			'matthias.miller@techbank.com', 
			'markus.long@techbank.com', 
			'michael.arlt@techbank.com', 
			'michael.frisch@techbank.com', 
			'melanie.murrende@techbank.com', 
			'matthias.zugler@techbank.com', 
			'michaela.pfeffer@techbank.com', 
			'magi.clavi@techbank.com', 
			'markus.tretschok@techbank.com', 
			'mirko.traesch@techbank.com', 
			'mario.gamez@techbank.com', 
			'marianne.michel@techbank.com', 
			'mick.jugger@techbank.com', 
			'manuel.neiner@techbank.com', 
			'matthias.ossler@techbank.com']
	}
	void testQueryNodeWithManyParametersOneResult()
	{
		def n = new NetworkService()
		def queryObject = [email : 'm*', lastName : 'Miller']
		def data = n.queryNode(queryObject)
		println data
		assert data == ['matthias.miller@techbank.com'] 
	}
	
	void testQueryNodeWithManyParametersNoResult()
	{
		def n = new NetworkService()
		def queryObject = [email : 'm*', firstName : 'Zakotnik']
		def data = n.queryNode(queryObject)
		println data
		assert data == []
	}
	
	void testCreateRelationship()
	{
		def n = new NetworkService()
		//todo, first remove relationship between jure and ingmar, then add it
		
		def data = n.createRelationship(['startNode':'jure.zakotnik@techbank.com','endNode':'ingmar.mueller@techbank.com','tags':'zCloud zJava'])
		println data
		assert data == "http://localhost:7474/db/data/relationship/9"
	}
	
	void testReadRelationship()
	{
		def props = ['startNode':'jure.zakotnik@techbank.com','endNode':'ingmar.mueller@techbank.com']
		def n = new NetworkService()
		def json = n.readRelationship(props)
		println json
		assert json == ['http://localhost:7474/db/data/relationship/70']
	}

	void testSetProperty()
	{
		def n = new NetworkService()
		def data = n.setProperty(['startNode':'jure.zakotnik@techbank.com','endNode':'ingmar.mueller@techbank.com','tags':'zCloud zJava'])
		println data
		assert data

	}
	
	void testGetProperty()
	{
		def n = new NetworkService()
		def data = n.getProperty(['http://localhost:7474/db/data/relationship/70'])
		println data
		assert data == ['zCloud', 'zJava']
	}
	
	void testCreateDatabase() //use this only with an empty database
	{
		def n = new ImportDataService()
		//read nodes file
		def nodesInput = new File('.\\test\\unit\\spine\\nodes.txt')
		//println nodesInput.text
		//n.importNodes(nodesInput.text)
		def edgesInput = new File('.\\test\\unit\\spine\\edges.txt')
		//println edgesInput.text
		//n.importEdges(edgesInput.text)
		
		assert true
	}
}
