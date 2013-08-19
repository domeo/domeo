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
<script type="text/JavaScript">

	function getName(item) {
		if(item.annotationSetIndex.label && item.annotationSetIndex.label>0 && item.annotationSetIndex.label!='Default Set') 
			return item.annotationSetIndex.label;
		else return 'Annotation Set'
	}

	function getDescription(item) {
		if(item.annotationSetIndex.description && item.annotationSetIndex.description.length>0 && item.annotationSetIndex.description!='The default set is created automatically by Domeo when no other set is existing.')
			return '. ' + item.annotationSetIndex.description;
		else return "";
	}

	function getProvenance(item) {
		return 'By  <a onclick=\"javascript:displayUser(\'' + item.annotationSetIndex.createdBy.id + '\')\" style=\"cursor: pointer;\">'  + item.annotationSetIndex.createdBy.displayName + '</a> (et al) on ' + item.annotationSetIndex.createdOn + ' with v. ' + item.annotationSetIndex.versionNumber;
	}

	function getStats(item) {
		return '# annotation items: '+item.annotationSetIndex.size + displayAccessType(item.permissionType) + displayLock(item.isLocked);
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

	function isLocked(lock) {
		return lock=='true';
	}

	function getModifyLink(i, item) {
		return "<a onclick=\"javascript:edit('" + item.annotationSetIndex.individualUri + "')\" style=\"text-decoration: none; cursor: pointer;\"><img id=\"groupsSpinner\" src=\"${resource(dir:'images/secure',file:'black_edit.gif',plugin:'users-module')}\" /> Document</a><br/>";
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
		document.location = '${appBaseUrl}/secure/set/' + annotationUri;
	}

	function displayUser(userId) {
		document.location = '${appBaseUrl}/secure/user/' + userId;
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
		  	  	$("#items-"+setId).html('');
		  	  	$.each(data, function(i,item){
		  	  		$("#items-"+setId).append("<br/>");
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
					  	  			supportingText += '<td>Statement: <span style="font-weight: bold;">' + support[j]['reif:resource']['mp:hasContent'] + '</span> from ' + 
					  	  				from + '</td>'
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

	function search() {
		//$("#basic_info_loader_message").text("Saving...");
		$("#progressIcon").show();
		var savingRequest = $.ajax({
			type: "POST",
			contentType : "text/plain",
	        dataType: 'json', 
			url: "${request.getContextPath()}/ajaxPersistence/search",
			data: JSON.stringify(getSearchFormValues())
		}).done(function( data ) {
			$("#progressIcon").hide();
			$("#resultsList").html("");

			if(data.annotationListItemWrappers.length==0) {
				$('#resultsList').append("No results<br/>");
			} 
			
			var users = new Array();
			$.each(data.annotationListItemWrappers, function(i,item){
				var color = i%2==0?"#fff":"#efefef"
  				$('#resultsList').append('<div style="border: 1px solid #eee; padding: 5px; background: '+color+'"><table width="100%"><tr><td>' +
  					'<span style="font-weight: bold;">'+getName(item) + '</span>' + getDescription(item) +
  					'<br/>' +
  					getProvenance(item) +
  					'<br/>' +
  					getStats(item) + 
  					'<br/>' +
  					getTarget(item)  +
  					//'<div id="citation-'+item.annotationSetIndex.id+'"><img id=\"groupsSpinner\" src=\"${resource(dir:'images',file:'spinner.gif',plugin:'users-module')}\" /> Retrieving Citation</div>' +
  					'</div>' +
  					'</td>' +
  					'<td width="90px">' +
  					getModifyLink(i, item) +
  					getExploreLink(item) +
  					getShareLink(i, item) +
  					getHistoryLink(item) + 
		  			'</td>' + 
  					'</tr></table>' +
  					
  					//getTarget(item)  +
  					'<div id="citation-'+item.annotationSetIndex.id+'"><img id=\"groupsSpinner\" src=\"${resource(dir:'images',file:'spinner.gif',plugin:'users-module')}\" /> Retrieving Citation</div>' +
  					'<div id="items-'+item.annotationSetIndex.id+'" style="padding-left:12px; margin-left: 7px;border-left: 2px #999 solid;"><img id=\"groupsSpinner\" src=\"${resource(dir:'images',file:'spinner.gif',plugin:'users-module')}\" /> Retrieving Items</div>' 
  					//'</div>' 	
  					
  					//'<br/>'
					
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

	$(document).ready(function() {
		hideBasicInfoComponents();
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
		//$("#progressIconMessage").hide();
	}
</script>
</head>
<body>
	
  <div class="content">
	<div class="content_resize">
		<div id="domeoSearch">
		  	<g:form>
			    <div class="sidebar" style="padding-top: 30px;padding-bottom: 30px; padding-right:2px;">
			    	<div align="center" style="background: #cc3300; padding: 5px; color: #fff; font-weight: bold;">Facets</div>
			    	<div style="background: #fff; padding: 5px; padding-top: 10px; border: 2px solid #cc3300;">
					    <div align="left" style="padding-left:4px; background: #FFCC00"><b>By Access</b><br/></div>
					    <g:checkBox name="permissionsPublic"  checked="${true}"/> Public<br/>
					    <input type="checkbox" name="permissionsGroups" value="Groups" checked>Groups<br>
					    
					  	<div id="groupsList">
					  		<g:each in="${userGroups}" status="i" var="usergroup">
					  			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="checkbox" name="${usergroup.group.name}" value="" checked>${usergroup.group.name}<br/>
					  		</g:each>
					  	</div>
					    
						<input type="checkbox" name="permissionsPrivate" checked>Private<br/>
						<br/>
						
						<div align="left" style="padding-left:4px; background: #FFCC00"><b>By Agent (not implemented)</b><br/></div>
						<g:checkBox name="agentHuman"  checked="${true}"/> Human<br/>
						<g:checkBox name="agentSoftware"  checked="${true}"/> Software<br/>
						
						<br/>
						<div align="left" style="padding-left:4px; background: #FFCC00"><b>Incuding (not implemented)</b><br/></div>
						<g:checkBox name="annQualifier"  checked="${true}"/> Qualifiers<br/>
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<g:checkBox name="ontoPRO"  checked="${true}"/> Protein Ontology<br/>
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<g:checkBox name="ontoGO"  checked="${true}"/> Gene Ontology<br/>
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<g:checkBox name="ontoNIF"  checked="${true}"/> NIFSTD Ontology<br/>
						<g:checkBox name="annHighlights"  checked="${true}"/> Highlights<br/>
						<g:checkBox name="annNotes"  checked="${true}"/> Notes<br/>
					</div>
					<br/>
					<%-- 
					<div align="center" style="background: #cc3300; padding: 5px; color: #fff; font-weight: bold;">Annotators</div>
			    	
			    	<div style="background: #fff; padding: 5px; padding-top: 10px; border: 2px solid #cc3300;">
			    		
			    	</div>
			    	--%>
			  	</div>
		 
		 		<!-- Browsing Navigation -->
			    <div style="background: #cc3300; color: #fff;">
			   		<ul class="bar">
						<li><g:link controller="secure" action="browser"><span>Annotation Sets</span></g:link></li>
						<li><g:link controller="secure" action="documents"><span>Documents</span></g:link></li>
						<li><a href="#">Bibliography</a></li>
					</ul> 
			    </div>
			    
			    
			    <table width="730px;">
			    	<tr><td>
			    		<div id="resultsSummary" style="padding: 5px; padding-left: 10px;"></div>
			    	</td><td style="text-align:right">
			    		<div id="resultsStats" style="padding: 5px; "></div>
			    	</td></tr>
			    </table>
			    <div id="searchArea" align="center" style="padding: 5px; padding-top: 15px ;padding-left: 10px; width: 715px;">
			    	
			    		<g:textField id="queryField" name="query" size="70" />
			    		<g:submitButton name="search" value="Search" />   
			    	
			    </div>
		</g:form>
		  <div id="progressIcon" align="center" style="padding: 5px; padding-left: 10px; display: none;"><img id="groupsSpinner" src="${resource(dir:'images',file:'progress-bar-2.gif',plugin:'users-module')}" /></div>
	    <div id="resultsList" style="padding: 5px; padding-left: 10px; width: 715px;">
	    	
	    </div>
	    <div class="resultsPagination"></div>
      	<div class="clr"></div>
    </div>
     </div>
 
</body>
</html>
