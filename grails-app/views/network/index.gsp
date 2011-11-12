<html>
<head>
  	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  	<title>My Spine</title>
  	<link href="/spine/css/reset.css" rel="stylesheet" type="text/css">
  	<link href="/spine/css/960.css" rel="stylesheet" type="text/css">
  	<link href="/spine/css/design.css" rel="stylesheet" type="text/css">
  	<link href="/spine/css/ajax.css" rel="stylesheet" type="text/css">
  
  	<g:javascript library='scriptaculous' />
  		<g:javascript>
  			window.onload = function(){
                  new Ajax.Autocompleter("autocomplete", "autocomplete_choices", "/spine/network/ajaxAutoComplete",{});
        	}
        	
        	function updateSelectedUser(e) {
        		 // evaluate the JSON
    			var user = eval("("+e.responseText+")");
    			$("selectedUserName").innerHTML = user.email
    			//alert(user.email);
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
      <img src="/spine/images/logo.png" alt="Spine" width="222" height="61" class="logo" />
    
      <ul class="links">
      	<li><a href="#">About</a></li>
        <li><a href="/spine/user/logout">Logout</a></li>
      </ul>
    
      <form method="post">
        <input type="text" value="Search Spine" class="search" default="Search Spine" />
      </form>
    
    </div>
  </div>
  
  <div id="nav">
    <div class="container_24" id="hot_tags">
      <ul>
        <li><img src="/spine/images/home/hot_tags.png" width="75" height="23" alt="Hot Tags" ></li>
        <li><a href="#">#soap</a></li>
        <li><a href="#">#cloud</a></li>
        <li><a href="#">#html</a></li>
        <li><a href="#">#xhtml</a></li>
        <li><a href="#">#java</a></li>
      </ul>
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
    
      <!-- BEGIN : Messages & Badges -->
      <div class="grid_12 alpha header">
        <h2>${user.firstName}'s Spine</h2>
        <p class="news">
          <img src="/spine/images/home/bubble.png" alt="Bubble" width="42" height="39" class="bubble" />
          You've got 7 new tags and 1 new badge.
        </p>
        <br/>
        <div id="message"></div>
        <div id="error"></div>
      </div>
      
      <div class="grid_8 omega badges">
        <ul>
          <li><img src="/spine/images/badges/html.png" width="37" height="38" alt="HTML"></li>
          <li><img src="/spine/images/badges/html.png" width="37" height="38" alt="HTML"></li>
          <li><img src="/spine/images/badges/html.png" width="37" height="38" alt="HTML"></li>
          <li><img src="/spine/images/badges/html.png" width="37" height="38" alt="HTML"></li>
          <li><img src="/spine/images/badges/html.png" width="37" height="38" alt="HTML"></li>
          <li><img src="/spine/images/badges/html.png" width="37" height="38" alt="HTML"></li>
        </ul>
        <p class="all"><a href="">All ${user.firstName}'s Badges</a></p>
      </div>
      <!-- END : Messages & Badges -->
    
      <!-- BEGIN : Feed & Details blocks -->
      <div class="grid_20 feed_and_details">
      <!-- BEGIN : Feed block -->
      <div class="grid_14 alpha feed">
      
          <!-- BEGIN : filter & my updates -->
          <div class="grid_10 alpha filter">
                    
            <g:form name="filterByTag" method="post" action="index">
              <input type="text" name="filter" id="autocomplete" value="Filter ${user.firstName}'s spine" default="Filter Christian‘s spine" />
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
          
         
          <g:each in="${neighbours}" var="n">
	          <!-- BEGIN : 1 person -->
	          <div class="grid_14 alpha omega contact">
	          	<div class="grid_3 alpha picture"><img src="/spine/images/avatar2.jpg" alt="Avatar" class="avatar" /></div>
	            <div class="grid_10 description omega">
	              <ul class="badges">
	                <li><img src="/spine/images/badges/html.png" width="36" height="35" alt="HTML"></li>
	                 <li><img src="/spine/images/badges/html.png" width="36" height="35" alt="HTML"></li>
	              </ul>
	             
	              <h3><g:remoteLink action="getUser" id="1" update="foo" onSuccess="updateSelectedUser(e)">${n.firstName} ${n.lastName}</g:remoteLink></h3>
	              <p class="company">Accenture GmbH, ${user.city}, ${user.country}<br> </p>
	              
	              <div class="grid_7 alpha">
	              	<p class="quote"><span>„</span>Looking forward to new challenges<span>„</span></p>
	                <ul class="tags">
	                    <li>	                    
		                    <a href="#" onmouseover="javascript:tagsMinusOnMouseOver();" onmouseout="javascript:tagsMinusOnMouseOut();">#soap</a>
		                    <span id="minus" style="{display: none;}">
		                    	<g:remoteLink action="removeTag" id="1" update="[success:'message',failure:'error']">-</g:remoteLink>
		                    </span>
	                    </li>
	                    <li>&nbsp;</li>
	                    <li> 
	                    	
		                    <span class="plus">
		                    	<g:remoteLink action="setTag" id="1" update="[success:'message',failure:'error']">+</g:remoteLink>
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
          
          <img src="/spine/images/profiles/${user.email}.jpg" alt="${user.firstName}" width="100" height="150" class="avatar" />
     
          
          <ul class="description">
            <li class="name" id="selectedUserName">Alewxander Niemz</li>
            <li class="company">Accenture GmbH</li>
            <li class="city">Wien</li>
          </ul>
          
          <ul class="tags">
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
          
          <p class="all_tags"><a href="#">All Alexander's tags</a></p>
        </div> 
        <!-- END : Details block -->
      </div>
      <!-- END : Feed & Details blocks -->
      
      <!-- BEGIN : Footer -->
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
      <!-- END : Footer -->
      
      <!-- BEGIN : Copyright -->
      <div class="grid_20 alpha omega copyright">
        <div class="grid_15 alpha"><a href="#">Disclaimer</a>   | <a href="#">Terms of Service</a></div>
        <div class="grid_5 omega">All Rights Reserved - Spine 2011</div>
      </div>
      <!-- END : Copyright -->
      
    </div>
    <!-- END : Right column -->
    
  </div>
  <!-- END : container -->
  
  
  
  <script type="text/javascript" src="js/jquery-1.7.min.js"></script>
  <script type="text/javascript" src="js/main.js"></script>
  
</body>

</html>