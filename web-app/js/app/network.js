
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

var getFirstUser = function() {
	return firstUser;
}

var setFirstUser = function(user) {
	firstUser = user;
}


/**
 * Window & DOM ready we can interact with it ! 
 */
window.onload = function() {

	viewAllTags = jQuery('#right .details_panel p.all_tags');
	viewAllTagsLink = viewAllTags.find('a');
	
	/**
	 * Auto completion for tags
	 */
	jQuery('.autocomplete_tags').autocomplete({
		minLength: 2, 
		source: autocompleteAllTagsSource
	});
	
	
	/**
	 * View more or less tags in the details panel
	 */
	viewAllTagsLink.on('click', toggleViewAllTags);
	
	/**
	 * Drag & Drop user in the left panel to access the selected user network
	 */
	Droppables.add('left', {
		accept : 'contact',
		hoverclass : 'hover',
		onDrop : function(e) {
			window.location = "index?user=" + e.id;
		}
	});
	
	/**
	 * Remove or add one tag from the network view
	 */
	jQuery('#right').on('click', 'a.remove_tag', removeTagClick );
	jQuery('#right').on('click', 'a.add_tag', addTagClick );

}


/**
 * Function uses by jQuery UI autocomplete widget to retrieve tags. 
 */
var autocompleteAllTagsSource = function (request, response) {
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
var updateSelectedUser = function(e) {
	var networkedUser = jQuery.parseJSON(e.responseText);
	var user = networkedUser.user;
	
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
	jQuery.getJSON('/spine/network/getTags/' + user.email, function (tags) {
		var nb_tags = 0;

		jQuery.each(tags, function (tag) {
			nb_tags++;
			var li = jQuery(document.createElement('li'));
			li.text(tag);
			if(nb_tags <= 10) 
				li.appendTo(selectedTags);
			else
				li.appendTo(selectedTagsMore);
		});
		
		// Managing the "All tags" link
		if(nb_tags > 10) {
			viewAllTags.show();
			viewAllTagsLink.text("All tags");
		}
		else
			viewAllTags.hide();
	});	
}

/**
 * Display more or less tags in the right panel
 */
var toggleViewAllTags = function () {
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

var tagsMinusOnMouseOver = function(e) {
	// alert(e);
	$(e).appear();
	$(e).onmouseout = function() {
		$(this).fade();
	}
	return false;
}

var tagsMinusOnMouseOut = function(e) {
	// alert("test");
	$(e).fade({
		duration : 7.0
	});
	return false;
}

var tagsPlusOnMouseOver = function() {
	// alert("test");
	$('minus').appear(5);

	return false;
}

var tagsPlusOnMouseOut = function() {
	// alert("test");
	$('minus').fade();
	return false;
}

/**
 * 
 * @param e
 */
var inviteNewUser = function(e) {
	alert("dd");
}

var addTagClick = function () {
	var jObject_tag = jQuery(this).closest('.tag');
	var tag = jObject_tag.attr('tag');
	var targetEmail = jObject_tag.closest('.contact').attr('id');
	jQuery.getJSON('/spine/network/addTag', {tag: tag, email: targetEmail}, function () {
		jObject_tag.addClass('direct_tag'); // the clicked tag is now a direct tag
		jObject_tag.attr('nb', parseInt(jObject_tag.attr('nb')) + 1); // nb property is increased by 1
		jObject_tag.find('.add_tag').removeClass('add_tag').addClass('remove_tag').text('-'); // toggle add/remove tag action
		reorderTags(jObject_tag.closest('.tags')); 
	});
	return false;
}

var removeTagClick = function () {
	var jObject_tag = jQuery(this).closest('.tag');
	var tag = jObject_tag.attr('tag');
	var targetEmail = jObject_tag.closest('.contact').attr('id');
	jQuery.getJSON('/spine/network/removeTag', {tag: tag, email: targetEmail}, function (data) {
		if(jObject_tag.attr('nb') == 1)
			jObject_tag.remove();
		else {
			jObject_tag.removeClass('direct_tag');
			jObject_tag.attr('nb', parseInt(jObject_tag.attr('nb')) - 1);
			jObject_tag.find('.remove_tag').removeClass('remove_tag').addClass('add_tag').text('+');  // toggle add/remove tag action
			reorderTags(jObject_tag.closest('.tags'));
		}
	});
	return false;
}

var reorderTags = function (jObject_tagList) {
	
	// Get tags and dispatch them into two arrays, according if they are direct or not
	var directTags = []
	var notDirectTags = []
	jObject_tagList.find('.tag').each(function () {
		self = jQuery(this);
		if(self.hasClass('direct_tag'))
			directTags.push(self)
		else
			notDirectTags.push(self)
	});
	
	// Sort the tags by "nb" ASC
	directTags.sort(function (a, b) {
		return parseInt(a.attr('nb')) - parseInt(b.attr('nb'))
	});
	notDirectTags.sort(function (a, b) {
		return parseInt(a.attr('nb')) - parseInt(b.attr('nb'))
	});
	
	// Merge the tags into one array
	var tags = jQuery.merge(notDirectTags, directTags);
	
	// Remove the tags in the DOM to put the new ordered ones
	jObject_tagList.find('.tag').remove();
	jQuery.each(tags, function () {
		this.prependTo(jObject_tagList);
	});
}


///**
// * 
// * 
// * @param e
// */
//var addTagUpdate = function(e, id){
//	
//	var response = eval("(" + e.responseText + ")");
//	
//	//alert(response.tag);
//	
//	var ul = $(id);
//	var li = document.createElement("li");
//	li.innerHTML = response.tag;
//	ul.insertBefore(li, ul.getElementsByTagName("li")[1]);
//	
//}
//
//
///**
// * 
// * @param e
// */
//var removeTagUpdate = function(e){
//	
//	$(e).fade({
//		duration : 2.0
//	});
//}