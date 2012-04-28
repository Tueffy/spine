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

/**
 * Landing namespace 
 */
var landing = {};

landing.form = {
		
	// Properties
	
	jForm: null, 
	jFormPagination: null, 
	jFormPages: null,
	jFormNavigation: null, 
	
	// Functions
	
	/**
	 * Initialize the registration form
	 */
	init: function () {
		var self = this;
		jQuery(document).ready(function () {
			self.jForm = jQuery('form.registration');
			self.jFormPagination = self.jForm.find('.pagination');
			self.jFormPages = self.jForm.find('.page');
			self.jFormNavigation = self.jForm.find('.controls');
			
			// Events
			
			// Trap submit event
			self.jForm.on('submit', function () {
				self.submit();
			})
			
			// Form navigation
			self.jFormNavigation.on('click', 'a.next', function () { 
				self.nextPage();
			});
			self.jFormNavigation.on('click', 'a.previous', function () {
				self.previousPage();
			});
			
			self.goToPage(1);
		});
	}, 
	
	submit: function () {
		if(this.getCurrentPage() < this.getNbOfPages()) {
			this.nextPage();
			return false;
		}
	}, 
	
	/**
	 * Get the current step or page
	 * @return int
	 */
	getCurrentPage: function () {
		var self = this;
		var i = 1;
		var current = 1;
		self.jFormPagination.find('li').each(function () {
			if(jQuery(this).hasClass('current')) {
				current = i;
				return;
			}
			i++;
		});
		
		return current;
	}, 
	
	/**
	 * Jump to the n-th page
	 * @param int n
	 */
	goToPage: function (n) {
		var i = 1;
		this.jFormPages.hide();
		jQuery(this.jFormPages[n - 1]).show();
		
		var jFormPaginationItems = this.jFormPagination.find('li');
		jFormPaginationItems.removeClass('current');
		jQuery(jFormPaginationItems[n - 1]).addClass('current');
		
		if(n == 1) // The first page
			this.jFormNavigation.find('a.previous').css('visibility', 'hidden');
		else 
			this.jFormNavigation.find('a.previous').css('visibility', 'visible');
		
		if(n == this.getNbOfPages())
			this.jFormNavigation.find('a.next').text('Submit');
		else
			this.jFormNavigation.find('a.next').text('Next');
	},
	
	/**
	 * Get the number of pages in the form
	 */
	getNbOfPages: function () {
		return this.jFormPages.length
	}, 
	
	/**
	 * Move to the next page
	 */
	nextPage: function () {
		var current_page = this.getCurrentPage();
		var nb_pages = this.getNbOfPages();
		if(current_page == nb_pages) {
			this.jForm.submit();
		}
		else 
			this.goToPage(current_page + 1);
	}, 
	
	/**
	 * Move to the previous page
	 */
	previousPage: function () {
		var current_page = this.getCurrentPage();
		var nb_pages = this.getNbOfPages();
		if(current_page > 1)
			this.goToPage(current_page - 1);
	}
	
}