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
class GroupEditCommand extends GroupCreateCommand {
	
	String id;
	
	static constraints = {
		id (nullable:false, blank: false)
	}
}
