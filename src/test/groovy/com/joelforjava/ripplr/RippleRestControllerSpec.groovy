package com.joelforjava.ripplr

import grails.plugin.springsecurity.SpringSecurityService
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Ignore
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(RippleRestController)
@Mock([User, Ripple])
class RippleRestControllerSpec extends Specification {

    def setupSpec() {
        defineBeans {
            springSecurityService(SpringSecurityService)
        }
    }

    def cleanup() {
    }


    void 'GET a list of Ripples as JSON'() {

        given: 'A set of ripples'
        initUsersAndRipples()

        when: 'I invoke the index action'
        controller.index()

        then: 'I get the expected ripples as a JSON list'
        response.json*.content.sort() == [
                "always be closing",
                "better luck next time",
                "could you not?",
                "dirty deeds done dirt cheap" ]
    }

    @Ignore
    void 'POST a ripple using JSON'() {
        given: 'An existing user'
        def username = initUsersAndRipples()

        and: 'A new message to post'
        def message = 'Engaging new content'

        and: 'A mocked springSecurityService'
        def mockSecurityService = Mock(SpringSecurityService) {
            getCurrentUser() >> [username: username]
        }
        controller.springSecurityService = mockSecurityService

        def mockRippleService = Mock(RippleService) {
            1 * createRipple(username, message) >> new Ripple(content: message)
        }
        controller.rippleService = mockRippleService

        when: 'I invoke the save action with a JSON payload'
        request.json = '{"message":"' + message + '"}'
        controller.save()

        then: "I get a 201 JSON response with the ID of the new post"
        response.status == 201
        response.json.content == message
    }

    private initUsersAndRipples() {
        def bob = new User(username: 'bob_belcher', passwordHash: 'clever_passwd')

        bob.addToRipples( new Ripple(content: 'always be closing'))
        bob.addToRipples( new Ripple(content: 'better luck next time'))
        bob.save(failOnError: true)

        def louise = new User(username: 'louise_b', passwordHash: 'clever_passwd')

        louise.addToRipples( new Ripple(content: 'could you not?'))
        louise.addToRipples( new Ripple(content: 'dirty deeds done dirt cheap'))
        louise.save(failOnError: true, flush: true)

        return bob.username
    }
}
