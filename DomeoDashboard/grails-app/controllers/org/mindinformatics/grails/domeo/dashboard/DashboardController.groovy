package org.mindinformatics.grails.domeo.dashboard



import grails.converters.JSON

import org.mindinformatics.grails.domeo.dashboard.circles.Circle
import org.mindinformatics.grails.domeo.dashboard.circles.UserCircle
import org.mindinformatics.grails.domeo.dashboard.groups.DefaultGroupRoles
import org.mindinformatics.grails.domeo.dashboard.groups.DefaultGroupStatus
import org.mindinformatics.grails.domeo.dashboard.groups.DefaultUserStatusInGroup
import org.mindinformatics.grails.domeo.dashboard.groups.Group
import org.mindinformatics.grails.domeo.dashboard.groups.GroupRole
import org.mindinformatics.grails.domeo.dashboard.groups.UserGroup
import org.mindinformatics.grails.domeo.dashboard.groups.UserStatusInGroup
import org.mindinformatics.grails.domeo.dashboard.security.OpenID
import org.mindinformatics.grails.domeo.dashboard.security.Role
import org.mindinformatics.grails.domeo.dashboard.security.User
import org.mindinformatics.grails.domeo.dashboard.security.UserRole
import org.mindinformatics.grails.domeo.dashboard.security.UserStatus

/**
 * @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
 */
class DashboardController {

	def springSecurityService
	def usersManagementService
	
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

	/**
	 * Sends to the main dashboard page that is similar for all the available roles.
	 * The page displays the different tools available to the different roles.
	 * Pushes
	 * - loggedUser: used for passing the current user in a uniform way
	 */
	def index = {
		redirect(action: 'dashboard', mapping: "/dashboard")
	}
	
	def dashboard = {
		def loggedUser = injectUserProfile()
		if(loggedUser!=null) render (view:'/dashboard/home',
			model:[loggedUser: loggedUser,
				loggedUserRoles: usersManagementService.getUserRoles(loggedUser)]);
	}
	
	/**
	 * Displays the user profile.
	 * Pushes:
	 * - loggedUser: used for passing the current user in a uniform way
	 * - item: user to display
	 */
	def showProfile = {
		if(params.id!=null) {
			def loggedUser = injectUserProfile()
			def user = usersManagementService.getUser(params.id);
			if(user!=null && loggedUser!=null) {
				if(user.id==loggedUser.id) {
					render (view:'/dashboard/showProfile', 
						model:[menuitem: 'showProfile', 
							loggedUser:loggedUser, loggedUserRoles: usersManagementService.getUserRoles(loggedUser),
							user: user,
							userRoles: usersManagementService.getUserRoles(user),
							userGroups: usersManagementService.getUserGroups(user),
							userCircles: usersManagementService.getUserCircles(user),
							userCommunities: usersManagementService.getUserCommunities(user),
							appBaseUrl: request.getContextPath()
							]);
				} else {
					render (view:'/dashboard/showUser',
						model:[menuitem: 'showUser', 
							loggedUser:loggedUser, loggedUserRoles: usersManagementService.getUserRoles(loggedUser),
							user: user,
							userRoles: usersManagementService.getUserRoles(user),
							userGroups: usersManagementService.getUserGroups(user),
							userCircles: usersManagementService.getUserCircles(user),
							userCommunities: usersManagementService.getUserCommunities(user),
							appBaseUrl: request.getContextPath()
							]);
				}
			} else {
				render (view:'/error', model:[message: "User not found for id: "+params.id]);
			}
		} else {
			render (view:'/error', model:[message: "User id not defined!"]);
		}
	}
	
	def editProfile = {
		def loggedUser = injectUserProfile()
		def user = User.findById(params.id)
		if(user!=null && loggedUser!=null) {
			render (view:'/dashboard/editProfile', 
				model:[menuitem: 'showProfile', 
					loggedUser:loggedUser, loggedUserRoles: usersManagementService.getUserRoles(loggedUser),
					user: user,
					userRoles: getUserRoles(user), roles: Role.list(), userGroups: getUserGroups(user), action: "edit",
					groupRoles: listOfGroupRoles(), groupStatus: listOfUserGroupStatus()])
		}
	}
	
