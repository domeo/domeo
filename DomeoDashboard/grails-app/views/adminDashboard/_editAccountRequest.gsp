<%-- by Paolo Ciccarese --%>
<%-- 
Parameters list
 1) user | instance of UserCreateCommand
 2) error | errors in text format
--%>
<div id="request" class="sectioncontainer">
<g:form method="post" >
		<div class="dialog" >
				<g:hiddenField name="id" value="${item?.id}" /> 
				<table>
					<tr>
						<td valign="top" colspan="2">
							<g:if test="${msgError!=null}">
								${msgError}
							</g:if>
						</td>
					</tr>
					<g:render template="/dashboard/accountRequestForm" plugin="domeo-dashboard" />
					<!-- g:render template="/dashboard/profileAccountForm" plugin="domeo-dashboard" />  -->
					<tr>
						<td valign="top" colspan="2" >
							<div class="buttons">
								<span class="button">
									<g:actionSubmit class="save" controller="generalDashboard" action="updateAccountRequest" value="${message(code: 'default.button.edit.account.label', default: 'Update account request')}" />
								</span>
								<span class="button">
									<g:actionSubmit controller="adminDashboard" class="cancel" action="moderateAccountsRequests" value="${message(code: 'default.button.edit.account.label', default: 'Cancel')}" />
								</span>
							</div>
						</td>
					</tr>
				</table>
		</div>
</g:form>
</div>