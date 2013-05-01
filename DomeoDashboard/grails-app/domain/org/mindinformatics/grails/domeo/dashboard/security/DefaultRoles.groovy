package org.mindinformatics.grails.domeo.dashboard.security

/**
* @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
*/
enum DefaultRoles {
	ADMIN("ROLE_ADMIN", 10000, "Administrator", 
		"Administrator can set up an instance of the platform and create other administrators or managers."), 
	MANAGER("ROLE_MANAGER", 10, "Manager", 
		"Managers are allowed to create groups and assign their management to specific users that will cover specific roles in that context."), 
	USER("ROLE_USER", 1, "User", "Users can request to managers the creation of groups.");
	
	DefaultRoles(String value, int ranking, String label, String description) { 
		this.value = value; 
		this.ranking = ranking;
		this.label = label;	
		this.description = description;
	}
	
	private String value
	public String value() { return value }
	private int ranking
	public int ranking() { return ranking }
	private String label
	public String label() { return label }
	private String description
	public String description() { return description }
}
