package org.mindinformatics.services.connector.pubmed.dataaccess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mindinformatics.services.connector.pubmed.fetch.PubmedArticle;
import org.mindinformatics.services.connector.pubmed.fetch.PubmedArticleSet;

/**
 * @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
 */
public class PubmedArticleManagerImpl implements IPubmedArticleManager {
	
    private static final Log logger = LogFactory
            .getLog(PubmedArticleManagerImpl.class);
    
	/**
	 * The MesH publication types that we currently support. We are hard coding them currently
	 */
	private static final String[] ALLOWABLE_PUBLICATION_TYPES = {"Journal Article","Comment","News","Newspaper Article","Letter"};
	/**
	 * Creating the PubMed Search Agent as singleton
	 */
	private PubmedSearchAgent pubmedSearchAgent = PubmedSearchAgent.getInstance();
	
	private static final int DEFAULT_MAX_NUMBER_SEARCH_RESULTS = 90;
	private int maxNumberSearchResults = DEFAULT_MAX_NUMBER_SEARCH_RESULTS;
	
	public PubmedArticleManagerImpl(String proxyIp, String proxyPort) {
		pubmedSearchAgent.setProxyIp(proxyIp);
		pubmedSearchAgent.setProxyPort(proxyPort);
	}
	
	/* (non-Javadoc)
	 * @see org.mindinformatics.swan.pubmed.dataaccess.PubmedArticleDAO#getMaxNumberSearchResults()
	 */
	public int getMaxNumberSearchResults() {
		return maxNumberSearchResults;
	}
	/* (non-Javadoc)
	 * @see org.mindinformatics.swan.pubmed.dataaccess.PubmedArticleDAO#setMaxNumberSearchResults(int)
	 */
	public void setMaxNumberSearchResults(int maxNumberSearchResults) {
		this.maxNumberSearchResults = maxNumberSearchResults;
	}
	
	/*
	 * Single PubMed record retrieval. 
	 * 
	 * This is typically used to retrieve the PubMed record of the loaded document.
	 * 
	 * (non-Javadoc)
	 * @see org.mindinformatics.services.connector.pubmed.dataaccess.IPubmedArticleManager#getPubmedArticle(java.lang.String)
	 */
	@Override
	public ExternalPubmedArticle getPubmedArticle(String pubmedId){
		List<String> pubmedIds = new ArrayList<String>();
		pubmedIds.add(pubmedId);
		
		List<ExternalPubmedArticle> queryResults = this.getPubmedArticles(pubmedIds);
		return (queryResults.size() == 0)? null : queryResults.get(0);
	}	
	
	/*
	 * Multiple PubMed records retrieval through identifiers. 
	 * 
	 * An example of usage of this method is after running a terms based search. 
	 * The returned identifiers are then used to fetch the full records.
	 * 
	 * (non-Javadoc)
	 * @see org.mindinformatics.services.connector.pubmed.dataaccess.IPubmedArticleManager#getPubmedArticles(java.util.List)
	 */
	@Override
	public List<ExternalPubmedArticle> getPubmedArticles(List<String>pubmedIds) {
	    try {
    		PubmedArticleSet results = pubmedSearchAgent.fetchPubmedDocuments(pubmedIds);
    		return this.convertToExternalPubmedArticles(results);
	    } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
	}
	
