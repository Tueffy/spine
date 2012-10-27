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
			Send us feedback
		</h2>		
		<p class="subtitle"
			style="font-size: 20pt; font-family: Tahoma; color: #67624e">
			We want to constantly improve our services
		</p>
		<g:form method="post" action="doFeedback" class="feedback" controller="home">
	
			<input type="text" name="subject" value="Enter the subject" default="Enter the subject" class="auto_refill" />
			<textarea name="message" cols="50" rows="5">Dear SPINE team, ...</textarea>
			<input type="text" name="email" value="Enter your email address" default="Enter your email address" class="auto_refill" />
			<span style="align: right"><input type="submit" value="Send" /></span>
		</g:form>
	</div>
</body>
</html>