<html>
<head>
	<meta name="layout" content="main">
	<title>Spine</title>
	<link href="/spine/css/landing.css" rel="stylesheet" type="text/css">
	<link rel="icon" type="image/gif" href="/spine/images/spinefavicon.gif" />
</head>
<body>
	<div class="grid_10 omega knowledge" style="padding-top: 80px">
		<img src="${resource(dir:'images/home',file:'spine-connected.png')}"
			alt="Connected">
	</div>
	<div class="grid_14 alpha landing" style="padding-top: 40px">
		<h2 style="font-size: 36pt; color: #000000">
			Stay connected <br /> with your co-workers
		</h2>
		<p class="subtitle"
			style="font-size: 20pt; font-family: Tahoma; color: #67624e">
			Spine is the private social network <br />inside your company
		</p>
		<g:form method="post" action="doSignup" class="registration" controller="home">
		    <g:if test="flash['message']"><p>${flash['message']}</p></g:if>
			<g:if test="${home?.email}"><input type="text" name="email" value="${home?.email}" default="${home?.email}" class="auto_refill" /></g:if>
			<g:else><input type="text" name="email" value="Enter your email address" default="Enter your email address" class="auto_refill" /></g:else>
			<span style="align: right"><input type="submit"
				value="Sign up" /></span>
		</g:form>
		<br /> <span
			style="align: right; font-size: 18px; font-family: Tahoma; font-weight: bold"><a
			href="/spine/user/login">Already registered?</a></span>
	</div>
</body>
</html>