package com.joelforjava.ripplr

class RippleController {

	def springSecurityService
	def rippleService
	def userService

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

    def add(String content) {
    	def user = springSecurityService.currentUser

    	try {
    		if (content) {
	    		def newRipple = rippleService.createRipple user.username, content
	    		flash.message = "Added new ripple: ${newRipple.content}" // For verification purposes. Will eventually remove
    		} else {
    			flash.message = "Invalid content entered."
    		}
    	} catch (RippleException te) {
    		flash.message = te.message
    	}

    	redirect action: 'timeline', id: user.username
    }

    def addAjax(String content) {
    	log.debug "Attempting to add $content Ripple via AJAX"
        // Thread.sleep(5000) // artificially slow down to test UI
    	def user = springSecurityService.currentUser

    	try {
    		if (content) {
    			log.debug "content appears valid. Attempting to create"
	    		def newRipple = rippleService.createRipple user.username, content
                // this returns the latest ripples for a specific user to be re-displayed
                // on a page. What happens when the user is on the global timeline?
                // perhaps we should only return the newly created ripple and prepend it
                // to the data on the page? It may not be 100% accurate when you have multiple
                // users posting, but it at least will retain the 'global' data, when required.
	    		def recentTopics = rippleService.retrieveLatestRipplesForUser(user.username, 10)
	    		log.debug "recentTopics = $recentTopics"
	    		render template: 'topicEntry', collection: recentTopics, var: 'ripple'
    		} else {
    			render "Invalid content entered."
    		}
    	} catch (RippleException te) {
    		render {
    			div(class:"errors", te.message)
    		}
    	}
    }
}
