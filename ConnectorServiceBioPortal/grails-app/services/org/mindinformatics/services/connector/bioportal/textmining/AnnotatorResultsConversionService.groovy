package org.mindinformatics.services.connector.bioportal.textmining

import java.text.SimpleDateFormat;
import java.util.regex.Matcher
import java.util.regex.Pattern


import org.apache.commons.validator.UrlValidator
import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.json.JSONObject
import org.mindinformatics.grails.domeo.persistence.UUID
import org.mindinformatics.grails.domeo.persistence.services.IOntology
import org.mindinformatics.services.connector.bioportal.textmining.BioPortalAnnotatorResults.Annotation
import org.mindinformatics.services.connector.bioportal.textmining.BioPortalAnnotatorResults.Concept
import org.mindinformatics.services.connector.bioportal.textmining.BioPortalAnnotatorResults.Context
import org.mindinformatics.services.connector.bioportal.textmining.BioPortalAnnotatorResults.MappingContext
import org.mindinformatics.services.connector.bioportal.textmining.BioPortalAnnotatorResults.MgrepContext


/**
* @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
*/
class AnnotatorResultsConversionService {
	static transactional =true
	def grailsApplication
	
	private static Integer MAX_LENGTH_PREFIX_AND_SUFFIX=50
	
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
	Map<String,JSONObject> urlToAnnotationTerm = (Map<String,JSONObject>)[:]
	
//	AnnotationModelPersistenceService annotationModelPersistenceService
//	AgentPerson cachedAgent
//	//def log
//	
	//TODO These URIs are just fabricated. Need to come up with different one
	static String PREFIX_SEMANTIC_TYPE_URI = 'http://rest.bioontology.org/obs/semanticTypes#'
	static String PREFIX_ONTOLOGY_URI = 'http://rest.bioontology.org/obs/ontologies#'
	
	
	AnnotatorResultsConversionService(){
		super()
	}
	
