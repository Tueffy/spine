jQuery(document).ready(function(e) {
	
	function emptyInputText(jObject)
	{
		if(jObject.val() == jObject.attr('default'))
			jObject.val('');
	}
	
	function unEmptyInputText(jObject)
	{
		if(jQuery.trim(jObject.val()) == '')
			jObject.val(jObject.attr('default'));
	}
	
    // Erease / refill default text in the search box
	jQuery('#header').on('focus', '.search', function () { emptyInputText(jQuery(this)) });
	jQuery('#header').on('blur', '.search', function () { unEmptyInputText(jQuery(this)) });
	
	// Erease / refill default text in the filter box
	jQuery('#container .feed  .filter').on('focus', 'input[type=text]', function () { emptyInputText(jQuery(this)) });
	jQuery('#container .feed  .filter').on('blur', 'input[type=text]', function () { unEmptyInputText(jQuery(this)) });

});