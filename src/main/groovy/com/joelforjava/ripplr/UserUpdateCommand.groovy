package com.joelforjava.ripplr

import grails.validation.Validateable
import org.springframework.validation.Errors

/**
 * Created by joel on 3/18/17.
 */
class UserUpdateCommand implements Validateable {
    String username
    String password
    String passwordVerify

    ProfileRegisterCommand profile

    boolean usernameDirty
    boolean passwordDirty

    static constraints = {
        importFrom User
        profile validator: { ProfileRegisterCommand val, UserUpdateCommand obj, Errors errors ->
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
        password size: 6..150, nullable: true,
                validator: { passwd, urc ->
                    return passwd != urc.username
                }
        passwordVerify nullable: true,
                validator: { passwd2, urc ->
                    return passwd2 == urc.password
                }
    }
}
