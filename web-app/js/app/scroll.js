jQuery(document).ready(function(e) {
	
	function scrolling_callback(data)
	{
		jQuery('div.network_page:last').after(data);
	}
	
	function load_network_page() 
	{
		if(jQuery.trim(jQuery('div.network_page:last').html()) == '') return false;
		var page = jQuery('div.network_page').length + 1;
		var filter = jQuery('#filter_helper').val();
		jQuery.get(
				'./ajaxPage', 					// URL
				{ page: page, filter: filter}, 	// Data
				scrolling_callback 				// Callback
		);
	}
	
	jQuery(window).on('scroll', function () {
		if  (jQuery(window).scrollTop() == jQuery(document).height() - jQuery(window).height())
			load_network_page();
	});
});