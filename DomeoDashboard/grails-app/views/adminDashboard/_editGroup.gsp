<%-- by Paolo Ciccarese --%>
<%-- 
Parameters list
 1) item | instance of GroupCreateCommand
 2) error | errors in text format
--%>
	<div class="sectioncontainer">
<g:form method="post" >

<div class="dialog" >
<div class="title">Edit Group</div>
	<g:hiddenField name="id" value="${item?.id}" /> 
	<table style="width: 460px">
		<tr>
			<td valign="top" colspan="2">
				<g:if test="${msgError!=null}">
					${msgError}
				</g:if>
			</td>
		</tr>
		<g:render template="groupEditFields" plugin="domeo-dashboard" />
		<tr>
			<td valign="top" colspan="2">
				<div class="buttons">
					<span class="button">
						<g:actionSubmit class="save" action="updateGroup" id="${item?.id}" value="${message(code: 'default.button.edit.account.label', default: 'Save group')}" />
					</span>
					<span class="button">
						<g:actionSubmit class="cancel" action="showGroup" id="${item?.id}" value="${message(code: 'default.button.edit.account.label', default: 'Cancel')}" />
					</span>
				</div>
			</td>
		</tr>
	</table>
	</div>
</g:form>
</div>