<%@page import="com.sun.xml.internal.bind.v2.TODO"%>
<html>
<head>
<meta name="layout" content="main">
  <title>Spine Landing</title>
  <link href="/spine/css/landing.css" rel="stylesheet" type="text/css">
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  <script type="text/javascript" src="/spine/js/prototype/event.simulate.js"></script>
  <script type="text/javascript" src="/spine/js/app/landing.js"></script>
  <script type="text/javascript">landing.form.init();</script>
  <uploader:head />
  <cropper:head />
</head>

<body>







<div class="registration">
	<div class="grid_10 omega knowledge" style="padding-top: 110px;">
    	<img src="${resource(dir:'images/home',file:'spine-connected.png')}" alt="Connected">
    </div>  
    
    <div class="grid_14 alpha landing">
    
    	<h2 style="font-size:36pt;color:#000000;display:inline;">Register</h2>
        <p class="subtitle" style="display:inline;">Join Spine now!</p>
        
        <g:if test="flash['message']">
			<p>${flash['message']}</p>
		</g:if>
    	
    	<g:form action="doRegister" method="post" name="registration" class="registration">
    	
    		<ul class="pagination">
	    		<li class="current">Personal Details</li>
	    		<li>Photo</li>
	    		<li>Additional info</li>
	    	</ul>
    	
    		<div class="pages">
	    		<!-- First Step -->
	    		<div class="page">
	    		
	    			<h3>1. Personal Details</h3>
	    		
	    			<table>
	    				<tr>
							<th class="gender">
								<label for='gender'>Gender:</label>
							</th>
							<td class="label">
								<select name="gender" id="gender">
									<option value=""<g:if test="${user?.gender == null}"> selected="selected"</g:if>></option>
									<option value="male"<g:if test="${user?.gender == 'male'}"> selected="selected"</g:if>>Male</option>
									<option value="female"<g:if test="${user?.gender == 'female'}"> selected="selected"</g:if>>Female</option>
								</select>
							</td>
						</tr>
	    				<tr>
							<th class="label">
								<label for='lastName'>Last Name:</label>
							</th>
							<td class="label">
								<input id="lastName" type="text" name="lastName" value="${user?.lastname}" />
							</td>
						</tr>
						<tr>
							<th class="label">
								<label for="firstName">First Name:</label>
							</th>
							<td>
								<input id="firstName" type="text" name="firstName" value="${user?.firstname}" />
							</td>
						</tr>
						<tr>
							<th class="label">
								<label for="birthday">Birthday:</label>
							</th>
							<td>
								<input id="birthday" type="text" name="birthday" value="${user?.birthday}" />
							</td>
						</tr>
						<tr>
							<th class="label">
								<label for="email">Email:</label>
							</th>
							<td>
								<input id="email" type="text" name="email" value="${tmp_email}" />
							</td>
						</tr>
						<tr>
							<th class="label">
								<label for="country">Country:</label>
							</th>
							<td>
								<input id="country" type="text" name="country" value="${user?.country}" />
							</td>
						</tr>
						<tr>
							<th class="label">
								<label for='city'>City:</label>
							</th>
							<td>
								<input id="city" type="text" name="city" value="${user?.city}" />
							</td>
						</tr>
						<tr>
							<th class="label">
								<label for='department'>Department:</label>
							</th>
							<td>
								<input id="department" type="text" name="department" value="${user?.department}" />
							</td>
						</tr>
						<tr>
							<th class="label">
								<label for='jobTitle'>Job Title:</label>
							</th>
							<td>
								<input id="jobTitle" type="text" name="jobTitle" value="${user?.jobTitle}" />
							</td>
						</tr>
						<tr>
							<th class="label">
								<label for='phone'>Phone:</label>
							</th>
							<td>
								<input id="phone" type="text" name="phone" value="${user?.phone}" />
							</td>
						</tr>
						<tr>
							<th class="label">
								<label for='company'>Company:</label>
							</th>
							<td>
								<input id="company" type="text" name="company" value="${user?.company}" />
							</td>
						</tr>
						<tr>
							<th class="label">
								<label for='password'>Password:</label>
							</th>
							<td>
								<input id="password" type="password" name="password" value="${user?.password}" />
							</td>
						</tr>
					</table>
	    		</div>
	    		
	    		<!-- Second Step -->
	    		<div class="page">
	    			<h3>2. Photo</h3>
	    			
	    			<div class="upload_box">
						<uploader:uploader id="picture" multiple="false">
							<uploader:onComplete>
								$('picture_field').writeAttribute('value', responseJSON.filename);
								$('au-picture').hide();
								$('cropper').show();
								$('cropper-img').writeAttribute('src', '../' + responseJSON.dir + responseJSON.filename);
								new Cropper.Img('cropper-img',{
								 	autoIncludeCSS: false, 
								 	ratioDim:{x:40,y:40}, 
								 	minWidth: 162, 
								 	minHeight: 162, 
								 	displayOnInit: 1, 
								 	onEndCrop: function(coords, dimensions) { 
										$( 'crop_x1' ).value = coords.x1;
										$( 'crop_y1' ).value = coords.y1;
										$( 'crop_x2' ).value = coords.x2;
										$( 'crop_y2' ).value = coords.y2;
										$( 'crop_w' ).value = dimensions.width;
										$( 'crop_h' ).value = dimensions.height;
								 	} }
								);
							</uploader:onComplete>
						</uploader:uploader>
					</div>
					
					<div id="cropper" style="display: none;">
						<img id="cropper-img" />
						<input type="hidden" name="crop_x1" id="crop_x1" value="0" />
						<input type="hidden" name="crop_y1" id="crop_y1" value="0" />
						<input type="hidden" name="crop_x2" id="crop_x2" value="0" />
						<input type="hidden" name="crop_y2" id="crop_y2" value="0" />
						<input type="hidden" name="crop_h" id="crop_h" value="0" />
						<input type="hidden" name="crop_w" id="crop_w" value="0" />
						<input type="hidden" name="picture" id="picture_field" />
					</div>
	    		</div>
	    		
	    		<!-- Third Step -->
	    		<div class="page">
	    			<h3>3. Additional info </h3>
					<textarea name="freeText">...</textarea>
	    		</div>
	    		
	    		<div class="controls">
					<a href="#" class="previous">Previous</a>
					<a href="#" class="next">Next</a>
	    		</div>
	    		
    		</div>
    	
    	</g:form>
    </div>
