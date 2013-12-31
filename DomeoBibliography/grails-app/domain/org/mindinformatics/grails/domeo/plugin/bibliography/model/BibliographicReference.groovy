package org.mindinformatics.grails.domeo.plugin.bibliography.model

import java.util.Date;

/**
 * @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
 */
class BibliographicReference {

	private static final int NAME_MAX_SIZE = 512;
	
	String id
	String title;
	String journalName;
	String journalIssn;
	String authorNames;
	String publicationDate;
	String publicationInfo;
	String publicationType;
	String text;
	
	// IDs
	String doi;
	String pubMedId;
	String pubMedCentralId;
	String publisherItemId;
	
	Date dateCreated, lastUpdated // Grails automatic timestamping
	
	static mapping = {
		id generator:'uuid', sqlType: "varchar(36)"
	}
	
	static constraints = {
		id maxSize: 36
	
		title (nullable: true, blank: true, maxSize:NAME_MAX_SIZE)
		journalName (nullable: true, blank: true, maxSize:NAME_MAX_SIZE)
		journalIssn (nullable: true, blank: true, maxSize:NAME_MAX_SIZE)
		authorNames (nullable: true, blank: true, maxSize:NAME_MAX_SIZE)
		publicationDate (nullable: true, blank: true, maxSize:NAME_MAX_SIZE)		
		publicationInfo (nullable: true, blank: true, maxSize:NAME_MAX_SIZE)
		publicationType (nullable: true, blank: true, maxSize:NAME_MAX_SIZE)
		
		text (nullable: true, blank: true, maxSize:NAME_MAX_SIZE)
		
		doi (nullable: true, blank: true, maxSize:NAME_MAX_SIZE)
		pubMedId (nullable: true, blank: true, maxSize:NAME_MAX_SIZE)
		pubMedCentralId (nullable: true, blank: true, maxSize:NAME_MAX_SIZE)
		publisherItemId (nullable: true, blank: true, maxSize:NAME_MAX_SIZE)
	}
	
}

