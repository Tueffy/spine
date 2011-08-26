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
<g:link action="linkProperties" controller="network">Show available properties</g:link><br>
<g:link url="${resource(dir:'network', file:'importGraph.gsp')}">Import Graph</g:link> 
<br>
<g:link action="exportGraph" controller="graph">Export Graph to stdout</g:link><br>

<g:form name="filterGraph" method="post" action="filterGraph">
Node: <input name="filterProperty" type="text"/> <br>
<input class="calculate" type="submit" value="Filter graph" />
</g:form>

<h1>Filtered graph</h1>
${param}
<br>
<div id="chart"></div>
<g:javascript>var filterString='${param}'</g:javascript>
<g:javascript src="force.js" />
</div>

</body>
</html>