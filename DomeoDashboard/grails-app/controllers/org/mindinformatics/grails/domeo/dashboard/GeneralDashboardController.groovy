package org.mindinformatics.grails.domeo.dashboard

import grails.converters.JSON

import org.mindinformatics.grails.domeo.dashboard.groups.DefaultGroupPrivacy
import org.mindinformatics.grails.domeo.dashboard.groups.DefaultGroupRoles
import org.mindinformatics.grails.domeo.dashboard.groups.DefaultGroupStatus
import org.mindinformatics.grails.domeo.dashboard.groups.DefaultUserStatusInGroup
import org.mindinformatics.grails.domeo.dashboard.groups.Group
import org.mindinformatics.grails.domeo.dashboard.groups.GroupPrivacy
import org.mindinformatics.grails.domeo.dashboard.groups.GroupRole
import org.mindinformatics.grails.domeo.dashboard.groups.UserGroup
import org.mindinformatics.grails.domeo.dashboard.groups.UserStatusInGroup
import org.mindinformatics.grails.domeo.dashboard.security.AccountRequest
import org.mindinformatics.grails.domeo.dashboard.security.DefaultRoles
import org.mindinformatics.grails.domeo.dashboard.security.Role
import org.mindinformatics.grails.domeo.dashboard.security.User
import org.mindinformatics.grails.domeo.dashboard.security.UserRole
import org.mindinformatics.grails.domeo.dashboard.security.UserStatus

/**
* @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
*/
class GeneralDashboardController {

    def emailingService
	def springSecurityService
	def usersManagementService
	def usersGroupsManagementService
	
	protected def injectUserProfile() {
		def principal = springSecurityService.principal
		if(principal.equals("anonymousUser")) {
			redirect(controller: "login", action: "index");
		} else {
			String username = principal.username
			def user = User.findByUsername(username);
			if(user==null) {
				log.error "Error:User not found for username: " + username
				render (view:'error', model:[message: "User not found for username: "+username]);
			}
			user
		}
	}
	
	def resetUserPassword = {
		def user = User.findById(params.user)
		
		render (view:'resetUserPassword', model: [user: user]);
	}
	
	def saveUserPassword = {UserResetPasswordCommand userResetPasswordCommand->
		def user = User.findById(params.user)
		if(userResetPasswordCommand.hasErrors()) {
			userResetPasswordCommand.errors.allErrors.each { println it }
			render(view:'resetUserPassword', model:[user: user, item:userResetPasswordCommand,
				,msgError: 'The password has not been saved successfully'])
		} else {
			user.password = springSecurityService.encodePassword(userResetPasswordCommand.password);
			
			redirect (action:'showUser',id: user.id, model: [
						msgSuccess: 'Passowrd saved successfully']);
		}
	}
	
	def showUser = {
		if(params.id!=null) {
			def user = usersGroupsManagementService.getUser(params.id);
			if(user!=null) {
				render (view:'showUser', model:[item: user,
							userRoles: usersManagementService.getUserRoles(user),
							userGroups: usersManagementService.getUserGroups(user),
							userCircles: usersManagementService.getUserCircles(user),
							userCommunities: usersManagementService.getUserCommunities(user),
							appBaseUrl: request.getContextPath()
						]);
			} else {
				render (view:'/error', model:[message: "User not found for id: "+params.id]);
			}
		} else {
			render (view:'/error', model:[message: "User id not defined!"]);
		}
	}
	
	def showReadOnlyUser = {
		if(params.id!=null) {
			def user = usersGroupsManagementService.getUser(params.id);
			if(user!=null) {
				render (view:'readOnlyUser', model:[item: user,
							userRoles: usersManagementService.getUserRoles(user),
							userGroups: usersManagementService.getUserGroups(user),
							userCircles: usersManagementService.getUserCircles(user),
							userCommunities: usersManagementService.getUserCommunities(user),
							appBaseUrl: request.getContextPath()
]);
			} else {
				render (view:'error', model:[message: "User not found for id: "+params.id]);
			}
		} else {
			render (view:'error', model:[message: "User id not defined!"]);
		}
	}
	
