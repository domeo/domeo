package org.mindinformatics.grails.domeo.dashboard.groups


/**
* @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
*/
enum DefaultGroupRoles {
	ADMIN("GROUP_ADMIN", 10000, "Admin", "Administrator can set up an instance of the platform and create other administrators or managers."), 
	MANAGER("GROUP_MANAGER", 1000, "Manager", "Managers are allowed to create groups and assign their management to specific users that will cover specific roles in that context."), 
	CURATOR("GROUP_CURATOR", 100, "Curator", "Curators are allowed to moderate the annotation content that has been made public for the group."),
	USER("GROUP_USER", 10, "User", "Users can request to managers the creation of groups "),
	GUEST("GROUP_GUEST", 1, "Guest", "Guests can see the annotation but they cannot create new annotation nore modify the existing one")
	
	DefaultGroupRoles(String value, int ranking, String label, String description) {
		this.value = value;
		this.ranking = ranking;
		this.label = label;
		this.description = description;
	}
	
	private final String value
	public String value() { return value }
	private final int ranking
	public String ranking() { return ranking }
	private final String label
	public String label() { return label }
	private final String description
	public String description() { return description }
}
