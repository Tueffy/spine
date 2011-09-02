<html>
<head>
 <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
 <meta name="layout" content="main" />
 <title>User Login</title>
</head>
<body>
<div class="body">
 <g:form action="doRegister" method="post">
 <div class="dialog">
 <p>Enter your login details below:</p>
 <table class="userForm">
 <tr class='prop'>
  <td valign='top' style='text-align:left;' width='20%'>
  <label for='name'>Name:</label>
 </td>
 <td valign='top' style='text-align:left;' width='80%'>
 <input id="name" type='text' name='name' value='${user?.name}' />
 </td>
 </tr>
  <tr class='prop'>
  <td valign='top' style='text-align:left;' width='20%'>
  <label for='email'>Email:</label>
 </td>
 <td valign='top' style='text-align:left;' width='80%'>
 <input id="email" type='text' name='email' value='${user?.email}' />
 </td>
 </tr>
 <tr class='prop'>
 <td valign='top' style='text-align:left;' width='20%'>
 <label for='password'>Password:</label>
 </td>
 <td valign='top' style='text-align:left;' width='80%'>
 <input id="password" type='password' name='password' value='${user?.password}' />
 </td>
 </tr>
</table>
</div>
<div class="buttons">
 <span class="formButton">
 <input type="submit" value="Register"></input>
 </span>
</div>
</g:form>
</div>
</body>
</html>