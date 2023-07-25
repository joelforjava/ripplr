package com.joelforjava.ripplr

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification

@Rollback
@Integration
class ResponseIntegrationSpec extends Specification {

    def "Responding to a Ripple"() {
        given: "Two existing Users"

        def gene = new User(username:'gene', passwordHash:'burger')
        gene.save(failOnError: true)
        def tina = new User(username:'tina', passwordHash:'burgers')
        tina.save(failOnError: true)

        and: "An existing Ripple by one of the users"

        def geneRipple = new Ripple(content:"I just can't get enough of the acoustics in this place! Here! What song is this?")
        gene.addToRipples(geneRipple)

        when: "The other user responds to this ripple"

        def tinaRipple = new Ripple(content:"@gene Aqua Boogie, by P-Funk?")
        tina.addToRipples(tinaRipple)
        def response = new Response(ripple: tinaRipple)
        geneRipple.addToResponses response
        gene.save(failOnError: true)
        tina.save(failOnError: true)

        then: "We see the expected Ripple as the inResponseTo property"

        response.inResponseTo == geneRipple
    }

}
