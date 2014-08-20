package org.mindinformatics.grails.domeo.dashboard.security.ldap;

import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Listens to login events so if an LDAP login occurs, but with the remember
 * me option set the roles are correctly updated from AD allowing the roles for
 * a user to change even though they have saved their login session.
 * @author Tom Wilkin - Eli Lilly & Co <wilkin_thomas@lilly.com> */
//@Component
public class LDAPAuthenticationApplicationListener 
		implements ApplicationListener<InteractiveAuthenticationSuccessEvent>
{
	
	@Override
	@Transactional
	public void onApplicationEvent(final InteractiveAuthenticationSuccessEvent event) {
		// check if this is the log in event
		if(event.getSource( ) instanceof UsernamePasswordAuthenticationToken) {
			// check if this is an LDAP bind
			UsernamePasswordAuthenticationToken token = 
					(UsernamePasswordAuthenticationToken)event.getSource( );
			if(token.getPrincipal( ) instanceof LDAPUserDetails) {
				// update the roles for this user from the groups
				LDAPUserDetails userDetails = 
						(LDAPUserDetails)token.getPrincipal( );
				LDAPUserDetailsContextMapper.updateRoles(userDetails);
			}
		}
	}

};
