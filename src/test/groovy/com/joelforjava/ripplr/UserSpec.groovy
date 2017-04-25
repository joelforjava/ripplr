package com.joelforjava.ripplr

import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(User)
class UserSpec extends Specification {


    void 'toString returns more detail'() {
        when: 'We have a new user'
        def user = new User(username: 'lars-ulrich', id: 70)

        then: 'toString returns more detail'
        user.toString() ==~ /User lars-ulrich \(id.*/
    }

    void "displayString returns the username for display purposes"() {
        when: 'We have a new user'
        def user = new User(username: 'james-hetfield')

        then: 'The displayString will be the user name'
        user.displayString == user.username
    }
}
