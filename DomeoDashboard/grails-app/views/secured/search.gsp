<!doctype html>
<!--[if lt IE 7 ]> <html lang="en" class="no-js ie6"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="no-js ie7"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="no-js ie8"> <![endif]-->
<!--[if IE 9 ]>    <html lang="en" class="no-js ie9"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> <html lang="en" class="no-js"><!--<![endif]-->
<head>
	<meta name="layout" content="domeo-layout-secured" />
	<title>Secured Area - Domeo - The Annotation Toolkit</title>
	
<script>
$(document).ready(function() {
	//hideBasicInfoComponents();
	
	$('#queryField').bind("enterKey",function(e){
	   searchAnnotation();
	});
	$('#queryField').keyup(function(e){
	    if(e.keyCode == 13)
	    {
	        searchAnnotation();
	    }
	});

	//$("#publicFilter").attr('checked', true);
	//$("#privateFilter").attr('checked', true);

	
	
	try {
		$("#domeoSearch form").submit(function(e) {
			search();		
			return e.preventDefault();
		});
	} catch(e) {
		alert(e);
	}
	//alert('ready');

	if('${query}') {
		$('#queryField').val(decodeURI('${query}'));
		var offset = ('${offset}'.length>0?'${offset}':0);
		if($('#queryField').val() && ${offset}>0) searchAnnotation(${offset});
		else searchAnnotation();
	}
});

