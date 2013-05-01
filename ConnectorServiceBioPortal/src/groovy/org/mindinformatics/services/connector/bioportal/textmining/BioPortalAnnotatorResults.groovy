package org.mindinformatics.services.connector.bioportal.textmining


/**
 * @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
 */
class BioPortalAnnotatorResults{
	String accessedResource
	String accessDate
	List<Annotation> annotations = []
	String textToAnnotate

	public static class Annotation {
		Integer score
		Context context = new Context()
		Concept concept = new Concept()
	}
	static class Concept {
		String id
		String localConceptId
		Boolean isTopLevel
		String fullId
		String preferredName
		List<String> synonyms = []
		List<SemanticType> semanticTypes = []
		OntologyRecord ontology
	}
	static class SemanticType {
		String id
		String typeId
		String description
	}
	static class Context {
		String contextName
		Boolean isDirect
		Integer from
		Integer to
		boolean isMappingContext(){
			this.contextName == 'MAPPING'
		}
		boolean isMgrepContext(){
			this.contextName == 'MGREP'
		}
	}
	static class MappingContext extends Context {
		Concept mappedConcept = new Concept()
		String mappingType
	}
	static class IsAContext extends Context {
		Concept concept
	}
	static class MgrepContext extends Context {
		Term term
	}
	static class Term {
		String name
		String localConceptId
		Concept concept
		Boolean isPreferred
		String dictionaryId
	}
}