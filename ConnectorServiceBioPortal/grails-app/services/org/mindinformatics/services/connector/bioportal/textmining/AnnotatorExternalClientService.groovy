/*
* Copyright 2013 Massachusetts General Hospital
*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.mindinformatics.services.connector.bioportal.textmining

import org.apache.http.conn.params.ConnRoutePNames
import org.mindinformatics.domeo.grails.plugins.utils.ConnectorHttpResponseException
import org.mindinformatics.domeo.grails.plugins.utils.MiscUtils
import org.mindinformatics.services.connector.bioportal.textmining.BioPortalAnnotatorResults.Annotation

import groovyx.net.http.ContentType
import groovyx.net.http.EncoderRegistry
import groovyx.net.http.HTTPBuilder


/**
 * @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
 * @author Marco Ocana
 *
 * This is the client for the NCBO Text Mining Service. It should not have any dependencies on the rest of the system
 */
class AnnotatorExternalClientService {
    static transactional = false
	
	def grailsApplication;
    def domeoConfigAccessService;
	
	/**
	 * We will submit text at chunks of this size plus the length of an additional word (don't wan't to split whole words)
	 */
	int MAX_INITIAL_TEXT_LENGTH = 10000
	static final String SERVICE_URL = 'http://rest.bioontology.org/obs/annotator'
    static final String ONTOLOGIES_LIST_URL = 'http://rest.bioontology.org/obs/ontologies?apikey='
    
	List<ExtendedRecord> findAllOntologies(String){
		HTTPBuilder http = new HTTPBuilder(ONTOLOGIES_LIST_URL+apikey)
        http.encoderRegistry = new EncoderRegistry(charset: MiscUtils.DEFAULT_ENCODING)
        if(domeoConfigAccessService.isProxyDefined()) {
            http.client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, domeoConfigAccessService.getProxyHttpHost());
        }

   		http.request(groovyx.net.http.Method.GET) {
            requestContentType = groovyx.net.http.ContentType.URLENC


			response.success = {resp, xml ->
				log.debug("got a $resp")
				log.debug("The response contenType is $resp.contentType")
				//  theLogger.debug("The output from stream follows:\n")
				def parser = new BioPortalOntologyListParser()
				List<ExtendedRecord> results = parser.parse(xml)
				return results
			}
			response.failure = {resp, xml ->
				log.error(resp.getStatusLine() + ' got failure')
				def errorObject = new BioPortalTextMiningResultsParser(xml).parseError()
				throw new AnnotatorException(resp, errorObject)
			}
		}
	}
	 
//  List<BioontologyOntologyExtendedRecord> getAllAvailableOntologies(){
//    //Magic 28 comes from  http://www.bioontology.org/wiki/index.php/Annotator_User_Guide
//    this.findAllOntologies().findAll {it.status == '28' }
//  }


	BioPortalAnnotatorResults textmineDocument(BioPortalTextMiningRequestParameters params) throws AnnotatorException {
		String originalTextToAnnotate = params.textToAnnotate
		List<Range> ranges = this.rangesForText(params.textToAnnotate)
		BioPortalAnnotatorResults  consolidatedResults = new BioPortalAnnotatorResults()
		consolidatedResults.textToAnnotate = originalTextToAnnotate
		try {
			ranges.each{Range textRange ->
				params.textToAnnotate = originalTextToAnnotate[textRange]
				BioPortalAnnotatorResults currentResults = _callWebService(params)
				currentResults.annotations.each{Annotation annotation->
					def context = annotation.context
					context.from = context.from + textRange.from
					context.to = context.to + textRange.from
					consolidatedResults.annotations << annotation
				}
				consolidatedResults.accessDate = currentResults.accessDate
				consolidatedResults.accessedResource = currentResults.accessedResource
			}
		} finally {
			params.textToAnnotate = originalTextToAnnotate
		}
		return consolidatedResults

	}


	private def _callWebService(BioPortalTextMiningRequestParameters params) {

		HTTPBuilder http = new HTTPBuilder(SERVICE_URL)
        http.encoderRegistry = new EncoderRegistry(charset: MiscUtils.DEFAULT_ENCODING)
        if(domeoConfigAccessService.isProxyDefined()) {
            http.client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, domeoConfigAccessService.getProxyHttpHost());
        }

		http.request(groovyx.net.http.Method.POST) {
            requestContentType = ContentType.URLENC
			body = params.toMap()

			//This defines how the incoming content should be treated, regardless of the content type in the response
			//In this case, we are getting xml back. Change
			//contentType = 'text/plain'
			response.success = {resp, xml ->
                log.info("response status: ${resp.statusLine}");
                log.info("The response contenType is $resp.contentType");
				def parser = new BioPortalTextMiningResultsParser(xml)
				BioPortalAnnotatorResults results = parser.parse()
				return results
			}
            response.'404' = { resp ->
                log.error('Not found: ' + resp.getStatusLine())
                throw new ConnectorHttpResponseException(resp, 404, 'Service not found. The problem has been reported')
            }
            
            response.'503' = { resp ->
                log.error('Not available: ' + resp.getStatusLine())
                throw new ConnectorHttpResponseException(resp, 503, 'Service temporarily not available. Try again later.')
            }
			response.failure = {resp, xml ->
                log.error('Failure: ' + resp.getStatusLine())
				def errorObject = null
				if (resp.contentType.indexOf('xml') >= 0) {
					errorObject = new BioPortalTextMiningResultsParser(xml).parseError()
				}
				throw new AnnotatorException(resp, errorObject)
			}
		}
	}
	
	/**
	 * Service seems to choke on documents that are too large we are going to try and split up the text if need be
	 * Need to make sure that we don't split words so the miner can find them.
	 * @param textToAnnotate
	 * @return
	 */
	List<Range> rangesForText(String text) {
		List<Range> ranges = []
		if(text==null || text == ''){
			ranges << new EmptyRange()
			return ranges
		}
		String prefix = text.find(/\s*\S/)
		//log.trace("The beginning=*${prefix}*")
		String suffix = text.find(/\S\s*$/)
		//log.trace("The end=*${suffix}*")

		Integer startMiddle = prefix ? prefix.length() - 1 : 0
		Integer endMiddle = suffix ? text.length() - suffix.length() : 0
		//log.trace("The middle=*${text[startMiddle..endMiddle]}*")

		Integer currentStart = startMiddle


		def calcCurrentEnd = {innerCurrentStart ->
			int initialEnd = Math.min(innerCurrentStart + MAX_INITIAL_TEXT_LENGTH - 1, endMiddle)
			if (initialEnd == endMiddle) return initialEnd
			String stringToCheckForAdditionalWord = text[initialEnd + 1..endMiddle]
			def matcher = stringToCheckForAdditionalWord =~ /\w+\b/
			if (matcher.find()) {
				return Math.min(initialEnd + matcher.end() + 1, endMiddle)
			} else {
				return initialEnd
			}

		}
		Integer currentEnd = calcCurrentEnd(currentStart)
		int chunkIndex = 0

		while (currentStart <= endMiddle) {
			ranges.add(currentStart..currentEnd)
			currentStart = currentEnd + 1
			currentEnd = calcCurrentEnd(currentStart)

		}
		return ranges
	}


}
