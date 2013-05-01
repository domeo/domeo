<%-- by Paolo Ciccarese --%>
<%-- 
Parameters list
 1) item | instance of UserCreateCommand
 2) error | errors in text format
--%>
<div id="request" class="sectioncontainer">
<g:form method="post" >
		<div class="dialog" >
				<g:hiddenField name="id" value="${item?.id}" /> 
				<g:hiddenField name="username" value="${item?.username}" /> 
				<table>
					<tr>
						<td valign="top" colspan="2">
							<g:if test="${msgError!=null}">
								${msgError}
							</g:if>
						</td>
					</tr>
					<g:render template="/shared/personFields" plugin="domeo-dashboard" />
					<g:render template="/shared/accountEditFields" plugin="domeo-dashboard" />
					<tr>
						<td valign="top" colspan="2" >
							<div class="buttons">
								<span class="button">
									<g:actionSubmit class="save" action="updateUser" value="${message(code: 'default.button.edit.account.label', default: 'Update user')}" />
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