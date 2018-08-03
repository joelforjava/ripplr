package com.joelforjava.ripplr

import grails.compiler.GrailsCompileStatic
import grails.gorm.transactions.Transactional

/**
 * This is the main service for working with Profiles.
 */
@GrailsCompileStatic
@Transactional
class ProfileService {

    /**
     * Retrieve a User Profile belonging to a given User ID.
     *
     * @param userId - The unique User ID
     * @return the user profile
     */
    Profile findProfile(Long userId) {
        Profile.read(userId)
    }

    /**
     * Find a User Profile belonging to a user with a given username.
     *
     * @param username - The user's unique user name.
     * @return the user profile
     */
    Profile findProfile(String username) {
        Profile.where {
            user.username == username
        }.get()
    }

    /**
     * Update an existing User Profile.
     *
     * @param userId - the user ID to which the profile belongs
     * @param pc - the updated profile data
     * @param flush - flush the transaction?
     * @return - the updated profile, with errors if it could not be saved, or null if profile could not be found.
     */
    Profile updateProfile(Long userId, ProfileRegisterCommand pc, boolean flush = false) {
        def profile = Profile.where {
            user.id == userId
        }.get()
        if (!profile) {
            return profile
        }
        profile.with {
            fullName = pc.fullName
            about = pc.about
            homepage = pc.homepage
            email = pc.email
            twitterProfile = pc.twitterProfile
            facebookProfile = pc.facebookProfile
            timezone = pc.timezone
            country = pc.country
            skin = pc.skin
        }
        if (!profile.save(flush: flush)) {
            log.error("Could not update profile: ${profile.errors.toString()}")
        }
        profile
    }
}
