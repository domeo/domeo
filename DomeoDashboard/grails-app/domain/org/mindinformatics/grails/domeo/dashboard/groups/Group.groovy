package org.mindinformatics.grails.domeo.dashboard.groups

import java.util.Date;

import grails.validation.Validateable;

import org.mindinformatics.grails.domeo.dashboard.UsersCollectionType
import org.mindinformatics.grails.domeo.dashboard.security.Privacy

/**
* @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
*/
@Validateable
class Group extends UsersCollectionType {

	String description;
	String membersCounter;
	
	Privacy privacy;
	
	boolean enabled
	boolean locked
	
	String getStatus() {
		return GroupUtils.getStatusValue(this);
	}
	
	String getStatusUuid() {
		return GroupUtils.getStatusUuid(this);
	}
	
	String getStatusLabel() {
		return GroupUtils.getStatusLabel(this);
	}
	
	String getUri() {
		return "urn:group:uuid:"+id;
	}
	
	static mapping = {
		table 'ggroupp'
	}
	
	static transients = [
		'membersCounter'
	]
	
	static constraints = {
		description (nullable:false, blank:true, maxSize:1024)
	}
}
