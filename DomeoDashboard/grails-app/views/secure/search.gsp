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
			  	  	} else if(item._source["@type"]=='ao:PostIt') {
			  	  		$("#items-"+setId).append(
				  	  		"Post it (score: " + item._score + ") <br/>" 
		  	  			);
		  	  			alert(item._source["ao:context"][0]["ao:hasSelector"]["ao:prefix"]);
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
