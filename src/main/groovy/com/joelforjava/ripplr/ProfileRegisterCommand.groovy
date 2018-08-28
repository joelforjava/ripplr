package com.joelforjava.ripplr

import grails.validation.Validateable

/**
 * Created by joel on 3/18/17.
 */
class ProfileRegisterCommand implements Validateable {
    ImageUploadCommand mainPhoto
    ImageUploadCommand coverPhoto
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
