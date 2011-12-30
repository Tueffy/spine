<%@ page contentType="text/html"%>
<html>
<head>
<style>
body { font-family:Tahoma; 
background-color:#f0ebd5;
margin:0;}

.cellMargin{padding-left:40px;}

h1 {font-size:350%; margin-bottom:0px;}

.callToAction{
font-size:18px; 
font-weight:bold;
}


.callToAction A:link {text-decoration: none; color:#000000;}
.callToAction A:visited {text-decoration: none ; color:#000000; }
.callToAction A:active {text-decoration: none ; color:#000000;}
.callToAction A:hover {text-decoration: none; color:#7cd6f8;}


.link{
font-size:10px; 
}

.link A:link {text-decoration: none; color:#000000;}
.link A:visited {text-decoration: none ; color:#000000; }
.link A:active {text-decoration: none ; color:#000000;}
.link A:hover {text-decoration: none; color:#7cd6f8;}

.text{font-size:12px; }
</style>
</head>

<body>
	<table width="100%" border="0" cellspacing="0">
		<tr>
			<td height="120" bgcolor="#7cd6f8" class="cellMargin">
				<a href="#" target="blank"> 	
					<img src="spine-logo.gif" width="235" height="80" border="0">
				</a>
			</td>
		</tr>
		<tr border="0">
			<td  height="30" bgcolor="#a0dded" ></td>
		</tr>
		<tr>
			<td height="500" valign="top" class="cellMargin"> <br/><h1>Congrats!</h1>
			<h2>You have just joined Spine, <br/>
			 your private social network <br/>
			 for your company.</h2><br/><br/>
			<div class="callToAction">Follow the link below to confirm your subscription to spine.</div>
			<div class="link"><a href="#" target=blank> http://localhost/spine/user/activate/${id} 
			</a></div><br/><br/><div class="text">
			If you received this email by mistake, simply delete it. <br/>
			You won't be subscribed if you don't click the confirmation link above.<br/>
			<br/>
			For questions about this service, please contact: <a href="mailto:contact@spine.de">contact@spine.de<br/></div>		
		</td>
		</tr>
	</table>
</body>
</html>