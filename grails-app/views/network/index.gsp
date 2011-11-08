<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="layout" content="main" />
<title>spine</title>
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

	<g:link action="about" controller="home">About spine</g:link> . 
	<g:link action="contact" controller="home">Contact us</g:link> . 
	Current User: ${user.firstName} ${user.lastName}
	<g:link action="doLogout" controller="user">Logout</g:link>
	
    <h1>spine. the backbone of your knowledge organisation</h1>

	<table>
	<tr>
		<th>${user.firstName}'s profile</th>
		<th>${user.firstName}'s spine</th>
		<th>placeholder</th>
	</tr>
	<tr>
		<td>
			<img src="/spine/images/profiles/${user.email}.jpg" alt="${user.firstName}" width="100" height="150" /><br>
			${user.email}<br>
			${user.city}<br>
			${user.country}<br>
			here we need to get all tags for the user
		</td>
		<td>
		<g:form name="filterByTag" method="post" action="index">
Filter ${user.firstName}'s Spine: <input name="filter" type="text" id="autocomplete"/>
			<input class="calculate" type="submit" value="Filter" />
		</g:form>
		<div id="autocomplete_choices" class="autocomplete"></div>
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
		</td>
		<td>
		here area for tag cloud, news, banners, etc.
		</td>
		</tr>
		</table>
		<!--  
		 <div id="chart"></div>
		<g:javascript>var filterString='${param}'</g:javascript>
		<g:javascript>var userID='${user.name}'</g:javascript>
		<g:javascript src="force.js" />
	</div>-->
</body>
</html>
