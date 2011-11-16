package spine

import grails.test.*


class BadgeServiceTests extends GrailsUnitTestCase {

	def badgeService = new BadgeService()
	def taglist1 = [Java:5, Spring:3, Operations:9, ITIL:11]
	def taglist2 = [Frankfurt:6]
	
    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
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