	/*
	 * Search of PubMed articles records with pagination.
	 * 
	 * (non-Javadoc)
	 * @see org.mindinformatics.services.connector.pubmed.dataaccess.IPubmedArticleManager#searchPubmedArticles(java.lang.String, java.util.List, java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public  Map<Map<String,String>, List<ExternalPubmedArticle>> searchPubmedArticles(
			String typeQuery, List<String> queryTerms, 
			Integer pubStartMonth, Integer pubStartYear,
			Integer pubEndMonth, Integer pubEndYear,
			Integer range, Integer offset) {
		
		System.out.println("A1******* "+typeQuery);
		PubmedQueryTermBuilder termBuilder = new PubmedQueryTermBuilder();
		// We are restricting the  publication types to a restricted list
		// This restriction might be reconsidered
		List<String> pubTypes = new ArrayList<String>();
		for(String theType : ALLOWABLE_PUBLICATION_TYPES){
			pubTypes.add(theType.replace(" ", "+"));
		}

		if(CollectionUtils.isNotEmpty(queryTerms)) {
			System.out.println("******* "+typeQuery);
			if(typeQuery.equals(QUERY_TYPE_TITLE)) {
				termBuilder.addJournalArticleTitleWords(queryTerms);
			} else if(typeQuery.equals(QUERY_TYPE_PUBMED_IDS)) {
				termBuilder.addPubmedIds(queryTerms);
				this.maxNumberSearchResults = queryTerms.size();
			}else if(typeQuery.equals(QUERY_TYPE_DOIS)) {
				termBuilder.add(queryTerms);
			} else if(typeQuery.equals(QUERY_TYPE_PUBMED_CENTRAL_IDS)) {
				termBuilder.add(queryTerms);
			} else {
				termBuilder.add(queryTerms);
			}
		} 
	
		termBuilder.setPublicationDateRange(pubStartMonth, pubStartYear, pubEndMonth, pubEndYear);
		
		// The Integer is the total number of results
		int offsetValidated = 0;
		int maxResultsValidated = 0;
		Map<Integer,PubmedArticleSet> results;
		if(range>0 && offset>=0) {
			offsetValidated = offset;
			maxResultsValidated = range;
			results = pubmedSearchAgent.fetchWithStats(termBuilder.toString(), maxResultsValidated, offsetValidated);
		} else {
			// This is just returning the first group of results
			maxResultsValidated = this.getMaxNumberSearchResults();
			results = pubmedSearchAgent.fetchWithStats(termBuilder.toString(), this.getMaxNumberSearchResults(), 0);
		}
		
		Map<Map<String,String>, List<ExternalPubmedArticle>> mapToReturn = new HashMap<Map<String,String>, List<ExternalPubmedArticle>>();
		
		if (results == null){ 
			Map<String,String> stats = new HashMap<String,String>();
			stats.put("total", Integer.toString(0));
			stats.put("exception", "PubmedArticleManagerImpl.searchPubmedArticles().nullresults");
			
			mapToReturn.put(stats, new ArrayList<ExternalPubmedArticle>());
			return mapToReturn;
		}
		
		int totalResults = results.keySet().iterator().next();
		List<ExternalPubmedArticle> convertedResults = convertToExternalPubmedArticles(results.values().iterator().next());
		
		Map<String,String> stats = new HashMap<String,String>();
		stats.put("total", Integer.toString(totalResults));
		stats.put("range", Integer.toString(maxResultsValidated));
		stats.put("offset", Integer.toString(offsetValidated));

		mapToReturn.put(stats, convertedResults);
		return mapToReturn;
	}
	
	/**
	 * Converts the set of article results into a list of suitable objects.
	 * @param results	The list of records to convert
	 * @return The list of objects
	 */
	private List<ExternalPubmedArticle> convertToExternalPubmedArticles(PubmedArticleSet records) {
		List<ExternalPubmedArticle> articles = new ArrayList<ExternalPubmedArticle>();
		for(PubmedArticle currentArticle : records.getPubmedArticle()){
			if (currentArticle == null){
				articles.add(null);
			} else {
				articles.add(new ExternalPubmedArticle(currentArticle));
			}
		}
		return articles;
	}
	
