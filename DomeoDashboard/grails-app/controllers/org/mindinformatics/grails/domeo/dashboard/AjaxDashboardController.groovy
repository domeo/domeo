package org.mindinformatics.grails.domeo.dashboard

import grails.converters.JSON

import org.mindinformatics.grails.domeo.dashboard.circles.UserCircle
import org.mindinformatics.grails.domeo.dashboard.groups.Group
import org.mindinformatics.grails.domeo.dashboard.groups.UserGroup
import org.mindinformatics.grails.domeo.dashboard.security.User

class AjaxDashboardController {

	// GROUPS
	//--------------------------------
	/*
	 * Pass through method that extracts the id parameter
	 * of the user and returns hers UserGroup entities.
	 */
	def userGroups = {
		return getUserGroups(User.findById(params.id));
	}
	
	/*
	* This returns UserGroup entities as that makes possible
	* retrieving the details for this relationship and both
	* the user and the group data
	*/
   def getUserGroups(def user) {
	   def userGroups = []
	   userGroups = UserGroup.findAllByUser(user)
	   JSON.use("deep")
	   render userGroups as JSON;
   }
   
   
   def userCircles = {
	   return getUserCircles(User.findById(params.id));
   }
	
	/*
	 * This returns UserGroup entities as that makes possible
	 * retrieving the details for this relationship and both
	 * the user and the group data
	 */
	def getUserCircles(def user) {
		def userCircles = []
		userCircles = UserCircle.findAllByUser(user)
		JSON.use("deep")
		render userCircles as JSON;
	}
	
	def groupUsers = {
		return getGroupUsers(Group.findById(params.id));
	}
	
	def getGroupUsers(def group) {
		def groupUsers = []
		groupUsers = UserGroup.findAllByGroup(group)
		JSON.use("deep")
		render groupUsers as JSON;
	}


//	def searchGroups = {
//		searchUserGroups(User.findById(params.id));
//	}
//	
//	def searchUserGroups(def user) {
//		def groups = []
//		groups = Group.findAll()
//		groups.each {
//			it.setMembersCounter("0")
//		}
//		render groups as JSON;
//	}
}
