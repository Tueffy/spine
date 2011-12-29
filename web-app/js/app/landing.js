document.observe("dom:loaded", function() {
	
	// Handle registration form
	// May be generalized if needed
	
	function fireEvent(element,event){
	    if (document.createEventObject){
	        // dispatch for IE
	        var evt = document.createEventObject();
	        return element.fireEvent('on'+event,evt)
	    }
	    else{
	        // dispatch for firefox + others
	        var evt = document.createEvent("HTMLEvents");
	        evt.initEvent(event, true, true ); // event type,bubbling,cancelable
	        return !element.dispatchEvent(evt);
	    }
	}
	
	/**
	 * For a given event, go to the next Page / slide
	 * @param event
	 */
	function nextPage(event)
	{
		var current_slide = event.element().up('.form_slide');
		var next_slide = current_slide.next('.form_slide');
		current_slide.removeClassName('current');
		next_slide.addClassName('current');
	}
	
	/**
	 * For a given event, go to the previous page / slide
	 * @param event
	 */
	function previousPage(event)
	{
		var current_slide = event.element().up('.form_slide');
		var previous_slide = current_slide.previous('.form_slide');
		current_slide.removeClassName('current');
		previous_slide.addClassName('current');
	}
	
	/**
	 * Open the upload box associated with a button
	 * @param event
	 */
	function openUploadBox(event, element)
	{
		var file_input = Selector.findElement(element.siblings(), 'input[type=file]');
		if(file_input == null)
		{
			alert("ERROR : Upload box not found ! ");
			return false;
		}
//		alert(file_input.readAttribute('name'));
//		file_input.simulate('click');
//		file_input.fire('click');
		fireEvent(file_input, 'click');
	}

	var registration_form = $('registration_form');
	if(registration_form != null)
	{
		// Observes the click events on "next" buttons
		registration_form.on('click', 'input.next', nextPage); 
		
		// Observes the click events on "previous" buttons
		registration_form.on('click', 'input.prev', previousPage);
		
		// Observe the click events on "upload" buttons
		registration_form.on('click', 'div.upload_box input[type=button]', openUploadBox); 
		
		// Enable the file input click ; may look tricky don't modify except if you know what you're doing
		registration_form.on('click', 'div.upload_box input[type=file]', function (event, element) { return true; });
	}
	
});