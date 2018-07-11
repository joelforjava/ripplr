package com.joelforjava.ripplr

import grails.testing.gorm.DataTest
import grails.testing.services.ServiceUnitTest
import spock.lang.Specification

class ProfileServiceSpec extends Specification implements ServiceUnitTest<ProfileService>, DataTest {

	def user

    def setup() {
    	user = new User(username: "Sterling", passwordHash: "HashedPasswd").save(flush: true)
    }

    def setupSpec() {
        mockDomains(User, Profile)
    }

    def "Profile properties can be updated via save method"() {
        given: "An existing profile"

        def existingProfile = new Profile(fullName: "Archer", email: "archer@isis.com", user: user)
        user.profile = existingProfile
        user.save(flush: true, failOnError: true)

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

        def existingProfile = new Profile(fullName: "Archer", email: "archer@isis.com", user: user)
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

        def existingProfile = new Profile(fullName: "Archer", email: "archer@isis.com", user: user)
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
