package com.joelforjava.ripplr

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import spock.lang.Specification

@Rollback
@Integration
class RepostIntegrationSpec extends Specification {

    def "Re-posting a Ripple"() {
        given: "Two existing Users"

        def louise = new User(username:'louise', passwordHash:'burgers')
        louise.save(failOnError: true)
        def gene = new User(username:'gene', passwordHash:'burger')
        gene.save(failOnError: true)

        and: "An existing Ripple by one of the users"

        def geneRipple = new Ripple(content:"Wow. I should exercise.")
        gene.addToRipples(geneRipple)

        when: "The other user wants to re-post the Ripple"

        def reRipple = new Repost(original: geneRipple)
        // TODO - is this really how it should work? Wouldn't it be nice to add it to one
        //        or the other and have it propagate?
        geneRipple.addToReposts(reRipple)
        louise.addToReposts(reRipple)
        gene.save(failOnError: true)
        louise.save(failOnError: true)

        then: "The repost count is as expected"

        reRipple.original == geneRipple
        reRipple.user == louise

        louise.reposts.size() == 1
        geneRipple.reposts.size() == 1

    }
}
