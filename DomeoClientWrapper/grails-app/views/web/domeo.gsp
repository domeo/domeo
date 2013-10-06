<!doctype html>
<!-- The DOCTYPE declaration above will set the    -->
<!-- browser's rendering engine into               -->
<!-- "Standards Mode". Replacing this declaration  -->
<!-- with a "Quirks Mode" doctype may lead to some -->
<!-- differences in layout.                        -->

<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <meta name="author" content="Dr. Paolo Ciccarese">
	<meta name="author" content="http://www.paolociccarese.info" />

	<link rel='shortcut icon' href="${resource(dir:'images/general',file:'domeo.ico')}" type='image/x-icon'/ >
	<link rel="stylesheet" href="${resource(dir:'',file:'Domeo.css')}">
	
	<script type="text/javascript" language="javascript" src="${resource(dir:'js',file:'modernizr.min.js')}"></script>
	
	<script type="text/javascript" src="https://www.google.com/jsapi"></script>
	<!--  script type="text/javascript">

      // Load the Visualization API and the piechart package.
      google.load('visualization', '1.0', {'packages':['corechart']});
      </script -->
	

    <!--                                           -->
    <!-- Title and default user                    -->
    <!--                                           -->
    <title>Domeo (Development Mode ++)</title>
    
    
    
    <script>
	   var username = "current";
	   var servername = "Domeo";
	   var serverversion = "b5";
	   var standalone = "false";
	   var jsonformat = "true";
	   var testFiles = "true";
	   var defaultSetPrivacy = "${grailsApplication.config.domeo.default.annotationset.privacy} ";
	</script> 
    
    <!--                                           -->
    <!-- This script loads your compiled module.   -->
    <!-- If you add any GWT meta tags, they must   -->
    <!-- be added before this line.                -->
    <!--                                           -->
    <script type="text/javascript" language="javascript" src="${resource(dir:'js',file:'utils.js')}"></script> 
    <script type="text/javascript" language="javascript" src="${resource(dir:'js',file:'scroll.js')}"></script> 
    <script type="text/javascript" language="javascript" src="${resource(dir:'js',file:'annotation.js')}"></script> 
    
    <!--  script type="text/javascript" language="javascript" src="../gwt/domeo/domeo.nocache.js"></script -->
    
    <script type="text/javascript" language="javascript" src="${resource(dir: 'gwt/domeo', file: 'domeo.nocache.js', plugin: 'domeo-client-wrapper')}"></script> 
    
    <script type="text/javascript">

	  var _gaq = _gaq || [];
	  _gaq.push(['_setAccount', 'UA-30203751-1']);
	  _gaq.push(['_trackPageview']);
	
	  (function() {
	    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
	    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
	    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
	  })();
	
	</script>
  </head>

  <!--                                           -->
  <!-- The body can have arbitrary html, or      -->
  <!-- you can leave the body empty if you want  -->
  <!-- to create a completely dynamic UI.        -->
  <!--                                           -->
  <body>

    <!-- OPTIONAL: include this if you want history support -->
    <iframe src="javascript:''" id="__gwt_historyFrame" tabIndex='-1' style="position:absolute;width:0;height:0;border:0"></iframe>
    
    <!-- RECOMMENDED if your web app will not function without JavaScript enabled -->
    <noscript>
      <div style="width: 22em; position: absolute; left: 50%; margin-left: -11em; color: red; background-color: white; border: 1px solid red; padding: 4px; font-family: sans-serif">
        Your web browser must have JavaScript enabled
        in order for this application to display correctly.
      </div>
    </noscript>

	<div id="af-init" style="width: 1200px; padding-left:10px"></div>
  </body>
</html>