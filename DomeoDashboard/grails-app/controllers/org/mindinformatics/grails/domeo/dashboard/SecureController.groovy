package org.mindinformatics.grails.domeo.dashboard

import org.mindinformatics.grails.domeo.dashboard.security.User

class SecureController {

	def springSecurityService
	def usersManagementService
    def annotationSearchService
	
	/**
	* User injection
	* @return The logged user
	*/
   private def injectUserProfile() {
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
	
	def index = {
		redirect(action:'home');
	}
	
	def home = {
		render(view:'home', model:[menuitem: 'home']);
	}
	
	def browser = {
		
		// Query the lineages the user can access (newst to oldest) 
		// -> pagination?
		// -> permission facets?
		def loggedUser = injectUserProfile();
		render(view:'browser', model:[loggedUser: loggedUser, appBaseUrl: request.getContextPath(),
			userGroups: usersManagementService.getUserGroups(loggedUser),
			menuitem: 'browser', navitem: 'annotationSets']);
	}
	
	def annotator = {
		redirect(controller:'web', action:'domeo');
	}
	
	def set = {
		def loggedUser = injectUserProfile();
		render(view:'browseAnnotationSet', model:[loggedUser: loggedUser, appBaseUrl: request.getContextPath(),
			userGroups: usersManagementService.getUserGroups(loggedUser), setUri: params.id,
			menuitem: 'browser', navitem: 'annotationSet']);
	}
	
    def search = {
        def loggedUser = injectUserProfile();
        
        println "Query" + params.query;
        
        def results;
        if(params.query) {
            results = annotationSearchService.search("domeo_!DOMEO_NS!_agents.@type" , "foafx:Person");
            //results = annotationSearchService.search("ao_!DOMEO_NS!_item.ao_!DOMEO_NS!_context.ao_!DOMEO_NS!_hasSource", "http://www.ncbi.nlm.nih.gov/pmc/articles/PMC2700002/");
        }
        
        render(view:'search', model:[loggedUser: loggedUser, appBaseUrl: request.getContextPath(),
            userGroups: usersManagementService.getUserGroups(loggedUser), results: results,
            menuitem: 'search', navitem: 'search']);
	}
    
	def user = {
		redirect(action:'home', model:[menuitem: 'browser']);
	}
}
