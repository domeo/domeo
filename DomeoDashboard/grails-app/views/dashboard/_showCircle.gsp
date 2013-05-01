<%-- by Paolo Ciccarese --%>
<%-- 
Parameters list
 1) group | instance of GroupCreateCommand
Stylesheet
 1) fieldError | background and font color in erroneous text fields
--%>
<%@ page import="org.mindinformatics.grails.domeo.dashboard.groups.GroupStatus" %>
<div class="sectioncontainer">
<div class="dialog" >
	<fieldset>
		<table style="width: 460px">
			<tbody>
				<tr>
					<td valign="top" width="80px"  align="left">
						<label for="name">Name</label>
					</td>
					<td valign="top" width="265px" align="left">
						${circle?.name}
					</td>
				</tr>
				<tr>
					<td valign="top"  align="left">
						<label for="nickname">Short Name</label>
					</td>
					<td valign="top" align="left">
						${circle?.shortName}
					</td>
				</tr> 
			</tbody>
		</table>
		</fieldset>
		<br/>
		<%-- 
		<fieldset>
			<legend>Membership</legend>
			<table style="width: 460px">
				<tr>
					<td valign="top" width="80px" align="left">
						<label for="status">Member Since</label>
					</td>
					<td valign="top" width="265px" align="left">
						${usergroup.dateCreated}
					</td>
				</tr>
				<tr>
					<td colspan="2">
						<div class="buttons">
							<g:form>
								<g:hiddenField name="id" value="${usergroup.group?.id}" /> 
								<g:hiddenField name="user" value="${loggedUser.id}" /> 
								<g:hiddenField name="redirect" value="showGroups" />
									<g:if test="${usergroup.status.value == 'IG_LOCKED'}">
										<span class="button">
											<g:actionSubmit class="unlock" action="unlockUserinGroup" value="${message(code: 'default.button.unlock.account.label', default: 'Unlock')}" />
										</span>
									</g:if>
									<g:elseif test="${usergroup.status.value == 'IG_ACTIVE' || usergroup.status.value == 'IG_SUSPENDED' || usergroup.status.value == 'IG_DELETED'}">
										<span class="button">
											<g:actionSubmit class="lock" action="lockUserInGroup" value="${message(code: 'default.button.lock.account.label', default: 'Lock')}"
											onclick="return confirm('${message(code: 'default.button.lock.account.confirm.message', default: 'Are you sure you want to lock the user membership in this group?')}');" />
										</span>
									</g:elseif>
									<g:if test="${usergroup.status.value == 'IG_SUSPENDED' || usergroup.status.value == 'IG_DELETED'}">
										<span class="button">
											<g:actionSubmit class="enable" action="enableUserInGroup" value="${message(code: 'default.button.enable.account.label', default: 'Activate')}" />
										</span>
									</g:if>
									<g:elseif test="${usergroup.status.value == 'IG_ACTIVE' || usergroup.status.value == 'IG_LOCKED'}">
										<span class="button">
											<g:actionSubmit class="disable" action="disableUserInGroup" value="${message(code: 'default.button.disable.account.label', default: 'Suspend')}" 
											onclick="return confirm('${message(code: 'default.button.disable.account.confirm.message', default: 'Are you sure you want to disable the user membership in this group?')}');"/>
										</span>
									</g:elseif>
									<span class="button">
										<g:actionSubmit class="deleteUser" action="removeUserFromGroup" value="${message(code: 'default.button.edit.account.label', default: 'Leave group')}" 
										onclick="return confirm('${message(code: 'default.button.disable.account.confirm.message', default: 'Are you sure you want to remove the user from the group?')}');"/>
									</span>
							</g:form>
						</div>
					</td>
				</tr>
			</tbody>
		</table>
		</fieldset>
		--%>
</div>
</div>
