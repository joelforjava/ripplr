package com.joelforjava.ripplr

import grails.validation.Validateable
import groovy.transform.ToString

/**
 * Created by joel on 3/18/17.
 */
@ToString(includeNames = true)
class ProfileRegisterCommand implements Validateable {
    ImageRegisterCommand mainPhoto
    ImageRegisterCommand coverPhoto
    String fullName
    String about
    String homepage
    String email
    String twitterProfile // URL or String?
    String facebookProfile // URL or String?
    String timezone
    String country
    String skin

    static constraints = {
        importFrom Profile
//        mainPhoto nullable: true, validator: photoValidator.curry('mainPhoto')
//        coverPhoto nullable: true, validator: photoValidator.curry('coverPhoto')
    }

    static photoValidator = { propertyName, val, obj, errors ->
        if (val == null) {
            return true
        }
        if (!val.validate()) {
            if (!val.validate()) {
                val.errors.allErrors.each { err ->
                    def fieldName = err.arguments ? err.arguments[0] : err.properties['field']
                    if (fieldName) {
                        String errorCode = "${propertyName}.${err.code}"
                        if (val.hasProperty(fieldName)) {
                            errorCode = "${propertyName}.${err.arguments[0]}.${err.code}"
                        }
                        errors.rejectValue("${propertyName}.${err.properties['field']}", errorCode, err.arguments, "Invalid value for {0}")
                    }
                }
            }
        }
    }

    transient Profile toProfile() {
        def profile = new Profile(fullName: this.fullName, about: this.about, homepage: this.homepage, email: this.email,
                                  twitterProfile: this.twitterProfile, facebookProfile: this.facebookProfile,
                                  timezone: this.timezone, country: this.country, skin: this.skin,
                                  mainPhoto: this.mainPhoto as Image, coverPhoto: this.coverPhoto as Image)
        profile
    }

    transient asType(Class target) {
        if (target == Profile) {
            return this.toProfile()
        }
        throw new ClassCastException("ProfileRegisterCommand object cannot be cast to $target")
    }
}
