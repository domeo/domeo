package org.mindinformatics.grails.domeo.testing

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

class ProfilesController {

	public static SimpleDateFormat dayTime = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss Z");
	
	def info = {
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
			render("        \"@id\": \"");
			render("maurizio.mosca");
			render("\",");
			render("    \"@type\": \"");
			render("foafx:Person");
			render("\",");
			render("        \"foafx:name\": \"");
			render("Dr. Maurizio Mosca");
			render("\"");
			render("      }");
			render("    ],");
			render("    \"statusplugins\": [");
			
			render("       {");
			render("         \"uuid\": \"");
			render("org.mindinformatics.gwt.domeo.plugins.annotation.qualifier");
			render("\",");
			render("         \"status\": \"");
			render("enabled");
			render("\"");
			render("       },");
			
			render("       {");
			render("         \"uuid\": \"");
			render("org.mindinformatics.gwt.domeo.plugins.annotation.nif.antibodies");
			render("\",");
			render("         \"status\": \"");
			render("enabled");
			render("\"");
			render("       },");
			
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
			render("        \"@id\": \"");
			render("maurizio.mosca");
			render("\",");
			render("    \"@type\": \"");
			render("foafx:Person");
			render("\",");
			render("        \"foafx:name\": \"");
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
			render("        \"@id\": \"");
			render("paolo.ciccarese");
			render("\",");
			render("    \"@type\": \"");
			render("foafx:Person");
			render("\",");
			render("        \"foafx:name\": \"");
			render("Dr. Paolo Ciccarese");
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
