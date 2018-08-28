package com.joelforjava.ripplr

import grails.compiler.GrailsCompileStatic
import groovy.transform.EqualsAndHashCode

@GrailsCompileStatic
@EqualsAndHashCode
class Image {

    private static final int ONE_KILOBYTE = 1024

    byte[] bytes
    String uri
    String description
    String contentType

    static constraints = {
        bytes nullable: true, maxSize: 25 * ONE_KILOBYTE * ONE_KILOBYTE
        contentType nullable: true
        uri nullable: true
        description nullable: true
    }

//    static mapping = {
//        bytes column: 'image_bytes', sqlType: 'longBlob'
//    }

    @Override
    String toString() {
        if (uri) {
            return "Image: location: $uri, description: $description, contentType: $contentType"
        }
        "Image: description: $description, contentType: $contentType"
    }
}
