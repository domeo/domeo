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
			<legend><span id="circlesTitle">Loading Circles</span> <img id="circlesSpinner" src="${resource(dir:'images',file:'spinner.gif',plugin:'users-module')}" /></legend>
			<div class="list tablescroll">
				<table id="circlesTable" style="border: 1px solid #ddd;">
					<thead>
						<tr>
							<g:sortableColumn property="name" title="${message(code: 'agentPerson.id.label', default: 'Circle Name')}" style="width: 200px" />
							<g:sortableColumn property="members" title="${message(code: 'agentPerson.id.label', default: 'Members')}" style="width: 200px" />
							<g:sortableColumn property="since" title="${message(code: 'agentPerson.id.label', default: 'Created on')}" style="width: 200px" />
						</tr>
					</thead>
					<tbody id="circlesContent">
					</tbody>
				</table>
				<div class="paginateButtons">
			   		<g:paginate total="1" />
				</div>
			</div>
			<div>
			<span class="button">
				<g:link class="edit" controller="dashboard" action="showCircles" id="${user.id}" style="text-decoration: none;">Manage Circles</g:link>
			</span>
			&nbsp;
			<span class="button">
				<g:link class="edit" controller="users" action="manageCirclesOfUser" id="${user.id}" style="text-decoration: none;">Create Circle</g:link>
			</span>
			</div>
		</fieldset> 
	</div>
</div>
