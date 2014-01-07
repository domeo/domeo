package org.mindinformatics.grails.domeo.plugin.bibliography

import grails.converters.JSON

import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.json.JSONObject
import org.hibernate.criterion.*
import org.mindinformatics.grails.domeo.dashboard.security.User
import org.mindinformatics.grails.domeo.plugin.bibliography.model.BibliographicEntry
import org.mindinformatics.grails.domeo.plugin.bibliography.model.BibliographicReference
import org.mindinformatics.grails.domeo.plugin.bibliography.model.UserBibliography


/**
 * @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
 */
class BibliographyController {

	def mailService;
	def grailsApplication;
	def springSecurityService;
	def usersManagementService;
	def bibliographyService;
	
	def isStarred = {
		def user = userProfile();
		
		String textContent = request.getReader().text;
		logInfo(user.id, "Starring document: " + textContent);
		
		def jsonRequest = parseJson(user.id, textContent, "Parsing of the set json content failed");
		if(jsonRequest==null) return;
		else if(jsonRequest.isEmpty()) {
			trackException(user.id, textContent, "Detected empty content (array) while saving");
			return;
		}
		
		UserBibliography ub = UserBibliography.findByUser(user);
		if(ub==null) {
			log.info("Creating bibliogrphy for user " + user.username);
			ub = new UserBibliography(user:user).save(flush:true)
		}
		println '---> '+ub
		println '---> '+ub.user
		println '---> '+jsonRequest.url 
		
		JSONObject jsonResponse = new JSONObject();
		jsonResponse.put('url',jsonRequest.url);
		jsonResponse.put('label',jsonRequest.label);
		def be = BibliographicEntry.findByUrlAndUserBibliography(jsonRequest.url, ub);
		if(be!=null && be.starred) {
			jsonResponse.put('starred','true');
		} else {
			jsonResponse.put('starred','false');
		}
		render (jsonResponse as JSON);
	}
	
	def star = {
		def user = userProfile();
		
		String textContent = request.getReader().text;
		logInfo(user.id, "Starring document: " + textContent);
		
		def jsonRequest = parseJson(user.id, textContent, "Parsing of the set json content failed");
		if(jsonRequest==null) return;
		else if(jsonRequest.isEmpty()) {
			trackException(user.id, textContent, "Detected empty content (array) while saving");
			return;
		}
		
		bibliographyService.createEntry(user, jsonRequest, true);
		
		/*
		UserBibliography ub = UserBibliography.findByUser(user);
		if(ub==null) {
			log.info("Creating bibliogrphy for user " + user.username);
			ub = new UserBibliography(user:user).save(flush:true)
		} 
		
		def be;
		def reference;
		def jsonReference = jsonRequest.reference;
		if(jsonReference!=null) {		
			if(reference==null && jsonReference.doi) {
				reference = BibliographicReference.findByDoi(jsonReference.doi);
			}
			if(reference==null && jsonReference.pmid) {
				reference = BibliographicReference.findByPubMedId(jsonReference.pmid);
			}
			if(reference==null && jsonReference.pmcid) {
				reference = BibliographicReference.findByPubMedCentralId(jsonReference.pmid);
			}
			
			if(reference==null) {
				reference = new BibliographicReference(
					title: jsonReference.title,
					journalName: jsonReference.journalName,
					journalIssn: jsonReference.journalIssn,
					publicationDate: jsonReference.publicationDate,
					authorNames: jsonReference.authorNames,
					publicationInfo: jsonReference.publicationInfo,
					publicationType: jsonReference.publicationType,
					text: jsonReference.unrecognized,
					
					doi: jsonReference.doi,
					pubMedId: jsonReference.pmid,
					pubMedCentralId: jsonReference.pmcid,
					publisherItemId: jsonReference.pii
				)
				log.info("Creating reference " + reference);
				reference.save(flush:true)
			}
			
			if(reference) be = BibliographicEntry.findByReferenceAndUserBibliography(reference, ub); 
		}
		
		if(!reference) be = BibliographicEntry.findByUrlAndUserBibliography(jsonRequest.url, ub);

		if(be==null) {
			log.info(ub);
			be = new BibliographicEntry(
				url:jsonRequest.url,
				title:jsonRequest.label,
				reference: reference,
				userBibliography: ub,
				starred: true
			)
			log.info("Creating bibliographic entry " + be + " - " + be.userBibliography);
			be.save(flush:true)
			ub.entries.add(be);
		} else {
			be.starred = true;
		}
			*/
		
		render 'yo'
		
	}
	
