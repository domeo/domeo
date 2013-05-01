package org.mindinformatics.grails.domeo.dashboard.groups

/**
* @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
*/
enum DefaultGroupStatus {

	ACTIVE("G_ACTIVE", "Active", "", "org.mindinformatics.domeo.uris.status.Active"),
	LOCKED("G_LOCKED", "Locked", "Not active but visible", "org.mindinformatics.domeo.uris.status.Locked"),
	DISABLED("G_DISABLED", "Disabled", "Not active and not visible", "org.mindinformatics.domeo.uris.status.Disabled"),
	DELETED("G_DELETED", "Deleted", "Not active, not visible and not restorable.", "org.mindinformatics.domeo.uris.status.Deleted")
	
	DefaultGroupStatus(String value, String label, String description, String uuid) {
		this.value = value
		this.label = label;
		this.description = description;
		this.uuid = uuid;
	}
	
	private final String uuid
	public String uuid() { return uuid }
	private final String value
	public String value() { return value }
	private final String label
	public String label() { return label }
	private final String description
	public String description() { return description }
}
