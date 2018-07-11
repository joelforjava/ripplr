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
class UserControllerSpec extends Specification {

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
        def mockUserService = Mock(UserService)
        1 * mockUserService.createUserAndProfile(*_) >> new User(username:"mocked", passwordHash:"hashmocked",
                                                                 profile: new Profile(fullName:"Mocked Name", email:"mock@mocking.com"))
        controller.userService = mockUserService

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
        def mockUserService = Mock(UserService)
        0 * mockUserService.createUserAndProfile(_)
        controller.userService = mockUserService

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

    def "Updating user and profile via update action with command object"() {
    	given: "An existing user with an existing profile"
    	def existingUser = new User(username:'gene', passwordHash:'burger').save(failOnError: true)
        def existingProfile = new Profile(fullName: 'gene belcher', email: 'gene@bobs.com')
        existingUser.profile = existingProfile
        existingUser.save(flush: true)

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
        def mockUserService = Mock(UserService) {
            1 * retrieveUser(_) >> existingUser
            1 * saveUser(_, _, _) >> new User(username: urc.username, passwordHash: urc.password)
        }
        controller.userService = mockUserService

        and: "a mock profile service"
        def mockProfileService = Mock(ProfileService)
        1 * mockProfileService.saveProfile(_, _, _, _, _, _, _, _, _, _) >> new Profile(fullName:"Mocked Users", email: urc.profile.email)
        controller.profileService = mockProfileService

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
        def existingUser = new User(username:'lisaturtle', passwordHash:'burger').save(failOnError: true)
        def existingProfile = new Profile(fullName: 'screech powers', email: 'screech@sanchez.com')
        existingUser.profile = existingProfile
        existingUser.save(flush: true)

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
        def mockUserService = Mock(UserService) {
            1 * retrieveUser(_) >> existingUser
            1 * updateUsername(_, _) >> new User(username: urc.username, passwordHash: existingUser.passwordHash)
        }
        controller.userService = mockUserService

        and: "a mock profile service"
        def mockProfileService = Mock(ProfileService)
        1 * mockProfileService.saveProfile(_, _, _, _, _, _, _, _, _, _) >> new Profile(fullName:"Mocked Users", email: urc.profile.email)
        controller.profileService = mockProfileService

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
        def existingUser = new User(username:'louisebelcher', passwordHash:'burger').save(failOnError: true)
        def existingProfile = new Profile(fullName: 'screech powers', email: 'lou@bobs.com')
        existingUser.profile = existingProfile
        existingUser.save(flush: true)

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
        def mockUserService = Mock(UserService) {
            1 * retrieveUser(_) >> existingUser
        }
        controller.userService = mockUserService

        and: "a mock profile service"
        def mockProfileService = Mock(ProfileService)
        1 * mockProfileService.saveProfile(_, _, _, _, _, _, _, _, _, _) >> new Profile(fullName:"Mocked Users", email: urc.profile.email)
        controller.profileService = mockProfileService

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

    def "Updating user with invalid command object returns to the update view for fixing errors"() {
        given: "an invalid command object"

        def uuc = mockCommandObject UserUpdateCommand
        uuc.profile = mockCommandObject ProfileCommand
        !uuc.validate()

        and: "we have an existing user"
        def user = new User(username: "carol", passwordHash:"notcarollouise")

        and: "we have the form token set"
        def tokenHolder = SynchronizerTokensHolder.store(session)
        params[SynchronizerTokensHolder.TOKEN_URI] = '/user/updateProfile'
        params[SynchronizerTokensHolder.TOKEN_KEY] = tokenHolder.generateToken(params[SynchronizerTokensHolder.TOKEN_URI])

        when: "the new update action is invoked"
        controller.updateProfile uuc

        then: "we are redirected to the update view"

        view == "/user/update"

    }

    def "when user service throws exception during user retrieval for profile update an error occurs"() {
        given: "An existing user with an existing profile"
        def existingUser = new User(username:'louisebelcher', passwordHash:'burger').save(failOnError: true)
        def existingProfile = new Profile(fullName: 'screech powers', email: 'lou@bobs.com')
        existingUser.profile = existingProfile
        existingUser.save(flush: true)

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

        and: "a mock user service that throws an exception during user retrieval"
        def mockUserService = Mock(UserService) {
            1 * retrieveUser(_) >> { throw new UserException(message: 'exception') }
        }
        controller.userService = mockUserService

        and: "a mock profile service"
        def mockProfileService = Mock(ProfileService)
        0 * mockProfileService.saveProfile(_, _, _, _, _, _, _, _, _, _)
        controller.profileService = mockProfileService

        and: "we have the form token set"
        def tokenHolder = SynchronizerTokensHolder.store(session)
        params[SynchronizerTokensHolder.TOKEN_URI] = '/user/updateProfile'
        params[SynchronizerTokensHolder.TOKEN_KEY] = tokenHolder.generateToken(params[SynchronizerTokensHolder.TOKEN_URI])

        when: "the new update action is invoked"
        def model = controller.updateProfile urc

        then: "we receive an error in the response map"

        model.error.message == 'exception' // set above
        
    }

    def "calling update action on a logged in user returns a user's details in command object"() {
        given: "an existing user with a profile"
        def user = new User(username:'louise', passwordHash:'pinkhat').save(failOnError: true)
        user.profile = new Profile(fullName: 'louise belcher', email: 'louise@bobsburgers.io', about: 'not sharing that', 
                                    homepage: 'http://louisebelcher.me', twitterProfile: 'http://twitter.com/louise', 
                                    facebookProfile: 'http://www.facebook.com', country: 'United States', timezone: 'Zone')
        user.save(flush: true)

        and: "a mock security service"
        controller.springSecurityService = Mock(SpringSecurityService) {
            1 * getCurrentUser() >> user
        }

        when: "we call update"

        def model = controller.update()

        then: "we receive a command object with the user's profile details"

        model.user.profile.fullName == user.profile.fullName
        model.user.profile.email == user.profile.email
        model.user.username == user.username


    }

    /* --- Profile Specs --- */

    // This will actually need to change and make the profile
    // unviewable if user isn't logged in? -- good luck with that
    def "profile action returns profile for valid user when there is no one logged in"() {
        given: "an existing user with profile"
        def existingUser = new User(username:'gene', passwordHash:'burger').save(failOnError: true)
        def existingProfile = new Profile(fullName: 'gene belcher', email: 'gene@bobs.com')
        existingUser.profile = existingProfile
        existingUser.save(flush: true)

        and: "a mock user service"
        def mockUserService = Mock(UserService) {
            1 * retrieveUser(_) >> existingUser
        }
        controller.userService = mockUserService

        and: "a mock security service"
        controller.springSecurityService = Mock(SpringSecurityService) {
            1 * isLoggedIn() >> false
        }

        when: "the profile action is called"

        def model = controller.profile 'gene'

        then: "we receive the profile for viewing"

        //view == '/user/profile'
        model.profile.fullName == existingProfile.fullName
        model.profile.email == existingProfile.email

    }

    def "profile action returns profile for valid user when there is a user logged in"() {
        given: "an existing user with profile"
        def existingUser = new User(username:'gene', passwordHash:'burger').save(failOnError: true)
        def existingProfile = new Profile(fullName: 'gene belcher', email: 'gene@bobs.com')
        existingUser.profile = existingProfile
        existingUser.save(flush: true)

        and: "a logged in user"
        def loggedInUser = new User(username:'sterling', passwordHash:'duchess').save(failOnError: true)

        and: "a mock user service"
        def mockUserService = Mock(UserService) {
            1 * retrieveUser(_) >> existingUser
        }
        controller.userService = mockUserService


        and: "a mock security service"
        controller.springSecurityService = Mock(SpringSecurityService) {
            1 * isLoggedIn() >> true
            1 * getCurrentUser() >> loggedInUser
        }

        when: "the profile action is called"

        def model = controller.profile 'gene'

        then: "we receive the profile for viewing"

        //view == '/user/profile'
        model.profile.fullName == existingProfile.fullName
        model.profile.email == existingProfile.email

    }

    def "profile action responds with error when profile is requested for a user that is blocking logged-in user"() {
        given: "two users where the first user blocks the second"
        def user1 = new User(username:'linda', passwordHash:'burger').save(failOnError: true)
        user1.profile = new Profile(fullName:'linda belcher', email:'linda@burgers.com')
        user1.save()
        def user2 = new User(username:'lindaimpersonator', passwordHash:'catfish').save(failOnError: true)
        user2.profile = new Profile(fullName:'linda belcher', email:'linda@catfish.com')
        user2.save(flush: true)
        user1.addToBlocking user2
        user1.save(flush: true)

        and: "a mock user service"
        controller.userService = Mock(UserService) {
            1 * retrieveUser(user1.username) >> user1
        }

        and: "a mock security service"
        controller.springSecurityService = Mock(SpringSecurityService) {
            1 * isLoggedIn() >> true
            1 * getCurrentUser() >> user2
        }

        when: "the profile action is called"

        def model = controller.profile 'linda'

        then: "we receive a 404 error"
        response.status == 404
        model == [] // currently returns empty list until I refactor
    }

    def "profile action sends error when user service returns null"() {
        given: "a mock user service that returns null"
        def mockUserService = Mock(UserService) {
            1 * retrieveUser(_) >> null
        }
        controller.userService = mockUserService

        when: "we call profile action with invalid username"

        // name irrelevant since service is set up to return null
        controller.profile 'not_a_real_name'

        then: "we receive an error in the response"

        response.status == 404
    }

    def "profile action sends error when user not found"() {
        given: "a mock user service that returns null"
        def mockUserService = Mock(UserService) {
            1 * retrieveUser(_) >> { throw new UserException(message: 'exception!') }
        }
        controller.userService = mockUserService

        when: "we call profile action with invalid username"

        // name irrelevant since service is set up to return null
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
        def mockUserService = Mock(UserService) {
            1 * addToFollowing(_, _) >> true
        }
        controller.userService = mockUserService

        and: "a mocked security service"
        def mockSecurityService = Stub(SpringSecurityService.class)
        mockSecurityService.currentUser >> userToBeLoggedIn
        controller.springSecurityService = mockSecurityService

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
        def mockUserService = Mock(UserService) {
            1 * addToFollowing(_, _) >> false
        }
        controller.userService = mockUserService

        and: "a mocked security service"
        def mockSecurityService = Stub(SpringSecurityService.class)
        mockSecurityService.currentUser >> userToBeLoggedIn
        controller.springSecurityService = mockSecurityService

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
        def mockUserService = Mock(UserService) {
            1 * addToFollowing(_, _) >> { throw new UserException(message: 'exception!') }
        }
        controller.userService = mockUserService

        and: "a mocked security service"
        def mockSecurityService = Stub(SpringSecurityService.class)
        mockSecurityService.currentUser >> userToBeLoggedIn
        controller.springSecurityService = mockSecurityService

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
        def mockUserService = Mock(UserService) {
            1 * removeFromFollowing(_, _) >> true
        }
        controller.userService = mockUserService

        and: "a mocked security service"
        def mockSecurityService = Stub(SpringSecurityService.class)
        mockSecurityService.currentUser >> userToBeLoggedIn
        controller.springSecurityService = mockSecurityService

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
        def mockUserService = Mock(UserService) {
            1 * removeFromFollowing(_, _) >> false
        }
        controller.userService = mockUserService

        and: "a mocked security service"
        def mockSecurityService = Stub(SpringSecurityService.class)
        mockSecurityService.currentUser >> userToBeLoggedIn
        controller.springSecurityService = mockSecurityService

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
        def mockUserService = Mock(UserService) {
            1 * removeFromFollowing(_, _) >> { throw new UserException(message: 'exception!') }
        }
        controller.userService = mockUserService

        and: "a mocked security service"
        def mockSecurityService = Stub(SpringSecurityService.class)
        mockSecurityService.currentUser >> userToBeLoggedIn
        controller.springSecurityService = mockSecurityService

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
        def mockUserService = Mock(UserService) {
            1 * addToBlocking(_, _) >> true
        }
        controller.userService = mockUserService

        and: "a mocked security service"
        def mockSecurityService = Stub(SpringSecurityService.class)
        mockSecurityService.currentUser >> userToBeLoggedIn
        controller.springSecurityService = mockSecurityService

        when: "we call ajaxBlock action"

        controller.ajaxBlock existingUser.username

        then: "we see a success message"
        response.text ==~ /.*Successfully blocked.*/
    }

    /* ---- helper methods ---- */
    private def createUsersForUnfollowingTests(addFollowing) {
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

//    private def mockCommandObject(Class clazz) {
//        clazz.newInstance()
//    }

}
