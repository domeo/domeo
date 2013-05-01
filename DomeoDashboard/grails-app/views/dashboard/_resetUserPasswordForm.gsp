<%-- by Paolo Ciccarese --%>
<%-- 
Parameters list
 1) item | instance of UserCreateCommand
 2) error | errors in text format
--%>
<div class="title">
	 	${user?.displayName} password reset
	</div>
<div id="request" class="sectioncontainer">
<g:form method="post" >
		<div class="dialog" >
				<g:hiddenField name="id" value="${user?.id}" /> 
				<g:hiddenField name="user" value="${user?.id}" /> 
				<table style="width: 540px;">
					<tr>
						<td valign="top" colspan="2">
							<g:if test="${msgError!=null}">
								${msgError}
							</g:if>
						</td>
					</tr>
					<tr class="prop">
					<td valign="top" class="name">
						<label for="password">Password*</label>
					</td>
					<td valign="top" class="value">
						<g:textField name="password" style="width: 240px;"
							class="${hasErrors(bean: item, field: 'password', 'fieldError')}"/>
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
							class="${hasErrors(bean: user, field: 'passwordConfirmation', 'fieldError')}"/>
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
					<tr>
						<td valign="top" colspan="3" >
							<div class="buttons">
								<span class="button">
									<g:actionSubmit class="password" action="saveUserPassword" value="${message(code: 'default.button.edit.account.label', default: 'Save password')}" />
								</span>
								<span class="button">
									<g:actionSubmit class="cancel" action="showUser" value="${message(code: 'default.button.edit.account.label', default: 'Cancel')}" />
								</span>
							</div>
						</td>
					</tr>
				</table>
		</div>
</g:form>
</div>