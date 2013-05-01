package org.mindinformatics.grails.domeo.persistence

class SavingItemRecovery {

	String id;
	String userId;
	String exception;
	String mongoUuid;
	
	Date dateCreated;
	
	static mapping = {
		id generator:'uuid'
	}
	
	static constraints = {
		exception maxSize: 15000
		exception nullable: true
		mongoUuid nullable: true
	}
}
