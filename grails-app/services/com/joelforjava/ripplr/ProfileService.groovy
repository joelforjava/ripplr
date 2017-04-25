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

	/**
	 * @deprecated use UserService.createUserAndProfile instead.
	 */
	@Deprecated
    Profile createProfile(userId, String fullName, String about, String homepage, String email, String twitterProfile,
    				 String facebookProfile, String timezone, String country, String skin) {

    	// load user first
    	def user = User.findById(userId)

    	if (user) {
	    	def profile = new Profile(fullName: fullName, about: about, homepage: homepage, email: email,
	    							twitterProfile: twitterProfile, facebookProfile: facebookProfile,
	    							timezone: timezone, country: country, skin: skin)
	    	user.profile = profile
	    	// This really should be profile.validate() but it breaks unit tests
	    	// Need to figure out how to get unit tests to work with profile.validate()
	    	if (user.validate() && user.save()) {
	    		return profile
	    	} else {
	    		throw new ProfileException(message: 'Invalid profile values', profile: profile)
	    	}
    	}
    	throw new ProfileException(message: 'Invalid User Reference, $userId')
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
