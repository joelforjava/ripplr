package com.joelforjava.ripplr

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
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
}
