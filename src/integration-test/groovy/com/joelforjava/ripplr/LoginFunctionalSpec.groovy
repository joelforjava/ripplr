package com.joelforjava.ripplr

import com.joelforjava.ripplr.page.LoginPage
import com.joelforjava.ripplr.page.TimelinePage
import com.joelforjava.ripplr.util.ServerUtils as AppServer
import geb.spock.GebSpec
import grails.testing.mixin.integration.Integration
import spock.lang.Requires

@Integration
@Requires({ AppServer.isOnline() })
class LoginFunctionalSpec extends GebSpec {

    void 'Logging in directly from the login URL will redirect you to the timeline'() {
        given: 'We navigate to the login page'
        to LoginPage

        when: 'We login'
        login('jezza', 'topgear')

        then: 'We are at the timeline page'
        at TimelinePage

    }
}
