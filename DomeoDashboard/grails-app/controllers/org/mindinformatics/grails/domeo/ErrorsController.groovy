package org.mindinformatics.grails.domeo

class ErrorsController {

    def notFound = {
        log.info('404 Not Found: ' + request.forwardURI);
        render(view: "/notfound");
    }
    
    def forbidden = {
        log.info('403 Forbidden: ' + request.forwardURI);
        render(view: "/forbidden");
    }
}
