<!doctype html>
<html>
	<head>
		<title>Domeo Runtime Exception</title>
		<meta name="layout" content="domeo-public">
		<link rel="stylesheet" href="${resource(dir: 'css', file: 'errors.css')}" type="text/css">
	</head>
	<body>
		<g:if env="development">
			${message}
			<g:renderException exception="${exception}" />
		</g:if>
		<g:else>
			${message}
			<g:renderException exception="${exception}" />
		</g:else>
	</body>
</html>