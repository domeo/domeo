package org.mindinformatics.domeo.persistence;



import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Basic Java wrapper for the Sleepy Mongoose driver
 * 
 * This is not production quality code.  For one thing, the simplistic GET and
 * POST code should be replaced with something like Jakarta Commons HttpClient.
 * The code is not currently thread-safe due to the re-use of output buffers.
 * 
 * Sleepy Mongoose references:
 * - http://www.kchodorow.com/blog/2010/02/22/sleepy-mongoose-a-mongodb-rest-interface/
 * = https://github.com/kchodorow/sleepy.mongoose
 *
 * @author Keith Gutfreund, Elsevier Labs 2012
 */
public class SleepyMongooseWrapper  {

	/** Sleepy Mongoose url */
	//final String SLEEPY_MONGOOSE_URL = "http://50.17.25.89:27080/"; //"http://localhost:27080/";
	String SLEEPY_MONGOOSE_URL;
	
	/** MongoDB database name */
	//final String MONGODB_DB = "foo";
	String MONGODB_DB;
	
	/** MongoDB collection name */
	//final String MONGODB_COLLECTION = "bar";
	String MONGODB_COLLECTION;
	
	/** Find one or more docs */
	final String SLEEPY_MONGOOSE_FIND = "_find";
	
	/** Insert a doc */
	final String SLEEPY_MONGOOSE_INSERT = "_insert";
	
	/** Remove a doc */
	final String SLEEPY_MONGOOSE_DELETE = "_remove";
	
	/** Create the Sleepy Mongoose find url */
	String findUrl() { return SLEEPY_MONGOOSE_URL + MONGODB_DB + "/" + MONGODB_COLLECTION + "/" + SLEEPY_MONGOOSE_FIND; }
	
	/** Create the Sleepy Mongoose insert url */
	String insertUrl() { return SLEEPY_MONGOOSE_URL + MONGODB_DB + "/" + MONGODB_COLLECTION + "/" + SLEEPY_MONGOOSE_INSERT; }
	
	/** Create the Sleepy Mongoose remove url */
	String deleteUrl() { return SLEEPY_MONGOOSE_URL + MONGODB_DB + "/" + MONGODB_COLLECTION + "/" + SLEEPY_MONGOOSE_DELETE; }
	
	/** Last response from GET, POST - Good for debug but this is not thread safe! */
	String lastResponse;
	
//	public SleepyMongooseWrapper() {
//		
//	}
	
	public SleepyMongooseWrapper(String url, String database, String collection) {
		SLEEPY_MONGOOSE_URL = url;
		MONGODB_DB = database;
		MONGODB_COLLECTION = collection;
	}
	
	// Convenience method to read in sample json doc 
	String readSampleJsonDoc(String file) {
		StringBuffer sb = new StringBuffer();
		BufferedReader br = null;
		
		try {
			br = new BufferedReader(new FileReader(new File(file)));
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try { if (br != null) { br.close(); } } catch (IOException e) { }
		}
		return sb.toString();
	}
	
	
	/**
	 * Insert document into the MongoDB collection
	 * @param doc
	 * @return success: {"status": {"connectionId": 4, "ok": 1.0, "err": null, "n": 0}, "oids": {"$oid": "50548c726865e40c86114091"}}
	 */
	String doMongoDBInsert(String doc) {
		return doHttpPost(insertUrl(), "docs", doc);
	}
	
	/**
	 * Return all docs from MongoDB collection
	 */
	String doMongoDBFindAll() {
		return doHttpGet(findUrl());
	}
	
