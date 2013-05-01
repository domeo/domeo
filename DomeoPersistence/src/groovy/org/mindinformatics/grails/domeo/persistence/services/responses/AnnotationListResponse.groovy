package org.mindinformatics.grails.domeo.persistence.services.responses

import org.mindinformatics.grails.domeo.dashboard.security.User
import org.mindinformatics.grails.domeo.persistence.LastAnnotationSetIndex

class AnnotationListResponse {

	int totalResponses;
	User latestContributor;
	Date latestContribution;
	List<AnnotationListItemWrapper> annotationListItemWrappers;
}
