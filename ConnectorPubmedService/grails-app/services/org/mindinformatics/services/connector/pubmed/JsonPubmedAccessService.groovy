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
package org.mindinformatics.services.connector.pubmed



import java.util.List;
import java.util.Map;

import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.json.JSONObject
import org.json.simple.JSONValue
import org.mindinformatics.services.connector.pubmed.dataaccess.ExternalPubmedArticle
import org.mindinformatics.services.connector.pubmed.dataaccess.IPubmedArticleManager
import org.mindinformatics.services.connector.pubmed.dataaccess.PubmedArticleManagerImpl
import org.mindinformatics.services.connector.pubmed.dataaccess.ExternalPubmedArticle.ExternalAuthor
import org.mindinformatics.services.connector.pubmed.fetch.PublicationType

/**
* @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
*/
class JsonPubmedAccessService {

	static transactional = true;
	
	def grailsApplication;
	
	final def UNRECOGNIZED = "UNRECOGNIZED";
	
	public static final int DEFAULT_START_MONTH = 1;
	public static final int DEFAULT_START_YEAR = 1900;
	public static final int DEFAULT_END_MONTH = 12;
	public static final int DEFAULT_END_YEAR = Calendar.getInstance().get(Calendar.YEAR);
	public static final int DEFAULT_MAX_RESULTS = 90;
	public static final int DEFAULT_OFFSET = 0;
	
	// grailsApplication.config.domeo.proxy.ip!=null && grailsApplication.config.domeo.proxy.port
	 	
	/**
	 * Return the json representation of the metadata record of a PubMed
	 * article through a PubMed identifier.
	 * @param pmid	The PubMed identifier
	 * @return The json metadata record of a PubMed article entry
	 */
	public JSONObject getPubmedArticle(String pmid) {
		log.info("proxy: " + grailsApplication.config.domeo.proxy.ip + "-" + grailsApplication.config.domeo.proxy.port) ;
		IPubmedArticleManager pa = new PubmedArticleManagerImpl((grailsApplication.config.domeo.proxy.ip.isEmpty()?"":grailsApplication.config.domeo.proxy.ip), 
			(grailsApplication.config.domeo.proxy.port.isEmpty()?"":grailsApplication.config.domeo.proxy.port));
		
		ExternalPubmedArticle xpa = pa.getPubmedArticle(pmid);
		return convertExternalPubmedArticle(xpa);
	}
	
	/**
	 * Returns the json representations of the metadata records of PubMed
	 * articles through PubMed identifiers. It returns a 'UNRECOGNIZED' string
	 * when no entry is found.
	 * @param pmids The PubMed identifiers
	 * @return The json metadata record of the PubMed article entries
	 */
	public JSONArray getPubmedArticles(List<String> pmids) {
		log.info("proxy: " + grailsApplication.config.domeo.proxy.ip + "-" + grailsApplication.config.domeo.proxy.port) ;
		IPubmedArticleManager pa = new PubmedArticleManagerImpl((grailsApplication.config.domeo.proxy.ip.isEmpty()?"":grailsApplication.config.domeo.proxy.ip), 
			(grailsApplication.config.domeo.proxy.port.isEmpty()?"":grailsApplication.config.domeo.proxy.port));
        JSONArray pas = new JSONArray();
        
        try {
    		List<ExternalPubmedArticle> epas = pa.getPubmedArticles(pmids);
    		Iterator<ExternalPubmedArticle> references = epas.iterator();
    		
    		for(String pmid: pmids) {
    			if(pmid.equals(UNRECOGNIZED)) {
    				pas.add(UNRECOGNIZED);
    			} else {
    				if(references.hasNext()) {
    					ExternalPubmedArticle ref = references.next();
    					if(pmid.equals(ref.getAuthoritativeId()))
    						pas.add(convertExternalPubmedArticle(ref));
    				} else {
    					log.error "getPubmedArticles()-Not found: " + pmid;
    				}
    			}
    		}
        } catch(Exception e) {
            log.error(e.getMessage());
        }
		return pas;
	}
	
