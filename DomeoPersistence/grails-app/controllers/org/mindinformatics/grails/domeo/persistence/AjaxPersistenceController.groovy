package org.mindinformatics.grails.domeo.persistence

import grails.converters.JSON

import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.json.JSONObject
import org.mindinformatics.domeo.persistence.ElasticSearchWrapper
import org.mindinformatics.domeo.persistence.SleepyMongooseWrapper
import org.mindinformatics.grails.domeo.dashboard.security.User
import org.mindinformatics.grails.domeo.persistence.services.IOntology
import org.mindinformatics.grails.domeo.persistence.services.IPermissionTypes
import org.mindinformatics.grails.domeo.persistence.services.responses.AnnotationListItemWrapper
import org.mindinformatics.grails.domeo.persistence.services.responses.AnnotationListResponse
import org.mindinformatics.grails.domeo.persistence.services.responses.AnnotationSetItemWrapper

class AjaxPersistenceController {

    private boolean ELASTICO = true;
    
	def springSecurityService;
	def usersManagementService;
	def AnnotationPermissionService
	def usersGroupsManagementService
	def annotationSearchService;
	
	private def userProfileId() {
		def user;
		def principal = springSecurityService.principal
		if(!principal.equals("anonymousUser")) {
			String username = principal.username
			user = User.findByUsername(username);
			return user.id
		}
		"<unknown>"
	}
	
	private def userProfile() {
		def user;
		def principal = springSecurityService.principal
		if(!principal.equals("anonymousUser")) {
			String username = principal.username
			user = User.findByUsername(username);
		}
		user
	}
	
	// --------------------------------------------
	//  Logging utils
	// --------------------------------------------
	private def logInfo(def userId, message) {
		log.info(":" + userId + ": " + message);
	}
	
	private def logDebug(def userId, message) {
		log.debug(":" + userId + ": " + message);
	}
	
	private def logWarning(def userId, message) {
		log.warn(":" + userId + ": " + message);
	}
	
	private def logException(def userId, message) {
		log.error(":" + userId + ": " + message);
	}
	
	private def packageJsonErrorMessage(def userId, def exception, def ticket) {
		JSONObject message = new JSONObject();
		message.put("@type", "Exception");
		message.put("userid", userId);
		message.put("ticket", ticket);
		message.put("message", exception);
		JSONArray messages = new JSONArray();
		messages.put(message);
		return messages;
	}
	
	private void trackException(def userId, String textContent, String msg) {
		logException(userId, msg);
		//def ticket = saveAnnotationExitStrategy(userId, textContent, msg);
		response.status = 500
		//render (packageJsonErrorMessage(userId, msg, ticket) as JSON);
		return;
	}
	
	def annotationSets = {
		def user = userProfile();
        def documentUrl = params.documentUrl;
		
		try {
			User latestContributor = null;
			Date latestContribution = null;
			
			ArrayList<AnnotationListItemWrapper> annotationListItemWrappers = new ArrayList<AnnotationListItemWrapper>();
			List<LastAnnotationSetIndex> lastAnnotationSetIndexes;
            
            if(!documentUrl) lastAnnotationSetIndexes = LastAnnotationSetIndex.findAll('from LastAnnotationSetIndex as b order by b.lastUpdated desc')
            else lastAnnotationSetIndexes = LastAnnotationSetIndex.findAll('from LastAnnotationSetIndex as b where b.annotatesUrl=\'' + documentUrl + '\' order by b.lastUpdated desc')
            
			for(LastAnnotationSetIndex lastAnnotationSetIndex: lastAnnotationSetIndexes) {
				if(latestContribution==null || lastAnnotationSetIndex.lastVersion.createdOn.after(latestContribution)) {
					latestContribution = lastAnnotationSetIndex.lastVersion.createdOn;
					latestContributor = lastAnnotationSetIndex.lastVersion.createdBy;
				}
                
				if(annotationPermissionService.isPermissionGranted(user, lastAnnotationSetIndex.lastVersion)) {
					AnnotationListItemWrapper annotationListItemWrapper = new AnnotationListItemWrapper(lastAnnotationSetIndex: lastAnnotationSetIndex);
					annotationListItemWrappers.add(annotationListItemWrapper);
                 
					List<String> permissions = annotationPermissionService.getAnnotationSetPermissions(user.id, lastAnnotationSetIndex.lastVersion);
					if (permissions.get(0)==IPermissionTypes.publicAccess) {
						annotationListItemWrapper.permissionType = IPermissionTypes.publicAccess;
					} else if (permissions.get(0)==IPermissionTypes.groupsAccess) {
						annotationListItemWrapper.permissionType = IPermissionTypes.groupsAccess;
					} else {
						annotationListItemWrapper.permissionType = IPermissionTypes.privateAccess;
					}
					annotationListItemWrapper.isLocked = annotationPermissionService.isLocked(lastAnnotationSetIndex.lastVersion);
				}
			}
			
			AnnotationListResponse theResponse = new AnnotationListResponse(latestContributor: latestContributor,
				latestContribution: latestContribution, annotationListItemWrappers: annotationListItemWrappers, totalResponses: annotationListItemWrappers.size());
			JSON.use("deep")
			render (theResponse as JSON);
		} catch(Exception e) {
			trackException(user.id, "", "FAILURE: Retrieval of the list of existing annotation sets failed " + e.getMessage());
		}
	}
	
	def annotationSetHistory = {
		def user = userProfile();

		ArrayList<AnnotationSetIndex> annotationListItemWrappers = new ArrayList<AnnotationSetIndex>();
		
		def setIndex = AnnotationSetIndex.findByIndividualUri(params.setUri);
		if(setIndex!=null) {
			def setIndexes = AnnotationSetIndex.findAllByLineageUri(setIndex.lineageUri, [sort: "lastSavedOn", order: "desc"]);
			if(setIndexes!=null) {
				for(AnnotationSetIndex annotationSetIndex: setIndexes) {
					if(annotationPermissionService.isPermissionGranted(user, annotationSetIndex)) {
						AnnotationSetItemWrapper annotationListItemWrapper = new AnnotationSetItemWrapper(annotationSetIndex: annotationSetIndex);
						annotationListItemWrappers.add(annotationListItemWrapper);
					 
						List<String> permissions = annotationPermissionService.getAnnotationSetPermissions(user.id, annotationSetIndex);
						if (permissions.get(0)==IPermissionTypes.publicAccess) {
							annotationListItemWrapper.permissionType = IPermissionTypes.publicAccess;
						} else if (permissions.get(0)==IPermissionTypes.groupsAccess) {
							annotationListItemWrapper.permissionType = IPermissionTypes.groupsAccess;
						} else {
							annotationListItemWrapper.permissionType = IPermissionTypes.privateAccess;
						}
						annotationListItemWrapper.isLocked = annotationPermissionService.isLocked(annotationSetIndex);
					}
				}
			}
		}
		
		AnnotationListResponse theResponse = new AnnotationListResponse(
			annotationListItemWrappers: annotationListItemWrappers, totalResponses: annotationListItemWrappers.size());
		JSON.use("deep")
		render (theResponse as JSON);
	}
	
