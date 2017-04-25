package com.joelforjava.ripplr

import grails.persistence.Entity

@Entity
class Tag {

	String name
	User user

	static belongsTo = [ User, Ripple ]

    static constraints = {
    	name blank: false
    }

    String toString() { return "Tag $name (id: $id)" }
    String getDisplayString() { return name }
}
