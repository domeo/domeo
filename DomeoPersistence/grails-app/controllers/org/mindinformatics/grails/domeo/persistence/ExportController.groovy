package org.mindinformatics.grails.domeo.persistence

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
	def mappingsService;
    
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
	
	// http://localhost:3333/Domeo/export/idmappings?id=10.1111%2Fj.1460-9568.2004.03745.x
	// http://localhost:3333/Domeo/export/idmappings/15548226
	def idmappings = {
		def TYPE = "id-mappings";
		def id = params.id;
		def ip = request.remoteAddr;

		// Request details
		JSONObject req = new JSONObject();
		JSONObject reqContent = new JSONObject();
		reqContent.put("id", (id!=null)?id:"");
		req.put("content", reqContent);
		req.put("type", TYPE);
		req.put("ip", ip);
		
		try {
			JSONObject mo = new JSONObject();
			mo.put("request", req);
			mo.put("response", mappingsService.findMappingsById(id));

			log.debug mo;
			render mo
		} catch(Exception e) {
			JSONObject mo = new JSONObject();
			mo.put("request", req);
			mo.put("exception", e.getMessage().replaceAll("\n", " "));
			render mo;
		}	
	}
	
	def document = {
		def urls = [] as Set
		def id = params.id;
		
		log.info "----------------------------"
		log.info " Looking for: " + id
		
		if(id!=null) {
			def ids = BibliographicIdMapping.findAllByIdValue(id);
			log.info " Mappings: " + ids
			ids.each {
				def bibs = BibliographicSetIndex.findAllByUuidBibliographicIdMapping(it.uuid);
				bibs.each { bib ->
					urls.add(bib.annotatesUrl)
				}
			}
		}
		
		
		
		
		
		def ids = [] as Set
		urls.each {
			//render '**** ' + it + '<br/>'
			def lasts = LastAnnotationSetIndex.findAllByAnnotatesUrl(it);
			lasts.each { last ->
				ids.add(last.lastVersionId);
				//render last.lastVersionId + '<br/>'
			}
		}
		
		ElasticSearchWrapper esWrapper = new ElasticSearchWrapper(grailsApplication.config.elastico.database, grailsApplication.config.elastico.collection, grailsApplication.config.elastico.ip, grailsApplication.config.elastico.port);
		
		def O = "<td>"
		def C = "</td>"
		def CO = C + O;
		
		render '<table>'
		ids.each{	
			
			//render AnnotationSetIndex.findById(it).mongoUuid + '<br/>';				
			String document = esWrapper.getDocument(AnnotationSetIndex.findById(it).mongoUuid);
			//render document + '<br/>';
			def json = JSON.parse(document);
			def annotates = json.hits.hits._source[0]['ao:annotatesResource'];
			println annotates
			def items = json.hits.hits._source[0]["ao:item"];
			items.each {
				render '<tr>'
				//render json.hits.hits._source[0] 
				def anntype = it['@type'];
				if(it["ao:context"]['ao:hasSource'][0]) 
					render O + it["@id"] + CO + it["ao:context"]['ao:hasSource'][0] + CO + it['@type'] + CO + it["pav:lastSavedOn"]
				else {
					println 'problem with serialization ' + it
					render O + it["@id"] + CO + annotates + CO + it['@type'] + CO + it["pav:lastSavedOn"]
				}
				if(it["ao:hasTopic"]) {
					 //render " --- " + it["ao:hasTopic"][0]
					 render CO + it["ao:hasTopic"][0]["rdfs:label"]
					 render CO + it["ao:hasTopic"][0]["@id"]
				} else if (it["ao:body"]){
					if(anntype.contains("oa:PostIt") || anntype.contains("ao:Comment")) {
						render CO + it["ao:body"][0]["cnt:chars"]
					} else if (anntype.contains("AntibodyAnnotation")) {
						if(it["ao:body"][0]["domeo:protocol"])
						render CO + it["ao:body"][0]["domeo:protocol"]["rdfs:label"]
						if(it["ao:body"][0]["domeo:method"])
						render CO + it["ao:body"][0]["domeo:method"]["rdfs:label"]
						if(it["ao:body"][0]["domeo:antibody"])
						render CO + it["ao:body"][0]["domeo:antibody"]["@id"]
					} else if (anntype.contains("Curation")) {
						//println "Curation " + it 
						if(it["ao:body"][0]["rdf:value"])
							render CO + it["ao:body"][0]["rdf:value"] + CO + it["ao:context"][0]["ao:hasSelector"]["ao:annotation"]
					}  
//					render " --- " + it["ao:body"]["@type"]
					
//					render " --- " + it["ao:body"]["@type"]
//					if(it["ao:body"][0] && it["ao:body"][0]["@type"] && it["ao:body"][0]["@type"]=="cnt:ContentAsText") {
//						render " --- " + it["ao:body"][0]["@type"]
//					}
				} else {
					if (anntype.contains("ao:Highlight")) {
						if(it["ao:context"][0]["ao:hasSelector"]["@type"]=="ao:PrefixSuffixTextSelector") 
							render CO + it["ao:context"][0]["ao:hasSelector"]["ao:exact"]
					}
				}
				//render it  
				render C
				render '</tr>'
			}
			
			//log.debug("Retrieved: " + document);
		}
		
		render '</table>'
	}
	
	// http://localhost:3333/Domeo/export/document/PMC2700002

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
