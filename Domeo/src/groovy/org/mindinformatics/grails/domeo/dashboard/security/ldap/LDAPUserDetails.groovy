package org.mindinformatics.grails.domeo.dashboard.security.ldap;

import java.util.Collection;
import grails.plugin.springsecurity.userdetails.GrailsUser;
import org.mindinformatics.grails.domeo.dashboard.security.User;
import org.springframework.security.core.GrantedAuthority;

/** Wrapper for the Spring Security UserDetails object. This allows storage of
 * the Domeo user object when it has been created by the 
 * LDAPUserDetailsContextWrapper class.
 * @author Tom Wilkin */
public class LDAPUserDetails extends GrailsUser {
	
	/** Construct a new LDAPUserDetails object with the given role authorities 
	 * and the specified Domeo user.
	 * @param authorities The role authorities this user has.
	 * @param user The Domeo user to wrap. */
	public LDAPUserDetails(final Collection<GrantedAuthority> authorities, final User user) {
		super(user.getUsername( ), user.getPassword( ), user.isEnabled( ),
				!user.isAccountExpired( ), !user.isPasswordExpired( ),
				!user.getAccountLocked( ), authorities, 
				user.getId( ));
	}

};
