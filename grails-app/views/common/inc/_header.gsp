<div class="container_24">
      <span>
      		<a href="/spine"><img src="/spine/images/home/logo.png" alt="Spine" width="222" height="61" class="logo" /></a>
      </span>
      <span>
	      <ul class="links">
	      	<li><a href="/spine/about">About Spine</a></li>
	      	<li><a href="/spine/how">How it works</a></li>
	      	<li><a href="/spine/terms">Terms and conditions</a></li>
	      	<li><a href="/spine/disclaimer">Disclaimer</a></li>	      	
	      	<!-- BEGIN : CHECK USER FOR SESSION LOGGED IN -->
	      	<g:if test="${session.user != $null}">
	      	 	<li><a href="/spine/network">My Network</a></li>
     			<li><g:link controller="user" onclick="return confirm('Are you sure?');" action="doLogout">Logout</g:link ></li>
			</g:if>
			<g:else env="development">
			   <li><a href="/spine/user/login">Login</a></li>
			</g:else>
			<!-- END : CHECK USER FOR SESSION LOGGED IN -->
	      </ul>
      </span> 
</div>