<!doctype html>
<html>
	<head>
		<title>Domeo Runtime Exception</title>
		<meta name="layout" content="domeo-public">
		<link rel="stylesheet" href="${resource(dir: 'css', file: 'errors.css')}" type="text/css">
		<script type="text/JavaScript">
		<!--
		/* Redirects to the homepage */
		setTimeout("location.href = '${request.getContextPath()}/public/';",2500);
		-->
		</script>
	</head>
	<body>
		<g:render template="/public/shared/before-slider" />
		<div style="height: 100px; color: #fff;">
			<br/><br/>
			<img src="${resource(dir: 'images/public', file: 'face-sad.png')}"/> We are very sorry, we could not find the page you have requested!
		</div>
		<g:render template="/public/shared/after-slider" />
		
		<div class="content">
    		<div class="content_resize">
				<br/><br/>
				<br/><br/>
				<br/><br/>
				<br/><br/>
				<br/><br/>
				<br/><br/>
				<br/><br/>
				<br/><br/>
			</div>
		</div>
	</body>
</html>