package org.mindinformatics.domeo.persistence;

import java.io.*;
import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ContentHandler;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Sax-like JSON parser
 * Slightly modified from: http://code.google.com/p/json-simple/wiki/DecodingExamples
 * Original License: Apache 2.0 http://code.google.com/p/json-simple/
 */

class Transformer implements ContentHandler {

	/** Unique replacement character string for colons found in JSON object key */
	final static String COLON_REPLACEMENT = "_!DOMEO_NS!_";
	
	/** Stack of transformed JSON objects */
	private Stack<Object> valueStack;


	/**
	* Retrieve the transformed JSON document from the stack.
	* @return transformed document or null
	*/
	public Object getResult(){
		if(valueStack == null || valueStack.size() == 0) {
			return null;
		}
		return valueStack.peek();
	}

	/**
	* End of JSON array (right square bracket)
	*/
	public boolean endArray () throws ParseException, IOException {
		trackBack();
		return true;
	}

	/**
	* End of JSON document detected
	*/
	public void endJSON () throws ParseException, IOException {}

	/**
	* End of JSON object (right brace)
	*/
	public boolean endObject () throws ParseException, IOException {
		trackBack();
		return true;
	}

	/**
	* If this is the end of a JSON object, then we pop off one of the duplicate objects
	*/
	private void trackBack(){
		if(valueStack.size() > 1) {
			Object value = valueStack.pop();
			Object prev = valueStack.peek();
			if(prev instanceof String) {
				valueStack.push(value);
			}
		}
	}

	/**
	* Add the new value on to the stack.
	* If the previous item on the stack is a list, then append the value to the list
	* @param value
	*/
	private void consumeValue(Object value) {
	    if (valueStack.size()==0){
			valueStack.push(value);
        }
		else {
			Object prev = valueStack.peek();
			if (prev instanceof List) {
				@SuppressWarnings("unchecked")
				List<Object> array = (List<Object>)prev;
				array.add(value);
			}
			else {
				valueStack.push(value);
			}
		}
	}

	/**
	* The value part of the JSON key-value pair
	* We don't care if this has embedded colon characters
	*/
	public boolean primitive (Object value) throws ParseException, IOException {
		consumeValue(value);
		return true;
	}

	/**
	* Either we have reached the end of a JSON object or we have just called
	* primitive above to push the value of the JSON key-value pair on to the
	* stack.
	*
	* Pop the key-value items off the stack and add them as children of the
	* previous stack entry (the parents)
	*/
	public boolean endObjectEntry () throws ParseException, IOException {
		Object value = valueStack.pop();
		Object key = valueStack.pop();
		@SuppressWarnings("unchecked")
		Map<Object, Object> parent = (Map<Object, Object>)valueStack.peek();
		parent.put(key, value);
		return true;
	}

	/**
	* Start of JSON array (left square bracket detected)
	*/
	public boolean startArray () throws ParseException, IOException {
		@SuppressWarnings("rawtypes")
		List array = new JSONArray();
		consumeValue(array);
		valueStack.push(array);
		return true;
	}

	/**
	* Start of JSON document detected
	*/
	public void startJSON () throws ParseException, IOException {
		valueStack = new Stack<Object>();
	}

	/**
	* Start of JSON object (left brace) detected
	*/
	public boolean startObject () throws ParseException, IOException {
		@SuppressWarnings("rawtypes")
		Map object = new JSONObject();
		consumeValue(object);
		valueStack.push(object);
		return true;
	}

	/**
	* Found the key of a JSON object
	* Replace all colons in key before placing on stack
	*/
	public boolean startObjectEntry (String key) throws ParseException, IOException {
		key = key.replaceAll(":", COLON_REPLACEMENT);
		valueStack.push(key);
		return true;
	}

	/**
	* Demonstrate parser with namespace encoding and then decoding
	*
	* The Domeo document key-value pairs may include namespaces. The colon
	* character in the namespace is incompatible with ElasticSearch and must
	* be removed before the document is indexed.
	*/
	public static void main(String[] args) {
		// Sample json with namespace key
		String jsonText = "{\"ns:first\": \"peanuts\", \"second\": [4, 5, 6], \"third\": 789}";

		// 1. Demonstrate parse and replace colons in namespace
		JSONParser parser = new JSONParser();
		Transformer transformer = new Transformer();
		
		try {
			parser.parse(jsonText, transformer);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		Object value = transformer.getResult();
		System.out.println("Final value:\n" + value);
		
		// 2. Return the colons to the namespace form
		System.out.println(value.toString().replaceAll(COLON_REPLACEMENT, ":"));		
	}

}