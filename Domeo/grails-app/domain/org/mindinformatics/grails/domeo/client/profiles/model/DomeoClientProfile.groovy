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
package org.mindinformatics.grails.domeo.client.profiles.model

import java.util.Date;

import org.mindinformatics.grails.domeo.dashboard.security.User

/**
 * @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
 */
class DomeoClientProfile {
	
	private static final int NAME_MAX_SIZE = 255;
	
	String id;
	String name;
	String description;
	User createdBy;
	Date dateCreated, lastUpdated
	
	static hasMany = [entries: DomeoClientProfileEntry] 
	
	static constraints = {
		id maxSize: 36
		description (nullable: true, blank: true, maxSize:NAME_MAX_SIZE)
	}
	
	static mapping = {
		id generator:'uuid', sqlType: "varchar(36)"
	}
}
