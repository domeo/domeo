<!doctype html>
<!--[if lt IE 7 ]> <html lang="en" class="no-js ie6"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="no-js ie7"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="no-js ie8"> <![endif]-->
<!--[if IE 9 ]>    <html lang="en" class="no-js ie9"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> <html lang="en" class="no-js"><!--<![endif]-->
<%@ page import="org.mindinformatics.grails.domeo.dashboard.security.User" %>
<%@ page import="org.mindinformatics.grails.domeo.dashboard.groups.Group" %>
<head>
	<title>Access to Domeo - The Annotation Toolkit</title>
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
	
<script type="text/javascript">	

	$(document).ready(function() {
		$('#progressIcon').css("display","block");

		$.ajax({
			url: "${appBaseUrl}/ajaxPersistence/stats",
	  	  	success: function(data){
		  	  	if(data) {
		  	  		$('#domeoNumberOfAnnotations').text(data.numberofanns);
		  	  		$('#domeoNumberOfAnnotationSets').text(data.numberofsets);
		  	  		$('#domeoNumberOfAnnotatedResources').text(data.numberofreso);
		  	  	}
	  	  	}
		});

	});
</script>
	
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
      	<h1><a href="index.html">DOMEO<img src="${resource(dir: 'images/public', file: 'domeo_logo.png')}" width="82" height="65" alt="Domeo Toolkit " /><small>${grailsApplication.config.domeo.subtitle}</small></a></h1>
      </div>
      <div class="menu_nav">
        <ul>
		<li class="active"><g:link controller="openId" action="auth"><span>Access</span></g:link></li>
		<%-- 
		<li>
			<g:link controller="public" action="search"><span>Search</span><br/>
				<img src="${resource(dir: 'images/public', file: 'try.png')}" width="45" height="45" alt="Domeo Toolkit " />
			</g:link>
		</li>
		 --%>
          <li><g:link controller="public" action="signup"><span>Sign Up</span></g:link></li>
          <%-- 
          <li><g:link controller="openId" action="linkAccount"><span>Open ID</span></g:link></li>
          --%>
          <li><g:link controller="public" action="nodeinfo"><span>Node</span></g:link></li>
          <li><g:link controller="public" action="credits"><span>Credits</span></g:link></li>
        </ul>
      </div>
      <div class="clr"></div>
      <div class="slider" style="border-top: 0px solid #CC3300;padding-top: 10px;" align="center">
        <g:render template="/openId/loginBox" />        
       <div class="clr"></div>
      </div>
      <div class="clr"></div>
    </div>
  </div>

  <div class="content">
    <div class="content_resize">
   
      <div class="mainbar">
      
        <div class="article">
        <div align="center">
         
         </div>
          <h2>Welcome to Domeo</h2>
          <p style="text-align:justify;"><strong>Domeo</strong> is an
extensible web application enabling users to visually and
efficiently create and share ontology-based stand-off annotation
 on HTML or XML document targets. The tool supports
manual, fully automated, and semi-automated annotation
with complete provenance records, as well as personal or
community annotation with access authorization and control.</p>
<div align="center">

</div>

          <div class="post_contents">
            <p align="justify">Domeo enables annotation sharing with individual colleagues, selected groups, or at web scale.  
            Other applications can access and display the annotation created through Domeo, and share their own annotation with Domeo, using our RDF exports and our APIs.
            Annotations are currently internally represented using an open standard, the <a href="http://code.google.com/p/annotation-ontology/" target="_blank">Annotation Ontology (AO) </a>RDF model.  
            We are also members of the <a href="http://www.w3.org/community/openannotation/">W3C Open Annotation Community Group</a> and we expect to export the annotation in Open Annotation format soon.</p>
         
            <p class="spec"><a href="http://www.annotationframework.org/overview.html" class="rm">Read more about Domeo</a></p>
          </div>
          <div class="clr"></div>
        </div>
      </div>
      
      <div class="sidebar">
        <div class="clr"></div>

        <div class="gadget">
          <h2 class="star"><span>Sponsors</span></h2>
          <div class="clr"></div>
          Domeo has been made possible by generous support from: 
          <ul class="ex_menu">
            <li><a href="http://www.lilly.com/Pages/home.aspx" target="_blank">Eli Lilly and Company</a></li>
            <li><a href="http://www.nih.gov/" target="_blank">NIH</a> via subcontract with <a href="http://www.neuinfo.org/" target="_blank"> Neuroscience Information Framework (NIF)</a>, and </li>
            <li><a href="http://www.elsevier.com/" target="_blank">Elsevier B.V.</a> </li>
          </ul>
        </div>
      </div>
      <div class="clr"></div>
    </div>
  </div>
  <div class="fbg">
    <div class="fbg_resize">
      <div class="col c1">
        <h2>Current Version</h2>
     	<p><g:meta name="app.version"/> (build <g:meta name="app.build"/> - <g:meta name="app.date"/>)</p>  
     	<h2>Statistics</h2>
      	<div>
      		Annotated resources: <span id="domeoNumberOfAnnotatedResources"></span><br/>
      		Annotations: <span id="domeoNumberOfAnnotations"></span><br/>
      		Annotation Sets: <span id="domeoNumberOfAnnotationSets"></span><br/>
      		Members: <% println User.count() %><br/>
      		Groups: <% println Group.count() %><br/>
      	</div>
      </div>
      <div class="col c2">
      	<h2>Want Domeo?</h2>
      	<p>If you are interested in deploying your own version of Domeo, that is possible and we  <a href="mailto:paolo.ciccarese@gmail.com">can help you witht that</a>.</p>
        <h2>Need Customization?</h2>
        <p>Domeo is a highly customizable and extendable platform. Therefore, if you are interested in using Domeo and you need ad-hoc features, 
        please <a href="mailto:paolo.ciccarese@gmail.com">contact us</a>. </p>
        
      </div>
      <div class="col c3">
        <h2><span>Become a Developer</span></h2>
        <img src="${resource(dir: 'images/public', file: 'domeo-code.png')}" alt="Domeo Code by Paolo Ciccarese" width="262" height="101" />
        <p>Domeo is open source and the code is available in GitHub. If you are interested in contributing to the project 
        in coordination and with the support of our team please <a href="mailto:paolo.ciccarese@gmail.com">contact us</a>.</p>
      </div>
      <div class="clr"></div>
    </div>
  </div>
  <div class="footer">
    <div class="footer_resize">
      <p class="lf">&copy;2013 Copyright <a href="http://www.massgeneral.org/">Massachusetts General Hospital</a></p>
      <p class="rf">Design by Dream <a href="http://www.dreamtemplate.com/">Web Templates</a></p>
      <div style="clear:both;"></div>
    </div>
  </div>
</div>
<script>

(function() { document.forms['loginForm'].elements['username'].focus(); })();

var openid = false;

function toggleForms() {
	if (!openid) {
		document.getElementById('openidLogin').style.display = '';
		document.getElementById('formLogin').style.display = 'none';
	}
	else {
		document.getElementById('openidLogin').style.display = 'none';
		document.getElementById('formLogin').style.display = '';
	}
	openid = !openid;
}
</script>
</body>
</html>