	/*
     * (non-Javadoc)
     * @see org.mindinformatics.services.connector.pubmed.dataaccess.IPubmedArticleManager#getPubmedArticles(java.lang.String, java.util.List, java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.Integer)
     */
	public List<ExternalPubmedArticle> getPubmedArticles(
			String typeQuery, List<String>titleAndAbstractWords, 
			Integer pubStartMonth, Integer pubStartYear,
			Integer pubEndMonth, Integer pubEndYear) {
		
		System.out.println("A2******* "+typeQuery);
		
		PubmedQueryTermBuilder termBuilder = new PubmedQueryTermBuilder();
		//We are restricting the  publication types to include these
		List<String> pubTypes = new ArrayList<String>();
		for(String theType : ALLOWABLE_PUBLICATION_TYPES){
			pubTypes.add(theType.replace(" ", "+"));
		}
		
		// TODO To be considered 
		// termBuilder.addPublicationTypes(pubTypes);
		if(CollectionUtils.isNotEmpty(titleAndAbstractWords)){
			System.out.println("******* "+typeQuery);
			if(typeQuery.equals(QUERY_TYPE_TITLE)) 
				termBuilder.addJournalArticleTitleWords(titleAndAbstractWords);
			else if(typeQuery.equals(QUERY_TYPE_PUBMED_IDS)) {
				termBuilder.addPubmedIds(titleAndAbstractWords);
				this.maxNumberSearchResults = titleAndAbstractWords.size();
			}else if(typeQuery.equals(QUERY_TYPE_DOIS)) {
				termBuilder.add(titleAndAbstractWords);
			} else if(typeQuery.equals(QUERY_TYPE_PUBMED_CENTRAL_IDS)) {
				termBuilder.add(titleAndAbstractWords);
			} else if(typeQuery.equals(QUERY_TYPE_PUBMED_CENTRAL_ID)) {
				termBuilder.add(titleAndAbstractWords);
			} 
		}
		termBuilder.setPublicationDateRange(pubStartMonth, pubStartYear, pubEndMonth, pubEndYear);
		
		PubmedArticleSet results = pubmedSearchAgent.fetch(termBuilder.toString(), this.getMaxNumberSearchResults(), 0);
		if (results == null){
			return null;
		}
		List<ExternalPubmedArticle> convertedResults = convertToExternalPubmedArticles(results);
		return convertedResults;
	}
	
	
//	public List<ExternalPubmedArticle> getPubmedArticles(
//			String typeQuery, List<String>titleAndAbstractWords, 
//			Integer pubStartMonth, Integer pubStartYear,
//			Integer pubEndMonth, Integer pubEndYear, 
//			String maxResults, String offset){
//		
//		PubmedQueryTermBuilder termBuilder = new PubmedQueryTermBuilder();
//		//We are restricting the  publication types to include these
//		List<String> pubTypes = new ArrayList<String>();
//		for(String theType : ALLOWABLE_PUBLICATION_TYPES){
//			pubTypes.add(theType.replace(" ", "+"));
//		}
//		//termBuilder.addPublicationTypes(pubTypes);
//		if(CollectionUtils.isNotEmpty(titleAndAbstractWords)){
//			if(typeQuery.equals("titleAndAbstract")) 
//				termBuilder.addTitleAndAbstractSearchWords(titleAndAbstractWords);
//			else if(typeQuery.equals("pubmedIds")) {
//				termBuilder.addPubmedIds(titleAndAbstractWords);
//				this.maxNumberSearchResults = titleAndAbstractWords.size();
//			}else if(typeQuery.equals("dois")) {
//				termBuilder.add(titleAndAbstractWords);
//			} else if(typeQuery.equals("pubmedCentralIds"))
//				termBuilder.add(titleAndAbstractWords);
//		}
//		termBuilder.setPublicationDateRange(pubStartMonth, pubStartYear, pubEndMonth, pubEndYear);
//		
//		System.out.println(termBuilder.toString());
//		
//		PubmedArticleSet results = pubmedSearchAgent.fetch(termBuilder.toString(), Integer.parseInt(maxResults), Integer.parseInt(offset));
//		if (results == null){
//			return null;
//		}
//		List<ExternalPubmedArticle> convertedResults = convertToExternalPubmedArticles(results);
//		/*
//		CollectionUtils.filter(convertedResults, new Predicate(){
//			public boolean evaluate(Object object) {
//				return ((ExternalPubmedArticle)object).getOntologyType() != null;
//				
//			}});
//			*/
//		return convertedResults;
//	}
	
	
	
	
//	/* (non-Javadoc)
//	 * @see org.mindinformatics.swan.pubmed.dataaccess.PubmedArticleDAO#getPubmedArticles(java.util.List, java.util.List, java.util.List, java.util.List, java.util.List, java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.Integer)
//	 */
//	public List<ExternalPubmedArticle> getPubmedArticles(List<String>pubmedIdList,List<String>journalArticleTitleWords,List<String>titleAndAbstractWords,List<String>meshTerms,List<String>authorNames,Integer pubStartMonth,Integer pubStartYear,Integer pubEndMonth,Integer pubEndYear){
//		PubmedQueryTermBuilder termBuilder = new PubmedQueryTermBuilder();
//		//We are restricting the  publication types to include these
//		List<String> pubTypes = new ArrayList<String>();
//		for(String theType : ALLOWABLE_PUBLICATION_TYPES){
//			pubTypes.add(theType.replace(" ", "+"));
//		}
//		termBuilder.addPublicationTypes(pubTypes);
//		
//		if (CollectionUtils.isNotEmpty(journalArticleTitleWords)){
//			termBuilder.addJournalArticleTitleWords(journalArticleTitleWords);
//		}
//		if(CollectionUtils.isNotEmpty(titleAndAbstractWords)){
//			termBuilder.addTitleAndAbstractSearchWords(titleAndAbstractWords);
//		}
//		if (CollectionUtils.isNotEmpty(meshTerms)){
//			termBuilder.addMeshTerms(meshTerms);
//		}
//		if(CollectionUtils.isNotEmpty(authorNames)){
//			termBuilder.addAuthors(authorNames);
//		}
//		if (CollectionUtils.isNotEmpty(pubmedIdList)){
//			termBuilder.addPubmedIds(pubmedIdList);
//		}
//		termBuilder.setPublicationDateRange(pubStartMonth, pubStartYear, pubEndMonth, pubEndYear);
//		
//		PubmedArticleSet results = pubmedSearchAgent.fetch(termBuilder.toString(), this.getMaxNumberSearchResults(), 0);
//		
//		if (results == null){
//			return null;
//		}
//		List<ExternalPubmedArticle> convertedResults = convertToExternalPubmedArticles(results);
//		/*
//		CollectionUtils.filter(convertedResults, new Predicate(){
//			public boolean evaluate(Object object) {
//				return ((ExternalPubmedArticle)object).getOntologyType() != null;
//				
//			}});
//			*/
//		return convertedResults;
//	}
	/* (non-Javadoc)
	 * @see org.mindinformatics.swan.pubmed.dataaccess.PubmedArticleDAO#getPubmedArticles(java.util.List)
	 */

}
