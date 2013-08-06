package org.mindinformatics.grails.domeo

import org.mindinformatics.grails.domeo.dashboard.security.User
import org.mindinformatics.grails.domeo.persistence.AnnotationSetIndex
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder

class WebController { 
	
	def springSecurityService
	
	def domeo = {

		// Url specified as parameter of the annotator url
		if(params.url && !params.lineageId) { 
			render (view:'domeo', model:[documentUrl: params.url])
			return;
		} else if(params.url && params.lineageId) {
			// If the lineageId is specified, the annotationId is ignored
			render (view:'domeo', model:[documentUrl: params.url, lineageId: params.lineageId])
			return;
		} else if(params.url && params.annotationId) {
			render (view:'domeo', model:[documentUrl: params.url, annotationId: params.annotationId])
			return;
		} else if(params.annotationId) {
			def url = AnnotationSetIndex.findByIndividualUri(params.annotationId).annotatesUrl;
			render (view:'domeo', model:[documentUrl: url, annotationId: params.annotationId])
			return;
		}
		render(view:'domeo');
	}
	
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
	
	public Collection<GrantedAuthority> getAuthorities() {
		//make everyone ROLE_USER
		Collection<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
		GrantedAuthority grantedAuthority = new GrantedAuthority() {
			//anonymous inner type
			public String getAuthority() {
				return "ROLE_USER";
			}
		};
		grantedAuthorities.add(grantedAuthority);
		return grantedAuthorities;
	}
	
	def nifmember = {
		if(grailsApplication.config.domeo.tunnel.active=='true') {
			User user = User.findByUsername(grailsApplication.config.domeo.tunnel.username);
			
			// forcing user login
			Authentication auth =
				new UsernamePasswordAuthenticationToken(user, null, getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(auth);
			injectUserProfile()
			
			String url=null;
			if(params.url) url = params.url;
		
			if(url!=null) {
				redirect(controller:"web",action:"domeo", params:[url:  url] );
			} else {
				SecurityContextHolder.clearContext();
			}
		} else redirect(controller:"web",action:"domeo", params:[url:  params.url] );
	}
}
