package org.mindinformatics.grails.domeo

import java.text.SimpleDateFormat;
import java.util.Date;

import org.mindinformatics.grails.domeo.dashboard.security.User

class ProfilesController {

	def springSecurityService
	def usersManagementService;
	
	public static SimpleDateFormat dayTime = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss Z");
	
	private def getUser() {
		def principal = springSecurityService.principal
		if(principal.equals("anonymousUser")) {
			redirect(controller: "login", action: "index");
		} else {
			String username = principal.username
			def user = User.findByUsername(username);
			if(user==null) {
				render (view:'error', model:[message: "User not found for username: "+username]);
			}
			user
		}
	}
	
	def info = {
		def user = getUser();
		if(params.format.equals("json") ) {
			render('[');
			
			render("  {");
			render("    \"uuid\": \"");
			render("4fa09e38adb4d0.96200877");
			render("\",");
			render("    \"name\": \"");
			render("Complete Bio Profile");
			render("\",");
			render("    \"description\": \"");
			render("All the tools you need for biocuration");
			render("\",");
			render("    \"createdon\": \"");
			render(dayTime.format(new Date()));
			render("\",");
			render("    \"createdby\": [");
			render("      {");
			render("        \"uuid\": \"");
			render("maurizio.mosca");
			render("\",");
			render("        \"name\": \"");
			render("Dr. Maurizio Mosca");
			render("\"");
			render("      }");
			render("    ],");
			render("    \"statusplugins\": [");
			render("       {");
			render("         \"uuid\": \"");
			render("org.mindinformatics.gwt.domeo.plugins.resource.pubmed");
			render("\",");
			render("         \"status\": \"");
			render("enabled");
			render("\"");
			render("       },");
			render("       {");
			render("         \"uuid\": \"");
			render("org.mindinformatics.gwt.domeo.plugins.resource.pubmedcentral");
			render("\",");
			render("         \"status\": \"");
			render("enabled");
			render("\"");
			render("       },");
			render("       {");
			render("         \"uuid\": \"");
			render("org.mindinformatics.gwt.domeo.plugins.resource.omim");
			render("\",");
			render("         \"status\": \"");
			render("enabled");
			render("\"");
			render("       },");
			render("       {");
			render("         \"uuid\": \"");
			render("org.mindinformatics.gwt.domeo.plugins.resource.bioportal");
			render("\",");
			render("         \"status\": \"");
			render("enabled");
			render("\"");
			render("       },");
			render("       {");
			render("         \"uuid\": \"");
			render("org.mindinformatics.gwt.domeo.client.component.clipboard");
			render("\",");
			render("         \"status\": \"");
			render("enabled");
			render("\"");
			render("       }");
			render("    ]");
			render("  }");
			
			render(']');
		}
	}
	
	def all = {
		def user = getUser();
		if(params.format.equals("json")) {
			render('[');
			
			render("  {");
			render("    \"uuid\": \"");
			render("4fa09e38adb4d0.96200877");
			render("\",");
			render("    \"name\": \"");
			render("Complete Bio Profile");
			render("\",");
			render("    \"description\": \"");
			render("All the tools you need for biocuration");
			render("\",");
			render("    \"createdon\": \"");
			render(dayTime.format(new Date()));
			render("\",");
			render("    \"createdby\": [");
			render("      {");
			render("        \"uuid\": \"");
			render("maurizio.mosca");
			render("\",");
			render("        \"name\": \"");
			render("Dr. Maurizio Mosca");
			render("\"");
			render("      }");
			render("    ],");
			render("    \"statusplugins\": [");
			render("       {");
			render("         \"uuid\": \"");
			render("org.mindinformatics.gwt.domeo.plugins.resource.pubmed");
			render("\",");
			render("         \"status\": \"");
			render("enabled");
			render("\"");
			render("       },");
			render("       {");
			render("         \"uuid\": \"");
			render("org.mindinformatics.gwt.domeo.plugins.resource.pubmedcentral");
			render("\",");
			render("         \"status\": \"");
			render("enabled");
			render("\"");
			render("       },");
			render("       {");
			render("         \"uuid\": \"");
			render("org.mindinformatics.gwt.domeo.plugins.resource.omim");
			render("\",");
			render("         \"status\": \"");
			render("enabled");
			render("\"");
			render("       },");
			render("       {");
			render("         \"uuid\": \"");
			render("org.mindinformatics.gwt.domeo.plugins.resource.bioportal");
			render("\",");
			render("         \"status\": \"");
			render("enabled");
			render("\"");
			render("       },");
			render("       {"); 
			render("         \"uuid\": \"");
			render("org.mindinformatics.gwt.domeo.client.component.clipboard");
			render("\",");
			render("         \"status\": \"");
			render("enabled");
			render(" \"}   ]");
			render("  },");
			
			render("  {");
			render("    \"uuid\": \"");
			render("4fa09e38adb4d0.96200878");
			render("\",");
			render("    \"name\": \"");
			render("Simple Bio Profile");
			render("\",");
			render("    \"description\": \"");
			render("A few tools to start");
			render("\",");
			render("    \"createdon\": \"");
			render(dayTime.format(new Date()));
			render("\",");
			render("    \"createdby\": [");
			render("      {");
			render("        \"uuid\": \"");
			render("Dr. Paolo Ciccarese");
			render("\",");
			render("        \"name\": \"");
			render("paolo.ciccarese");
			render("\"");
			render("      }");
			render("    ],");
			render("    \"statusplugins\": [");
			render("       {");
			render("         \"uuid\": \"");
			render("org.mindinformatics.gwt.domeo.plugins.resource.pubmed");
			render("\",");
			render("         \"status\": \"");
			render("enabled");
			render("\"");
			render("       },");
			render("       {");
			render("         \"uuid\": \"");
			render("org.mindinformatics.gwt.domeo.plugins.resource.pubmedcentral");
			render("\",");
			render("         \"status\": \"");
			render("enabled");
			render("\"");
			render("       },");
			render("       {");
			render("         \"uuid\": \"");
			render("org.mindinformatics.gwt.domeo.plugins.resource.bioportal");
			render("\",");
			render("         \"status\": \"");
			render("enabled");
			render("\"");
			render("       }");
			render("    ]");
			render("  }");
			
			render(']');
		}
	}
}
