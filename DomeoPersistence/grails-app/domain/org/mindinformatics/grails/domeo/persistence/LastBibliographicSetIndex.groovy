package org.mindinformatics.grails.domeo.persistence

class LastBibliographicSetIndex {

	String id; 	
	String doi;
	String pmcId;
	String pubmedId;
	String annotatesUrl;
			
	/*
	 * Uuid of the last version, by calling this Uuid the last version of the annotation set is always returned
	 */
	String lineageUri; 
	/*
	 * This is redaundant as it is part of 'lastVersion', however 
	 */
	String lastVersionUri;
	
	/*
	* Pointer to the index of the last recorded version of the annotation set
	*/
    BibliographicSetIndex lastVersion
	
	static hasMany = [urls: UrlIndex]
	static mapping = {id generator:'uuid'}
	
	static constraints = {
		doi nullable:true
		pmcId nullable:true
		pubmedId nullable:true
	}
}
