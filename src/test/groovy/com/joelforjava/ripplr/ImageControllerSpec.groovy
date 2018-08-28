package com.joelforjava.ripplr

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.grails.plugins.testing.GrailsMockMultipartFile
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(ImageController)
@Mock([User, Profile])
class ImageControllerSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    def "setting profile picture for a valid user"() {
    	given: "An existing user with an existing profile"
    	def existingUser = new User(username:'gene', passwordHash:'burger').save(failOnError: true)
        def existingProfile = new Profile(fullName: 'gene belcher', email: 'gene@bobs.com')
        existingUser.profile = existingProfile
        existingUser.save(flush: true)
/*
        and: "a mock user service"
    	def mockSecurityService = Mock(SpringSecurityService)
    	mockSecurityService.getCurrentUser() >> existingUser
    	controller.springSecurityService = mockSecurityService
*/
    	and: "a property configured and mocked command object"
        ImageUploadCommand iuc = mockCommandObject ImageUploadCommand
        iuc.type = ImageType.PROFILE
        iuc.photo = populateMultipartFile 'filename.jpeg', [1, 0, 1] as byte[]
        iuc.username = existingUser.username

        and: "it has been validated"
        iuc.validate()

        when: "we call the upload action"
        controller.upload iuc

        then: "the user is updated with a profile photo and the browser is redirected"
        !iuc.hasErrors()
        response.redirectedUrl == "/user/profile/gene"
        existingProfile.mainPhoto != null
        //flash.message == "Changes Saved."

    }

    def populateMultipartFile(String filename, byte[] content) {
        new GrailsMockMultipartFile(filename, filename, 'content-type', content)
    }

}
