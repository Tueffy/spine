<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  	<title><g:layoutTitle default="spine" /></title>
  	<link rel="shortcut icon" href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon" />
  	
  	<link href="${resource(dir:'js/jquery/themes/redmond',file:'jquery-ui-1.8.20.custom.css')}" rel="stylesheet" type="text/css" />
  	<link href="${resource(dir:'js/jquery/themes',file:'jquery-ui.css')}" rel="stylesheet" type="text/css" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'main.css')}" />
  	<link href="${resource(dir:'css',file:'reset.css')}" rel="stylesheet" type="text/css" />
  	<link href="${resource(dir:'css',file:'960.css')}" rel="stylesheet" type="text/css" />
  	<link href="${resource(dir:'css',file:'design.css')}" rel="stylesheet" type="text/css" />
  	<link href="${resource(dir:'css',file:'ajax.css')}" rel="stylesheet" type="text/css" />
  	
  
  	<g:javascript src="jquery/jquery-1.7.2.min.js" />
  	<g:javascript src="jquery/jquery-ui-1.8.20.custom.min.js" />
  	<g:javascript>
  		jQuery.noConflict();
  	</g:javascript>
  	<g:javascript src="main.js" />
  	<g:javascript library='scriptaculous' />
  	<g:layoutHead />
</head>

<body>
	
		<!-- BEGIN : header & nav -->
		<div id="header">
			<g:render template="/inc/header"></g:render>
		</div> 
		
		<div id="nav">
			<g:if test="${hotTags}">
				<g:render template="/inc/nav"></g:render>
			</g:if>
		</div>
		<!-- END : header & nav -->
		
		<!-- BEGIN : container -->
		<div id="container" class="container_24">
			<g:layoutBody />
		</div>
		<!-- END : container -->           
	

</body>


</html> 