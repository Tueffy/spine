
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
	jQuery('#right').on('submit', 'form.add_tag', addTagText);
	
	/**
	 * Set up live profile update
	 */
	setUpLiveProfileEdit();

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
	jQuery('#selectedCity').text(user.city);
	jQuery('#selectedCountry').text(user.country);
	jQuery('#selectedCompany').text(user.company);
	jQuery('#selectedDepartment').text(user.department);
	jQuery('#selectedJobTitle').text(user.jobTitle);
	jQuery('#selectedPhone').text('Phone: ' + user.phone);
	jQuery('#selectedFreeText').text('„' + user.freeText + '„');
	jQuery('#selectedImage').attr('src', '/spine/images/profiles/' + user.email + '.jpg');
	
	// Updating the badges list
	var badgesList = jQuery('#selectedBadges');
	badgesList.html('');
	jQuery.each(user.badges, function (i, badge) {
		var li = jQuery(document.createElement('li'));
		li.addClass('badge');
		li.html('<img src="/spine/images/badges/36x36/' + badge.image + '" title="' + badge.name + '" alt="' + badge.name + '" width="36" height="36" />');
		li.appendTo(badgesList);
	});
	
	// Updating the tag list
	var selectedTags = jQuery('#selectedTags');
	var selectedTagsMore = jQuery('#selectedTagsMore').hide();
	selectedTags.find('li').remove();
	selectedTagsMore.find('li').remove();
	
	var nb_tags = 0;
	jQuery.each(user.tags, function (tag, nb) // for each tag
	{
		var li = jQuery(document.createElement('li'));
		li.html('<span class="tag"><a href="/spine/network/index?filter='+ tag +'">' + tag + '</a></span><span class="nb">' + nb + '</span>');
		if(jQuery.inArray(tag, networkedUser.directTags) != -1) // check if the tag is a direct tag
			li.addClass('direct_tag');
		
		// Put the li element into the right ul element
		nb_tags++;
		if(nb_tags <= 10) 
			li.appendTo(selectedTags);
		else
			li.appendTo(selectedTagsMore);
	})
		
	// Managing the "All tags" link
	if(nb_tags > 10) {
		viewAllTags.show();
		viewAllTagsLink.text("More tags");
	}
	else
		viewAllTags.hide();
	
	jQuery('#details_panel > ul.description').show();
	
	// Go to the top : 
	window.location.hash = "#details_panel";
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

/**
 * Tag added by clicking the "+" link
 */
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
};

/**
 * Tag added by filling a text field
 */
var addTagText = function () {
	var $this = jQuery(this);
	var $contact = $this.closest('.contact');
	var $input = $this.find('input[name=tag]');
	var tag = $input.val();
	var targetEmail = $contact.attr('id');
	
	// Make the AJAX call
	jQuery.getJSON('/spine/network/addTag', {tag: tag, email: targetEmail}, function (data) {
		var tag_added = data.tag;
		var ok = false; 
		
		// Go through all tags to check if the tag is already applied to the targeted user
		$contact.find('.tags .tag').each(function () {
			var $tag = jQuery(this);
			if(tag_added == $tag.attr('tag')) // tag found on the user :) 
			{
				ok = true;
				// The targetted person is already tagged with this tag
				if($tag.hasClass('direct_tag'))
					return false;
				$tag.addClass('direct_tag'); // the tag is now a direct tag
				$tag.attr('nb', parseInt($tag.attr('nb')) + 1); // nb property is increased by 1
				$tag.find('.add_tag').removeClass('add_tag').addClass('remove_tag').text('-'); // toggle add/remove tag action
			}
		});
		
		if(!ok) // if it's a new tag, let's add it ! 
		{
			var $tag = jQuery(document.createElement('li'))
								.addClass('tag')
								.addClass('direct_tag')
								.attr('tag', tag_added)
								.attr('nb', 1)
								.text(tag_added)
								.append('<a class="remove_tag" href="#">-</a>')
								.appendTo($contact.find('.tags'));
			reorderTags($contact.find('.tags'));
		}
		
		// Clear text input
		$input.val('');
		
	});
	
	return false;
};

