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
package org.mindinformatics.services.connector.bioportal.services

import groovyx.net.http.ContentType
import groovyx.net.http.EncoderRegistry
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method

import org.apache.http.conn.params.ConnRoutePNames
import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.json.JSONObject
import org.mindinformatics.domeo.grails.plugins.utils.ConnectorHttpResponseException
import org.mindinformatics.domeo.grails.plugins.utils.MiscUtils
import org.mindinformatics.services.connector.bioportal.BioPortalAnnotatorRequestParameters

/**
 * @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
 */
class JsonBioPortalVocabulariesService {
	static transactional = false
	
		def grailsApplication;
		def domeoConfigAccessService;
	
		final static APIKEY = "?apikey=";
		final static QUERY = "&q=";
		final static ONTOLOGIES = "&ontologies=";
		final static PAGE = "&page=";
		final static PAGESIZE = "&pagesize=";
		
		/**
		 * Returns all the matches for a search query.
		 * @param query	The textual query
		 * @return All the matches for the query
		 */
		public JSONObject retrieveOntologies(String apikey) {
			
			String uri = 'http://data.bioontology.org/resource_index/ontologies' + APIKEY + apikey;
			log.info("List vocabularies with URI: " + uri);
			if(domeoConfigAccessService.isProxyDefined()) {
				log.info("proxy: " + domeoConfigAccessService.getProxyIp() + "-" + domeoConfigAccessService.getProxyPort());
				//return ParseXMLVocabulariesFile.parseXMLFile(uri, domeoConfigAccessService.getProxy());
			} else {
				log.info("no proxy " + uri);
				//return ParseXMLVocabulariesFile.parseXMLFile(uri, null);
			}

			try {
				//ArrayList<NifAnnotationItem> items = new ArrayList<NifAnnotationItem>();
				def http = new HTTPBuilder(uri)
				http.encoderRegistry = new EncoderRegistry(charset: MiscUtils.DEFAULT_ENCODING)
				if(domeoConfigAccessService.isProxyDefined()) {
					http.client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, domeoConfigAccessService.getProxyHttpHost());
				}
							
				// perform a POST request, expecting TEXT response
				http.request(Method.GET, ContentType.JSON) {
					requestContentType = ContentType.URLENC
					
				response.success = { resp, json ->		
//					println json.names();
//					println resp.getClass().getName();
//					println resp.getData().getClass().getName();
					
//					json.data.each {
//						println "one";
//					  }
//					
//					println resp.status
//									
					json.each {  // iterate over JSON 'status' object in the response:
						//println it
						println '*****************************************'
						println it;
						/*
						println it.getValue().getClass().getName();
						println it.getValue().names();
						println it.getValue().get("data").getClass().getName();	
						it.getValue().get("data").each { el ->
							println el.getClass().getName();
							println el.names();
							println el.get("list").getClass().getName();
							el.get("list").each { el2 ->
								println el2.getClass().getName();
								println el2.names();
								println el2.get("ontologyBean").getClass().getName();
							}
						}
						*/
				    }
				}
					
//					// response handler for a success response code
//					response.success = { resp, xml ->
//						log.info("response status: ${resp.statusLine}");
//						log.info("The response contenType is $resp.contentType");
//						log.info("XML was ${xml} " + xml.text());
//	
//						//def annotations = new XmlParser().parse(xml);
//						def annotations = xml.childNodes();
//						annotations.each { annotation ->
//							NifAnnotationItem item = new NifAnnotationItem();
//							//println "annotation " + annotation.text();
//							item.match = annotation.text();
//							def attributes = annotation.attributes();
//							//println attributes.get("start");
//	
//							item.start = new Integer(attributes.get("start"));
//							//println attributes.get("end");
//							item.end = new Integer(attributes.get("end"));
//							def entity = annotation.childNodes().next();
//							//println entity.text();
//	
//							def entityAttributes = entity.attributes();
//							//println entityAttributes.get("category");
//							item.category = entityAttributes.get("category");
//							//println entityAttributes.get("id");
//							item.id =  entityAttributes.get("id");
//							items.add(item);
//						}
//						return items;
//					}
					
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
//				return items
	
			} catch (groovyx.net.http.HttpResponseException ex) {
				log.error("HttpResponseException: " + ex.getMessage())
				throw new RuntimeException(ex);
			} catch (java.net.ConnectException ex) {
				log.error("ConnectException: " + ex.getMessage())
				throw new RuntimeException(ex);
			}
			
			return new JSONObject();
		}
		
