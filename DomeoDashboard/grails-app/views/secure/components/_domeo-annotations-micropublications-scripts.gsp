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

function processMicroPublication(annotation) {
	for(var j=0; j<annotation.body[0]['mp:argues']['mp:qualifiedBy'].length;j++) {
		//var tag = annotation.body[0]['mp:argues']['mp:qualifiedBy'][j]['reif:resource'];
		//tags[tag['@id']]=tag;
		addTag(annotation.body[0]['mp:argues']['mp:qualifiedBy'][j]['reif:resource']);
	}
	if(annotation.body[0]['mp:argues']['mp:supportedBy']) {
		for(var j=0; j<annotation.body[0]['mp:argues']['mp:supportedBy'].length;j++) {
			if(annotation.body[0]['mp:argues']['mp:supportedBy'][j]['reif:resource']) {
				var ref = annotation.body[0]['mp:argues']['mp:supportedBy'][j]['reif:resource'];
				if(ref['@type'].contains('PublicationArticleReference')) {
					references[ref['@id']]=ref;
				}
			}
		}
	}
	if(annotation.body[0]['mp:argues']['mp:challengedBy']) {
		for(var j=0; j<annotation.body[0]['mp:argues']['mp:challengedBy'].length;j++) {
			alert('challengedBy');
		}
	}	
}

</script>