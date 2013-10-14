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

function getAnnotationComments(annotation) {
	if(annotation.annotatedBy && annotation.annotatedBy.length>0) {
		var annotations = '<div class="contextTitle">'+ getAnnotationCurationsCounter(annotation) + getAnnotationCommentsCounter(annotation) + '</div>';
		for(var j=0; j<annotation.annotatedBy.length; j++) {
			agents[annotation.annotatedBy[j].createdBy['@id']] = annotation.annotatedBy[j].createdBy;
			annotations += getAnnotationView('c'+j, annotation.annotatedBy[j], 20, true);
		}
		return annotations;
	} else return "";
}

function getAnnotationCommentsCounter(annotation) {
	if (annotation.commentsCounter)
		return '<span>' +
			'<img src="${resource(dir:'images/secure',file:'commentIcon_16.png',plugin:'users-module')}"/> <span>' + annotation.commentsCounter + '</span> ' + (annotation.commentsCounter==1?'Comment':'Comments') +
		'</span>';
	else return '';
} 

function getAnnotationCurationsCounter(annotation) {
	if (annotation.curationsCounter)
		return 	'<span>' +
			'<img src="${resource(dir:'images/secure',file:'cursor_arrow.png',plugin:'users-module')}"/> <span>' + annotation.curationsCounter + '</span> ' + (annotation.curationsCounter==1?'Curation':'Curations') +
		'</span>';
	else return '';
}

</script>