	protected def listOfGroupRoles() {
		def roles = []
		roles.add(DefaultGroupRoles.ADMIN)
		roles.add(DefaultGroupRoles.MANAGER)
		roles.add(DefaultGroupRoles.USER)
		roles
	}

	protected def listOfUserGroupStatus() {
		def roles = []
		roles.add(DefaultGroupStatus.ACTIVE)
		roles.add(DefaultGroupStatus.DISABLED)
		roles.add(DefaultGroupStatus.DELETED)
		roles
	}
	
	def getUserRoles(def user) {
		def userRoles = []
		def ur = UserRole.findAllByUser(user)
		println ur
		ur.each { userRoles.add(it.role)}
		return userRoles
	}

	def getUserGroups(def user) {
		def ur = []
		def userGroups = []
		ur = UserGroup.findAllByUser(user)
		ur.each { userGroups.add(it.group)}
		return ur
	}
	
	protected def updateUserRole(def user, def role, def value) {
		if(value=='on') {
			UserRole ur = UserRole.create(user, role)
			ur.save(flush:true)
		} else {
			def ur = UserRole.findByUserAndRole(user, role)
			if(ur!=null) {
				ur.delete(flush:true)
			}
		}
	}
	
	def updateUserStatus(def user, def status) {
		println status
		if(status==UserStatus.CREATED_USER.value()) {
			user.enabled = false
			user.accountExpired = false
			user.accountLocked = false
		} else if(status==UserStatus.ACTIVE_USER.value()) {
			user.enabled = true
			user.accountExpired = false
			user.accountLocked = false
		} else if(status==UserStatus.DISABLED_USER.value()) {
			user.enabled = false
			user.accountExpired = false
			user.accountLocked = false
		} else if(status==UserStatus.LOCKED_USER.value()) {
			user.enabled = true
			user.accountExpired = false
			user.accountLocked = true
		}
	}
	
	def lockUser = {
		def user = User.findById(params.id)
		user.accountLocked = true
		user.enabled = true;
		if(params.redirect)
			redirect(action:params.redirect, params:[id: params.id])
		else
			redirect(action:'showUser', params:[id: params.id])
			//render (view:'showProfile', model:[user: user])
	}

	def unlockUser = {
		def user = User.findById(params.id)
		user.accountLocked = false
		user.enabled = true;
		if(params.redirect)
			redirect(action:params.redirect, params:[id: params.id])
		else
			redirect(action:'showUser', params:[id: params.id])
	}

	def enableUser = {
		def user = User.findById(params.id)
		user.enabled = true
		user.accountLocked = false
		if(params.redirect)
			redirect(action:params.redirect, params:[id: params.id])
		else
			redirect(action:'showUser', params:[id: params.id])
	}

	def disableUser = {
		def user = User.findById(params.id)
		user.enabled = false
		user.accountLocked = false
		if(params.redirect)
			redirect(action:params.redirect, params:[id: params.id])
		else
			redirect(action:'showUser', params:[id: params.id])
	}
	
	def manageUserGroups = {
		def user = User.findById(params.id)
		
		if (!params.max) params.max = 15
		if (!params.offset) params.offset = 0
		if (!params.sort) params.sort = "name"
		if (!params.order) params.order = "asc"

		def results = usersManagementService.listUserGroups(user, params.max, params.offset, params.sort, params.order);

		render (view:'manageGroups', model:["usergroups" : results, "groupsTotal": Group.count(), "menuitem" : "listGroups", "user": user])
	}
	
	def editUserRoleInGroup = {
		def user = User.findById(params.user)
		def group = Group.findById(params.id)
		
		def ug = UserGroup.findByUserAndGroup(user, group)
	
		render (view:'editUserInGroup', model:[action: "edit", usergroup: ug, userRoles: GroupRole.list()])
	}
	
