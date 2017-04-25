package com.joelforjava.ripplr

import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(Tag)
class TagSpec extends Specification {

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
}
