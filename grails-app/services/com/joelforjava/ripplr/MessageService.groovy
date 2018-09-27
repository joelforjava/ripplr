package com.joelforjava.ripplr

import grails.gorm.transactions.Transactional
import grails.web.servlet.mvc.GrailsParameterMap

@Transactional
class MessageService {

    List<Message> listMessagesForUser(User user, GrailsParameterMap params) {
        params.max = Math.min(Math.abs(params.max ?: 10), 100)
        List<Message> messageList = Message.where {
            recipient.username == user.username
        }.list(params)

        messageList
    }

    List<Message> listSentMessagesForUser(User user, GrailsParameterMap params) {
        params.max = Math.min(Math.abs(params.max ?: 10), 100)
        List<Message> messageList = Message.where {
            sender.username == user.username
        }.list(params)

        messageList
    }

    Message create(GrailsParameterMap params) {
        new Message(params)
    }

    @Transactional(readOnly = true)
    Message load(Long id) {
        Message.read(id)
    }

    Message save(User sender, User recipient, String subject, String content) {
        def message = new Message(sender: sender, recipient: recipient, subject: subject, content: content, dateSent: new Date())
        if (!message.save()) {
            log.error("Could not save message: $message")
        }
        message
    }

    Message markRead(Long id) {
        Message message = this.load id
        if (!message) {
            return message
        }
        if (!message.dateRead) {
            message.dateRead = new Date()
        }
        if (!message.save()) {
            log.error("Could not mark message read: $message")
        }
        message
    }
}
