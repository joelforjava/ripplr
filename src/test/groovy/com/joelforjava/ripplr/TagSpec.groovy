package com.joelforjava.ripplr

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class TagSpec extends Specification implements DomainUnitTest<Tag> {

    void "toString returns name and ID"() {
		when: "we have a new tag"
		def tag = new Tag(name: 'awesomeTag', id: 1)
		
		then: "toString works as expected"
		assert tag.toString() ==~ /Tag awesomeTag.*/
    }
	
	void "displayString returns only the name of the tag"() {
		when: "we have a new tag"
		def tag = new Tag(name:"New tag", id: 1)
		
		then: "displayString returns only the tag name"
		assert tag.displayString == tag.name
	}

	void 'Two tags with the same name but null users are considered equal'() {
        when: 'We have two tags with the same name value'
        def tag1 = new Tag(name: 'Onions')
        def tag2 = new Tag(name: 'Onions')

        then: 'They are considered equal'
        tag1 == tag2

        and: 'They do not refer to the same object'
        !tag1.is(tag2)
    }

    void 'Two tags with the same name but different users are not equal'() {
        when: 'We have two users'
        def user1 = new User(username: 'james-hetfield')
        def user2 = new User(username: 'kirk-hammett')

        and: 'We have a single tag with the same name value for each user'
        def tag1 = new Tag(name: 'Guitar', user: user1)
        def tag2 = new Tag(name: 'Guitar', user: user2)

        then: 'They are not considered equal'
        tag1 != tag2
    }
}