	def unstar = {
		def user = userProfile();
		
		String textContent = request.getReader().text;
		logInfo(user.id, "Unstarring document: " + textContent);
		
		def jsonRequest = parseJson(user.id, textContent, "Parsing of the set json content failed");
		if(jsonRequest==null) return;
		else if(jsonRequest.isEmpty()) {
			trackException(user.id, textContent, "Detected empty content (array) while saving");
			return;
		}
		
		UserBibliography ub = UserBibliography.findByUser(user);
		if(ub!=null) {
			def be = BibliographicEntry.findByUrlAndUserBibliography(jsonRequest.url, ub);
			if(be!=null) {
				be.starred = false;
			}
			log.info("Updated bibliographic entry " + be);
			render 'updated'
			return		
		}
		
		render 'not updated'
	}
	
	def bibliography = {
		def user = userProfile();
		def starred = (params.starred=='true')? true: false;
		def withReference = (params.withReference=='true')? true: false;

		UserBibliography ub = UserBibliography.findByUser(user);
		if(ub!=null) {
			render (view:'bibliography', model:[user: user, bibliographicItems: ub.entries, 
				starred: starred, withReference: withReference,
				loggedUserRoles: usersManagementService.getUserRoles(user),,
				bibliographicItemsCount: ub.entries.size()]);
			return;
		}	
		render 'No results'		
	}
	
	def searchBibliography = {
		def user = userProfile();
		//def starred = (params.starred=='true')? true: false;
		//def withReference = (params.withReference=='true')? true: false;

		if (!params.max) params.max = 2
		if (!params.offset) params.offset = 0
		if (!params.sort) params.sort = "dateCreated"
		if (!params.order) params.order = "asc"
		
		int maxResults = params.max?Integer.parseInt(params.max):10;
		
		println("search------ " + maxResults + "-" + params.search);
		UserBibliography ub = UserBibliography.findByUser(user);
		if(ub!=null) {	
			if(params.search) {
	
				def c = BibliographicEntry.createCriteria()
				def results = c.list {
					and {
						or {
							and {
								like("title", '%'+params.search+'%')
								//isNull('reference')
							}
							and {
								isNotNull('reference')
								or {
									createAlias("reference","ref", CriteriaSpecification.LEFT_JOIN)
									like("title", '%'+params.search+'%')
									like("ref.title", '%'+params.search+'%')
									like("ref.authorNames", '%'+params.search+'%')
								}
							}
						}
						eq("userBibliography", ub)
					}
					projections {
						property('id')
						property('url')
						property('title')
						property('starred')
						property('dateCreated')
						property('ref.id')
						property('ref.title')
						property('ref.authorNames')
					}
				}
				println results.size();
				
				List references = results.collect{record -> [id : record[0], url:record[1], title:record[2], starred:record[3], dateCreated:record[4], reference: BibliographicReference.findById(record[5])]} //, "reference.title":record[5], authors:record[6]]}
				
				JSONArray jsonentries = new JSONArray();
				references.each { entry ->
					println entry.getClass().getName();
					JSONObject jsonentry = new JSONObject();
					jsonentry.put("id", entry.id);
					jsonentry.put("url", entry.url);
					jsonentry.put("title", entry.title);
					jsonentry.put("starred", entry.starred);
					jsonentry.put("createdOn", entry.dateCreated);
					if(entry.reference) {
						JSONObject jsonreference = new JSONObject();
						jsonreference.put("id", entry.reference.id);
						jsonreference.put("title", entry.reference.title);
						jsonreference.put("authors", entry.reference.authorNames);
						jsonreference.put("info", entry.reference.publicationInfo);
						jsonreference.put("doi", entry.reference.doi);
						jsonreference.put("pmid", entry.reference.pubMedId);
						jsonreference.put("pmcid", entry.reference.pubMedCentralId);
						jsonentry.put("reference", jsonreference);
					}
					jsonentries.add(jsonentry);
				}
				
				render jsonentries as JSON
			} else {
				def entries = BibliographicEntry.findAllByUserBibliography(ub, [max: params.max])
				JSONArray jsonentries = new JSONArray();
				entries.each { entry ->
					println entry.getClass().getName();
					JSONObject jsonentry = new JSONObject();
					jsonentry.put("id", entry.id);
					jsonentry.put("url", entry.url);
					jsonentry.put("title", entry.title);
					jsonentry.put("starred", entry.starred);
					jsonentry.put("createdOn", entry.dateCreated);
					if(entry.reference) {
						JSONObject jsonreference = new JSONObject();
						jsonreference.put("id", entry.reference.id);
						jsonreference.put("title", entry.reference.title);
						jsonreference.put("authors", entry.reference.authorNames);
						jsonreference.put("info", entry.reference.publicationInfo);
						jsonreference.put("doi", entry.reference.doi);
						jsonreference.put("pmid", entry.reference.pubMedId);
						jsonreference.put("pmcid", entry.reference.pubMedCentralId);
						jsonentry.put("reference", jsonreference);
					}
					jsonentries.add(jsonentry);
				}
				
				render jsonentries as JSON
			}
		}

		render "";	
	}