	JSONObject convert(String url, BioPortalAnnotatorResults ncboResults, BioPortalTextMiningRequestParameters params) {
		
		def ncboTextToAnnotate = params.textToAnnotate;
		JSONArray agents = new JSONArray();
		
		JSONObject annotationSet = new JSONObject();
		annotationSet.put(IOntology.generalId, UUID.uuid());
		annotationSet.put(IOntology.generalType, "ao:AnnotationSet"); 
		annotationSet.put(IOntology.generalLabel, "NCBO Annotator Results");
		annotationSet.put(IOntology.generalDescription, generateSetDescription(params));
		
		JSONObject ncboAnnotator = new JSONObject();
		ncboAnnotator.put(IOntology.generalId, "http://www.bioontology.org/wiki/index.php/Annotator_Web_service");
		ncboAnnotator.put(IOntology.generalType, "foafx:Software");
		ncboAnnotator.put(IOntology.generalLabel, "NCBO Annotator Web Service");
		ncboAnnotator.put("foafx:name", "NCBO Annotator Web Service");
		ncboAnnotator.put("foafx:version", "1.0");
		ncboAnnotator.put("foafx:build", "001");
		agents.add(0, ncboAnnotator);

		JSONObject bioportalConnector = new JSONObject();
		def connectorUrn = "urn:domeo:software:id:BioPortalConnector-0.1-001";
		bioportalConnector.put(IOntology.generalId, connectorUrn);
		bioportalConnector.put(IOntology.generalType, "foafx:Software");
		bioportalConnector.put(IOntology.generalLabel, "BioPortalConnector");
		bioportalConnector.put("foafx:name", "BioPortalConnector");
		bioportalConnector.put("foafx:version", "0.1");
		bioportalConnector.put("foafx:build", "001");
		agents.add(1, bioportalConnector);
		
		JSONObject domeo = new JSONObject();
		def domeoUrn = "urn:domeo:software:id:"+grailsApplication.metadata.'app.name'+"-"+grailsApplication.metadata.'app.version'+"-"+grailsApplication.metadata.'app.build';
		domeo.put(IOntology.generalId, domeoUrn);
		domeo.put(IOntology.generalType, "foafx:Software");
		domeo.put(IOntology.generalLabel, grailsApplication.metadata.'app.fullname');
		domeo.put("foafx:name", grailsApplication.metadata.'app.name');
		domeo.put("foafx:version", grailsApplication.metadata.'app.version');
		domeo.put("foafx:build", grailsApplication.metadata.'app.build');
		agents.add(2, domeo);
		
		// annotationSet.put("pav:lineageUri", "");
		// annotationSet.put("pav:createdBy", "");
		// annotationSet.put("pav:lastSavedOn", dateFormat.format(new Date()));
		
		annotationSet.put("pav:importedFrom", "http://www.bioontology.org/wiki/index.php/Annotator_Web_service");
		annotationSet.put("pav:importedBy", connectorUrn);
		annotationSet.put("pav:importedOn", dateFormat.format(new Date()));
		//annotationSet.put("pav:createdWith", domeoUrn);
		//annotationSet.put("pav:createdOn", dateFormat.format(new Date()));
		
		
		JSONObject permissions = new JSONObject();
		permissions.put("permissions:isLocked", "false");
		permissions.put("permissions:accessType", "urn:domeo:access:public");
		annotationSet.put("permissions:permissions", permissions);
		
		// Annotations
		def sortedAnnotations = (ncboResults.annotations as List)
		// Go through the ncbo results in the order the matches are found in the document
		sortedAnnotations.sort{a,b-> a.context.from.compareTo(b.context.from)}
		
		JSONArray annotations = new JSONArray();
		Integer previousNcboFromIdx = sortedAnnotations.empty ? null : sortedAnnotations[0].context.from
		Integer previousSelectorOffset = 0
		sortedAnnotations.each{Annotation ncboAnnotation ->
			Context ncboContext = ncboAnnotation.context
			boolean ncboResultsMoved = (ncboContext.from != previousNcboFromIdx);
			if(ncboResultsMoved) {
				previousSelectorOffset = previousSelectorOffset + 1
			}
			previousNcboFromIdx = ncboContext.from;
			
			JSONObject selector = findOrCreateAndSaveSelectorUsingStringSearch(ncboContext, ncboTextToAnnotate, previousSelectorOffset);
			if(selector) previousSelectorOffset = selector['ao:offset']
			
			JSONObject specificTarget = new JSONObject();
			specificTarget.put(IOntology.generalId, UUID.uuid());
			specificTarget.put(IOntology.generalType, IOntology.specificResource);
			specificTarget.put(IOntology.source, url);
			specificTarget.put(IOntology.selector, selector);
			
			JSONObject annotation = new JSONObject();
			annotation.put(IOntology.generalId, UUID.uuid());
			annotation.put(IOntology.generalType, IOntology.annotationQualifier);
			annotation.put(IOntology.generalLabel, "Qualifier");
			annotation.put("pav:createdBy", "http://www.bioontology.org/wiki/index.php/Annotator_Web_service");
			annotation.put("pav:createdOn", dateFormat.format(new Date()));
			annotation.put("pav:createdWith", domeoUrn);
			annotation.put("pav:importedFrom", "http://www.bioontology.org/wiki/index.php/Annotator_Web_service");
			annotation.put("pav:importedBy", connectorUrn);
			annotation.put("pav:lastSavedOn", dateFormat.format(new Date()));
			annotation.put("pav:previousVersion", "");
			annotation.put("pav:versionNumber", "");
			
			JSONArray targets = new JSONArray();
			targets.add(0,specificTarget )
			annotation.put(IOntology.hasTarget, targets);
			
			JSONObject term = createAnnotationTerm(ncboAnnotation.concept);
			JSONArray topics = new JSONArray();
			topics.add(0,term);
			annotation.put(IOntology.topic, topics);
			annotations.put(annotation);
			
			// TODO add score
		//			//Integer score = ncboAnnotation.score;
		//            // AnnotationTerm theTerm = createAnnotationTerm(ncboAnnotation.concept)
		//			//println "sortedAnnotations.each " + ncboAnnotation.concept
		//			
		//
		//			updateAnnotationItems(ourSelector,theTerm, ncboAnnotation.score)
		}
		
		annotationSet.put(IOntology.annotations, annotations);
		annotationSet.put(IOntology.agents, agents);
		return annotationSet;
	}
	
