package org.mindinformatics.grails.domeo.plugin.bibliography

import org.mindinformatics.grails.domeo.dashboard.security.User
import org.mindinformatics.grails.domeo.plugin.bibliography.model.BibliographicEntry
import org.mindinformatics.grails.domeo.plugin.bibliography.model.BibliographicReference
import org.mindinformatics.grails.domeo.plugin.bibliography.model.UserBibliography

class BibliographyService {

	def createEntry(User user, def jsonRequest, def star) {
		UserBibliography ub = UserBibliography.findByUser(user);
		if(ub==null) {
			log.info("Creating bibliogrphy for user " + user.username);
			ub = new UserBibliography(user:user).save(flush:true)
		}
		
		def be;
		def reference;
		def jsonReference = jsonRequest.reference;
		if(jsonReference!=null) {
			if(reference==null && jsonReference.doi) {
				reference = BibliographicReference.findByDoi(jsonReference.doi);
			}
			if(reference==null && jsonReference.pmid) {
				reference = BibliographicReference.findByPubMedId(jsonReference.pmid);
			}
			if(reference==null && jsonReference.pmcid) {
				reference = BibliographicReference.findByPubMedCentralId(jsonReference.pmid);
			}
			
			if(reference==null) {
				reference = new BibliographicReference(
					title: jsonReference.title,
					journalName: jsonReference.journalName,
					journalIssn: jsonReference.journalIssn,
					publicationDate: jsonReference.publicationDate,
					authorNames: jsonReference.authorNames,
					publicationInfo: jsonReference.publicationInfo,
					publicationType: jsonReference.publicationType,
					text: jsonReference.unrecognized,
					
					doi: jsonReference.doi,
					pubMedId: jsonReference.pmid,
					pubMedCentralId: jsonReference.pmcid,
					publisherItemId: jsonReference.pii
				)
				log.info("Creating reference " + reference);
				reference.save(flush:true)
			}
			
			if(reference) be = BibliographicEntry.findByReferenceAndUserBibliography(reference, ub);
		}
		
		if(!reference) be = BibliographicEntry.findByUrlAndUserBibliography(jsonRequest.url, ub);

		if(be==null) {
			log.info(ub);
			be = new BibliographicEntry(
				url:jsonRequest.url,
				title:jsonRequest.label,
				reference: reference,
				userBibliography: ub,
				starred: star
			)
			log.info("Creating bibliographic entry " + be + " - " + be.userBibliography);
			be.save(flush:true)
			ub.entries.add(be);
		} else {
			be.starred = true;
		}
	}
}
