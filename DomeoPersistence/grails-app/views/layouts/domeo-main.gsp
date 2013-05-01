<!doctype html>
<!--[if lt IE 7 ]> <html lang="en" class="no-js ie6"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="no-js ie7"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="no-js ie8"> <![endif]-->
<!--[if IE 9 ]>    <html lang="en" class="no-js ie9"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> <html lang="en" class="no-js"><!--<![endif]-->
	<head>
		<g:render template="/public/shared/meta" />
		<g:render template="/public/shared/title-and-icon" />
		<g:render template="/secure/shared/stylesheets" />
		<g:render template="/public/shared/scripts" />
		<g:layoutHead/>
        <r:layoutResources />
	</head>
	<body>
		<div class="header" >
		    <div class="header_resize" style="borders-bottom: 10px solid #cc3300;">
		      <div class="logo">
		      	<h1><g:link controller="secure" action="home">DOMEO<img src="${resource(dir: 'images/public', file: 'domeo_logo.png')}" width="82" height="65" alt="Domeo Toolkit " /></g:link></h1>
		      </div>
		       <div class="clr"></div>
		    </div>
		  </div>
		<div class="main">
			<g:layoutBody/>
			<g:render template="/public/shared/footer" />
		</div>
	</body>
</html>