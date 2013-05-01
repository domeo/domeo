<head>
<meta name="layout" content="domeo-public" />
<title><g:message code="springSecurity.denied.title" /></title>
</head>

<body>
  <div class="header" >
    <div class="header_resize">
      <div class="logo">
      	<h1><a href="index.html">DOMEO<img src="${resource(dir: 'images/public', file: 'domeo_logo.png')}" width="82" height="65" alt="Domeo Toolkit " /><small>Annotation ToolKIT</small></a></h1>
      </div>
      <div class="menu_nav">
        <ul>
          <li><g:link controller="public" action="index"><span>Back</span></g:link></li>
        </ul>
      </div>
      <div class="clr"></div>
    </div>
  </div>
   <div class="content">
   	<div class="content_resize">
   	<div class="clr"></div>
      <div class="slider" style="padding-top: 10px;color:white;" align="center">
		<div id='public-formbox'>
			<br/>
			<br/>
				<div class='errors'><img src="${resource(dir: 'images/public', file: 'face-sad.png')}"/> <g:message code="springSecurity.denied.message" /></div>
			<br/>
		</div>
	  </div>
	<br/><br/><br/><br/><br/><br/>
	</div>
</div>
</body>
