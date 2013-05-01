<%-- by Paolo Ciccarese --%>
<%-- 
Parameters list
 1) item | instance of GroupCreateCommand
 2) error | errors in text format
--%>
	<div class="sectioncontainer">
<g:form method="post" >

<div class="dialog" >
<div class="title">Edit ${usergroup.user.displayName} Membership in Group ${usergroup.group.name}</div>
	<g:hiddenField name="group" value="${usergroup.group.id}" /> 
	<g:hiddenField name="user" value="${usergroup.user.id}" /> 
	<g:hiddenField name="redirect" value="manageUserGroups" /> 
	<table style="width: 550px">
		<tr>
			<td valign="top" colspan="2">
				<g:if test="${msgError!=null}">
					${msgError}
				</g:if>
			</td>
		</tr>
		<g:render template="/shared/userInGroupEditFields" plugin="domeo-dashboard" />
		<tr>
			<td valign="top" colspan="2">
				<div class="buttons">
					<span class="button">
						<g:actionSubmit class="save" action="updateUserInGroup"  id="${usergroup.user?.id}" value="${message(code: 'default.button.edit.account.label', default: 'Save membership')}" />
					</span>
					<span class="button">
						<g:actionSubmit class="cancel" action="showUser" id="${usergroup.user?.id}" value="${message(code: 'default.button.edit.account.label', default: 'Cancel')}" />
					</span>
				</div>
			</td>
		</tr>
	</table>
	</div>
</g:form>
</div>