package com.joelforjava.ripplr

import org.grails.web.servlet.mvc.SynchronizerTokensHolder

import grails.plugin.springsecurity.SpringSecurityService
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Unroll

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
// TODO: DO NOT UPDATE to the Testing Support Framework until you figure out WTF you get the 'is not a domain' error on the command objects!
@TestFor(UserController)
@Mock([User, UserRole, Profile])
class UserControllerSpec extends Specification implements DomainDataFactory {

    /* --- Register Specs --- */

    @Unroll
    def "Registering with command object for #username validates correctly"() {

        given: "A mocked register command object"
        def urc = mockCommandObject UserRegisterCommand

        and: "a set of initial values"
        urc.username = username
        urc.password = password
        urc.passwordVerify = passwordVerify
        urc.profile = mockCommandObject ProfileCommand
        urc.profile.fullName = fullName
        urc.profile.email = email
        // Other items are optional

        when: "the validator is invoked"
        def isValid = urc.validate()

        then: "the appropriate fields are flagged as errors"
        isValid == anticipatedValid
        urc.errors.getFieldError(fieldInError)?.code == errorCode

        where:
        username   | password   | passwordVerify    | fullName		| email					| anticipatedValid  | fieldInError      | errorCode
        "kirk_ham" | "password" | "IDontMatch"      | "Kirk Hammett"| "kirk@metallica.com"	| false             | "passwordVerify"  | "validator.invalid"
        "james_het"| "password" | "password"        | "Jim Hetfield"| "james@metallica.com" | true				| null              | null
        "dave_must"| "guitars"  | "guitars"         | "Dave"        | "dave@megadeth.com"   | true				| null              | null
        "dr"       | "password" | "password"        | "Doc"			| "doc@derp.com"		| false             | "username"        | "size.toosmall"
        "jeeves"   | "password" | "password"        | ""			| "jeeves@metallica.com"| false				| "profile"		    | "validator.invalid"
        "kirk_ham" | "password" | "password"		| "Kirk Hammett"| ""					| false				| "profile"			| "validator.invalid"
        "james_het"| "guest12"	| "guest12"			| "Jim"			| "NotAnEmailAddress"	| false				| "profile"	        | "validator.invalid"
    }

    @Unroll
    def "Updating with command object for #username validates correctly"() {

        given: "A mocked update command object"
        def uuc = mockCommandObject UserUpdateCommand

        and: "a set of initial values"
        uuc.username = username
        uuc.password = password
        uuc.passwordVerify = passwordVerify
        uuc.profile = mockCommandObject ProfileCommand
        uuc.profile.fullName = fullName
        uuc.profile.email = email
        uuc.usernameDirty = false
        uuc.passwordDirty = false
        // Other items are optional

        when: "the validator is invoked"
        def isValid = uuc.validate()

        then: "the appropriate fields are flagged as errors"
        println uuc.errors
        isValid == anticipatedValid
        uuc.errors.getFieldError(fieldInError)?.code == errorCode

        where:
        username   | password   | passwordVerify    | fullName      | email                 | anticipatedValid  | fieldInError      | errorCode
        "kirk_ham" | "password" | "IDontMatch"      | "Kirk Hammett"| "kirk@metallica.com"  | false             | "passwordVerify"  | "validator.invalid"
        "james_het"| "password" | "password"        | "Jim Hetfield"| "james@metallica.com" | true              | null              | null
        "dave_must"| "guitars"  | "guitars"         | "Dave"        | "dave@megadeth.com"   | true              | null              | null
        "dr"       | "password" | "password"        | "Doc"         | "doc@derp.com"        | false             | "username"        | "size.toosmall"
        "jeeves"   | "password" | "password"        | ""            | "jeeves@metallica.com"| false             | "profile"         | "validator.invalid"
        "kirk_ham" | "password" | "password"        | "Kirk Hammett"| ""                    | false             | "profile"         | "validator.invalid"
        "j4mes_h3t"| "guest12"  | "guest12"         | "Jim"         | "NotAnEmailAddress"   | false             | "profile"         | "validator.invalid"
    }

