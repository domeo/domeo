
<%-- by Paolo Ciccarese --%>
<%-- 
Parameters list
 1) user | instance of UserCreateCommand
Stylesheet
 1) fieldError | background and font color in erroneous text fields
--%>
<!-- Begin account request form -->
<tr>
	<td valign="top">
		<table>
			<tbody>
				<tr class="prop">
					<td valign="top" width="190px" class="name">
						<label for="name">First name*</label>
					</td>
					<td valign="top" width="155px" class="value">
						<g:textField name="firstName" style="width: 240px;"
							value="${item?.firstName}"  class="${hasErrors(bean: item, field: 'firstName', 'fieldError')}"/>
					</td>
					<td valign="top" class="caption">
						(max 255 chars)
						<g:if test="${item && item.errors.hasFieldErrors('firstName')}">
							 ${item.errors.getFieldError("firstName").getCode() }
						</g:if> 
			        </td>
				</tr>
				<tr class="prop">
					<td valign="top" class="name">
						<label for="name">Last name*</label>
					</td>
					<td valign="top" class="value">
						<g:textField name="lastName" style="width: 240px;"
							value="${item?.lastName}"  class="${hasErrors(bean: item, field: 'lastName', 'fieldError')}"/>
					</td>
					<td valign="top" class="caption">
						(max 255 chars)
						<g:if test="${item && item.errors.hasFieldErrors('lastName')}">
							 ${item.errors.getFieldError("lastName").getCode() }
						</g:if> 
			        </td>
				</tr>
				<tr class="prop">
					<td valign="top" class="name">
						<label for="name">Display name*</label>
					</td>
					<td valign="top" class="value">
						<g:textField name="displayName" style="width: 240px;"
							value="${item?.displayName}"  class="${hasErrors(bean: item, field: 'displayName', 'fieldError')}"/>
					</td>
					<td valign="top" class="caption">
						(max 255 chars)
						<g:if test="${item && item.errors.hasFieldErrors('displayName')}">
							 ${item.errors.getFieldError("displayName").getCode() }
						</g:if> 
			        </td>
				</tr>
				<tr class="prop">
					<td valign="top" class="name">
						<label for="name">Email*</label>
					</td>
					<td valign="top" class="value">
						<g:if test="${errorCode=="3"}">
							<g:textField name="email" style="width: 240px;"
								value="${item?.email}"  class="fieldError"/>
						</g:if>
						<g:else>
							<g:textField name="email" style="width: 240px;"
								value="${item?.email}"  class="${hasErrors(bean: item, field: 'email', 'fieldError')}"/>
						</g:else>
					</td>
					<td valign="top" class="caption">
						(valid email)
						<g:if test="${item && item.errors.hasFieldErrors('email')}">
							 ${item.errors.getFieldError("email").getCode() }
						</g:if> 
						<g:if test="${errorCode=="3"}">
							Email already registered
						</g:if>
			        </td>
				</tr>
				<tr class="prop">
					<td valign="top" class="name">
						<label for="name">Affiliation*</label>
					</td>
					<td valign="top" class="value">
						<g:textField name="affiliation" style="width: 240px;"
							value="${item?.affiliation}"  class="${hasErrors(bean: item, field: 'affiliation', 'fieldError')}"/>
					</td>
					<td valign="top" class="caption">
						(max 255 chars)
						<g:if test="${item && item.errors.hasFieldErrors('affiliation')}">
							 ${item.errors.getFieldError("affiliation").getCode() }
						</g:if> 
			        </td>
				</tr>
				<tr class="prop">
					<td valign="top" class="name">
						<label for="name">Country*</label>
					</td>
					<td valign="top" class="value">
						<g:textField name="country" style="width: 240px;"
							value="${item?.country}"  class="${hasErrors(bean: item, field: 'country', 'fieldError')}"/>
					</td>
					<td valign="top" class="caption">
						(valid country)
						<g:if test="${item && item.errors.hasFieldErrors('country')}">
							 ${item.errors.getFieldError("country").getCode() }
						</g:if> 
			        </td>
				</tr>
			</tbody>
		</table>
	</td>
</tr>