function searchAnnotation(paginationOffset, paginationRange) {
	var query = $('#queryField').val();
	if(!query) {
		alert('No search criteria defined!');
		return
	}

	//alert($("#publicFilter").val());
	
	// Modify the url
	var url = "home?query="+encodeURI(query);
	if(paginationOffset) url = url + "&offset=" + paginationOffset;
	if(paginationRange) url = url + "&range=" + paginationRange;
	window.history.pushState("", "", encodeURI(url));

	var groups = '';
	$(".groupCheckbox").each(function(i) {
		if($(this).is(':checked')) 
			groups += 'urn:group:uuid:' + $(this).attr('value') + " ";
	});

	var dataToSend = { 
		userId: '${loggedUser.id}', 
		query: query,
		paginationOffset: paginationOffset, 
		paginationRange: paginationRange, 
		permissionsPublic: $("#publicFilter").is(':checked'), 
		permissionsGroups: (groups.length>0?true:false), 
		groupsIds: groups,
		permissionsPrivate: $("#privateFilter").is(':checked'),
		agentHuman: $("#agentHuman").attr('checked')!==undefined, 
		agentSoftware: $("#agentSoftware").attr('checked')!==undefined, 
	};
	
	$("#progressIcon").show();
	$("#resultsList").html("");
	$('.resultsPaginationTop').empty();
	$('.resultsPaginationBottom').empty(); 
	
	var savingRequest = $.ajax({
		type: "POST",
		contentType : "text/plain",
        dataType: 'json', 
		url: "${request.getContextPath()}/ajaxPersistence/searchAnnotationSets",
		data: JSON.stringify(dataToSend)
	}).done(function( data ) {
		$("#progressIcon").hide();
		
		$("#resultsList").html("");
		$('.resultsPaginationTop').empty();
		$('.resultsPaginationBottom').empty(); 
		

		if(data.annotationListItemWrappers.length==0) {
			$('#resultsList').append("No results<br/>");
		} 
		
		var numberButtons = Math.ceil(data.totalResponses/data.paginationRange);
  		var currentPage = Math.floor((data.paginationOffset+1)/data.paginationRange);
  		//alert(data.paginationRange + '-' + data.paginationOffset + '-' + currentPage);

		var label = data.annotationListItemWrappers.length == 1 ? ' Set' : ' Sets';
			if(data.annotationListItemWrappers.length == 0) {
				$("#resultsSummary").html("No set meeting the filtering criteria");
			} else if(data.annotationListItemWrappers.length == 1) {
				if(data.totalResponses==1)
				$("#resultsSummary").html("Set <span style='font-weight: bold;font-size:16px;'>1</span> out of <span style='font-weight: bold;font-size:16px;'>" + 
						(data.totalResponses>-1?data.totalResponses:0) + '</span> meeting the searching criteria');
			else
				$("#resultsSummary").html("Set <span style='font-weight: bold;font-size:16px;'>" + (data.paginationOffset+1) + "</span> out of <span style='font-weight: bold;font-size:16px;'>" + 
						(data.totalResponses>-1?data.totalResponses:0) + '</span> meeting the searching criteria');
		} else {	  			
  			$("#resultsSummary").html(label + " <span style='font-weight: bold;font-size:16px;'>" + 
		  			(data.paginationOffset!=0?data.paginationOffset+1:1) + " - " + 
  					(data.paginationOffset+Math.min(data.paginationRange,data.annotationListItemWrappers.length)) + 
		  			"</span> out of <span style='font-weight: bold;font-size:16px;'>" + (data.totalResponses>-1?data.totalResponses:0) + '</span> meeting the searching criteria');
		}

		var paginationHtml = '';
  		//var paginationHtml = '<a href="#" class="page">first</a>';
  		for(var x=0; x<numberButtons; x++) {
	  		if(x==currentPage) paginationHtml += '<a href="#" class="page active">' + (x+1) + '</a>';
	  		else paginationHtml += '<a href="#" class="page" onclick="searchAnnotation(' + (x*data.paginationRange)+ ')"">' + (x+1) + '</a>';
	  	}

  		$('.resultsPaginationTop').append(paginationHtml);	  		
  		$('.resultsPaginationBottom').append(paginationHtml);

  		var users = new Array();
  		$.each(data.annotationListItemWrappers, function(i,item){
			var color = i%2==0?"#fff":"#fff"; //"#FFF8DC"
			$('#resultsList').append(
				'<div style="border: 1px solid #eee; padding: 5px; background: '+color+'">' +
				'<table width="100%" style="border-bottom: solid #ddd 1px;">' +
					'<tr>' + 
						'<td class="topBar">' +
							'<span style="font-weight: bold;">'+getName(item) + '</span> ' +
							getProvenanceCreator(item) + '<br/>' +
							getProvenanceDate(item) +
						'</td>' +
						'<td width="90" class="topBar" align="right">' +
							getStats(item) + '<br/>' +
							displayAccessType(item.permissionType) +
						'</td>' +
						'<td width="100px" rowspan="2" style="padding-left:10px; border-left: 0px solid #eee; vertical-align: top;">' +
							getModifyLink(i, item) +
							getExploreLink(item) +
							getShareLink(i, item) +
							getHistoryLink(item) +
						'</td>' +
					'</tr>' + 
					'<tr>' +
						'<td colspan="2">' + 
							getDescription(item) + 
							getTarget(item)  +
						'</td>' +
					'</tr>' + 
					'<tr>' +
						'<td>' + 
							'<div id="citation-'+item.annotationSetIndex.id+'"><img id=\"groupsSpinner\" src=\"${resource(dir:'images',file:'spinner.gif',plugin:'users-module')}\" /> Retrieving Citation</div>' +
						'</td>' +
					'</tr>' +
					'<tr>' +
						'<td style="height:8px;"> </td>' +
					'</tr>' +
				'</table>' +
				'<div id="items-summary-'+item.annotationSetIndex.id+'" style="padding-left:0px; margin-left: 7px; padding-top: 5px;"><img id=\"groupsSpinner\" src=\"${resource(dir:'images',file:'spinner.gif',plugin:'users-module')}\" /> Retrieving Items Summary</div>' +
				'<div id="items-'+item.annotationSetIndex.id+'" style="padding-left:12px; margin-left: 7px;border-left: 2px #999 solid;"><img id=\"groupsSpinner\" src=\"${resource(dir:'images',file:'spinner.gif',plugin:'users-module')}\" /> Retrieving Items</div>' +
				'</div>' 
			);
			
			retrieveCitation(item);
			retrieveItems(item.annotationSetIndex.id, item.annotationSetIndex.individualUri, $('#queryField').val());

			$('#resultsList').append("<br/>");
			users[i] = item.createdBy;
  	 	});	
	}).fail(function() { 
		alert("search error"); 
	}).always(function() { 
		// Do nothing
	});	
}

