<div id="request" class="sectioncontainer">
<div class="dialog">
	<div class="title">
		Account Requests List - total# ${accountRequestsTotal}
	</div>
	<div class="list">
	<table class="tablelist">
		<thead>
			<tr>
				<g:sortableColumn property="username" title="${message(code: 'agentPerson.id.label', default: 'Username')}" />
				<g:sortableColumn property="name" title="${message(code: 'agentPerson.id.label', default: 'Name')}" />
				<g:sortableColumn property="dateCreated" title="${message(code: 'agentPerson.id.label', default: 'Requested On')}" />
				<g:sortableColumn property="dateUpdated" title="${message(code: 'agentPerson.id.label', default: 'Validated On')}" />
				<g:sortableColumn property="email" title="${message(code: 'agentPerson.id.label', default: 'Email')}" />
				<g:sortableColumn property="country" title="${message(code: 'agentPerson.id.label', default: 'Country')}" />
				<g:sortableColumn property="affiliation" title="${message(code: 'agentPerson.id.label', default: 'Affiliation')}" />
				<%-- <g:sortableColumn property="status" title="${message(code: 'agentPerson.id.label', default: 'Status')}" /> --%>
				<th>Actions</th>
			</tr>
		</thead>
		<tbody>
			<g:each in="${users}" status="i" var="user">
				<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
		     		<td>
		     			<g:if test="${user.userId!=null}">
		     				<g:link controller="dashboard" action="showProfile" id="${user.userId}">${user.username}</g:link>
		     			</g:if>
		     			<g:else>
		     				${user.username}
		     			</g:else>
		     		</td>
		     		<td>${user.lastName} ${user.firstName} <g:if test="${user?.displayName?.length()>0}">(${user.displayName})</g:if></td>
		     		<td><g:formatDate format="MM/dd/yyyy hh:mm" date="${user.dateCreated}"/></td>
		     		<td>
		     			<g:if test="${user.validated==true}">
		     				<g:formatDate format="MM/dd/yyyy hh:mm" date="${user.lastUpdated}"/>
		     			</g:if>
		     			<g:else>
		     				Not Validated
		     			</g:else>
		     		</td>
		     		<td>${user.email}</td>
		     		<td>${user.country}</td>
		     		<td>${user.affiliation}</td>
		     		<td>
		     			<div class="buttons">
							<g:form>
								<g:hiddenField name="id" value="${user?.id}" /> 
								<g:hiddenField name="accountRequest" value="${user?.id}" /> 
								<g:hiddenField name="redirect" value="listUsers" />
								<span class="button">
									<g:actionSubmit class="edit" action="editAccountRequest" value="${message(code: 'default.button.edit.account.label', default: 'Edit')}" />
								</span>
								<span class="button">
									<g:if test="${user.moderated==false}">
										<g:actionSubmit class="enable" action="approveAccountRequest" value="${message(code: 'default.button.enable.account.label', default: 'Approve')}" />
									</g:if>
									<g:else>
										<g:if test="${user.validated==false}">
					     					Rejected
					     				</g:if>
					     				<g:else>
					     					Approved
					     				</g:else>
					     			</g:else>
								</span>
								<span class="button">
									<g:actionSubmit class="delete" action="declineAccountRequest" value="${message(code: 'default.button.enable.account.label', default: 'Decline')}" />
								</span>
							</g:form>
						</div>
		     		</td>
		     	</tr>
			</g:each>
		</tbody>
	</table>
	<div class="paginateButtons">
   		<g:paginate total="${accountRequestsTotal}" params="[groupId: groupId]"/>
	</div>
	
</div>
</div>
</div>