	def updateUserInGroup = {
		def user = User.findById(params.user)
		def group = Group.findById(params.group)
		
		def ug = UserGroup.findByUserAndGroup(user, group)
		ug.roles = []
		
		updateUserInGroupRole(ug, GroupRole.findByAuthority(DefaultGroupRoles.ADMIN.value()), params.Admin)
		updateUserInGroupRole(ug, GroupRole.findByAuthority(DefaultGroupRoles.MANAGER.value()), params.Manager)
		updateUserInGroupRole(ug, GroupRole.findByAuthority(DefaultGroupRoles.CURATOR.value()), params.Curator)
		updateUserInGroupRole(ug, GroupRole.findByAuthority(DefaultGroupRoles.GUEST.value()), params.Guest)
		updateUserInGroupRole(ug, GroupRole.findByAuthority(DefaultGroupRoles.USER.value()), params.User)
		
		if(params.Admin!='on' && params.Manager!='on' && params.Curator!='on' && params.Guest!='on'
			 	&& params.User!='on') {
			 updateUserInGroupRole(ug, GroupRole.findByAuthority(DefaultGroupRoles.GUEST.value()), 'on')
		}
		
		if(params.redirect)
			redirect(action:params.redirect, params: [id: params.user])
		else
			render (view:'/shared/showUser', model:[item: user])
	}
	
	def updateUserInGroupRole(def userGroup, def role, def value) {
		if(value=='on') {
			userGroup.roles.add role
		}
	}
	
	def getUserRolesInGroup(def user) {
		// UserInGroupRole (multiple roles are allowed)
		def userRoles = []
		def ur = UserRole.findAllByUser(user)
		println ur
		ur.each { userRoles.add(it.role)}
		return userRoles
	}
	
	def lockUserInGroup = {
		def user = User.findById(params.user)
		def group = Group.findById(params.id)
		def usergroup = UserGroup.findByUserAndGroup(user, group);
		if(usergroup!=null) {
			usergroup.status = UserStatusInGroup.findByValue(DefaultUserStatusInGroup.LOCKED.value())
		}
		if(params.redirect)
			redirect(action:params.redirect, params: [id: params.user])
		else
			render (view:'/shared/showUser', model:[item: user])
	}
	
	def unlockUserinGroup = {
		def user = User.findById(params.user)
		def group = Group.findById(params.id)
		def usergroup = UserGroup.findByUserAndGroup(user, group);
		if(usergroup!=null) {
			usergroup.status = UserStatusInGroup.findByValue(DefaultUserStatusInGroup.ACTIVE.value())
		}
		if(params.redirect)
			redirect(action:params.redirect, params: [id: params.user])
		else
			render (view:'/shared/showUser', model:[item: user])
	}
	
	def enableUserInGroup = {
		def user = User.findById(params.user)
		def group = Group.findById(params.id)
		def usergroup = UserGroup.findByUserAndGroup(user, group);
		if(usergroup!=null) {
			usergroup.status = UserStatusInGroup.findByValue(DefaultUserStatusInGroup.ACTIVE.value())
		}
		if(params.redirect)
			redirect(action:params.redirect, params: [id: params.user])
		else
			render (view:'/shared/showUser', model:[item: user])
	}
	
	def disableUserInGroup = {
		def user = User.findById(params.user)
		def group = Group.findById(params.id)
		def usergroup = UserGroup.findByUserAndGroup(user, group);
		if(usergroup!=null) {
			usergroup.status = UserStatusInGroup.findByValue(DefaultUserStatusInGroup.SUSPENDED.value())
		}
		if(params.redirect)
			redirect(action:params.redirect, params: [id: params.user])
		else
			render (view:'/shared/showUser', model:[item: user])
	}
	
	def removeUserFromGroup = {
		def user = User.findById(params.user)
		def group = Group.findById(params.id)
		def usergroup = UserGroup.findByUserAndGroup(user, group);
		if(usergroup!=null) {
			usergroup.delete()
		}
		if(params.redirect)
			redirect(action:params.redirect, params: [id: params.user])
		else
			render (view:'/shared/showUser', model:[item: user])
	}
	
	def addUserGroups = {
		def user = User.findById(params.id)
		render (view:'addUserGroups', model:["menuitem" : "searchGroup", 'user': user,
			appBaseUrl: request.getContextPath()]);
	}
	
	def enrollUserInGroup = {
		def user = User.findById(params.user)
		def group = Group.findById(params.group)
		
		def ug = new UserGroup(user:user, group:group,
			status: UserStatusInGroup.findByValue(DefaultUserStatusInGroup.ACTIVE.value()));
		
		if(!ug.save(flush: true)) {
			ug.errors.allErrors.each { println it }
		} else {
			ug.roles = []
			ug.roles.add GroupRole.findByAuthority(DefaultGroupRoles.USER.value())
		}
			
		redirect(action:'showUser', params: [id: params.user]);
	}
	
