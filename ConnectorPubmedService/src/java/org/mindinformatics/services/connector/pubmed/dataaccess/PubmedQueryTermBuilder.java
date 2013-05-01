package org.mindinformatics.services.connector.pubmed.dataaccess;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;

// http://www.ncbi.nlm.nih.gov/bookshelf/br.fcgi?book=helppubmed&part=pubmedhelp
// http://www.ncbi.nlm.nih.gov/bookshelf/br.fcgi?book=helplinks&part=linkshelp

/**
 * @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
 */
public class PubmedQueryTermBuilder {
	
	// http://www.ncbi.nlm.nih.gov/books/NBK3827/table/pubmedhelp.T43/?report=objectonly
	String[] stopwords = {
		"a", "about", "again", "all", "almost", "also", "although", "always", "among", "an", "and", "another", "any", "are", "as", "at",
		"be", "because", "been", "before", "being", "between", "both", "but", "by",
		"can", "could",
		"did", "do", "does", "done", "due", "during",
		"each", "either", "enough", "especially", "etc",
		"for", "found", "from", "further",
		"had", "has", "have", "having", "here", "how", "however",
		"i", "if", "in", "into", "is", "it", "its", "itself",
		"just",
		"kg", "km",
		"made", "mainly", "make", "may", "mg", "might", "ml", "mm", "most", "mostly", "must",
		"nearly", "neither", "no", "nor",
		"obtained", "of", "often", "on", "our", "overall",
		"perhaps", "pmid",
		"quite",
		"rather", "really", "regarding",
		"seem", "seen", "several", "should", "show", "showed", "shown", "shows", "significantly", "since", "so", "some", "such",
		"than", "that", "the", "their", "theirs", "them", "then", "there", "therefore", "these", "they", "this", "those", "through", "thus","to",
		"upon", "use", "used", "using",
		"various", "very",
		"was", "we", "were", "what", "when", "which", "while", "with", "within", "without", "would"
	};
	
	public static final Integer EARLIEST_PUBLICATION_START_YEAR = 1900;
	public static String AUTHOR_FIELD_TAG="AU";
	public static String JOURNAL_TITLE_FIELD_TAG="TA";
	public static String TITLE_FIELD_TAG="TI";
	public static String TITLE_AND_ABSTRACT_FIELD_TAG="TIAB";
	public static String MESH_TERM_FIELD_TAG="MH";
	public static String PUBLICATION_DATE_TERM_FIELD_TAG="DP";
	public static String PUBMED_ID_FIELD_TAG="PMID";
	public static String ALL_FIELD_TAG="ALL";
	
	private List<String> authors = new ArrayList<String>();
	private List<String> journalTitles = new ArrayList<String>();
	private List<String> titles = new ArrayList<String>();
	private List<String> titleAndAbstract = new ArrayList<String>();
	private List<String> meshTerms = new ArrayList<String>();
	private List<String> pubmedIds = new ArrayList<String>();
	private List<String> all = new ArrayList<String>();
	private List<String> publicationTypes = new ArrayList<String>();
	
	private Integer publicationStartMonthIndex;
	private Integer publicationEndMonthIndex;
	private Integer publicationStartYearIndex;
	private Integer publicationEndYearIndex;
	
	private Map<String,List<String>> tagToFieldValuesMap = new LinkedHashMap<String,List<String>>();
	
	public PubmedQueryTermBuilder(){
		tagToFieldValuesMap.put(AUTHOR_FIELD_TAG, authors);
		tagToFieldValuesMap.put(JOURNAL_TITLE_FIELD_TAG,journalTitles);
		tagToFieldValuesMap.put(TITLE_FIELD_TAG,titles);
		tagToFieldValuesMap.put(TITLE_AND_ABSTRACT_FIELD_TAG, titleAndAbstract);
		tagToFieldValuesMap.put(MESH_TERM_FIELD_TAG,meshTerms);
	}
	
	public void setPublicationDateRange(Integer startMonthIndex, Integer startYearIndex,Integer endMonthIndex, Integer endYearIndex){
		this.publicationStartMonthIndex = startMonthIndex;
		this.publicationStartYearIndex = startYearIndex;
		this.publicationEndMonthIndex = endMonthIndex;
		this.publicationEndYearIndex = endYearIndex;
	}
	public void addPublicationTypes(Collection<String> publicationTypes){
		this.publicationTypes.addAll(publicationTypes);
		
	}
	
	public void addPubmedIds(Collection<String> pubmedIds){
		this.pubmedIds.addAll(pubmedIds);
	}
	public void add(Collection<String> pubmedCentralIds){
		this.all.addAll(pubmedCentralIds);
	}
	public void addAuthor(String authorName){
		authors.add(authorName);
	}
	public void addAuthors(Collection<String> authorNames){
		authors.addAll(authorNames);
	}
	public void addJournalTitle(String journalTitle){
		journalTitles.add(journalTitle);
	}
	public void addJournalTitles(Collection<String>journalTitles){
		journalTitles.addAll(journalTitles);
	}
	public void addJournalArticleTitleWord(String title){
//		for(String word: stopwords) {
//			if(word.equals(title)) return;
//		}
		titles.add(title);
	}
	public void addJournalArticleTitleWords(Collection<String>titleWords){
//		for(String titleWord: titleWords) {
//			System.out.println(">> " + titleWord);
//			addJournalArticleTitleWord(titleWord);
//			//if(!stopwords.contains(titleWord)) titles.add(titleWord);
//		}
		
		titles.addAll(titleWords);
	}
//	public void addTitleAndAbstractSearchWord(String aTerm){
//		titleAndAbstract.add(aTerm);
//	}
//	public void addTitleAndAbstractSearchWords(Collection<String>searchWords){
//		titleAndAbstract.addAll(searchWords);
//	}
	public void addMeshTerm(String meshTerm){
		this.meshTerms.add(meshTerm);
	}
	public void addMeshTerms(Collection<String>meshTerms){
		this.meshTerms.addAll(meshTerms);
	}
	
