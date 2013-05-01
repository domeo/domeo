package org.mindinformatics.grails.domeo.dashboard.groups

import org.mindinformatics.grails.domeo.dashboard.security.UserStatus

/**
* @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
*/
class GroupUtils {

	static String getStatusValue(Group group) {
		if(group.isEnabled()) {
			 if(group.isLocked()) return DefaultGroupStatus.LOCKED.value();
			 else return DefaultGroupStatus.ACTIVE.value();
		} else {
			return DefaultGroupStatus.DISABLED.value();
		}
	}
	
	static String getStatusUuid(Group group) {
		if(group.isEnabled()) {
			 if(group.isLocked()) return DefaultGroupStatus.LOCKED.uuid();
			 else return DefaultGroupStatus.ACTIVE.uuid();
		} else {
			return DefaultGroupStatus.DISABLED.uuid();
		}
	}
	
	static String getStatusLabel(Group group) {
		if(group.isEnabled()) {
			 if(group.isLocked()) return DefaultGroupStatus.LOCKED.label();
			 else return DefaultGroupStatus.ACTIVE.label();
		} else {
			return DefaultGroupStatus.DISABLED.label();
		}
	}
}
