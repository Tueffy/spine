<html>
<head>
	<meta name="layout" content="main">
	<title>Spine</title>
	<link href="/spine/css/landing.css" rel="stylesheet" type="text/css">
	<link rel="icon" type="image/gif" href="/spine/images/spinefavicon.gif" />
</head>
<body>
	<div class="body">

		<h3>
			User:${user}
		</h3>
		<g:link action="doLogout" controller="user">Logout</g:link>
		<br> <br>
		<br>

		<g:link action="index" controller="graph">Home</g:link>
		<br> Import connections file (format: Source \tab Target \tab
		;-separated properties)
		<g:uploadForm action="importGraph" method="post">
			<input type="file" name="edgesFile" id="edges" />
			<input type="submit" />
		</g:uploadForm>
		<br> Import nodes file
		<g:uploadForm action="importGraph" method="post">
			<input type="file" name="nodesFile" id="nodes" />
			<input type="submit" />
		</g:uploadForm>
	</div>
</body>
</html>