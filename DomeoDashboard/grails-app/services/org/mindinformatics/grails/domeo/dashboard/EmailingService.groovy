package org.mindinformatics.grails.domeo.dashboard

class EmailingService {

    def mailService;
    def grailsApplication;
    
    def sendAccountRequestConfirmation(def appBase, def userSignupCommand) {
        log.info('Sending confirmation email to ' + userSignupCommand.username)
        mailService.sendMail {
            to userSignupCommand.email
            subject "Confirmation of Domeo account creation"
            html "Dear " + userSignupCommand.firstName + ", <br/>"+
                " your account has been created and it is waiting to be activated by an administrator. <br/><br/>" + 
                "You will receive an email when the activation process is completed." +
                "<br/><br/>Sincerely, <br/> "+ grailsApplication.config.domeo.administrator + "<br/>" +
                " The Domeo Annotation Tool team";
        }
    }
    
    def sendAccountConfirmation(def appBase, def accountRequest) {
        log.info('Sending confirmation email to ' + accountRequest.username)
        mailService.sendMail {
            to accountRequest.email
            subject "Domeo account activated"
            html "Dear " + accountRequest.firstName + ", <br/>"+
                " your account has been activated.<br/><br/>" +
                "Please contact us if you experience login issues." +
                "<br/><br/>Sincerely, <br/> "+ grailsApplication.config.domeo.administrator + "<br/>" +
                " The Domeo Annotation Tool team";
        }
    }
    
    def sendApprovalRequest(def appBase, def userSignupCommand) {
        log.info('Sending approval request email for ' + userSignupCommand.email)
        mailService.sendMail {
            to grailsApplication.config.domeo.admin.email.to
            subject "New user request awaiting for confirmation"
            html "<b>Dear Paolo,</b> a new user request by " + userSignupCommand.email + "is awaiting for confirmation"
        }
    }
    
    def broadcastMessage(def appBase, def destination, def type, def body) {
        log.info('Broadcasting email to all ' + destination)
        mailService.sendMail {
            to destination
            subject "Domeo Annotation Tool - " + type
            html    "<b>Dear Domeo user,<br/> if you are receiving this email is because you are a user of the Domeo installation at "+ appBase +".<br/><br/>" +
                    "We would like to communicate that <br/>" +
                    body + "<br/><br/>" +
                    "Sincerely,<br/>" +
                    grailsApplication.config.domeo.administrator +
                    "<br/>"
        }
    }
}
