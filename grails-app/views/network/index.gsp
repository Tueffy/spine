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
				  
				
			}
        	
        	function updateSelectedUser(e) {
        		//alert(e);
        		 // evaluate the JSON
    			var user = eval("("+e.responseText+")");
    			$("selectedUserName").innerHTML = user.email;
    			//$("selectedCity").innerHTML = user.email;
    			//$("selectedCountry").innerHTML = user.email;
    			
    			$("selectedImage").src = "/spine/images/profiles/"+ user.email + ".jpg";
    			
    			var container = $("selectedTags");
							
				var liList = container.childNodes;
				
				
				for(var i = 0;i < liList.length;i++){	
					//alert (liList[i+1].nodeName);	
					var li = liList[i]
					if(li.nodeName == "LI"){
						$(li).fade();
					}
    			}
				
				for(var i = 0;i<10;i++){				
					var new_element = document.createElement('li');
					new_element.innerHTML = "#" + "test";	
					container.insertBefore(new_element, container.firstchild);
	    			$(new_element).grow();
    			}
    			
    			
			}
			
			
			function tagsMinusOnMouseOver(){
				//alert("test");
				$('minus').appear(); 
				
				return false;
			}
			
			
			function tagsMinusOnMouseOut(){
				//alert("test");
				$('minus').fade();
				return false;
			}
					
			function tagsPlusOnMouseOver(){
				//alert("test");
				$('minus').appear(); 
				
				return false;
			}
			
			
			function tagsPlusOnMouseOut(){
				//alert("test");
				$('minus').fade();
				return false;
			}
			
			
			
			
    </g:javascript>
  
</head>

