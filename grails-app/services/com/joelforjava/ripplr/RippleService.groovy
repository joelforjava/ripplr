package com.joelforjava.ripplr

import grails.gorm.transactions.Transactional
import grails.web.servlet.mvc.GrailsParameterMap


class RippleException extends RuntimeException {
	String message
	Ripple ripple
}

@Transactional
class RippleService {

	Ripple createRipple(String username, String content) {
		def user = User.findByUsername username

		if (user) {

			def ripple = new Ripple(content: content)

            def tagNames = extractHashTags content
            if (tagNames) {
                tagNames.each { tagVal ->
                    def tag = new Tag(name: "$tagVal", user: user)
                    user.addToTags tag
                    // At some point, you'll need to write your own functionality
                    // for ripple.addToTags. As-is, a new tag is ALWAYS created
                    // even if it's a pre-existing tag
                    ripple.addToTags tag
                }
            }
			user.addToRipples ripple
			user.save(flush: true)

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

    def list(GrailsParameterMap params) {
        Ripple.list(params)
    }

	def retrieveLatestRipplesForUser(String username, int maxLatest) {
		def user = User.findByUsername username
		def latestRipples = Ripple.findAllByUser(user, [sort: 'dateCreated', order: 'desc', max: maxLatest])
	}

    private List<String> extractHashTags(String content) {
        def tags = []
        if (content?.contains("#")) {
            // Regex from: https://stackoverflow.com/questions/8451846/actual-twitter-format-for-hashtags-not-your-regex-not-his-code-the-actual
            def findHashtags = (content =~ /#(\w*[0-9a-zA-Z]+\w*[0-9a-zA-Z])/)

            findHashtags.each { result ->
                if (result.size() >= 2) {
                    tags << result[1]
                }
            }
        }
        tags
    }
}