	public JSONObject search(String apikey, String query, def ontologies, def pageNumber, def pageSize) {  
        
        String ontos = parseOntologiesIds(ontologies);
          
		String uri = 'http://data.bioontology.org/search' + 
			APIKEY + apikey + 
			QUERY + URLEncoder.encode(query, MiscUtils.DEFAULT_ENCODING)  +
			ONTOLOGIES + ((!ontos.isEmpty())?(ONTOLOGIES + ontos):'') +
			PAGESIZE + pageSize +
			PAGE + pageNumber;
			
        log.info("Search term with URI: " + uri);
        if(domeoConfigAccessService.isProxyDefined()) {
            log.info("proxy: " + domeoConfigAccessService.getProxyIp() + "-" + domeoConfigAccessService.getProxyPort());
        } else {
             log.info("NO PROXY selected while accessing " + uri);
        }
				
		JSONObject jsonResponse = new JSONObject();
		try {
			//ArrayList<NifAnnotationItem> items = new ArrayList<NifAnnotationItem>();
			def http = new HTTPBuilder(uri)
			
			int TENSECONDS = 10*1000;
			int THIRTYSECONDS = 30*1000;
			
			http.getClient().getParams().setParameter("http.connection.timeout", new Integer(TENSECONDS))
			http.getClient().getParams().setParameter("http.socket.timeout", new Integer(THIRTYSECONDS))
			
			http.encoderRegistry = new EncoderRegistry(charset: MiscUtils.DEFAULT_ENCODING)
			if(domeoConfigAccessService.isProxyDefined()) {
				http.client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, domeoConfigAccessService.getProxyHttpHost());
			}
		
			// perform a POST request, expecting TEXT response
			http.request(Method.GET, ContentType.JSON) {
				requestContentType = ContentType.URLENC
				
				response.success = { resp, json ->
					
// http://rest.bioontology.org/bioportal/search/?query=Gene&isexactmatch=1&apikey=fef6b9da-4b3b-46d2-9d83-9a1a718f6a22
// http://data.bioontology.org/search?q=melanoma (page of 50 items)
// http://data.bioontology.org/search?q=melanoma&page=2&pagesize=5 (page of 5 items)					
					
					if(true) {
						// Old implementation
						jsonResponse.put("pagesize", pageSize);
						jsonResponse.put("pagenumber", json.page);
						jsonResponse.put("totalpages", json.pageCount);
						
						JSONArray elements = new JSONArray();
						json.collection.each {  // iterate over JSON 'status' object in the response:
							JSONObject element = new JSONObject();
							element.put("termUri", it['@id']);
							element.put("termLabel", it.prefLabel);
							if(it.definition!=null) element.put("description", it.definition[0]);
							element.put("sourceUri", it.links.ontology);
							//element.put("description", it.links.ontology);
							//element.put("IAO:IAO_0000115", it.definition);
							
							elements.add(element);
						}
						jsonResponse.put("terms", elements);
						
					} else {
						jsonResponse.put("@type", "co:Set");
					
						// New implementation
						println 'Page: ' + json.page
						println 'Page count: ' + json.pageCount
						println 'Previous page: ' + json.prevPage
						println 'Next page: ' + json.nextPage
						
						JSONArray elements = new JSONArray();
						json.collection.each {  // iterate over JSON 'status' object in the response:
							JSONObject element = new JSONObject();
							element.put("@id", it['@id']);
							element.put("rdfs:label", it.prefLabel);
							element.put("dc:description", it.definition);
							//element.put("IAO:IAO_0000115", it.definition);
							
							JSONObject source = new JSONObject();
							source.put("@id", it.links.ontology);
							element.put("rdf:isDefinedBy", source);
							elements.add(element);
						}
						jsonResponse.put("co:element", elements);
					}
					
				}
				
				response.'404' = { resp ->
					log.error('Not found: ' + resp.getStatusLine())
					throw new ConnectorHttpResponseException(resp, 404, 'Service not found. The problem has been reported')
				}
			 
				response.'503' = { resp ->
					log.error('Not available: ' + resp.getStatusLine())
					throw new ConnectorHttpResponseException(resp, 503, 'Service temporarily not available. Try again later.')
				}
				
				response.'401' = { resp ->
					log.error('UNAUTHORIZED access to URI: ' + uri)
					throw new ConnectorHttpResponseException(resp, 401, 'Unauthorized access ot the service.')
				}
				
				response.'400' = { resp ->
					log.error('BAD REQUEST: ' + uri)
					throw new ConnectorHttpResponseException(resp, 401, 'Unauthorized access ot the service.')
				}
				
				response.failure = { resp, json ->
					log.error('failure: ' + resp.getStatusLine())
				}
			}
		} catch (groovyx.net.http.HttpResponseException ex) {
			log.error("HttpResponseException: " + ex.getMessage())
			throw new RuntimeException(ex);
		}  catch (java.net.SocketTimeoutException ex) {
			log.error("SocketTimeoutException: " + ex.getMessage())
			throw new RuntimeException(ex);
		} catch (java.net.ConnectException ex) {
			log.error("ConnectException: " + ex.getMessage())
			throw new RuntimeException(ex);
		}
		
