<html>
<head>
  	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  	<title>My Spine</title>
  	<link href="/spine/css/reset.css" rel="stylesheet" type="text/css">
  	<link href="/spine/css/960.css" rel="stylesheet" type="text/css">
  	<link href="/spine/css/design.css" rel="stylesheet" type="text/css">
  	<link href="/spine/css/ajax.css" rel="stylesheet" type="text/css">  
  	<g:javascript src="jquery/jquery-1.7.min.js" />
  	<g:javascript>
  		jQuery.noConflict();
  	</g:javascript>
  	<g:javascript src="main.js" />
  	<g:javascript library='scriptaculous' />
  	<g:javascript src="app/scroll.js" />
  	<g:javascript>
  			window.onload = function(){
                
                  new Ajax.Autocompleter("autocomplete", "autocomplete_choices", "/spine/network/ajaxAutoComplete",{});
                                   
                  Droppables.add('left', { 
				    accept: 'contact',
				    hoverclass: 'hover',
				    onDrop: function(e) { 
				    		//alert(e.id);				    
				    		window.location = "index?user="+e.id;
				    			
				   		}
				  });
								  
				  /*				  
				  new Ajax.Request('/spine/network/getUserStatistics/${user.email}', {
    				asynchronous:true,
    				evalScripts:true,
    				onSuccess: function(transport) {
   						var tagsJSON = transport.responseText;
   						var tags = eval("("+tagsJSON+")");		
										   
					    for (var key in tags) {						   
			    		   $(key).innerHTML = tags[key];
				   		}			    		  
				   		
					}
				  });		
				*/
			}
			
			var firstUser = null;
			
			getFirstUser = function(){
				return firstUser;
			}
			
			setFirstUser = function(user){
				firstUser = user;
			}
        	
        	updateSelectedUser = function (e){
        		//alert(e);
        		// evaluate the JSON
    			var user = eval("("+e.responseText+")");
    			$("selectUser").fade();
    			$("selectedUserName").innerHTML = user.firstName +' ' + user.lastName;
    			$("selectedCity").innerHTML = user.city;
    			//$("selectedCountry").innerHTML = user.country;
    			$("selectedImage").appear();
    			$("selectedImage").src = "/spine/images/profiles/"+ user.email + ".jpg";
    			      			
    			var container = $("selectedTags");
							
				var liList = container.childNodes;
				
				for(var i = 0;i < liList.length;i++){	
					//alert (liList[i+1].nodeName);	
					var li = liList[i];
					if(li.nodeName == "LI"){
						$(li).fade();
					}
    			}
    			    			
    			new Ajax.Request('/spine/network/getTags/'+ user.email, {
    				asynchronous:true,
    				evalScripts:true,
    				onSuccess: function(transport) {
   						var tagsJSON = transport.responseText;
   						var tags = eval("("+tagsJSON+")");		
						for (var key in tags) {						   
						   var new_element = document.createElement('li');
						   new_element.innerHTML = "#" + key;	
						   container.insertBefore(new_element, container.firstchild);
			    		   $(new_element).grow();
				   		}
					}
    			});
    			
    			
    			
			}
			
			
			tagsMinusOnMouseOver = function(e){
				//alert(e);
				$(e).appear();
				$(e).onmouseout = function(){
					$(this).fade();
				}
				return false;
			}
			
			
			tagsMinusOnMouseOut = function(e){
				//alert("test");
				$(e).fade({ duration: 7.0});
				return false;
			}
					
			tagsPlusOnMouseOver = function(){
				//alert("test");
				$('minus').appear(5); 
				
				return false;
			}
			
			
			tagsPlusOnMouseOut = function(){
				//alert("test");
				$('minus').fade();
				return false;
			}			
			
			
			inviteNewUser = function (e) {
				alert("dd");
			}
			
    </g:javascript>
