package spine

import grails.test.GrailsUnitTestCase

class SpineServiceTests extends GrailsUnitTestCase {

    private SpineService s
    def u1 = new User()
    def u2 = new User()
    def u3 = new User()
    def u4 = new User()

    protected void setUp() {
        super.setUp()

        //Define Users, which can be used during the testing
        u1.email = 'markus.long@techbank.com'
        u2.email = 'jure.zakotnik@techbank.com'
        u3.email = 'christian.tueffers@techbank.com'
        u4.email = 'fero.bacak@techbank.com'

        s = new BeanCtxFactory().createAppCtx().getBean(SpineService.class)
    }

    protected void tearDown() {
        super.tearDown()
    }

    // tests for loginUser

    void testLoginUser1() {

        def success = s.loginUser('christian.tueffers@techbank.com', 'manage')
        assert success.lastName == 'Tueffers'
        assert success.firstName == 'Christian'
        assert success.country == 'Germany'
        assert success.city == 'Frankfurt'
        assert success.email == 'christian.tueffers@techbank.com'
        assert success.imagePath == 'christian.tueffers@techbank.com.jpg'
        assert success.badges.size() == 0
    }

    void testLoginUser2() {

        def failure = s.loginUser('christian.tueffers@techbank.com', 'password')
        assert failure == null
    }

    void testLoginUser3() {

        def notexist = s.loginUser('christian.tueffers@techbank.com', 'password')
        assert notexist == null
    }

    void testLoginUser4() {

        def success = s.loginUser('markus.long@techbank.com', 'clojure')
        assert success.lastName == 'Long'
        assert success.firstName == 'Markus'
        assert success.country == 'Germany'
        assert success.city == 'Hamburg'
        assert success.email == 'markus.long@techbank.com'
        assert success.imagePath == 'markus.long@techbank.com.jpg'
        assert success.tags.size() == 5
        assert success.badges.size() == 1
    }

    // tests for getUserNetwork

    void testGetUserNetwork1() {

        def result = s.getUserNetwork(u1, '', 0)
        assert result.size() == 10
    }

    void testGetUserNetwork2() {

        def result = s.getUserNetwork(u2, '', 0)
        assert result.size() == 10
    }

    // tests for getUser

    void testGetUser1() {

        def result = s.getUser('markus.long@techbank.com')
        assert result != null

    }

    // tests for getUserTags

    void testGetUserTags1() {

        def output = s.getUserTags(u1)
        assert output == ['ITIL': 4, 'Help': 3, 'Operations': 5, 'Desk': 3, 'IT': 3]

    }

    void testGetUserTags2() {

        def output = s.getUserTags(u2)
        assert output == ['Spring': 4, 'Java': 8, 'IT': 9, 'Agile': 11, 'Cloud': 1, 'BPM': 1, 'RPG': 2, 'Operations': 2, 'Bielefeld': 5, 'Development': 1, 'Warhammer': 1, 'SSL': 1, 'Munich': 2, 'Jax': 2, '2011': 2, 'Wine': 1, 'Soccer': 1]
    }

    void testGetUserTags3() {

        def output = s.getUserTags(u3)
        assert output == [:]
    }

    void testGetUserTags4() {

        def output = s.getUserTags(u4)
        assert output == [Spring: 7, Java: 9, Development: 2, IT: 1, HTML: 3, SQL: 2, SSL: 1, zCloud: 1, zJava: 1]
    }

/*	void testAddTag(){

        def test = s.addTag(u1,'ingmar.mueller@techbank.com','zCloud zJava')
        assert test == true
    }
*/

}