		return jsonResponse;
	}
	
	public JSONObject annotate(String url, String apikey, String[] ontologies, String text, def parametrization) { // throws AnnotatorException {
		BioPortalAnnotatorRequestParameters params = defaultParams();
		params.apikey = apikey
		params.text = URLEncoder.encode(text, MiscUtils.DEFAULT_ENCODING);
		params.ontologies = ontologies
		if(parametrization.getAt("minimum_match_length")!=null) 
			params.minimum_match_length = new Integer(parametrization.getAt("minimum_match_length"));
		if(parametrization.getAt("max_level")!=null)
			params.max_level = new Integer(parametrization.getAt("max_level"));

		String ontos = parseOntologiesIds(ontologies);
		String uri = 'http://data.bioontology.org/annotator' + params.toParameterString();
		  
		log.info("Annotate with URI: " + uri);
		if(domeoConfigAccessService.isProxyDefined()) {
			log.info("proxy: " + domeoConfigAccessService.getProxyIp() + "-" + domeoConfigAccessService.getProxyPort());
		} else {
		   	log.info("NO PROXY selected while accessing " + uri);
		}
			  
	  	JSONObject jsonResponse = new JSONObject();
	  	try {
			  def http = new HTTPBuilder(uri)
			  
			  int TENSECONDS = 10*1000;
			  int THIRTYSECONDS = 30*1000;
			  
			  http.getClient().getParams().setParameter("http.connection.timeout", new Integer(TENSECONDS))
			  http.getClient().getParams().setParameter("http.socket.timeout", new Integer(THIRTYSECONDS))
			  
			  http.encoderRegistry = new EncoderRegistry(charset: MiscUtils.DEFAULT_ENCODING)
			  if(domeoConfigAccessService.isProxyDefined()) {
				  http.client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, domeoConfigAccessService.getProxyHttpHost());
			  }
		  
			 
			  
			  // perform a POST request, expecting TEXT response
			  http.request(Method.GET, ContentType.JSON) {
				  requestContentType = ContentType.URLENC
				  
				  response.success = { resp, json ->
					  if(true) {
						  println json.size();
						  
						  json.eachWithIndex { annotation, i ->
							  println i + "- " + annotation.annotations
							  def annotations = annotation.annotations;
							  
							  println i + "- " + annotation.annotatedClass["@id"]
							  def conceptId = annotation.annotatedClass["@id"]
							  println i + "- " + annotation.annotatedClass.links.ontology
							  def ontologyId = annotation.annotatedClass.links.ontology
							  
							  annotations.each{ ann ->
								  println conceptId + " - " + ontologyId + " - " + ann;
							  }
							  
						  }
					  } else {
					  
					  }
				  }
			 
				  response.'404' = { resp ->
					  log.error('Not found: ' + resp.getStatusLine() + ' ' + resp.entity.content.text)
					  throw new ConnectorHttpResponseException(resp, 404, 'Service not found. The problem has been reported')
				  }
			   
				  response.'503' = { resp ->
					  log.error('Not available: ' + resp.getStatusLine())
					  throw new ConnectorHttpResponseException(resp, 503, 'Service temporarily not available. Try again later.')
				  }
				  
				  response.'401' = { resp ->
					  log.error('UNAUTHORIZED access to URI: ' + uri)
					  throw new ConnectorHttpResponseException(resp, 401, 'Unauthorized access ot the service.')
				  }
				  
				  response.'400' = { resp ->
					  log.error('BAD REQUEST: ' + uri)
					  throw new ConnectorHttpResponseException(resp, 401, 'Unauthorized access ot the service.')
				  }
			  
				  response.failure = { resp, json ->
					  log.error('failure: ' + resp.getStatusLine())
				  }
			  }
	  	} catch (groovyx.net.http.HttpResponseException ex) {
		  	log.error("HttpResponseException: [" + ex.getStatusCode() + "] " + ex.getMessage())
		  	throw new RuntimeException(ex);
	  	} catch (java.net.SocketTimeoutException ex) {
		  	log.error("SocketTimeoutException: " + ex.getMessage())
		  	throw new RuntimeException(ex);
	  	} catch (java.net.ConnectException ex) {
		  	log.error("ConnectException: " + ex.getMessage())
		  	throw new RuntimeException(ex);
	  	}
		return new JSONObject();
	}
	
	BioPortalAnnotatorRequestParameters defaultParams(){
		BioPortalAnnotatorRequestParameters params = new BioPortalAnnotatorRequestParameters()
		params.mappingTypes = ['Manual'] as Set
		params
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
