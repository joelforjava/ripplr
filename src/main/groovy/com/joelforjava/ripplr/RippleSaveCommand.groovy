package com.joelforjava.ripplr

import grails.validation.Validateable

/**
 * Created by joel on 3/21/17.
 */
class RippleSaveCommand implements Validateable {
    String message

    static constraints = {
        message blank: false, nullable: false
    }
}