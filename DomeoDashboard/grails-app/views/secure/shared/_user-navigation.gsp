
<!-- Begin top navigation menu -->
<div class="menu_nav">
	<ul>
		<g:if test="${menuitem=='home'}">
			<li class="active"><g:link controller="secure" action="home"><span>Home</span></g:link></li>
		</g:if>
		<g:else>
			<li><g:link controller="secure" action="home"><span>Home</span></g:link></li>
		</g:else>
		<!-- <li><g:link controller="logout" action="index">Feeds</g:link></li>  -->
		<li><g:link controller="secure" action="annotator"><span>Annotator</span></g:link></li>
		
		<g:if test="${menuitem=='search'}">
			<li class="active"><g:link controller="secure" action="search"><span>Search</span></g:link></li>
		</g:if>
		<g:else>
			<li><g:link controller="secure" action="search"><span>Search</span></g:link></li>
		</g:else>
		
		<g:if test="${menuitem=='browser'}">
			<li class="active"><g:link controller="secure" action="browser"><span>Viewer</span></g:link></li>
		</g:if>
		<g:else>
			<li><g:link controller="secure" action="browser"><span>Viewer</span></g:link></li>
		</g:else>
		
		<li><g:link controller="dashboard" action="index"><span>Dashboard</span></g:link></li>
		<li><g:link controller="logout" action="index"><img id="groupsSpinner" src="${resource(dir:'images/secure',file:'exit.png',plugin:'users-module')}" title="Logout" /></g:link></li>
	</ul>
</div>
<!-- End top navigation menu -->