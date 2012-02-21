window.onload = function() {

	new Ajax.Autocompleter("autocomplete", "autocomplete_choices",
			"/spine/network/ajaxAutoComplete", {});

	Droppables.add('left', {
		accept : 'contact',
		hoverclass : 'hover',
		onDrop : function(e) {
			// alert(e.id);
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

var firstUser = null;

getFirstUser = function() {
	return firstUser;
}

setFirstUser = function(user) {
	firstUser = user;
}

updateSelectedUser = function(e) {
	// alert(e);
	// evaluate the JSON
	var user = eval("(" + e.responseText + ")");
	$("selectUser").fade();
	$("selectedUserName").innerHTML = user.firstName + ' ' + user.lastName;
	$("selectedCity").innerHTML = user.city;
	// $("selectedCountry").innerHTML = user.country;
	$("selectedImage").appear();
	$("selectedImage").src = "/spine/images/profiles/" + user.email + ".jpg";

	var container = $("selectedTags");

	var liList = container.childNodes;

	for ( var i = 0; i < liList.length; i++) {
		// alert (liList[i+1].nodeName);
		var li = liList[i];
		if (li.nodeName == "LI") {
			$(li).fade();
		}
	}

	new Ajax.Request('/spine/network/getTags/' + user.email, {
		asynchronous : true,
		evalScripts : true,
		onSuccess : function(transport) {
			var tagsJSON = transport.responseText;
			var tags = eval("(" + tagsJSON + ")");
			for ( var key in tags) {
				var new_element = document.createElement('li');
				new_element.innerHTML = "#" + key;
				container.insertBefore(new_element, container.firstchild);
				$(new_element).grow();
			}
		}
	});

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

inviteNewUser = function(e) {
	alert("dd");
}