package org.mindinformatics.domeo.persistence.services

import org.mindinformatics.grails.domeo.dashboard.groups.UserGroup
import org.mindinformatics.grails.domeo.persistence.AnnotationSetGroup
import org.mindinformatics.grails.domeo.persistence.AnnotationSetPermissions
import org.mindinformatics.grails.domeo.persistence.services.IPermissionTypes

class AnnotationPermissionService {

	def usersGroupsManagementService;
	
	def getAnnotationSetPermissions(def userId, def annotationSet) {
		log.info(":" + userId + ": retrieving annotation set permissions " + annotationSet.id);
		List<String> toReturn = new ArrayList<String>();
		def permissions = AnnotationSetPermissions.findByAnnotationSet(annotationSet);
		toReturn.add(permissions.permissionType);
		toReturn
	}
	
	def isLocked(def annotationSet) {
		def permissions = AnnotationSetPermissions.findByAnnotationSet(annotationSet);
		return permissions.isLocked;
	}
	
	def isPermissionGranted(def user, def annotationSet, def privateData, def groupsData, def publicData) {
        if(!user.hasProperty("id")) {
            // Request by anonymous user
            log.info("Permission denied for User " + user);
            return false;
        } else {
            // Request by logged user
    		log.info(":User" + user.id + ": check permission " + annotationSet.id);
    		def permissions = AnnotationSetPermissions.findByAnnotationSet(annotationSet);
    		if(permissions!=null) {
    			if(publicData && permissions.permissionType.equals(IPermissionTypes.publicAccess)) {
                    log.info(":User " + user.id + ": granted permission of " + annotationSet.id);
    				return true;
    			} else if(groupsData && permissions.permissionType.equals(IPermissionTypes.groupsAccess)) {
    				def annotationSetGroups = AnnotationSetGroup.findAllByAnnotationSet(annotationSet); 
    				
    				def groups = [];
    				def userGroups = UserGroup.findAllByUser(user)
    				userGroups.each {
    					groups.add(it.group);
    				}
    				boolean isDone = false
    				groups.each { group ->
    					isDone = annotationSetGroups.find() { g->
    						if(g.groupUri=="urn:group:uuid:"+group.id) return true;
    						else return false;
    					}
    					if(isDone) return;
    				} 
    				if(isDone) {
                        log.info(":User " + user.id + ": granted permission of " + annotationSet.id);
                        return true;
    				}
    			} else {
    				if(privateData && permissions.permissionType.equals('urn:person:uuid:'+user.id)) {
                        log.info(":User " + user.id + ": granted permission of " + annotationSet.id);
                        return true;
    				} 
    			}
    		} else {
    			log.error(":" + user.id + ": permissions not found for " + annotationSet.id);
    		}
    		return false;
        }
	}
    
	def isPermissionGranted(def user, def annotationSet) {
        if(!user.hasProperty("id")) {
            // Request by anonymous user
            log.info("Permission denied for User " + user);
            return false;
        } else {
            // Request by logged user
    		log.info(":User" + user.id + ": check permission " + annotationSet.id);
    		def permissions = AnnotationSetPermissions.findByAnnotationSet(annotationSet);
    		if(permissions!=null) {
    			if(permissions.permissionType.equals(IPermissionTypes.publicAccess)) {
                    log.info(":User " + user.id + ": granted permission of " + annotationSet.id);
    				return true;
    			} else if(permissions.permissionType.equals(IPermissionTypes.groupsAccess)) {
    				def annotationSetGroups = AnnotationSetGroup.findAllByAnnotationSet(annotationSet); 
    				
    				def groups = [];
    				def userGroups = UserGroup.findAllByUser(user)
    				userGroups.each {
    					groups.add(it.group);
    				}
    				boolean isDone = false
    				groups.each { group ->
    					isDone = annotationSetGroups.find() { g->
    						if(g.groupUri=="urn:group:uuid:"+group.id) return true;
    						else return false;
    					}
    					if(isDone) return;
    				} 
    				if(isDone) {
                        log.info(":User " + user.id + ": granted permission of " + annotationSet.id);
                        return true;
    				}
    			} else {
    				if(permissions.permissionType.equals('urn:person:uuid:'+user.id)) {
                        log.info(":User " + user.id + ": granted permission of " + annotationSet.id);
                        return true;
    				} 
    			}
    		} else {
    			log.error(":" + user.id + ": permissions not found for " + annotationSet.id);
    		}
    		return false;
        }
	}
}