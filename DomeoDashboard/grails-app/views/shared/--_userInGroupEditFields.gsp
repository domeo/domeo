<%-- by Paolo Ciccarese --%>
<%-- 
Parameters list
 1) item | instance of GroupCreateCommand
Stylesheet
 1) fieldError | background and font color in erroneous text fields
--%>
<%@ page import="org.mindinformatics.grails.domeo.dashboard.groups.DefaultGroupStatus" %>
<%@ page import="org.mindinformatics.grails.domeo.dashboard.groups.DefaultGroupPrivacy" %>
<tr>
	<td valign="top">
		<table>
			<tbody>
				<tr class="prop">
					<td valign="top" width="70px"  class="name">
						<label for="name">User</label>
					</td>
					<td valign="top" width="255px" class="value">
						${usergroup.user.displayName}
					</td>
				</tr>
				<tr class="prop">
					<td valign="top" width="70px"  class="name">
						<label for="name">Group</label>
					</td>
					<td valign="top" width="255px" class="value">
						${usergroup.group.name}
					</td>
				</tr>
				<tr class="prop">
					<td valign="top" class="name">
						<label for="shortName">Member Since</label>
					</td>
					<td valign="top" class="value">
						${usergroup.dateCreated}
					</td>
				</tr>
				<tr class="prop">
					<td valign="top" class="name">
						<label for="description">Last Membership Update</label>
					</td>
					<td valign="top" class="value">
						${usergroup.lastUpdated}
					</td>
				</tr>
				<tr class="prop">
					<td valign="top" class="name">
						<label for="description">Role</label>
					</td>
					<td valign="top" colspan="2" class="value">
						<div>
							<g:each in="${userRoles}">
								<g:set var="roleFlag" value="false" />
								<g:each in="${usergroup.roles}" var="userRole">
									<g:if test="${it.label==userRole.label}">
										<g:set var="roleFlag" value="true" />
									</g:if>
								</g:each>
								<g:if test="${roleFlag=='true'}">
									<g:checkBox name="${it.label}" value="${true}" /> ${it.label}
								</g:if>
								<g:else>
									<g:checkBox name="${it.label}" /> ${it.label}
								</g:else>
							</g:each>
						</div>
			        </td>
				</tr>
			</tbody>
		</table>
	</td>
</tr>
