package org.mindinformatics.grails.domeo.dashboard.security

class RoleType {

	String authority
	int ranking
	String label
	String description

	static mapping = {
		cache true
	}

	static constraints = {
		authority blank: false, unique: true
		ranking blank: false
		label blank: false
		description blank: true
	}
}