	def annotationSet = {
		def setUri = params.setUri;
		def userId = params.userId;
		
		println 'setUri ' + setUri;
		println 'userId ' + userId;
		 
		if (setUri?.trim() && userId?.trim()) { 
			int counter = 0;
			JSONArray responseToSets = new JSONArray();
			def annotationSetIndex = AnnotationSetIndex.findByIndividualUri(setUri);
			if(annotationSetIndex) {
                if(!ELASTICO) {
    				println 'MongoId: ' + annotationSetIndex.mongoUuid
    
    				SleepyMongooseWrapper mongoWrapper = new SleepyMongooseWrapper(grailsApplication.config.mongodb.url, grailsApplication.config.mongodb.database, grailsApplication.config.mongodb.collection);
    				String document = mongoWrapper.doMongoDBFindByObjectId(annotationSetIndex.mongoUuid);
    				if(document!=null && document.length()>0) {
    					def set = JSON.parse(document);
    					if(set.results.size()>0) {
    						responseToSets.add(set.results[0]);
    						counter++;
    					}
    				}
                } else {
                    println 'Elastico: ' + annotationSetIndex.mongoUuid
                    
                    ElasticSearchWrapper esWrapper = new ElasticSearchWrapper(grailsApplication.config.elastico.database, grailsApplication.config.elastico.collection, grailsApplication.config.elastico.ip, grailsApplication.config.elastico.port);
                    String document = esWrapper.getDocument(annotationSetIndex.mongoUuid, false, null);
                    println document;
                   
                    if(document!=null) {
                        def ret = JSON.parse(document);
                        if(ret.hits.total==1) {
                            def set = ret.hits.hits[0]._source;
                            println set;
                            if(set!=null) {
                                responseToSets.add(set);
                                counter++;
                            }
                        }
                    }
                }
				
			}
			JSON.use("deep")
			render (responseToSets as JSON);
			return;
		}
	}
	
