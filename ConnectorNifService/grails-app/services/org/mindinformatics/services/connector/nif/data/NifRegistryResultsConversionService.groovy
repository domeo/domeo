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
package org.mindinformatics.services.connector.nif.data

import java.util.List;

import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.json.JSONObject;

/**
* @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
*/
class NifRegistryResultsConversionService {
    static transactional = false;

	static final String RESOURCE_TERM_URL ="http://ontology.neuinfo.org/NIF/DigitalEntities/NIF-Resource.owl#nlx_res_20090101"
	
	public JSONObject convert(String url, List<NifResourceItem> items, NifDataRequestParameters params) {
		
		JSONObject message = new JSONObject();
		message.put("pagesize", items.size());
		message.put("pagenumber", "1");
		message.put("totalpages", "1");
		message.put("requesturi", url);
		
		JSONArray results = new JSONArray();
		items.each { item ->
			JSONObject result = new JSONObject();
			result.put("@id", item.url);
			result.put("@type", RESOURCE_TERM_URL);
            result.put("termLabel", item.name);
            result.put("description", item.description);
            result.put("sourceUri", "http://www.neuinfo.org");
            result.put("sourceLabel", "NIF");
			results.add(result);
		}
		message.put("terms", results);
		
		JSONArray entities = new JSONArray();
		JSONObject entityType = new JSONObject();
		entityType.put("@id", RESOURCE_TERM_URL);
		entityType.put("rdfs:label", "Resource");
		entityType.put("obo_annot:synonym", "Resource Descriptor");
		entities.add(entityType);
		message.put("entities", entities);
		
		return message;
	}
}