    @Unroll
    def "Common Profile command object for #fullName validates correctly"() {

        given: "A mocked register command object"
        def pc = mockCommandObject ProfileCommand

        and: "a set of initial values"
        pc.fullName = fullName
        pc.email = email
        // Other items are optional

        when: "the validator is invoked"
        def isValid = pc.validate()

        then: "the appropriate fields are flagged as errors"
        isValid == anticipatedValid
        pc.errors.getFieldError(fieldInError)?.code == errorCode

        where:
        fullName		| email					| anticipatedValid  | fieldInError      | errorCode
        "Jim Hetfield"  | "james@metallica.com" | true				| null              | null
        "Dave"          | "dave@megadeth.com"   | true				| null              | null
        ""			    | "jeeves@metallica.com"| false				| "fullName"		| "blank"
        "Kirk Hammett"  | ""					| false				| "email"			| "blank"
        "Jim"			| "NotAnEmailAddress"	| false				| "email"	        | "email.invalid"
    }

    def "Invoking register action with command object"() {
    	given: "A properly configured command object"
    	def urc = mockCommandObject UserRegisterCommand
    	urc.with {
    		username = "sterling"
    		password = "duchess"
    		passwordVerify = "duchess"
            profile = mockCommandObject ProfileCommand
    		profile.fullName = "Sterling Archer"
    		profile.email = "sterling@isis.com"
    	}

    	and: "it has been validated"
    	urc.validate()

        and: "a mock user service"
        controller.userService = Mock(UserService) {
            1 * createUserAndProfile(*_) >> validUserAndProfile()
        }

    	and: "we have the form token set"
        def tokenHolder = SynchronizerTokensHolder.store(session)
        params[SynchronizerTokensHolder.TOKEN_URI] = '/user/register'
        params[SynchronizerTokensHolder.TOKEN_KEY] = tokenHolder.generateToken(params[SynchronizerTokensHolder.TOKEN_URI])

        when: "the new register action is invoked"
        controller.register urc

        then: "the user is registered and the browser is redirected"
        !urc.hasErrors()
        response.redirectedUrl == "/"

    }

    def "Invoking register action without token results in an error"() {
    	given: "A properly configured command object"
    	def urc = mockCommandObject UserRegisterCommand
    	urc.with {
    		username = "sterling"
    		password = "duchess"
    		passwordVerify = "duchess"
            profile = mockCommandObject ProfileCommand
    		profile.fullName = "Sterling Archer"
    		profile.email = "sterling@isis.com"
    	}

    	and: "it has been validated"
    	urc.validate()

        and: "a mock user service"
        controller.userService = Mock(UserService) {
            0 * createUserAndProfile(_)
        }

        when: "the new register action is invoked"
        controller.register urc

        then: "the user is not registered and an error message is received"
        !urc.hasErrors()
        response.text == "Invalid or duplicate form submission"

    }

    def "Registering user with invalid command object returns to the registration view for fixing errors"() {
        given: "an invalid command object"

        def urc = mockCommandObject UserRegisterCommand
        urc.profile = mockCommandObject ProfileCommand // need to do this to prevent NPE
        !urc.validate()

        and: "we have the form token set"
        def tokenHolder = SynchronizerTokensHolder.store(session)
        params[SynchronizerTokensHolder.TOKEN_URI] = '/user/register'
        params[SynchronizerTokensHolder.TOKEN_KEY] = tokenHolder.generateToken(params[SynchronizerTokensHolder.TOKEN_URI])

        when: "the register action is invoked"
        controller.register urc

        then: "we are redirected to the registration view"

        view == "/user/registration"

    }

    /* --- Update Specs --- */