	private JSONObject findOrCreateAndSaveSelectorUsingStringSearch(Context ncboContext, String ncboTextToAnnotate, Integer start){
		String putativeExactMatch = null
		Map<String,Object> matchInfo = null
		if(ncboContext instanceof MgrepContext) {
			putativeExactMatch = ((MgrepContext)ncboContext).term.name
			Integer matchLength = ncboContext.to - ncboContext.from + 1
			if(matchLength != putativeExactMatch.size()){
				throw new RuntimeException("The length of the match in results from ${ncboContext.from} to ${ncboContext.to} does not match the length of the exact match ${putativeExactMatch}")
			}
			matchInfo =  searchForMatch(ncboTextToAnnotate, putativeExactMatch, start)
			//@TODO This case has not been tested. The results we have been working with have not included MappingContext
		}else if(ncboContext instanceof MappingContext){
			def allSynonyms = ((MappingContext)ncboContext).mappedConcept.synonyms as Set
			allSynonyms << ((MappingContext)ncboContext).mappedConcept.preferredName
			def matchSize = ((MappingContext)ncboContext).to -  ((MappingContext)ncboContext).from + 1
			def selectedSynonyms = allSynonyms.findAll{it.size() == matchSize}
			def earliestMatchOffset = Integer.MAX_VALUE
			def matchInfos = selectedSynonyms.collect{synonym-> matchInfo = searchForMatch(ncboTextToAnnotate, synonym, start)
				if(matchInfo)earliestMatchOffset = Math.min(matchInfo.offset,earliestMatchOffset)
				matchInfo
			}
			matchInfo = matchInfos.find{theMatchInfo-> theMatchInfo && theMatchInfo.offset == earliestMatchOffset}
		}

		if(!matchInfo){
			String termNotFoundMsg = null
			if(ncboContext instanceof MgrepContext){
				termNotFoundMsg = "MgrepContext.term.name=${ncboContext.term.name}"
			}else if(ncboContext instanceof MappingContext){
				termNotFoundMsg = "MappingContext.mappedConcept.preferredName=${ncboContext.mappedConcept.preferredName}"
			}
			log && log.warn("A selector could not be generated for annotation bean with from=${ncboContext.from},to=${ncboContext.to} ${termNotFoundMsg}")
			println "A selector could not be generated for annotation bean with from=${ncboContext.from},to=${ncboContext.to} ${termNotFoundMsg}"
			return null
		}
		
		JSONObject selector = new JSONObject();
		selector.put(IOntology.generalId, UUID.uuid());
		selector.put(IOntology.generalType, IOntology.selectorTextQuote);
		selector.put("pav:createdOn", dateFormat.format(new Date()));
		//println "Prefix: " + matchInfo.prefix
		selector.put(IOntology.selectorTextQuotePrefix, matchInfo.prefix);
		//println "Match: " + matchInfo.exact
		selector.put(IOntology.selectorTextQuoteMatch, matchInfo.exact);
		//println "Suffix: " + matchInfo.suffix
		selector.put(IOntology.selectorTextQuoteSuffix, matchInfo.suffix);
		selector.put("ao:offset", matchInfo.offset);
		return selector;		

//		if(theSelector.validate()){
//			//		PersistenceUtils.saveDomainObject(theSelector)
//			return theSelector
//		}else{
//			if (theSelector!=null) println "Prefix -" + theSelector.prefix + " - Exact -" + theSelector.exact + " - Suffix " + theSelector.suffix
//			else println 'Selector is null'
//			log && log.warn("A selector is not valid and will be skipped")
//			theSelector.errors.allErrors.each{error ->
//				if(error!=null) {
//					log && log.warn(error)
//				}
//			}
//			return null
//		}
	}
	
	private String normalizeTextSelectorSegment(String segment){
		String normalizedString =  (segment =~ /\s+/).replaceAll(' ')
		return normalizedString.replaceAll('\n', ' ')	
	}
	
