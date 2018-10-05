package com.joelforjava.ripplr

import grails.converters.JSON

import javax.annotation.PostConstruct
import java.text.SimpleDateFormat

/**
 * Created by joel on 3/21/17.
 */
class MarshallerRegistrar {

    def dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss")
    @PostConstruct
    void registerMarshallers() {
        JSON.registerObjectMarshaller(Ripple) { Ripple r ->
            return [ id: r.id, published: r.dateCreated ? dateFormatter.format(r.dateCreated) : new Date(), //hack to prevent issues with ES results. Can remove when switching to GSON Views
                     message: r.content, user: r.user.username, tags: r.tags.collect { it.name }]
        }
    }
}
