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
package org.mindinformatics.services.connector.nif.antibodies

import java.util.List;

import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.json.JSONObject;
import org.mindinformatics.services.connector.nif.data.NifAntibodyItem;
import org.mindinformatics.services.connector.nif.data.NifDataRequestParameters;

/**
* @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
*/
class NifAntibodiesResultsConversionService {
    static transactional = false;

	static final String ANTIBODY_TERM_URL ="http://ontology.neuinfo.org/NIF/DigitalEntities/NIF-Investigation.owl#birnlex_2110"
	
    static final String ANTIBODY_REGISTRY_URL = "http://www.antibodyregistry.org"
    static final String ANTIBODY_REGISTRY_LABEL = "Antibodyregistry.org"
	
	public JSONObject convert(String url, List<NifAntibodyItem> items, NifDataRequestParameters params) {
		
		JSONObject message = new JSONObject();
		message.put("pagesize", items.size());
		message.put("pagenumber", "1");
		message.put("totalpages", "1");
		message.put("requesturi", url);
		
		JSONArray results = new JSONArray();
		items.each { item ->
			JSONObject result = new JSONObject();
			result.put("@id", item.antibodyUrl);
			result.put("@type", ANTIBODY_TERM_URL);
			result.put("termId", item.antibodyId);
			result.put("termUri", item.antibodyUrl);
			result.put("termLabel", item.name);
			result.put("description", item.name);
			result.put("target", item.target);
			result.put("vendor", item.vendor);
			result.put("catalog", item.catalog);
			result.put("clonality", item.clonality);
			result.put("cloneId", item.cloneId);
			result.put("sourceOrganism", item.sourceOrganism);
			result.put("sourceUri", ANTIBODY_REGISTRY_URL);
			result.put("sourceLabel", ANTIBODY_REGISTRY_LABEL);
			results.add(result);
		}
		message.put("terms", results);
		
		JSONArray entities = new JSONArray();
		JSONObject antibody = new JSONObject();
		antibody.put("@id", ANTIBODY_TERM_URL);
		antibody.put("rdfs:label", "Antibody");
		antibody.put("obo_annot:synonym", "antibodies");
		entities.add(antibody);
		message.put("entities", entities);
		
		return message;
	}
}
