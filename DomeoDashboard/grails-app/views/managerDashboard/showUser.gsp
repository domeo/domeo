<!DOCTYPE html>
<%-- by Paolo Ciccarese --%>

<html>
  	<head>
	    <meta name="layout" content="dashboard-manager" />
	    <title>:: User - ${item?.firstName} ${item?.lastName} <g:if test="${item?.displayName?.length()>0}">(${item.displayName})</g:if></title>
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

			  	
			  	var dataToSend = { id: '${item.id}' };
			  	/*
			  	$.ajax({
			  	  	url: "/UsersManagement/usersAjax/userCommunities",
			  	  	context: $("#communitiesContent"),
			  	  	data: dataToSend,
			  	  	success: function(data){
			  			$("#communitiesSpinner").css("display","none");
			  			var label = data.length == 1 ? data.length + ' Community' : data.length + ' Communities';
			  			$("#communitiesTitle").text(label);
			  			$.each(data, function(i,item){
			  				$('#communitiesTable').append('<tr><td><a href="http://tochange.org/' + item.community.id + '">' + item.community.name 
					  				+ '</a></td><td>' + item.role.label + '</td><td>'+ item.dateCreated + '</td></tr>');
			  		    });
				  	}
			  	});
			  	*/

				
			  	
			  	$.ajax({
			  	  	url: "${appBaseUrl}/ajaxDashboard/userGroups",
			  	  	context: $("#groupsContent"),
			  	  	data: dataToSend,
			  	  	success: function(data){
			  			$("#groupsSpinner").css("display","none");
			  			var label = data.length == 1 ? data.length + ' Group' : data.length + ' Groups';
			  			$("#groupsTitle").html("<b>"+label+"</b>");
			  			$.each(data, function(i,item){
				  			var roles ="";
							for(var i=0; i<item.roles.length; i++) {
								roles+=item.roles[i].label
							}
			  				$('#groupsTable').append('<tr><td><a href="../showGroup/' + 
					  				item.group.id + '">' + item.group.name + '</a></td><td>' + 
					  				item.dateCreated + '</td><td>'+ roles +
					  				'</td><td> '+ item.status.label + '</td></tr>');
			  		    });
			  					  			
				  	}
			  	});
			  	/*
				$.ajax({
			  	  	url: "/UsersManagement/usersAjax/userCircles",
			  	  	context: $("#circlesContent"),
			  	  	data: dataToSend,
			  	  	success: function(data){
						$("#circlesSpinner").css("display","none");
						var label = data.length == 1 ? data.length + ' Circle' : data.length + ' Circles';
						$("#circlesTitle").text(label);
						$.each(data, function(i,item){
			  				$('#circlesTable').append('<tr><td><a href="http://tochange.org/' + item.circle.id + '">' + item.circle.name 
					  				+ '</a></td><td>' + item.role.label + '</td><td>' + item.dateCreated + '</td></tr>');
			  		    });
				  	}
			  	});
			  	*/
		  	});

		  
		  			  	
	  	</script>
	  	    	<g:if test="${fieldValue(bean: item, field: 'displayName') == ''}">
          	<div class="title">User Information </div>
		</g:if>
		<g:else>
			<div class="title">${item?.displayName}</div>
		</g:else>
		<table>
			<tr>
				<td valign="top" width="400px"><g:render template="/managerDashboard/showUser" /></td>
				<td valign="top">
					<%-- g:render template="/users/ajaxShowUserCommunities" / --%>
    				<g:render template="/managerDashboard/ajaxShowUserGroups" />
    				<%-- g:render template="/users/ajaxShowUserCircles" / --%>
				</td>
			</tr>
		</table>
	</body>
</html>