	private def searchForMatch(String ncboTextToAnnotate, String putativeExactMatch, int start) {
		String matchRegex = putativeExactMatch.replaceAll(/\s+/,"\\\\s+")
		matchRegex = matchRegex.replaceAll("[)]", "\\\\)")
		matchRegex = matchRegex.replaceAll("[(]", "\\\\(")
		Pattern pattern = Pattern.compile("\\b${matchRegex}\\b", Pattern.MULTILINE | Pattern.CASE_INSENSITIVE)
		Matcher matcher = pattern.matcher(ncboTextToAnnotate)
		int startPos = -1
		int endPos = -1
		if (matcher.find(start)) {
			startPos = matcher.start()
			endPos = matcher.end()
			String exactMatch = ncboTextToAnnotate[startPos..endPos - 1]
			
			String prefix = null;
			if(startPos == 0) {
				prefix = '';
			} else {
			 	prefix = ncboTextToAnnotate.getAt([
					 Math.max(startPos - (MAX_LENGTH_PREFIX_AND_SUFFIX + 1), 0)..Math.max(0, startPos - 1)
				])
			}
			
			String suffix = null;
			if(Math.min(endPos, ncboTextToAnnotate.length() - 1)==Math.min(startPos + MAX_LENGTH_PREFIX_AND_SUFFIX, ncboTextToAnnotate.length()-1)) {
				suffix = "";
			} else {
				suffix = ncboTextToAnnotate.getAt([
					Math.min(endPos, ncboTextToAnnotate.length() - 1)..Math.min(startPos + MAX_LENGTH_PREFIX_AND_SUFFIX, ncboTextToAnnotate.length()-1)
				])
			}
			
			return ['offset':startPos,'prefix': prefix, 'exact': exactMatch, 'suffix': suffix]
		}else{
			return null
		}

	}
	
	private boolean looksLikeURL(String putativeURL){
		UrlValidator urlValidator = new UrlValidator()
		return urlValidator.isValid(putativeURL)
	}

	private def createAnnotationTerm(Concept ncboConcept) {
		String fullId =  ncboConcept.fullId
		//reject any Concept that does not have a fullId that looks like a URL

		if(!looksLikeURL(fullId)){
			log && log.warn("Not converting Concept in NCBO results with fullId=$fullId")
			return null
		}

		println "uri " + fullId
		println "label " + ncboConcept.preferredName
		println "category " + ncboConcept.semanticTypes[0].description

		if(urlToAnnotationTerm[fullId]) return urlToAnnotationTerm[fullId]
		//Look it up in the database by URI. If not there, create a new one
		JSONObject term = urlToAnnotationTerm.get(fullId);
		if(!term) {
			term = new JSONObject();
			term.put(IOntology.generalId, fullId);
			term.put(IOntology.generalLabel, ncboConcept.preferredName);
			term.put(IOntology.generalDescription, ncboConcept.synonyms);
			term.put('domeo:category', ncboConcept.semanticTypes[0].description);
			JSONObject source = new JSONObject();
			source.put(IOntology.generalId, PREFIX_ONTOLOGY_URI+ ncboConcept.ontology.localOntologyId);
			source.put(IOntology.generalLabel, ncboConcept.ontology.name);
			term.put(IOntology.generalSource, source);
		}
		
		urlToAnnotationTerm[fullId] = term
		term
	}
	
//	private PrefixPostfixTextSelector findOrCreateAndSaveSelectorUsingStringSearch(SourceDocument theDocument,Context ncboContext,String ncboTextToAnnotate,Integer start){
//		Integer offset = ncboContext.from
//		String putativeExactMatch = null
//		Map<String,Object> matchInfo = null
//		if(ncboContext instanceof MgrepContext) {
//			putativeExactMatch = ((MgrepContext)ncboContext).term.name
//			Integer matchLength = ncboContext.to - ncboContext.from + 1
//			if(matchLength != putativeExactMatch.size()){
//				throw new RuntimeException("The length of the match in results from ${ncboContext.from} to ${ncboContext.to} does not match the length of the exact match ${putativeExactMatch}")
//			}
//			matchInfo =  searchForMatch(ncboTextToAnnotate, putativeExactMatch, start)
//			//@TODO This case has not been tested. The results we have been working with have not included MappingContext
//		} else if(ncboContext instanceof MappingContext) {
//			def allSynonyms = ((MappingContext)ncboContext).mappedConcept.synonyms as Set
//			allSynonyms << ((MappingContext)ncboContext).mappedConcept.preferredName
//			def matchSize = ((MappingContext)ncboContext).to -  ((MappingContext)ncboContext).from + 1
//			def selectedSynonyms = allSynonyms.findAll{it.size() == matchSize}
//			def earliestMatchOffset = Integer.MAX_VALUE
//			def matchInfos = selectedSynonyms.collect{synonym-> matchInfo = searchForMatch(ncboTextToAnnotate, synonym, start)
//			if(matchInfo)earliestMatchOffset = Math.min(matchInfo.offset,earliestMatchOffset)
//				matchInfo
//			}
//			matchInfo = matchInfos.find{theMatchInfo-> theMatchInfo && theMatchInfo.offset == earliestMatchOffset}
//		}
//		
//		if(!matchInfo){
//			String termNotFoundMsg = null
//			if(ncboContext instanceof MgrepContext){
//				termNotFoundMsg = "MgrepContext.term.name=${ncboContext.term.name}"
//			} else if(ncboContext instanceof MappingContext) {
//				termNotFoundMsg = "MappingContext.mappedConcept.preferredName=${ncboContext.mappedConcept.preferredName}"
//		}
//		log && log.warn("A selector could not be generated for annotation bean with from=${ncboContext.from},to=${ncboContext.to} ${termNotFoundMsg}")
//		return null
//	}
	