</head>
<body>
  <!-- BEGIN : HEADER -->
  <div id="header">
  	<div class="container_24">
      <g:render template="/inc/header"></g:render>
      <p class="news">
          <img src="/spine/images/home/bubble.png" alt="Bubble" width="42" height="39" class="bubble" />
          <span id="message">You've got 7 new tags and 1 new badge.</span>
      </p>       
    </div>
  </div>  
  <!-- END : HEADER -->
  <!-- BEGIN : NAV -->
  <div id="nav">
    <div id="hot_tags" class="container_24">    
      <ul>
        <li><img src="/spine/images/home/hot_tags.png" width="75" height="23" alt="Hot Tags" ></li>
        <g:each in="${hotTags}" var="t" >    	
	        <li class="hot_tags" id="hot_tags_soap"><a href="#">#${t}</a></li>
	    </g:each>
      </ul>
      <script>var mydrag = new Draggable('hot_tags_soap', { revert: true });</script>
    </div>
  </div>
  <!-- END : NAV -->
  <!-- BEGIN : CONTAINER -->
  <div id="container" class="container_24">
    <!-- START : LEFT MENU -->
    <div class="grid_5" id="left">
      <img src="/spine/images/profiles/${user.email}.jpg" alt="${user.firstName}" width="100" height="150" class="avatar" style="margin-left:45px"/>     
	  <ul class="description"  style="margin-left:45px">
          <li class="name"><a href="../user/profile">${user.firstName} ${user.lastName}</a></li>
          <li class="company">${user.company}</li>
          <li class="city">${user.country}</li>
	  </ul>      
      <div class="badges"  style="margin-left:35px">
        <ul>
          <li><img src="/spine/images/badges/html.png" width="37" height="38" alt="HTML"></li>
          <li><img src="/spine/images/badges/html.png" width="37" height="38" alt="HTML"></li>
          <li><img src="/spine/images/badges/html.png" width="37" height="38" alt="HTML"></li>
          <li><img src="/spine/images/badges/html.png" width="37" height="38" alt="HTML"></li>
          <li><img src="/spine/images/badges/html.png" width="37" height="38" alt="HTML"></li>
          <li><img src="/spine/images/badges/html.png" width="37" height="38" alt="HTML"></li>
        </ul>
      </div>
      <ul class="menu">
        <li><a href="#"><span id="badgesNumber">${user.badges.size()}</span> Badges</a></li> 
        <li><a href="#"><span id="tagsNumber">${user.tags.size()}</span> Tags</a></li>
      </ul>   
      <br/>   
      <p>
         Invite new user
         <form>
         	Email: <input type="text" id="inviteUserMail"/>
         	<g:remoteLink action="inviteNewUser" id="invite" update="foo" onSuccess="inviteNewUser(e)">Invite</g:remoteLink>
         </form>
      </p>
    </div>
    <!-- END : LEFT MENU -->    
    <!-- BEGIN : RIGHT COLUMN -->
    <div class="grid_19" id="right">    
      <!-- BEGIN : Feed & Details blocks -->
      <div class="grid_19 feed separator">
      <!-- BEGIN : Feed block -->
      <div class="grid_14 alpha">
          <!-- BEGIN : filter & my updates -->
          <div class="grid_10 alpha filter">
            <g:form name="filterByTag" method="post" action="index">
              <input type="text" name="filter" id="autocomplete" value="Filter ${user.firstName}'s spine" default="Filter Christian‘s spine" onClick="javascript:this.value='';" />
              <div id="autocomplete_choices" class="autocomplete"></div>              
              <ul class="filter_list">
                <li>Filter by</li>
                <li><a href="#">Expertise</a></li>
                <li><a href="#">Distances</a></li>
            </ul>
            </g:form>            
          </div>
          <div class="grid_4 omega my_updates" id="test1">
            <a href="#"><img src="/spine/images/home/my_updates.png" width="60" height="54" alt="Update box"></a>
            <p><a href="#">My Spine Updates</a></p>
          </div>
           <!-- END : filter & my updates -->          
         
          <!--  BEGIN Flux -->
          <g:render template="page"></g:render>
          <!-- END Flux -->
          
        </div>
        <!-- END : Feed block -->
        
        <!-- BEGIN : Details block -->
        <div class="grid_5 omega details_panel" >          
          <!-- <img src="/spine/images/avatar2.jpg" alt="Avatar" class="avatar" />  -->
          <img src="/spine/images/profiles/anonymous.gif"" alt="anonymous" width="100" height="150" class="avatar" id="selectedImage" style="margin-left:45px"/>
     	  <span id="selectUser" style="font-size:9pt">Please choose one of your contacts to see more details!</span>
          <ul class="description">
            <li class="name" id="selectedUserName"></li>
            <li class="company" id="selectedCompany"></li>
            <li class="city" id="selectedCity"></li>
          </ul>          
          <ul class="tags" id="selectedTags">
           
          </ul>          
          <p class="all_tags"><a href="#">All tags</a></p>
        </div> 
        <!-- END : Details block -->
      </div>
      <!-- END : Feed & Details blocks -->  
      <div class="grid_19 alpha omega close">
        &nbsp;
      </div>      
      <!-- BEGIN : Copyright -->
      <!-- > -->
      <div class="grid_19 alpha omega copyright">
        <div class="grid_5 omega">&copy; Spine 2011 - All rights reserved</div>
      </div>
      <!-- END : Copyright -->
      
    </div>
    <!-- END : Right column -->
    
  </div>
  <!-- END : container -->  
</body>
</html>