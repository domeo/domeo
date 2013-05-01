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
							<td align="left">Log in</td>
							<td align="right" class="openid-loginbox-useopenid">
								<input type="checkbox" id="toggle" checked='checked' onclick='toggleForms()'/>
								<label for='toggle'>Use OpenID</label>
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td>
				<div id='openidLogin' style='display: none'>
					<form action='${openIdPostUrl}' method='POST' autocomplete='off' name='openIdLoginForm'>
					<table class="openid-loginbox-userpass">
						<tr>
							<td>OpenID:</td>
							<td><input type="text" name="${openidIdentifier}" class="openid-identifier"/></td>
						</tr>
						<g:if test='${persistentRememberMe}'>
						<tr>
							<td><label for='remember_me'>Remember me</label></td>
							<td>
								<input type='checkbox' name='${rememberMeParameter}' id='remember_me'/>
							</td>
						</tr>
						</g:if>
						<tr>
							<td colspan='2' class="openid-submit" align="center">
								<input type="submit" value="Log in" />
							</td>
						</tr>
					</table>
					</form>
				</div>
	
				<div id='formLogin' >
					<form action='${daoPostUrl}' method='POST' autocomplete='off' name='loginForm'>
					<table class="openid-loginbox-userpass">
						<tr>
							<td>Username:</td>
							<td><input type="text" name='j_username' id='username' class="username" /></td>
						</tr>
						<tr>
							<td>Password:</td>
							<td><input type="password" name='j_password' id='password' /></td>
						</tr>
						<tr>
							<td><label for='remember_me'>Remember me</label></td>
							<td>
								<input type='checkbox' name='${rememberMeParameter}' id='remember_me'/>
							</td>
						</tr>
						<tr>
							<td colspan='2' class="openid-submit" align="center">
								<input type="submit" value="Log in" />
							</td>
						</tr>
					</table>
					</form>
				</div>
	
				</td>
			</tr>
		</table>
	</div>
</div>
