package com.joelforjava.ripplr

import grails.testing.gorm.DataTest
import grails.testing.services.ServiceUnitTest
import spock.lang.Ignore
import spock.lang.Specification

class UserServiceSpec extends Specification implements ServiceUnitTest<UserService>, DataTest {

    def setupSpec() {
		mockDomains(User, Profile)
    }

    void 'Attempting to call a method that no longer exists will cause an exception'() {
    	given: 'New user values'

		String username = 'james'
		String passwordHash = 'hashedpass'

		when: 'the service is sent null values where booleans should be sent'

		def newUser = service.createUser(username, passwordHash, null, null, null)

		then: 'an exception is thrown because the method cannot be found'

		thrown groovy.lang.MissingMethodException

    }

    def 'Service can successfully create a user with profile'() {
        given: "A properly configured command object"
        def urc = new UserRegisterCommand()
        urc.with {
            username = "sterling"
            password = "duchess"
            passwordVerify = "duchess"
            profile = new ProfileRegisterCommand()
            profile.fullName = "Sterling Archer"
            profile.email = "sterling@isis.com"
        }

        when: 'We call the service to create the user and profile'
        def newUser = service.create(urc)

        then: 'We get back a new user'
        !newUser.hasErrors()
        newUser.username == urc.username
        newUser.profile.fullName == urc.profile.fullName
    }

	def 'Service will return a user with errors if the user could not be saved'() {
        given: "An improperly configured command object"
        def urc = new UserRegisterCommand()
        urc.with {
            password = "duchess"
            passwordVerify = "duchess"
        }

        when: 'We call the service to create the user and profile'
        def newUser = service.create(urc)

        then: 'We get back a user object with errors'
        newUser.hasErrors()
        "nullable" == newUser.errors.getFieldError("username").code
        null == newUser.errors.getFieldError("username").rejectedValue
    }

