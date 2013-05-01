package org.mindinformatics.services.connector.pubmed.dataaccess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.mindinformatics.services.connector.pubmed.fetch.ArticleId;
import org.mindinformatics.services.connector.pubmed.fetch.Author;
import org.mindinformatics.services.connector.pubmed.fetch.MeshHeading;
import org.mindinformatics.services.connector.pubmed.fetch.Pagination;
import org.mindinformatics.services.connector.pubmed.fetch.PubDate;
import org.mindinformatics.services.connector.pubmed.fetch.PublicationType;
import org.mindinformatics.services.connector.pubmed.fetch.PubmedArticle;
import org.mindinformatics.services.connector.pubmed.fetch.QualifierName;


public class ExternalPubmedArticle implements PublicationI {
	private PubmedArticle article;
	private String swanId;
	
	private static String[] MONTH_ABBREV_ARRAY = {"JAN","FEB","MAR","APR","MAY","JUN","JUL","AUG","SEP","OCT","NOV","DEC"};
	private static  List<String> MONTH_ABBREVS  = Arrays.asList(MONTH_ABBREV_ARRAY);
	
	
	ExternalPubmedArticle(PubmedArticle article){
		super();
		this.article = article;
	}
	public static List<String> getMonthAbbrevs(){
		if (MONTH_ABBREVS == null){
			MONTH_ABBREVS = Arrays.asList(MONTH_ABBREV_ARRAY);
		}
		return MONTH_ABBREVS;
	}
	
	public String getOntologyType(){
		/*
		if(this.isJournalArticle() || this.isLetter())return JournalArticle.TYPE.toString();
		if(this.isJournalComment())return JournalComment.TYPE.toString();
		if(this.isJournalNews())return JournalNews.TYPE.toString();
		if(this.isNewspaperArticle())return NewspaperArticle.TYPE.toString();
		*/
		return null;
	}
	
	
	
	public class ExternalAuthor{
		private Author theAuthor;
		ExternalAuthor(Author anAuthor){
			theAuthor = anAuthor;
		}
		public String getFirstName(){		  
		    return this.getFirstAndMiddle()[0];
		}
		private String[] getFirstAndMiddle(){
		    String[] firstAndMiddle = {null,null};
		    String foreName = this.getForname();
		    if(StringUtils.isNotEmpty(foreName)){
			String[] theSplit = foreName.split(" ");
			for(int i = 0; i < theSplit.length && i < firstAndMiddle.length; i++){
			    firstAndMiddle[i] = theSplit[i];
			}
		    }
		    return firstAndMiddle;
		    
		}
		
		public String getForname(){
			if (theAuthor.getForeName() == null){
				return null;
			}
			return theAuthor.getForeName().getContent();
		}
		public String getSurname(){
			if (theAuthor.getLastName() == null){
				return null;
			}
			return theAuthor.getLastName().getContent();
		}
		public String getInitials(){
			if (theAuthor.getInitials() == null){
				return null;
			}
			return theAuthor.getInitials().getContent();
		}
		public String getMiddlename(){
			if (theAuthor.getMiddleName() == null){
				return this.getFirstAndMiddle()[1];
			}
			return theAuthor.getMiddleName().getContent();
		}
		public String getSuffix(){
			if(theAuthor.getSuffix() == null){
				return null;
			}
			return theAuthor.getSuffix().getContent();
		}
		public String getFullname(){
			StringBuilder builder = new StringBuilder();
			if (!StringUtils.isEmpty(this.getSurname())){
				builder.append(this.getSurname());
			}
			if (!StringUtils.isEmpty(this.getForname())){
				builder.append(", ");
				builder.append(this.getForname());
			}
			if (!StringUtils.isEmpty(this.getInitials())){
				builder.append(" (");
				builder.append(this.getInitials());
				builder.append(")");
			}
			return builder.toString();
		}
	}
	//getAuthoredBy_asPerson()
	public String getAuthoritativeId(){
		try {
			return article.getMedlineCitation().getPMID().getContent();
		}catch(NullPointerException e){
			return null;
		}
	}
	/**
	 * For now, returning the name of the database as it appears in 
	 * http://www.ncbi.nlm.nih.gov/entrez/query/static/eutils_help.html#PrimaryIDs
	 * @return the database name
	 */
	public String getAuthoritativeSource(){
		return "pubmed";
	}
	public String getDOI(){
		List<ArticleId> articleIds =  article.getPubmedData().getArticleIdList().getArticleId();
		for(ArticleId articleId : articleIds){
			if (articleId.getIdType().equals("doi")){
				return articleId.getContent();
			}
		}
		return null;
	}
	public String getPMC(){
		List<ArticleId> articleIds =  article.getPubmedData().getArticleIdList().getArticleId();
		for(ArticleId articleId : articleIds){
			//System.out.println("type: " + articleId.getIdType());
			if (articleId.getIdType().equals("pmc")){
				return articleId.getContent();
			}
		}
		return null;
	}
	//getEnteredBy()
	public List<ExternalAuthor> getHasAuthors(){
		List<Author> authors = null;
		try {
			authors = this.article.getMedlineCitation().getArticle().getAuthorList().getAuthor();
		}catch(NullPointerException e){
			authors = new ArrayList<Author>();
		}
		List<ExternalAuthor> returnList = new ArrayList<ExternalAuthor>();
		for(Author currentAuthor : authors){
			//Case where we have authors that are not people. Can't handle this yet
			if (currentAuthor.getLastName() == null && currentAuthor.getInitials() == null){
				continue;
			}
			returnList.add(new ExternalAuthor(currentAuthor));
		}
		return returnList;
		
	}
	public String getCreationDate(){return null;}
	public String getAuthorNamesString(){
		StringBuilder builder = new StringBuilder();
		String separator = ", ";
		List<ExternalAuthor> authors = getHasAuthors();
		if (authors.isEmpty()){
			return "No Authors Listed";
		}
		for(ExternalAuthor currentAuthor : authors){
			builder
				.append(currentAuthor.getSurname())
				.append(" ")
				.append(currentAuthor.getInitials())
				.append(", ");
		}
		if (builder.length() > 0){
			builder.delete(builder.length() - separator.length(), builder.length());
		}
		return builder.toString();
	}
	//public String getImportDate(){return null;}
	public String getISSN(){
		try {
		return article.getMedlineCitation().getArticle().getJournal().getISSN().getContent();
		} catch(NullPointerException e){
			return null;
		}
	}
	public String getIssue(){
		try{
			return article.getMedlineCitation().getArticle().getJournal().getJournalIssue().getIssue().getContent();
		}catch(NullPointerException e){
			return null;
		}
	}
	public String getJournalName(){
		try{
			return article.getMedlineCitation().getArticle().getJournal().getTitle().getContent();
		}catch(NullPointerException e){
			return null;
		}
	}
	public String getLocation(){
		return null;
	}

