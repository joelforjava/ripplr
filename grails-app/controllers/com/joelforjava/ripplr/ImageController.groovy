package com.joelforjava.ripplr

class ImageController {

	def springSecurityService

	def upload(ImageUploadCommand iuc) {
//    	def user = springSecurityService.currentUser
		def user = User.findByUsername iuc.username
    	switch (iuc.type) {
    		case ImageType.PROFILE:
    			uploadProfilePhoto(user.profile, iuc.photo)
    			break
    		case ImageType.COVER:
    			uploadCoverPhoto(user.profile, iuc.photo)
    			break
    		default:
    			// do nothing
    			break
    	}

    	// probably need to call save on the user or profile

    	redirect controller: "user", action: "profile", id: iuc.username

	}

    def renderMainPhoto(String username) {
    	def user = User.findByUsername username
    	if (user?.profile?.mainPhoto) {
    		response.setContentLength user.profile.mainPhoto.size()
    		response.outputStream.write user.profile.mainPhoto
    	} else {
    		response.sendError 404
    	}
    }

    private def uploadProfilePhoto(profile, photo) {
    	if (profile) {
    		profile.mainPhoto = photo
    	}
    }

    private def uploadCoverPhoto(profile, photo) {
    	if (profile) {
    		profile.coverPhoto = photo
    	}
    }
}

class ImageUploadCommand {
	byte[] photo
	ImageType type // this would designate 'profile' vs. 'cover', etc.
	String username // could probably get by with using spring security service
}

enum ImageType { PROFILE, COVER }