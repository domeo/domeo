<%-- by Paolo Ciccarese --%>
<%-- 
Parameters list
 1) user | instance of UserCreateCommand
 2) error | errors in text format
--%>
<div id="request" class="sectioncontainer">
<g:form method="post" >
		<div class="dialog" >
				<g:hiddenField name="id" value="${user?.id}" /> 
				<g:hiddenField name="username" value="${user?.username}" /> 
				<table>
					<tr>
						<td valign="top" colspan="2">
							<g:if test="${msgError!=null}">
								${msgError}
							</g:if>
						</td>
					</tr>
					<g:render template="/dashboard/profilePersonForm" plugin="domeo-dashboard" />
					<g:render template="/dashboard/profileAccountForm" plugin="domeo-dashboard" />
					<tr>
						<td valign="top" colspan="2" >
							<div class="buttons">
								<span class="button">
									<g:actionSubmit class="save" action="updateProfile" value="${message(code: 'default.button.edit.account.label', default: 'Update my profile')}" />
								</span>
								<span class="button">
									<g:actionSubmit class="cancel" action="showProfile" value="${message(code: 'default.button.edit.account.label', default: 'Cancel')}" />
								</span>
							</div>
						</td>
					</tr>
				</table>
		</div>
</g:form>
</div>