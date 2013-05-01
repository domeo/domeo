package org.mindinformatics.grails.domeo.persistence

class BibliographicIdMapping implements Serializable {

	String uuid;
	String idLabel;
	String idValue;

	static constraints = {
		uuid maxSize: 38
		idLabel maxSize: 30
	}
	
	static mapping = {
		uuid sqlType: "varchar(38)"
		idLabel sqlType: "varchar(30)"
		id composite: ['uuid', 'idLabel']
	}
}

