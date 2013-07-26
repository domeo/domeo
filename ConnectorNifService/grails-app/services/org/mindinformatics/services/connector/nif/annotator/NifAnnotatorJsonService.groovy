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


import groovyx.net.http.ContentType
import groovyx.net.http.EncoderRegistry
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method

import org.apache.http.conn.params.ConnRoutePNames
import org.codehaus.groovy.grails.web.json.JSONObject;
import org.mindinformatics.domeo.grails.plugins.utils.ConnectorHttpResponseException
import org.mindinformatics.domeo.grails.plugins.utils.MiscUtils

/**
 * This is the service that connects to the NIF (Neuroscience Information Framework)
 * annotator and converts the results into a suitable JSON format that can be consumed
 * by the Domeo Annotation Web Toolkit.
 * 
 * @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
 */
class NifAnnotatorJsonService {
	static transactional = false;
    
	final String SERVICE_URL = 'http://beta.neuinfo.org/services/v1/annotate/entities' //'http://nif-services.neuinfo.org/servicesv1/v1/annotate/entities' 
	final String CONTENT = 'content'
	final String ONTOLOGY_IN = 'includeCat'
	final String ONTOLOGY_OUT = 'excludeCat'
	final String LONGEST_ONLY = 'longestOnly'
	final String INCLUDE_ABBREV = 'includeAbbrev'
	final String INCLUDE_ACRONYM = 'includeAcronym'
	final String INCLUDE_NUMBERS = 'includeNumbers'
	
	def grailsApplication;
    def domeoConfigAccessService;
	def nifAnnotatorResultsConversionService;
	
	/**
	 * 
	 * @param url		The URL of the document where the content comes from
	 * @param content	The textual content to be annotated
	 * @return	The results in JSON format.
	 * @throws AnnotatorException
	 */
	public JSONObject annotate(String url, String content) throws ConnectorHttpResponseException {
        log.info("Nif annotate(" + url + "," + content + ")");
		NifAnnotatorRequestParameters params = new NifAnnotatorRequestParameters();
		params.content = content;
		ArrayList<NifAnnotationItem> annotations = callService(composeUrl(content, "", ""));
		return nifAnnotatorResultsConversionService.convert(url, annotations, params);
	}
	
	/**
	 * 
	 * @param url		The URL of the document where the content comes from
	 * @param content	The textual content to be annotated
	 * @param include	The list of categories to include (comma separated)
	 * @param exclude	The list of categories to exclude (comma separated)
	 * @return	The results in JSON format
	 * @throws AnnotatorException
	 */
	public JSONObject annotate(String url, String content, String include, String exclude, String longestOnly, String includeAbbrev, String includeAcronym, String includeNumbers) throws ConnectorHttpResponseException {
		log.info("Nif annotate(" + url + "," + content + "," + include + "," + exclude + "," + longestOnly + "," + includeAbbrev + "," + includeAcronym + "," + includeAcronym + ")"); 
		NifAnnotatorRequestParameters params = new NifAnnotatorRequestParameters();
		params.content = content;
		ArrayList<NifAnnotationItem> annotations = callService(composeUrl(content, "", "", longestOnly, includeAbbrev, includeAcronym, includeAcronym));
		return nifAnnotatorResultsConversionService.convert(url, annotations, params);
	}
	
	/**
	 * Returns the full URL for the call to the NIF annotator
	 * @param content	The textual content to be annotated
	 * @param include	The list of categories to include (comma separated)
	 * @param exclude	The list of categories to exclude (comma separated)
	 * @return	The URL for the service call
	 */
	private String composeUrl(String content, String include, String exclude, String longestOnly, String includeAbbrev, String includeAcronym, String includeNumbers) {
		def includes = parseCommaSeparatedList(include);
		def excludes = parseCommaSeparatedList(exclude);
		def includesText = createParametersList(ONTOLOGY_IN, includes);
		def excludesText = createParametersList(ONTOLOGY_OUT, excludes);
		def longestOnlyText = createParameterItem(LONGEST_ONLY, longestOnly);
		def includeAbbrevText = createParameterItem(INCLUDE_ABBREV, includeAbbrev);
		def includeAcronymText = createParameterItem(INCLUDE_ACRONYM, includeAcronym);
		def includeNumbersText = createParameterItem(INCLUDE_NUMBERS, includeNumbers);

		return SERVICE_URL + '?content=' + java.net.URLEncoder.encode(content)  + longestOnlyText + includeAbbrevText + includeAcronymText + includeNumbersText;
	}
	
