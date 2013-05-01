package org.mindinformatics.grails.domeo.dashboard

import grails.validation.Validateable

import org.mindinformatics.grails.domeo.dashboard.security.User
import org.mindinformatics.grails.domeo.dashboard.security.UserStatus;


/**
* Object command for User validation and creation.
*
* @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
*/
@Validateable
class AccountRequestEditCommand {

	def springSecurityService;
	
	public static final Integer NAME_MAX_SIZE = 255;
	
	
	//Users' data
	String firstName
	String lastName
	String displayName
	String email
	String affiliation
	String country
	
	//Account credentials
	String id
	
	static constraints = {
		//Users' data
		firstName (blank: false, maxSize:NAME_MAX_SIZE)
		lastName (blank: false, maxSize:NAME_MAX_SIZE)
		displayName (blank: true, maxSize:NAME_MAX_SIZE)
		email (blank: false, email: true,  maxSize:NAME_MAX_SIZE)
		affiliation (blank: true, maxSize:NAME_MAX_SIZE)
		country (blank: true, maxSize:NAME_MAX_SIZE)
		//Account credentials
		id (blank: false)
	}
}