    def "User properties can be updated via save method"() {
    	given: "An existing user"

    	def user = new User(username: "james", passwordHash: "passwordhashed").save(failOnError: true)

    	when: "The user properties are updated"

    	def savedUser = service.saveUser(user.id, user.username, user.passwordHash, true, user.accountExpired, true)

    	then: "The user is updated in the database"

    	user.id == savedUser.id
    	user.accountLocked
    	user.passwordExpired
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

    def "Calling save on a user that does not exist results in null objects returned"() {
    	when: "We attempt to save a user that was not first created"

    	def result = service.saveUser(null, "jason", "passwordhasheddude", false, false, false)

    	then: "we have a null object"

    	!result

    	when: "We attempt to save a user that was not first created via alternate save"

    	result = service.saveUser(null, "Jason", "hashedpassword")

    	then: "we have a null object"

    	!result
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

	def 'Updating user with a command object when the password has been changed'() {
        given: 'An existing user'
        def user = new User(username:"gene", passwordHash:"hamburgerhamburger").save(failOnError: true)

        and: 'A command object denoting that the password has been changed'
        def cmd = new UserUpdateCommand()
        cmd.with {
            username = user.username
            password = 'mynewpasswordisgreat'
            passwordDirty = true
        }

        when: 'We call update'
        def updatedUser = service.update(cmd)

        then: 'We get the updated user'
        updatedUser.username == user.username
        user.passwordHash != old(user.passwordHash)
    }

    @Ignore("This can't really work! The system currently looks up a user by username! So, either add a 'newUsername' field or look up by ID!")
    def 'Updating user with command object when the username has been changed'() {
        given: 'an existing user'
        def user = new User(username: 'ralph', passwordHash: 'hashedpassword').save(failOnError: true, flush: true)

        and: 'A command object denoting that the password has been changed'
        def cmd = new UserUpdateCommand()
        cmd.with {
            username = 'ralph_malph'
            usernameDirty = true
        }

        when: 'we call update'
        def updatedUser = service.update cmd

        then: 'the username property is updated'
        user.id == updatedUser.id
        user.username != old(user.username)
    }

    def 'Calling updateUser without any dirty flags set results in receiving the original user object'() {
        given: 'An existing user'
        def user = new User(username:"gene", passwordHash:"hamburgerhamburger").save(failOnError: true)

        and: 'A command object denoting that the password has been changed'
        def cmd = new UserUpdateCommand()
        cmd.with {
            username = user.username
            password = 'mynewpasswordisgreat'
        }

        when: 'We call update'
        def updatedUser = service.update(cmd)

        then: 'We receive the original user object'
        updatedUser == user
        updatedUser.username == old(user.username)
        updatedUser.passwordHash == old(user.passwordHash)
    }


    def "Service can retrieve a user with a valid user ID"() {
    	given: "An existing user"

    	def user = new User(username: "james", passwordHash: "passwordhashed").save(failOnError: true)

    	when: "We attempt to retrieve the user via the service"

    	def retrievedUser = service.findUser(user.id)

    	then: "We have a valid user object to work with"

    	retrievedUser.id == user.id
    	retrievedUser.username == user.username
    }

    def "Attempting to retrieve user with invalid ID results in a null object"() {
    	when: "An attempt is made to retrieve an invalid user"

    	def result = service.findUser(-1)

    	then: "We receive a null object"

    	!result
    }

    def "Service can retrieve a user with a valid username"() {
    	given: "An existing user"

    	def user = new User(username: "james", passwordHash: "passwordhashed").save(failOnError: true)

    	when: "We attempt to retrieve the user via the service"

    	def retrievedUser = service.findUser(user.username)

    	then: "We have a valid user object to work with"

    	retrievedUser.id == user.id
    	retrievedUser.username == user.username
    }

    def "Attempting to retrieve user with an invalid username causes an error"() {
    	when: "An attempt is made to retrieve a user with an invalid username"

    	def result = service.findUser("Jambalaya")

    	then: "We get a null object"
		!result
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
        -30      | 5

    }

    def "Attempting to retrieve latest users when no users exist results in an empty list"() {
        when: "An attempt is made to retrieve the latest users when none exist"

        def results = service.retrieveLatestUsers(5)

        then: 'We get back an empty list'

        results.empty
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

    def 'Attempting to update the username with an invalid User ID results in a null object'() {
        when: 'We call updateUsername with an invalid User ID'
        def result = service.updateUsername(-331L, 'MyNewUserName')

        then: 'We receive a null object'
        !result
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

//	@Ignore
	void 'getFollowedByForUser returns the users that follow a particular user'() {
        given: 'Existing users that follow a user'

        def existingUser1 = new User(username:'gene', passwordHash:'burger').save(failOnError: true)
        existingUser1.profile = new Profile(fullName: 'gene belcher', email: 'gene@bobs.com', user: existingUser1)
        existingUser1.save(flush: true)

        def existingUser2 = new User(username:'lana', passwordHash:'testpasswd').save(failOnError: true)
        existingUser2.profile = new Profile(fullName: 'lana kane', email: 'lana@isis.com', user: existingUser2)
        existingUser2.save(flush: true)

        existingUser1.addToFollowing existingUser2
        existingUser1.save(flush: true)

        when: 'We retrieve the users that follow a user'
        def users = service.getFollowersForUser existingUser2.username

        then: 'We get the expected results'
        users != null
        !users.isEmpty()
        users.contains existingUser1
    }

//	@Ignore
    void 'getBlockedByOthersForUser returns the users that block a particular user'() {
        given: 'Existing users that follow a user'

        def existingUser1 = new User(username:'gene', passwordHash:'burger').save(failOnError: true)
        existingUser1.profile = new Profile(fullName: 'gene belcher', email: 'gene@bobs.com', user: existingUser1)
        existingUser1.save(flush: true)

        def existingUser2 = new User(username:'lana', passwordHash:'testpasswd').save(failOnError: true)
        existingUser2.profile = new Profile(fullName: 'lana kane', email: 'lana@isis.com', user: existingUser2)
        existingUser2.save(flush: true)

        existingUser1.addToBlocking existingUser2
        existingUser1.save(flush: true)

        when: 'We retrieve the users that block a user'
        def users = service.getBlockersForUser existingUser2.username

        then: 'We get the expected results'
        users != null
        !users.isEmpty()
        users.contains existingUser1
    }

    void 'onlineUserCount just returns the total user count for now'() {
        given: 'A few existing users'
        new User(username:'gene', passwordHash:'burger').save(failOnError: true)
        new User(username:'lana', passwordHash:'testpasswd').save(failOnError: true)

        when: 'We call onlineUserCount on the service'
        def count = service.onlineUserCount()

        then: 'For the moment it will return the total user count'
        2 == count
    }
}
