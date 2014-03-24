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

function processAntibodies(annotation) {

	if(annotation.body[0]["domeo:protocol"] && annotation.body[0]["domeo:protocol"].length>0) {
		for(var i=0; i<annotation.body[0]["domeo:protocol"].length; i++) {
			addTag(annotation.body[0]["domeo:protocol"][i]);
		}
	}

	if(annotation.body[0]["domeo:model"]) 
		addTag(annotation.body[0]["domeo:model"]);
}

function getAnnotationAntibodiesView(index, annotation, indentation, annotationOnAnnotation) {

	var methods = '';
	if(annotation.body[0]["domeo:protocol"] && annotation.body[0]["domeo:protocol"].length>0) {
		methods = (annotation.body[0]["domeo:protocol"].length==1 ? 'Method: ': 'Methods: ');
		for(var i=0; i<annotation.body[0]["domeo:protocol"].length; i++) {
			methods+= '<a target="_blank" href="' + annotation.body[0]["domeo:protocol"][i]["@id"] + '">' + annotation.body[0]["domeo:protocol"][i]["rdfs:label"] + '</a>';
			if(i>-1 && i<annotation.body[0]["domeo:protocol"].length-1)  methods+= ', ';
		}
	}

	var subject = 'Subject not specified';
	if(annotation.body[0]["domeo:model"]) subject ='Subject: <a target="_blank" href="' + annotation.body[0]["domeo:model"]["@id"] + '">' + 
		annotation.body[0]["domeo:model"]["rdfs:label"] + '</a>';

	var antibody = '';
	if(annotation.body[0]["domeo:antibody"][0]) 
		antibody += 'Antibody: <a target="_blank" href="' + annotation.body[0]["domeo:antibody"][0]["@id"] + '">' + annotation.body[0]["domeo:antibody"][0]["rdfs:label"] + '</a>';

	var comment = '';
	if(annotation.body[0]["dct:description"])
		comment = 'Note: ' + annotation.body[0]["dct:description"];
		
	return '<div style="padding-left: ' + indentation + 'px; padding-right: ' + indentation + 'px;padding-bottom: 10px;">' + 
		'<div style="border: 1px solid #ddd;">' +
			'<table width="100%" class="barContainer">' +
				'<tr>' +
					'<td width="500px">' +
						injectAnnotationTopBar(annotation) +
					'</td>' +
					'<td>' +
						'<div id="annotationCounters_' + index + '"></div>' +
					'</td>' +
				'</tr>' +
			'</table>' +		
			'<div class="annbody">' +
   				'<div class="annbody-content">' +
   					antibody + "<br/>" +
					methods + "<br/>" +
					subject + "<br/>" +
					comment + "<br/>" +
   				'</div>' +
   				(!annotationOnAnnotation? getAnnotationContext(annotation):'')+
   				'<div id="annotationCounters_aoa' + index + '"></div>' +
   				'</div>' +
   		'</div>' +
		'</div>';
}

</script>