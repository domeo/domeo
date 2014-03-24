<%--
/*
 * Copyright 2013 Massachusetts General Hospital
 * 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
 
/*
 * @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
 */
--%>
<script type="text/javascript">

var CLASS_PERSON = 'foafx:Person';
var CLASS_SOFTWARE = 'foafx:Software';

var USER_PREFIX = '/secure/user/';
var SOFTWARE_PREFIX = '/secure/software/';

var agents = {};

function buildAgentsList() {
	$('#contributorsTitle').append("<div style='padding-top: 4px; padding-bottom: 5px'>" +
		"<span style='font-size:18px; padding-right: 5px;'>" + Object.keys(agents).length + "</span>" + 
		(Object.keys(agents).length!=1?"Contributors":"Contributor") + 
	"</div>");
	
	var agentsBuffer = '';	
	for(var i=0; i<Object.keys(agents).length; i++) {
		agentsBuffer += injectAgentTemplate(agents[Object.keys(agents)[i]]);
	}	
	$('#contributors').append(agentsBuffer);
}

function injectAgentLabel(agent) {
	if(agent['@type']==CLASS_PERSON) {
		return injectUserLabel(agent);
	} else if(agent['@type']==CLASS_SOFTWARE) {
		return injectSoftwareLabel(agent);
	}
}

function injectAgentTemplate(agent) {
	if(agent['@type']==CLASS_PERSON) {
		return injectUserTemplate(agent);
	} else if(agent['@type']==CLASS_SOFTWARE) {
		return injectSoftwareTemplate(agent);
	}
}

/**
 * ============================================================================
 *  Users utilities
 * ============================================================================
 */
function displayUser(userId, userName, userTitle, userHomepage) {
	$("#overlayTitle").empty();
	$("#overlayContent").empty();
	$("#overlayLinks").empty();


	if(($(window).width()-900)<400)  {
		$("#viewer").width(400);
		$("#overlayTable").width(380);
	} else { 
		$("#viewer").width(($(window).width()-900));
		$("#overlayTable").width(($(window).width()-920));
	}
	
	if(userId=='urn:person:uuid:${loggedUser.id}') $("#overlayTitle").append("Me (" + userName + ")");
	else $("#overlayTitle").append("User " + userName);
	
	$("#overlayContent").append("<img src='${resource(dir:'images/secure',file:'person.png')}' style='max-width:40px;'><br/>");
	$("#overlayContent").append("<a href=\"javascript:browseUser('" + userId + "')\">" + userName + "</a><br/>");
	if(userHomepage) $("#overlayContent").append("Homepage " + userHomepage + "<br/>");
	
	$("#overlayLinks").append("Browse user's annotations<br/>");
	
	$("#viewer").overlay().load();
}

function browseUser(userId) {
	document.location = appBaseUrl + USER_PREFIX + userId;
}

function injectUserLabel(user) {
	return "<a onclick=\"javascript:displayUser('" + user['@id'] + "', '" + user['foafx:name'] + "', '" + user['foafx:title'] + 
		"', '" + user['foafx:homepage'] +  "')\" style=\"cursor: pointer;\">" + 
		user['foafx:name'] + 
	"</a>";
}

function injectUserTemplate(user) {
	return "<div style='border-bottom:1px solid #ddd; padding: 2px;'>" + 
		"<table><tr>" + 
			"<td style='width: 50px;'>" +
				"<img src='${resource(dir:'images/secure',file:'person.png')}' style='max-width:40px;'>" + 
			"</td>" + 
			"<td style='vertical-align: middle;'>" + 
				"<a onclick=\"javascript:displayUser('" + user['@id'] + "', '" + user['foafx:name'] + "', '" + user['foafx:title'] + 
						"', '" + user['foafx:homepage'] + "')\" style=\"cursor: pointer;\">"  + 
					user['foafx:name'] + "</a>" +
			"</td>" +
		"</tr></table>" + 
	"</div>";	
}

/**
 * ============================================================================
 *  Software utilities
 * ============================================================================
 */
function displaySoftware(softwareId) {
	//document.location = appBaseUrl + SOFTWARE_PREFIX + softwareId;
	alert('Software agent: ' + softwareId);
}

function injectSoftwareLabel(software) {
	return "<a onclick=\"javascript:displaySoftware('" + software['@id'] + "')\" style=\"cursor: pointer;\">" + 
		software['foafx:name'] + 
	"</a>";
}

function injectSoftwareTemplate(software) {
	return "<div style='border-bottom:1px solid #ddd; padding: 2px;'>" + 
		"<table><tr>" + 
			"<td style='width: 50px;'>" +
				"<img src='${resource(dir:'images/secure',file:'mycomputer.png')}' style='max-width:40px;'>" + 
			"</td>" + 
			"<td style='vertical-align: middle;'>" + 
				"<a onclick=\"javascript:displaySoftware('" + software['@id'] + "')\" style=\"cursor: pointer;\">"  + software['foafx:name'] + "</a>" +
			"</td>" +
		"</tr></table>" + 
	"</div>";	
}

</script>