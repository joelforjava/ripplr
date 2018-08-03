package com.joelforjava.ripplr

class UserRestController {

    def springSecurityService
    def userService

    def index() { }

    def follow(String usernameToFollow) {
        log.debug "now entering follow with username $usernameToFollow"
//        Thread.sleep(5000) // artificially slow down to test UI
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
        log.debug "now exiting follow"
    }

    def unfollow(String usernameToUnfollow) {
        log.debug "now entering unfollow with username $usernameToUnfollow"
//        Thread.sleep(5000) // artificially slow down to test UI
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
        log.debug "now exiting unfollow"
    }

    def block(String usernameToBlock) {
//        Thread.sleep(5000) // artificially slow down to test UI
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
}
