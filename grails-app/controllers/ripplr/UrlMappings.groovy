package ripplr

class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        "/global" {
            controller = "ripple"
            action = "global"
        }

        "/timeline" {
            controller = "ripple"
            action = "dashboard"
        }

        "/" {
            controller = "ripple"
            action = 'global'
        }

        "/api/ripples"(resources: 'rippleRest')

        // "/"(view:"/index")
        "500"(view:'/error')
        "404"(view:'/notFound')
    }
}
