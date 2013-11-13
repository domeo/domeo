<div id="request" class="sectioncontainer">
<div class="dialog">
	<div class="title">
		Users of group ${group.name} - total# ${usersTotal}
	</div>
	<div class="list">
	<table class="tablelist">
		<thead>
			<tr>
				<g:sortableColumn property="username" title="${message(code: 'agentPerson.id.label', default: 'Username')}" />
				<g:sortableColumn property="name" title="${message(code: 'agentPerson.id.label', default: 'Name')}" />
				<g:sortableColumn property="isAdmin" title="${message(code: 'agentPerson.id.label', default: 'Adm')}" />
				<g:sortableColumn property="isManager" title="${message(code: 'agentPerson.id.label', default: 'Mgr')}" />
				<g:sortableColumn property="isUser" title="${message(code: 'agentPerson.id.label', default: 'Usr')}" />
				<g:sortableColumn property="dateCreated" title="${message(code: 'agentPerson.id.label', default: 'Member Since')}" />
				<g:sortableColumn property="lastUpdated" title="${message(code: 'agentPerson.id.label', default: 'Last updated')}" />
				<g:sortableColumn property="status" title="${message(code: 'agentPerson.id.label', default: 'Status')}" />
				<th>Actions</th>
			</tr>
		</thead>
		<tbody>
			<g:each in="${users}" status="i" var="user">
				<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
		     		<td><g:link controller="dashboard" action="showProfile" id="${user.id}">${user.username}</g:link></td>
		     		<td>${user.lastName} ${user.firstName} <g:if test="${user?.displayName?.length()>0}">(${user.displayName})</g:if></td>
		     		<%--<td>${user.getRole()}</td>--%>
		     		<td>
		     			${user.isAdmin}
				    </td>
				    <td>
		     			${user.isManager}
				    </td>
				     <td>
		     			${user.isUser}
				    </td>
		     		<td><g:formatDate format="MM/dd/yyyy hh:mm" date="${user.dateCreated}"/></td>
		     		<td><g:formatDate format="MM/dd/yyyy hh:mm" date="${user.lastUpdated}"/></td>
		     		<td>
						${user.status}
					</td>
					<td>
			     		<div class="buttons">
							<g:form>
								<g:hiddenField name="id" value="${group?.id}" /> 
								<g:hiddenField name="redirect" value="manageUserGroups" />
								<span class="button">
									<g:actionSubmit class="deleteUser" action="removeUser" value="${message(code: 'default.button.edit.account.label', default: 'Remove')}" />
								</span>
							</g:form>
						</div>	     		
		     		</td>
		     		<%-- 
		     		<td>
		     			<div class="buttons">
							<g:form>
								<g:hiddenField name="id" value="${user?.id}" /> 
								<g:hiddenField name="redirect" value="listUsers" />
								<span class="button">
									<g:actionSubmit class="edit" action="editUser" value="${message(code: 'default.button.edit.account.label', default: 'Edit')}" />
								</span>
								<g:if test="${fieldValue(bean: user, field: 'accountLocked') == 'true'}">
									<span class="button">
										<g:actionSubmit class="unlock" action="unlockUser" value="${message(code: 'default.button.unlock.account.label', default: 'Unlock')}" />
									</span>
								</g:if>
								<g:elseif test="${fieldValue(bean: user, field: 'accountLocked') == 'false'}">
									<span class="button">
										<g:actionSubmit class="lock" action="lockUser" value="${message(code: 'default.button.lock.account.label', default: 'Lock')}"
										onclick="return confirm('${message(code: 'default.button.lock.account.confirm.message', default: 'Are you sure you want to lock this account?')}');" />
									</span>
								</g:elseif>
								<g:if test="${fieldValue(bean: user, field: 'enabled') == 'true'}">
									<span class="button">
										<g:actionSubmit class="disable" action="disableUser" value="${message(code: 'default.button.disable.account.label', default: 'Disable')}" 
										onclick="return confirm('${message(code: 'default.button.disable.account.confirm.message', default: 'Are you sure you want to disable this account?')}');"/>
									</span>
								</g:if>
								<g:elseif test="${fieldValue(bean: user, field: 'enabled') == 'false'}">
									<span class="button">
										<g:actionSubmit class="enable" action="enableUser" value="${message(code: 'default.button.enable.account.label', default: 'Enable')}" />
									</span>
								</g:elseif>
								<span class="button">
									<g:actionSubmit class="password" action="resetPassword" value="${message(code: 'default.button.resetpwd.label', default: 'Reset password')}" 
									onclick="return confirm('${message(code: 'default.button.resetpwd.account.confirm.message', default: 'Are you sure you want to reset the password?')}');"/>
								</span>
							</g:form>
						</div>
		     		</td>
		     		--%>
		     	</tr>
		     	
			</g:each>
		</tbody>
	</table>
	<%-- 
	<div class="paginateButtons">
   		<g:paginate total="${usersTotal}" params="[groupId: groupId]"/>
	</div>
	--%>
	
</div>
</div>
</div>