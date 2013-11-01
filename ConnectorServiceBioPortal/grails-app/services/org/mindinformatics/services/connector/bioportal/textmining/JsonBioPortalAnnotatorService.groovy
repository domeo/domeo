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
package org.mindinformatics.services.connector.bioportal.textmining

import org.codehaus.groovy.grails.web.json.JSONObject;

/**
* @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
*/
class JsonBioPortalAnnotatorService {
	static transactional = false
	
	AnnotatorResultsConversionService annotatorResultsConversionService;
	AnnotatorExternalClientService annotatorExternalClientService;

	
	public JSONObject textMine(String url, String apikey, int[] ontologies, String documentText, def parametrization) throws AnnotatorException {
		BioPortalTextMiningRequestParameters params = defaultParams();
        params.apikey = apikey
		params.textToAnnotate = documentText;		
		params.ontologiesToKeepInResult = ontologies
		params.longestOnly = new Boolean(parametrization.getAt("longestOnly"));
		params.wholeWordOnly = new Boolean(parametrization.getAt("wholeWordOnly"));
		params.filterNumbers = new Boolean(parametrization.getAt("filterNumbers"));
		params.withDefaultStopWords = new Boolean(parametrization.getAt("withDefaultStopWords"));
		params.isStopWordsCaseSensitive = new Boolean(parametrization.getAt("isStopWordsCaseSensitive"));
		params.scored = new Boolean(parametrization.getAt("scored"));
		params.withSynonyms = new Boolean(parametrization.getAt("withSynonyms"));
		
		BioPortalAnnotatorResults ncboResults = annotatorExternalClientService.textmineDocument(params)
		annotatorResultsConversionService.convert(url, ncboResults, params)
	}
	
	 BioPortalTextMiningRequestParameters defaultParams(String apikey){
		BioPortalTextMiningRequestParameters params = new BioPortalTextMiningRequestParameters()
		//On examination of results, automatic mappings can be non-sensical. Lets use only manual ones
		params.mappingTypes = ['Manual'] as Set
		//Also, from direct observation, ontologies of format RRF, concepts do not have URIs. Let's exclude those from results right off the bat
		//List<ExtendedRecord> ontologiesInResults = annotatorExternalClientService.findAllOntologies(apikey).findAll{ExtendedRecord ontology-> ontology.format != 'RRF'}
		//params.ontologiesToKeepInResult  = ontologiesInResults.collect{ExtendedRecord ontology-> ontology.localOntologyId} as Set
		params
	}
}
