package com.joelforjava.ripplr

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification

@Rollback
@Integration
class TagIntegrationSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    def "tags can be added to a user"() {
    	given: "a user"

    	def sterling = new User(username:'archer', passwordHash:'guest').save()

    	and: "a set of tags"

    	def tagLana = new Tag(name: 'lana')
    	def tagZone = new Tag(name: 'danger zone')

    	when: "the tags are saved to the user"

    	sterling.addToTags tagLana
    	sterling.addToTags tagZone
    	sterling.save()

    	then: "the tags are retrievable from the user"

    	sterling.tags*.name.sort() == ['danger zone', 'lana']
    	2 == sterling.tags.size()
    }

	void 'Two tags with the same name but different users are not equal'() {
		when: 'We have two users'
		def user1 = new User(username: 'james-hetfield', passwordHash: 'guest').save()
		def user2 = new User(username: 'kirk-hammett', passwordHash: 'guest').save()

		and: 'We have a single tag with the same name value for each user'
		def tag1 = new Tag(name: 'Guitar', user: user1)
		def tag2 = new Tag(name: 'Guitar', user: user2)

		then: 'They are not considered equal'
		tag1 != tag2
	}

}
