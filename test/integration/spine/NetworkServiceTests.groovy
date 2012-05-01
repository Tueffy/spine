package spine

import org.junit.rules.ExpectedException;

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
		def Network network = networkService.queryForNeighbourNodes('ahmed.fatir@innonet-bank.com', 0, 5, '', false)
		def List networkEmailList = network.toEmailList()
				
		def targetResultList = [
			'oliver.schaefer@innonet-bank.com', 
			'marc.wunder@innonet-bank.com', 
			'girish.vaseddy@innonet-bank.com', 
			'dan.shaeffer@innonet-bank.com', 
			'selma.gross@innonet-bank.com'
		]

		assert networkEmailList.size() == 5
		assert networkEmailList.containsAll(targetResultList)
	}

	void testQueryForNeighbourNodes2() {
		// Do not look for extended network
		def Network network = networkService.queryForNeighbourNodes('ahmed.fatir@innonet-bank.com', 3, 5, '', false)
		def List networkEmailList = network.toEmailList()
		
		def targetResultList = [
			'dan.shaeffer@innonet-bank.com', 
			'selma.gross@innonet-bank.com', 
			'wolf.becker@innonet-bank.com', 
			'carl.gelpson@innonet-bank.com', 
			'tino.bodden@innonet-bank.com'
		]
		
		assert networkEmailList.size() == 5
		assert networkEmailList.containsAll(targetResultList)
	}

	void testQueryForNeighbourNodes3() {
		// Do not look for extended network
		def Network network = networkService.queryForNeighbourNodes('does_not_exist@techbank.com', 3, 5, '', false)
		def List networkEmailList = network.toEmailList()
		
		def targetResultList = []
		
		assert networkEmailList.size() == 0
		assert networkEmailList.containsAll(targetResultList)
	}
	
	void testQueryForNeighbourNodes4()
	{
		// Do not look for extended network
		def filter = 'Agile'
		def Network network = networkService.queryForNeighbourNodes('ahmed.fatir@innonet-bank.com', 0, 20, filter, false)
		def List networkEmailList = network.toEmailList()
		
		def targetResultList = [
			'dan.shaeffer@innonet-bank.com',
			'pete.jameson@innonet-bank.com', 
			'heiko.hakon@innonet-bank.com', 
			'chris.johnson@innonet-bank.com', 
			'heinz.schiffer@innonet-bank.com'
		]
		
		assert networkEmailList.size() == targetResultList.size()
		assert networkEmailList.containsAll(targetResultList)
	}
	
	void testQueryForNeighbourNodes5()
	{
		// Do not look for extended network
		def filter = 'IT'
		def Network network = networkService.queryForNeighbourNodes('ahmed.fatir@innonet-bank.com', 0, 20, filter, false)
		def List networkEmailList = network.toEmailList()
		
		def targetResultList = [
			'girish.vaseddy@innonet-bank.com', 
			'heiko.hakon@innonet-bank.com'
		]
		
		assert networkEmailList.size() == targetResultList.size()
		assert networkEmailList.containsAll(targetResultList)
	}
	
	void testQueryForNeighbourNodes6()
	{
		// Do not look for extended network
		def filter = 'Agile IT Operations'
		def Network network = networkService.queryForNeighbourNodes('ahmed.fatir@innonet-bank.com', 0, 20, filter, false)
		def List networkEmailList = network.toEmailList()
		
		def targetResultList = [
			'girish.vaseddy@innonet-bank.com', 
			'dan.shaeffer@innonet-bank.com', 
			'pete.jameson@innonet-bank.com', 
			'heiko.hakon@innonet-bank.com', 
			'dirk.felwag@innonet-bank.com', 
			'chris.johnson@innonet-bank.com', 
			'ralph.mckenzie@innonet-bank.com', 
			'heinz.schiffer@innonet-bank.com'
		]
		
		assert networkEmailList.size() == targetResultList.size()
		assert networkEmailList.containsAll(targetResultList)
	}
	
	void testGetNodeURIFromEmail() {
        assert networkService.getNodeURIFromEmail('ahmed.fatir@innonet-bank.com') == 'http://localhost:7474/db/data/node/1'
        assert networkService.getNodeURIFromEmail('andrea.poulson@innonet-bank.com') == 'http://localhost:7474/db/data/node/6'
        assert networkService.getNodeURIFromEmail('invalidemail.email@techbank.com') == null
    }

	void testGetIncomingTagsForNode2() {
        def output = networkService.getIncomingTagsForNode('ahmed.fatir@innonet-bank.com')
        assert output == ['HTML':1, 'Java':7, 'BI':8, 'BJ':1]
    }

	void testGetIncomingTagsForNode() {
		def output = networkService.getIncomingTagsForNode('tom.foller@innonet-bank.com')
		assert output == ['Soccer': 1]
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
		def props = ['startNode': 'ahmed.fatir@innonet-bank.com', 'endNode': 'michelle.beaufond@innonet-bank.com']
		def output = networkService.createRelationship(props)
		println 'Create relationship output: ' + output
		assert output
		networkService.deleteRelationship(props)
		assert networkService.readRelationship(props) == []
	}
	
	void testCreateAndDeleteRelationship2() {
		def props = ['startNode': 'ahmed.fatir@innonet-bank.com', 'endNode': 'michelle.beaufond@innonet-bank.com', 'tags': ['Prototype']]
		def output = networkService.createRelationship(props)
		println 'Create relationship output: ' + output
		assert output
		networkService.deleteRelationship(props)
		assert networkService.readRelationship(props) == []
	}

	void testReadNode() {
		def data = networkService.readNode('ahmed.fatir@innonet-bank.com')
		println data
		assert data != null
		assert data.lastName == 'Fatir'
		assert data.firstName == 'Ahmed'
		assert data.password == 'password'
		assert data.country == 'UK'
		assert data.city == 'London'
		assert data.email == 'ahmed.fatir@innonet-bank.com'
		assert data.image == 'ahmed.fatir@innonet-bank.com.jpg'
	}

	void testUpdateNode() {
		def r = ''
		def data = [lastName: 'Fatir', firstName: 'Ahmed', password: 'password2', country: 'UK', city: 'London', email: 'ahmed.fatir@innonet-bank.com', image: 'ahmed.fatir@innonet-bank.com.jpg']
		networkService.updateNode('ahmed.fatir@innonet-bank.com', data, true)
		r = networkService.readNode('ahmed.fatir@innonet-bank.com')
		println r
		assert r.password == 'password2'
		networkService.updateNode('ahmed.fatir@innonet-bank.com', [password: 'password'])
		r = networkService.readNode('ahmed.fatir@innonet-bank.com')
		println r
		assert r.password == 'password'
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
		
		def targetList = [
			"marc.wunder@innonet-bank.com", 				"markus.bauer@innonet-bank.com", 
			"marc.zallen@innonet-bank.com", 				"mark.salches@innonet-bank.com", 
			"mathieu.menton@innonet-bank.com", 				"melanie.karr@innonet-bank.com", 
			"martin.schlager@innonet-bank.com", 			"michael.doring@innonet-bank.com", 
			"martin.taylor@innonet-bank.com", 				"mel.wallert@innonet-bank.com", 
			"mary.ashton@innonet-bank.com", 				"michael.schneider@innonet-bank.com", 
			"monika.kollberg@innonet-bank.com", 			"michelle.beaufond@innonet-bank.com", 
			"mike.bernhardt@innonet-bank.com"
		]
		
		assert data.size() == targetList.size()
		assert data.containsAll(targetList)
    }

    void testQueryNodeWithSingleParameter2() {
        def queryObject = [email: 'ahmed.fatir@innonet-bank.com']
        def data = networkService.queryNode(queryObject)
        println data
        assert data == ['ahmed.fatir@innonet-bank.com']
    }


	void testQueryNodeWithManyParametersOneResult() {
		def queryObject = [email: 'm*', lastName: 'Wunder']
		def data = networkService.queryNode(queryObject)
		println data
		assert data == [
			'marc.wunder@innonet-bank.com'
		]
	}



	void testQueryNodeWithManyParametersNoResult() {
		def queryObject = [email: 'm*', firstName: 'Zakotnik']
		def data = networkService.queryNode(queryObject)
		println data
		assert data == []
	}


	void testReadRelationship() {
        def props = ['startNode': 'ahmed.fatir@innonet-bank.com', 'endNode': 'oliver.schaefer@innonet-bank.com']
        def json = networkService.readRelationship(props)
        println json
        assert json == ['http://localhost:7474/db/data/relationship/2']
    }

	void testSetProperty() {
		def data = networkService.setProperty(['startNode': 'ahmed.fatir@innonet-bank.com', 'endNode': 'oliver.schaefer@innonet-bank.com', 'tags': 'zCloud zJava'])
	    println data
	    assert data
	}

	void testGetProperty() {
        def data = networkService.getProperty(['http://localhost:7474/db/data/relationship/70'])
        println data
        assert data == ['BalanceSheet', 'Cycling', 'Wine']
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
				 assert allProps ==  [
					 Bonds:30, 			Swaps:10, 			FX:15, 				Derivates:6, 
					 London:6, 			Clubbing:10, 		Securities:44, 		Innovation:24, 
					 Soccer:18, 		Golf:18, 			ITIL:17, 			Configuration:1, 
					 ChangeMgmt:1, 		HTML:11, 			Java:49, 			IndianFood:2, 
					 PM:25, 			Agile:11, 			SOA:20, 			Sales:21, 
					 Wine:30, 			PMO:5, 				Support:6, 			Graphics:3, 
					 IT:2, 				Procurement:2, 		Law:2, 				Opera:10, 
					 Food:17, 			DrHouse:1, 			Shopping:19, 		Diversity:22, 
					 Springsteen:1, 	Jobs:1, 			Positions:1, 		Coffee:7, 
					 Sailing:1, 		Bookkeeping:2, 		Cycling:6, 			GeneralLedger:8, 
					 Ibiza:1, 			Indexes:6, 			Risk:13, 			Renault:3, 
					 Surfing:1, 		Parallelism:1, 		BI:46, 				MonteCarlo:1,
					 Algo:7, 			Gadgets:1, 			Games:1, 			SQL:7, 
					 XML:3, 			Architecture:17, 	Visionary:2, 		'2020':9, 
					 Reporting:9, 		Leadership:25, 		Tennis:3, 			Jazz:3, 
					 Encryption:5, 		PKI:5, 				ISO27001:5, 		Scheduling:22, 
					 RealTime:3, 		Volatilities:3, 	OTC:3, 				Securitites:2, 
					 BalanceSheet:4, 	TigerWoods:1, 		Pastis:1, 			Cheese:1, 
					 Wife:1, 			TVJunkie:1, 		Beer:2, 			Estimating:1, 
					 Forecasting:1, 	Operations:3, 		ReleaseMgmt:3, 		Theater:1, 
					 MSProject:2, 		ProjectPlan:1, 		TimeReporting:3, 	SocialNetwork:1, 
					 Strategy:3, 		Putting:1, 			Planning:1, 		Audi:1, 
					 Montmartre:2, 		SoftSkills:1, 		Security:5, 		MiniCooper:3, 
					 Equities:11, 		Eclipse:1, 			Accounting:2, 		DRCProject:1, 
					 Becks:1, 			'.Net':6, 			ASP:1, 				KPIs:3, 
					 Waterfall:1, 		Requirements:1, 	Scorecards:1, 		BestPractices:1, 
					 Husband:1, 		TradIX:21, 			Recruiting:6, 		Silverlight:1, 
					 Collaboration:4, 	Murex:1, 			Continuity:1, 		Monopoly:1, 
					 SocialNetworking:1, ProjectPlanning:1, Serengeti:1, 		Infrastructure:1, 
					 'ASP.NET':2, 		Cars:3, 			Spurs:1, 			Web:2, 
					 Certificates:3, 	Mercedes:1, 		BAM:5, 				CEP:2, 
					 FluxProject:1, 	ThnkTank:1, 		Mergers:1, 			Chocolate:1, 
					 AppStore:2, 		Databases:1, 		Unix:1, 			Cranberra:1, 
					 Australia:2, 		Quality:1, 			Information:2, 		DataWarehouse:6, 
					 JavaScript:1, 		Negotiations:1, 	BJ:1, 				Status:1, 
					 Scrum:2, 			FinPortal:7,		Dilbert:1, 			Greece:1, 
					 NonRepudiation:1, 	Integrity:1, 		SAP:8, 				TourDeFrance:2, 
					 BMW:1, 			ITL:1, 				SQLServer:2, 		Printing:2, 
					 Forecast:1, 		Cloud:2, 			WebServices:1, 		BigData:1, 
					 iPad:1, 			Mobile:2, 			Simulations:2, 		Piano:1, 
					 Shakespeare:1, 	Whisky:1, 			Vision:1, 			Stratey:1, 
					 Bremen:1, 			Eintracht:1, 		wine:1, 			SocialNetworks:1, 
					 Applications:2, 	Shoes:1, 			RiskAnalysis:1, 	TourEiffel:1, 
					 Fonds:4, 			Workplace:1, 		Perrier:1, 			Tarantino:1, 
					 BloodhoundDogs:1, 	Toscana:2, 			XP:1, 				Party:1, 
					 PairProgramming:1, Trading:1, 			Trades:1, 			PubQuiz:1, 
					 ClassicMusic:1, 	FishChips:1, 		Spaghetti:2, 		Madonna:1, 
					 Marketing:1, 		HelpDesk:1, 		BradPitt:1, 		LesBleus:5, 
					 SharePoint:1, 		zCloud:1, 			zJava:1
				 ]
	}

	void testCreateAndDeleteProperty() {
		def props = ['startNode': 'ahmed.fatir@innonet-bank.com', 'endNode': 'oliver.schaefer@innonet-bank.com', 'tags':'Sales']
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
		def props = ['startNode': 'ahmed.fatir@innonet-bank.com', 'endNode': 'kerstin.kruse@innonet-bank.com', 'tags':'HTML']
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
		def contextUserEmail = 'ahmed.fatir@innonet-bank.com'
		def targetEmail = 'oliver.schaefer@innonet-bank.com'
		
		def NetworkedUser networkedUser = networkService.queryUserInNetworkContext(contextUserEmail, targetEmail)
		
		def directTagsExpected = [
			'Innovation', 'Mobile', 'zCloud', 'zJava'
		]
		
		assert networkedUser.directTags.sort() == directTagsExpected.sort()
		assert networkedUser.distance == 1
		assert networkedUser.user.email == targetEmail
	}
	
	void testParseSearchQueryIntoLuceneQuery() {
		def query = 'Java'
		def expected = '(tag : java OR badge : java OR email : java OR firstname : java OR lastname : java OR city : java)'
		assert networkService.parseSearchQueryIntoLuceneQuery(query) == expected
	}
	
	void testParseSearchQueryIntoLuceneQuery2() {
		def query = 'Java Frankfurt'
		def expected = '(tag : java OR badge : java OR email : java OR firstname : java OR lastname : java OR city : java)' + 
			' OR (tag : frankfurt OR badge : frankfurt OR email : frankfurt OR firstname : frankfurt OR lastname : frankfurt OR city : frankfurt)'
		assert networkService.parseSearchQueryIntoLuceneQuery(query) == expected
	}
	
	void testParseSearchQueryIntoLuceneQuery3() {
		def query = 'Java AND Frankfurt'
		def expected = '(tag : java OR badge : java OR email : java OR firstname : java OR lastname : java OR city : java)' +
			' AND (tag : frankfurt OR badge : frankfurt OR email : frankfurt OR firstname : frankfurt OR lastname : frankfurt OR city : frankfurt)'
		assert networkService.parseSearchQueryIntoLuceneQuery(query) == expected
	}
}

