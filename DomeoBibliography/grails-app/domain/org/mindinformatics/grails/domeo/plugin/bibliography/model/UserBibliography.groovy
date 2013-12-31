package org.mindinformatics.grails.domeo.plugin.bibliography.model

import java.util.Date;

import org.mindinformatics.grails.domeo.dashboard.security.User

/**
 * @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
 */
class UserBibliography {

	String id
	
	User user
	Date dateCreated, lastUpdated // Grails automatic timestamping

	static hasMany = [entries: BibliographicEntry]

	static mapping = {
		id generator:'uuid', sqlType: "varchar(36)"
	}
	
	static constraints = {
		id maxSize: 36
	}
}
