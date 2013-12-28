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
package org.mindinformatics.services.connector.yaleimagefinder



import grails.converters.JSON

import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.json.JSONObject

import com.hp.hpl.jena.query.QueryExecution
import com.hp.hpl.jena.query.QueryExecutionFactory
import com.hp.hpl.jena.query.QueryFactory
import com.hp.hpl.jena.query.QuerySolution
import com.hp.hpl.jena.query.ResultSet

/**
 * @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
 */
class YaleImageFinderController {

	def retrievePmcImagesData = {
		
		println request.JSON
		println "pmid " + request.JSON.pmid[0]
		println "pmcid " + request.JSON.pmcid
		println "doi " + request.JSON.doi
		
		//def pmid = '16725051'
		def pmid = request.JSON.pmid[0];
		
		String serviceEndpoint = " http://cbakerlab.unbsj.ca:8080/openrdf-sesame/repositories/yaleImageRepo";
		// This query will return the figures of corrosponding pmid
		String qImageAndValues = "SELECT DISTINCT ?s ?figid ?pmid WHERE {" +
				"?s <http://cbakerlab.unbsj.ca:8085/unbvps/uysie#hasImageID> ?figid." +
				"?s <http://cbakerlab.unbsj.ca:8085/unbvps/uysie#hasPMID> ?pmid." +
				"FILTER(?pmid='" + pmid + "')" +
				"} LIMIT 10";

		log.debug qImageAndValues;
		
		log.debug '*************************************'
		
		com.hp.hpl.jena.query.Query query = QueryFactory.create(qImageAndValues);
		QueryExecution qe = QueryExecutionFactory.sparqlService(serviceEndpoint, query);
		ResultSet results = qe.execSelect();
		
		def pmids = []
		
		def counter = 0;
		while (results.hasNext()) {
			QuerySolution soln = results.nextSolution();
			pmids.add soln.getResource('s').getURI();
		}
		log.debug '*************************************3 ' + pmids
		qe.close();
		
		StringBuffer sb = new StringBuffer();
		pmids.eachWithIndex() { itt, i ->
			sb.append(' <' + itt + '> ');
			if(pmids.size()-1>0 && i<pmids.size()-1) sb.append(',');
		}
		
		String queryTotal = "SELECT DISTINCT * WHERE {" +
		"?s ?p ?o . " +
			" FILTER (?s IN (" +
			sb.toString() +
			" )) " + 
		"}";
			
		log.debug '*************************************4 ' + queryTotal;
			
		com.hp.hpl.jena.query.Query query2 = QueryFactory.create(queryTotal);
		QueryExecution qe2 = QueryExecutionFactory.sparqlService(serviceEndpoint, query2);
		ResultSet results2 = qe2.execSelect();
		
		
		
		
		HashMap<String, HashMap<String, String>> mapResponse = new HashMap<String, HashMap<String, String>>();
 		
		def counter2 = 0;
		while (results2.hasNext()) {
			QuerySolution soln = results2.nextSolution();
			if(mapResponse.containsKey(soln.getResource('s').getURI())) {
				HashMap map = mapResponse.get(soln.getResource('s').getURI());	
				if(map.containsKey(soln.getResource('p').getURI().replaceAll("http://cbakerlab.unbsj.ca:8085/unbvps/uysie#", "uysie:"))) {
					
				} else {
					map.put(soln.getResource('p').getURI().replaceAll("http://cbakerlab.unbsj.ca:8085/unbvps/uysie#", "uysie:"), ((soln.get('o').isLiteral()) ? soln.getLiteral('o').toString() : soln.getResource('o').getURI()));
				}
			} else {
				HashMap tuple = new HashMap<String, String>();
				tuple.put(soln.getResource('p').getURI().replaceAll("http://cbakerlab.unbsj.ca:8085/unbvps/uysie#", "uysie:"), ((soln.get('o').isLiteral()) ? soln.getLiteral('o').toString() : soln.getResource('o').getURI()));
				mapResponse.put(soln.getResource('s').getURI(), tuple);
			}
			
//			println soln.getResource('s').getURI() + " - " + soln.getResource('p').getURI() +
//			 	" - " + ((soln.get('o').isLiteral()) ? soln.getLiteral('o') : soln.getResource('o'));
//				 
//			println mapResponse;
		}
			
		//println "queryTotal " + queryTotal;
		
		qe2.close();
		
		JSONArray jsonResponse = new JSONArray();
		Set<String> keys = mapResponse.keySet();
		for(String key: keys) {
			HashMap<String, String> map = mapResponse.get(key);
			JSONObject o = new JSONObject();
			Set<String> keysInner = map.keySet();
			for(String innerKey: keysInner) {
				o.put(innerKey, map.get(innerKey));
			}
			jsonResponse.put(o);
		}
		
		println jsonResponse;
		render jsonResponse as JSON;
		/*
		 * SELECT * WHERE
{
  VALUES (?value) { ( "v1" ) ( "v2 " ) }
  ?s _:prop ?value .

  # Use ?s in other patterns
}
		 */
		
		
//		render "[{\"uysie:hasCaption\": \"Representative self-terminating radical reactions.\"," + 
//			"\"uysie:hasFullText\": \"Most organic radical reactions occur through a cascade of two or more individual steps [1,2]. Knowledge of the nature and rates of these steps Ì¢‰âÂ‰ÛÏ in other words, the mechanism of the reaction Ì¢‰âÂ‰ÛÏ is of fundamental interest and is also important in synthetic planning. In synthesis, both the generation of the initial radical of the cascade and the removal of the final radical are crucial events [3]. Many useful radical reactions occur through chains that provide a naturally coupled regulation of radical generation and removal. Among the non-chain methods, generation and removal of radicals by oxidation and reduction are important, as is the\"," +
//			"\"uysie:hasFileName\": \"nihms28314f3\"," + 
//			"\"uysie:hasImageID\": \"PMC1524793/1860-5397-2-10-1\"," +
//			"\"uysie:hasTitle\": \"Do alpha-acyloxy and alpha-alkoxycarbonyloxy radicals fragment to form acyl and alkoxycarbonyl radicals?\"}]";
	}
}
