package org.mindinformatics.grails.domeo.testing

class AgentsController {

	def info = {
		if(params.format.equals("json") && params.id.equals("paolo.ciccarese")) {
			render('[');
			
			render("  {");
			render("    \"uri\": \"");
			render("http://www.commonsemantics.com/agent/paolociccarese");
			render("\",");
			render("    \"uuid\": \"");
			render("paolo.ciccarese");
			render("\",");
			render("    \"email\": \"");
			render("paolo.ciccarese@gmail.com");
			render("\",");
			render("    \"title\": \"");
			render("Dr.");
			render("\",");
			render("    \"email\": \"");
			render("paolo.ciccarese@gmail.com");
			render("\",");
			render("    \"name\": \"");
			render("Paolo Ciccarese");
			render("\",");
			render("    \"firstname\": \"");
			render("Paolo");
			render("\",");
			render("    \"middlename\": \"");
			render("Nunzio");
			render("\",");
			render("    \"lastname\": \"");
			render("Ciccarese");
			render("\",");
			render("    \"picture\": \"");
			render("http://www.hcklab.org/images/me/paolo%20ciccarese-boston.jpg");
			render("\"");
			render("  },");
			
			render(']');
		}
	}
	
	def software = {
		if(params.format.equals("json") && params.id.equals("domeo")) {
			render('[');
			
			render("  {");
			render("    \"uri\": \"");
			render("http://www.commonsemantics.com/agent/domeo_"+request);
			render("\",");
			render("    \"uuid\": \"");
			render("domeo_"+request);
			render("\",");
			render("    \"name\": \"");
			render("Domeo");
			render("\",");
			render("    \"version\": \"");
			render(request);
			render("\"");
			render("  },");
			
			render(']');
		}
	}
}
