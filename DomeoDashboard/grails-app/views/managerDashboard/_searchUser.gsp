<div id="request" class="sectioncontainer">
	<g:formRemote name="from_form"
	     url="[controller:'managerDashboard', action:'performSarchUser']"
	     before="setDefaultValue();"
	     onSuccess="addResults(data)">
	     
	    <div class="dialog" >
	    <div class="title">
		User Search
	</div>
	    <div style="padding-left: 5px; padding-top: 10px;">
			<table style="width:840px; border: 1px #ddd solid;">
				<tbody>     
					<tr class="prop">          
						<td class="name" valign="top">Last Name:</td>             
						<td class="value" valign="top"><input name="lastName" value="${lastName}" type="text"></td>                   
						<td class="name" valign="top">First Name:</td>             
						<td class="value" valign="top"><input name="firstName" value="${firstName}" type="text"></td>            
						<td class="name" valign="top">or by Display Name:</td>             
						<td class="value" valign="top"><input name="displayName" value="${displayName}" type="text"></td>   
						<td style="padding-left: 4px;width: 20px; display: block; padding-top:6px;"><span id="ajaxIcon" class="ajaxIcon" style="display:none;"><img src="${resource(dir:'images',file:'spinner.gif')}" alt="${message(code:'spinner.alt',default:'Loading...')}"/></span></td>
						<td><input type="submit" value="Search" /></td>
					</tr>
				</tbody>	
			</table>
		</div>
</div>
	
	</g:formRemote>
</div>
<div id="results" class="sectioncontainer" style="display:none;">
	<div class="list">
		<table class="tablelist">
			<thead>
				<tr>
					<g:sortableColumn property="username" title="${message(code: 'agentPerson.id.label', default: 'Username')}" />
					<g:sortableColumn property="name" title="${message(code: 'agentPerson.id.label', default: 'Name (Display name)')}" />
					<g:sortableColumn property="isAdmin" title="${message(code: 'agentPerson.id.label', default: 'Adm')}" />
					<g:sortableColumn property="isManager" title="${message(code: 'agentPerson.id.label', default: 'Mgr')}" />
					<g:sortableColumn property="isUser" title="${message(code: 'agentPerson.id.label', default: 'Usr')}" />
					<g:sortableColumn property="createdOn" title="${message(code: 'agentPerson.id.label', default: 'Member Since')}" />
					<g:sortableColumn property="status" title="${message(code: 'agentPerson.id.label', default: 'Status')}" />
				</tr>
			</thead>
			<tbody id="content">
			</tbody>
		</table>
		<div class="paginateButtons">
	   		
		</div>
	</div>
	
</div>
