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
package org.mindinformatics.grails.domeo.client.profiles.services

import org.mindinformatics.grails.domeo.client.profiles.model.DomeoClientProfile
import org.mindinformatics.grails.domeo.client.profiles.model.DomeoClientProfileEntry
import org.mindinformatics.grails.domeo.client.profiles.model.UserCurrentDomeoClientProfile
import org.mindinformatics.grails.domeo.client.profiles.model.UserAvailableDomeoClientProfile




/**
 * @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
 */
class ProfilesService {

	def grailsApplication
	
	public boolean saveCurrentUserProfile(def user, def domeoClientProfile) {
		def currentProfile = UserCurrentDomeoClientProfile.findByUser(user);
		if(currentProfile!=null) {
			currentProfile.currentProfile = domeoClientProfile;
		} else {
			def current = new UserCurrentDomeoClientProfile(user: user, currentProfile: domeoClientProfile)
			if(!current.save()) {
				log.error("Cannot save current profile!");
				return false;
			}
			return true;
		}
	}
	
	public DomeoClientProfile getDefaultProfile() {
		// TODO better define the default profile
		return DomeoClientProfile.list().get(0);
	}
	
	public DomeoClientProfile getCurrentUserProfileOrDefault(def user) {
		def domeoProfile = getCurrentUserProfile(user);
		if(domeoProfile!=null) return domeoProfile.currentProfile;
		else getDefaultProfile();
	}
	
	public boolean saveCurrentProfile(def user, def uuid) {
		if(grailsApplication.config.domeo.profiles.available.all.equalsIgnoreCase("true")) {
			def profiles = DomeoClientProfile.list();
			profiles.each { profile ->
				if(profile.id==uuid) {
					def pr = DomeoClientProfile.findById(uuid);
					def cp = UserCurrentDomeoClientProfile.findByUser(user);
					if(cp!=null) {
						log.info('Updating current profile ' + uuid + ' for user ' + user.id)
						cp.currentProfile = pr;
						return true;
					} else {
						log.info('Saving new current profile ' + uuid + ' for user ' + user.id)
						new UserCurrentDomeoClientProfile(
							user: user,
							currentProfile: profile
						).save(failOnError: true, flash: true)
						return true;
					}
				}
			}
		} else {
			def profiles = UserAvailableDomeoClientProfile.findAllByUser(user);
			profiles.each { userProfile ->
				if(userProfile.profile.id==uuid) {
					log.info('Updating current profile ' + uuid + ' for user ' + user.id)
					def pr = DomeoClientProfile.findById(uuid);
					def cp = UserCurrentDomeoClientProfile.findByUser(user);
					if(cp!=null) {
						cp.currentProfile = pr;
						return true;
					}
				}			
			}
		}
		return false;
	}
	
	public def getProfileEntries(def profile) {
		DomeoClientProfileEntry.findAllByProfile(profile);
	}
	
	public def getProfileEntries(def profile, def type) {
		DomeoClientProfileEntry.findAllByProfileAndType(profile,type);
	}
	
	public UserCurrentDomeoClientProfile getCurrentUserProfile(def user) {
		return UserCurrentDomeoClientProfile.findByUser(user);
	}
	
	public def getDefaultProfiles() {
		// TODO better define the default profile
		return DomeoClientProfile.list();
	}
	
	public def getAvailableUserProfiles(def user) {
		def profiles = UserAvailableDomeoClientProfile.findAllByUser(user);
		if(profiles!=null) return profiles;
		else getDefaultProfiles();
	}
}
