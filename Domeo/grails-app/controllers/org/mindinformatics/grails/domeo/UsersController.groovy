package org.mindinformatics.grails.domeo

import grails.converters.JSON

import org.mindinformatics.grails.domeo.dashboard.groups.DefaultGroupStatus
import org.mindinformatics.grails.domeo.dashboard.groups.DefaultUserStatusInGroup
import org.mindinformatics.grails.domeo.dashboard.groups.GroupUtils
import org.mindinformatics.grails.domeo.dashboard.groups.UserGroup
import org.mindinformatics.grails.domeo.dashboard.security.User

class UsersController {
	
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
			render("urn:user:uuid:"+user.username);
			render("\",");
			render("    \"username\": \"");
			render(user.username);
			render("\",");
			render("    \"screenname\": \"");
			render(user.displayName);
			render("\"");
			render("  },");
			render(']');
		} else {
			render(" info >>>>>> " + params.id);
		}
	}
	
	def groups = {
		def user = getUser();
		def userGroups = usersManagementService.listUserGroups(user);
		
		if(params.format.equals("json")) {
			render('[');
			
			userGroups.each { userGroup->
				
				// TODO groups
				render("  {");
				render("    \"uuid\": \"");
				render(userGroup.group.id);
				render("\",");
				render("    \"uri\": \"");
				//render("urn:group:uuid:"+ userGroup.group.id);
				render(userGroup.group.uri);
				render("\",");
				render("    \"name\": \"");
				render(userGroup.group.name);
				render("\",");
				render("    \"description\": \"");
				render(userGroup.group.description);
				render("\",");
				render("    \"memberssince\": \"");
				render(userGroup.dateCreated);
				render("\",");
				render("    \"grouplink\": \"");
				render("http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath()+"/group/?uri="+
					"urn:group:uuid:"+ userGroup.group.id);
				render("\",");
				render("    \"roles\": [");
				userGroup.roles.each{role->
					render("       {");
					render("         \"uuid\": \"");
					render(role.authority);
					render("\",");
					render("         \"name\": \"");
					render(role.label);
					render("\"");
					render("       },");
				}
				render("    ]");
				render(",");
				render("    \"visibility\": [");
				render("       {");
				render(          "\"uuid\": \"");
				render(          userGroup.group.privacy.uuid);
				render(          "\",");
				render(			 "\"name\": \"");
				render(          userGroup.group.privacy.label);
				render(          "\"");
				render("       }");
				render("    ]");
				render(",");
				render(          "\"permissionread\": \"");
				
				// Guest role !!!!!!
				
				if((GroupUtils.getStatusValue(userGroup.group)==DefaultGroupStatus.ACTIVE.value() ||
					GroupUtils.getStatusValue(userGroup.group)==DefaultGroupStatus.LOCKED.value()) &&
					(userGroup.status.value==DefaultUserStatusInGroup.ACTIVE.value() ||
					userGroup.status.value==DefaultUserStatusInGroup.LOCKED.value())) {
					render(      true);
				} else {
					render(      false);
				}
				render(          "\",");
				render(			 "\"permissionwrite\": \"");
				if(GroupUtils.getStatusValue(userGroup.group)==DefaultGroupStatus.ACTIVE.value() &&
					userGroup.status.value==DefaultUserStatusInGroup.ACTIVE.value() && !userGroup.isGuest()) {
					render(      true);
				} else {
					render(      false);
				}
				render(          "\"");
				render("  },");
			}
			
			render(']');
		} else {
			render(" groups >>>>>> " + params.id);
		}
	}
}
