package org.mindinformatics.grails.domeo.plugin.bibliography

import grails.converters.JSON

import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.json.JSONObject
import org.mindinformatics.grails.domeo.dashboard.security.User

class BibliographyController {

	def mailService;
	def grailsApplication;
	def springSecurityService;
	
	def star = {
		def userId = userProfileId();
		String textContent = request.getReader().text;
		logInfo(userId, "Starring document: " + textContent);
		
		def jsonResponse = parseJson(userId, textContent, "Parsing of the set json content failed");
		if(jsonResponse==null) return;
		else if(jsonResponse.isEmpty()) {
			trackException(userId, textContent, "Detected empty content (array) while saving");
			return;
		}
		
		render 'yo'
		
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
