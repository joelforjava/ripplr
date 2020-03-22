package com.joelforjava.ripplr

import grails.persistence.Entity

@Entity
class Response {

	Ripple ripple

	static belongsTo = [ inResponseTo: Ripple ]

}
