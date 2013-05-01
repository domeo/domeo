package org.mindinformatics.grails.domeo.dashboard.security

/**
* @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
*/
class UserUtils {

	static String getStatusLabel(User user) {
		if(user.isEnabled()) {
			 if(user.isAccountLocked()) return UserStatus.LOCKED_USER.value();
			 else return UserStatus.ACTIVE_USER.value();
		} else {
			return UserStatus.DISABLED_USER.value();
		}
	}
}
