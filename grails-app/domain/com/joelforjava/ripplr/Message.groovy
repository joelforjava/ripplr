package com.joelforjava.ripplr

class Message {

    String subject
    String content
    Date dateCreated
    Date dateSent
    Date dateRead

    static belongsTo = [ recipient : User, sender : User ]

    static constraints = {
        subject nullable: true, maxSize: 150
        content nullable: false, maxSize: 10_000
        dateRead nullable: true
        dateSent nullable: true
        recipient nullable: false, validator: { val, obj ->
            val != obj.sender
        }
    }

}
