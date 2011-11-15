<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  <title>Login</title>
  <link href="/spine/css/reset.css" rel="stylesheet" type="text/css">
  <link href="/spine/css/960.css" rel="stylesheet" type="text/css">
  <link href="/spine/css/design.css" rel="stylesheet" type="text/css">
  <link href="/spine/css/landing.css" rel="stylesheet" type="text/css">
</head>

<body>
  <div id="header">
    <div class="container_24">
      <img src="/spine/images/home/logo.png" alt="Spine" width="222" height="61" class="logo" />
    
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
        <p class="subtitle">Register yourself to join Spine!</p>
        <g:if test="flash['message']">
			${flash['message']}
		</g:if>
		<g:form action="doRegister" method="post">
			<div class="dialog">
				<table class="userForm">
					<tr class='prop'>
						<td valign='top' style='text-align: left;' width='20%'>
							<label for='lastName'>Last Name:</label>
						</td>
						<td valign='top' style='text-align: left;' width='80%'>
							<input id="lastName" type='text' name='lastName' value='${user?.lastName}' />
						</td>
					</tr>
					<tr class='prop'>
						<td valign='top' style='text-align: left;' width='20%'>
							<label for='firstName'>First Name:</label>
						</td>
						<td valign='top' style='text-align: left;' width='80%'>
							<input id="firstName" type='text' name='firstName' value='${user?.firstName}' />
						</td>
					</tr>
					<tr class='prop'>
						<td valign='top' style='text-align: left;' width='20%'>
							<label for='email'>Email:</label>
					</td>
						<td valign='top' style='text-align: left;' width='80%'>
							<input id="email" type='text' name='email' value='${tmp_email}' />
						</td>
					</tr>
					<tr class='prop'>
						<td valign='top' style='text-align: left;' width='20%'>
							<label for='country'>Country:</label>
						</td>
						<td valign='top' style='text-align: left;' width='80%'>
							<input id="country" type='text' name='country' value='${user?.country}' />
						</td>
					</tr>
					<tr class='prop'>
						<td valign='top' style='text-align: left;' width='20%'>
							<label for='city'>City:</label>
						</td>
						<td valign='top' style='text-align: left;' width='80%'>
							<input id="city" type='text' name='city' value='${user?.city}' />
						</td>
					</tr>
					<tr class='prop'>
						<td valign='top' style='text-align: left;' width='20%'>
						<label for='password'>Password:</label>
					</td>
						<td valign='top' style='text-align: left;' width='80%'>
							<input id="password" type='password' name='password' value='${user?.password}' />
						</td>
					</tr>
				</table>
			</div>
			<div class="buttons">
				<span class="formButton"> <input type="submit"
					value="Register"></input> </span>
			</div>
		</g:form>
    </div>
    <div class="grid_10 omega knowledge"><img src="${resource(dir:'images/home',file:'knowledge.png')}" width="399" height="299" alt="Knowledge"></div>
  </div>
  <!-- END : container -->
  
</body>
</html>