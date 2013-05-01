package org.mindinformatics.grails.domeo.testing

import java.io.PrintWriter;

class UsersController {

	def info = {
		if(params.format.equals("json") && params.id.equals("paolo.ciccarese")) {
			render('[');
			render("  {");
			render("    \"uri\": \"");
			render("http://paolociccarese.info");
			render("\",");
			render("    \"username\": \"");
			render("paolo.ciccarese");
			render("\",");
			render("    \"screenname\": \"");
			render("Dr. Paolo Ciccarese");
			render("\"");
			render("  },");
			render(']');
		} else {
			render(" info >>>>>> " + params.id);
		}
	}
	
	def groups = {
		if(params.format.equals("json") && params.id.equals("paolo.ciccarese")) {
			render('[');
			// TODO groups
			render("  {");
			render("    \"uuid\": \"");
			render("nif");
			render("\",");
			render("    \"uri\": \"");
			render("http://www.commonsemantics.com/group/nif");
			render("\",");
			render("    \"name\": \"");
			render("NIF");
			render("\",");
			render("    \"description\": \"");
			render("Neuroscience Information Framework");
			render("\",");
			render("    \"mermberscount\": \"");
			render("1");
			render("\",");
			render("    \"roles\": [");
			render("       {");
			render("         \"uuid\": \"");
			render("org.mindinformatics.domeo.uris.roles.Admin");
			render("\",");
			render("         \"name\": \"");
			render("Admin");
			render("\"");
			render("       },");
			render("       {");
			render("         \"uuid\": \"");
			render("org.mindinformatics.domeo.uris.roles.Curator");
			render("\",");
			render("         \"name\": \"");
			render("Curator");
			render("\"");
			render("       }");
			render("    ]");
			render(",");
			render("    \"visibility\": [");
			render("       {");
			render("         \"uuid\": \"");
			render("org.mindinformatics.domeo.uris.visibility.Public");
			render("\",");
			render("         \"name\": \"");
			render("Public");
			render("\"");
			render("       }");
			render("    ]");
			render("  },");
			
			render("  {");
			render("    \"uuid\": \"");
			render("mind");
			render("\",");
			render("    \"uri\": \"");
			render("http://www.commonsemantics.com/group/mind");
			render("\",");
			render("    \"name\": \"");
			render("MIND");
			render("\",");
			render("    \"description\": \"");
			render("MIND Informatics Group");
			render("\",");
			render("    \"mermberscount\": \"");
			render("4");
			render("\",");
			render("    \"roles\": [");
			render("       {");
			render("         \"uuid\": \"");
			render("org.mindinformatics.domeo.uris.roles.Member");
			render("\",");
			render("         \"name\": \"");
			render("Member");
			render("\"");
			render("       }");
			render("    ]");
			render("  }");
			
			render(']');
		} else {
			render(" groups >>>>>> " + params.id);
		}
	}
}
