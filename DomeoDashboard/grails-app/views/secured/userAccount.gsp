<!DOCTYPE html>
<%-- by Paolo Ciccarese --%>

<html>
  	<head>
	    <meta name="layout" content="domeo-layout-secured" />
	    <title>:: User - ${user?.firstName} ${user?.lastName} <g:if test="${user?.displayName?.length()>0}">(${user.displayName})</g:if></title>
	  	<style type="text/css">

		.tablescroll
		{ font: 12px normal Tahoma, Geneva, "Helvetica Neue", Helvetica, Arial, sans-serif; background-color:#fff; }
		 
		.tablescroll td, 
		.tablescroll_wrapper,
		.tablescroll_head,
		.tablescroll_foot
		{ border:1px solid #ccc; }
		 
		.tablescroll td
		{ padding:3px 5px; }
		 
		.tablescroll_wrapper
		{ border-left:0; }
		 
		.tablescroll_head
		{ font-size:11px; font-weight:bold; background-color:#eee; border-left:0; border-top:0; margin-bottom:3px; }
		 
		.tablescroll thead td
		{ border-right:0; border-bottom:0; }
		 
		.tablescroll tbody td
		{ border-right:0; border-bottom:0; }
		 
		.tablescroll tbody tr.first td
		{ border-top:0; }
		 
		.tablescroll_foot
		{ font-weight:bold; background-color:#eee; border-left:0; border-top:0; margin-top:3px; }
		 
		.tablescroll tfoot td
		{ border-right:0; border-bottom:0; }
	  	
	  	table {
		    border-collapse: collapse;
		    font-size: 13px;
		    width: 100%;
		}
		table.tablelist {
		}
		tr {
		    border: 0 none;
		}
		tr > td:first-child, tr > th:first-child {
		    padding-left: 1.25em;
		}
		tr > td:last-child, tr > th:last-child {
		    padding-right: 1.25em;
		}
		td, th {
		    line-height: 1.5em;
		    padding: 0.4em 0.5em;
		    text-align: left;
		    vertical-align: top;
		}
		th {
		    background-color: #EFEFEF;
		    background-image: -moz-linear-gradient(center top , #FFFFFF, #EAEAEA);
		    color: #666666;
		    font-weight: bold;
		    line-height: 1.7em;
		    padding: 0.2em 0.6em;
		}
		thead th {
		    white-space: nowrap;
		}
		th a {
		    display: block;
		    text-decoration: none;
		}
		th a:link, th a:visited {
		    color: #666666;
		}
		th a:hover, th a:focus {
		    color: #333333;
		}
		th.sortable a {
		    background-position: right center;
		    background-repeat: no-repeat;
		    padding-right: 1.1em;
		}
		th.asc a {
		    background-image: url("../../images/skin/sorted_asc.gif");
		}
		th.desc a {
		    background-image: url("../../images/skin/sorted_desc.gif");
		}
		.odd {
		    background: none repeat scroll 0 0 #F7F7F7;
		}
		.even {
		    background: none repeat scroll 0 0 #FFFFFF;
		}
	  	
	  	</style>
  	</head>

	<body>
		 <g:javascript library="jquery"/>
	   
	  	<script type="text/javascript" src="${resource(dir:'js',file:'jquery.tablescroll.js',plugin:'users-module')}"></script>
	  	<script type="text/javascript" src="${resource(dir:'js',file:'jquery.dateFormat-1.0.js',plugin:'users-module')}"></script>
	  	<script type="text/javascript">

		  	$(document).ready(function() {

		  		/*
			  	$.fn.tableScroll.defaults =
			  	{
			  		flush: true, // makes the last thead and tbody column flush with the scrollbar
			  		width: null, // width of the table (head, body and foot), null defaults to the tables natural width
			  		height: 100, // height of the scrollable area
			  		containerClass: 'tablescroll' // the plugin wraps the table in a div with this css class
			  	};
			  	*/
		  	
		  		
		  		$('#groupsTable').tableScroll({height:200});
		  		 
		  		// other examples
		  	 
		  		// sets the table to have a scrollable area of 200px
		  		//$('#groupsTable').tableScroll({height:20}); 
		  	 
		  		// sets a hard width limit for the table, setting this too small 
		  		// may not always work
		  		$('#groupsTable').tableScroll({width:400}); 
		  	 
		  		// by default the plugin will wrap everything in a div with this 
		  		// css class, if it finds that you have manually wrapped the 
		  		// table with a custom element using this same css class it 
		  		// will forgo creating a container DIV element
		  		$('#groupsTable').tableScroll({containerClass:'tablescroll'});

			  	
			  	var dataToSend = { id: '${user.id}' };

			  	$.ajax({
			  	  	url: "${appBaseUrl}/ajaxDashboard/userGroups",
			  	  	context: $("#groupsContent"),
			  	  	data: dataToSend,
			  	  	success: function(data){
			  			$("#groupsSpinner").css("display","none");
			  			var label = data.length == 1 ? data.length + ' Group' : data.length + ' Groups';
			  			$("#groupsTitle").html("<b>"+label+"</b>");
			  			$.each(data, function(i,user){
				  			var roles ="";
							for(var i=0; i<user.roles.length; i++) {
								roles+=user.roles[i].label
							}
							/*
			  				$('#groupsTable').append('<tr><td><a href="../showGroup/' + 
					  				user.group.id + '">' + user.group.name + '</a></td><td>' + 
					  				user.dateCreated + '</td><td>'+ roles +
					  				'</td><td> '+ user.status.label + '</td></tr>');
					  				*/

			  				$('#groupsTable').append('<tr><td>' + user.group.name + '</td><td>' + 
					  				user.dateCreated + '</td><td>'+ roles +
					  				'</td><td> '+ user.status.label + '</td></tr>');
			  		    });
			  					  			
				  	}
			  	});
			  	/*
			  	$.ajax({
			  	  	url: "/UsersManagement/usersAjax/userCommunities",
			  	  	context: $("#communitiesContent"),
			  	  	data: dataToSend,
			  	  	success: function(data){
			  			$("#communitiesSpinner").css("display","none");
			  			var label = data.length == 1 ? data.length + ' Community' : data.length + ' Communities';
			  			$("#communitiesTitle").text(label);
			  			$.each(data, function(i,user){
			  				$('#communitiesTable').append('<tr><td><a href="http://tochange.org/' + user.community.id + '">' + user.community.name 
					  				+ '</a></td><td>' + user.role.label + '</td><td>'+ user.dateCreated + '</td></tr>');
			  		    });
				  	}
			  	});
			  	*/ 	
		  	});

		  
		  			  	
	  	</script>
	  	
	  	<div class="container">
		  	<!-- Title bar -->
		  	<g:if test="${fieldValue(bean: user, field: 'displayName') == ''}">
	          	<div style="background: #616161; color: #fff; line-height: 10px; padding-top:10px; font-weight: bold; padding-bottom: 10px; margin-bottom: 10px; height: 30px;">
		    		&nbsp;
	          		<g:if test="${user?.id==loggedUser?.id}">My profile</g:if>
					<g:else>User Information</g:else>
	          	 </div>
			</g:if>
			<g:else>
				<div style="background: #616161; color: #fff; line-height: 10px; padding-top:10px; font-weight: bold; padding-bottom: 10px; margin-bottom: 10px; height: 30px;">
		    		&nbsp;
					<g:if test="${user?.id==loggedUser?.id}">My profile: ${loggedUser?.displayName}</g:if>
					<g:else>${user?.displayName}</g:else>
				</div>
			</g:else>
			
			<div style="padding-left: 10px">
				<table>
					<tr>
						<td valign="top" width="400px"><g:render template="/secured/userAccount" /></td>
						<td valign="top">
							<%-- g:render template="/users/ajaxShowUserCommunities" / --%>
		    				<g:render template="/secured/ajaxShowUsersGroups" />
						</td>
					</tr>
				</table>
			</div>
		</div>
	</body>
</html>