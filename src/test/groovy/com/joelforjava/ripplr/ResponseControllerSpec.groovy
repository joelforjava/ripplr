package com.joelforjava.ripplr

import com.joelforjava.ripplr.ResponseController
import grails.plugin.springsecurity.SpringSecurityService
import grails.testing.gorm.DataTest
import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification

class ResponseControllerSpec extends Specification implements ControllerUnitTest<ResponseController>,
                                                              DataTest, DomainDataFactory, CommandObjectDataFactory {

    def setupSpec() {
        mockDomains(User, Ripple, Response)
    }

    void "saving a valid Response results in recent Ripples being returned"() {
        given: 'An existing Ripple'
        def existing = validRipple()
        existing.save(failOnError: true)

        and: 'New Response Content'
        def content = 'New response to Ripple'

        and: 'A Ripple to represent the Response Content'
        def ripple = validRipple()

        and: 'A properly mocked security service'
        controller.springSecurityService = Mock(SpringSecurityService) {
            1 * getCurrentUser() >> existing.user
        }

        and: 'A properly mocked rippleService'
        controller.rippleService = Mock(RippleService) {
            1 * createRipple(*_) >> ripple
            1 * list(*_) >> [ripple]
        }

        and:
        response.reset()
        request.contentType = FORM_CONTENT_TYPE
        request.method = 'POST'

        when: 'The save action is invoked'
        controller.save(existing.id, content)

        then: "the list of ripple entries are returned"
        status == 200
        flash.message ==~ /.*Added new ripple.*/

        and: 'We get the HTML as expected'
        // issues with 'rip' tags being unbound will not allow use of response.xml
        def xmlSlurper = new XmlSlurper(false, false)
        def resultHtml = xmlSlurper.parseText(response.text)
        resultHtml.div[0].@class ==~ /.*topicEntry.*/
        resultHtml.div[0].div[0].div[0].div[1].h4.text() ==~ /.*${ripple.user.username}.*/
    }
}
