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
		
		.topBar {
			background: #eee;
			border: 5px #eee solid;
		}
		
		.titleBar {
			font-weight: bold;
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


		

		
		.tags{
	margin:0;
	padding:0;
	position:relative;
	right:24px;
	bottom:0px;
	list-style:none;
	padding-left: 2px;
	left:-10px;
	display: inline;
	}
	
.tags li, .tags a{
	float:left;
	height:16px;
	line-height:16px;
	position:relative;
	font-size:11px;
	padding-top: 2px;
	padding-bottom: 4px;
	}
	
.tags a{
	margin-left:10px;
	padding:0 7px 0 7px;
	/*
	background:#0089e0;
	color:#fff;
	*/
	color:#333;
	background:#EEEEEE;
	border: 1px solid #CCCCCC;
	
	text-decoration:none;
	-moz-border-radius-bottomright:4px;
	-webkit-border-bottom-right-radius:4px;	
	border-bottom-right-radius:4px;
	-moz-border-radius-topright:4px;
	-webkit-border-top-right-radius:4px;	
	border-top-right-radius:4px;	
	} 
	
.tags a:before{
	content:"";
	float:left;
	position:absolute;
	top:-1px;
	left:-8px;
	width:0;
	height:0;
	border-color:transparent #eee transparent transparent;
	/*
	border-color:transparent #0089e0 transparent transparent;
	*/
	border-style:solid;
	/*
	border-width:8px 8px 8px 0;	
	*/	
	border-width:10px 8px 8px 0;
    border-color: #888;
	}
	
.tags a:after{
	content:"";
	position:absolute;
	top:2px;
	left:-6px;
	float:left;
	width:4px;
	height:4px;
	-moz-border-radius:2px;
	-webkit-border-radius:2px;
	border-radius:2px;
	background:#fff;
	-moz-box-shadow:-1px -1px 2px #aaa;
	-webkit-box-shadow:-1px -1px 2px #aaa;
	box-shadow:-1px -1px 2px #aaa;
	/*
	-moz-box-shadow:-1px -1px 2px #004977;
	-webkit-box-shadow:-1px -1px 2px #004977;
	box-shadow:-1px -1px 2px #004977;
	*/
	}
	
.tags a:hover{background:#ddd; text-decoration: none;}	

.tags a:hover:before{/*border-color:transparent #ddd transparent transparent;*/}

.tags a:visited{color: #333;}

.tags .source {
	padding-left: 5px;
	font-style:italic;
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
	
	 <script>

</script>

<g:render template="/secure/components/domeo-agents-scripts" />
<g:render template="/secure/components/domeo-tags-scripts" />
<g:render template="/secure/components/domeo-references-scripts" />
<g:render template="/secure/components/domeo-annotations-scripts" />

<script type="text/javascript">

	// Contract
	var appBaseUrl = '${appBaseUrl}';
	
	// Model
	
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
		  				$('#resultsList').append(getAnnotationTitleBar(data.items.items[i], 0, false));
		  				//if(data.items.items[i].annotatedBy && data.items.items[i].annotatedBy.length>0) {
		  				//	$('#resultsList').append(getAnnotationTitleBar(data.items.items[i].annotatedBy[0], 20));
			  			//}
			  		}
		  		}
		  		
		  		//alert(Object.keys(agents).length);
		  		
		  		buildAgentsList();
		  		buildTagCloud();
		  		buildReferenceList();
		  		buildAnnotationTitle(data);
	  		}
		});

	});

	function buildAnnotationTitle(data) {
		$('#resultsListTitle').append("<div style='padding-top: 4px; padding-bottom: 5px'><span style='font-size:18px; padding-right: 5px;'>" + data.items.items.length + "</span>" + (data.items.items.length!=1?"Annotations":"Annotation") + " by <span style='font-size:18px; padding-right: 5px;'>" + Object.keys(agents).length + "</span>" + (Object.keys(agents).length!=1?"Contributors":"Contributor") + "</div>");
	}
	


	
	function getAnnotationTitleBar(annotation, indentation, annotationOnAnnotation) {
		agents[annotation.createdBy['@id']] = annotation.createdBy;
		var annotationType = annotation.type;
		if(annotationType=='ao:Qualifier') {
			for(var j=0; j<annotation.body.length;j++) {
				addTag(annotation.body[j]);
				//tags[annotation.body[j]['@id']]=annotation.body[j];
			}
		} else if(annotationType=='ao:MicroPublicationAnnotation') {
			for(var j=0; j<annotation.body[0]['mp:argues']['mp:qualifiedBy'].length;j++) {
				//var tag = annotation.body[0]['mp:argues']['mp:qualifiedBy'][j]['reif:resource'];
				//tags[tag['@id']]=tag;
				addTag(annotation.body[0]['mp:argues']['mp:qualifiedBy'][j]['reif:resource']);
			}
			if(annotation.body[0]['mp:argues']['mp:supportedBy']) {
				for(var j=0; j<annotation.body[0]['mp:argues']['mp:supportedBy'].length;j++) {
					if(annotation.body[0]['mp:argues']['mp:supportedBy'][j]['reif:resource']) {
						var ref = annotation.body[0]['mp:argues']['mp:supportedBy'][j]['reif:resource'];
						if(ref['@type'].contains('PublicationArticleReference')) {
							references[ref['@id']]=ref;
						}
					}
				}
			}
			if(annotation.body[0]['mp:argues']['mp:challengedBy']) {
				for(var j=0; j<annotation.body[0]['mp:argues']['mp:challengedBy'].length;j++) {
					alert('challengedBy');
				}
			}
		}
		
		return '<div style="padding-left: ' + indentation + 'px; padding-right: ' + indentation + 'px;padding-bottom: 10px;">' + 
			'<div style="border: 1px solid #ddd;">' +
				'<table width="100%" class="barContainer">' +
					'<tr>' +
						'<td width="500px">' +
							injectAnnotationTopBar(annotation) +
						'</td>' +
						'<td>' +
							getAnnotationCommentsCounter(annotation) +
						'</td>' +
					'</tr>' +
				'</table>' +		
				'<div class="annbody">' +
	   				'<div class="annbody-content">' + annotation.content + '</div>' +
	   				(!annotationOnAnnotation? getAnnotationContext(annotation):'')+
	   				getAnnotationComments(annotation) +
	   				'</div>' +
	   		'</div>' +
   		'</div>';
	}

	function getAnnotationComments(annotation) {
		if(annotation.annotatedBy && annotation.annotatedBy.length>0) {
			var comments = '<div class="contextTitle">'+ getAnnotationCommentsCounter(annotation) +'</div>';
			for(var j=0; j<annotation.annotatedBy.length; j++) {
				comments += getAnnotationTitleBar(annotation.annotatedBy[j], 20, true);
			}
			return comments;
		} else return "";
	}

	function getAnnotationCommentsCounter(annotation) {
		if (annotation.commentsCounter)
			return 	'<div class="miscBar">' +
	   		'<span ex:if-exists=".commentsCounter">' +
				'<img src="${resource(dir:'images/secure',file:'comments16x16.png',plugin:'users-module')}"/> <span>' + annotation.commentsCounter + '</span> ' + (annotation.commentsCounter==1?'Comment':'Comments') +
			'</span></div>';
		else return '';
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

	function getHistoryLink(item) {
		return "<a onclick=\"javascript:displayHistory('" + item.set.id+ "')\" style=\"text-decoration: none; cursor: pointer;\"><img id=\"groupsSpinner\" src=\"${resource(dir:'images/secure',file:'history.png',plugin:'users-module')}\" /> History</a>";
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


	function displaySoftware(softwareId) {
		alert('Not implemented: ' + softwareId);
	}
	
	function displayTag(resource) {
		alert('Not implemented: ' + resource['@id']);
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

</body>
</html>