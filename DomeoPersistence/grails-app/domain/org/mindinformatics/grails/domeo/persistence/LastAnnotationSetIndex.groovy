package org.mindinformatics.grails.domeo.persistence

class LastAnnotationSetIndex {

	String id; 			
	/*
	 * Uuid of the last version, by calling this Uuid the last version of the annotation set is always returned
	 */
	String lineageUri; 
	String lastVersionUri;
	
	boolean isDeleted;
	
	// TODO abstract for pages with multiple URLs
	String annotatesUrl;
	
	Date dateCreated;
	Date lastUpdated;
	
	/*
	 * Pointer to the index of the last recorded version of the annotation set		
	 */
	AnnotationSetIndex lastVersion
	
	static mapping = {
		id generator:'uuid'
	}
}
