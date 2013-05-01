package org.mindinformatics.grails.domeo.dashboard

import grails.validation.Validateable;

import org.mindinformatics.grails.domeo.dashboard.groups.DefaultGroupStatus
import org.mindinformatics.grails.domeo.dashboard.groups.Group
import org.mindinformatics.grails.domeo.dashboard.groups.GroupPrivacy
import org.mindinformatics.grails.domeo.dashboard.groups.GroupStatus
import org.mindinformatics.grails.domeo.dashboard.security.Privacy
import org.mindinformatics.grails.domeo.dashboard.security.Status


/**
 * Object command for Group validation and creation.
 * 
 * @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
 */
@Validateable
class GroupCreateCommand {
	
	public static final Integer NAME_MAX_SIZE = 255;
	public static final Integer SHORTNAME_MAX_SIZE = 16;
	public static final Integer DESCRIPION_MAX_SIZE = 1024;
	
	String name;
	String shortName;
	String description;

	String status;
	String privacy;
	
	static constraints = {
		name (nullable:false, blank: false, maxSize:NAME_MAX_SIZE)
		shortName (nullable:false, blank: false, maxSize:SHORTNAME_MAX_SIZE)
		description (nullable:false, blank:true, maxSize:DESCRIPION_MAX_SIZE)
	}
	
	boolean isEnabled() {
		return status.equals(DefaultGroupStatus.ACTIVE.value);
	}
	
	boolean isLocked() {
		return status.equals(DefaultGroupStatus.LOCKED.value);
	}
	
	Status getStatus(def value) {
		def status = GroupStatus.findByValue(value);
		println 'getStatus ' + status;
		if(status) return status;
		else return new Status(value: DefaultGroupStatus.ACTIVE.value(), label: DefaultGroupStatus.ACTIVE.label(), description: DefaultGroupStatus.ACTIVE.description());
	}
	
	Privacy getPrivacy(def value) {
		def privacy = GroupPrivacy.findByValue(value);
		if(privacy) return privacy;
		else return new Privacy(value: "G_PUBLIC", label: "Public", description: "Public");
	}
	
	Group createGroup() {
		// Names and nicknames are supposed to be unique
		println 'createGroup'
		if(Group.findByName(name)!=null || Group.findByShortName(shortName)!=null) return null;
		// If the group does not exist I create a new one
		else return new Group(name:name, shortName:shortName, description:description, 
			status: getStatus("G_ACTIVE"), privacy: getPrivacy("G_PUBLIC"));
	}
}
