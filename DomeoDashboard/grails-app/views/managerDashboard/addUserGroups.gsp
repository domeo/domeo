<!DOCTYPE html>
<%-- by Paolo Ciccarese --%>
<!-- DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" -->
<!-- The HTML 4.01 Transitional DOCTYPE declaration-->
<!-- above set at the top of the file will set     -->
<!-- the browser's rendering engine into           -->
<!-- "Quirks Mode". Replacing this declaration     -->
<!-- with a "Standards Mode" doctype is supported, -->
<!-- but may lead to some differences in layout.   -->

<html>
  <head>
	<meta name="layout" content="dashboard-manager" />
	
    <g:javascript library="jquery" plugin="jquery"/>
    
    <script type="text/javascript">
	function setDefaultValue() {
		var eResults = document.getElementById('results');
		eResults.style.display="none"
		var eAjaxIcon = document.getElementById('ajaxIcon');
		eAjaxIcon.style.display="inline"
	}
    
	function addResults(response) {  
		var eAjaxIcon = document.getElementById('ajaxIcon');
		eAjaxIcon.style.display="none"

		var eContent = document.getElementById('content');
		while(eContent.firstChild) {
			eContent.removeChild(eContent.firstChild);
		}
			
		var eResults = document.getElementById('results');
		if (eResults.style.display=="none") eResults.style.display="block"

		for(var i=0; i< response.groups.length; i++) {
			var eTr = document.createElement('tr');
			var eUsername = document.createElement('td');
			var eLink = document.createElement('a');
			eLink.href = "../showGroup/" + response.groups[i].id;
			eLink.innerHTML = response.groups[i].name;
			eUsername.appendChild(eLink);
			eTr.appendChild(eUsername);

			var eAdmin = document.createElement('td');
			eAdmin.innerHTML = response.groups[i].shortName;
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

			var eLink = document.createElement('a');
			eLink.href =  '/DomeoDashboard/managerDashboard/enrollUserInGroup?group=' +response.groups[i].id + '&user=' + '${user.id}';
			var eImg = document.createElement('img');
			//eImg.src = '/DomeoDashboard/static/images/dashboard/add_user.png';
			var eAct = document.createElement('td');
			eAct.innerHTML = "Enroll"
			eAct.appendChild(eImg);
			eLink.appendChild(eAct);
			eTr.appendChild(eLink);
			
			eContent.appendChild(eTr);
		}
	}
	</script>
  </head>

	<body>
		<g:render template="/shared/addGroupForm" />
		<g:render template="/shared/addGroupResults" />
	</body>
</html>