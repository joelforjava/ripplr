package com.joelforjava.ripplr

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import grails.persistence.Entity

@Entity
@EqualsAndHashCode(includes='username')
@ToString(includes='username', includeNames=true, includePackage=false)
class User implements Serializable {

	private static final long serialVersionUID = 1

	transient springSecurityService

	String username
	String passwordHash
	boolean enabled = true
	boolean accountExpired
	boolean accountLocked
	boolean passwordExpired
	Date dateCreated

	Set<Role> getAuthorities() {
		UserRole.findAllByUser(this)*.role
	}

	def beforeInsert() {
		encodePassword()
	}

	def beforeUpdate() {
		if (isDirty('passwordHash')) {
			encodePassword()
		}
	}

	protected void encodePassword() {
		passwordHash = springSecurityService?.passwordEncoder ? springSecurityService.encodePassword(passwordHash) : passwordHash
	}

	String toString() {
		return "User $username (id: $id)"
	}

	String getDisplayString() {
		return username
	}

	static hasOne = [ profile: Profile ]
	static hasMany = [ ripples: Ripple, tags: Tag, following: User, blocking: User ]

	static transients = ['springSecurityService']

	static constraints = {
		username size: 3..150, blank: false, unique: true
		passwordHash blank: false
		profile nullable: true
	}

	static mapping = {
		passwordHash column: '`passwordHash`'
		ripples sort: "dateCreated", order: "desc"
		following joinTable: [name: 'user_user_following']
        blocking joinTable:  [name: 'user_user_blocking']
		autowire true
	}

	static searchable = {
		//except = ["passwordHash"]
		only = ["username"]
		//ripples component:true
	}

}
