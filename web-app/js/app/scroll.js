jQuery(document).ready(function(e) {
	
	function load_network_page() 
	{
		if(jQuery.trim(jQuery('div.network_page:last').html()) == '') return false;
		var page = jQuery('div.network_page').length + 1;
		jQuery.get('./ajaxPage?page=' + page, function (data) {
			jQuery('div.network_page:last').after(data);
		});
	}
	
	jQuery(window).on('scroll', function () {
		if  (jQuery(window).scrollTop() == jQuery(document).height() - jQuery(window).height())
			load_network_page();
	});
});