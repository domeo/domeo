package org.mindinformatics.domeo.persistence;

import grails.converters.JSON;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codehaus.groovy.grails.web.json.JSONArray;
import org.codehaus.groovy.grails.web.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


/**
 * Basic HTTP wrapper for ElasticSearch
 * 
 * This is not production quality code.  For one thing, the simplistic GET and
 * POST code should be replaced with something like Jakarta Commons HttpClient.
 * 
 * If retrieving a document by id then the results header followed by the complete 
 * document in the _source field is returned 
 * Failed:
 * {"took":1,"timed_out":false,"_shards":{"total":5,"successful":5,"failed":0},"hits":{"total":0,"max_score":null,"hits":[]}}
 * 
 * Successful:
 * {"took":1,"timed_out":false,"_shards":{"total":5,"successful":5,"failed":0},"hits":{"total":1,"max_score":1.0,"hits":[{"_index":"twitter","_type":"test","_id":"aviMdI48QkSGOhQL6ncMZw","_score":1.0, "_source" : { "f1" : "field value > & one", "f2" : "field value two" }}]}}
 * 
 * The following is returned:
 * {
 *   "ok" : true,
 *   "_index" : "twitter",
 *   "_type" : "tweet",
 *   "_id" : "1",
 *   "found" : true
 * }
 * 
 * WARNING: If the document to be deleted is not in the index then this generates 
 * a java.io.FileNotFoundException
 * 
 * Sample search returns:
 * Failed search:
 * {"took":1,"timed_out":false,"_shards":{"total":5,"successful":5,"failed":0},
 *  "hits":{"total":0,"max_score":null,"hits":[]}}
 * 
 * Successful search (2 results):
 * {"took":2,"timed_out":false,"_shards":{"total":5,"successful":5,"failed":0},
 *  "hits":{"total":5,"max_score":1.0,
 *  "hits":[{"_index":"twitter","_type":"test","_id":"Y-SKPAcBQeefMJNKBxCdmg","_score":1.0},
 *          {"_index":"twitter","_type":"test","_id":"5","_score":1.0}]}}
 * 
 * @author Keith Gutfreund, Elsevier Labs 2013
 */
public class ElasticSearchWrapper {

	Logger  logger = Logger.getLogger("org.mindinformatics.domeo.persistence.ElasticSearchWrapper");
	
	/**
	 * Last response from GET, POST - Good for debug but if static this is not
	 * thread safe!
	 */
	String lastResponse;

	/** Index name set at construction */
	final String index;

	/** Type name set at construction */
	final String type;

	/** Subdoc type name set at construction */
	final String subdocType;

	/** ES manage mapping */
	final String esMapping;

	/** ES manage mapping for sub documents */
	final String esMappingSubdoc;

	/** ES check, create and delete index url */
	final String esIndexUrl;

	/** ES check if index type exists */
	final String esIndexTypeUrl;

	/** ES check if index type sub-doc exists */
	final String esIndexTypeSubdocUrl;

	/** ElasticSearch insert and delete document url */
	final String esInsertDeleteUrl;

	/** ElasticSearch insert and delete sub-document url */
	final String esInsertDeleteSubdocUrl;

	/** ElasticSearch search url */
	final String esSearchUrl;

	/** ElasticSearch search subdoc url */
	final String esSearchSubdocUrl;

	/** ES Scan url */
	final String esScanUrl;

	/** ES Scan url for sub documents */
	final String esScanSubdocUrl;

	/** ES Scroll url */
	final String esScrollUrl;

	/** Refresh index url */
	final String esRefreshIndexUrl;

	/** Set read timeout to 20s - just a guess if this is sufficient */
	final int HTTP_READ_TIMEOUT = 20000;

	/** Get all docs using scroll */
	final String ES_QUERY_SCROLL = "{ \"fields\" : [\"_id\"], \"query\" : { \"match_all\" : {} } }";

	/** Get doc by _id field */
	final String ES_QUERY_BY_DOCID = "{ \"query\" : { \"term\" : { \"_id\" : \"%s\" } } }";

	/** Boolean AND or OR query on 1 or more parsed terms in a single field */
	final String ES_BOOL_QUERY_SINGLE_PARSED_FIELD = "{ \"fields\" : [\"_id\"], \"query\": { \"match\": { \"%s\": { \"query\" : \"%s\", \"operator\" : \"%s\" } } } }";

	// Boolean operators
	final String AND_OPERATOR = "and";
	final String OR_OPERATOR = "or";

	// HTTP Operations
	final static String HTTP_POST = "POST";
	final static String HTTP_PUT = "PUT";
	final static String HTTP_GET = "GET";
	final static String HTTP_DELETE = "DELETE";
	final static String HTTP_HEAD = "HEAD";

	/** Default scan-scroll timeout */
	final int DEFAULT_TIMEOUT = 10;

	/** Unique replacement character string for colons found in JSON object key */
	final static String COLON_REPLACEMENT = "_!DOMEO_NS!_";

	/** Match "field_name": this is quoted field name followed by : */
	final static Pattern PAT_NSMATCH = Pattern.compile("(\".+?\"):");

