package com.joelforjava.ripplr

import grails.validation.Validateable

class MessageSaveCommand implements Validateable {
    String recipientUsername
    String subject
    String content

    static constraints = {
        importFrom Message
        recipientUsername nullable: false
    }
}
