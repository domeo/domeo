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
class UserResetPasswordCommand {

	def springSecurityService;
	
	public static final Integer NAME_MAX_SIZE = 255;

	//Account credentials
	String password
	String passwordConfirmation
	
	static constraints = {
		//Account credentials
		password (blank: false, minSize:6, maxSize:NAME_MAX_SIZE)
		passwordConfirmation (blank: false, minSize:6, maxSize:NAME_MAX_SIZE)
	}
	
	
	boolean isPasswordValid() {
		return password.equals(passwordConfirmation);
	}	
}
