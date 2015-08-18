package org.mindinformatics.grails.domeo.dashboard.security.oauth;

import org.mindinformatics.grails.domeo.dashboard.security.User;
import org.scribe.builder.ServiceBuilder
import org.scribe.model.OAuthConfig;
import org.scribe.model.OAuthConstants;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verifier
import org.scribe.oauth.OAuthService
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**Controller used to handle the steps used in approval OAuth connection to Annotopia.
 * @author Tom Wilkin */
class AnnotopiaController {
	
	def grailsApplication;
	def springSecurityService;
	
	/**
	 * User injection
	 * @return The logged user
	 */
	 private def injectUserProfile() {
		def principal = springSecurityService.principal
		if(principal.equals("anonymousUser")) {
			redirect(controller: "login", action: "index");
		} else {
			String username = principal.username
			def user = User.findByUsername(username);
			if(user==null) {
				render (view:'error', model:[message: "User not found for username: "+username]);
			}
			
			user
		}
	 }
	
	def index = {
		redirect(action: "authenticate", mapping: "/authenticate")
	}

	/** Method to redirect to the configured Annotopia authenticate page. */
	def authenticate( ) {
		// redirect the user to the Annotopia authorisation URL
		println "sdfasdfasadfsadfasfasfasfsafsafassdfadfasdfasffssafDDDDDDDDDD"
		if(isAnnotopiaOAuthEnabled( )) {
			println "sdfasdfasdfadfasdfasffssafDDDDDDDDDD"
			redirect(url: getService( ).getAuthorizationUrl( ));
		}
	}
	
	/** Redirect for the Annotopia authenticate page which will return with the
	 * response code once the user authorises the access. */
	def callback( ) {
		// request the access token using the verification code
		if(isAnnotopiaOAuthEnabled( )) {
			User user = injectUserProfile( );
			getService( ).requestAccessToken(user, params.code);
			session['annotopia:oasAccessToken'] = user.getAnnotopiaAccessToken( );
			return redirect(uri: "/")
		}
	}
	
	/** Redirect for the application to ensure the access token is still valid. */
	def refresh( ) {
		// request the access token via the refresh token
		if(isAnnotopiaOAuthEnabled( )) {
			User user = injectUserProfile( );
			getService( ).requestAccessTokenByRefreshToken(user);
			session['annotopia:oasAccessToken'] = user.getAnnotopiaAccessToken( );
			return redirect(uri: "/")
		}
	}

	/** Method to construct an instance of the AnnotopiaOAuthService.
	 * @return An instance of the AnnotopiaOAuthService. */
	private AnnotopiaOAuthService getService( ) {
		return new ServiceBuilder( )
			.provider(AnnotopiaApi.class)		
			.apiKey(grailsApplication.config.domeo.storage.annotopia.oauth.system.name)
			.apiSecret(grailsApplication.config.domeo.storage.annotopia.oauth.system.secret)
			.callback("/annotopia/callback")
			.build( );
	}
	
	/** Return whether OAuth is enabled for Annotopia.
	 * @return Whether OAuth is enabled for Annotopia. */
	private boolean isAnnotopiaOAuthEnabled( ) {
		return (grailsApplication.config.domeo.storage.annotopia.enabled.equalsIgnoreCase("true") 
			&& grailsApplication.config.domeo.storage.annotopia.oauth.enabled.equalsIgnoreCase("true"));
	}
	
};
