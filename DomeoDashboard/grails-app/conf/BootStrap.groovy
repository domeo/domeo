import org.mindinformatics.grails.domeo.dashboard.circles.Circle
import org.mindinformatics.grails.domeo.dashboard.circles.UserCircle
import org.mindinformatics.grails.domeo.dashboard.groups.DefaultGroupPrivacy
import org.mindinformatics.grails.domeo.dashboard.groups.DefaultGroupRoles
import org.mindinformatics.grails.domeo.dashboard.groups.DefaultGroupStatus
import org.mindinformatics.grails.domeo.dashboard.groups.DefaultUserStatusInGroup
import org.mindinformatics.grails.domeo.dashboard.groups.Group
import org.mindinformatics.grails.domeo.dashboard.groups.GroupPrivacy
import org.mindinformatics.grails.domeo.dashboard.groups.GroupRole
import org.mindinformatics.grails.domeo.dashboard.groups.GroupStatus
import org.mindinformatics.grails.domeo.dashboard.groups.UserGroup
import org.mindinformatics.grails.domeo.dashboard.groups.UserStatusInGroup
import org.mindinformatics.grails.domeo.dashboard.security.DefaultRoles
import org.mindinformatics.grails.domeo.dashboard.security.Role
import org.mindinformatics.grails.domeo.dashboard.security.User
import org.mindinformatics.grails.domeo.dashboard.security.UserRole

class BootStrap {

	def springSecurityService
	
