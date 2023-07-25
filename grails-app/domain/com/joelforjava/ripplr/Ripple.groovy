package com.joelforjava.ripplr

import grails.persistence.Entity
import groovy.transform.EqualsAndHashCode

//@EqualsAndHashCode(includes=['id', 'content', 'user'])
@Entity
class Ripple {

	String content
	Date dateCreated

	// TODO: Should a Ripple be aware that it is a response/re-post/etc.?

	static belongsTo = [ user: User ]
	static hasMany = [ tags: Tag, responses: Response, reposts: Repost ]
	static mappedBy = [ responses: 'inResponseTo', reposts: 'original' ]

    static constraints = {
    	content blank: false
    }

    static searchable = {
		content boost: 2.0
		user component: true
//		tags component: true
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
