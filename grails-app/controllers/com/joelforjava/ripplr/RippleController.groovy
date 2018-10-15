package com.joelforjava.ripplr

import grails.plugin.springsecurity.SpringSecurityService
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus

class RippleController {

    MessageSource messageSource
	SpringSecurityService springSecurityService
	RippleService rippleService
	UserService userService

    def index() {
    	if (!params.id) {
    		response.sendError 400 // do we want to send this? what can be done to prevent it?
            return
		}

        redirect action: 'timeline', params: params

    }

    def global() {
    	def user = springSecurityService.currentUser
        def latestUsers = userService.retrieveLatestUsers 5
        [ ripples : Ripple.list(params), rippleCount : Ripple.count(), user : user, latestUsers : latestUsers ]
    }


    def timeline(String id) {

    	def user = userService.findUser id

    	if (!user) {
    		response.sendError 404
			return
    	}

        [ user : user ]
    }

    // this should evolve to include topics of those followed
    def dashboard() {
    	def user = springSecurityService.currentUser

    	render view: 'timeline', model: [ user : user ]
    }

    def save(RippleSaveCommand command) {
        if (command.hasErrors()) {
            respond command.errors, view: 'create', status: HttpStatus.BAD_REQUEST
            return
        }
        def user = springSecurityService.currentUser
        def newRipple
        def recentRipples
        try {
            newRipple = rippleService.createRipple user.username, command.content

            if (command.fromPage == 'timeline') {
                recentRipples = rippleService.retrieveLatestRipplesForUser(user.username, 10)
            } else {
                params.sort = 'dateCreated'
                params.order = 'desc'
                params.max = 10     // TODO - at some point, let the UI pass some of these params
                recentRipples = rippleService.list(params)
            }
            log.debug "recentRipples = $recentRipples"

            request.withFormat {
                form multipartForm {
                    flash.message = "Added new ripple: ${newRipple.content}" // For verification purposes. Will eventually remove
                    render template: 'topicEntry', collection: recentRipples, var: 'ripple'
                }
                '*' { respond recentRipples, [status: HttpStatus.CREATED] }
            }
        } catch(RippleException re) {
            flash.message = re.message
            respond command.errors, view: 'create', status: HttpStatus.UNPROCESSABLE_ENTITY
        }

    }

    protected void badRequest() {
        request.withFormat {
            form multipartForm {
                flash.message = messageSource.getMessage('ripple.content.invalid',
                                                        [] as Object[],
                                                        'Invalid content entered.', request.locale)
                // Where do we go from here?
            }
            '*' { render status: HttpStatus.BAD_REQUEST }
        }
    }
}
