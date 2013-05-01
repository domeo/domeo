package org.mindinformatics.services.connector.pubmed.dataaccess;

/**
 * Common interface to facilitate generating UI for publications of different kinds
 * @author Marco
 *
 */
public interface PublicationI {
	public String getId();
	public String getAuthoritativeId();
	public String getAuthorNamesString();
	public String getTitle();
	public String getJournalPublicationInfoString();
	public String getPublicationDateString();
	public String getOntologyType();
}
