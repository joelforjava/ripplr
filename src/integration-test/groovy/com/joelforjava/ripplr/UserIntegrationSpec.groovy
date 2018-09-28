package com.joelforjava.ripplr

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import spock.lang.Specification

@Rollback
@Integration
class UserIntegrationSpec extends Specification {

    def "Saving a new user to the database"() {
    	given: "A new user"
    	def gene = new User(username:'gene', passwordHash:'burger')

    	when: "The user is saved"
    	gene.save()

    	then: "It saved successfully and can be found in the database"

    	gene.errors.errorCount == 0
    	gene.id != 0
    	User.get(gene.id).username == gene.username
    }

    def "Updating a saved user changes its properties"() {
    	given: "An existing user"

    	def existingUser = new User(username:'gene', passwordHash:'burger').save(failOnError: true)

    	when: "A property is changed"

    	def foundUser = User.get(existingUser.id)
    	foundUser.passwordHash = 'planted'
    	foundUser.save(failOnError: true)

    	then: "The change is reflected in the database"

    	User.get(existingUser.id).passwordHash == foundUser.passwordHash
    }

    def "Updating an existing user does not cause a new user to be created"() {
        given: "An existing user"

        def existingUser = new User(username:'robert', passwordHash:'burger').save(failOnError: true)

        when: "A property is changed"

        def foundUser = User.get(existingUser.id)
        foundUser.passwordHash = 'plantedHash'
        foundUser.save(failOnError: true)

        then: "The user count does not increase"

        User.count() == old(User.count())
    }

    def "Deleting an existing user removes it from the database"() {
    	given: "An existing user"

    	def existingUser = new User(username:'louise', passwordHash:'burger').save(failOnError: true)

    	when: "The user is deleted"

    	def foundUser = User.get(existingUser.id)
    	foundUser.delete(flush: true)

    	then: "The user is removed from the database"

    	!User.exists(foundUser.id)
    }

    def "Saving a user with invalid properties causes an error"() {
    	given: "A user which fails field validation"

    	def user = new User(username:"linda", passwordHash:'')

    	when: "The user is validated"

    	user.validate()

    	then:

    	user.hasErrors()
    	"nullable" == user.errors.getFieldError("passwordHash").code
    	null == user.errors.getFieldError("passwordHash").rejectedValue
    }

    def "Recovering from a failed save by fixing invalid properties"() {
    	given: "A user which fails field validation"

    	def user = new User(username:"linda", passwordHash:'')
    	assert user.save() == null
    	assert user.hasErrors()

    	when: "We fix the invalid properties"

    	user.passwordHash = "burgers"
    	user.validate()

    	then: "The user will save with no problems"

    	!user.hasErrors()
    	user.save() != null
    }

    def "Ensure a user can follow other users"() {
    	given: "A set of baseline users"

    	def bob = new User(username: 'bob', passwordHash: 'burgers').save()
    	def gene = new User(username: 'gene', passwordHash: 'keyboard').save()
    	def tina = new User(username: 'tina', passwordHash: 'jimmyjr').save()

    	when: "Bob follows Gene and Tina, Tina follows Gene, and Gene follows Bob"

    	bob.addToFollowing(gene)
    	bob.addToFollowing(tina)

    	tina.addToFollowing(gene)

    	gene.addToFollowing(bob)

    	then: "Follower counts should increase accordingly"

    	2 == bob.following.size()
    	1 == tina.following.size()
    	1 == gene.following.size()

    }

    def "Ensure a user can unfollow currently followed users"() {
        given: "A set of baseline users following each other"

        def bob = new User(username: 'bob', passwordHash: 'burgers').save()
        def gene = new User(username: 'gene', passwordHash: 'keyboard').save()

        bob.addToFollowing(gene)
        gene.addToFollowing(bob)

        when: "A user unfollows the other user"

        bob.removeFromFollowing gene

        then: "The removed user is no longer is the other's following collection"

        bob.following.size() == old(bob.following.size()) - 1
        bob.following.size() == 0

    }

    def "Ensure a user can block other users"() {
    	given: "A set of baseline users"

    	def bob = new User(username: 'bob', passwordHash: 'burgers').save()
    	def gene = new User(username: 'gene', passwordHash: 'keyboard').save()
    	def tina = new User(username: 'tina', passwordHash: 'jimmyjr').save()

    	when: "Bob blocks Gene and Tina, Tina blocks Gene, and Gene blocks Bob"

    	bob.addToBlocking(gene)
    	bob.addToBlocking(tina)

    	tina.addToBlocking(gene)

    	gene.addToBlocking(bob)

    	then: "Blocking counts should increase accordingly"

    	2 == bob.blocking.size()
    	1 == tina.blocking.size()
    	1 == gene.blocking.size()
    	
    }

	def "Saving a user and profile at the same time"() {
		given: "A new user with profile "
		def gene = new User(username:'gene', passwordHash:'burger', profile: new Profile(fullName:"Full Name", email: "gene@bobsburgers.com"))

		when: "The user is saved"
		gene.save()

		then: "It saved successfully and can be found in the database"

		gene.errors.errorCount == 0
		gene.id != 0
		User.get(gene.id).username == gene.username
	}

    void 'Adding two tags with the same name will result in only one tag being saved'() {
        given: 'We have a user'
        def user = new User(username: 'lars-ulrich', passwordHash: 'guiestz').save(failOnError: true)

        and: 'We have two tags with the same name value'
        def tag1 = new Tag(name: 'Apples', user: user)
        def tag2 = new Tag(name: 'Apples', user: user)

        when: 'We add each of them to the tag collection'
        user.addToTags tag1
        user.addToTags tag2

        then: 'The tag collection will only show one tag'
        user.tags.size() == 1
        user.tags[0].name == tag1.name
    }

}