    // TODO - rethink and refactor these tests to make sure they are LOGICAL! (See UserServiceSpec ignored test)
    def "Updating user and profile via update action with command object"() {
    	given: "An existing user with an existing profile"
    	def existingUser = new User(username:'gene', passwordHash:'burger',
                profile: new Profile(fullName: 'gene belcher', email: 'gene@bobs.com'))

    	and: "A property configured command object"
    	def urc = mockCommandObject UserUpdateCommand
    	urc.with {
    		username = "gene"
    		password = "merman"
    		passwordVerify = "merman"
            profile = mockCommandObject ProfileCommand
    		profile.fullName = "Gene Belcher"
    		profile.email = "gene@genesburgers.com"
            passwordDirty = true
            usernameDirty = false
    	}

    	and: "it has been validated"
    	urc.validate()

        and: "a mock user service"
        controller.userService = Mock(UserService) {
            1 * updateUser(_) >> new User(username: urc.username, passwordHash: urc.password)
        }

        and: "a mock profile service"
        controller.profileService = Mock(ProfileService) {
            1 * updateProfile(*_) >> new Profile(fullName:"Mocked Users", email: urc.profile.email)
        }

    	and: "we have the form token set"
        def tokenHolder = SynchronizerTokensHolder.store(session)
        params[SynchronizerTokensHolder.TOKEN_URI] = '/user/updateProfile'
        params[SynchronizerTokensHolder.TOKEN_KEY] = tokenHolder.generateToken(params[SynchronizerTokensHolder.TOKEN_URI])

        when: "the new update action is invoked"
        controller.updateProfile urc

        then: "the user is updated and the browser is redirected"
        !urc.hasErrors()
        response.redirectedUrl == "/"
        flash.message == "Changes Saved."
    }

    def "Updating username via update action with command object"() {
        given: "An existing user with an existing profile"
        def existingUser = new User(username:'lisaturtle', passwordHash:'burger',
                profile: new Profile(fullName: 'screech powers', email: 'screech@sanchez.com'))

        and: "A property configured command object"
        def urc = mockCommandObject UserUpdateCommand
        urc.with {
            username = "gene"
            password = "burger"
            passwordVerify = "burger"
            profile = mockCommandObject ProfileCommand
            profile.fullName = "Gene Belcher"
            profile.email = "gene@genesburgers.com"
            passwordDirty = false
            usernameDirty = true
        }

        and: "it has been validated"
        urc.validate()

        and: "a mock user service"
        controller.userService = Mock(UserService) {
            1 * updateUser(_) >> new User(username: urc.username, passwordHash: existingUser.passwordHash)
        }

        and: "a mock profile service"
        controller.profileService = Mock(ProfileService) {
            1 * updateProfile(*_) >> new Profile(fullName:"Mocked Users", email: urc.profile.email)
        }

        and: "we have the form token set"
        def tokenHolder = SynchronizerTokensHolder.store(session)
        params[SynchronizerTokensHolder.TOKEN_URI] = '/user/updateProfile'
        params[SynchronizerTokensHolder.TOKEN_KEY] = tokenHolder.generateToken(params[SynchronizerTokensHolder.TOKEN_URI])

        when: "the new update action is invoked"
        controller.updateProfile urc

        then: "the user is updated and the browser is redirected"
        !urc.hasErrors()
        response.redirectedUrl == "/"
        flash.message == "Changes Saved."
    }

    def "Updating only profile values via update action with command object"() {
        given: "An existing user with an existing profile"
        def existingUser = new User(username:'louisebelcher', passwordHash:'burger',
                profile: new Profile(fullName: 'screech powers', email: 'lou@bobs.com'))

        and: "A property configured command object"
        def urc = mockCommandObject UserUpdateCommand
        urc.with {
            username = existingUser.username
            password = "burger"
            passwordVerify = "burger"
            profile = mockCommandObject ProfileCommand
            profile.fullName = "Louise Belcher"
            profile.email = "louise@genesburgers.com"
            passwordDirty = false
            usernameDirty = false
        }

        and: "it has been validated"
        urc.validate()

        and: "a mock user service"
        controller.userService = Mock(UserService) {
            1 * updateUser(_) >> existingUser
        }

        and: "a mock profile service"
        controller.profileService = Mock(ProfileService) {
            1 * updateProfile(*_) >> new Profile(fullName:"Mocked Users", email: urc.profile.email)
        }

        and: "we have the form token set"
        def tokenHolder = SynchronizerTokensHolder.store(session)
        params[SynchronizerTokensHolder.TOKEN_URI] = '/user/updateProfile'
        params[SynchronizerTokensHolder.TOKEN_KEY] = tokenHolder.generateToken(params[SynchronizerTokensHolder.TOKEN_URI])

        when: "the new update action is invoked"
        controller.updateProfile urc

        then: "the user is updated and the browser is redirected"
        !urc.hasErrors()
        response.redirectedUrl == "/"
        flash.message == "Changes Saved."
    }

