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
package org.mindinformatics.grails.domeo.client.profiles

import grails.converters.JSON

import java.text.SimpleDateFormat

import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.json.JSONObject
import org.mindinformatics.grails.domeo.dashboard.security.User
import org.mindinformatics.grails.domeo.utils.DomeoControllerUtils



/**
 * @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
 */
class ProfilesController extends DomeoControllerUtils {

	def springSecurityService
	def usersManagementService;
	def profilesService;
	
	public static SimpleDateFormat dayTime = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss Z");
	
	private def loggedUser() {
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

	def saveCurrentUserProfile = {
		if(params.format.equals("json") ) {
			String textContent = request.getReader().text;
			def jsonResponse = parseJson(getUser().id, textContent, "Parsing of user's current profile failed");
			
		}
	}
	
	def getCurrentUserProfile = {
		def user = loggedUser();
		def current = profilesService.getCurrentUserProfileOrDefault(user);
		//def plugins = profilesService.getProfileEntries(current);
		
		def plugins = profilesService.getProfileEntries(current, 'plugin');
		def features = profilesService.getProfileEntries(current, 'feature');
		
		JSONArray profiles = new JSONArray();
		profiles.add(serializeDomeoClientProfile(current, plugins, features));
		render (profiles as JSON)
	}
	
	private JSONObject serializeDomeoClientProfile(def profile, def plugins, def features) {
		JSONObject p = new JSONObject();
		p.put("uuid", profile.id);
		p.put("name", profile.name);
		p.put("description", profile.description);
		p.put("createdOn", dayTime.format(profile.dateCreated));
		
		JSONArray creators = new JSONArray();
		JSONObject createdBy = new JSONObject();
		createdBy.put("@id", profile.createdBy.id);
		createdBy.put("@type", "foafx:Person");
		createdBy.put("foafx:name", profile.createdBy.displayName);
		creators.add(createdBy)
		p.put("createdBy", creators);
		
		JSONArray featureStatus = new JSONArray();
		features.each { feature ->
			JSONObject pg = new JSONObject();
			pg.put("name", feature.plugin);
			pg.put("status", feature.status);
			featureStatus.add(pg);
		}
		p.put("features", featureStatus);
		JSONArray pluginStatus = new JSONArray();
		plugins.each { plugin ->
			JSONObject pg = new JSONObject();
			pg.put("name", plugin.plugin);
			pg.put("status", plugin.status);
			pluginStatus.add(pg);
		}
		p.put("plugins", pluginStatus);
		return p;
	}
	
	def getUserProfiles = {
		def user = loggedUser();
		def profiles = profilesService.getAvailableUserProfiles(user);
		
		JSONArray ps = new JSONArray();
		profiles.each {
			def plugins = profilesService.getProfileEntries(it.profile, 'plugin');
			def features = profilesService.getProfileEntries(it.profile, 'feature');
			
			ps.add(serializeDomeoClientProfile(it.profile, plugins, features));
		}
		render (ps as JSON)
	}
	
	
	def save = {
		def user = loggedUser();
		String textContent = request.getReader().text;
		def jsonResponse = parseJson(user.id, textContent, "Parsing of the set json content failed");
		def ret = profilesService.saveCurrentProfile(user, jsonResponse.get(0).get('uuid'));
		if(ret) render (jsonResponse as JSON);
		else render "";
	}
	
	def info = {
		getCurrentUserProfile();
	}
	
	def all = {
		getUserProfiles();
	}
}