	def showGroup = {
		def group = Group.findById(params.id)
		render (view:'showGroup', model:[item: group,
			appBaseUrl: request.getContextPath()])
	}
	
	def createGroup = {
		render (view:'createGroup',  model:[action: "create", "menuitem" : "createGroup"]);
	}

	def saveGroup = {GroupCreateCommand groupCreateCmd->
		if(groupCreateCmd.hasErrors()) {
			groupCreateCmd.errors.allErrors.each { println it }
			render(view:'createUser', model:[item:groupCreateCmd, roles: Role.list(),
						defaultRole: Role.findByAuthority("ROLE_USER")])
		} else {
			def group = groupCreateCmd.createGroup()
			if(group)  {
				updateGroupPrivacy(group, groupCreateCmd.privacy);
				updateGroupStatus(group, groupCreateCmd.status);

				if(!group.save()) {
					// Failure in saving
					group.errors.allErrors.each { println it }
					render(view:'/shared/createGroup', model:[item:groupCreateCmd,
								msgError: 'The group has not been saved successfully'])
				} else {
					redirect (action:'showGroup', id: group.id, model: [
								msgSuccess: 'Group saved successfully']);
				}
			} else {
				// User already existing
				render(view:'/shared/createGroup', model:[item:groupCreateCmd,
							msgError: 'A group with this name is already existing'])
			}
		}
	}
	
	def listGroups = {
		if (!params.max) params.max = 15
		if (!params.offset) params.offset = 0
		if (!params.sort) params.sort = "groupsCount"
		if (!params.order) params.order = "asc"

		def results = usersManagementService.listGroups(params.max, params.offset, params.sort, params.order);

		render (view:'listGroups', model:["groups" : results[0], "groupsTotal": Group.count(), "groupsCount": results[1], "menuitem" : "listGroups",
			appBaseUrl: request.getContextPath()])
	}
	
	def listGroupUsers = {
		def user = injectUserProfile()
		def group = Group.findById(params.id)

		if (!params.max) params.max = 10
		if (!params.offset) params.offset = 0
		if (!params.sort) params.sort = "username"
		if (!params.order) params.order = "asc"

		def users = usersManagementService.listGroupUsers(group, params.max, params.offset, params.sort, params.order);

		render (view:'listGroupUsers', model:[group: group, "users" : users, "usersTotal": User.count(), "usersroles": UserRole.list(), "roles" : Role.list(), "menuitem" : "listUsers"])
	}
	
	def searchGroup = {
		render (view:'searchGroup', model:["menuitem" : "searchGroup"]);
	}
	
	def performGroupSearch = {
		def user = injectUserProfile()

		if (!params.max) params.max = 15
		if (!params.offset) params.offset = 0
		if (!params.sort) params.sort = "name"
		if (!params.order) params.order = "asc"

		def groups = [];
		def groupsCount = [:]
		def groupsStatus = [:]
		Group.list().each { agroup ->
			groupsCount.put (agroup.id, UserGroup.findAllWhere(group: agroup).size())
			groupsStatus.put (agroup.id, agroup.status)
		}

		// Search with no ordering
		def groupCriteria = Group.createCriteria();
		def r = [];

		if(params.name!=null && params.name.trim().length()>0 &&
		params.shortName!=null && params.shortName.trim().length()>0) {
			println 'case 1'
			r = groupCriteria.list {
				maxResults(params.max?.toInteger())
				firstResult(params.offset?.toInteger())
				order(params.sort, params.order)
				and {
					like('name', params.name)
					like('shortName', params.shortName)
				}
			}
		} else if(params.name!=null && params.name.trim().length()>0 &&
		(params.shortName==null || params.shortName.trim().length()==0)) {
			println 'case 2'
			r = groupCriteria.list {
				maxResults(params.max?.toInteger())
				firstResult(params.offset?.toInteger())
				order(params.sort, params.order)
				like('name', params.name)
			}
		} else if((params.name==null || params.name.trim().length()==0) &&
		params.shortName!=null &&  params.shortName.trim().length()>0) {
			println 'case 3'
			r = groupCriteria.list {
				maxResults(params.max?.toInteger())
				firstResult(params.offset?.toInteger())
				order(params.sort, params.order)
				like('shortName', params.shortName)
			}
		} else {
			println 'case 4'
			r = groupCriteria.list {
				maxResults(params.max?.toInteger())
				firstResult(params.offset?.toInteger())
				order(params.sort, params.order)
			}
		}
		groups = r.toList();
		//}


		def groupsResults = []
		groups.each { groupItem ->
			def groupResult = [id:groupItem.id, name:groupItem.name, shortName: groupItem.shortName,
						description: groupItem.description, status: groupItem.statusLabel, dateCreated: groupItem.dateCreated]
			groupsResults << groupResult
		}

		def paginationResults = ['offset':params.offset+params.max, 'sort':params.sort, 'order':params.order]


		def results = [groups: groupsResults, pagination: paginationResults, groupsCount: groupsCount]
		render results as JSON
	}
	
