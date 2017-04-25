package com.joelforjava.ripplr

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(RippleService)
@Mock([User, Ripple])
class RippleServiceSpec extends Specification {

	def user

    def setup() {
    	user = new User(username: "Sterling", passwordHash: "HashedPasswd").save(flush: true)
    }

    def cleanup() {
    }
	
	/* createRipple _, _ tests */

    def "ripple service saves valid content to a ripple object"() {
    	given: "valid content"

    	def content = "My first ripple"

    	when: "we call createRipple on the ripple service"

    	service.createRipple user.username, content

    	then: "a ripple is created"

    	Ripple.count() == old(Ripple.count()) + 1
    	user.ripples.size() == 1
    }

    def "ripple service throws exception when invalid username is used"() {
    	given: "valid content"

    	def content = "Hey, another ripple!"

    	and: "an invalid username"

    	def invalidUsername = "invalid_"

    	when: "we call createRipple on the ripple service"

    	service.createRipple invalidUsername, content

    	then: "an exception is thrown"

    	thrown RippleException
    }

    def "ripple service throws exception when invalid content is used"() {
    	given: "invalid content"

    	def invalidContent = null

    	when: "we call createRipple on the ripple service"

    	service.createRipple user.username, invalidContent

    	then: "an exception is thrown"

    	thrown RippleException
    }

    def "ripple service throws exception when invalid content and invalid username is used"() {
    	given: "invalid content"

    	def invalidContent = null

    	and: "an invalid username"

    	def invalidUsername = "invalid_"

    	when: "we call createRipple on the ripple service"

    	service.createRipple invalidUsername, invalidContent

    	then: "an exception is thrown"

    	thrown RippleException
    }

    def "ripple service can delete an existing ripple"() {
    	given: "an existing ripple"

    	def ripple = new Ripple(content: "new ripple")
    	user.addToRipples ripple
    	user.save(flush: true)

    	when: "we call deleteRipple on the ripple service"

    	service.deleteRipple ripple.id

    	then: "the ripple is deleted"

    	Ripple.count() == old(Ripple.count()) - 1

    }

    def "ripple service throws exception when an attempt to delete a non-existent ripple is made"() {
    	when: "we call deleteRipple with an invalid ID"

    	service.deleteRipple(-1)

    	then: "an exception is thrown"

    	thrown RippleException
    }
	
}