	def exhibitAnnotationSet = {
		def setUri = params.setUri;
		def userId = params.userId;
		
		println 'setUri ' + setUri;
		println 'userId ' + userId;
		 
		if (setUri?.trim() && userId?.trim()) {
			int counter = 0;
			JSONObject responseToSets = new JSONObject();
			def annotationSetIndex = AnnotationSetIndex.findByIndividualUri(setUri);
			if(annotationSetIndex) {
				println 'MongoId: ' + annotationSetIndex.mongoUuid

                def set;
                if(!ELASTICO) { 
                    SleepyMongooseWrapper mongoWrapper = new SleepyMongooseWrapper(grailsApplication.config.mongodb.url, grailsApplication.config.mongodb.database, grailsApplication.config.mongodb.collection);
                    String document = mongoWrapper.doMongoDBFindByObjectId(annotationSetIndex.mongoUuid);
                    if(document!=null && document.length()>0) {
                        def temp = JSON.parse(document);
                        if(temp.results.size()>0) {
                            set = temp.results[0]
                        }
                    }
                } else {
                    ElasticSearchWrapper esWrapper = new ElasticSearchWrapper(grailsApplication.config.elastico.database, grailsApplication.config.elastico.collection, grailsApplication.config.elastico.ip, grailsApplication.config.elastico.port);
                    String document = esWrapper.getDocument(annotationSetIndex.mongoUuid, false, null);
                    if(document!=null) {
                        def ret = JSON.parse(document);
                        if(ret.hits.total==1) {
                            set = ret.hits.hits[0]._source;
                        }
                    }
                }
                
				//if(set.results.size()>0) {
					//responseToSets.add(set.results[0]);
					
					// Cache agents
					def agents = set[IOntology.agents];
					def agentsCache = [:];
					agents.each { agent ->
						agentsCache.put(agent["@id"], agent);
					}
					
					JSONObject jsonSet = new JSONObject();
					jsonSet.put("id", set[IOntology.generalId]);
					jsonSet.put("type", set[IOntology.generalType]);
					jsonSet.put("label", set[IOntology.generalLabel]);
					jsonSet.put("description", set[IOntology.generalDescription]);
					jsonSet.put("target", set[IOntology.target]);
					jsonSet.put("createdOn", set[IOntology.pavCreatedOn]);
					
					//jsonSet.put("createdBy", agentsCache.get(set[IOntology.pavCreatedBy]['@id']));
					jsonSet.put("createdBy", agentsCache.get(set[IOntology.pavCreatedBy]));
					
					//def up1 = agentsCache.get(set[IOntology.pavCreatedBy]['@id'])['@id'].toString();
					def up1 = agentsCache.get(set[IOntology.pavCreatedBy])['@id'].toString();
					jsonSet.put("createdById", up1.replaceAll(~/urn:person:uuid:/, ""));
					
					//jsonSet.put("createdByName", agentsCache.get(set[IOntology.pavCreatedBy]['@id'])['foafx:name']);
					jsonSet.put("createdByName", agentsCache.get(set[IOntology.pavCreatedBy])['foafx:name']);
					jsonSet.put("version", set[IOntology.pavVersionNumber]);
					jsonSet.put("permissions", set[IOntology.permissions]);
					responseToSets.put("set", jsonSet);
					
					// All annotations
					def annotations = set[IOntology.annotations];
					
					// Comments extraction
					def commentsMap = [:];
					def annotatedByMap = [:];
					for(def i=0; i<annotations.length(); i++) {
						JSONObject annotation = new JSONObject();
						annotation.put("id", annotations[i][IOntology.generalId]);

						// Extract types
						def typesSet = [] as Set;
						if(annotations[i][IOntology.generalType] instanceof  org.codehaus.groovy.grails.web.json.JSONArray) {
							for(def j=0; j<annotations[i][IOntology.generalType].length(); j++) {
								typesSet.add(annotations[i][IOntology.generalType][j]);
								if(!annotations[i][IOntology.generalType][j].equals("ao:PostIt")) {
									annotation.put("type", annotations[i][IOntology.generalType][j]);
									annotation.put("label", annotations[i][IOntology.generalLabel]);
								}
							}
						} else {
							annotation.put("type", annotations[i][IOntology.generalType]);
							annotation.put("label", annotations[i][IOntology.generalLabel]);
							typesSet.add(annotations[i][IOntology.generalType]);
						}
						
						if(typesSet.contains(IOntology.annotationComment)) {
							// General
							annotation.put("createdOn", annotations[i][IOntology.pavCreatedOn]);
							//annotation.put("createdBy", agentsCache.get(annotations[i][IOntology.pavCreatedBy]['@id']));
							annotation.put("createdBy", agentsCache.get(annotations[i][IOntology.pavCreatedBy]));
							//annotation.put("createdByName", agentsCache.get(annotations[i][IOntology.pavCreatedBy]['@id'])['foafx:name']);
							annotation.put("createdByName", agentsCache.get(annotations[i][IOntology.pavCreatedBy])['foafx:name']);
						
							//def up = agentsCache.get(annotations[i][IOntology.pavCreatedBy]['@id'])['@id'].toString();
							def up = agentsCache.get(annotations[i][IOntology.pavCreatedBy])['@id'].toString();
							annotation.put("createdById", up.replaceAll(~/urn:person:uuid:/, ""));
							//annotation.put("createdByUri", agentsCache.get(annotations[i][IOntology.pavCreatedBy]['@id'])['@id']);
							annotation.put("createdByUri", agentsCache.get(annotations[i][IOntology.pavCreatedBy])['@id']);
							annotation.put("lastSavedOn", annotations[i][IOntology.pavLastSavedOn]);
							annotation.put("version", annotations[i][IOntology.pavVersionNumber]);
							
							// Comment dependent
							annotation.put("content", annotations[i][IOntology.content]);
							
							if(annotations[i][IOntology.hasTarget][0][IOntology.selector]!=null && annotations[i][IOntology.hasTarget][0][IOntology.selector][IOntology.generalType] == IOntology.selectorAnnotation) {
								println 'Comment ' +  annotations[i][IOntology.generalId] + ' on ' + annotations[i][IOntology.hasTarget][0][IOntology.selector]['ao:annotation']
								commentsMap.put(annotations[i][IOntology.generalId], annotations[i][IOntology.hasTarget][0][IOntology.selector]['ao:annotation']);
								annotatedByMap.put(annotations[i][IOntology.hasTarget][0][IOntology.selector]['ao:annotation'], annotations[i]);
							}
						}
					}
					
					
					def annotationsMap = [:];
				
					def commentsCounter = [:];
					def jsonItems = new JSONArray();
					
					for(def i=0; i<annotations.length(); i++) {
						JSONObject annotation = new JSONObject();
						def typesSet = extractBasicAnnotationProperties(agentsCache, annotation, annotations[i]);
						
						/*
						annotation.put("id", annotations[i][IOntology.generalId]);

						// Extract types
						def typesSet = [] as Set;
						if(annotations[i][IOntology.generalType] instanceof  org.codehaus.groovy.grails.web.json.JSONArray) {
							for(def j=0; j<annotations[i][IOntology.generalType].length(); j++) {
								typesSet.add(annotations[i][IOntology.generalType][j]);
								if(!annotations[i][IOntology.generalType][j].equals("ao:PostIt")) {
									annotation.put("type", annotations[i][IOntology.generalType][j]);
									annotation.put("label", annotations[i][IOntology.generalLabel]);
								}
							}
						} else {
							annotation.put("type", annotations[i][IOntology.generalType]);
							annotation.put("label", annotations[i][IOntology.generalLabel]);
							typesSet.add(annotations[i][IOntology.generalType]);
						}
						
						
						annotation.put("createdOn", annotations[i][IOntology.pavCreatedOn]);
						//annotation.put("createdBy", agentsCache.get(annotations[i][IOntology.pavCreatedBy]['@id']));
						annotation.put("createdBy", agentsCache.get(annotations[i][IOntology.pavCreatedBy]));
						//annotation.put("createdByName", agentsCache.get(annotations[i][IOntology.pavCreatedBy]['@id'])['foafx:name']);
						annotation.put("createdByName", agentsCache.get(annotations[i][IOntology.pavCreatedBy])['foafx:name']);
					
						//def up = agentsCache.get(annotations[i][IOntology.pavCreatedBy]['@id'])['@id'].toString();
						def up = agentsCache.get(annotations[i][IOntology.pavCreatedBy])['@id'].toString();
						annotation.put("createdById", up.replaceAll(~/urn:person:uuid:/, ""));
						//annotation.put("createdByUri", agentsCache.get(annotations[i][IOntology.pavCreatedBy]['@id'])['@id']);
						annotation.put("createdByUri", agentsCache.get(annotations[i][IOntology.pavCreatedBy])['@id']);
						annotation.put("lastSavedOn", annotations[i][IOntology.pavLastSavedOn]);
						annotation.put("version", annotations[i][IOntology.pavVersionNumber]);
						*/
						
						if(typesSet.contains(IOntology.annotationHighlight)) {
							annotation.put("content", annotations[i][IOntology.hasTarget][0]["ao:hasSelector"]["ao:exact"]);
							/*
							JSONArray cloudTerms = new JSONArray();
							StringTokenizer st = new StringTokenizer(annotations[i][IOntology.hasTarget][0]["ao:hasSelector"]["ao:exact"]);
							while(st.hasMoreTokens()) {
								cloudTerms.add(st.nextToken());
							}
							annotation.put("cloud", cloudTerms);
							*/
						} else if(typesSet.contains(IOntology.annotationQualifier)) {
							JSONArray cloudTerms = new JSONArray();
							JSONArray contentTerms = new JSONArray();
							for(def kk=0; kk<annotations[i]["ao:hasTopic"].length(); kk++) {
								cloudTerms.add("'" + annotations[i]["ao:hasTopic"][kk][IOntology.generalLabel] + "'");
								contentTerms.add("<a href=\"" + annotations[i]["ao:hasTopic"][kk][IOntology.generalId] + "\">" + annotations[i]["ao:hasTopic"][kk][IOntology.generalLabel] + "</a>");
							}
							annotation.put("cloud", cloudTerms);
							annotation.put("content", contentTerms);
						} else if(typesSet.contains(IOntology.annotationPostIt)) {
							annotation.put("content", annotations[i][IOntology.content]['cnt:chars']);
							println annotations[i][IOntology.content]['cnt:chars']
							/*
							JSONArray cloudTerms = new JSONArray();
							StringTokenizer st = new StringTokenizer(annotations[i][IOntology.content]);
							while(st.hasMoreTokens()) {
								cloudTerms.add(st.nextToken());
							}
							annotation.put("cloud", cloudTerms);
							*/
						} else if(typesSet.contains(IOntology.annotationAntibody)) {
							annotation.put("content", annotations[i][IOntology.content][0]['domeo:antibody'][0]['rdfs:label']);
							println annotations[i][IOntology.content]['cnt:chars']
							/*
							JSONArray cloudTerms = new JSONArray();
							StringTokenizer st = new StringTokenizer(annotations[i][IOntology.content]);
							while(st.hasMoreTokens()) {
								cloudTerms.add(st.nextToken());
							}
							annotation.put("cloud", cloudTerms);
							*/
						} else if(typesSet.contains(IOntology.annotationComment)) {
							//annotation.put("content", annotations[i][IOntology.content]);
						} else if(typesSet.contains(IOntology.annotationMicroPublication)) {
							def typo = annotations[i][IOntology.content][0]["mp:argues"]['@type'].indexOf("Hypo")>0? "Hypothesis" : "Claim";
							def content = typo + ": " + annotations[i][IOntology.content][0]["mp:argues"]["mp:hasContent"];
							if(annotations[i][IOntology.content][0]["mp:argues"]["mp:supportedBy"]!=null && annotations[i][IOntology.content][0]["mp:argues"]["mp:supportedBy"].size()>0) {
								content += '<br/><div style="margin-top:5px;">supportedBy</div>'
								for(int x=0; x<annotations[i][IOntology.content][0]["mp:argues"]["mp:supportedBy"].size(); x++) {
									def item = annotations[i][IOntology.content][0]["mp:argues"]["mp:supportedBy"][x]["reif:resource"];
									if(item["@type"]=="mp:DataImage") { 
										content += "<table><td style='padding:5px; padding-top: 10px; vertical-align: top;'><img src='"+ createLinkTo(dir:"images/secure", file:"database-green.gif") + "'></td><td style='padding:5px; padding-top: 10px;'>" + "<img src='" + item["ao:context"]["domeo:displaySource"] + "'></td></tr></table>"
									} else if(item["@type"]=="mp:Statement") {
										content += "<table><td style='padding:5px; padding-top: 10px; vertical-align: top;'><img src='"+ createLinkTo(dir:"images/secure", file:"document-green.gif") + "'></td><td style='padding:5px; padding-top: 10px;'>" + "Statement: <span style='font-weight: bold;'>" + item["mp:hasContent"] + "</span></td></tr></table>"
									} else if(item["@type"].indexOf("ArticleReference")>0) {
										content += "<table><td style='padding:5px; padding-top: 10px;'><img src='"+ createLinkTo(dir:"images/secure", file:"document-green.gif") + "'></td><td style='padding:5px;padding-top: 10px; '>" + item["authorNames"] + ". <span style='font-weight: bold;'>" + item["title"] + "</span>. " + item["publicationInfo"]+ "</td></tr></table>"
									}
								}
							}
							if(annotations[i][IOntology.content][0]["mp:argues"]["mp:challengedBy"]!=null && annotations[i][IOntology.content][0]["mp:argues"]["mp:challengedBy"].size()>0) {
								content += '<br/><div style="margin-top:5px;">challengedBy</div>'
								for(int x=0; x<annotations[i][IOntology.content]["mp:argues"]["mp:challengedBy"].size(); x++) {
									def item = annotations[i][IOntology.content][0]["mp:argues"]["mp:challengedBy"][x]["reif:resource"];
									if(item["@type"]=="mp:DataImage") { 
										content += "<table><td style='padding:5px; padding-top: 10px; vertical-align: top;'><img src='"+ createLinkTo(dir:"images/secure", file:"database-red.gif") + "'></td><td>" + "<img src='" + item["ao:context"]["domeo:displaySource"] + "'></td></tr></table><br/>"
									} else if(item["@type"]=="mp:Statement") {
										content += "<table><td style='padding:5px; padding-top: 10px; vertical-align: top;'><img src='"+ createLinkTo(dir:"images/secure", file:"document-red.gif") + "'></td><td style='padding:5px; padding-top: 10px;'>" + "Statement: <span style='font-weight: bold;'>" + item["mp:hasContent"] + "</span></td></tr></table>"
									} else if(item["@type"].indexOf("ArticleReference")>0) {
										content += "<table><td style='padding:5px; padding-top: 10px; vertical-align: top;'><img src='"+ createLinkTo(dir:"images/secure", file:"document-red.gif") + "'></td><td>" + item["authorNames"] + ". <span style='font-weight: bold;'>" + item["title"] + "</span>. " + item["publicationInfo"]+ "</td></tr></table><br/>"
									}
								}
							}
							
							JSONArray cloudTerms = new JSONArray();
							JSONArray contentTerms = new JSONArray();
							
							
							if(annotations[i][IOntology.content][0]["mp:argues"]["mp:qualifiedBy"]!=null && annotations[i][IOntology.content][0]["mp:argues"]["mp:qualifiedBy"].size()>0) {
								content += '<br/><div style="margin-top:5px;">qualifiedBy</div>' 
								content += '<ul class="tags">'
								for(def kk=0; kk<annotations[i][IOntology.content][0]["mp:argues"]["mp:qualifiedBy"].size(); kk++) {
									cloudTerms.add("'" + annotations[i][IOntology.content][0]["mp:argues"]["mp:qualifiedBy"][kk]["reif:resource"][IOntology.generalLabel] + "'");
									contentTerms.add("<a href=\"" +annotations[i][IOntology.content][0]["mp:argues"]["mp:qualifiedBy"][kk]["reif:resource"][IOntology.generalId] + "\">" + 
										annotations[i][IOntology.content][0]["mp:argues"]["mp:qualifiedBy"][kk]["reif:resource"][IOntology.generalLabel] + "</a>");
									content += '<li>' + annotations[i][IOntology.content][0]["mp:argues"]["mp:qualifiedBy"][kk]["reif:resource"][IOntology.generalLabel] + '</li>';
								}
								content += '</ul><br style="clear:both;"/>'
							}
							
							annotation.put("content", content);	
							annotation.put("cloud", cloudTerms);
							//annotation.put("content", contentTerms);
						}
						
						// TODO multiple targets and distinguish by selector
						if(annotations[i][IOntology.hasTarget][0][IOntology.selector]!=null && annotations[i][IOntology.hasTarget][0][IOntology.selector][IOntology.generalType] == IOntology.selectorTextQuote) {
							annotation.put("textQuoteSelector", annotations[i][IOntology.hasTarget][0][IOntology.selector][IOntology.generalType]);
							annotation.put("prefix", annotations[i][IOntology.hasTarget][0][IOntology.selector][IOntology.selectorTextQuotePrefix]);
							annotation.put("match", annotations[i][IOntology.hasTarget][0][IOntology.selector][IOntology.selectorTextQuoteMatch]);
							annotation.put("suffix", annotations[i][IOntology.hasTarget][0][IOntology.selector][IOntology.selectorTextQuoteSuffix]);
							commentsMap.put(annotations[i][IOntology.generalId], null);
							annotationsMap.put(annotations[i][IOntology.generalId], annotation);
						} else if(annotations[i][IOntology.hasTarget][0][IOntology.selector]!=null && annotations[i][IOntology.hasTarget][0][IOntology.selector][IOntology.generalType] == IOntology.selectorAnnotation) {
							// println 'Comment on ' + annotations[i][IOntology.hasTarget][0][IOntology.selector]['ao:annotation']
							//commentsMap.put(annotations[i][IOntology.generalId], annotations[i][IOntology.hasTarget][0][IOntology.selector]['ao:annotation']);
						} else if(annotations[i][IOntology.hasTarget][0][IOntology.selector]!=null && annotations[i][IOntology.hasTarget][0][IOntology.selector][IOntology.generalType] == IOntology.selectorImage) {
							annotation.put("imageInDocumentSelector", annotations[i][IOntology.hasTarget][0][IOntology.selector][IOntology.generalType]);
							annotation.put("image", annotations[i][IOntology.hasTarget][0][IOntology.source]);
							//if(annotations[i][IOntology.hasTarget][0][IOntology.displaySource]!=null) {
								annotation.put("display", annotations[i][IOntology.hasTarget][0][IOntology.displaySource]);
							//} else {
							//	annotation.put("display", annotations[i][IOntology.hasTarget][0][IOntology.source]);
							//}
							annotationsMap.put(annotations[i][IOntology.generalId], annotation);
						}
						
						println "Annotated by: " + annotatedByMap.get(annotations[i][IOntology.generalId]);
						
						JSONArray annotatedBys = new JSONArray();
						chainAnnotationsOnAnnotations(annotatedByMap, agentsCache, annotatedBys, annotations[i][IOntology.generalId]);
						if(annotatedBys.size()>0) annotation.put("annotatedBy", annotatedBys); 
						
						/*
						def annotatedBy = annotatedByMap.get(annotations[i][IOntology.generalId]);
						if(annotatedBy!=null) {
							JSONObject annotationOnAnnotation = new JSONObject();
							
							extractBasicAnnotationProperties(agentsCache, annotationOnAnnotation, annotatedBy);
							
							annotationOnAnnotation.put("id", annotatedBy[IOntology.generalId]);
							annotationOnAnnotation.put("content", annotatedBy[IOntology.content]['cnt:chars'][0]);

							
							annotatedBys.add(annotationOnAnnotation);
							
							annotation.put("annotatedBy", annotatedBys);
						}
						*/
					}
					
					
			
					
					// Count comments for each annotation
					commentsMap.values().each { comment ->
						println '---> ' + comment;
					}
					
					commentsMap.keySet().each { commentId ->
						def pivot = commentId
						if(commentsMap.get(pivot)!=null) {
							while(commentsMap.get(pivot)!=null) {
								pivot = commentsMap.get(pivot);
							}
							println 'pivot ' + pivot
							if(commentsCounter.containsKey(pivot)) {
								def counting = commentsCounter.get(pivot);
								counting++;
								commentsCounter.put(pivot, counting);
							} else commentsCounter.put(pivot, "1");
						}
					}
					
					commentsCounter.keySet().each { comment ->
						println ""+ comment + " count " + commentsCounter.get(comment);
					}				
					annotationsMap.keySet().each { annotationId ->
						if(commentsCounter.get(annotationId) && commentsCounter.get(annotationId)!=null) {
							annotationsMap.get(annotationId).put("commentsCounter", commentsCounter.get(annotationId))
							annotationsMap.get(annotationId).put("withComments", "yes")
						}
						jsonItems.add(annotationsMap.get(annotationId));
					}
					
					def items = new JSONObject();
					items.put("items", jsonItems)
					responseToSets.put("items", items);
					counter++;
					
			}
			JSON.use("deep")
			render (responseToSets as JSON);
			return;
		}
	}
	
