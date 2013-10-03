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
package org.mindinformatics.services.connector.bioportal.vocabularies

import org.codehaus.groovy.grails.web.json.JSONObject
import org.mindinformatics.services.connector.bioportal.terms.ParseXMLVocabulariesFile


/**
* @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
*/
class JsonBioPortalVocabulariesRetrievalService {
	static transactional = false

	def grailsApplication;
    def domeoConfigAccessService;

	public static final String PREFIX = 'http://rest.bioontology.org/bioportal/ontologies';
	public static final String POSTFIX = '?apikey=';
	
	/**
	 * Returns all the matches for a search query.
	 * @param query	The textual query
	 * @return All the matches for the query
	 */
	public JSONObject retrieveOntologies(String apikey) {  
        
		String uri = PREFIX + POSTFIX + apikey;
        log.info("List vocabularies with URI: " + uri);
        if(domeoConfigAccessService.isProxyDefined()) {
            log.info("proxy: " + domeoConfigAccessService.getProxyIp() + "-" + domeoConfigAccessService.getProxyPort());
            return ParseXMLVocabulariesFile.parseXMLFile(uri, domeoConfigAccessService.getProxy());
        } else {
            return ParseXMLVocabulariesFile.parseXMLFile(uri, null);
        }
	}
}
