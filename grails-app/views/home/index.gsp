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

	<g:link action="about" controller="home">About spine</g:link> . 
	<g:link action="contact" controller="home">Contact us</g:link> . 
	<g:link action="login" controller="user">Login</g:link><br>
    <h1>spine. the backbone of your knowledge organisation</h1>

		<g:form action="doSignup" method="post" controller="home">
			<div class="dialog">
				<p>Provide organisational email address for signing up to spine:</p>
				<table class="userForm">
					<tr class='prop'>
						<td valign='top' style='text-align: left;' width='20%'><label
							for='email'>Email:</label></td>
						<td valign='top' style='text-align: left;' width='80%'><input
							id="email" type='text' name='email' value='${home?.email}' /></td>
					</tr>
				</table>
			</div>
			<div class="buttons">
				<span class="formButton"> <input type="submit"
					value="Sign Up"></input> </span>
			</div>
		</g:form>

</div>
</body>
</html>
