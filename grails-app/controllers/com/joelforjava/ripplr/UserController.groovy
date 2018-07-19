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

        try {
            def currentUser;
            def user = userService.retrieveUser(id)
            boolean isLoggedInFollowing = false
            if (user) {
                if (springSecurityService.isLoggedIn()) {
                    log.debug "A user is logged in"
                    currentUser = springSecurityService.currentUser
                    log.debug "The current user is ${currentUser.username}"
                    if (user.blocking?.contains(currentUser)) {
                        log.debug "${user.username} is blocking ${currentUser.username}"
                        response.sendError 404
                        return [] // prevent from 'falling through' to the return with profile data
                    }
                    if (currentUser.following?.contains(user)) {
                        isLoggedInFollowing = true
                    }
                }
                log.debug "Finished dealing with logged-in scenario"
                def followedBy = userService.getFollowedByForUser user.username
                return [profile: user.profile, followedBy: followedBy, currentLoggedInUser: currentUser, loggedInIsFollowing: isLoggedInFollowing]
            } else {
                response.sendError 404
            }
        } catch (ue) {
            response.sendError 404   
        }
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
        	} else {
                def user = userService.createUserAndProfile(urc)
                if (user.hasErrors()) {
                    render view: "registration", model: [ user : user ]
                    return
                }

                flash.message = "Welcome aboard!"
                redirect uri: "/"
        	}
        }.invalidToken {
            invalidToken()
        }
    }

    def update() {
    	def user = springSecurityService.currentUser
    	if (!user) {
    		response.sendError 404
    	} else {
    		[ user : user ]
    	}
    }

    def updateProfile(UserUpdateCommand uuc) {
    	withForm {
        	if (uuc.hasErrors()) {
        		render view: "update", model: [ user : uuc ]
        	} else {
                // TODO - make this the first route to remove exceptions, if possible
                def user
                try {
                    user = userService.retrieveUser(uuc.username)
                    if (uuc.passwordDirty) {
                       user = userService.saveUser(user.id, uuc.username, uuc.password)
                    } else if (uuc.usernameDirty) {
                        userService.updateUsername(user.id, uuc.username)
                    } // otherwise, no need to save user
                      // admin page will be used for locking/expiring accounts

                    profileService.updateProfile(user.id, uuc.profile, true)
                    flash.message = "Changes Saved."
                    redirect uri: "/"   // Maybe go back to 'update'?
                } catch (ue) {
                    return [ user : user , error : ue ]
                }
        	}
    	}.invalidToken {
            invalidToken()
    	}
    }

    def image() {
        if (request.method == "POST") {
            def user = springSecurityService.currentUser
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