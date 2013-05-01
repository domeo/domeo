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
package org.mindinformatics.services.connector.bioportal.terms;

import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.codehaus.groovy.grails.web.json.JSONArray;
import org.codehaus.groovy.grails.web.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
* @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
*/
public class ParseXMLFile {
    
    private static Logger log = Logger.getLogger(ParseXMLFile.class.getName());
    
	/**
	 * @param uri RESTful Search URI for BioPortal web services
	 */
	public static JSONObject parseXMLFile(String uri, Proxy proxy){
		
		JSONObject results = new JSONObject();
		results.put("requesturi", uri);
		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

			Document doc = null;
			if(proxy!=null) {
				HttpURLConnection connectionWithProxy = (HttpURLConnection) new URL(uri).openConnection(proxy);
				connectionWithProxy.setRequestProperty("Content-type", "text/xml");
				connectionWithProxy.setRequestProperty("Accept", "text/xml, application/xml");
				connectionWithProxy.setRequestMethod("GET");
				connectionWithProxy.connect();
				doc = docBuilder.parse(connectionWithProxy.getInputStream());
			} else {
				doc = docBuilder.parse(uri);
			}

			//Normalize text representation
			doc.getDocumentElement().normalize();
			
			NodeList pageNumberResults = doc.getElementsByTagName("pageNum");
			System.out.println("Search pageNum: "+ pageNumberResults.getLength());
			if(pageNumberResults.getLength()>0) {
				Node  pageNumberNode = pageNumberResults.item(0);
				if(pageNumberNode.getNodeType()==1) 
					results.put("pagenumber", pageNumberNode.getTextContent().trim());
			}
			
			NodeList numPagesResults = doc.getElementsByTagName("numPages");
			if(numPagesResults.getLength()>0) {
				Node  numPagesNode = numPagesResults.item(0);
				if(numPagesNode.getNodeType()==1) 
					results.put("totalpages", numPagesNode.getTextContent().trim());
			}
			
			NodeList pageSizeResults = doc.getElementsByTagName("pageSize");
			if(pageSizeResults.getLength()>0) {
				Node  pageSizeNode = pageSizeResults.item(0);
				if(pageSizeNode.getNodeType()==1) 
					results.put("pagesize", pageSizeNode.getTextContent().trim());
			}
			
			/**
			 * NOTE: The values for "getElementsByTagName may need to be changed depending on the web service used 
			 */
			NodeList listOfSearchResults = doc.getElementsByTagName("searchBean");
			int totalSearchResults = listOfSearchResults.getLength(); //total search results also available as XML value
			System.out.println("Search uri: "+ uri);
			System.out.println("Total Results: "+ totalSearchResults);
			
			JSONArray terms = new JSONArray();

			//String TAB = "\t";
			if (totalSearchResults == 0) {
				System.out.print("NO SEARCH RESULTS: "+uri+"\n"); 
			} else {		
				for(int s=0; s<listOfSearchResults.getLength(); s++) {
					Node firstSearchNode = listOfSearchResults.item(s);

					JSONObject term = new JSONObject();
					if(firstSearchNode.getNodeType() == Node.ELEMENT_NODE){
						Element firstSearchElement = (Element)firstSearchNode;                    
	
						//-------
						//NodeList ontologyVersionId = firstSearchElement.getElementsByTagName("ontologyVersionId");
						//Element ontologyVersionIdElement = (Element)ontologyVersionId.item(0);	
						//NodeList textOVIDList = ontologyVersionIdElement.getChildNodes();					
						//System.out.print(((Node)textOVIDList.item(0)).getNodeValue().trim()+TAB);

						//-------	
						NodeList ontologyIdList = firstSearchElement.getElementsByTagName("ontologyId");
						Element ontologyIdElement = (Element)ontologyIdList.item(0);
						NodeList textOIDList = ontologyIdElement.getChildNodes();
						String bioPortalOntologyId = (((Node)textOIDList.item(0)).getNodeValue().trim());
						term.put("sourceUri", "http://bioportal.bioontology.org/ontologies/"+bioPortalOntologyId);

						//-------
						NodeList ontologyDisplayLabelList = firstSearchElement.getElementsByTagName("ontologyDisplayLabel");
						Element ontologyDisplayLabelElement = (Element)ontologyDisplayLabelList.item(0);
						NodeList ontologyDLList = ontologyDisplayLabelElement.getChildNodes();
						term.put("sourceLabel", ((Node)ontologyDLList.item(0)).getNodeValue().trim());
						//System.out.print(((Node)ontologyDLList.item(0)).getNodeValue().trim()+TAB);

						//----
						//NodeList conceptIdShortList = firstSearchElement.getElementsByTagName("conceptIdShort");
						//Element conceptIdShortElement = (Element)conceptIdShortList.item(0);
						//NodeList conceptIDShortList = conceptIdShortElement.getChildNodes();
						//String conceptIdentifier = (((Node)conceptIDShortList.item(0)).getNodeValue().trim()+TAB);
						//System.out.print(conceptIdentifier);
						
						//------
						NodeList conceptIdList = firstSearchElement.getElementsByTagName("conceptId");
						Element conceptIdElement = (Element)conceptIdList.item(0);
						NodeList textconceptIdList = conceptIdElement.getChildNodes();
						term.put("termUri", ((Node)textconceptIdList.item(0)).getNodeValue().trim());
						//System.out.print(((Node)textconceptIdList.item(0)).getNodeValue().trim()+TAB);
						
						//------
						NodeList preferredNameList = firstSearchElement.getElementsByTagName("preferredName");
						Element preferredNameElement = (Element)preferredNameList.item(0);
						NodeList textPreferredNameList = preferredNameElement.getChildNodes();
						term.put("termLabel", ((Node)textPreferredNameList.item(0)).getNodeValue().trim());
						//System.out.print(((Node)textPreferredNameList.item(0)).getNodeValue().trim()+TAB);
						
						NodeList contentsList = firstSearchElement.getElementsByTagName("contents");
						Element contentsElement = (Element)contentsList.item(0);
						NodeList textContentsList = contentsElement.getChildNodes();
						term.put("description", ((Node)textContentsList.item(0)).getNodeValue().trim());
					}
					terms.add(term);
				}
				results.put("terms", terms);
			}
			return results;
		}catch (SAXParseException e) {
		    log.error("SAXParseException: " + e.getMessage());
			throw new RuntimeException(e);
		}catch (SAXException e) {
		    log.error("SAXException: " + e.getMessage());
            throw new RuntimeException(e);
		}catch (Exception e) {
		    log.error("Exception: " + e.getMessage());
            throw new RuntimeException(e);
		}
	}
}
