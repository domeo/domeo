package org.mindinformatics.grails.domeo.dashboard

import java.util.Date;

/**
* @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
*/
class UsersCollectionType {

	Date dateCreated, lastUpdated // Grails automatic timestamping
	
	String id;
	String name;
	String shortName;
	
	static mapping = {
		id generator:'uuid', sqlType: "varchar(36)"
	}
	
	static constraints = {
		id maxSize: 36
		name (nullable:false, blank: false, unique: true, maxSize:255)
		shortName  (nullable:true, blank: true, maxSize:100)
	}
}

