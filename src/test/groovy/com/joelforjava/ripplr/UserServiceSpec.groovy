package com.joelforjava.ripplr

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Ignore
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(UserService)
@Mock([User, Profile])
class UserServiceSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void 'Valid user data will allow creation of new user'() {
    	given: 'New user values'

		String username = 'james'
		String passwordHash = 'hashedpass'
		boolean accountExpired = false
		boolean accountLocked = false
		boolean passwordExpired = false

		when: 'A new user is created by the service'

		def newUser = service.createUser(username, passwordHash, accountLocked, accountExpired, passwordExpired)

		then: 'The user is returned by the service'

		newUser.username == username
		newUser.accountExpired == accountExpired
		User.count() == old(User.count()) + 1
    }

    void 'Null values for booleans in user creation will cause an exception'() {
    	given: 'New user values'

		String username = 'james'
		String passwordHash = 'hashedpass'

		when: 'the service is sent null values where booleans should be sent'

		def newUser = service.createUser(username, passwordHash, null, null, null)

		then: 'an exception is thrown because the method cannot be found'

		thrown groovy.lang.MissingMethodException

    }

    def "Calling alternate create method defaults security properties to false"() {
    	given: "New user values"

		String username = "james"
		String passwordHash = "hashedpass"

		when: "A new user is created by the service via alternate method"

		def newUser = service.createUser(username, passwordHash)

		then: "The user is returned by the service with security defaults of false"

		newUser.accountExpired == false
		newUser.accountLocked == false
		newUser.passwordExpired == false

    }

    def 'Attempting to save a user with an empty password results in an unsaved user object'() {
    	when: "An attempt is made to create a user with no password"

    	def user = service.createUser("gene", "")

    	then: "the user has errors"

    	user.hasErrors()
		"nullable" == user.errors.getFieldError("passwordHash").code
		null == user.errors.getFieldError("passwordHash").rejectedValue
    }

	@Ignore
    def 'Service can successfully create a user with profile'() {
        given: "A properly configured command object"
        def urc = new UserRegisterCommand()
        urc.with {
            username = "sterling"
            password = "duchess"
            passwordVerify = "duchess"
            profile = new ProfileCommand()
            profile.fullName = "Sterling Archer"
            profile.email = "sterling@isis.com"
        }

        when: 'We call the service to create the user and profile'
        def newUser = service.createUserAndProfile(urc)

        then: 'We get back a new user'
        !newUser.hasErrors()
        newUser.username == urc.username
        newUser.profile.fullName == urc.profile.fullName
    }

    def "User properties can be updated via save method"() {
    	given: "An existing user"

    	def user = new User(username: "james", passwordHash: "passwordhashed").save(failOnError: true)

    	when: "The user properties are updated"

    	def savedUser = service.saveUser(user.id, user.username, user.passwordHash, true, user.accountExpired, true)

    	then: "The user is updated in the database"

    	user.id == savedUser.id
    	user.accountLocked == true
    	user.passwordExpired == true
		User.count() == old(User.count())
    }

    def "User properties can be updated with an alternate save method"() {
    	given: "An existing user"

    	def user = new User(username: "james", passwordHash: "passwordhashed").save(failOnError: true)

    	when: "The user properties are updated"

    	def savedUser = service.saveUser(user.id, "Jameson", user.passwordHash)

    	then: "The user is updated in the database"

    	user.id == savedUser.id
    	user.accountLocked == savedUser.accountLocked
    	user.passwordExpired == savedUser.passwordExpired
    	user.username != old(user.username)
		User.count() == old(User.count())
    }

    def "Calling save on a user that does not exist results in error"() {
    	when: "We attempt to save a user that was not first created"

    	service.saveUser(null, "jason", "passwordhasheddude", false, false, false)

    	then: "An exception is thrown"

    	thrown UserException

    	when: "We attempt to save a user that was not first created via alternate save"

    	service.saveUser(null, "Jason", "hashedpassword")

    	then: "An exception is thrown"

    	thrown UserException
    }

    def 'Saving an existing user with an invalid passwordHash value results in an error'() {
    	given: 'An existing user'

    	def user = new User(username: 'james', passwordHash: 'passwordhashed').save(failOnError: true)

    	when: 'We attempt to save the user with an empty password'

    	def savedUser = service.saveUser(user.id, user.username, "")

		then: 'the user has errors'

		savedUser.hasErrors()
		'blank' == savedUser.errors.getFieldError('passwordHash').code
		'' == savedUser.errors.getFieldError('passwordHash').rejectedValue

    }

    def 'Saving user with an invalid username value results in an error'() {
    	given: "An existing user"

    	def user = new User(username:"gene", passwordHash:"hamburgerhamburger").save(failOnError: true)

    	when: "We attempt to save the user with an empty username"

    	def savedUser = service.saveUser(user.id, "", user.passwordHash, false, false, false)

		then: 'the user has errors'

		savedUser.hasErrors()
		'blank' == savedUser.errors.getFieldError('username').code
		'' == savedUser.errors.getFieldError('username').rejectedValue
    }

    def "Service can retrieve a user with a valid user ID"() {
    	given: "An existing user"

    	def user = new User(username: "james", passwordHash: "passwordhashed").save(failOnError: true)

    	when: "We attempt to retrieve the user via the service"

    	def retrievedUser = service.retrieveUser(user.id)

    	then: "We have a valid user object to work with"

    	retrievedUser.id == user.id
    	retrievedUser.username == user.username
    }

    def "Attempting to retrieve user with invalid ID causes an error"() {
    	when: "An attempt is made to retrieve an invalid user"

    	service.retrieveUser(-1)

    	then: "An exception is thrown"

    	thrown UserException
    }

    def "Service can retrieve a user with a valid username"() {
    	given: "An existing user"

    	def user = new User(username: "james", passwordHash: "passwordhashed").save(failOnError: true)

    	when: "We attempt to retrieve the user via the service"

    	def retrievedUser = service.retrieveUser(user.username)

    	then: "We have a valid user object to work with"

    	retrievedUser.id == user.id
    	retrievedUser.username == user.username
    }

    def "Attempting to retrieve user with an invalid username causes an error"() {
    	when: "An attempt is made to retrieve a user with an invalid username"

    	service.retrieveUser("Jambalaya")

    	then: "An exception is thrown"

    	thrown UserException
    }

    def "Service can retrieve a list of the latest users"() {
        given: "Some existing users"

        ['james', 'kirk', 'lars', 'cliff', 'dave', 
         'jason', 'robert', 'david', 'nick', 'marty', 'tom'].each { name ->
            new User(username: "$name", passwordHash: 'passwordhashed').save(failOnError: true)
         }

        when: "We attempt to retrieve the latest users via the service"

        def retrievedUsers = service.retrieveLatestUsers(numUsers)

        then: "We have a valid user object to work with"

        retrievedUsers != null
        retrievedUsers.size() == expectedCount

        where:
        numUsers | expectedCount
        0        | 5
        5        | 5
        10       | 10
        15       | 11

    }

    def "Attempting to retrieve latest users when no users exist results in an error"() {
        when: "An attempt is made to retrieve the latest users when none exist"

        service.retrieveLatestUsers(5)

        then: "An exception is thrown"

        thrown UserException
    }

    def "Updating username saves updated value for valid usernames"() {
    	given: "an existing user"
    	def user = new User(username:"ralph", passwordHash: "hashedpassword").save(failOnError: true)

    	when: "we attempt to update the username via service"
    	def updatedUser = service.updateUsername(user.id, "ralph_malph")

    	then: "the username property is updated"
    	user.id == updatedUser.id
    	user.username != old(user.username)
    }

    def "Attempting to update username to invalid value results in error"() {
    	given: "an existing user"
    	def user = new User(username:"honey_booboo", passwordHash: "hashedpassword").save(failOnError: true)

    	when: "we attempt to update the username to empty string"
    	def savedUser = service.updateUsername(user.id, "")

		then: 'the user has errors'

		savedUser.hasErrors()
		'blank' == savedUser.errors.getFieldError('username').code
		'' == savedUser.errors.getFieldError('username').rejectedValue

    	when: "we attempt to update the username to null value"
    	savedUser = service.updateUsername(user.id, null)

		then: 'the user has errors'

		savedUser.hasErrors()
		'nullable' == savedUser.errors.getFieldError('username').code
		null == savedUser.errors.getFieldError('username').rejectedValue
    }

    def "addToFollowing allows the current user to add another user to following collection"() {
        given: "two existing users that do not follow each other"

        def existingUser1 = new User(username:'gene', passwordHash:'burger').save(failOnError: true)
        existingUser1.profile = new Profile(fullName: 'gene belcher', email: 'gene@bobs.com')
        existingUser1.save(flush: true)

        def existingUser2 = new User(username:'lana', passwordHash:'testpasswd').save(failOnError: true)
        existingUser2.profile = new Profile(fullName: 'lana kane', email: 'lana@isis.com')
        existingUser2.save(flush: true)

        when: "we attempt to add user 2 to user 1's following collection"
        service.addToFollowing existingUser1.username, existingUser2.username

        then: "User 1's following collection is updated with User 2"
        existingUser1.following.size() == 1
        existingUser1.following[0] == existingUser2
    }

    def "having a user attempt to follow himself will result in an exception"() {
    	given: "an existing user"
    	def user = new User(username:"ralph", passwordHash: "hashedpassword").save(failOnError: true)

		when: "we attempt to save the user to its own following collection"
		service.addToFollowing user.username, user.username

		then: "an exception is thrown"
		thrown UserException    	
    }

    def "attempting to add a non-existent user to a following collection results in an exception"() {
    	given: "an existing user"
    	def user = new User(username:"ralph", passwordHash: "hashedpassword").save(failOnError: true)

		when: "we attempt to save a non-existent user to a user's following collection"
		service.addToFollowing user.username, "dontexist"

		then: "an exception is thrown"
		thrown UserException    	
    }

    def "attempting to add a user to a non-existent user's following collection results in an exception"() {
    	given: "an existing user"
    	def user = new User(username:"ralph", passwordHash: "hashedpassword").save(failOnError: true)

		when: "we attempt to save a user to a non-existent user's following collection"
		service.addToFollowing "dontexist", user.username

		then: "an exception is thrown"
		thrown UserException    	
    }

    def "removeFromFollowing allows the current user to remove another user from following collection"() {
        given: "two existing users that follow each other"

        def existingUser1 = new User(username:'gene', passwordHash:'burger').save(failOnError: true)
        existingUser1.profile = new Profile(fullName: 'gene belcher', email: 'gene@bobs.com')
        existingUser1.save(flush: true)

        def existingUser2 = new User(username:'lana', passwordHash:'testpasswd').save(failOnError: true)
        existingUser2.profile = new Profile(fullName: 'lana kane', email: 'lana@isis.com')
        existingUser2.save(flush: true)

        existingUser1.addToFollowing existingUser2
        existingUser2.addToFollowing existingUser1

        existingUser1.save(flush: true)
        existingUser2.save(flush: true)

        when: "User 1 attempts to unfollow User 2"
        service.removeFromFollowing existingUser1.username, existingUser2.username

        then: "User 2 is removed from User 1's following collection"

        existingUser1.following.size() == old(existingUser1.following.size()) - 1
        existingUser1.following.size() == 0
    }

    // other scenarios for removeFromFollowing

    def "addToBlocking allows the current user to add another user to blocking collection"() {
        given: "two existing users"

        def existingUser1 = new User(username:'gene', passwordHash:'burger').save(failOnError: true)
        existingUser1.profile = new Profile(fullName: 'gene belcher', email: 'gene@bobs.com')
        existingUser1.save(flush: true)

        def existingUser2 = new User(username:'lana', passwordHash:'testpasswd').save(failOnError: true)
        existingUser2.profile = new Profile(fullName: 'lana kane', email: 'lana@isis.com')
        existingUser2.save(flush: true)

        when: "we attempt to add user 2 to user 1's following collection"
        service.addToBlocking existingUser1.username, existingUser2.username

        then: "User 1's following collection is updated with User 2"
        existingUser1.blocking.size() == 1
        existingUser1.blocking[0] == existingUser2
    }

	@Ignore
	void 'getFollowedByForUser returns the users that follow a particular user'() {
        given: 'Existing users that follow a user'

        def existingUser1 = new User(username:'gene', passwordHash:'burger').save(failOnError: true)
        existingUser1.profile = new Profile(fullName: 'gene belcher', email: 'gene@bobs.com')
        existingUser1.save(flush: true)

        def existingUser2 = new User(username:'lana', passwordHash:'testpasswd').save(failOnError: true)
        existingUser2.profile = new Profile(fullName: 'lana kane', email: 'lana@isis.com')
        existingUser2.save(flush: true)

        existingUser1.addToFollowing existingUser2
        existingUser1.save(flush: true)

        when: 'We retrieve the users that follow a user'
        def users = service.getFollowedByForUser existingUser2.username

        then: 'We get the expected results'
        users != null
        !users.isEmpty()
        users.contains existingUser1
    }

	@Ignore
    void 'getBlockedByOthersForUser returns the users that block a particular user'() {
        given: 'Existing users that follow a user'

        def existingUser1 = new User(username:'gene', passwordHash:'burger').save(failOnError: true)
        existingUser1.profile = new Profile(fullName: 'gene belcher', email: 'gene@bobs.com')
        existingUser1.save(flush: true)

        def existingUser2 = new User(username:'lana', passwordHash:'testpasswd').save(failOnError: true)
        existingUser2.profile = new Profile(fullName: 'lana kane', email: 'lana@isis.com')
        existingUser2.save(flush: true)

        existingUser1.addToBlocking existingUser2
        existingUser1.save(flush: true)

        when: 'We retrieve the users that block a user'
        def users = service.getBlockedByOthersForUser existingUser2.username

        then: 'We get the expected results'
        users != null
        !users.isEmpty()
        users.contains existingUser1
    }

}
