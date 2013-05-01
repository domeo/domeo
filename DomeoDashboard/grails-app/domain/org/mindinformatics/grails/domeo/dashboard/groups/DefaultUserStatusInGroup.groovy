package org.mindinformatics.grails.domeo.dashboard.groups

/**
* @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
*/
enum DefaultUserStatusInGroup {
	
	ACTIVE("IG_ACTIVE", "Active", ""),
	PENDING("IG_PENDING", "Pending", "Used for the groups that require acceptance"),
	LOCKED("IG_LOCKED", "Locked", "Not active but visible"),
	SUSPENDED("IG_SUSPENDED", "Suspended", "Not active and not visible"),
	DELETED("IG_DELETED", "Deleted", "Not active, not visible and not restorable.")
	
	DefaultUserStatusInGroup(String value, String label, String description) {
		this.value = value
		this.label = label;
		this.description = description;
	}
	
	private final String value
	public String value() { return value }
	private final String label
	public String label() { return label }
	private final String description
	public String description() { return description }
}
