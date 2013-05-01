package org.mindinformatics.grails.domeo.dashboard.circles

import org.mindinformatics.grails.domeo.dashboard.UsersCollectionType
import org.mindinformatics.grails.domeo.dashboard.groups.Group
import org.mindinformatics.grails.domeo.dashboard.security.User

/**
 * @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
 */
class Circle extends UsersCollectionType {

	String getUri() {
		return "urn:circle:uuid:"+id;
	}
}