	def listRoles = {
		def user = injectUserProfile()

		if (!params.max) params.max = 15
		if (!params.offset) params.offset = 0
		if (!params.sort) params.sort = "authority"
		if (!params.order) params.order = "asc"

		def results = usersManagementService.listRoles(user, params.max, params.offset, params.sort, params.order);

		render (view:'listRoles', model:[user : user, "roles" : results[0], "rolesTotal": Role.count(), "rolesCount": results[1], "menuitem" : "listRoles"])
	}
	
	def listRoleUsers = {
		def user = injectUserProfile()

		if (!params.max) params.max = 15
		if (!params.offset) params.offset = 0
		if (!params.sort) params.sort = "username"
		if (!params.order) params.order = "asc"

		//TODO fix pagination
		def users = [];
		if (params.sort == 'status') {
			def buffer = [];
			def usersStatus = [:]
			User.list().each { auser ->
				usersStatus.put (auser.id, auser.status)
			}
			usersStatus = usersStatus.sort{ a, b -> a.value.compareTo(b.value) }
			if(params.order == "desc")
				usersStatus.each { userStatus ->
					buffer.add(User.findById(userStatus.key));
				}
			else
				usersStatus.reverseEach { userStatus ->
					buffer.add(User.findById(userStatus.key));
				}

			int offset = (params.offset instanceof String) ? Integer.parseInt(params.offset) : params.offset
			int max = (params.max instanceof String) ? Integer.parseInt(params.max) : params.max
			for(int i=offset;i< Math.min(offset+max, usersStatus.size()); i++) {
				users.add(buffer[i]);
			}
		} else if (params.sort == 'isAdmin' || params.sort == 'isAnalyst' || params.sort == 'isManager'
		|| params.sort == 'isCurator' || params.sort == 'isUser') {

		} else if (params.sort == 'name') {
			def buffer = [];
			def usersNames = [:]
			User.list().each { auser ->
				usersNames.put (auser.id, auser.name)
			}
			usersNames = usersNames.sort{ a, b -> a.value.compareTo(b.value) }
			if(params.order == "desc")
				usersNames.each { userName ->
					buffer.add(User.findById(userName.key));
				}
			else
				usersNames.reverseEach { userName ->
					buffer.add(User.findById(userName.key));
				}
			int offset = (params.offset instanceof String) ? Integer.parseInt(params.offset) : params.offset
			int max = (params.max instanceof String) ? Integer.parseInt(params.max) : params.max
			for(int i=offset;i< Math.min(offset+max, usersNames.size()); i++) {
				users.add(buffer[i]);
			}
		} else {
			//def userRoles = UserRole.findAllByRole(Role.findById(params.id));
			//userRoles.each {
			//	users.add it.user;
			//}
		}
		
		def role = Role.findById(params.id)
		users = UserRole.executeQuery("select user from UserRole as userrole join userrole.role as role join userrole.user as user where userrole.role =:role",['role':role]);
		// Missing ordering and pagination
		// http://docs.jboss.org/hibernate/orm/3.3/reference/en/html/queryhql.html#queryhql-expressions

		render (view:'listUsers', model:[user: user, "users" : users, "usersTotal": User.count(), "usersroles": UserRole.list(), "roles" : Role.list(),
					"menuitem" : "listUsers", role: role])
	}
	
