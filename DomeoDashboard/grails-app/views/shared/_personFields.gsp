
<%-- by Paolo Ciccarese --%>
<%-- 
Parameters list
 1) item | instance of UserCreateCommand
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
							value="${item?.title}"  class="${hasErrors(bean: item, field: 'title', 'fieldError')}"/>
					</td>
					<td valign="top" class="caption">
						<g:if test="${item?.title!=null}">
							<g:renderErrors bean="${item}" field="title" />
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
							value="${item?.firstName}"  class="${hasErrors(bean: item, field: 'firstName', 'fieldError')}"/>
					</td>
					<td valign="top" class="caption">
						<g:if test="${item?.firstName!=null}">
							<g:renderErrors bean="${item}" field="firstName" />
						</g:if> 
						<g:else>
			           		(max 255 chars)
			            </g:else>
			        </td>
				</tr>
				<tr class="prop">
					<td valign="top" width="190px" class="name">
						<label for="name">Middle name</label>
					</td>
					<td valign="top" width="155px" class="value">
						<g:textField name="middleName" style="width: 240px;"
							value="${item?.middleName}"  class="${hasErrors(bean: item, field: 'middleName', 'fieldError')}"/>
					</td>
					<td valign="top" class="caption">
						<g:if test="${item?.middleName!=null}">
							<g:renderErrors bean="${item}" field="middleName" />
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
							value="${item?.lastName}"  class="${hasErrors(bean: item, field: 'lastName', 'fieldError')}"/>
					</td>
					<td valign="top" class="caption">
						<g:if test="${item?.lastName!=null}">
							<g:renderErrors bean="${item}" field="lastName" />
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
							value="${item?.displayName}"  class="${hasErrors(bean: item, field: 'displayName', 'fieldError')}"/>
					</td>
					<td valign="top" class="caption">
						<g:if test="${item?.displayName!=null}">
							<g:renderErrors bean="${item}" field="displayName" />
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
							value="${item?.email}"  class="${hasErrors(bean: item, field: 'displayName', 'fieldError')}"/>
					</td>
					<td valign="top" class="caption">
						<g:if test="${item?.email!=null}">
							<g:renderErrors bean="${item}" field="email" />
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
							value="${item?.affiliation}"  class="${hasErrors(bean: item, field: 'affiliation', 'fieldError')}"/>
					</td>
					<td valign="top" class="caption">
						<g:if test="${item?.affiliation!=null}">
							<g:renderErrors bean="${item}" field="affiliation" />
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
							value="${item?.country}"  class="${hasErrors(bean: item, field: 'displayName', 'fieldError')}"/>
					</td>
					<td valign="top" class="caption">
						<g:if test="${item?.country!=null}">
							<g:renderErrors bean="${item}" field="country" />
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