	private String generateSetDescription(BioPortalTextMiningRequestParameters params){
		return 'Generated by NCBO text mining service using the following ontologies virtual IDs: ' + params.ontologiesToKeepInResult
	}

	//	
//	//TODO need to clarify if sourceId and sourceLabel refer to, say, an ontology, database, text mining process, etc.
//	//we will assume for now that it refers to the ontology 
//	AnnotationSet convert(SourceDocument document,AgentPerson theEditor,BioontologyTextMiningResults ncboResults, BioPortalTextMiningRequestParameters params){
//		//TODO we should probably not be creating the annotation set within the coverter. Leaving it here for now
//		AnnotationSet theSet = new AnnotationSet()
//		theSet.sourceDocument = document
//		theSet.editor = theEditor
//		theSet.isLocked = true
//        theSet.label = "NCBO Annotator Results"
//        theSet.creator = this.softwareAgent
//		theSet.description = this.generateSetDescription(ncboResults, params)
//		String ncboTextToAnnotate = ncboResults.textToAnnotate
//        Integer annotationIdx = 0
//        Integer missedAnnotationIdx = 0
//        Date creationDate = new Date()
//		
//		def updateAnnotationItems = {selector,term, score ->
//			//Note: we are only creating an annotation items when the "concept"  associated with the annotationBean has a URL. Othewise,
//			//The term will be null and we won't accept it.
//			//println "updateAnnotationItems> " term && selector
//			if(term && selector){
//              log.trace("Adding annotation ${++annotationIdx}")
//              theSet.annotations << createAnnotationItem(selector,term,theSet,creationDate, score)
//            }else{
//              log.trace("skipping adding annotation ${++missedAnnotationIdx}")
//            }
//		}
//       
//
//        def sortedAnnotations = (ncboResults.annotations as List)
//        
//        //Go through the ncbo results in the order the matches are found in the document
//        sortedAnnotations.sort{a,b-> a.context.from.compareTo(b.context.from)}
//        
//        Integer previousNcboFromIdx = sortedAnnotations.empty ? null : sortedAnnotations[0].context.from
//        Integer previousSelectorOffset = 0
//		sortedAnnotations.each{Annotation ncboAnnotation ->
//			//println "sortedAnnotations.each" + ncboAnnotation.context
//			Context ncboContext = ncboAnnotation.context
//			//PrefixPostfixTextSelector ourSelector = findOrCreateAndSaveSelectorUsingOffset(document,ncboContext, ncboTextToAnnotate)
//            boolean ncboResultsMoved = (ncboContext.from != previousNcboFromIdx)
//            previousNcboFromIdx = ncboContext.from
//            if(ncboResultsMoved){
//              previousSelectorOffset = previousSelectorOffset + 1
//            }
//            PrefixPostfixTextSelector ourSelector = findOrCreateAndSaveSelectorUsingStringSearch(document,ncboContext,ncboTextToAnnotate,previousSelectorOffset)
//            if(ourSelector)previousSelectorOffset = ourSelector.offset
//			//Integer score = ncboAnnotation.score;
//            // AnnotationTerm theTerm = createAnnotationTerm(ncboAnnotation.concept)
//			//println "sortedAnnotations.each " + ncboAnnotation.concept
//			AnnotationTerm theTerm = createAnnotationTerm(ncboAnnotation.concept);	
//            
//			updateAnnotationItems(ourSelector,theTerm, ncboAnnotation.score)
//		}
//		theSet
//	}
//	private String generateSetDescription(BioontologyTextMiningResults ncboResults, BioPortalTextMiningRequestParameters params){
//		return 'Generated from NCBO text mining service using the following ontologies virtual IDs: ' + params.ontologiesToKeepInResult
//	} 
//	
//	private def createAnnotationItem(PrefixPostfixTextSelector theSelector, AnnotationTerm term,AnnotationSet theSet,Date createdDate, Integer theScore) {
//		AnnotationItem newItem = new AnnotationItem()
//		newItem.identity {
//			//annotationSet = theSet
//			selector = theSelector
//			topic = term
//			score = theScore
//			creator = getSoftwareAgent()
//            createdOn = createdDate
//		}
//		println "createAnnotationItem> " + newItem.selector.exact;
//		newItem
//	}
//	
//	private Agent getSoftwareAgent(){
//		if(cachedAgent)return cachedAgent
//		AgentSoftware prototypeAgent = new AgentSoftware(name:'NCBO Annotator Web Service',homepage:'http://www.bioontology.org/wiki/index.php/Annotator_Web_service',ver:'1.0')
//		AgentSoftware cachedAgent = AgentSoftware.find(prototypeAgent)
//		if(!cachedAgent){
//			cachedAgent = PersistenceUtils.saveDomainObject(prototypeAgent)
//		}
//		cachedAgent
//	}
//	

//	private boolean looksLikeURL(String putativeURL){
//        UrlValidator urlValidator = new UrlValidator()
//        return urlValidator.isValid(putativeURL)
//	}
//    private PrefixPostfixTextSelector findOrCreateAndSaveSelectorUsingStringSearch(SourceDocument theDocument,Context ncboContext,String ncboTextToAnnotate,Integer start){
//		//Integer offset = ncboContext.from
//        String putativeExactMatch = null
//      Map<String,Object> matchInfo = null
//      if(ncboContext instanceof MgrepContext) {            
//         putativeExactMatch = ((MgrepContext)ncboContext).term.name
//         Integer matchLength = ncboContext.to - ncboContext.from + 1
//         if(matchLength != putativeExactMatch.size()){
//           throw new RuntimeException("The length of the match in results from ${ncboContext.from} to ${ncboContext.to} does not match the length of the exact match ${putativeExactMatch}")
//         }
//         matchInfo =  searchForMatch(ncboTextToAnnotate, putativeExactMatch, start)
//      //@TODO This case has not been tested. The results we have been working with have not included MappingContext
//      }else if(ncboContext instanceof MappingContext){
//        def allSynonyms = ((MappingContext)ncboContext).mappedConcept.synonyms as Set
//        allSynonyms << ((MappingContext)ncboContext).mappedConcept.preferredName
//        def matchSize = ((MappingContext)ncboContext).to -  ((MappingContext)ncboContext).from + 1
//        def selectedSynonyms = allSynonyms.findAll{it.size() == matchSize}
//        def earliestMatchOffset = Integer.MAX_VALUE
//        def matchInfos = selectedSynonyms.collect{synonym-> matchInfo = searchForMatch(ncboTextToAnnotate, synonym, start)
//          if(matchInfo)earliestMatchOffset = Math.min(matchInfo.offset,earliestMatchOffset)
//          matchInfo
//        }
//        matchInfo = matchInfos.find{theMatchInfo-> theMatchInfo && theMatchInfo.offset == earliestMatchOffset}
//      }
//
//      if(!matchInfo){
//
//        String termNotFoundMsg = null
//        if(ncboContext instanceof MgrepContext){
//          termNotFoundMsg = "MgrepContext.term.name=${ncboContext.term.name}"
//        }else if(ncboContext instanceof MappingContext){
//          termNotFoundMsg = "MappingContext.mappedConcept.preferredName=${ncboContext.mappedConcept.preferredName}"
//        }
//        log && log.warn("A selector could not be generated for annotation bean with from=${ncboContext.from},to=${ncboContext.to} ${termNotFoundMsg}")
//        return null
//      }
//
///*
//
//		String prefix = ncboTextToAnnotate.getAt([
//			Math.max(offset - (MAX_LENGTH_PREFIX_AND_SUFFIX +1), 0)..Math.max(0,ncboContext.from - 2)
//		])
//		String exact = ncboTextToAnnotate.getAt([
//			(ncboContext.from-1)..ncboContext.to-1
//		])
//		String suffix = ncboTextToAnnotate.getAt([
//			Math.min(ncboContext.to,ncboTextToAnnotate.length() - 1)..Math.min(ncboContext.to + MAX_LENGTH_PREFIX_AND_SUFFIX, ncboTextToAnnotate.length() - 1)
//		])
//*/		PrefixPostfixTextSelector theSelector = annotationModelPersistenceService.findPrePostTextSelector(theDocument.id,matchInfo.prefix,matchInfo.exact,matchInfo.suffix)
//		if(!theSelector){
//			theSelector = annotationModelPersistenceService.createNormalizedPrefixPostfixTextSelector(theDocument.id, matchInfo.exact, matchInfo.offset, matchInfo.prefix, matchInfo.suffix)
//		}
//		
//		
//		
//		if(theSelector.validate()){
//	//		PersistenceUtils.saveDomainObject(theSelector)
//			return theSelector
//		}else{
//			if (theSelector!=null) println "Prefix -" + theSelector.prefix + " - Exact -" + theSelector.exact + " - Suffix " + theSelector.suffix
//			else println 'Selector is null'
//			log && log.warn("A selector is not valid and will be skipped")
//			theSelector.errors.allErrors.each{error ->
//				if(error!=null) {
//					log && log.warn(error)
//				}
//			}
//			return null
//		}
//
//	}
//

//
//  private PrefixPostfixTextSelector findOrCreateAndSaveSelectorUsingOffset(SourceDocument theDocument,Context ncboContext,String ncboTextToAnnotate){
//		Integer offset = ncboContext.from
//		String prefix = ncboTextToAnnotate.getAt([
//			Math.max(offset - (MAX_LENGTH_PREFIX_AND_SUFFIX +1), 0)..Math.max(0,ncboContext.from - 2)
//		])
//		String exact = ncboTextToAnnotate.getAt([
//			(ncboContext.from-1)..ncboContext.to-1
//		])
//		String suffix = ncboTextToAnnotate.getAt([
//			Math.min(ncboContext.to,ncboTextToAnnotate.length() - 1)..Math.min(ncboContext.to + MAX_LENGTH_PREFIX_AND_SUFFIX, ncboTextToAnnotate.length() - 1)
//		])
//		PrefixPostfixTextSelector theSelector = annotationModelPersistenceService.findPrePostTextSelector(theDocument.id,prefix,exact,suffix)
//		if(!theSelector){
//			theSelector = annotationModelPersistenceService.createNormalizedPrefixPostfixTextSelector(theDocument.id, exact, offset, prefix, suffix)
//		}
//		if(theSelector.validate()){
//	//		PersistenceUtils.saveDomainObject(theSelector)
//			return theSelector
//		}else{
//			log & log.warn("A selector is not valid and will be skipped")
//			theSelector.errors.allErrors.each{error ->
//			  log && log.warn(error)
//			}
//			return null
//		}
//		
//	}
	
	
}
