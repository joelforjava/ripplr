package com.joelforjava.ripplr

import grails.testing.gorm.DataTest
import grails.testing.web.controllers.ControllerUnitTest
import org.grails.web.servlet.mvc.SynchronizerTokensHolder

import grails.plugin.springsecurity.SpringSecurityService
import spock.lang.Specification

class UserControllerSpec extends Specification implements ControllerUnitTest<UserController>, DataTest,
        CommandObjectDataFactory, DomainDataFactory {

    def setupSpec() {
        mockDomains(User, UserRole, Profile)
    }
    /* --- Register Specs --- */

    def "Invoking register action with command object"() {
    	given: "A properly configured command object"
    	def urc = validUserRegisterCommandObject()

    	and: "it has been validated"
//    	urc.validate()

        and: "a mock user service"
        controller.userService = Mock(UserService) {
            1 * save(_ as UserRegisterCommand) >> validUserAndProfile()
        }

    	and: "we have the form token set"
        def tokenHolder = SynchronizerTokensHolder.store(session)
        params[SynchronizerTokensHolder.TOKEN_URI] = '/user/save'
        params[SynchronizerTokensHolder.TOKEN_KEY] = tokenHolder.generateToken(params[SynchronizerTokensHolder.TOKEN_URI])

        when: "the new save action is invoked"
        controller.save urc

        then: "the user is registered and the browser is redirected"
        !urc.hasErrors()
        response.redirectedUrl == "/"

    }

    def "Invoking register action without token results in an error"() {
    	given: "A properly configured command object"
        def urc = validUserRegisterCommandObject()

    	and: "it has been validated"
//    	urc.validate()

        and: "a mock user service"
        controller.userService = Mock(UserService) {
            0 * save(_ as UserRegisterCommand)
        }

        when: "the new save action is invoked"
        controller.save urc

        then: "the user is not registered and an error message is received"
        !urc.hasErrors()
        response.text == "Invalid or duplicate form submission"

    }

    def "Registering user with invalid command object returns to the registration view for fixing errors"() {
        given: "an invalid command object"

        def urc = mockCommandObject UserRegisterCommand
        urc.profile = mockCommandObject ProfileRegisterCommand // need to do this to prevent NPE
        !urc.validate()

        and: "we have the form token set"
        def tokenHolder = SynchronizerTokensHolder.store(session)
        params[SynchronizerTokensHolder.TOKEN_URI] = '/user/save'
        params[SynchronizerTokensHolder.TOKEN_KEY] = tokenHolder.generateToken(params[SynchronizerTokensHolder.TOKEN_URI])

        when: "the save action is invoked"
        controller.save urc

        then: "we are redirected to the registration view"

        view == "/user/registration"

    }

    def 'Calling registration when a user is already logged in will redirect to home'() {
        given: 'We have mocked the springSecurityService'
        controller.springSecurityService = Mock(SpringSecurityService) {
            1 * isLoggedIn() >> true
        }

        when: 'We attempt to view the registration page'
        controller.registration()

        then: 'We are redirected to home'
        response.redirectedUrl == "/"

        and: 'Receive a message stating we are already registered'
        flash.message
    }

    /* --- Update Specs --- */

    // TODO - rethink and refactor these tests to make sure they are LOGICAL! (See UserServiceSpec ignored test)
    def "Calling updateProfile when given a valid command object and when the services return expected domain objects results in no errors returned"() {
    	given: "A property configured command object"
            def urc = validUserUpdateCommandObject()

        and: "a mock user service"
            controller.userService = Mock(UserService) {
                1 * update(_ as UserUpdateCommand) >> new User(username: urc.username, passwordHash: urc.password)
            }

        and: "a mock profile service"
            controller.profileService = Mock(ProfileService) {
                1 * updateProfile(*_) >> new Profile(fullName:"Mocked Users", email: urc.profile.email)
            }

    	and: "we have the form token set"
            def tokenHolder = SynchronizerTokensHolder.store(session)
            params[SynchronizerTokensHolder.TOKEN_URI] = '/user/updateProfile'
            params[SynchronizerTokensHolder.TOKEN_KEY] =
                    tokenHolder.generateToken(params[SynchronizerTokensHolder.TOKEN_URI])

        when: "the new update action is invoked"
            controller.updateProfile urc

        then: "the user is updated and the browser is redirected"
            !urc.hasErrors()
            response.redirectedUrl == '/'
            flash.message == 'Changes Saved.'
    }

    def 'Updating user without a form token results in an error'() {
        given: 'A properly configured command object'
            def urc = validUserUpdateCommandObject()

        when: 'the new save action is invoked'
            controller.updateProfile urc

        then: 'the user is not registered and an error message is received'
            response.text == 'Invalid or duplicate form submission'
    }

    def 'Calling update with no username set in the command object results in a NOT FOUND error'() {
        given: 'an empty command object'
            def uuc = emptyUserUpdateCommandObject()

        and: 'we have the form token set'
            def tokenHolder = SynchronizerTokensHolder.store(session)
            params[SynchronizerTokensHolder.TOKEN_URI] = '/user/updateProfile'
            params[SynchronizerTokensHolder.TOKEN_KEY] =
                    tokenHolder.generateToken(params[SynchronizerTokensHolder.TOKEN_URI])

        when: 'the new update action is invoked'
            controller.updateProfile uuc

        then: 'we receive a NOT FOUND error'
            status == 404

    }

    def 'Calling update with an invalid command object results in being sent back to the update page'() {
        given: 'an invalid command object'
            def uuc = Spy(UserUpdateCommand) {
                1 * hasErrors() >> true
            }
            uuc.username = 'obviously-real-username'

        and: 'we have the form token set'
            def tokenHolder = SynchronizerTokensHolder.store(session)
            params[SynchronizerTokensHolder.TOKEN_URI] = '/user/updateProfile'
            params[SynchronizerTokensHolder.TOKEN_KEY] =
                    tokenHolder.generateToken(params[SynchronizerTokensHolder.TOKEN_URI])

        when: 'the new update action is invoked'
            controller.updateProfile uuc

        then: 'we are sent back to the update page'
            view == 'update'

        and: 'we get the command object sent back to us'
            model.user == uuc
    }

    def 'Calling update with a username that results in no user being returns results in a NOT FOUND error'() {
        given: 'a command object with a username'
            def uuc = validUserUpdateCommandObject()

        and: 'we have the form token set'
            def tokenHolder = SynchronizerTokensHolder.store(session)
            params[SynchronizerTokensHolder.TOKEN_URI] = '/user/updateProfile'
            params[SynchronizerTokensHolder.TOKEN_KEY] =
                    tokenHolder.generateToken(params[SynchronizerTokensHolder.TOKEN_URI])

        and: 'a mock user service to return null'
            controller.userService = Mock(UserService) {
                1 * update(_ as UserUpdateCommand) >> null
            }

        when: 'the new update action is invoked'
            controller.updateProfile uuc

        then: "we receive a NOT FOUND error"
            status == 404
    }

    def "calling update action on a logged in user returns a user's details in model"() {
        given: "an existing user with a profile"
            def user = new User(username:'louise', passwordHash:'pinkhat',
                    profile: new Profile(fullName: 'louise belcher', email: 'louise@bobsburgers.io',
                                        about: 'not sharing that', homepage: 'http://louisebelcher.me',
                                        twitterProfile: 'http://twitter.com/louise',
                                        facebookProfile: 'http://www.facebook.com',
                                        country: 'United States', timezone: 'Zone'))

        and: "a mock security service"
            controller.springSecurityService = Mock(SpringSecurityService) {
                1 * getCurrentUser() >> user
            }

        when: "we call update"
            def model = controller.update()

        then: "we receive a model object with the user's profile details"
            model.user.profile.fullName == user.profile.fullName
            model.user.profile.email == user.profile.email
            model.user.username == user.username
    }

    /* --- Profile Specs --- */

    // This will actually need to change and make the profile
    // unviewable if user isn't logged in? -- good luck with that
    def "profile action returns profile for valid user when there is no one logged in"() {
        given: "an existing user with profile"
        def existingUser = new User(username:'gene', passwordHash:'burger',
                profile: new Profile(fullName: 'gene belcher', email: 'gene@bobs.com'))

        and: "a mock user service"
        controller.userService = Mock(UserService) {
            1 * findUser(_ as String) >> existingUser
        }

        and: "a mock security service"
        controller.springSecurityService = Mock(SpringSecurityService) {
            1 * isLoggedIn() >> false
        }

        when: "the profile action is called"

        def model = controller.profile 'gene'

        then: "we receive the profile for viewing"

        //view == '/user/profile'
        model.profile.fullName == existingUser.profile.fullName
        model.profile.email == existingUser.profile.email

    }

    def "profile action returns profile for valid user when there is a user logged in"() {
        given: "an existing user with profile"
        def existingUser = new User(username:'gene', passwordHash:'burger',
                profile: new Profile(fullName: 'gene belcher', email: 'gene@bobs.com'))

        and: "a logged in user"
        def loggedInUser = new User(username:'sterling', passwordHash:'duchess')
                //.save(failOnError: true)

        and: "a mock user service"
        controller.userService = Mock(UserService) {
            1 * findUser(_ as String) >> existingUser
        }

        and: "a mock security service"
        controller.springSecurityService = Mock(SpringSecurityService) {
            1 * isLoggedIn() >> true
            1 * getCurrentUser() >> loggedInUser
        }

        when: "the profile action is called"

        def model = controller.profile 'gene'

        then: "we receive the profile for viewing"

        //view == '/user/profile'
        model.profile.fullName == existingUser.profile.fullName
        model.profile.email == existingUser.profile.email

    }

    def 'profile action sets loggedInIsFollowing appropriately when a profile is being followed by the viewer'() {
        given: 'an existing user with profile'
        def existingUser = new User(username:'gene', passwordHash:'burger',
                profile: new Profile(fullName: 'gene belcher', email: 'gene@bobs.com'))

        and: 'a logged in user that follows the existing user'
        def loggedInUser = new User(username:'sterling', passwordHash:'duchess')
        loggedInUser.addToFollowing existingUser

        and: 'a mock user service'
        controller.userService = Mock(UserService) {
            1 * findUser(_ as String) >> existingUser
            1 * getFollowersForUser(_ as String) >> [loggedInUser]
        }

        and: 'a mock security service'
        controller.springSecurityService = Mock(SpringSecurityService) {
            1 * isLoggedIn() >> true
            1 * getCurrentUser() >> loggedInUser
        }

        when: 'the profile action is called'
        def results = controller.profile 'gene'

        then: 'We see isLoggedInFollowing set correctly'
        results.loggedInIsFollowing

    }

    def 'profile action responds with error when profile is requested for a user that is blocking logged-in user'() {
        given: 'two users where the first user blocks the second'
        def user1 = new User(username:'linda', passwordHash:'burger',
                profile: new Profile(fullName:'linda belcher', email:'linda@burgers.com'))
        def user2 = new User(username:'lindaimpersonator', passwordHash:'catfish',
                profile: new Profile(fullName:'linda belcher', email:'linda@catfish.com'))
        user1.addToBlocking user2

        and: 'a mock user service'
        controller.userService = Mock(UserService) {
            1 * findUser(user1.username) >> user1
        }

        and: 'a mock security service where the blocked user is logged in'
        controller.springSecurityService = Mock(SpringSecurityService) {
            1 * isLoggedIn() >> true
            1 * getCurrentUser() >> user2
        }

        when: 'the profile action is called'
        def model = controller.profile 'linda'

        then: 'we receive a 404 error'
        response.status == 404
        model == [] // currently returns empty list until I refactor
    }

    def "profile action sends error when user service returns null"() {
        given: "a mock user service that returns null"
        controller.userService = Mock(UserService) {
            1 * findUser(_ as String) >> null
        }

        when: "we call profile action with invalid username"

        controller.profile 'not_a_real_name'

        then: "we receive an error in the response"

        response.status == 404
    }

    def "profile action sends error when user not found"() {
        given: "a mock user service that returns null"
        controller.userService = Mock(UserService) {
            1 * findUser(_ as String) >> null
        }

        when: "we call profile action with invalid username"

        controller.profile 'not_a_real_name'

        then: "we receive an error in the response"

        response.status == 404
    }

    /* --- follow Specs --- */

    def "ajaxFollow shows success when user successfully follows another user"() {
        given: "two existing users that do not follow each other"

        def users = createUsersForUnfollowingTests(false)

        def existingUser = users[0]
        def userToBeLoggedIn = users[1]

        and: "a mocked user service to show success"
        controller.userService = Mock(UserService) {
            1 * addToFollowing(*_) >> true
        }

        and: "a mocked security service"
        controller.springSecurityService = Stub(SpringSecurityService.class) {
            getCurrentUser() >> userToBeLoggedIn
        }

        when: "we call follow action"

        controller.follow existingUser.username

        then: "we see a success message"
        response.text ==~ /.*Successfully added.*/
    }

    def "ajaxFollow shows error when userService does not successfully add user to following collection"() {
        given: "two existing users that do not follow each other"

        def users = createUsersForUnfollowingTests(false)

        def existingUser = users[0]
        def userToBeLoggedIn = users[1]

        and: "a mocked user service to show failure"
        controller.userService = Mock(UserService) {
            1 * addToFollowing(*_) >> false
        }

        and: "a mocked security service"
        controller.springSecurityService = Stub(SpringSecurityService.class) {
            getCurrentUser() >> userToBeLoggedIn
        }

        when: "we call follow action"

        controller.follow existingUser.username

        then: "we see an error message"
        response.text ==~ /.*errors.*/
    }

    def "ajaxFollow shows error when userService throws an exception"() {
        given: "two existing users that do not follow each other"

        def users = createUsersForUnfollowingTests(false)

        def existingUser = users[0]
        def userToBeLoggedIn = users[1]

        and: "a mocked user service to throw an exception"
        controller.userService = Mock(UserService) {
            1 * addToFollowing(*_) >> { throw new UserException(message: 'exception!') }
        }

        and: "a mocked security service"
        controller.springSecurityService = Stub(SpringSecurityService.class) {
            getCurrentUser() >> userToBeLoggedIn
        }

        when: "we call follow action"

        controller.follow existingUser.username

        then: "we see an error message"
        response.text ==~ /.*errors.*/
    }

    /* --- unfollow Specs --- */

    def "ajaxUnfollow shows success when user successfully unfollows another user"() {
        given: "two existing users where one follows the other"

        def users = createUsersForUnfollowingTests(true)

        def existingUser = users[0]
        def userToBeLoggedIn = users[1]

        and: "a mocked user service to show success"
        controller.userService = Mock(UserService) {
            1 * removeFromFollowing(*_) >> true
        }

        and: "a mocked security service"
        controller.springSecurityService = Stub(SpringSecurityService.class) {
            getCurrentUser() >> userToBeLoggedIn
        }

        when: "we call unfollow action"

        controller.unfollow existingUser.username

        then: "we see a success message"
        response.text ==~ /.*Successfully unfollowed.*/
    }

    def "ajaxUnfollow shows error when userService does not successfully remove user from following collection"() {
        given: "two existing users where one follows the other"

        def users = createUsersForUnfollowingTests(true)

        def existingUser = users[0]
        def userToBeLoggedIn = users[1]

        and: "a mocked user service to show failure"
        controller.userService = Mock(UserService) {
            1 * removeFromFollowing(_ as String, _ as String) >> false
        }

        and: "a mocked security service"
        controller.springSecurityService = Stub(SpringSecurityService.class) {
            getCurrentUser() >> userToBeLoggedIn
        }

        when: "we call unfollow action"

        controller.unfollow existingUser.username

        then: "we see an error message"
        response.text ==~ /.*errors.*/
    }

    def "ajaxUnfollow shows error when userService throws an exception"() {
        given: "two existing users where one follows the other"

        def users = createUsersForUnfollowingTests(true)

        def existingUser = users[0]
        def userToBeLoggedIn = users[1]

        and: "a mocked user service to throw an exception"
        controller.userService = Mock(UserService) {
            1 * removeFromFollowing(_ as String, _ as String) >> { throw new UserException(message: 'exception!') }
        }

        and: "a mocked security service"
        controller.springSecurityService = Stub(SpringSecurityService.class) {
            getCurrentUser() >> userToBeLoggedIn
        }

        when: "we call unfollow action"

        controller.unfollow existingUser.username

        then: "we see an error message"
        response.text ==~ /.*errors.*/
    }

    /* --- block Specs --- */

    def "ajaxBlock shows success when user successfully unfollows another user"() {
        given: "two existing users where one follows the other"

        def users = createUsersForUnfollowingTests(false)

        def existingUser = users[0]
        def userToBeLoggedIn = users[1]

        and: "a mocked user service to show success"
        controller.userService = Mock(UserService) {
            1 * addToBlocking(_ as String, _ as String) >> true
        }

        and: "a mocked security service"
        controller.springSecurityService = Stub(SpringSecurityService.class) {
            getCurrentUser() >> userToBeLoggedIn
        }

        when: "we call block action"

        controller.block existingUser.username

        then: "we see a success message"
        response.text ==~ /.*Successfully blocked.*/
    }

    /* ---- helper methods ---- */
    private def createUsersForUnfollowingTests(addFollowing) {
        def existingUser = new User(username:'gene', passwordHash:'burger',
                profile: new Profile(fullName: 'gene belcher', email: 'gene@bobs.com'))

        def userToBeLoggedIn = new User(username:'lana', passwordHash:'testpasswd',
                profile: new Profile(fullName: 'lana kane', email: 'lana@isis.com'))

        if (addFollowing) {
            userToBeLoggedIn.addToFollowing existingUser
        }

        [existingUser, userToBeLoggedIn]
    }

    private def mockCommandObject(Class clazz) {
        clazz.newInstance()
    }
}
