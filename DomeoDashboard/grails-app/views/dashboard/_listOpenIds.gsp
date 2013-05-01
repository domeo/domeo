<%-- by Paolo Ciccarese --%>
<%-- 
Parameters list
 1) item | instance of UserCreateCommand
 2) error | errors in text format
--%>
<div class="title">
	Open IDs for ${user?.displayName}
</div>
<div class="list">
	<g:set var="g" value="${group}"/>
	<table class="tablelist">
		<thead>
			<tr>
				<g:sortableColumn property="openId" title="${message(code: 'agentPerson.id.label', default: 'OpenID')}" />
				<th>Linked on</th>
				<th>Actions</th>
			</tr>
		</thead>
		<tbody>
			<g:each in="${user.openIds}" status="i" var="openId">
				<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
		     		<td>
		     			${openId.url}
		     		</td>
		     		<td>Unknown</td>
		     		<td>
		     			<div class="buttons">
			     			<g:form>
							<%-- <g:hiddenField name="id" value="${usergroup.group?.id}" /> --%>
								<g:hiddenField name="userId" value="${openId.user.id}" /> 
								<g:hiddenField name="url" value="${openId.url}" />
								<g:hiddenField name="redirect" value="listOpenIds" />
								<span class="button">
									<g:actionSubmit class="remove" action="removeOpenId" value="${message(code: 'default.button.edit.account.label', default: 'Remove OpenID')}" 
									onclick="return confirm('${message(code: 'default.button.disable.account.confirm.message', default: 'Are you sure you want to remove the OpenID?')}');"/>
								</span>
							</g:form>
						</div>
					</td>
		     	</tr>
		     </g:each>
		</tbody>
	</table>
</div>