	def bibs = {
		def user = userProfile();
		def starred = (params.starred=='true')? true: false;
		def withReference = (params.withReference=='true')? true: false;

		UserBibliography ub = UserBibliography.findByUser(user);
		if(ub!=null) {
			render (view:'bibs', model:[user: user, appBaseUrl: request.getContextPath()]);
			return;
		}
		render 'No results'
	}
	
	def search = {
		def user = userProfile();
		def starred = (params.starred=='true')? true: false;
		def withReference = (params.withReference=='true')? true: false;
		
		int maxResults = params.max?Integer.parseInt(params.max):10; 
		
		if (!params.max) params.max = 2
		if (!params.offset) params.offset = 0
		if (!params.sort) params.sort = "dateCreated"
		if (!params.order) params.order = "asc"
		
		println("search------ " + maxResults + "-" + params.search);
		
		if(params.search) { 

			def c = BibliographicEntry.createCriteria()
			def results = c.list {
				or {
					and {
						like("title", '%'+params.search+'%')
						//isNull('reference')
					}			
					and {
						isNotNull('reference')
						or {
							createAlias("reference","ref", CriteriaSpecification.LEFT_JOIN)
							like("title", '%'+params.search+'%')
							like("ref.title", '%'+params.search+'%')
							like("ref.authorNames", '%'+params.search+'%')
						}
					}	
				}
				projections {
					property('id')
					property('url')
					property('title')
					property('starred')
					property('dateCreated')
					property('ref.id')
					property('ref.title')
					property('ref.authorNames')
				}				
			}
			println results.size();
			
			List references = results.collect{record -> [id : record[0], url:record[1], title:record[2], starred:record[3], dateCreated:record[4], reference: BibliographicReference.findById(record[5])]} //, "reference.title":record[5], authors:record[6]]}
			render (view:'bibliography', model:[user: user, bibliographicItems: references, starred: starred, withReference: withReference, bibliographicItemsCount: references.size(),
				max: maxResults]);
		} else {
		println 'yolo'
			UserBibliography ub = UserBibliography.findByUser(user);
			def entries = BibliographicEntry.findAllByUserBibliography(ub, [max: params.max])
			render (view:'bibliography', model:[user: user, bibliographicItems: entries, starred: starred, withReference: withReference, 
						bibliographicItemsCount: ub.entries.size(), max: params.max]);
		}
	}
	
	// --------------------------------------------
	//  JSON utils
	// --------------------------------------------
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
	//  Profile authentication
	// --------------------------------------------
	private def userProfile() {
		def user;
		def principal = springSecurityService.principal
		if(!principal.equals("anonymousUser")) {
			String username = principal.username
			user = User.findByUsername(username);
			return user
		}
		return null;
	}
	
	private void trackException(def userId, String textContent, String msg) {
		logException(userId, msg);
		response.status = 500
		render (packageJsonErrorMessage(userId, msg) as JSON);
		return;
	}
	
	private def packageJsonErrorMessage(def userId, def exception) {
		JSONObject message = new JSONObject();
		message.put("@type", "Exception");
		message.put("userid", userId);
		message.put("message", exception);
		JSONArray messages = new JSONArray();
		messages.put(message);
		return messages;
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
}
