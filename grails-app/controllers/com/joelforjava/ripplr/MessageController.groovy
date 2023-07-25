package com.joelforjava.ripplr

import grails.plugin.springsecurity.SpringSecurityService
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus

class MessageController {

    SpringSecurityService springSecurityService
    UserService userService
    MessageService messageService
    MessageSource messageSource

    def index(Integer max) {
        def currentUser = springSecurityService.currentUser

        params.max = Math.min(max ?: 10, 100)
        List<Message> messageList = messageService.listMessagesForUser(currentUser, params)
        respond messageList, model: [messageCount: messageList.size()]
    }

    def show(Long id) {
        if (!id) {
            notFound()
            return
        }

        // TODO - using markRead here causes `TransactionRequiredException: no transaction is in progress`.
        //        and I'm not sure why. You could use this method if hibernate.allow_update_outside_transaction
        //        is set to true, but I'd rather not use it if Hibernate is moving toward enforcing transactions.
        //        More here: https://github.com/grails/grails-core/issues/11376, but it really doesn't provide
        //        much insight. The markRead logic needs to be fleshed out anyway and should probably be a
        //        separate call.
        respond messageService.load(id)
    }

    def create() {
        respond messageService.create(params)
    }

    def save(String recipientUsername, String subject, String content) {
        if (!content || !recipientUsername) {
            render status: HttpStatus.BAD_REQUEST
            return
        }

        def currentUser = springSecurityService.currentUser

        def recipient = userService.findUser(recipientUsername)

        def message = messageService.save(currentUser, recipient, subject, content)

        if (message.hasErrors()) {
            respond message.errors, view: 'create', status: HttpStatus.UNPROCESSABLE_ENTITY  // 422
            return
        }

        request.withFormat {
            form multipartForm {
                String msg = messageSource.getMessage('message.label', [] as Object[], 'Message', request.locale)
                flash.message = messageSource.getMessage('default.created.message', [msg, message.id] as Object[], 'Message sent', request.locale)
                redirect(action: 'index')
            }

            '*' { render status: HttpStatus.CREATED }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                String msg = messageSource.getMessage('message.label', [] as Object[], 'Message', request.locale)
                flash.message = messageSource.getMessage('default.not.found.message', [msg, params.id] as Object[], 'Message not found', request.locale)
                redirect(uri: '/', method: 'GET')
            }
            '*' { render status: HttpStatus.NOT_FOUND }
        }
    }

}
