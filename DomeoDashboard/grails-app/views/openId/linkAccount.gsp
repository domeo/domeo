<!doctype html>
<!--[if lt IE 7 ]> <html lang="en" class="no-js ie6"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="no-js ie7"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="no-js ie8"> <![endif]-->
<!--[if IE 9 ]>    <html lang="en" class="no-js ie9"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> <html lang="en" class="no-js"><!--<![endif]-->
<head>
	<title>Link OpenID - The Annotation Toolkit</title>
	<meta name="author" content="Dr. Paolo Ciccarese" />
	<meta name="author" content="http://www.paolociccarese.info" />
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

	<link rel="stylesheet" href="${resource(dir: 'css/public', file: 'public.css')}" type="text/css"></link>
	<link rel="SHORTCUT ICON" href="${resource(dir: 'images/public', file: 'domeo.ico')}" ></link>

	<script type="text/javascript" src="${resource(dir: 'js/public', file: 'jquery-1.4.2.min.js')}"></script>
	<script type="text/javascript" src="${resource(dir: 'js/public', file: 'cufon-yui.js')}"></script>
	<script type="text/javascript" src="${resource(dir: 'js/public', file: 'cufon-chunkfive.js')}"></script>
	<script type="text/javascript" src="${resource(dir: 'js/public', file: 'script.js')}"></script>
	
	<g:render template="/public/shared/analytics" />
	<style type='text/css' media='screen'>


div.openid-loginbox {
	width: 800px;
	margin-left: auto;
	margin-right: auto;
	background: #fff;
	padding: 15px;
}

.openid-loginbox-inner {
	width: 450px;
	border: 3px #FFCC00 solid;
	background: #fff;
	height: 140px;
	border-bottom-left-radius:22px;
	border-bottom-right-radius:22px;
	/*
	border-radius:25px;
	-moz-border-radius:25px;*/ /* Firefox 3.6 and earlier */
}

td.openid-loginbox-title {
	
	background: #FFCC00;
	border-bottom: 1px #cc3300 solid;
	/*
	border-top-left-radius:22px;
	border-top-right-radius:22px;
	*/
	color: #000;
	padding: 0;
	padding-left: 10px;
	padding-right: 10px;
	height: 20px;
}

td.openid-loginbox-title table {
	width: 100%;
	font-size: 18px;
}
.openid-loginbox-useopenid {
	font-weight: normal;
	font-size: 14px;
}
td.openid-loginbox-title img {
	border: 0;
	vertical-align: middle;
	padding-right: 3px;
}
table.openid-loginbox-userpass {
	margin: 3px 3px 3px 8px;
	height: 130px;
}
table.openid-loginbox-userpass td {
	height: 25px;
}
input.openid-identifier {
/*
	background: url(http://stat.livejournal.com/img/openid-inputicon.gif) no-repeat;
	background-color: #fff;
	background-position: 0 50%;
	padding-left: 18px;*/
}

input[type='text'],input[type='password'] {
	font-size: 16px;
	width: 310px;
}
input[type='submit'] {
	font-size: 14px;
}

td.openid-submit {
	padding: 3px;
}

.logoContainer {
	background-color: red;
	padding: 10px;
}

.logo {
	padding:0;
	padding-top: 5px;
	padding-left: 10px;
	width:240px;
	float:left;
	height:130px;
	background-color: #fff;
}

.logo h1{
	margin:0;
	padding:0px 0 0;
	font-size:36px;
	font-weight:normal;
	line-height:1.2em;
	text-transform:uppercase;
}
.logo h1>a{
	padding-left: 4px;
}
h1 {
	margin:0;
	padding:34px 0 0;
	font-size:36px;
	font-weight:normal;
	line-height:1.2em;
	text-transform:uppercase;
}
h1 a, h1 a:hover {
	color:#ffae00;
	text-decoration:none;
}
h1 span {
}
h1 small {
	display:block;
	font-size:18px;
	line-height:1.2em;
	letter-spacing:normal;
	text-transform:uppercase;
	border-top: 3px solid #CC3300;
	padding-top: 6px;
	padding-left: 6px;
	color:#3f3f3f;
}



</style>
</head>

<body>

<div class="main">
  <div class="header">
    <div class="header_resize">
      <div class="logo">
      	<h1><g:link controller="public" action="index">DOMEO<img src="${resource(dir: 'images/public', file: 'domeo_logo.png')}" width="82" height="65" alt="Domeo Toolkit " /><small>Annotation ToolKIT</small></g:link></h1>
      </div>
      <g:render template="/public/shared/top-navigation" />
      <div class="clr"></div>
      <div class="slider" style="border-top: 0px solid #CC3300;padding-top: 10px;" align="center">
    	 <g:render template="/openId/linkAccountBox" />        
     </div>
      <div class="clr"></div>
    </div>
  </div>
  </div>
  <g:render template="/public/shared/banner-bottom" />  
  <g:render template="/public/shared/footer" />  

<script>
(function() { document.forms['loginForm'].elements['username'].focus(); })();
</script>
</body>
</html>
