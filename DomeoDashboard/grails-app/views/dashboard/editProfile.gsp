<!DOCTYPE html>
<%-- by Paolo Ciccarese --%>

<html>
  <head>
    <meta name="layout" content="dashboard-main" />
        
    <g:javascript library="jquery" plugin="jquery"/>
    
    <script type="text/javascript">
	function setDefaultValue() {
		var eAjaxIcon = document.getElementById('ajaxIcon');
		eAjaxIcon.style.display="inline"
	}
    
	function addResults(response) {  
		var eAjaxIcon = document.getElementById('ajaxIcon');
		eAjaxIcon.style.display="none"
		
		var eResults = document.getElementById('results');
		if (eResults.style.display=="none") eResults.style.display="block"

		var eContent = document.getElementById('content');
		while(eContent.firstChild) {
			eContent.removeChild(eContent.firstChild);
		}

		for(var i=0; i< response.groups.length; i++) {
			var eTr = document.createElement('tr');

		    var cb = document.createElement('input');
		    cb.type = 'checkbox';
		    cb.name = 'group';
		    cb.value = response.groups[i].id;
		    cb.appendChild(document.createTextNode('group'));
			
			var check = document.createElement('td');
			check.appendChild(cb);
			eTr.appendChild(check);
			
			var eUsername = document.createElement('td');
			var eLink = document.createElement('a');
			eLink.href = "showGroup/" + response.groups[i].id;
			eLink.innerHTML = response.groups[i].name;
			eUsername.appendChild(eLink);
			eTr.appendChild(eUsername);

			var eAdmin = document.createElement('td');
			eAdmin.innerHTML = response.groups[i].nickname;
			eTr.appendChild(eAdmin);
			var eMgr = document.createElement('td');
			eMgr.innerHTML = response.groups[i].description;
			eTr.appendChild(eMgr);
			var eDat = document.createElement('td');
			eDat.innerHTML = response.groups[i].dateCreated;
			eTr.appendChild(eDat);
			var eUsr = document.createElement('td');
			eUsr.innerHTML = response.groups[i].status;
			eTr.appendChild(eUsr);
			var eCnt = document.createElement('td');
			eCnt.innerHTML = response.groupsCount[response.groups[i].id];
			eTr.appendChild(eCnt);
			eContent.appendChild(eTr);
		}
	}
	</script>
  </head>

	<body>
		<g:if test="${fieldValue(bean: user, field: 'displayName') == ''}">
          	<div class="title">Edit Profile</div>
		</g:if>
		<g:else>
			<div class="title">
				<g:if test="${user?.id==loggedUser?.id}">My profile: ${loggedUser?.displayName}</g:if>
				<g:else>Edit profile: ${user?.displayName}</g:else>
			</div>
		</g:else>
		<table>
			<tr>
				<td valign="top"><g:render template="/dashboard/editProfile" /></td>
			</tr>
		</table>
	</body>
</html>