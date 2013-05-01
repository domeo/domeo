class UrlMappings {

	static mappings = {
		
		"/login/auth" {
			controller = 'openId'
			action = 'auth'
		}
		
		"/login/openIdCreateAccount" {
			controller = 'openId'
			action = 'createAccount'
		}
		
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}
		
		
		"/" {
			controller = 'secure'
			action = 'index'
		}
		"/secure/" {
			controller = 'secure'
			action = 'index'
		}
		"/specs" (view:"/index")
		//"/"(view:"/index")
		"500"(view:'/error')
	}
}
