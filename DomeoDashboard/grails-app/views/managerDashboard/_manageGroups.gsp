<div id="request" class="sectioncontainer">
<div class="dialog">
	<div class="title">Groups List for ${user.displayName} - total# ${usergroups.size()}</div>

<div class="list">
	<g:set var="g" value="${group}"/>
	<table class="tablelist">
		<thead>
			<tr>
				<g:sortableColumn property="name" title="${message(code: 'agentPerson.id.label', default: 'Name')}" />
				<g:sortableColumn property="nickname" title="${message(code: 'agentPerson.id.label', default: 'Short')}" />
				<g:sortableColumn property="description" title="${message(code: 'agentPerson.id.label', default: 'Description')}" />
				<g:sortableColumn property="dateCreated" title="${message(code: 'agentPerson.id.label', default: 'Mamber since')}" />
				<g:sortableColumn property="lastUpdated" title="${message(code: 'agentPerson.id.label', default: 'Last updated')}" />
				<g:sortableColumn property="role" title="${message(code: 'agentPerson.id.label', default: 'Role')}" />
				<g:sortableColumn property="status" title="${message(code: 'agentPerson.id.label', default: 'Membership')}" />
				<th>Actions on Membership</th>
			</tr>
		</thead>
		<tbody>
			<g:each in="${usergroups}" status="i" var="usergroup">
				<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
		     		<td>
						<g:link action="showGroup" id="${usergroup.group.id}">
		     				${usergroup.group.name}
						</g:link>
		     		</td>
		     		<td>
						${usergroup.group.shortName}
		     		</td>
		     		<td>
						${usergroup.group.description}
		     		</td>
		     		<td><g:formatDate format="MM/dd/yyyy hh:mm" date="${usergroup.dateCreated}"/></td>
		     		<td><g:formatDate format="MM/dd/yyyy hh:mm" date="${usergroup.lastUpdated}"/></td>
		     		<td>
		     			<g:each in="${usergroup.roles}">
			     			${it.label}
			     		</g:each>
		     		</td>
		     		<td>
			     		${usergroup.status.label}
		     		</td>
		     		<td>
			     		<div class="buttons">
							<g:form>
								<g:hiddenField name="id" value="${usergroup.group?.id}" /> 
								<g:hiddenField name="user" value="${user.id}" /> 
								<g:hiddenField name="redirect" value="manageUserGroups" />
									<span class="button">
										<g:actionSubmit class="edit" action="editUserRoleInGroup" value="${message(code: 'default.button.edit.account.label', default: 'Edit Role')}" />
									</span>
									<g:if test="${usergroup.status.value == 'IG_LOCKED'}">
										<span class="button">
											<g:actionSubmit class="unlock" action="unlockUserinGroup" value="${message(code: 'default.button.unlock.account.label', default: 'Unlock')}" />
										</span>
									</g:if>
									<g:elseif test="${usergroup.status.value == 'IG_ACTIVE' || usergroup.status.value == 'IG_SUSPENDED' || usergroup.status.value == 'IG_DELETED'}">
										<span class="button">
											<g:actionSubmit class="lock" action="lockUserInGroup" value="${message(code: 'default.button.lock.account.label', default: 'Lock')}"
											onclick="return confirm('${message(code: 'default.button.lock.account.confirm.message', default: 'Are you sure you want to lock the user membership in this group?')}');" />
										</span>
									</g:elseif>
									<g:if test="${usergroup.status.value == 'IG_SUSPENDED' || usergroup.status.value == 'IG_DELETED'}">
										<span class="button">
											<g:actionSubmit class="enable" action="enableUserInGroup" value="${message(code: 'default.button.enable.account.label', default: 'Activate')}" />
										</span>
									</g:if>
									<g:elseif test="${usergroup.status.value == 'IG_ACTIVE' || usergroup.status.value == 'IG_LOCKED'}">
										<span class="button">
											<g:actionSubmit class="disable" action="disableUserInGroup" value="${message(code: 'default.button.disable.account.label', default: 'Suspend')}" 
											onclick="return confirm('${message(code: 'default.button.disable.account.confirm.message', default: 'Are you sure you want to disable the user membership in this group?')}');"/>
										</span>
									</g:elseif>
									<span class="button">
										<g:actionSubmit class="deleteUser" action="removeUserFromGroup" value="${message(code: 'default.button.edit.account.label', default: 'Remove')}" 
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
   		<g:paginate total="${groupsTotal}" />
	</div>
</div>
</fieldset>
</div>
</div>