<!doctype html>
<!--[if lt IE 7 ]> <html lang="en" class="no-js ie6"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="no-js ie7"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="no-js ie8"> <![endif]-->
<!--[if IE 9 ]>    <html lang="en" class="no-js ie9"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> <html lang="en" class="no-js"><!--<![endif]-->

<head>
	<meta name="layout" content="domeo-public" />
	<title>Info for Domeo - The Annotation Toolkit</title>
</head>
<body>
<div class="header">
    <g:render template="/public/shared/before-slider" />
    
<div id='public-formbox'>
    <div>
        <table style="width: 900px;" class='public-formbox-inner'>
            <tr>
                <td class="public-formbox-title">
                    <table>
                        <tr>
                            <td align="left">Domeo Node Info</td>
                        </tr>
                    </table>
                </td>
            </tr>
            <tr>
                <td>
                    <table>
                        <tr>
                            <td align="left"> <p style="text-align: justify;"> 
                                This installation is running 
                                the <span style="font-weight: bold;"><g:meta name="app.fullname"/> <g:meta name="app.version"/> (build <g:meta name="app.build"/> - <g:meta name="app.date"/>)</span>.<br/>
                                The instance is administered by <span style="font-weight: bold;">
                                <g:if test="${grailsApplication.config.domeo.administrator}">
                                    ${grailsApplication.config.domeo.administrator}
                                </g:if>
                                <g:else>(anonymous)</g:else>
                                at
                                <g:if test="${grailsApplication.config.domeo.organization}">
                                    ${grailsApplication.config.domeo.organization}.
                                </g:if>
                                <g:else>(not specified)</g:else>
                                </span><br/>
                                For enquiries please contact <span style="font-weight: bold;">
                                <g:if test="${grailsApplication.config.domeo.admin.email.display}">
                                    ${grailsApplication.config.domeo.admin.email.display}
                                </g:if>
                                <g:else>(not specified)</g:else>
                                </span></p>
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
        </table>
    </div>
</div>     
            
<g:render template="/public/shared/after-slider" />

<div class="content">
    <div class="content_resize">
        <div style="padding-left: 20px; padding-right: 20px; padding-top: 10px">
            <h3>The Domeo federated architecture</h3>
            <p>Coming soon... </p><br/><br/><br/><br/><br/><br/><br/>
      	</div>
      	<div class="clr"></div>
    </div>
</div>

<g:render template="/public/shared/banner-bottom" />        
</body>
</html>
