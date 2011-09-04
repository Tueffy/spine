<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="layout" content="main" />
<title>Import Social Graph</title>
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
	</div>
</body>
</html>