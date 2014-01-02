import grails.util.Metadata;

// Necessary for Grails 2.0 as the variable ${appName} is not available 
// anymore in the log4j closure. It needs the import above.
def appName = Metadata.current.getApplicationName();

// locations to search for config files that get merged into the main config
// config files can either be Java properties files or ConfigSlurper scripts

// See: http://stackoverflow.com/questions/3807267/grails-external-configuration-grails-config-locations-absolute-path-file
grails.config.locations = ["classpath:${appName}-config.properties", "file:./${appName}-config.properties"]

grails.project.groupId = appName // change this to alter the default package name and Maven publishing destination
grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
grails.mime.use.accept.header = false
grails.mime.types = [ html: ['text/html','application/xhtml+xml'],
                      xml: ['text/xml', 'application/xml'],
                      text: 'text/plain',
                      js: 'text/javascript',
                      rss: 'application/rss+xml',
                      atom: 'application/atom+xml',
                      css: 'text/css',
                      csv: 'text/csv',
                      all: '*/*',
                      json: ['application/json','text/json'],
                      form: 'application/x-www-form-urlencoded',
                      multipartForm: 'multipart/form-data'
                    ]

// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.cache.maxsize = 1000

// What URL patterns should be processed by the resources plugin
grails.resources.adhoc.patterns = ['/images/*', '/css/*', '/js/*', '/plugins/*']
grails.resources.adhoc.excludes = ['**/gwt/**']

// The default codec used to encode data with ${}
grails.views.default.codec = "none" // none, html, base64
grails.views.gsp.encoding = "UTF-8"
grails.converters.encoding = "UTF-8"
// enable Sitemesh preprocessing of GSP pages
grails.views.gsp.sitemesh.preprocess = true
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []
// whether to disable processing of multi part requests
grails.web.disable.multipart=false

// request parameters to mask when logging exceptions
grails.exceptionresolver.params.exclude = ['password']

// enable query caching by default
grails.hibernate.cache.queries = true

grails {
	mail {
//	  host = "smtp.gmail.com"
//	  port = 465
//	  username = ""
//	  password = ""
	  props = ["mail.smtp.auth":"true",
			   "mail.smtp.socketFactory.port":"465",
			   "mail.smtp.socketFactory.class":"javax.net.ssl.SSLSocketFactory",
			   "mail.smtp.socketFactory.fallback":"false"]
 
	} 
}
//grails.mail.default.from="domeoannotationtool@gmail.com"
//grails.mail.overrideAddress="domeoannotationtool@gmail.com"


// set per-environment serverURL stem for creating absolute links
environments {
    development {
        //grails.logging.jul.usebridge = false
		//grails.serverURL = "http://localhost:8080/${appName}"
		
		log4j = {
			// Pattern layouts guide:
			// http://logging.apache.org/log4j/1.2/apidocs/org/apache/log4j/PatternLayout.html
			appenders {
				// Uncomment the following line for a more detailed logging in development mode
			    console name:'stdout', threshold: org.apache.log4j.Level.DEBUG, layout:pattern(conversionPattern: '%d{dd MMM yyyy HH:mm:ss,SSS} %c{2} %m%n')
				//console name:'stdout', threshold: org.apache.log4j.Level.INFO, layout:pattern(conversionPattern: '%m%n')
			}
			
			debug   "server.RealmVerifier",
					"consumer.ConsumerManager",
					'grails.app.controllers.org.mindinformatics.grails.domeo.persistence.ExportController',
					'grails.app.controllers.org.mindinformatics.services.connector.yaleimagefinder.YaleImageFinderController'
            
            info    'grails.app', // Necessary for Bootstrap logging
                    'org.mindinformatics.grails.domeo.dashboard.security',
                    'org.mindinformatics.services.connector.pubmed.dataaccess',
                    'org.mindinformatics.services.connector.pubmed',
					'grails.app.controllers.org.mindinformatics.grails.domeo.plugin.bibliography.BibliographyController'
		
			error  'org.codehaus.groovy.grails.web.servlet',  //  controllers
				   'org.codehaus.groovy.grails.web.pages', //  GSP
				   'org.codehaus.groovy.grails.web.sitemesh', //  layouts
				   'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
				   'org.codehaus.groovy.grails.web.mapping', // URL mapping
				   'org.codehaus.groovy.grails.commons', // core / classloading
				   'org.codehaus.groovy.grails.plugins', // plugins
				   'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
				   'org.springframework',
				   'org.hibernate',
				   'net.sf.ehcache.hibernate',
					'grails.app.controllers.org.mindinformatics.grails.domeo.ErrorsController'
		}
    }
    production {
        grails.logging.jul.usebridge = false
        
		def catalinaBase = System.properties.getProperty('catalina.base')
		if (!catalinaBase) catalinaBase = '.'   // just in case
		def logDirectory = "${catalinaBase}/logs"
		
		log4j = {
			appenders {
                // Set up a log file in the standard tomcat area; be sure to use .toString() with ${}
                rollingFile name:'tomcatLog', file:"${logDirectory}/"+appName+".log".toString(), maxFileSize:'100KB'
				
				// Disabling the creation of stacktrace.log from a Grails application in production mode
				// this can be commented out if it is proven that the deployment environment does not fail 
				// in the creation of the stacktrace.log file
                'null' name:'stacktrace'
            }
			
			root {
				// Change the root logger to my tomcatLog file
				info 'tomcatLog'
				additivity = true
			}
			
			info 'org.mindinformatics.grails.domeo.dashboard.security',
				 'org.mindinformatics.services.connector.pubmed.dataaccess',
				 'org.mindinformatics.services.connector.pubmed',
				 'grails.app'
		
			error  'org.codehaus.groovy.grails.web.servlet',  //  controllers
				   'org.codehaus.groovy.grails.web.pages', //  GSP
				   'org.codehaus.groovy.grails.web.sitemesh', //  layouts
				   'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
				   'org.codehaus.groovy.grails.web.mapping', // URL mapping
				   'org.codehaus.groovy.grails.commons', // core / classloading
				   'org.codehaus.groovy.grails.plugins', // plugins
				   'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
				   'org.springframework',
				   'org.hibernate',
				   'net.sf.ehcache.hibernate'
		}
    }
}



