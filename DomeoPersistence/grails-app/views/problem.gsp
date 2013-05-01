<!doctype html>
<html>
	<head>
		<title>Domeo Runtime Exception: ${message}</title>
		<meta name="layout" content="domeo-main">
		<link rel="stylesheet" href="${resource(dir: 'css', file: 'errors.css')}" type="text/css">
		<script type="text/JavaScript">
		<!--
		/* Redirects to the homepage */
		/* setTimeout("location.href = '${request.getContextPath()}/public/';",3000); */
		-->
		</script>
	</head>
	<body>

	
		<div class="content">
		    <div class="content_resize">
		    	<br/><br/><br/><br/>
		        <div style="padding-left: 20px; padding-right: 20px; padding-top: 10px" align="center">
					<img src="${resource(dir: 'images/public', file: 'face-sad.png')}"/> ${message}. The problem has been recorded. 
				</div>
		      	<div class="clr"></div>
		      	<br/><br/><br/><br/><br/><br/><br/><br/>
		    </div>
		</div>
		
	</body>
</html>