	def jsonAnnotationSet = {
		def setUri = params.setUri;
		def userId = params.userId;
		
		println 'setUri ' + setUri;
		println 'userId ' + userId;
		 
		if (setUri?.trim() && userId?.trim()) {
			int counter = 0;
			JSONObject responseToSets = new JSONObject();
			def annotationSetIndex = AnnotationSetIndex.findByIndividualUri(setUri);
			if(annotationSetIndex) {
				println 'MongoId: ' + annotationSetIndex.mongoUuid

				def set;
				if(!ELASTICO) {
					SleepyMongooseWrapper mongoWrapper = new SleepyMongooseWrapper(grailsApplication.config.mongodb.url, grailsApplication.config.mongodb.database, grailsApplication.config.mongodb.collection);
					String document = mongoWrapper.doMongoDBFindByObjectId(annotationSetIndex.mongoUuid);
					if(document!=null && document.length()>0) {
						def temp = JSON.parse(document);
						if(temp.results.size()>0) {
							set = temp.results[0]
						}
					}
				} else {
					ElasticSearchWrapper esWrapper = new ElasticSearchWrapper(grailsApplication.config.elastico.database, grailsApplication.config.elastico.collection, grailsApplication.config.elastico.ip, grailsApplication.config.elastico.port);
					String document = esWrapper.getDocument(annotationSetIndex.mongoUuid, false, null);
					if(document!=null) {
						def ret = JSON.parse(document);
						if(ret.hits.total==1) {
							set = ret.hits.hits[0]._source;
						}
					}
				}
				
				// Cache agents
				def agents = set[IOntology.agents];
				def agentsCache = [:];
				agents.each { agent ->
					agentsCache.put(agent["@id"], agent);
				}
				
				JSONObject jsonSet = new JSONObject();
				jsonSet.put("id", set[IOntology.generalId]);
				jsonSet.put("type", set[IOntology.generalType]);
				jsonSet.put("label", set[IOntology.generalLabel]);
				jsonSet.put("description", set[IOntology.generalDescription]);
				jsonSet.put("target", set[IOntology.target]);
				jsonSet.put("createdOn", set[IOntology.pavCreatedOn]);
				
				//jsonSet.put("createdBy", agentsCache.get(set[IOntology.pavCreatedBy]['@id']));
				jsonSet.put("createdBy", agentsCache.get(set[IOntology.pavCreatedBy]));
				
				//def up1 = agentsCache.get(set[IOntology.pavCreatedBy]['@id'])['@id'].toString();
				def up1 = agentsCache.get(set[IOntology.pavCreatedBy])['@id'].toString();
				jsonSet.put("createdById", up1.replaceAll(~/urn:person:uuid:/, ""));
				
				//jsonSet.put("createdByName", agentsCache.get(set[IOntology.pavCreatedBy]['@id'])['foafx:name']);
				jsonSet.put("createdByName", agentsCache.get(set[IOntology.pavCreatedBy])['foafx:name']);
				jsonSet.put("version", set[IOntology.pavVersionNumber]);
				jsonSet.put("permissions", set[IOntology.permissions]);
				responseToSets.put("set", jsonSet);
					
				// All annotations
				def annotations = set[IOntology.annotations];
					
				// Comments extraction
				def commentsMap = [:];
				def curationsMap = [:];
				def annotatedByMap = [:];
				for(def i=0; i<annotations.length(); i++) {
					JSONObject annotation = new JSONObject();
					annotation.put("id", annotations[i][IOntology.generalId]);
					// Extract types
					def typesSet = [] as Set;
					if(annotations[i][IOntology.generalType] instanceof  org.codehaus.groovy.grails.web.json.JSONArray) {
						for(def j=0; j<annotations[i][IOntology.generalType].length(); j++) {
							typesSet.add(annotations[i][IOntology.generalType][j]);
							if(!annotations[i][IOntology.generalType][j].equals("ao:PostIt")) {
								annotation.put("type", annotations[i][IOntology.generalType][j]);
								annotation.put("label", annotations[i][IOntology.generalLabel]);
							}
						}
					} else {
						annotation.put("type", annotations[i][IOntology.generalType]);
						annotation.put("label", annotations[i][IOntology.generalLabel]);
						typesSet.add(annotations[i][IOntology.generalType]);
					}
					
					if(typesSet.contains(IOntology.annotationComment)) {
						// General
						annotation.put("createdOn", annotations[i][IOntology.pavCreatedOn]);
						//annotation.put("createdBy", agentsCache.get(annotations[i][IOntology.pavCreatedBy]['@id']));
						annotation.put("createdBy", agentsCache.get(annotations[i][IOntology.pavCreatedBy]));
						//annotation.put("createdByName", agentsCache.get(annotations[i][IOntology.pavCreatedBy]['@id'])['foafx:name']);
						annotation.put("createdByName", agentsCache.get(annotations[i][IOntology.pavCreatedBy])['foafx:name']);
					
						//def up = agentsCache.get(annotations[i][IOntology.pavCreatedBy]['@id'])['@id'].toString();
						def up = agentsCache.get(annotations[i][IOntology.pavCreatedBy])['@id'].toString();           
						annotation.put("createdById", up.replaceAll(~/urn:person:uuid:/, ""));
						//annotation.put("createdByUri", agentsCache.get(annotations[i][IOntology.pavCreatedBy]['@id'])['@id']);
						annotation.put("createdByUri", agentsCache.get(annotations[i][IOntology.pavCreatedBy])['@id']);
						annotation.put("lastSavedOn", annotations[i][IOntology.pavLastSavedOn]);
						annotation.put("version", annotations[i][IOntology.pavVersionNumber]);
						
						// Comment dependent
						annotation.put("content", annotations[i][IOntology.content]);
					
						if(annotations[i][IOntology.hasTarget][0][IOntology.selector]!=null && annotations[i][IOntology.hasTarget][0][IOntology.selector][IOntology.generalType] == IOntology.selectorAnnotation) {
							println 'Comment ' +  annotations[i][IOntology.generalId] + ' on ' + annotations[i][IOntology.hasTarget][0][IOntology.selector]['ao:annotation']
							commentsMap.put(annotations[i][IOntology.generalId], annotations[i][IOntology.hasTarget][0][IOntology.selector]['ao:annotation']);
							annotatedByMap.put(annotations[i][IOntology.hasTarget][0][IOntology.selector]['ao:annotation'], annotations[i]);
						}
					} else if(typesSet.contains(IOntology.annotationCuration)) {
						annotation.put("createdOn", annotations[i][IOntology.pavCreatedOn]);
						//annotation.put("createdBy", agentsCache.get(annotations[i][IOntology.pavCreatedBy]['@id']));
						annotation.put("createdBy", agentsCache.get(annotations[i][IOntology.pavCreatedBy]));
						//annotation.put("createdByName", agentsCache.get(annotations[i][IOntology.pavCreatedBy]['@id'])['foafx:name']);
						annotation.put("createdByName", agentsCache.get(annotations[i][IOntology.pavCreatedBy])['foafx:name']);
					
						//def up = agentsCache.get(annotations[i][IOntology.pavCreatedBy]['@id'])['@id'].toString();
						def up = agentsCache.get(annotations[i][IOntology.pavCreatedBy])['@id'].toString();
						annotation.put("createdById", up.replaceAll(~/urn:person:uuid:/, ""));
						//annotation.put("createdByUri", agentsCache.get(annotations[i][IOntology.pavCreatedBy]['@id'])['@id']);
						annotation.put("createdByUri", agentsCache.get(annotations[i][IOntology.pavCreatedBy])['@id']);
						annotation.put("lastSavedOn", annotations[i][IOntology.pavLastSavedOn]);
						annotation.put("version", annotations[i][IOntology.pavVersionNumber]);
						
						// Comment dependent         
						annotation.put("content", annotations[i][IOntology.content][0]['rdf:value']);
						
						if(annotations[i][IOntology.hasTarget][0][IOntology.selector]!=null && annotations[i][IOntology.hasTarget][0][IOntology.selector][IOntology.generalType] == IOntology.selectorAnnotation) {
							println 'Curation ' +  annotations[i][IOntology.generalId] + ' on ' + annotations[i][IOntology.hasTarget][0][IOntology.selector]['ao:annotation']
							commentsMap.put(annotations[i][IOntology.generalId], annotations[i][IOntology.hasTarget][0][IOntology.selector]['ao:annotation']);
							annotatedByMap.put(annotations[i][IOntology.hasTarget][0][IOntology.selector]['ao:annotation'], annotations[i]);
						}
					}
				}
					
					
				def annotationsMap = [:];			
				def commentsCounter = [:];
				def curationsCounter = [:];
				
				def curationsTotal = 0;
				def commentsTotal = 0;
				
				def jsonItems = new JSONArray();
				
				for(def i=0; i<annotations.length(); i++) {
					JSONObject annotation = new JSONObject();
					def typesSet = extractBasicAnnotationProperties(agentsCache, annotation, annotations[i]);

					if(typesSet.contains(IOntology.annotationHighlight)) {
						annotation.put("content", annotations[i][IOntology.hasTarget][0]["ao:hasSelector"]["ao:exact"]);
					} else if(typesSet.contains(IOntology.annotationQualifier)) {
						JSONArray cloudTerms = new JSONArray();
						StringBuffer contentTerms = new StringBuffer();
						contentTerms.append("<div style='overflow: hidden;'><ul class='tags'>");
						for(def kk=0; kk<annotations[i]["ao:hasTopic"].length(); kk++) {
							cloudTerms.add("'" + annotations[i]["ao:hasTopic"][kk][IOntology.generalLabel] + "'");
							contentTerms.append("<li><a href=\"" + annotations[i]["ao:hasTopic"][kk][IOntology.generalId] + "\">" + 
								annotations[i]["ao:hasTopic"][kk][IOntology.generalLabel] + "</a><span class='source'>from " + annotations[i]["ao:hasTopic"][kk]["dct:source"]["rdfs:label"] + "</span></li>");
						}
						contentTerms.append("</ul></div>");
						annotation.put("cloud", cloudTerms.toString());
						annotation.put("content", contentTerms);
						annotation.put("body", annotations[i]["ao:hasTopic"]);
					} else if(typesSet.contains(IOntology.annotationPostIt)) {
						annotation.put("content", annotations[i][IOntology.content]['cnt:chars']);
					} else if(typesSet.contains(IOntology.annotationAntibody)) {
						annotation.put("content", annotations[i][IOntology.content][0]['domeo:antibody'][0]['rdfs:label']);
					} else if(typesSet.contains(IOntology.annotationComment)) {
						//annotation.put("content", annotations[i][IOntology.content]);
					} else if(typesSet.contains(IOntology.annotationMicroPublication)) {
						def typo = annotations[i][IOntology.content][0]["mp:argues"]['@type'].indexOf("Hypo")>0? "Hypothesis" : "Claim";
						def content = "<span style='font-weight: bold;'>" +typo + "</span>: " + annotations[i][IOntology.content][0]["mp:argues"]["mp:hasContent"];
						if(annotations[i][IOntology.content][0]["mp:argues"]["mp:supportedBy"]!=null && annotations[i][IOntology.content][0]["mp:argues"]["mp:supportedBy"].size()>0) {
							content += '<br/><div style="margin-top:5px;font-weight: bold;">Supported By:</div>'
							for(int x=0; x<annotations[i][IOntology.content][0]["mp:argues"]["mp:supportedBy"].size(); x++) {
								def item = annotations[i][IOntology.content][0]["mp:argues"]["mp:supportedBy"][x]["reif:resource"];
								if(item["@type"]=="mp:DataImage") {
									content += "<table><td style='padding:5px; padding-top: 10px; vertical-align: top;'><img src='"+ createLinkTo(dir:"images/secure", file:"database-green.gif") + "'></td><td style='padding:5px; padding-top: 10px;'>" + "<img src='" + item["ao:context"]["domeo:displaySource"] + "' style='max-width:500px'></td></tr></table>"
								} else if(item["@type"]=="mp:Statement") {
									content += "<table><td style='padding:5px; padding-top: 10px; vertical-align: top;'><img src='"+ createLinkTo(dir:"images/secure", file:"document-green.gif") + "'></td><td style='padding:5px; padding-top: 10px;'>" + "Statement: <span style='font-weight: bold;'>" + item["mp:hasContent"] + "</span></td></tr></table>"
								} else if(item["@type"].indexOf("ArticleReference")>0) {
									content += "<table><td style='padding:5px; padding-top: 10px;'><img src='"+ createLinkTo(dir:"images/secure", file:"document-green.gif") + "'></td><td style='padding:5px;padding-top: 10px; '>" + item["authorNames"] + ". <span style='font-weight: bold;'>" + item["title"] + "</span>. " + item["publicationInfo"]+ "</td></tr></table>"
								}
							}
						}
						if(annotations[i][IOntology.content][0]["mp:argues"]["mp:challengedBy"]!=null && annotations[i][IOntology.content][0]["mp:argues"]["mp:challengedBy"].size()>0) {
							content += '<br/><div style="margin-top:5px;font-weight: bold;">Challenged By:</div>'
							for(int x=0; x<annotations[i][IOntology.content]["mp:argues"]["mp:challengedBy"].size(); x++) {
								def item = annotations[i][IOntology.content][0]["mp:argues"]["mp:challengedBy"][x]["reif:resource"];
								if(item["@type"]=="mp:DataImage") {
									content += "<table><td style='padding:5px; padding-top: 10px; vertical-align: top;'><img src='"+ createLinkTo(dir:"images/secure", file:"database-red.gif") + "'></td><td>" + "<img src='" + item["ao:context"]["domeo:displaySource"] + "' style='max-width:500px'></td></tr></table><br/>"
								} else if(item["@type"]=="mp:Statement") {
									content += "<table><td style='padding:5px; padding-top: 10px; vertical-align: top;'><img src='"+ createLinkTo(dir:"images/secure", file:"document-red.gif") + "'></td><td style='padding:5px; padding-top: 10px;'>" + "Statement: <span style='font-weight: bold;'>" + item["mp:hasContent"] + "</span></td></tr></table>"
								} else if(item["@type"].indexOf("ArticleReference")>0) {
									content += "<table><td style='padding:5px; padding-top: 10px; vertical-align: top;'><img src='"+ createLinkTo(dir:"images/secure", file:"document-red.gif") + "'></td><td>" + item["authorNames"] + ". <span style='font-weight: bold;'>" + item["title"] + "</span>. " + item["publicationInfo"]+ "</td></tr></table><br/>"
								}
							}
						}
						
						JSONArray cloudTerms = new JSONArray();
						JSONArray contentTerms = new JSONArray();
						
						
						
						if(annotations[i][IOntology.content][0]["mp:argues"]["mp:qualifiedBy"]!=null && annotations[i][IOntology.content][0]["mp:argues"]["mp:qualifiedBy"].size()>0) {
							content += '<br/><div style="margin-top:5px;font-weight: bold;">Qualified By:</div>'
							content += '<ul class="tags">'
							for(def kk=0; kk<annotations[i][IOntology.content][0]["mp:argues"]["mp:qualifiedBy"].size(); kk++) {
								cloudTerms.add("'" + annotations[i][IOntology.content][0]["mp:argues"]["mp:qualifiedBy"][kk]["reif:resource"][IOntology.generalLabel] + "'");
								contentTerms.add("<a href=\"" +annotations[i][IOntology.content][0]["mp:argues"]["mp:qualifiedBy"][kk]["reif:resource"][IOntology.generalId] + "\">" +
									annotations[i][IOntology.content][0]["mp:argues"]["mp:qualifiedBy"][kk]["reif:resource"][IOntology.generalLabel] + "</a>");
								content += "<li><a href=\"" +annotations[i][IOntology.content][0]["mp:argues"]["mp:qualifiedBy"][kk]["reif:resource"][IOntology.generalId] + "\">" +
									annotations[i][IOntology.content][0]["mp:argues"]["mp:qualifiedBy"][kk]["reif:resource"][IOntology.generalLabel] + "</a><span class='source'> from " + annotations[i][IOntology.content][0]["mp:argues"]["mp:qualifiedBy"][kk]["reif:resource"]["dct:source"]["rdfs:label"] + "</span></li>";
							}
							content += '</ul><br style="clear:both;"/>'
						}
						
						annotation.put("content", content);
						annotation.put("cloud", cloudTerms);
						annotation.put("body", annotations[i][IOntology.content]);
						//annotation.put("content", contentTerms);
					}
					
					// TODO multiple targets and distinguish by selector
					if(annotations[i][IOntology.hasTarget][0][IOntology.selector]!=null && annotations[i][IOntology.hasTarget][0][IOntology.selector][IOntology.generalType] == IOntology.selectorTextQuote) {
						annotation.put("textQuoteSelector", annotations[i][IOntology.hasTarget][0][IOntology.selector][IOntology.generalType]);
						annotation.put("prefix", annotations[i][IOntology.hasTarget][0][IOntology.selector][IOntology.selectorTextQuotePrefix]);
						annotation.put("match", annotations[i][IOntology.hasTarget][0][IOntology.selector][IOntology.selectorTextQuoteMatch]);
						annotation.put("suffix", annotations[i][IOntology.hasTarget][0][IOntology.selector][IOntology.selectorTextQuoteSuffix]);
						//commentsMap.put(annotations[i][IOntology.generalId], null);
						annotationsMap.put(annotations[i][IOntology.generalId], annotation);
					} else if(annotations[i][IOntology.hasTarget][0][IOntology.selector]!=null && annotations[i][IOntology.hasTarget][0][IOntology.selector][IOntology.generalType] == IOntology.selectorAnnotation) {
						// println 'Comment on ' + annotations[i][IOntology.hasTarget][0][IOntology.selector]['ao:annotation']
						//commentsMap.put(annotations[i][IOntology.generalId], annotations[i][IOntology.hasTarget][0][IOntology.selector]['ao:annotation']);
						//annotationsMap.put(annotations[i][IOntology.generalId], annotation);
					} else if(annotations[i][IOntology.hasTarget][0][IOntology.selector]!=null && annotations[i][IOntology.hasTarget][0][IOntology.selector][IOntology.generalType] == IOntology.selectorImage) {
						annotation.put("imageInDocumentSelector", annotations[i][IOntology.hasTarget][0][IOntology.selector][IOntology.generalType]);
						annotation.put("image", annotations[i][IOntology.hasTarget][0][IOntology.source]);
						//if(annotations[i][IOntology.hasTarget][0][IOntology.displaySource]!=null) {
							annotation.put("display", annotations[i][IOntology.hasTarget][0][IOntology.displaySource]);
						//} else {
						//	annotation.put("display", annotations[i][IOntology.hasTarget][0][IOntology.source]);
						//}
						annotationsMap.put(annotations[i][IOntology.generalId], annotation);
					}
					
					println "Annotated by: " + annotatedByMap.get(annotations[i][IOntology.generalId]);
					
					JSONArray annotatedBys = new JSONArray();
					chainAnnotationsOnAnnotations(annotatedByMap, agentsCache, annotatedBys, annotations[i][IOntology.generalId]);
					if(annotatedBys.size()>0) annotation.put("annotatedBy", annotatedBys);
				}
					
				annotationsMap.keySet().each { annotationId ->
					println 'annotation: ' + annotationId;
					def pivot = annotationId
					if(annotatedByMap.get(pivot)!=null) {
						while(pivot!=null && annotatedByMap.get(pivot)!=null) {
							if(annotatedByMap.get(pivot)['@type']==IOntology.annotationComment) {
								commentsTotal++;
								if(commentsCounter.containsKey(annotationId)) {
									def counting = commentsCounter.get(annotationId);
									counting++;
									commentsCounter.put(annotationId, counting);
								} else commentsCounter.put(annotationId, "1");
							} else if(annotatedByMap.get(pivot)['@type']==IOntology.annotationCuration) {
								curationsTotal++;
								if(curationsCounter.containsKey(annotationId)) {
									def counting = curationsCounter.get(annotationId);
									counting++;
									curationsCounter.put(annotationId, counting);
								} else curationsCounter.put(annotationId, "1");
							}
							println annotatedByMap.get(pivot)['@type'] + " on " + pivot;
							pivot = annotatedByMap.get(pivot)['@id'];
							//println pivot
						}
					}
				}
				
				
//					annotatedByMap.keySet().each { annotationId ->
//						def pivot = annotationId
//						if(annotatedByMap.get(pivot)!=null) {
//							println annotatedByMap.get(pivot)['@type'];
//							while(annotatedByMap.get(pivot)!=null) {
//								pivot = annotatedByMap.get(annotatedByMap.get(pivot)['@id']);
//								println pivot
//								if(pivot!=null) {
//									println annotatedByMap.get(pivot)['@type'] + " - " + annotatedByMap.get(pivot)['@id'];							
//								}
//							}
//						}
//					}
					
					// Count comments for each annotation					
//					commentsMap.keySet().each { commentId ->
//						def pivot = commentId
//						if(annotatedByMap.get(pivot)!=null) println annotatedByMap.get(pivot)['@type'];
//						if(commentsMap.get(pivot)!=null) {
//							while(commentsMap.get(pivot)!=null) {
//								//println 'pivot ' + pivot
//								//println annotatedByMap.get(pivot)
//								if(annotatedByMap.get(pivot)!=null) println annotatedByMap.get(pivot)['@type'];
//								pivot = commentsMap.get(pivot);
//								
//								
//							}
//							
//							println 'comment pivot ' + pivot
//							
//							if(commentsCounter.containsKey(pivot)) {
//								def counting = commentsCounter.get(pivot);
//								
//								counting++;
//								commentsCounter.put(pivot, counting);
//							} else commentsCounter.put(pivot, "1");
//						}
//					}

					
//					commentsCounter.keySet().each { comment ->
//						println "comment "+ comment + " count " + commentsCounter.get(comment);
//					}

					annotationsMap.keySet().each { annotationId ->
						//println 'id: ' + annotationId
						if(commentsCounter.get(annotationId) && commentsCounter.get(annotationId)!=null) {
							println 'comment ' + commentsCounter.get(annotationId)
							annotationsMap.get(annotationId).put("commentsCounter", commentsCounter.get(annotationId))
							//annotationsMap.get(annotationId).put("withComments", "yes")
						}
						if(curationsCounter.get(annotationId) && curationsCounter.get(annotationId)!=null) {
							println 'curation ' + curationsCounter.get(annotationId)
							annotationsMap.get(annotationId).put("curationsCounter", curationsCounter.get(annotationId))
							//annotationsMap.get(annotationId).put("withComments", "yes")
						}
						jsonItems.add(annotationsMap.get(annotationId));
					}
					
					def items = new JSONObject();
					items.put("items", jsonItems)
					responseToSets.put("items", items);
					counter++;
					
					responseToSets.put("totalComments", commentsTotal);
					responseToSets.put("totalCurations", curationsTotal);
					
			}
			JSON.use("deep")
			render (responseToSets as JSON);
			return;
		}
	}
	
