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
package org.mindinformatics.grails.domeo.persistence

import grails.converters.JSON

import org.codehaus.groovy.grails.web.json.JSONObject
import org.mindinformatics.domeo.persistence.ElasticSearchWrapper

/**
 * @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
 * 
 * curl -v http://localhost:3333/Domeo/api/exportLastVersionPublicAnnotation?key=xxx
 */
class ApiController {

	def readOnlyService;
	def grailsApplication;
	
	def exportLastVersionPublicAnnotation = {
		
		def key = params.key;
		
		if(key==grailsApplication.config.domeo.api.consumer.key) {
			ElasticSearchWrapper esWrapper = new ElasticSearchWrapper(grailsApplication.config.elastico.database, grailsApplication.config.elastico.collection, grailsApplication.config.elastico.ip, grailsApplication.config.elastico.port);
			
			def publicAnnotationSets = [] as Set;
			def publicLineageIdentifiers = [] as Set;
			def publicSets = AnnotationSetPermissions.findAllByPermissionType("urn:domeo:access:public");
			publicSets.each {
				publicLineageIdentifiers.add(it.lineageUri);
			}
			
			// Query for all the annotation sets available for the URL
			// and crossing them with those available to the user
			def existingAnnotationSets = LastAnnotationSetIndex.list();
			existingAnnotationSets.each { annotationSet ->
				publicLineageIdentifiers.each { lineageUri ->
					if(annotationSet.lineageUri.equals(lineageUri))
						publicAnnotationSets.add(annotationSet)
				}
			}
			
			def s;
			JSONObject buffer;

			response.setHeader "Content-disposition", "attachment; filename=export"
			response.contentType = 'application/json;charset=utf-8'
			//response.characterEncoding = 'utf-8'
			response.outputStream << '{';
			//response.outputStream << readOnlyService.getExportHeader(loggedUser, request);
			response.outputStream << '"oa:item" : ['
			
			int counter = 0;
			for(LastAnnotationSetIndex set:publicAnnotationSets) {
				
				s = AnnotationSetIndex.findByIndividualUri(set.lastVersionUri);
				if(s!=null) {
				   
					log.info('Serializing ' + s.individualUri);
					buffer = readOnlyService.getAnnotationSet(esWrapper, s);
					if(!buffer.isEmpty()) {
						log.info("Serializing set: " + set.lastVersionUri)
						response.outputStream << buffer.toString()
						if(++counter<publicAnnotationSets.size()) response.outputStream << ','
					} else {
						log.info("Skipping serialization of set: " + set.lastVersionUri)
					}
				} else {
					log.warn("Annotation set dump failed for set: " + set.lastVersionUri);
				}
			}
			response.outputStream << ']'
			response.outputStream << '}'
			
			response.outputStream.flush()
			response.outputStream.close()
		} else {
			response.setHeader "Content-disposition", "attachment; filename=export"
			response.contentType = 'application/json;charset=utf-8'
			response.status = 401
			response.outputStream << '{';
			response.outputStream << '"error" : "System does not have permissions to access the knowledge base"';
			response.outputStream << '}'
			
			response.outputStream.flush()
			response.outputStream.close()
		}
	}
}
