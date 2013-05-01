package org.mindinformatics.grails.domeo.dashboard.security

import grails.validation.Validateable

import java.util.Set

/**
* @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
*/
@Validateable
class User { 

	private static final int NAME_MAX_SIZE = 255;
	
	String title
	String firstName
	String middleName
	String lastName
	String displayName
	String country
	String affiliation
	
	String id
	String username
	String password
    String email
	Date dateCreated, lastUpdated // Grails automatic timestamping
	boolean enabled
	boolean accountExpired
	boolean accountLocked
	boolean passwordExpired
	
	static hasMany = [openIds: OpenID]

	static transients = ['name','status','isAdmin','isUser','isManager','isCurator','isAnalyst']
	
	String getStatus() {
		return UserUtils.getStatusLabel(this);
	}
	
	String getName() {
		return lastName + " " + firstName;
	}
	
	static constraints = {
		id maxSize: 36

		//Users' data
		title (nullable: true, blank: true, maxSize:NAME_MAX_SIZE)
		firstName (blank: false, maxSize:NAME_MAX_SIZE)
		middleName (nullable: true, blank: true, maxSize:NAME_MAX_SIZE)
		lastName (blank: false, maxSize:NAME_MAX_SIZE)
		displayName (blank: false, maxSize:NAME_MAX_SIZE)
		affiliation (blank: true, maxSize:NAME_MAX_SIZE)
		country (blank: true, maxSize:NAME_MAX_SIZE)
		
		username blank: false, unique: true
		password blank: false
		email blank: false, unique: true, email: true
	}

	static mapping = {
		password column: '`password`'
		id generator:'uuid', sqlType: "varchar(36)"
	}

	Set<Role> getAuthorities() {
		UserRole.findAllByUser(this).collect { it.role } as Set
	}
	
	String getIsAdmin() {
		boolean flag = false;
		def userrole = UserRole.findAllByUser(this)
		userrole.each { 
			if(it.role.authority.equals(DefaultRoles.ADMIN.value())) {
				flag = true;
			} 	
		}
		flag ? "y" : ""
	}
	
	String getIsManager() {
		boolean flag = false;
		def userrole = UserRole.findAllByUser(this)
		userrole.each {
			if(it.role.authority.equals(DefaultRoles.MANAGER.value())) {
				flag = true;
			}
		}
		flag ? "y" : ""
	}
	
	String getIsUser() {
		boolean flag = false;
		def userrole = UserRole.findAllByUser(this)
		userrole.each {
			if(it.role.authority.equals(DefaultRoles.USER.value())) {
				flag = true;
			}
		}
		flag ? "y" : ""
	}
	
	String getIsAnalyst() {
		boolean flag = false;
		def userrole = UserRole.findAllByUser(this)
		userrole.each {
			if(it.role.authority.equals(DefaultRoles.ANALYST.value())) {
				flag = true;
			}
		}
		flag ? "y" : ""
	}
	
	def getRoleRank() {
		int rank = 0;
		def userrole = UserRole.findAllByUser(this)
		userrole.each {
			println it.role
			println it.role.getRanking()
			rank += it.role.getRanking();
		}
		rank
	}
	
	def getRole() {
		def userrole = UserRole.findByUser(this)
		if(userrole) { 
			if(userrole.role.authority.equals("ROLE_ADMIN")) {
				return "Admin"
			} else if(userrole.role.authority.equals("ROLE_USER")) {
				return "User"
			} else {
				return userrole.role.authority;
			}
		} else {
			return "Error"
		}
	}
}