	private void chainAnnotationsOnAnnotations(def annotatedByMap, def agentsCache, JSONArray annotationsOnAnnotation, def annotationId) {
		JSONObject annotationOnAnnotation = new JSONObject();
		def annotatedBy = annotatedByMap.get(annotationId);
		if(annotatedBy!=null) {		
			extractBasicAnnotationProperties(agentsCache, annotationOnAnnotation, annotatedBy);
			annotationOnAnnotation.put("id", annotatedBy[IOntology.generalId]);
			if(annotatedBy['@type'].contains(IOntology.annotationComment)) {
				annotationOnAnnotation.put("content", annotatedBy[IOntology.content][0]['cnt:chars']);
			} else if(annotatedBy['@type'].contains(IOntology.annotationCuration)) {
				annotationOnAnnotation.put("content", annotatedBy[IOntology.content][0]['rdf:value']);
			}
			annotationsOnAnnotation.add(annotationOnAnnotation);
			chainAnnotationsOnAnnotations(annotatedByMap, agentsCache, annotationsOnAnnotation, annotatedBy[IOntology.generalId])
		}
	}
	
	private Set extractBasicAnnotationProperties(def agentsCache, def jsonAnnotation, def originalAnnotation) {
		jsonAnnotation.put("id", originalAnnotation[IOntology.generalId]);
		
		def typesSet = [] as Set;
		if(originalAnnotation[IOntology.generalType] instanceof  org.codehaus.groovy.grails.web.json.JSONArray) {
			for(def j=0; j<originalAnnotation[IOntology.generalType].length(); j++) {
				typesSet.add(originalAnnotation[IOntology.generalType][j]);
				if(!originalAnnotation[IOntology.generalType][j].equals("ao:PostIt")) {
					jsonAnnotation.put("type", originalAnnotation[IOntology.generalType][j]);
					jsonAnnotation.put("label", originalAnnotation[IOntology.generalLabel]);
				}
			}
		} else {
			jsonAnnotation.put("type", originalAnnotation[IOntology.generalType]);
			jsonAnnotation.put("label", originalAnnotation[IOntology.generalLabel]);
			typesSet.add(originalAnnotation[IOntology.generalType]);
		}
		
		jsonAnnotation.put("createdOn", originalAnnotation[IOntology.pavCreatedOn]);
		jsonAnnotation.put("createdBy", agentsCache.get(originalAnnotation[IOntology.pavCreatedBy]));
		jsonAnnotation.put("createdByName", agentsCache.get(originalAnnotation[IOntology.pavCreatedBy])['foafx:name']);

		def up = agentsCache.get(originalAnnotation[IOntology.pavCreatedBy])['@id'].toString();
		jsonAnnotation.put("createdById", up.replaceAll(~/urn:person:uuid:/, ""));
		jsonAnnotation.put("createdByUri", agentsCache.get(originalAnnotation[IOntology.pavCreatedBy])['@id']);
		jsonAnnotation.put("lastSavedOn", originalAnnotation[IOntology.pavLastSavedOn]);
		jsonAnnotation.put("version", originalAnnotation[IOntology.pavVersionNumber]);		
		return typesSet;
	}
	
