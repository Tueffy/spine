
/** 
 * Defining some UI object : 
 */
var viewAllTags;
var viewAllTagsLink;


/**
 * Dealing with the first user
 * TODO: Can someone write down what it is used for ? 
 */
var firstUser = null;

getFirstUser = function() {
	return firstUser;
}

setFirstUser = function(user) {
	firstUser = user;
}


/**
 * Window & DOM ready we can interact with it ! 
 */
window.onload = function() {

	viewAllTags = jQuery('#right .details_panel p.all_tags');
	viewAllTagsLink = viewAllTags.find('a');
	
	/****************************************
	 * Auto completion for tags             *
	 ****************************************/
	jQuery('.autocomplete_tags').autocomplete({
		minLength: 2, 
		source: autocompleteAllTagsSource
	});
	
	
	/****************************************
	 * View more or less tags in the        *
	 * details panel                        *
	 ****************************************/
	viewAllTagsLink.on('click', toggleViewAllTags);
	

	/****************************************
	 *  Drag & Drop user in the left panel  *
	 *  to access the selected user network *
	 ****************************************/
	Droppables.add('left', {
		accept : 'contact',
		hoverclass : 'hover',
		onDrop : function(e) {
			window.location = "index?user=" + e.id;
		}
	});

	/*
	 * new Ajax.Request('/spine/network/getUserStatistics/${user.email}', {
	 * asynchronous:true, evalScripts:true, onSuccess: function(transport) { var
	 * tagsJSON = transport.responseText; var tags = eval("("+tagsJSON+")");
	 * 
	 * for (var key in tags) { $(key).innerHTML = tags[key]; }
	 *  } });
	 */
}


/**
 * Function uses by jQuery UI autocomplete widget to retrieve tags. 
 */
autocompleteAllTagsSource = function (request, response) {
	// Empty Array we will populate for the response
	var responseData = new Array();
	
	// Call the server, expecting a JSON answer
	jQuery.getJSON('/spine/network/ajaxAutoComplete', {'term': request.term}, function (data) {
		
		// Server responded, now you can populate the responseData array
		jQuery.each(data, function (i, elem) {
			responseData.push({
				'label': elem.tag + ' : ' + elem.number, 
				'value': elem.tag
			});
		});
		
		// Sending back the responseData array for the autocompleter
		response(responseData);
		
	});	
}

/**
 * Refresh the panel on the right with data about the selected user 
 * Uses jQuery
 */
updateSelectedUser = function(e) {
	var user = jQuery.parseJSON(e.responseText);
	
	// The user is no more asked to select a user : 
	jQuery('#selectUser').hide();
	
	// Fill basic info about the user
	jQuery('#selectedUserName').text(user.firstName + ' ' + user.lastName);
	jQuery('#selectedCity').innerHTML = user.city;
	jQuery('#selectedImage').attr('src', '/spine/images/profiles/' + user.email + '.jpg');
	
	// Updating the tag list
	var selectedTags = jQuery('#selectedTags');
	var selectedTagsMore = jQuery('#selectedTagsMore').hide();
	selectedTags.find('li').remove();
	selectedTagsMore.find('li').remove();
	
	// Get the tags & update the list with them
	var nb_tags = 0;
	jQuery.getJSON('/spine/network/getTags/' + user.email, function (tags) {
		jQuery.each(tags, function (tag) {
			nb_tags++;
			var li = jQuery(document.createElement('li'));
			li.text(tag);
			if(nb_tags <= 10) 
				li.appendTo(selectedTags);
			else
				li.appendTo(selectedTagsMore);
			i++;
		});
	});	
	
	// Managing the "All tags" link
	if(nb_tags > 10) {
		viewAllTags.show();
		viewAllTagsLink.text("All tags");
	}
	else
		viewAllTags.hide();
}

/**
 * Display more or less tags in the right panel
 */
toggleViewAllTags = function () {
	var selectedTagsMore = jQuery('#selectedTagsMore');
	if(selectedTagsMore.css('display') == 'none') {
		selectedTagsMore.show('slow');
		jQuery(this).text("Less tags");
	}
	else {
		selectedTagsMore.hide('slow');
		jQuery(this).text("More tags");
	}
	return false;
}

tagsMinusOnMouseOver = function(e) {
	// alert(e);
	$(e).appear();
	$(e).onmouseout = function() {
		$(this).fade();
	}
	return false;
}

tagsMinusOnMouseOut = function(e) {
	// alert("test");
	$(e).fade({
		duration : 7.0
	});
	return false;
}

tagsPlusOnMouseOver = function() {
	// alert("test");
	$('minus').appear(5);

	return false;
}

tagsPlusOnMouseOut = function() {
	// alert("test");
	$('minus').fade();
	return false;
}

/**
 * 
 * @param e
 */
inviteNewUser = function(e) {
	alert("dd");
}


/**
 * 
 * 
 * @param e
 */
addTagUpdate = function(e, id){
	
	var response = eval("(" + e.responseText + ")");
	
	//alert(response.tag);
	
	var ul = $(id);
	var li = document.createElement("li");
	li.innerHTML = response.tag;
	ul.insertBefore(li, ul.getElementsByTagName("li")[1]);
	
}


/**
 * 
 * @param e
 */
removeTagUpdate = function(e){
	
	$(e).fade({
		duration : 2.0
	});
}