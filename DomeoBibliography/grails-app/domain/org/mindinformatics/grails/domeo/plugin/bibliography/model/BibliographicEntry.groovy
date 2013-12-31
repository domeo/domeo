package org.mindinformatics.grails.domeo.plugin.bibliography.model

import org.mindinformatics.grails.domeo.dashboard.security.User

/**
 * @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
 */
class BibliographicEntry {

	private static final int NAME_MAX_SIZE = 512;
	
	String id
	String url
	String title 
	
	boolean starred
	
	BibliographicReference reference
	Date dateCreated, lastUpdated // Grails automatic timestamping
	
	static belongsTo = [userBibliography: UserBibliography]
	
	static mapping = {
		id generator:'uuid', sqlType: "varchar(36)"
	}
	
	static constraints = {
		id maxSize: 36
		
		title (nullable: true, blank: true, maxSize:NAME_MAX_SIZE)
		reference (nullable: true, blank: true)
	}
	
}
