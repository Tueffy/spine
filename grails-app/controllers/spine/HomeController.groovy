package spine

class HomeController {

	def index = {
		if (session.user != null) {
			redirect (controller : 'network' , action : 'index')
		}
	}
}