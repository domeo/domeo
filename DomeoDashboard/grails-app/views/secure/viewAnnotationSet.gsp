<!doctype html>
<!--[if lt IE 7 ]> <html lang="en" class="no-js ie6"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="no-js ie7"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="no-js ie8"> <![endif]-->
<!--[if IE 9 ]>    <html lang="en" class="no-js ie9"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> <html lang="en" class="no-js"><!--<![endif]-->
<head>

<meta name="layout" content="domeo-secure-no-jquery" />
<script src="${resource(dir:'js',file:'jquery-1.9.1.js')}"></script>
<script src="${resource(dir:'js',file:'jquery-ui-1.10.3.js')}"></script>
<script src="${resource(dir:'js',file:'jquery.tools.min.js')}"></script>
<script src="${resource(dir:'js',file:'jquery.tagcloud.js')}"></script>



<title>Set viewer</title>
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
		
		.barContainer {
			border: 0px #ccc solid;
			background: #eee;
		}
		

		

		
		.annbody-content {
			border: 4px #fff solid;
		}
		

		
		.context {
			padding: 10px;
		}
		
		.context-content {
			border: 2px #fff solid;
			border-left: 4px #fff solid;
			border-right: 4px #fff solid;
		}




		


.viewerSidebar {
	float: right;
	width: 332px;
	margin-right: 8px;
}

#content a:hover {
	color: #333;
}

#banner-secondary p.intro {
	padding: 0;
	float: left;
	width: 50%;
}

#banner-secondary .download-box {
	border: 1px solid #aaa;
	background: #333;
	background: -moz-linear-gradient(left, #333 0%, #444 100%);
	background: -webkit-linear-gradient(left, #333 0%, #444 100%);
	background: -o-linear-gradient(left, #333 0%, #444 100%);
	background: linear-gradient(to right, #333 0%, #444 100%);
	float: right;
	width: 40%;
	text-align: center;
	font-size: 20px;
	padding: 10px;
	border-radius: 5px;
	box-shadow: 0 0 8px rgba(0, 0, 0, 0.8);
}

#banner-secondary .download-box h2 {
	color: #71D1FF;
	font-size: 26px;
}

#banner-secondary .download-box .button {
	float: none;
	display: block;
	margin-top: 15px;
}

#banner-secondary .download-box p {
	margin: 15px 0 5px;
}

#banner-secondary .download-option {
	width: 45%;
	float: left;
	font-size: 16px;
}

#banner-secondary .download-legacy {
	float: right;
}

#banner-secondary .download-option span {
	display: block;
	font-size: 14px;
	color: #71D1FF;
}

#content .dev-links {
	float: right;
	width: 30%;
	margin: -15px -25px .5em 1em;
	padding: 1em;
	border: 1px solid #666;
	border-width: 0 0 1px 1px;
	border-radius: 0 0 0 5px;
	box-shadow: -2px 2px 10px -2px #666;
}

#content .dev-links ul {
	margin: 0;
}

#content .dev-links li {
	padding: 0;
	margin: .25em 0 .25em 1em;
	background-image: none;
}

.demo-list {
	float: right;
	width: 25%;
}

.demo-list h2 {
	font-weight: normal;
	margin-bottom: 0;
}

#content .demo-list ul {
	width: 100%;
	border-top: 1px solid #ccc;
	margin: 0;
}

#content .demo-list li {
	border-bottom: 1px solid #ccc;
	margin: 0;
	padding: 0;
	background: #eee;
}

#content .demo-list .active {
	background: #fff;
}

#content .demo-list a {
	text-decoration: none;
	display: block;
	font-weight: bold;
	font-size: 13px;
	color: #3f3f3f;
	text-shadow: 1px 1px #fff;
	padding: 2% 4%;
}

.demo-frame {
	width: 70%;
	height: 350px;
}

.view-source a {
	cursor: pointer;
}

.view-source > div {
	overflow: hidden;
	display: none;
}

@media all and (max-width: 600px) {
	#banner-secondary p.intro,
	#banner-secondary .download-box {
		float: none;
		width: auto;
	}

	#banner-secondary .download-box {
		overflow: auto;
	}
}

@media only screen and (max-width: 480px) {
	#content .dev-links {
		width: 55%;
		margin: -15px -29px .5em 1em;
		overflow: hidden;
	}
}

	</style>

<g:render template="/secure/components/domeo-overlay-scripts" />
<g:render template="/secure/components/domeo-agents-scripts" />
<g:render template="/secure/components/domeo-tags-scripts" />
<g:render template="/secure/components/domeo-references-scripts" />
<g:render template="/secure/components/domeo-annotations-scripts" />
<g:render template="/secure/components/domeo-annotations-on-annotation-scripts" />
<g:render template="/secure/components/domeo-annotations-qualifiers-scripts" />
<g:render template="/secure/components/domeo-annotations-antibodies-scripts" />
<g:render template="/secure/components/domeo-annotations-micropublications-scripts" />

