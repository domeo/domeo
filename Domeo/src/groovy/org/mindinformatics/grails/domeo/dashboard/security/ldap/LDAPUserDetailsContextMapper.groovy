package org.mindinformatics.grails.domeo.dashboard.security.ldap;

import org.mindinformatics.grails.domeo.client.profiles.model.DomeoClientProfile
import org.mindinformatics.grails.domeo.client.profiles.model.UserCurrentDomeoClientProfile
import org.mindinformatics.grails.domeo.dashboard.security.Role
import org.mindinformatics.grails.domeo.dashboard.security.User
import org.mindinformatics.grails.domeo.dashboard.security.UserRole
import org.springframework.ldap.core.DirContextAdapter
import org.springframework.ldap.core.DirContextOperations
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper
import org.springframework.transaction.annotation.*

/** Map the details from LDAP to a Domeo user and add that user to the database.
 * Also map the roles from any appropriate AD groups to roles in the database
 * on each login.
 * @author Tom Wilkin */
class LDAPUserDetailsContextMapper implements UserDetailsContextMapper {
	
	def grailsApplication;
	
	@Override
	@Transactional
	public UserDetails mapUserFromContext(final DirContextOperations context,
			final String username, final Collection<GrantedAuthority> authorities)
	{
		// check if the country and affiliation values have been set
		String country = null, affiliation = null;
		if(grailsApplication.config.domeo.ldap.ad.country != null 
			&& !grailsApplication.config.domeo.ldap.ad.country.equals(""))
		{
			country = context.getStringAttribute(
					grailsApplication.config.domeo.ldap.ad.country);
		}
		if(country == null || country.equals("")) {
			country = "Unknown";
		}
		if(grailsApplication.config.domeo.ldap.ad.affiliation != null
			&& !grailsApplication.config.domeo.ldap.ad.affiliation.equals(""))
		{
			affiliation = context.getStringAttribute(
					grailsApplication.config.domeo.ldap.ad.affiliation);
		}
		if(affiliation == null || affiliation.equals("")) {
			affiliation = "Unknown";
		}
		
		// get the values and check they are set
		String firstName, lastName, displayName, email;
		firstName = context.getStringAttribute(
			grailsApplication.config.domeo.ldap.ad.first_name);
		if(firstName == null || firstName.equals("")) {
			firstName = "Unknown";
		}
		lastName = context.getStringAttribute(
			grailsApplication.config.domeo.ldap.ad.last_name);
		if(lastName == null || lastName.equals("")) {
			lastName = "User";
		}
		displayName = context.getStringAttribute(
			grailsApplication.config.domeo.ldap.ad.display_name);
		if(displayName == null || displayName.equals("")) {
			displayName = username;
		}
		email = context.getStringAttribute(
			grailsApplication.config.domeo.ldap.ad.email);
		if(email == null || email.equals("")) {
			email = "unknown@mindinformatics.org";
		}
		
		// check if the user already exists
		User user = User.findByUsername(username);
		if(!user) {
			// create the user instance
			user = new User(
				firstName: firstName,
				lastName: lastName,
				displayName: displayName, 
				country: country,
				affiliation: affiliation, 
				username: username,
				password: "null", 
				email: email, 
				enabled: "true"
			);
		
			// save the user
			if(!user.save(flush: true)) {
				user.errors.allErrors.each { render it }
			}
			
			// load the default profile from configuration
			def defaultProfile = grailsApplication.config.domeo.ldap.ad.defaultProfile;
			if(defaultProfile == null || defaultProfile.equals("")) {
				defaultProfile = DomeoClientProfile.SIMPLE_PROFILE_NAME;
			}
			def profile = DomeoClientProfile.findByName(defaultProfile);
			if(profile == null) {
				throw new Exception("Profile '" + defaultProfile + "' cannot be found.");
			}
			
			// set the user profile
			UserCurrentDomeoClientProfile.findByUser(user)?: new UserCurrentDomeoClientProfile(
				user: user,
				currentProfile: profile
			).save(failOnError: true, flash: true);
		} else {
			// update the user details from AD
			user.setFirstName(firstName);
			user.setLastName(lastName);
			user.setDisplayName(displayName);
			user.setCountry(country);
			user.setAffiliation(affiliation);
			user.setEmail(email);
			user.save(flush: true);
		}
		
		// update the access roles from AD
		updateRoles(user, authorities);
		
		// create the user details for this user
		UserDetails ud = new LDAPUserDetails(authorities, user);
		return ud;
	}

	@Override
	public void mapUserToContext(final UserDetails user, final DirContextAdapter context) {
		// not required		
	}
	
	/** Update the roles for the given user to the given authorities from AD.
	 * @param user The user to update the roles for.
	 * @param authorities The authorities from AD to create roles from. */
	@Transactional
	public static void updateRoles(final User user, final Collection<GrantedAuthority> authorities)	{
		// remove all this user's roles
		UserRole.removeAll(user);
		
		// iterate through the AD roles and add matching roles to the database
		Role[ ] roles = Role.findAll( );
		for(GrantedAuthority authority : authorities) {
			for(Role role : roles) {
				if(authority.getAuthority( ).equals(role.authority)) {
					UserRole userRole = new UserRole(user: user, role: role);
					userRole.save(flush: true);
				}
			}
		}
	}
	
	/** Allow updating of the roles for the given user.
	 * @param ud The LDAPUserDetail instance to update the roles for. */
	@Transactional
	public static void updateRoles(final LDAPUserDetails ud) {
		User user = User.findByUsername(ud.getUsername( ));
		updateRoles(user, ud.getAuthorities( ));
	}

};
