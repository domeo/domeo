class UrlMappings {

	static mappings = {
		
		//"/user/account" (controller:"/secured", action: "userAccount")
		//"/user/settings" (controller:"/secured", action: "userSettings")
		
		"/users/$id/info" (controller:"/users", action: "info") 
		"/users/$id/groups" (controller:"/users", action: "groups")
		"/agents/$id/info" (controller:"/agents", action: "info")
		"/agents/$id/$version" (controller:"/agents", action: "software")
		
		"/profile/$id/info" (controller:"/profiles", action: "info")
		"/profile/$id/save" (controller:"/profiles", action: "save")
		"/profile/$id/all" (controller:"/profiles", action: "all")
		
		"/group/$id" (controller:"/dashboard", action: "showGroup")
		"/user/$id" (controller:"/dashboard", action: "showProfile")
		
		"/pubmed/$action?/$id" (controller:"/pubmed")
		
		//"/ncbo/$action" (controller:"/bioPortal")
		"/ncbo/textmine" (controller:"/bioPortalConnector", action: "/annotate")
		"/ncbo/$action" (controller:"/bioPortalConnector")
		
		"/nif/$action" (controller:"/nif")
		
		/*
		 * Authentication 
		 */
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
		
		/*
		"/secure/annotator" {
			controller = 'web'
			action = 'domeo'
		}
		*/
		
		
		"/secure/" {
			controller = 'secured'
			action = 'index'
		}
		"/specs" (view:"/index")
 
		"/" {
			controller = 'secured'
			action = 'index'
		}
		"500"(view:'/error')
        "403"(controller:"errors", action:"forbidden")
        "404"(controller:"errors", action:"notFound")
	}
}
