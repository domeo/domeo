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

var tags = {};
var tagsCounters = {};

$(document).ready(function() {
	$.fn.tagcloud.defaults = {
	  size: {start: 9, end: 12, unit: 'pt'},
	  color: {start: '#999', end: '#333'}
	};
});

function displayLinkedResource(url, label, description, source) {
	alert('Linked resource: ' + url);
}

function buildTagCloud() {
	if(Object.keys(tags).length>0) {
		$('#tagCloudTitle').append("<div style='padding-top: 4px; padding-bottom: 5px'>" +
			"<span style='font-size:18px; padding-right: 5px;'>" + Object.keys(tags).length + "</span>" + 
			(Object.keys(tags).length!=1?"Tags":"Tag") + 
		"</div>");
		for(var i=0; i<Object.keys(tags).length; i++) {
			$('#tagCloudItems').append("<a onclick=\"javascript:displayLinkedResource('" + tags[Object.keys(tags)[i]]['@id'] + "', '" + tags[Object.keys(tags)[i]]['rdfs:label'] + "','" + tags[Object.keys(tags)[i]]['dct:description'] + "','" + tags[Object.keys(tags)[i]]['dct:source']['@id'] + "')\" style=\"cursor: pointer;\">" + tags[Object.keys(tags)[i]]['rdfs:label'] + '</a> ');
		}	
		 $('#tagCloudItems a').tagcloud();
	} else {
		$('#tagCloudTitle').hide();
		$('#tagCloud').hide();
	}
}

function addTag(tag) {
	tags[tag['@id']]=tag;
	if(tagsCounters[tag['@id']]) {
		tagsCounters[tag['@id']]=tagsCounters[tag['@id']]+1
	} else tagsCounters[tag['@id']]=1;
}

</script>