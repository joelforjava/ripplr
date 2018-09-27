package com.joelforjava.ripplr

import grails.plugin.springsecurity.SpringSecurityService
import grails.testing.gorm.DataTest
import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification

class MessageControllerSpec extends Specification implements ControllerUnitTest<MessageController>, DataTest, DomainDataFactory {

    def setupSpec() {
        mockDomains(User, Message)
    }

    def cleanup() {
    }

    void 'Index action returns nothing when no messages exist'() {

        given: 'The current user'
        def receiver = validUserWithUsername('receiver@receiver.com')

        and: "a mock security service"
        controller.springSecurityService = Mock(SpringSecurityService) {
            1 * getCurrentUser() >> receiver
        }

        and: 'A mock message service'
        controller.messageService = Mock(MessageService) {
            1 * listMessagesForUser(*_) >> []
        }

        when: 'We call the index action with the max param set to zero with no existing messages'
        controller.index(0)

        then: 'We get the expected results'
        status == 200
        !model.messageList
        model.messageCount == 0
    }

    void 'Index action returns messages'() {
        given: 'We have a sender'
        def sender = validUserWithUsername('sender@sender.com')

        and: 'A receiver'
        def receiver = validUserWithUsername('receiver@receiver.com')

        and: 'We have created messages'
        def messages = []
        (0..<15).each {
            messages << new Message(sender: sender, recipient: receiver, dateSent: new Date(), content: 'Hello there')
        }

        and: "a mock security service"
        controller.springSecurityService = Mock(SpringSecurityService) {
            1 * getCurrentUser() >> receiver
        }

        and: 'A mock message service'
        controller.messageService = Mock(MessageService) {
            1 * listMessagesForUser(*_) >> messages
        }

        when: 'We call the index action with a particular value'
        controller.index(15)

        then: 'We get the expected results'
        status == 200
        model.messageList
        model.messageList.size() == 15
        model.messageCount == model.messageList.size()

    }

    /** Save tests */

    void 'Saving a valid message using JSON'() {
        given: 'We have a sender'
        def sender = validUserWithUsername('sender@sender.com')

        and: 'A receiver'
        def receiver = validUserWithUsername('receiver@receiver.com')

        and: 'Valid message field values'
        def subject = 'My first message'
        def content = 'This is my content in my first message'

        and: "a mock security service"
        controller.springSecurityService = Mock(SpringSecurityService) {
            1 * getCurrentUser() >> sender
        }

        and: "a mock user service"
        controller.userService = Mock(UserService) {
            1 * findUser(_ as String) >> receiver
        }

        and: 'A mock message service'
        controller.messageService = Mock(MessageService) {
            1 * save(*_) >> validMessageWithSenderAndRecipient(sender, receiver)
        }

        and:
        response.reset()
        request.contentType = JSON_CONTENT_TYPE
        request.method = 'POST'

        when: 'We call save on the controller'
        controller.save receiver.username, subject, content

        then: 'The message is saved (sent)'
        status == 201
        model.message
        model.message.dateSent
    }

    void 'Saving a valid message using a form'() {
        given: 'We have a sender'
        def sender = validUserWithUsername('sender@sender.com').save()

        and: 'A receiver'
        def receiver = validUserWithUsername('receiver@receiver.com').save()

        and: 'Valid message field values'
        def subject = 'My first message'
        def content = 'This is my content in my first message'

        and: "a mock security service"
        controller.springSecurityService = Mock(SpringSecurityService) {
            1 * getCurrentUser() >> sender
        }

        and: "a mock user service"
        controller.userService = Mock(UserService) {
            1 * findUser(_ as String) >> receiver
        }

        and: 'A mock message service'
        controller.messageService = Mock(MessageService) {
            1 * save(*_) >> validMessageWithSenderAndRecipient(sender, receiver).save()
        }

        and:
        response.reset()
        request.contentType = FORM_CONTENT_TYPE
        request.method = 'POST'

        when: 'We call save on the controller'
        controller.save receiver.username, subject, content

        then: 'The message is saved (sent) and we are redirected'
        status == 302
        response.redirectedUrl.startsWith('/message')
    }

    void 'Attempting to save a message with no content results in an error'() {
        given: 'We have null content'

        def content = null

        and: 'Valid values for subject and receiver'
        def subject = 'My first message'
        def recipientUsername = 'recipient'

        when: 'We call save on the controller'
        controller.save recipientUsername, subject, content

        then: 'The message is returned with errors'
        status == 400
        !model

    }

    void 'Attempting to save a message with no recipient user name results in an error'() {
        given: 'We have a null recipient user name'
        def recipientUsername = null

        and: 'Valid values for subject and content'
        def subject = 'My message to you'
        def content = 'My message content'

        when: 'We call save on the controller'
        controller.save recipientUsername, subject, content

        then: 'The message is returned with errors'
        status == 400
        !model
    }

    void 'Show will return a not found error when given a null ID'() {
        given: 'We have a null ID'
        def messageId = null

        when: 'We call show with this ID'
        controller.show messageId

        then: 'We get a 404 error'
        status == 404
        !model
    }
}
