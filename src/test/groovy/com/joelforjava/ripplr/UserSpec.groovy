package com.joelforjava.ripplr

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class UserSpec extends Specification implements DomainUnitTest<User> {


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

    void 'Two users with the same username are equal'() {
        when: 'We have two users with the same username'
        def user1 = new User(username: 'randy')
        def user2 = new User(username: 'randy')

        then: 'They are considered equal'
        user1 == user2

        and: 'They do not refer to the same object'
        !user1.is(user2)
    }

    void 'Adding two tags with the same name will result in only one tag being saved'() {
        given: 'We have a user'
        def user = new User(username: 'lars-ulrich')

        and: 'We have two tags with the same name value'
        def tag1 = new Tag(name: 'Apples', user: user)
        def tag2 = new Tag(name: 'Apples', user: user)

        when: 'We add each of them to the tag collection'
        user.addToTags tag1
        user.addToTags tag2

        then: 'The tag collection will only show one tag'
        user.tags.size() == 1
    }
}
