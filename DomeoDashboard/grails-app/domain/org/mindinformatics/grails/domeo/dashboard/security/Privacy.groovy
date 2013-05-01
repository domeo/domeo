package org.mindinformatics.grails.domeo.dashboard.security

/**
* @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
*/
class Privacy {

	String uuid
	String value
	String label
	String description

	static mapping = {
		cache true
	}

	static constraints = {
		uuid blank:false
		value blank: false, unique: true
		label blank: false
		description blank: true
	}
}
