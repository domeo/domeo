package org.mindinformatics.grails.domeo.dashboard.security

import grails.validation.Validateable

import java.util.Set

/**
* @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
*/
@Validateable
class AccountRequest { 

	private static final int NAME_MAX_SIZE = 255;
	
	String firstName
	String lastName
	String displayName
	String country
	String affiliation
	
	String id
	String username
	String password
	String email
	Date dateCreated, lastUpdated // Grails automatic timestamping
	boolean validated
	
	boolean moderated
	User moderatedBy
	boolean approved
	
	String userId
	
	static hasMany = [openIds: OpenID]

	static transients = ['name','status','isAdmin','isUser','isManager','isCurator','isAnalyst']
	
	String getStatus() {
		return UserUtils.getStatusLabel(this);
	}
	
	String getName() {
		return lastName + " " + firstName;
	}
	
	static constraints = {
		//Users' data
		firstName (blank: false, maxSize:NAME_MAX_SIZE)
		lastName (blank: false, maxSize:NAME_MAX_SIZE)
		displayName (blank: true, maxSize:NAME_MAX_SIZE)
		affiliation (blank: true, maxSize:NAME_MAX_SIZE)
		country (blank: true, maxSize:NAME_MAX_SIZE)
		validated (blank: false)
		
		username (blank: false, unique: true, minSize:4, maxSize:60)
		password blank: false
		email blank: false, unique: true, email: true
		
		userId nullable:true, blank:true
		moderatedBy nullable:true, blank:true
	}

	static mapping = {
		password column: '`password`'
		id generator:'uuid'
	}
}
