
<%@ page import="org.mindinformatics.grails.domeo.dashboard.security.*" %>

<!-- Begin Navigation Menu -->
<div id="navcontainer">
	<h3>General</h3>
	<ul id="navlist">
		<g:if test="${menuitem=='showProfile' && user?.id==loggedUser?.id}">
			<li class="active"><g:link action="showProfile" id="${loggedUser.id}">My Profile</g:link></li>
		</g:if>
		<g:else>
			<li><g:link action="showProfile" id="${loggedUser.id}">My Profile</g:link></li>
		</g:else>
		<g:if test="${menuitem=='showGroups' && user?.id==loggedUser?.id}">
			<li class="active"><g:link action="showGroups" id="${loggedUser.id}">My Groups</g:link></li>
		</g:if>
		<g:else>
			<li><g:link action="showGroups" id="${loggedUser.id}">My Groups</g:link></li>
		</g:else>
		<li><g:link controller="logout" action="index">Logout</g:link></li>
	</ul>
	<br/>
	<h3>Tools</h3>
	<ul id="navlist">
		<g:each in="${userRoles}" var="role"> 
			<g:if test="${role.label==DefaultRoles.ADMIN.label}"><li><g:link controller="adminDashboard" action="adminDashboard">Administrator Tools</g:link></li></g:if>
			<g:if test="${role.label==DefaultRoles.MANAGER.label}"><li><g:link controller="managerDashboard" action="managerDashboard">Manager Tools</g:link></li></g:if>
			<g:if test="${role.label==DefaultRoles.USER.label}"><li><g:link action="dashboardUser">User Tools</g:link></li></g:if>
		</g:each>
	</ul>
</div>
<!-- End Navigation Menu -->