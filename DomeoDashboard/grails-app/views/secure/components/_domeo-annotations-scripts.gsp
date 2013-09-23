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
<style>
.topBar {
	background: #eee;
	border: 5px #eee solid;
}

.titleBar {
	font-weight: bold;
}

.provenanceBar {
}

.contextTitle {
	text-align: left;
	padding-left: 5px;
	border-top: 4px #fff solid;
}

blockquote.contextQuote {
  font: 14px/20px;
  padding-left: 40px;
  padding-right: 10px;
  min-height: 40px;
  margin: 5px;
  background-image: url(../../images/secure/quotes.gif);
  background-position: middle left;
  background-repeat: no-repeat;
  text-indent: 5px;
} 
		
.contextPrefix {
	color: #aaa;
	font-style: italic;
}

.contextMatch {
	font-weight: bold;
}

.contextSuffix {
	color: #aaa;
	font-style: italic;
}
</style>
<script type="text/javascript">

function  processAnnotation(annotation) {
	agents[annotation.createdBy['@id']] = annotation.createdBy;
	var annotationType = annotation.type;
	if(annotationType=='ao:Qualifier') {
		processQualifier(annotation);
	} else if(annotationType=='ao:MicroPublicationAnnotation') {
		processMicroPublication(annotation);
	} else if(annotationType=='ao:AntibodyAnnotation') {
		processAntibodies(annotation);
	}
}

function injectAnnotationTopBar(annotation) {
	return '<div class="topBar">' +
		'<div class="titleBar"><span>' + annotation.label + '</span> ' +
			' by ' + injectAgentLabel(annotation.createdBy) + 
			'<br/>' +
		'</div>' +
		'<div class="provenanceBar">' +
			'Last saved on <span>' + annotation.lastSavedOn + //'</span> with version <span>' + annotation.version +
		'</div>' +
	'</div>';
}

function getAnnotationContext(annotation) {
	if(annotation.match) 
	return '<div class="contextTitle">Annotating: </div>' +
		'<blockquote class="contextQuote">' +
			'...' +
       		'<span class="contextPrefix">' + annotation.prefix + '</span>' +
       		'<span class="contextMatch">' + annotation.match + '</span>' +
       		'<span class="contextSuffix">' + annotation.suffix + '</span>' +
       		'...' +
       	'</blockquote>';
    else return '<div class="contextTitle">Annotating: </div>' + 
    	'<blockquote class="quote">' +
       		'<img src="' + annotation.display+ '" style="max-width:500px">' +
       	'</blockquote>';
}

function getAnnotationView(index, annotation, indentation, annotationOnAnnotation) {
	if(annotation.type=="ao:AntibodyAnnotation") 
		return getAnnotationAntibodiesView(index, annotation, indentation, annotationOnAnnotation);
	else
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
   				'<div class="annbody-content">' + annotation.content + '</div>' +
   				(!annotationOnAnnotation? getAnnotationContext(annotation):'')+
   				'<div id="annotationCounters_aoa' + index + '"></div>' +
   				'</div>' +
   		'</div>' +
		'</div>';
}
</script>