package org.mindinformatics.domeo.persistence.services

import org.codehaus.groovy.grails.web.json.JSONObject
import org.mindinformatics.grails.domeo.persistence.BibliographicIdMapping

class MappingsService {

	def findMappingsById(String id) {
		long start = System.currentTimeMillis();
		
		JSONObject res = new JSONObject();
		JSONObject resContent = new JSONObject();
		if(id!=null) {
			def identifier = BibliographicIdMapping.findByIdValue(id);
			def identifiers = BibliographicIdMapping.findAllByUuid(identifier.uuid);
			identifiers.each { resContent.put(it.idLabel, it.idValue); }
							
			JSONObject resContentMappings = new JSONObject();
			resContentMappings.put("mappings", resContent);
			res.put("content", resContentMappings);
		} else {
			JSONObject resContentMappings = new JSONObject();
			resContentMappings.put("type", "java.lang.IllegalArgumentException");
			resContentMappings.put("message", "Requested id not specified");
			res.put("exception", resContentMappings);
		}
		res.put("duration", (System.currentTimeMillis()-start) + "ms");
		return res;
	}
	
	def findUrlsById(String id) {
		long start = System.currentTimeMillis();
		
	}
}
