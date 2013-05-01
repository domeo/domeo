<!doctype html>
<!--[if lt IE 7 ]> <html lang="en" class="no-js ie6"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="no-js ie7"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="no-js ie8"> <![endif]-->
<!--[if IE 9 ]>    <html lang="en" class="no-js ie9"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> <html lang="en" class="no-js"><!--<![endif]-->
<head>
<meta name="layout" content="domeo-public" />
<title>Domeo Confirmation - The Annotation Toolkit</title>
</head>
<body>
  <div class="header" >
    <div class="header_resize">
      <div class="logo">
      	<h1><a href="index.html">DOMEO<img src="${resource(dir: 'images/public', file: 'domeo_logo.png')}" width="82" height="65" alt="Domeo Toolkit " /><small>Annotation ToolKIT</small></a></h1>
      </div>
      <div class="menu_nav">
        <ul>
		 <li><g:link controller="public" action="index"><span>Back</span></g:link></li>
        </ul>
      </div>
      <div class="clr"></div>
 		<div class="slider" style="border-top: 0px solid #CC3300;padding-top: 10px; color: white;" align="center">
                <div id='public-formbox' style="padding-left: 40px; padding-right: 40px">
			<br/>
			<br/>
               <table>
	      	  <tr>
	      	  	<td><img src="${resource(dir: 'images/public', file: 'face-smile.png')}" /></td>
	      	  	<td style="vertical-align:middle; padding-left: 20px;">${title}</td>
	      	  </tr>
	      	</table>
                <br/>
       <div class="clr"></div>
      </div>
      <div class="clr"></div>
    </div>
  </div>

  <div class="content">
    <div class="content_resize">
      <div style="padding-left: 40px; padding-right: 40px">
      	<br/><br/>
      	<table>
      	  <tr>
      	  	<td><img src="${resource(dir: 'images/public', file: 'email.png')}" width="82" height="65" alt="Domeo Toolkit " /></td>
      	  	<td style="vertical-align:top; padding-left: 20px;">${message}</td>
      	  </tr>
      	</table>
	    <br/><br/>
      </div>
      <div class="clr"></div>
      
    </div>
  </div>
  
<g:render template="/public/shared/banner-bottom" />  
</body>
</html>
