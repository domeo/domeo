package org.mindinformatics.grails.domeo.dashboard

import org.mindinformatics.grails.domeo.dashboard.groups.DefaultGroupStatus
import org.mindinformatics.grails.domeo.dashboard.groups.Group
import org.mindinformatics.grails.domeo.dashboard.groups.UserGroup
import org.mindinformatics.grails.domeo.dashboard.security.AccountRequest
import org.mindinformatics.grails.domeo.dashboard.security.Role
import org.mindinformatics.grails.domeo.dashboard.security.User
import org.mindinformatics.grails.domeo.dashboard.security.UserRole

/**
* @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
*/
class UsersManagementService {

	def getUser(def id) {
		def user = User.findById(id.substring(id.lastIndexOf(":")+1))
		user 
	}
	
	def getUserByUsername(def username) {
		def user = User.findByUsername(username)
		user
	}
	
	def listUsers(def user, def max, def offset, def sort, def _order) {
		
		//TODO fix pagination
		def users = [];
		if (sort == 'status') {
			def buffer = [];
			def usersStatus = [:]
			User.list().each { auser ->
				usersStatus.put (auser.id, auser.status)
			}
			usersStatus = usersStatus.sort{ a, b -> a.value.compareTo(b.value) }
			if(_order == "desc")
				usersStatus.each { userStatus ->
					buffer.add(User.findById(userStatus.key));
				}
			else
				usersStatus.reverseEach { userStatus ->
					buffer.add(User.findById(userStatus.key));
				}
				
			int _offset = (offset instanceof String) ? Integer.parseInt(offset) : offset
			int _max = (max instanceof String) ? Integer.parseInt(max) : max
			for(int i=offset;i< Math.min(_offset+_max, usersStatus.size()); i++) {
				users.add(buffer[i]);
			}
		} else if (sort == 'isAdmin' || sort == 'isAnalyst' || sort == 'isManager'
				|| sort == 'isCurator' || sort == 'isUser') {
		
		} else if (sort == 'name') {
			def buffer = [];
			def usersNames = [:]
			User.list().each { auser ->
				usersNames.put (auser.id, auser.name)
			}
			usersNames = usersNames.sort{ a, b -> a.value.compareTo(b.value) }
			if(_order == "desc")
				usersNames.each { userName ->
					buffer.add(User.findById(userName.key));
				}
			else
				usersNames.reverseEach { userName ->
					buffer.add(User.findById(userName.key));
				}
			int _offset = (offset instanceof String) ? Integer.parseInt(offset) : offset
			int _max = (max instanceof String) ? Integer.parseInt(max) : max
			for(int i=offset;i< Math.min(_offset+_max, usersNames.size()); i++) {
				users.add(buffer[i]);
			}
		} else {
			def userStatusCriteria = User.createCriteria();
			def r = userStatusCriteria.list {
				maxResults(max?.toInteger())
				firstResult(offset?.toInteger())
				order(sort, _order)
			}
			users = r.toList();
		}

		users
	}
	
	def moderateAccountRequests(def user, def max, def offset, def sort, def _order) {
		//AccountRequest.list();
		
		def accountRequestCriteria = AccountRequest.createCriteria();
		def r = accountRequestCriteria.list {
			maxResults(max?.toInteger())
			firstResult(offset?.toInteger())
			order(sort, _order)
			eq('moderated', false )
			
		}
		r.toList();
	}
	
	def pastAccountRequests(def user, def max, def offset, def sort, def _order) {
		//AccountRequest.list();
		
		def accountRequestCriteria = AccountRequest.createCriteria();
		def r = accountRequestCriteria.list {
			maxResults(max?.toInteger())
			firstResult(offset?.toInteger())
			order(sort, _order)
			eq('moderated', true )
			
		}
		r.toList();
	}
	
	def listGroups(def max, def offset, def sort, def _order) {
		
		def groups = [];
		def groupsCount = [:]
		Group.list().each { agroup ->
			groupsCount.put (agroup.id, UserGroup.findAllWhere(group: agroup).size())
		}
		def groupsStatus = [:]
		Group.list().each { agroup ->
			groupsStatus.put (agroup.id, agroup.status)
		}
		
		if (sort == 'groupsCount') {
			groupsCount = groupsCount.sort{ a, b -> a.value <=> b.value }
			if(_order == "desc")
				groupsCount.each { groupCount ->
					groups.add Group.findById(groupCount.key);
				}
			else
				groupsCount.reverseEach { groupCount ->
					groups.add Group.findById(groupCount.key);
				}
		} else if (sort == 'status') {
			groupsStatus = groupsStatus.sort{ a, b -> a.value.compareTo(b.value) }
			if(_order == "desc")
				groupsStatus.each { groupStatus ->
					groups.add Group.findById(groupStatus.key);
				}
			else
				groupsStatus.reverseEach { groupStatus ->
					groups.add Group.findById(groupStatus.key);
				}
		} else {
			groups = Group.withCriteria {
				maxResults(max?.toInteger())
				firstResult(offset?.toInteger())
				order(sort, _order)
			}
		}
		
		[groups, groupsCount]
	}
	
