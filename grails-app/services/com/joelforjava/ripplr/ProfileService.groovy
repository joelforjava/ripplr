package com.joelforjava.ripplr

import grails.transaction.Transactional

class ProfileException extends RuntimeException {
	String message
	Profile profile
}

@Transactional
class ProfileService {

    /**
     * Retrieve a User Profile belonging to a given User ID.
     *
     * @param userId - The unique User ID
     * @return the user profile
     * @throws ProfileException if no profile is found.
     */
	Profile retrieveProfile(userId) {

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

    Profile saveProfile(userId, String fullName, String about, String homepage, String email, String twitterProfile,
    				 String facebookProfile, String timezone, String country, String skin) {

		def profile = Profile.where {
			user.id == userId
		}.get()

		if (profile) {
			profile.fullName = fullName
			profile.about = about
			profile.homepage = homepage
			profile.email = email
			profile.twitterProfile = twitterProfile
			profile.facebookProfile = facebookProfile
			profile.timezone = timezone
			profile.country = country
			profile.skin = skin

			if (profile.validate() && profile.save()) {
				return profile
			} else {
				throw new ProfileException(message: "Invalid profile properties", profile: profile)
			}
		}

		throw new ProfileException(message: "Invalid user provided or profile does not exist.")

    }

}
