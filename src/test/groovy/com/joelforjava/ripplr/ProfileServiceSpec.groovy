package com.joelforjava.ripplr

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(ProfileService)
@Mock([User, Profile])
class ProfileServiceSpec extends Specification {

	def user

    def setup() {
    	user = new User(username: "Sterling", passwordHash: "HashedPasswd").save(flush: true)
    }


    def "Valid profile data will allow creation of profile"() {
    	given: "Valid profile values"
    	String fullName = "Sterling Mallory Archer"
    	String email = "duchess@isis.com"

    	and: "Empty strings for optional values"
		String about = ""
		String homepage = ""
		String twitterProfile = ""
		String facebookProfile = ""
		String timezone = ""
		String country = ""
		String skin = ""

    	when: "We attempt to create a new profile"

    	def profile = service.createProfile(user.id, fullName, about, homepage, 
                                email, twitterProfile, facebookProfile, timezone, country, skin)

    	then: "The profile is returned by the service"

    	Profile.count() == old(Profile.count()) + 1
    	profile.fullName == fullName
    	profile.email == email
    }

    def "Attempting to create a profile with invalid profile parameters will cause an error"() {
        when: "We call the service with invalid parameters"

        service.createProfile(user.id, "", "", "", "", "", "", "", "", "")

        then: "An exception is thrown"

        thrown ProfileException
    }

    def "Attempting to create a profile with an invalid user ID will cause an error"() {
        when: "We call the service with an invalid user ID"

        service.createProfile(-1, "Sterling Archer", "Undercover agent for ISIS", "archer.com", 
                    "archer@isis.com", "twitter.com/archer", "facebook.com/archer", "", "", "")

        then: "An exception is thrown"

        thrown ProfileException
    }

    def "Profile properties can be updated via save method"() {
        given: "An existing profile"

        def existingProfile = new Profile(fullName: "Archer", email: "archer@isis.com")
        user.profile = existingProfile
        user.save(flush: true)

        when: "We attempt to change values"

        def savedProfile = service.saveProfile(user.id, "Sterling Archer", "Undercover agent for ISIS", "http://archer.com", 
                                    "duchess@isis.com", "http://twitter.com/archer", "http://facebook.com/archer", "", "", "")

        then: "The profile values are updated"

        existingProfile.fullName == savedProfile.fullName
        existingProfile.fullName == "Sterling Archer"
        existingProfile.twitterProfile == savedProfile.twitterProfile

        Profile.count() == old(Profile.count())

    }

    def "If the profile cannot be found during a save then an error is thrown"() {

        when: "We attempt to save a profile that doesn't exist"

        def savedProfile = service.saveProfile(user.id, "Sterling Archer", "Undercover agent for ISIS", "http://archer.com", 
                                    "duchess@isis.com", "http://twitter.com/archer", "http://facebook.com/archer", "", "", "")

        then: "An exception is thrown"

        thrown ProfileException
    }

    def "Attempting to update a profile with invalid values results in an error"() {
        when: "We call the service with an invalid user ID"

        service.saveProfile(user.id, "Sterling Archer", "Undercover agent for ISIS", "archer.com", 
                    "archer@isis.com", "twitter.com/archer", "facebook.com/archer", "", "", "")

        then: "An exception is thrown"

        thrown ProfileException
    }

    def "Service can retrieve profile of a user with a valid user ID"() {
        given: "An existing profile for a user"

        def existingProfile = new Profile(fullName: "Archer", email: "archer@isis.com")
        user.profile = existingProfile
        user.save(flush: true)

        when: "We attempt to retrieve the profile with the user ID"

        def retrievedProfile = service.retrieveProfile(user.id)

        then: "We recieve the profile from the service"

        retrievedProfile.fullName == existingProfile.fullName
        retrievedProfile.email == existingProfile.email
    }

    def "Attempting to retrieve a user's profile that doesn't exist with the user's ID results in an error"() {
        when: "We attempt to retrieve a profile for a user with no profile using the user ID"

        service.retrieveProfile(user.id)

        then: "An exception is thrown"

        thrown ProfileException
    }

    def "Service can retrieve profile of a user with a valid username"() {
        given: "An existing profile for a user"

        def existingProfile = new Profile(fullName: "Archer", email: "archer@isis.com")
        user.profile = existingProfile
        user.save(flush: true)

        when: "We attempt to retrieve the profile with the username"

        def retrievedProfile = service.retrieveProfile(user.username)

        then: "We recieve the profile from the service"

        retrievedProfile.fullName == existingProfile.fullName
        retrievedProfile.email == existingProfile.email
    }

    def "Attempting to retrieve a user's profile that doesn't exist with the username results in an error"() {
        when: "We attempt to retrieve a profile for a user with no profile using the username"

        service.retrieveProfile(user.username)

        then: "An exception is thrown"

        thrown ProfileException
    }
}
