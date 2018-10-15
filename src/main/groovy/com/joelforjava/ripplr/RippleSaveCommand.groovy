package com.joelforjava.ripplr

import grails.validation.Validateable

/**
 * Created by joel on 3/21/17.
 */
class RippleSaveCommand implements Validateable {
    String content
    String fromPage // TODO - convert to enum

    static constraints = {
        content blank: false, nullable: false
        fromPage nullable: false, inList: ['global', 'timeline']
    }
}
