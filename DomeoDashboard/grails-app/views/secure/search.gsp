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
.btn {
    display: inline-block;
    padding: 10px;
    border-radius: 5px; /*optional*/
    color: #aaa;
    font-size: .875em;
}

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
	
	#btn_i {
	    width:125px;
	    margin-left:auto;
	    margin-right:auto;
	}
	
	.prefix {
		color: #aaa;
		font-style: italic;
	}
	
	.match {
		font-weight: bold;
		font-size: 1.1em;
	}
	
	.suffix {
		color: #aaa;
		font-style: italic;
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
	font-style:italic;
}

</style>
<style>
.topBar {
	background: #eee;
	padding: 5px;;
	/*border-bottom: 1px solid grey;*/
}
</style>
<script type="text/JavaScript">

	function getName(item) {
		if(item.annotationSetIndex.label && item.annotationSetIndex.label>0 && item.annotationSetIndex.label!='Default Set') 
			return item.annotationSetIndex.label;
		else return 'Annotation Set'
	}

	function getDescription(item) {
		if(item.annotationSetIndex.description && item.annotationSetIndex.description.length>0 && item.annotationSetIndex.description!='The default set is created automatically by Domeo when no other set is existing.')
			return item.annotationSetIndex.description;
		else return "";
	}

	function getProvenance(item) {
		return 'By  <a onclick=\"javascript:displayUser(\'' + item.annotationSetIndex.createdBy.id + '\')\" style=\"cursor: pointer;\">'  + item.annotationSetIndex.createdBy.displayName + '</a> (et al) on ' + item.annotationSetIndex.createdOn + ' with v. ' + item.annotationSetIndex.versionNumber;
	}

	/*
	function getStats(item) {
		return '# annotation items: '+item.annotationSetIndex.size + displayAccessType(item.permissionType) + displayLock(item.isLocked);
	}
	*/

	function getStats(item) {
		return "<span style='font-size:18px; padding-right: 5px;'>" + item.annotationSetIndex.size + "</span>" + (item.annotationSetIndex.size!=1?'items':'item') + displayLock(item.isLocked);
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

	function edit(annotationId, url) {
		document.location = '${appBaseUrl}/web/domeo?url=' + encodeURIComponent(url) + '&setId=' + encodeURIComponent(annotationId);
	}

	function isLocked(lock) {
		return lock=='true';
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

	function getShareByUrlLink(url) {
		return "<a onclick=\"javascript:displayByUrlShare('" + url + "')\" style=\"text-decoration: none; cursor: pointer;\"><img id=\"groupsSpinner\" src=\"${resource(dir:'images/secure',file:'block-share.png',plugin:'users-module')}\" /> Export listed sets</a><br/>";
	}

	function displaySet(annotationUri) {
		document.location = '${appBaseUrl}/secure/annset/' + annotationUri;
	}

	function displayUser(userId) {
		document.location = '${appBaseUrl}/secure/user/' + userId;
	}

	function getProvenanceCreator(item) {
		return 'by <a onclick=\"javascript:displayUser(\'' + item.annotationSetIndex.createdBy.id + '\')\" style=\"cursor: pointer;\">' + item.annotationSetIndex.createdBy.displayName + '</a>';
	}
	
	function getProvenanceDate(item) {
		return 'Last saved on ' + item.createdOn + ' with v. ' + item.versionNumber;
	}
	
	function getTarget(item) {
		var u = item.annotationSetIndex.annotatesUrl;
		var temp = String(u);
		if(temp.length>60) {
			u = temp.substring(0, 30) + '...' + temp.substring(temp.length-25);
		}
		return "On  <a href='#' onclick='javascript:loadData(\""+item.annotationSetIndex.annotatesUrl+"\")'>"+ u + " <img id=\"groupsSpinner\" src=\"${resource(dir:'images/secure',file:'show.gif',plugin:'users-module')}\" /></a> ";
	}

	function getConnector(i, length) {
		if(i<length-1) return "<div align=\"center\" style=\"padding: 10px;\"><img id=\"groupsSpinner\" src=\"${resource(dir:'images/secure',file:'downarrow16x16.png',plugin:'users-module')}\" />Has Previous version</div>";
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

	function getTargetOut(item) {
		var u = item;
		var temp = String(u);
		if(temp.length>60) {
			u = temp.substring(0, 30) + '...' + temp.substring(temp.length-25);
		}
		return "Annotation sets for:<br/>  <a target='_blank' href='"+item+"'>"+ u + " <img id=\"groupsSpinner\" src=\"${resource(dir:'images/secure',file:'external.png',plugin:'users-module')}\" /></a> ";
	}

	function getHistoryLink(item) {
		if(item.versionNumber>1) 
		return "<a onclick=\"javascript:displayHistory('" + item.annotationSetIndex.individualUri + "')\" style=\"text-decoration: none; cursor: pointer;\"><img id=\"groupsSpinner\" src=\"${resource(dir:'images/secure',file:'history.png',plugin:'users-module')}\" /> History</a><br/>";
		else return "";
	}

	function getSearchFormValues() {
		var $inputs = $('#domeoSearch :input');
		var values = {};
	    $inputs.each(function() {
		    if($(this).is(':checked')) 
		    	values[this.name] = 'checked';
		    else if($(this).val()!="") 
	        	values[this.name] = $(this).val();
	    });
	    return values;
	}


	$(document).ready(function() {
		hideBasicInfoComponents();
		
		$('#queryField').bind("enterKey",function(e){
		   searchAnnotation();
		});
		$('#queryField').keyup(function(e){
		    if(e.keyCode == 13)
		    {
		        searchAnnotation();
		    }
		});
		
		try {
			
		
			$("#domeoSearch form").submit(function(e) {
				search();
				
				return e.preventDefault();
			});
		} catch(e) {
			alert(e);
		}
	});

	function hideBasicInfoComponents() {
		$("#progressIcon").hide();
	}
	
	var paginationOffset;
	var paginationRange;
	
	function searchAnnotation(paginationOffset, paginationRange) {
		var query = $('#queryField').val();
		if(!query) {
			alert('empty');
			return
		}
	
		var groups = '';
		$(".groupCheckbox").each(function(i) {
			if($(this).attr('checked')!=undefined) 
				groups += $(this).attr('value') + " ";
		});
	
		var dataToSend = { 
			userId: '${loggedUser.id}', 
			query: query,
			paginationOffset: paginationOffset, 
			paginationRange: paginationRange, 
			permissionsPublic: $("#publicFilter").attr('checked')!==undefined, 
			permissionsGroups: $("#groupsFilter").attr('checked')!==undefined, 
			groupsIds: groups,
			permissionsPrivate: $("#privateFilter").attr('checked')!==undefined,
			agentHuman: $("#agentHuman").attr('checked')!==undefined, 
			agentSoftware: $("#agentSoftware").attr('checked')!==undefined, 
		};
		
		$("#progressIcon").show();
		var savingRequest = $.ajax({
			type: "POST",
			contentType : "text/plain",
	        dataType: 'json', 
			url: "${request.getContextPath()}/ajaxPersistence/search",
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
	  		//paginationHtml += '<a href="#" class="page">last</a>';
	  		
	  		
	  		$('.resultsPaginationTop').append(paginationHtml);	  		
	  		$('.resultsPaginationBottom').append(paginationHtml);
			
			var users = new Array();
			$.each(data.annotationListItemWrappers, function(i,item){
				var color = i%2==0?"#fff":"#fff"; //"#FFF8DC"
  				$('#resultsList').append('<div style="border: 1px solid #eee; padding: 5px; background: '+color+'">' +
  				
  				
  				  	'<table width="100%" style="border-bottom: solid #ddd 1px;"><tr><td class="topBar">' +
  					'<span style="font-weight: bold;">'+getName(item) + '</span> ' + getProvenanceCreator(item)  + '<br/>' +
  					getProvenanceDate(item)  +
  					
  					'</div>' +
  					'</td>' +
  					'<td width="90" class="topBar" align="right">' +
  					getStats(item) + 
  					'<br/>' +
  					displayAccessType(item.permissionType) + 
  					
  					'</td>' +
  					'<td width="90px" rowspan="2" style="padding-left:4px; border-left: 0px solid #eee;">' +
  					
  					getModifyLink(i, item) +
  					getExploreLink(item) +
  					getShareLink(i, item) +
  					getHistoryLink(item) + 
		  			'</td>' +
		  			'</tr><tr><td>' + getDescription(item) + '<br/>' +
		  			//(item.lastAnnotationSetIndex.lastVersion.description!='The default set is created automatically by Domeo when no other set is existing.' && item.lastAnnotationSetIndex.lastVersion.description!=''? (item.lastAnnotationSetIndex.lastVersion.description + '<br/>') :'') +
  					
  					
  					getTarget(item)  +
  					//'<div id="citation-'+item.lastAnnotationSetIndex.lastVersion.id+'"><img id=\"groupsSpinner\" src=\"${resource(dir:'images',file:'spinner.gif',plugin:'users-module')}\" /> Retrieving Citation</div>' +
		  			'</td></tr><tr><td>' + 
		  			'<div id="citation-'+item.annotationSetIndex.id+'"><img id=\"groupsSpinner\" src=\"${resource(dir:'images',file:'spinner.gif',plugin:'users-module')}\" /> Retrieving Citation</div>' +
		  			
  					'</td></tr><tr><td style="height:5px;"> </td></tr></table><br/>' +
  					'<div id="items-summary-'+item.annotationSetIndex.id+'" style="padding-left:0px; margin-left: 7px;"><img id=\"groupsSpinner\" src=\"${resource(dir:'images',file:'spinner.gif',plugin:'users-module')}\" /> Retrieving Items Summary</div>' +
  					'<div id="items-'+item.annotationSetIndex.id+'" style="padding-left:12px; margin-left: 7px;border-left: 2px #999 solid;"><img id=\"groupsSpinner\" src=\"${resource(dir:'images',file:'spinner.gif',plugin:'users-module')}\" /> Retrieving Items</div>' 
  					);
  				retrieveCitation(item);
  				retrieveItems(item.annotationSetIndex.id, item.annotationSetIndex.individualUri, $('#queryField').val());
  				$('#resultsList').append("<br/>");
  				//$('#resultsList').append(getConnector(i, data.annotationListItemWrappers.length));
  				
				users[i] = item.createdBy;
  				//alert(item.lastAnnotationSetIndex.lastVersion.createdBy.displayName);
  				//$('#resultsList').append('<input type="checkbox" name="vehicle" value="Bike">' 
  		  		//		+ item.group.name + '<br/>'); 
  		  				 
		  				//item.dateCreated + '</td><td>'+ roles +
		  				//'</td><td> '+ item.status.label + '</td></tr>');
  				 
  		    });	
			
			
		}).fail(function() { 
			alert("search error"); 
		}).always(function() { 
			// Do nothing
		});	
	}
</script>
<style>
.viewerSidebar {
	float: right;
	width: 242px;
	margin-right: 8px;
}

</style>
</head>
<body>
	
  <div class="content">
	<div class="content_resize">
		<div id="sidebar" class="viewerSidebar" style="padding-top: 30px;padding-bottom: 30px; padding-right:2px;">
			<%--
	    	<div id='contributorsTitle'>Filtering by Access</div>
			<div id="contributors" style="border-top: 3px solid #ddd; padding-bottom: 2px;"></div>
	    	<div style="background: #fff; padding: 5px; padding-top: 10px; ">
			    <input id="publicFilter" type="checkbox" name="vehicle" checked="checked">Public<br>
			    <input id="groupsFilter" type="checkbox" name="vehicle" >Groups<br>			    
			  	<div id="groupsList">
			  		<g:each in="${userGroups}" status="i" var="usergroup">
			  			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="checkbox" name="${usergroup.group.name}" class="groupCheckbox" value="${usergroup.group.id}">${usergroup.group.name}<br/>
			  		</g:each>
			  	</div>
				<input id="privateFilter" type="checkbox" name="vehicle" checked="checked">Private<br/><br/>
			</div>
			
			<div id='contributorsTitle'>Filtering by Agent</div>
			<div id="contributors" style="border-top: 3px solid #ddd; padding-bottom: 2px;"></div>
			<div style="background: #fff; padding: 5px; padding-top: 10px; ">
				<g:checkBox id="agentHuman" name="agentHuman"  checked="${true}"/> Human<br/>
				<g:checkBox id="agentSoftware" name="agentSoftware"  checked="${true}"/> Software<br/>
			</div>
			<br/>
			<div align="center"><input value="Refresh" title="Search" name="lucky" type="submit" id="btn_i" onclick="searchAnnotation()"></div>
			--%>
	  	</div>
		 
 		<!-- Browsing Navigation -->
	    <div style="background: #cc3300; color: #fff;">
	    	&nbsp;
	    	<!--  
	   		<ul class="bar">
				<li><g:link controller="secure" action="browser"><span>Annotation Sets</span></g:link></li>
				<li><g:link controller="secure" action="documents"><span>Documents</span></g:link></li>
				<li><a href="#">Bibliography</a></li>
			</ul>
			--> 
	    </div>
			    
	    <table width="705px;">
	    	<tr><td>
	    		<div id="resultsSummary" style="padding: 5px; padding-left: 10px;"></div>
	    	</td><td style="text-align:right">
	    		<div id="resultsStats" style="padding: 5px; "></div>
	    	</td></tr>
	    </table>
	    <div id="searchArea" align="center" style="padding: 5px; padding-top: 15px ;padding-left: 10px; width: 715px;">
	    	
	    		<g:textField id="queryField" name="query" size="70" />
	    		<input value="Search" title="Search" name="lucky" type="submit" id="btn_i" onclick="searchAnnotation()">
	    	
	    </div>
		
		<div id="progressIcon" align="center" style="padding: 5px; padding-left: 10px; display: none;"><img id="groupsSpinner" src="${resource(dir:'images',file:'progress-bar-2.gif',plugin:'users-module')}" /></div>
	 
	 	<div id="resultsSummary" style="padding: 5px; padding-left: 10px;"></div>
	    <div class="resultsPaginationTop"></div>
	    <div id="resultsList" style="padding: 5px; padding-left: 10px; width: 715px;"></div>
	   	<div class="resultsPaginationBottom"></div>
      	<div class="clr"></div>
    </div>
     </div>
 
</body>
</html>
