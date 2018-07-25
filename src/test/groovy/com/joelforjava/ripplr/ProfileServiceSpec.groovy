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

    def 'Updating profile properties via updateProfile method'() {
        given: "An existing profile"

        def existingProfile = new Profile(fullName: "Archer", email: "archer@isis.com", user: user)
        user.profile = existingProfile
        user.save(flush: true, failOnError: true)

        when: "We attempt to change values"
        def savedProfile = service.updateProfile(user.id,
                                        new ProfileCommand(fullName: "Sterling Archer",
                                                about: "Undercover agent for ISIS", homepage: "http://archer.com",
                                                email: "duchess@isis.com", twitterProfile: "http://twitter.com/archer",
                                                facebookProfile: "http://facebook.com/archer"))

        then: "The profile values are updated"

        existingProfile.fullName == savedProfile.fullName
        existingProfile.fullName == "Sterling Archer"
        existingProfile.twitterProfile == savedProfile.twitterProfile

        Profile.count() == old(Profile.count())

    }

    def 'Updating a profile when no profile exists results in receiving a null object'() {
        when: "We attempt to update a profile that doesn't exist"
        def profile = service.updateProfile(user.id, new ProfileCommand())

        then: 'The response is null'
        !profile
    }

    def 'Updating the profile of an invalid userId will result in a null object being returned'() {
        when: 'We attempt to update a profile for a user ID that does not exist'
        def profile = service.updateProfile(-42L, new ProfileCommand())

        then: 'The response is null'
        !profile
    }

    def 'Attempting to update a profile with invalid values will prevent the domain object from being saved and we will have errors'() {
        given: "An existing profile"

        def existingProfile = new Profile(fullName: "Archer", email: "archer@isis.com", user: user)
        user.profile = existingProfile
        user.save(flush: true, failOnError: true)

        when: "We attempt to change values"
        def savedProfile = service.updateProfile(user.id,
                                        new ProfileCommand(fullName: "Sterling Archer",
                                                about: "Undercover agent for ISIS", homepage: "http://archer.com",
                                                email: "duchessisis.com", twitterProfile: "http://twitter.com/archer",
                                                facebookProfile: "http://facebook.com/archer"))

        then: 'The profile comes back with errors'
        existingProfile.errors.allErrors.size() == 1
        existingProfile.errors.getFieldError('email').code == 'email.invalid'

    }

    def "Service can retrieve profile of a user with a valid user ID"() {
        given: "An existing profile for a user"

        def existingProfile = new Profile(fullName: "Archer", email: "archer@isis.com", user: user)
        user.profile = existingProfile
        user.save(flush: true)

        when: "We attempt to retrieve the profile with the user ID"

        def retrievedProfile = service.findProfile(user.id)

        then: "We recieve the profile from the service"

        retrievedProfile.fullName == existingProfile.fullName
        retrievedProfile.email == existingProfile.email
    }

    def "Attempting to retrieve a user's profile that doesn't exist with the user's ID results in an error"() {
        when: "We attempt to retrieve a profile for a user with no profile using the user ID"

        def profile = service.findProfile(user.id)

        then: 'We get a null object'

        !profile
    }

    def "Service can retrieve profile of a user with a valid username"() {
        given: "An existing profile for a user"

        def existingProfile = new Profile(fullName: "Archer", email: "archer@isis.com", user: user)
        user.profile = existingProfile
        user.save(flush: true)

        when: "We attempt to retrieve the profile with the username"

        def retrievedProfile = service.findProfile(user.username)

        then: "We recieve the profile from the service"

        retrievedProfile.fullName == existingProfile.fullName
        retrievedProfile.email == existingProfile.email
    }

    def "Attempting to retrieve a user's profile that doesn't exist with the username results in an error"() {
        when: "We attempt to retrieve a profile for a user with no profile using the username"

        def profile = service.findProfile(user.username)

        then: 'We get a null object'

        !profile
    }
}