	/**
	 * Returns the list of comma-separated items as a list.
	 * @param list	The list of comma separated items
	 * @return	The list of the items.
	 */
	private def parseCommaSeparatedList(String list) {
		def items = [];
		StringTokenizer st = new StringTokenizer(list,",");
		while(st.hasMoreTokens()) {
			items <- st.nextToken();
		}
		return items;
	}
	
	/**
	 * Returns a URL representation of the list of values for a given parameter
	 * @param name		The name of the parameter
	 * @param values	The list of values
	 * @return	The textual representation (for URL) of parameters.
	 */
	private String createParametersList(String name, def values) {
		StringBuffer sb = new StringBuffer();
		values.each { value ->
			sb.append("&").append(name).append("=").append(java.net.URLEncoder.encode(value));
		}
		sb.toString();
	}
	
	private String createParameterItem(String name, String value) {
		if(value==null) return "";
		"&" + name + "=" + java.net.URLEncoder.encode(value);
	}
	
	/**
	 * Performs the call to the service.
	 * @param url	The complete URL request
	 * @return	The results.
	 */
	private ArrayList<NifAnnotationItem> callService(String url) {
        log.info("Annotating with URL: " + url);
		try {
			ArrayList<NifAnnotationItem> items = new ArrayList<NifAnnotationItem>();
			def http = new HTTPBuilder(url)
            http.encoderRegistry = new EncoderRegistry(charset: MiscUtils.DEFAULT_ENCODING)
            if(domeoConfigAccessService.isProxyDefined()) {
                http.client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, domeoConfigAccessService.getProxyHttpHost());
            }
						
			// perform a POST request, expecting TEXT response
			http.request(Method.GET) {
				requestContentType = ContentType.URLENC
				
				// response handler for a success response code
				response.success = { resp, xml ->
                    log.info("response status: ${resp.statusLine}");
                    log.info("The response contenType is $resp.contentType");
                    log.info("XML was ${xml} " + xml.text());

					//def annotations = new XmlParser().parse(xml);
					def annotations = xml.childNodes();
					annotations.each { annotation ->
						NifAnnotationItem item = new NifAnnotationItem();
						//println "annotation " + annotation.text();
						item.match = annotation.text();
						def attributes = annotation.attributes();
						//println attributes.get("start");

						item.start = new Integer(attributes.get("start"));
						//println attributes.get("end");
						item.end = new Integer(attributes.get("end"));
						def entity = annotation.childNodes().next();
						//println entity.text();

						def entityAttributes = entity.attributes();
						//println entityAttributes.get("category");
						item.category = entityAttributes.get("category");
						//println entityAttributes.get("id");
						item.id =  entityAttributes.get("id");
						items.add(item);
					}
					return items;
				}
				
                response.'404' = { resp ->
                    log.error('Not found: ' + resp.getStatusLine())
                    throw new ConnectorHttpResponseException(resp, 404, 'Service not found. The problem has been reported')
                }
             
                response.'503' = { resp ->
                    log.error('Not available: ' + resp.getStatusLine())
                    throw new ConnectorHttpResponseException(resp, 503, 'Service temporarily not available. Try again later.')
                }
                
                response.failure = { resp, xml ->
                    log.error('failure: ' + resp.getStatusLine())
                    throw new ConnectorHttpResponseException(resp, resp.getStatusLine())
                }
			}
			return items

		} catch (groovyx.net.http.HttpResponseException ex) {
			log.error("HttpResponseException: " + ex.getMessage())
            throw new RuntimeException(ex);
		} catch (java.net.ConnectException ex) {
			log.error("ConnectException: " + ex.getMessage())
            throw new RuntimeException(ex);
		}
	}
}
