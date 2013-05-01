<!DOCTYPE html>
<%-- by Paolo Ciccarese --%>

<html>
    <head>
    	<g:javascript library="jquery" plugin="jquery"/>
        <meta name="layout" content="dashboard-admin" /> 
	    
	
    
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

		for(var i=0; i< response.users.length; i++) {
			var eTr = document.createElement('tr');
			var eUsername = document.createElement('td');
			var eLink = document.createElement('a');
			eLink.href = "showUser/" + response.users[i].id;
			eLink.innerHTML = response.users[i].username;
			eUsername.appendChild(eLink);
			eTr.appendChild(eUsername);
			var displayName = ""
			if(response.users[i].displayName!=null && response.users[i].displayName.trim().length>0) {
				displayName = " ("+response.users[i].displayName+")";
			}
			var eName = document.createElement('td');
			eName.innerHTML = response.users[i].name + displayName;
			eTr.appendChild(eName);
			var eAdmin = document.createElement('td');
			eAdmin.innerHTML = response.users[i].isAdmin;
			eTr.appendChild(eAdmin);
			var eMgr = document.createElement('td');
			eMgr.innerHTML = response.users[i].isManager;
			eTr.appendChild(eMgr);
			var eUsr = document.createElement('td');
			eUsr.innerHTML = response.users[i].isUser;
			eTr.appendChild(eUsr);
			var eCreation = document.createElement('td');
			eCreation.innerHTML = response.users[i].dateCreated;
			eTr.appendChild(eCreation);
			var eStatus = document.createElement('td');
			eStatus.innerHTML = response.users[i].status;
			eTr.appendChild(eStatus);
			eContent.appendChild(eTr);
		}
	}
	</script>
  </head>

	<body>
		<g:render template="/adminDashboard/searchUser" />
	</body>
</html>