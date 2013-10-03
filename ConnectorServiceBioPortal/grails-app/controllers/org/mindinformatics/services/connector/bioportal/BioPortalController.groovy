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

import org.codehaus.groovy.grails.web.json.JSONObject
import org.mindinformatics.domeo.grails.plugins.utils.MiscUtils


/**
* @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
*/
class BioPortalController {

    //static String DEFAULT_API_KEY = 'fef6b9da-4b3b-46d2-9d83-9a1a718f6a22';
    static int[] DEFAULT_ANNOTATOR_ONTOLOGIES = [1062, 1070, 1009, 1084];
    static int[] DEFAULT_SEARCH_ONTOLOGIES = [1062, 1070, 1009, 1084];
    
    // 1009 Human Disease Ontology
    // 1084 NIFSTD
    // 1062 Protein Ontology
    // 1070 Gene Ontology (GO)
    
	def grailsApplication;
    def domeoConfigAccessService;   // To access configurations
    def mailingService;             // Mailing service used for notifications of failure
    
	def jsonBioPortalTermsRetrievalService;
	def jsonBioPortalAnnotatorService;
	def jsonBioPortalVocabulariesRetrievalService;
	
	/**
	 * Example: http://localhost:8080/ConnectorServiceBioPortal/BioPortal/search?textQuery=app&ontologies=1084,1062&pagesize=10&pagenumber=3
	 */
	def search = {
		String apikey = grailsApplication.config.domeo.plugins.connector.bioportal.apikey;
		String textQuery = params.textQuery;
		String ontologies = params.ontologies;
		String pageNumber = params.pagenumber;
		String pageSize = params.pagesize;
		
		JSONObject jsonResult = new JSONObject();	
        try {
    		if(ontologies!=null && ontologies.trim().length()>0 
                && pageNumber!=null && pageNumber.trim().length()>0 
                && pageSize!=null && pageSize.trim().length()>0) {
 
    			def ontos = [];
    			StringTokenizer st = new StringTokenizer(ontologies, ",");
    			while(st.hasMoreTokens()) {
    				ontos << st.nextToken();
    			}
                
    			jsonResult = jsonBioPortalTermsRetrievalService.searchTerm(apikey, textQuery, ontos, 
    				Integer.parseInt(pageNumber), Integer.parseInt(pageSize));
    		} else {
    			jsonResult = jsonBioPortalTermsRetrievalService.searchTerm(apikey, textQuery, DEFAULT_SEARCH_ONTOLOGIES);
    		}		
    		render(contentType:'text/json', encoding:MiscUtils.DEFAULT_ENCODING,  text: jsonResult.toString());
        } catch(Exception e) {
            mailingService.notifyProblemByEmail("BioPortal search", "[apikey:"+ apikey + ", query:"+ textQuery + 
                ", ontologies:"+ ontologies + ", pageNumber:"+ pageNumber + "] " + e.getMessage());
            render(status: "500", text: "BioPortal Search: " + e.getMessage());
        }
	}
    
    def ontologies = {
		String apikey = grailsApplication.config.domeo.plugins.connector.bioportal.apikey;
		
		try {
			JSONObject jsonResult = jsonBioPortalVocabulariesRetrievalService.retrieveOntologies(apikey);
			render(contentType:'text/json', encoding:MiscUtils.DEFAULT_ENCODING,  text: jsonResult.toString());
		} catch(Exception e) {
			mailingService.notifyProblemByEmail("BioPortal Annotator Vocabularies", "[apikey:"+ apikey + "] " + e.getMessage());
			render(status: "500", text: "BioPortal vocabularies: " + e.getMessage());
		}
    }
	
	def textmine = {
		String url = params.url;
		String apikey = grailsApplication.config.domeo.plugins.connector.bioportal.apikey;
		String textContent = params.textContent;
		String ontologies = params.ontologies;
        
        try {
    		JSONObject jsonResult = jsonBioPortalAnnotatorService.textMine(url, apikey, DEFAULT_ANNOTATOR_ONTOLOGIES, textContent);
    		render(contentType:'text/json', encoding:MiscUtils.DEFAULT_ENCODING,  text: jsonResult.toString());
        } catch(Exception e) {
            mailingService.notifyProblemByEmail("BioPortal Annotator", "[apikey:"+ apikey + ", url:"+ url + 
                ", ontologies:" + ontologies + ", textContent:"+ textContent + "] " + e.getMessage());
            render(status: "500", text: "BioPortal Search: " + e.getMessage());
        }
	}
}
