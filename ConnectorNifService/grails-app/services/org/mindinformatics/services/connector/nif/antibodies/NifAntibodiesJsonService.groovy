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
package org.mindinformatics.services.connector.nif.antibodies

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
import org.mindinformatics.services.connector.nif.data.NifAntibodyItem;
import org.mindinformatics.services.connector.nif.data.NifDataRequestParameters;

/**
 * @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
 * 
 * Service for retrieval of NIF antibodies entities.
 * 
 * Example for Antibodies search: 
 * http://neuinfo.org/servicesv1/v1/federation/data/nif-0000-07730-1?q=APP
 */
class NifAntibodiesJsonService {
    static transactional = false;

    static final String SERVICE_URL = "http://neuinfo.org/servicesv1/v1/federation/data/";
    
    def grailsApplication;
    def domeoConfigAccessService;
    def nifAntibodiesResultsConversionService;
    
    /**
     * Search for antibodyregistry.org antibodies.
     * @param resource  The antibody resource
     * @param query     The search query
     * @param vendor    The antibody vendor
     * @param type      The type (Name, Catalog Number or Clone Number)
     * @return The JSON results
     */
	public JSONObject antibodies(String resource, String query, String vendor, String type) {
        log.info("Searching " + resource + "," + vendor + "," + type + " for: " + query)
        def url = composeUrl(resource, query, type, vendor);
        return nifAntibodiesResultsConversionService.convert(url, callAntibodiesService(url), new NifDataRequestParameters());
    }
    
    /**
     * Definition of the search URL.
     * @param resource  The antibody resource
     * @param query     The search query
     * @param vendor    The antibody vendor
     * @param type      The type (Name, Catalog Number or Clone Number)
     * @return The URL for search
     */
    private String composeUrl(String resource, String  query, String  type, String vendor) {
        if(type=="catalog") {
            return SERVICE_URL + resource + '?q=*' + 
                ((query!=null&&query.trim().length()>0)?'&filter=Cat%20Num:' + URLEncoder.encode(query, MiscUtils.DEFAULT_ENCODING):'') + 
                ((vendor!=null&&vendor.trim().length()>0)?'&filter=Vendor:' + URLEncoder.encode(vendor, MiscUtils.DEFAULT_ENCODING):'');
        } else if(type=="clone") {
            return SERVICE_URL + resource + '?q=*' + 
                ((query!=null&&query.trim().length()>0)?'&filter=Clone%20ID:' + URLEncoder.encode(query, MiscUtils.DEFAULT_ENCODING):'') + 
                ((vendor!=null&&vendor.trim().length()>0)?'&filter=Vendor:' + URLEncoder.encode(vendor, MiscUtils.DEFAULT_ENCODING):'');
        }
        
        // TODO manage filters
        return SERVICE_URL + resource + '?q=' + 
            ((query!=null&&query.trim().length()>0)?URLEncoder.encode(query,MiscUtils.DEFAULT_ENCODING):'*') + (
                (vendor!=null&&vendor.trim().length()>0)?'&filter=Vendor:' + URLEncoder.encode(vendor, MiscUtils.DEFAULT_ENCODING):'');
    }
    
    /**
     * The actual call to the antibodies service.
     * @param url   The URL for the search
     * @return The list of Antibody items
     */
    private ArrayList<NifAntibodyItem> callAntibodiesService(String url) {
        log.info("Searching antibodies with URL: " + url);
        try {
            ArrayList<NifAntibodyItem> items = new ArrayList<NifAntibodyItem>();
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
                                        rowElements.each { rowElement ->
                                            def valuesMap = [:];
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
                                                if(name=="Antibody ID") {
                                                    int hrefStartIndex = value.indexOf('href="');
                                                    int hrefEndIndex = value.indexOf('">', hrefStartIndex);
                                                    int endIdIndex = value.indexOf('</a', hrefEndIndex);
                                                    valuesMap.put("Antibody URL", value.substring(hrefStartIndex+6, hrefEndIndex));
                                                    valuesMap.put(name, value.substring(hrefEndIndex+2, endIdIndex));
                                                } else {
                                                    valuesMap.put(name, value);
                                                }
                                            }
                                            
                                            if(valuesMap.get("Antibody URL")==null)
                                                log.warn("**************** Antibody URL NOT FOUND *********************");
                                            
                                            NifAntibodyItem item = new NifAntibodyItem();
                                            item.antibodyId = valuesMap.get("Antibody ID");
                                            item.antibodyUrl = valuesMap.get("Antibody URL");
                                            item.name = valuesMap.get("Antibody Name");
                                            item.target = valuesMap.get("Antibody Target");
                                            item.clonality = valuesMap.get("Clonality:Monoclonal");
                                            item.cloneId = valuesMap.get("Clone ID");
                                            item.vendor = valuesMap.get("Vendor");
                                            item.catalog = valuesMap.get("Cat Num");
                                            item.sourceOrganism = valuesMap.get("Source Organism");
                                            
                                            counter++;
                                            log.info("Antibody URL: " + valuesMap.get("Antibody URL"));
                                            //println "total name/value pairs: " + valuesMap;
                                            items.add(item);
                                            /*
                                             [
                                                CatNum: H00008028-M02,
                                                AntibodyURL: http://antibodyregistry.org/Antibody12/antibodyform.html?gui_type=advanced&ab_id=714777,
                                                Antibody ID:AB_714777,
                                                Antibody Name:Mouse Anti-MLLT10 Monoclonal Antibody, Unconjugated, Clone 2B9,
                                                Antibody Target:<a class="external" target="_blank" href="http: //www.ncbi.nlm.nih.gov/gene?term="> MLLT10  </a>,
                                                Vendor:Abnova Corporation,
                                                Clonality:Monoclonal Antibody; Monoclonal Antibody,
                                                Clone ID:Clone 2B9,
                                                Source Organism: mouse,
                                                Comments:manufacturer recommendations: ELISA; Western Blot; ELISA,S-ELISA, Western Blotting-Ce,
                                                People Using this AB:<a class="external" target="_blank" href="http: //www.ncbi.nlm.nih.gov/pubmed?term="></a>]
                                            ]
                                             */
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
