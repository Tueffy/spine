<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  <title>Spine Landing</title>
  <link href="/spine/css/reset.css" rel="stylesheet" type="text/css">
  <link href="/spine/css/960.css" rel="stylesheet" type="text/css">
  <link href="/spine/css/design.css" rel="stylesheet" type="text/css">
  <link href="/spine/css/landing.css" rel="stylesheet" type="text/css">
</head>
<body>
  <div id="header">
    	<g:render template="/inc/header"></g:render>
  </div>
  <div id="nav">
   		&nbsp;
  </div>
  <!-- BEGIN : CONTAINER -->
  <div id="container" class="container_24" style="margin-top:80px">
    <div class="grid_10 omega knowledge">
    	<img src="${resource(dir:'images/home',file:'spine-connected.png')}" alt="Connected">
    </div>  
    <div class="grid_14 alpha landing">
    	<h2 style="font-size:36pt;color:#000000">Forgot your password?</h2>
    	<p class="subtitle" style="font-size:20pt;font-family:Tahoma;color:#67624e">We will send it to you ...</p>
        <g:form method="post" action="doPasswordRecovery" class="registration" controller="user">
          <input type="text" name="email" value="${home?.email}" />
          <span style="{align:right}"><input type="submit" value="Send" /></span>
        </g:form>
    </div>	
  </div>  
  <!-- END : CONTAINER -->
</body>
</html>