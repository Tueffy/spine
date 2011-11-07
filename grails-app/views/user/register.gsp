<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="layout" content="main" />
<title>User Login</title>
</head>
<body>
	<div class="body">
	
	<g:link action="about" controller="home">About spine</g:link> . 
	<g:link action="contact" controller="home">Contact us</g:link> . 
	<g:link action="login" controller="user">Login</g:link><br>
    <h1>spine. the backbone of your knowledge organisation</h1>
	
		<g:if test="flash['message']">
			${flash['message']}
		</g:if>
		<g:form action="doRegister" method="post">
			<div class="dialog">
				<p>Enter your details below:</p>
				<table class="userForm">
					<tr class='prop'>
						<td valign='top' style='text-align: left;' width='20%'><label
							for='lastname'>Last Name:</label></td>
						<td valign='top' style='text-align: left;' width='80%'><input
							id="lastname" type='text' name='lastname' value='${user?.lastname}' /></td>
					</tr>
					<tr class='prop'>
						<td valign='top' style='text-align: left;' width='20%'><label
							for='firstname'>First Name:</label></td>
						<td valign='top' style='text-align: left;' width='80%'><input
							id="firstname" type='text' name='firstname' value='${user?.firstname}' /></td>
					</tr>
					<tr class='prop'>
						<td valign='top' style='text-align: left;' width='20%'><label
							for='email'>Email:</label></td>
						<td valign='top' style='text-align: left;' width='80%'><input
							id="email" type='text' name='email' value='${tmp_email}' /></td>
					</tr>
					<tr class='prop'>
						<td valign='top' style='text-align: left;' width='20%'><label
							for='country'>Country:</label></td>
						<td valign='top' style='text-align: left;' width='80%'><input
							id="country" type='text' name='country' value='${user?.country}' /></td>
					</tr>
					<tr class='prop'>
						<td valign='top' style='text-align: left;' width='20%'><label
							for='city'>City:</label></td>
						<td valign='top' style='text-align: left;' width='80%'><input
							id="city" type='text' name='city' value='${user?.city}' /></td>
					</tr>
					<tr class='prop'>
						<td valign='top' style='text-align: left;' width='20%'><label
							for='password'>Password:</label></td>
						<td valign='top' style='text-align: left;' width='80%'><input
							id="password" type='password' name='password'
							value='${user?.password}' /></td>
					</tr>
				</table>
			</div>
			<div class="buttons">
				<span class="formButton"> <input type="submit"
					value="Register"></input> </span>
			</div>
		</g:form>
	</div>
</body>
</html>