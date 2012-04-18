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
		// Do not look for extended network
		def Network network = networkService.queryForNeighbourNodes('jure.zakotnik@techbank.com', 0, 5, [], false)
		def List networkEmailList = network.toEmailList()
				
		def targetResultList = [
			'karina.wocek@techbank.com',
			'gudrun.mosters@techbank.com', 
			'petra.gerste@techbank.com', 
			'mario.gamez@techbank.com',
			'markus.long@techbank.com'
		]

		assert networkEmailList.size() == 5
		assert networkEmailList.containsAll(targetResultList)
	}

	void testQueryForNeighbourNodes2() {
		// Do not look for extended network
		def Network network = networkService.queryForNeighbourNodes('jure.zakotnik@techbank.com', 3, 5, [], false)
		def List networkEmailList = network.toEmailList()
		
		def targetResultList = [
			'mario.gamez@techbank.com',
			'markus.long@techbank.com', 
			'jack.rumpsy@techbank.com', 
			'alice.weisse@techbank.com', 
			'john.holland@techbank.com'
		]
		
		assert networkEmailList.size() == 5
		assert networkEmailList.containsAll(targetResultList)
	}

	void testQueryForNeighbourNodes3() {
		// Do not look for extended network
		def Network network = networkService.queryForNeighbourNodes('does_not_exist@techbank.com', 3, 5, [], false)
		def List networkEmailList = network.toEmailList()
		
		def targetResultList = []
		
		assert networkEmailList.size() == 0
		assert networkEmailList.containsAll(targetResultList)
	}
	
	void testQueryForNeighbourNodes4()
	{
		// Do not look for extended network
		def tagsToSearchFor = ['Agile']
		def Network network = networkService.queryForNeighbourNodes('christian.tueffers@techbank.com', 0, 20, tagsToSearchFor, false)
		def List networkEmailList = network.toEmailList()
		
		def targetResultList = [
			'jure.zakotnik@techbank.com', 
			'bas.hoffe@techbank.com'
		]
		
		assert networkEmailList.size() == 2
		assert networkEmailList.containsAll(targetResultList)
	}
	
	void testQueryForNeighbourNodes5()
	{
		// Do not look for extended network
		def tagsToSearchFor = ['IT']
		def Network network = networkService.queryForNeighbourNodes('christian.tueffers@techbank.com', 0, 20, tagsToSearchFor, false)
		def List networkEmailList = network.toEmailList()
		
		def targetResultList = [
			'jure.zakotnik@techbank.com',
			 'markus.long@techbank.com'
		]
		
		assert networkEmailList.containsAll(targetResultList)
	}
	
	void testQueryForNeighbourNodes6()
	{
		// Do not look for extended network
		def tagsToSearchFor = ['Agile', 'IT', 'Operations']
		def Network network = networkService.queryForNeighbourNodes('christian.tueffers@techbank.com', 0, 20, tagsToSearchFor, false)
		def List networkEmailList = network.toEmailList()
		
		def targetResultList = [
			'jure.zakotnik@techbank.com', 
			'markus.long@techbank.com', 
			'michaela.pfeffer@techbank.com', 
			'alice.weisse@techbank.com', 
			'bernhard.mainburger@techbank.com', 
			'melanie.murrende@techbank.com', 
			'jim.stooge@techbank.com', 
			'peter.boll@techbank.com', 
			'fero.bacak@techbank.com', 
			'steve.hill@techbank.com', 
			'jeanluc.greedy@techbank.com']
		
		assert networkEmailList.containsAll(targetResultList)
	}
	
	void testQueryForNeighbourNodes7()
	{
		// Do not look for extended network
		def tagsToSearchFor = ['Agile']
		def Network network = networkService.queryForNeighbourNodes('christian.tueffers@techbank.com', 0, 20, tagsToSearchFor, false)
		def List networkEmailList = network.toEmailList()
		
		def targetResultList = ['jure.zakotnik@techbank.com']
		
		assert networkEmailList.containsAll(targetResultList)
	}

	void testGetNodeURIFromEmail() {
        assert networkService.getNodeURIFromEmail('jure.zakotnik@techbank.com') == 'http://localhost:7474/db/data/node/4'
        assert networkService.getNodeURIFromEmail('monika.hoppe@techbank.com') == 'http://localhost:7474/db/data/node/6'
        assert networkService.getNodeURIFromEmail('invalidemail.email@techbank.com') == null
    }

	void testGetIncomingTagsForNode2() {
        def output = networkService.getIncomingTagsForNode('markus.long@techbank.com')
        assert output == ['Help':3, 'ITIL':5, 'Operations':6, 'Desk':3, 'IT':4, 'Java':2, 'SOA':1]
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
	
	void testCreateAndDeleteRelationship2() {
		def props = ['startNode': 'christian.tueffers@techbank.com', 'endNode': 'ingmar.mueller@techbank.com', 'tags': ['Prototype']]
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
		assert data.containsAll(['monika.hoppe@techbank.com', 
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
			'matthias.ossler@techbank.com', 
			'mario.drache@eurobank.int'])
    }

    void testQueryNodeWithSingleParameter2() {
        def queryObject = [email: 'christian.tueffers@techbank.com']
        def data = networkService.queryNode(queryObject)
        println data
        assert data == ['christian.tueffers@techbank.com']
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
        assert json == ['http://localhost:7474/db/data/relationship/59']
    }

	void testSetProperty() {
		def data = networkService.setProperty(['startNode': 'jure.zakotnik@techbank.com', 'endNode': 'fero.bacak@techbank.com', 'tags': 'zCloud zJava'])
	    println data
	    assert data
	}

	void testGetProperty() {
        def data = networkService.getProperty(['http://localhost:7474/db/data/relationship/70'])
        println data
        assert data == ['Soccer', 'Frankfurt']
    }

	void testCreateDatabase() //use this only with an empty database
	{

		//read nodes file
		def nodesInput = new File('.\\test\\unit\\spine\\nodes1.txt')
		//println nodesInput.text
		//networkService.importNodes(nodesInput.text)
		def edgesInput = new File('.\\test\\unit\\spine\\edges2.txt')
		//println edgesInput.text
		//networkService.importEdges(edgesInput.text)

		assert true
	}

	void testGetAllProperties() {
		def allProps = networkService.getAllProperties()
				 assert allProps ==  ['Agile':16, 
          			'IT':30, 
          			'Java':27, 
          			'Bielefeld':8, 
          			'Soccer':9, 
          			'Spring':11, 
          			'Wine':4, 
          			'Munich':2, 
          			'Jax':2, 
          			'2011':2, 
          			'Warhammer':1, 
          			'SSL':2, 
          			'RPG':2, 
          			'Operations':15, 
          			'Development':4, 
          			'Cloud':6, 
          			'BPM':1, 
          			'Accounting':6, 
          			'Conf':1, 
          			'Gartner':1, 
          			'Retail':2, 
          			'ITIL':7, 
          			'Violin':3, 
          			'Beethoven':4, 
          			'Front':13, 
          			'Risk':4, 
          			'Trading':12, 
          			'Market':3, 
          			'Office':13, 
          			'Swaps':6, 
          			'Bonds':7, 
          			'Stocks':1, 
          			'Tradings':1, 
          			'Derivates':1, 
          			'Products':3, 
          			'Cash':3, 
          			'Warrants':3, 
          			'OTC':1, 
          			'Help':3, 
          			'Desk':3, 
          			'Innovation':8, 
          			'HTML':4, 
          			'SQL':2, 
          			'Frankfurt':5, 
          			'ISO20000':2, 
          			'Chinese':7, 
          			'Sales':2, 
          			'Business':2, 
          			'Switzerland':1, 
          			'London':7, 
          			'Clearing':1, 
          			'Recruiting':4, 
          			'Collaboration':3, 
          			'Law':1, 
          			'Contract':1, 
          			'CIO':3, 
          			'Equity':1, 
          			'Brokerage':1, 
          			'Leadership':2, 
          			'SOA':3, 
          			'HR':2, 
          			'TOP':1, 
          			'Love':1, 
          			'ProjectX':13, 
          			'zCloud':1, 
          			'zJava':1]
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
	
	void testQueryUserInNetworkContext() {
		def contextUserEmail = 'christian.tueffers@techbank.com'
		def targetEmail = 'jure.zakotnik@techbank.com'
		
		def NetworkedUser networkedUser = networkService.queryUserInNetworkContext(contextUserEmail, targetEmail)
		
		def directTagsExpected = [
			'Bielefeld', 'Warhammer', 'Agile',
			'SSL', 'Munich', 'Jax',
			'2011', 'Spring', 'IT',
			'RPG', 'Java'
		]
		
		assert networkedUser.directTags.sort() == directTagsExpected.sort()
		assert networkedUser.distance == 1
		assert networkedUser.user.email == targetEmail
	}
}