	private void extractAnnotationOnAnnotationChain(def annotatedByMap, def annotation, def maxLevels, def counter) {
		
	}
	
	/*
	def stream = {
		injectUserProfile()
		def person = AgentPerson.findById(params.id)
		def user = User.findByEmail(person.email);
		def userApplicationProfiles = UserApplicationProfile.getByUserId(user.id)
		def appProfiles = new ArrayList<ApplicationProfile>();
		
		LinkedHashMap<SourceDocument, ArrayList<DocumentAnnotation>> documentAnnotationMap = new LinkedHashMap<SourceDocument, ArrayList<DocumentAnnotation>>();
		
		Date latestContribution = null;
		AgentPerson latestContributor = null;
		List<DocumentAnnotation> documentAnnotationResults = DocumentAnnotation.findAll('from DocumentAnnotation as b order by b.savedOn desc')
		for(DocumentAnnotation documentAnnotationResult: documentAnnotationResults) {
			if(latestContribution==null || documentAnnotationResult.savedOn.after(latestContribution)) {
				latestContribution = documentAnnotationResult.savedOn;
				latestContributor = documentAnnotationResult.editor;
			}
			
			if(applicationPermissionService.isPermissionGranted(user, documentAnnotationResult)) {
				if(!documentAnnotationMap.containsKey(documentAnnotationResult.document)) {
					ArrayList<DocumentAnnotation> da = new ArrayList<DocumentAnnotation>();
					da.add(documentAnnotationResult);
					documentAnnotationMap.putAt(documentAnnotationResult.document, da);
				} else {
					documentAnnotationMap.get(documentAnnotationResult.document).add(documentAnnotationResult);
				}
			}
		}
		
		render (view:'browse', model:[user : user, person: person, documentAnnotations: documentAnnotationMap, latestContribution: latestContribution, latestContributor: latestContributor])
	}
	*/
	
