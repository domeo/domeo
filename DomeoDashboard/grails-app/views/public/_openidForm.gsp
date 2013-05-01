<%-- by Paolo Ciccarese --%>
<%-- 
Parameters list
 1) item | instance of UserCreateCommand
 2) error | errors in text format
--%>
<div id='public-formbox'>
	<div>
		<g:if test='${flash.message}'>
				<div class='login_message'>${flash.message}</div>
		</g:if>
		<form method="post" >
		<table style="width: 900px;" class='public-formbox-inner'>
			<tr>
				<td class="public-formbox-title" colspan='3'>
					<table>
						<tr>
							<td align="left">Link Open ID</td>
							<td align="right" class="openid-loginbox-useopenid"></td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td valign="top" colspan="2">
					<g:if test="${msgError!=null}">
						${msgError}
					</g:if>
				</td>
			</tr>
			<tr>
				<td>
					<table>
						<tbody>
							<tr class="prop">
								<td valign="top" width="190px" class="name">
									<label for="username">Username*</label>
								</td>
								<td valign="top" width="155px" class="value">
									<div>
										<g:textField name="username" style="width: 240px;"
											value="${item?.username}" class="${hasErrors(bean: item, field: 'username', 'fieldError')}"/>
									</div>
								</td>
								<td valign="top"  class="caption">
									<g:if test="${item?.username!=null}">
										<g:renderErrors bean="${item}" field="username" />
									</g:if> 
									<g:else>
							        	(8-60 chars)
							        </g:else>
							    </td>
							</tr>
							<tr class="prop">
								<td valign="top" class="name">
									<label for="password">Password*</label>
								</td>
								<td valign="top" class="value">
									<g:textField name="password" style="width: 240px;"
										value="${item?.password}" class="${hasErrors(bean: item, field: 'password', 'fieldError')}"/>
								</td>
								<td valign="top" class="caption">
									<g:if test="${item?.password!=null}">
										<g:renderErrors bean="${item}" field="password" />
									</g:if> 
									<g:else>
						           		(6-16 chars)
						            </g:else>
						        </td>
							</tr>
							<tr class="prop">
								<td valign="top" class="name">
									<label for="openId">OpenID*</label>
								</td>
								<td valign="top" class="value">
									<g:textField name="openId" style="width: 240px;"
										value="${item?.openId}"  class="${hasErrors(bean: item, field: 'openId', 'fieldError')}"/>
								</td>
								<td valign="top" class="caption">
									<g:if test="${item?.openId!=null}">
										<g:renderErrors bean="${item}" field="openId" />
									</g:if> 
									<g:else>
						           		(Valid OpenID)
						            </g:else>
						        </td>
							</tr>
							<tr>
								<td valign="top" colspan="3">
									<g:if test="${msgError!=null}">
										${msgError}
									</g:if>
								</td>
							</tr>
						</tbody>
					</table>
				</td>
			</tr>
			<tr>
				<td valign="top" colspan="3" >
					<div id='openidLogin' align="center">
						<div class="buttons">
							<span class="button">
								<g:actionSubmit class="save" action="saveOpenIdLink" value="${message(code: 'default.button.edit.account.label', default: 'Connect')}" />
							</span>
						</div>
					</div>
				</td>
			</tr>
		</table>
		</form>
	</div>
</div>