	/**
	 * Find a mongodb object by its objectId
	 * Example: $curl -X GET 'http://localhost:27080/foo/bar/_find?criteria={"_id":{"$oid":"4f8c6f05db61e2a72600001d"}}'
	 * @param objectId
	 * @return successful return {"ok": 1, "results": [{"x": 2, "_id": {"$oid": "5053cd03e16977d9fc000006"}}], "id": 4}
	 * @return failure return {"ok": 1, "results": [], "id": 5}
	 */
	String doMongoDBFindByObjectId(String objectId) {		
		String queryParam = "";
		String criteria = "{\"_id\":{\"$oid\":\"" + objectId + "\"}}";
	    try {
	    	queryParam = URLEncoder.encode("criteria", "UTF-8") + "=" + URLEncoder.encode(criteria, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

		// Construct query parameter
		String findUrlVal = findUrl() + "?" + queryParam;		
		return doHttpGet(findUrlVal);
	}
	
	/**
	 * Find objects by key + integer value pair
	 * @param key
	 * @param val
	 * @return success {"ok": 1, "results": [{"x": 2, "_id": {"$oid": "5053cd03e16977d9fc000006"}}], "id": 10}
	 * @return failure {"ok": 1, "results": [], "id": 11}
	 */
	String doMongoDBFindByKeyVal(String key, int val) {
		String queryParam = "";
		String criteria = "{\"" + key + "\":" + val + "}"; //{\"$oid\":\"" + objectId + "\"}}";
	    try {
	    	queryParam = URLEncoder.encode("criteria", "UTF-8") + "=" + URLEncoder.encode(criteria, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

		// Construct query parameter
		String findUrlVal = findUrl() + "?" + queryParam;		
		return doHttpGet(findUrlVal);
	}
	
	/**
	 * Find objects by key + val (string) pair
	 * @param key
	 * @param val
	 * @return success {"ok": 1, "results": [{"x": "2", "_id": {"$oid": "1233cd03e16977d9fc000006"}}], "id": 10}
	 * @return failure {"ok": 1, "results": [], "id": 11}
	 */
	String doMongoDBFindByKeyVal(String key, String val) {
		String queryParam = "";
		String criteria = "{\"" + key + "\":" + "\"" + val + "\"}"; //{\"$oid\":\"" + objectId + "\"}}";
	    try {
	    	queryParam = URLEncoder.encode("criteria", "UTF-8") + "=" + URLEncoder.encode(criteria, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

		// Construct query parameter
		String findUrlVal = findUrl() + "?" + queryParam;		
		return doHttpGet(findUrlVal);
	}
	
	/**
	 * Remove one or more documents from MongoDB collection
	 * @param val
	 * @return n is the number of documents deleted as per below:
	 * @return success: {"connectionId": 4, "ok": 1.0, "err": null, "n": 1}
	 * @return failure: {"connectionId": 4, "ok": 1.0, "err": null, "n": 0}
	 */
	String doMongoDBDelete(String val) {
		return doHttpPost(deleteUrl(), "criteria", val);
	}

	/**
	 * Very basic HTTP GET
	 * @param urlString
	 * @return
	 * @throws IOException
	 */
	String doHttpGet(String urlString) {
		InputStream response = null;
		BufferedReader reader = null;
		
		StringBuffer sb = new StringBuffer();
		lastResponse = "";

		try {
			// Send
			URL url = new URL(urlString);
			response = url.openStream();

			// Read response
			reader = new BufferedReader(new InputStreamReader(response));
			for (String line; (line = reader.readLine()) != null;) {
				sb.append(line + "\n");
				System.out.println(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try { if (response != null) { response.close(); } } catch (IOException e) { }
			try { if (reader != null) { reader.close(); } } catch (IOException e) { }
		}

		lastResponse = sb.toString();
		return lastResponse;
	}
	
	/**
	 * Very basic HTTP POST
	 * @param urlString
	 * @param key
	 * @param val
	 * @return lastResponse (may be empty)
	 */
	String doHttpPost(String urlString, String key, String val) {
		OutputStreamWriter wr = null;
		BufferedReader rd = null;
		
		StringBuffer sb = new StringBuffer();
		lastResponse = "";

		try {
		    // Construct data
		    String data = URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(val, "UTF-8");
		    data += "&" + URLEncoder.encode("safe", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8");

		    // Send data
		    URL url = new URL(urlString);
		    URLConnection conn = url.openConnection();
		    conn.setDoOutput(true);
		    wr = new OutputStreamWriter(conn.getOutputStream());
		    wr.write(data);
		    wr.flush();

		    // Get the response
		    rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
		    while ((line = rd.readLine()) != null) {
		    	sb.append(line + "\n");
		    }
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try { if (wr != null) { wr.close(); } } catch (IOException e) { }
			try { if (rd != null) { rd.close(); } } catch (IOException e) { }
		}
		
		lastResponse = sb.toString();
		return lastResponse;
	}
	
	void doTests() {
		String res = "";
		res = doMongoDBFindByObjectId("5053cd03e16977d9fc000006");
		res = doMongoDBFindByKeyVal("x", 2);
		res = doMongoDBFindByKeyVal("x", "2");
		
		System.out.println(res);
	}
	
//	/**
//	 * Sample calling examples
//	 * @param args
//	 * @throws IOException
//	 */
//	public static void main(String[] args) throws IOException {
//		SleepyMongooseWrapper s = new SleepyMongooseWrapper();
//		@SuppressWarnings("unused")
//		String res;  String jsonDoc;
//
//		s.doTests();
//		
//		// Test: insert json document and try to remove it
//		jsonDoc = s.readSampleJsonDoc("/temp/sample_domeo_doc.json");
//		res = s.doMongoDBInsert(jsonDoc);
//		res = s.doMongoDBDelete("{\"domeo:isLocked\":\"true\"}");  //should fail
//		res = s.doHttpGet(s.findUrl());
//		res = s.doMongoDBDelete("{\"domeo:isLocked\":\"false\"}");  //should succeed
//		res = s.doHttpGet(s.findUrl());
//		
//		// Test: find document by its mongodb object id
//		res = s.doMongoDBFindByObjectId("5053cd03e16977d9fc000006");
//		
//		// Test: insert and remove multiple documents
//		res = s.doMongoDBDelete("{\"x\":13}");
//		res = s.doHttpGet(s.findUrl());
//		res = s.doMongoDBDelete("{\"x\":14}");
//		res = s.doMongoDBInsert("{\"x\":15}");
//		res = s.doMongoDBInsert("[{\"x\":14},{\"x\":15}]");
//		res = s.doHttpGet(s.findUrl());
//		res = s.doMongoDBDelete("[{\"x\":13},{\"x\":14},{\"x\":15}]");  //doesn't work
//		res = s.doHttpGet(s.findUrl());
//	}

}
