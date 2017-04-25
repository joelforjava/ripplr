package com.joelforjava.ripplr

import grails.plugin.springsecurity.SpringSecurityService
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Ignore
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(RippleController)
@Mock([User, Ripple, UserService])
class RippleControllerSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

	/* --- global specs --- */
	def "getting the global timeline view"() {
		given: "a user with posts"

		def user = new User(username: 'bob_belcher', passwordHash: 'mvemjsnu')

		user.addToRipples( new Ripple(content: 'a very important ripple'))
		user.addToRipples( new Ripple(content: 'this one is not as important'))
		user.save(failOnError: true)

		and: "A logged in user"
		def loggedInUser = new User(username: 'gene_belcher', passwordHash: 'nomnomburger').save(failOnError: true)

		and: "a mocked user service"
		controller.userService = Mock(UserService) {
			1 * retrieveLatestUsers(_) >> [user]
		}

		and: "a mocked security service"
		def mockSecurityService = Mock(SpringSecurityService)
		mockSecurityService.getCurrentUser() >> loggedInUser
		controller.springSecurityService = mockSecurityService

		when: 'we invoke the global action'
		def model = controller.global()

		then: 'we get the expected results'
		model.user.username == loggedInUser.username
		model.rippleCount == 2

	}

	/* --- timeline specs --- */

    def "getting timeline of a valid user"() {
    	given: "a user with posts"

    	def user = new User(username: 'bob_belcher', passwordHash: 'mvemjsnu')

    	user.addToRipples( new Ripple(content: 'a very important ripple'))
    	user.addToRipples( new Ripple(content: 'this one is not as important'))
    	user.save(failOnError: true)

    	and: "a username parameter"
    	params.id = 'bob_belcher'

    	and: "a mocked user service"
    	controller.userService = Mock(UserService) {
    		1 * retrieveUser(_) >> user
    	}

    	when: "we invoke timeline action"
    	def model = controller.timeline()

    	then: "the user is returned in the model"

    	model.user.username == user.username
    	model.user.ripples.size() == 2
    }

    def "attempting a timeline of an invalid user results in an error"() {
    	given: "an invalid username" // value unimportant due to mocked user service
    	params.id = "i_am_invalid"

    	and: "a mocked user service"
    	controller.userService = Mock(UserService) {
    		1 * retrieveUser(_) >> null
    	}

    	when: "the timeline action is invoked"
    	controller.timeline()

    	then: "a 404 error is received"

    	response.status == 404
    }

	/* --- dashboard specs --- */

    def "getting dashboard of a valid user"() {
    	given: "a user with posts"

    	def user = new User(username: 'bob_belcher', passwordHash: 'mvemjsnu')

    	user.addToRipples( new Ripple(content: 'a very important ripple'))
    	user.addToRipples( new Ripple(content: 'this one is not as important'))
    	user.save(failOnError: true)

    	and: "a mocked security service"
    	def mockSecurityService = Mock(SpringSecurityService)
    	mockSecurityService.getCurrentUser() >> user
    	controller.springSecurityService = mockSecurityService

    	when: "we invoke dashboard action"
    	controller.dashboard()

    	then: "we are sent to the timeline view"

    	view == '/ripple/timeline'
    	model.user.username == user.username
    	model.user.ripples.size() == 2
    }

	/* --- index specs --- */

    def "getting index with current user results in timeline"() {
    	given: "A mocked param ID"
    	params.id = "bob_belcher"

    	and: "a user"
    	def user = new User(username: 'bob_belcher', passwordHash: 'mvemjsnu')
    	user.save(failOnError: true)

    	when: "we invoke index action"
    	controller.index()

    	then: "we are sent to the timeline view"

    	response.redirectedUrl == '/ripple/timeline/bob_belcher'
		response.status == 302

    }

    def "getting index with no id param results in error"() {
    	given: "The ID param is set to null"
    	params.id = null

    	when: "we invoke index action"
    	controller.index()

    	then: "we receive an error (currently 400 since realistically a client should always send the ID)"
    	response.status == 400

    }

	/* --- add specs --- */

    def "adding valid a ripple to the timeline"() {
    	given: "a mock security service"
    	controller.springSecurityService = Mock(SpringSecurityService) {
    		1 * getCurrentUser() >> new User(username: "jeeves")
    	}

    	and: "a mock ripple service"
    	controller.rippleService = Mock(RippleService) {
    		1 * createRipple(_, _) >> new Ripple(content: "Mock ripple", user: new User(username: "jeeves"))
    	}

    	when: "add action is invoked"
    	def result = controller.add("Mock ripple")

    	then: "redirected to timeline, flash message confirms addition"
    	flash.message ==~ /Added new ripple.*/
    	response.redirectedUrl == '/ripple/timeline/jeeves'
    }

    def "attempting to add invalid ripple to timeline results in error message"() {
    	given: "a mock security service"
    	controller.springSecurityService = Mock(SpringSecurityService) {
    		1 * getCurrentUser() >> new User(username: "jeeves")
    	}

    	when: "add action is invoked with invalid ripple data"
    	def result = controller.add("")

    	then: "redirected to timeline, flash message mentions invalid content"
    	flash.message ==~ /Invalid content.*/
    	response.redirectedUrl == '/ripple/timeline/jeeves'
    }

    def "attempting to add ripple when ripple service throws error results in error message"() {
    	given: "a mock security service"
    	controller.springSecurityService = Mock(SpringSecurityService) {
    		1 * getCurrentUser() >> new User(username: "jeeves")
    	}

    	and: "a mocked error message"
    	def errorMessage = "Ripple Service Error"

    	and: "a mock ripple service"
    	controller.rippleService = Mock(RippleService) {
    		1 * createRipple(_, _) >> { throw new RippleException(message:"$errorMessage") }
    	}

    	when: "add action is invoked"
    	def result = controller.add("Mock ripple")

    	then: "redirected to timeline, flash message confirms addition"
    	flash.message == errorMessage
    	response.redirectedUrl == '/ripple/timeline/jeeves'

    }

    /* --- ajaxAdd Specs --- */

    def "adding valid a ripple to the timeline via ajax"() {
    	given: "a mock security service"
    	controller.springSecurityService = Mock(SpringSecurityService) {
    		1 * getCurrentUser() >> new User(username: "jeeves")
    	}

        and: "a mocked ripple object"
        def mockRipple = new Ripple(content: "Mock ripple #rocks", user: new User(username: "jeeves"))
    	and: "a mock ripple service"
    	controller.rippleService = Mock(RippleService) {
            1 * createRipple(_, _, _) >> mockRipple
            1 * retrieveLatestRipplesForUser(_, _) >> [mockRipple]
    	}

    	when: "add action is invoked"
    	def result = controller.addAjax("Mock ripple")

    	then: "the list of ripple entries are returned"
    	// issues with 'rip' tags being unbound will not allow use of response.xml
    	def xmlSlurper = new XmlSlurper(false, false)

    	def resultHtml = xmlSlurper.parseText(response.text)
    	resultHtml.@class ==~ /.*topicEntry.*/
        resultHtml.div[0].div[0].div[1].h4.text() ==~ /.*jeeves.*/
        //the ripple text is 'beside' the h4 tag. Not sure how to get to it
        // I'm sure a regex would do it, but can't figure out the right one
        //resultHtml.div[0].div[0].div[1].text() ==~ /.*Mock ripple.*/

    }

    def "adding a valid ripple with valid hashtags to the timeline via ajax"() {
        given: "a mock security service"
        controller.springSecurityService = Mock(SpringSecurityService) {
            1 * getCurrentUser() >> new User(username: "jeeves")
        }

        and: "a mocked ripple object"
        def mockRipple = new Ripple(content: "Mock ripple #rocks", user: new User(username: "jeeves"))
        and: "a mock ripple service"
        controller.rippleService = Mock(RippleService) {
            1 * createRipple(_, _, _) >> mockRipple
            1 * retrieveLatestRipplesForUser(_, _) >> [mockRipple]
        }

        when: "add action is invoked"
        def result = controller.addAjax("Mock ripple #rocks")

        then: "the list of ripple entries are returned"
        // issues with 'rip' tags being unbound will not allow use of response.xml
        def xmlSlurper = new XmlSlurper(false, false)

        def resultHtml = xmlSlurper.parseText(response.text)
        resultHtml.@class ==~ /.*topicEntry.*/
        resultHtml.div[0].div[0].div[1].h4.text() ==~ /.*jeeves.*/
        //resultHtml.div[0].div[0].div[1].text() ==~ /.*Mock ripple.*/

    }

}