	/**
	 * Returns a json representation of the statistics and results of a PubMed search.
	 * @param typeQuery		Specifies if the search terms are identifiers or terms and where to search them (title, abstract...)
	 * @param queryTerms 	The list of terms to be searched
	 * @param range			The number of results to be returned in one time
	 * @param offset		The offset in relation to the list of all the results
	 * @return The json representation of statistics and results
	 */
	public JSONArray searchPubmedArticlesWithStats(String typeQuery, List<String> queryTerms, int range, int offset) {
		return searchPubmedArticlesWithStats(typeQuery, queryTerms,
			DEFAULT_START_MONTH, DEFAULT_START_YEAR,
			DEFAULT_END_MONTH, DEFAULT_END_YEAR,
			DEFAULT_MAX_RESULTS, DEFAULT_OFFSET,
			range, offset);
	}
	
	/**
	 * Returns a json representation of statistics and results
     * @param typeQuery		Specifies if the search terms are identifiers or terms and where to search them (title, abstract...)
	 * @param queryTerms 	The list of terms to be searched
	 * @param pubStartMonth	The starting month
	 * @param pubStartYear	The starting year
	 * @param pubEndMonth	The ending month
	 * @param pubEndYear	The ending year
	 * @param range			The number of results to be returned in one time
	 * @param offset		The offset in relation to the list of all the results
	 * @return The json representation of statistics and results
	 */
	public JSONObject searchPubmedArticlesWithStats(
			String typeQuery, List<String> queryTerms, 
			int startMonth, int startYear, int endMonth, int endYear,
			int range, int offset) {
			
		log.info("proxy: " + grailsApplication.config.domeo.proxy.ip + "-" + grailsApplication.config.domeo.proxy.port) ;
		IPubmedArticleManager pa = new PubmedArticleManagerImpl((grailsApplication.config.domeo.proxy.ip.isEmpty()?"":grailsApplication.config.domeo.proxy.ip), 
			(grailsApplication.config.domeo.proxy.port.isEmpty()?"":grailsApplication.config.domeo.proxy.port));
		Map<Map<String,String>, List<ExternalPubmedArticle>> epas;
		if(startMonth>0 && endMonth>0 && startYear>=1900 && endYear>=1900 && startYear<=endYear) {
			epas = pa.searchPubmedArticles(typeQuery, queryTerms, 
				startMonth, startYear, endMonth, endYear,
				(range>0 ? range:DEFAULT_MAX_RESULTS), (offset>=0 ? offset:DEFAULT_OFFSET));
		} else {
			epas = pa.searchPubmedArticles(typeQuery, queryTerms, 
				DEFAULT_START_MONTH, DEFAULT_START_YEAR, DEFAULT_END_MONTH, DEFAULT_END_YEAR,
				(range>0 ? range:DEFAULT_MAX_RESULTS), (offset>=0 ? offset:DEFAULT_OFFSET));
		} 
		
		JSONObject toReturn = new JSONObject();
		Map<String,String> stats = epas.keySet().iterator().next();
		Iterator<String> it = stats.keySet().iterator();
		while(it.hasNext()) {
			String key = it.next();
			toReturn.put(key, stats.get(key));
		}
		
		JSONArray pas = new JSONArray();
		for(ExternalPubmedArticle epa: epas.values().iterator().next()) {
			pas.add(convertExternalPubmedArticle(epa));
		}

		JSONArray articlesJson = new JSONArray();
		if(typeQuery.equals(IPubmedArticleManager.QUERY_TYPE_PUBMED_IDS)) {
			for(String id: queryTerms) {
				if(id.equals(IPubmedArticleManager.UNRECOGNIZED)) {
					toReturn.add(IPubmedArticleManager.UNRECOGNIZED);
				} else {
					for(JSONObject publication: pas) {
						if(publication.containsKey("pmid") && publication.get("pmid").equals(id)) {
							articlesJson.add(publication);
							break;
						}
					}
				}
			}
			toReturn.put("results", articlesJson);
		} else {
			toReturn.put("results", pas);
		}
		return toReturn;
	}
			
