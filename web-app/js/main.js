$(document).ready(function(e) {
	
	function emptyInputText(jObject)
	{
		if(jObject.val() == jObject.attr('default'))
			jObject.val('');
	}
	
	function unEmptyInputText(jObject)
	{
		if($.trim(jObject.val()) == '')
			jObject.val(jObject.attr('default'));
	}
	
    // Erease / refill default text in the search box
	$('#header').on('focus', '.search', function () { emptyInputText($(this)) });
	$('#header').on('blur', '.search', function () { unEmptyInputText($(this)) });
	
	// Erease / refill default text in the filter box
	$('#container .feed  .filter').on('focus', 'input[type=text]', function () { emptyInputText($(this)) });
	$('#container .feed  .filter').on('blur', 'input[type=text]', function () { unEmptyInputText($(this)) });

});