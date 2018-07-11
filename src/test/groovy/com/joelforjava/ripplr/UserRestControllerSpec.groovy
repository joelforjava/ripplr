package com.joelforjava.ripplr

import grails.plugin.springsecurity.SpringSecurityService
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Ignore
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(UserRestController)
@Mock([User, UserRole, Profile])
class UserRestControllerSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    /* --- follow Specs --- */

    def "follow shows success when user successfully follows another user"() {
        given: "two existing users that do not follow each other"

        def users = createUsersForUnfollowingTests(false)

        def existingUser = users[0]
        def userToBeLoggedIn = users[1]

        and: "a mocked user service to show success"
        def mockUserService = Mock(UserService) {
            1 * addToFollowing(_, _) >> true
        }
        controller.userService = mockUserService

        and: "a mocked security service"
        def mockSecurityService = Stub(SpringSecurityService.class)
        mockSecurityService.currentUser >> userToBeLoggedIn
        controller.springSecurityService = mockSecurityService

        when: "we call follow action"

        controller.follow existingUser.username

        then: "we see a success message"
        response.text ==~ /.*Successfully added.*/
    }

    def "follow shows error when userService does not successfully add user to following collection"() {
        given: "two existing users that do not follow each other"

        def users = createUsersForUnfollowingTests(false)

        def existingUser = users[0]
        def userToBeLoggedIn = users[1]

        and: "a mocked user service to show failure"
        def mockUserService = Mock(UserService) {
            1 * addToFollowing(_, _) >> false
        }
        controller.userService = mockUserService

        and: "a mocked security service"
        def mockSecurityService = Stub(SpringSecurityService.class)
        mockSecurityService.currentUser >> userToBeLoggedIn
        controller.springSecurityService = mockSecurityService

        when: "we call follow action"

        controller.follow existingUser.username

        then: "we see an error message"
        response.text ==~ /.*errors.*/
    }

    def "follow shows error when userService throws an exception"() {
        given: "two existing users that do not follow each other"

        def users = createUsersForUnfollowingTests(false)

        def existingUser = users[0]
        def userToBeLoggedIn = users[1]

        and: "a mocked user service to throw an exception"
        def mockUserService = Mock(UserService) {
            1 * addToFollowing(_, _) >> { throw new UserException(message: 'exception!') }
        }
        controller.userService = mockUserService

        and: "a mocked security service"
        def mockSecurityService = Stub(SpringSecurityService.class)
        mockSecurityService.currentUser >> userToBeLoggedIn
        controller.springSecurityService = mockSecurityService

        when: "we call follow action"

        controller.follow existingUser.username

        then: "we see an error message"
        response.text ==~ /.*errors.*/
    }

    /* --- unfollow Specs --- */

    def "unfollow shows success when user successfully unfollows another user"() {
        given: "two existing users where one follows the other"

        def users = createUsersForUnfollowingTests(true)

        def existingUser = users[0]
        def userToBeLoggedIn = users[1]

        and: "a mocked user service to show success"
        def mockUserService = Mock(UserService) {
            1 * removeFromFollowing(_, _) >> true
        }
        controller.userService = mockUserService

        and: "a mocked security service"
        def mockSecurityService = Stub(SpringSecurityService.class)
        mockSecurityService.currentUser >> userToBeLoggedIn
        controller.springSecurityService = mockSecurityService

        when: "we call unfollow action"

        controller.unfollow existingUser.username

        then: "we see a success message"
        response.text ==~ /.*Successfully unfollowed.*/
    }

    def "unfollow shows error when userService does not successfully remove user from following collection"() {
        given: "two existing users where one follows the other"

        def users = createUsersForUnfollowingTests(true)

        def existingUser = users[0]
        def userToBeLoggedIn = users[1]

        and: "a mocked user service to show failure"
        def mockUserService = Mock(UserService) {
            1 * removeFromFollowing(_, _) >> false
        }
        controller.userService = mockUserService

        and: "a mocked security service"
        def mockSecurityService = Stub(SpringSecurityService.class)
        mockSecurityService.currentUser >> userToBeLoggedIn
        controller.springSecurityService = mockSecurityService

        when: "we call unfollow action"

        controller.unfollow existingUser.username

        then: "we see an error message"
        response.text ==~ /.*errors.*/
    }

    def "unfollow shows error when userService throws an exception"() {
        given: "two existing users where one follows the other"

        def users = createUsersForUnfollowingTests(true)

        def existingUser = users[0]
        def userToBeLoggedIn = users[1]

        and: "a mocked user service to throw an exception"
        def mockUserService = Mock(UserService) {
            1 * removeFromFollowing(_, _) >> { throw new UserException(message: 'exception!') }
        }
        controller.userService = mockUserService

        and: "a mocked security service"
        def mockSecurityService = Stub(SpringSecurityService.class)
        mockSecurityService.currentUser >> userToBeLoggedIn
        controller.springSecurityService = mockSecurityService

        when: "we call unfollow action"

        controller.unfollow existingUser.username

        then: "we see an error message"
        response.text ==~ /.*errors.*/
    }

    /* --- block Specs --- */

    def "block shows success when user successfully unfollows another user"() {
        given: "two existing users where one follows the other"

        def users = createUsersForUnfollowingTests(false)

        def existingUser = users[0]
        def userToBeLoggedIn = users[1]

        and: "a mocked user service to show success"
        def mockUserService = Mock(UserService) {
            1 * addToBlocking(_, _) >> true
        }
        controller.userService = mockUserService

        and: "a mocked security service"
        def mockSecurityService = Stub(SpringSecurityService.class)
        mockSecurityService.currentUser >> userToBeLoggedIn
        controller.springSecurityService = mockSecurityService

        when: "we call ajaxBlock action"

        controller.block existingUser.username

        then: "we see a success message"
        response.text ==~ /.*Successfully blocked.*/
    }

    /* ---- helper methods ---- */
    private createUsersForUnfollowingTests(addFollowing) {
        def existingUser = new User(username:'gene', passwordHash:'burger').save(failOnError: true)
        existingUser.profile = new Profile(fullName: 'gene belcher', email: 'gene@bobs.com', user: existingUser)
        existingUser.save(flush: true)

        def userToBeLoggedIn = new User(username:'lana', passwordHash:'testpasswd').save(failOnError: true)
        userToBeLoggedIn.profile = new Profile(fullName: 'lana kane', email: 'lana@isis.com', user: userToBeLoggedIn)
        if (addFollowing) {
            userToBeLoggedIn.addToFollowing existingUser
        }
        userToBeLoggedIn.save(flush: true)

        [existingUser, userToBeLoggedIn]
    }

}
