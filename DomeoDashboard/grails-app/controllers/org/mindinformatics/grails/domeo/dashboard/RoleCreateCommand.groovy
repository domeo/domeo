package org.mindinformatics.grails.domeo.dashboard

import org.mindinformatics.grails.domeo.dashboard.security.DefaultRoles
import org.mindinformatics.grails.domeo.dashboard.security.Role


/**
* Object command for Roles validation and creation.
*
* @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
*/
class RoleCreateCommand {

	String authorithy;
	
	boolean isAnAllowedRole(Role role) {
		switch (role) {
			case DefaultRoles.ADMIN:
				return false;
			case DefaultRoles.SUPER_USER:
				return true;
			case DefaultRoles.USER:
				return true;
			default:
				return false;
		}
	}
	
	Role createRole() {
		if(isAnAllowedRole(authorithy)) {
			def role =  Role.findByAuthority(authorithy)
			return Role.findByAuthority(authorithy) ? null:
				new Role(authorithy: authorithy)
		} else {
			return null;
		}
	}
}
