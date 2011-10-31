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
		<g:link action="index" controller="network">Show Graph</g:link>
		<br> <br>

		<h1>Connect people</h1>
		<div class="body">

			<h3>
				User:${user}
			</h3>
			<g:link action="doLogout" controller="user">Logout</g:link>
			<br> <br>
			<br>

			<g:form action="connectPeople" method="post">
			Start person: <input type="text" name="sourcePerson"
					id="autocomplete" />
				<br>
			Target person: <input type="text" name="targetPerson"
					id="targetPerson" />
				<br>
			Properties(';'-separated): <input type="text" name="linkProps"
					id="linkProps" />
				<br>
				<input type="submit" />
				<%-- supporting div to display potential matches.--%>
			</g:form>
			
		<h1>Disconnect people</h1>
			<g:form action="connectPeople" method="post">
			Start person: <input type="text" name="sourcePerson2" id="autocomplete" /><br>
			Target person: <input type="text" name="targetPerson2" id="targetPerson2" /><br>
			<input type="submit" />
			</g:form>	
			
			
			<div id="autocomplete_choices" class="autocomplete"></div>
		</div>


		${param}
	
</body>
</html>