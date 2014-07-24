import org.mindinformatics.grails.domeo.dashboard.security.ldap.LDAPAuthenticationApplicationListener;
import org.mindinformatics.grails.domeo.dashboard.security.ldap.LDAPUserDetailsContextMapper;

// Place your Spring DSL code here
beans = {
	
	// For LDAP
	ldapUserDetailsMapper(LDAPUserDetailsContextMapper) {
		grailsApplication = ref("grailsApplication")
	}
	
	applicationListener(LDAPAuthenticationApplicationListener)
	
}
