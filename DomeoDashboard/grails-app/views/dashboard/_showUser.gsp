<%-- by Paolo Ciccarese --%>
<%-- 
Parameters list
 1) user | instance of GroupCreateCommand
Stylesheet
 1) fieldError | background and font color in erroneous text fields
--%>
<div class="sectioncontainer">
<div class="dialog" style="width: 330px">
    <fieldset>
		<table style="width: 460px">
			<tbody>
				<%-- 
				<tr class="sprop">
					<td valign="top" colspan="2"  align="center">
						<img src="${resource(dir:'images/dashboard',file:'no_image.gif',plugin:'users-module')}" width="200px" />
					</td>
				</tr>
				--%>
				<tr class="sprop">
					<td valign="top" width="120px"  align="left">
						<label for="firstName">First name</label>
					</td>
					<td valign="top" width="265px" align="left">
						${user?.firstName}
					</td>
				</tr>
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
				<tr class="sprop">
					<td valign="top" width="140px" align="left">
						<label for="userRole">Role</label>
					</td>
					<td valign="top" colspan="2" align="left">
						<div>
							<g:each in="${userRoles}">
								${it.label}
							</g:each>
						</div>
					</td>
				</tr>
				<tr class="sprop">
					<td valign="top" width="140px" align="left">
						<label for="userRole">Account Status</label>
					</td>
					<td valign="top" colspan="2" align="left">
						<div>
							${user?.status}
						</div>
					</td>
				</tr>
				<%-- 
				<tr class="sprop">
					<td colspan="2">
						<div class="buttons">
							<g:form>
								<g:hiddenField name="id" value="${user?.id}" /> 
								<g:hiddenField name="redirect" value="listUsers" />
								<span class="button">
									<g:actionSubmit class="edit" action="editUser" value="${message(code: 'default.button.edit.account.label', default: 'Edit user information')}" />
								</span>
							</g:form>
						</div>
					</td>
				</tr>
				--%>
			</tbody>
		</table>
</fieldset>
</div>
</div>
