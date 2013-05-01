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
package org.mindinformatics.services.connector.nif.annotator

import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.regex.Matcher
import java.util.regex.Pattern

import org.apache.commons.validator.UrlValidator
import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.json.JSONObject;
import org.mindinformatics.grails.domeo.persistence.UUID
import org.mindinformatics.grails.domeo.persistence.services.IOntology

/**
* Parser for translating the NIF annotator results into a
* suitable JSON format.
*
* @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
*/
class NifAnnotatorResultsConversionService {
    static transactional = false;

	def grailsApplication
	
	private static Integer MAX_LENGTH_PREFIX_AND_SUFFIX=50
	
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
	Map<String,JSONObject> urlToAnnotationTerm = (Map<String,JSONObject>)[:]
	
	public JSONObject convert(String url, List<NifAnnotationItem> items, NifAnnotatorRequestParameters params) {
		
		def textToAnnotate = params.content;
		JSONArray agents = new JSONArray();
		
		JSONObject annotationSet = new JSONObject();
		annotationSet.put(IOntology.generalId, UUID.uuid());
		annotationSet.put(IOntology.generalType, "ao:AnnotationSet");
		annotationSet.put(IOntology.generalLabel, "Nif Annotator Results");
		annotationSet.put(IOntology.generalDescription, generateSetDescription());
		
		JSONObject ncboAnnotator = new JSONObject();
		ncboAnnotator.put(IOntology.generalId, "http://nif-services.neuinfo.org/servicesv1/resource_AnnotateService.html");
		ncboAnnotator.put(IOntology.generalType, "foafx:Software");
		ncboAnnotator.put(IOntology.generalLabel, "Nif Annotator Web Service");
		ncboAnnotator.put("foafx:name", "Nif Annotator Web Service");
		ncboAnnotator.put("foafx:version", "1.0");
		ncboAnnotator.put("foafx:build", "001");
		agents.add(0, ncboAnnotator);

		JSONObject bioportalConnector = new JSONObject();
		def connectorUrn = "urn:domeo:software:id:NifConnector-0.1-001";
		bioportalConnector.put(IOntology.generalId, connectorUrn);
		bioportalConnector.put(IOntology.generalType, "foafx:Software");
		bioportalConnector.put(IOntology.generalLabel, "NifConnector");
		bioportalConnector.put("foafx:name", "NifConnector");
		bioportalConnector.put("foafx:version", "0.1");
		bioportalConnector.put("foafx:build", "001");
		agents.add(1, bioportalConnector);
		
		JSONObject domeo = new JSONObject();
		def domeoUrn = "urn:domeo:software:id:"+grailsApplication.metadata.'app.name'+"-"+grailsApplication.metadata.'app.version'+"-"+grailsApplication.metadata.'app.build';
		domeo.put(IOntology.generalId, domeoUrn);
		domeo.put(IOntology.generalType, "foafx:Software");
		domeo.put(IOntology.generalLabel, grailsApplication.metadata.'app.fullname');
		domeo.put("foafx:name", grailsApplication.metadata.'app.name');
		domeo.put("foafx:version", grailsApplication.metadata.'app.version');
		domeo.put("foafx:build", grailsApplication.metadata.'app.build');
		agents.add(2, domeo);
		
		// annotationSet.put("pav:lineageUri", "");
		// annotationSet.put("pav:createdBy", "");
		// annotationSet.put("pav:lastSavedOn", dateFormat.format(new Date()));
		
		annotationSet.put("pav:importedFrom", "http://nif-services.neuinfo.org/servicesv1/resource_AnnotateService.html");
		annotationSet.put("pav:importedBy", connectorUrn);
		annotationSet.put("pav:importedOn", dateFormat.format(new Date()));
		//annotationSet.put("pav:createdWith", domeoUrn);
		//annotationSet.put("pav:createdOn", dateFormat.format(new Date()));
		
		
		JSONObject permissions = new JSONObject();
		permissions.put("permissions:isLocked", "false");
		permissions.put("permissions:accessType", "urn:domeo:access:public");
		annotationSet.put("permissions:permissions", permissions);
		
		// Annotations
		def sortedAnnotations = items
		// Go through the ncbo results in the order the matches are found in the document
		sortedAnnotations.sort{a,b-> a.start.compareTo(b.start)}
		
		JSONArray annotations = new JSONArray();
		Integer previousStartIdx = sortedAnnotations.empty ? null : sortedAnnotations[0].start
		Integer previousSelectorOffset = 0
		sortedAnnotations.each {NifAnnotationItem nifAnnotation ->
			boolean resultsMoved = (nifAnnotation.start != previousStartIdx);
			if(resultsMoved) {
				previousSelectorOffset = previousSelectorOffset + 1
			}
			previousStartIdx = nifAnnotation.start;
			
			JSONObject selector = findOrCreateAndSaveSelectorUsingStringSearch(nifAnnotation, textToAnnotate, previousSelectorOffset);
			if(selector) previousSelectorOffset = selector['ao:offset']
			
			JSONObject specificTarget = new JSONObject();
			specificTarget.put(IOntology.generalId, UUID.uuid());
			specificTarget.put(IOntology.generalType, IOntology.specificResource);
			specificTarget.put(IOntology.source, url);
			specificTarget.put(IOntology.selector, selector);
			
			JSONObject annotation = new JSONObject();
			annotation.put(IOntology.generalId, UUID.uuid());
			annotation.put(IOntology.generalType, IOntology.annotationQualifier);
			annotation.put(IOntology.generalLabel, "Qualifier");
			annotation.put("pav:createdBy", "http://nif-services.neuinfo.org/servicesv1/resource_AnnotateService.html");
			annotation.put("pav:createdOn", dateFormat.format(new Date()));
			annotation.put("pav:createdWith", domeoUrn);
			annotation.put("pav:importedFrom", "http://nif-services.neuinfo.org/servicesv1/resource_AnnotateService.html");
			annotation.put("pav:importedBy", connectorUrn);
			annotation.put("pav:lastSavedOn", dateFormat.format(new Date()));
			annotation.put("pav:previousVersion", "");
			annotation.put("pav:versionNumber", "");
			
			JSONArray targets = new JSONArray();
			targets.add(0,specificTarget )
			annotation.put(IOntology.hasTarget, targets);
			
			JSONObject term = createAnnotationTerm(nifAnnotation);
			JSONArray topics = new JSONArray();
			topics.add(0,term);
			annotation.put(IOntology.topic, topics);
			annotations.put(annotation);
		}
		
		annotationSet.put(IOntology.annotations, annotations);
		annotationSet.put(IOntology.agents, agents);
		return annotationSet;
	}
	
