
<%-- by Paolo Ciccarese --%>
<%-- 
Parameters list
 1) user | instance of UserCreateCommand
Stylesheet
 1) fieldError | background and font color in erroneous text fields
--%>
<tr>
	<td valign="top">
		<table>
			<tbody>
				<tr class="prop">
					<td valign="top" width="190px" class="name">
						<label for="name">Title</label>
					</td>
					<td valign="top" width="155px" class="value">
						<g:textField name="title" style="width: 240px;"
							value="${user?.title}"  class="${hasErrors(bean: user, field: 'title', 'fieldError')}"/>
					</td>
					<td valign="top" class="caption">
						<g:if test="${user?.title!=null}">
							<g:renderErrors bean="${user}" field="title" />
						</g:if> 
						<g:else>
			           		(max 255 chars)
			            </g:else>
			        </td>
				</tr>
				<tr class="prop">
					<td valign="top" width="190px" class="name">
						<label for="name">First name*</label>
					</td>
					<td valign="top" width="155px" class="value">
						<g:textField name="firstName" style="width: 240px;"
							value="${user?.firstName}"  class="${hasErrors(bean: user, field: 'firstName', 'fieldError')}"/>
					</td>
					<td valign="top" class="caption">
						<g:if test="${user?.firstName!=null}">
							<g:renderErrors bean="${user}" field="firstName" />
						</g:if> 
						<g:else>
			           		(max 255 chars)
			            </g:else>
			        </td>
				</tr>
				<tr class="prop">
					<td valign="top" class="name">
						<label for="name">Middle name</label>
					</td>
					<td valign="top" class="value">
						<g:textField name="middleName" style="width: 240px;"
							value="${user?.middleName}"  class="${hasErrors(bean: user, field: 'middleName', 'fieldError')}"/>
					</td>
					<td valign="top" class="caption">
						<g:if test="${user?.middleName!=null}">
							<g:renderErrors bean="${user}" field="middleName" />
						</g:if> 
						<g:else>
			           		(max 255 chars)
			            </g:else>
			        </td>
				</tr>
				<tr class="prop">
					<td valign="top" class="name">
						<label for="name">Last name*</label>
					</td>
					<td valign="top" class="value">
						<g:textField name="lastName" style="width: 240px;"
							value="${user?.lastName}"  class="${hasErrors(bean: user, field: 'lastName', 'fieldError')}"/>
					</td>
					<td valign="top" class="caption">
						<g:if test="${user?.lastName!=null}">
							<g:renderErrors bean="${user}" field="lastName" />
						</g:if> 
						<g:else>
			           		(max 255 chars)
			            </g:else>
			        </td>
				</tr>
				<tr class="prop">
					<td valign="top" class="name">
						<label for="name">Display name*</label>
					</td>
					<td valign="top" class="value">
						<g:textField name="displayName" style="width: 240px;"
							value="${user?.displayName}"  class="${hasErrors(bean: user, field: 'displayName', 'fieldError')}"/>
					</td>
					<td valign="top" class="caption">
						<g:if test="${user?.displayName!=null}">
							<g:renderErrors bean="${user}" field="displayName" />
						</g:if> 
						<g:else>
			           		(max 255 chars)
			            </g:else>
			        </td>
				</tr>
				<tr class="prop">
					<td valign="top" class="name">
						<label for="name">Email*</label>
					</td>
					<td valign="top" class="value">
						<g:textField name="email" style="width: 240px;"
							value="${user?.email}"  class="${hasErrors(bean: user, field: 'displayName', 'fieldError')}"/>
					</td>
					<td valign="top" class="caption">
						<g:if test="${user?.email!=null}">
							<g:renderErrors bean="${user}" field="email" />
						</g:if> 
						<g:else>
			           		(valid email)
			            </g:else>
			        </td>
				</tr>
				<tr class="prop">
					<td valign="top" class="name">
						<label for="name">Affiliation*</label>
					</td>
					<td valign="top" class="value">
						<g:textField name="affiliation" style="width: 240px;"
							value="${user?.affiliation}"  class="${hasErrors(bean: user, field: 'affiliation', 'fieldError')}"/>
					</td>
					<td valign="top" class="caption">
						<g:if test="${user?.affiliation!=null}">
							<g:renderErrors bean="${user}" field="affiliation" />
						</g:if> 
						<g:else>
			           		(max 255 chars)
			            </g:else>
			        </td>
				</tr>
				<tr class="prop">
					<td valign="top" class="name">
						<label for="name">Country*</label>
					</td>
					<td valign="top" class="value">
						<g:textField name="country" style="width: 240px;"
							value="${user?.country}"  class="${hasErrors(bean: user, field: 'displayName', 'fieldError')}"/>
					</td>
					<td valign="top" class="caption">
						<g:if test="${user?.country!=null}">
							<g:renderErrors bean="${user}" field="country" />
						</g:if> 
						<g:else>
			           		(valid country)
			            </g:else>
			        </td>
				</tr>
			</tbody>
		</table>
	</td>
</tr>
