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
	padding-left: 5px;
	font-style:italic;
}
</style>
<script type="text/javascript">

function processQualifier(annotation) {
	for(var j=0; j<annotation.body.length;j++) {
		addTag(annotation.body[j]);
	}
}

</script>