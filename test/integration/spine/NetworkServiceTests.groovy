package spine

import grails.test.GrailsUnitTestCase


class NetworkServiceTests extends GrailsUnitTestCase {
	def NetworkService networkService
	def ImportDataService importDataService
	def checkDbResults

	protected void setUp() {
		super.setUp()
		checkDbResults = importDataService.checkDB()
	}

	/**
	 * Checks the status of the test database. This checks: number of nodes, relationships, properties, indices
	 */
	protected void tearDown() {
		super.tearDown()
		assert checkDbResults == importDataService.checkDB()
	}

	void testQueryForNeighbourNodes1() {
		def result = networkService.queryForNeighbourNodes('jure.zakotnik@techbank.com', 0, 5)
		def targetResultList = [
			'anne.brown@techbank.com',
			'fero.bacak@techbank.com',
			'petra.gerste@techbank.com',
			'markus.long@techbank.com',
			'jonas.jux@techbank.com'
		]

		def resultList = []
		result.each {
			resultList.add(it.email)
		}
		assert resultList.containsAll(targetResultList)
	}

	void testQueryForNeighbourNodes2() {
		def result = networkService.queryForNeighbourNodes('jure.zakotnik@techbank.com', 3, 5)
		def targetResultList = [
			'markus.long@techbank.com',
			'jonas.jux@techbank.com',
			'matthias.zugler@techbank.com',
			'falk.seibild@techbank.com',
			'brigitte.prinz@techbank.com'
		]
		def resultList = []
		result.each {
			resultList.add(it.email)
		}
		assert resultList.containsAll(targetResultList)
	}

	void testQueryForNeighbourNodes3() {
		def result = networkService.queryForNeighbourNodes('does_not_exist@techbank.com', 3, 5)
		def targetResultList = []
		def resultList = []
		result.each {
			resultList.add(it.email)
		}
		assert resultList.containsAll(targetResultList)
	}

	void testGetNodeURIFromEmail() {
		assert networkService.getNodeURIFromEmail('jure.zakotnik@techbank.com') == 'http://localhost:7474/db/data/node/3'
		assert networkService.getNodeURIFromEmail('monika.hoppe@techbank.com') == 'http://localhost:7474/db/data/node/5'
		assert networkService.getNodeURIFromEmail('invalidemail.email@techbank.com') == null
	}

	void testGetIncomingTagsForNode2() {
		def output = networkService.getIncomingTagsForNode('markus.long@techbank.com')
		assert output == ['ITIL': 4, 'Help': 3, 'Operations': 5, 'Desk': 3, 'IT': 3]
	}

	void testGetIncomingTagsForNode() {
		def output = networkService.getIncomingTagsForNode('christan.tueffers@techbank.com')
		assert output == [:]
	}

	void testCreateAndDeleteNode() {
		def r = ''
		def data = [lastName: 'UnitTestNode', firstName: 'Node', password: 'manage', country: 'Germany', city: 'Frankfurt', email: 'unit.test@techbank.com', image: 'jure.zakotnik@techbank.com.jpg']
		def output = networkService.createNode(data)
		println 'Create node output: ' + output
		assert networkService.readNode('unit.test@techbank.com')
		networkService.deleteNode('unit.test@techbank.com')
		assert networkService.readNode('unit.test@techbank.com') == null
	}

	void testCreateAndDeleteRelationship() {
		def props = ['startNode': 'christian.tueffers@techbank.com', 'endNode': 'ingmar.mueller@techbank.com']
		def output = networkService.createRelationship(props)
		println 'Create relationship output: ' + output
		assert output
		networkService.deleteRelationship(props)
		assert networkService.readRelationship(props) == []
	}

	void testReadNode() {
		def data = networkService.readNode('jure.zakotnik@techbank.com')
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
		def r = ''
		def data = [lastName: 'Zakotnik', firstName: 'Jure', password: 'cluster2', country: 'Germany', city: 'Frankfurt', email: 'jure.zakotnik@techbank.com', image: 'jure.zakotnik@techbank.com.jpg']
		networkService.updateNode('jure.zakotnik@techbank.com', data)
		r = networkService.readNode('jure.zakotnik@techbank.com')
		println r
		assert r.password == 'cluster2'
		networkService.updateNode('jure.zakotnik@techbank.com', [password: 'cluster'])
		r = networkService.readNode('jure.zakotnik@techbank.com')
		println r
		assert r.password == 'cluster'
	}

	void testReadNodeEmpty() {
		def data = networkService.readNode('noemail.zakotnik@techbank.com')
		println data
		assert data == null
	}

	void testQueryNodeWithSingleParameter() {
		def queryObject = [email: 'm*']
		def data = networkService.queryNode(queryObject)
		println data
		assert data == [
			'monika.hoppe@techbank.com',
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
			'matthias.ossler@techbank.com'
		]
	}

