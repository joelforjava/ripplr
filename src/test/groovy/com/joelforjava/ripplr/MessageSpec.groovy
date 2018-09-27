package com.joelforjava.ripplr

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification
import spock.lang.Unroll

class MessageSpec extends Specification implements DomainUnitTest<Message> {

    def setup() {
    }

    def cleanup() {
    }

    void 'Creating a valid message'() {
        given: 'A user to send a message'
        def sender = new User(username: 'bob@burgers.com', passwordHash: 'nocleverpassword').save(failOnError: true)

        and: 'A user to receive a message'
        def recipient = new User(username: 'linda@burgers.com', passwordHash: 'passwordOne').save(failOnError: true)

        when: 'We attempt to save a message from sender to recipient'
        def message = new Message(sender: sender, recipient: recipient, dateSent: new Date(), content: 'Hello there').save()

        then: 'The message is saved'
        message
        message.id > 0

        // NOTE: This part of the test fails when it is an integration test!
        and: 'The users have the expected values for sent and received messages'
        User.read(sender.id).sentMessages
        sender.sentMessages.size() == 1
        recipient.receivedMessages
        recipient.receivedMessages.size() == 1
    }

    void 'A message without a sender is invalid'() {
        given: 'A user to receive a message'
        def recipient = new User(username: 'linda@burgers.com', passwordHash: 'passwordOne').save(failOnError: true)

        and: 'We create a message from a null user to recipient'
        def message = new Message(sender: null, recipient: recipient, dateSent: new Date(), content: 'Hello there')

        when: 'We try to send the message'
        def response = message.save()

        then: 'The message is not saved'
        !response
        !message.id
        message.hasErrors()
        'nullable' == message.errors.getFieldError('sender').code
    }

    void 'A message without a recipient is invalid'() {
        given: 'A user to send a message'
        def sender = new User(username: 'bob@burgers.com', passwordHash: 'nocleverpassword').save(failOnError: true)

        and: 'We create a message from sender to a null user'
        def message = new Message(sender: sender, recipient: null, dateSent: new Date(), content: 'Hello there')

        when: 'We try to send the message'
        def response = message.save()

        then: 'The message is not saved'
        !response
        !message.id
        message.hasErrors()
        'nullable' == message.errors.getFieldError('recipient').code
    }


    void 'A message can be saved from the user sending the message'() {
        given: 'A user to send a message'
        def sender = new User(username: 'bob@burgers.com', passwordHash: 'nocleverpassword').save(failOnError: true)

        and: 'A user to receive a message'
        def recipient = new User(username: 'linda@burgers.com', passwordHash: 'passwordOne').save(failOnError: true)

        and: 'We have a new message'
        def message = new Message(recipient: recipient, dateSent: new Date(), content: 'Hello there')

        when: 'We attempt to save a message to the sender\'s sent messages'
        sender.addToSentMessages(message)
        sender.save()

        then: 'The message is saved'
        message
        message.id > 0

        and: 'The data is propagated where expected'
        sender.sentMessages
        sender.sentMessages.size() == 1
        sender.sentMessages[0].sender == sender
        recipient.receivedMessages
        recipient.receivedMessages.size() == 1
    }

    void 'A message can be saved via the recipient of the message'() {
        given: 'A user to send a message'
        def sender = new User(username: 'bob@burgers.com', passwordHash: 'nocleverpassword').save(failOnError: true)

        and: 'A user to receive a message'
        def recipient = new User(username: 'linda@burgers.com', passwordHash: 'passwordOne').save(failOnError: true)

        and: 'We have a new message'
        def message = new Message(sender: sender, dateSent: new Date(), content: 'Hello there')

        when: 'We attempt to save a message to the recipient\'s received messages'
        recipient.addToReceivedMessages(message)
        recipient.save()

        then: 'The message is saved'
        message
        message.id > 0

        and: 'The data is propagated where expected'
        sender.sentMessages
        sender.sentMessages.size() == 1
        recipient.receivedMessages
        recipient.receivedMessages.size() == 1
        recipient.receivedMessages[0].recipient == recipient
    }

    @Unroll("Testing message from #senderName to #recipientName is valid - #expValid")
    void 'Testing various size constraints'() {
        given: 'A user to send a message'
        def sender = new User(username: senderName, passwordHash: 'nocleverpassword')

        and: 'A user to receive a message'
        def recipient = new User(username: recipientName, passwordHash: 'passwordOne')

        and: 'We have a message from sender to recipient'
        def message = new Message(sender: sender, recipient: recipient, dateSent: new Date(), content: msgContent, subject: subjectText)

        when: 'We invoke validation'
        def isValid = message.validate()

        then: 'The appropriate fields are flagged as errors'
        isValid == expValid
        message.errors.allErrors.size() == numErrors
        message.errors.getFieldError(fieldInError)?.code == errorCode

        where:
        senderName | recipientName | msgContent    | subjectText || expValid | numErrors | fieldInError | errorCode
        'gene'     | 'megan'       | 'hello'       | null        || true     | 0         | null         | null
        'bobby'    | 'linda'       | 'hi'          | 'hi'        || true     | 0         | null         | null
        'homer'    | 'marge'       | 'hello there' | '-' * 150   || true     | 0         | null         | null
        'bart'     | 'lisa'        | 'hello'       | '-' * 151   || false    | 1         | 'subject'    | 'maxSize.exceeded'
        'chris'    | 'peter'       | '-' * 10_000  | null        || true     | 0         | null         | null
        'peter'    | 'chris'       | '-' * 10_001  | null        || false    | 1         | 'content'    | 'maxSize.exceeded'
        'lois'     | 'stewie'      | null          | null        || false    | 1         | 'content'    | 'nullable'
    }
}
