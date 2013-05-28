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
package org.mindinformatics.services.connector.nif.data

import java.util.ArrayList;

import groovyx.net.http.ContentType
import groovyx.net.http.EncoderRegistry
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method

import org.apache.http.conn.params.ConnRoutePNames
import org.codehaus.groovy.grails.web.json.JSONObject;

import org.codehaus.groovy.grails.web.json.JSONObject
import org.mindinformatics.domeo.grails.plugins.utils.ConnectorHttpResponseException
import org.mindinformatics.domeo.grails.plugins.utils.MiscUtils


/**
 * @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
 * 
 * Service for retrieval of NIF entities.
 * 
 * Example for Antibodies search: 
 * http://neuinfo.org/servicesv1/v1/federation/data/nif-0000-07730-1?q=APP
 */
class NifEntitiesJsonService {
    static transactional = false;

    static final String SERVICE_URL = "http://neuinfo.org/servicesv1/v1/federation/data/";
    
    def grailsApplication;
    def domeoConfigAccessService;
    def nifRegistryResultsConversionService;
    
    /**
     * Searching for entities
     * @param resource  The resource to search   
     * @param query     The search query
     * @return The JSON results
     */
    public JSONObject entities(String resource, String query) {
        log.info("Searching " + resource + " for: " + query)
        def url = composeUrl(resource, query);
        log.info("Searching url: " + url);
        return nifRegistryResultsConversionService.convert(url, callRegistryService(url), new NifDataRequestParameters());
    }
    
    /**
     * Definition of the URL for performing the query
     * @param resource  The resource to search   
     * @param query     The search query
     * @return The search URL
     */
    private String composeUrl(String resource, String  query) {
        return SERVICE_URL + resource + '?q=' + 
            ((query!=null&&query.trim().length()>0)?URLEncoder.encode(query, MiscUtils.DEFAULT_ENCODING):'*');
    }
    
    private ArrayList<NifResourceItem> callRegistryService(String url) {
        try {
            ArrayList<NifResourceItem> items = new ArrayList<NifResourceItem>();
            def http = new HTTPBuilder(url)
            http.encoderRegistry = new EncoderRegistry(charset: MiscUtils.DEFAULT_ENCODING) // was utf-8
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
                    def dataItems = xml.childNodes();
                    dataItems.each { dataItem ->
                        // println 'namespace1: ' + dataItem.name(); -> query
                        if(dataItem.name()=="result") {
                            def dataItemAttributes = dataItem.attributes();
                            int totalCount = new Integer(dataItemAttributes.get("resultCount"));
                            log.info('total results: ' + totalCount);
                            if(totalCount>0) {
                                def result = dataItem.childNodes();
                                result.each { resultsElement ->
                                    if(resultsElement.name()=="results") {
                                        int counter = 0;
                                        def rowElements = resultsElement.childNodes();
                                        //println 'page results: ' + rowElements.size();
                                        rowElements.each { rowElement ->
                                            def valuesMap = [:];
											
											def code = 0;
											
                                            // println counter + ' items: ' + rowElement.name(); -> row
                                            // Creating the map
                                            def dataElements = rowElement.childNodes();
                                            dataElements.each { dataElement ->
                                                def name
                                                def value
                                                def nameValueElements = dataElement.childNodes();
                                                nameValueElements.each { nameValueElement ->
                                                    if(nameValueElement.name()=="name") name = nameValueElement.text();
                                                    if(nameValueElement.name()=="value") value = nameValueElement.text();
                                                }
												
                                                if(name=="Resource Name" || name=="Database/Catalog id") {
													if(name=="Database/Catalog id") code = 1; 
                                                    int hrefStartIndex = value.indexOf('href="');
                                                    int hrefEndIndex = value.indexOf('">', hrefStartIndex);
                                                    int endIdIndex = value.indexOf('</a', hrefEndIndex);
                                                    valuesMap.put("Resource URL", value.substring(hrefStartIndex+6, hrefEndIndex));
                                                    valuesMap.put("Resource Name", value.substring(hrefEndIndex+2, endIdIndex));
                                                } else {
                                                    valuesMap.put(name, value);
                                                }
                                            }
											
											// http://neuinfo.org/servicesv1/v1/federation/data/nif-0000-08137-1?q=mouse
                                            
                                            if(valuesMap.get("Resource URL")==null)
                                                log.warn("**************** Resource URL NOT FOUND *********************");
                                            
                                            NifResourceItem item = new NifResourceItem();
                                            item.id = valuesMap.get("Resource URL");
                                            item.url = valuesMap.get("Resource URL");
											
											if(code==1) item.name = valuesMap.get("Name");
											else item.name = valuesMap.get("Resource Name");
											
											if(code==1) {
												item.description = valuesMap.get("Name") + "; " + valuesMap.get("Gene Symbol") + "; " + valuesMap.get("Genomic Alteration");
											} else item.description = valuesMap.get("Description");
										
											log.warn("Description " + valuesMap.get("Description"));
                                            counter++;
                                            log.info("Resource URL: " + valuesMap.get("Resource URL"));

                                            items.add(item);
                                        }
                                    }
                                }
                            }
                        }
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
                    throw new ConnectorHttpResponseException(resp, resp.getStatusLine(), 'Failure')
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
