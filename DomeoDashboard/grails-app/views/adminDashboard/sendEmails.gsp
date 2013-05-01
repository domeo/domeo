<!DOCTYPE html>
<%-- by Paolo Ciccarese --%>
<html>
<head>
<meta name="layout" content="dashboard-admin" />
</head>

<body>
<!-- Begin Content -->
<div class="title">Global notification service</div>

<div class="contentsection">
<div class="paragraph">
With this form you will be sending an email to all the Domeo users.<br/><br/>

<form action="sendMessage" method="post" name="emailMessage" id="emailMessage">
<font style="font-weight: bold;">Subject</font>: <br/>
Domeo Annotation Tool - <g:select name="category" from="${['Software update', 'Maintanance', 'General message']}"></g:select><br/>
<font style="font-weight: bold;">Body</font>: <br/>
Dear Domeo user,<br/>
if you are receiving this email is because you are a user of one of our Domeo installations.<br/>

We would like to communicate that <br/>
<g:textArea name="body" rows="5" cols="40"/><br/>
Sincerely,<br/>
${grailsApplication.config.domeo.administrator}<br/>
<g:submitButton name="update" value="Send" />
</form>
</div>
</div>
<!-- End Content -->
</body>
</html>