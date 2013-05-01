package org.mindinformatics.services.connector.pubmed.dataaccess;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mindinformatics.services.connector.pubmed.fetch.PubmedArticle;
import org.mindinformatics.services.connector.pubmed.fetch.PubmedArticleSet;
import org.mindinformatics.services.connector.pubmed.search.ESearchResult;
import org.mindinformatics.services.connector.pubmed.search.Id;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

/**
 * @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
 */
public class PubmedSearchAgent {
	// private static String ENCODING = "ISO-8859-1"; //"ISO-8859-1"
	private static final Log logger = LogFactory
			.getLog(PubmedSearchAgent.class);

	private static String BASE_SEARCH_URL = 
		"http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pubmed&term=";
	private static String BASE_FETCH_URL = 
		"http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=pubmed&id=";
	private static String SEARCH_PACKAGE_NAME = 
		"org.mindinformatics.services.connector.pubmed.search";
	private static String FETCH_PACKAGE_NAME = 
		"org.mindinformatics.services.connector.pubmed.fetch";
	
	private static JAXBContext searchJaxbContext = null;
	private static Unmarshaller searchUnmarshaller = null;
	private static JAXBContext fetchJaxbContext = null;
	private static Unmarshaller fetchUnmarshaller = null;
	
	private String proxyIp;
	private String proxyPort;

	private static PubmedSearchAgent instance = null;

