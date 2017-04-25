package com.joelforjava.ripplr

import grails.rest.RestfulController
import org.springframework.http.HttpStatus

class RippleRestController {

    static responseFormats = ['json']

    static allowedMethods = []

    def springSecurityService
    def rippleService

    def index() {
        respond Ripple.list()
    }

    def save(RippleDetail ripple) {

        if (!ripple.hasErrors()) {
            def user =  springSecurityService.currentUser
            def newRipple = rippleService.createRipple user.username, ripple.message
            respond newRipple, status: 201
        } else {
            respond ripple
        }
    }

    def delete(Ripple ripple) {
        render status: HttpStatus.METHOD_NOT_ALLOWED
    }
}
