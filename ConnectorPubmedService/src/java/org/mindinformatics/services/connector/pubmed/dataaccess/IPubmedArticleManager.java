package org.mindinformatics.services.connector.pubmed.dataaccess;

import java.util.List;
import java.util.Map;

/**
 * @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
 */
public interface IPubmedArticleManager {
	
	// Supported query types
	public static final String QUERY_TYPE_TITLE = "title";
	//public static final String QUERY_TYPE_TITLE_AND_ABSTRACT = "titleAndAbstract";
	public static final String QUERY_TYPE_PUBMED_CENTRAL_IDS = "pubmedCentralIds";
	public static final String QUERY_TYPE_PUBMED_CENTRAL_ID = "pubmedCentralId";
	public static final String QUERY_TYPE_PUBMED_IDS = "pubmedIds";
	public static final String QUERY_TYPE_DOIS = "dois";
	
	/**
	 * This is used as a place holder for those requested items that don't have
	 * a suitable identifier.
	 */
	public static final String UNRECOGNIZED = "UNRECOGNIZED";
	
	/**
	 * PubMed search by terms with offset and range for pagination
	 * @param typeQuery		Specifies if the search terms are identifiers or terms and where to search them (title, abstract...)
	 * @param queryTerms 	The list of terms to be searched
	 * @param pubStartMonth	The starting month
	 * @param pubStartYear	The starting year
	 * @param pubEndMonth	The ending month
	 * @param pubEndYear	The ending year
	 * @param range			The number of results to be returned in one time
	 * @param offset		The offset in relation to the list of all the results
	 * @return The total number of hits, the pagination info and the list of returned results
	 */
	public Map<Map<String,String>, List<ExternalPubmedArticle>> searchPubmedArticles(
			String typeQuery,
			List<String> queryTerms, 
			Integer pubStartMonth, Integer pubStartYear,
			Integer pubEndMonth, Integer pubEndYear,
			Integer range, Integer offset);

	/**
	 * Returns the PubMed metadata of the article with the requested PubMed
	 * identifier
	 * @param pubmedId	The requested PubMed identifier
	 * @return	The correspondent PubMed metadata entry
	 */
	public ExternalPubmedArticle getPubmedArticle(String pubmedId);

	/**
	 * Returns the PubMed metadata of the articles with the requested PubMed
	 * identifiers
	 * @param pubmedIds	List of the requested identifiers
	 * @return The list of correspondent PubMed records
	 */
	public List<ExternalPubmedArticle> getPubmedArticles(List<String>pubmedIds);
	
	/**
	 * 
	 * @param typeQuery
	 * @param titleAndAbstractWords
	 * @param pubStartMonth
	 * @param pubStartYear
	 * @param pubEndMonth
	 * @param pubEndYear
	 * @return
	 */
	public List<ExternalPubmedArticle>  getPubmedArticles(
			String typeQuery, List<String> queryWords, 
			Integer pubStartMonth, Integer pubStartYear,
			Integer pubEndMonth, Integer pubEndYear);

	public abstract int getMaxNumberSearchResults();

	public abstract void setMaxNumberSearchResults(int maxNumberSearchResults);
}