function getName(item) {
	if(item.annotationSetIndex.label && item.annotationSetIndex.label>0 && item.annotationSetIndex.label!='Default Set') 
		return item.annotationSetIndex.label;
	else return 'Annotation Set'
}

function getProvenanceCreator(item) {
	return 'by <a onclick=\"javascript:displayUser(\'' + item.annotationSetIndex.createdBy.id + '\')\" style=\"cursor: pointer;\">' + item.annotationSetIndex.createdBy.displayName + '</a>';
}

function getProvenanceDate(item) {
	return 'Last saved on ' + item.createdOn + ' with v. ' + item.versionNumber;
}

function getStats(item) {
	return "<span style='font-size:18px; padding-right: 5px;'>" + item.annotationSetIndex.size + "</span>" + (item.annotationSetIndex.size!=1?'items':'item') + displayLock(item.isLocked);
}

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
	document.location = '${appBaseUrl}/secured/annotationSet/' + encodeURIComponent(annotationUri);
}

function displayHistory(annotationSetUri) {
	document.location = '${appBaseUrl}/secured/annotationSetHistory/' + encodeURIComponent(annotationSetUri);
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


function displayLock(lock) {
	if(lock=='true') {
		return ", <img id=\"groupsSpinner\" src=\"${resource(dir:'images/secure',file:'lock16x16.png',plugin:'users-module')}\" /> Locked"
	} else {
		return "";
	}
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

function getModifyLink(i, item) {
	return "<a onclick=\"javascript:edit('" + item.annotationSetIndex.individualUri + "','" + item.annotationSetIndex.annotatesUrl + "')\" style=\"text-decoration: none; cursor: pointer;\"><img id=\"groupsSpinner\" src=\"${resource(dir:'images/secure',file:'black_edit.gif',plugin:'users-module')}\" /> Document</a><br/>";
}

function getExploreLink(item) {
	return "<a onclick=\"javascript:displaySet('" + item.annotationSetIndex.individualUri + "')\" style=\"text-decoration: none; cursor: pointer;\"><img id=\"groupsSpinner\" src=\"${resource(dir:'images/secure',file:'clipboard-list.png',plugin:'users-module')}\" /> Browse</a><br/>";
}

function getShareLink(i, item) {
	return "<a onclick=\"javascript:displayShare('" + item.annotationSetIndex.individualUri + "')\" style=\"text-decoration: none; cursor: pointer;\"><img id=\"groupsSpinner\" src=\"${resource(dir:'images/secure',file:'block-share.png',plugin:'users-module')}\" /> Share</a><br/>";
}

function getHistoryLink(item) {
	if(item.versionNumber>1) 
	return "<a onclick=\"javascript:displayHistory('" + item.annotationSetIndex.individualUri + "')\" style=\"text-decoration: none; cursor: pointer;\"><img id=\"groupsSpinner\" src=\"${resource(dir:'images/secure',file:'history.png',plugin:'users-module')}\" /> History</a><br/>";
	else return "";
}

function getDescription(item) {
	if(item.annotationSetIndex.description && item.annotationSetIndex.description.length>0 && item.annotationSetIndex.description!='The default set is created automatically by Domeo when no other set is existing.')
		return item.annotationSetIndex.description + '<br/>';
	else return "";
}

function getTarget(item) {
	var u = item.annotationSetIndex.annotatesUrl;
	var temp = String(u);
	if(temp.length>60) {
		u = temp.substring(0, 30) + '...' + temp.substring(temp.length-25);
	}
	return "On  <a href='#' onclick='javascript:loadData(\""+item.annotationSetIndex.annotatesUrl+"\")'>"+ u + " <img id=\"groupsSpinner\" src=\"${resource(dir:'images/secure',file:'show.gif',plugin:'users-module')}\" /></a> ";
}

function retrieveCitation(item) {
	var dataToSend = { url: item.annotationSetIndex.annotatesUrl, annotationId: item.annotationSetIndex.id };
	$.ajax({
		url: "${appBaseUrl}/ajaxBibliographic/url",
  	  	context: $("#resultsList"),
  	  	data: dataToSend,
  	  	success: function(data){
	  	  	if(data.message) {	
	  	  		$("#citation-"+item.annotationSetIndex.id).html(
		  	  		"<img id=\"groupsSpinner\" src=\"${resource(dir:'images/secure',file:'black_info.gif',plugin:'users-module')}\" /> " +
		  	  		data.message  
  	  			);
	  	  	}
  	  	}
	});
}

function retrieveItems(setId, setIndividualUri, query) {
	var dataToSend = { setId: setIndividualUri, query: query };
	$.ajax({
		type: "POST",
		dataType: "json",
		contentType : "text/plain",
		url: "${appBaseUrl}/ajaxPersistence/searchSet",
  	  	context: $("#resultsList"),
  	  	data: JSON.stringify(dataToSend),
  	  	success: function(data){
			var summ = "<span style='font-size:18px; padding-right: 5px;'>" + data.length + "</span> " + (data.length==1?'Item':'Items') + " of interest";
	
	  	  	
	  	  	$("#items-"+setId).html('');
	  	    $("#items-summary-"+setId).html('');
	  	  	$("#items-summary-"+setId).append('<div style="padding-bottom: 5px;">' + summ+ '</div>');
	  	  	
	  	  	$.each(data, function(i,item){
	  	  		//$("#items-"+setId).append("<br/>");
		  	  	if(item._source["@type"]=='ao:Highlight') {
		  	  		$("#items-"+setId).append(
			  	  		"Highlight (score: " + item._score + ") <br/>" 
	  	  			);
		  	  		$("#items-"+setId).append(
		  	  			'<span class="prefix">' + item._source["ao:context"][0]["ao:hasSelector"]["ao:prefix"] + "</span>" 
				  	);
		  	  		$("#items-"+setId).append(
		  	  			'<span class="match">' + item._source["ao:context"][0]["ao:hasSelector"]["ao:exact"] + "</span>" 
				  	);
		  	  		$("#items-"+setId).append(
		  	  			'<span class="suffix">' + item._source["ao:context"][0]["ao:hasSelector"]["ao:suffix"] + "</span>" 
				  	);
		  	  	} else if(JSON.stringify(item._source["@type"]).indexOf('ao:PostIt')>0) {
		  	  		$("#items-"+setId).append(
			  	  		"Post it (score: " + item._score + ") <br/>" 
	  	  			);
	  	  			//alert(item._source["ao:context"][0]["ao:hasSelector"]["ao:prefix"]);
	  	  			if(item._source["ao:context"][0]["ao:hasSelector"]["ao:prefix"] || item._source["ao:context"][0]["ao:hasSelector"]["ao:exact"] 
		  	  				|| item._source["ao:context"][0]["ao:hasSelector"]["ao:suffix"] ) {
			  	  		$("#items-"+setId).append(
			  	  			'<span class="prefix">' + item._source["ao:context"][0]["ao:hasSelector"]["ao:prefix"] + "</span>" 
					  	);
			  	  		$("#items-"+setId).append(
			  	  			'<span class="match">' + item._source["ao:context"][0]["ao:hasSelector"]["ao:exact"] + "</span>" 
					  	);
			  	  		$("#items-"+setId).append(
			  	  			'<span class="suffix">' + item._source["ao:context"][0]["ao:hasSelector"]["ao:suffix"] + "</span>" 
					  	);
	  	  			} else if(item._source["ao:context"][0]["domeo:displaySource"]) {
		  	  			$("#items-"+setId).append(
			  	  			'<img alt="Loading image...."  src="' + item._source["ao:context"][0]["domeo:displaySource"] + '"/>' 
					  	);
			  	  	}

	  	  			$("#items-"+setId).append(
	  	  				'<br/>' + item._source["rdfs:label"] + ': '+item._source["ao:body"][0]['cnt:chars']
			  	  	);
			  	} else if(item._source["@type"]=='ao:Qualifier') {
		  	  		$("#items-"+setId).append(
			  	  		"Qualifier (score: " + item._score + ") <br/>" 
	  	  			);
		  	  		$("#items-"+setId).append(
		  	  			'<div class="match" style="display: inline;"><a target="_blank" href="' + item._source["ao:hasTopic"][0]["@id"] + '">' + item._source["ao:hasTopic"][0]["rdfs:label"] + "</a>" +
		  	  			"</div> <div style='display: inline;'>from " + item._source["ao:hasTopic"][0]["dct:source"]["rdfs:label"] + "</div><br/>" 
		  	  		);
		  	  		if(item._source["ao:context"][0]["ao:hasSelector"]["ao:prefix"] || item._source["ao:context"][0]["ao:hasSelector"]["ao:exact"] 
  	  						|| item._source["ao:context"][0]["ao:hasSelector"]["ao:suffix"] ) {
			  	  		$("#items-"+setId).append(
			  	  			'<span class="prefix">' + item._source["ao:context"][0]["ao:hasSelector"]["ao:prefix"] + "</span>" 
					  	);
			  	  		$("#items-"+setId).append(
			  	  			'<span class="match">' + item._source["ao:context"][0]["ao:hasSelector"]["ao:exact"] + "</span>" 
					  	);
			  	  		$("#items-"+setId).append(
			  	  			'<span class="suffix">' + item._source["ao:context"][0]["ao:hasSelector"]["ao:suffix"] + "</span>" 
					  	);
		  	  		} else if(item._source["ao:context"][0]["domeo:displaySource"]) {
		  	  			$("#items-"+setId).append(
			  	  			'<img alt="Loading image...."  src="' + item._source["ao:context"][0]["domeo:displaySource"] + '"/>' 
					  	);
			  	  	}
			  	} else if(item._source["@type"]=='ao:AntibodyAnnotation') {
		  	  		$("#items-"+setId).append(
				  	  		"AntibodyAnnotation (score: " + item._score + ") <br/>" 
		  	  			);
	  	  			
			  	  		$("#items-"+setId).append(
			  	  			'<div class="match" style="display: inline;"><a target="_blank" href="' + item._source["ao:body"][0]["domeo:antibody"]["@id"] + '">' + item._source["ao:body"][0]["domeo:antibody"][0]["rdfs:label"] + "</a>" +
			  	  			"</div> <div style='display: inline;'>" +
			  	  			(item._source["ao:body"][0]["domeo:protocol"]?"with method " + item._source["ao:body"][0]["domeo:protocol"][0]["rdfs:label"]:"")  + 
			  	  			(item._source["ao:body"][0]["domeo:model"]? " on " + item._source["ao:body"][0]["domeo:model"]["rdfs:label"]:"") + 
			  	  			"</div><br/>" 
			  	  		);
			  	  	
			  	  		if(item._source["ao:context"][0]["ao:hasSelector"]["ao:prefix"] || item._source["ao:context"][0]["ao:hasSelector"]["ao:exact"] 
	  	  						|| item._source["ao:context"][0]["ao:hasSelector"]["ao:suffix"] ) {
				  	  		$("#items-"+setId).append(
				  	  			'<span class="prefix">' + item._source["ao:context"][0]["ao:hasSelector"]["ao:prefix"] + "</span>" 
						  	);
				  	  		$("#items-"+setId).append(
				  	  			'<span class="match">' + item._source["ao:context"][0]["ao:hasSelector"]["ao:exact"] + "</span>" 
						  	);
				  	  		$("#items-"+setId).append(
				  	  			'<span class="suffix">' + item._source["ao:context"][0]["ao:hasSelector"]["ao:suffix"] + "</span>" 
						  	);
			  	  		} else if(item._source["ao:context"][0]["domeo:displaySource"]) {
			  	  			$("#items-"+setId).append(
				  	  			'<img alt="Loading image...."  src="' + item._source["ao:context"][0]["domeo:displaySource"] + '"/>' 
						  	);
				  	  	}
			  	} else if(item._source["@type"]=='ao:MicroPublicationAnnotation') {
			  		$("#items-"+setId).append(
			  	  		"MicroPublicationAnnotation (score: " + item._score + ") <br/>" 
	  	  			);

			  		var type = 'Claim';
	  	  			if(JSON.stringify(item._source["ao:body"][0]["mp:argues"]["@type"]).indexOf('Hypo')>0) type = "Hypothesis";

			  		$("#items-"+setId).append(
			  				type + ': <div class="match" style="display: inline;"><a target="_blank" href="' + item._source["ao:body"][0]["mp:argues"]["@id"] + '">' + item._source["ao:body"][0]["mp:argues"]["mp:hasContent"] + "</a>" +/*
		  	  			"</div> <div style='display: inline;'>" +
		  	  			(item._source["ao:body"][0]["domeo:protocol"]?"with method " + item._source["ao:body"][0]["domeo:protocol"][0]["rdfs:label"]:"")  + 
		  	  			(item._source["ao:body"][0]["domeo:model"]? " on " + item._source["ao:body"][0]["domeo:model"]["rdfs:label"]:"") + */
		  	  			"</div>" 
		  	  		);

			  		var support = item._source["ao:body"][0]["mp:argues"]["mp:supportedBy"];
		  	  		if(support) {
		  	  			$("#items-"+setId).append('<br/>supportedBy:<br/>');
			  	  		for(var j=0; j<support.length; j++) {
			  	  			var supportingText = '';
			  	  			if(support[j]['reif:resource']['@type']=='mp:DataImage') {
			  	  				supportingText += '<td style="padding:5px; vertical-align: top;"><img src=\"${resource(dir:'images/secure',file:'database-green.gif')}\"/></td>';
			  	  				supportingText += '<td><img src=\"' + support[j]['reif:resource']['ao:context']['domeo:displaySource'] + '\"/></td>';
				  	  		} else if(support[j]['reif:resource']['@type'].indexOf('ArticleReference')>0) {
				  	  			supportingText += '<td style="padding:5px; vertical-align: top;"><img src=\"${resource(dir:'images/secure',file:'document-green.gif')}\"/></td>';
	  	  						supportingText += '<td>'+support[j]['reif:resource']['authorNames'] + ". <span style='font-weight: bold;'>" + support[j]['reif:resource']['title'] + "</span>. " +
			  	  					support[j]['reif:resource']['publicationInfo'] + '</td>';
				  	  		} else if(support[j]['reif:resource']['@type']=='mp:Statement') {
				  	  			var from = ' same source'
					  	  		if(support[j]['reif:resource']['ao:context']['ao:hasSource']!=item._source["ao:body"][0]['mp:argues']['ao:context']['ao:hasSource']) from =  support[j]['reif:resource']['ao:context']['ao:hasSource'];
				  	  			supportingText += '<td style="padding:5px; vertical-align: top;"><img src=\"${resource(dir:'images/secure',file:'double-arrow-green.gif')}\"/></td>';
				  	  			supportingText += '<td>Statement: <span style="font-weight: bold;">' + support[j]['reif:resource']['mp:hasContent'] + '</span> from ' + from + '</td>';
				  	  		}
			  	  			$("#items-"+setId).append('<table><tr>' + supportingText + '</tr></table>');
			  	  		}
			  	  	}

			  	  	var challenge = item._source["ao:body"][0]["mp:argues"]["mp:challengedBy"];
		  	  		if(challenge) {
		  	  			$("#items-"+setId).append('<br/>challengedBy:<br/>');
			  	  		for(var j=0; j<challenge.length; j++) {
			  	  			var supportingText = '';
			  	  			if(challenge[j]['reif:resource']['@type']=='mp:DataImage') {
			  	  				supportingText += '<td style="padding:5px; vertical-align: top;"><img src=\"${resource(dir:'images/secure',file:'database-red.gif')}\"/></td>';
	  	  						supportingText += '<td><img src=\"' + challenge[j]['reif:resource']['ao:context']['domeo:displaySource'] + '\"/></td>';
				  	  		} else if(challenge[j]['reif:resource']['@type'].indexOf('ArticleReference')>0) {
				  	  			supportingText += '<td style="padding:5px; vertical-align: top;"><img src=\"${resource(dir:'images/secure',file:'document-red.gif')}\"/></td>';
	  	  						supportingText += '<td>'+challenge[j]['reif:resource']['authorNames'] + ". <span style='font-weight: bold;'>" + challenge[j]['reif:resource']['title'] + "</span>. " +
				  	  				challenge[j]['reif:resource']['publicationInfo'] + '</td>';
				  	  		} else if(support[j]['reif:resource']['@type']=='mp:Statement') {
								var from = ' same source'
					  	  		if(challenge[j]['reif:resource']['ao:context']['ao:hasSource']!=item._source["ao:body"][0]['mp:argues']['ao:context']['ao:hasSource']) from =  challenge[j]['reif:resource']['ao:context']['ao:hasSource'];
				  	  			supportingText += '<td style="padding:5px; vertical-align: top;"><img src=\"${resource(dir:'images/secure',file:'double-arrow-red.gif')}\"/></td>';
				  	  			supportingText += '<td>Statement: <span style="font-weight: bold;">'+ challenge[j]['reif:resource']['mp:hasContent'] + '</span> from ' + 
				  	  				from + '</td>'
				  	  		}
			  	  			$("#items-"+setId).append('<table><tr>' + supportingText + '</tr></table>');
			  	  		}
			  	  	}

			  	  	var qualifiers = item._source["ao:body"][0]["mp:argues"]["mp:qualifiedBy"];
		  	  		if(qualifiers) {
		  	  			$("#items-"+setId).append('<br/>qualifiedBy:<br/>');
		  	  			var tagsText = '';
			  	  		for(var j=0; j<qualifiers.length; j++) {
			  	  			tagsText +=
				  	  			'<li><a target="_blank" href="' + qualifiers[j]["reif:resource"]["@id"] + '">' + qualifiers[j]["reif:resource"]["rdfs:label"] + "</a>" +
				  	  			"from " + qualifiers[j]["reif:resource"]["dct:source"]["rdfs:label"] + "</li>";
			  	  		}
			  	  		$("#items-"+setId).append('<div style="overflow: hidden;"><ul class="tags">' + tagsText + '</ul></div>');
			  	  	}
				}else {
		  	  		$("#items-"+setId).append(
			  	  		'Item: ' + item + "<br/>" 
	  	  			);
			  	
			  	}
		  	  	$("#items-"+setId).append("<br/>");
		  	  	$("#items-"+setId).append("<br/>");
		  	});		
  	  	}
	});
}
</script>
</head>
<body>
 	<div class="container">
 		<div id="searchArea" align="left" style="padding: 5px; padding-top: 20px;padding-left: 10px; padding-bottom: 20px;">	    	
    		<g:textField id="queryField" name="query" size="70"/>
    		<input value="Search" title="Search" name="lucky" type="submit" id="btn_i" onclick="searchAnnotation()" class="btn btn-success">
    		<div id="resultsSummary" style="display: inline; padding-left: 20px;">Displaying most recent anntation sets</div>
	    </div>
	</div>
	<div class="container">
		<div id="sidebar" class="viewerSidebar well" style="display:block; ">
	    	<div id='contributorsTitle'>Filtering by permissions</div>
			<div id="contributors" style="border-top: 3px solid #ddd; padding-bottom: 2px;"></div>
	    	<div style="padding: 5px; padding-top: 10px; ">
			    <input id="publicFilter" type="checkbox" name="public"> Public<br>
			    <input id="privateFilter" type="checkbox" name="private"> Private<br/>
			    <%--<input id="groupsFilter" type="checkbox" name="groups"> Groups<br>		--%>
			  
			  	<g:if test="${userGroups.size()>0}">
				  	<div id="groupsList">
				  	 	<br/>Groups<br/>	    
				  		<g:each in="${userGroups}" status="i" var="usergroup">
				  			<input type="checkbox" name="${usergroup.group.name}" class="groupCheckbox" value="${usergroup.group.id}"> ${usergroup.group.name}<br/>
				  		</g:each>
				  	</div>
			  	</g:if>
				<br/>
				<div align="center"><input value="Refresh" title="Search" name="lucky" type="submit" id="btn_i" onclick="searchAnnotation()" class="btn btn-success"></div>
			</div>
	  	</div>
	  	<div id="progressIcon" align="center" style="padding: 5px; padding-left: 10px; display: none;">
	    	<img id="groupsSpinner" src="${resource(dir:'images/secured',file:'ajax-loader-4CAE4C.gif',plugin:'users-module')}" />
	    </div>
	  	<div class="resultsPaginationTop"></div>
		<div id="resultsList" style="width: 790px;">
	    <%-- Most recent anntation sets --%>
	    </div>
	    <div class="resultsPaginationBottom"></div>
	</div>
	
	
</body>
</html>