package com.joelforjava.ripplr

import grails.compiler.GrailsCompileStatic
import grails.persistence.Entity
import groovy.transform.EqualsAndHashCode


//@GrailsCompileStatic
@EqualsAndHashCode(includes=['name', 'user'])
@Entity
class Tag {

	String name
	User user

	static belongsTo = [ User, Ripple ]

    static constraints = {
    	name blank: false
    }

    static searchable = {
        name boost: 4.0
        //user reference: false
        //spellCheck "include"
    }

    String toString() { return "Tag $name (id: $id)" }
    String getDisplayString() { return name }
}
