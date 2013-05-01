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
						<label for="name">Name</label>
					</td>
					<td valign="top" width="255px" class="value">
						<div>
							<g:textField name="name" style="width: 240px;"
								value="${item?.name}" class="${hasErrors(bean: item, field: 'name', 'fieldError')}"/>
						</div>
					</td>
					<td valign="top" class="caption">
						<g:if test="${item?.name!=null}">
							<g:renderErrors bean="${item}" field="name" />
						</g:if> 
						<g:else>
				        	(max 16 chars)
				        </g:else>
				    </td>
				</tr>
				<tr class="prop">
					<td valign="top" class="name">
						<label for="shortName">Short Name</label>
					</td>
					<td valign="top" class="value">
						<g:textField name="shortName" style="width: 240px;"
							value="${item?.shortName}" class="${hasErrors(bean: item, field: 'shortName', 'fieldError')}"/>
					</td>
					<td valign="top" class="caption">
						<g:if test="${item?.shortName!=null}">
							<g:renderErrors bean="${item}" field="shortName" />
						</g:if> 
						<g:else>
			           		(max 255 chars)
			            </g:else>
			        </td>
				</tr>
				<tr class="prop">
					<td valign="top" class="name">
						<label for="description">Description</label>
					</td>
					<td valign="top" class="value">
						<g:textField name="description" style="width: 240px;"
							value="${item?.description}"  class="${hasErrors(bean: item, field: 'description', 'fieldError')}"/>
					</td>
					<td valign="top" class="caption">
						<g:if test="${item?.description!=null}">
							<g:renderErrors bean="${item}" field="description" />
						</g:if> 
						<g:else>
			           		(max 255 chars)
			            </g:else>
			        </td>
				</tr>
				<tr class="prop">
					<td valign="top" class="name">
						<label for="privacy">Privacy</label>
					</td>
					<td valign="top" colspan="2" class="value">
						<g:if test="${action=='create'}">
							<g:radio name="privacy" value="${DefaultGroupPrivacy.PUBLIC.value()}" checked="checked"/> Public
							<g:radio name="privacy" value="${DefaultGroupPrivacy.RESTRICTED.value()}"/> Restricted
							<g:radio name="privacy" value="${DefaultGroupPrivacy.PRIVATE.value()}"/> Private
						</g:if>
						<g:else>
							<g:if test="${item?.privacy?.value==DefaultGroupPrivacy.PUBLIC.value()}">
								<g:radio name="privacy" value="${DefaultGroupPrivacy.PUBLIC.value()}" checked="checked"/> Public
								<g:radio name="privacy" value="${DefaultGroupPrivacy.RESTRICTED.value()}"/> Restricted
								<g:radio name="privacy" value="${DefaultGroupPrivacy.PRIVATE.value()}"/> Private
							</g:if>
							<g:elseif test="${item?.privacy?.value==DefaultGroupPrivacy.RESTRICTED.value()}">
								<g:radio name="privacy" value="${DefaultGroupPrivacy.PUBLIC.value()}"/> Public
								<g:radio name="privacy" value="${DefaultGroupPrivacy.RESTRICTED.value()}" checked="checked"/> Restricted
								<g:radio name="privacy" value="${DefaultGroupPrivacy.PRIVATE.value()}"/> Private
							</g:elseif>
							<g:elseif test="${item?.privacy?.value==DefaultGroupPrivacy.PRIVATE.value()}">
								<g:radio name="privacy" value="${DefaultGroupPrivacy.PUBLIC.value()}"/> Public
								<g:radio name="privacy" value="${DefaultGroupPrivacy.RESTRICTED.value()}"/> Restricted
								<g:radio name="privacy" value="${DefaultGroupPrivacy.PRIVATE.value()}" checked="checked"/> Private
							</g:elseif>
						</g:else>
			        </td>
				</tr>
				<tr class="prop">
					<td valign="top" class="name">
						<label for="description">Status</label>
					</td>
					<td valign="top" colspan="2" class="value">
						<g:if test="${action=='create'}">
							<g:radio name="status" value="${DefaultGroupStatus.ACTIVE.value()}" checked="checked"/> Enabled
						</g:if>
						<g:else>
							<g:if test="${item.status==DefaultGroupStatus.ACTIVE.value()}">
								<g:radio name="status" value="${DefaultGroupStatus.ACTIVE.value()}" checked="checked"/> Enabled
								<g:radio name="status" value="${DefaultGroupStatus.LOCKED.value()}"/> Locked
								<g:radio name="status" value="${DefaultGroupStatus.DISABLED.value()}"/> Disabled
							</g:if>
							<g:elseif test="${item.status==DefaultGroupStatus.LOCKED.value()}">
								<g:radio name="status" value="${DefaultGroupStatus.ACTIVE.value()}"/> Enabled
								<g:radio name="status" value="${DefaultGroupStatus.LOCKED.value()}" checked="checked"/> Locked
								<g:radio name="status" value="${DefaultGroupStatus.DISABLED.value()}"/> Disabled
							</g:elseif>
							<g:elseif test="${item.status==DefaultGroupStatus.DISABLED.value()}">
								<g:radio name="status" value="${DefaultGroupStatus.ACTIVE.value()}"/> Enabled
								<g:radio name="status" value="${DefaultGroupStatus.LOCKED.value()}"/> Locked
								<g:radio name="status" value="${DefaultGroupStatus.DISABLED.value()}" checked="checked"/> Disabled
							</g:elseif>
						</g:else>
			        </td>
				</tr>
			</tbody>
		</table>
	</td>
</tr>
