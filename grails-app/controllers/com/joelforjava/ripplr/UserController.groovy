package com.joelforjava.ripplr

import grails.plugin.springsecurity.SpringSecurityService
import groovy.util.logging.Slf4j

@Slf4j
class UserController {

    ProfileService profileService
	SpringSecurityService springSecurityService
    UserService userService

    def index() {
        redirect(action:'dashboard', controller:'ripple', params: params)
    }

    /**
     * returns the profile of any non-private user
     */
    def profile(String id) {

        def user = userService.findUser(id)
        if (!user) {
            response.sendError 404
            return
        }

        def currentUser
        boolean isLoggedInFollowing = false
        if (springSecurityService.isLoggedIn()) {
            log.debug "A user is logged in"
            currentUser = springSecurityService.currentUser
            log.debug "The current user is ${currentUser.username}"
            if (user.blocking?.contains(currentUser)) {
                log.debug "${user.username} is blocking ${currentUser.username}"
                response.sendError 404
                return [] // prevent from 'falling through' to the return with profile data
            }
            // TODO - should this be refactored into a service call?
            if (currentUser.following?.contains(user)) {
                isLoggedInFollowing = true
            }
        }
        log.debug "Finished dealing with logged-in scenario"

        def followers = userService.getFollowersForUser user.username
        return [profile: user.profile, followedBy: followers, currentLoggedInUser: currentUser, loggedInIsFollowing: isLoggedInFollowing]
    }

    def registration() { }

    /**
     * Performs user registration
     * @param urc a valid UserRegisterCommand object
     */
    def register(UserRegisterCommand urc) {
        withForm {
        	if (urc.hasErrors()) {
        		render view: "registration", model: [ user : urc ]
                return
        	}

            def user = userService.createUserAndProfile(urc)
            if (user.hasErrors()) {
                render view: "registration", model: [ user : user ]
                return
            }

            flash.message = "Welcome aboard!"
            redirect uri: "/"
        }.invalidToken {
            invalidToken()
        }
    }

    def update() {
    	def user = springSecurityService.currentUser
    	if (!user) {
    		response.sendError 404
            return
    	}
        [ user : user ]
    }

    // TODO - consider if this really should be separated out into user/profile updates
    def updateProfile(UserUpdateCommand uuc) {
    	withForm {

            if (!uuc.username) {
                response.sendError 404
                return
            }

        	if (uuc.hasErrors()) {
        		respond uuc.errors, [view: "update", model: [ user : uuc ] ]
                return
        	}

            def user = userService.updateUser(uuc)
            if (user == null) {
                response.sendError 404
                return
            }

            // TODO - this really shouldn't send 404. What's the best response?
            Profile profile = profileService.updateProfile(user.id, uuc.profile, true)
            if (profile == null) {
                response.sendError 404
                return
            }

            flash.message = "Changes Saved."
            redirect uri: "/"   // Maybe go back to 'update'?
    	}.invalidToken {
            invalidToken()
    	}
    }

    @Deprecated
    def ajaxFollow(String usernameToFollow) {
        log.debug "now entering ajaxFollow with username $usernameToFollow"
        Thread.sleep(5000) // artificially slow down to test UI
        try {
            def user = springSecurityService.currentUser

            log.debug "current logged in user is ${user.username}"

            def success = userService.addToFollowing user.username, usernameToFollow

            if (success) {
                render "Successfully added $usernameToFollow"
            } else {
                render {
                    div(class:"errors", user.errors)
                }
            }
        } catch (e) {
            log.error "An error! ${e}"
            render {
                div(class:"errors", e.message)
            }
        }
        log.debug "now exiting ajaxFollow"
    }

    @Deprecated
    def ajaxUnfollow(String usernameToUnfollow) {
        log.debug "now entering ajaxUnfollow with username $usernameToUnfollow"
        Thread.sleep(5000) // artificially slow down to test UI
        try {
            def user = springSecurityService.currentUser

            log.debug "current logged in user is ${user.username}"

            def success = userService.removeFromFollowing user.username, usernameToUnfollow

            if (success) {
                render "Successfully unfollowed $usernameToUnfollow"
            } else {
                render {
                    div(class:"errors", user.errors)
                }
            }
        } catch (e) {
            log.error "An error! ${e}"
            render {
                div(class:"errors", e.message)
            }
        }
        log.debug "now exiting ajaxUnfollow"
    }

    @Deprecated
    def ajaxBlock(String usernameToBlock) {
        Thread.sleep(5000) // artificially slow down to test UI
        try {
            def user = springSecurityService.currentUser

            log.debug "current logged in user is ${user.username}"

            def success = userService.addToBlocking user.username, usernameToBlock

            if (success) {
                render {
                    div(class:"success", "Successfully blocked $usernameToBlock")
                }
            } else {
                render {
                    div(class:"errors", user.errors)
                }
            }
        } catch (e) {
            log.error "An error! ${e}"
            render {
                div(class:"errors", e.message)
            }
        }

    }

    protected void invalidToken() {
        render 'Invalid or duplicate form submission'
    }

}