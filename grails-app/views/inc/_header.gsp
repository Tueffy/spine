<div class="container_24">
      <span>
      		<g:if test="${session.user != $null}">
      			<a href="/spine/network/index"><img src="/spine/images/home/logo.png" alt="Spine" class="logo" /></a>
      		</g:if>
      		<g:else>
      			<a href="/spine"><img src="/spine/images/home/logo.png" alt="Spine" class="logo" /></a>
      		</g:else>     		
      </span>
      <span>
	      <ul class="links">
	      	<li><a href="/spine/network/index">Home</a></li>
	      	<li><a href="/spine/about">About Spine</a></li>
	      	<li><a href="/spine/how">How it works</a></li>
	      	<li><a href="/spine/feedback">Feedback</a></li>
	      	<li><a href="/spine/disclaimer">Disclaimer</a></li>
	      	<!-- BEGIN : CHECK USER FOR SESSION LOGGED IN -->
	      	<g:if test="${session.user != $null}">
     			<li><g:link controller="user" action="doLogout">Logout</g:link ></li>
			</g:if>
			<g:else env="development">
			   <li><a href="/spine/user/login">Login</a></li>
			</g:else>
			<!-- END : CHECK USER FOR SESSION LOGGED IN -->
	      </ul>
      </span> 

</div>