<body>
  <div id="header">
  	<div class="container_24">
      <span>
      		<img src="/spine/images/home/logo.png" alt="Spine" width="222" height="61" class="logo" />
      </span>
      <span>
	      <ul class="links">
	      	<li><a href="#">About Spine</a></li>
	      	<li><a href="#">How it works</a></li>
	      	<li><a href="#">Terms and conditions</a></li>
	      	<li><a href="#">Disclaimer</a></li>
	        <li><g:link controller="user"  action="doLogout">Logout</g:link ></li>
	      </ul>
    	     
      <p class="news">
          <img src="/spine/images/home/bubble.png" alt="Bubble" width="42" height="39" class="bubble" />
          <span id="message">You've got 7 new tags and 1 new badge.</span>
      </p>       
    </div>
  </div>
  
  <div id="nav">
    <div class="container_24" id="hot_tags">
    
      <ul >
        <li><img src="/spine/images/home/hot_tags.png" width="75" height="23" alt="Hot Tags" ></li>
        <li class="hot_tags" id="hot_tags_soap"><a href="#">#soap</a></li>
        <li><a href="#">#cloud</a></li>
        <li><a href="#">#html</a></li>
        <li><a href="#">#xhtml</a></li>
        <li><a href="#">#java</a></li>
      </ul>
      <script>var mydrag = new Draggable('hot_tags_soap', { revert: true });</script>
    </div>
  </div>
  
  <!-- BEGIN : container -->
  <div id="container" class="container_24">
  	
    <!-- START : LEFT menu -->
    <div class="grid_4" id="left">
      <img src="/spine/images/profiles/${user.email}.jpg" alt="${user.firstName}" width="100" height="150" class="avatar" />
     
	  <ul class="description">
          <li class="name">${user.firstName} ${user.lastName}</li>
          <li class="company">tbd</li>
          <li class="city">${user.country}</li>
	  </ul>
      
      <div class="grid_8 omega badges">
        <ul>
          <li><img src="/spine/images/badges/html.png" width="37" height="38" alt="HTML"></li>
          <li><img src="/spine/images/badges/html.png" width="37" height="38" alt="HTML"></li>
          <li><img src="/spine/images/badges/html.png" width="37" height="38" alt="HTML"></li>
          <li><img src="/spine/images/badges/html.png" width="37" height="38" alt="HTML"></li>
          <li><img src="/spine/images/badges/html.png" width="37" height="38" alt="HTML"></li>
          <li><img src="/spine/images/badges/html.png" width="37" height="38" alt="HTML"></li>
        </ul>
      </div>
      <br/>
      <ul class="menu">
        <li><a href="#"><span>13</span> Badges</a></li>
        <li><a href="#"><span>146</span> Tags</a></li>
        <li><a href="#"><span>3</span> Events</a></li>
        <li><a href="#"><span>11</span> My Notes</a></li>
        <li><a href="#"><span>56</span> Messages</a></li>
        <li><a href="#"><span>2</span> Last Jobs</a></li>
      </ul>
    </div>
    <!-- END : LEFT menu -->
    
    
    <!-- BEGIN : RIGHT column -->
    <div class="grid_20" id="right">
    
      <!-- BEGIN : Messages -->
      <div class="grid_12 alpha header">
        <!--  
        <p class="news">
          Happening now: <span id="message"></span><span id="error"></span>
        </p>       
      -->
      </div>
      
    
      <!-- END : Messages -->
    
      <!-- BEGIN : Feed & Details blocks -->
      <div class="grid_20 feed_and_details">
      <!-- BEGIN : Feed block -->
      <div class="grid_14 alpha feed">
      
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
          <div class="grid_4 omega my_updates">
            <a href="#"><img src="/spine/images/home/my_updates.png" width="60" height="54" alt="Update box"></a>
            <p><a href="#">My Spine Updates</a></p>
          </div>
           <!-- END : filter & my updates -->          
         
          <!--  BEGIN Flux -->
          <g:render template="page"></g:render>
          <!-- END Flux -->
          
                  
          <!-- BEGIN : pagination -->
          <!--
          <div class="grid_14 alpha omega pagination">
            <div class="grid_1 alpha prev"><p><a href="#">Prev</a></p></div>
            <div class="grid_10 pages">
            	<ul>
                  <li><a href="#">1</a></li>
                  <li><a href="#">2</a></li>
                  <li><a href="#">3</a></li>
                  <li><a href="#">4</a></li>
                  <li><a href="#">5</a></li>
                </ul>
            </div>
            <div class="grid_2 omega next"><p><a href="#">Next</a></p></div>
          </div>
          --!>
          <!-- END : pagination --> 
          
        </div>
        <!-- END : Feed block -->
        
        <!-- BEGIN : Details block -->
        <div class="grid_6 omega details_panel">
          
          <!-- <img src="/spine/images/avatar2.jpg" alt="Avatar" class="avatar" />  -->
          
          <img src="/spine/images/profiles/${user.email}.jpg" alt="${user.firstName}" width="100" height="150" class="avatar" id="selectedImage"/>
     
          
          <ul class="description">
            <li class="name" id="selectedUserName">Alewxander Niemz</li>
            <li class="company" id="selectedCompany">Accenture GmbH</li>
            <li class="city" id="selectedCity">Wien</li>
          </ul>
          
          <ul class="tags" id="selectedTags">
            <li><a href="#">#html</a></li>
            <li><a href="#">#vienna</a></li>
            <li><a href="#">#jazz</a></li>
            <li><a href="#">#rock</a></li>
            <li><a href="#">#css</a></li>
            <li><a href="#">#html5</a></li>
            <li><a href="#">#prater</a></li>
            <li><a href="#">#blockwurst</a></li>
            <li><a href="#">#samba</a></li>
            <li><a href="#">#dancing</a></li>
            <li><a href="#">#lisp</a></li>
            <li><a href="#">#foto</a></li>
            <li><a href="#">#drama</a></li>
            <li><a href="#">#sesamestreet</a></li>
            <li><a href="#">#rest</a></li>
            <li><a href="#">#namibia</a></li>
            <li><a href="#">#travel</a></li>
            <li><a href="#">#testing</a></li>
          </ul>
          
          <p class="all_tags"><a href="#">All tags</a></p>
        </div> 
        <!-- END : Details block -->
      </div>
      <!-- END : Feed & Details blocks -->
      
      <!-- BEGIN : Footer -->
      <!--  
      <div class="grid_20 alpha omega footer">
        <div class="grid_5 alpha logo"><img src="/spine/images/logo.png" alt="Spine" width="123" height="34" class="logo" /></div>
        <div class="grid_15 omega links">
          <ul>
            <li><a href="#">Über uns</a></li>
            <li><a href="#">How it works</a></li>
            <li><a href="#">Spine Blog</a></li>
            <li><a href="#">Privacy</a></li>
            <li><a href="#">Service</a></li>
            <li><a href="#">Facebook</a></li>
          </ul>
        </div>
      </div>
      -->
      <!-- END : Footer -->
      
      <div class="grid_20 alpha omega close">
        &nbsp;
      </div>
      
      <!-- BEGIN : Copyright -->
      <!-- > -->
      <div class="grid_20 alpha omega copyright">
        <div class="grid_5 omega">&copy; Spine 2011 - All rights reserved</div>
      </div>
      <!-- END : Copyright -->
      
    </div>
    <!-- END : Right column -->
    
  </div>
  <!-- END : container -->
  
  
	
  
</body>

</html>