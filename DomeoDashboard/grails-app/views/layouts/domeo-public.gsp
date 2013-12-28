<!doctype html>
<!--[if lt IE 7 ]> <html lang="en" class="no-js ie6"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="no-js ie7"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="no-js ie8"> <![endif]-->
<!--[if IE 9 ]>    <html lang="en" class="no-js ie9"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> <html lang="en" class="no-js"><!--<![endif]-->
<head>
	<g:render template="/public/shared/meta" />
	<g:render template="/public/shared/title-and-icon" />
	
	<link rel="stylesheet" href="${resource(dir: 'css/shared', file: 'reset.css', plugin: 'af-shared')}" type="text/css">
	<link rel="stylesheet" href="${resource(dir: 'css/public', file: 'public.css')}" type="text/css"></link>
	<link rel="stylesheet" href="${resource(dir: 'css/dashboard', file: 'dashboard-main.css')}" type="text/css"></link>
	
	<g:render template="/public/shared/scripts" />
    
	<g:layoutHead/>
	<r:layoutResources />
	
	<g:render template="/public/shared/analytics" />	
	
</head>
<body>
    <div class="main">
        <g:layoutBody/>
		<g:render template="/public/shared/footer" />
		
    </div>
</body>