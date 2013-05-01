package org.mindinformatics.grails.domeo.dashboard

import org.mindinformatics.grails.domeo.dashboard.security.DefaultRoles
import org.mindinformatics.grails.domeo.dashboard.security.Role
import org.mindinformatics.grails.domeo.dashboard.security.User
import org.mindinformatics.grails.domeo.dashboard.security.UserRole

/**
* @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
*/
//@Mixin(GeneralDashboardController)
class ManagerDashboardController extends GeneralDashboardController {

	static defaultAction = "adminDashboard"
	
	def managerDashboard = {
		def user = injectUserProfile()
		if(user!=null) render (view:'homeManager', model:[user: user, userRoles: usersManagementService.getUserRoles(user)]);
	}
	
	def listUsers = {
		def user = injectUserProfile()

		if (!params.max) params.max = 10
		if (!params.offset) params.offset = 0
		if (!params.sort) params.sort = "username"
		if (!params.order) params.order = "asc"

		def users = usersGroupsManagementService.listUsers(user, params.max, params.offset, params.sort, params.order);

		render (view:'listUsers', model:[user: user, "users" : users, "usersTotal": User.count(), "usersroles": UserRole.list(), "roles" : Role.list(), "menuitem" : "listUsers"])
	}
	
	def showUser = {
		if(params.id!=null) {
			def user = usersGroupsManagementService.getUser(params.id);
			if(user!=null) {
				def flag;
				def roles = UserRole.findAllByUser(user);
				roles.each {
					println it.user.roleRank;
				}
				
				render (view:'showUser', model:[item: user,
							userRoles: usersManagementService.getUserRoles(user),
							userGroups: usersManagementService.getUserGroups(user),
							userCircles: usersManagementService.getUserCircles(user),
							userCommunities: usersManagementService.getUserCommunities(user)]);
			} else {
				render (view:'error', model:[message: "User not found for id: "+params.id]);
			}
		} else {
			render (view:'error', model:[message: "User id not defined!"]);
		}
	}
	
	def createUser = {
		render (view:'createUser',  model:[action: "create", roles: Role.list(), defaultRole: DefaultRoles.USER, "menuitem" : "createUser"]);
	}

	def saveUser = {UserCreateCommand userCreateCmd->
		if(userCreateCmd.hasErrors()) {
			userCreateCmd.errors.allErrors.each { println it }
			render(view:'createUser', model:[item:userCreateCmd, roles: Role.list(),
						defaultRole: Role.findByAuthority("ROLE_USER")])
		} else {
			def user = userCreateCmd.createUser()
			if(user)  {
				if(!user.save()) {
					// Failure in saving
					user.errors.allErrors.each { println it }
					render(view:'createUser', model:[item:userCreateCmd, roles: Role.list(),
								msgError: 'The user has not been saved successfully'])
				} else {
					// A Manager cannot create and administrator
					//updateUserRole(user, Role.findByAuthority(DefaultRoles.ADMIN.value()), params.Admin)
					updateUserRole(user, Role.findByAuthority(DefaultRoles.MANAGER.value()), params.Manager)
					updateUserRole(user, Role.findByAuthority(DefaultRoles.USER.value()), params.User)

					redirect (action:'showUser',id: user.id, model: [
								msgSuccess: 'Group saved successfully']);
				}
			} else {
				// User already existing
				render(view:'createUser', model:[item:userCreateCmd, roles: Role.list(),
							msgError: 'A user with this name is already existing'])
			}
		}
	}
	
	def editUser = {
		def user = User.findById(params.id)

		render (view:'/shared/editUser', model:[item: user, userRoles: getUserRoles(user), roles: Role.list(), userGroups: getUserGroups(user), action: "edit",
					groupRoles: listOfGroupRoles(), groupStatus: listOfUserGroupStatus()])
	}
	
	def updateUser = { UserEditCommand userEditCmd ->
		if(userEditCmd.hasErrors()) {
			userEditCmd.errors.allErrors.each { println it }
			render(view:'/shared/editUser', model:[item:userEditCmd])
		} else {
			def user = User.findById(params.id)
			user.firstName = userEditCmd.firstName
			user.lastName = userEditCmd.lastName
			user.displayName = userEditCmd.displayName
			user.email = userEditCmd.email
			user.affiliation = userEditCmd.affiliation
			user.country = userEditCmd.country

			updateUserRole(user, Role.findByAuthority(DefaultRoles.ADMIN.value()), params.Admin)
			updateUserRole(user, Role.findByAuthority(DefaultRoles.MANAGER.value()), params.Manager)
			updateUserRole(user, Role.findByAuthority(DefaultRoles.USER.value()), params.User)

			updateUserStatus(user, params.status)

			render (view:'showUser', model:[item: user, userRoles: getUserRoles(user)])
		}

	}
}