	def searchUser = {
		render (view:'searchUser', model:["menuitem" : "searchUser"]);
	}
	
	def performSarchUser = {
		def user = injectUserProfile()

		if (!params.max) params.max = 1
		if (!params.offset) params.offset = 0
		if (!params.sort) params.sort = "username"
		if (!params.order) params.order = "asc"

		//TODO fix pagination
		def users = [];
		if (params.sort == 'status') {
			def buffer = [];
			def usersStatus = [:]
			User.list().each { auser ->
				usersStatus.put (auser.id, auser.status)
			}
			usersStatus = usersStatus.sort{ a, b -> a.value.compareTo(b.value) }
			if(params.order == "desc")
				usersStatus.each { userStatus ->
					buffer.add(User.findById(userStatus.key));
				}
			else
				usersStatus.reverseEach { userStatus ->
					buffer.add(User.findById(userStatus.key));
				}

			int offset = (params.offset instanceof String) ? Integer.parseInt(params.offset) : params.offset
			int max = (params.max instanceof String) ? Integer.parseInt(params.max) : params.max
			for(int i=offset;i< Math.min(offset+max, usersStatus.size()); i++) {
				users.add(buffer[i]);
			}
		} else if (params.sort == 'isAdmin' || params.sort == 'isAnalyst' || params.sort == 'isManager'
		|| params.sort == 'isCurator' || params.sort == 'isUser') {

		} else if (params.sort == 'name') {
			def buffer = [];
			def usersNames = [:]
			User.list().each { auser ->
				usersNames.put (auser.id, auser.name)
			}
			usersNames = usersNames.sort{ a, b -> a.value.compareTo(b.value) }
			if(params.order == "desc")
				usersNames.each { userName ->
					buffer.add(User.findById(userName.key));
				}
			else
				usersNames.reverseEach { userName ->
					buffer.add(User.findById(userName.key));
				}
			int offset = (params.offset instanceof String) ? Integer.parseInt(params.offset) : params.offset
			int max = (params.max instanceof String) ? Integer.parseInt(params.max) : params.max
			for(int i=offset;i< Math.min(offset+max, usersNames.size()); i++) {
				users.add(buffer[i]);
			}
		} else {
			// Search with no ordering
			def userStatusCriteria = User.createCriteria();
			def r = [];
			if(params.firstName!=null && params.firstName.trim().length()>0 &&
			params.lastName!=null && params.lastName.trim().length()>0) {
				r = userStatusCriteria.list {
					maxResults(params.max?.toInteger())
					firstResult(params.offset?.toInteger())
					order(params.sort, params.order)
					and {
						like('firstName', params.firstName)
						like('lastName', params.lastName)
					}
				}
			} else if(params.firstName!=null && params.firstName.trim().length()>0 &&
			(params.lastName==null || params.lastName.trim().length()==0)) {
				r = userStatusCriteria.list {
					maxResults(params.max?.toInteger())
					firstResult(params.offset?.toInteger())
					order(params.sort, params.order)
					like('firstName', params.firstName)
				}
			} else if((params.firstName==null || params.firstName.trim().length()==0) &&
			params.lastName!=null &&  params.lastName.trim().length()>0) {
				r = userStatusCriteria.list {
					maxResults(params.max?.toInteger())
					firstResult(params.offset?.toInteger())
					order(params.sort, params.order)
					like('lastName', params.lastName)
				}
			} else if(params.displayName!=null && params.displayName.trim().length()>0) {
				r = userStatusCriteria.list {
					maxResults(params.max?.toInteger())
					firstResult(params.offset?.toInteger())
					order(params.sort, params.order)
					like('displayName', params.displayName)
				}
			}
			users = r.toList();
		}

		def usersResults = []
		users.each { userItem ->
			def userResult = [id:userItem.id, username:userItem.username, name: userItem.getName(),
						displayName: userItem.getDisplayName(),
						isAdmin: userItem.getIsAdmin(), isManager: userItem.getIsManager(), isUser: userItem.getIsUser(),
						status: userItem.getStatus(), dateCreated: userItem.dateCreated]
			usersResults << userResult
		}

		def paginationResults = ['offset':params.offset+params.max, 'sort':params.sort, 'order':params.order]


		def results = [users: usersResults, pagination: paginationResults]
		render results as JSON
	}
	
