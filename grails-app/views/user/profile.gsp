<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>My Spine</title>
<link href="/spine/css/reset.css" rel="stylesheet" type="text/css">
<link href="/spine/css/960.css" rel="stylesheet" type="text/css">
<link href="/spine/css/design.css" rel="stylesheet" type="text/css">
  <link href="/spine/css/landing.css" rel="stylesheet" type="text/css">
<link href="/spine/css/ajax.css" rel="stylesheet" type="text/css">

	<g:javascript src="jquery/jquery-1.7.min.js" />
	<g:javascript>jQuery.noConflict();</g:javascript>
	<g:javascript src="main.js" />
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
			<span> <img src="/spine/images/home/logo.png" alt="Spine"
				width="222" height="61" class="logo" />
			</span> <span>
				<ul class="links">
					<li><a href="#">About Spine</a></li>
					<li><a href="#">How it works</a></li>
					<li><a href="#">Terms and conditions</a></li>
					<li><a href="#">Disclaimer</a></li>
					<li><g:link controller="user" action="doLogout">Logout</g:link></li>
				</ul>

				<p class="news">
					<img src="/spine/images/home/bubble.png" alt="Bubble" width="42"
						height="39" class="bubble" /> <span id="message">You've
						got 7 new tags and 1 new badge.</span>
				</p>
		</div>
	</div>

	<div id="nav">
		<div class="container_24" id="hot_tags">

			<ul>
				<li><img src="/spine/images/home/hot_tags.png" width="75"
					height="23" alt="Hot Tags"></li>
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

		<div class="landing">
			<g:form action="updateProfile">
				<table class="userForm" style="width:100%;">
					<tr class='prop'>
						<td valign='top' style='text-align: left;' width='20%'><label
							for='lastname'>Last Name:</label></td>
						<td valign='top' style='text-align: left;' width='80%'><input
							id="lastname" type='text' name='lastname'
							value='${user?.lastName}' /></td>
					</tr>
					<tr class='prop'>
						<td valign='top' style='text-align: left;' width='20%'><label
							for='firstname'>First Name:</label></td>
						<td valign='top' style='text-align: left;' width='80%'><input
							id="firstname" type='text' name='firstname'
							value='${user?.firstName}' /></td>
					</tr>
					<tr class='prop'>
						<td valign='top' style='text-align: left;' width='20%'><label
							for='country'>Country:</label></td>
						<td valign='top' style='text-align: left;' width='80%'><input
							id="country" type='text' name='country' value='${user?.country}' />
						</td>
					</tr>
					<tr class='prop'>
						<td valign='top' style='text-align: left;' width='20%'><label
							for='city'>City:</label></td>
						<td valign='top' style='text-align: left;' width='80%'><input
							id="city" type='text' name='city' value='${user?.city}' /></td>
					</tr>
					<tr class='prop'>
						<td valign='top' style='text-align: left;' width='20%'><label
							for='password'>Password:</label></td>
						<td valign='top' style='text-align: left;' width='80%'><input
							id="password" type='password' name='password'
							value='' /></td>
					</tr>
					<tr class='prop'>
						<td valign='top' style='text-align: left;' width='20%'><label
							for='freeText'>More about yourself:</label></td>
						<td valign='top' style='text-align: left;' width='80%'>
							<textarea id="freeText" name="freeText">${user?.freeText}</textarea>
						</td>
					</tr>
					<tr class='prop'>
						<td valign='top' style='text-align: left;' width='20%'>Change picture : </td>
						<td>
							<div class="upload_box"><uploader:uploader id="picture" multiple="false">
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
							</uploader:uploader></div>
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
				
				<div class="buttons">
					<input type="submit" value="Envoyer" />
				</div>
				
			</g:form>
		</div>

	</div>
	<!-- END : container -->




</body>

</html>