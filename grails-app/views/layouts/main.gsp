<!DOCTYPE html>
<html>
    <head>
        <title><g:layoutTitle default="spine" /></title>
        <link rel="stylesheet" href="${resource(dir:'css',file:'main.css')}" />
        <link rel="shortcut icon" href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon" />
        <g:layoutHead />
        <g:javascript library="application" />
	    <link href="${resource(dir:'css',file:'reset.css')}" rel="stylesheet" type="text/css">
	  	<link href="${resource(dir:'css',file:'960.css')}" rel="stylesheet" type="text/css">
	  	<link href="${resource(dir:'css',file:'design.css')}" rel="stylesheet" type="text/css">
	  	<link href="${resource(dir:'css',file:'landing.css')}" rel="stylesheet" type="text/css">
    </head>
    <body>
 
 	<div class="body">
  			<div id="header">
    			<div class="container_24">
      				<a href="http://spine-it.com">
      					<img src="${resource(dir:'images/home',file:'logo.png')}" alt="Spine" width="222" height="61" class="logo" />
      				</a>
        <ul class="links">
    	  	<li><g:link action="login" controller="user">Login</g:link></li><br>
      		<li><g:link action="about" controller="home">About Spine</g:link></li><br>
      		<li><g:link action="contact" controller="home">Contact us</g:link></li>
        
      	</ul>
    </div>            
        <g:layoutBody />
    </body>
</html> 