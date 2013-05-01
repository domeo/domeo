<!doctype html>
<html>
	<head>
		<title>Grails Runtime Exception</title>
		<meta name="layout" content="main">
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