<script type="text/javascript">

	// Contract
	var appBaseUrl = '${appBaseUrl}';
	

	$(document).ready(function() {
		$('#progressIcon').css("display","block");

		var dataToSend = { userId: '${loggedUser.id}', setUri:'${setUri}' };
		$.ajax({
	  	  	url: "${appBaseUrl}/ajaxPersistence/jsonAnnotationSet",
	  	  	context: $("#resultsList"),
	  	  	data: dataToSend,
	  	  	success: function(data){
		  	  	var set;
	  			$("#progressIcon").css("display","none");
	  			if(data.length==0) {
	  				$("#resultsSummary").html("");
					$("#resultsList").html("No annotation set retrieved");
		  		} else {
		  			$("#resultsSummary").html("Displaying Set");
			  		set = data[0];

				  	$("#resultsSummary").html("Saved by " + injectUserLabel(data.set.createdBy) + " on " + data.set.createdOn + "<br/>" + getVersion(data.set)
				  	     + displayAccessType(data.set.permissions['permissions:accessType']) + displayLock(data.set.permissions['permissions:isLocked']));
				  	$("#resultsStats").append('<table width="160px;"><tr><td align="left">'+getModifyLink(data, data.set.target) + '</td><td align="left"> ' + getHistoryLink(data) + '</td></tr><tr><td align="left">' + getShareLink(data) + '</td><td></td></tr></table>');
				  	$('#resultsIntro').append(
				  		getTitle(data.set) + ' ' + getDescription(data.set) + '<br/>' + getTarget(data.set) +
				  		'<div id="citation-' + data.set.id.substring(data.set.id.lastIndexOf(':')+1) + '"><img id=\"groupsSpinner\" src=\"${resource(dir:'images',file:'spinner.gif',plugin:'users-module')}\" /> Retrieving Citation</div>' 
					);
		  			retrieveCitation(data.set);
//		  			$('#resultsList').append("<div style='padding-top: 4px; padding-bottom: 5px'><span style='font-size:18px; padding-right: 5px;'>" + data.items.items.length + "</span>" + (data.items.items.length!=1?"Annotations":"Annotation") + " by <span style='font-size:18px; padding-right: 5px;'>" + Object.keys(agents).length + "</span>" + (Object.keys(agents).length!=1?"Contributors":"Contributor") + "</div>");
		  			for(var i=0; i<data.items.items.length; i++) {
		  				processAnnotation(data.items.items[i]);
		  				$('#resultsList').append(getAnnotationView(i, data.items.items[i], 0, false));
		  				$('#annotationCounters_'+i).append("<div>"+getAnnotationCurationsCounter(data.items.items[i])+"</div>");
		  				$('#annotationCounters_'+i).append("<div>"+getAnnotationCommentsCounter(data.items.items[i])+"</div>");		  				
		  				$('#annotationCounters_aoa'+i).append(getAnnotationComments(data.items.items[i]));
			  		}
		  		}
		  		buildAgentsList();
		  		buildTagCloud();
		  		buildReferenceList();
		  		buildAnnotationTitle(data);
	  		}
		});

		    // select the overlay element - and "make it an overlay"
		     
		 

	});

	function buildAnnotationTitle(data) {
		$('#resultsListTitle').append(
			"<div style='padding-top: 4px; padding-bottom: 5px'><span style='font-size:18px; padding-right: 5px;'>" + data.items.items.length + "</span>" + (data.items.items.length!=1?"Annotations":"Annotation") + 
				(data.totalCurations>0?", <span style='font-size:18px; padding-right: 5px;'>" + data.totalCurations + "</span>" + (data.totalCurations!=1?"Curations":"Curation"):"") +
				(data.totalComments>0?", <span style='font-size:18px; padding-right: 5px;'>" + data.totalComments + "</span>" + (data.totalComments!=1?" Comments":" Comment"):"") +
				" by <span style='font-size:18px; padding-right: 5px;'>" + Object.keys(agents).length + "</span>" + (Object.keys(agents).length!=1?"Contributors":"Contributor") + 
			"</div>");
	}



	function getTitle(item) {
		return '<span style="font-weight: bold;">' + item.label + '</span>';
	}

	function getDescription(item) {
		return '<span style="font-weight: normal;">' + item.description + '</span>';
	}

	function getVersion(item) {
		return "Version " + item.version;
	}

	function displayAccessType(accessType) {
		if(accessType=='urn:domeo:access:public') {
			return ", <img id=\"groupsSpinner\" src=\"${resource(dir:'images/secure',file:'world16x16.png',plugin:'users-module')}\" /> Public"
		} else if(accessType=='urn:domeo:access:private') {
			return ", <img id=\"groupsSpinner\" src=\"${resource(dir:'images/secure',file:'personal16x16.png',plugin:'users-module')}\" /> Private"
		} else if(accessType=='urn:domeo:access:groups') {
			return ", <img id=\"groupsSpinner\" src=\"${resource(dir:'images/secure',file:'group16x16.png',plugin:'users-module')}\" /> Restricted"
		}
	}

	function displayLock(lock) {
		if(lock=='true') {
			return ", <img id=\"groupsSpinner\" src=\"${resource(dir:'images/secure',file:'lock16x16.png',plugin:'users-module')}\" /> Locked"
		} else {
			return "";
		}
	}

	function getModifyLink(item, url) {
		return "<a onclick=\"javascript:edit('" + item.set.id + "','" + url + "')\" style=\"text-decoration: none; cursor: pointer;\"><img id=\"groupsSpinner\" src=\"${resource(dir:'images/secure',file:'black_edit.gif',plugin:'users-module')}\" /> Document</a>";
	}

	function getShareLink(item) {
		return "<a onclick=\"javascript:displayShare('" + item.set.id+ "')\" style=\"text-decoration: none; cursor: pointer;\"><img id=\"groupsSpinner\" src=\"${resource(dir:'images/secure',file:'block-share.png',plugin:'users-module')}\" /> Share</a>";
	}

	function displayShare(annotationId) {
		open_in_new_tab('${appBaseUrl}/share/set/' + annotationId);
	}

	function open_in_new_tab(url)
	{
	  var win=window.open(url, '_blank');
	  win.focus();
	}

	function getHistoryLink(item) {
		return "<a onclick=\"javascript:displayHistory('" + item.set.id+ "')\" style=\"text-decoration: none; cursor: pointer;\"><img id=\"groupsSpinner\" src=\"${resource(dir:'images/secure',file:'history.png',plugin:'users-module')}\" /> History</a>";
	}

	function displayHistory(annotationSetUri) {
		document.location = '${appBaseUrl}/secure/setHistory/' + encodeURIComponent(annotationSetUri);
	}
	
	function getTarget(item) {
		return "On  <a target='_blank' href='"+ item.target+"'>"+ item.target + " <img id=\"groupsSpinner\" src=\"${resource(dir:'images/secure',file:'external.png',plugin:'users-module')}\" /></a> ";
	}

	function retrieveCitation(item) {
		var dataToSend = { url: item.target, annotationSetId: item.id };
		$.ajax({
			url: "${appBaseUrl}/ajaxBibliographic/url",
	  	  	//context: $("#resultsList"),
	  	  	data: dataToSend,
	  	  	success: function(data){
		  	  	if(data.message) {
		  	  		$("#citation-"+item.id.substring(item.id.lastIndexOf(':')+1)).html(
			  	  		"<img id=\"groupsSpinner\" src=\"${resource(dir:'images/secure',file:'black_info.gif',plugin:'users-module')}\" /> " +
			  	  		data.message  
	  	  			);
		  	  	}
	  	  	}
		});
	}  	
	
	function edit(annotationId, url) {
		// document.location = '${appBaseUrl}/web/domeo?annotationId=' + annotationId;
		document.location = '${appBaseUrl}/web/domeo?url=' + encodeURIComponent(url) + '&setId=' + encodeURIComponent(annotationId);
	}

	</script>
