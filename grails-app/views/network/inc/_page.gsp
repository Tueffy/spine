<div class="network_page">
	<g:each in="${network.networkedUsers}" var="networkedUser">
	          	  
         <!-- BEGIN : 1 person -->
         <div class="grid_14 alpha omega contact" id="${networkedUser.user.email}" >
         
	        <script>
	
	         	var mydrag = new Draggable('${networkedUser.user.email}', { revert: true });
	
		        Droppables.add('${networkedUser.user.email}', { 
				    accept: 'hot_tags',
				    hoverclass: 'hover',
				    onDrop: function(dragged, dropped, event) { 
				    	//alert('Dragged: ' + dragged.id);
				    	//alert('Dropped onto: ' + dropped.id);
				    	//alert('Held ctrl key: ' + event.ctrlKey);			    
				    	alert(dropped.id);
				    	new Ajax.Request('/spine/network/addTag/'+dropped.id, {asynchronous:true,evalScripts:true,parameters:'e='+dragged.id});
					    	
				   	}
				});
	        </script>
	         
			<div class="grid_3 alpha picture"><img src="/spine/images/profiles/${networkedUser.user.email}.jpg" alt="${networkedUser.user.firstName}" width="75" height="75" class="avatar" /></div>
			
			
           <div class="grid_10 description omega">
           
             <!-- BADGES -->
             <ul class="badges">
             	<g:each in="${networkedUser.user.badges}" var="badge">
             		<li><img src="/spine/images/badges/36x36/${badge.image}" width="36" height="35" alt="${badge}" title="${badge}" /></li>
             	</g:each>
             </ul>
            
             <h3><g:remoteLink action="getUser" id="${networkedUser.user.email}" update="foo" onSuccess="updateSelectedUser(e)">${networkedUser.user.firstName} ${networkedUser.user.lastName}</g:remoteLink></h3>
             <p class="company">
             	<g:if test="${networkedUser.user?.company}">${networkedUser.user.company}</g:if>,  
             	${networkedUser.user.city}, 
             	${networkedUser.user.country}
             <br> </p>
             
             <div class="grid_7 alpha">
               	<p class="quote">„Looking forward to new challenges„</p>
               	<ul class="tags" id="${networkedUser.user.email}_tags">               		
               		<g:each in="${networkedUser.user.tags}" var="t">    	        	  
	                    <li id="${networkedUser.user.email}_${t.key}">	                    
		                    <a href="#" onmouseover="javascript:tagsMinusOnMouseOver('${networkedUser.user.email}_${t.key}_minus');" onmouseout="javascript:tagsMinusOnMouseOut('${networkedUser.user.email}_${t.key}_minus');">${t.key}</a>
		                    <span id="${networkedUser.user.email}_${t.key}_minus">
		                    	<g:remoteLink action="removeTag" id="${t.key}" params="[user:networkedUser.user.email]" onComplete="removeTagUpdate('${networkedUser.user.email}_${t.key}')">-</g:remoteLink>
		                    </span>
	                    </li>
                   	</g:each>                 
                   	<li>&nbsp;</li>
                   	<li> 	                    	
                    	<span class="plus">
                    		<g:form action="addTag" controller="network"  >
                    			<input name="email" type="hidden" value="${networkedUser.user.email}"/>
						        <input name="tag" type="text" class="autocomplete_tags" /> 
						        <a href="#"><g:submitToRemote  action="addTag" controller="network" value="+" onComplete="addTagUpdate(e,'${networkedUser.user.email}_tags')"/></a>
							</g:form>
                    	</span>
                 	</li>
               	</ul>
             </div>
             <div class="grid_3 omega">
             	<p class="distance">${networkedUser.distance}</p>
               <div class="distance_arrows"></div>
             </div>
           </div>
         </div>
         <div class="grid_14 alpha omega"><div class="separator"></div></div>
         <!-- END : 1 personne -->
        </g:each>
</div>