// Added by the Spring Security Core plugin:
grails.plugin.springsecurity.userLookup.userDomainClassName = 'org.mindinformatics.grails.domeo.dashboard.security.User'
grails.plugin.springsecurity.userLookup.authorityJoinClassName = 'org.mindinformatics.grails.domeo.dashboard.security.UserRole'
grails.plugin.springsecurity.authority.className = 'org.mindinformatics.grails.domeo.dashboard.security.Role'
grails.plugin.springsecurity.rememberMe.persistent = true
grails.plugin.springsecurity.rememberMe.persistentToken.domainClassName = 'org.mindinformatics.grails.domeo.dashboard.security.PersistentLogin'
grails.plugin.springsecurity.securityConfigType = "Annotation"
grails.plugin.springsecurity.password.algorithm = 'bcrypt' // Default since spring-security-core:2.0-RC2
// http://grails-plugins.github.io/grails-spring-security-core/docs/manual/guide/newInV2.html
grails.plugin.springsecurity.logout.postOnly = false

// http://www.redtoad.ca/ataylor/2011/05/logging-spring-security-events-in-grails/
grails.plugin.springsecurity.useSecurityEventListener = true
grails.plugin.springsecurity.logout.handlerNames = ['rememberMeServices', 'securityContextLogoutHandler', 'securityEventListener']

grails.plugin.springsecurity.openid.domainClass = 'org.mindinformatics.grails.domeo.dashboard.security.OpenID'

// grails.plugins.springsecurity.providerNames = ['daoAuthenticationProvider', 'ldapAuthProvider', 'rememberMeAuthenticationProvider']
grails.plugin.springsecurity.controllerAnnotations.staticRules = [
	'/secure/**': ['ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_USER'],
	'/secured/**': ['ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_USER'],
	'/web/domeo': ['ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_USER'],
	'/web/pdf': ['ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_USER'],
	'/persistence/**': ['ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_USER'],
	'/bibliography/**': ['ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_USER'],
	'/ajaxPersistence/**': ['ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_USER'],
	'/ajaxBibliographic/**': ['ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_USER'],
	'/plugins/**': ['ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_USER'],
	'/dashboard/**': ['ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_USER'],
	'/agents/**': ['ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_USER'],
	'/users/**': ['ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_USER'],
	'/nif/**': ['ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_USER'],
	'/bioPortal/**': ['ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_USER'],
	'/bioPortalConnector/**': ['ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_USER'],
	'/profiles/**': ['ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_USER'],
	'/persistence/**': ['ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_USER'],
	'/managerDashboard/**': ['ROLE_MANAGER'],
	'/adminDashboard/**': ['ROLE_ADMIN'],
	'/ajaxDashboard/**': ['ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_USER'],
	'/retrievePmcImagesData/**': ['ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_USER'],
	'/pubmed/**': ['permitAll'],
	'/public/**': ['permitAll'],
	'/index': ['ROLE_ADMIN'],
	'/errors/**': ['ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_USER'],
	'/yaleImageFinder/**': ['ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_USER'],
]
