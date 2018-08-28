package com.joelforjava.ripplr

class ImageController {

	def springSecurityService

	def upload(ImageUploadCommand iuc) {
//    	def user = springSecurityService.currentUser
		def user = User.findByUsername iuc.username
    	switch (iuc.type) {
    		case ImageType.PROFILE:
    			uploadProfilePhoto(user.profile, iuc as Image)
    			break
    		case ImageType.COVER:
    			uploadCoverPhoto(user.profile, iuc as Image)
    			break
    		default:
    			// do nothing
    			break
    	}

    	// probably need to call save on the user or profile

    	redirect controller: "user", action: "profile", id: iuc.username

	}

    def renderMainPhoto(String id) {
    	def user = User.findByUsername id
    	if (user?.profile?.mainPhoto) {
//    		response.setContentLength user.profile.mainPhoto.size()
//    		response.outputStream.write user.profile.mainPhoto
			render file: user.profile.mainPhoto.bytes, contentType: user.profile.mainPhoto.contentType
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
