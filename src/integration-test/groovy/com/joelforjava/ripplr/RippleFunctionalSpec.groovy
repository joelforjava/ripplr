package com.joelforjava.ripplr

import com.joelforjava.ripplr.util.ServerUtils
import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Requires
import spock.lang.Shared
import spock.lang.Specification
import wslite.rest.ContentType
import wslite.rest.RESTClient

@Integration
@Rollback
@Requires({ ServerUtils.isOnline() })
class RippleFunctionalSpec extends Specification {

    @Shared
    def restClient = new RESTClient('http://localhost:8080/')

    def setup() {
        restClient.httpClient.sslTrustAllCerts = true
    }

    void 'GET a list of Ripples as JSON'() {
        when: 'I send a GET to the ripples URL requesting JSON'
        def response = restClient.get(path: '/ripples', accept: ContentType.JSON)

        then: 'I am redirected to the login URL'
        response.url
        response.url.toString() == 'http://localhost:8080/login/auth'

        // TODO - simulate a login session and get Ripples
    }
}
