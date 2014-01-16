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
package org.mindinformatics.services.connector.bioportal

/**
 * @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
 *
 * Parameters for calling NCBO's Bioontology text mining service
 * Based on documentation at http://www.bioontology.org/wiki/index.php/Annotator_User_Guide (wiki modification date=12 June 2010, at 01:08)
 */
class BioPortalAnnotatorRequestParameters {
	String text = ''
	String apikey = '<to-be-defined>'
	Set<String> ontologies = []as Set
	Set<String> mappingTypes = []as Set
	Integer minimum_match_length = 0
	Integer max_level = 0
	
	String toParameterString() {
		Map props = this.properties as Map
		StringBuffer theParams = new StringBuffer();
		
		int counter = 0;
		props.each{key,value ->
			
			if(!['class', 'metaClass'].contains(key)){
				def newValue = value
				println "$key -> $value"
				if(value instanceof java.util.Collection){
					newValue = value.join(',')
				}
				if(key) {
					if(counter==0) theParams.append("?");
					else theParams.append("&");
				
					theParams.append(key+"="+newValue);
				} 
			}
			counter++;
		}
		theParams
	}
	
	Map toMap(){
		Map props = this.properties as Map
		Map theParams = [:]
		props.each{key,value ->
			if(!['class', 'metaClass'].contains(key)){
				def newValue = value
				println "$key -> $value"
				if(value instanceof java.util.Collection){
					newValue = value.join(',')
				}
				theParams[key] = newValue
			}
		}
		theParams
	}
}
