package com.joelforjava.ripplr


import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(RestInterceptor)
class RestInterceptorSpec extends Specification {

    void "Test rest interceptor matching"() {
        when:"A request matches the interceptor"
        withRequest(controller:"RippleRest", action: 'delete')

        then:"The interceptor does match"
        interceptor.doesMatch()

        when:"A request does not the interceptor"
        withRequest(controller:"RippleRest", action: 'index')

        then:"The interceptor does not match"
        !interceptor.doesMatch()
    }
}
