<!doctype html>
<!--[if lt IE 7 ]> <html lang="en" class="no-js ie6"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="no-js ie7"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="no-js ie8"> <![endif]-->
<!--[if IE 9 ]>    <html lang="en" class="no-js ie9"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> <html lang="en" class="no-js"><!--<![endif]-->
<head>
<g:render template="/public/shared/meta" />
<g:render template="/public/shared/title-and-icon" />

<link rel="stylesheet" href="${resource(dir: 'css/shared', file: 'reset.css', plugin: 'domeo-shared')}" type="text/css">
<link rel="stylesheet" href="${resource(dir: 'css/secure', file: 'secure.css')}" type="text/css"></link>
<link rel="stylesheet" href="${resource(dir: 'css/secure', file: 'logo.css')}" type="text/css"></link>

<g:render template="/public/shared/scripts" />
<title>Sign Up for Domeo - The Annotation Toolkit</title>

<g:javascript library="jquery"/>
<g:layoutHead/>
<r:layoutResources />
<g:render template="/public/shared/analytics" />	

<style type='text/css' media='screen'>

</style>
</head>
<body>
<div class="header" >
    <div class="header_resize" style="borders-bottom: 10px solid #cc3300;">
      <div class="logo">
      	<h1><g:link controller="secure" action="home">DOMEO<img src="${resource(dir: 'images/public', file: 'domeo_logo.png')}" width="82" height="65" alt="Domeo Toolkit " /></g:link></h1>
      </div>
      <g:render template="/secure/shared/user-navigation" />
       <div class="clr"></div>
    </div>
  </div>

<div class="main">
	<g:layoutBody/>
	<g:render template="/public/shared/footer" />
</div>
</body>