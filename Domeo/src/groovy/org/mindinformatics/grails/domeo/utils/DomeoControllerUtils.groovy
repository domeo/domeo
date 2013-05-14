/*
 * Copyright 2013 Massachusetts General Hospital
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.mindinformatics.grails.domeo.utils

import grails.converters.JSON

import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.json.JSONObject

class DomeoControllerUtils {
	
	// --------------------------------------------
	//  Logging utils
	// --------------------------------------------
	protected def logInfo(def log, def userId, message) {
		log.info(":" + userId + ": " + message);
	}
	
	private def logDebug(def log, def userId, message) {
		log.debug(":" + userId + ": " + message);
	}
	
	private def logWarning(def log, def userId, message) {
		log.warn(":" + userId + ": " + message);
	}
	
	private def logException(def log, def userId, message) {
		log.error(":" + userId + ": " + message);
	}
	
	/**
	 * Parsing of JSON content and management of exceptions.
	 * @param userId		The id of the user that triggered the parsing
	 * @param textContent	The textual content to parse into JSON
	 * @param errorMessage	The message to display in case of error
	 * @return	The content in JSON format or null if exception occurred
	 */
	protected def parseJson(def userId, def textContent, String errorMessage) {
		try {
			return JSON.parse(textContent);
		} catch(Exception e) {
			e.printStackTrace();
			logException(userId, e.getMessage());
			return packageJsonErrorMessage(userId, e.getMessage());
		}
	}
	
	protected def packageJsonErrorMessage(def userId, def exception) {
		JSONObject message = new JSONObject();
		message.put("@type", "Exception");
		message.put("userid", userId);
		message.put("message", exception);
		JSONArray messages = new JSONArray();
		messages.put(message);
		return messages;
	}
}
