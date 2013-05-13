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

<!--  
Uncomment for local testing
<script src="../../js/exhibit 2.2.0/api/exhibit-api.js?bundle=false&autoCreate=false" type="text/javascript"></script> 
-->
 
  <script src="http://api.simile-widgets.org/exhibit/2.2.0/exhibit-api.js?bundle=false&autoCreate=false"
            type="text/javascript"></script>

            <script>SimileAjax.History.enabled = false;</script>
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
	
	.barContainer {
		border: 1px #eee solid;
		background: #FFCC00;
	}
	
	.topBar {
		background: #FFCC00;
		border: 5px #FFCC00 solid;
	}
	
	.titleBar {
		font-weight: bold;
	}
	
	.provenanceBar {
	}
	
	.miscBar {
		
		border: 4px #FFCC00 solid;
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
	
	.contextTitle {
		text-align: center;
		border-top: 4px #fff solid;
	}
	
	.context {
		padding: 10px;
		border: 5px #fff solid;
	}
	
	.context-content {
		border: 2px #fff solid;
		border-left: 4px #fff solid;
		border-right: 4px #fff solid;
	}
	
	.annbody {
		border-left: 1px #eee solid;
		border-right: 1px #eee solid;
		border-bottom: 1px #eee solid;
		font-size: 120%;
	}
	
	.annbody-content {
		border: 4px #fff solid;
	}
	
	.match-highlight {
		font-weight: bold;
		background: yellow;
	}

</style>
<script type="text/javascript">

	function edit(annotationId) {
		document.location = '${appBaseUrl}/web/domeo?annotationId=' + annotationId;
	}

	function display(userId) {
		document.location = '${appBaseUrl}/secure/user/' + userId;
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


	// ----------
	
	function getModifyLink(item) {
		return "<a onclick=\"javascript:edit('" + item.set.id + "')\" style=\"text-decoration: none; cursor: pointer;\"><img id=\"groupsSpinner\" src=\"${resource(dir:'images/secure',file:'black_edit.gif',plugin:'users-module')}\" /> Document</a>";
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

	function getTitle(item) {
		return '<span style="font-weight: bold;">' + item.label + '</span>';
	}

	function getDescription(item) {
		return '<span style="font-weight: normal;">' + item.description + '</span>';
	}

	function getVersion(item) {
		return "Version " + item.version;
	}
	
	function getNumberAnnotations(item) {
		return 'Containing ' + item.domeo_annotations.length + ' annotations';
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
			  		//alert(data.get(0));
			  		$("#resultsSummary").html("Displaying Set");
			  		set = data[0];
		  			//$("#resultsSummary").html("Saved by " + "<a onclick=\"javascript:display('" + set.pav_createdBy + "')\" style=\"cursor: pointer;\">" + 
		  			//		set.pav_createdBy + "</a> on " + set.pav_createdOn + "<br/>" +getVersion(set) + displayAccessType(set.permissions['permissions:accessType']) + displayLock(set.permissions['permissions:isLocked']));
		  			//$("#resultsStats").append('<table width="160px;"><tr><td align="left">'+getModifyLink(set) + '</td><td align="left"> '  + getHistoryLink(set) + '</td></tr><tr><td align="left">' + getShareLink(set) + '</td><td></td></tr></table>');
		  			//$('#resultsIntro').append(
					//	getTitle(set) + ' ' + getDescription(set)+ '<br/>' + getTarget(set) +
					//	'<div id="citation-'+set.id+'"><img id=\"groupsSpinner\" src=\"${resource(dir:'images',file:'spinner.gif',plugin:'users-module')}\" /> Retrieving Citation</div>' 
					//	
				  	//);
				  	
				  	$("#resultsSummary").html("Saved by " + "<a onclick=\"javascript:display('" + data.set.createdById + "')\" style=\"cursor: pointer;\">" + 
				  			data.set.createdBy['foafx:name'] + "</a> on " + data.set.createdOn + "<br/>" + getVersion(data.set)
				  	     + displayAccessType(data.set.permissions['permissions:accessType']) + displayLock(data.set.permissions['permissions:isLocked']));
				  	$("#resultsStats").append('<table width="160px;"><tr><td align="left">'+getModifyLink(data) + '</td><td align="left"> ' + getHistoryLink(data) + '</td></tr><tr><td align="left">' + getShareLink(data) + '</td><td></td></tr></table>');
				  	$('#resultsIntro').append(
				  		getTitle(data.set) + ' ' + getDescription(data.set) + '<br/>' + getTarget(data.set) +
				  		'<div id="citation-' + data.set.id.substring(data.set.id.lastIndexOf(':')+1) + '"><img id=\"groupsSpinner\" src=\"${resource(dir:'images',file:'spinner.gif',plugin:'users-module')}\" /> Retrieving Citation</div>' 
					);
		  			retrieveCitation(data.set);

		  			/*
		  			var annotations='';
		  			$.each(set.domeo_annotations, function(i,item){
			  			
			  			if(item['@type'] == 'oax:Highlight') {
			  				annotations += item['@type'] + ': ';
			  				annotations += item['ao:context'][0]['ao:hasSelector'].oax_prefix + ' <span="background-color: yellow;">' + item['ao:context'][0]['ao:hasSelector'].oax_exact + '</span> ' + item['ao:context'][0].oa_hasSelector.oax_suffix + '</br/></br/>';
				  		} else if(item['@type'] == 'ao:PostIt') {
			  				annotations += item['@type'] + ': ';
			  				annotations += item['ao:context'][0]['ao:hasSelector'].oax_exact + '</br/></br/>';
				  		} else if(item['@type'] == 'oa:Annotation') {
			  				annotations += item['@type'] + ': ';
			  				annotations += item['ao:context'][0]['ao:hasSelector'].oax_exact + '</br/></br/>';
				  		} else {
				  			alert(item['@type']);
					  	}
		  			});
		  			
		  			$('#resultsList').append(annotations);
		  			*/


		  			var fDone = function() {
		  	            window.exhibit = Exhibit.create();
		  	            window.exhibit.configureFromDOM();
		  	        };
		  	        
		  	        try {
		  	            var s = Exhibit.getAttribute(document.body, "ondataload");
		  	            if (s != null && typeof s == "string" && s.length > 0) {
		  	                fDone = function() {
		  	                    var f = eval(s);
		  	                    if (typeof f == "function") {
		  	                        f.call();
		  	                    }
		  	                }
		  	            }
		  	        } catch (e) {
		  	            // silent
		  	        }
		  	        
		  	        window.database = Exhibit.Database.create();

		  	       //var data = eval("(" + '{"items": [{"id": "1", "url": "http://localhost/1", "type" :"annotation", "label" : "My person", "prop" : "Mine"}, {"id": "2","type" :"annotation", "label" : "My person2"}, {"id": "3","type" :"annotation", "label" : "My person3"}]}' + ")");
		  	       window.database.loadData(data.items);
		  	     fDone();
		  			  			

			  	}

	  			
			  	
	  	  		}

  		
		  	});

		

	});
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
	    
	    <!--  
	    <div class="sidebar" style="padding-top: 30px;padding-bottom: 30px; padding-right:2px;">
	    	<div align="center" style="background: #cc3300; padding: 5px; color: #fff; font-weight: bold;">Filter (not implemented yet)</div>
	    	<div style="background: #fff; padding: 5px; padding-top: 10px; border: 2px solid #cc3300;">
	    		<div align="left" style="padding-left:4px; background: #FFCC00"><b>By Text</b><br/></div>
			    <input type="text" name="search" style="width: 220px;"><br><br>
			    <div align="left" style="padding-left:4px; background: #FFCC00"><b>By Access</b><br/></div>
			     <input type="checkbox" name="vehicle" value="Bike" checked>All <input type="checkbox" name="vehicle" value="Bike" checked>Mine<br>
			    <br>
			    <div align="left" style="padding-left:4px; background: #FFCC00"><b>By Access</b><br/></div>
			   
			    <input type="checkbox" name="vehicle" value="Bike">Public<br>
				<input type="checkbox" name="vehicle" value="Car">Private<br/>
			  	<div align="center">My Groups</div>
			  	<div id="groupsList">
			  		<g:each in="${userGroups}" status="i" var="usergroup">
			  			<input type="checkbox" name="${usergroup.group.name}" value="Car">${usergroup.group.name}<br/>
			  		</g:each>
			  	</div>
			  		
				<br/>
				<div align="center"><input value="Refresh" title="Search" name="lucky" type="submit" id="btn_i"></div>
			</div>
	  	</div>
	  	-->
	    
	    <div id="resultsList" style="padding: 5px; padding-left: 10px; width: 715px;">

	    </div>
	    <div style="padding-left:8px; padding-right: 8px; min-height: 20px;">
			<div ex:role="facet"  ex:showMissing="false" ex:facetClass="Cloud" ex:expression=".cloud"></div>
		</div>
	    <table width="100%">
	    <tr>
	    <td ex:role="viewPanel" style="padding-left:8px;padding-right: 8px;"><div ex:role="view" ex:grouped="false" ex:possibleOrders=".image" /></div></td>
	    <td width="280px;" style="padding-right:8px; padding-top: 10px;">
		    <div align="center" style="background: #cc3300; padding: 5px; color: #fff; font-weight: bold;">Filter</div>
		    <div style="background: #fff; padding: 5px; padding-top: 10px; border: 2px solid #cc3300;">
		    	<div style="padding-right:5px; padding-bottom: 10px;">
		    		<div ex:role="facet" ex:facetClass="TextSearch" ex:facetLabel="By Text"></div>
		    	</div>
		    	<div ex:role="facet" ex:expression=".type" ex:facetLabel="By Annotation Types"></div>
		    	<div ex:role="facet" ex:expression=".createdByName" ex:facetLabel="By Authors"></div>
		    	<div ex:role="facet" ex:expression=".withComments" ex:facetLabel="With comments" ex:showMissing="false" ex:height="30px"></div>
		    </div>
	    </td>
	    </tr>
	    </table>

<!--  
		<div ex:role="lens" ex:itemTypes="oax:Highlight" style="display: none">
			Hello
        </div>
-->

	    <div class="resultsPagination"></div>
      	<div class="clr"></div>
      	<br/><br/>
    </div>
    </div>
    
	<g:render template="/secure/exhibit-lenses/ann-postit" />
	<g:render template="/secure/exhibit-lenses/ann-comment" />
	<g:render template="/secure/exhibit-lenses/ann-highlight" />
	<g:render template="/secure/exhibit-lenses/ann-qualifier" />
	<g:render template="/secure/exhibit-lenses/ann-antibody" />
	
	
</body>
</html>
