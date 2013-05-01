package org.mindinformatics.grails.domeo.dashboard.security



class OpenID {

	String url

	static belongsTo = [user: User]

	static constraints = {
		url unique: true
	}
}
