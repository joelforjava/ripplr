package com.joelforjava.ripplr

import grails.testing.gorm.DataTest
import grails.testing.services.ServiceUnitTest
import grails.web.servlet.mvc.GrailsParameterMap
import spock.lang.Specification
import spock.lang.Unroll

class MessageServiceSpec extends Specification implements ServiceUnitTest<MessageService>, DataTest, DomainDataFactory {

    private static final int DEFAULT_NUM_MESSAGES_RETURNED = 10
    private static final int MAX_NUM_MESSAGES_RETURNED = 100

    def setupSpec() {
        mockDomains(User, Message)
    }

    @Unroll("listMessagesForUser returns #expMessages when #numMessages exist and #maxMessages are requested")
    void 'listMessagesForUser will return the expected amount of messages for a given user'() {
        given: 'We have a sender'
        def sender = validUserWithUsername('sender@sender.com').save(failOnError: true)

        and: 'A receiver'
        def receiver = validUserWithUsername('receiver@receiver.com').save(failOnError: true)

        and: 'We have created messages'
        (0..<numMessages).each {
            new Message(sender: sender, recipient: receiver, dateSent: new Date(), content: 'Hello there').save(failOnError: true)
        }

        and: 'We have the params.max set'
        def params = new GrailsParameterMap([max: maxMessages], null)

        when: 'We call the listMessagesForUser method with a particular value'
        def messages = service.listMessagesForUser(receiver, params)

        then: 'We get back the expected messages'
        messages
        messages.size() == expMessages
        messages.every { it.recipient == receiver }

        where:

        numMessages | maxMessages || expMessages
        20          | 0           || DEFAULT_NUM_MESSAGES_RETURNED
        120         | 120         || MAX_NUM_MESSAGES_RETURNED
        200         | 200         || MAX_NUM_MESSAGES_RETURNED
        1           | 0           || 1
        30          | 25          || 25
        15          | -1          || 1
        120         | -1          || 1
        25          | null        || DEFAULT_NUM_MESSAGES_RETURNED
    }

    @Unroll("listSentMessagesForUser returns #expMessages when #numMessages exist and #maxMessages are requested")
    void 'listSentMessagesForUser will return the expected amount of messages for a given user'() {
        given: 'We have a sender'
        def sender = validUserWithUsername('sender@sender.com').save(failOnError: true)

        and: 'A receiver'
        def receiver = validUserWithUsername('receiver@receiver.com').save(failOnError: true)

        and: 'We have created messages'
        (0..<numMessages).each {
            new Message(sender: sender, recipient: receiver, dateSent: new Date(), content: 'Hello there').save(failOnError: true)
        }

        and: 'We have the params.max set'
        def params = new GrailsParameterMap([max: maxMessages], null)

        when: 'We call the listSentMessagesForUser method with a particular value'
        def messages = service.listSentMessagesForUser(sender, params)

        then: 'We get back the expected messages'
        messages
        messages.size() == expMessages
        messages.every { it.sender == sender }

        where:

        numMessages | maxMessages || expMessages
        20          | 0           || DEFAULT_NUM_MESSAGES_RETURNED
        120         | 120         || MAX_NUM_MESSAGES_RETURNED
        200         | 200         || MAX_NUM_MESSAGES_RETURNED
        1           | 0           || 1
        30          | 25          || 25
        15          | -1          || 1
        120         | -1          || 1
        25          | null        || DEFAULT_NUM_MESSAGES_RETURNED
    }

    void 'Saving a valid message'() {
        given: 'We have a sender'
        def sender = validUserWithUsername('sender@sender.com')

        and: 'A receiver'
        def receiver = validUserWithUsername('receiver@receiver.com')

        and: 'Valid message field values'
        def subject = 'My first message'
        def content = 'This is my content in my first message'

        when: 'We call the save method'
        def message = service.save sender, receiver, subject, content

        then: 'We get back a valid message object'
        message
        !message.hasErrors()
        message.dateSent
    }

    void 'Saving an invalid message results in a message with errors'() {
        given: 'We have a sender'
        def sender = validUserWithUsername('sender@sender.com')

        and: 'A receiver'
        def receiver = validUserWithUsername('receiver@receiver.com')

        and: 'Invalid message field values'
        def subject = 'My first message'
        def content = null

        when: 'We call the save method'
        def message = service.save sender, receiver, subject, content

        then: 'We get back an invalid message object'
        message
        message.hasErrors()
    }

    void 'Load will return a valid message given a valid message id'() {
        given: 'We have a valid message'
        def message = validMessage().save(failOnError: true)

        when: 'We call load with the message ID'
        def retrievedMessage = service.load(message.id)

        then: 'We get back the expected message'
        retrievedMessage
        retrievedMessage.content == message.content
    }

    void 'Load returns null when given an invalid message ID'() {
        given: 'We have an invalid message ID'
        def invalidId = -171L

        when: 'We call load with the message ID'
        def retrievedMessage = service.load(invalidId)

        then: 'We get back the expected result'
        !retrievedMessage
    }

    void 'Create will return a new object'() {
        given: 'We have an empty params object'
        def params = new GrailsParameterMap([:], null)

        when: 'We call create'
        def message = service.create(params)

        then: 'We get back a new object'
        message
        !message.id
    }

    void 'markRead will mark a valid message as read by setting dateRead'() {
        given: 'We have a valid message'
        def message = validMessage().save(failOnError: true)

        when: 'We call markRead'
        def retrieved = service.markRead(message.id)

        then: 'We get back a message with dateRead set'
        retrieved
        retrieved.dateRead
    }

    void 'markRead will return null when a given message is not found'() {
        given: 'We have an invalid message ID'
        def invalidId = -171L

        when: 'We call load with the message ID'
        def retrievedMessage = service.markRead(invalidId)

        then: 'We get back the expected result'
        !retrievedMessage
    }

    void 'markRead will not update the dateRead if it is already set'() {
        given: 'We have a valid message'
        def message = validMessage()

        and: 'We have set the dateRead'
        message.dateRead = new Date()
        message.save(failOnError: true)

        when: 'We call markRead'
        def retrieved = service.markRead(message.id)

        then: 'We get back a message with the existing dateRead value'
        retrieved
        retrieved.dateRead == message.dateRead
    }
}
