<html>
<head>
  	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  	<title>My Spine</title>
  	<link href="${resource(dir:'css',file:'reset.css')}" rel="stylesheet" type="text/css" />
  	<link href="${resource(dir:'css',file:'960.css')}" rel="stylesheet" type="text/css" />
  	<link href="${resource(dir:'css',file:'design.css')}" rel="stylesheet" type="text/css" />
  	<link href="${resource(dir:'css',file:'ajax.css')}" rel="stylesheet" type="text/css" />
  
  	<g:javascript src="jquery/jquery-1.7.min.js" />
  	<g:javascript>
  		jQuery.noConflict();
  	</g:javascript>
  	<g:javascript src="main.js" />
  	<g:javascript library='scriptaculous' />
  	<g:layoutHead />
</head>

<body>
  <div id="header">
  	<div class="container_24">
      <span>
      		<img src="/spine/images/home/logo.png" alt="Spine" width="222" height="61" class="logo" />
      </span>
      <ul class="links">
	      <g:if test="${session.user.email}">
	      	<li><g:link controller="network" action="index">My Network</g:link ></li>
	      	<li><g:link controller="user" action="profile">Profile</g:link></li>
	      	<li><a href="#">About Spine</a></li>
	        <li><g:link controller="user"  action="doLogout">Logout</g:link ></li>
	      </g:if>
	      <g:else>
	      	<li><g:link controller="user" action="login">Login</g:link ></li>
	      </g:else>
      </ul>
    	     
      <p class="news">
          <img src="/spine/images/home/bubble.png" alt="Bubble" width="42" height="39" class="bubble" />
          <span id="message">You've got 7 new tags and 1 new badge.</span>
      </p>       
    </div>
  </div>
  
  <div id="nav">
    <div class="container_24" id="hot_tags">
    
      <ul >
        <li><img src="/spine/images/home/hot_tags.png" width="75" height="23" alt="Hot Tags" ></li>
        <li class="hot_tags" id="hot_tags_soap"><a href="#">#soap</a></li>
        <li><a href="#">#cloud</a></li>
        <li><a href="#">#html</a></li>
        <li><a href="#">#xhtml</a></li>
        <li><a href="#">#java</a></li>
      </ul>
      <script>var mydrag = new Draggable('hot_tags_soap', { revert: true });</script>
    </div>
  </div>
  
  <!-- BEGIN : container -->
  <div id="container" class="container_24">
  	<g:layoutBody />
  </div>
  <!-- END : container -->
  
	
  
</body>

</html>