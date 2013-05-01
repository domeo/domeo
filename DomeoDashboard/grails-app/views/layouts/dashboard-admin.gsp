<!doctype html>
<!--[if lt IE 7 ]> <html lang="en" class="no-js ie6"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="no-js ie7"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="no-js ie8"> <![endif]-->
<!--[if IE 9 ]>    <html lang="en" class="no-js ie9"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> <html lang="en" class="no-js"><!--<![endif]-->
<%@ page import="org.mindinformatics.grails.domeo.dashboard.security.DefaultRoles" %>
<head>	
<g:render template="/general/meta" />
<g:render template="/general/title-and-icon" />
<g:render template="/general/stylesheets" />
<g:javascript library="jquery"/>
<g:layoutHead/>
<r:layoutResources />
</head>
<body>
	<div id="maincontainer">
		<div id="contentwrapper">
			<div id="contentcolumn">
				<div class="innertube">
					<g:layoutBody/>
				</div>
			</div>
		</div>
		
		<div id="leftcolumn">
			<div class="logo">
				<g:link controller="dashboard" action="index">
				<h1>
					DOMEO<img src="${resource(dir: 'images/public', file: 'domeo_logo.png')}" width="82" height="65" alt="Domeo Toolkit " /><small>${DefaultRoles.ADMIN.label}</small>
				</h1>
				</g:link>
			</div>
	      	<g:render template="/adminDashboard/adminMenu" />
		</div>
		
		<g:render template="/general/footer" />
		<g:render template="/general/last-imports" />
       	<r:layoutResources />
       </div>
</body>
</html>