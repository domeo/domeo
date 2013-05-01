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
package org.mindinformatics.services.connector.pubmed

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.json.JSONObject


/**
* @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
*/
class PubmedController {

	private static final Log logger = LogFactory.getLog(PubmedController.class);
	
	final def PUBMED_ID = "pubmedId";
	final def PUBMED_IDS = "pubmedIds";
	final def PUBMED_CENTRAL_ID = "pubmedCentralId";
	final def PUBMED_CENTRAL_IDS = "pubmedCentralIds";
	
	def jsonPubmedAccessService;
	
	// TODO Exception management
	/**
	 * Allows only single identifiers requests: PUBMED_ID or PUBMED_CENTRAL_ID.
	 * Multiple requests should go through the entries method.
	 */
	def entry = {
		String apikey = params.apikey;
		String typeQuery = params.typeQuery;
		String textQuery = params.textQuery;
		
		logger.info("PubMed entry request typeQuery: " + typeQuery + " | textQuery: "+ textQuery);
		
		JSONArray json = new JSONArray();
		if(typeQuery.trim().equals(PUBMED_ID)) {			
			JSONObject jsonObject = jsonPubmedAccessService.getPubmedArticle(textQuery);
			if(jsonObject!=null) json.add(jsonObject);
			else log.warn "No record returned for PubMed id: " + textQuery;
			render(contentType:'text/json', text: json.toString())
		} else if(typeQuery.trim().equals(PUBMED_CENTRAL_ID)) {
			StringTokenizer st = new StringTokenizer(textQuery,",");
			List<String> ids = new ArrayList<String>();
			while (st.hasMoreTokens()) {
				ids.add(st.nextToken());
			}
			json = jsonPubmedAccessService.searchPubmedArticles(typeQuery, ids);
			println json;
			render(contentType:'text/json', text: json.toString());
		} else {
			logger.warn("entry() cannot execute a query of type: " + typeQuery);
			render(contentType:'text/json', text: new JSONArray());
		}
	}
	
	/**
	 * Returns the PubMed json records for the entries identified by the PubMed  
	 * comma separated ids in the textQuery.
	 */
	def entries = {	
		String apikey = params.apikey;
		String typeQuery = params.typeQuery;
		String textQuery = params.textQuery;
		
		logger.info("PubMed entries request typeQuery: " + typeQuery + " | textQuery: "+ textQuery);
		println "PubmedController.entries request typeQuery: " + typeQuery + " | textQuery: "+ textQuery;
		
		if(typeQuery.equals(PUBMED_IDS)) {
			StringTokenizer st = new StringTokenizer(textQuery, ",");
			List<String> pmids = new ArrayList<String>();
			while(st.hasMoreTokens()) {
				pmids.add(st.nextToken())
			}
			JSONArray json = jsonPubmedAccessService.getPubmedArticles(pmids);
			render(contentType:'text/json', text: json.toString())
		} else {
			logger.warn("entries() cannot execute a query of type: " + typeQuery);
			render(contentType:'text/json', text: new JSONArray());
		}
	}
	
	def search = {
		String apikey = params.apikey;
		String typeQuery = params.typeQuery;
		String textQuery = params.textQuery;
		
		if(textQuery==null) { // If text query is empty return empty results list
			render(contentType:'text/json', text: '{"total":"0","results":[],"exception":"Text query is empty"}');
			return;
		} else { // Else trim the query text
			textQuery = textQuery.trim();
		}

		int startMonth = (params.startMonth!=null) ? Integer.parseInt(params.startMonth) : -1;
		int startYear = (params.startYear!=null) ? Integer.parseInt(params.startYear) : -1;
		int endMonth = (params.endMonth!=null) ? Integer.parseInt(params.endMonth) : -1;
		int endYear = (params.endYear!=null) ? Integer.parseInt(params.endYear) : -1;
		
		int maxResults = (params.maxResults!=null) ? Integer.parseInt(params.maxResults) : -1;
		int offset = (params.offset!=null) ? Integer.parseInt(params.offset) : -1;
		
		logger.info("PubMed search request typeQuery: " + typeQuery + " | textQuery: "+ textQuery + " | maxResults: " + maxResults+ " | offset: " + offset);
		
		StringTokenizer st = new StringTokenizer(textQuery, ",");
		List<String> queryTerms = new ArrayList<String>();
		while(st.hasMoreTokens()) {
			queryTerms.add(st.nextToken().trim())
		}

		JSONObject json = jsonPubmedAccessService.searchPubmedArticlesWithStats(typeQuery, queryTerms, startMonth, startYear, endMonth, endYear, maxResults, offset);
		render(contentType:'text/json', text: json.toString())
	}
}