	def updateProfile = { UserEditCommand userEditCmd ->
		def loggedUser = injectUserProfile()
		if(loggedUser!=null) {
			if(userEditCmd.hasErrors()) {
				userEditCmd.errors.allErrors.each { println it }
				render(view:'/dashboard/editProfile', 
					model:[menuitem: 'showProfile', 
						loggedUser:loggedUser,
						item:userEditCmd])
			} else {
				def user = User.findById(params.id)
				user.firstName = userEditCmd.firstName
				user.lastName = userEditCmd.lastName
				user.displayName = userEditCmd.displayName
				user.email = userEditCmd.email
				user.affiliation = userEditCmd.affiliation
				user.country  = userEditCmd.country
	
				updateUserStatus(user, params.status)
	 
				redirect (action:'showProfile', id: user.id, params: [
					msgSuccess: 'Profile updated successfully']);
			}
		}
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
	
	public def listOfGroupRoles() {
		def roles = []
		roles.add(DefaultGroupRoles.ADMIN)
		roles.add(DefaultGroupRoles.MANAGER)
		roles.add(DefaultGroupRoles.USER)
		roles
	}

	public def listOfUserGroupStatus() {
		def roles = []
		roles.add(DefaultGroupStatus.ACTIVE)
		roles.add(DefaultGroupStatus.DISABLED)
		roles.add(DefaultGroupStatus.DELETED)
		roles
	}
	
	def lockUser = {
		def user = User.findById(params.id)
		user.accountLocked = true
		user.enabled = true;
		if(params.redirect)
			redirect(action:params.redirect, params:[id: params.id])
		else
			redirect(action:showProfile, params:[id: params.id])
			//render (view:'showProfile', model:[user: user])
	}


	def unlockUser = {
		def user = User.findById(params.id)
		user.accountLocked = false
		user.enabled = true;
		if(params.redirect)
			redirect(action:params.redirect, params:[id: params.id])
		else
			redirect(action:showProfile, params:[id: params.id])
	}

	def enableUser = {
		def user = User.findById(params.id)
		user.enabled = true
		user.accountLocked = false
		if(params.redirect)
			redirect(action:params.redirect, params:[id: params.id])
		else
			redirect(action:showProfile, params:[id: params.id])
	}

	def disableUser = {
		def user = User.findById(params.id)
		user.enabled = false
		user.accountLocked = false
		if(params.redirect)
			redirect(action:params.redirect, params:[id: params.id])
		else
			redirect(action:showProfile, params:[id: params.id])
	}
	
	def resetUserPassword = {
		def loggedUser = injectUserProfile()
		def user = User.findById(params.user)
		
		render (view:'resetUserPassword', model: [
			loggedUser:loggedUser, loggedUserRoles: usersManagementService.getUserRoles(loggedUser),
			user: user]);
	}
	
	def saveUserPassword = {UserResetPasswordCommand userResetPasswordCommand->
		def loggedUser = injectUserProfile()
		def user = User.findById(params.user)
		if(userResetPasswordCommand.hasErrors()) {
			userResetPasswordCommand.errors.allErrors.each { println it }
			render(view:'resetUserPassword', model:[
				loggedUser:loggedUser, loggedUserRoles: usersManagementService.getUserRoles(loggedUser),
				user: user, item:userResetPasswordCommand,
				msgError: 'The password has not been saved successfully'])
		} else {
			render user.password
			user.password = springSecurityService.encodePassword(userResetPasswordCommand.password);
			
			redirect(action:'showProfile', params:[id: params.user, msgSuccess: 'Password saved successfully']);
		}
	}
	
	def openIdForm = {
		def loggedUser = injectUserProfile()
		def user = User.findById(params.id)
		
		render (view:'provideOpenID', model: [
			menuitem: 'openId',
			loggedUser:loggedUser, loggedUserRoles: usersManagementService.getUserRoles(loggedUser),
			user: user]);
	}
	
	def listOpenIds = {
		def loggedUser = injectUserProfile()
		def user = User.findById(params.id)
		
		render (view:'listOpenIds', model: [
			menuitem: 'openId',
			loggedUser:loggedUser, loggedUserRoles: usersManagementService.getUserRoles(loggedUser),
			user: user]);
	}
	
	def removeOpenId = {
		def loggedUser = injectUserProfile()
		def user = User.findById(params.userId)
		def oi = OpenID.findByUrlAndUser(params.url, user);
		oi.delete();
		redirect(action: params.redirect, params:[id: params.userId])
	}
	
	def verifyAndSaveOpenId = {
		println params.openId
		render params.openId
	}
	
	def showUser = {
		if(params.id!=null) {
			def loggedUser = injectUserProfile()
			def user = usersManagementService.getUser(params.id);
			if(user!=null) {
				if(user.id==loggedUser.id) {
					render (view:'/dashboard/showUser',
						model:[menuitem: 'showUser',
							loggedUser:loggedUser, loggedUserRoles: usersManagementService.getUserRoles(loggedUser),
							user: user,
							userRoles: usersManagementService.getUserRoles(user),
							userGroups: usersManagementService.getUserGroups(user),
							userCircles: usersManagementService.getUserCircles(user),
							userCommunities: usersManagementService.getUserCommunities(user),
							appBaseUrl: request.getContextPath()]);
				} else {
					redirect (action: 'index')
				}
			} else {
				render (view:'error', model:[message: "User not found for id: "+params.id]);
			}
		} else {
			render (view:'error', model:[message: "User id not defined!"]);
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
	
	def showGroups = {
		def loggedUser = injectUserProfile()
		def user = User.findById(params.id)
		
		if (!params.max) params.max = 15
		if (!params.offset) params.offset = 0
		if (!params.sort) params.sort = "name"
		if (!params.order) params.order = "asc"

		def results = usersManagementService.listUserGroups(user, params.max, params.offset, params.sort, params.order);
		
		render (view:'/dashboard/showGroups', 
			model:[menuitem: 'showGroups',
				loggedUser:loggedUser, loggedUserRoles: usersManagementService.getUserRoles(loggedUser),
				user: user, 
				"usergroups" : results, "groupsTotal": Group.count()])
	}
	
	def showGroup = {
		def loggedUser = injectUserProfile()
		def group = Group.findById(params.id)
		render (view:'showGroup', 
			model:[menuitem: 'showGroups',
				loggedUser:loggedUser, loggedUserRoles: usersManagementService.getUserRoles(loggedUser),
				usergroup: UserGroup.findByUserAndGroup(loggedUser, group), group: group,
				appBaseUrl: request.getContextPath()])
	}
	
	def availableGroups = {
		def loggedUser = injectUserProfile()
		def user = User.findById(params.id)
		render (view:'availableGroups', 
			model:[menuitem : "availableGroups", 
				loggedUser:loggedUser, loggedUserRoles: usersManagementService.getUserRoles(loggedUser),
				user: user]);
	}
	
	def enrollUserInGroup = {
		def loggedUser = injectUserProfile()
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
			
		if(params.mode=='close') 
			redirect(action:'showProfile', params: [id: loggedUser.id]);
		else	
			redirect(action:'availableGroups', params: [id: loggedUser.id]);
	}
	
	def lockUserInGroup = {
		def user = User.findById(params.user)
		def group = Group.findById(params.id)
		def usergroup = UserGroup.findByUserAndGroup(user, group);
		if(usergroup!=null) {
			usergroup.status = UserStatusInGroup.findByValue(DefaultUserStatusInGroup.LOCKED.value())
		}

		if(params.redirect)
			redirect(action:params.redirect, params: [id: user.id])
		else
			render (view:'showProfile', model:[item: user])
	}
	
	def unlockUserinGroup = {
		def user = User.findById(params.user)
		def group = Group.findById(params.id)
		def usergroup = UserGroup.findByUserAndGroup(user, group);
		if(usergroup!=null) {
			usergroup.status = UserStatusInGroup.findByValue(DefaultUserStatusInGroup.ACTIVE.value())
		}
		if(params.redirect)
			redirect(action:params.redirect, params: [id: user.id])
		else
			redirect(action:'showProfile', model:[item: user]);
	}
	
	def enableUserInGroup = {
		def user = User.findById(params.user)
		def group = Group.findById(params.id)
		def usergroup = UserGroup.findByUserAndGroup(user, group);
		if(usergroup!=null) {
			usergroup.status = UserStatusInGroup.findByValue(DefaultUserStatusInGroup.ACTIVE.value())
		}
		if(params.redirect)
			redirect(action:params.redirect, params: [id: user.id])
		else
			redirect(action:'showProfile', model:[item: user]);
	}
	
	def disableUserInGroup = {
		def user = User.findById(params.user)
		def group = Group.findById(params.id)
		def usergroup = UserGroup.findByUserAndGroup(user, group);
		if(usergroup!=null) {
			usergroup.status = UserStatusInGroup.findByValue(DefaultUserStatusInGroup.SUSPENDED.value())
		}
		if(params.redirect)
			redirect(action:params.redirect, params: [id: user.id])
		else
			redirect(action:'showProfile', model:[item: user]);
	}
	
	def removeUserFromGroup = {
		def user = User.findById(params.user)
		def group = Group.findById(params.id)
		def usergroup = UserGroup.findByUserAndGroup(user, group);
		if(usergroup!=null) {
			usergroup.delete()
		}
		if(params.redirect)
			redirect(action:params.redirect, params: [id: user.id])
		else
			redirect(action:'showProfile', model:[item: user]);
	}
	
	//???
	def leaveGroup = {
		def group = Group.findById(params.id)
		
		def principal = springSecurityService.principal
		String username = principal.username
		def user = User.findByUsername(username);
		
		def ug = UserGroup.findByUserAndGroup(user, group);
		if(ug!=null) {
			ug.status = UserStatusInGroup.findByValue(DefaultUserStatusInGroup.SUSPENDED.value())
		}
		
		if(params.redirect)
			redirect(action:params.redirect)
		else
			render (view:'showProfile', model:[item: user])
	}
	
	def searchGroup = {
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
		def ug = UserGroup.findAllByUser(user)
		groups.each { groupItem ->
			boolean alreadyMember = false;
			for(int i=0; i<ug.size(); i++) {
				if(ug.get(i).group.id==groupItem.id)
					alreadyMember = true;
			}
			def groupResult = [id:groupItem.id, name:groupItem.name, shortName: groupItem.shortName,
					description: groupItem.description, status: groupItem.statusLabel, 
					dateCreated: groupItem.dateCreated, member: alreadyMember]
			groupsResults << groupResult
		}

		def paginationResults = ['offset':params.offset+params.max, 'sort':params.sort, 'order':params.order]


		def results = [groups: groupsResults, pagination: paginationResults, groupsCount: groupsCount]
		render results as JSON
	}
	
	def showCircles = {
		def loggedUser = injectUserProfile()
		def user = loggedUser
		
		if (!params.max) params.max = 15
		if (!params.offset) params.offset = 0
		if (!params.sort) params.sort = "name"
		if (!params.order) params.order = "asc"

		def results = UserCircle.findAllByUser(user);
		
		//def results = usersManagementService.listUserGroups(user, params.max, params.offset, params.sort, params.order);
		
		/*
		render (view:'/dashboard/showGroups',
			model:[menuitem: 'showGroups',
				loggedUser:loggedUser, loggedUserRoles: usersManagementService.getUserRoles(loggedUser),
				user: user,
				"usergroups" : results, "groupsTotal": Group.count()])
				*/
		
		render (view:'/dashboard/showCircles', model:[menuitem: 'showCircles',
				loggedUser:loggedUser, loggedUserRoles: usersManagementService.getUserRoles(loggedUser),
				user: user, userCircles: results, "circlesTotal": 0])
	}
	
	def showCircle = {
		def loggedUser = injectUserProfile()
		def circle = Circle.findById(params.id)
		render (view:'/dashboard/showCircle',
			model:[menuitem: 'showCircles',
				loggedUser:loggedUser, loggedUserRoles: usersManagementService.getUserRoles(loggedUser),
				usercircle: UserCircle.findByUserAndCircle(loggedUser, circle), circle: circle,
				appBaseUrl: request.getContextPath()])
	}
	
    def exportOptions = {
        def loggedUser = injectUserProfile();
        def user = loggedUser;
        render (view:'/dashboard/exportOptions',
            model:[menuitem: 'export',
                loggedUser:loggedUser, loggedUserRoles: usersManagementService.getUserRoles(loggedUser),
                user: user]);
    }
	
// Old code, to be removed at some point
	
//	def dashboardAdmin = {
//		def user = injectUserProfile()
//		if(user!=null) render (view:'/shared/dashboardAdmin', model:[user: user, userRoles: usersManagementService.getUserRoles(user)]);
//	}
//
//	// ----------------------------------------------------
//	//  USERS MANAGEMENT
//	// ----------------------------------------------------
//	def listUsers = {
//		def user = injectUserProfile()
//
//		if (!params.max) params.max = 10
//		if (!params.offset) params.offset = 0
//		if (!params.sort) params.sort = "username"
//		if (!params.order) params.order = "asc"
//
//		def users = usersManagementService.listUsers(user, params.max, params.offset, params.sort, params.order);
//
//		render (view:'listUsers', model:[user: user, "users" : users, "usersTotal": User.count(), "usersroles": UserRole.list(), "roles" : Role.list(), "menuitem" : "listUsers"])
//	}
//
//	
//
//	def createUser = {
//		render (view:'createUser',  model:[action: "create", roles: Role.list(), defaultRole: DefaultRoles.USER, "menuitem" : "createUser"]);
//	}
//
//	def saveUser = {UserCreateCommand userCreateCmd->
//		if(userCreateCmd.hasErrors()) {
//			userCreateCmd.errors.allErrors.each { println it }
//			render(view:'createUser', model:[item:userCreateCmd, roles: Role.list(),
//						defaultRole: Role.findByAuthority("ROLE_USER")])
//		} else {
//			def user = userCreateCmd.createUser()
//			if(user)  {
//				if(!user.save()) {
//					// Failure in saving
//					user.errors.allErrors.each { println it }
//					render(view:'createUser', model:[item:userCreateCmd, roles: Role.list(),
//								msgError: 'The user has not been saved successfully'])
//				} else {
//					updateUserRole(user, Role.findByAuthority(DefaultRoles.ADMIN.value()), params.Admin)
//					updateUserRole(user, Role.findByAuthority(DefaultRoles.MANAGER.value()), params.Manager)
//					updateUserRole(user, Role.findByAuthority(DefaultRoles.USER.value()), params.User)
//
//					redirect (action:'showProfile',id: user.id, model: [
//								msgSuccess: 'Group saved successfully']);
//				}
//			} else {
//				// User already existing
//				render(view:'createUser', model:[item:userCreateCmd, roles: Role.list(),
//							msgError: 'A user with this name is already existing'])
//			}
//		}
//	}
//
//	/*
//	def editUser = {
//		def user = User.findById(params.id)
//
//		render (view:'editUser', model:[item: user, userRoles: getUserRoles(user), roles: Role.list(), userGroups: getUserGroups(user), action: "edit",
//					groupRoles: listOfGroupRoles(), groupStatus: listOfUserGroupStatus()])
//	}
//	*/
//
//	public def listOfGroupRoles() {
//		def roles = []
//		roles.add(DefaultGroupRoles.ADMIN)
//		roles.add(DefaultGroupRoles.MANAGER)
//		roles.add(DefaultGroupRoles.USER)
//		roles
//	}
//
//	public def listOfUserGroupStatus() {
//		def roles = []
//		roles.add(DefaultGroupStatus.ACTIVE)
//		roles.add(DefaultGroupStatus.DISABLED)
//		roles.add(DefaultGroupStatus.DELETED)
//		roles
//	}
//
//	/*@Secured(['ROLE_ADMIN'])*/
//	def updateUser = { UserEditCommand userEditCmd ->
//		if(userEditCmd.hasErrors()) {
//			userEditCmd.errors.allErrors.each { println it }
//			render(view:'editProfile', model:[item:userEditCmd])
//		} else {
//			def user = User.findById(params.id)
//			user.firstName = userEditCmd.firstName
//			user.lastName = userEditCmd.lastName
//			user.displayName = userEditCmd.displayName
//			user.email = userEditCmd.email
//
//			updateUserRole(user, Role.findByAuthority(DefaultRoles.ADMIN.value()), params.Admin)
//			updateUserRole(user, Role.findByAuthority(DefaultRoles.MANAGER.value()), params.Manager)
//			updateUserRole(user, Role.findByAuthority(DefaultRoles.USER.value()), params.User)
//
//			updateUserStatus(user, params.status)
//
//			render (view:'showProfile', model:[item: user, userRoles: getUserRoles(user)])
//		}
//
//	}
//
//	def getUserRoles(def user) {
//		def userRoles = []
//		def ur = UserRole.findAllByUser(user)
//		println ur
//		ur.each { userRoles.add(it.role)}
//		return userRoles
//	}
//
//	def getUserGroups(def user) {
//		def ur = []
//		def userGroups = []
//		ur = UserGroup.findAllByUser(user)
//		ur.each { userGroups.add(it.group)}
//		return ur
//	}
//
//	def updateUserRole(def user, def role, def value) {
//		if(value=='on') {
//			UserRole ur = UserRole.create(user, role)
//			ur.save(flush:true)
//		} else {
//			def ur = UserRole.findByUserAndRole(user, role)
//			if(ur!=null) {
//				ur.delete(flush:true)
//			}
//		}
//	}
//
//	def updateUserStatus(def user, def status) {
//		println status
//		if(status==UserStatus.CREATED_USER.value()) {
//			user.enabled = false
//			user.accountExpired = false
//			user.accountLocked = false
//		} else if(status==UserStatus.ACTIVE_USER.value()) {
//			user.enabled = true
//			user.accountExpired = false
//			user.accountLocked = false
//		} else if(status==UserStatus.DISABLED_USER.value()) {
//			user.enabled = false
//			user.accountExpired = false
//			user.accountLocked = false
//		} else if(status==UserStatus.LOCKED_USER.value()) {
//			user.enabled = true
//			user.accountExpired = false
//			user.accountLocked = true
//		}
//	}
//
//	def searchUserForm = {
//		render (view:'searchUser', model:["menuitem" : "searchUser"]);
//	}
//
//	def searchUser = {
//		def user = injectUserProfile()
//
//		if (!params.max) params.max = 1
//		if (!params.offset) params.offset = 0
//		if (!params.sort) params.sort = "username"
//		if (!params.order) params.order = "asc"
//
//		//TODO fix pagination
//		def users = [];
//		if (params.sort == 'status') {
//			def buffer = [];
//			def usersStatus = [:]
//			User.list().each { auser ->
//				usersStatus.put (auser.id, auser.status)
//			}
//			usersStatus = usersStatus.sort{ a, b -> a.value.compareTo(b.value) }
//			if(params.order == "desc")
//				usersStatus.each { userStatus ->
//					buffer.add(User.findById(userStatus.key));
//				}
//			else
//				usersStatus.reverseEach { userStatus ->
//					buffer.add(User.findById(userStatus.key));
//				}
//
//			int offset = (params.offset instanceof String) ? Integer.parseInt(params.offset) : params.offset
//			int max = (params.max instanceof String) ? Integer.parseInt(params.max) : params.max
//			for(int i=offset;i< Math.min(offset+max, usersStatus.size()); i++) {
//				users.add(buffer[i]);
//			}
//		} else if (params.sort == 'isAdmin' || params.sort == 'isAnalyst' || params.sort == 'isManager'
//		|| params.sort == 'isCurator' || params.sort == 'isUser') {
//
//		} else if (params.sort == 'name') {
//			def buffer = [];
//			def usersNames = [:]
//			User.list().each { auser ->
//				usersNames.put (auser.id, auser.name)
//			}
//			usersNames = usersNames.sort{ a, b -> a.value.compareTo(b.value) }
//			if(params.order == "desc")
//				usersNames.each { userName ->
//					buffer.add(User.findById(userName.key));
//				}
//			else
//				usersNames.reverseEach { userName ->
//					buffer.add(User.findById(userName.key));
//				}
//			int offset = (params.offset instanceof String) ? Integer.parseInt(params.offset) : params.offset
//			int max = (params.max instanceof String) ? Integer.parseInt(params.max) : params.max
//			for(int i=offset;i< Math.min(offset+max, usersNames.size()); i++) {
//				users.add(buffer[i]);
//			}
//		} else {
//			// Search with no ordering
//			def userStatusCriteria = User.createCriteria();
//			def r = [];
//			if(params.firstName!=null && params.firstName.trim().length()>0 &&
//			params.lastName!=null && params.lastName.trim().length()>0) {
//				r = userStatusCriteria.list {
//					maxResults(params.max?.toInteger())
//					firstResult(params.offset?.toInteger())
//					order(params.sort, params.order)
//					and {
//						like('firstName', params.firstName)
//						like('lastName', params.lastName)
//					}
//				}
//			} else if(params.firstName!=null && params.firstName.trim().length()>0 &&
//			(params.lastName==null || params.lastName.trim().length()==0)) {
//				r = userStatusCriteria.list {
//					maxResults(params.max?.toInteger())
//					firstResult(params.offset?.toInteger())
//					order(params.sort, params.order)
//					like('firstName', params.firstName)
//				}
//			} else if((params.firstName==null || params.firstName.trim().length()==0) &&
//			params.lastName!=null &&  params.lastName.trim().length()>0) {
//				r = userStatusCriteria.list {
//					maxResults(params.max?.toInteger())
//					firstResult(params.offset?.toInteger())
//					order(params.sort, params.order)
//					like('lastName', params.lastName)
//				}
//			} else if(params.displayName!=null && params.displayName.trim().length()>0) {
//				r = userStatusCriteria.list {
//					maxResults(params.max?.toInteger())
//					firstResult(params.offset?.toInteger())
//					order(params.sort, params.order)
//					like('displayName', params.displayName)
//				}
//			}
//			users = r.toList();
//		}
//
//		def usersResults = []
//		users.each { userItem ->
//			def userResult = [id:userItem.id, username:userItem.username, name: userItem.getName(),
//						displayName: userItem.getDisplayName(),
//						isAdmin: userItem.getIsAdmin(), isManager: userItem.getIsManager(), isUser: userItem.getIsUser(),
//						status: userItem.getStatus(), dateCreated: userItem.dateCreated]
//			usersResults << userResult
//		}
//
//		def paginationResults = ['offset':params.offset+params.max, 'sort':params.sort, 'order':params.order]
//
//
//		def results = [users: usersResults, pagination: paginationResults]
//		render results as JSON
//	}
//
//	def listRoles = {
//		def user = injectUserProfile()
//
//		if (!params.max) params.max = 15
//		if (!params.offset) params.offset = 0
//		if (!params.sort) params.sort = "authority"
//		if (!params.order) params.order = "asc"
//
//		def results = usersManagementService.listRoles(user, params.max, params.offset, params.sort, params.order);
//
//		render (view:'listRoles', model:[user : user, "roles" : results[0], "rolesTotal": Role.count(), "rolesCount": results[1], "menuitem" : "listRoles"])
//	}
//
//	def listRoleUsers = {
//		def user = injectUserProfile()
//
//		if (!params.max) params.max = 15
//		if (!params.offset) params.offset = 0
//		if (!params.sort) params.sort = "username"
//		if (!params.order) params.order = "asc"
//
//		//TODO fix pagination
//		def users = [];
//		if (params.sort == 'status') {
//			def buffer = [];
//			def usersStatus = [:]
//			User.list().each { auser ->
//				usersStatus.put (auser.id, auser.status)
//			}
//			usersStatus = usersStatus.sort{ a, b -> a.value.compareTo(b.value) }
//			if(params.order == "desc")
//				usersStatus.each { userStatus ->
//					buffer.add(User.findById(userStatus.key));
//				}
//			else
//				usersStatus.reverseEach { userStatus ->
//					buffer.add(User.findById(userStatus.key));
//				}
//
//			int offset = (params.offset instanceof String) ? Integer.parseInt(params.offset) : params.offset
//			int max = (params.max instanceof String) ? Integer.parseInt(params.max) : params.max
//			for(int i=offset;i< Math.min(offset+max, usersStatus.size()); i++) {
//				users.add(buffer[i]);
//			}
//		} else if (params.sort == 'isAdmin' || params.sort == 'isAnalyst' || params.sort == 'isManager'
//		|| params.sort == 'isCurator' || params.sort == 'isUser') {
//
//		} else if (params.sort == 'name') {
//			def buffer = [];
//			def usersNames = [:]
//			User.list().each { auser ->
//				usersNames.put (auser.id, auser.name)
//			}
//			usersNames = usersNames.sort{ a, b -> a.value.compareTo(b.value) }
//			if(params.order == "desc")
//				usersNames.each { userName ->
//					buffer.add(User.findById(userName.key));
//				}
//			else
//				usersNames.reverseEach { userName ->
//					buffer.add(User.findById(userName.key));
//				}
//			int offset = (params.offset instanceof String) ? Integer.parseInt(params.offset) : params.offset
//			int max = (params.max instanceof String) ? Integer.parseInt(params.max) : params.max
//			for(int i=offset;i< Math.min(offset+max, usersNames.size()); i++) {
//				users.add(buffer[i]);
//			}
//		} else {
//			//def userRoles = UserRole.findAllByRole(Role.findById(params.id));
//			//userRoles.each {
//			//	users.add it.user;
//			//}
//		}
//
//		def role = Role.findById(params.id)
//		users = UserRole.executeQuery("select user from UserRole as userrole join userrole.role as role join userrole.user as user where userrole.role =:role",['role':role]);
//		// Missing ordering and pagination
//		// http://docs.jboss.org/hibernate/orm/3.3/reference/en/html/queryhql.html#queryhql-expressions
//
//		render (view:'listUsers', model:[user: user, "users" : users, "usersTotal": User.count(), "usersroles": UserRole.list(), "roles" : Role.list(),
//					"menuitem" : "listUsers", role: role])
//	}
//
//	/*
//	def lockUser = {
//		def user = User.findById(params.id)
//		user.accountLocked = true
//		user.enabled = true;
//		if(params.redirect)
//			redirect(action:params.redirect)
//		else
//			render (view:'showProfile', model:[item: user])
//	}
//
//
//	def unlockUser = {
//		def user = User.findById(params.id)
//		user.accountLocked = false
//		user.enabled = true;
//		if(params.redirect)
//			redirect(action:params.redirect)
//		else
//			render (view:'showProfile', model:[item: user])
//	}
//
//	def enableUser = {
//		def user = User.findById(params.id)
//		user.enabled = true
//		user.accountLocked = false
//		if(params.redirect)
//			redirect(action:params.redirect)
//		else
//			render (view:'showProfile', model:[item: user])
//	}
//
//	def disableUser = {
//		def user = User.findById(params.id)
//		user.enabled = false
//		user.accountLocked = false
//		if(params.redirect)
//			redirect(action:params.redirect)
//		else
//			render (view:'showProfile', model:[item: user])
//	}
//	*/
//	
//	def manageUserGroups = {
//		def user = User.findById(params.id)
//		
//		if (!params.max) params.max = 15
//		if (!params.offset) params.offset = 0
//		if (!params.sort) params.sort = "name"
//		if (!params.order) params.order = "asc"
//
//		def results = usersManagementService.listUserGroups(user, params.max, params.offset, params.sort, params.order);
//
//		render (view:'manageGroups', model:["usergroups" : results, "groupsTotal": Group.count(), "menuitem" : "listGroups", "user": user])
//	}
//	
//	/*
//	def enrollUserInGroup = {
//		def user = User.findById(params.user)
//		def group = Group.findById(params.group)
//		
//		def ug = new UserGroup(user:user, group:group,
//			status: UserStatusInGroup.findByValue(DefaultUserStatusInGroup.ACTIVE.value()));
//		
//		if(!ug.save(flush: true)) {
//			ug.errors.allErrors.each { println it }
//		} else {
//			ug.roles = []
//			ug.roles.add GroupRole.findByAuthority(DefaultGroupRoles.USER.value())
//		}
//			
//		redirect(action:'showProfile', params: [id: params.user]);
//	}
//	*/
//	
//	
//	/*
//	def lockUserInGroup = {
//		def user = User.findById(params.user)
//		def group = Group.findById(params.id)
//		def usergroup = UserGroup.findByUserAndGroup(user, group);
//		if(usergroup!=null) {
//			usergroup.status = UserStatusInGroup.findByValue(DefaultUserStatusInGroup.LOCKED.value())
//		}
//		if(params.redirect)
//			redirect(action:params.redirect)
//		else
//			render (view:'showProfile', model:[item: user])
//	}
//	
//	def unlockUserinGroup = {
//		def user = User.findById(params.user)
//		def group = Group.findById(params.id)
//		def usergroup = UserGroup.findByUserAndGroup(user, group);
//		if(usergroup!=null) {
//			usergroup.status = UserStatusInGroup.findByValue(DefaultUserStatusInGroup.ACTIVE.value())
//		}
//		if(params.redirect)
//			redirect(action:params.redirect)
//		else
//			render (view:'showProfile', model:[item: user])
//	}
//	
//	def enableUserInGroup = {
//		def user = User.findById(params.user)
//		def group = Group.findById(params.id)
//		def usergroup = UserGroup.findByUserAndGroup(user, group);
//		if(usergroup!=null) {
//			usergroup.status = UserStatusInGroup.findByValue(DefaultUserStatusInGroup.ACTIVE.value())
//		}
//		if(params.redirect)
//			redirect(action:params.redirect)
//		else
//			render (view:'showProfile', model:[item: user])
//	}
//	
//	def disableUserInGroup = {
//		def user = User.findById(params.user)
//		def group = Group.findById(params.id)
//		def usergroup = UserGroup.findByUserAndGroup(user, group);
//		if(usergroup!=null) {
//			usergroup.status = UserStatusInGroup.findByValue(DefaultUserStatusInGroup.SUSPENDED.value())
//		}
//		if(params.redirect)
//			redirect(action:params.redirect)
//		else
//			render (view:'showProfile', model:[item: user])
//	}
//	
//	def removeUserFromGroup = {
//		def user = User.findById(params.user)
//		def group = Group.findById(params.id)
//		def usergroup = UserGroup.findByUserAndGroup(user, group);
//		if(usergroup!=null) {
//			usergroup.delete()
//		}
//		if(params.redirect)
//			redirect(action:params.redirect)
//		else
//			render (view:'showProfile', model:[item: user])
//	}
//	
//	def leaveGroup = {
//		def group = Group.findById(params.id)
//		
//		def principal = springSecurityService.principal
//		String username = principal.username
//		def user = User.findByUsername(username);
//		
//		def ug = UserGroup.findByUserAndGroup(user, group);
//		if(ug!=null) { 
//			ug.status = UserStatusInGroup.findByValue(DefaultUserStatusInGroup.SUSPENDED.value())
//		}
//		
//		if(params.redirect)
//			redirect(action:params.redirect)
//		else
//			render (view:'showProfile', model:[item: user])
//	}
//*/
//	
//	def editUserRoleInGroup = {
//		def user = User.findById(params.user)
//		def group = Group.findById(params.id)
//		
//		def ug = UserGroup.findByUserAndGroup(user, group)
//		
//		render (view:'editUserInGroup', model:[action: "edit", usergroup: ug, userRoles: GroupRole.list()])
//	}
//	
//	def updateUserInGroup = {
//		def user = User.findById(params.user)
//		def group = Group.findById(params.group)
//		
//		def ug = UserGroup.findByUserAndGroup(user, group)
//		ug.roles = []
//		
//		updateUserInGroupRole(ug, GroupRole.findByAuthority(DefaultGroupRoles.ADMIN.value()), params.Admin)
//		updateUserInGroupRole(ug, GroupRole.findByAuthority(DefaultGroupRoles.MANAGER.value()), params.Manager)
//		updateUserInGroupRole(ug, GroupRole.findByAuthority(DefaultGroupRoles.CURATOR.value()), params.Curator)
//		updateUserInGroupRole(ug, GroupRole.findByAuthority(DefaultGroupRoles.USER.value()), params.User)
//		
//		if(params.redirect)
//			redirect(action:params.redirect)
//		else
//			render (view:'showProfile', model:[item: user])
//	}
//	
//	def updateUserInGroupRole(def userGroup, def role, def value) {
//		if(value=='on') {
//			userGroup.roles.add role
//		} 
//	}
//	
//	def getUserRolesInGroup(def user) {
//		// UserInGroupRole (multiple roles are allowed)
//		def userRoles = []
//		def ur = UserRole.findAllByUser(user)
//		println ur
//		ur.each { userRoles.add(it.role)}
//		return userRoles
//	}
//	
//	/*
//	def resetUserPassword = {
//		def user = User.findById(params.user)
//		
//		render (view:'resetUserPassword', model: [user: user]);
//	}
//	
//	def saveUserPassword = {UserResetPasswordCommand userResetPasswordCommand->
//		def user = User.findById(params.user)
//		if(userResetPasswordCommand.hasErrors()) {
//			userResetPasswordCommand.errors.allErrors.each { println it }
//			render(view:'resetUserPassword', model:[user: user, item:userResetPasswordCommand,
//				,msgError: 'The password has not been saved successfully'])
//		} else {
//			user.password = springSecurityService.encodePassword(userResetPasswordCommand.password);
//			
//			redirect (action:'showProfile',id: user.id, model: [
//						msgSuccess: 'Passowrd saved successfully']);
//		} 
//	}
//	*/
//	
//	// ----------------------------------------------------
//	//  GROUPS MANAGEMENT
//	// ----------------------------------------------------
//	def listGroups = {
//		if (!params.max) params.max = 15
//		if (!params.offset) params.offset = 0
//		if (!params.sort) params.sort = "groupsCount"
//		if (!params.order) params.order = "asc"
//
//		def results = usersManagementService.listGroups(params.max, params.offset, params.sort, params.order);
//
//		render (view:'listGroups', model:["groups" : results[0], "groupsTotal": Group.count(), "groupsCount": results[1], "menuitem" : "listGroups"])
//	}
//
//	def deleteGroup = { GroupCreateCommand groupCreateCmd ->
//		def group = Group.findById(params.id)
//		group.delete()
//		redirect (action:'listGroups')
//	}
//
//	/*
//	def showGroup = {
//		def group = Group.findById(params.id)
//		render (view:'showGroup', model:[item: group])
//	}
//	*/
//
//	def searchGroupForm = {
//		render (view:'searchGroup', model:["menuitem" : "searchGroup"]);
//	}
//
//	def searchGroup = {
//		def user = injectUserProfile()
//
//		if (!params.max) params.max = 15
//		if (!params.offset) params.offset = 0
//		if (!params.sort) params.sort = "name"
//		if (!params.order) params.order = "asc"
//
//		def groups = [];
//		def groupsCount = [:]
//		Group.list().each { agroup ->
//			groupsCount.put (agroup.id, UserGroup.findAllWhere(group: agroup).size())
//		}
//		def groupsStatus = [:]
//		Group.list().each { agroup ->
//			groupsStatus.put (agroup.id, agroup.status)
//		}
//		/*
//		 if (params.sort == 'groupsCount') {
//		 groupsCount = groupsCount.sort{ a, b -> a.value <=> b.value }
//		 if(params.order == "desc")
//		 groupsCount.each { groupCount ->
//		 groups.add Group.findById(groupCount.key);
//		 }
//		 else
//		 groupsCount.reverseEach { groupCount ->
//		 groups.add Group.findById(groupCount.key);
//		 }
//		 } else if (params.sort == 'status') {
//		 groupsStatus = groupsStatus.sort{ a, b -> a.value.compareTo(b.value) }
//		 if(params.order == "desc")
//		 groupsStatus.each { groupStatus ->
//		 groups.add Group.findById(groupStatus.key);
//		 }
//		 else
//		 groupsStatus.reverseEach { groupStatus ->
//		 groups.add Group.findById(groupStatus.key);
//		 }
//		 } else {*/
//		// Search with no ordering
//		def groupCriteria = Group.createCriteria();
//		def r = [];
//
//		if(params.name!=null && params.name.trim().length()>0 &&
//		params.shortName!=null && params.shortName.trim().length()>0) {
//			println 'case 1'
//			r = groupCriteria.list {
//				maxResults(params.max?.toInteger())
//				firstResult(params.offset?.toInteger())
//				order(params.sort, params.order)
//				and {
//					like('name', params.name)
//					like('shortName', params.shortName)
//				}
//			}
//		} else if(params.name!=null && params.name.trim().length()>0 &&
//		(params.shortName==null || params.shortName.trim().length()==0)) {
//			println 'case 2'
//			r = groupCriteria.list {
//				maxResults(params.max?.toInteger())
//				firstResult(params.offset?.toInteger())
//				order(params.sort, params.order)
//				like('name', params.name)
//			}
//		} else if((params.name==null || params.name.trim().length()==0) &&
//		params.shortName!=null &&  params.shortName.trim().length()>0) {
//			println 'case 3'
//			r = groupCriteria.list {
//				maxResults(params.max?.toInteger())
//				firstResult(params.offset?.toInteger())
//				order(params.sort, params.order)
//				like('shortName', params.shortName)
//			}
//		} else {
//			println 'case 4'
//			r = groupCriteria.list {
//				maxResults(params.max?.toInteger())
//				firstResult(params.offset?.toInteger())
//				order(params.sort, params.order)
//			}
//		}
//		groups = r.toList();
//		//}
//
//
//		def groupsResults = []
//		groups.each { groupItem ->
//			def groupResult = [id:groupItem.id, name:groupItem.name, shortName: groupItem.shortName,
//						description: groupItem.description, status: groupItem.statusLabel, dateCreated: groupItem.dateCreated]
//			groupsResults << groupResult
//		}
//
//		def paginationResults = ['offset':params.offset+params.max, 'sort':params.sort, 'order':params.order]
//
//
//		def results = [groups: groupsResults, pagination: paginationResults, groupsCount: groupsCount]
//		render results as JSON
//	}
//
//	def editGroup = {
//		def group = Group.findById(params.id)
//		render (view:'editGroup', model:[item: group, action: "edit"])
//	}
//
//	def updateGroup = { GroupEditCommand groupEditCmd ->
//		if(groupEditCmd.hasErrors()) {
//			/* groupCreateCmd.errors.allErrors.each { println it } */
//			render(view:'editGroup', model:[item:groupCreateCmd])
//		} else {
//			def group = Group.findById(params.id)
//			group.name = groupEditCmd.name
//			group.shortName = groupEditCmd.shortName
//			group.description = groupEditCmd.description
//
//			if(groupEditCmd.privacy.equals(DefaultGroupPrivacy.PRIVATE.value())) {
//				group.privacy = GroupPrivacy.findByValue(DefaultGroupPrivacy.PRIVATE.value());
//			} else if(groupEditCmd.privacy.equals(DefaultGroupPrivacy.RESTRICTED.value())) {
//				group.privacy = GroupPrivacy.findByValue(DefaultGroupPrivacy.RESTRICTED.value());
//			} else if(groupEditCmd.privacy.equals(DefaultGroupPrivacy.PUBLIC.value())) {
//				group.privacy = GroupPrivacy.findByValue(DefaultGroupPrivacy.PUBLIC.value());
//			}
//
//			if(groupEditCmd.status.equals(DefaultGroupStatus.ACTIVE.value())) {
//				group.enabled = true
//				group.locked = false
//			} else if(groupEditCmd.status.equals(DefaultGroupStatus.DISABLED.value())) {
//				group.enabled = false
//				group.locked = false
//			} else if(groupEditCmd.status.equals(DefaultGroupStatus.LOCKED.value())) {
//				group.enabled = true
//				group.locked = true
//			}
//
//			render (view:'showGroup', model:[item: group])
//		}
//	}
//
//	def createGroup = {
//		render (view:'createGroup',  model:[action: "create", "menuitem" : "createGroup"]);
//	}
//
//	def saveGroup = {GroupCreateCommand groupCreateCmd->
//		if(groupCreateCmd.hasErrors()) {
//			groupCreateCmd.errors.allErrors.each { println it }
//			render(view:'createUser', model:[item:groupCreateCmd, roles: Role.list(),
//						defaultRole: Role.findByAuthority("ROLE_USER")])
//		} else {
//			def group = groupCreateCmd.createGroup()
//			if(group)  {
//				updateGroupPrivacy(group, groupCreateCmd.privacy);
//				updateGroupStatus(group, groupCreateCmd.status);
//
//				if(!group.save()) {
//					// Failure in saving
//					group.errors.allErrors.each { println it }
//					render(view:'createGroup', model:[item:groupCreateCmd,
//								msgError: 'The group has not been saved successfully'])
//				} else {
//					redirect (action:'showGroup', id: group.id, model: [
//								msgSuccess: 'Group saved successfully']);
//				}
//			} else {
//				// User already existing
//				render(view:'createGroup', model:[item:groupCreateCmd,
//							msgError: 'A group with this name is already existing'])
//			}
//		}
//	}
//
//	def updateGroupPrivacy(def group, def privacy) {
//		if(privacy==DefaultGroupPrivacy.PRIVATE.value()) {
//			group.privacy = GroupPrivacy.findByValue(DefaultGroupPrivacy.PRIVATE.value());
//		} else if(privacy==DefaultGroupPrivacy.RESTRICTED.value()) {
//			group.privacy = GroupPrivacy.findByValue(DefaultGroupPrivacy.RESTRICTED.value());
//		} else if(privacy==DefaultGroupPrivacy.PUBLIC.value()) {
//			group.privacy = GroupPrivacy.findByValue(DefaultGroupPrivacy.PUBLIC.value());
//		}
//	}
//
//	def updateGroupStatus(def group, def status) {
//		if(status.equals(DefaultGroupStatus.ACTIVE.value())) {
//			group.enabled = true
//			group.locked = false
//		} else if(status.equals(DefaultGroupStatus.DISABLED.value())) {
//			group.enabled = false
//			group.locked = false
//		} else if(status.equals(DefaultGroupStatus.LOCKED.value())) {
//			group.enabled = true
//			group.locked = true
//		}
//	}
//
//	def lockGroup = {
//		def group = Group.findById(params.id)
//		group.locked = true
//		group.enabled = true;
//		if(params.redirect)
//			redirect(action:params.redirect)
//		else
//			render (view:'showGroup', model:[item: group])
//	}
//
//
//	def unlockGroup = {
//		def group = Group.findById(params.id)
//		group.locked = false
//		group.enabled = true;
//		if(params.redirect)
//			redirect(action:params.redirect)
//		else
//			render (view:'showGroup', model:[item: group])
//	}
//
//	def enableGroup = {
//		def group = Group.findById(params.id)
//		group.enabled = true
//		group.locked = false
//		if(params.redirect)
//			redirect(action:params.redirect)
//		else
//			render (view:'showGroup', model:[item: group])
//	}
//
//	def disableGroup = {
//		def group = Group.findById(params.id)
//		group.enabled = false
//		group.locked = false
//		if(params.redirect)
//			redirect(action:params.redirect)
//		else
//			render (view:'showGroup', model:[item: group])
//	}
//	
//	def listGroupUsers = {
//		def user = injectUserProfile()
//		def group = Group.findById(params.id)
//
//		if (!params.max) params.max = 10
//		if (!params.offset) params.offset = 0
//		if (!params.sort) params.sort = "username"
//		if (!params.order) params.order = "asc"
//
//		def users = usersManagementService.listGroupUsers(group, params.max, params.offset, params.sort, params.order);
//
//		render (view:'listGroupUsers', model:[group: group, "users" : users, "usersTotal": User.count(), "usersroles": UserRole.list(), "roles" : Role.list(), "menuitem" : "listUsers"])
//	}
//	
//	def moderateAccountsRequests = {
//		def user = injectUserProfile()
//		
//		if (!params.max) params.max = 10
//		if (!params.offset) params.offset = 0
//		if (!params.sort) params.sort = "username"
//		if (!params.order) params.order = "asc"
//	
//		def users = usersManagementService.moderateAccountRequests(user, params.max, params.offset, params.sort, params.order);
//	
//		render (view:'moderateAccountRequests', model:[user: user, "users" : users, "accountRequestsTotal": AccountRequest.count(), "usersroles": UserRole.list(), "roles" : Role.list(), "menuitem" : "listUsers"])	
//	}
//	
//	def pastAccountsRequests = {
//		def user = injectUserProfile()
//		
//		if (!params.max) params.max = 10
//		if (!params.offset) params.offset = 0
//		if (!params.sort) params.sort = "username"
//		if (!params.order) params.order = "asc"
//	
//		def users = usersManagementService.pastAccountRequests(user, params.max, params.offset, params.sort, params.order);
//	
//		render (view:'pastAccountRequests', model:[user: user, "users" : users, "accountRequestsTotal": AccountRequest.count(), "usersroles": UserRole.list(), "roles" : Role.list(), "menuitem" : "listUsers"])
//	}
//	
//	def approveAccountRequest = {
//		def user = injectUserProfile()
//		def accountRequest = AccountRequest.findById(params.accountRequest);
//		if(accountRequest) {
//			User newUser = new User(firstName:accountRequest.firstName, lastName:accountRequest.lastName,
//				displayName:accountRequest.displayName, country:accountRequest.country,
//				affiliation:accountRequest.affiliation, username:accountRequest.username,
//				password:accountRequest.password, email:accountRequest.email, enabled: 'true');
//			if(!newUser.save(flush:true)) {
//				newUser.errors.allErrors.each { render it }
//			} else {
//				accountRequest.moderated=true;
//				accountRequest.moderatedBy=user;
//				accountRequest.approved=true;
//				accountRequest.userId=newUser.id;
//				updateUserRole(newUser, Role.findByAuthority(DefaultRoles.USER.value()), 'on')
//				render (view:'showProfile', model:[item: newUser,
//							userRoles: usersManagementService.getUserRoles(newUser),
//							userGroups: usersManagementService.getUserGroups(newUser),
//							userCircles: usersManagementService.getUserCircles(newUser),
//							userCommunities: usersManagementService.getUserCommunities(newUser)]);
//			}
//		}
//	}
//	
//	def declineAccountRequest = {
//		def user = injectUserProfile()
//		def accountRequest = AccountRequest.findById(params.accountRequest);
//		if(accountRequest) {
//			accountRequest.moderated=true;
//			accountRequest.moderatedBy=user;
//			accountRequest.approved=false;
//			redirect (action: 'moderateAccountsRequests');
//		}
//	}
//	
//	def editAccountRequest = {
//		def accountRequest = AccountRequest.findById(params.accountRequest);
//		if(accountRequest) {
//			render accountRequest.id;
//		}
//	}
}