	private String generateSetDescription(){
		return 'Generated by NIF annotator service'
	}
	
	private JSONObject findOrCreateAndSaveSelectorUsingStringSearch(NifAnnotationItem annotation, String content, Integer start){
		String putativeExactMatch = null
		Map<String,Object> matchInfo = null
		
		//putativeExactMatch = annotation.match;
		putativeExactMatch = content.getAt([annotation.start..(annotation.end-1)])
		//println ')))))))))))))' + putativeExactMatch + "(((((((((((((";
		Integer matchLength = annotation.end - annotation.start 
		//if(matchLength != putativeExactMatch.size()){
		//	throw new RuntimeException("The length of the match in results from ${annotation.start} to ${annotation.end} does not match the length of the exact match ${putativeExactMatch}")
		//}
		matchInfo =  searchForMatch(content, putativeExactMatch, start)

		if(!matchInfo){
			String termNotFoundMsg = "MgrepContext.term.name=${annotation.match}"
			log && log.warn("A selector could not be generated for annotation bean with from=${annotation.start},to=${annotation.end} ${termNotFoundMsg}")
			//println "A selector could not be generated for annotation bean with from=${annotation.start},to=${annotation.end} ${termNotFoundMsg}"
			return null
		}
		
		JSONObject selector = new JSONObject();
		selector.put(IOntology.generalId, UUID.uuid());
		selector.put(IOntology.generalType, IOntology.selectorTextQuote);
		selector.put("pav:createdOn", dateFormat.format(new Date()));
		selector.put(IOntology.selectorTextQuotePrefix, matchInfo.prefix);
		selector.put(IOntology.selectorTextQuoteMatch, matchInfo.exact);
		selector.put(IOntology.selectorTextQuoteSuffix, matchInfo.suffix);
		selector.put("ao:offset", matchInfo.offset);
		return selector;
	}
	
	private def searchForMatch(String textToAnnotate, String putativeExactMatch, int start) {
		String matchRegex = putativeExactMatch.replaceAll(/\s+/,"\\\\s+")
		Pattern pattern = Pattern.compile("\\b${matchRegex}\\b", Pattern.MULTILINE | Pattern.CASE_INSENSITIVE)
		Matcher matcher = pattern.matcher(textToAnnotate)
		int startPos = -1
		int endPos = -1
		if (matcher.find(start)) {
			startPos = matcher.start()
			endPos = matcher.end()
			String exactMatch = textToAnnotate[startPos..endPos - 1]
			String prefix = null;
			if(startPos == 0) {
				prefix = '';
			} else {
			 	prefix = textToAnnotate.getAt([
					 Math.max(startPos - (MAX_LENGTH_PREFIX_AND_SUFFIX + 1), 0)..Math.max(0, startPos - 1)
				])
			}

			String suffix = null;
			if(Math.min(endPos, textToAnnotate.length() - 1)==Math.min(startPos + MAX_LENGTH_PREFIX_AND_SUFFIX, textToAnnotate.length()-1)) {
				suffix = "";
			} else {
				suffix = textToAnnotate.getAt([
					Math.min(endPos, textToAnnotate.length() - 1)..Math.min(startPos + MAX_LENGTH_PREFIX_AND_SUFFIX, textToAnnotate.length()-1)
				])
			}
			//We add 1 to suffix. NCBO position seems to be
			return ['offset':startPos,'prefix': prefix, 'exact': exactMatch, 'suffix': suffix]
		}else{
			return null
		}
	}

	private def createAnnotationTerm(NifAnnotationItem annotation) {
		String fullId =  "http://uri.neuinfo.org/nif/nifstd/" + annotation.id
		//reject any Concept that does not have a fullId that looks like a URL
		log.debug("uri " + fullId);
		log.debug("label " + annotation.match);
		log.debug("category " + annotation.category);

		if(urlToAnnotationTerm[fullId]) return urlToAnnotationTerm[fullId]
		//Look it up in the database by URI. If not there, create a new one
		JSONObject term = urlToAnnotationTerm.get(fullId);
		if(!term) {
			term = new JSONObject();
			term.put(IOntology.generalId, fullId);
			term.put(IOntology.generalLabel, annotation.match);
			term.put(IOntology.generalDescription, "");
			term.put('domeo:category', annotation.category);
			JSONObject source = new JSONObject();
			//source.put(IOntology.generalId, PREFIX_ONTOLOGY_URI+ ncboConcept.ontology.localOntologyId);
			source.put(IOntology.generalId, "http://uri.neuinfo.org/nif/nifstd/");
			//source.put(IOntology.generalLabel, ncboConcept.ontology.name);
			source.put(IOntology.generalLabel, "NIF ontology");
			term.put(IOntology.generalSource, source);
		}
		
		urlToAnnotationTerm[fullId] = term
		term
	}
}
