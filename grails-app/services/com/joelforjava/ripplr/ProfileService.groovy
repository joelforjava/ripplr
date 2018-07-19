package com.joelforjava.ripplr

import grails.gorm.transactions.Transactional


/**
 * Exception thrown by the ProfileService when an exception occurs.
 */
class ProfileException extends RuntimeException {
	String message
	Profile profile
}

/**
 * This is the main service for working with Profiles.
 */
@Transactional
class ProfileService {

    /**
     * Retrieve a User Profile belonging to a given User ID.
     *
     * @param userId - The unique User ID
     * @return the user profile
     * @throws ProfileException if no profile is found.
     */
	Profile retrieveProfile(Long userId) {

		def profile = Profile.where {
			user.id == userId
		}.get()

		if(!profile) {
			throw new ProfileException(message:"Invalid user ID provided")
		}

		profile
	}

    /**
     * Retrieve a User Profile belonging to a user with a given username.
     *
     * @param username - The user's unique user name.
     * @return the user profile
     * @throws ProfileException if no profile is found
     */
	Profile retrieveProfile(String username) {

		def profile = Profile.where {
			user.username == username
		}.get()

		if(!profile) {
			throw new ProfileException(message:"Invalid username provided")
		}

		profile
	}

    /**
     * Update an existing User Profile.
     *
     * @param userId - the user ID to which the profile belongs
     * @param pc - the updated profile data
     * @param flush - flush the transaction?
     * @return - the updated profile, with errors if it could not be saved, or null if profile could not be found.
     */
	Profile updateProfile(Long userId, ProfileCommand pc, boolean flush = false) {
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
