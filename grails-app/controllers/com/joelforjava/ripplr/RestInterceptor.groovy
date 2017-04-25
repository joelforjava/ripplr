package com.joelforjava.ripplr

import groovy.transform.CompileStatic

//@CompileStatic
class RestInterceptor {

    RestInterceptor() {
        match(controller: 'RippleRest', action: 'delete')
    }
    boolean before() {
        if (request.method in ['DELETE']) {
            render status: 405
            return false
        }
        return true
    }

    boolean after() { true }

    void afterView() {
        // no-op
    }
}
