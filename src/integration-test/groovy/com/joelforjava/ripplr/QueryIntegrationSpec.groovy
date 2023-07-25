package com.joelforjava.ripplr

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification

@Rollback
@Integration
class QueryIntegrationSpec extends Specification {

    void 'Querying for tags using Criteria API with Tag class'() {
        given: 'We have a user'
        def user = new User(username: 'lars-ulrich', passwordHash: 'guiestz').save(failOnError: true)

        and: 'We have two tags with the same name value'
        def tag1 = new Tag(name: 'Apples', user: user)
        def tag2 = new Tag(name: 'Apples', user: user)

        and: 'We add each of them to the tag collection'
        user.addToTags tag1
        user.addToTags tag2

        and: 'We have saved the user'
        user.save(failOnError: true, flush: true)

        when: 'We query for the tags via the Criteria API'
        def tags = Tag.withCriteria { c ->
            eq('name', 'Apples')
        }

        then: 'We get back the expected number of tags'
        tags
        !tags.isEmpty()
        tags.size() == 1
        tags.every { it.name == 'Apples' }

    }

    void 'Querying for tags using HQL'() {
        given: 'We have a user'
        def user = new User(username: 'lars-ulrich', passwordHash: 'guiestz').save(failOnError: true)

        and: 'We have two tags with the same name value'
        def tag1 = new Tag(name: 'Apples', user: user)
        def tag2 = new Tag(name: 'Apples', user: user)

        and: 'We add each of them to the tag collection'
        user.addToTags tag1
        user.addToTags tag2

        and: 'We have saved the user'
        user.save(failOnError: true, flush: true)

        when: 'We query for the tags via the Criteria API'
        def tags = Tag.findAll("""
            from Tag as t
            where t.name = :tagName""", [tagName: 'Apples'])

        then: 'We get back the expected number of tags'
        tags
        !tags.isEmpty()
        tags.size() == 1
        tags.every { it.name == 'Apples' }

    }
}
