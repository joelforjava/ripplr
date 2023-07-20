package com.joelforjava.ripplr

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification

@Rollback
@Integration
class RippleIntegrationSpec extends Specification {

    def setup() {
    	// if searchable service is used, uncomment
    	//searchableService.stopMirroring()

    	// Research searchability
    }

    def "Adding ripples to a user links the ripple to the user"() {

    	given: "A brand new user"

    	def gene = new User(username:'gene', passwordHash:'burger')
    	gene.save(failOnError: true)

    	when: "Several ripples are added to the user"
    	
    	gene.addToRipples(new Ripple(content:"I just can't get enough of the acousitcs in this place! Here! What song is this?"))
    	gene.addToRipples(new Ripple(content:"You should know, when you hold hands with me, you're holding hands with everything I've ever eaten."))
    	gene.addToRipples(new Ripple(content:"My life is more difficult than anyone else's on the planet, and yes I'm including starving children, so don't ask!"))
    	gene.addToRipples(new Ripple(content:"He gave us his magic, and then he disappeared. Just like Toad the Wet Sprocket."))
    	gene.addToRipples(new Ripple(content:"Great. Now my candy tastes like guilt."))

    	then: "The user has a list of ripples attached"

    	5 == User.get(gene.id).ripples.size()
    }

    def "Ripples with no content causes errors during validation"() {
    	given: "A brand new ripple"

    	def ripple = new Ripple()

    	when: "The ripple is validated"

    	ripple.validate()

    	then:

    	ripple.hasErrors()
    	"nullable" == ripple.errors.getFieldError("content").code
    	null == ripple.errors.getFieldError("content").rejectedValue
    }

    def "Ripples with no user cannot be saved"() {
    	given: "A brand new ripple"

    	def ripple = new Ripple(content:"Ripple one")

    	when: "The ripple is saved"

    	ripple.save()

    	then:

    	ripple.hasErrors()
    	"nullable" == ripple.errors.getFieldError("user").code
    	null == ripple.errors.getFieldError("user").rejectedValue
    }

    def "Ripples linked to a user can be retrieved"() {
    	given: "A user with several posts"

    	def user = new User(username:'gene', passwordHash:'burger')
    	user.addToRipples(new Ripple(content:"1 Ripple"))
    	user.addToRipples(new Ripple(content:"2 Ripple"))
    	user.addToRipples(new Ripple(content:"3 Ripple"))
    	user.addToRipples(new Ripple(content:"4 Ripple"))
    	user.save(failOnError: true)

    	when: "The user is retrieved via id"

    	def foundUser = User.get user.id
    	def sortedRipples = foundUser.ripples.collect { it.content }.sort()

    	then: "The ripples are retrieved along with the user"

    	sortedRipples == ['1 Ripple', '2 Ripple', '3 Ripple', '4 Ripple']
    }

    def "Ripples can be tagged with at least one tag"() {
    	given: "A user with a set of tags"

    	def tina = new User(username:'tina', passwordHash:'burgers')
    	def tagJimmyJr = new Tag(name:"jimmy junior")
    	def tagTinaProbs = new Tag(name:"tina problems")

    	tina.addToTags tagJimmyJr
    	tina.addToTags tagTinaProbs

    	tina.save(failOnError: true)

    	when: "The user tags three ripples"

    	def jjRipple = new Ripple(content:"Jimmy Junior should win class president")
    	tina.addToRipples jjRipple
    	jjRipple.addToTags tagJimmyJr

    	def tpRipple = new Ripple(content:"Tina Problems ripple")
    	tina.addToRipples tpRipple
    	tpRipple.addToTags tagTinaProbs

    	def bothRipple = new Ripple(content:"Jimmy Junior doesn't notice me")
    	tina.addToRipples bothRipple
    	bothRipple.addToTags tagJimmyJr
    	bothRipple.addToTags tagTinaProbs

    	then: "The tags are available via the ripples"

    	tina.tags*.name.sort() == ["jimmy junior", "tina problems"]
    	1 == jjRipple.tags.size()
    	1 == tpRipple.tags.size()
    	2 == bothRipple.tags.size()
    }

}
