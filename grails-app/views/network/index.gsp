<html>
<head>
	<meta name="layout" content="main">
  	<g:javascript src="app/scroll.js" />
	<g:javascript src="app/network.js" />
</head>
<body>
    <!-- START : LEFT MENU -->
    <div class="grid_5" id="left">
      <img src="/spine/images/profiles/${user.email}.jpg" alt="${user.firstName}" width="130" height="130" class="avatar" />     
	  <ul class="description">
          <li class="name"><a href="../user/profile">${user.firstName} ${user.lastName}</a></li>
          <li class="company">${user.company}</li>
          <li class="city">${user.country}</li>
	  </ul>      
      <div class="badges"  style="margin-left:35px">
        <ul>
          <g:each in="${badges}" var="b"> 
          	 <li><img src="/spine/images/badges/${b.image}" width="37" height="38" alt="${b.name}"></li>          
          </g:each>
        </ul>
      </div>
      <ul class="menu">
        <li><a href="#"><span id="badgesNumber">${badges.size()}</span> Badges</a></li> 
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
      <div class="grid_19 feed  ">
      	<!-- BEGIN : Feed block -->
      	<div class="grid_14 alpha columnSeparator">
          <!-- BEGIN : filter & my updates -->
          <div class="grid_10 alpha filter">
            <g:form name="filterByTag" method="post" action="index">
            
              <!-- Input text : search Box -->
              <g:if test="${param}">
              	<input type="text" name="filter" id="autocomplete" value="${param}" default="${param}" onClick="javascript:this.value='';" />
              </g:if>
              <g:else>
              	<input type="text" name="filter" id="autocomplete" value="Filter ${user.firstName}'s spine" default="Filter ${user.firstName}'s spine" onClick="javascript:this.value='';" />
              </g:else>
              
              
              <div id="autocomplete_choices" class="autocomplete"></div>              
              <ul class="filter_list">
                <li>Filter by</li>
                <li><a href="#">Expertise</a></li>
                <li><a href="#">Distances</a></li>
            </ul>
            	<input type="hidden" value="${param}" id="filter_helper" />
            </g:form>            
          </div>
          <div class="grid_4 omega my_updates" id="test1">
            <a href="#"><img src="/spine/images/home/my_updates.png" width="60" height="54" alt="Update box"></a>
            <p><a href="#">My Spine Updates</a></p>
          </div>
           <!-- END : filter & my updates -->          
         
          <!--  BEGIN Flux -->
          <g:render template="inc/page"></g:render>
          <!-- END Flux -->
          
        </div>
        <!-- END : Feed block -->
        
        <!-- BEGIN : Details block -->
        <div class="grid_5 omega details_panel" >          
          <!-- <img src="/spine/images/avatar2.jpg" alt="Avatar" class="avatar" />  -->
          <img src="/spine/images/profiles/anonymous.gif"" alt="anonymous" width="125" height="125" class="avatar" id="selectedImage" />
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
    
</body>
</html>