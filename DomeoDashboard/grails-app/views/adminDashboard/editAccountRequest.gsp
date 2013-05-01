<!DOCTYPE html>
<%-- by Paolo Ciccarese --%>

<html>
  <head>
    <meta name="layout" content="dashboard-admin" />
  </head>

	<body>
		<g:if test="${fieldValue(bean: user, field: 'displayName') == ''}">
          	<div class="title">Edit Account Request</div>
		</g:if>
		<g:else>
			<div class="title">
				Edit account request: ${item?.displayName}
			</div>
		</g:else>
		<table>
			<tr>
				<td valign="top"><g:render template="/adminDashboard/editAccountRequest" /></td>
			</tr>
		</table>
	</body>
</html>