	public String getPagination(){
		try {
			Pagination pagination = article.getMedlineCitation().getArticle().getPagination();
			if (pagination == null){
				return null;
			}
			StringBuilder builder = new StringBuilder();
			for(Object currentPagination : pagination.getContent()){
				builder.append(BeanUtils.getProperty(currentPagination, "content"));
				builder.append(",");
			}
			if (builder.length()> 0){
				return builder.substring(0, builder.length() -1);
			} else {
				return null;
			}
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}

	
	/**
	 * Return date in http://www.w3.org/2001/XMLSchema#date format
	 * @return the date string
	 */
	public PubDate getPublicationDate(){
		PubDate pubDate = null;
	
		try {
		  pubDate = article.getMedlineCitation().getArticle().getJournal().getJournalIssue().getPubDate();
		}catch(NullPointerException e){
			return null;
		}
		return pubDate;
	}
	public Integer getPublicationDay(){
		PubDate pubDate = this.getPubDate();
		if (pubDate.getDay() != null){
			return new Integer(pubDate.getDay().getContent());
		} else {
			if (pubDate.getMedlineDate() != null && pubDate.getMedlineDate().getContent() != null ){
				String medlineDate = pubDate.getMedlineDate().getContent();
				int firstSpaceIdx = medlineDate.indexOf(" ");
				int secondSpaceIdx = medlineDate.indexOf(" ",firstSpaceIdx + 1);
				int dateDash = medlineDate.indexOf("-",secondSpaceIdx);
				int endOfDayIdx = -1;
				if (dateDash > 0){
					endOfDayIdx = dateDash;
				} else {
					endOfDayIdx = medlineDate.length();
				}
				int putativeDay = NumberUtils.toInt(medlineDate.substring(secondSpaceIdx+1,endOfDayIdx).trim());
				return (putativeDay == 0) ? null : putativeDay;
			}
			return null;
		}
		
	}
	public Integer getPublicationMonth(){
		PubDate pubDate = this.getPubDate();
		String monthString = null;
		if (pubDate.getMonth() != null){
			monthString = pubDate.getMonth().getContent();
		} else if (pubDate.getMedlineDate() != null && pubDate.getMedlineDate().getContent() != null){
			String medlineDate = pubDate.getMedlineDate().getContent();
			int firstSpaceIdx = medlineDate.indexOf(" ") + 1;
			int secondSpaceIdx = medlineDate.indexOf(" ",firstSpaceIdx + 1);
			if (firstSpaceIdx > 0 && secondSpaceIdx > 0){
				monthString = medlineDate.substring(firstSpaceIdx,secondSpaceIdx);
			}
		}
		if (monthString == null){
			return null;
		}
		
		int monthIndex = MONTH_ABBREVS.indexOf(monthString.toUpperCase()) +1;
		return (monthIndex == 0) ? null : monthIndex;
	}
	public Integer getPublicationYear(){
		PubDate pubDate = this.getPubDate();
		if (pubDate.getYear() != null){
			return new Integer(pubDate.getYear().getContent());
		} else if (pubDate.getMedlineDate() != null && pubDate.getMedlineDate().getContent() != null ){
				String medlineDate = pubDate.getMedlineDate().getContent();
				int putativeYear = NumberUtils.toInt(medlineDate.substring(0,medlineDate.indexOf(" ")));
				if (putativeYear > 1900 && putativeYear < 2050){
					return putativeYear;
				}else {
					return null;
				}
		}
		return null;
	}
	
	private PubDate getPubDate(){
		return article.getMedlineCitation().getArticle().getJournal().getJournalIssue().getPubDate();
		
	}
	public String getPublicationDateString(){
		StringBuilder builder = new StringBuilder();
		PubDate pubDate = null;
		try {
			 pubDate = article.getMedlineCitation().getArticle().getJournal().getJournalIssue().getPubDate();
		}catch(NullPointerException e){
			pubDate = null;
		}
		if (pubDate == null){
			return "no pub date available";
		}
		if (pubDate.getMedlineDate() != null){
			return pubDate.getMedlineDate().getContent();
		}
		if (pubDate.getYear() != null){
			builder.append(pubDate.getYear().getContent());
			builder.append(" ");
		}
		
		if (pubDate.getMonth() != null){
			builder.append(pubDate.getMonth().getContent());
			builder.append(" ");
		}
		if (pubDate.getDay() != null){
			builder.append(pubDate.getDay().getContent());
			builder.append(" ");
		}
		return builder.toString();
		
	}
	public String getJournalPublicationInfoString(){
		StringBuilder builder = new StringBuilder();
		builder
			.append(StringUtils.defaultString(this.getJournalName()))
			.append(". ")
			.append(StringUtils.defaultString(this.getPublicationDateString()))
			.append(";");
		if(StringUtils.isNotEmpty(this.getVolume())){
			builder
				.append("Vol ")
				.append(StringUtils.defaultString(this.getVolume()));
		}
		if (StringUtils.isNotEmpty(this.getIssue())){
			builder
				.append(" Issue ")
				.append(StringUtils.defaultString(this.getIssue()));
		}
		if (StringUtils.isNotEmpty(this.getPagination())){
			builder
				.append(" :")
				.append(StringUtils.defaultString(this.getPagination()));
		}
		return builder.toString();
	}
	public String getTitle(){
		try {
			return article.getMedlineCitation().getArticle().getArticleTitle().getContent();
		}catch (NullPointerException e){
			return null;
		}
	}
	public String getVolume(){
		try {
			return article.getMedlineCitation().getArticle().getJournal().getJournalIssue().getVolume().getContent();
		}catch (NullPointerException e){
			return null;
		}
	}
	
/*
	private String getTwoDigitFormattedInteger(String theNumber,String numberPattern,String defaultString) {
		DecimalFormat numberFormat = new DecimalFormat(numberPattern);
		if (StringUtils.isNumeric(theNumber)){
			return numberFormat.format(NumberUtils.toLong(theNumber));
		} else {
			return defaultString;
		}
	}
	*/
	public boolean isJournalComment(){
		return this.isOfPublicationType("Comment");
	}
	public boolean isJournalArticle(){
		return this.isOfPublicationType("Journal Article");
	}
	public boolean isNewspaperArticle(){
		return this.isOfPublicationType("Newspaper Article");
	}
	public boolean isJournalNews(){
		return this.isOfPublicationType("News");
	}
	public boolean isLetter(){
		return this.isOfPublicationType("Letter");
	}
	private boolean isOfPublicationType(String thePublicationTypeName){
		 List<PublicationType> publicationTypes = this.getPublicationTypes();
		 if (publicationTypes == null){
			 return false;
		 }
		 for(PublicationType currentPubType : publicationTypes){
			 if (currentPubType.getContent().toUpperCase().equals(thePublicationTypeName.toUpperCase())){
				 return true;
			 }
		 }
		 return false;
		
	}
	private List<PublicationType> getPublicationTypes(){
		try {
			return article.getMedlineCitation().getArticle().getPublicationTypeList().getPublicationType();
		}catch(NullPointerException e){
			return null;
		}
	}

	public void getMeshTerms() {
		List<MeshHeading> meshHeadings = article.getMedlineCitation().getMeshHeadingList().getMeshHeading();
		for(MeshHeading meshHeading: meshHeadings) {
			System.out.println("D: " + meshHeading.getDescriptorName().getContent()); 
			List<QualifierName> qualifierNames = meshHeading.getQualifierName();
			for(QualifierName qualifierName: qualifierNames) {
				System.out.println("Q: " + qualifierName.getContent());
			}
			
		}
	}
	
	/*
	private String xsdDateString(String day, String month, String year) {
		StringBuilder builder = new StringBuilder();
		builder.append(getTwoDigitFormattedInteger(day,"##","01"));
		builder.append("-");
		builder.append(getTwoDigitFormattedInteger(month,"##","01"));
		builder.append("-");
		builder.append(year);
		return builder.toString();
		
	}
*/
	public String getId() {
		return swanId;
	}
	public void setSWANId(String swanId) {
		this.swanId = swanId;
	}

}
