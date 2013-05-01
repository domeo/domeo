package org.mindinformatics.grails.domeo.persistence

import java.text.SimpleDateFormat;

import grails.converters.JSON

import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.json.JSONObject
import org.mindinformatics.domeo.persistence.ElasticSearchWrapper
import org.mindinformatics.grails.domeo.dashboard.groups.UserGroup
import org.mindinformatics.grails.domeo.dashboard.security.User

class ExportController {

    private boolean ELASTICO = true;
    
    def readOnlyService;
    def springSecurityService
    def usersManagementService
    
    def export = {
        if (params.mine) {
            
            def lineages = []
            def loggedUser = injectUserProfile();
            
            if(!loggedUser.hasProperty("id")) {
                log.warn("Annotation export *own* denied for anonymous User");
                render (view:'/problem', model:[message: 'You need to be logged in to access the export tools'])
                return
            }
            
            log.info("User: " + loggedUser.id + " exporting *own* annotation.")
            
            // Find all the latest versions of my sets
            def permissions = AnnotationSetPermissions.findAllByPermissionType("urn:person:uuid:"+loggedUser.id);
            if(permissions) {
                for(AnnotationSetPermissions permission: permissions) {
                    log.debug("permission: " + permission)
                    lineages.add(permission.lineageUri);
                }
            }
         
            // 
            JSONArray responseToSets = new JSONArray();
            for(String lineageUri: lineages) {
                log.debug('lineageUri ' + lineageUri)
                def last = LastAnnotationSetIndex.findByLineageUri(lineageUri);
                if(last) {
                    def set = AnnotationSetIndex.findByIndividualUri(last.lastVersionUri)
                    if(set) {
                        try {
                            int counter = 0;
                            
                            if(ELASTICO) {
                                ElasticSearchWrapper esWrapper = new ElasticSearchWrapper(grailsApplication.config.elastico.database, grailsApplication.config.elastico.collection, grailsApplication.config.elastico.ip, grailsApplication.config.elastico.port);
                                String document = esWrapper.getDocument(set.mongoUuid);
                                log.debug("Retrieved: " + document);
                               
                                if(document!=null) {
                                    def ret = JSON.parse(document);
                                    if(ret.hits.total==1) {
                                        def jsonSet = ret.hits.hits[0]._source;
                                        
                                        // Removing permissions details
                                        //jsonSet.remove("permissions:permissions");
                                        
                                        log.debug("Sharing: " + jsonSet);
                                        if(jsonSet!=null) {
                                            responseToSets.add(jsonSet);
                                            counter++;
                                        }
                                    }
                                }
                            }
                        } catch(Exception e) {
                            //trackException(userId, textContent, "FAILURE: Retrieval of existing annotation sets failed " + e.getMessage());
                            println  e.getMessage();
                        }
                    } else {
                        println 'no set found'
                    }
                } else {
                    println 'no lineage found'
                }
            }
            JSONObject results = readOnlyService.getExportContainer(loggedUser, request);
            results.put("ao:item", responseToSets)

            render (text: results as JSON, contentType: 'application/json', encoding:"UTF-8");
        } else if(params.groups) {
            def lineages = []
            def loggedUser = injectUserProfile();
            
            if(!loggedUser.hasProperty("id")) {
                log.warn("Annotation export *groups* denied for anonymous User");
                render (view:'/problem', model:[message: 'You need to be logged in to access the export tools'])
                return
            }
            
            log.info("User: " + loggedUser.id + " exporting *groups* annotation.")
            
            def groups = []
            def userGroups = UserGroup.findAllByUser(loggedUser);
            if(userGroups) {
                for(def userGroup: userGroups) {
                    groups.add(userGroup.group);
                }
            }
            log.debug("groups: " + groups);
            
            // Find all the lineages of groups sets
            def permissions = AnnotationSetPermissions.findAllByPermissionType("urn:domeo:access:groups");
            if(permissions) {
                for(AnnotationSetPermissions permission: permissions) {
                    log.debug("permission: " + permission.annotationSet)
                    lineages.add(permission.lineageUri);
                }
            }
            
            // Find all the latest versions of sets
            JSONArray responseToSets = new JSONArray();
            for(String lineageUri: lineages) {
                log.debug('lineageUri ' + lineageUri);
                def last = LastAnnotationSetIndex.findByLineageUri(lineageUri);
                if(last) {
                    def set = AnnotationSetIndex.findByIndividualUri(last.lastVersionUri)
                    if(set) {
                        try {
                            int counter = 0;
                            
                            if(ELASTICO) {
                                ElasticSearchWrapper esWrapper = new ElasticSearchWrapper(grailsApplication.config.elastico.database, grailsApplication.config.elastico.collection, grailsApplication.config.elastico.ip, grailsApplication.config.elastico.port);
                                String document = esWrapper.getDocument(set.mongoUuid);
                                log.debug("Retrieved: " + document);
                               
                                if(document!=null) {
                                    def ret = JSON.parse(document);
                                    if(ret.hits.total==1) {
                                        def jsonSet = ret.hits.hits[0]._source;
                                        
                                        // Removing permissions details
                                        //jsonSet.remove("permissions:permissions");
                                        
                                        log.debug("Sharing: " + jsonSet);
                                        if(jsonSet!=null) {
                                            responseToSets.add(jsonSet);
                                            counter++;
                                        }
                                    }
                                }
                            }
                        } catch(Exception e) {
                            //trackException(userId, textContent, "FAILURE: Retrieval of existing annotation sets failed " + e.getMessage());
                            println  e.getMessage();
                        }
                    } else {
                        println 'no set found'
                    }
                } else {
                    println 'no lineage found'
                }
            }
            
            JSONObject results = readOnlyService.getExportContainer(loggedUser, request);
            results.put("ao:item", responseToSets)
            
            render (text: results as JSON, contentType: 'application/json', encoding:"UTF-8");
        } else {
            render "Nothing to be exported"
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
