package org.mindinformatics.grails.domeo.persistence

import java.util.Date;

import org.mindinformatics.grails.domeo.dashboard.security.User

class AnnotationSetIndex {
 
	String id; 			 
	String individualUri; // Identifier of the annotation set individual
	String lineageUri;    // Identifier of the annotation set lineage (see versioning)
	String versionNumber;
	String previousVersion;
	String mongoUuid;
	
	String type;
	String label;
	String description; 
	
	// TODO abstract for pages with multiple URLs
	String annotatesUrl;
	
	Integer size;
	
	User createdBy;
	Date createdOn;
	Date lastSavedOn;
	
	static mapping = { 
		id generator:'uuid' 
	}
	
	static constraints = {
		label maxSize: 5000
		description maxSize: 6000
		versionNumber nullable:true
		previousVersion nullable:true
		mongoUuid nullable: true
	}
}
