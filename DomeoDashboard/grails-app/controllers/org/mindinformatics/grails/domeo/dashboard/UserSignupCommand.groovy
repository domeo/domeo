package org.mindinformatics.grails.domeo.dashboard

import grails.validation.Validateable

import org.mindinformatics.grails.domeo.dashboard.security.AccountRequest
import org.mindinformatics.grails.domeo.dashboard.security.User
import org.mindinformatics.grails.domeo.dashboard.security.UserStatus;


/**
* Object command for User validation and creation.
*
* @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
*/
@Validateable
class UserSignupCommand {

	def springSecurityService;
	
	public static final Integer NAME_MAX_SIZE = 255;
	
	// Users status values
	//---------------------
	String status
	
	//Users' data
	String firstName
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
		firstName (blank: false, maxSize:NAME_MAX_SIZE)
		lastName (blank: false, maxSize:NAME_MAX_SIZE)
		displayName (blank: false, maxSize:NAME_MAX_SIZE)
		email (blank: false, email: true,  maxSize:NAME_MAX_SIZE)
		affiliation (blank: false, maxSize:NAME_MAX_SIZE)
		country (blank: false, maxSize:NAME_MAX_SIZE)
		//Account credentials
		username (blank: false, unique: true, minSize:4, maxSize:60)
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
		println "isPasswordValid()"
		return password.equals(passwordConfirmation);
	}	
	
	boolean isUsernameAvailable() {
		println "isUsernameAvailable() " + User.findByUsername(username)
		return ((User.findByUsername(username)!=null || AccountRequest.findByUsername(username)!=null)? false : true);
	}
	
	boolean isEmailAvailable() {
		println "isEmailRegistered() " + User.findByEmail(email) + ' ' + AccountRequest.findByEmail(email)
		return ((User.findByEmail(email)!=null || AccountRequest.findByEmail(email)!=null) ? false : true);
	}
	
	AccountRequest createAccountRequest() {
		println "createAccountRequest()"
		return AccountRequest.findByEmail(email) ? null:
			new AccountRequest(firstName: firstName, lastName: lastName, displayName: displayName, username: username, 
				validated:false, email: email, country: country, affiliation: affiliation, password: springSecurityService.encodePassword(password), enabled:isEnabled())
	}
	
}
