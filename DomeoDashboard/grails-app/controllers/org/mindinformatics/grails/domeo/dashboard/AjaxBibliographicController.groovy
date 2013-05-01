package org.mindinformatics.grails.domeo.dashboard



import org.codehaus.groovy.grails.web.json.JSONObject

import grails.converters.JSON

class AjaxBibliographicController {

	def url = {
		def citation = new JSONObject();
		citation.put("message", "WIP: Citation not available");
		render citation as JSON;
	}
}
