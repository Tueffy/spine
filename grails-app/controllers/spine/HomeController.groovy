package spine

class HomeController {
//small change
	def index = {
		if (session.user != null) {
			redirect (controller : 'network' , action : 'index')
		}
	}
}