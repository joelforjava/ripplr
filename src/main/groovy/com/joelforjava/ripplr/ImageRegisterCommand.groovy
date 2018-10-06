package com.joelforjava.ripplr

import grails.validation.Validateable
import org.springframework.web.multipart.MultipartFile

class ImageRegisterCommand implements Validateable {
    MultipartFile photo
    String description

    static constraints = {
        description nullable: true
        photo nullable: true, validator: { val, obj ->
            if (val == null) {
                return true
            }

            if (val.empty) {
                if (val.originalFilename) {
                    return false
                }
                return true
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
