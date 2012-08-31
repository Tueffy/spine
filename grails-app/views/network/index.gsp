<html>
<head>
	<meta name="layout" content="main">
  	<g:javascript src="app/scroll.js" />
	<g:javascript src="app/network.js" />
</head>
<body>
    <!-- START : LEFT MENU -->
    <div class="grid_5" id="left">
    	<div class="details_panel">
    		<img src="/spine/images/profiles/${user.email}.jpg" alt="${user.firstName}" width="125" height="125" class="avatar" />
			<ul class="description">
				<li class="name">${user.firstName} ${user.lastName}</li>
				<li class="company">${user.company}</li>
				<li class="jobTitle"><span id="userDepartment">${user.department}</span>, <span id="userJobTitle">${user.jobTitle}</span></li>
				<li class="phone">Phone: <span id="userPhone">${user.phone}</span></li>
				<li class="city"><span id="userCity">${user.city}</span>, <span id="userCountry">${user.country}</span></li>
				<li class="freeText">„${user.freeText}„</li>
			</ul>
			
			<h3 class="badgesHeading"><span>${badges.size()}</span> Badges : </h3>
			<ul class="badges">
				<g:each in="${badges}" var="badge">
					<li><img src="/spine/images/badges/36x36/${badge.image}" alt="${badge}" title="${badge}"/><li>
				</g:each>
			</ul>
			
			<hr class="clear" />
			
			<h3 class="tagsHeading"><span>${user.tags.size()}</span> Tags : </h3>
			<ul class="tags">
				<g:each in="${user.tags}" var="tag">
					<li>
						<span class="tag">
							<a href="/spine/network/index?filter=Innovation">${tag.key}</a>
							<a href="/spine/network/untagMe?tag=${tag.key}" class="untagMe" title="Untag me" >-</a>
						</span>
						<span class="nb">${tag.value}</span></li>
					</li>
				</g:each>
			</ul>
			
    	</div>
    </div>
    <!-- END : LEFT MENU -->    
    
    <!-- BEGIN : RIGHT COLUMN -->
    <div class="grid_19" id="right">    
      <!-- BEGIN : Feed & Details blocks -->
      <div class="grid_19 feed  ">
      	<!-- BEGIN : Feed block -->
      	<div class="grid_14 alpha columnSeparator">
          <!-- BEGIN : filter & my updates -->
          <div class="grid_13 alpha omega filter">
            <g:form name="filterByTag" method="post" action="index" params="[user: user.email]">
            
              <!-- Input text : search Box -->
              <g:if test="${param}">
              	<input type="text" name="filter" id="autocomplete" class="autocomplete_tags" value="${param}" default="${param}" onClick="javascript:this.value='';" />
              </g:if>
              <g:else>
              	<input type="text" name="filter" id="autocomplete" class="autocomplete_tags" value="Filter ${user.firstName}'s spine" default="Filter ${user.firstName}'s spine" onClick="javascript:this.value='';" />
              </g:else>
                            
<%--              <ul class="filter_list">--%>
<%--                <li>Filter by</li>--%>
<%--                <li><a href="#">Expertise</a></li>--%>
<%--                <li><a href="#">Distances</a></li>--%>
<%--            </ul>--%>
            	<input type="hidden" value="${param}" id="filter_helper" />
            </g:form>            
          </div>
<%--          <div class="grid_4 omega my_updates" id="test1">--%>
<%--            <a href="#"><img src="/spine/images/home/my_updates.png" width="60" height="54" alt="Update box"></a>--%>
<%--            <p><a href="#">My Spine Updates</a></p>--%>
<%--          </div>--%>
           <!-- END : filter & my updates -->          
         
          <!--  BEGIN Flux -->
          <g:render template="inc/page"></g:render>
          <!-- END Flux -->
          
        </div>
        <!-- END : Feed block -->
        
        <!-- BEGIN : Details block -->
        <div class="grid_5 omega details_panel" id="details_panel" >          
          <!-- <img src="/spine/images/avatar2.jpg" alt="Avatar" class="avatar" />  -->
          <img src="/spine/images/profiles/anonymous.jpg"" alt="anonymous" width="125" height="125" class="avatar" id="selectedImage" />
     	  <span id="selectUser" style="font-size:9pt">Please choose one of your contacts to see more details!</span>
          <ul class="description">
            <li class="name" id="selectedUserName"></li>
            <li class="company" id="selectedCompany"></li>
            <li class="jobTitle"><span id="selectedDepartment"></span>, <span id="selectedJobTitle"></span></li>
            <li class="phone" id="selectedPhone"></li>
            <li class="city"><span id="selectedCity"></span>, <span id="selectedCountry"></span></li>
            <li class="freeText" id="selectedFreeText"></li>
          </ul>
          <ul class="badges" id="selectedBadges"></ul>
          <ul class="tags" id="selectedTags"></ul>   
          <ul class="tags" id="selectedTagsMore"></ul>        
          <p class="all_tags" style="display: none;"><a href="">More tags</a></p>
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
        <div class="grid_5 omega">&copy; Spine 2012 - All rights reserved</div>
      </div>
      <!-- END : Copyright -->
      
    </div>
    <!-- END : Right column -->
    
</body>
</html>