	/** Match "ok":true in results */
	final String ES_SUCCESS = "\"ok\":true";
	final static Pattern PAT_ES_SUCCESS = Pattern.compile(
			"\"ok\"\\s*:\\s*true", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

	final static String NEWLINE = System.getProperty("line.separator");

	/**
	 * Construct wrapper for specified index and type
	 * 
	 * @param index
	 * @param type
	 * @param serverIP
	 * @param port
	 */
	public ElasticSearchWrapper(String index, String type, String serverIP, String port) {
		this.index = index;
		this.type = type;
		this.subdocType = type + "1";

		// esUrl = "http://75.101.244.195:8081/" + index + "/" + type + "/";
		esInsertDeleteUrl = "http://" + serverIP + ":" + port + "/" + index
				+ "/" + type + "/";
		esInsertDeleteSubdocUrl = "http://" + serverIP + ":" + port + "/"
				+ index + "/" + subdocType + "/";
		esSearchUrl = "http://" + serverIP + ":" + port + "/" + index + "/"
				+ type + "/_search";
		esSearchSubdocUrl = "http://" + serverIP + ":" + port + "/" + index
				+ "/" + subdocType + "/_search";
		esIndexUrl = "http://" + serverIP + ":" + port + "/" + index + "/";
		esIndexTypeUrl = "http://" + serverIP + ":" + port + "/" + index + "/"
				+ type + "/";
		esIndexTypeSubdocUrl = "http://" + serverIP + ":" + port + "/" + index
				+ "/" + subdocType + "/";
		esMapping = "http://" + serverIP + ":" + port + "/" + index + "/"
				+ type + "/_mapping";
		esMappingSubdoc = "http://" + serverIP + ":" + port + "/" + index + "/"
				+ subdocType + "/_mapping";
		esScanUrl = "http://" + serverIP + ":" + port + "/" + index + "/"
				+ type + "/_search?search_type=scan&scroll=2m&size=";
		esScanSubdocUrl = "http://" + serverIP + ":" + port + "/" + index + "/"
				+ subdocType + "/_search?search_type=scan&scroll=2m&size=";
		esScrollUrl = "http://" + serverIP + ":" + port
				+ "/_search/scroll?scroll=2m&scroll_id=";
		esRefreshIndexUrl = "http://" + serverIP + ":" + port + "/" + index
				+ "/_refresh";
	}

	/**
	 * Quick check the results for "ok":true
	 * 
	 * @param response
	 * @return true if result is true, false otherwise
	 */
	static boolean checkOk(String response) {
		Matcher m = PAT_ES_SUCCESS.matcher(response);
		if (m.find()) {
			return true;
		}
		return false;
	}

	/**
	 * Create the current index. Will fail if the index already exists.
	 * 
	 * @return results with "ok":true if index successfully created, false if it
	 *         fails.
	 */
	String createIndex() {
		boolean res = indexExists();
		@SuppressWarnings("unused")
		int resCode;
		if (!res) {
			resCode = doHttpOperation(esIndexUrl, HTTP_PUT, null);
		}
		return lastResponse;
	}

	boolean indexExists() {
		int resCode = doHttpOperation(esIndexUrl, HTTP_HEAD, null);
		return (resCode == 200);
	}

	boolean indexTypeExists() {
		int resCode = doHttpOperation(esIndexTypeUrl, HTTP_HEAD, null);
		return (resCode == 200);
	}

	/**
	 * Delete the index On success returns: {"ok":true,"acknowledged":true} On
	 * failure returns:
	 * {"error":"IndexMissingException[[domeo2] missing]","status":404}
	 * 
	 * @return
	 */
	String deleteIndex() {
		@SuppressWarnings("unused")
		int resCode = doHttpOperation(esIndexUrl, HTTP_DELETE, null);
		return lastResponse;
	}

	/**
	 * Delete the index type On success returns: {"ok":true} On failure returns:
	 * {"error":"TypeMissingException[[newindex] type[newtype2] missing]",
	 * "status":404}
	 * 
	 * @return
	 */
	String deleteIndexType() {
		@SuppressWarnings("unused")
		int resCode = doHttpOperation(esIndexTypeUrl, HTTP_DELETE, null);
		return lastResponse;
	}

	/**
	 * Refresh the index On success returns: {"ok":true} On failure returns:
	 * {"error":"IndexMissingException[[domeo1] missing]","status":404}
	 */
	String refreshIndex() {
		@SuppressWarnings("unused")
		int resCode = doHttpOperation(esRefreshIndexUrl, HTTP_POST, null);
		return lastResponse;
	}

	/**
	 * Do mapping operation
	 * 
	 * @param mapping
	 * @param isSubdoc
	 *            true if this is for the subdoc type
	 * @param operation
	 *            : HTTP_GET, HTTP_PUT, HTTP_DELETE, ...
	 * @return
	 */
	String doMapping(String mapping, boolean isSubdoc, String operation) {
		String mappingUrl = isSubdoc ? esMappingSubdoc : esMapping;
		@SuppressWarnings("unused")
		int resCode = doHttpOperation(mappingUrl, operation, mapping);
		return lastResponse;
	}

	/**
	 * Efficient way to process large number of results is via scan-scroll
	 * 
	 * @param numDocsPerShard
	 * @return
	 */
	String getAllDocumentsScan(int numDocsPerShard) {
		String url = esScanUrl + numDocsPerShard;
		@SuppressWarnings("unused")
		int resCode = doHttpOperation(url, HTTP_GET, ES_QUERY_SCROLL);
		return lastResponse;
	}

	/**
	 * Note: when using curl we need to use &scrollId=scrollvalue
	 * 
	 * @param scroll
	 * @return
	 */
	String getAllDocumentsScroll(String scroll) {
		@SuppressWarnings("deprecation")
		String urlEncode = URLEncoder.encode(scroll);
		String url = esScrollUrl + urlEncode;
		@SuppressWarnings("unused")
		int resCode = doHttpOperation(url, HTTP_GET, null);
		return lastResponse;
	}

	/**
	 * Extract each of the ao:item objects as individual documents
	 * 
	 * @param doc
	 */
	void insertItemSubdocuments(String doc) {
		System.out.println("Subdocuments");
		JSONObject obj1 = null;
		try {
			obj1 = (JSONObject) JSON.parse(doc);
		} catch (Exception e) { // multiple exceptions possible
			e.printStackTrace();
			return;
		}
		
		String aoItem = "ao" + COLON_REPLACEMENT + "item";
		JSONArray aoItems = (JSONArray) (obj1.get(aoItem));
		System.out.println("Subdocuments found: " + aoItems);
		if (aoItems != null) {
			System.out.println("Subdocuments found!");
			for (int i = 0; i < aoItems.size(); i++) {
				JSONObject item = (JSONObject) aoItems.get(i);
				@SuppressWarnings("unused")
				int resCode = doHttpOperation(esInsertDeleteSubdocUrl,
						HTTP_POST, item.toString());
			}
		} else {
			logger.warning("No subdocuments found!");
			System.out.println("No subdocuments found!");
		}
	}

	/**
	 * Insert a document into the index using the specified document Id. This
	 * allows for replacing existing documents. Remove colons from field names
	 * Also insert the individual ao:Item objects as sub-documents
	 * 
	 * @param doc
	 *            to be inserted
	 * @param docId
	 *            document id for inserted document
	 * @return documnent id for inserted document
	 */
	String insertDocument(String doc, String docId) {
		String encodedDoc = encodeNS(doc);

		// Insert all the ao:Item subdocuments
		insertItemSubdocuments(encodedDoc);

		// Insert the complete document
		String url = esInsertDeleteUrl;
		if ((docId != null) && (docId.trim().length() > 0)) {
			url += docId;
		}
		@SuppressWarnings("unused")
		int resCode = doHttpOperation(url, HTTP_POST, encodedDoc);
		return lastResponse;
	}

	/**
	 * Insert a document into the index. Remove colons from field names
	 * 
	 * @param doc
	 *            to be inserted
	 * @return result header with document id for inserted document
	 */
	String insertDocument(String doc) {
		return insertDocument(doc, null); // no docId
	}

	/**
	 * Remove a document from the index
	 * 
	 * WARNING: If the document to be deleted is not in the index then this
	 * generates a java.io.FileNotFoundException
	 * 
	 * @param docId
	 *            of doc to be removed
	 * @return
	 */
	String deleteDocument(String docId) {
		String deleteUrl = esInsertDeleteUrl + docId;
		@SuppressWarnings("unused")
		int resCode = doHttpOperation(deleteUrl, HTTP_DELETE, null);
		return lastResponse;
	}
	
	/**
	 * Retrieve a single document using its _id field.  Doc should be decoded
	 * so that any namespace characters that were removed at insertion are replaced.
	 * @param docID
	 * @return retrieved document preceded by results header
	 */
	String getDocument(String docID) {
		return getDocument(docID, false, null);
	}

	/**
	 * Retrieve a single document using its _id field. Doc should be decoded so
	 * that any namespace characters that were removed at insertion are
	 * replaced.
	 * 
	 * @param docID
	 * @param isSubdoc
	 *            true if we are searching the subdoc index type
	 * @param permissions
	 *            optional, used for filtering by permission
	 * @return retrieved document preceded by results header
	 */
	String getDocument(String docID, boolean isSubdoc,
			DomeoPermissions permissions3) {
		String data = "";
		if (permissions3 == null) {
			data = String.format(ES_QUERY_BY_DOCID, docID);
		} else {
			String filter = permissions3.buildQueryFilter();
			data = "{ \"query\" : { \"term\" : { \"_id\" : \"" + docID
					+ "\" } } " + filter + " }";
		}

		String searchUrl = isSubdoc ? esSearchSubdocUrl : esSearchUrl;
		@SuppressWarnings("unused")
		int resCode = doHttpOperation(searchUrl, HTTP_POST, data);
		return decodeNS(lastResponse);
	}

	/**
	 * Build a simple parsed field query string
	 * 
	 * @param field
	 *            field we are searching against
	 * @param val
	 *            parsed phrase to match
	 * @param from
	 *            starting result number
	 * @param size
	 *            maximum number of results to show
	 * @return formatted query string
	 */
	String buildQuery(String field, String val, int from, int size,
			DomeoPermissions permissions3) {
		StringBuffer sb = new StringBuffer("{ ");

		// Check for starting position (from) and max results (size)
		if ((from > -1) && (size > -1)) {
			sb.append("\"from\" : " + from + ", \"size\" : " + size + ", ");
		} else if (from > -1) { // from only, no size
			sb.append("\"from\" : " + from + ", ");
		} else if (size > -1) { // size only, no from
			sb.append("\"size\" : " + size + ", ");
		}

		if (permissions3 == null) {
			sb.append("\"fields\" : [\"_id\"], \"query\" : { \"match\" : { \""
					+ field + "\": \"" + val + "\" } } } ");
		} else {
			String filter = permissions3.buildQueryFilter();
			sb.append("\"fields\" : \"[_id]\", \"query\" : { \"match\" : { \""
					+ field + "\": \"" + val + "\" } } " + filter + " } ");
		}
		return sb.toString();
	}

	/**
	 * Build a simple phrase query string
	 * 
	 * @param field
	 *            field we are searching against
	 * @param val
	 *            parsed phrase to match
	 * @param from
	 *            starting result number
	 * @param size
	 *            maximum number of results to show
	 * @return formatted query string
	 */
	String buildPhraseQuery(String field, String val, int from, int size,
			DomeoPermissions permissions3) {
		StringBuffer sb = new StringBuffer("{ ");

		// Check for starting position (from) and max results (size)
		if ((from > -1) && (size > -1)) {
			sb.append("\"from\" : " + from + ", \"size\" : " + size + ", ");
		} else if (from > -1) { // from only, no size
			sb.append("\"from\" : " + from + ", ");
		} else if (size > -1) { // size only, no from
			sb.append("\"size\" : " + size + ", ");
		}

		if (permissions3 == null) {
			sb.append("\"fields\" : [\"_id\"], \"query\" : { \"match_phrase\" : { \""
					+ field + "\": \"" + val + "\" } } } ");
		} else {
			String filter = permissions3.buildQueryFilter();
			sb.append("\"fields\" : \"[_id]\", \"query\" : { \"match_phrase\" : { \""
					+ field + "\": \"" + val + "\" } } " + filter + " } ");
		}

		return sb.toString();
	}

	/**
	 * Build a simple term query string
	 * 
	 * @param field
	 *            field we are searching against
	 * @param val
	 *            unparsed keyword to match
	 * @param from
	 *            starting result number
	 * @param size
	 *            maximum number of results to show
	 * @return formatted query string
	 */
	String buildTermQuery(String field, String val, int from, int size,
			DomeoPermissions permissions3) {
		StringBuffer sb = new StringBuffer("{ ");

		// Check for starting position (from) and max results (size)
		if ((from > -1) && (size > -1)) {
			sb.append("\"from\" : " + from + ", \"size\" : " + size + ", ");
		} else if (from > -1) { // from only, no size
			sb.append("\"from\" : " + from + ", ");
		} else if (size > -1) { // size only, no from
			sb.append("\"size\" : " + size + ", ");
		}

		if (permissions3 == null) {
			sb.append("\"fields\" : [\"_id\"], \"query\" : { \"term\" : { \""
					+ field + "\": \"" + val + "\" } } } ");
		} else {
			String filter = permissions3.buildQueryFilter();
			sb.append("\"fields\" : [\"_id\"], \"query\" : { \"term\" : { \""
					+ field + "\": \"" + val + "\" } } " + filter + " } ");
		}
		return sb.toString();
	}

	/**
	 * Build a simple boolean query string
	 * 
	 * @param field
	 *            field we are searching against
	 * @param val
	 *            parsed string of 1 or more words
	 * @param operator
	 *            and or or
	 * @param from
	 *            starting result number
	 * @param size
	 *            maximum number of results to show
	 * @param from
	 *            starting result number
	 * @param size
	 *            maximum number of results to show
	 * @return formatted query string
	 */
	String buildSimpleParsedBooleanQuery(String field, String val,
			String operator, int from, int size, DomeoPermissions permissions3) {
		StringBuffer sb = new StringBuffer("{ ");

		// Check for starting position (from) and max results (size)
		if ((from > -1) && (size > -1)) {
			sb.append("\"from\" : " + from + ", \"size\" : " + size + ", ");
		} else if (from > -1) { // from only, no size
			sb.append("\"from\" : " + from + ", ");
		} else if (size > -1) { // size only, no from
			sb.append("\"size\" : " + size + ", ");
		}

		// Use "fields" to limit fields returned in results
		if (permissions3 == null) {
			sb.append("\"fields\" : [\"_id\"], \"query\" : { \"match\" : { \""
					+ field + "\": { \"query\" : \"" + val
					+ "\", \"operator\" : \"" + operator + "\" } } } }");
		} else {
			String filter = permissions3.buildQueryFilter();
			sb.append("\"fields\" : [\"_id\"], \"query\" : { \"match\" : { \""
					+ field + "\": { \"query\" : \"" + val
					+ "\", \"operator\" : \"" + operator + "\" } } } " + filter
					+ " } ");
		}

		return sb.toString();
	}

	/**
	 * Build a generic boolean query string
	 * 
	 * @param fields
	 *            array of fields
	 * @param vals
	 *            array of vals
	 * @param parsed
	 *            array of "match" or "term" for parsed and unparsed
	 * @param operator
	 *            "or" or "and"
	 * @param from
	 *            starting result number
	 * @param size
	 *            maximum number of results to show
	 * @return formatted query string
	 */
	String buildGenericBooleanQuery(String[] fields, String[] vals,
			String[] parsed, String operator, int from, int size,
			DomeoPermissions permissions3) {
		StringBuffer sb = new StringBuffer("{ ");

		// Check for starting position (from) and max results (size)
		if ((from > -1) && (size > -1)) {
			sb.append("\"from\" : " + from + ", \"size\" : " + size + ", ");
		} else if (from > -1) { // from only, no size
			sb.append("\"from\" : " + from + ", ");
		} else if (size > -1) { // size only, no from
			sb.append("\"size\" : " + size + ", ");
		}

		// Use "fields" to limit fields returned in results
		sb.append("\"fields\" : [\"_id\"], \"query\" : { \"bool\" : { ");

		// Operator AND -> must and OR -> should
		if (operator.equals(AND_OPERATOR)) {
			sb.append("\"must\" : [  ");
		} else {
			sb.append("\"should\" : [  ");
		}

		// Append clause for each field
		for (int i = 0; i < fields.length; i++) {
			if (i != 0) {
				sb.append(", ");
			}
			sb.append("{ " + "\"" + parsed[i] + "\"" + " : { " + "\""
					+ fields[i] + "\" : " + "\"" + vals[i] + "\"" + "} }");
		}
		if (permissions3 == null) {
			sb.append("] } } }");
		} else {
			String filter = permissions3.buildQueryFilter();
			sb.append("] } } " + filter + " } ");

			// sb.append("] } }, \"filter\" : { \"term\" : { \"" +
			// permissions.key.getValue().toString() + "\" : " + "\"" +
			// permissions.value + "\" } } } ");
		}

		return sb.toString();
	}

	/**
	 * Perform a single-field term (keyword) query. Doc should be decoded so
	 * that any namespace characters that were removed at insertion are
	 * replaced.
	 * 
	 * @param field
	 *            to be searched
	 * @param val
	 *            one word to be matched (unparsed)
	 * @param from
	 *            starting result number
	 * @param size
	 *            maximum number of results to show
	 * @param isSubdoc
	 *            true if we are searching the subdoc index type
	 * @permissions3 permissions object
	 * @return list of matching document ids
	 */
	String termQuery(String field, String val, int from, int size,
			boolean isSubdoc, DomeoPermissions permissions3) {
		String query = buildTermQuery(field, val, from, size, permissions3);
		String searchUrl = isSubdoc ? esSearchSubdocUrl : esSearchUrl;
		@SuppressWarnings("unused")
		int resCode = doHttpOperation(searchUrl, HTTP_POST, query);
		return decodeNS(lastResponse);
	}

	/**
	 * Perform a single-field query. Doc should be decoded so that any namespace
	 * characters that were removed at insertion are replaced.
	 * 
	 * @param field
	 *            to be searched
	 * @param val
	 *            parsed phrase to be matched
	 * @param from
	 *            starting result number
	 * @param size
	 *            maximum number of results to show
	 * @param isSubdoc
	 *            true if we are searching the subdoc index type
	 * @permissions3 permissions object
	 * @return list of matching document ids
	 */
	String query(String field, String val, int from, int size,
			boolean isSubdoc, DomeoPermissions permissions3) {
		String query = buildQuery(field, val, from, size, permissions3);
		String searchUrl = isSubdoc ? esSearchSubdocUrl : esSearchUrl;
		@SuppressWarnings("unused")
		int resCode = doHttpOperation(searchUrl, HTTP_POST, query);
		return decodeNS(lastResponse);
	}

	/**
	 * Perform a single-field phrase query. Doc should be decoded so that any
	 * namespace characters that were removed at insertion are replaced.
	 * 
	 * @param field
	 *            to be searched
	 * @param val
	 *            parsed phrase to be matched
	 * @param from
	 *            starting result number
	 * @param size
	 *            maximum number of results to show
	 * @param isSubdoc
	 *            true if we are searching the subdoc index type
	 * @permissions3 permissions object
	 * @return list of matching document ids
	 */
	String phraseQuery(String field, String val, int from, int size,
			boolean isSubdoc, DomeoPermissions permissions3) {
		String query = buildPhraseQuery(field, val, from, size, permissions3);
		String searchUrl = isSubdoc ? esSearchSubdocUrl : esSearchUrl;
		@SuppressWarnings("unused")
		int resCode = doHttpOperation(searchUrl, HTTP_POST, query);
		return decodeNS(lastResponse);
	}

	/**
	 * Perform a boolean query against all the parsed words withinin 1 field Doc
	 * should be decoded so that any namespace characters that were removed at
	 * insertion are replaced. Sample Results:
	 * {"took":3,"timed_out":false,"_shards"
	 * :{"total":5,"successful":5,"failed":0
	 * },"hits":{"total":1,"max_score":0.008439008
	 * ,"hits":[{"_index":"domeo","_type"
	 * :"test","_id":"1","_score":0.008439008}]}}
	 * 
	 * @param field
	 *            to be searched
	 * @param val
	 *            one or more words to be matched
	 * @param operator
	 *            "or" or "and"
	 * @param from
	 *            starting result number
	 * @param size
	 *            maximum number of results to show
	 * @param isSubdoc
	 *            true if we are searching the subdoc index type
	 * @permissions3 permissions object
	 * @return list of matching document ids
	 */
	String booleanQuerySingleParsedField(String field, String val,
			String operator, int from, int size, boolean isSubdoc,
			DomeoPermissions permissions3) {
		String query = buildSimpleParsedBooleanQuery(field, val, operator,
				from, size, permissions3);
		String searchUrl = isSubdoc ? esSearchSubdocUrl : esSearchUrl;
		@SuppressWarnings("unused")
		int resCode = doHttpOperation(searchUrl, HTTP_POST, query);
		return decodeNS(lastResponse);
	}

	/**
	 * Perform a boolean query against all the specified fields Doc should be
	 * decoded so that any namespace characters that were removed at insertion
	 * are replaced.
	 * 
	 * @param fields
	 *            array of fields
	 * @param vals
	 *            array of vals
	 * @param parsed
	 *            array of "match" or "term" for parsed and unparsed
	 * @param operator
	 *            "or" or "and"
	 * @param from
	 *            starting result number
	 * @param size
	 *            maximum number of results to show
	 * @param isSubdoc
	 *            true if we are searching the subdoc index type
	 * @permissions3 permissions object
	 * @return search results
	 */
	String booleanQueryMultipleFields(String[] fields, String[] vals,
			String[] parsed, String operator, int from, int size,
			boolean isSubdoc, DomeoPermissions permissions3) {
		String query = buildGenericBooleanQuery(fields, vals, parsed, operator,
				from, size, permissions3);
		System.out.println(query);
		String searchUrl = isSubdoc ? esSearchSubdocUrl : esSearchUrl;
		@SuppressWarnings("unused")
		int resCode = doHttpOperation(searchUrl, HTTP_POST, query);
		return decodeNS(lastResponse);
	}

	/**
	 * Do the specified HTTP operation: GET, POST, PUT, DELETE
	 * 
	 * @param urlString
	 * @param operation
	 *            is GET, POST, PUT, etc
	 * @param data
	 *            may be null if GET
	 * @return response code
	 */
	int doHttpOperation(String urlString, String operation, String data) {
		OutputStreamWriter wr = null;
		BufferedReader rd = null;

		int resCode = -1;

		StringBuffer sb = new StringBuffer();
		lastResponse = " ";

		try {
			// Send data
			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			;
			conn.setDoOutput(true);
			conn.setRequestMethod(operation);
			conn.setReadTimeout(HTTP_READ_TIMEOUT);

			// POST and PUT write data
			if (data != null) {
				wr = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
				wr.write(data);
				wr.flush();
			}

			resCode = conn.getResponseCode();

			// Get the response - ok if nothing (happens on 404 returns so need
			// try catch for FileNotFoundException)
			if (resCode <= 299) { // covers 200, 201 for inserts
				rd = new BufferedReader(new InputStreamReader(
						conn.getInputStream(), "UTF-8"));
				String line;
				while ((line = rd.readLine()) != null) {
					sb.append(line + NEWLINE);
				}
			}

			else { // 404 and 400 and 500 errors
				InputStream errorStream = conn.getErrorStream();
				if (errorStream != null) {
					rd = new BufferedReader(new InputStreamReader(errorStream,
							"UTF-8"));
					String line1;
					while ((line1 = rd.readLine()) != null) {
						sb.append(line1 + NEWLINE);
					}
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (wr != null) {
					wr.close();
				}
			} catch (IOException e) {
			}
			try {
				if (rd != null) {
					rd.close();
				}
			} catch (IOException e) {
			}
		}

		lastResponse = sb.toString();
		return resCode;
	}

	/**
	 * Replace any ':' characters in field names with a replacement string since
	 * ElasticSearch does not permit these. Uses JSON-Simple parser
	 * 
	 * @param doc
	 *            that may contain ':' character in field names
	 * @return document with field names encoded
	 */
	String encodeNS(String doc) {
		JSONParser parser = new JSONParser();
		Transformer transformer = new Transformer();
		try {
			parser.parse(doc, transformer);
			Object value = transformer.getResult();
			return value.toString();
		} catch (ParseException e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * Replace all encoded colons in field names with the single colon character
	 * 
	 * @param doc
	 *            to be decoded
	 * @return decoded doc
	 */
	String decodeNS(String doc) {
		return doc.replaceAll(COLON_REPLACEMENT, ":");
	}

	// Convenience method to read in sample json doc for debug
	String readSampleJsonDoc(String file) {
		StringBuffer sb = new StringBuffer();
		BufferedReader br = null;

		try {
			br = new BufferedReader(new FileReader(new File(file)));
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line + NEWLINE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException e) {
			}
		}
		return sb.toString();
	}

	/**
	 * Setup a debug index
	 */
	void testSetup() {
		// Delete any existing index
		deleteIndex();

		// Create the index
		createIndex();

		// Apply mappings for type and subtypes
		BufferedReader br;
		String line;
		StringBuffer sb;
		try {
			// Mappings for index + type
			br = new BufferedReader(new FileReader(
					"./data/mappings_domeo_v2.json"));
			sb = new StringBuffer();
			while ((line = br.readLine()) != null) {
				sb.append(line + NEWLINE);
			}
			br.close();
			doMapping(sb.toString(), false, HTTP_POST);

			// Mappings for index + sub-type
			br = new BufferedReader(new FileReader(
					"./data/mappings_domeo_subtype_v2.json"));
			sb = new StringBuffer();
			while ((line = br.readLine()) != null) {
				sb.append(line + NEWLINE);
			}
			br.close();
			doMapping(sb.toString(), true, HTTP_POST);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Index a doc
		try {
			br = new BufferedReader(new FileReader("./data/sample_docs.json"));
			if ((line = br.readLine()) != null) {
				insertDocument(line);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		refreshIndex();
	}

	/**
	 * Run some basic tests
	 */
	void doTests() {
		String doc = "", r = "";

		DomeoPermissions dp3 = new DomeoPermissions(
				null,
				null,
				new String[] { "urn:group:uuid:4028808c3dccfe48013dccfe95ea0005 1" });
		r = getDocument("1", false, dp3);

		r = termQuery("domeo_!DOMEO_NS!_agents.@type", "foafx:Person", 0, 10,
				false, dp3);

		r = phraseQuery(
				"ao_!DOMEO_NS!_item.ao_!DOMEO_NS!_context.ao_!DOMEO_NS!_hasSelector.ao_!DOMEO_NS!_suffix",
				"enabling application", 0, 10, false, dp3);

		dp3 = new DomeoPermissions(
				null,
				null,
				new String[] { "urn:group:uuid:4028808c3dccfe48013dccfe95ea0005 1" });
		r = query(
				"ao_!DOMEO_NS!_item.ao_!DOMEO_NS!_context.ao_!DOMEO_NS!_hasSelector.ao_!DOMEO_NS!_suffix",
				"enabling application", 0, 10, false, dp3);

		// Test: Term (keyword) query
		// r = termQuery("domeo_!DOMEO_NS!_agents.@type", "foafx:Person", 0, 10,
		// dp);

		// Test: Phrase query
		r = phraseQuery("dct_!DOMEO_NS!_description", "created automatically",
				0, 10, false, dp3);

		// Test: Delete a document
		// r = deleteDocument("7TdnuBsjTjWaTcbW7RVP3Q");

		// Test: Generic boolean query: 4 fields (3 keyword fields, 1 parsed
		// field)

		String[] fields = { "ao_!DOMEO_NS!_item.@type",
				"ao_!DOMEO_NS!_item.@id",
				"ao_!DOMEO_NS!_item.ao_!DOMEO_NS!_body.@type",
				"ao_!DOMEO_NS!_item.ao_!DOMEO_NS!_body.cnt_!DOMEO_NS!_chars" };
		String[] vals = { "ao:Highlight",
				"urn:domeoclient:uuid:D3062173-8E53-41E9-9248-F0B8A7F65E5B",
				"cnt:ContentAsText", "paolo" };
		String[] parsed = { "term", "term", "term", "match" };
		r = booleanQueryMultipleFields(fields, vals, parsed, "and", 0, 10,
				false, dp3);

		// Test: Single field boolean query
		r = booleanQuerySingleParsedField(
				"ao_!DOMEO_NS!_item.ao_!DOMEO_NS!_context.ao_!DOMEO_NS!_hasSelector.ao_!DOMEO_NS!_suffix",
				"formal biomedical ontologies", "or", 0, 10, false, null);

		// Test: Retrieve a single doc by id
		r = getDocument("aviMdI48QkSGOhQL6ncMZw", false, null);

		// Test: insert a document, return it's auto-assigned id
		doc = "{ \"f1\" : \"field value one\", \"f2\" : \"field value two\" }";
		r = insertDocument(doc);

		// Test: insert a doc with specified id (replace if already present)
		doc = "{ \"f1\" : \"field value one\", \"f2\" : \"field value two\" }";
		r = insertDocument(doc, "5");
		System.out.println(r);

		// Test: insert json document and try to remove it
		doc = readSampleJsonDoc("/temp/sample_domeo_doc.json");
		System.out.println(doc);
		r = insertDocument(doc);
	}
	
//	/** Last response from GET, POST - Good for debug but if static this is not thread safe! */
//	String lastResponse;
//	
//	/** Index name set at construction */
//	final String index;
//	
//	/** Type name set at construction */
//	final String type;
//	
//	/** ES manage mapping */
//	final String esMapping;
//	
//	/** ES check, create and delete index url */
//	final String esIndexUrl;
//	
//	/** ES check if index type exists */
//	final String esIndexTypeUrl;
//	
//	/** ElasticSearch insert and delete document url */
//	final String esInsertDeleteUrl;
//	
//	/** ElasticSearch insert url */
//	final String esSearchUrl;
//	
//	/** ES Scan url */
//	final String esScanUrl;
//	
//	/** ES Scroll url */
//	final String esScrollUrl;
//	
//	/** Set read timeout to 20s - just a guess if this is sufficient */
//	final int HTTP_READ_TIMEOUT = 20000;
//	
//	/** Get all docs using scroll */
//	final String ES_QUERY_SCROLL = "{ \"fields\" : [\"_id\"], \"query\" : { \"match_all\" : {} } }";
//	
//	/** Get doc by _id field */
//	final String ES_QUERY_BY_DOCID = "{ \"query\" : { \"term\" : { \"_id\" : \"%s\" } } }";
//
//	/** Boolean AND or OR query on 1 or more parsed terms in a single field */
//	final String ES_BOOL_QUERY_SINGLE_PARSED_FIELD = 
//		"{ \"fields\" : [\"_id\"], \"query\": { \"match\": { \"%s\": { \"query\" : \"%s\", \"operator\" : \"%s\" } } } }";
//
//	// Boolean operators
//	final String AND_OPERATOR = "and";
//	final String OR_OPERATOR = "or";
//	
//	// HTTP Operations
//	final static String HTTP_POST   = "POST";
//	final static String HTTP_PUT    = "PUT";
//	final static String HTTP_GET    = "GET";
//	final static String HTTP_DELETE = "DELETE";
//	final static String HTTP_HEAD   = "HEAD";
//	
//	/** Default scan-scroll timeout */
//	final int DEFAULT_TIMEOUT = 10;
//	
//	/** Unique replacement character string for colons found in JSON object key */
//	final static String COLON_REPLACEMENT = "_!DOMEO_NS!_";
//
//	/** Match "field_name":  this is quoted field name followed by : */
//	final static Pattern PAT_NSMATCH = Pattern.compile("(\".+?\"):");
//
//	/** Match "ok":true in results */
//	final String ES_SUCCESS = "\"ok\":true";
//	final static Pattern PAT_ES_SUCCESS = Pattern.compile("\"ok\"\\s*:\\s*true", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
//	
//	final static String NEWLINE = System.getProperty("line.separator");
//	
//	
//	/**
//	 * Construct wrapper for specified index and type
//	 * @param index
//	 * @param type
//	 * @param serverIP
//	 * @param port
//	 */
//	public ElasticSearchWrapper(String index, String type, String serverIP, String port) {
//		this.index = index;
//		this.type = type;
//		//esUrl = "http://75.101.244.195:8081/" + index + "/" + type + "/";
//		esInsertDeleteUrl = "http://" + serverIP + ":" + port + "/" + index + "/" + type + "/";
//		esSearchUrl = "http://" + serverIP + ":" + port + "/" + index + "/" + type + "/_search";
//		esIndexUrl = "http://" + serverIP + ":" + port + "/" + index + "/";
//		esIndexTypeUrl = "http://" + serverIP + ":" + port + "/" + index + "/" + type + "/";
//		esMapping = "http://" + serverIP + ":" + port + "/" + index + "/" + type + "/_mapping";
//		esScanUrl = "http://" + serverIP + ":" + port + "/" + index + "/" + type + "/_search?search_type=scan&scroll=2m&size=";
//		esScrollUrl = "http://" + serverIP + ":" + port + "/_search/scroll?scroll=2m&scroll_id=";
//	}
//
//
//	/**
//	 * Quick check the results for "ok":true
//	 * @param response
//	 * @return true if result is true, false otherwise
//	 */
//	static boolean checkOk(String response) {
//		Matcher m = PAT_ES_SUCCESS.matcher(response);
//		if (m.find()) {
//			return true;
//		}
//		return false;
//	}
//	
//	/**
//	 * Create the current index.  Will fail if the index already exists.
//	 * @return results with "ok":true if index successfully created, false if it fails.
//	 */
//	String createIndex() {
//		boolean res = indexExists();
//		@SuppressWarnings("unused")
//		int resCode;
//		if (!res) {
//			resCode = doHttpOperation(esIndexUrl, HTTP_PUT, null);
//		}
//		return lastResponse;
//	}
//	
//	boolean indexExists() {
//		int resCode = doHttpOperation(esIndexUrl, HTTP_HEAD, null);
//		return (resCode == 200);
//	}
//	
//	boolean indexTypeExists() {
//		int resCode = doHttpOperation(esIndexTypeUrl, HTTP_HEAD, null);
//		return (resCode == 200);
//	}
//	
//	/**
//	 * Delete the index
//	 *   On success returns: {"ok":true,"acknowledged":true}
//	 *   On failure returns: {"error":"IndexMissingException[[domeo2] missing]","status":404}
//	 * @return
//	 */
//	String deleteIndex() {
//		@SuppressWarnings("unused")
//		int resCode = doHttpOperation(esIndexUrl, HTTP_DELETE, null);
//		return lastResponse;
//	}
//	
//	/**
//	 * Delete the index type 
//	 *   On success returns: {"ok":true}
//	 *   On failure returns: {"error":"TypeMissingException[[newindex] type[newtype2] missing]","status":404}
//	 * @return
//	 */
//	String deleteIndexType() {
//		@SuppressWarnings("unused")
//		int resCode = doHttpOperation(esIndexTypeUrl, HTTP_DELETE, null);
//		return lastResponse;
//	}
//	
//	/**
//	 * Do mapping operation
//	 * @param mapping
//	 * @param operation: HTTP_GET, HTTP_PUT, HTTP_DELETE, ...
//	 * @return
//	 */
//	String doMapping(String mapping, String operation) {
//		@SuppressWarnings("unused")
//		int resCode = doHttpOperation(esMapping, operation, mapping);
//		return lastResponse;
//	}
//	
//	/**
//	 * Efficient way to process large number of results is via scan-scroll
//	 * @param numDocsPerShard
//	 * @return
//	 */
//	String getAllDocumentsScan(int numDocsPerShard) {
//		String url = esScanUrl + numDocsPerShard;
//		@SuppressWarnings("unused")
//		int resCode = doHttpOperation(url, HTTP_GET, ES_QUERY_SCROLL);
//		return lastResponse;
//	}
//	
//	/**
//	 * Note: when using curl we need to use &scrollId=scrollvalue
//	 * @param scroll
//	 * @return
//	 */
//	String getAllDocumentsScroll(String scroll) {
//		@SuppressWarnings("deprecation")
//		String urlEncode = URLEncoder.encode(scroll);
//		String url = esScrollUrl + urlEncode;
//		@SuppressWarnings("unused")
//		int resCode = doHttpOperation(url, HTTP_GET, null);
//		return lastResponse;
//	}
//	
//	/**
//	 * Insert a document into the index using the specified document Id.  This
//	 * allows for replacing existing documents.  Remove colons from field names
//	 * @param doc to be inserted
//	 * @param docId document id for inserted document
//	 * @return documnent id for inserted document
//	 */
//	String insertDocument(String doc, String docId) {
//		String encodedDoc = encodeNS(doc);
//		String url = esInsertDeleteUrl + docId;
//		@SuppressWarnings("unused")
//		int resCode = doHttpOperation(url, HTTP_POST, encodedDoc);
//		return lastResponse;
//	}
//
//	/**
//	 * Insert a document into the index.  Remove colons from field names
//	 * @param doc to be inserted
//	 * @return result header with document id for inserted document
//	 */
//	String insertDocument(String doc) {
//		String encodedDoc = encodeNS(doc);
//		@SuppressWarnings("unused")
//		int resCode = doHttpOperation(esInsertDeleteUrl, HTTP_POST, encodedDoc);
//		return lastResponse;
//	}
//
//	/**
//	 * Remove a document from the index
//	 * 
//	 * WARNING: If the document to be deleted is not in the index then this generates 
//	 * a java.io.FileNotFoundException
//	 * 
//	 * @param docId of doc to be removed
//	 * @return
//	 */
//	String deleteDocument(String docId) {
//		String deleteUrl = esInsertDeleteUrl + docId;
//		@SuppressWarnings("unused")
//		int resCode = doHttpOperation(deleteUrl, HTTP_DELETE, null);
//		return lastResponse;
//	}
//	
//	/**
//	 * Retrieve a single document using its _id field.  Doc should be decoded
//	 * so that any namespace characters that were removed at insertion are replaced.
//	 * @param docID
//	 * @param permissions optional, used for filtering by permission
//	 * @return retrieved document preceded by results header
//	 */
//	String getDocument(String docID) {
//		String data =  "{ \"query\" : { \"term\" : { \"_id\" : \"" + docID + "\" } } }";
//		
//		@SuppressWarnings("unused")
//		int resCode = doHttpOperation(esSearchUrl, HTTP_POST, data);
//		return decodeNS(lastResponse);
//	}
//	
//	/**
//	 * Retrieve a single document using its _id field.  Doc should be decoded
//	 * so that any namespace characters that were removed at insertion are replaced.
//	 * @param docID
//	 * @param permissions optional, used for filtering by permission
//	 * @return retrieved document preceded by results header
//	 */
//	String getDocument(String docID, DomeoPermissions permissions3) {
//		String data = "";
//		if (permissions3 == null) {
//			data = String.format(ES_QUERY_BY_DOCID, docID);
//		} 
//		else {
//			String filter = permissions3.buildQueryFilter();
//			data = "{ \"query\" : { \"term\" : { \"_id\" : \"" + docID + "\" } } " + filter + " }";
//		}
//		
//		@SuppressWarnings("unused")
//		int resCode = doHttpOperation(esSearchUrl, HTTP_POST, data);
//		return decodeNS(lastResponse);
//	}
//	
//	/**
//	 * Build a simple parsed field query string
//	 * @param field field we are searching against
//	 * @param val parsed phrase to match
//	 * @param from starting result number
//	 * @param size maximum number of results to show
//	 * @return formatted query string
//	 */	
//	String buildQuery(String field, String val, int from, int size, DomeoPermissions permissions3) {
//		StringBuffer sb = new StringBuffer("{ ");
//
//		// Check for starting position (from) and max results (size)
//		if ((from > -1) && (size > -1)) {
//			sb.append("\"from\" : " + from + ", \"size\" : " + size + ", ");
//		}		
//		else if (from > -1) { // from only, no size
//			sb.append("\"from\" : " + from + ", ");
//		}		
//		else if (size > -1) { // size only, no from
//			sb.append("\"size\" : " + size + ", ");
//		}
//
//		if (permissions3 == null) {
//			sb.append("\"fields\" : [\"_id\"], \"query\" : { \"match\" : { \"" + field + "\": \"" + val + "\" } } } ");
//		}
//		else {
//			String filter = permissions3.buildQueryFilter();
//			sb.append("\"fields\" : \"[_id]\", \"query\" : { \"match\" : { \"" + field + "\": \"" + val + 
//					"\" } } " + filter + " } ");
//		}
//		return sb.toString();
//	}
//	
//
//	
//	/**
//	 * Build a simple phrase query string
//	 * @param field field we are searching against
//	 * @param val parsed phrase to match
//	 * @param from starting result number
//	 * @param size maximum number of results to show
//	 * @return formatted query string
//	 */	
//	String buildPhraseQuery(String field, String val, int from, int size, DomeoPermissions permissions3) {
//		StringBuffer sb = new StringBuffer("{ ");
//		
//		// Check for starting position (from) and max results (size)
//		if ((from > -1) && (size > -1)) {
//			sb.append("\"from\" : " + from + ", \"size\" : " + size + ", ");
//		}		
//		else if (from > -1) { // from only, no size
//			sb.append("\"from\" : " + from + ", ");
//		}		
//		else if (size > -1) { // size only, no from
//			sb.append("\"size\" : " + size + ", ");
//		}
//
//		if (permissions3 == null) {
//			sb.append("\"fields\" : [\"_id\"], \"query\" : { \"match_phrase\" : { \"" + field + "\": \"" + val + "\" } } } ");
//		}
//		else {
//			String filter = permissions3.buildQueryFilter();
//			sb.append("\"fields\" : \"[_id]\", \"query\" : { \"match_phrase\" : { \"" + field + "\": \"" + val + 
//					"\" } } " + filter + " } ");
//		}
//
//		return sb.toString();
//	}
//	
//	/**
//	 * Build a simple term query string
//	 * @param field field we are searching against
//	 * @param val unparsed keyword to match
//	 * @param from starting result number
//	 * @param size maximum number of results to show
//	 * @return formatted query string
//	 */	
//	String buildTermQuery(String field, String val, int from, int size, DomeoPermissions permissions3) {
//		StringBuffer sb = new StringBuffer("{ ");
//		
//		// Check for starting position (from) and max results (size)
//		if ((from > -1) && (size > -1)) {
//			sb.append("\"from\" : " + from + ", \"size\" : " + size + ", ");
//		}		
//		else if (from > -1) { // from only, no size
//			sb.append("\"from\" : " + from + ", ");
//		}		
//		else if (size > -1) { // size only, no from
//			sb.append("\"size\" : " + size + ", ");
//		}
//
//		if (permissions3 == null) {
//			sb.append("\"fields\" : [\"_id\"], \"query\" : { \"term\" : { \"" + field + "\": \"" + val + "\" } } } ");
//		}
//		else {
//			String filter = permissions3.buildQueryFilter();
//			sb.append("\"fields\" : [\"_id\"], \"query\" : { \"term\" : { \"" + field + "\": \"" + val + 
//					"\" } } " + filter + " } ");
//		}
//		return sb.toString();
//	}
//	
//	/**
//	 * Build a simple boolean query string
//	 * @param field field we are searching against
//	 * @param val parsed string of 1 or more words
//	 * @param operator and or or
//	 * @param from starting result number
//	 * @param size maximum number of results to show
//	 * @param from starting result number
//	 * @param size maximum number of results to show
//	 * @return formatted query string
//	 */
//	String buildSimpleParsedBooleanQuery(String field, String val, String operator, int from, int size, DomeoPermissions permissions3) {
//		StringBuffer sb = new StringBuffer("{ ");
//		
//		// Check for starting position (from) and max results (size)
//		if ((from > -1) && (size > -1)) {
//			sb.append("\"from\" : " + from + ", \"size\" : " + size + ", ");
//		}		
//		else if (from > -1) { // from only, no size
//			sb.append("\"from\" : " + from + ", ");
//		}		
//		else if (size > -1) { // size only, no from
//			sb.append("\"size\" : " + size + ", ");
//		}
//		
//		// Use "fields" to limit fields returned in results
//		if (permissions3 == null) {
//			sb.append("\"fields\" : [\"_id\"], \"query\" : { \"match\" : { \"" + field + "\": { \"query\" : \"" + val + 
//					"\", \"operator\" : \"" + operator + "\" } } } }");
//		}
//		else {
//			String filter = permissions3.buildQueryFilter();
//			sb.append("\"fields\" : [\"_id\"], \"query\" : { \"match\" : { \"" + field + "\": { \"query\" : \"" + val + 
//					"\", \"operator\" : \"" + operator + "\" } } } " + filter + " } ");
//		}
//		
//		return sb.toString();
//	}
//	
//	/**
//	 * Build a generic boolean query string
//	 * @param fields array of fields
//	 * @param vals array of vals
//	 * @param parsed array of "match" or "term" for parsed and unparsed
//	 * @param operator "or" or "and"
//	 * @param from starting result number
//	 * @param size maximum number of results to show
//	 * @return formatted query string
//	 */	
//	String buildGenericBooleanQuery(String[] fields, String[] vals, String[] parsed, String operator, int from, int size, DomeoPermissions permissions3) {
//		StringBuffer sb = new StringBuffer("{ ");
//		
//		// Check for starting position (from) and max results (size)
//		if ((from > -1) && (size > -1)) {
//			sb.append("\"from\" : " + from + ", \"size\" : " + size + ", ");
//		}		
//		else if (from > -1) { // from only, no size
//			sb.append("\"from\" : " + from + ", ");
//		}		
//		else if (size > -1) { // size only, no from
//			sb.append("\"size\" : " + size + ", ");
//		}
//		
//		// Use "fields" to limit fields returned in results
//		sb.append("\"fields\" : [\"_id\"], \"query\" : { \"bool\" : { ");
//		
//		// Operator AND -> must and OR -> should
//		if (operator.equals(AND_OPERATOR)) {
//			sb.append("\"must\" : [  ");
//		}
//		else {
//			sb.append("\"should\" : [  ");
//		}
//		
//		// Append clause for each field
//		for (int i = 0; i < fields.length; i++) {
//			if (i != 0) {
//				sb.append(", ");
//			}
//			sb.append("{ " + "\"" + parsed[i] + "\"" + " : { " + "\"" + fields[i] + "\" : " + "\"" + vals[i] + "\"" + "} }");
//		}
//		if (permissions3 == null) {
//			sb.append("] } } }");
//		}
//		else {
//			String filter = permissions3.buildQueryFilter();
//			sb.append("] } } " + filter + " } ");
//
//			//sb.append("] } }, \"filter\" : { \"term\" : { \"" +
//			//permissions.key.getValue().toString() + "\" : " + "\"" + permissions.value + "\" } } } ");
//		}
//		
//		return sb.toString();
//	}
//
//	/**
//	 * Perform a single-field term (keyword) query.  Doc should be decoded
//	 * so that any namespace characters that were removed at insertion are replaced.
//	 * @param field to be searched
//	 * @param val one word to be matched (unparsed)
//	 * @param from starting result number
//	 * @param size maximum number of results to show
//	 * @return list of matching document ids
//	 */
//	String termQuery(String field, String val, int from, int size, DomeoPermissions permissions3) {
//		String query = buildTermQuery(field, val, from, size, permissions3);
//		@SuppressWarnings("unused")
//		int resCode = doHttpOperation(esSearchUrl, HTTP_POST, query);
//		return decodeNS(lastResponse);
//	}
//
//	/**
//	 * Perform a single-field query.  Doc should be decoded
//	 * so that any namespace characters that were removed at insertion are replaced.
//	 * @param field to be searched
//	 * @param val parsed phrase to be matched
//	 * @param from starting result number
//	 * @param size maximum number of results to show
//	 * @param permissions3
//	 * @return list of matching document ids
//	 */
//	String query(String field, String val, int from, int size, DomeoPermissions permissions3) {
//		String query = buildQuery(field, val, from, size, permissions3);
//		@SuppressWarnings("unused")
//		int resCode = doHttpOperation(esSearchUrl, HTTP_POST, query);
//		return decodeNS(lastResponse);
//	}
//
//	/**
//	 * Perform a single-field phrase query.  Doc should be decoded
//	 * so that any namespace characters that were removed at insertion are replaced.
//	 * @param field to be searched
//	 * @param val parsed phrase to be matched
//	 * @param from starting result number
//	 * @param size maximum number of results to show
//	 * @return list of matching document ids
//	 */
//	String phraseQuery(String field, String val, int from, int size, DomeoPermissions permissions3) {
//		String query = buildPhraseQuery(field, val, from, size, permissions3);
//		System.out.println(query);
//		@SuppressWarnings("unused")
//		int resCode = doHttpOperation(esSearchUrl, HTTP_POST, query);
//		return decodeNS(lastResponse);
//	}
//
//	/**
//	 * Perform a boolean query against all the parsed words withinin 1 field
//	 * Doc should be decoded so that any namespace characters that were removed 
//	 * at insertion are replaced.
//	 * Sample Results: {"took":3,"timed_out":false,"_shards":{"total":5,"successful":5,"failed":0},"hits":{"total":1,"max_score":0.008439008,"hits":[{"_index":"domeo","_type":"test","_id":"1","_score":0.008439008}]}}
//	 * @param field to be searched
//	 * @param val one or more words to be matched
//	 * @param operator "or" or "and"
//	 * @param from starting result number
//	 * @param size maximum number of results to show
//	 * @return list of matching document ids
//	 */
//	String booleanQuerySingleParsedField(String field, String val, String operator, int from, int size, DomeoPermissions permissions3) {
//		String query = buildSimpleParsedBooleanQuery(field, val, operator, from, size, permissions3);
//		@SuppressWarnings("unused")
//		int resCode = doHttpOperation(esSearchUrl, HTTP_POST, query);
//		return decodeNS(lastResponse);
//	}
//	
//	/**
//	 * Perform a boolean query against all the specified fields
//	 * Doc should be decoded so that any namespace characters that were removed 
//	 * at insertion are replaced.
//	 * @param fields array of fields
//	 * @param vals array of vals
//	 * @param parsed array of "match" or "term" for parsed and unparsed
//	 * @param operator "or" or "and"
//	 * @param from starting result number
//	 * @param size maximum number of results to show
//	 * @return search results
//	 */
//	String booleanQueryMultipleFields(String[] fields, String[] vals, String[] parsed, String operator, int from, int size, DomeoPermissions permissions3 ) {
//		String query = buildGenericBooleanQuery(fields, vals, parsed, operator, from, size, permissions3);
//		System.out.println(query);
//		@SuppressWarnings("unused")
//		int resCode = doHttpOperation(esSearchUrl, HTTP_POST, query);
//		return decodeNS(lastResponse);
//	}
//
//	
//	/**
//	 * Do the specified HTTP operation: GET, POST, PUT, DELETE
//	 * @param urlString
//	 * @param operation is GET, POST, PUT, etc
//	 * @param data may be null if GET
//	 * @return response code
//	 */
//	int doHttpOperation(String urlString, String operation, String data) {
//		OutputStreamWriter wr = null;
//		BufferedReader rd = null;
//		
//		int resCode = -1;
//		
//		StringBuffer sb = new StringBuffer();
//		lastResponse = " ";
//
//		try {
//		    // Send data
//		    URL url = new URL(urlString);
//		    HttpURLConnection conn = (HttpURLConnection)url.openConnection();;
//		    conn.setDoOutput(true);
//		    conn.setRequestMethod(operation);
//		    conn.setReadTimeout(HTTP_READ_TIMEOUT);
//
//		    // POST and PUT write data
//		    if (data != null) {
//		    	wr = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
//		    	wr.write(data);
//		    	wr.flush();
//		    }
//
//		    resCode = conn.getResponseCode();
//
//		    // Get the response - ok if nothing (happens on 404 returns so need try catch for FileNotFoundException)
//	    	if (resCode <= 299) {  //covers 200, 201 for inserts
//		    	rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
//		    	String line;
//		    	while ((line = rd.readLine()) != null) {
//		    		sb.append(line + NEWLINE);
//		    	}
//	    	}
//
//	    	else {  //404 and 400 and 500 errors 
//	    		InputStream errorStream = conn.getErrorStream();
//	    		if (errorStream != null) {
//	    			rd = new BufferedReader(new InputStreamReader(errorStream, "UTF-8"));
//	    			String line1;
//	    			while ((line1 = rd.readLine()) != null) {
//	    				sb.append(line1 + NEWLINE);
//	    			}
//	    		}
//	    	}
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally {
//			try { if (wr != null) { wr.close(); } } catch (IOException e) { }
//			try { if (rd != null) { rd.close(); } } catch (IOException e) { }
//		}
//		
//		lastResponse = sb.toString();
//		return resCode;
//	}
//
//	/**
//	 * Replace any ':' characters in field names with a replacement string since
//	 * ElasticSearch does not permit these.
//	 * Uses JSON-Simple parser
//	 * @param doc that may contain ':' character in field names
//	 * @return document with field names encoded
//	 */	
//	String encodeNS(String doc)  {
//		JSONParser parser = new JSONParser();
//		Transformer transformer = new Transformer();
//		try {
//			parser.parse(doc, transformer);
//			Object value = transformer.getResult();
//			return value.toString();
//		} catch (ParseException e) { 
//			e.printStackTrace(); 
//			return "";
//		}
//	}
//	
//	
//	/**
//	 * Replace all encoded colons in field names with the single colon character
//	 * @param doc to be decoded
//	 * @return decoded doc
//	 */
//	String decodeNS(String doc) {
//		return doc.replaceAll(COLON_REPLACEMENT, ":");
//	}
//
//	
//	// Convenience method to read in sample json doc for debug
//	String readSampleJsonDoc(String file) {
//		StringBuffer sb = new StringBuffer();
//		BufferedReader br = null;
//		
//		try {
//			br = new BufferedReader(new FileReader(new File(file)));
//			String line;
//			while ((line = br.readLine()) != null) {
//				sb.append(line + NEWLINE);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			try { if (br != null) { br.close(); } } catch (IOException e) { }
//		}
//		return sb.toString();
//	}
//	
//	/**
//	 * Run some basic tests
//	 */
//	void doTests() {		
//		String doc = "", r = "";
//		
//		DomeoPermissions dp3 = new DomeoPermissions(null, null, new String[] {"urn:group:uuid:4028808c3dccfe48013dccfe95ea0005 1"});
//		r = getDocument("1", dp3);
//		
//		r = termQuery("domeo_!DOMEO_NS!_agents.@type", "foafx:Person", 0, 10, dp3);
//		
//		r = phraseQuery("ao_!DOMEO_NS!_item.ao_!DOMEO_NS!_context.ao_!DOMEO_NS!_hasSelector.ao_!DOMEO_NS!_suffix", "enabling application", 0, 10, dp3);
//				
//		dp3 = new DomeoPermissions(null, null, new String[] {"urn:group:uuid:4028808c3dccfe48013dccfe95ea0005 1"});
//		r = query("ao_!DOMEO_NS!_item.ao_!DOMEO_NS!_context.ao_!DOMEO_NS!_hasSelector.ao_!DOMEO_NS!_suffix", "enabling application", 0, 10, dp3);
//		
//		// Test: Term (keyword) query
//		//r = termQuery("domeo_!DOMEO_NS!_agents.@type", "foafx:Person", 0, 10, dp);
//		
//		// Test: Phrase query
//		r = phraseQuery("dct_!DOMEO_NS!_description", "created automatically", 0, 10, dp3);
//		
//		
//		// Test: Delete a document
//		//r = deleteDocument("7TdnuBsjTjWaTcbW7RVP3Q");
//		
//		// Test: Generic boolean query: 4 fields (3 keyword fields, 1 parsed field)
//		
//		String[] fields = {"ao_!DOMEO_NS!_item.@type", "ao_!DOMEO_NS!_item.@id", "ao_!DOMEO_NS!_item.ao_!DOMEO_NS!_body.@type", "ao_!DOMEO_NS!_item.ao_!DOMEO_NS!_body.cnt_!DOMEO_NS!_chars"}; 
//		String[] vals = {"ao:Highlight", "urn:domeoclient:uuid:D3062173-8E53-41E9-9248-F0B8A7F65E5B", "cnt:ContentAsText", "paolo"};
//		String[] parsed = {"term", "term", "term", "match"};
//		r = booleanQueryMultipleFields(fields, vals, parsed, "and", 0, 10, dp3);
//
//		// Test: Single field boolean query
//		r = booleanQuerySingleParsedField(
//				"ao_!DOMEO_NS!_item.ao_!DOMEO_NS!_context.ao_!DOMEO_NS!_hasSelector.ao_!DOMEO_NS!_suffix",
//				"formal biomedical ontologies",
//				"or", 0, 10, null);
//		
//		// Test: Retrieve a single doc by id
//		r = getDocument("aviMdI48QkSGOhQL6ncMZw",null);
//
//		// Test: insert a document, return it's auto-assigned id
//		doc = "{ \"f1\" : \"field value one\", \"f2\" : \"field value two\" }";
//		r = insertDocument(doc);
//
//		// Test: insert a doc with specified id (replace if already present)
//		doc = "{ \"f1\" : \"field value one\", \"f2\" : \"field value two\" }";
//		r = insertDocument(doc, "5");
//		System.out.println(r);	
//		
//		// Test: insert json document and try to remove it
//		doc = readSampleJsonDoc("/temp/sample_domeo_doc.json");
//		System.out.println(doc);	
//		r = insertDocument(doc);
//	}
//
//	
//	/**
//	 * Sample calling examples
//	 * @param args
//	 * @throws IOException
//	 */
//	public static void main(String[] args) throws IOException {
//		ElasticSearchWrapper s = new ElasticSearchWrapper("domeo", "docs", "localhost", "9200");
//		s.doTests();
//		//s.createClient("localhost", 9300);	
//	}

}
