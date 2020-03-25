package com.joelforjava.ripplr

import grails.plugin.springsecurity.SpringSecurityService
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus

class ResponseController {

    MessageSource messageSource
    SpringSecurityService springSecurityService
    RippleService rippleService
    UserService userService

    def index() { }

    def save(int inResponseTo, String content) {
        def user = springSecurityService.currentUser

        // A lot of this was taken from the RippleController. Should probably find a way to refactor common logic
        // Also TODO: Should a Ripple be aware that it is a response/re-post/etc.?
        try {
            // TODO: Create service method to create a new Response
            def newRipple = rippleService.createRipple user.username, content
            def original = Ripple.findById inResponseTo
            def response = new Response(inResponseTo: original, ripple: newRipple)
            response.save(flush: true, failOnError: true)
            params.sort = 'dateCreated'
            params.order = 'desc'
            params.max = 10
            def recentRipples = rippleService.list(params)

            request.withFormat {
                // TODO - determine the best template. Just using a copy of the standard Ripple _topicEntry for now
                form multipartForm {
                    flash.message = "Added new ripple: ${newRipple.content}" // For verification purposes. Will eventually remove
                    render template: 'topicEntry', collection: recentRipples, var: 'ripple'
                }
                // TODO - what would an appropriate response payload be here?
                //        For now, just use most recently posted Ripples
                '*' { respond recentRipples, [status: HttpStatus.CREATED] }
            }
        } catch(RippleException re) {
            flash.message = re.message
            respond command.errors, view: 'create', status: HttpStatus.UNPROCESSABLE_ENTITY
        }
    }
}