	def editGroup = {
		def group = Group.findById(params.id)
		render (view:'editGroup', model:[item: group, action: "edit"])
	}

	def updateGroup = { GroupEditCommand groupEditCmd ->
		if(groupEditCmd.hasErrors()) {
			/* groupCreateCmd.errors.allErrors.each { println it } */
			render(view:'editGroup', model:[item:groupCreateCmd])
		} else {
			def group = Group.findById(params.id)
			group.name = groupEditCmd.name
			group.shortName = groupEditCmd.shortName
			group.description = groupEditCmd.description

			if(groupEditCmd.privacy.equals(DefaultGroupPrivacy.PRIVATE.value())) {
				group.privacy = GroupPrivacy.findByValue(DefaultGroupPrivacy.PRIVATE.value());
			} else if(groupEditCmd.privacy.equals(DefaultGroupPrivacy.RESTRICTED.value())) {
				group.privacy = GroupPrivacy.findByValue(DefaultGroupPrivacy.RESTRICTED.value());
			} else if(groupEditCmd.privacy.equals(DefaultGroupPrivacy.PUBLIC.value())) {
				group.privacy = GroupPrivacy.findByValue(DefaultGroupPrivacy.PUBLIC.value());
			}

			if(groupEditCmd.status.equals(DefaultGroupStatus.ACTIVE.value())) {
				group.enabled = true
				group.locked = false
			} else if(groupEditCmd.status.equals(DefaultGroupStatus.DISABLED.value())) {
				group.enabled = false
				group.locked = false
			} else if(groupEditCmd.status.equals(DefaultGroupStatus.LOCKED.value())) {
				group.enabled = true
				group.locked = true
			}

			render (view:'showGroup', model:[item: group,
				appBaseUrl: request.getContextPath()])
		}
	}

	def updateGroupPrivacy(def group, def privacy) {
		if(privacy==DefaultGroupPrivacy.PRIVATE.value()) {
			group.privacy = GroupPrivacy.findByValue(DefaultGroupPrivacy.PRIVATE.value());
		} else if(privacy==DefaultGroupPrivacy.RESTRICTED.value()) {
			group.privacy = GroupPrivacy.findByValue(DefaultGroupPrivacy.RESTRICTED.value());
		} else if(privacy==DefaultGroupPrivacy.PUBLIC.value()) {
			group.privacy = GroupPrivacy.findByValue(DefaultGroupPrivacy.PUBLIC.value());
		}
	}

	def updateGroupStatus(def group, def status) {
		if(status.equals(DefaultGroupStatus.ACTIVE.value())) {
			group.enabled = true
			group.locked = false
		} else if(status.equals(DefaultGroupStatus.DISABLED.value())) {
			group.enabled = false
			group.locked = false
		} else if(status.equals(DefaultGroupStatus.LOCKED.value())) {
			group.enabled = true
			group.locked = true
		}
	}

	def lockGroup = {
		def group = Group.findById(params.id)
		group.locked = true
		group.enabled = true;
		if(params.redirect)
			redirect(action:params.redirect)
		else
			render (view:'showGroup', model:[item: group])
	}


	def unlockGroup = {
		def group = Group.findById(params.id)
		group.locked = false
		group.enabled = true;
		if(params.redirect)
			redirect(action:params.redirect)
		else
			render (view:'showGroup', model:[item: group])
	}

	def enableGroup = {
		def group = Group.findById(params.id)
		group.enabled = true
		group.locked = false
		if(params.redirect)
			redirect(action:params.redirect)
		else
			render (view:'showGroup', model:[item: group])
	}

	def disableGroup = {
		def group = Group.findById(params.id)
		group.enabled = false
		group.locked = false
		if(params.redirect)
			redirect(action:params.redirect)
		else
			render (view:'showGroup', model:[item: group])
	}
	