    def "Updating user without a form token results in an error"() {
        given: "A properly configured command object"
        def urc = mockCommandObject UserUpdateCommand
        urc.with {
            username = "sterling"
            password = "duchess"
            passwordVerify = "duchess"
            profile = mockCommandObject ProfileCommand
            profile.fullName = "Sterling Archer"
            profile.email = "sterling@isis.com"
        }

        and: "it has been validated"
        urc.validate()

        when: "the new register action is invoked"
        controller.updateProfile urc

        then: "the user is not registered and an error message is received"
        response.text == "Invalid or duplicate form submission"
    }

    def "Calling update with no username set in the command object results in a NOT FOUND error"() {
        given: "an invalid command object"

        def uuc = mockCommandObject UserUpdateCommand
        !uuc.validate()

        and: "we have the form token set"
        def tokenHolder = SynchronizerTokensHolder.store(session)
        params[SynchronizerTokensHolder.TOKEN_URI] = '/user/updateProfile'
        params[SynchronizerTokensHolder.TOKEN_KEY] = tokenHolder.generateToken(params[SynchronizerTokensHolder.TOKEN_URI])

        when: "the new update action is invoked"
        controller.updateProfile uuc

        then: "we receive a NOT FOUND error"
        status == 404

    }

    def 'Calling update with an invalid command object results in being sent back to the update page'() {
        given: 'an invalid command object'

        def uuc = mockCommandObject UserUpdateCommand
        uuc.username = 'obviously-real-username'
        !uuc.validate()

        and: 'we have the form token set'
        def tokenHolder = SynchronizerTokensHolder.store(session)
        params[SynchronizerTokensHolder.TOKEN_URI] = '/user/updateProfile'
        params[SynchronizerTokensHolder.TOKEN_KEY] = tokenHolder.generateToken(params[SynchronizerTokensHolder.TOKEN_URI])

        when: 'the new update action is invoked'
        controller.updateProfile uuc

        then: 'we are sent back to the update page'
        view == 'update'

    }

    def 'Calling update with a username that results in no user being returns results in a NOT FOUND error'() {
        given: 'a command object with a username'

        def uuc = mockCommandObject UserUpdateCommand
        uuc.with {
            username = "sterling"
            password = "duchess"
            passwordVerify = "duchess"
            profile = mockCommandObject ProfileCommand
            profile.fullName = "Sterling Archer"
            profile.email = "sterling@isis.com"
        }

        and: 'we have the form token set'
        def tokenHolder = SynchronizerTokensHolder.store(session)
        params[SynchronizerTokensHolder.TOKEN_URI] = '/user/updateProfile'
        params[SynchronizerTokensHolder.TOKEN_KEY] = tokenHolder.generateToken(params[SynchronizerTokensHolder.TOKEN_URI])

        and: 'a mock user service to return null'
        controller.userService = Mock(UserService) {
            1 * updateUser(_) >> null
        }

        when: 'the new update action is invoked'
        controller.updateProfile uuc

        then: "we receive a NOT FOUND error"
        status == 404
    }

    def "calling update action on a logged in user returns a user's details in model"() {
        given: "an existing user with a profile"
        def user = new User(username:'louise', passwordHash:'pinkhat',
                profile: new Profile(fullName: 'louise belcher', email: 'louise@bobsburgers.io', about: 'not sharing that',
                                    homepage: 'http://louisebelcher.me', twitterProfile: 'http://twitter.com/louise', 
                                    facebookProfile: 'http://www.facebook.com', country: 'United States', timezone: 'Zone'))

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
            1 * findUser(_) >> existingUser
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
            1 * findUser(_) >> existingUser
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
            1 * findUser(_) >> existingUser
            1 * getFollowedByForUser(_) >> [loggedInUser]
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
            1 * findUser(_) >> null
        }

