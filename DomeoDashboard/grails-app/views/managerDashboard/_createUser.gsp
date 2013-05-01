<%-- by Paolo Ciccarese --%>
<%-- 
Parameters list
 1) item | instance of UserCreateCommand
 2) error | errors in text format
--%>
<div class="sectioncontainer">
	<g:form method="post" >
	 	<div class="dialog" >
		    <div class="title">User Creation </div>
			<table style="width: 700px;">
				<tr>
					<td valign="top" colspan="2">
						<g:if test="${msgError!=null}">
							${msgError}
						</g:if>
					</td>
				</tr>
				<g:render template="/shared/personFields" plugin="domeo-dashboard" />
				<g:render template="/managerDashboard/accountFields" plugin="domeo-dashboard" />
				<tr>
					<td valign="top" colspan="2" >
						<div class="buttons">
							<span class="button">
								<g:actionSubmit class="save" action="saveUser" value="${message(code: 'default.button.edit.account.label', default: 'Save user')}" />
							</span>
							<span class="button">
								<g:actionSubmit class="cancel" action="listUsers" value="${message(code: 'default.button.edit.account.label', default: 'Cancel')}" />
							</span>
						</div>
					</td>
				</tr>
			</table>
		</div>
	</g:form>
</div>