import grails.util.Metadata;

// Necessary for Grails 2.0 as the variable ${appName} is not available 
// anymore in the log4j closure. It needs the import above.
def appName = Metadata.current.getApplicationName();

// locations to search for config files that get merged into the main config
// config files can either be Java properties files or ConfigSlurper scripts

// See: http://stackoverflow.com/questions/3807267/grails-external-configuration-grails-config-locations-absolute-path-file
grails.config.locations = ["classpath:${appName}-config.properties", "file:./${appName}-config.properties"]

environments {
     development {
        grails.serverURL = "http://localhost:8080/${appName}"
		
		log4j = {
			// Pattern layouts guide:
			// http://logging.apache.org/log4j/1.2/apidocs/org/apache/log4j/PatternLayout.html
			appenders {
			    console name:'stdout', threshold: org.apache.log4j.Level.DEBUG, layout:pattern(conversionPattern: '%d{dd MMM yyyy HH:mm:ss,SSS} %c{2} %m%n')
			}

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
		
		    warn   'org.mortbay.log'
			
			info   'grails.app', // Necessary for Bootstrap logging
				   'grails.app.service.org.mindinformatics.services.connector.bioportal.services.JsonBioPortalVocabulariesService',
				   'grails.app.services.org.mindinformatics.services.connector.bioportal.services.JsonBioPortalVocabulariesService',
				   'grails.app.controller.org.mindinformatics.services.connector.bioportal.BioPortalConnectorController',
				   'grails.app.controllers.org.mindinformatics.services.connector.bioportal.BioPortalConnectorController'
		}
     }
}
