package org.mindinformatics.domeo.grails.plugins.mailing

class MailingService {

    def mailService;
    def grailsApplication;
    def domeoConfigAccessService;
    
    /**
    * Notifies exception via Email to the system administrator (if defined)
    * @param service   Name of the service that failed
    * @param message   Message detailing the nature of the failure
    */
   def notifyProblemByEmail(String service, String message) {
       log.error(service + ' - ' + message);
       if(domeoConfigAccessService.doesAdministratorEmailAddressExists()) {
           def email = domeoConfigAccessService.getAdministratorEmailAddress();
           log.info('Sending ' + service + ' exception email to ' + email)
           try {
               mailService.sendMail {
                   to email
                   subject "Problem with " + service + " Service managed by " + domeoConfigAccessService.getAdministratorName() + " (" + domeoConfigAccessService.getAdministratorOrganization()+ ")"
                   html service + ": " + message
               }
           } catch(Exception ex) {
               log.error("Failed sending " + service + " exception email to " + email + " with message: " + message + " - "+ ex.getMessage());
           }
       } else {
           log.warn(domeoConfigAccessService.getDomeoConfigAdminMissingMessage());
       }
   }
}
