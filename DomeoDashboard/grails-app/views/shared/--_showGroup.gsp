<%-- by Paolo Ciccarese --%>
<%-- 
Parameters list
 1) item | instance of GroupCreateCommand
Stylesheet
 1) fieldError | background and font color in erroneous text fields
--%>
<%@ page import="org.mindinformatics.grails.domeo.dashboard.groups.GroupStatus" %>
<div class="sectioncontainer">
<div class="dialog" >
	<fieldset>
    	<legend>Group Profile </legend>
		<table style="width: 460px">
			<tbody>
				<tr>
					<td valign="top" width="80px"  align="left">
						<label for="name">Name</label>
					</td>
					<td valign="top" width="265px" align="left">
						${item?.name}
					</td>
				</tr>
				<tr>
					<td valign="top"  align="left">
						<label for="nickname">Short Name</label>
					</td>
					<td valign="top" align="left">
						${item?.shortName}
					</td>
				</tr> 
				<tr>
					<td valign="top"  align="left">
						<label for="description">Description</label>
					</td>
					<td valign="top" align="left">
						${item?.description}
					</td>
				</tr>
				<tr>
					<td valign="top"  align="left">
						<label for="privacy">Privacy</label>
					</td>
					<td valign="top" align="left">
						${item?.privacy?.label}
					</td>
				</tr>
				<tr>
					<td valign="top"  align="left">
						<label for="status">Status</label>
					</td>
					<td valign="top" align="left">
						${item?.statusLabel}
					</td>
				</tr>
				<tr>
					<td colspan="2">
						<div class="buttons">
							<g:form>
								<g:hiddenField name="id" value="${item?.id}" /> 
								<g:hiddenField name="redirect" value="listGroups" />
								<span class="button">
									<g:actionSubmit class="edit" action="editGroup" value="${message(code: 'default.button.edit.account.label', default: 'Edit group')}" />
								</span>
							</g:form>
						</div>
					</td>
				</tr>
			</tbody>
		</table>
		</fieldset>
</div>
</div>
