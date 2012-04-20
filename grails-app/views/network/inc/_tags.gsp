<ul class="tags" id="${networkedUser.user.email}_tags">
	<g:each in="${networkedUser.user.tags}" var="tag">
		<li tag="${tag.key}" nb="${tag.value}" class="tag<g:if test="${networkedUser.isDirectTag(tag.key)}"> direct_tag</g:if>"> 
			${tag.key}
			<g:if test="${networkedUser.isDirectTag(tag.key)}">
				<a href="#" class="remove_tag">-</a>
			</g:if> <g:else>
				<a href="#" class="add_tag">+</a>
			</g:else>
		</li>
	</g:each>
	
	<li>&nbsp;</li>
	<li> 	                    	
	<span class="plus">
		<g:form action="addTag" controller="network" >
			<input name="email" type="hidden" value="${networkedUser.user.email}"/>
			<input name="tag" type="text" class="autocomplete_tags" /> 
			<a href="#"><g:submitToRemote  action="addTag" controller="network" value="+" onComplete="addTagUpdate(e,'${networkedUser.user.email}_tags')"/></a>
		</g:form>
		</span>
	</li>
</ul>

