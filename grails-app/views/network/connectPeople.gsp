<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<meta name="layout" content="main" />
<title>Social Graph</title>

</head>
<body>
<div class="body">
<g:link action="index" controller="network">Show Graph</g:link><br>
<br>

<h1>Connect people</h1>
<div class="body">
		<g:form action="connectPeople" method="post">
			Start person: <input type="text" name="sourcePerson" id="sourcePerson" /><br>
			Target person: <input type="text" name="targetPerson" id="targetPerson" /><br>
			Properties(';'-separated): <input type="text" name="linkProps" id="linkProps" /><br>
			<input type="submit" />
		</g:form>
</div>
${param}
</body>
</html>