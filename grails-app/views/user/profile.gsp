<html>
<head>
	<meta name="layout" content="main">
	<title>Spine</title>
	<link href="/spine/css/landing.css" rel="stylesheet" type="text/css">
	<link rel="icon" type="image/gif" href="/spine/images/spinefavicon.gif" />
	<g:javascript src="jquery/jquery-1.7.min.js" />
	<g:javascript>jQuery.noConflict();</g:javascript>
	<g:javascript src="app/landing.js" />
	<uploader:head />
	<cropper:head />
</head>
<body>

		<div class="landing">
			<h2>My Profile</h2>
        	<p class="subtitle">Update your profile details</p>
			<g:form action="updateProfile">
				<table class="userForm" style="width:100%;">
					<tr class='prop'>
						<td valign='middle' style='text-align: left;' width='20%'><label for='lastName'>Last Name:</label></td>
						<td valign='middle' style='text-align: left;' width='80%'><input id="lastName" type='text' name='lastName'	value='${user?.lastName}' /></td>
					</tr>
					<tr class='prop'>
						<td valign='middle' style='text-align: left;' width='20%'><label for='firstName'>First Name:</label></td>
						<td valign='middle' style='text-align: left;' width='80%'><input id="firstName" type='text' name='firstName' value='${user?.firstName}' /></td>
					</tr>
					<tr class='prop'>
						<td valign='middle' style='text-align: left;' width='20%'><label for='country'>Country:</label></td>
						<td valign='middle' style='text-align: left;' width='80%'><input id="country" type='text' name='country' value='${user?.country}' />
						</td>
					</tr>
					<tr class='prop'>
						<td valign='middle' style='text-align: left;' width='20%'><label for='city'>City:</label></td>
						<td valign='middle' style='text-align: left;' width='80%'><input id="city" type='text' name='city' value='${user?.city}' /></td>
					</tr>
					<tr class='prop'>
						<td valign='middle' style='text-align: left;' width='20%'><label for='password'>Password:</label></td>
						<td valign='middle' style='text-align: left;' width='80%'><input id="password" type='password' name='password' value='' /></td>
					</tr>
					<tr class='prop'>
						<td valign='middle' style='text-align: left;' width='20%'><label for='freeText'>More about yourself:</label></td>
						<td valign='middle' style='text-align: left;' width='80%'><textarea id="freeText" name="freeText">${user?.freeText}</textarea>
						</td>
					</tr>
					<tr class='prop'>
						<td valign='middle' style='text-align: left;' width='20%'>Change picture : </td>
						<td>
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
						</td>
					</tr>
				</table>
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
				<div style="text-align:right;margin-right:50px">					
						<input type="submit" value="Update"></input>					
				</div>
			</g:form>
		</div> 

</body>
</html>