package org.mindinformatics.grails.domeo.persistence

import grails.converters.JSON

import java.text.SimpleDateFormat;

import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.json.JSONObject;
import org.mindinformatics.domeo.persistence.ElasticSearchWrapper
import org.mindinformatics.grails.domeo.dashboard.security.User

class DumpController {

    def agentsService;
    def readOnlyService;
    def grailsApplication;
    def springSecurityService;
    
    def dumpAnnotation = {
        def loggedUser = injectUserProfile();
        
        if(!loggedUser.hasProperty("id")) {
            log.warn("Annotation dump denied for anonymous User");
            render (view:'/problem', model:[message: 'You need to be logged in to access the administration tools'])
            return
        }
        
        if(loggedUser.getIsAdmin()) {
            ElasticSearchWrapper esWrapper = new ElasticSearchWrapper(grailsApplication.config.elastico.database, grailsApplication.config.elastico.collection, grailsApplication.config.elastico.ip, grailsApplication.config.elastico.port);
            def sets = AnnotationSetIndex.list();
            
            def s;
            JSONObject buffer;

            response.setHeader "Content-disposition", "attachment; filename=exportall"
            response.contentType = 'application/json;charset=utf-8'
            //response.characterEncoding = 'utf-8'
            response.outputStream << '{';
            response.outputStream << readOnlyService.getExportHeader(loggedUser, request);
            response.outputStream << '"oa:item" : ['
            
            int counter = 0;
            for(AnnotationSetIndex set:sets) {
                buffer = readOnlyService.getAnnotationSet(esWrapper, set);
                if(!buffer.isEmpty()) {
                    log.info("Serializing set: " + set.individualUri)
                    response.outputStream << buffer.toString()
                        if(++counter<sets.size()) response.outputStream << ','
                } else {
                    log.info("Skipping serialization of set: " + set.individualUri)
                }
            }
        } else {
            render (view:'/problem', model:[message: "User does not have permissions to dump the entire knowledge base"]);
        }
    } 
    
    def dumpLastVersionAnnotation = {
        def loggedUser = injectUserProfile();
        
        if(!loggedUser.hasProperty("id")) {
            log.warn("Annotation dump denied for anonymous User");
            render (view:'/problem', model:[message: 'You need to be logged in to access the administration tools'])
            return
        }
        
        if(loggedUser.getIsAdmin()) {
            ElasticSearchWrapper esWrapper = new ElasticSearchWrapper(grailsApplication.config.elastico.database, grailsApplication.config.elastico.collection, grailsApplication.config.elastico.ip, grailsApplication.config.elastico.port);
            def sets = LastAnnotationSetIndex.list();
            
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
        } else {
            render (view:'/problem', model:[message: "User does not have permissions to dump the entire knowledge base"]);
        }
    }
    
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
}