	def listGroupUsers(def group, def _max, def _offset, def sort, def _order) {
		def groupUsers = UserGroup.findAllByGroup(group)
		def users = []
		groupUsers.each {
			users.add it.user
		}
		users
	}
	
	def listUserGroups(def user) {
		def userGroups = [];
		def allUserGroups = [];
		def searchResult = UserGroup.createCriteria().list() {
			eq('user', user);
		}
		searchResult.each {
			println it.group.enabled
			if(it.group.enabled!=false) {
				allUserGroups.add it
			}
		}
		allUserGroups
	}
	
	def listUserGroups(def user, def _max, def _offset, def sort, def _order) {
		
		/*
		def groups = [];
		def groupsCount = [:]
		def groupsStatus = [:]
		Group.list().each { agroup ->
			if(UserGroup.findByUserAndGroup(user, agroup)!=null) {
				groupsCount.put (agroup.id, UserGroup.findAllWhere(group: agroup).size())
				groupsStatus.put (agroup.id, agroup.status)
				groups.add (agroup)
			}
		}

		if (sort == 'groupsCount') {
			groupsCount = groupsCount.sort{ a, b -> a.value <=> b.value }
			if(_order == "desc")
				groupsCount.each { groupCount ->
					groups.add Group.findById(groupCount.key);
				}
			else
				groupsCount.reverseEach { groupCount ->
					groups.add Group.findById(groupCount.key);
				}
		} else if (sort == 'status') {
			groupsStatus = groupsStatus.sort{ a, b -> a.value.compareTo(b.value) }
			if(_order == "desc")
				groupsStatus.each { groupStatus ->
					groups.add Group.findById(groupStatus.key);
				}
			else
				groupsStatus.reverseEach { groupStatus ->
					groups.add Group.findById(groupStatus.key);
				}
		} else {
		
			groups = Group.withCriteria {
				maxResults(_max?.toInteger())
				firstResult(_offset?.toInteger())
				order(sort, _order)
			}
		}
		*/
		
		def userGroups = [];
		UserGroup.createCriteria()
		
		def searchResult = UserGroup.createCriteria().list(
			max:_max, offset:_offset) {
				eq('user', user);
		}
		def allUserGroups = searchResult
		
		/*
		def userGroups = [];
		def groupsCount = [:]
		def groupsStatus = [:]
		def allUserGroups = UserGroup.findAllByUser(user);
		allUserGroups.each { userGroup ->
			groupsCount.put (userGroup.group, UserGroup.findAllWhere(group: userGroup.group).size())
			groupsStatus.put (userGroup.group, userGroup.group.status)
		}
		
		if (sort == 'groupsCount') {
			groupsCount = groupsCount.sort{ a, b -> a.value <=> b.value }
			if(_order == "desc")
				groupsCount.each { groupCount ->
					userGroups.add UserGroup.findByGroup(groupCount.key);
				}
			else
				groupsCount.reverseEach { groupCount ->
					userGroups.add UserGroup.findByGroup(groupCount.key);
				}
		}
		*/
		
		allUserGroups
	}
	
	def listRoles(def user, def max, def offset, def sort, def _order) {
		def rolesCount = [:]
		Role.list().each { arole ->
			rolesCount.put (arole.id, UserRole.findAllWhere(role: arole).size())
		}
		
		def roles = [];
		if (sort == 'rolesCount') {
			rolesCount.sort({ a, b -> a <=> b } as Comparator)
			if(order == "desc")
				rolesCount.each { roleCount ->
					roles.add Role.findById(roleCount.key);
				}
			else
				rolesCount.reverseEach { roleCount ->
					roles.add Role.findById(roleCount.key);
				}
		} else {
			roles = Role.withCriteria {
				maxResults(max?.toInteger())
				firstResult(offset?.toInteger())
				order(sort, _order)
			}
		}
		[roles, rolesCount]
	}
	
	def getUserRoles(def user) {
		def userRoles = []
		def ur = UserRole.findAllByUser(user)
		println ur
		ur.each { userRoles.add(it.role)}
		return userRoles;
	}
	
	def getUserGroups(def user) {
		def ur = []
		def userGroups = []
		ur = UserGroup.findAllByUser(user)
		ur.each { userGroups.add(it.group)}
		return ur;
	}
	
	def getUserCircles(def user) {
		def ur = []
		/*
		def userCircles = []
		ur = UserCircle.findAllByUser(user)
		ur.each { userCircles.add(it.circle)}
		*/
		return ur;
	}
	
	def getUserCommunities(def user) {
		def ur = []
		/*
		def userCommunities = []
		ur = UserCommunity.findAllByUser(user)
		ur.each { userCommunities.add(it.community)}
		*/
		return ur;
	}

}
