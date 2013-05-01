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
package org.mindinformatics.services.connector.bioportal.search

import org.codehaus.groovy.grails.web.json.JSONObject
import org.mindinformatics.domeo.grails.plugins.utils.MiscUtils
import org.mindinformatics.services.connector.bioportal.terms.ParseXMLFile


/**
* @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
*/
class JsonBioPortalTermsRetrievalService {
	static transactional = false

	def grailsApplication;
    def domeoConfigAccessService;

	public static final String PREFIX = 'http://rest.bioontology.org/bioportal/search/?query=';
	public static final String POSTFIX = '&apikey=';
	public static final String ONTOLOGIES = '&ontologyids=';
	
	/**
	 * Returns all the matches for a search query.
	 * @param query	The textual query
	 * @return All the matches for the query
	 */
	public JSONObject searchTerm(String apikey, String query, def ontologies) {  
        
        String ontos = parseOntologiesIds(ontologies);
          
		String uri = PREFIX + URLEncoder.encode(query, MiscUtils.DEFAULT_ENCODING) + POSTFIX + apikey + ONTOLOGIES + ontos;
        log.info("Search term with URI: " + uri);
        if(domeoConfigAccessService.isProxyDefined()) {
            log.info("proxy: " + domeoConfigAccessService.getProxyIp() + "-" + domeoConfigAccessService.getProxyPort());
            return ParseXMLFile.parseXMLFile(uri, domeoConfigAccessService.getProxy());
        } else {
            return ParseXMLFile.parseXMLFile(uri, null);
        }
	}
	
	/**
	 * Returns the list of terms and the stats for pagination.
	 * Example: http://rest.bioontology.org/bioportal/search/app?apikey=fef6b9da-4b3b-46d2-9d83-9a1a718f6a22&ontologyids=1084,1062&pagesize=10
	 * 
	 * @param query			The textual query
	 * @param ontologies	The list of ontologies to be searched
	 * @return The list of terms and the pagination statistics.
	 */
	public JSONObject searchTerm(String apikey, String query, def ontologies, int pageNumber, int pageSize) {
		String ontos = parseOntologiesIds(ontologies);

		String uri = PREFIX + URLEncoder.encode(query, encoding:MiscUtils.DEFAULT_ENCODING, ) + POSTFIX + '&ontologyids=' + ontos +
			'&pagenum=' + pageNumber + '&pagesize=' + pageSize;
        log.info("Search term with URI: " + uri);
		if(domeoConfigAccessService.isProxyDefined()) {
			log.info("proxy: " + domeoConfigAccessService.getProxyIp() + "-" + domeoConfigAccessService.getProxyPort());
            return ParseXMLFile.parseXMLFile(uri, domeoConfigAccessService.getProxy());
		} else {
		    return ParseXMLFile.parseXMLFile(uri, null);
		}
	}
    
    private String parseOntologiesIds(def ontologies) {
        StringBuffer ontos = new StringBuffer();
        int counter=0;
        ontologies.each {
            ontos.append(it);
            if((counter++)<ontologies.size()-1) ontos.append(",");
        }
        return ontos.toString();
    }
}
