<%-- by Paolo Ciccarese --%>
<%-- 
Parameters list
 1) item | instance of UserCreateCommand
Stylesheet
 1) fieldError | background and font color in erroneous text fields
--%>
<%@ page import="org.mindinformatics.grails.domeo.dashboard.security.UserStatus" %>
<%@ page import="org.mindinformatics.grails.domeo.dashboard.security.DefaultRoles" %>
<tr>
	<td valign="top">
		<table>
			<tbody>
				<tr class="prop">
					<td valign="top" width="190px" class="name">
						<label for="username">Username*</label>
					</td>
					<td valign="top" width="155px" class="value">
						<div>
							<g:textField name="username" style="width: 240px;"
								value="${item?.username}" class="${hasErrors(bean: item, field: 'username', 'fieldError')}"/>
						</div>
					</td>
					<td valign="top"  class="caption">
						<g:if test="${item?.username!=null}">
							<g:renderErrors bean="${item}" field="username" />
						</g:if> 
						<g:else>
				        	(8-60 chars)
				        </g:else>
				    </td>
				</tr>
				<tr class="prop">
					<td valign="top" class="name">
						<label for="password">Password*</label>
					</td>
					<td valign="top" class="value">
						<g:textField name="password" style="width: 240px;"
							value="${item?.password}" class="${hasErrors(bean: item, field: 'password', 'fieldError')}"/>
					</td>
					<td valign="top" class="caption">
						<g:if test="${item?.password!=null}">
							<g:renderErrors bean="${item}" field="password" />
						</g:if> 
						<g:else>
			           		(6-16 chars)
			            </g:else>
			        </td>
				</tr>
				<tr class="prop">
					<td valign="top" class="name">
						<label for="passwordConfirmation">Re-type Password*</label>
					</td>
					<td valign="top" class="value">
						<g:textField name="passwordConfirmation" style="width: 240px;"
							value="${item?.passwordConfirmation}"  class="${hasErrors(bean: item, field: 'passwordConfirmation', 'fieldError')}"/>
					</td>
					<td valign="top" class="caption">
						<g:if test="${item?.passwordConfirmation!=null}">
							<g:renderErrors bean="${item}" field="passwordConfirmation" />
						</g:if> 
						<g:else>
			           		(6-16 chars)
			            </g:else>
			        </td>
				</tr>
				<tr class="prop">
					<td valign="top" class="name" >
						<label for="userRole">Role</label>
					</td>
					<td valign="top" class="value" colspan="2">
						<div>
							<g:if test="${roles!=null}">
								<g:each in="${roles}">
									<g:if test="${it.authority!=DefaultRoles.ADMIN.value()}">
										<g:if test="${it.label==defaultRole.label}">
											<g:checkBox name="${it.label}" value="${true}" /> ${it.label}
										</g:if>
										<g:else>
											<g:checkBox name="${it.label}" /> ${it.label}
										</g:else>
									</g:if>
								</g:each>
							</g:if>
						</div>
					</td>
				</tr>
				<tr class="prop">
					<td valign="top"  class="name">
						<label for="userStatus">Account Status</label>
					</td>
					<td valign="top" class="value" colspan="2">
						<div>
							<g:if test="${item?.status==UserStatus.CREATED_USER.value()}">
								<g:radio name="status" value="${UserStatus.CREATED_USER.value()}" checked="checked"/> New account
								<g:radio name="status" value="${UserStatus.ACTIVE_USER.value()}"/> Active account
							</g:if>
							<g:elseif test="${item?.status==UserStatus.ACTIVE_USER.value()}">
								<g:radio name="status" value="${UserStatus.CREATED_USER.value()}"/> New account
								<g:radio name="status" value="${UserStatus.ACTIVE_USER.value()}" checked="checked"/> Active account
							</g:elseif>
							<g:else>
								<g:radio name="status" value="${UserStatus.CREATED_USER.value()}" checked="checked"/> New account
								<g:radio name="status" value="${UserStatus.ACTIVE_USER.value()}"/> Active account
							</g:else>
						</div>
					</td>
				</tr>
			</tbody>
		</table>
	</td>
</tr>
