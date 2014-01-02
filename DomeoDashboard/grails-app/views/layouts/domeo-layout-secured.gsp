<!doctype html>
<!--[if lt IE 7 ]> <html lang="en" class="no-js ie6"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="no-js ie7"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="no-js ie8"> <![endif]-->
<!--[if IE 9 ]>    <html lang="en" class="no-js ie9"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> <html lang="en" class="no-js"><!--<![endif]-->
<head>
	<g:render template="/public/shared/meta" />
	
	<link rel="stylesheet" href="${resource(dir: 'css/shared', file: 'reset.css', plugin: 'af-shared')}" type="text/css">
	<link rel="stylesheet" href="${resource(dir: 'css/navigation', file: 'bootstrap.css')}" type="text/css">
	<link rel="stylesheet" href="${resource(dir: 'css/navigation', file: 'navigation-custom.css')}" type="text/css">
	<link rel="stylesheet" href="${resource(dir: 'css/secured', file: 'pagination.css')}" type="text/css">
	<link rel="stylesheet" href="${resource(dir: 'css/secured', file: 'search-results.css')}" type="text/css">
	
	<script type="text/javascript" src="${resource(dir: 'js/jquery', file: 'jquery-1.10.2.min.js')}"></script>
	<script type="text/javascript" src="${resource(dir: 'js/navigation', file: 'bootstrap.min.js')}"></script>
	
	
	<g:layoutHead/>
	<r:layoutResources />
</head>
<body>
<div class="header" >
<g:render template="/secured/navigation-bar" />
</div>
	<div style="top:60px;position: relative;">
		<g:layoutBody/>
	</div>
	<br/><br/><br/><br/>
	<g:render template="/secured/footer" />
</body>
</html>