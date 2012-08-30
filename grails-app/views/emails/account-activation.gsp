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
			<td height="500" valign="top" class="cellMargin"> <br/><h1>Welcome expert!</h1>
			<h2>You have just joined Spine, your social knowledge tool.</h2><br/><br/>
			<div class="callToAction">Follow the link below to confirm your subscription to Spine.</div>
			<div class="link"><a href="${grailsApplication.config.grails.serverURL}/user/activate/${id}">Activation link</a> for ${id}</div><br/><br/><div class="text">
			If you received this email by mistake, please delete it. <br/>
			
			<br/>
			For questions about this service, please contact: <a href="mailto:team@spine-it.com">team@spine-it.com<br/></div>		
		</td>
		</tr>
	</table>
</body>
</html>