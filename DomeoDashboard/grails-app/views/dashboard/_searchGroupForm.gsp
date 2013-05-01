<div id="request" class="sectioncontainer">
	<g:formRemote name="from_form"
	     url="[controller:'dashboard', action:'searchGroup']"
	     before="setDefaultValue();"
	     onSuccess="addResults(data)">
	     <g:hiddenField name="id" value="${user?.id}" />
		<div class="dialog" >
		<div class="title">
		Add User ${user.displayName} to Groups
	</div>
	           	<%-- 
				<table>
					<tbody>
						<tr class="prop">             
							<td class="name" valign="top">Name:</td>             
							<td class="value" valign="top"><input name="name" value="${name}" type="text"></td>          
						</tr>
						<tr class="prop">             
							<td class="name" valign="top">Nickname:</td>             
							<td class="value" valign="top"><input name="nickname" value="${nickname}" type="text"></td>   
						</tr>
						<tr>
							<td><input type="submit" value="Search" /></td>
							<td>
							<span id="ajaxIcon" class="ajaxIcon" style="display:none;"><img src="${resource(dir:'images',file:'spinner.gif')}" alt="${message(code:'spinner.alt',default:'Loading...')}"/> Searching...</span>
						</td>
						</tr>
					</tbody>	
				</table>
				--%>
				 <div style="padding-left: 5px; padding-top: 10px;">
				<table style="width: 900px; border: 1px #ddd solid;">
					<tbody>
						<tr class="prop">             
							<td class="name" valign="top">Name:</td>             
							<td class="value" valign="top"><input name="name" value="${name}" type="text"></td>                  
							<td class="name" valign="top">Short name:</td>             
							<td class="value" valign="top"><input name="shortName" value="${shortName}" type="text"></td>  
							<td class="name" valign="top"><input name="hideempty" value="${hideempty}" type="checkbox"> Include empty</td> 
							<td class="name" valign="top"><input name="onlyactive" value="${onlyactive}" type="checkbox"> Include inactive</td>   
							<td style="padding-left: 4px;width: 20px; display: block; padding-top:6px;"><span id="ajaxIcon" class="ajaxIcon" style="display:none;"><img src="${resource(dir:'images',file:'spinner.gif')}" alt="${message(code:'spinner.alt',default:'Loading...')}"/></span></td>
							<td style="padding-left: 4px;"><input type="submit" value="Search" style="width:120px" /></td>
						</tr>
					</tbody>	
				</table>
</div>
		</div>
	</g:formRemote>
</div>