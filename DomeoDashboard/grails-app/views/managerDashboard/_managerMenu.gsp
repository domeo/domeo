<div id="navcontainer">
<h3>Users Management</h3>
<ul id="navlist">
<li id="active"><g:link controller="managerDashboard" action="listUsers">List Users</g:link></li>
<li id="active"><g:link controller="managerDashboard" action="listRoles">List Roles</g:link></li>
<li><g:link controller="managerDashboard" action="searchUser">Search Users</g:link></li>
<g:if test="${grailsApplication.config.domeo.dashboard.management.users.disable!='true'}">
	<li><g:link controller="managerDashboard" action="createUser">Create User</g:link></li>
</g:if>
</ul>
<br/>
<h3>Groups Management</h3>
<ul id="navlist">
<li id="active"><g:link controller="managerDashboard" action="listGroups">List Groups</g:link></li>
<li><g:link controller="managerDashboard" action="searchGroup">Search Groups</g:link></li>
<li><g:link controller="managerDashboard" action="createGroup">Create Group</g:link></li>
</ul>
<%-- 
<li><a href="#">Groups Network</a></li>
--%>
<%--
<br/>
<h3>Communities Management</h3>
<ul id="navlist">
<li id="active"><g:link action="listCommunities">List Communities</g:link></li>
<li><g:link action="searchGroup">Search Communities</g:link></li>
<li><a href="#">Create Communities</a></li>
</ul>
--%>
<g:if test="${grailsApplication.config.domeo.dashboard.management.users.disable!='true'}">
	<br/>
	<h3>Moderation Queue</h3>
	<ul id="navlist">
	<li><g:link action="pastAccountsRequests">Past Account Requests</g:link></li>
	<li><g:link action="moderateAccountsRequests">Moderate Account Requests</g:link></li>
	</ul>
</g:if>
<%--<li><g:link action="activateUser">Groups Requests</g:link></li>--%>
<%-- 
<li><a href="#">Users Network</a></li>
--%>
<%--
</ul>
--%>
<%--
<br/>
<h3>Circles Management</h3>
<ul id="navlist">
<li id="active"><a href="#" id="current">List Circles</a></li>
<li><a href="#">Find Circles</a></li>
<li><a href="#">Circles Network</a></li>
</ul>
<br/>
<h3>Event Management</h3>
<ul id="navlist">
<li id="active"><a href="#" id="current">List Events</a></li>
<li><a href="#">Find Events</a></li>
<li><a href="#">Create Event</a></li>
<li><a href="#">Events Timeline</a></li>
</ul>
<br/>
<h3>Nodes Management</h3>
<ul id="navlist">
<li><a href="#">Node Info</a></li>
<li id="active"><a href="#" id="current">List Nodes</a></li>
<li><a href="#">Nodes Network</a></li>
</ul>
<br/>
<h3>Statistics</h3>
<ul id="navlist">
<li><a href="#">Users Usage</a></li>
<li><a href="#">Annotated Resources</a></li>
<li><a href="#">Annotations Items</a></li>
</ul>

<br/>
<h3>Export Management</h3>
<ul id="navlist">
<li><a href="#">Export all</a></li>
</ul>
--%>
<br/>
<br/>
</div>