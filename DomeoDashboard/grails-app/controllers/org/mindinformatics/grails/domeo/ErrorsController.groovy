package org.mindinformatics.grails.domeo

class ErrorsController {

    def notFound = {
        log.error('404 Not Found: ' + request.forwardURI);
        render(view: "/notfound");
    }
    
    def forbidden = {
        log.error('403 Forbidden: ' + request.forwardURI);
        render(view: "/forbidden");
    }
}
