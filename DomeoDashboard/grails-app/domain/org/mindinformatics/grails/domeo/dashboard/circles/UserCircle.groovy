package org.mindinformatics.grails.domeo.dashboard.circles

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.builder.HashCodeBuilder 
import org.mindinformatics.grails.domeo.dashboard.groups.Group
import org.mindinformatics.grails.domeo.dashboard.security.User

/**
* @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
*/
class UserCircle implements Serializable {

	Date dateCreated, lastUpdated // Grails automatic timestamping
	
	User user
	Circle circle
	
	static hasMany = [users: User, groups: Group]
		
	boolean equals(other) {
		if (!(other instanceof UserCircle)) {
			return false
		}

		other.user?.id == user?.id &&
			other.circle?.id == circle?.id
	}

	int hashCode() {
		def builder = new HashCodeBuilder()
		if (user) builder.append(user.id)
		if (circle) builder.append(circle.id)
		builder.toHashCode()
	}

	static UserCircle get(long userId, long circleId) {
		find 'from UserCircle where user.id=:userId and group.id=:circleId',
			[userId: userId, groupId: circleId]
	}
	
	static def getByUserId(long userId) {
		findAll 'from UserCircle where user.id=:userId',
			[userId: userId]
	}

	static UserCircle create(User user, Circle circle, boolean flush = false) {
		new UserCircle(user: user, circle: circle).save(flush: flush, insert: true)
	}

	static boolean remove(User user, Circle circle, boolean flush = false) {
		UserCircle instance = UserCircle.findByUserAndCircle(user, circle)
		instance ? instance.delete(flush: flush) : false
	}

	static void removeAll(User user) {
		executeUpdate 'DELETE FROM UserCircle WHERE user=:user', [user: user]
	}

	static void removeAll(Circle circle) {
		executeUpdate 'DELETE FROM UserCircle WHERE circle=:circle', [circle: circle]
	}

	static mapping = {
		id composite: ['circle', 'user']
		version false
	}
}
