
<!-- Begin top navigation menu -->
<div class="menu_nav">
    <ul>
    	<%-- Access --%> 
        <li><g:link controller="public" action="_access"><span>Access</span></g:link></li>
        <%-- Search --%> 
        <%--
        <li><g:link controller="public" action="search"><span>Search</span></g:link></li>
         --%>
        <%-- Signup --%> 
        <g:if test="${grailsApplication.config.domeo.dashboard.management.users.disable!='true'}">
	        <g:if test="${menuitem=='signup'}"><li class="active"><g:link controller="openid" action="auth"><span>Sign Up</span></g:link></li></g:if>
	        <g:else><li><g:link controller="public" action="signup"><span>Sign Up</span></g:link></li></g:else>
        </g:if>
        <%-- Node info --%> 
        <g:if test="${menuitem=='nodeinfo'}"><li class="active"><g:link controller="public" action="nodeinfo"><span>Node</span></g:link></li></g:if>
        <g:else><li><g:link controller="public" action="nodeinfo"><span>Node</span></g:link></li></g:else>
        <%-- Credits --%> 
        <g:if test="${menuitem=='credits'}"><li class="active"><g:link controller="public" action="credits"><span>Credits</span></g:link></li></g:if>
        <g:else><li><g:link controller="public" action="credits"><span>Credits</span></g:link></li></g:else>
        
    </ul>
</div>
<!-- End top navigation menu -->

<%-- 
<g:if test="${menuitem=='openid'}"><li class="active"><g:link controller="openid" action="linkAccount"><span>Open ID</span></g:link></li></g:if>
<g:else><li><g:link controller="openid" action="linkAccount"><span>Open ID</span></g:link></li></g:else>
--%>