	// Stats
	def stats = {
		int annotations = 0;
		def sets = AnnotationSetIndex.findAll();
		def resources = [] as Set;
		int annotationSets = sets.size();
		sets.each {
			annotations+=it.size;
			resources.add(it.annotatesUrl);
		}
		
		JSONObject stats = new JSONObject();
		stats.put('numberofsets', annotationSets);
		stats.put('numberofanns', annotations);
		stats.put('numberofreso', resources.size());
	
		JSON.use("deep")
		render (stats as JSON);
		return;
	}
	
	
	def searchSet = {
		def user = userProfile();

		JSONArray results = new JSONArray();
		if(request.JSON) {
			def res;
			
			if(request.JSON.setId && request.JSON.query) {
				String[] fields = ['_all', 'domeo_!DOMEO_NS!_belongsToSet']
				String[] values = [request.JSON.query, request.JSON.setId]
				
				res = annotationSearchService.searchItems(fields , values, false, null)
				ElasticSearchWrapper esWrapper = new ElasticSearchWrapper(grailsApplication.config.elastico.database, grailsApplication.config.elastico.collection, grailsApplication.config.elastico.ip, grailsApplication.config.elastico.port);

				JSONObject r = JSON.parse(res);
				def hits = r.hits.hits;
				hits.each { hit ->
					String sub = esWrapper.getDocument(hit._id, true, null);
					JSONObject subJson = JSON.parse(sub);
					subJson.hits.hits[0]._score = hit._score;
					subJson.hits.hits[0].remove("_type")
					subJson.hits.hits[0].remove("_index")
					results.add(subJson.hits.hits[0]);
				}
			}
		}
		
		JSON.use("deep")
		render (results as JSON);
	}
	
