<div class="network_page">
	<g:each in="${neighbours}" var="n">
	          	  
         <!-- BEGIN : 1 person -->
         <div class="grid_14 alpha omega contact" id="${n.email}" >
         
	        <script>
	
	         	var mydrag = new Draggable('${n.email}', { revert: true });
	
		        Droppables.add('${n.email}', { 
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
	         
			<div class="grid_3 alpha picture"><img src="/spine/images/profiles/${n.email}.jpg" alt="${n.firstName}" width="75" height="75" class="avatar" /></div>
			
			
           <div class="grid_10 description omega">
             <ul class="badges">
               <li><img src="/spine/images/badges/html.png" width="36" height="35" alt="HTML"></li>
               <li><img src="/spine/images/badges/html.png" width="36" height="35" alt="HTML"></li>
             </ul>
            
             <h3><g:remoteLink action="getUser" id="${n.email}" update="foo" onSuccess="updateSelectedUser(e)">${n.firstName} ${n.lastName}</g:remoteLink></h3>
             <p class="company">
             	<g:if test="${n?.company}">${n.company}</g:if>,  
             	${n.city}, 
             	${n.country}
             <br> </p>
             
             <div class="grid_7 alpha">
               	<p class="quote">„Looking forward to new challenges„</p>
               	<ul class="tags" id="${n.email}_tags">               		
               		<g:each in="${n.tags}" var="t">    	        	  
	                    <li id="${n.email}_${t.key}">	                    
		                    <a href="#" onmouseover="javascript:tagsMinusOnMouseOver('${n.email}_${t.key}_minus');" onmouseout="javascript:tagsMinusOnMouseOut('${n.email}_${t.key}_minus');">${t.key}</a>
		                    <span id="${n.email}_${t.key}_minus">
		                    	<g:remoteLink action="removeTag" id="${t.key}" params="[user:n.email]" onComplete="removeTagUpdate('${n.email}_${t.key}')">-</g:remoteLink>
		                    </span>
	                    </li>
                   	</g:each>                 
                   	<li>&nbsp;</li>
                   	<li> 	                    	
                    	<span class="plus">
                    		<g:form action="addTag" controller="network"  >
                    			<input name="email" type="hidden" value="${n.email}"/>
						        <input name="tag" type="text" /> <a href="#"><g:submitToRemote  action="addTag" controller="network" value="+" onComplete="addTagUpdate(e,'${n.email}_tags')"/></a>
							</g:form>
                    	</span>
                 	</li>
               	</ul>
             </div>
             <div class="grid_3 omega">
             	<p class="distance">${n.distance}</p>
               <div class="distance_arrows"></div>
             </div>
           </div>
         </div>
         <div class="grid_14 alpha omega"><div class="separator"></div></div>
         <!-- END : 1 personne -->
        </g:each>
</div>