</div>








<%--
  	<div class="grid_10 omega knowledge" style="padding-top:80px">
    	<img src="${resource(dir:'images/home',file:'spine-connected.png')}" alt="Connected">
    </div>  
  	<div class="grid_14 alpha landing" style="padding-top:40px">
    	<h2 style="font-size:36pt;color:#000000">Register</h2>
        <p class="subtitle">Join Spine now!</p>
        <g:if test="flash['message']">
			${flash['message']}
		</g:if>
		
		
		<g:form action="doRegister" method="post" name="registration_form">
		
		
			<!--  BEGIN : First Slide -->
			<div class="form_slide dialog current">
				<table class="userForm">
					<tr class='prop'>
						<td valign='top' style='text-align: left;' width='20%'>
							<label for='lastName'>Last Name:</label>
						</td>
						<td valign='top' style='text-align: left;' width='80%'>
							<input id="lastName" type='text' name='lastName' value='${user?.lastname}' />
						</td>
					</tr>
					<tr class='prop'>
						<td valign='top' style='text-align: left;' width='20%'>
							<label for='firstName'>First Name:</label>
						</td>
						<td valign='top' style='text-align: left;' width='80%'>
							<input id="firstName" type='text' name='firstName' value='${user?.firstname}' />
						</td>
					</tr>
					<tr class='prop'>
						<td valign='top' style='text-align: left;' width='20%'>
							<label for='email'>Email:</label>
					</td>
						<td valign='top' style='text-align: left;' width='80%'>
							<input id="email" type='text' name='email' value='${tmp_email}' />
						</td>
					</tr>
					<tr class='prop'>
						<td valign='top' style='text-align: left;' width='20%'>
							<label for='country'>Country:</label>
						</td>
						<td valign='top' style='text-align: left;' width='80%'>
							<input id="country" type='text' name='country' value='${user?.country}' />
						</td>
					</tr>
					<tr class='prop'>
						<td valign='top' style='text-align: left;' width='20%'>
							<label for='city'>City:</label>
						</td>
						<td valign='top' style='text-align: left;' width='80%'>
							<input id="city" type='text' name='city' value='${user?.city}' />
						</td>
					</tr>
					<tr class='prop'>
						<td valign='top' style='text-align: left;' width='20%'>
						<label for='password'>Password:</label>
					</td>
						<td valign='top' style='text-align: left;' width='80%'>
							<input id="password" type='password' name='password' value='${user?.password}' />
						</td>
					</tr>
				</table>
				<div class="buttons" style="text-align:right;margin-right:50px">
					<!-- <span class="formButton"> <input type="submit" value="Register"></input> </span> -->
					<span class="formButton"><input type="button" value="Next" class="next" /></span>
				</div>
			</div>
			<!--  END : First slide -->
			
			
			<!--  BEGIN : Second slide -->
			<div class="form_slide dialog">
				
				<h3>Upload a picture of you  (Optional)</h3>
				
				<div class="upload_box">
					<uploader:uploader id="picture" multiple="false">
						<uploader:onComplete>
							$('picture_field').writeAttribute('value', responseJSON.filename);
							$('au-picture').hide();
							$('cropper').show();
							$('cropper-img').writeAttribute('src', '../' + responseJSON.dir + responseJSON.filename);
							new Cropper.Img('cropper-img',{
							 	autoIncludeCSS: false, 
							 	ratioDim:{x:40,y:40}, 
							 	onEndCrop: function(coords, dimensions) { 
									$( 'crop_x1' ).value = coords.x1;
									$( 'crop_y1' ).value = coords.y1;
									$( 'crop_x2' ).value = coords.x2;
									$( 'crop_y2' ).value = coords.y2;
									$( 'crop_w' ).value = dimensions.width;
									$( 'crop_h' ).value = dimensions.height;
							 	} }
							);
						</uploader:onComplete>
					</uploader:uploader>
				</div>
				
				<div id="cropper" style="display: none;">
					<img id="cropper-img" />
					<input type="hidden" name="crop_x1" id="crop_x1" value="0" />
					<input type="hidden" name="crop_y1" id="crop_y1" value="0" />
					<input type="hidden" name="crop_x2" id="crop_x2" value="0" />
					<input type="hidden" name="crop_y2" id="crop_y2" value="0" />
					<input type="hidden" name="crop_h" id="crop_h" value="0" />
					<input type="hidden" name="crop_w" id="crop_w" value="0" />
					<input type="hidden" name="picture" id="picture_field" />
				</div>
				
				<br />
				<div class="buttons">
					<span class="formButton"><input type="button" value="Previous" class="prev" /></span>
					<span class="formButton"><input type="button" value="Next" class="next" /></span>
				</div>
			</div>
			<!--  END : Second slide -->
			
			<!-- BEGIN : Third slide -->
			<div class="form_slide dialog">
				<p>Tell us more about yourself (Optional) </p>
				
				<textarea name="freeText">...</textarea>
				
				<div class="buttons">
					<br />
					<span class="formButton"><input type="button" value="Previous" class="prev" /></span>
					<span class="formButton"><input type="submit" value="Finish" class="finish" /></span>
				</div>
			</div>
			<!--  END : Third slide -->
		
		</g:form>
    </div>
    
    
  --%>
  
</body>
</html>