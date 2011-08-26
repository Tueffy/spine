<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<meta name="layout" content="main" />
<title>Social Graph</title>
<g:javascript src="d3.js" />
<g:javascript src="d3.geom.js" />
<g:javascript src="d3.layout.js" />

</head>
<body>
<div class="body">
<g:link action="index" controller="network">Show Graph</g:link><br>
<br>

<h1>All properties</h1>
<g:each in="${param}" var="p">
<g:link action="index" params="['filter':p]">${p}</g:link><br>
</g:each>


</body>
</html>