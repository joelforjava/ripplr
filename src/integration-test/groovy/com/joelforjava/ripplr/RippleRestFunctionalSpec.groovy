package com.joelforjava.ripplr

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.*

import wslite.rest.*

import spock.lang.*

/**
 * See http://www.gebish.org/manual/current/ for more instructions
 */
@Ignore('Ignoring since we have to manually start the server each round of testing.')
@Integration
@Rollback
class RippleRestFunctionalSpec extends Specification {

    @Shared
    def restClient = new RESTClient('http://localhost:8080/api/')

    def setup() {
        restClient.httpClient.sslTrustAllCerts = true
    }

    void 'GET a list of Ripples as JSON'() {
        when: 'I send a GET to the ripples URL requesting JSON'
        def response = restClient.get(path: '/ripples', accept: ContentType.JSON)

        then: 'I get the expected ripples as a JSON list'
        response.json*.message.sort()[0..1] == [
                'Aliqua jerky leberkas boudin dolor meatloaf turkey tenderloin ut tri-tip irure dolore jowl mollit',
                'Bacon ipsum dolor amet duis ribeye drumstick mollit turkey meatball excepteur t-bone ut short loin short ribs turducken']
    }

    @Ignore('Currently redirects to /login due to the Spring Security configuration')
    void 'Attempting to DELETE a Ripple is not allowed'() {
        when: 'I send a GET to the ripples URL requesting JSON'
        def response = restClient.delete(path: '/ripples/1', accept: ContentType.JSON)

        then: 'I get the not allowed status'
        response.statusCode == 405

    }
}
