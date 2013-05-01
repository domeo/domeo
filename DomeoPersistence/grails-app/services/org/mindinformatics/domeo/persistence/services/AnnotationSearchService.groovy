package org.mindinformatics.domeo.persistence.services

import org.codehaus.groovy.grails.web.json.JSONObject;
import org.mindinformatics.domeo.persistence.ElasticSearchWrapper

class AnnotationSearchService {

    def grailsApplication;
    
    public String search(String field, String query) {
        ElasticSearchWrapper esWrapper = new ElasticSearchWrapper(grailsApplication.config.elastico.database, grailsApplication.config.elastico.collection, grailsApplication.config.elastico.ip, grailsApplication.config.elastico.port);
        String results = esWrapper.termQuery(field, query, 0, 20);
        results
    }
}
