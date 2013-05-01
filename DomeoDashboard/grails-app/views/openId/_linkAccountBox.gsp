<div id='openid-loginbox'>
	<div class='inner'>
		<g:if test='${flash.message}'>
				<div class='login_message'>${flash.message}</div>
		</g:if>
		<table class='openid-loginbox-inner' cellpadding="0" cellspacing="0">
			<tr>
				<td class="openid-loginbox-title">
					<table>
						<tr>
							<td align="left">Link Open ID to existing account</td>
							<td align="right" class="openid-loginbox-useopenid">
							
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td>
	
				<div id='formLogin' >
					<g:form action='linkAccount'>
						<table class="openid-loginbox-userpass">
						<tr>
							<td>Open ID:</td>
							<td><span id='openid'>${openId}</span></td>
						</tr>
				
						<tr>
							<td><label for='username'>Username:</label></td>
							<td><g:textField name='username' value='${command?.username}'/></td>
						</tr>
				
						<tr>
							<td><label for='password'>Password:</label></td>
							<td><g:passwordField name='password' value='${command?.password}'/></td>
						</tr>
						<tr>
							<td colspan='2' class="openid-submit" align="center">
								<input type='submit' value='Link'/>
							</td>
						</tr>
						</table>
				
						
				
					</g:form>
				</div>
	
				</td>
			</tr>
		</table>
	</div>
</div>
