package org.mindinformatics.grails.domeo.persistence

import java.text.SimpleDateFormat

import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.json.JSONObject

import org.mindinformatics.domeo.persistence.SleepyMongooseWrapper
import org.mindinformatics.domeo.persistence.ElasticSearchWrapper
import org.mindinformatics.grails.domeo.dashboard.security.User
import org.mindinformatics.grails.domeo.persistence.UUID

import grails.converters.JSON


class PersistenceController {
    
    private boolean ELASTICO = true;

	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
	
	def mailService;
	def grailsApplication;
	def springSecurityService;
	def usersManagementService;
	def usersGroupsManagementService
	def transactionalPersistenceService;
    
    def readOnlyService;
    
	
	def index = {
		log.info('index');
		render 'persistence controller home'
	}
	
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
	
	private def saveAnnotationExitStrategy(String userId, String textContent, String exception) {
		try {
			SavingItemRecovery recoveryItem = new SavingItemRecovery(userId: userId);
//			log.debug('Saving in Mongo recovery item.... ' + textContent)
//			SleepyMongooseWrapper mongoWrapper = new SleepyMongooseWrapper(grailsApplication.config.mongodb.url, grailsApplication.config.mongodb.database, grailsApplication.config.mongodb.collection);
//			String mongoResponse = mongoWrapper.doMongoDBInsert(textContent);
//			logInfo(userId,'Mongo response: ' + mongoResponse);
//			def mongoJsonResponse = JSON.parse(mongoResponse);
//			log.debug('Saved recovery item with document id: ' + mongoJsonResponse.oids.$oid);
            
            log.debug('Saving recovery item.... ' + textContent)
            ElasticSearchWrapper esWrapper = new ElasticSearchWrapper(grailsApplication.config.elastico.database, grailsApplication.config.elastico.collection, grailsApplication.config.elastico.ip, grailsApplication.config.elastico.port);
            String esResponse = esWrapper.insertDocument(textContent);
            logInfo(userId,'Response: ' + esResponse);
            def esJsonResponse = JSON.parse(esResponse);
            log.debug('Saved recovery item with document id: ' + esJsonResponse);
            
			// Updating the pointer to the MongoDB document
			//recoveryItem.mongoUuid = esJsonResponse.hits.hits.;
			//recoveryItem.exception = exception;
			//transactionalPersistenceService.saveSavingItemRecovery(recoveryItem);
			
			//logInfo(userId, "TICKET with id " + recoveryItem.id);
			//return recoveryItem.id;
		} catch (Exception e) {
			log.error('Cannot save in Mongo the recovery item!');
			return "none";
		}
		/*
		sendMail {
			to "paolo.ciccarese@gmail.com"
			subject "Domeo Exception"
			body (userId + " ******* " + exception + " ******* " + textContent)
		}
		*/
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
	
	/**
	 * Parsing of JSON content and management of exceptions.
	 * @param userId		The id of the user that triggered the parsing
	 * @param textContent	The textual content to parse into JSON
	 * @param errorMessage	The message to display in case of error
	 * @return	The content in JSON format or null if exception occurred
	 */
	private def parseJson(def userId, def textContent, String errorMessage) {
		try {
			return JSON.parse(textContent);
		} catch(Exception e) {
			trackException(userId, textContent, errorMessage + ": " + e.getMessage());
			e.printStackTrace();
			return
		}
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

	// --------------------------------------------
	//  Saving utils
	// --------------------------------------------
	private def findCreator(def userId, def creatorUri) {
		try {
			return usersManagementService.getUser(creatorUri.substring((int)(creatorUri.lastIndexOf(":")+1)));
		} catch (Exception e) {
			logException(userId, e.getMessage());
			return null;
		}
	}
	
	private def computePreviousVersion(def previousVersionUrn) {
		return (previousVersionUrn!=""? previousVersionUrn : "");
	}
	
	private def computeVersionNumber(def existingVersionNumber) {
		return (existingVersionNumber!=""? (existingVersionNumber+1) : 1);
	}
	
	/**
	 * Roll back for domain objects
	 * @param userId	The id of the user that triggered the saving process
	 * @param obj		The object to roll back
	 * @param id		The id of the object to roll back
	 * @param message	The message to display in the logging
	 */
	private void rollback(def userId, def obj, def id, def message) {
		logException(userId,"ROLLBACK: " + message + " with id " + id);
		obj.delete(flush: true);
	}
	
	private void trackException(def userId, String textContent, String msg) {
		logException(userId, msg);
		def ticket = saveAnnotationExitStrategy(userId, textContent, msg);
		response.status = 500
		render (packageJsonErrorMessage(userId, msg, ticket) as JSON);
		return;
	}
	
	def saveAnnotation = {
		def userId = userProfileId();
		
		String textContent = request.getReader().text;
		logInfo(userId, "Saving annotation: " + textContent);
		 
		JSONArray responseToSets = new JSONArray();
		
		// The content currently consists in Annotation Sets organized in an array
		def jsonResponse = parseJson(userId, textContent, "Parsing of the set json content failed");
		if(jsonResponse==null) return;
		else if(jsonResponse.isEmpty()) {
			trackException(userId, textContent, "Detected empty content (array) while saving");
			return;
		}
        
        def jsonContent = jsonResponse.get("sets");
	
		for(def i=0; i<jsonContent.size(); i++) {
			def JSON_SET = jsonContent.getJSONObject(i);
			println JSON_SET;

			try {
				// Mandatory preconditions, if missing
				// A JSONException is triggered
				def SET_URN = JSON_SET.get("@id");
				def SET_TYPE = JSON_SET.get("@type");
	
				if(SET_TYPE.equals("ao:AnnotationSet")  || SET_TYPE.equals("domeo:DiscussionSet")) {
					def lineageUri = JSON_SET.get("pav:lineageUri");
					def SET_LABEL = JSON_SET.get("rdfs:label");
					def SET_DESCRIPTION = JSON_SET.get("dct:description");
					def SET_TARGET_URL = JSON_SET.get("ao:annotatesResource");
					def SET_CREATED_ON = JSON_SET.get("pav:createdOn");
					
					def SET_DELETED = 'false'
					if(JSON_SET.containsKey("domeo:deleted")) {
						JSON_SET.get("domeo:deleted");
					}
					
					// Creator
					def creator = findCreator(userId, JSON_SET.get("pav:createdBy")); //.get("@id")); 
					if(creator==null) {
						trackException(userId, textContent, "FAILURE: Annotation set creator is null");
						return;
					}
					
					// Annotation
					def annotations = JSON_SET.get("ao:item");
					if(annotations==null) {
						trackException(userId, textContent, "ATTENTION: Detected empty set while saving (annotations is null)");
						return;
					}
					
					def annotationSetSize = annotations.size();
					
					def set = null;
					def previousVersion = "";
					def versionNumber = computeVersionNumber(JSON_SET.get("pav:versionNumber"));
					def annotationSetExistenceFlag = false;
					
					// Checks if the annotation set has been already saved before
					// by checking if the urn already exists in the database
					if(AnnotationSetIndex.findByIndividualUri(SET_URN)==null) {
						// If it does not exists it means the annotation set is brand new
						// a uri for the new lineage will be assigned.
						lineageUri = "urn:domeoserver:annotationset:" + UUID.uuid();
						// Checks for collision
						while(LastAnnotationSetIndex.findByLineageUri(lineageUri)!=null) {
							logWarning(userId, "Detected lineageUri collision " + lineageUri);
							lineageUri = "urn:domeoserver:annotationset:" + UUID.uuid();
						}
						
						// As the annotation set is brand new, there is no previous
						// version and the version number is computed to 1
						previousVersion = "";
						
						// Updating lineage uri in the annotation set document
						JSON_SET.put("pav:lineageUri", lineageUri);
					} else {
						// If the set already exist we need to save it as
						// a new version with the same uri lineage
						previousVersion = SET_URN;
						annotationSetExistenceFlag = true;
						// New individual uri for the annotation set
						SET_URN = "urn:domeoserver:annotationset:" + UUID.uuid();
						// Checks for collision
						while(AnnotationSetIndex.findByIndividualUri(SET_URN)!=null) {
							logWarning(userId, "Detected individualUri collision " + SET_URN);
							SET_URN = "urn:domeoserver:annotationset:" + UUID.uuid();
						}
					}
				
					def lastSavedOnDate = new Date(); 
					try {
						set = new AnnotationSetIndex(type: SET_TYPE, individualUri:SET_URN,
							lineageUri:lineageUri, size:  annotationSetSize, createdBy: creator,
							previousVersion: previousVersion, versionNumber: versionNumber,
							annotatesUrl: SET_TARGET_URL, label:SET_LABEL, description: SET_DESCRIPTION, isDeleted: SET_DELETED=='true');
						set.createdOn = dateFormat.parse(SET_CREATED_ON);
						set.lastSavedOn = dateFormat.parse(dateFormat.format(lastSavedOnDate));
						
						transactionalPersistenceService.saveAnnotationSetIndex(set);
						logInfo(userId, "SUCCESS: Annotation set saved " + set.id);
					} catch(RuntimeException e) {
						trackException(userId, textContent, "FAILURE: Could not save the annotation set: " + e.getMessage());
						return;
					}
					
					// Updating annotation set versioning info before saving the
					// annotation document in MongoDB
					JSON_SET.put("@id", SET_URN);
					JSON_SET.put("pav:versionNumber", versionNumber);
					JSON_SET.put("pav:previousVersion", previousVersion);
					JSON_SET.put("pav:lastSavedOn", dateFormat.format(lastSavedOnDate));
					
					// Creating the index pointing to the last version of the annotation set
					// of a given lineage
					def lastVersion;
					if(!annotationSetExistenceFlag) {
						lastVersion = new LastAnnotationSetIndex(
							lineageUri: lineageUri, lastVersionUri: set.individualUri,
							lastVersion:set, annotatesUrl: SET_TARGET_URL, isDeleted: SET_DELETED=='true');
						try {
							transactionalPersistenceService.saveLastAnnotationSetIndex(lastVersion);
							logInfo(userId, "SUCCESS: Last version index saved " + lastVersion.id);
						} catch(RuntimeException e) {
							rollback(userId, set, set.individualUri, "Annotation Set");
							trackException(userId, textContent, "FAILURE: Could not save the last version index " + e.getMessage());
							return;
						}
					} else {
						// If the annotation set was already existing we update
						// the set last version index.
						try {
							logDebug(userId, "Updating the last annotation set index");
							lastVersion = LastAnnotationSetIndex.findByLineageUri(lineageUri);
							lastVersion.lastVersionUri = SET_URN;
							lastVersion.lastVersion = set;
							lastVersion.isDeleted = SET_DELETED=='true';
						} catch(RuntimeException e) {
							rollback(userId, set, set.individualUri, "Annotation Set");
							trackException(userId, textContent, "FAILURE: Could not save the last version index "+ e.getMessage());
							return;
						}
					}
					
					// Update permissions
					
					def jsonPermissions = JSON_SET['permissions:permissions']
					AnnotationSetPermissions permissions;
					if(jsonPermissions!=null) {
						logDebug(userId, 'permission type: ' + jsonPermissions['permissions:accessType']);
						logDebug(userId, 'permission details: ' + jsonPermissions['permissions:accessDetails']);
	
						permissions = new AnnotationSetPermissions(annotationSet:set,
							lineageUri: lineageUri, permissionType: jsonPermissions['permissions:accessType'], isLocked: jsonPermissions['permissions:isLocked']);
						try {
							transactionalPersistenceService.saveAnnotationSetPermissions(permissions);
							logInfo(userId, "SUCCESS: Annotation Set Permissions saved " + permissions.id);
						} catch(RuntimeException e) {
							rollback(userId, lastVersion, lastVersion.id, "Last Version Set");
							rollback(userId, set, set.individualUri, "Annotation Set");
							trackException(userId, textContent, "FAILURE: Could not save the set permission details " + e.getMessage());
							return;
						}
					} else {
						rollback(userId, lastVersion, lastVersion.id, "Last Version Set");
						rollback(userId, set, set.individualUri, "Annotation Set");
						trackException(userId, textContent, "FAILURE: Could not save the set permission details as the message format is not correct.");
						return;
					}
					
					def annotationSetGroups = [] as Set;
					if(jsonPermissions['permissions:accessType'].equals("urn:domeo:access:groups")) {
						JSONArray groups = jsonPermissions['permissions:accessDetails']['permissions:allowedGroups'];
						for(def groupsIndex=0; groupsIndex<groups.size(); groupsIndex++) {
							AnnotationSetGroup annotationSetGroup = new AnnotationSetGroup(annotationSet:set, groupUri:groups.get(groupsIndex)['@id']);
							try {
								annotationSetGroups.add(transactionalPersistenceService.saveAnnotationSetGroup(annotationSetGroup));
								logInfo(userId, "SUCCESS: Annotation Set Group saved " + annotationSetGroup.id);
							} catch(RuntimeException e) {
								for(def gro: annotationSetGroups) { rollback(userId, gro, gro.id, "Group permission"); }
								rollback(userId, permissions, permissions.id, "Set permission");
								rollback(userId, lastVersion, lastVersion.id, "Last Version Set");
								rollback(userId, set, set.individualUri, "Annotation Set");
								trackException(userId, textContent, "FAILURE: Could not save the set permission details " + e.getMessage());
								return;
							} 
						}
					}
						
					// Response summary creation
					JSONObject responseToSet = new JSONObject();
					responseToSet.put("@id", SET_URN);
					responseToSet.put("domeo_new_id", SET_URN);
					responseToSet.put("pav:lineageUri", lineageUri);
					responseToSet.put("domeo_temp_localId", JSON_SET['domeo_temp_localId']);
					responseToSet.put("pav:lastSavedOn", dateFormat.format(lastSavedOnDate));
					responseToSet.put("pav:versionNumber", versionNumber);
					responseToSet.put("pav:previousVersion", previousVersion);
					
					// Updating the annotation items info
					JSONArray responseToAnnotations = new JSONArray();
					for(def annotationIndex=0; annotationIndex<JSON_SET.get("ao:item").size(); annotationIndex++) {
						def annotation = JSON_SET.get("ao:item").get(annotationIndex);
						def annotationUri = annotation.get("@id");
						def annHasChanged = annotation.domeo_temp_hasChanged;
						def annNewVersion = annotation.domeo_temp_saveAsNewVersion;
						def annLineageUri = annotation.get("pav:lineageUri");
						def annVersionNumber = annotation.get("pav:versionNumber");
						def annPreviousVersion = annotation.get("pav:previousVersion");
						def annLastSavedOn = annotation.get("pav:lastSavedOn");
                        def contexts = annotation.get("ao:context");
						annotation.put('domeo:belongsToSet', SET_URN);
						if(annLineageUri==null || annLineageUri.trim().length()==0) {
							String annLineageUuid = UUID.uuid();
							annLineageUri = "urn:domeoserver:annotation:" + annLineageUuid;
							annotation.put("pav:lineageUri", annLineageUri);
							annVersionNumber = computeVersionNumber(annotation.get("pav:versionNumber"));
							annotation.put("pav:versionNumber", annVersionNumber);
							annLastSavedOn = dateFormat.format(new Date());
						} else {
							if(annHasChanged && annHasChanged=='true') {
								annLastSavedOn = dateFormat.format(new Date());
								if(annNewVersion && annNewVersion=='true') {
									println "Saving as new version"
									// Updating version info
									annPreviousVersion = annLineageUri;
									annVersionNumber = computeVersionNumber(annotation.get("pav:versionNumber"));
									// Updating annotation version info
									annotation.put("pav:versionNumber", annVersionNumber);
									annotation.put("pav:previousVersion", annPreviousVersion);
									// Updating individual uri
									String annUuid = UUID.uuid();
									annotationUri  = "urn:domeoserver:annotation:" + annUuid;
									// Updating annotation uri
									annotation.put("@id", annotationUri);
								} else {
									// Update the annotation without changing versions
									logInfo(userId, "Saving as same version");
								}
							} else {
								annotation.put("pav:lastSavedOn", annLastSavedOn);
								// Cleaning up the original annotation temp
								annotation.remove("domeo_temp_localId");
								annotation.remove("domeo_temp_hasChanged");
								annotation.remove("domeo_temp_saveAsNewVersion");
								continue;
							}
						}
						annotation.put("pav:lastSavedOn", annLastSavedOn);
						
						JSONObject responseToAnnotation = new  JSONObject();
						responseToAnnotation.put("@id", annotationUri);
						responseToAnnotation.put("pav:lineageUri", annLineageUri);
						responseToAnnotation.put("domeo_temp_localId", annotation.domeo_temp_localId);
						responseToAnnotation.put("pav:lastSavedOn", annotation.get("pav:lastSavedOn"));
						responseToAnnotation.put("pav:versionNumber", annVersionNumber);
						responseToAnnotation.put("pav:previousVersion", annPreviousVersion);
						responseToAnnotations.add(responseToAnnotation);
						
						// Cleaning up the original annotation temp
						annotation.remove("domeo_temp_localId");
						annotation.remove("domeo_temp_hasChanged");
						annotation.remove("domeo_temp_saveAsNewVersion");
 
                        println '*****CONTEXT*****' + contexts;
                        if(contexts!=null) {
                            println '*****CONTEXT*****' + contexts;
                            for(def contextIndex=0; contextIndex<contexts.size(); contextIndex++) {
                                def context = contexts.get(contextIndex);
                                if(context.hasProperty("domeo_temp_localId")) 
                                    context.remove("domeo_temp_localId");
                                if(context.hasProperty("ao:hasSelector")) {
                                    def selector = context.get("ao:hasSelector");
                                    println '*****SELECTOR*****' + selector;
                                    if(selector!=null) {
                                        if(selector.hasProperty("domeo_temp_localId"))
                                            selector.remove("domeo_temp_localId");
                                    }
                                }
                            }       
                        } 
					}
					responseToSet.put("ao:item", responseToAnnotations);
					responseToSets.add(responseToSet);
					
					// Cleaning up the original annotation set temp
					JSON_SET.remove("domeo_temp_localId");
					
					// Saving in MongoDB
					def mongoJsonResponse;
					log.debug('Saving.... ' + JSON_SET.toString())
					try {
						// TODO Manage MongoDb errors 
                        if(!ELASTICO) {
    						SleepyMongooseWrapper mongoWrapper = new SleepyMongooseWrapper(grailsApplication.config.mongodb.url, grailsApplication.config.mongodb.database, grailsApplication.config.mongodb.collection);
    						String mongoResponse = mongoWrapper.doMongoDBInsert(JSON_SET.toString());
    						mongoJsonResponse = JSON.parse(mongoResponse);
    						logInfo(userId, 'MongoDB response: ' + mongoJsonResponse);
    						logInfo(userId, 'Saved with document id: ' + mongoJsonResponse.oids.$oid);
    						
    						if(!JSONObject.NULL.equals(mongoJsonResponse.status.err)) {
    							logInfo(userId, 'Failure: MongoDB error: ' + mongoJsonResponse.status.err);
    							rollback(userId, lastVersion, lastVersion.id, "Last Version Set");
    							rollback(userId, permissions, permissions.id, "Permission Set");
    							annotationSetGroups.each {
    								rollback(userId, it, it.id, "Permission Set Group");
    							}
    							rollback(userId, set, set.individualUri, "Annotation Set");
    							logException(userId, "FAILURE: Set cannot be saved in MongoDb: " + textContent);
    							response.status = 500
    							render (packageJsonErrorMessage(userId, "FAILURE: Set cannot be saved in MongoDb", "<none>") as JSON);
    							return
    						} 
                        } else {
                            ElasticSearchWrapper esWrapper = new ElasticSearchWrapper(grailsApplication.config.elastico.database, grailsApplication.config.elastico.collection, grailsApplication.config.elastico.ip, grailsApplication.config.elastico.port);
                            String esResponse = esWrapper.insertDocument(JSON_SET.toString());
                            mongoJsonResponse = JSON.parse(esResponse);
                            logInfo(userId, 'Elastico response: ' + mongoJsonResponse);
                            logInfo(userId, 'Saved with document id: ' + mongoJsonResponse._id);
                            
                            if(mongoJsonResponse.ok.equals("false")) {
                                logInfo(userId, 'Failure: Elastico insert error: ' + mongoJsonResponse.ok);
                                rollback(userId, lastVersion, lastVersion.id, "Last Version Set");
                                rollback(userId, permissions, permissions.id, "Permission Set");
                                annotationSetGroups.each {
                                    rollback(userId, it, it.id, "Permission Set Group");
                                }
                                rollback(userId, set, set.individualUri, "Annotation Set");
                                logException(userId, "FAILURE: Set cannot be saved in Elastico: " + textContent);
                                response.status = 500
                                render (packageJsonErrorMessage(userId, "FAILURE: Set cannot be saved in Elastico", "<none>") as JSON);
                                return;
                            }     
                        }                   
					} catch(Exception e) {
                        if(ELASTICO) {
                            // Order is important
                            rollback(userId, lastVersion, lastVersion.id, "Last Version Set");
                            rollback(userId, permissions, permissions.id, "Permission Set");
                            annotationSetGroups.each {
                                rollback(userId, it, it.id, "Permission Set Group");
                            }
                            rollback(userId, set, set.individualUri, "Annotation Set");
                            trackException(userId, textContent, "FAILURE: Set not saved in Elastico " + e.getMessage());
                            response.status = 500
                            render (packageJsonErrorMessage(userId, "FAILURE: Set cannot be saved in Elastico", "<none>") as JSON);
                            return
                        } else {
                            // Order is important
                            rollback(userId, lastVersion, lastVersion.id, "Last Version Set");
                            rollback(userId, permissions, permissions.id, "Permission Set");
                            annotationSetGroups.each {
                                rollback(userId, it, it.id, "Permission Set Group");
                            }
                            rollback(userId, set, set.individualUri, "Annotation Set");
                            trackException(userId, textContent, "FAILURE: Set not saved in MongoBD " + e.getMessage());
                            response.status = 500
                            render (packageJsonErrorMessage(userId, "FAILURE: Set cannot be saved in MongoBD", "<none>") as JSON);
                            return
                        }
						
					}
					
					// Updating the pointer to the MongoDB document
                    if(ELASTICO) set.mongoUuid = mongoJsonResponse._id;
                    else set.mongoUuid = mongoJsonResponse.oids.$oid;
                    
					logInfo(userId, 'SUCCESS: Saving annotation set process completed!');
					
				} else {
					trackException(userId, textContent, "FAILURE: Set type not recognized (skipped): " + SET_TYPE);
					return;
				}
			} catch(Exception e) {
				trackException(userId, textContent, "FAILURE: Annotation Set saving failed " + e.getMessage());
				return;
			}
		}
		
		render (responseToSets as JSON);
		
		ElasticSearchWrapper esWrapper = new ElasticSearchWrapper(grailsApplication.config.elastico.database, grailsApplication.config.elastico.collection, grailsApplication.config.elastico.ip, grailsApplication.config.elastico.port);
		String esResponse = esWrapper.refreshIndex();
		
		logInfo(userId, 'SUCCESS: Saving process completed!');
	}
	
	def private findBibliographicIdentifier(def userId, def idType, def idValue) {
		logInfo(userId, "Searching for  " + idType + " with value: " + idValue);
		def mappings = BibliographicIdMapping.findByIdLabelAndIdValue(idType, idValue)
		if(mappings!=null) {
			logInfo(userId, 'Mapping already existing with uuid: ' + mappings.uuid);
			return mappings.uuid
		}
	}
	
	def private saveBibliographicIdentifier(def userId, def textContent, def uuid, def idType, def idValue) {
		if(BibliographicIdMapping.findByIdLabelAndIdValue(idType, idValue)==null) {
			logInfo(userId, "Saving identifier " + idType + " with value: " + idValue);
			BibliographicIdMapping mappings = new BibliographicIdMapping(uuid:uuid, idLabel:idType, idValue:idValue);
			transactionalPersistenceService.saveBibliographicIdMapping(mappings);
			logInfo(userId, "SUCCESS: Saved identifier doi: " + idType + " with value: " + idValue);
			return mappings;
		} 
	}
	
	def saveBibliography = {
		def userId = userProfileId();
		
		String textContent = request.getReader().text;
		logInfo(userId, "Saving bibliography: " + textContent);

		// The content currently consists in Annotation Sets organized in an array
		def JSON_SET = parseJson(userId, textContent, "Parsing of the set json content failed");
		if(JSON_SET==null) return;
		else if(JSON_SET.isEmpty()) {
			trackException(userId, textContent, "Detected empty content (object) while saving");
			return;
		}
	
		try {
			println "******** ID: " + JSON_SET.get("@id");
			def SET_URN = JSON_SET.get("@id");
			def SET_TYPE = JSON_SET.get("@type");
			
			if(SET_TYPE.equals("domeo:BibliographicSet")) {
				def SET_LINEAGE_URI = JSON_SET.get("pav:lineageUri");
				def SET_LABEL = JSON_SET.get("rdfs:label");
				def SET_DESCRIPTION = JSON_SET.get("dct:description");
				def SET_TARGET_URL = JSON_SET.get("ao:annotatesResource");
				def SET_CREATED_ON = JSON_SET.get("pav:createdOn");
				def SET_VERSION = JSON_SET.get("pav:versionNumber");
				def BIBLIOGRAPHY_LEVEL = JSON_SET.get("domeo:extractionLevel");
				
				// Creator
				def creator = findCreator(userId, JSON_SET.get("pav:createdBy"));
				if(creator==null) {
					trackException(userId, textContent, "FAILURE: Annotation bibliography creator is null");
					return;
				}
				
				// Annotation
				def annotations = JSON_SET.get("ao:item");
				if(annotations==null) {
					trackException(userId, textContent, "ATTENTION: Detected empty set while saving (annotations is null)");
					return;
				}

				def annotationSetSize = JSON_SET.get("ao:item").size();
				
				def set = null;
				def ids = [] as Set
				def previousVersion = "";
				def versionNumber = computeVersionNumber(JSON_SET.get("pav:versionNumber"));
				def annotationSetExistenceFlag = false;
				
				// Check if the annotation set for this URL exists already 
				// and the new set is not a new version of the old one.
				// In this case the bibliography should not be saved as 
				// it would create a duplicate.
				println "******* LINEAGE: " + SET_LINEAGE_URI
				println "******* URN: " + SET_URN
				println "******* " + BibliographicSetIndex.findByIndividualUri(SET_URN)
				//println "******* " + LastBibliographicSetIndex.findByAnnotatesUrl(SET_TARGET_URL).lastVersionUri
				println "******* LEVEL: " + BIBLIOGRAPHY_LEVEL
				
				
				boolean sameLineageFlag = false;
				if(SET_LINEAGE_URI==null || (SET_LINEAGE_URI!=null && SET_LINEAGE_URI.trim().length()==0)) {
					println "******* No lineage..."
					if(BibliographicSetIndex.findAllByAnnotatesUrlAndVersionNumber(SET_TARGET_URL, "1").size()>0 && SET_URN!=null && BibliographicSetIndex.findByIndividualUri(SET_URN)==null) {
						
						int newLevel=0;
						if(BIBLIOGRAPHY_LEVEL!=null && BIBLIOGRAPHY_LEVEL.trim().length()>0) newLevel = new Integer(BIBLIOGRAPHY_LEVEL);

						// Save the out of lineage set only if it is more detailed (greater level) than the existing one
						if(LastBibliographicSetIndex.findByAnnotatesUrl(SET_TARGET_URL)!=null && LastBibliographicSetIndex.findByAnnotatesUrl(SET_TARGET_URL).lastVersionUri!=null && 
								BibliographicSetIndex.findByIndividualUri(LastBibliographicSetIndex.findByAnnotatesUrl(SET_TARGET_URL).lastVersionUri).level>=newLevel) {
							trackException(userId, textContent, "ATTENTION: Detected existing set (bibliography already present)");
							return;
						} 
						// Saving anyway by inecting in the existing lineage
						sameLineageFlag = true;
						SET_LINEAGE_URI = LastBibliographicSetIndex.findByAnnotatesUrl(SET_TARGET_URL).lineageUri;
					} 
				} 
				
				
				
//				if(BibliographicSetIndex.findAllByAnnotatesUrlAndVersionNumber(SET_TARGET_URL, "1").size()>0 && BibliographicSetIndex.findByIndividualUri(SET_URN)==null) {
//					trackException(userId, textContent, "ATTENTION: Detected existing set (bibliography already present)");
//					return;
//				}
				
				// Checks if the annotation set has been already saved before
				// by checking if the urn already exists in the database
				if(BibliographicSetIndex.findByIndividualUri(SET_URN)==null) {
					// If it does not exists it means the annotation set is brand new
					// a uri for the new lineage will be assigned.
					if(SET_LINEAGE_URI==null || SET_LINEAGE_URI.trim().length()==0) {
						SET_LINEAGE_URI = "urn:domeoserver:annotationset:" + UUID.uuid();
						previousVersion = "";
					} else {
						previousVersion = JSON_SET.get("@id");
						println "****** : other case"
					}
					
					
					// Checks for collision
// Wrong, there can be multiple sets with same lineage
//					while(BibliographicSetIndex.findByLineageUri(SET_LINEAGE_URI)!=null) {
//						logWarning(userId, "Detected lineageUri collision " + SET_LINEAGE_URI);
//						SET_LINEAGE_URI = "urn:domeoserver:annotationbibliography:" + UUID.uuid();
//					}
					
					// As the annotation set is brand new, there is no previous
					// version and the version number is computed to 1
					
					
					// Updating lineage uri in the annotation set document
					JSON_SET.put("pav:lineageUri", SET_LINEAGE_URI);
				} else {
					// If the set already exist we need to save it as
					// a new version with the same uri lineage
					previousVersion = SET_URN;
					annotationSetExistenceFlag = true;
					// New individual uri for the annotation set
					SET_URN = "urn:domeoserver:annotationset:" + UUID.uuid();
					// Checks for collision
					while(BibliographicSetIndex.findByIndividualUri(SET_URN)!=null) {
						logWarning(userId, "Detected individualUri collision " + SET_URN);
						SET_URN = "urn:domeoserver:annotationbibliography:" + UUID.uuid();
					}
				}
				
				
				set = new BibliographicSetIndex(lineageUri:SET_LINEAGE_URI,
					individualUri:SET_URN,
					size:  annotationSetSize, createdBy: creator,
					previousVersion: previousVersion, versionNumber: versionNumber,
					annotatesUrl: SET_TARGET_URL, label:SET_LABEL, description: SET_DESCRIPTION, level: BIBLIOGRAPHY_LEVEL);
				set.createdOn = dateFormat.parse(SET_CREATED_ON);
				set.lastSavedOn = dateFormat.parse(dateFormat.format(new Date()));

				def uuid;
				for(def ii=0; ii<annotationSetSize; ii++) {
					def jsonAnnotation = JSON_SET.get("ao:item").get(ii);
					def jsonTarget = jsonAnnotation["ao:context"][0];
					if(jsonTarget!=null && jsonTarget.get("@type").equals("domeo:TargetSelector")) {
						def content = jsonAnnotation["ao:body"];
						
						int counter = 0;
						if(content.doi!='<unknown>') {
							uuid = findBibliographicIdentifier(userId, "doi", content.doi);
						}
						if(uuid==null && content.pmid!='<unknown>') {
							uuid = findBibliographicIdentifier(userId, "pmid", content.pmid);
						}
						if(uuid==null && content.pmcid!='<unknown>') {
							uuid = findBibliographicIdentifier(userId, "pmcid", content.pmcid);
						}
						
						// If no mapping is existing a new uuid is geenrated
						if(uuid==null) uuid = UUID.uuid();
						// TODO check for collisions
						
						def idType;
						def idValue;
						def idItem;
						try {
							if(content.doi!=null && content.doi!='<unknown>') {
								idType = 'doi';
								idValue = content.doi;
								idItem = saveBibliographicIdentifier(userId, textContent, uuid, idType, idValue);
								if(idItem!=null) ids.add(idItem);
							}
							if(content.pmid!=null && content.pmid!='<unknown>') {
								idType = 'pmid';
								idValue = content.pmid;
								idItem = saveBibliographicIdentifier(userId, textContent, uuid, idType, idValue);
								if(idItem!=null) ids.add(idItem);
							}
							if(content.pmcid!=null && content.pmcid!='<unknown>') {
								idType = 'pmcid';
								idValue = content.pmcid;
								idItem = saveBibliographicIdentifier(userId, textContent, uuid, idType, idValue);
								if(idItem!=null) ids.add(idItem);
							}
						} catch(RuntimeException e) {
							ids.each{ idMapping -> rollback(userId, idMapping, idMapping.uuid, "Bibliographic Set " + idMapping.idLabel); };
							trackException(userId, textContent, "FAILURE: Could not save the identifier: " + idType + " with value: " + idValue);
							return;
						}
					}
				}
				if(uuid!=null) set.uuidBibliographicIdMapping = uuid;
				else set.uuidBibliographicIdMapping = "";

				try {
					transactionalPersistenceService.saveBibliographicSetIndex(set);
					logInfo(userId, "SUCCESS: Bibliography set saved");
				} catch(RuntimeException e) {
					ids.each{ idMapping -> rollback(userId, idMapping, idMapping.uuid, "Bibliographic Set " + idMapping.idLabel); };
					trackException(userId, textContent, "FAILURE: Could not save the bibliography set: " + e.getMessage());
					return;
				}
				 
				// Updating annotation set versioning info before saving the
				// annotation document in MongoDB
				JSON_SET.put("@id", SET_URN);
				JSON_SET.put("pav:versionNumber", versionNumber);
				JSON_SET.put("pav:previousVersion", previousVersion);
				JSON_SET.put("pav:lastSavedOn", dateFormat.format(set.lastSavedOn));
				
				// Creating the index pointing to the last version of the annotation set
				// of a given lineage
				def lastVersion;
				if(LastBibliographicSetIndex.findByAnnotatesUrl(SET_TARGET_URL)==null) {
					 lastVersion = new LastBibliographicSetIndex(
						lineageUri: SET_LINEAGE_URI, lastVersionUri: set.individualUri,
						lastVersion:set, annotatesUrl: SET_TARGET_URL);
					
					try {
						transactionalPersistenceService.saveLastBibliographicSetIndex(lastVersion);
						logInfo(userId, "SUCCESS: Last version index saved");
					} catch(RuntimeException e) {
						rollback(userId, set, set.individualUri, "Bibliographic Set");
						ids.each{ idMapping -> rollback(userId, idMapping, idMapping.uuid, "Bibliographic Set " + idMapping.idLabel); };
						trackException(userId, textContent, "FAILURE: Could not save the last Bibliographic Set version index " + e.getMessage());
						return;
					}
				} else {
					// If the annotation set was already existing we update
					// the set last version index.
					try {
						logDebug(userId, "Updating the last bibliographic set index with lineageUri " + SET_LINEAGE_URI);
						lastVersion = LastBibliographicSetIndex.findByAnnotatesUrl(SET_TARGET_URL);
						lastVersion.lastVersionUri = SET_URN;
						lastVersion.lastVersion = set;
					} catch(RuntimeException e) {
						rollback(userId, set, set.individualUri, "Bibliographic Set");
						ids.each{ idMapping -> rollback(userId, idMapping, idMapping.uuid, "Bibliographic Set " + idMapping.idLabel); };
						trackException(userId, textContent, "FAILURE: Could not save the last version index "+ e.getMessage());
						return;
					}
				}

				// Response summary creation
				JSONObject responseToSet = new JSONObject();
				responseToSet.put("@id", SET_URN);
				responseToSet.put("pav:lineageUri", SET_LINEAGE_URI);
				responseToSet.put("domeo_temp_localId", JSON_SET['domeo_temp_localId']);
				responseToSet.put("pav:lastSavedOn", dateFormat.format(set.lastSavedOn));
				responseToSet.put("pav:versionNumber", versionNumber);
				responseToSet.put("pav:previousVersion", previousVersion);
				
				// Updating the annotation items info
				JSONArray responseToAnnotations = new JSONArray();
				for(def annotationIndex=0; annotationIndex<JSON_SET.get("ao:item").size(); annotationIndex++) {
					def annotation = JSON_SET.get("ao:item").get(annotationIndex);
					def annotationUri = annotation.get("@id");
					def annHasChanged = annotation.domeo_temp_hasChanged;
					def annNewVersion = annotation.domeo_temp_saveAsNewVersion;
					def annLineageUri = annotation.get("pav:lineageUri");
					def annVersionNumber = annotation.get("pav:versionNumber");
					def annPreviousVersion = annotation.get("pav:previousVersion");
					def annLastSavedOn = annotation.get("pav:lastSavedOn");
                    def contexts = annotation.get("ao:context");
					
					if(annLineageUri==null || annLineageUri.trim().length()==0) {
						String annLineageUuid = UUID.uuid();
						annLineageUri = "urn:domeoserver:annotation:" + annLineageUuid;
						annotation.put("pav:lineageUri", annLineageUri);
						annVersionNumber = computeVersionNumber(annotation.get("pav:versionNumber"));
						annotation.put("pav:versionNumber", annVersionNumber);
						annLastSavedOn = dateFormat.format(new Date());
					} else {
						if(annHasChanged && annHasChanged=='true') {
							annLastSavedOn = dateFormat.format(new Date());
							if(annNewVersion && annNewVersion=='true') {
								println "Saving as new version"
								// Updating version info
								annPreviousVersion = annLineageUri;
								annVersionNumber = computeVersionNumber(annotation.get("pav:versionNumber"));
								// Updating annotation version info
								annotation.put("pav:versionNumber", annVersionNumber);
								annotation.put("pav:previousVersion", annPreviousVersion);
								// Updating individual uri
								String annUuid = UUID.uuid();
								annotationUri  = "urn:domeoserver:annotation:" + annUuid;
								// Updating annotation uri
								annotation.put("@id", annotationUri);
							} else {
								// Update the annotation without changing versions
								println "Saving as same version"
							}
						} else {
							annotation.put("pav:lastSavedOn", annLastSavedOn);
							// Cleaning up the original annotation temp
							annotation.remove("domeo_temp_localId");
							annotation.remove("domeo_temp_hasChanged");
							annotation.remove("domeo_temp_saveAsNewVersion");
							continue;
						}
					}
					annotation.put("pav:lastSavedOn", annLastSavedOn);
					
					JSONObject responseToAnnotation = new  JSONObject();
					responseToAnnotation.put("@id", annotationUri);
					responseToAnnotation.put("pav:lineageUri", annLineageUri);
					responseToAnnotation.put("domeo_temp_localId", annotation.domeo_temp_localId);
					responseToAnnotation.put("pav:lastSavedOn", annotation.get("pav:lastSavedOn"));
					responseToAnnotation.put("pav:versionNumber", annVersionNumber);
					responseToAnnotation.put("pav:previousVersion", annPreviousVersion);
					responseToAnnotations.add(responseToAnnotation);
					
					// Cleaning up the original annotation temp
					annotation.remove("domeo_temp_localId");
					annotation.remove("domeo_temp_hasChanged");
					annotation.remove("domeo_temp_saveAsNewVersion");
                    
                    println '*****CONTEXT*****' + contexts;
                    if(contexts!=null) {
                        println '*****CONTEXT*****' + contexts;
                        for(def contextIndex=0; contextIndex<contexts.size(); contextIndex++) {
                            def context = contexts.get(contextIndex);
                            context.remove("domeo_temp_localId");
                            if(context.has("ao:hasSelector")) {
                                def selector = context.get("ao:hasSelector");
                                println '*****SELECTOR*****' + selector;
                                if(selector!=null) {
                                    selector.remove("domeo_temp_localId");
                                }
                            }
                        }
                    }
					
				}
				responseToSet.put("ao:item", responseToAnnotations);
				
				// Cleaning up the original annotation set temp
				JSON_SET.remove("domeo_temp_localId");
				
				// Saving in MongoDB
				def mongoJsonResponse;
                if(!ELASTICO) { 
    				log.debug('Saving in MongoDb.... ' + JSON_SET.toString())
    				try {
    					// TODO manage MongoDb errors
    					SleepyMongooseWrapper mongoWrapper = new SleepyMongooseWrapper(grailsApplication.config.mongodb.url, grailsApplication.config.mongodb.database, grailsApplication.config.mongodb.collection);
    					String mongoResponse = mongoWrapper.doMongoDBInsert(JSON_SET.toString());
    					mongoJsonResponse = JSON.parse(mongoResponse);
    					logInfo(userId, 'MongoDB response: ' + mongoJsonResponse);
    					logInfo(userId, 'Saved with document id: ' + mongoJsonResponse.oids.$oid);
    					
    					if(!JSONObject.NULL.equals(mongoJsonResponse.status.err)) {
    						logInfo(userId, 'Failure: MongoDB error: ' + mongoJsonResponse.status.err);
    						rollback(userId, lastVersion, lastVersion.id, "Last Version Bibliographic Set");
    						rollback(userId, set, set.individualUri, "Bibliographic Set");
    						ids.each{ idMapping -> rollback(userId, idMapping, idMapping.uuid, "Bibliographic Set " + idMapping.idLabel); };
    						logException(userId, "FAILURE: Bibliographic Set cannot be saved in MongoDb: " + textContent);
    						return
    					}
    				} catch(Exception e) {
    					// Order is important
    					rollback(userId, lastVersion, lastVersion.id, "Last Version Bibliographic Set");
    					rollback(userId, set, set.individualUri, "Bibliographic Set");
    					ids.each{ idMapping -> rollback(userId, idMapping, idMapping.uuid, "Bibliographic Set " + idMapping.idLabel); };
    					trackException(userId, textContent, "FAILURE: Bibliographic Set not saved in MongoDb " + e.getMessage());
    					return
    				}
                } else {
                    log.debug('Saving in Elastico.... ' + JSON_SET.toString())
                    println 'Saving in Elastico.... ' + JSON_SET.toString()
                    try {
                        ElasticSearchWrapper esWrapper = new ElasticSearchWrapper(grailsApplication.config.elastico.database, grailsApplication.config.elastico.collection, grailsApplication.config.elastico.ip, grailsApplication.config.elastico.port);
                        String esResponse = esWrapper.insertDocument(JSON_SET.toString());
                        println 'esResponse aa: ' + esResponse
                        mongoJsonResponse = JSON.parse(esResponse);
                        logInfo(userId, 'Elastico response: ' + mongoJsonResponse);
                        logInfo(userId, 'Saved with document id: ' + mongoJsonResponse._id);
                        
                        if(mongoJsonResponse.ok.equals("false")) {
                            logInfo(userId, 'Failure: Elastico insert error: ' + mongoJsonResponse.ok);
                            rollback(userId, lastVersion, lastVersion.id, "Last Version Bibliographic Set");
                            rollback(userId, set, set.individualUri, "Bibliographic Set");
                            ids.each{ idMapping -> rollback(userId, idMapping, idMapping.uuid, "Bibliographic Set " + idMapping.idLabel); };
                            logException(userId, "FAILURE: Set cannot be saved in Elastico: " + textContent);
                            //response.status = 500
                            //render (packageJsonErrorMessage(userId, "FAILURE: Set cannot be saved in Elastico", "<none>") as JSON);
                            return;
                        }
                    } catch (Exception e) {
                        // Order is important
                        rollback(userId, lastVersion, lastVersion.id, "Last Version Bibliographic Set");
                        rollback(userId, set, set.individualUri, "Bibliographic Set");
                        ids.each{ idMapping -> rollback(userId, idMapping, idMapping.uuid, "Bibliographic Set " + idMapping.idLabel); };
                        trackException(userId, textContent, "FAILURE: Bibliographic Set not saved in Elastico " + e.getMessage());
                        return
                    }
                }
					
				// Updating the pointer to the MongoDB document
                if(ELASTICO) set.mongoUuid = mongoJsonResponse._id;
				else set.mongoUuid = mongoJsonResponse.oids.$oid;
				logInfo(userId, 'SUCCESS: Bibliographic set saving process completed!');
				
				JSONArray array = new JSONArray();
				array.add(responseToSet)
				render (array as JSON);
			} else {
				trackException(userId, textContent, "FAILURE: Set type not recognized (skipped): " + SET_TYPE);
			}
		} catch(Exception e) {
			trackException(userId, textContent, "FAILURE: Annotation Bibliography saving failed: " + e.getMessage());
		}
	}
	
	def retrieveExistingAnotationSetsList = {

		//Thread.currentThread().sleep(12*1000)
		def userId = userProfileId();
		
		String textContent = request.getReader().text;
		logInfo(userId, "Retrieving list of existing annotation sets: " + textContent);
		
		// The content currently consists in Annotation Sets organized in an array
		def JSON_REQUEST = parseJson(userId, textContent, "Parsing of the json content request failed");
		if(JSON_REQUEST==null) return;
		else if(JSON_REQUEST.isEmpty()) {
			trackException(userId, textContent, "Detected empty content (object) while parsing the list of existing annotation sets request");
			return;
		}
		
		try {
			def privateAnnotationSets = [] as Set;
			def privateLineageIdentifiers = [] as Set;
			def groupsAnnotationSets = [] as Set;
			def groupsLineageIdentifiers = [] as Set;
			def publicAnnotationSets = [] as Set;
			def publicLineageIdentifiers = [] as Set;
		
			// Query all the annotation sets available to the user and save lineageUris
			def userSets = AnnotationSetPermissions.findAllByPermissionType(JSON_REQUEST.get(0).person);
			userSets.each {
				privateLineageIdentifiers.add(it.lineageUri);
			}
			
			/*
			// Query userId groups
			// Query sets lineage for those groups
			def groupsSets = AnnotationSetPermissions.findAllByPermissionType("urn:domeo:access:groups");
			groupsSets.each {
				groupsLineageIdentifiers.add(it.lineageUri);
			}
			*/
			
			def groupSets = [] as Set;
			def groups = usersGroupsManagementService.getUserGroups(usersGroupsManagementService.getUser(userId));
			groups.each { group -> 
				def gSets = AnnotationSetGroup.findAllByGroupUri(group.uri);
				gSets.each { gSet ->
					groupSets.add(gSet.annotationSet);
				}
			}
			groupSets.each {
				groupsLineageIdentifiers.add(it.lineageUri);
			}

			def publicSets = AnnotationSetPermissions.findAllByPermissionType("urn:domeo:access:public");
			publicSets.each {
				publicLineageIdentifiers.add(it.lineageUri);
			}
			
			// Query for all the annotation sets available for the URL
			// and crossing them with those available to the user
			def existingAnnotationSets = LastAnnotationSetIndex.findAllByAnnotatesUrlAndIsDeleted(JSON_REQUEST.get(0).url, false, [sort:"dateCreated", order:"desc"]);
			existingAnnotationSets.each { annotationSet ->
				privateLineageIdentifiers.each { lineageUri ->
					if(annotationSet.lineageUri.equals(lineageUri))
						privateAnnotationSets.add(annotationSet)
				}
				groupsLineageIdentifiers.each { lineageUri ->
					if(annotationSet.lineageUri.equals(lineageUri))
						groupsAnnotationSets.add(annotationSet)
				}
				publicLineageIdentifiers.each { lineageUri ->
					if(annotationSet.lineageUri.equals(lineageUri))
						publicAnnotationSets.add(annotationSet)
				}
			}
			
			JSONArray responseToSetsList = new JSONArray();
			if(privateAnnotationSets!=null && privateAnnotationSets.size()>0) {
				privateAnnotationSets.each {
					def setLastVersion = AnnotationSetIndex.findByIndividualUri(it.lastVersionUri);
					if(setLastVersion!=null) { 
						responseToSetsList.add(readOnlyService.createAnnotationSetSummary(setLastVersion, JSON_REQUEST.get(0).person));
					}
				}  
			}
			if(groupsAnnotationSets!=null && groupsAnnotationSets.size()>0) {
				groupsAnnotationSets.each {
					def setLastVersion = AnnotationSetIndex.findByIndividualUri(it.lastVersionUri);
					if(setLastVersion!=null) {
						responseToSetsList.add(readOnlyService.createAnnotationSetSummary(setLastVersion, "urn:domeo:access:groups"));
					}
				}
			}
			if(publicAnnotationSets!=null && publicAnnotationSets.size()>0) {
				publicAnnotationSets.each {
					def setLastVersion = AnnotationSetIndex.findByIndividualUri(it.lastVersionUri);
					if(setLastVersion!=null) {
						responseToSetsList.add(readOnlyService.createAnnotationSetSummary(setLastVersion, "urn:domeo:access:public"));
					}
				}
			}
			render (responseToSetsList as JSON);
		} catch(Exception e) {
			trackException(userId, textContent, "FAILURE: Retrieval of the list of existing annotation sets failed " + e.getMessage());
		}
	}
	
//	private def createAnnotationSetSummary(def setLastVersion, String accessType) {
//		JSONObject setSummary = new JSONObject();
//		setSummary.put("@id", setLastVersion.individualUri);
//		setSummary.put("@type", setLastVersion.type);
//		setSummary.put("rdfs:label", setLastVersion.label);
//		setSummary.put("dct:description", setLastVersion.description);
//		setSummary.put("pav:lineageUri", setLastVersion.lineageUri);
//		setSummary.put("domeo:mongoUuid", setLastVersion.mongoUuid);
//		
//		setSummary.put("pav:versionNumber", setLastVersion.versionNumber);
//		setSummary.put("pav:previousVersion", setLastVersion.previousVersion);
//		setSummary.put("ao:numberItems", setLastVersion.size);
//		setSummary.put("pav:lastSavedOn", dateFormat.format(setLastVersion.lastSavedOn));
//		setSummary.put("pav:createdOn", dateFormat.format(setLastVersion.createdOn));
//		
//		setSummary.put("permissions:accessType", accessType);
//		
//		def createdBy = setLastVersion.createdBy;
//		
//		JSONObject creator = new JSONObject();
//		creator.put("uri", createdBy.id);
//		creator.put("screenname", createdBy.displayName);
//		creator.put("foaf_title", createdBy.title);
//		creator.put("foaf_first_name", createdBy.firstName);
//		creator.put("foaf_middle_name", createdBy.middleName);
//		creator.put("foaf_last_name", createdBy.lastName);
//		
//		setSummary.put("pav:createdBy", creator);
//		setSummary;
//	}
	
	def retrieveExistingAnotationSets = {
		def userId = userProfileId();
		
		String textContent = request.getReader().text;
		logInfo(userId, "Retrieving existing annotation sets: " + textContent);
		
		// The content currently consists in Annotation Sets organized in an array
		def JSON_REQUEST = parseJson(userId, textContent, "Parsing of the json existing annotation sets request failed");
		if(JSON_REQUEST==null) return;
		else if(JSON_REQUEST.isEmpty()) {
			trackException(userId, textContent, "Detected empty content (object) while parsing the existing annotation sets request");
			return;
		}
	   
		try {
			int counter = 0;
			JSONArray responseToSets = new JSONArray();
			for(def i=0; i<JSON_REQUEST[0].ids.size(); i++) {
				def setLastVersion = AnnotationSetIndex.findByIndividualUri(JSON_REQUEST[0].ids[i]);
				if(setLastVersion!=null) {
                    if(ELASTICO) {
                        ElasticSearchWrapper esWrapper = new ElasticSearchWrapper(grailsApplication.config.elastico.database, grailsApplication.config.elastico.collection, grailsApplication.config.elastico.ip, grailsApplication.config.elastico.port);
                        String document = esWrapper.getDocument(setLastVersion.mongoUuid);
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
                    } else {
    					SleepyMongooseWrapper mongoWrapper = new SleepyMongooseWrapper(grailsApplication.config.mongodb.url, grailsApplication.config.mongodb.database, grailsApplication.config.mongodb.collection);
    					String document = mongoWrapper.doMongoDBFindByObjectId(setLastVersion.mongoUuid);                        
                        if(document!=null) {
    						def set = JSON.parse(document);
    						if(set.results.size()>0) {
    							responseToSets.add(set.results[0]);
    							counter++;
    						}
    					}
                    }
				}
			}
			if(JSON_REQUEST[0].ids.size()>counter) {
				trackException(userId, textContent, "FAILURE: Something went terribly wrong while retrieving the existing annotation. " +
					"Only " + counter + " sets out of " + JSON_REQUEST[0].ids.size() + " have been retrieved.");
				return;
			}
			
//			String ret = '[{"@type":"ao:AnnotationSet","@id":"urn:domeoclient:uuid:4C4BC864-BC0E-49A2-AA68-853F781792E4","ao:annotatesResource":"http://localhost:8080/text/faces/DocumentViewPage.xhtml?setId=Laut&docId=3","pav:createdBy":"urn:person:uuid:paolociccarese","pav:createdOn":"2013-06-13 15:13:35 -0400","pav:createdWith":"http://www.commonsemantics.com/agent/domeo_b5","rdfs:label":"Default Set","dct:description":"The default set is created automatically by Domeo when no other set is existing.","pav:lineageUri":"urn:domeoserver:annotationset:145ce92b-e79a-4f60-acc3-7cf118ef57f5","pav:versionNumber":"1","pav:previousVersion":"","ao:item":[{"@type":"ao:Highlight","@id":"urn:domeoclient:uuid:8A972D93-C84F-450E-BE4C-3FCBE7BF8601","pav:createdBy":"urn:person:uuid:paolociccarese","pav:createdOn":"2013-06-13 15:13:35 -0400","pav:createdWith":"http://www.commonsemantics.com/agent/domeo_b5","pav:lastSavedOn":"2013-06-13 15:13:37 -0400","rdfs:label":"Highlight","pav:lineageUri":"urn:domeoserver:annotation:eaaa3822-f24a-4a96-9bfc-54f85e28419c","pav:versionNumber":"1","pav:previousVersion":"","domeo_temp_hasChanged":"true","domeo_temp_saveAsNewVersion":"true","ao:context":[{"@type":"ao:SpecificResource","@id":"urn:domeoclient:uuid:158FB4A3-B9A5-47F5-9856-455F54B42756","ao:hasSource":"http://localhost:8080/text/faces/DocumentViewPage.xhtml?setId=Laut&docId=3","ao:hasSelector":{"@type":"ao:PrefixSuffixTextSelector","@id":"urn:domeoclient:uuid:158FB4A3-B9A5-47F5-9856-455F54B42756","domeo:uuid":"158FB4A3-B9A5-47F5-9856-455F54B42756","pav:createdOn":"2013-06-13 15:13:35 -0400","ao:prefix":"expressed ","ao:exact":"in many ","ao:suffix":" tissues and concentrated in the"}}]}],"permissions:permissions":{"permissions:accessType":"urn:domeo:access:public","permissions:isLocked":"false"},"domeo:agents":[{"@id":"urn:person:uuid:paolociccarese","@type":"foafx:Person","rdfs:label":"Paolo Ciccarese","foafx:name":"Paolo Ciccarese","foafx:homepage":"","foafx:title":"Dr.","foafx:email":"paolo.ciccarese@gmail.com","foafx:firstname":"Paolo","foafx:middlename":"Nunzio","foafx:lastname":"Ciccarese","foafx:picture":"http://www.hcklab.org/images/me/paolo%20ciccarese-boston.jpg"},{"@id":"http://www.commonsemantics.com/agent/domeo_b5","@type":"foafx:Software","rdfs:label":"Domeo","foafx:name":"Domeo","foafx:homepage":"","foafx:version":"b5","foafx:build":""}],"pav:lastSavedOn":"2013-06-13 15:13:37 -0400"}]'
//			def responseToSets = JSON.parse(ret);
			render (responseToSets as JSON);
		} catch(Exception e) {
			trackException(userId, textContent, "FAILURE: Retrieval of existing annotation sets failed " + e.getMessage());
		}
   }
	
	def retrieveExistingBibliographicSets = {

		def userId = userProfileId();
		
		String textContent = request.getReader().text;
		logInfo(userId, "Retrieving existing bibliographic sets: " + textContent);
		
		// The content currently consists in Annotation Sets organized in an array
		def JSON_REQUEST = parseJson(userId, textContent, "Parsing of the json existing bibliographic sets content request failed");
		if(JSON_REQUEST==null) return;
		else if(JSON_REQUEST.isEmpty()) {
			trackException(userId, textContent, "Detected empty content (object) while parsing the existing bibliography sets request");
			return;
		}

		int counter = 0;
		JSONArray responseToSets = new JSONArray();
		
		try {
			def mapping;
			def firstResponse = JSON_REQUEST.get(0);
			def ids = firstResponse.ids;
			if(ids!=null && ids.size()>0) {
				for(def i=0; i<firstResponse.ids.size(); i++) {
					logInfo(userId, "Retrieving mappping " + firstResponse.ids.get(i).getAt('idLabel') + "-" + firstResponse.ids.get(i).getAt('idValue'));
					mapping = BibliographicIdMapping.findByIdLabelAndIdValue(firstResponse.ids.get(i).getAt('idLabel'), firstResponse.ids.get(i).getAt('idValue'));
					if(mapping!=null) break;
				}
								
				if(mapping!=null) {
					println '********: ' + mapping.uuid
					def setById = BibliographicSetIndex.findAllByUuidBibliographicIdMappingAndLevel(mapping.uuid, "2", [sort: "createdOn", order: "asc"]);
					
					if(setById!=null && setById.size()>0) {
						println '********: ' + setById.last().lineageUri
						def set = setById.last()
						//def last = LastBibliographicSetIndex.findByLineageUri(setById.last().lineageUri);
						//if(last!=null) {
							//println '********: ' + last.lastVersionUri
							//def set = BibliographicSetIndex.findByIndividualUri(last.lastVersionUri);
							if(set!=null) {
                                if(ELASTICO) {
                                    logInfo(userId,  "Loading Elastico document " + set.mongoUuid);
                                    ElasticSearchWrapper esWrapper = new ElasticSearchWrapper(grailsApplication.config.elastico.database, grailsApplication.config.elastico.collection, grailsApplication.config.elastico.ip, grailsApplication.config.elastico.port);
                                    String document = esWrapper.getDocument(set.mongoUuid);
                                    println document;
                                    
                                    if(document!=null) {
                                        def ret = JSON.parse(document);
                                        if(ret.hits.total==1) {
                                            def set2 = ret.hits.hits[0]._source;
                                            println set2;
                                            if(set2!=null) {
                                                responseToSets.add(set2);
                                                counter++;
                                            }
                                        }
                                    }
                                } else {
    								logInfo(userId,  "Loading MongoDb document " + set.mongoUuid);
    								SleepyMongooseWrapper mongoWrapper = new SleepyMongooseWrapper(grailsApplication.config.mongodb.url, grailsApplication.config.mongodb.database, grailsApplication.config.mongodb.collection);
    								String document = mongoWrapper.doMongoDBFindByObjectId(set.mongoUuid);
    								if(document!=null) {
    									def set2 = JSON.parse(document);
    									if(set2.results.size()>0) {
    										// TODO inject groups
    										responseToSets.add(set2.results[0]);
    										counter++;
    									}
    								}
                                }
							}
						//}
					}
				}
			} else {
				// Bibliographic set retrieval through url
				if(firstResponse.url!=null) {
					def lastSet = LastBibliographicSetIndex.findByAnnotatesUrl(firstResponse.url);
					if(lastSet!=null) { 
						if(lastSet.lastVersionUri!=null ) {
							def setById = BibliographicSetIndex.findByIndividualUri(lastSet.lastVersionUri);
							if(setById!=null) {
                                if(ELASTICO) {
                                    logInfo(userId,  "Loading Elastico document " + setById.mongoUuid);
                                    ElasticSearchWrapper esWrapper = new ElasticSearchWrapper(grailsApplication.config.elastico.database, grailsApplication.config.elastico.collection, grailsApplication.config.elastico.ip, grailsApplication.config.elastico.port);
                                    String document = esWrapper.getDocument(setById.mongoUuid);
                                    println document;
                                    
                                    if(document!=null) {
                                        def ret = JSON.parse(document);
                                        if(ret.hits.total==1) {
                                            def set2 = ret.hits.hits[0]._source;
                                            println set2;
                                            if(set2!=null) {
                                                responseToSets.add(set2);
                                                counter++;
                                            }
                                        }
                                    }
                                } else {
    								logInfo(userId,  "Loading MongoDb document " + setById.mongoUuid);
    								SleepyMongooseWrapper mongoWrapper = new SleepyMongooseWrapper(grailsApplication.config.mongodb.url, grailsApplication.config.mongodb.database, grailsApplication.config.mongodb.collection);
    								String document = mongoWrapper.doMongoDBFindByObjectId(setById.mongoUuid);
    								if(document!=null) {
    									def set = JSON.parse(document);
    									if(set.results.size()>0) {
    										// TODO inject groups
    										responseToSets.add(set.results[0]);
    										counter++;
    									}
    								}
                                }
							}
						}
					}
				}
			}
			render (responseToSets as JSON);
		} catch(Exception e) {
			trackException(userId, textContent, "FAILURE: Retrieval of existing bibliographic sets failed " + e.getMessage());
		}
	}
}
