package org.mindinformatics.grails.domeo.dashboard

import org.mindinformatics.grails.domeo.dashboard.groups.UserGroup
import org.mindinformatics.grails.domeo.dashboard.security.User

/**
* @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
*/
class UsersGroupsManagementService {

	def getUser(def id) {
		def user = User.findById(id)
		user
	}
	
	def getUserGroups(def user) {
		def groups = [];
		def userGroups = UserGroup.findAllByUser(user)
		userGroups.each {
			groups.add(it.group);
		}
		groups
	}
	
	/**
	 * Returns the list of all the users of the node with pagination
	 * @param user		The user requesting the list
	 * @param max		The maximum results to be returned
	 * @param offset	The offset from the beginning of the list
	 * @param sort		The sorting criteria
	 * @param _order	The order asc or desc
	 * @return The list of users
	 */
	def listUsers(def user, def _max, def _offset, def _sort, def _order) {
		
		def users = [];
		
		if(_sort == 'name') {
			def userStatusCriteria = User.createCriteria();
			def r = userStatusCriteria.list {
				maxResults(_max?.toInteger())
				firstResult(_offset?.toInteger())
				order('lastName', _order)
				order('firstName', _order)
			}
			users = r.toList();
		}  else if (_sort == 'isAdmin' || _sort == 'isAnalyst' || _sort == 'isManager'
				|| _sort == 'isUser') {
			def buffer = [];
			def usersStatus = [:]
			User.list().each { auser ->
				if(_sort == 'isAdmin') 
					usersStatus.put (auser.id, auser.isAdmin)
				else if(_sort == 'isManager') 
					usersStatus.put (auser.id, auser.isManager)
				else if(_sort == 'isUser')
					usersStatus.put (auser.id, auser.isUser)
				//else if(_sort == 'isAnalyst')
				//	usersStatus.put (auser.id, auser.isAnalyst)
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
				
			int offset = (_offset instanceof String) ? Integer.parseInt(_offset) : _offset
			int max = (_max instanceof String) ? Integer.parseInt(_max) : _max
			for(int i=offset;i< Math.min(offset+max, usersStatus.size()); i++) {
				users.add(buffer[i]);
			}
		} else if (_sort == 'status') {
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
				
			int offset = (_offset instanceof String) ? Integer.parseInt(_offset) : _offset
			int max = (_max instanceof String) ? Integer.parseInt(_max) : _max
			for(int i=offset;i< Math.min(offset+max, usersStatus.size()); i++) {
				users.add(buffer[i]);
			}
		} else {
			def userStatusCriteria = User.createCriteria();
			def r = userStatusCriteria.list {
				maxResults(_max?.toInteger())
				firstResult(_offset?.toInteger())
				order(_sort, _order)
			}
			users = r.toList();
		}
		users
	}
}
