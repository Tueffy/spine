<html>
<head>
  <g:render template="/common/inc/html-head"></g:render>
</head>
<body>
  <div id="header">
    	<g:render template="/common/inc/header"></g:render>
  </div>
  <div id="nav">
   		&nbsp;
  </div>
  <!-- BEGIN : CONTAINER -->
  <div id="container" class="container_24">
    <div class="grid_10 omega knowledge" style="padding-top:80px">
    	<img src="${resource(dir:'images/home',file:'spine-connected.png')}" alt="Connected">
    </div>  
    <div class="grid_14 alpha landing" style="padding-top:40px">
    	<h2 style="font-size:36pt;color:#000000">Stay connected <br/> with your co-workers</h2>
        <p class="subtitle" style="font-size:20pt;font-family:Tahoma;color:#67624e">Spine is the private social network <br/>inside  your company</p>
        <g:form method="post" action="doSignup" class="registration" controller="home">
          <input type="text" name="email" value="${home?.email}" />
          <span style="{align:right}"><input type="submit" value="Sign up" /></span>
        </g:form>
        <br/>
        <span style="{align:right;font-size:11px;font-family:Tahoma;font-weight:bold}"><a href="/spine/user/login">Already registered?</a></span>
    </div>	
  </div>  
  <!-- END : CONTAINER -->
</body>
</html>