<div id="request" class="sectioncontainer">
<div class="dialog">
	<div class="title">Export options ${loggedUser.displayName}</div>
	<div style="padding: 10px;">
		<g:form controller="export" action="export">
	    	<g:checkBox name="mine"/> Created by me <br/>
	    	<g:checkBox name="groups"/> Created by my groups <br/>
	    	<g:submitButton name="export" value="Export" />
	  	</g:form>
	  	
	  	<%-- 
	  	<g:form controller="export" action="export">
	  		<g:radio name="export" value="mine" checked="true"/> Created by me <br/>
			<g:radio name="export" value="groups"/> Created by my groups <br/>
			<g:radio name="export" value="public"/> Created by my groups <br/>
	    	<g:submitButton name="export" value="Export" />
	  	</g:form>
	  	--%>
	</div>
</div>

</div>