        when: "we call profile action with invalid username"

        controller.profile 'not_a_real_name'

        then: "we receive an error in the response"

        response.status == 404
    }

    def "profile action sends error when user not found"() {
        given: "a mock user service that returns null"
        controller.userService = Mock(UserService) {
            1 * findUser(_) >> { throw new UserException(message: 'exception!') }
        }

        when: "we call profile action with invalid username"

        controller.profile 'not_a_real_name'

        then: "we receive an error in the response"

        response.status == 404
    }

    /* --- ajaxFollow Specs --- */

    def "ajaxFollow shows success when user successfully follows another user"() {
        given: "two existing users that do not follow each other"

        def users = createUsersForUnfollowingTests(false)

        def existingUser = users[0]
        def userToBeLoggedIn = users[1]

        and: "a mocked user service to show success"
        controller.userService = Mock(UserService) {
            1 * addToFollowing(_, _) >> true
        }

        and: "a mocked security service"
        controller.springSecurityService = Stub(SpringSecurityService.class) {
            getCurrentUser() >> userToBeLoggedIn
        }

        when: "we call ajaxFollow action"

        controller.ajaxFollow existingUser.username

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
            1 * addToFollowing(_, _) >> false
        }

        and: "a mocked security service"
        controller.springSecurityService = Stub(SpringSecurityService.class) {
            getCurrentUser() >> userToBeLoggedIn
        }

        when: "we call ajaxFollow action"

        controller.ajaxFollow existingUser.username

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
            1 * addToFollowing(_, _) >> { throw new UserException(message: 'exception!') }
        }

        and: "a mocked security service"
        controller.springSecurityService = Stub(SpringSecurityService.class) {
            getCurrentUser() >> userToBeLoggedIn
        }

        when: "we call ajaxFollow action"

        controller.ajaxFollow existingUser.username

        then: "we see an error message"
        response.text ==~ /.*errors.*/
    }

    /* --- ajaxUnfollow Specs --- */

    def "ajaxUnfollow shows success when user successfully unfollows another user"() {
        given: "two existing users where one follows the other"

        def users = createUsersForUnfollowingTests(true)

        def existingUser = users[0]
        def userToBeLoggedIn = users[1]

        and: "a mocked user service to show success"
        controller.userService = Mock(UserService) {
            1 * removeFromFollowing(_, _) >> true
        }

        and: "a mocked security service"
        controller.springSecurityService = Stub(SpringSecurityService.class) {
            getCurrentUser() >> userToBeLoggedIn
        }

        when: "we call ajaxUnfollow action"

        controller.ajaxUnfollow existingUser.username

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
            1 * removeFromFollowing(_, _) >> false
        }

        and: "a mocked security service"
        controller.springSecurityService = Stub(SpringSecurityService.class) {
            getCurrentUser() >> userToBeLoggedIn
        }

        when: "we call ajaxUnfollow action"

        controller.ajaxUnfollow existingUser.username

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
            1 * removeFromFollowing(_, _) >> { throw new UserException(message: 'exception!') }
        }

        and: "a mocked security service"
        controller.springSecurityService = Stub(SpringSecurityService.class) {
            getCurrentUser() >> userToBeLoggedIn
        }

        when: "we call ajaxUnfollow action"

        controller.ajaxUnfollow existingUser.username

        then: "we see an error message"
        response.text ==~ /.*errors.*/
    }

    /* --- ajaxBlock Specs --- */

    def "ajaxBlock shows success when user successfully unfollows another user"() {
        given: "two existing users where one follows the other"

        def users = createUsersForUnfollowingTests(false)

        def existingUser = users[0]
        def userToBeLoggedIn = users[1]

        and: "a mocked user service to show success"
        controller.userService = Mock(UserService) {
            1 * addToBlocking(_, _) >> true
        }

        and: "a mocked security service"
        controller.springSecurityService = Stub(SpringSecurityService.class) {
            getCurrentUser() >> userToBeLoggedIn
        }

        when: "we call ajaxBlock action"

        controller.ajaxBlock existingUser.username

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

//    private def mockCommandObject(Class clazz) {
//        clazz.newInstance()
//    }

}
