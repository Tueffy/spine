<html>
<head>
  	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  	<title>My Spine</title>
  	<link href="/spine/css/reset.css" rel="stylesheet" type="text/css">
  	<link href="/spine/css/960.css" rel="stylesheet" type="text/css">
  	<link href="/spine/css/design.css" rel="stylesheet" type="text/css">
  	<link href="/spine/css/landing.css" rel="stylesheet" type="text/css">
  	<link href="/spine/css/ajax.css" rel="stylesheet" type="text/css">  
  	<g:javascript src="jquery/jquery-1.7.min.js" />
  	<g:javascript>
  		jQuery.noConflict();
  	</g:javascript>
  	<g:javascript src="main.js" />
  	<g:javascript library='scriptaculous' />
  	<g:javascript src="app/scroll.js" />
  	<g:javascript></g:javascript>
</head>
<body>
  <div id="header">
  	<div class="container_24">
      <span>
      		<img src="/spine/images/home/logo.png" alt="Spine" width="222" height="61" class="logo" />
      </span>
      <span>
	      <ul class="links">
	      	<li><a href="/spine/about">About Spine</a></li>
	      	<li><a href="/spine/how">How it works</a></li>
	      	<li><a href="/spine/terms">Terms and conditions</a></li>
	      	<li><a href="/spine/disclaimer">Disclaimer</a></li>
	        <li><g:link controller="user" onclick="return confirm('Are you sure?');" action="doLogout">Logout</g:link ></li>
	      </ul>
      </span>      
    </div>
  </div>
  <div id="nav">
    <div class="container_24" id="hot_tags"></div>
  </div>  
  <!-- BEGIN : container -->
  <div id="container" class="container_24">  	   
  	<div class="grid_21 landing" id="left">
      <h2>How it works</h2> 
    </div>
  	<br/>
    <!-- START : LEFT menu -->
    <div class="grid_5" id="left">
      T
    </div>
    <!-- END : LEFT menu -->    
    <!-- BEGIN : RIGHT column -->
    <div class="grid_19" id="right" style="color:white">  
    	<!-- BEGIN : Feed & Details blocks -->
      	<div class="grid_19 alpha feed">
    		Content
    	</div>
    </div>
    <!-- END : Right column -->    
  </div>
  <!-- END : container -->  
</body>
</html>