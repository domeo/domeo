<!doctype html>
<!--[if lt IE 7 ]> <html lang="en" class="no-js ie6"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="no-js ie7"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="no-js ie8"> <![endif]-->
<!--[if IE 9 ]>    <html lang="en" class="no-js ie9"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> <html lang="en" class="no-js"><!--<![endif]-->
<head>
<g:javascript library="jquery" plugin="jquery"/>
<meta name="layout" content="domeo-layout-secured" />
<title>Secured Area - Domeo Browser</title>
<style>

.pagination {
    background: #f2f2f2;
    padding: 20px;
    margin-bottom: 20px;
}

.page {
    display: inline-block;
    padding: 0px 9px;
    margin-right: 4px;
    border-radius: 3px;
    border: solid 1px #c0c0c0;
    background: #e9e9e9;
    box-shadow: inset 0px 1px 0px rgba(255,255,255, .8), 0px 1px 3px rgba(0,0,0, .1);
    font-size: .875em;
    font-weight: bold;
    text-decoration: none;
    color: #717171;
    text-shadow: 0px 1px 0px rgba(255,255,255, 1);
}

.page:hover, .page.gradient:hover {
    background: #fefefe;
    background: -webkit-gradient(linear, 0% 0%, 0% 100%, from(#FEFEFE), to(#f0f0f0));
    background: -moz-linear-gradient(0% 0% 270deg,#FEFEFE, #f0f0f0);
}

.page.active {
    border: none;
    background: #616161;
    box-shadow: inset 0px 0px 8px rgba(0,0,0, .5), 0px 1px 0px rgba(255,255,255, .8);
    color: #f0f0f0;
    text-shadow: 0px 0px 3px rgba(0,0,0, .5);
}

.page.gradient {
    background: -webkit-gradient(linear, 0% 0%, 0% 100%, from(#f8f8f8), to(#e9e9e9));
    background: -moz-linear-gradient(0% 0% 270deg,#f8f8f8, #e9e9e9);
}

.pagination.dark {
    background: #414449;
    color: #feffff;
}

.page.dark {
    border: solid 1px #32373b;
    background: #3e4347;
    box-shadow: inset 0px 1px 1px rgba(255,255,255, .1), 0px 1px 3px rgba(0,0,0, .1);
    color: #feffff;
    text-shadow: 0px 1px 0px rgba(0,0,0, .5);
}

.page.dark:hover, .page.dark.gradient:hover {
    background: #3d4f5d;
    background: -webkit-gradient(linear, 0% 0%, 0% 100%, from(#547085), to(#3d4f5d));
    background: -moz-linear-gradient(0% 0% 270deg,#547085, #3d4f5d);
}

.page.dark.active {
    border: none;
    background: #2f3237;
    box-shadow: inset 0px 0px 8px rgba(0,0,0, .5), 0px 1px 0px rgba(255,255,255, .1);
}

.page.dark.gradient {
    background: -webkit-gradient(linear, 0% 0%, 0% 100%, from(#565b5f), to(#3e4347));
    background: -moz-linear-gradient(0% 0% 270deg,#565b5f, #3e4347);
}

.resultsPaginationTop { padding: 5px; padding-left: 10px;}
.resultsPaginationBottom { padding: 5px; padding-left: 10px;}

</style>
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


.topBar {
	background: #eee;
	padding: 5px;;
}
</style>
<style>
.viewerSidebar {
	float: right;
	width: 332px;
	margin-right: 8px;
}

</style>
<script type="text/javascript">

	function edit(annotationId, url) {
		document.location = '${appBaseUrl}/web/domeo?url=' + encodeURIComponent(url) + '&setId=' + encodeURIComponent(annotationId);
	}

	function display(userId) {
		document.location = '${appBaseUrl}/secure/user/' + userId;
	}

	function displayUser(userId) {
		document.location = '${appBaseUrl}/secure/user/' + userId;
	}

	function displaySet(annotationUri) {
		document.location = '${appBaseUrl}/secure/annset/' + encodeURIComponent(annotationUri);
	}

	function displayHistory(annotationSetUri) {
		document.location = '${appBaseUrl}/secure/setHistory/' + encodeURIComponent(annotationSetUri);
	}

	function displayShare(annotationId) {
		open_in_new_tab('${appBaseUrl}/share/set/' + encodeURIComponent(annotationId));
	}

	function displayByUrlShare(url) {
		open_in_new_tab('${appBaseUrl}/share/sets/?url=' + encodeURIComponent(url));
	}
	
	function open_in_new_tab(url)
	{
	  var win=window.open(url, '_blank');
	  win.focus();
	}

		

	function displayAccessType(accessType) {
		if(accessType=='urn:domeo:access:public') {
			return "Public <img id=\"groupsSpinner\" src=\"${resource(dir:'images/secure',file:'world16x16.png',plugin:'users-module')}\" />"
		} else if(accessType=='urn:domeo:access:private') {
			return "Private <img id=\"groupsSpinner\" src=\"${resource(dir:'images/secure',file:'personal16x16.png',plugin:'users-module')}\" />"
		} else if(accessType=='urn:domeo:access:groups') {
			return "Restricted <img id=\"groupsSpinner\" src=\"${resource(dir:'images/secure',file:'group16x16.png',plugin:'users-module')}\" />"
		}
	}

	function displayLock(lock) {
		if(lock=='true') {
			return ", <img id=\"groupsSpinner\" src=\"${resource(dir:'images/secure',file:'lock16x16.png',plugin:'users-module')}\" /> Locked"
		} else {
			return "";
		}
	}

	function isLocked(lock) {
		return lock=='true';
	}

	function getModifyLink(item) {
		return "<a onclick=\"javascript:edit('" + item.lastAnnotationSetIndex.lastVersion.individualUri + "', '" + item.lastAnnotationSetIndex.lastVersion.annotatesUrl + "')\" style=\"text-decoration: none; cursor: pointer;\"><img id=\"groupsSpinner\" src=\"${resource(dir:'images/secure',file:'black_edit.gif',plugin:'users-module')}\" /> Document</a><br/>";
	}

	function getExploreLink(item) {
		return "<a onclick=\"javascript:displaySet('" + item.lastAnnotationSetIndex.lastVersion.individualUri + "')\" style=\"text-decoration: none; cursor: pointer;\"><img id=\"groupsSpinner\" src=\"${resource(dir:'images/secure',file:'clipboard-list.png',plugin:'users-module')}\" /> Browse</a><br/>";
	}

	function getShareLink(item) {
		return "<a onclick=\"javascript:displayShare('" + item.lastAnnotationSetIndex.lastVersion.individualUri + "')\" style=\"text-decoration: none; cursor: pointer;\"><img id=\"groupsSpinner\" src=\"${resource(dir:'images/secure',file:'block-share.png',plugin:'users-module')}\" /> Share</a><br/>";
	}

	function getShareByUrlLink(url) {
		return "<a onclick=\"javascript:displayByUrlShare('" + url + "')\" style=\"text-decoration: none; cursor: pointer;\"><img id=\"groupsSpinner\" src=\"${resource(dir:'images/secure',file:'block-share.png',plugin:'users-module')}\" /> Export all sets</a><br/>";
	}

	function getHistoryLink(item) {
		if(item.lastAnnotationSetIndex.lastVersion.versionNumber>1) 
		return "<a onclick=\"javascript:displayHistory('" + item.lastAnnotationSetIndex.lastVersion.individualUri + "')\" style=\"text-decoration: none; cursor: pointer;\"><img id=\"groupsSpinner\" src=\"${resource(dir:'images/secure',file:'history.png',plugin:'users-module')}\" /> History</a><br/>";
		else return "";
	}

	function getTarget(item) {
		var u = item.lastAnnotationSetIndex.lastVersion.annotatesUrl;
		var temp = String(u);
		if(temp.length>60) {
			u = temp.substring(0, 30) + '...' + temp.substring(temp.length-25);
		}
		return "On  <a href='#' onclick='javascript:loadData(\""+item.lastAnnotationSetIndex.lastVersion.annotatesUrl+"\")'>"+ u + "</a> ";
	}

	function getTargetOut(item) {
		var u = item;
		var temp = String(u);
		if(temp.length>60) {
			u = temp.substring(0, 30) + '...' + temp.substring(temp.length-25);
		}
		return "Annotation sets for:<br/>  <a target='_blank' href='"+item+"'>"+ u + " <img id=\"groupsSpinner\" src=\"${resource(dir:'images/secure',file:'external.png',plugin:'users-module')}\" /></a> ";
	}

	function getProvenanceCreator(item) {
		return 'by <a onclick=\"javascript:displayUser(\'' + item.lastAnnotationSetIndex.lastVersion.createdBy.id + '\')\" style=\"cursor: pointer;\">' + item.lastAnnotationSetIndex.lastVersion.createdBy.displayName + '</a>';
	}
	
	function getProvenanceDate(item) {
		return 'Last saved on ' + item.lastAnnotationSetIndex.lastVersion.createdOn + ' with v. ' + item.lastAnnotationSetIndex.lastVersion.versionNumber;
	}
	

	function getStats(item) {
		return "<span style='font-size:18px; padding-right: 5px;'>" + item.lastAnnotationSetIndex.lastVersion.size + "</span>" + (item.lastAnnotationSetIndex.lastVersion.size!=1?'items':'item') + displayLock(item.isLocked);
	}

	function retrieveCitation(item) {
		var dataToSend = { url: item.lastAnnotationSetIndex.lastVersion.annotatesUrl, annotationId: item.lastAnnotationSetIndex.lastVersion.id };
		$.ajax({
			url: "${appBaseUrl}/ajaxBibliographic/url",
	  	  	context: $("#resultsList"),
	  	  	data: dataToSend,
	  	  	success: function(data){
		  	  	if(data.message) {	
		  	  		$("#citation-"+item.lastAnnotationSetIndex.lastVersion.id).html(
				  	  		"<img id=\"groupsSpinner\" src=\"${resource(dir:'images/secure',file:'black_info.gif',plugin:'users-module')}\" /> " +
				  	  		data.message  
		  	  			);
		  	  	}
	  	  	}
		});
	}

	var paginationOffset;
	var paginationRange;

	function loadAnnotationSets(url, paginationOffset, paginationRange) {
		
		
		$("#resultsList").empty();
		$('.resultsPaginationTop').empty();
		$('.resultsPaginationBottom').empty(); 

		var groups = '';
		$(".groupCheckbox").each(function(i) {
			if($(this).attr('checked')!=undefined) 
				groups += $(this).attr('value') + " ";
		});

	
		
		try {
			var dataToSend = { 
				id: '${loggedUser.id}', 
				documentUrl: url,
				paginationOffset:paginationOffset, 
				paginationRange:paginationRange, 
				publicData: $("#publicFilter").attr('checked')!==undefined, 
				groupsData: $("#groupsFilter").attr('checked')!==undefined, 
				groupsIds: groups,
				privateData:$("#privateFilter").attr('checked')!==undefined
			};
			$.ajax({
		  	  	url: "${appBaseUrl}/ajaxPersistence/browseAnnotationSets",
		  	  	context: $("#resultsList"),
		  	  	data: dataToSend,
		  	  	success: function(data){
		  			$("#progressIcon").css("display","none");
		  			if(!data.annotationListItemWrappers || data.annotationListItemWrappers==null
		  		  			|| data.annotationListItemWrappers==undefined) {
		  				$("#resultsSummary").html("");
						$("#resultsList").html("No results to display");
			  		} else {
			  			var label = data.annotationListItemWrappers.length == 1 ? ' Set' : ' Sets';
			  			if(data.annotationListItemWrappers.length == 0) {
			  				$("#resultsSummary").html("No set meeting the filtering criteria");
			  			} else if(data.annotationListItemWrappers.length == 1) {
			  				if(data.totalResponses==1)
								$("#resultsSummary").html("Set <span style='font-weight: bold;font-size:16px;'>1</span> out of <span style='font-weight: bold;font-size:16px;'>" + 
										(data.totalResponses>-1?data.totalResponses:0) + '</span> meeting the filtering criteria');
			  				else
								$("#resultsSummary").html("Set <span style='font-weight: bold;font-size:16px;'>" + (data.paginationOffset+1) + "</span> out of <span style='font-weight: bold;font-size:16px;'>" + 
										(data.totalResponses>-1?data.totalResponses:0) + '</span> meeting the filtering criteria');
						} else {	  			
				  			$("#resultsSummary").html(label + " <span style='font-weight: bold;font-size:16px;'>" + 
						  			(data.paginationOffset!=0?data.paginationOffset+1:1) + " - " + 
				  					(data.paginationOffset+Math.min(data.paginationRange,data.annotationListItemWrappers.length)) + 
						  			"</span> out of <span style='font-weight: bold;font-size:16px;'>" + (data.totalResponses>-1?data.totalResponses:0) + '</span> meeting the filtering criteria');
						}
						
			  			if(data.latestContributor) {
				  			$("#resultsStats").html("Last by " + "<a onclick=\"javascript:display('" + data.latestContributor.id + "')\" style=\"cursor: pointer;\">" + 
				  		  			data.latestContributor.displayName + "</a><br/> on " + data.latestContribution);
				  		}
				  		if(url) {
					  		$('#resultsList').append(getTargetOut(url)); 
					  		$('#resultsList').append("<br/>");
					  		$('#resultsList').append(getShareByUrlLink(url));
					  		$('#resultsList').append("<br/>");
					  		//if(getURLParameter('url')==null) {
					  		//	window.history.pushState({url:url},"", window.location+'/?url='+url);
					  		//}
				  		}
	
				  		var users = new Array();
	
				  		var numberButtons = Math.ceil(data.totalResponses/data.paginationRange);
				  		var currentPage = Math.floor((data.paginationOffset+1)/data.paginationRange);

				  		var paginationHtml = '';
				  		//var paginationHtml = '<a href="#" class="page">first</a>';
				  		for(var x=0; x<numberButtons; x++) {
					  		if(x==currentPage) paginationHtml += '<a href="#" class="page active">' + (x+1) + '</a>';
					  		else paginationHtml += '<a href="#" class="page" onclick="loadAnnotationSets(\'\',' + (x*data.paginationRange)+ ')"">' + (x+1) + '</a>';
					  	}
				  		//paginationHtml += '<a href="#" class="page">last</a>';
				  		
				  		
				  		$('.resultsPaginationTop').append(paginationHtml);
				  		
				  		$('.resultsPaginationBottom').append(paginationHtml);
				  		
			  			$.each(data.annotationListItemWrappers, function(i,item){
			  				$('#resultsList').append('<div style="border: 1px solid #eee; padding: 3px; margin-bottom:10px;">' +
					  			'<table width="100%"><tr><td class="topBar">' + 
					  			(item.lastAnnotationSetIndex.lastVersion.type=='domeo:DiscussionSet'?"<img id=\"groupsSpinner\" src=\"${resource(dir:'images/secure',file:'commentsIcon_24.png')}\" /> ":'') +
			  					'<span style="font-weight: bold;">'+item.lastAnnotationSetIndex.lastVersion.label + '</span> ' + getProvenanceCreator(item)  + '<br/>' +
			  					getProvenanceDate(item)  +
			  					
			  					'</div>' +
			  					'</td>' +
			  					'<td width="90" class="topBar" align="right">' +
			  					getStats(item) + 
			  					'<br/>' +
			  					displayAccessType(item.permissionType) + 
			  					
			  					'</td>' +
			  					'<td width="100px" rowspan="2" style="padding-left:10px; border-left: 0px solid #eee; vertical-align: top;">' +
			  					
			  					getModifyLink(item) +
			  					getExploreLink(item) +
			  					getShareLink(item) +
			  					getHistoryLink(item) + 
					  			'</td>' +
					  			'</tr><tr><td>' + 
					  			(item.lastAnnotationSetIndex.lastVersion.description!='The default set is created automatically by Domeo when no other set is existing.' && item.lastAnnotationSetIndex.lastVersion.description!=''? (item.lastAnnotationSetIndex.lastVersion.description + '<br/>') :'') +
			  					
			  					
			  					getTarget(item)  +
			  					'<div id="citation-'+item.lastAnnotationSetIndex.lastVersion.id+'"><img id=\"groupsSpinner\" src=\"${resource(dir:'images',file:'spinner.gif',plugin:'users-module')}\" /> Retrieving Citation</div>' +
					  			'</td>' + 
			  					'</tr></table>' 
			  					
			  					//getTarget(item)  +
			  					//'<div id="citation-'+item.lastAnnotationSetIndex.lastVersion.id+'"><img id=\"groupsSpinner\" src=\"${resource(dir:'images',file:'spinner.gif',plugin:'users-module')}\" /> Retrieving Citation</div>' +
			  					//'</div>' +
			  					
			  					//'<br/>'
								
			  					);
			  				retrieveCitation(item);
							users[i] = item.lastAnnotationSetIndex.lastVersion.createdBy;
			  				//alert(item.lastAnnotationSetIndex.lastVersion.createdBy.displayName);
			  				//$('#resultsList').append('<input type="checkbox" name="vehicle" value="Bike">' 
			  		  		//		+ item.group.name + '<br/>'); 
			  		  				 
					  				//item.dateCreated + '</td><td>'+ roles +
					  				//'</td><td> '+ item.status.label + '</td></tr>');
			  		    });	
			  		}  			
			  	}
		  	});
		} catch(e) {
			alert(e);
		}
	}
	
	function getURLParameter(name) {
	    return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)','i').exec(window.location)||[,""])[1].replace(/\+/g, '%20'))||null;
	}

	function loadData(url) {
		loadAnnotationSets(url, paginationOffset, paginationRange);
	}

	/*
	function loadData(url) {
		$("#resultsList").empty();
		var dataToSend = { id: '${loggedUser.id}', documentUrl: url, paginationOffset:paginationOffset, paginationRange:paginationRange };
		$.ajax({
	  	  	url: "${appBaseUrl}/ajaxPersistence/annotationSets",
	  	  	context: $("#resultsList"),
	  	  	data: dataToSend,
	  	  	success: function(data){
	  			$("#progressIcon").css("display","none");
	  			if(!data.annotationListItemWrappers || data.annotationListItemWrappers==null
	  		  			|| data.annotationListItemWrappers==undefined) {
	  				$("#resultsSummary").html("");
					$("#resultsList").html("No results to display");
		  		} else {
		  			var label = data.annotationListItemWrappers.length == 1 ? ' Set' : ' Sets';
		  			$("#resultsSummary").html("Displaying <span style='font-weight: bold;''>" + (data.paginationOffset+1) + " - " + data.paginationRange +label+
		  				"</span> out of " + data.totalResponses);
		  			if(data.latestContributor) {
			  			$("#resultsStats").html("Last by " + "<a onclick=\"javascript:display('" + data.latestContributor.id + "')\" style=\"cursor: pointer;\">" + 
			  		  			data.latestContributor.displayName + "</a><br/> on " + data.latestContribution);
			  		}
			  		if(url) {
				  		$('#resultsList').append(getTargetOut(url)); 
				  		$('#resultsList').append("<br/>");
				  		$('#resultsList').append(getShareByUrlLink(url));
				  		$('#resultsList').append("<br/>");
				  		window.history.pushState({url:url},"", window.location+'/?url='+url);
			  		}

			  		var users = new Array();

			  		var numberButtons = Math.ceil(data.totalResponses/data.paginationRange);
			  		var currentPage = Math.floor((data.paginationOffset+1)/data.paginationRange);
			  		
			  		var paginationHtml = '<a href="#" class="page">first</a>';
			  		for(var x=0; x<numberButtons; x++) {
				  		if(x==currentPage) paginationHtml += '<a href="#" class="page active">' + (x+1) + '</a>';
				  		else paginationHtml += '<a href="#" class="page" onclick="loadAnnotationSets(\'\',' + (x*data.paginationRange)+ ')"">' + (x+1) + '</a>';
				  	}
			  		paginationHtml += '<a href="#" class="page">last</a>';

			  		$('.resultsPaginationTop').empty();
			  		$('.resultsPaginationTop').append(paginationHtml);
			  		$('.resultsPaginationBottom').empty(); 
			  		$('.resultsPaginationBottom').append(paginationHtml);
			  		
		  			$.each(data.annotationListItemWrappers, function(i,item){
		  				$('#resultsList').append('<div style="border: 1px solid #eee; padding: 3px; margin-bottom:10px;">' +
				  			'<table width="100%"><tr><td class="topBar">' +
		  					'<span style="font-weight: bold;">'+item.lastAnnotationSetIndex.lastVersion.label + '</span> ' + getProvenanceCreator(item)  + '<br/>' +
		  					getProvenanceDate(item)  +
		  					
		  					'</div>' +
		  					'</td>' +
		  					'<td width="90" class="topBar" align="right">' +
		  					getStats(item) + 
		  					'<br/>' +
		  					displayAccessType(item.permissionType) + 
		  					
		  					'</td>' +
		  					'<td width="90px" rowspan="2" style="padding-left:4px; border-left: 0px solid #eee;">' +
		  					
		  					getModifyLink(item) +
		  					getExploreLink(item) +
		  					getShareLink(item) +
		  					getHistoryLink(item) + 
				  			'</td>' +
				  			'</tr><tr><td>' + 
				  			(item.lastAnnotationSetIndex.lastVersion.description!='The default set is created automatically by Domeo when no other set is existing.' && item.lastAnnotationSetIndex.lastVersion.description!=''? (item.lastAnnotationSetIndex.lastVersion.description + '<br/>') :'') +
		  					
		  					
		  					getTarget(item)  +
		  					'<div id="citation-'+item.lastAnnotationSetIndex.lastVersion.id+'"><img id=\"groupsSpinner\" src=\"${resource(dir:'images',file:'spinner.gif',plugin:'users-module')}\" /> Retrieving Citation</div>' +
				  			'</td>' + 
		  					'</tr></table>' 
		  					
		  					//getTarget(item)  +
		  					//'<div id="citation-'+item.lastAnnotationSetIndex.lastVersion.id+'"><img id=\"groupsSpinner\" src=\"${resource(dir:'images',file:'spinner.gif',plugin:'users-module')}\" /> Retrieving Citation</div>' +
		  					//'</div>' +
		  					
		  					//'<br/>'
							
		  					);
		  				retrieveCitation(item);
						users[i] = item.lastAnnotationSetIndex.lastVersion.createdBy;
		  				//alert(item.lastAnnotationSetIndex.lastVersion.createdBy.displayName);
		  				//$('#resultsList').append('<input type="checkbox" name="vehicle" value="Bike">' 
		  		  		//		+ item.group.name + '<br/>'); 
		  		  				 
				  				//item.dateCreated + '</td><td>'+ roles +
				  				//'</td><td> '+ item.status.label + '</td></tr>');
		  		    });	
		  		}  			
		  	}
	  	});
	}
	*/

	$(document).ready(function() {
		$('#progressIcon').css("display","block");
		//var url = getURLParameter("url")!=null ? getURLParameter("url"):'';
		var url = '';
		loadAnnotationSets('${url}',0);
	});
	

	
</script>
</head>
<body>
  <div class="content">
    <div class="content_resize">
 		 <div class="container">
 		 	<!-- Browsing Navigation -->
		    <div style="background: #616161; color: #fff; line-height: 10px; padding-top:10px; font-weight: bold; padding-bottom: 10px; margin-bottom: 10px; height: 30px;">
		    	&nbsp;
		   		Annotation Sets for URL ${url} ${error}
		    </div>
 		 
 		 	<div id="sidebar" class="viewerSidebar well" >
		    	<div id='contributorsTitle'>Filtering by permissions</div>
				<div id="contributors" style="border-top: 3px solid #ddd; padding-bottom: 2px;"></div>
		    	<div style="padding: 5px; padding-top: 10px; ">
				    <input id="publicFilter" type="checkbox" name="vehicle" checked="checked"> Public<br>
				    <input id="privateFilter" type="checkbox" name="vehicle" checked="checked"> Private<br/>
				    
				  	<g:if test="${userGroups.size()>0}">
					  	<div id="groupsList">
					  	 	<br/>Groups<br/>	    
					  		<g:each in="${userGroups}" status="i" var="usergroup">
					  			<input type="checkbox" name="${usergroup.group.name}" class="groupCheckbox" value="${usergroup.group.id}"> ${usergroup.group.name}<br/>
					  		</g:each>
					  	</div>
				  	</g:if>
					<br/>
				    
					
					<div align="center"><input value="Refresh" title="Search" name="lucky" type="submit" id="btn_i" onclick="loadAnnotationSets('${url}', 0, '')" class="btn btn-success"></div>
				</div>
		  	</div>
 		 

	      
		    <div id="progressIcon" align="center" style="padding: 5px; padding-left: 10px; display: none;">
		    	<img id="groupsSpinner" src="${resource(dir:'images/secured',file:'ajax-loader-4CAE4C.gif',plugin:'users-module')}" />
		    </div>
		    <table width="790px;">
		    	<tr><td>
		    		<div id="resultsSummary" style="padding:5px; padding-left: 10px;"></div>
		    		<div class="resultsPaginationTop"></div>
		    	</td><td style="text-align:right">
		    		<div id="resultsStats" style="padding: 5px; "></div>
		    	</td></tr>
		    </table>
		    
		    <div id="resultsList" style="padding: 5px; padding-left: 10px; width: 790px;"></div>
		    <div class="resultsPaginationBottom"></div>
	      	<div class="clr"></div>
	     </div>
    </div>
  </div>
</body>
</html>