	def deleteGroup = { GroupCreateCommand groupCreateCmd ->
		def group = Group.findById(params.id)
		group.delete()
		redirect (action:'listGroups')
	}
	
	def moderateAccountsRequests = {
		def user = injectUserProfile()
		
		if (!params.max) params.max = 10
		if (!params.offset) params.offset = 0
		if (!params.sort) params.sort = "username"
		if (!params.order) params.order = "asc"
	
		def users = usersManagementService.moderateAccountRequests(user, params.max, params.offset, params.sort, params.order);
	
		render (view:'moderateAccountRequests', model:[user: user, "users" : users, "accountRequestsTotal": AccountRequest.count(), "usersroles": UserRole.list(), "roles" : Role.list(), "menuitem" : "listUsers"])
	}
	
	
	def pastAccountsRequests = {
		def user = injectUserProfile()
		
		if (!params.max) params.max = 10
		if (!params.offset) params.offset = 0
		if (!params.sort) params.sort = "username"
		if (!params.order) params.order = "asc"
	
		def users = usersManagementService.pastAccountRequests(user, params.max, params.offset, params.sort, params.order);
	
		render (view:'pastAccountRequests', model:[user: user, "users" : users, "accountRequestsTotal": AccountRequest.count(), "usersroles": UserRole.list(), "roles" : Role.list(), "menuitem" : "listUsers"])
	}
	
	def approveAccountRequest = {
        def appBase = request.getContextPath();
		def user = injectUserProfile()
		def accountRequest = AccountRequest.findById(params.accountRequest);
		if(accountRequest) {
			User newUser = new User(firstName:accountRequest.firstName, lastName:accountRequest.lastName,
				displayName:accountRequest.displayName, country:accountRequest.country,
				affiliation:accountRequest.affiliation, username:accountRequest.username,
				password:accountRequest.password, email:accountRequest.email, enabled: 'true');
			if(!newUser.save(flush:true)) {
				newUser.errors.allErrors.each { render it }
			} else {
				accountRequest.moderated=true;
				accountRequest.moderatedBy=user;
				accountRequest.approved=true;
				accountRequest.userId=newUser.id;
				updateUserRole(newUser, Role.findByAuthority(DefaultRoles.USER.value()), 'on')
                
                try {
                    emailingService.sendAccountConfirmation(appBase, accountRequest)
                } catch(Exception e) {
                    log.error(e.getMessage());
                }
                
				render (view:'showUser', model:[item: newUser,
							userRoles: usersManagementService.getUserRoles(newUser),
							userGroups: usersManagementService.getUserGroups(newUser),
							userCircles: usersManagementService.getUserCircles(newUser),
							userCommunities: usersManagementService.getUserCommunities(newUser)]);
			}
		}
	}
	
	def declineAccountRequest = {
		def user = injectUserProfile()
		def accountRequest = AccountRequest.findById(params.accountRequest);
		if(accountRequest) {
			accountRequest.moderated=true;
			accountRequest.moderatedBy=user;
			accountRequest.approved=false;
			redirect (action: 'moderateAccountsRequests');
		}
	}
	
	def editAccountRequest = {
		def accountRequest = AccountRequest.findById(params.accountRequest);
		if(accountRequest) {
			render (view:'editAccountRequest', model:[item: accountRequest]);
		}
	}
	
	def updateAccountRequest = { AccountRequestEditCommand accountRequestEditCommand->
		if(accountRequestEditCommand.hasErrors()) {
			accountRequestEditCommand.errors.allErrors.each { println it } 
			render(view:'editAccountRequest', model:[item:accountRequestEditCommand])
		} else {
			def user = injectUserProfile()
			def accountRequest = AccountRequest.findById(accountRequestEditCommand.id);
			if(accountRequest) {
				accountRequest.firstName = accountRequestEditCommand.firstName
				accountRequest.lastName = accountRequestEditCommand.lastName
				accountRequest.displayName = accountRequestEditCommand.displayName
				accountRequest.email = accountRequestEditCommand.email
				accountRequest.affiliation = accountRequestEditCommand.affiliation
				accountRequest.country = accountRequestEditCommand.country
			}
			render(view:'editAccountRequest', model:[item:accountRequestEditCommand])
		}
	}

}