	/**
	 * Returns the list of PubMed metadata entries matching the search criteria.
	 * @param typeQuery	The type of query
	 * @param textQuery	The text of the query (
	 * @return
	 */
	private JSONArray searchPubmedArticles(String typeQuery, List<String> textQuery) {
		log.info("proxy: " + grailsApplication.config.domeo.proxy.ip + "-" + grailsApplication.config.domeo.proxy.port) ;
		IPubmedArticleManager pa = new PubmedArticleManagerImpl((grailsApplication.config.domeo.proxy.ip.isEmpty()?"":grailsApplication.config.domeo.proxy.ip), 
			(grailsApplication.config.domeo.proxy.port.isEmpty()?"":grailsApplication.config.domeo.proxy.port));
		List<ExternalPubmedArticle> epas = pa.getPubmedArticles(typeQuery, textQuery, new Integer(1), new Integer(1900), new Integer(DEFAULT_END_MONTH), new Integer(DEFAULT_END_YEAR));
		JSONArray pas = new JSONArray();
		for(ExternalPubmedArticle epa: epas) {
			pas.add(convertExternalPubmedArticle(epa));
		}
		
		JSONArray toReturn = new JSONArray();
		if(typeQuery.equals("pubmedIds")) {
			for(String id: textQuery) {
				if(id.equals("UNRECOGNIZED")) {
					toReturn.add("UNRECOGNIZED");
				} else {
					for(JSONObject publication: pas) {
						if(publication.get("authoritativeId").equals(id)) {
							toReturn.add(publication);
							break;
						}
					}
				}
			}
		} else {
			return pas;
		}
		
		return
	}
	
	/**
	* Converts a PubMed metadata entry into json
	* @param epa	The PubMed record
	* @return	The json representation of the PubMed record
	*/
   private JSONObject convertExternalPubmedArticle(ExternalPubmedArticle epa) {
	   
	   try {
		   JSONObject pubmedArticle = new JSONObject();
		   pubmedArticle.put("id", epa.getId());
		   pubmedArticle.put("url", "http://www.ncbi.nlm.nih.gov/pubmed/" + epa.getAuthoritativeId());
		   pubmedArticle.put("title", epa.getTitle());
		   pubmedArticle.put("source", 'http://dbpedia.org/resource/PubMed');
		   pubmedArticle.put("type", epa.getOntologyType());
		   pubmedArticle.put("pmid", epa.getAuthoritativeId());
		   pubmedArticle.put("pmcid", epa.getPMC());
		   pubmedArticle.put("doi", epa.getDOI());
		   pubmedArticle.put("publicationAuthors", epa.getAuthorNamesString());
		   pubmedArticle.put("publicationInfo", epa.getJournalPublicationInfoString());
		   pubmedArticle.put("publicationDate", epa.getPublicationDateString());
		   pubmedArticle.put("journalName",epa.getJournalName());
		   pubmedArticle.put("journalIssn", epa.getISSN());
		 
		   
		   JSONArray pubmedArticleTypes = new JSONArray();
		   List<PublicationType> types = epa.getPublicationTypes();
		   for(PublicationType type: types) {
			   if(type.getContent().trim().equals("Journal Article") || type.getContent().trim().equals("Letter")) {
				   pubmedArticleTypes.add("Journal Article");
				   //break;
			   } else {
				   pubmedArticleTypes.add("Other type");
			   }
		   }
		   pubmedArticle.put("types", pubmedArticleTypes);
		   
		   JSONArray authorNamesList = new JSONArray();
		   List<ExternalAuthor> authors = epa.getHasAuthors();
		   for(ExternalAuthor author: authors) {
			   JSONObject personName = new JSONObject();
			   personName.put("firstName", author.getFirstName());
			   personName.put("middleName", author.getMiddlename());
			   personName.put("lastName", author.getSurname());
			   personName.put("fullName", author.getFullname());
			   authorNamesList.add(personName);
		   }
		   
		   pubmedArticle.put("authorNames", authorNamesList);
		   return pubmedArticle;
	   } catch (Exception e) {
		   log.error "Error: ${e.message}", e
		   return null;
	   }
   }
}
