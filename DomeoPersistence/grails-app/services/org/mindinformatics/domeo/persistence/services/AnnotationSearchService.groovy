package org.mindinformatics.domeo.persistence.services

import org.mindinformatics.domeo.persistence.DomeoPermissions
import org.mindinformatics.domeo.persistence.ElasticSearchWrapper

class AnnotationSearchService {

    def grailsApplication;
    
    public String search(String field, String query, boolean isPublic, String isPrivate) {
		
		DomeoPermissions dp = new DomeoPermissions(isPublic?DomeoPermissions.PUBLIC_VALUE:null, isPrivate!=null?isPrivate:null, null);
		
        ElasticSearchWrapper esWrapper = new ElasticSearchWrapper(grailsApplication.config.elastico.database, grailsApplication.config.elastico.collection, grailsApplication.config.elastico.ip, grailsApplication.config.elastico.port);
        String results = esWrapper.phraseQuery(field, query, 0, 20, dp);
        results
    }
}
