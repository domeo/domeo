package org.mindinformatics.grails.domeo.dashboard.groups

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.builder.HashCodeBuilder 
import org.mindinformatics.grails.domeo.dashboard.security.Status
import org.mindinformatics.grails.domeo.dashboard.security.User

/**
* @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
*/
class UserGroup implements Serializable {

	Date dateCreated, lastUpdated // Grails automatic timestamping
	
	User user
	Group group
	Status status
	
	static hasMany = [roles: GroupRole]
	
	boolean isGuest() {
		def course = roles.find { it.authority == DefaultGroupRoles.GUEST.value() }
		if(roles.size()==1 && course!=null) return true;
		return false;
	}
	
	boolean equals(other) {
		if (!(other instanceof UserGroup)) {
			return false
		}

		other.user?.id == user?.id &&
			other.group?.id == group?.id
	}

	int hashCode() {
		def builder = new HashCodeBuilder()
		if (user) builder.append(user.id)
		if (group) builder.append(group.id)
		builder.toHashCode()
	}

	static UserGroup get(long userId, long groupId) {
		find 'from UserGroup where user.id=:userId and group.id=:groupId',
			[userId: userId, groupId: groupId]
	}
	
	static def getByUserId(long userId) {
		findAll 'from UserGroup where user.id=:userId',
			[userId: userId]
	}

	static UserGroup create(User user, Group group, boolean flush = false) {
		new UserGroup(user: user, group: group).save(flush: flush, insert: true)
	}

	static boolean remove(User user, Group group, boolean flush = false) {
		UserGroup instance = UserGroup.findByUserAndGroup(user, group)
		instance ? instance.delete(flush: flush) : false
	}

	static void removeAll(User user) {
		executeUpdate 'DELETE FROM UserGroup WHERE user=:user', [user: user]
	}

	static void removeAll(Group group) {
		executeUpdate 'DELETE FROM UserGroup WHERE group=:group', [group: group]
	}

	static mapping = {
		id composite: ['group', 'user']
		version false
	}
}
