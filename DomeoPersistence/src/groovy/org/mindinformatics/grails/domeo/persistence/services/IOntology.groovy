package org.mindinformatics.grails.domeo.persistence.services

class IOntology {
	
	static final generalId = "@id";
	static final generalType = "@type";
	
	static final generalLabel = "rdfs:label";
	static final generalSource = "dct:source";
	static final generalDescription = "dct:description";
	
	static final agents = "domeo:agents";
	static final permissions = "permissions:permissions";
	
	static final content = "ao:body" ;
	static final topic = "ao:hasTopic" ;
	
	static final annotations = 'ao:item';
	static final target = "ao:annotatesResource";
	
	static final pavCreatedOn = "pav:createdOn";
	static final pavLastSavedOn = "pav:lastSavedOn";
	static final pavCreatedBy = "pav:createdBy";
	static final pavVersionNumber = "pav:versionNumber";
	
	static final annotationComment = "ao:Comment";
	static final annotationLinearComment = "ao:LinearComment";
	static final annotationDescription = "ao:Description";
	static final annotationHighlight = "ao:Highlight";
	static final annotationQualifier = "ao:Qualifier";
	static final annotationLink = "ao:Link";
	static final annotationPostIt = "ao:PostIt";
	static final annotationAntibody = "ao:AntibodyAnnotation";
	static final annotationCuration = "ao:Curation";
	static final annotationMicroPublication = "ao:MicroPublicationAnnotation";
	
	static final hasTarget = "ao:context";
	static final selector = "ao:hasSelector";
	static final source = "ao:hasSource" ;
	static final specificResource = "ao:SpecificResource";
	
	static final annotationSet = "ao:AnnotationSet";
	static final discussionSet = "domeo:DiscussionSet";
	static final selectorAnnotation = "domeo:AnnotationSelector";
	static final selectorImage = "domeo:ImageInDocumentSelector";
	static final selectorTarget = "domeo:TargetSelector";
	static final selectorTextQuote = "ao:PrefixSuffixTextSelector";
	static final selectorTextQuotePrefix = "ao:prefix";
	static final selectorTextQuoteMatch = "ao:exact";
	static final selectorTextQuoteSuffix = "ao:suffix";
	 
	static final displaySource = "domeo:displaySource" ;
}
