package org.mindinformatics.domeo.persistence.services

import org.mindinformatics.domeo.persistence.DomeoPermissions
import org.mindinformatics.domeo.persistence.ElasticSearchWrapper

class AnnotationSearchService {

    def grailsApplication;
    
    public String search(String field, String query, boolean isPublic, String isPrivate) {
		
		DomeoPermissions dp = new DomeoPermissions(isPublic?DomeoPermissions.PUBLIC_VALUE:null, isPrivate!=null?isPrivate:null, null);
		
        ElasticSearchWrapper esWrapper = new ElasticSearchWrapper(grailsApplication.config.elastico.database, grailsApplication.config.elastico.collection, grailsApplication.config.elastico.ip, grailsApplication.config.elastico.port);
        String results = esWrapper.phraseQuery(field, query, 0, 200, false, dp);
        results
    }
	
	public String searchMultiple(String[] fields, String[] vals, String[] parsed, boolean isPublic, String isPrivate, String groupId) {
		
		ArrayList<String> l = new ArrayList<String>();
		if(groupId.trim().length()>0) {			
			StringTokenizer st = new StringTokenizer(groupId, ',');
			while(st.hasMoreTokens()) l.add(st.nextToken().trim());
			//println '++++++++++++++++++++ ' + l.size();
		}
				
		String[] ids = new String[l.size()];
		for(int i = 0; i<l.size(); i++) {
			ids[i] = l.get(i);
		}
		println '++++++++++++++++++++ ' + ids;
		
		DomeoPermissions dp = new DomeoPermissions(isPublic?DomeoPermissions.PUBLIC_VALUE:null, isPrivate!=null?isPrivate:null, ids);
		
		ElasticSearchWrapper esWrapper = new ElasticSearchWrapper(grailsApplication.config.elastico.database, grailsApplication.config.elastico.collection, grailsApplication.config.elastico.ip, grailsApplication.config.elastico.port);
		//String[] parsed = ["term", "term", "term", "match"];
		String results = esWrapper.booleanQueryMultipleFields(fields, vals, parsed, "and", 0, 200, false, dp);
		results
	}
	
	public String searchItems(String[] fields, String[] vals, String[] parsed, boolean isPublic, String isPrivate) {
		DomeoPermissions dp = new DomeoPermissions(isPublic?DomeoPermissions.PUBLIC_VALUE:null, isPrivate!=null?isPrivate:null, null);
		
		ElasticSearchWrapper esWrapper = new ElasticSearchWrapper(grailsApplication.config.elastico.database, grailsApplication.config.elastico.collection, grailsApplication.config.elastico.ip, grailsApplication.config.elastico.port);
		//String[] parsed = ["match","match", "term"];
		String results = esWrapper.booleanQueryMultipleFields(fields, vals, parsed, "and", 0, 200, true, dp);
		results
	}
}
