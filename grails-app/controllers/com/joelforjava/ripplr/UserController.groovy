package com.joelforjava.ripplr

import grails.plugin.springsecurity.SpringSecurityService
import groovy.transform.CompileDynamic
import groovy.util.logging.Slf4j
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus

@Slf4j
class UserController {

    MessageSource messageSource
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

        // TODO - at some point, we will want to add a 'private' indicator to the user or profile
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

    def registration() {
        if (springSecurityService.loggedIn) {
            flash.message = 'You are already a registered user!'
            redirect uri: '/'
        }
    }

    /**
     * Performs user registration
     * @param urc a valid UserRegisterCommand object
     */
    def save(UserRegisterCommand urc) {
        withForm {
        	if (urc.hasErrors()) {
        		render view: 'registration', model: [ user : urc ]
                return
        	}

            def user = userService.save(urc)
            if (user.hasErrors()) {
                render view: 'registration', model: [ user : user ]
                return
            }

            flash.message = 'Welcome aboard!'
            redirect uri: '/'
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
    // Also TODO - actually make update do some updating. And make sure one user can't update another!
    def updateProfile(UserUpdateCommand uuc) {
    	withForm {

            if (!uuc.username) {
                notFound()
                return
            }

        	if (uuc.hasErrors()) {
        		respond uuc.errors, [view: "update", model: [ user : uuc ] ]
                return
        	}

            def user = userService.update(uuc)
            if (user == null) {
                notFound()
                return
            }

            if (user.hasErrors()) {
                respond user.errors, [view: 'update', model: [ user : user ]]
                return
            }

            // TODO - this really shouldn't send 404. What's the best response?
            Profile profile = profileService.updateProfile(user.id, uuc.profile, true)
            if (profile == null) {
                notFound()
                return
            }

            flash.message = "Changes Saved."
            redirect uri: "/"   // Maybe go back to 'update'?
    	}.invalidToken {
            invalidToken()
    	}
    }

    def follow(String usernameToFollow) {
        log.debug "now entering follow with username $usernameToFollow"
//        Thread.sleep(5000) // artificially slow down to test UI
        try {
            def user = springSecurityService.currentUser

            log.debug "current logged in user is ${user.username}"

            def success = userService.addToFollowing user.username, usernameToFollow
            if (!success) {
                render {
                    div(class:"errors", user.errors)
                }
                return
            }

            render "Successfully added $usernameToFollow"
        } catch (e) {
            log.error "An error! ${e}"
            render {
                div(class:"errors", e.message)
            }
        }
        log.debug "now exiting follow"
    }

    def unfollow(String usernameToUnfollow) {
        log.debug "now entering unfollow with username $usernameToUnfollow"
//        Thread.sleep(5000) // artificially slow down to test UI
        try {
            def user = springSecurityService.currentUser

            log.debug "current logged in user is ${user.username}"

            def success = userService.removeFromFollowing user.username, usernameToUnfollow
            if (!success) {
                render {
                    div(class:"errors", user.errors)
                }
                return
            }

            render "Successfully unfollowed $usernameToUnfollow"
        } catch (e) {
            log.error "An error! ${e}"
            render {
                div(class:"errors", e.message)
            }
        }
        log.debug "now exiting unfollow"
    }

    def block(String usernameToBlock) {
//        Thread.sleep(5000) // artificially slow down to test UI
        try {
            def user = springSecurityService.currentUser

            log.debug "current logged in user is ${user.username}"

            def success = userService.addToBlocking user.username, usernameToBlock
            if (!success) {
                render {
                    div(class:"errors", user.errors)
                }
                return
            }

            render "Successfully blocked $usernameToBlock"
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

    @CompileDynamic
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                String profileLabel = messageSource.getMessage('profile.label',
                                                                [] as Object[], 'Profile', request.locale)
                flash.message = messageSource.getMessage('default.not.found.message',
                                                          [profileLabel, params.id] as Object[],
                                                          'Profile not found', request.locale)
                redirect(uri: '/', method: 'GET')
            }
            '*' { render status: HttpStatus.NOT_FOUND }
        }
    }

}