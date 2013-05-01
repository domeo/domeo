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
package org.mindinformatics.services.connector.nif.linkout

import grails.converters.JSON

import org.codehaus.groovy.grails.web.json.JSONObject;

/**
* @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
*/
class NifLinkOutJsonService {
    static transactional = false;
	
	private static String PREFIX =
		"http://disco.neuinfo.org/webportal/WebServices/REST/DISCOInfo/getResourceLinkOutDataJson/";

	def nifLinkOutResultsConversionService;
	
	public JSONObject linkOut(String pmid, String url) {
		
		ArrayList<NifLinkOutItem> items = callService(pmid);
		return nifLinkOutResultsConversionService.convert(url, items);
	}
	
	private ArrayList<NifLinkOutItem> callService(String pmid) {
		
		// TODO PROXY!!!!!!!!!!!!
		
		
		String uri = PREFIX + pmid;
		URL oracle = new URL(uri);
		BufferedReader input = new BufferedReader(
		new InputStreamReader(
		oracle.openStream()));
		StringBuffer inputText= new StringBuffer();
		String inputLine;
		while ((inputLine = input.readLine()) != null) inputText.append(inputLine);
		input.close();
		
		def jsonArray = JSON.parse(inputText.toString());
		jsonArray.each { println "Value: ${it}" }
		
		ArrayList<NifLinkOutItem> items = new  ArrayList<NifLinkOutItem>();
		println jsonArray.getJSONArray("link_category").each {
			NifLinkOutItem item = new NifLinkOutItem();
			
			println extractLinkCategoryName(item, it)
			println extractResource(item, it)
			println extractLink(item, it)
			
			items.add(item);
		}
		return items;
	}
	
	private String extractLinkCategoryName(NifLinkOutItem term, JSONObject object) {
		term.category = object.get("@name");
		"extractLinkCategoryName> " + object.get("@name")
	}
	
	private String extractResource(NifLinkOutItem term, JSONObject object) {
		term.sourceId = "http://www.neuinfo.org/nif/nifgwt.html?query=" + object.get("resource").get("@id");
		term.sourceLabel = object.get("resource").get("@name");
		"extractResource> " + term.sourceId + " " + term.sourceLabel
	}
	
	private String extractLink(NifLinkOutItem term, JSONObject object) {
		term.label = object.get("resource").get("link").get("@name");
		term.uri = object.get("resource").get("link").get("@url");
		term.id = object.get("resource").get("link").get("@id");
		"extractLink> " + term.label + " " + term.uri
	}
}
