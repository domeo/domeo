<!doctype html>
<!--[if lt IE 7 ]> <html lang="en" class="no-js ie6"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="no-js ie7"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="no-js ie8"> <![endif]-->
<!--[if IE 9 ]>    <html lang="en" class="no-js ie9"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> <html lang="en" class="no-js"><!--<![endif]-->
<head>
<meta name="layout" content="domeo-secure" />
<title>Domeo user: ${user?.displayName}</title>
<style>
	table.userData
	{
		list-style-type:none;
		margin:0;
		padding:10px;
		overflow:hidden;
	}
	table.userData td
	{
		vertical-align: top;
		padding: 5px;
	}
</style>
</head>
<body>
  <div class="content">
  
    <div class="content_resize">
    <h3 style="background: #cc3300; color: #fff; padding: 3px ;padding-left:5px;">Secured Area</h3>
    <div style="padding-left: 10px">
    <br/>
    
	<table class="userData">
		<tr>
			<td><img src="${resource(dir:'images/dashboard',file:'no_image.gif',plugin:'users-module')}" width="200px" /><br/>
			<h3 align="center">${user?.displayName}</h3></td>
			<td valign="top">
				<table class="userData">
					<tbody>
						<%--
						<tr class="sprop">
							<td valign="top" colspan="2"  align="center">
								<img src="${resource(dir:'images/dashboard',file:'no_image.gif',plugin:'users-module')}" width="200px" />
							</td>
						</tr>
						 --%>
						<g:if test="${user?.title!=null}">
							<tr class="sprop">
								<td valign="top" width="180px"  align="left">
									<label for="title">Title</label>
								</td>
								<td valign="top" width="265px" align="left">
									${user?.title}
								</td>
							</tr>
						</g:if>
						<tr class="sprop">
							<td valign="top" align="left">
								<label for="firstName">First name</label>
							</td>
							<td valign="top" width="265px" align="left">
								${user?.firstName}
							</td>
						</tr>
						<g:if test="${user?.middleName!=null}">
							<tr class="sprop">
								<td valign="top"  align="left">
									<label for="middleName">Middle name</label>
								</td>
								<td valign="top" width="265px" align="left">
									${user?.middleName}
								</td>
							</tr>
						</g:if>
						<tr class="sprop">
							<td valign="top"  align="left">
								<label for="lastName">Last name</label>
							</td>
							<td valign="top" align="left">
								${user?.lastName}
							</td>
						</tr>
						<tr class="sprop">
							<td valign="top"  align="left">
								<label for="displayName">Display name</label>
							</td>
							<td valign="top" align="left">
								${user?.displayName}
							</td>
						</tr>
						<tr class="sprop">
							<td valign="top"  align="left">
								<label for="displayName">Username</label>
							</td>
							<td valign="top" align="left">
								${user?.username}
							</td>
						</tr>
						<tr class="sprop">
							<td valign="top"  align="left">
								<label for="displayName">Email</label>
							</td>
							<td valign="top" align="left">
								${user?.email}
							</td>
						</tr>
						<tr class="sprop">
							<td valign="top"  align="left">
								<label for="displayName">Affiliation</label>
							</td>
							<td valign="top" align="left">
								${user?.affiliation}
							</td>
						</tr>
						<tr class="sprop">
							<td valign="top"  align="left">
								<label for="displayName">Country</label>
							</td>
							<td valign="top" align="left">
								${user?.country}
							</td>
						</tr>
					</tbody>
				</table>
			</td>
		</tr>
	</table>
	
	<br/>
	
      </div>
      <div class="clr"></div>
    </div>
  </div>
  

</body>
</html>
