import org.mindinformatics.grails.domeo.dashboard.security.LoggingSecurityEventListener

// Place your Spring DSL code here
beans = {
    securityEventListener(LoggingSecurityEventListener)
}