/**
 * Tag removed by clicking the "-" link
 */
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
};

var reorderTagsByNb = function (jObject_tagList) {
	
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
	
	// Sort the tags by "nb" DESC
	directTags.sort(function (a, b) {
		return parseInt(a.attr('tag')) - parseInt(b.attr('tag'))
	});
	notDirectTags.sort(function (a, b) {
		return parseInt(a.attr('tag')) - parseInt(b.attr('tag'))
	});
	
	// Merge the tags into one array
	var tags = jQuery.merge(notDirectTags, directTags);
	
	// Remove the tags in the DOM to put the new ordered ones
	jObject_tagList.find('.tag').remove();
	jQuery.each(tags, function () {
		this.prependTo(jObject_tagList);
	});
};

var reorderTags = function (jObject_tagList) {
	
	console.log('Re-ordering ! ');
//	console.log(jObject_tagList);
//	console.log(jObject_tagList.html());
	// Get tags and dispatch them into two arrays, according if they are direct or not
	var directTags = []
	var notDirectTags = []
	jObject_tagList.find('.tag').each(function () {
		self = jQuery(this);
		if(self.hasClass('direct_tag'))
			directTags.push(self)
		else
			notDirectTags.push(self)
//		console.log(self.attr('tag'));
	});
	
	// Sort the tags by "tag" ASC
	directTags.sort(function (a, b) {
		return b.attr('tag') - a.attr('tag')
	});
	notDirectTags.sort(function (a, b) {
		return b.attr('tag') - a.attr('tag')
	});
	
	// Merge the tags into one array
	var tags = jQuery.merge(notDirectTags, directTags);
	
	// Remove the tags in the DOM to put the new ordered ones
	jObject_tagList.find('.tag').remove();
	jQuery.each(tags, function () {
		this.prependTo(jObject_tagList);
	});
};

var setUpLiveProfileEdit = function () {
	jQuery('#left').on('dblclick', '#userJobTitle', { spanID:'userJobTitle', inputID:'userJobTitleEdit', propertyName:'jobTitle' }, setUpLiveProfileEditDblClick);
	jQuery('#left').on('blur', '#userJobTitleEdit', { spanID:'userJobTitle', inputID:'userJobTitleEdit', propertyName:'jobTitle' }, setUpLiveProfileEditBlur);
	jQuery('#left').on('dblclick', '#userDepartment', { spanID:'userDepartment', inputID:'userDepartmentEdit', propertyName:'department' }, setUpLiveProfileEditDblClick);
	jQuery('#left').on('blur', '#userDepartmentEdit', { spanID:'userDepartment', inputID:'userDepartmentEdit', propertyName:'department' }, setUpLiveProfileEditBlur);
	jQuery('#left').on('dblclick', '#userPhone', { spanID:'userPhone', inputID:'userPhoneEdit', propertyName:'phone' }, setUpLiveProfileEditDblClick);
	jQuery('#left').on('blur', '#userPhoneEdit', { spanID:'userPhone', inputID:'userPhoneEdit', propertyName:'phone' }, setUpLiveProfileEditBlur);
};

var setUpLiveProfileEditDblClick = function (event) {
	var $input = jQuery('#' + event.data.inputID);
	var $span = jQuery(this);
	if($input.length == 0) 
	{
		$input = jQuery(document.createElement('input'));
		$input.attr({
			'type' : 'text', 
			'id' : event.data.inputID
		});
		$input.width($span.width());
		$input.val($span.text());
		$span.hide();
		$input.insertAfter($span);
		$input.focus();
	}
	else 
	{
		$input.val($span.text());
		$span.hide();
		$input.show();
		$input.focus();
	}
};

var setUpLiveProfileEditBlur = function (event) {
	var $input = jQuery(this);
	var $span = jQuery('#' + event.data.spanID);
	var value = jQuery.trim($input.val());
	if(value != '') {
		jQuery.get('/spine/user/updateProfileAjax', {field: event.data.propertyName, data: value}, function (data) {
			$span.text(data[event.data.propertyName]);
		});
	}
	$input.hide();
	$span.show();
};

