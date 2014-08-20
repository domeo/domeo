package org.mindinformatics.grails.domeo.dashboard.security.oauth;

import org.apache.commons.codec.binary.Base64;
import org.codehaus.groovy.grails.commons.ApplicationHolder;
import org.mindinformatics.grails.domeo.dashboard.security.User
import org.scribe.model.OAuthConfig;
import org.scribe.model.OAuthConstants;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.springframework.security.core.context.SecurityContextHolder;

/** Service used to provide OAuth access to Annotopia.
 * @author Tom Wilkin */
class AnnotopiaOAuthService extends OAuth20Service {
	
	def grailsApplication = ApplicationHolder.application;
	
	/** The OAuth API instance for Annotopia. */
	private AnnotopiaApi api;
	
	/** The OAuth configuration for Annotopia. */
	private OAuthConfig config;

	/** Construct an AnnotopiaOAuthService.
	 * @param api The AnnotopiaAPI instance.
	 * @param config The OAuth configuration for Annotopia. */
	AnnotopiaOAuthService(final AnnotopiaApi api, final OAuthConfig config) {
		this.api = api;
		this.config = config;
	}

	@Override
	public Token getAccessToken(final Verifier verifier) {
		// request the access token
		OAuthRequest request = initialiseRequest("authorization_code");

		// add query parameters
		request.addQuerystringParameter(OAuthConstants.CODE, verifier.getValue( ));
		Response response = request.send( );
		if(response.code == 401 || response.code == 403) {
			throw new Exception("Client id or client secret is incorrect.\n" + response.body);
		}
		Token token = api.getAccessTokenExtractor( ).extract(response.getBody( ));
		return token;
	}

	@Override
	public String getAuthorizationUrl( ) {
		return api.getAuthorizationUrl(config);
	}

	@Override
	public void signRequest(final Token token, final OAuthRequest request) {
		request.addQuerystringParameter(OAuthConstants.ACCESS_TOKEN, token.getToken( ));
	}
	
	/** Request a new access token for this user with the verification code returned from
	 * Annotopia.
	 * @param user The user to request the access token for.
	 * @param code The verification code returned from Annotopia. */
	public void requestAccessToken(final User user, final String code) {
		Verifier verifier = new Verifier(code);
		Token accessToken = getAccessToken(verifier);
		Token refreshToken = api.getAccessTokenExtractor( ).extractRefresh(accessToken);
		storeTokens(user, accessToken, refreshToken);
	}
	
	/** Request a new access token for this user with their refresh token.
	 * @param user The user to request the access token for. */
	public void requestAccessTokenByRefreshToken(final User user) {
		Token accessToken = getAccessTokenByRefreshToken(user.getAnnotopiaRefreshToken( ));
		Token refreshToken = api.getAccessTokenExtractor( ).extractRefresh(accessToken);
		storeTokens(user, accessToken, refreshToken);
	}
	
	/** Initialise the OAuthRequest using the specified grant type.
	 * @param grantType The grant type for the request.
	 * @return The initialised OAuthRequest. */
	private OAuthRequest initialiseRequest(final String grantType) {
		String endpoint = String.format(api.getAccessTokenEndpoint( ), grantType);
		OAuthRequest request = new OAuthRequest(api.getAccessTokenVerb( ), endpoint);
		
		
		// add the client credentials
		String unencoded = config.getApiKey( ) + ":" + config.getApiSecret( );
		String secret = Base64.encodeBase64String(unencoded.getBytes( ));
		request.addHeader("Authorization", "Basic " + secret)

		// add query parameters
		request.addQuerystringParameter(OAuthConstants.REDIRECT_URI, grailsApplication.config.grails.serverURL.toString( ) + config.getCallback( ));
		
		return request;
	}

	/** Request a new access token using a refresh token.
	 * @param refreshToken The refresh token to use to request an access token.
	 * @return The new access token. */
	private Token getAccessTokenByRefreshToken(final String refreshToken) {
		// request the access token
		OAuthRequest request = initialiseRequest("refresh_token");
		
		// add query parameters
		request.addQuerystringParameter("refresh_token", refreshToken);
		Response response = request.send( );
		if(response.code == 401 || response.code == 403) {
			throw new Exception("Client id or client secret is incorrect.\n" + response.body);
		}
		Token token = api.getAccessTokenExtractor( ).extract(response.getBody( ));
		return token;
	}
	
	/** Store the new tokens for the given user.
	 * @param user The user the tokens belong to.
	 * @param access The new access token for this user.
	 * @param refresh The new refresh token for this user. */
	private void storeTokens(final User user, final Token access, final Token refresh) {
		user.setAnnotopiaAccessToken(access.getToken( ));
		user.setAnnotopiaRefreshToken(refresh.getToken( ));
		user.save(flush: true);
	}
	
};
