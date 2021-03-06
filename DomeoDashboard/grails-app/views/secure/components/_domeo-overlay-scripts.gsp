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
  #viewer {
    /* overlay is hidden before loading */
    display:none;

    /* standard decorations */
    width:500px;
    border:10px solid #666;

    /* for modern browsers use semi-transparent color on the border. nice! */
    border:10px solid rgba(82, 82, 82, 0.698);

    /* hot CSS3 features for mozilla and webkit-based browsers (rounded borders) */
    -moz-border-radius:8px;
    -webkit-border-radius:8px;
  }

  #viewer div {
    padding:10px;
    border:0px solid #3B5998;
    background-color:#fff;
    font-family:"lucida grande",tahoma,verdana,arial,sans-serif
  }

  #viewer h2 {
    margin:-11px;
    margin-bottom:0px;
    color:#fff;
    background-color:#FFCC00; //#6D84B4;
    padding:5px 10px;
    border:1px solid #3B5998;
    font-size:20px;
  }
</style>
<script type="text/javascript">
	$(document).ready(function() {
		 $("#viewer").overlay({
		    // custom top position
		    //top: 260,

		    // some mask tweaks suitable for facebox-looking dialogs
		    mask: {

			    // you might also consider a "transparent" color for the mask
			    color: '#fff',
	
			    // load mask a little faster
			    loadSpeed: 200,
	
			    // very transparent
			    opacity: 0.5
		    },

		    // disable this for modal dialog-type of overlays
		    closeOnClick: false,

		    // load it immediately after the construction
		    load: false
		 });
	});
</script>