	private String searchClause(List<String> fieldList,String fieldTag){
		StringBuilder builder = new StringBuilder();
		String connectingOp = "+AND+";
		for(String fieldValue: fieldList){
			builder.append(this.cleanupSearchTerm(fieldValue, fieldTag));
			builder.append("[");
			builder.append(fieldTag);
			builder.append("]");
			builder.append(connectingOp);
		}
		if (builder.length() > 0){
			return builder.substring(0,builder.length() - connectingOp.length());
		}
		return "";
	}
	/*
	private String getPublicationTypeSearchClause(){
		return getSearchClauseString(this.publicationTypes,PUBLICATION_TYPE_FIELD_TAG,"+OR+");
		
	}
	*/
	private String getPubmedIdSearchClause(){
		return getSearchClauseString(this.pubmedIds,PUBMED_ID_FIELD_TAG,"+OR+");
	}
	private String getAllSearchClause(){
		return getSearchClauseString(this.all,ALL_FIELD_TAG,"+OR+");
	}
	private String getSearchClauseString(Collection<String> operands,String fieldTag,String connectionOp){
		StringBuilder builder = new StringBuilder();
		builder.append("(");
		int i = 0;
		for(String operand : operands){
			
			builder.append(operand);
			builder.append("[");
			builder.append(fieldTag);
			builder.append("]");
			
			if(i < operands.size() -1){
				builder.append(connectionOp);
			}
			i++;
		}
		
		builder.append(")");
		
		return builder.toString();
		
	}
	private String publicationDateSearchTerm(){
		if(publicationStartYearIndex == null && publicationEndYearIndex == null){
			return null;
		}
		StringBuilder builder = new StringBuilder();
		if (this.publicationStartYearIndex != null){
			builder.append(this.dateTerm(null, publicationStartMonthIndex, publicationStartYearIndex));
		}
		if (publicationStartYearIndex == null && publicationEndYearIndex != null){
			builder.append(this.dateTerm(null,null,EARLIEST_PUBLICATION_START_YEAR));			
		}
		builder.append(":");
		if (this.publicationEndYearIndex != null){
			builder.append(this.dateTerm(null, publicationEndMonthIndex, publicationEndYearIndex));
		} else {
			GregorianCalendar calendar = new GregorianCalendar();
			System.out.println(calendar.get(GregorianCalendar.YEAR));
			builder.append(this.dateTerm(calendar.get(GregorianCalendar.DAY_OF_MONTH), calendar.get(GregorianCalendar.MONTH)+1, calendar.get(GregorianCalendar.YEAR)));
		}
		builder.append("[");
		builder.append(PUBLICATION_DATE_TERM_FIELD_TAG);
		builder.append("]");
		
		return builder.toString();
	}
	private String dateTerm(Integer day, Integer month, Integer year){
		if (year == null){
			return null;
		}
		StringBuilder builder = new StringBuilder();
		builder.append(year.toString());
		if(month != null){
			builder.append('/');
			builder.append(month.toString());
			if (day != null){
				builder.append('/');
				builder.append(day.toString());
			}
		}
		return builder.toString();		
	}
	private List<String> getSearchClauses(){
		List<String> searchClauses = new ArrayList<String>();
		Set<Entry<String,List<String>>> tagAndFieldValues = tagToFieldValuesMap.entrySet();
		for(Entry<String,List<String>> tagAndFieldValueList : tagAndFieldValues){
			String currentClause = this.searchClause(tagAndFieldValueList.getValue(),tagAndFieldValueList.getKey());
			if (StringUtils.isNotEmpty(currentClause)){
				searchClauses.add(currentClause);
			}
		}
		String publicationDateSearchTerm = publicationDateSearchTerm();
		if (StringUtils.isNotEmpty(publicationDateSearchTerm)){
			searchClauses.add(publicationDateSearchTerm);
		}
		if(pubmedIds.size() > 0){
			searchClauses.add(this.getPubmedIdSearchClause());
		}
		if(all.size() > 0){
			searchClauses.add(this.getAllSearchClause());
		}
		if(publicationTypes.size() > 0){
			//searchClauses.add(this.getPublicationTypeSearchClause());
		}
		return searchClauses;
		
	}
	public String toString(){
		StringBuilder builder = new StringBuilder();
		String connectingOp = "+AND+";
		for(String currentClause : this.getSearchClauses()){
				builder.append(currentClause);
				builder.append(connectingOp);
		}
		if (builder.length() > 0){
			return builder.substring(0,builder.length() - connectingOp.length());
		}
		return null;
	}
	private String cleanupSearchTerm(String searchTerm, String fieldTag){
		StringTokenizer tokenizer = new StringTokenizer(searchTerm);
		StringBuilder builder = new StringBuilder();
		while(tokenizer.hasMoreTokens()){
			String currentToken = tokenizer.nextToken();
			try {
				boolean isStopWord = false;
				for(String word: stopwords) {
					if(word.equals(currentToken.trim())) isStopWord=true;
				}
				if(!isStopWord) {
					builder.append(URLEncoder.encode(currentToken, "utf-8"));
					if (tokenizer.hasMoreTokens()){
						builder.append("["+fieldTag+"]+AND+");
					}
				}
			}catch(UnsupportedEncodingException e){
				continue;
			}
		}
		return builder.toString();
		
	}
	

}
