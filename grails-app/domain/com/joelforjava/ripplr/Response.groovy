package com.joelforjava.ripplr

import grails.persistence.Entity

@Entity
class Response {

	Ripple topic
	User inResponseTo
	// Should there be a ripple 'in response to' instead?
}
