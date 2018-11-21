package com.joelforjava.ripplr

import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification
import wslite.http.auth.HTTPBasicAuthorization
import wslite.rest.ContentType
import wslite.rest.RESTClient

@Ignore('Currently does not work. Intended to be replaced with a Login GebSpec')
class LoginFunctionalSpec extends Specification {

    @Shared
    def restClient = new RESTClient('http://localhost:8080/')

    def setup() {
        restClient.httpClient.sslTrustAllCerts = true
    }

    void 'Logging in via the login form'() {
        when: 'I send a GET to the ripples URL requesting JSON'
        restClient.authorization = new HTTPBasicAuthorization('jezza', 'topgear')
        def response = restClient.post(path: '/login/auth', accept: ContentType.JSON)

        then: 'I am redirected to the login URL'
        response.url
        response.url.toString() == 'http://localhost:8080/login/auth'

        // TODO - simulate a login session and get Ripples
    }
}