	void testQueryNodeWithSingleParameter2() {
		def queryObject = [email: 'christian.tueffers@techbank.com']
		def data = networkService.queryNode(queryObject)
		println data
		assert data == [
			'christian.tueffers@techbank.com'
		]
	}


	void testQueryNodeWithManyParametersOneResult() {
		def queryObject = [email: 'm*', lastName: 'Miller']
		def data = networkService.queryNode(queryObject)
		println data
		assert data == [
			'matthias.miller@techbank.com'
		]
	}



	void testQueryNodeWithManyParametersNoResult() {
		def queryObject = [email: 'm*', firstName: 'Zakotnik']
		def data = networkService.queryNode(queryObject)
		println data
		assert data == []
	}


	void testReadRelationship() {
		def props = ['startNode': 'jure.zakotnik@techbank.com', 'endNode': 'fero.bacak@techbank.com']
		def json = networkService.readRelationship(props)
		println json
		assert json == [
			'http://localhost:7474/db/data/relationship/50'
		]
	}

	void testSetProperty() {
		def data = networkService.setProperty(['startNode': 'jure.zakotnik@techbank.com', 'endNode': 'fero.bacak@techbank.com', 'tags': 'zCloud zJava'])
		println data
		assert data
	}

	void testGetProperty() {
		def data = networkService.getProperty([
			'http://localhost:7474/db/data/relationship/70'
		])
		println data
		assert data == ['Accounting']
	}

	void testCreateDatabase() //use this only with an empty database
	{

		//read nodes file
		def nodesInput = new File('.\\test\\unit\\spine\\nodes.txt')
		//println nodesInput.text
		//networkService.importNodes(nodesInput.text)
		def edgesInput = new File('.\\test\\unit\\spine\\edges.txt')
		//println edgesInput.text
		//networkService.importEdges(edgesInput.text)

		assert true
	}

	void testGetAllProperties() {
		def allProps = networkService.getAllProperties()
		assert allProps == ['Agile': 11,
			'IT': 14,
			'Java': 18,
			'Bielefeld': 5,
			'Soccer': 6,
			'Spring': 11,
			'Wine': 1,
			'Munich': 2,
			'Jax': 2,
			'2011': 2,
			'Warhammer': 1,
			'SSL': 2,
			'RPG': 2,
			'Operations': 7,
			'Development': 4,
			'Cloud': 3,
			'BPM': 1,
			'Violin': 3,
			'Beethoven': 4,
			'Risk': 2,
			'Front': 11,
			'Trading': 10,
			'Market': 2,
			'Office': 11,
			'Swaps': 4,
			'Bonds': 5,
			'Tradings': 1,
			'Stocks': 1,
			'Derivates': 1,
			'Products': 3,
			'Cash': 3,
			'Warrants': 3,
			'OTC': 1,
			'Help': 3,
			'ITIL': 4,
			'Desk': 3,
			'Innovation': 6,
			'HTML': 4,
			'SQL': 2,
			'Frankfurt': 5,
			'ISO20000': 2,
			'Accounting': 5,
			'Chinese': 5,
			'Sales': 2,
			'Business': 2,
			'Switzerland': 1,
			'London': 1,
			'zCloud': 1,
			'zJava': 1]
	}

	void testCreateAndDeleteProperty() {
		def props = ['startNode': 'ingmar.mueller@techbank.com', 'endNode': 'markus.long@techbank.com', 'tags':'Sales']
		def relationship = networkService.readRelationship([props])
		
		//add "Sales" property and take it away again
		networkService.setProperty(props)
		List foundTags = networkService.getProperty( networkService.readRelationship(props))
		assert (foundTags.contains('Sales'))
		networkService.deleteProperty(props)
		foundTags = networkService.getProperty( networkService.readRelationship(props))
		assert (!foundTags.contains('Sales'))
	}
	
	void testCreateAndDeleteProperty2() {
		// We want to test if when the last tag is removed, the property is actually deleted
		def props = ['startNode': 'markus.long@techbank.com', 'endNode': 'michael.arlt@techbank.com', 'tags':'HTML']
		def relationship = networkService.readRelationship([props])
		assert(!relationship) // This relationship is not supposed to exist
		
		networkService.createRelationship(props)
		relationship = networkService.readRelationship([props])
		assert(relationship) // Now we created it, we are supposed to find the relationship
		
		networkService.deleteProperty(props)
		relationship = networkService.readRelationship([props])
		assert(!relationship) //We deleted the last property of the relationship, to it should be deleted
	}
	
	void testGetIdFromURI() {
		assert(networkService.getIdFromURI('http://localhost:7474/db/data/node/101') == '101')
		assert(networkService.getIdFromURI('http://localhost:7474/db/data/relationship/42') == '42')
		assert(networkService.getIdFromURI('/db/data/node/101') == '101')
		assert(networkService.getIdFromURI('/db/data/relationship/42') == '42')
	}
}

