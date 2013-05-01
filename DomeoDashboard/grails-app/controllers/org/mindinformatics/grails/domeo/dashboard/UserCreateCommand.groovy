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
class UserCreateCommand {

	def springSecurityService;
	
	public static final Integer NAME_MAX_SIZE = 255;
	
	// Users status values
	//---------------------
	String status
	
	//Users' data
	String title
	String firstName
	String middleName
	String lastName
	String displayName
	String email
	String affiliation
	String country
	
	//Account credentials
	String username
	String password
	String passwordConfirmation
	
	static constraints = {
		//Users' data
		title (nullable: true, blank: true, maxSize:NAME_MAX_SIZE)
		firstName (blank: false, maxSize:NAME_MAX_SIZE)
		middleName (nullable: true, blank: true, maxSize:NAME_MAX_SIZE)
		lastName (blank: false, maxSize:NAME_MAX_SIZE)
		displayName (blank: false, maxSize:NAME_MAX_SIZE)
		email (blank: false, email: true,  maxSize:NAME_MAX_SIZE)
		affiliation (blank: true, maxSize:NAME_MAX_SIZE)
		country (blank: true, maxSize:NAME_MAX_SIZE)
		//Account credentials
		username (blank: false, maxSize:NAME_MAX_SIZE)
		password (blank: false, minSize:6, maxSize:NAME_MAX_SIZE)
		passwordConfirmation (blank: false, minSize:6, maxSize:NAME_MAX_SIZE)
	}
	
	boolean isEnabled() {
		return status.equals(UserStatus.ACTIVE_USER.value());
	}
	
	boolean isLocked() {
		return status.equals(UserStatus.LOCKED_USER.value());
	}
	
	boolean isPasswordValid() {
		return password.equals(passwordConfirmation);
	}	
	
	User createUser() {
		if(isPasswordValid()) {
			return User.findByUsername(username) ? null:
				new User(title: title, firstName: firstName, middleName: middleName, lastName: lastName, displayName: displayName, username: username, 
					email: email, affiliation: affiliation, country: country, password: springSecurityService.encodePassword(password), enabled:isEnabled())
		} else {
			return null;
		}
	}
}
