package com.joelforjava.ripplr

import grails.validation.Validateable

/**
 * Created by joel on 3/18/17.
 */
class ProfileCommand implements Validateable {
    byte[] mainPhoto
    byte[] coverPhoto
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
}
