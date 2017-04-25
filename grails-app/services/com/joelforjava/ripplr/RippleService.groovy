package com.joelforjava.ripplr

import grails.transaction.Transactional

class RippleException extends RuntimeException {
	String message
	Ripple ripple
}

@Transactional
class RippleService {

	// Need to allow for tags
	Ripple createRipple(String username, String content) {
		def user = User.findByUsername username

		if (user) {
			def ripple = new Ripple(content: content)

			user.addToRipples ripple

			if (!ripple.validate() || !user.save(flush: true)) {
				throw new RippleException(message: "Invalid content for ripple", ripple: ripple)
			} else {
				return ripple
			}

			// how do we handle responses?
			// are we going to create 'threaded' ripples that link ripples
			// or will the response only reference a user?
		}

		throw new RippleException(message: "Invalid user reference")
	}

	Ripple createRipple(String username, String content, List tagNames) {
		def user = User.findByUsername username
		
		if (user) {
			def ripple = new Ripple(content: content)

			if (tagNames) {
				tagNames.each { tagVal ->
					// probably need to look up tag first
					def tag = new Tag(name: "$tagVal")
					user.addToTags tag
					ripple.addToTags tag
				}
			}
			user.addToRipples ripple

			// this used to be !ripple.validate() || !user.save(flush: true)
			// can't really recall why I needed both.
			if (!user.save(flush: true)) {
				throw new RippleException(message: "Invalid content for ripple", ripple: ripple)
			} else {
				return ripple
			}

			// how do we handle responses?
			// are we going to create 'threaded' ripples that link ripples
			// or will the response only reference a user?
		}

		throw new RippleException(message: "Invalid user reference")

	}
	def deleteRipple(id) {
		def ripple = Ripple.get(id)

		if (ripple) {
			ripple.delete()
		} else {
			throw new RippleException(message: "Invalid ripple ID")
		}	
	}

	// add new methods to retrieve 'dashboard' ripples
	// user ripples are handled in user views, so no need for a get for them
	// will need something for a global dashboard that shows everyones ripples
	// likely a paginated service method

	def retrieveLatestRipplesForUser(String username, int maxLatest) {
		def user = User.findByUsername username
		def latestRipples = Ripple.findAllByUser(user, [sort: 'dateCreated', order: 'desc', max: maxLatest])
	}
}
