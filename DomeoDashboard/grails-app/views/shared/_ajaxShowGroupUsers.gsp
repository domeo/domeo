<%-- by Paolo Ciccarese --%>
<%-- 
Parameters list
 1) item | instance of GroupCreateCommand
Stylesheet
 1) fieldError | background and font color in erroneous text fields
--%>
<div class="sectioncontainer">
	<div class="dialog" style="width: 560px">
		<fieldset>
			<legend><span id="usersTitle">Loading Users</span> <img id="usersSpinner" src="${resource(dir:'images',file:'spinner.gif',plugin:'domeo-dashboard')}" /></legend>
			<div class="list tablescroll">
				<table id="usersTable" style="border: 1px solid #ddd;">
					<thead>
						<tr>
							<g:sortableColumn property="username" title="${message(code: 'agentPerson.id.label', default: 'Username')}" />
							<g:sortableColumn property="name" title="${message(code: 'agentPerson.id.label', default: 'Name')}" />
							<g:sortableColumn property="role" title="${message(code: 'agentPerson.id.label', default: 'Role')}" />
							<th>${message(code: 'agentPerson.id.label', default: 'Mamber Since')}</th>
						</tr>
					</thead>
					<tbody id="usersContent">
					</tbody>
				</table>
				<div class="paginateButtons">
			   		<g:paginate total="1" />
				</div>
			</div>
			<div>
			<g:link action="manageUsersOfGroup" id="${item.id}">Manage Users</g:link>
			
			</div>
		</fieldset> 
	</div>
</div>
