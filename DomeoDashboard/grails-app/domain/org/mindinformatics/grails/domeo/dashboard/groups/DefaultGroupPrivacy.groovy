package org.mindinformatics.grails.domeo.dashboard.groups


/**
* @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
*/
enum DefaultGroupPrivacy {

	PRIVATE("G_PRIVATE", "Private", "Private", "org.mindinformatics.domeo.uris.visibility.Private"),
	RESTRICTED("G_RESTRICTED", "Restricted", "Restricted", "org.mindinformatics.domeo.uris.visibility.Restricted"),
	PUBLIC("G_PUBLIC", "Public", "Public", "org.mindinformatics.domeo.uris.visibility.Public")
	
	DefaultGroupPrivacy(String value, String label, String description, String uuid) {
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
