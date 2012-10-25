class UrlMappings {

	static mappings = {
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}

		"/"(view:"/home/index")
		"/about"(view:"/home/about")
		"/how"(view:"/home/how")
		"/disclaimer"(view:"/home/disclaimer")
		"/feedback"(view:"/home/feedback")
		"/terms"(view:"/home/terms")
		"/user/forgot"(view:"/user/forgot")
		"/user/activate"(view:"/user/activate")
		"500"(view:'/error')
	}
}
