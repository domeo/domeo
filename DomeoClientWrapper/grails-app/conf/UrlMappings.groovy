class UrlMappings {

	static mappings = {
		
		"/gwt/domeo/users/$id/info" (controller:"/users", action: "info")
		"/users/$id/info" (controller:"/users", action: "info")
		"/gwt/domeo/users/$id/groups" (controller:"/users", action: "groups")
		"/users/$id/groups" (controller:"/users", action: "groups")
		"/gwt/domeo/agents/$id/info" (controller:"/agents", action: "info")
		"/agents/$id/info" (controller:"/agents", action: "info")
		"/gwt/domeo/agents/$id/info" (controller:"/agents", action: "info")
		"/agents/$id/info" (controller:"/agents", action: "info")
		"/gwt/domeo/agents/$id/$version" (controller:"/agents", action: "software")
		"/agents/$id/$version" (controller:"/agents", action: "software")
		"/gwt/domeo/profile/$id/info" (controller:"/profiles", action: "info")
		"/profile/$id/info" (controller:"/profiles", action: "info")
		"/gwt/domeo/profile/$id/all" (controller:"/profiles", action: "all")
		"/profile/$id/all" (controller:"/profiles", action: "all")
		
		//"/tests/gene6606.html" (controller:'testPages', action:'gene6606')
        //"/tests/gene6606" (controller:'testPages', action:'gene6606') 
		
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}	

		"/"(view:"/index")
		"500"(view:'/error')
	}
}