	// ------------------------------------------------------------------------
	//  Singleton and Initialization 
	// ------------------------------------------------------------------------
	private PubmedSearchAgent() {
		try {
			searchJaxbContext = JAXBContext.newInstance(SEARCH_PACKAGE_NAME);
			searchUnmarshaller = searchJaxbContext.createUnmarshaller();
			fetchJaxbContext = JAXBContext.newInstance(FETCH_PACKAGE_NAME);
			fetchUnmarshaller = fetchJaxbContext.createUnmarshaller();
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void setProxyIp(String proxyIp) {
		this.proxyIp = proxyIp;
	}

	public void setProxyPort(String proxyPort) {
		this.proxyPort = proxyPort;
	}

	public static synchronized PubmedSearchAgent getInstance() {
		if (instance == null) {
			instance = new PubmedSearchAgent();
		}
		return instance;
	}
	
	// ------------------------------------------------------------------------
	
	/**
	 * Returns the PubMed records corresponding to the list of requested PubMed
	 * identifiers
	 * @param pmids	The list of PubMed identifiers
	 * @return The correspondent PubMed records
	 */
	public PubmedArticleSet fetchPubmedDocuments(List<String> pmids) {
		String url = BASE_FETCH_URL + join(",", pmids) + "&retmode=xml";
		logger.info("fetchurl = " + url);
		return (PubmedArticleSet) unmarshall(url, fetchUnmarshaller);
	}
	
	/**
	 * Fetching metadata from the PubMed service. 
	 * @param query		The query
	 * @param range		The number of results to return
	 * @param offset	The offset
	 * @return The metadata of the PubMed entries
	 */
	public PubmedArticleSet fetch(String query, int range, int offset) {
		ESearchResult esResult = search(query, range, offset);

		if (esResult == null || esResult.getCount().getContent().equals("0")) {
			return null;
		}
		List<String> pmidStrings = new ArrayList<String>();
		for (Id currentId : esResult.getIdList().getId()) {
			pmidStrings.add(currentId.getContent());
		}
		return fetchPubmedDocuments(pmidStrings);
	}
	
	/**
	 * Fetching metadata from the PubMed service and returns also stats that can 
	 * be used for pagination
	 * @param query		The query
	 * @param range		The number of results to return
	 * @param offset	The offset
	 * @return The metadata of the PubMed entries and the statistics
	 */
	public Map<Integer,PubmedArticleSet> fetchWithStats(String query, int range, int offset) {
		logger.info("Query: " + query);
		ESearchResult esResult = search(query, range, offset);

		logger.info("Results count: " + esResult.getCount().getContent());
		if (esResult == null || esResult.getCount().getContent().equals("0")) {
			return null;
		}
		
		logger.info("Results #ids: " + esResult.getIdList().getId().size());
		List<String> pmidStrings = new ArrayList<String>();
		for (Id currentId : esResult.getIdList().getId()) {
			pmidStrings.add(currentId.getContent());
		}
		Map<Integer,PubmedArticleSet> map = new HashMap<Integer,PubmedArticleSet>();
		map.put(Integer.parseInt(esResult.getCount().getContent()), fetchPubmedDocuments(pmidStrings));
		return map;
	}

	public ESearchResult search(String query, int maxResults, int start) {
		String url = BASE_SEARCH_URL + query + "&retmax=" + maxResults + "&retstart=" + start;
		logger.info("Search url = " + url);
		return (ESearchResult) this.unmarshall(url, searchUnmarshaller);
	}
	

	private Object unmarshall(String url, Unmarshaller unmarshaller) {
		Object result = null;
		try {
			if(proxyIp!=null && proxyIp.trim().length()>3 && proxyPort!=null && proxyPort.trim().length()>1) {
				logger.info("proxy: " + proxyIp + "-" + new Integer(proxyPort)) ;
				Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyIp, new Integer(proxyPort)));
				HttpURLConnection connectionWithProxy = (HttpURLConnection) new URL(url).openConnection(proxy);
				connectionWithProxy.setRequestProperty("Content-type", "text/xml");
				connectionWithProxy.setRequestProperty("Accept", "text/xml, application/xml");
				connectionWithProxy.setRequestMethod("GET");
				connectionWithProxy.connect();
				
				SAXParserFactory spf = SAXParserFactory.newInstance();
                spf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
                spf.setFeature("http://apache.org/xml/features/validation/schema", false);
                spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
                XMLReader xmlReader = spf.newSAXParser().getXMLReader();
				
                InputSource theInputSource = new InputSource((connectionWithProxy.getInputStream()));
                
                SAXSource source = new SAXSource(xmlReader, theInputSource);
                
                logger.info("InputSource: " + theInputSource);
                result = unmarshaller.unmarshal(source);
			} else {
				logger.info("No proxy detected");
				InputStreamReader inputStreamReader = new InputStreamReader((new java.net.URL(url)).openStream(), "utf-8");
				BufferedReader theReader = new BufferedReader(inputStreamReader);
				InputSource theInputSource = new InputSource(theReader);
				result = unmarshaller.unmarshal(theInputSource);
			}
		} catch (Exception e) {
		    logger.error(e.getMessage());
			throw new RuntimeException(e);
		}
		return result;
	}

	private static String join(String delim, java.util.List<String> idList) {
		StringBuilder builder = new StringBuilder();
		Iterator<String> iter = idList.iterator();
		while (iter.hasNext()) {
			builder.append(iter.next());
			if (iter.hasNext()) {
				builder.append(delim);
			}
		}
		return builder.toString();
	}

	// TODO This is for testing and it has to be sanitized
	public static void main(String[] args) {
		String query = "semantic web";
		try {
			PubmedSearchAgent agent = new PubmedSearchAgent();
			PubmedArticleSet articleSet = agent.fetch(query, 20, 0);

			List<PubmedArticle> docs = articleSet.getPubmedArticle();
			PubmedArticle pubmedArticle = null;
			ListIterator<PubmedArticle> it = docs.listIterator();
			logger.info("count = " + docs.size());

			while (it.hasNext()) {
				pubmedArticle = (PubmedArticle) it.next();
				String abst = "";
				try {
					abst = pubmedArticle.getMedlineCitation().getArticle()
							.getAbstract().getAbstractText().getContent();
				} catch (NullPointerException e) {

				}
				logger.info(abst);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
