import spine.SpineService;

class BootStrap {

	SpineService spineService
	
    def init = { 
		spineService.getHotTags();
    }
	
    def destroy = {
		
    }
}
