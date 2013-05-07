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
import org.mindinformatics.grails.domeo.utils.DomeoControllerUtils;


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
		def plugins = profilesService.getProfileEntries(current);
		
		JSONArray profiles = new JSONArray();
		profiles.add(serializeDomeoClientProfile(current, plugins));
		render (profiles as JSON)
	}
	
	private JSONObject serializeDomeoClientProfile(def profile, def plugins) {
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
		
		JSONArray status = new JSONArray();
		plugins.each { plugin ->
			JSONObject pg = new JSONObject();
			pg.put("name", plugin.plugin);
			pg.put("status", plugin.status);
			status.add(pg);
		}
		p.put("plugins", status);
		return p;
	}
	
	def getUserProfiles = {
		def user = loggedUser();
		def profiles = profilesService.getAvailableUserProfiles(user);
		
		JSONArray ps = new JSONArray();
		profiles.each {
			def plugins = profilesService.getProfileEntries(it.profile);
			ps.add(serializeDomeoClientProfile(it.profile, plugins));
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
	/*
		if(params.format.equals("json") ) {
			render('[');
			 
			render("  {");
			render("    \"uuid\": \"");
			render("4fa09e38adb4d0.96200877");
			render("\",");
			render("    \"name\": \"");
			render("Complete Bio Profile");
			render("\",");
			render("    \"description\": \"");
			render("All the tools you need for biocuration");
			render("\",");
			render("    \"createdOn\": \"");
			render(dayTime.format(new Date()));
			render("\",");
			render("    \"createdBy\": [");
			render("      {");
			render("        \"@id\": \"");
			render("maurizio.mosca");
			render("\",");
			render("    \"@type\": \"");
			render("foafx:Person");
			render("\",");
			render("        \"foafx:name\": \"");
			render("Dr. Maurizio Mosca-D");
			render("\"");
			render("      }");
			render("    ],");
			render("    \"plugins\": [");
			
			render("       {");
			render("         \"uuid\": \"");
			render("org.mindinformatics.gwt.domeo.plugins.annotation.qualifier");
			render("\",");
			render("         \"status\": \"");
			render("enabled");
			render("\"");
			render("       },");
			
			render("       {");
			render("         \"uuid\": \"");
			render("org.mindinformatics.gwt.domeo.plugins.annotation.nif.antibodies");
			render("\",");
			render("         \"status\": \"");
			render("enabled");
			render("\"");
			render("       },");
			
			render("       {");
			render("         \"uuid\": \"");
			render("org.mindinformatics.gwt.domeo.plugins.resource.pubmed");
			render("\",");
			render("         \"status\": \"");
			render("enabled");
			render("\"");
			render("       },");
			render("       {");
			render("         \"uuid\": \"");
			render("org.mindinformatics.gwt.domeo.plugins.resource.pubmedcentral");
			render("\",");
			render("         \"status\": \"");
			render("enabled");
			render("\"");
			render("       },");
			render("       {");
			render("         \"uuid\": \"");
			render("org.mindinformatics.gwt.domeo.plugins.resource.omim");
			render("\",");
			render("         \"status\": \"");
			render("enabled");
			render("\"");
			render("       },");
			render("       {");
			render("         \"uuid\": \"");
			render("org.mindinformatics.gwt.domeo.plugins.resource.bioportal");
			render("\",");
			render("         \"status\": \"");
			render("enabled");
			render("\"");
			render("       },");
			render("       {");
			render("         \"uuid\": \"");
			render("org.mindinformatics.gwt.domeo.client.component.clipboard");
			render("\",");
			render("         \"status\": \"");
			render("enabled");
			render("\"");
			render("       }");
			render("    ]");
			render("  }");
			
			render(']');
		}
		*/
	}
	
	def all = {
		getUserProfiles();
		/*
		if(params.format.equals("json") ) {
			render('[');
			
			render("  {");
			render("    \"uuid\": \"");
			render("4fa09e38adb4d0.96200877");
			render("\",");
			render("    \"name\": \"");
			render("Complete Bio Profile");
			render("\",");
			render("    \"description\": \"");
			render("All the tools you need for biocuration");
			render("\",");
			render("    \"createdOn\": \"");
			render(dayTime.format(new Date()));
			render("\",");
			render("    \"createdBy\": [");
			render("      {");
			render("        \"@id\": \"");
			render("maurizio.mosca");
			render("\",");
			render("    \"@type\": \"");
			render("foafx:Person");
			render("\",");
			render("        \"foafx:name\": \"");
			render("Dr. Maurizio Mosca");
			render("\"");
			render("      }");
			render("    ],");
			render("    \"plugins\": [");
			
			render("       {");
			render("         \"uuid\": \"");
			render("org.mindinformatics.gwt.domeo.plugins.annotation.qualifier");
			render("\",");
			render("         \"status\": \"");
			render("enabled");
			render("\"");
			render("       },");
			
			render("       {");
			render("         \"uuid\": \"");
			render("org.mindinformatics.gwt.domeo.plugins.annotation.nif.antibodies");
			render("\",");
			render("         \"status\": \"");
			render("enabled");
			render("\"");
			render("       },");
			
			
			render("       {");
			render("         \"uuid\": \"");
			render("org.mindinformatics.gwt.domeo.plugins.resource.pubmed");
			render("\",");
			render("         \"status\": \"");
			render("enabled");
			render("\"");
			render("       },");
			render("       {");
			render("         \"uuid\": \"");
			render("org.mindinformatics.gwt.domeo.plugins.resource.pubmedcentral");
			render("\",");
			render("         \"status\": \"");
			render("enabled");
			render("\"");
			render("       },");
			render("       {");
			render("         \"uuid\": \"");
			render("org.mindinformatics.gwt.domeo.plugins.resource.omim");
			render("\",");
			render("         \"status\": \"");
			render("enabled");
			render("\"");
			render("       },");
			render("       {");
			render("         \"uuid\": \"");
			render("org.mindinformatics.gwt.domeo.plugins.resource.bioportal");
			render("\",");
			render("         \"status\": \"");
			render("enabled");
			render("\"");
			render("       },");
			render("       {"); 
			render("         \"uuid\": \"");
			render("org.mindinformatics.gwt.domeo.client.component.clipboard");
			render("\",");
			render("         \"status\": \"");
			render("enabled");
			render(" \"}   ]");
			render("  },");
			
			render("  {");
			render("    \"uuid\": \"");
			render("4fa09e38adb4d0.96200878");
			render("\",");
			render("    \"name\": \"");
			render("Simple Bio Profile");
			render("\",");
			render("    \"description\": \"");
			render("A few tools to start");
			render("\",");
			render("    \"createdOn\": \"");
			render(dayTime.format(new Date()));
			render("\",");
			render("    \"createdBy\": [");
			render("      {");
			render("        \"@id\": \"");
			render("paolo.ciccarese");
			render("\",");
			render("    \"@type\": \"");
			render("foafx:Person");
			render("\",");
			render("        \"foafx:name\": \"");
			render("Dr. Paolo Ciccarese");
			render("\"");
			render("      }");
			render("    ],");
			render("    \"plugins\": [");
			render("       {");
			render("         \"uuid\": \"");
			render("org.mindinformatics.gwt.domeo.plugins.resource.pubmed");
			render("\",");
			render("         \"status\": \"");
			render("enabled");
			render("\"");
			render("       },");
			render("       {");
			render("         \"uuid\": \"");
			render("org.mindinformatics.gwt.domeo.plugins.resource.pubmedcentral");
			render("\",");
			render("         \"status\": \"");
			render("enabled");
			render("\"");
			render("       },");
			render("       {");
			render("         \"uuid\": \"");
			render("org.mindinformatics.gwt.domeo.plugins.resource.bioportal");
			render("\",");
			render("         \"status\": \"");
			render("enabled");
			render("\"");
			render("       }");
			render("    ]");
			render("  }");
			
			render(']');
		}
		*/
	}
}
