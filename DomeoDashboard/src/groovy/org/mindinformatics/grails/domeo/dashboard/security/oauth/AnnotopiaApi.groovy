package org.mindinformatics.grails.domeo.dashboard.security.oauth;

import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.scribe.builder.api.DefaultApi20
import org.scribe.model.OAuthConfig
import org.scribe.oauth.OAuthService
import org.scribe.utils.OAuthEncoder

/** OAuth API definition for Annotopia.
 * @author Tom Wilkin */
final class AnnotopiaApi extends DefaultApi20 {
	
	/** The path to append to the Annotopia URL to request an authorisation code. */
	private static final String AUTH_URL = "oauth/authorize?response_type=code&client_id=%s&redirect_uri=%s";
	
	/** The path to append to the Annotopia URL to request an access token. */
	private static final String TOKEN_URL = "oauth/token?grant_type=%s";
	
	def grailsApplication = ApplicationHolder.application;
	
	AnnotopiaApi( ) { }
	
	@Override
	public AnnotopiaTokenExtractor getAccessTokenExtractor( ) {
		return new AnnotopiaTokenExtractor( );
	}

	@Override
	public String getAccessTokenEndpoint( ) {
		String path = grailsApplication.config.domeo.storage.annotopia.storage + TOKEN_URL;
		return path;
	}

	@Override
	public String getAuthorizationUrl(final OAuthConfig config) {
		String path = String.format(grailsApplication.config.domeo.storage.annotopia.storage + AUTH_URL, config.apiKey, 
			OAuthEncoder.encode(grailsApplication.config.grails.serverURL.toString( ) + config.callback));
		println 'path ' + path
		return path;
	}
	
	@Override
	public OAuthService createService(final OAuthConfig config) {
		return new AnnotopiaOAuthService(this, config);
	}

};
