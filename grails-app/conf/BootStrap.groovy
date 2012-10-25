import spine.SpineService;
import spine.NetworkService;

class BootStrap {

	SpineService spineService
	NetworkService networkService
	
    def init = { 
		spineService.getHotTags();
		networkService.initDefaultTags();
    }
	
    def destroy = {
		
    }
}
