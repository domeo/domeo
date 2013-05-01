package org.mindinformatics.domeo.persistence.services

import grails.converters.JSON

import java.text.SimpleDateFormat;

import org.codehaus.groovy.grails.web.json.JSONObject
import org.mindinformatics.domeo.persistence.ElasticSearchWrapper

class ReadOnlyService {

    def agentsService;
    def grailsApplication;
    
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
    
    private def createAnnotationSetSummary(def setLastVersion, String accessType) {
        JSONObject setSummary = new JSONObject();
        setSummary.put("@id", setLastVersion.individualUri);
        setSummary.put("@type", setLastVersion.type);
        setSummary.put("rdfs:label", setLastVersion.label);
        setSummary.put("dct:description", setLastVersion.description);
        setSummary.put("pav:lineageUri", setLastVersion.lineageUri);
        setSummary.put("domeo:mongoUuid", setLastVersion.mongoUuid);
        
        setSummary.put("pav:versionNumber", setLastVersion.versionNumber);
        setSummary.put("pav:previousVersion", setLastVersion.previousVersion);
        setSummary.put("ao:numberItems", setLastVersion.size);
        setSummary.put("pav:lastSavedOn", dateFormat.format(setLastVersion.lastSavedOn));
        setSummary.put("pav:createdOn", dateFormat.format(setLastVersion.createdOn));
        
        setSummary.put("permissions:accessType", accessType);
        
        def createdBy = setLastVersion.createdBy;
        
        JSONObject creator = new JSONObject();
        creator.put("uri", createdBy.id);
        creator.put("screenname", createdBy.displayName);
        creator.put("foaf_title", createdBy.title);
        creator.put("foaf_first_name", createdBy.firstName);
        creator.put("foaf_middle_name", createdBy.middleName);
        creator.put("foaf_last_name", createdBy.lastName);
        
        setSummary.put("pav:createdBy", creator);
        setSummary;
    }
    
    private JSONObject getExportContainer(def user, def request) {
        JSONObject results = new JSONObject();
        results.put("pav:createdBy", user.id);
        results.put("pav:createdOn", dateFormat.format(new Date()));
        results.put("pav:createdWith", JSON.parse(agentsService.getThisSoftware()));
        results.put("foaf:homepage", "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath());
        return results;
    }
    
    private String getExportHeader(def user, def request) {
        StringBuffer sb = new StringBuffer();
        sb.append('"pav:createdBy":"' + user.id + '",');
        sb.append('"pav:createdWith":' + JSON.parse(agentsService.getThisSoftware()).toString() + ',');
        sb.append('"foaf:homepage":"' + "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + '",');
        return sb.toString();
    }
    
    private JSONObject getAnnotationSet(ElasticSearchWrapper esWrapper, def set) {
        JSONObject jsonSet = new JSONObject();
        try {
            int counter = 0;
  
            String document = esWrapper.getDocument(set.mongoUuid);
            log.debug("Retrieved: " + document);

            if(document!=null) {
                def ret = JSON.parse(document);
                if(ret.hits.total==1) {
                    jsonSet = ret.hits.hits[0]._source;
                    
                    // Removing permissions details
                    //jsonSet.remove("permissions:permissions");

                    log.debug("Sharing: " + jsonSet);
                    if(jsonSet!=null) {
                        counter++;
                    }
                }
            }
        } catch(Exception e) {
            //trackException(userId, textContent, "FAILURE: Retrieval of existing annotation sets failed " + e.getMessage());
            log.error(e.getMessage());
        }
        return jsonSet;
    }
}
