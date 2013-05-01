<div id="request" class="sectioncontainer">
<div class="dialog">
	<div class="title">Subscriptions List for ${user.displayName} - total# ${circlesTotal}</div>

<div class="list">
	<g:set var="g" value="${userCircles}"/>
	<table class="tablelist">
		<thead>
			<tr>
				<g:sortableColumn property="name" title="${message(code: 'agentPerson.id.label', default: 'Name')}" />
				<g:sortableColumn property="nickname" title="${message(code: 'agentPerson.id.label', default: 'Short')}" />
				<g:sortableColumn property="users" title="${message(code: 'agentPerson.id.label', default: 'Users')}" />
				<g:sortableColumn property="groups" title="${message(code: 'agentPerson.id.label', default: 'Groups')}" />
				<th>Actions</th>
			</tr>
		</thead>
		<tbody>
			<g:each in="${userCircles}" status="i" var="userCircle">
				<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
		     		<td>
						<g:link action="showCircle" id="${userCircle.circle.id}">
		     				${userCircle.circle.name}
						</g:link>
		     		</td>
		     		<td>
						${userCircle.circle.shortName}
		     		</td>
		     		<td>
		     			${userCircle.users.size()}
		     		</td>
		     		<td>
		     			${userCircle.groups.size()}
		     		</td>
		     		<td>
			     		<div class="buttons">
							<g:form>
								<g:hiddenField name="id" value="${userCircle.circle?.id}" /> 
								<g:hiddenField name="user" value="${user.id}" /> 
								<g:hiddenField name="redirect" value="showCircles" />
									<span class="button">
										<g:actionSubmit class="editCircle" action="editCircle" value="${message(code: 'default.button.edit.account.label', default: 'Edit')}" />
									</span>
									<span class="button">
										<g:actionSubmit class="deleteCircle" action="deleteCircle" value="${message(code: 'default.button.edit.account.label', default: 'Delete')}" 
										onclick="return confirm('${message(code: 'default.button.disable.account.confirm.message', default: 'Are you sure you want to remove the user from the group?')}');"/>
									</span>
							</g:form>
						</div>	     		
		     		</td>
		     	</tr>
			</g:each>
		</tbody>
	</table>
	<div class="paginateButtons">
   		<g:paginate total="${circlesTotal}" />
	</div>
</div>
</fieldset>

</div>

</div>