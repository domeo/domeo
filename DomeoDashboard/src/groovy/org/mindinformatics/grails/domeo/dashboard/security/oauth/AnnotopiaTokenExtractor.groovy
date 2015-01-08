package org.mindinformatics.grails.domeo.dashboard.security.oauth;

import groovy.json.JsonSlurper;
import org.scribe.extractors.AccessTokenExtractor;
import org.scribe.model.Token;
import org.scribe.utils.Preconditions;

/** Class to define the custom extraction method to retrieve the access token
 * out of the JSON returned by the Annotopia OAuth.
 * @author Tom Wilkin */
class AnnotopiaTokenExtractor implements AccessTokenExtractor {

	@Override
	public Token extract(final String response) {
		Preconditions.checkEmptyString(response, "Response body is incorrect. Can't extract a token from an empty string.");
		
		// extract the token from the returned JSON
		def json = new JsonSlurper( ).parseText(response);
		return new Token(json.access_token, "", response);
	}
	
	/** Extract the refresh token from the response attached to the
	 * access token.
	 * @param accessToken The access token to extract the response from.
	 * @return The refresh token sent in the response. */
	public Token extractRefresh(final Token accessToken) {
		return extractRefresh(accessToken.getRawResponse( ));
	}
	
	/** Extract the refresh token from the response.
	 * @param response The response to extract the refresh token from.
	 * @return The refresh token sent in the response. */
	public Token extractRefresh(final String response) {
		Preconditions.checkEmptyString(response, "Response body is incorrect. Can't extract a token from an empty string.");
		
		// extract the token from the returned JSON
		def json = new JsonSlurper( ).parseText(response);
		return new Token(json.refresh_token, "", response);
	}

};
