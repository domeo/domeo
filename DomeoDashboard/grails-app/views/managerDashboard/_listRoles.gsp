<%@ page import="org.mindinformatics.grails.domeo.dashboard.security.DefaultRoles" %>
<div id="request" class="sectioncontainer">
<div class="dialog">
	<div class="title">
		Roles <g:if test="${role!=null}">(with Role: ${role.label})</g:if> List - total# ${roles.size()}
	</div>
	<div class="list">
		<table class="tablelist">
			<thead>
				<tr>
					<g:sortableColumn property="authority" title="${message(code: 'agentPerson.id.label', default: 'Role')}" />
					<th>Description</th>
					<g:sortableColumn property="rolesCount" title="${message(code: 'agentPerson.id.label', default: '# Mambers')}" />
				</tr>
			</thead>
			<tbody>
				<g:each in="${roles}" status="i" var="role">
					<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
			     		<td>
							${role.label}
			     		</td>
			     		<td>
			     			${role.description}
			     		</td>
			     		<td>
			     			<g:each in="${rolesCount}" var="roleCount">
			     				<g:if test="${roleCount.key == role.id}">
			     					<g:if test="${roleCount.value>0}">
				     					<g:link controller="managerDashboard" action="listRoleUsers" id="${role.id}">
				     						${roleCount.value}
				     					</g:link>
			     					</g:if>
			     					<g:else>
			     						${roleCount.value}
			     					</g:else>
			     				</g:if>
			     			</g:each>
			     		</td>
			     	</tr>
				</g:each>
			</tbody>
		</table>
		<div class="paginateButtons">
	   		<g:paginate total="${rolesTotal}" />
		</div>
	</div>
</div>
</div>