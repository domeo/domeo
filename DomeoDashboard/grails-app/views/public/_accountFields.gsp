<%-- by Paolo Ciccarese --%>
<%-- Parameters list
 1) item | instance of UserCreateCommand
Stylesheet
 1) fieldError | background and font color in erroneous text fields
--%>
<%@ page import="org.mindinformatics.grails.domeo.dashboard.security.UserStatus" %>
<!-- Begin account fields -->
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
							<g:if test="${errorCode=="2"}">
								<g:textField name="username" style="width: 240px;"
									value="${item?.username}" class="fieldError"/>
							</g:if>
							<g:else>
								<g:textField name="username" style="width: 240px;"
									value="${item?.username}" class="${hasErrors(bean: item, field: 'username', 'fieldError')}"/>
							</g:else>
						</div>
					</td>
					<td valign="top"  class="caption">
						(4-60 chars)
						<g:if test="${item && item.errors.hasFieldErrors('username')}">
							 ${item.errors.getFieldError("username").getCode() }
						</g:if> 
						<g:if test="${errorCode=="2"}">
							Username already existing
						</g:if>
				    </td>
				</tr>
				<tr class="prop">
					<td valign="top" class="name">
						<label for="password">Password*</label>
					</td>
					<td valign="top" class="value">
						<g:if test="${errorCode=="1"}">
							<g:passwordField name="password" style="width: 240px;"
								value="${item?.password}" class="fieldError"/>
						</g:if>
						<g:else>
							<g:passwordField name="password" style="width: 240px;"
								value="${item?.password}" class="${hasErrors(bean: item, field: 'password', 'fieldError')}"/>
						</g:else>
					</td>
					<td valign="top" class="caption">
						(6-16 chars)
						<g:if test="${item && item.errors.hasFieldErrors('password')}">
							 ${item.errors.getFieldError("password").getCode() }
						</g:if> 
						<g:if test="${errorCode=="1"}">
							Passwords don't match
						</g:if>
			        </td>
				</tr>
				<tr class="prop">
					<td valign="top" class="name">
						<label for="passwordConfirmation">Re-type Password*</label>
					</td>
					<td valign="top" class="value">
						<g:if test="${errorCode=="1"}">
							<g:passwordField name="passwordConfirmation" style="width: 240px;"
								value="${item?.passwordConfirmation}" class="fieldError"/>
						</g:if>
						<g:else>
							<g:passwordField name="passwordConfirmation" style="width: 240px;"
								value="${item?.passwordConfirmation}" class="${hasErrors(bean: item, field: 'passwordConfirmation', 'fieldError')}"/>
						</g:else>
					</td>
					<td valign="top" class="caption">
						(6-16 chars)
						<g:if test="${item && item.errors.hasFieldErrors('passwordConfirmation')}">
							 ${item.errors.getFieldError("passwordConfirmation").getCode() }
						</g:if> 
						<g:if test="${errorCode=="1"}">
							Passwords don't match
						</g:if>
			        </td>
				</tr>
			</tbody>
		</table>
	</td>
</tr>
