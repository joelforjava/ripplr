package com.joelforjava.ripplr

import grails.validation.Validateable

/**
 * Created by joel on 3/18/17.
 */
class UserRegisterCommand implements Validateable {
    String username
    String password
    String passwordVerify

    ProfileRegisterCommand profile

    static constraints = {
        importFrom User
        profile validator: { val, obj, errors ->
            if (!val.validate()) {
                val.errors.allErrors.each { err ->
                    def fieldName = err.arguments ? err.arguments[0] : err.properties['field']
                    if (fieldName) {
                        String errorCode = "profile.${err.code}"
                        if (val.hasProperty(fieldName)) {
                            errorCode = "profile.${err.arguments[0]}.${err.code}"
                        }
                        errors.rejectValue("profile.${err.properties['field']}", errorCode, err.arguments, "Invalid value for {0}")
                    }
                }
            }
        }
        password size: 6..150, blank: false,
                validator: { passwd, urc ->
                    return passwd != urc.username
                }
        passwordVerify nullable: false,
                validator: { passwd2, urc ->
                    return passwd2 == urc.password
                }
    }

    transient User toUser() {
        new User(username: username, passwordHash: password, profile: this.profile as Profile)
    }

    transient asType(Class target) {
        if (target == User) {
            return this.toUser()
        }
        throw new ClassCastException("UserRegisterCommand cannot be cast to $target")
    }
}
