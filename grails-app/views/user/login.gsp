<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  <title>Spine</title>
  <link rel="icon" type="image/gif" href="/spine/images/spinefavicon.gif" />
  <link href="/spine/css/reset.css" rel="stylesheet" type="text/css">
  <link href="/spine/css/960.css" rel="stylesheet" type="text/css">
  <link href="/spine/css/design.css" rel="stylesheet" type="text/css">
  <link href="/spine/css/landing.css" rel="stylesheet" type="text/css">
  
	<g:javascript src="jquery/jquery-1.7.min.js" />
	<g:javascript src="jquery/jquery-ui-1.8.18.custom.min.js" />
	<g:javascript>jQuery.noConflict();</g:javascript>
	<g:javascript src="main.js" />
	<g:javascript library='scriptaculous' />
	<script type="text/javascript" src="/spine/js/app/landing.js"></script>
	<script type="text/javascript">landing.loginForm.init();</script>
	
</head>
<body>
  <div id="header">
    	<g:render template="/inc/header"></g:render>
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
    	<h2 style="font-size:36pt;color:#000000">Login and do not lose time connecting...</h2>
        <g:if test="flash['message']">
			${flash['message']}
		</g:if>
		<br/>
		<br/>
		<g:form action="doLogin" method="post" class="login">
			<div class="dialog">
				<table class="userForm">
					<tr class='prop'>
						<td valign='top' style='text-align: left;' width='80%'>
							<input id="email" type='text' name='email' value='${user?.email}' default="Email" />
						</td>
					</tr>
					<tr class='prop'>
						<td valign='top' style='text-align: left;' width='80%'>
							<input id="password" type='password' name='password' value='${user?.password}' default="Password" />
							<div class="buttons" style="text-align:right;margin-right:50px">
								<span class="formButton"> 
									<input type="submit" value="Login">
								</span>
							</div>
						</td>
					</tr>
				</table>
			</div>
		</g:form>
		<span style="{align:right;font-size:11px;font-family:Tahoma;font-weight:bold}"><a href="/spine/user/forgot">Forgot your password?</a></span>
    </div>	
  </div>  
  <!-- END : CONTAINER -->
</body>
</html>