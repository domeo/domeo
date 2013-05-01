package org.mindinformatics.grails.domeo

import org.mindinformatics.grails.domeo.dashboard.security.User

class AgentsController {

	def springSecurityService
	def usersManagementService;
	
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
		if(params.format.equals("json")) {
			render('[');
			
			render("  {");
			render("    \"uri\": \"");
			//render("http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath()+"/user/"+
			//		user.id);
			render("urn:domeo:person:uuid:"+user.id);
			render("\",");
			render("    \"@id\": \"");
			//render("http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath()+"/user/"+
			//		user.id);
			render("urn:person:uuid:"+user.id);
			render("\",");
			render("    \"@type\": \"");
			render("foafx:Person");
			render("\",");
			render("    \"domeo:uuid\": \"");
			render(user.id);
			render("\",");
			render("    \"foafx:email\": \"");
			render(user.email);
			render("\",");
			render("    \"foafx:title\": \"");
			render(user.title!=null?user.title:"");
			render("\",");
			render("    \"foafx:name\": \"");
			render(user.displayName);
			render("\",");
			render("    \"foafx:firstname\": \"");
			render(user.firstName);
			render("\",");
			render("    \"foafx:middlename\": \"");
			render(user.middleName!=null?user.middleName:"");
			render("\",");
			render("    \"foafx:lastname\": \"");
			render(user.lastName);
			render("\",");
			render("    \"foafx:picture\": \"");
			render("http://www.hcklab.org/images/me/paolo%20ciccarese-boston.jpg");
			render("\"");
			render("  },");
			
			render(']');
		}
	}
	
	def software = {
		def name = grailsApplication.metadata['app.name']
		def fullname = grailsApplication.metadata['app.fullname']
		def build = grailsApplication.metadata['app.build']
		def version = grailsApplication.metadata['app.version']

		if(params.format.equals("json") && params.id.equals("domeo")) {
			render('[');
			
			render("  {");
			render("    \"@id\": \"");
			render("urn:domeo:software:id:"+name+"-"+version+"-"+build);
			render("\",");
			render("    \"@type\": \"");
			render("foafx:Software");
			render("\",");
			render("    \"domeo:uuid\": \"");
			render(name+"_"+version+"_"+build);
			render("\",");
			render("    \"foafx:name\": \"");
			render(fullname);
			render("\",");
			render("    \"foafx:version\": \"");
			render(version);
			render("\",");
			render("    \"foafx:build\": \"");
			render(build);
			render("\"");
			render("  },");
			
			render(']');
		}
	}
}
