<!doctype html>
<!--[if lt IE 7 ]> <html lang="en" class="no-js ie6"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="no-js ie7"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="no-js ie8"> <![endif]-->
<!--[if IE 9 ]>    <html lang="en" class="no-js ie9"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> <html lang="en" class="no-js"><!--<![endif]-->
<head>
<g:javascript library="jquery" plugin="jquery"/>
<meta name="layout" content="domeo-secure" />
<title>Secured Area - Domeo Browser</title>
<style>
	ul.bar
	{
	list-style-type:none;
	margin:0;
	padding:0;
	overflow:hidden;
	}
	ul.bar li
	{
	float:left;
	}
	
	ul.bar a
	{
	display:block;
	padding-left:10px;
	padding-right: 10px;
	color: white;
	font-weight: bold;
	text-decoration: none;
	text-transform:uppercase;
	}
	
	#btn_s{
	    width:100px;
	    margin-left:auto;
	    margin-right:auto;
	}
	
	#btn_i {
	    width:125px;
	    margin-left:auto;
	    margin-right:auto;
	}

</style>

</head>
<body>
  <div class="content">
    <div class="content_resize">
	    <div class="sidebar" style="padding-top: 30px;padding-bottom: 30px; padding-right:2px;">
	    	<div align="center" style="background: #cc3300; padding: 5px; color: #fff; font-weight: bold;">Filter (not implemented yet)</div>
	    	<div style="background: #fff; padding: 5px; padding-top: 10px; border: 2px solid #cc3300;">
			    <div align="left" style="padding-left:4px; background: #FFCC00"><b>By Access</b><br/></div>
			    <input type="checkbox" name="vehicle" value="Public" checked="checked">Public<br>
			    <input type="checkbox" name="vehicle" value="Groups">Groups<br>
			    
			  	<div id="groupsList">
			  		<g:each in="${userGroups}" status="i" var="usergroup">
			  			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="checkbox" name="${usergroup.group.name}" value="Car">${usergroup.group.name}<br/>
			  		</g:each>
			  	</div>
			    
				<input type="checkbox" name="vehicle" value="Private">Private<br/>
			  	
			  		
				<br/>
				
			</div>
			<br/>
			<div align="center" style="background: #cc3300; padding: 5px; color: #fff; font-weight: bold;">People</div>
	    	<div style="background: #fff; padding: 5px; padding-top: 10px; border: 2px solid #cc3300;">
	    	
	    	</div>
	  	</div>
 
 		<!-- Browsing Navigation -->
	    <div style="background: #cc3300; color: #fff;">
	   		<ul class="bar">
				<li><g:link controller="secure" action="browser"><span>Annotation Sets</span></g:link></li>
				<li><g:link controller="secure" action="documents"><span>Documents</span></g:link></li>
				<li><a href="#">Bibliography</a></li>
			</ul> 
	    </div>
	    
	    <div id="progressIcon" align="center" style="padding: 5px; padding-left: 10px; display: none;"><img id="groupsSpinner" src="${resource(dir:'images',file:'progress-bar-2.gif',plugin:'users-module')}" /></div>
	    <table width="730px;">
	    	<tr><td>
	    		<div id="resultsSummary" style="padding: 5px; padding-left: 10px;"></div>
	    	</td><td style="text-align:right">
	    		<div id="resultsStats" style="padding: 5px; "></div>
	    	</td></tr>
	    </table>
	    <div id="searchArea" align="center" style="padding: 5px; padding-top: 15px ;padding-left: 10px; width: 715px;">
	    	<g:form name="searchForm" action="search">
	    		<g:textField name="query" size="70" />
	    		<g:submitButton name="search" value="Search" />   
	    	</g:form>
	    </div>
	    <div id="resultsList" style="padding: 5px; padding-left: 10px; width: 715px;">
	    	${results}
	    </div>
	    <div class="resultsPagination"></div>
      	<div class="clr"></div>
    </div>
  </div>
</body>
</html>
