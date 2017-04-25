package com.joelforjava.ripplr

import grails.validation.Validateable

/**
 * Created by joel on 3/18/17.
 */
class UserRegisterCommand implements Validateable {
    String username
    String password
    String passwordVerify

    ProfileCommand profile

    static constraints = {
        importFrom User
        profile validator: { val, obj -> val.validate() }
        password size: 6..150, blank: false,
                validator: { passwd, urc ->
                    return passwd != urc.username
                }
        passwordVerify nullable: false,
                validator: { passwd2, urc ->
                    return passwd2 == urc.password
                }
    }
}
