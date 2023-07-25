package com.joelforjava.ripplr


import grails.testing.web.interceptor.InterceptorUnitTest
import spock.lang.Specification

class RestInterceptorSpec extends Specification implements InterceptorUnitTest<RestInterceptor> {

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