	def search = {		
		def user = userProfile();
		
		List<AnnotationSetIndex> annotationListItemWrappers = new ArrayList<AnnotationSetIndex>();
		if(request.JSON) {
			def res;
			println "Query " + request.JSON.query
			println "Public " + request.JSON.permissionsPublic
			println "Private " + request.JSON.permissionsPrivate
			
			println "Human " + request.JSON.agentHuman
			println "Software " + request.JSON.agentSoftware
			
			def agent;
			if(request.JSON.agentHuman=="checked" && request.JSON.agentSoftware !='checked') 
				agent = 'foafx:Person'
			else if(request.JSON.agentHuman!="checked" && request.JSON.agentSoftware =='checked') 
				agent = 'foafx:Software'
				
			if(request.JSON.query) {
				if(agent!=null) {
					String[] fields = ['_all', 'pav_!DOMEO_NS!_createdBy.@type']
					String[] values = [request.JSON.query, agent]
					
					res = annotationSearchService.searchMultiple(fields , values,
						 	(request.JSON.permissionsPublic=="checked")?true:false, (request.JSON.permissionsPrivate=="checked")?"urn:person:uuid:"+userProfileId():null);
				} else {
					res = annotationSearchService.search("_all" , request.JSON.query,
							(request.JSON.permissionsPublic=="checked")?true:false, (request.JSON.permissionsPrivate=="checked")?"urn:person:uuid:"+userProfileId():null);
				}
				
				JSONObject r = JSON.parse(res);
				def hits = r.hits.hits;
				hits.each { hit ->
					def annotationSetIndex = AnnotationSetIndex.findByMongoUuid(hit._id);
					if(annotationSetIndex!=null) {
						if(annotationPermissionService.isPermissionGranted(user, annotationSetIndex)) {
							AnnotationSetItemWrapper annotationListItemWrapper = new AnnotationSetItemWrapper(annotationSetIndex: annotationSetIndex);
							annotationListItemWrappers.add(annotationListItemWrapper);
						 
							List<String> permissions = annotationPermissionService.getAnnotationSetPermissions(user.id, annotationSetIndex);
							if (permissions.get(0)==IPermissionTypes.publicAccess) {
								annotationListItemWrapper.permissionType = IPermissionTypes.publicAccess;
							} else if (permissions.get(0)==IPermissionTypes.groupsAccess) {
								annotationListItemWrapper.permissionType = IPermissionTypes.groupsAccess;
							} else {
								annotationListItemWrapper.permissionType = IPermissionTypes.privateAccess;
							}
							annotationListItemWrapper.isLocked = annotationPermissionService.isLocked(annotationSetIndex);
						}
					}
				}
			
			}
		}
		
		AnnotationListResponse theResponse = new AnnotationListResponse(
			annotationListItemWrappers: annotationListItemWrappers, totalResponses: annotationListItemWrappers.size());
		
		JSON.use("deep")
		render (theResponse as JSON);
	}
	
	
	private def createAnnotationSetSummary(def setLastVersion, String accessType) {
		JSONObject setSummary = new JSONObject();
		setSummary.put("@id", setLastVersion.individualUri);
		setSummary.put("@type", setLastVersion.type);
		setSummary.put("rdfs:label", setLastVersion.label);
		setSummary.put("dct_description", setLastVersion.description);
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
}
