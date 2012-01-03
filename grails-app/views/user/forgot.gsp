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
  <!-- BEGIN : HEADER -->
  <div id="header">
  	 <g:render template="/common/inc/header"></g:render>
  </div>
  <!-- END : HEADER -->
  <!-- BEGIN : NAV -->
  <div id="nav">
    &nbsp;
  </div>  
  <!-- END : NAV -->
  <!-- BEGIN : CONTAINER -->
  <div id="container" class="container_24">
    <div class="grid_10 omega knowledge" style="padding-top:80px">
    	<img src="${resource(dir:'images/home',file:'spine-connected.png')}" alt="Connected">
    </div>  
    <div class="grid_14 alpha landing" style="padding-top:40px">
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