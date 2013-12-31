package org.mindinformatics.grails.domeo.dashboard

import grails.converters.JSON

import org.codehaus.groovy.grails.web.json.JSONObject
import org.mindinformatics.grails.domeo.dashboard.security.User

/**
 * @author Dr. Paolo Ciccarese paolo.ciccarese@gmail.com
 */
class SecuredController {

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
		redirect(action:'search');
	}
	
	def home = {
		redirect(action:'search');
	}
	
	def browse = {
		
		// Query the lineages the user can access (newst to oldest)
		// -> pagination?
		// -> permission facets?
		def loggedUser = injectUserProfile();
		render(view:'browser', model:[loggedUser: loggedUser, appBaseUrl: request.getContextPath(),
			loggedUserRoles: usersManagementService.getUserRoles(loggedUser),
			userGroups: usersManagementService.getUserGroups(loggedUser),
			menuitem: 'browser', navitem: 'annotationSets']);
	}

	def search = {
		def loggedUser = injectUserProfile();
		
		def offset = 0
		if(params.offset) {
			offset = params.offset;
		}
		
		render(view:'search', model:[menuitem: 'home', loggedUser: loggedUser, appBaseUrl: request.getContextPath(),
			loggedUserRoles: usersManagementService.getUserRoles(loggedUser),
			userGroups: usersManagementService.getUserGroups(loggedUser),
			query: params.query, offset: offset, params: params]);
	}
	
	/**
	 * Displays the user profile.
	 * Pushes:
	 * - loggedUser: used for passing the current user in a uniform way
	 * - item: user to display
	 */
	def userAccount = {
		def loggedUser = injectUserProfile()
		if(loggedUser!=null) {			
			render (view:'/secured/userAccount',
				model:[menuitem: 'showProfile',
					loggedUser:loggedUser, loggedUserRoles: usersManagementService.getUserRoles(loggedUser),
					user: loggedUser,
					userRoles: usersManagementService.getUserRoles(loggedUser),
					userGroups: usersManagementService.getUserGroups(loggedUser),
					userCircles: usersManagementService.getUserCircles(loggedUser),
					userCommunities: usersManagementService.getUserCommunities(loggedUser),
					appBaseUrl: request.getContextPath()
					]);
		} else {
			render (view:'/error', model:[message: "User not found for id: "+params.id]);
		}
	}
	
	def userSettings = {
		
	}	
}
