package spine

import grails.test.*

class NetworkServiceTests extends GrailsUnitTestCase {
    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }


	void testReadNodeViaCypher() {
		def n = new NetworkService()
		def result = n.readNodeViaCypher('christian.tueffers@techbank.com', 0, 5)
		assert n != null
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

	void testQueryNode()
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

}
