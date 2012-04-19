package spine;

import grails.test.GrailsUnitTestCase

class UserTests extends GrailsUnitTestCase {

	void testSortTags() {
		def User user = new User()
		user.tags = [Agile: 1, IT:5, Java: 3, Spring:1]
		def directTags = ['Java']
		
		user.sortTags(directTags)
		
		def expected = ['Java', 'IT', 'Agile', 'Spring']
		println user.tags
		println expected
		def i = 0
		user.tags.each {
			assert expected[i] == it.key
			i++
		}
	}
	
}
