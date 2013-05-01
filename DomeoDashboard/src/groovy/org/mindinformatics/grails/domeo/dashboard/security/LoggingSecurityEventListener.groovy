package org.mindinformatics.grails.domeo.dashboard.security

import javax.servlet.http.*
import org.apache.commons.logging.LogFactory
import org.springframework.context.ApplicationListener
import org.springframework.security.authentication.event.AbstractAuthenticationEvent
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.logout.LogoutHandler
 
// Taken from
// http://www.redtoad.ca/ataylor/2011/05/logging-spring-security-events-in-grails/
class LoggingSecurityEventListener implements
    ApplicationListener<AbstractAuthenticationEvent>, LogoutHandler {
 
    private static final log = LogFactory.getLog(this)
 
    void onApplicationEvent(AbstractAuthenticationEvent event) {
        event.authentication.with {
            def username = principal.hasProperty('username')?.getProperty(principal) ?: principal
            log.info "event=${event.class.simpleName} username=${username} " +
                "remoteAddress=${details?.remoteAddress} sessionId=${details?.sessionId}"
        }
    }
 
    void logout(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) {
		if(authentication==null) return;
        authentication.with {
            def username = principal.hasProperty('username')?.getProperty(principal) ?: principal
            log.info "event=Logout username=${username} " +
                "remoteAddress=${details?.remoteAddress} sessionId=${details?.sessionId}"
        }
    }
}
