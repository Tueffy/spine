<%@page import="com.sun.xml.internal.bind.v2.TODO"%>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  <title>Spine Landing</title>
  <link href="/spine/css/reset.css" rel="stylesheet" type="text/css">
  <link href="/spine/css/960.css" rel="stylesheet" type="text/css">
  <link href="/spine/css/design.css" rel="stylesheet" type="text/css">
  <link href="/spine/css/landing.css" rel="stylesheet" type="text/css">
  <script type="text/javascript" src="/spine/js/prototype/prototype.js"></script>
  <script type="text/javascript" src="/spine/js/prototype/event.simulate.js"></script>
  <script type="text/javascript" src="/spine/js/prototype/scriptaculous.js"></script>
  <script type="text/javascript" src="/spine/js/app/landing.js"></script>
  <uploader:head />
  <cropper:head />
</head>

<body>
  <div id="header">
    <div class="container_24">
      <img src="/spine/images/logo.png" alt="Spine" width="222" height="61" class="logo" />
    
      <ul class="links">
      	<li><a href="#">About</a></li>
        <li><a href="/spine/user/login">Log in</a></li>
      </ul>
    </div>
  </div>
  
  <!-- BEGIN : container -->
  <div id="container" class="container_24">
  	<div class="grid_14 alpha landing">
    	<h2>Connect with your co-workers</h2>
        <p class="subtitle">Register yourself to join Spine!</p>
        <g:if test="flash['message']">
			${flash['message']}
		</g:if>
		<g:form action="doRegister" method="post" name="registration_form">
		
		
			<!--  BEGIN : First Slide -->
			<div class="form_slide dialog current">
				<table class="userForm">
					<tr class='prop'>
						<td valign='top' style='text-align: left;' width='20%'>
							<label for='lastname'>Last Name:</label>
						</td>
						<td valign='top' style='text-align: left;' width='80%'>
							<input id="lastname" type='text' name='lastname' value='${user?.lastname}' />
						</td>
					</tr>
					<tr class='prop'>
						<td valign='top' style='text-align: left;' width='20%'>
							<label for='firstname'>First Name:</label>
						</td>
						<td valign='top' style='text-align: left;' width='80%'>
							<input id="firstname" type='text' name='firstname' value='${user?.firstname}' />
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
				<div class="buttons">
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
    <div class="grid_10 omega knowledge"><img src="${resource(dir:'images/home',file:'knowledge.png')}" width="399" height="299" alt="Knowledge"></div>
  </div>
  <!-- END : container -->
  
</body>
</html>