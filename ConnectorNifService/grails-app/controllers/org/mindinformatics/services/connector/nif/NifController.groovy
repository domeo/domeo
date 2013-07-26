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
package org.mindinformatics.services.connector.nif

import org.codehaus.groovy.grails.web.json.JSONObject
import org.mindinformatics.domeo.grails.plugins.utils.ConnectorHttpResponseException
import org.mindinformatics.domeo.grails.plugins.utils.MiscUtils
import org.mindinformatics.services.connector.nif.data.NifDataSources


/**
 * @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
 * 
 * The component has been written as part of a collaboration with
 * the Neuroscience Information Framework (NIF http://www.neuinfo.org) 
 * 
 * It currently manages the connection with three different components:
 * 1) NIF Annotator
 * It annotates arbitrary text with terms/entities from the NIF ontologies
 * Instructions: http://nif-services.neuinfo.org/servicesv1/resource_AnnotateService.html
 * Example: http://nif-services.neuinfo.org/servicesv1/v1/annotate/entities?content=This+is+a+sentence+about+the+cerebellum+and+hippocampus
 * 
 * The service is able to excludeCat or includeCat so if you really only want to annotate anatomical regions use this:
 * http://nif-services.neuinfo.org/servicesv1/v1/annotate?content=hippocampus&longestOnly=true&includeCat=anatomical_structure
 *
 * The list of categories that you may want to omit: &excludeCat=gene, &excludeCat=quality, &excludeCat=resource, You can add as many excludeCats as you like
 * The list of categories that you may want to search: &includeCat=anatomical_structure, &includeCat=cell, &includeCat=biological_process, &includeCat=disease 
 * The categories will appear as annotations like this <span class="nifAnnotation" data-nif="Cerebellum,birnlex_1489,anatomical_structure">cerebellum</span>
 * 
 * 2) NIF Search
 * 
 * 3) NIF LinkOut
 * Example: http://localhost:3333/Domeo/nif/linkout?pmid=11731556&url=http://www.google.com
 */
class NifController {
    
    def domeoConfigAccessService;   // To access configurations
    def mailingService;             // Mailing service used for notifications of failure              
    
    def nifEntitiesJsonService;     // Queries the NIF for entities and resources
    def nifAntibodiesJsonService;     // Queries the NIF for entities and resources
	def nifAnnotatorJsonService;    // Runs the NIF Annotator 
	def nifLinkOutJsonService;      // Queries the NIF LinkOut service
    

	def linkout = {
		/*
		 * List of parameters
		 * 1) The PubMed id (Mandatory)
		 * 2) The URL of the resource where the content has been taken from (Mandatory)
		 */
		String pmid = params.pmid;
		String url = params.url;
        
		JSONObject jsonResult = nifLinkOutJsonService.linkOut(pmid, url);
		render(contentType:'text/json', encoding:MiscUtils.DEFAULT_ENCODING, text: jsonResult.toString());
	}
	
    def data = {
        /*
		* List of parameters
		* 1) The resource id within NIF (Mandatory) (Antibodies: nif-0000-07730-1)
		* 2) The filters for searching (optional)
		*/
		String resource = params.resource;
		String query = params.query;
		String vendor = params.vendor;
		String type = params.type;
		
		try {
            if(resource.equals(NifDataSources.NIF_REGISTRY.identifier()) || resource.equals(NifDataSources.NIF_INTEGRATED_ANIMAL.identifier())) {
                JSONObject jsonResult = nifEntitiesJsonService.entities(resource, query);
                render(contentType:'text/json', encoding:MiscUtils.DEFAULT_ENCODING,  text: jsonResult.toString());
            } else if(resource.equals(NifDataSources.NIF_ANTIBODIES.identifier())) {
                JSONObject jsonResult = nifAntibodiesJsonService.antibodies(resource, query, vendor, type);
                render(contentType:'text/json', encoding:MiscUtils.DEFAULT_ENCODING,  text: jsonResult.toString());
            } else {
                render(status: "405", text: "NIF Data Search does not support: " + resource);
            }
		} catch (ConnectorHttpResponseException e) {
			mailingService.notifyProblemByEmail("NIF Data" + "[resource:"+ resource + ", query:"+ query + 
                ", vendor:"+ vendor + ", type:"+ type + "] ", e.getMessage());
			render(status: e.getCode(), text: "NIF Data Search: " + e.getMessage());
		} catch(Exception e) {
            mailingService.notifyProblemByEmail("NIF Data" + "[resource:"+ resource + ", query:"+ query + 
                ", vendor:"+ vendor + ", type:"+ type + "] ", e.getMessage());
            render(status: "500", text: "NIF Data Search: " + e.getMessage());
		}
	}
	
	def annotate = {
		/*
		 * List of parameters
		 * 1) The textual content to be sent to the NIF annotator (Mandatory)
		 * 2) The URL of the resource where the content has been taken from (Mandatory)
		 * 3) The list of ontologies to be included (optional)
		 * 4) The list of ontologies to be excluded (optional)
		 */
		String content = params.content;
		String url = params.url;
		String apikey = params.apikey;
		String longestOnly = params.longestOnly;
		String includeAbbrev = params.includeAbbrev;
		String includeAcronym = params.includeAcronym;
		String includeNumbers = params.includeNumbers;
		String ontologiesIn = params.ontologiesIn;
		String ontologiesOut = params.ontologiesOut
		
		try {
			JSONObject jsonResult = nifAnnotatorJsonService.annotate(url, content, ontologiesIn, ontologiesOut, longestOnly,
				includeAbbrev, includeAcronym, includeNumbers);
			render(contentType:'text/json', encoding:MiscUtils.DEFAULT_ENCODING,  text: jsonResult.toString());
		} catch (ConnectorHttpResponseException e) {
			mailingService.notifyProblemByEmail("NIF Annotator", e.getMessage());
			render(status: e.getCode(), text: "NIF Annotator: " + e.getMessage());
		} catch(Exception e) {
            mailingService.notifyProblemByEmail("NIF Annotator", e.getMessage());
            render(status: "500", text: "NIF Data Search: " + e.getMessage());
        }
	}
}
