<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="layout" content="main" />
<title>Social Graph</title>
<g:javascript library='scriptaculous' />
<g:javascript>
window.onload = function()
                {
                  new Ajax.Autocompleter("autocomplete",
                                         "autocomplete_choices",
                                         "/spine/network/ajaxAutoComplete",
                                         {}
                                        );
                }
</g:javascript>


</head>
<body>
	<div class="body">

		<h3>
			User:
			${user.lastName}
			<br> Email:
			${user.email}
		</h3>
		<g:link action="doLogout" controller="user">Logout</g:link>
		<br> <br> <br>

		<g:link action="linkProperties" controller="network">Show available properties</g:link>
		<br>
		<g:link action="connectPeople" controller="network">Connect people</g:link>
		<br>
		<g:link url="${resource(dir:'network', file:'importGraph.gsp')}">Import Graph</g:link>
		<br>
		<g:link action="exportGraph" controller="graph">Export Graph to stdout</g:link>
		<br>

		<g:form name="filterGraph" method="post" action="filterGraph">
Node: <input name="filterProperty" type="text" />
			<br>
			<input class="calculate" type="submit" value="Filter graph" />
		</g:form>

		<g:form name="filterByTag" method="post" action="index">
Filter ${user.firstName}'s Spine: <input name="filter" type="text" id="autocomplete"/>
			<input class="calculate" type="submit" value="Filter" />
		</g:form>
		<div id="autocomplete_choices" class="autocomplete"></div>
		<h1>
			${user.firstName}'s Spine ${param}
		</h1>
		<br>
		<table>
			<tr>
				<td>eMail</td>
				<td>distance</td>
			</tr>
			<g:each in="${neighbours}" var="n">
				<tr>
					<td>
						${n.key}
					</td>
					<td>
						${n.value}
					</td>
				</tr>
			</g:each>
		</table>
		<!--  
		 <div id="chart"></div>
		<g:javascript>var filterString='${param}'</g:javascript>
		<g:javascript>var userID='${user.name}'</g:javascript>
		<g:javascript src="force.js" />
	</div>-->
</body>
</html>