package com.joelforjava.ripplr

import grails.testing.mixin.integration.Integration
import grails.transaction.*
import spock.lang.Specification

@Integration
@Rollback
class MessageIntegrationSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void 'A user cannot be both the sender and recipient of a message'() {
        given: 'An existing user'
        def user = new User(username: 'gene@burgers.com', passwordHash: 'itsfridayletsdoShots').save(failOnError: true)

        and: 'We create a message with the user as the sender and recipient'
        def message = new Message(sender: user, recipient: user, dateSent: new Date(), content: 'Hello there')

        when: 'We attempt to save the message'
        def response = message.save()

        then: 'The message is not saved'
        !response
        !message.id
    }

    void 'Messages with no content are not valid'() {
        given: 'A user to send a message'
        def sender = new User(username: 'bob@burgers.com', passwordHash: 'nocleverpassword').save(failOnError: true)

        and: 'A user to receive a message'
        def recipient = new User(username: 'linda@burgers.com', passwordHash: 'passwordOne').save(failOnError: true)

        and: 'We create a message with no content'
        def message = new Message(sender: sender, recipient: recipient, content: null)

        when: 'We try to validate'
        def isValid = message.validate()

        then: 'We receive errors'
        !isValid
        'nullable' == message.errors.getFieldError('content').code
    }

    void 'Retrieving messages by sender'() {
        given: 'We have a user to send messages'
        def linda = new User(username: 'linda@burgers.com', passwordHash: 'nocleverpassword').save(failOnError: true)

        and: 'We have two users to receive messages'
        def louise = new User(username: 'louise@burgers.com', passwordHash: 'ivegotnothing').save(failOnError: true)
        def bob = new User(username: 'bob@burgers.com', passwordHash: 'nocleverpasswordhere').save(failOnError: true)

        and: 'the sender 5 messages to Recipient 1'
        (0..<5).each {
            new Message(sender: linda, recipient: louise, dateSent: new Date(), content: 'Hello there').save(failOnError: true)
        }

        and: 'the sender 3 messages to Recipient 2'
        (0..<3).each {
            new Message(sender: linda, recipient: bob, dateSent: new Date(), content: 'Hello there').save(failOnError: true)
        }

        when: 'We call list to find recipient 1\'s messages'
        def messages = Message.where {
            recipient.username == louise.username
        }.list()

        then: 'We get back the expected messages'
        messages
        messages.size() == 5
        messages.every { it.recipient.username == louise.username }
    }

    void 'Retrieving a list of messages'() {
        given: 'We have a user to send messages'
        def linda = new User(username: 'linda@burgers.com', passwordHash: 'nocleverpassword').save(failOnError: true)

        and: 'We have two users to receive messages'
        def louise = new User(username: 'louise@burgers.com', passwordHash: 'ivegotnothing').save(failOnError: true)
        def bob = new User(username: 'bob@burgers.com', passwordHash: 'nocleverpasswordhere').save(failOnError: true)

        and: 'the sender 5 messages to Recipient 1'
        (0..<5).each {
            new Message(sender: linda, recipient: louise, dateSent: new Date(), content: 'Hello there').save(failOnError: true)
        }

        and: 'the sender 3 messages to Recipient 2'
        (0..<3).each {
            new Message(sender: linda, recipient: bob, dateSent: new Date(), content: 'Hello there').save(failOnError: true)
        }

        when: 'We call list to find recipient 1\'s messages'
        def messages = Message.list()

        then: 'We get back the expected messages'
        messages
        messages.size() == 8
    }
}
