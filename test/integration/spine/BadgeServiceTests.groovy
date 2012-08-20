package spine

import grails.test.GrailsUnitTestCase

class BadgeServiceTests extends GrailsUnitTestCase {

	def BadgeService badgeService
	def taglist1 = [Java:5, Spring:3, Operations:9, ITIL:11]
	def taglist2 = [Frankfurt:6]
	def taglist3 = [Cloud:3, Soccer:5]
	
	def ImportDataService importDataService
	def checkDbResults
	
    protected void setUp() {
        super.setUp()
//		checkDbResults = importDataService.checkDB()
    }
	
	/**
	* Checks the status of the test database. This checks: number of nodes, relationships, properties, indices
	*/
    protected void tearDown() {
        super.tearDown()
//		assert checkDbResults == importDataService.checkDB()
    }

    void testEvaluateTags1() {

		def result = badgeService.evaluateTags(taglist1)
		assert result.size() == 3
    }

	void testEvaluateTags2() {

		def result = badgeService.evaluateTags(taglist2)
		assert result.size() == 1
    }
}

