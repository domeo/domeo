package org.mindinformatics.grails.domeo.dashboard

import grails.validation.Validateable

import org.mindinformatics.grails.domeo.dashboard.security.AccountRequest
import org.mindinformatics.grails.domeo.dashboard.security.UserStatus;


/**
* Object command for User validation and creation.
*
* @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
*/
@Validateable
class SaveOpenIdLinkCommand {

	def springSecurityService;
	
	public static final Integer NAME_MAX_SIZE = 255;

	//Account credentials
	String username
	String password
	String openId
	
	static constraints = {
		//Account credentials
		username (blank: false, maxSize:NAME_MAX_SIZE)
		password (blank: false, minSize:6, maxSize:NAME_MAX_SIZE)
		password (blank: false, minSize:6, maxSize:NAME_MAX_SIZE)
	}
	
	boolean isUserValid() {
		return status.equals(UserStatus.ACTIVE_USER.value());
	}
	
	boolean isOpenIdValid() {
		return status.equals(UserStatus.LOCKED_USER.value());
	}	
}
