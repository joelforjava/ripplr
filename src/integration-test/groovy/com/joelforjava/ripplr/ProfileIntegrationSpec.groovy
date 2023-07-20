package com.joelforjava.ripplr

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import org.apache.commons.logging.LogFactory

@Rollback
@Integration
class ProfileIntegrationSpec extends Specification {

    private static final log = LogFactory.getLog(this)

    def "Profiles with no user cannot be saved"() {
    	given: "A brand new profile"

    	def profile = new Profile(fullName:"Full Name")

    	when: "An attempt to save the profile is made"

    	profile.save()

    	then:

    	profile.hasErrors()
    	"nullable" == profile.errors.getFieldError("user").code
    	null == profile.errors.getFieldError("user").rejectedValue
    }

    def "Profiles with no fullName causes errors during validation"() {
    	given: "A brand new profile"

    	def profile = new Profile()

    	when: "The profile is validated"

    	profile.validate()

    	then:

    	profile.hasErrors()
    	"nullable" == profile.errors.getFieldError("fullName").code
    	null == profile.errors.getFieldError("fullName").rejectedValue
    }

    def "Profiles with a user and valid values can be saved"() {
        given: "A new user"

        def user = new User(username:'gene', passwordHash:'burger').save(failOnError: true)

        and: "A minimal profile"

        def profile = new Profile(fullName: "Gene Belcher", email: "gene@bobsburgers.com")

        when: "The user is added to the profile and the profile is saved"

        user.profile = profile
        profile.user = user
        profile.save(flush: true)

        then:

        !profile.hasErrors()
        profile.id != null
        Profile.get(profile.id).fullName == profile.fullName
        Profile.count() == old(Profile.count()) + 1
    }

    def "Profiles saved through User can access the user reference"() {
        given: "A new user"

        def user = new User(username:'gene', passwordHash:'burger').save(failOnError: true)

        and: "A minimal profile"

        def profile = new Profile(fullName: "Gene Belcher", email: "gene@bobsburgers.com")

        when: "The profile is added to the user and the user is saved"

        user.profile = profile
        user.save(flush: true)

        then: "We can access the user from the profile"

        profile.user.username == user.username
        Profile.count() == old(Profile.count()) + 1
    }
}
