<!doctype html>
<!--[if lt IE 7 ]> <html lang="en" class="no-js ie6"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="no-js ie7"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="no-js ie8"> <![endif]-->
<!--[if IE 9 ]>    <html lang="en" class="no-js ie9"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> <html lang="en" class="no-js"><!--<![endif]-->
<head>
<g:javascript library="jquery" plugin="jquery"/>
<meta name="layout" content="domeo-secure" />
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
		
		.contextTitle {
			text-align: left;
			padding-left: 5px;
			border-top: 4px #fff solid;
		}
		
		.context {
			padding: 10px;
		}
		
		.context-content {
			border: 2px #fff solid;
			border-left: 4px #fff solid;
			border-right: 4px #fff solid;
		}
		
		blockquote.style2 {
		  font: 14px/22px normal helvetica, sans-serif;
		  margin-top: 10px;
		  margin-bottom: 10px;
		  margin-left: 10px;
		  padding-left: 15px;
		  border-left: 3px solid #ccc;
		} 
		
		blockquote.style4 {
		  font: 14px/20px;
		  padding-left: 0px;
		  padding-right: 10px;
		  margin: 5px;
		  background-position: middle left;
		  background-repeat: no-repeat;
		  text-indent: 5px;
		} 
		
		.prefix {
			color: #aaa;
			font-style: italic;
		}
		
		.match {
			font-weight: bold;
		}
		
		.suffix {
			color: #aaa;
			font-style: italic;
		}
	</style>
	<script type="text/javascript">
	$(document).ready(function() {
		$('#progressIcon').css("display","block");
	
		var dataToSend = { userId: '${loggedUser.id}', setUri:'${setUri}' };
		$.ajax({
	  	  	url: "${appBaseUrl}/ajaxPersistence/exhibitAnnotationSet",
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

				  	$("#resultsSummary").html("Saved by " + "<a onclick=\"javascript:display('" + data.set.createdById + "')\" style=\"cursor: pointer;\">" + 
				  			data.set.createdBy['foafx:name'] + "</a> on " + data.set.createdOn + "<br/>" + getVersion(data.set)
				  	     + displayAccessType(data.set.permissions['permissions:accessType']) + displayLock(data.set.permissions['permissions:isLocked']));
				  	$("#resultsStats").append('<table width="160px;"><tr><td align="left">'+getModifyLink(data, data.set.target) + '</td><td align="left"> ' + getHistoryLink(data) + '</td></tr><tr><td align="left">' + getShareLink(data) + '</td><td></td></tr></table>');
				  	$('#resultsIntro').append(
				  		getTitle(data.set) + ' ' + getDescription(data.set) + '<br/>' + getTarget(data.set) +
				  		'<div id="citation-' + data.set.id.substring(data.set.id.lastIndexOf(':')+1) + '"><img id=\"groupsSpinner\" src=\"${resource(dir:'images',file:'spinner.gif',plugin:'users-module')}\" /> Retrieving Citation</div>' 
					);
		  			retrieveCitation(data.set);
		  			$('#resultsList').append("<span style='font-size:18px; padding-right: 5px;'>" + data.items.items.length + "</span>" + (data.items.items.length!=1?"Annotations":"Annotation") + "<br/>");
		  			for(var i=0; i<data.items.items.length; i++) {
		  				$('#resultsList').append(getAnnotationTitleBar(data.items.items[i], 0, false));
		  				$('#resultsList').append("<br/>");
		  				//if(data.items.items[i].annotatedBy && data.items.items[i].annotatedBy.length>0) {
		  				//	$('#resultsList').append(getAnnotationTitleBar(data.items.items[i].annotatedBy[0], 20));
			  			//}
			  		}
		  		}
	  		}
		});
	});

	function getAnnotationTitleBar(annotation, indentation) {
		return '<div style="padding-left: ' + indentation + 'px; padding-right: ' + indentation + 'px;padding-bottom: 10px;">' + 
			'<div style="border: 1px solid #ddd;">' +
				'<table width="100%" class="barContainer">' +
					'<tr>' +
						'<td width="600px">' +
							'<div class="topBar">' +
								'<div class="titleBar"><span>' + annotation.label + '</span> ' +
									//'created on <span>' + annotation.createdOn + '</span> by ' +
						       		' by <a ex:if-exists=".createdByUri" ex:href-subcontent="http://www.google.com/search/?q={{.createdByUri}}">' +
						       			'<span>' + annotation.createdBy['foafx:name'] + '</span></a>' +
						       		'<br/>' +
						       	'</div>' +
						       	'<div class="provenanceBar">' +
						       		//'Last saved on <span>' + annotation.lastSavedOn + '</span> <span ex:if-exists=".version">with version <span>' + annotation.version + '</span></span>' +
						       	'</div>' +
							'</div>' +
						'</td>' +
						'<td>' +
							getAnnotationCommentsCounter(annotation) +
						'</td>' +
					'</tr>' +
				'</table>' +		
			'<div class="annbody">' +
	   		'<div class="annbody-content">' + annotation.content + '</div>' +
	   		
	   		getAnnotationContext(annotation) +
	   		
	   		getAnnotationComments(annotation) +
	   		'</div>' +
   		'</div>';
	}

	function getAnnotationComments(annotation) {
		if(annotation.annotatedBy && annotation.annotatedBy.length>0) {
			return '<div class="contextTitle">'+ getAnnotationCommentsCounter(annotation) +'</div>' + getAnnotationTitleBar(annotation.annotatedBy[0], 20, true);
		} else return "";
	}

	function getAnnotationCommentsCounter(annotation) {
		if (annotation.commentsCounter)
			return 	'<div class="miscBar">' +
	   		'<span ex:if-exists=".commentsCounter">' +
				'<img src="${resource(dir:'images/secure',file:'comments16x16.png',plugin:'users-module')}"/> <span>' + annotation.commentsCounter + '</span> Comments' +
			'</span>';
		else return '';
	}

	function getAnnotationContext(annotation) {
		if(annotation.match) 
		return '<div class="contextTitle">Annotating: </div>' +
			'<blockquote class="style4">' +
    			'...' +
	       		'<span ex:content=".prefix" class="prefix">' + annotation.prefix + '</span>' +
	       		'<span ex:content=".match" class="match">' + annotation.match + '</span>' +
	       		'<span ex:content=".suffix" class="suffix">' + annotation.suffix + '</span>' +
	       		'...' +
	       	'</blockquote>';
	    else return '<div class="contextTitle">Annotating: </div>' + '<div  ex:if-exists=".imageInDocumentSelector" class="context-content">' +
	       		'<img src="' + annotation.image+ '">' +
	       	'</div>' + '</div>' ;
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
	    	<div id="resultsList" style="padding: 5px; padding-left: 10px; width: 715px;">

		    </div>
		    <div class="resultsPagination"></div>
	      	<div class="clr"></div>
	      	<br/><br/>
	    </div>
	</div>
</body>
</html>