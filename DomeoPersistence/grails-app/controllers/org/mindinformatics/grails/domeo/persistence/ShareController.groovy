package org.mindinformatics.grails.domeo.persistence

import grails.converters.JSON

import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.json.JSONObject
import org.mindinformatics.domeo.persistence.ElasticSearchWrapper
import org.mindinformatics.grails.domeo.dashboard.security.User



class ShareController {

    private boolean ELASTICO = true;
    
    def readOnlyService;
    def grailsApplication;
    def springSecurityService;
    def annotationPermissionService;
    
    /**
    * User injection
    * @return The logged user
    */
   private def injectUserProfile() {
       def principal = springSecurityService.principal
       if(principal.equals("anonymousUser")) {
           redirect(controller: "login", action: "index");
       } else {
           String username = principal.username
           def user = User.findByUsername(username);
           if(user==null) {
               render (view:'error', model:[message: "User not found for username: "+username]);
           }
           user
       }
   }
    
    /**
     * This method is meant to be used for integration with other systems or 
     * software components (for example browser plugins). It requires:
     * - url: the URL of the web resource for which we are checking if 
     *        annotation sets exists
     * - key: the unique key that is used for identification purposes (this
     *        might change in future versions).
     */
    def annotationSetsByUrl = {
        def key = params.key;
        def url = params.url;
        
        if(url && key) {
            log.info("Requested existing annotation for url: " + url);
            def sets = LastAnnotationSetIndex.findAllByAnnotatesUrl(url);
       
            int totalAnnotations = 0;
            JSONArray resultSets = new JSONArray();
            for(def set: sets) {
                def s = AnnotationSetIndex.findByIndividualUri(set.lastVersionUri);
                if(s) {
                    JSONObject resultSet = readOnlyService.createAnnotationSetSummary(s, "unknown");
                    resultSet.put("size", s.size);
                    totalAnnotations+=s.size;
                    resultSets.add(resultSet);
                }
            }
            
            JSONObject result = new JSONObject();
            result.put("sets", resultSets);
            result.put("totalAnnotations", totalAnnotations);
            render (result as JSON);
        } else {
            if(!url) log.info("Request for annotation existance without url ");
            if(!key) log.info("Request for annotation existance without key ");
            JSONObject result = new JSONObject();
            result.put("error", "Incomplete request");
            render (result as JSON);
        }
    }
    
    def set = {
        def annotationSetIndex = AnnotationSetIndex.findByIndividualUri(params.id);
        if(annotationSetIndex==null) {
            render (view:'/problem', model:[message: 'The requested Annotation Set has not been found'])
            return
        }
        
        def user = userProfile();     
        if(!user.hasProperty("id")) {
            log.warn("Annotation Set " + params.id + " denied for anonymous User");
            render (view:'/problem', model:[message: 'You need to be logged in to access the requested Annotation Set'])
            return
        }  
        if(!annotationPermissionService.isPermissionGranted(user, annotationSetIndex)) {
            log.warn("Annotation Set " + params.id + " denied for User " + (user.hasProperty("id")?user.id:user));
            render (view:'/problem', model:[message: 'You don\t have permission to access the requested Annotation Set'])
            return
        }
        
        JSONArray responseToSets = new JSONArray();
        log.info("User " + user.id + " requested *serialization of Annotation Set* " + params.id);
        try {
            int counter = 0;
            
            if(ELASTICO) {
                ElasticSearchWrapper esWrapper = new ElasticSearchWrapper(grailsApplication.config.elastico.database, grailsApplication.config.elastico.collection, grailsApplication.config.elastico.ip, grailsApplication.config.elastico.port);
                String document = esWrapper.getDocument(annotationSetIndex.mongoUuid);
                log.debug("Retrieved: " + document);
               
                if(document!=null) {
                    def ret = JSON.parse(document);
                    if(ret.hits.total==1) {
                        def set = ret.hits.hits[0]._source;
                        
                        // Removing permissions details
                        set.remove("permissions:permissions");
                        
                        log.debug("Sharing: " + set);
                        if(set!=null) {
                            responseToSets.add(set);
                            counter++;
                        }
                    }
                }
            }
        } catch(Exception e) {
            //trackException(userId, textContent, "FAILURE: Retrieval of existing annotation sets failed " + e.getMessage());
            println  e.getMessage();
        }
        
        render (responseToSets as JSON);
    }
    
    def sets = {
        def url = params.url
        def loggedUser = injectUserProfile();
        
        if(!loggedUser.hasProperty("id")) {
            log.warn("Annotation shared denied for anonymous User");
            render (view:'/problem', model:[message: 'You need to be logged in to access the administration tools'])
            return
        }
        
        ElasticSearchWrapper esWrapper = new ElasticSearchWrapper(grailsApplication.config.elastico.database, grailsApplication.config.elastico.collection, grailsApplication.config.elastico.ip, grailsApplication.config.elastico.port);
        def sets = LastAnnotationSetIndex.findAllByAnnotatesUrl(url);
        
        def s;
        JSONObject buffer;

        response.setHeader "Content-disposition", "attachment; filename=export"
        response.contentType = 'application/json;charset=utf-8'
        //response.characterEncoding = 'utf-8'
        response.outputStream << '{';
        response.outputStream << readOnlyService.getExportHeader(loggedUser, request);
        response.outputStream << '"oa:item" : ['
        
        int counter = 0;
        for(LastAnnotationSetIndex set:sets) {
            
            s = AnnotationSetIndex.findByIndividualUri(set.lastVersionUri);
            if(s!=null) {
               
                log.info('Serializing ' + s.individualUri);
                buffer = readOnlyService.getAnnotationSet(esWrapper, s);
                if(!buffer.isEmpty()) {
                    log.info("Serializing set: " + set.lastVersionUri)
                    response.outputStream << buffer.toString()
                    if(++counter<sets.size()) response.outputStream << ','
                } else {
                    log.info("Skipping serialization of set: " + set.lastVersionUri)
                }
            } else {
                log.warn("Annotation set dump failed for set: " + set.lastVersionUri);
            }
        }
        response.outputStream << ']'
        response.outputStream << '}'
        
        response.outputStream.flush()
        response.outputStream.close()
    }
    
    private def userProfile() {
        def user;
        def principal = springSecurityService.principal
        if(!principal.equals("anonymousUser")) {
            String username = principal.username
            user = User.findByUsername(username);
            return user
        }
        "<unknown>"
    }
}
