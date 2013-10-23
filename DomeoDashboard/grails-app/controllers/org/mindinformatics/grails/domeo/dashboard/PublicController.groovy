package org.mindinformatics.grails.domeo.dashboard

import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugin.springsecurity.userdetails.GrailsUser

import org.mindinformatics.grails.domeo.dashboard.security.DefaultRoles
import org.mindinformatics.grails.domeo.dashboard.security.RoleType
import org.mindinformatics.grails.domeo.dashboard.security.UserRole
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.GrantedAuthorityImpl
import org.springframework.security.web.savedrequest.DefaultSavedRequest

class PublicController {

	def emailingService
	def springSecurityService;
	
	def index = {
		redirect(action:'_access');
	}
	
	def _access = {
		redirect(controller:'openId', action:'auth');
		//render(view:'access');
	}
	
	def signup = {
		render(view:'signup', model:[menuitem: 'signup']);
	}
	
	def credits = {
		render(view:'credits', model:[menuitem: 'credits']);
	}
	
	def nodeinfo = {
		render(view:'nodeinfo', model:[menuitem: 'nodeinfo', administrator: UserRole.findByRole(RoleType.findByAuthority(DefaultRoles.ADMIN.value())).user.displayName,
			affiliation: UserRole.findByRole(RoleType.findByAuthority(DefaultRoles.ADMIN.value())).user.affiliation]);
	}
	
	def openid = {
		render(view:'openid', model:[menuitem: 'openid']);
	}
	
	def saveOpenIdLink = {
		def config = SpringSecurityUtils.securityConfig
		
		String openId = session[OIAFH.LAST_OPENID_USERNAME] 
		if (!openId) { 
			flash.error = 'Sorry, an OpenID was not found' 
			redirect uri: config.failureHandler.defaultFailureUrl 
			return 
		}
		
		def user = new GrailsUser(openId, 'password', true, true, true, true, [new GrantedAuthorityImpl('ROLE_OPENID')], 0)
		
		SCH.context.authentication = new UsernamePasswordAuthenticationToken( user, 'password', user.authorities)
		
		session.removeAttribute OIAFH.LAST_OPENID_USERNAME session.removeAttribute OIAFH.LAST_OPENID_ATTRIBUTES
		
		def savedRequest = session[DefaultSavedRequest.SPRING_SECURITY_SAVED_REQUEST_KEY] 
		if (savedRequest && !config.successHandler.alwaysUseDefault) { 
			redirect url: savedRequest.redirectUrl 
		} else { 
			redirect uri: config.successHandler.defaultTargetUrl 
		}
		// Verify OpenId format
		// Verify existence of user
		// Verify non presence of OpenId
		
		render(params.username)
		render(params.password)
		render(params.openId)
		//render(view:'openid');
	}
	
	def saveAccountRequest = {
		UserSignupCommand userSignupCommand->
        def appBase = request.getContextPath();
		if(userSignupCommand.hasErrors()) {
			userSignupCommand.errors.allErrors.each { println it }
			render(view:'signup', model:[item:userSignupCommand])
		} else {
			if(userSignupCommand.isPasswordValid()) {
				if(userSignupCommand.isUsernameAvailable()) {
					if(userSignupCommand.isEmailAvailable()) {
						def accountRequest = userSignupCommand.createAccountRequest()
						if(accountRequest)  {
							if(!accountRequest.save()) {
								// Failure in saving
								accountRequest.errors.allErrors.each { println it }
								render(view:'signup', model:[item:userSignupCommand, 
											msgError: 'The request has not been saved successfully'])
							} else {
								try {
									
                                    emailingService.sendAccountRequestConfirmation(appBase, userSignupCommand);
                                    /*
                                    log.info('Sending confirmation email to ' + userSignupCommand.email)
									mailService.sendMail {
										to userSignupCommand.email
										subject "Confirmation of Domeo account creation"
										html "Dear " + userSignupCommand.firstName + ", <br/>"+
                                            " your account has been created and it is waiting to be activated by an administrator. <br/><br/>" + 
                                            "You will receive an email when the activation process is completed." +
											"<br/><br/>Sincerely, <br/> "+ grailsApplication.config.domeo.administrator + "<br/>" +
                                            " The Domeo Annotation Tool team";
									}
									*/
                                    
                                    emailingService.sendApprovalRequest(appBase, userSignupCommand);
                                    
                                    /*
									log.info('Sending approval request email for ' + userSignupCommand.email)
									mailService.sendMail {
										to grailsApplication.config.domeo.admin.email.to
										subject "New user request awaiting for confirmation"
										html "<b>Dear Paolo,</b> a new user request by " + userSignupCommand.email + "is awaiting for confirmation"
									}
									*/
								} catch(Exception e) {
									log.error(e.getMessage());
								}
							
								redirect (action:'signupConfirmation',id: accountRequest.id, model: [
											msgSuccess: 'Signup completed successfully!']);
							}
						} else {
							// User already existing
							render(view:'signup', model:[item:userSignupCommand, errorCode: "2"])
						}
					} else {
						render(view:'signup', model:[item:userSignupCommand, errorCode: "3"])
					}
				} else {
					render(view:'signup', model:[item:userSignupCommand, errorCode: "2"])
				}
			} else {
				render(view:'signup', model:[item:userSignupCommand, errorCode: "1"])
			}
			// Put data in temporary table
			// Send email with link to activate account
			// Go to home
		}
		//redirect(controller:'openId', action:'auth');
	}
	
	def signupConfirmation = {
		render(view:'notification', model:[ title: 'Signup completed successfully!',
			message: ' Check your email, you should have received a confirmation email. If you haven\'t, it might be still ok, your request should be now awaiting for approval. Thank you!'])	
	}
}
