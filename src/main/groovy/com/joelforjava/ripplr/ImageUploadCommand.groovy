package com.joelforjava.ripplr

import grails.validation.Validateable
import org.springframework.web.multipart.MultipartFile

class ImageUploadCommand implements Validateable {
    MultipartFile photo
    ImageType type // this would designate 'profile' vs. 'cover', etc.
    String username // could probably get by with using spring security service
    String description

    static constraints = {
        description nullable: true
        photo nullable: true, validator: { val, obj ->
            if (val == null) {
                return true     // true is valid, for now. at some point, it will be required.
            }

            if (val.empty) {
                return false
            }

            ['jpeg', 'jpg', 'png'].any { extension ->
                val.originalFilename?.toLowerCase()?.endsWith(extension)
            }
        }
    }

    transient Image toImage() {
        if (!photo || photo.contentType == 'application/octet-stream') {
            return null
        }
        new Image(bytes: photo.bytes, description: description, contentType: photo.contentType)
    }

    transient asType(Class target) {
        if (target == Image) {
            return this.toImage()
        }
        throw new ClassCastException("ImageUploadCommand cannot be cast to $target")
    }

}

enum ImageType { PROFILE, COVER }