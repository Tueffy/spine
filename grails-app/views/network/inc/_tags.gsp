<ul class="tags" id="${networkedUser.user.email}_tags">
	<g:each in="${networkedUser.user.tags}" var="tag">
		<li tag="${tag.key}" nb="${tag.value}" class="tag<g:if test="${networkedUser.isDirectTag(tag.key)}"> direct_tag</g:if>"> 
			<g:link controller="network" action="index" params="[filter: tag.key]">${tag.key}</g:link>
			<g:if test="${networkedUser.isDirectTag(tag.key)}">
				<a href="#" class="remove_tag">-</a>
			</g:if> <g:else>
				<a href="#" class="add_tag">+</a>
			</g:else>
		</li>
	</g:each>
</ul>

<g:form action="addTag" controller="network" class="add_tag">
	<input name="email" type="hidden" value="${networkedUser.user.email}">
	<input name="tag" type="text" class="autocomplete_tags"> 
	<input type="submit" value="+">
</g:form>
