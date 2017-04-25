package com.joelforjava.ripplr

import grails.persistence.Entity

@Entity
class Profile implements Serializable {

	private static final long serialVersionUID = 1

	byte[] mainPhoto
	byte[] coverPhoto
	String fullName
	String about
	String homepage
	String email
	String twitterProfile // URL or String?
	String facebookProfile // URL or String?
	String timezone
	String country
	String skin // use instead (or with) coverPhoto?

	static belongsTo = [ user: User ]

    static constraints = {
    	fullName blank: false
    	about nullable: true, maxSize: 1500
    	homepage url: true, nullable: true
    	email email: true, blank: false
    	twitterProfile url: true, nullable: true
    	facebookProfile url: true, nullable: true
    	mainPhoto nullable: true, maxSize: 2 * 1024 * 1024
    	coverPhoto nullable: true, maxSize: 2 * 1024 * 1024
    	country nullable: true
    	timezone nullable: true
    	skin nullable: true, blank: true, inList: ['standard', 'darkness']
    }

    String toString() {
    	return "Profile of $fullName (id: $id)"
    }

    String getDisplayString() {
    	return fullName
    }
}
