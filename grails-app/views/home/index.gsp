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
    <div class="container_24">
      <img src="/spine/images/logo.png" alt="Spine" width="222" height="61" class="logo" />
    
      <ul class="links">
      	<li><a href="#">About Spine</a></li>
      	<li><a href="#">How it works</a></li>
      	<li><a href="#">Terms and conditions</a></li>
      	<li><a href="#">Disclaimer</a></li>
        <li><a href="/spine/user/login">Log in</a></li>
      </ul>
    </div>
  </div>
  
  <!-- BEGIN : container -->
  <div id="container" class="container_24">
  	<div class="grid_14 alpha landing">
    	<h2>Connect with your co-workers</h2>
        <p class="subtitle">Spine is the <strong>free</strong> private social network for your company</p>
        <g:form method="post" action="doSignup" class="registration" controller="home">
          <input type="text" name="email" value="${home?.email}" />
          <input type="submit" value="Sign up" />
        </g:form>
    </div>
    <div class="grid_10 omega knowledge"><img src="${resource(dir:'images/home',file:'knowledge.png')}" width="399" height="299" alt="Knowledge"></div>
  
    
  	
  </div>
  
  <!-- END : container -->
  
</body>
</html>
