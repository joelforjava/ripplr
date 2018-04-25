package com.joelforjava.ripplr

import grails.persistence.Entity

@Entity
class Ripple {

	String content
	Date dateCreated

	static belongsTo = [ user: User ]
	static hasMany = [ tags: Tag ]

    static constraints = {
    	content blank: false
    }

    static searchable = {
		content boost: 2.0
    	user parent: true, reference: true
    	//spellCheck "include"
    }

    static mapping = {
    	sort dateCreated: "desc"
    }

    String toString() {
    	return "Ripple '${shortContent}' (id: $id) from user '${user?.username}' created on: ${dateCreated}"
    }

    String getDisplayString() {
    	return shortContent
    }

    String getShortContent() {
    	def maxSize = 20
    	if (content?.size() > maxSize) {
    		return content.substring(0, maxSize - 3) + '...'
    	} else {
    		return content
    	}
    }
}
