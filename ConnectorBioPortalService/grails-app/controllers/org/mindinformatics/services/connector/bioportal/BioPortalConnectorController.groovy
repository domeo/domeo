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
package org.mindinformatics.services.connector.bioportal

import grails.converters.JSON

import org.codehaus.groovy.grails.web.json.JSONObject
import org.mindinformatics.domeo.grails.plugins.utils.MiscUtils

/**
 * Connector to the BioPortal new APIs
 * http://data.bioontology.org/documentation#Ontology
 * 
 * @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
 */
class BioPortalConnectorController {

	static String[] DEFAULT_ANNOTATOR_ONTOLOGIES = ["PR", "GO", "NIFSTD", "DOID"];
	//static int[] DEFAULT_SEARCH_ONTOLOGIES = [1062, 1070, 1009, 1084];
	
	def jsonBioPortalVocabulariesService;
	
	def index = {
		render 'BioPortalConnectorController: No defined default action';
	}
	
	def ontologies = {
		String apikey = grailsApplication.config.domeo.plugins.connector.bioportal.apikey;
		
		try {
			JSONObject jsonResult = jsonBioPortalVocabulariesService.retrieveOntologies(apikey);
			render(contentType:'text/json', encoding:MiscUtils.DEFAULT_ENCODING,  text: jsonResult.toString());
		} catch(Exception e) {
			// mailingService.notifyProblemByEmail("BioPortal Annotator Vocabularies", "[apikey:"+ apikey + "] " + e.getMessage());
			render(status: "500", text: "BioPortal vocabularies: " + e.getMessage());
		}
	}
	
	/*
	 * http://data.bioontology.org/documentation#nav_search
	 * 
	 * http://localhost:8080/ConnectorBioPortalService/bioPortalConnector/search?query=APP
	 */
	def search = {
		String apikey = grailsApplication.config.domeo.plugins.connector.bioportal.apikey;
		String textQuery = params.textQuery;
		String ontologies = params.ontologies;
		String pageNumber = (params.pagenumber?params.pagenumber:1);
		String pageSize = (params.pagesize?params.pagesize:50);

		JSONObject jsonResult = jsonBioPortalVocabulariesService.search(apikey, textQuery, "", pageNumber, pageSize);
		render jsonResult as JSON;
	}
	
	/*
	 * http://data.bioontology.org/documentation#nav_annotator
	 * 
	 * Generates 404
	 * http://data.bioontology.org/annotator?max_level=0&text=Melanoma+is+a+malignant+tumor+of+melanocytes+which+are+found+predominantly+in+skin+but+also+in+the+bowel+and+the+eye.&apikey=fef6b9da-4b3b-46d2-9d83-9a1a718f6a22&minimum_match_length=0&ontologies=1070,1084,1009,1062&mappingTypes=Manual
	 * 
	 * http://localhost:8080/ConnectorBioPortalService/bioPortalConnector/annotate?textContent=Melanoma+is+a+malignant+tumor+of+melanocytes+which+are+found+predominantly+in+melanoma+skin+but+also+in+the+bowel+and+the+eye.
	 * http://localhost:8080/ConnectorBioPortalService/bioPortalConnector/annotate?textContent=Melanoma+is+a+malignant+tumor+of+melanocytes+which+are+found+predominantly+in+skin+but+also+in+the+bowel+and+the+eye.
	 */
	def annotate = {
		String url = params.url;
		String apikey = grailsApplication.config.domeo.plugins.connector.bioportal.apikey;
		String textContent = params.textContent;
		String ontologies = params.ontologies;
		
		def parametrization = [:];

		
		if(params.semantic_types) parametrization.put("semantic_types", params.semantic_types); else parametrization.put("semantic_types", "false");
		if(params.mapping_types) parametrization.put("mapping_types", params.mapping_types); 
		if(params.stop_words) parametrization.put("stop_words", params.stop_words);
		if(params.max_level) parametrization.put("max_level", params.max_level);
		if(params.minimum_match_length) parametrization.put("minimum_match_length", params.minimum_match_length);
	
		try {
			JSONObject jsonResult = jsonBioPortalVocabulariesService.annotate(apikey, url, DEFAULT_ANNOTATOR_ONTOLOGIES, textContent, parametrization);
			render(contentType:'text/json', encoding:MiscUtils.DEFAULT_ENCODING,  text: jsonResult.toString());
		} catch(Exception e) {
		
			println 'yolo ' + e
//			mailingService.notifyProblemByEmail("BioPortal Annotator", "[apikey:"+ apikey + ", url:"+ url +
//				", ontologies:" + ontologies + ", textContent:"+ textContent + "] " + e.getMessage());
			render(status: "500", text: "BioPortal annotator endpoint: " + e.getMessage());
		}
	}
}
