package org.mindinformatics.grails.domeo.dashboard.security

/**
* @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
*/
class Status {

	String value
	String label
	String description

	static mapping = {
		cache true
	}

	static constraints = {
		value blank: false
		label blank: false
		description blank: true
	}
}
