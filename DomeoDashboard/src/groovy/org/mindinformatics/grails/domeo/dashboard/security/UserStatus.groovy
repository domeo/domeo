package org.mindinformatics.grails.domeo.dashboard.security

/**
* @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
*/
enum UserStatus {
	CREATED_USER("Created"),
	ACTIVE_USER("Active"),
	LOCKED_USER("Locked"),
	DISABLED_USER("Disabled")

	UserStatus(String value) {
		this.value = value
	}
	
	boolean isStatusValid(String status) {
		return status.equals(CREATED_USER.value) ||
			status.equals(ACTIVE_USER.value) ||
			status.equals(LOCKED_USER.value) ||
			status.equals(DISABLED_USER.value);
	}

	private final String value
	public String value() { return value }
}