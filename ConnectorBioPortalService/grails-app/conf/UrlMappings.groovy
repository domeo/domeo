class UrlMappings {

	static mappings = {
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}

		"/specs"(view:"/index")
		"/"(view:"/index")
		"500"(view:'/error')
	}
}