</head>
<body>
 	<div class="content">
    	<div class="content_resize"> 
		    <div style="background: #cc3300; color: #fff;">
		   		<ul class="bar">
					<li><a href="#">Annotation Set</a></li>
				</ul> 
		    </div>
		    <div id="progressIcon" align="center" style="padding: 5px; padding-left: 10px; display: none;"><img id="groupsSpinner" src="${resource(dir:'images',file:'progress-bar-2.gif',plugin:'users-module')}" /></div>
		    <table width="100%">
		    	<tr><td>
		    		<div id="resultsSummary" style="padding: 5px; padding-left: 10px;"></div>
		    	</td><td style="text-align:right" width="240px">
		    		<div id="resultsStats" style="padding: 5px; "></div>
		    	</td></tr>
		    </table>
	    	<div id="resultsIntro" style="padding: 10px; padding-left: 10px; width: 960px; background: #ddd;"></div>
	    	
	    	
			<div id='sidebar' class="viewerSidebar" style="padding-top: 5px;padding-bottom: 30px; padding-right:2px;">		
				<g:render template="/secure/components/domeo-tags-elements" />
				<div id='referencesTitle'></div>
				<div id="references" style="border-top:0px solid #ddd; padding-bottom: 18px;">
				</div>
				<g:render template="/secure/components/domeo-agents-elements" />
			</div>
		  	
		  	<div id="resultsListTitle" style="padding-top: 5px; padding-left: 10px; width: 615px;"></div>
		  	<div id="resultsList" style="padding-left: 10px; width: 615px;">
		  		
		  	</div>
	    	
		    <div class="resultsPagination"></div>
	      	<div class="clr"></div>
	      	<br/><br/>
	    </div>
	</div>
	
	<g:render template="/secure/components/domeo-overlay-elements" />
	
</body>
</html>