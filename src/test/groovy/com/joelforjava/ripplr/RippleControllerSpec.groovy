package com.joelforjava.ripplr

import grails.plugin.springsecurity.SpringSecurityService
import grails.testing.gorm.DataTest
import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification

class RippleControllerSpec extends Specification implements ControllerUnitTest<RippleController>,
                                                            DataTest, DomainDataFactory, CommandObjectDataFactory {

    def setupSpec() {
		mockDomains(User, Ripple)
    }

	/* --- global specs --- */
	def "getting the global timeline view"() {
		given: "a user with posts"

		def user = validUserWithUsername('bob_belcher')

		user.addToRipples( new Ripple(content: 'a very important ripple'))
		user.addToRipples( new Ripple(content: 'this one is not as important'))
		user.save(failOnError: true)

		and: "A logged in user"
		def loggedInUser = validUserWithUsername('gene_belcher')

		and: "a mocked user service"
		controller.userService = Mock(UserService) {
			1 * retrieveLatestUsers(_) >> [user]
		}

		and: "a mocked security service"
		controller.springSecurityService = Mock(SpringSecurityService) {
            1 * getCurrentUser() >> loggedInUser
		}

		when: 'we invoke the global action'
		def model = controller.global()

		then: 'we get the expected results'
		model.user.username == loggedInUser.username
		model.rippleCount == 2

	}

	/* --- timeline specs --- */

    def "getting timeline of a valid user"() {
    	given: "a user with posts"

        def user = validUserWithUsername('bob_belcher')

    	user.addToRipples( new Ripple(content: 'a very important ripple'))
    	user.addToRipples( new Ripple(content: 'this one is not as important'))
    	user.save(failOnError: true)

    	and: "a username parameter"
    	params.id = 'bob_belcher'

    	and: "a mocked user service"
    	controller.userService = Mock(UserService) {
    		1 * findUser(_) >> user
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
    		1 * findUser(_) >> null
    	}

    	when: "the timeline action is invoked"
    	controller.timeline()

    	then: "a 404 error is received"

    	response.status == 404
    }

	/* --- dashboard specs --- */

    def "getting dashboard of a valid user"() {
    	given: "a user with posts"

        def user = validUserWithUsername('bob_belcher')

    	user.addToRipples( new Ripple(content: 'a very important ripple'))
    	user.addToRipples( new Ripple(content: 'this one is not as important'))
    	user.save(failOnError: true)

    	and: "a mocked security service"
        controller.springSecurityService = Mock(SpringSecurityService) {
            1 * getCurrentUser() >> user
        }

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
        def user = validUserWithUsername('bob_belcher')
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

    /* --- save Specs --- */

	void 'Saving a valid ripple via a form'() {
        given: 'A valid save command object'
        def cmd = validRippleSaveCommand()

        and: 'A mocked ripple object'
        def ripple = validRipple()

        and: 'A properly mocked security service'
        controller.springSecurityService = Mock(SpringSecurityService) {
            1 * getCurrentUser() >> ripple.user
        }

        and: 'A properly mocked rippleService'
        controller.rippleService = Mock(RippleService) {
            1 * createRipple(*_) >> ripple
            1 * retrieveLatestRipplesForUser(*_) >> [ripple]
        }

        and:
        response.reset()
        request.contentType = FORM_CONTENT_TYPE
        request.method = 'POST'

        when: 'The save action is invoked'
        controller.save(cmd)

        then: "the list of ripple entries are returned"
        status == 200
        flash.message ==~ /.*Added new ripple.*/

        and: 'We get the HTML as expected'
        // issues with 'rip' tags being unbound will not allow use of response.xml
        def xmlSlurper = new XmlSlurper(false, false)
        def resultHtml = xmlSlurper.parseText(response.text)
        resultHtml.@class ==~ /.*topicEntry.*/
        resultHtml.div[0].div[0].div[1].h4.text() ==~ /.*${ripple.user.username}.*/
    }

    void 'Saving a null ripple via a form is not permitted'() {
        given: 'An empty command object'
        def cmd = emptyRippleSaveCommand()

        and: 'We have validated it'
        cmd.validate()

        when: 'We call save'
        controller.save(cmd)

        then: 'We get the expected results'
        status == 400
        view == 'create'
    }

    void 'Saving a ripple when the service causes an exception results in an error message'() {
        given: 'A valid save command object'
        def cmd = validRippleSaveCommand()

        and: 'A mocked ripple object'
        def ripple = validRipple()

        and: 'A properly mocked security service'
        controller.springSecurityService = Mock(SpringSecurityService) {
            1 * getCurrentUser() >> ripple.user
        }

        and: 'A properly mocked rippleService'
        controller.rippleService = Mock(RippleService) {
            1 * createRipple(*_) >> { throw new RippleException(message:  "Invalid content") }
        }

        and:
        response.reset()
        request.contentType = FORM_CONTENT_TYPE
        request.method = 'POST'

        when: 'The save action is invoked'
        controller.save(cmd)

        then: "we get a message from the exception"
        status == 422
        flash.message ==~ /.*Invalid.*/
    }

    void 'Saving a valid ripple via JSON'() {
        given: 'A valid save command object'
        def cmd = validRippleSaveCommand()

        and: 'A mocked ripple object'
        def ripple = validRipple()

        and: 'A properly mocked security service'
        controller.springSecurityService = Mock(SpringSecurityService) {
            1 * getCurrentUser() >> ripple.user
        }

        and: 'A properly mocked rippleService'
        controller.rippleService = Mock(RippleService) {
            1 * createRipple(*_) >> ripple
            1 * list(*_) >> [ripple]
        }

        and:
        response.reset()
        request.contentType = JSON_CONTENT_TYPE
        request.method = 'POST'
        request.json = '{"content":"' + cmd.content + '", "fromPage": "global" }'

        when: 'The save action is invoked'
        controller.save()

        then: 'We get the expected return status'
        status == 201

    }

}
