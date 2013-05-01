<%-- by Paolo Ciccarese --%>
<%-- 
Parameters list
 1) item | instance of UserCreateCommand
 2) error | errors in text format
--%>
<div class="title">
	 	New ${user?.displayName} Open ID
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
						<label for="openId">Open ID</label>
					</td>
					<td valign="top" class="value">
						<g:textField name="openId" style="width: 240px;"
							class="${hasErrors(bean: item, field: 'password', 'fieldError')}"/>
					</td>
					<td valign="top" class="caption">
						<g:if test="${item?.openId!=null}">
							<g:renderErrors bean="${item}" field="openId" />
						</g:if> 
						<g:else>
			           		Valid Open ID
			            </g:else>
			        </td>
				</tr>
					<tr>
						<td valign="top" colspan="3" >
							<div class="buttons">
								<span class="button">
									<g:actionSubmit class="save" controller="openId" action="createAccount" value="${message(code: 'default.button.edit.account.label', default: 'Save open Id')}" />
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