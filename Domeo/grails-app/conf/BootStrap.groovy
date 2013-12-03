import org.codehaus.groovy.grails.commons.ApplicationAttributes
import org.mindinformatics.grails.domeo.client.profiles.model.DomeoClientProfile
import org.mindinformatics.grails.domeo.client.profiles.model.DomeoClientProfileEntry
import org.mindinformatics.grails.domeo.client.profiles.model.UserAvailableDomeoClientProfile
import org.mindinformatics.grails.domeo.client.profiles.model.UserCurrentDomeoClientProfile
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
	
	def grailsApplication
	def springSecurityService

    def init = { servletContext ->
		
		/*
		String password = 'password'
		
		def roleAdmin = new Role(authority: 'ROLE_ADMIN').save()
		def roleUser = new Role(authority: 'ROLE_USER').save()

		def user = new User(username: 'user',
			password: password, enabled: true).save()
		def admin = new User(username: 'admin',
			password: password, enabled: true).save()

		UserRole.create user, roleUser
		UserRole.create admin, roleUser
		UserRole.create admin, roleAdmin, true
		
		//org.hsqldb.util.DatabaseManager.main()
*/
		log.info  '========================================================================';
		log.info  ' DOMEO ANNOTATION WEB TOOLKIT (v.' +
			grailsApplication.metadata['app.version'] + ", b." +
			grailsApplication.metadata['app.build'] + ")";
			
		separator();
		log.info  ' Designed and developed by Paolo Ciccarese'
		log.info  ' for MIND Informatics Labs directed by Tim Clark'
		log.info  ' A product of Massachusetts General Hospital, Boston, MA, USA (c) 2012'
		
		log.info  '========================================================================';
		log.info  'Bootstrapping....'
				
		separator();
		log.info  '** Configuration externalization: '
		log.info  ' ' +grailsApplication.config.grails.config.locations
		
		separator();
		log.info  '** MongoDB Configuration';
		log.info  ' url        : ' + grailsApplication.config.mongodb.url ;
		log.info  ' database   : ' + grailsApplication.config.mongodb.database ;
		log.info  ' collection : ' + grailsApplication.config.mongodb.collection ;
        
        separator();
        log.info  '** Elastic Search Configuration';
        log.info  ' ip         : ' + grailsApplication.config.elastico.ip ;
        log.info  ' port       : ' + grailsApplication.config.elastico.port ;
        log.info  ' database   : ' + grailsApplication.config.elastico.database ;
        log.info  ' collection : ' + grailsApplication.config.elastico.collection ;
        
		
		// Databse setup
		def ctx=servletContext.getAttribute(ApplicationAttributes.APPLICATION_CONTEXT)
		def dataSource = ctx.dataSource
  
		dataSource.targetDataSource.setMinEvictableIdleTimeMillis((long)1000 * 60 * 30)
		dataSource.targetDataSource.setTimeBetweenEvictionRunsMillis((long)1000 * 60 * 30)
		dataSource.targetDataSource.setNumTestsPerEvictionRun(3)
  
		dataSource.targetDataSource.setTestOnBorrow(true)
		dataSource.targetDataSource.setTestWhileIdle(true)
		dataSource.targetDataSource.setTestOnReturn(true)
		dataSource.targetDataSource.setValidationQuery("SELECT 1")
		
		separator();
		log.info  '** MySQL Configuration';
		log.info  ' url        : ' + dataSource.targetDataSource.url ;
		log.info  ' username   : ' + dataSource.targetDataSource.username ;
		log.info  ' password   : ' + 
			(dataSource.targetDataSource.password!=null?dataSource.targetDataSource.password:"<none>") ;
		
		// Uncomment for full database configuration
		//dataSource.targetDataSource.properties.each { println it }
		
		// PROXY
		separator();
		log.info  '** Proxy Configuration';
		log.info  ' proxy ip   : ' + grailsApplication.config.domeo.proxy.ip ;
		log.info  ' proxy port : ' + grailsApplication.config.domeo.proxy.port ;
				
		// ROLES
		separator();
		log.info  '** System Roles'
		DefaultRoles.values().each {
			if(!Role.findByAuthority(it.value())) { 
				new Role(authority: it.value(), ranking: it.ranking(), label: it.label(), description: it.description()).save(failOnError: true, flush: true)	
				log.info  createdPrefix() + it.value() + ', ' + it.ranking()
			}
		}
		
		// GROUPS
		// ------
		//////////ROLES
		separator();
		log.info  '** Groups Roles'
		DefaultGroupRoles.values().each {
			if(!GroupRole.findByAuthority(it.value())) { 
				new GroupRole(authority: it.value(), ranking: it.ranking(), label: it.label(), description: it.description()).save(failOnError: true)
				log.info  createdPrefix() + it.value() + ', ' + it.ranking()
			}
		}
		//////////STATUS
		separator();
		log.info  '** Groups Status'
		DefaultGroupStatus.values().each {
			if(!GroupStatus.findByValue(it.value())) {
				new GroupStatus(value: it.value(), uuid: it.uuid(), label: it.label(), description: it.description()).save(failOnError: true)
				
			}
		}
		//////////PRIVACY
		separator();
		log.info  '** Groups Privacy'
		DefaultGroupPrivacy.values().each {
			if(!GroupPrivacy.findByValue(it.value())) {
				new GroupPrivacy(value: it.value(), uuid: it.uuid(), label: it.label(), description: it.description()).save(failOnError: true)
				log.info  createdPrefix() + it.value()
			}
		}
		//////////USER STATUS IN GROUP
		separator();
		log.info  "** Users' Status in Group"
		DefaultUserStatusInGroup.values().each {
			if(!UserStatusInGroup.findByValue(it.value())) {
				new UserStatusInGroup(value: it.value(), label: it.label(), description: it.description()).save(failOnError: true)
				log.info  createdPrefix() + it.value()
			}
		}
		
		/*
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
		
		separator();
		
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
			
			log.info  'User role 0'
			if (!adminUser.authorities.contains(Role.findByAuthority(DefaultRoles.ADMIN.value()))) {
				log.info  'User role 1'
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

			log.info  'User role 0'
			if (!managerUser.authorities.contains(Role.findByAuthority(DefaultRoles.MANAGER.value()))) {
				log.info  'User role 1'
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

			log.info  'User role 0'
			if (!managerUser2.authorities.contains(Role.findByAuthority(DefaultRoles.MANAGER.value()))) {
				log.info  'User role 1'
				UserRole.create(managerUser2, Role.findByAuthority(DefaultRoles.MANAGER.value()))
			}
			
		def user1 = 'user'
		def accountUser1 = User.findByUsername(user1) ?: new User(
			firstName: 'Mark',
			lastName: 'Green',
			displayName: 'Dr. Mark Green',
			affiliation: 'ACME',
			country: 'Puerto Rico',
			username: user1,
			password: springSecurityService.encodePassword(user1),
			email: 'user1@commonsemantics.org',
			enabled: true).save(failOnError: true)

			log.info  'User role'
			if (!accountUser1.authorities.contains(Role.findByAuthority(DefaultRoles.USER.value()))) {
				log.info  'User role 1'
				UserRole.create(accountUser1, Role.findByAuthority(DefaultRoles.USER.value()))
			}
		
		def group0 = "Test Group 0"
		def testGroup0 = Group.findByName(group0) ?: new Group(
			name: group0,
			shortName: 'TG0',
			description: group0,
			enabled: true,
			locked: false,
			status: GroupStatus.findByValue(DefaultGroupStatus.ACTIVE.value()),
			privacy: GroupPrivacy.findByValue(DefaultGroupPrivacy.PUBLIC.value())
			).save(failOnError: true)
		def group1 = "Test Group 1"
		def testGroup1 = Group.findByName(group1) ?: new Group(
			name: group1,
			shortName: 'TG1',
			description: group1,
			enabled: true,
			locked: false,
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
			).save(failOnError: true, flash:true)
		//testCircle1.users = []
		
		def testUserCircle1 = UserCircle.findByUser(adminUser)?: new UserCircle(
			user: adminUser,
			circle: testCircle1
		).save(failOnError: true, flash: true)
		testUserCircle1.addToUsers managerUser;
			
		separator();
		log.info   '** Initializing profiles'
		separator();
		
		// -------------------
		//  COMPLETE PROFILES
		// -------------------
		log.info   'Initializing complete biomedical profile'
		
		// Plugins
		def completeProfile = DomeoClientProfile.findByName("Complete Biomedical Profile")?: new DomeoClientProfile(
			name: 'Complete Biomedical Profile',
			description: 'All the tools that Domeo has to offer for biomedicine',
			createdBy: adminUser
		).save(failOnError: true, flash: true)
		
		DomeoClientProfileEntry.findByProfileAndPlugin(completeProfile, "org.mindinformatics.gwt.domeo.plugins.annotation.qualifier")?: new DomeoClientProfileEntry(
			profile: completeProfile,
			plugin: "org.mindinformatics.gwt.domeo.plugins.annotation.qualifier",
			status: "enabled"
		).save(failOnError: true, flash: true)
		
		DomeoClientProfileEntry.findByProfileAndPlugin(completeProfile, "org.mindinformatics.gwt.domeo.plugins.annotation.nif.antibodies")?: new DomeoClientProfileEntry(
			profile: completeProfile,
			plugin: "org.mindinformatics.gwt.domeo.plugins.annotation.nif.antibodies",
			status: "enabled"
		).save(failOnError: true, flash: true)
		
		DomeoClientProfileEntry.findByProfileAndPlugin(completeProfile, "org.mindinformatics.gwt.domeo.plugins.annotation.micropubs")?: new DomeoClientProfileEntry(
			profile: completeProfile,
			plugin: "org.mindinformatics.gwt.domeo.plugins.annotation.micropubs",
			status: "enabled"
		).save(failOnError: true, flash: true)
		
		DomeoClientProfileEntry.findByProfileAndPlugin(completeProfile, "org.mindinformatics.gwt.domeo.plugins.resource.pubmed")?: new DomeoClientProfileEntry(
			profile: completeProfile,
			plugin: "org.mindinformatics.gwt.domeo.plugins.resource.pubmed",
			status: "enabled"
		).save(failOnError: true, flash: true)
		
		DomeoClientProfileEntry.findByProfileAndPlugin(completeProfile, "org.mindinformatics.gwt.domeo.plugins.resource.pubmedcentral")?: new DomeoClientProfileEntry(
			profile: completeProfile,
			plugin: "org.mindinformatics.gwt.domeo.plugins.resource.pubmedcentral",
			status: "enabled"
		).save(failOnError: true, flash: true)
		
		DomeoClientProfileEntry.findByProfileAndPlugin(completeProfile, "org.mindinformatics.gwt.domeo.plugins.resource.omim")?: new DomeoClientProfileEntry(
			profile: completeProfile,
			plugin: "org.mindinformatics.gwt.domeo.plugins.resource.omim",
			status: "enabled"
		).save(failOnError: true, flash: true)
		
		DomeoClientProfileEntry.findByProfileAndPlugin(completeProfile, "org.mindinformatics.gwt.domeo.plugins.resource.bioportal")?: new DomeoClientProfileEntry(
			profile: completeProfile,
			plugin: "org.mindinformatics.gwt.domeo.plugins.resource.bioportal",
			status: "enabled"
		).save(failOnError: true, flash: true)
		
		DomeoClientProfileEntry.findByProfileAndPlugin(completeProfile, "org.mindinformatics.gwt.domeo.client.component.clipboard")?: new DomeoClientProfileEntry(
			profile: completeProfile,
			plugin: "org.mindinformatics.gwt.domeo.client.component.clipboard",
			status: "enabled"
		).save(failOnError: true, flash: true)
		
		// Features
		DomeoClientProfileEntry.findByProfileAndPlugin(completeProfile, "org.mindinformatics.gwt.domeo.feature.branding")?: new DomeoClientProfileEntry(
			profile: completeProfile,
			plugin: "org.mindinformatics.gwt.domeo.feature.branding",
			status: "disabled",
			type: "feature"
		).save(failOnError: true, flash: true)
		
		if(DomeoClientProfileEntry.findByProfileAndPlugin(completeProfile, "org.mindinformatics.gwt.domeo.feature.addressbar")) {
			DomeoClientProfileEntry.findByProfileAndPlugin(completeProfile, "org.mindinformatics.gwt.domeo.feature.addressbar").status = "enabled"
		} else {
			new DomeoClientProfileEntry(
				profile: completeProfile,
				plugin: "org.mindinformatics.gwt.domeo.feature.addressbar",
				status: "enabled",
				type: "feature"
			).save(failOnError: true, flash: true)
		}
		
		DomeoClientProfileEntry.findByProfileAndPlugin(completeProfile, "org.mindinformatics.gwt.domeo.feature.analyze")?: new DomeoClientProfileEntry(
			profile: completeProfile,
			plugin: "org.mindinformatics.gwt.domeo.feature.analyze",
			status: "enabled",
			type: "feature"
		).save(failOnError: true, flash: true)
		
		DomeoClientProfileEntry.findByProfileAndPlugin(completeProfile, "org.mindinformatics.gwt.domeo.feature.preferences")?: new DomeoClientProfileEntry(
			profile: completeProfile,
			plugin: "org.mindinformatics.gwt.domeo.feature.preferences",
			status: "enabled",
			type: "feature"
		).save(failOnError: true, flash: true)
		
		if(DomeoClientProfileEntry.findByProfileAndPlugin(completeProfile, "org.mindinformatics.gwt.domeo.feature.sharing")) {
			DomeoClientProfileEntry.findByProfileAndPlugin(completeProfile, "org.mindinformatics.gwt.domeo.feature.sharing").status = "enabled"
		} else {
			new DomeoClientProfileEntry(
				profile: completeProfile,
				plugin: "org.mindinformatics.gwt.domeo.feature.sharing",
				status: "enabled",
				type: "feature"
			).save(failOnError: true, flash: true)
		}
		
		DomeoClientProfileEntry.findByProfileAndPlugin(completeProfile, "org.mindinformatics.gwt.domeo.feature.help")?: new DomeoClientProfileEntry(
			profile: completeProfile,
			plugin: "org.mindinformatics.gwt.domeo.feature.help",
			status: "enabled",
			type: "feature"
		).save(failOnError: true, flash: true)
		
		if(DomeoClientProfileEntry.findByProfileAndPlugin(completeProfile, "org.mindinformatics.gwt.domeo.feature.document.general.reference.self")) {
			DomeoClientProfileEntry.findByProfileAndPlugin(completeProfile, "org.mindinformatics.gwt.domeo.feature.document.general.reference.self").status = "disabled"
		} else {
			new DomeoClientProfileEntry(
				profile: completeProfile,
				plugin: "org.mindinformatics.gwt.domeo.feature.document.general.reference.self",
				status: "disabled",
				type: "feature"
			).save(failOnError: true, flash: true)
		}
		
		if(DomeoClientProfileEntry.findByProfileAndPlugin(completeProfile, "org.mindinformatics.gwt.domeo.feature.document.general.qualifiers.self")) {
			DomeoClientProfileEntry.findByProfileAndPlugin(completeProfile, "org.mindinformatics.gwt.domeo.feature.document.general.qualifiers.self").status = "disabled"
		} else {
			new DomeoClientProfileEntry(
				profile: completeProfile,
				plugin: "org.mindinformatics.gwt.domeo.feature.document.general.reference.self",
				status: "disabled",
				type: "feature"
			).save(failOnError: true, flash: true)
		}
		
		// ----------------
		//  BASIC PROFILES
		// ----------------
		separator();
		log.info   'Initializing basic profiles'
		def simpleProfile = DomeoClientProfile.findByName("Simple profile")?: new DomeoClientProfile(
			name: 'Simple profile',
			description: 'Simple profile: notes and commenting',
			createdBy: adminUser
		).save(failOnError: true, flash: true)
		
		DomeoClientProfileEntry.findByProfileAndPlugin(completeProfile, "org.mindinformatics.gwt.domeo.plugins.resource.pubmed")?: new DomeoClientProfileEntry(
			profile: simpleProfile,
			plugin: "org.mindinformatics.gwt.domeo.plugins.resource.pubmed",
			status: "enabled"
		).save(failOnError: true, flash: true)
		
		DomeoClientProfileEntry.findByProfileAndPlugin(completeProfile, "org.mindinformatics.gwt.domeo.plugins.resource.pubmedcentral")?: new DomeoClientProfileEntry(
			profile: simpleProfile,
			plugin: "org.mindinformatics.gwt.domeo.plugins.resource.pubmedcentral",
			status: "enabled"
		).save(failOnError: true, flash: true)
		
		DomeoClientProfileEntry.findByProfileAndPlugin(completeProfile, "org.mindinformatics.gwt.domeo.plugins.resource.omim")?: new DomeoClientProfileEntry(
			profile: simpleProfile,
			plugin: "org.mindinformatics.gwt.domeo.plugins.resource.omim",
			status: "enabled"
		).save(failOnError: true, flash: true)
		
		DomeoClientProfileEntry.findByProfileAndPlugin(completeProfile, "org.mindinformatics.gwt.domeo.plugins.resource.bioportal")?: new DomeoClientProfileEntry(
			profile: simpleProfile,
			plugin: "org.mindinformatics.gwt.domeo.plugins.resource.bioportal",
			status: "enabled"
		).save(failOnError: true, flash: true)
		
		DomeoClientProfileEntry.findByProfileAndPlugin(completeProfile, "org.mindinformatics.gwt.domeo.client.component.clipboard")?: new DomeoClientProfileEntry(
			profile: simpleProfile,
			plugin: "org.mindinformatics.gwt.domeo.client.component.clipboard",
			status: "enabled"
		).save(failOnError: true, flash: true)
		
		separator();
		log.info   'Initializing available profiles'
		log.info   'Administrator profiles'
		UserAvailableDomeoClientProfile.findByUserAndProfile(adminUser, completeProfile)?: new UserAvailableDomeoClientProfile(
			user: adminUser,
			profile: completeProfile
		).save(failOnError: true, flash: true)
		UserAvailableDomeoClientProfile.findByUserAndProfile(adminUser, simpleProfile)?: new UserAvailableDomeoClientProfile(
			user: adminUser,
			profile: simpleProfile
		).save(failOnError: true, flash: true)
		
		log.info   'Manager 1 profiles'
		UserAvailableDomeoClientProfile.findByUserAndProfile(managerUser, completeProfile)?: new UserAvailableDomeoClientProfile(
			user: managerUser,
			profile: completeProfile
		).save(failOnError: true, flash: true)
		UserAvailableDomeoClientProfile.findByUserAndProfile(managerUser, simpleProfile)?: new UserAvailableDomeoClientProfile(
			user: managerUser,
			profile: simpleProfile
		).save(failOnError: true, flash: true)
		
		log.info   'Manager 2 profiles'
		UserAvailableDomeoClientProfile.findByUserAndProfile(managerUser2, completeProfile)?: new UserAvailableDomeoClientProfile(
			user: managerUser2,
			profile: completeProfile
		).save(failOnError: true, flash: true)
		UserAvailableDomeoClientProfile.findByUserAndProfile(managerUser2, simpleProfile)?: new UserAvailableDomeoClientProfile(
			user: managerUser2,
			profile: simpleProfile
		).save(failOnError: true, flash: true)
		
		log.info   'User profiles'
		UserAvailableDomeoClientProfile.findByUserAndProfile(accountUser1, completeProfile)?: new UserAvailableDomeoClientProfile(
			user: accountUser1,
			profile: completeProfile
		).save(failOnError: true, flash: true)
		UserAvailableDomeoClientProfile.findByUserAndProfile(accountUser1, simpleProfile)?: new UserAvailableDomeoClientProfile(
			user: accountUser1,
			profile: simpleProfile
		).save(failOnError: true, flash: true)
		
		separator();
		log.info   '** Initializing current profiles'
		log.info   'Administrator profiles'
		log.info   'Administrator current profile'
		UserCurrentDomeoClientProfile.findByUser(adminUser)?: new UserCurrentDomeoClientProfile(
			user: adminUser,
			currentProfile: completeProfile
		).save(failOnError: true, flash: true)
		
		log.info   'Manager 1 current profile'
		UserCurrentDomeoClientProfile.findByUser(managerUser)?: new UserCurrentDomeoClientProfile(
			user: managerUser,
			currentProfile: completeProfile
		).save(failOnError: true, flash: true)
		
		log.info   'Manager 2 current profile'
		UserCurrentDomeoClientProfile.findByUser(managerUser2)?: new UserCurrentDomeoClientProfile(
			user: managerUser2,
			currentProfile: simpleProfile
		).save(failOnError: true, flash: true)
		
		log.info   'User current profile'
		UserCurrentDomeoClientProfile.findByUser(accountUser1)?: new UserCurrentDomeoClientProfile(
			user: accountUser1,
			currentProfile: simpleProfile
		).save(failOnError: true, flash: true)
		
		separator();
		log.info  'Bootstrapping complete!'
		log.info  '========================================================================';
    }
	def separator = {
		log.info  '------------------------------------------------------------------------';
	}
	def createdPrefix = {
		return ' created    : ';
	}
    def destroy = {
    }
}
