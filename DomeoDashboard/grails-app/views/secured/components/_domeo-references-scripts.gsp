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

var references = {};

function buildReferenceList() {
	if(Object.keys(references).length>0) {
		$('#referencesTitle').append("<div style='padding-top: 4px; border-bottom:3px solid #ddd; padding-bottom: 5px'><span style='font-size:18px; padding-right: 5px;'>" +Object.keys(references).length + "</span>" + (Object.keys(references).length!=1?"Linked publications":"Linked publication") + "</div>");
		for(var i=0; i<Object.keys(references).length; i++) {
			$('#references').append('<div style="padding-bottom: 6px; border-bottom:1px solid #ddd;">' + '<div style="font-weight: bold;background: #eee; padding: 5px;">' + references[Object.keys(references)[i]]['title'] + '</div> ' + references[Object.keys(references)[i]]['authorNames'] 
				 + '. <span style="font-style:italic">' + references[Object.keys(references)[i]]['publicationInfo'] + '</span></div>');
		}	
	} else {
		$('#referencesTitle').hide();
		$('#references').hide();
	}
}

</script>