    def init = { servletContext ->
		
		println "Bootstrapping ..............................................."

		// Roles
		/*
		def userRole = Role.findByAuthority(DefaultRoles.USER.value()) ?: new Role(authority: DefaultRoles.USER.value(), label: DefaultRoles.USER.label(), description: DefaultRoles.USER.description).save(failOnError: true)
		def superUserRole = Role.findByAuthority(DefaultRoles.MANAGER.value()) ?: new Role(authority: DefaultRoles.MANAGER.value(), label: DefaultRoles.MANAGER.label(), description: DefaultRoles.MANAGER.description).save(failOnError: true)
		def adminRole = Role.findByAuthority(DefaultRoles.ADMIN.value()) ?: new Role(authority: DefaultRoles.ADMIN.value(), label: DefaultRoles.ADMIN.label(), description: DefaultRoles.ADMIN.description).save(failOnError: true)
		*/
		
		// ROLES
		println 'Roles....'
		DefaultRoles.values().each {
			println  it.value() + " " + it.ranking()
			Role.findByAuthority(it.value()) ?:
			new Role(authority: it.value(), ranking: it.ranking(), label: it.label(), description: it.description()).save(failOnError: true, flush: true)
			println Role.findByAuthority(it.value()).getLabel();
		}
		
		// GROUPS
		// ------
		println 'Groups....'
		//////////ROLES
		println 'Groups roles....'
		DefaultGroupRoles.values().each {
			GroupRole.findByAuthority(it.value()) ?:
			new GroupRole(authority: it.value(), label: it.label(), description: it.description()).save(failOnError: true)
		}
		//////////STATUS
		println 'Groups status....'
		DefaultGroupStatus.values().each {
			GroupStatus.findByValue(it.value()) ?:
			new GroupStatus(value: it.value(), uuid: it.uuid(), label: it.label(), description: it.description()).save(failOnError: true)
		}
		//////////PRIVACY
		println 'Groups privacy....'
		DefaultGroupPrivacy.values().each {
			GroupPrivacy.findByValue(it.value()) ?:
			new GroupPrivacy(value: it.value(), uuid: it.uuid(), label: it.label(), description: it.description()).save(failOnError: true)
		}
		//////////USER STATUS IN GROUP
		println 'User status in group....'
		DefaultUserStatusInGroup.values().each {
			UserStatusInGroup.findByValue(it.value()) ?:
			new UserStatusInGroup(value: it.value(), label: it.label(), description: it.description()).save(failOnError: true)
		}
		/*
		// CIRCLES
		// -------
		//////////ROLES
		println 'Circles roles....'
		DefaultCircleRoles.values().each {
			CircleRole.findByAuthority(it.value()) ?:
			new CircleRole(authority: it.value(), label: it.label(), description: it.description()).save(failOnError: true)
		}
		//////////STATUS
		println 'Circles status....'
		DefaultGroupStatus.values().each {
			CircleStatus.findByValue(it.value()) ?:
			new CircleStatus(value: it.value(), label: it.label(), description: it.description()).save(failOnError: true)
		}
		//////////PRIVACY
		println 'Circles privacy....'
		DefaultCirclePrivacy.values().each {
			CirclePrivacy.findByValue(it.value()) ?:
			new CirclePrivacy(value: it.value(), label: it.label(), description: it.description()).save(failOnError: true)
		}
		//////////USER STATUS IN CIRCLE
		println 'User status in circle....'
		DefaultUserStatusInCircle.values().each {
			UserStatusInCircle.findByValue(it.value()) ?:
			new UserStatusInCircle(value: it.value(), label: it.label(), description: it.description()).save(failOnError: true)
		}
		
		// COMMUNITY
		// ---------
		//////////ROLES
		println 'Community roles....'
		DefaultCommunityRoles.values().each {
			CommunityRole.findByAuthority(it.value()) ?:
			new CommunityRole(authority: it.value(), label: it.label(), description: it.description()).save(failOnError: true)
		}
		//////////STATUS
		println 'Community status....'
		DefaultGroupStatus.values().each {
			CommunityStatus.findByValue(it.value()) ?:
			new CommunityStatus(value: it.value(), label: it.label(), description: it.description()).save(failOnError: true)
		}
		//////////PRIVACY
		println 'Community privacy....'
		DefaultCommunityPrivacy.values().each {
			CommunityPrivacy.findByValue(it.value()) ?:
			new CommunityPrivacy(value: it.value(), label: it.label(), description: it.description()).save(failOnError: true)
		}
		//////////USER STATUS IN COMMUNITY
		println 'User status in community....'
		DefaultUserStatusInCommunity.values().each {
			UserStatusInCommunity.findByValue(it.value()) ?:
			new UserStatusInCommunity(value: it.value(), label: it.label(), description: it.description()).save(failOnError: true)
		}
		*/
		
		def admin = 'admin'
		def adminUser = User.findByUsername(admin) ?: new User(
			firstName: 'John',
			lastName: 'Doe',
			displayName: 'Dr. John Doe',
			affiliation: 'ACME',
			country: 'Neverland',
			username: admin,
			password: springSecurityService.encodePassword(admin),
			email: 'admin@commonsemantics.org',
			enabled: true).save(failOnError: true) 
			
			println 'User role 0'
			if (!adminUser.authorities.contains(Role.findByAuthority(DefaultRoles.ADMIN.value()))) {
				println 'User role 1'
				UserRole.create(adminUser, Role.findByAuthority(DefaultRoles.ADMIN.value()))
			}
			
		def manager = 'manager'
		def managerUser = User.findByUsername(manager) ?: new User(
			firstName: 'Lou',
			lastName: 'Red',
			displayName: 'Dr. Lou Red',
			affiliation: 'ACME',
			country: 'Neverland',
			username: manager,
			password: springSecurityService.encodePassword(manager),
			email: 'manager@commonsemantics.org',
			enabled: true).save(failOnError: true)

			println 'User role 0'
			if (!managerUser.authorities.contains(Role.findByAuthority(DefaultRoles.MANAGER.value()))) {
				println 'User role 1'
				UserRole.create(managerUser, Role.findByAuthority(DefaultRoles.MANAGER.value()))
			}
			
		def manager2 = 'manager2'
		def managerUser2 = User.findByUsername(manager2) ?: new User(
			firstName: 'Big',
			lastName: 'White',
			displayName: 'Dr. Big White',
			affiliation: 'ACME',
			country: 'Neverland',
			username: manager2,
			password: springSecurityService.encodePassword(manager2),
			email: 'manager2@commonsemantics.org',
			enabled: true).save(failOnError: true)

			println 'User role 0'
			if (!managerUser2.authorities.contains(Role.findByAuthority(DefaultRoles.MANAGER.value()))) {
				println 'User role 1'
				UserRole.create(managerUser2, Role.findByAuthority(DefaultRoles.MANAGER.value()))
			}
		
		def group0 = "Test Group 0"
		def testGroup0 = Group.findByName(group0) ?: new Group(
			name: group0,
			shortName: 'TG0',
			description: group0,
			status: GroupStatus.findByValue(DefaultGroupStatus.ACTIVE.value()),
			privacy: GroupPrivacy.findByValue(DefaultGroupPrivacy.PUBLIC.value())
			).save(failOnError: true)
		def group1 = "Test Group 1"
		def testGroup1 = Group.findByName(group1) ?: new Group(
			name: group1,
			shortName: 'TG1',
			description: group1,
			status: GroupStatus.findByValue(DefaultGroupStatus.ACTIVE.value()),
			privacy: GroupPrivacy.findByValue(DefaultGroupPrivacy.PUBLIC.value())
			).save(failOnError: true)
		def testUserGroup1 = UserGroup.findByUserAndGroup(adminUser, testGroup1)?: new UserGroup(
			user: adminUser,
			group: testGroup1,
			status: UserStatusInGroup.findByValue(DefaultUserStatusInGroup.ACTIVE.value())
		).save(failOnError: true, flash: true)
		testUserGroup1.addToRoles GroupRole.findByAuthority(DefaultGroupRoles.ADMIN.value())
		
		def group3 = "Test Group 3"
		def testGroup3 = Group.findByName(group3) ?: new Group(
			name: group3,
			shortName: 'TG3',
			description: group3,
			status: GroupStatus.findByValue(DefaultGroupStatus.ACTIVE.value()),
			privacy: GroupPrivacy.findByValue(DefaultGroupPrivacy.PUBLIC.value())
			).save(failOnError: true)
			
		def circle1 = "Circle 1"
		def testCircle1 = Circle.findByName(circle1) ?: new Circle(
			name: circle1,
			shortName: circle1,
			).save(failOnError: true)
		def testUserCircle1 = UserCircle.findByUser(adminUser)?: new UserCircle(
			user: adminUser,
			circle: testCircle1
		).save(failOnError: true, flash: true)
		/*
		def circle1 = "Test Circle 1"
		def testCircle1 = Circle.findByName(circle1) ?: new Circle(
			name: circle1,
			nickname: 'TC1',
			description: circle1,
			status: CircleStatus.findByValue(DefaultGroupStatus.ACTIVE.value()),
			privacy: CirclePrivacy.findByValue(DefaultCirclePrivacy.PUBLIC.value())
			).save(failOnError: true)
		def testUserCircle1 = UserCircle.findByUserAndCircle(adminUser, testCircle1)?: new UserCircle(
			user: adminUser,
			role: CircleRole.findByAuthority(DefaultCircleRoles.ADMIN.value()),
			circle: testCircle1,
			status: UserStatusInCircle.findByValue(DefaultUserStatusInCircle.ACTIVE.value())
		).save(failOnError: true)
		
		def community1 = "Test Community 1"
		def testCommunity1 = Community.findByName(community1) ?: new Community(
			name: community1,
			shortName: 'TCo1',
			description: community1,
			status: CommunityStatus.findByValue(DefaultGroupStatus.ACTIVE.value()),
			privacy: CommunityPrivacy.findByValue(DefaultCommunityPrivacy.PUBLIC.value())
			).save(failOnError: true)
		def testUserCommunity1 = UserCommunity.findByUserAndCommunity(adminUser, testCommunity1)?: new UserCommunity(
			user: adminUser,
			role: CommunityRole.findByAuthority(DefaultCommunityRoles.ADMIN.value()),
			community: testCommunity1,
			status: UserStatusInCommunity.findByValue(DefaultUserStatusInCommunity.ACTIVE.value())
		).save(failOnError: true)
		*/	
		/*
		def group2 = "Test Group 2"
		def testGroup2 = Group.findByName(group2) ?: new Group(
			name: group2,
			nickname: 'TG2',
			description: 'Test Group 2').save(failOnError: true)
		def group3 = "Test Group 3"
		def testGroup3 = Group.findByName(group3) ?: new Group(
			name: group3,
			nickname: 'TG3',
			description: 'Test Group 3').save(failOnError: true)
			
		
		def testUserRoleGroup2 = UserGroup.findByUserAndGroup(adminUser, testGroup2)?: new UserGroup(
				user: adminUser,
				role: userGroupRole,
				group: testGroup2
			).save(failOnError: true)
		def testUserRoleGroup3 = UserGroup.findByUserAndGroup(adminUser, testGroup3)?: new UserGroup(
			user: adminUser,
			role: userGroupRole,
			group: testGroup3
		).save(failOnError: true)
		*/
		
		println "Bootstrap completed ........................................."
    }
    def destroy = {
    }
}
