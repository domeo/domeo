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

import org.mindinformatics.services.connector.bioportal.textmining.BioPortalAnnotatorResults.Annotation
import org.mindinformatics.services.connector.bioportal.textmining.BioPortalAnnotatorResults.Concept
import org.mindinformatics.services.connector.bioportal.textmining.BioPortalAnnotatorResults.Context
import org.mindinformatics.services.connector.bioportal.textmining.BioPortalAnnotatorResults.IsAContext
import org.mindinformatics.services.connector.bioportal.textmining.BioPortalAnnotatorResults.MappingContext
import org.mindinformatics.services.connector.bioportal.textmining.BioPortalAnnotatorResults.MgrepContext
import org.mindinformatics.services.connector.bioportal.textmining.BioPortalAnnotatorResults.SemanticType
import org.mindinformatics.services.connector.bioportal.textmining.BioPortalAnnotatorResults.Term


/**
 * @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
 * @author Marco Ocana
 */
class BioPortalTextMiningResultsParser {
    def rootObject
    BioPortalTextMiningResultsParser(def xmlObject){
      super()
      this.rootObject = xmlObject
    }

    public BioPortalAnnotatorResults parse(){
      BioPortalAnnotatorResults results = new BioPortalAnnotatorResults()
      results.accessDate = rootObject.accessDate
      println "The class for data is ${rootObject.'data'.getClass()}"
      println "The class for annotation is ${rootObject.'data'.annotations.getClass()}"
      println "The class for annotationBean is ${rootObject.'data'.annotations.annotationBean.getClass()}"
      results.accessedResource = rootObject.accessedResource.text()
      
      results.textToAnnotate = rootObject.'data'.annotatorResultBean.parameters.textToAnnotate.text()
      
      //println  "This annotations are ${rootObject.data.annotations.annotationBean.text()}"
      println "The size of the annotations is ${rootObject.'data'.annotatorResultBean.annotations.children().size()}"

      Map<String,Concept>idToConcept = [:]
      Map<String,Term>localConceptIdToTerm = [:]
      Map<String,SemanticType>typeIdToSemanticType = [:]
      Map<String,OntologyRecord>localIdToOntology = [:]
      rootObject.data.annotatorResultBean.ontologies.ontologyUsedBean.each{xmlOntology->
        OntologyRecord onto= new OntologyRecord()
        onto.identity{
          localOntologyId = xmlOntology.localOntologyId.text()
          virtualOntologyId =xmlOntology.virtualOntologyId.text()
          name = xmlOntology.name.text()
          version = xmlOntology.version.text()

        }
        localIdToOntology[onto.localOntologyId] = onto

      }
      rootObject.data.annotatorResultBean.annotations.annotationBean.each{xmlAnnotation ->
        Annotation newAnnotation = new Annotation()
        newAnnotation.identity {
          score = Integer.parseInt(xmlAnnotation.score.text())
          def xmlConcept = xmlAnnotation.concept
          concept = this.parseConcept(xmlConcept,idToConcept,typeIdToSemanticType,localIdToOntology)

          def xmlContext = xmlAnnotation.context
          switch (xmlContext.'@class'.text()){
            case 'mappingContextBean': context = this.parseMappingContext(xmlContext,idToConcept,typeIdToSemanticType,localIdToOntology);break
            case "mgrepContextBean" : context = this.parseMgrepContext(xmlContext,localConceptIdToTerm,idToConcept);break
            case "isaContextBean" : context = this.parseIsaContextBean(xmlContext,idToConcept,typeIdToSemanticType,localIdToOntology);break
            default: context = new Context()
            
          }

      //List<String> synonyms = []
        }

		//println '**********************************************'
		//println newAnnotation.getConcept().getFullId()
		//println newAnnotation.getContext().from + "-" + newAnnotation.getContext().to
		
        results.annotations << newAnnotation
      
        
      }
      updateTerms(localConceptIdToTerm,idToConcept)

      return results
    }
  public BioPortalTextMiningError parseError(){
    BioPortalTextMiningError error = new BioPortalTextMiningError()
    error.identity {
      accessDate = rootObject.accessDate
      accessedResource = rootObject.accessedResource
      errorCode = (rootObject.errorCode.text() || rootObject.errorCode.text().trim() == '') ? null: Integer.parseInt(rootObject.errorCode.text())
      shortMessage = rootObject.shortMessage
      longMessage = rootObject.longMessage
    }
    error
  }
  private updateTerms(Map<String,Term> localConceptIdToTerm,Map<String,Concept>localConceptIdToConcept){
    localConceptIdToTerm.values().each{Term term->
      term.concept = localConceptIdToConcept[term.localConceptId]
    }
  }
    private MappingContext parseMappingContext(def xml,Map<String,Concept>idToConcept,typeIdToSemanticType,localIdToOntology){
       MappingContext newContext = new MappingContext()
       newContext.identity{
         isDirect = (xml.isDirect.text() == 'true')
         from = Integer.parseInt(xml.from.text())
         to = Integer.parseInt(xml.to.text())
         mappedConcept = this.parseConcept(xml.mappedConcept,idToConcept,typeIdToSemanticType,localIdToOntology)

         }
        return newContext
       }
  private IsAContext parseIsaContextBean(def xml,Map<String,Concept>idToConcept,typeIdToSemanticType,localIdToOntology){
       IsAContext newContext = new IsAContext()
       newContext.identity{
         isDirect = (xml.isDirect.text() == 'true')
         from = Integer.parseInt(xml.from.text())
         to = Integer.parseInt(xml.to.text())
         concept = this.parseConcept(xml.concept,idToConcept,typeIdToSemanticType,localIdToOntology)

         }
        return newContext
       }
    private MgrepContext parseMgrepContext(def xml,Map<String,Term> localConceptIdToTerm,Map<String,Term> localConceptIdToConcept){
      MgrepContext newContext = new MgrepContext()
       newContext.identity{
         isDirect = (xml.isDirect.text() == 'true')
         from = Integer.parseInt(xml.from.text())
         to = Integer.parseInt(xml.to.text())
         term = parseTerm(xml,localConceptIdToTerm)

         }
        return newContext
    }

  private Term parseTerm(def xml,Map<String,Term> localConceptIdToTerm) {
    def xmlTerm = xml.term
    String theLocalConceptId = xmlTerm.localConceptId
    Term cachedTerm = null
    //was trying to cache the terms by localConceptId
    //however, MGrepContext has different names on occassion
    //Therefore we are not reusing them anymore
    //cachedTerm = localConceptIdToTerm[theLocalConceptId]
    //if(cachedTerm)return cachedTerm

    Term term = new Term()    
    term.identity {
      name = xmlTerm.name
      localConceptId = xmlTerm.localConceptId
      isPreferred = xmlTerm.isPreferred.text() == '1'
      dictionaryId = xmlTerm.dictionaryId
    }
    localConceptIdToTerm[theLocalConceptId]= term
    return term
  }

  private Concept parseConcept(def xmlConcept,Map<String,Concept> idToConcept,Map<String,SemanticType>typeIdToSemanticType,Map<String,OntologyRecord>localIdToOntology){
      String theLocalConceptId = xmlConcept.localConceptId
      Concept cachedConcept =idToConcept[theLocalConceptId]
      if(cachedConcept)return cachedConcept

      Concept concept = new Concept()
      concept.identity{
            id = xmlConcept.id
            localConceptId = theLocalConceptId
            ontology = localIdToOntology[xmlConcept.localOntologyId.text()]
            isTopLevel = xmlConcept.isTopLevel.text() != '0'
            fullId = xmlConcept.fullId
            preferredName = xmlConcept.preferredName
            def theSynonyms =  xmlConcept.synonyms.string*.text()
            synonyms.addAll(theSynonyms)
            def xmlSemanticTypeBeans = xmlConcept.semanticTypes.semanticTypeBean
            semanticTypes.addAll(xmlSemanticTypeBeans.collect{xmlSemanticType->parseSemanticType(xmlSemanticType,typeIdToSemanticType)})
          }
      idToConcept[theLocalConceptId] = concept
      return concept
    }
  private SemanticType parseSemanticType(def xmlSemanticType,Map<String,SemanticType> typeIdToSemanticType){
    String theId = xmlSemanticType.id
    SemanticType cachedSemanticType = typeIdToSemanticType[theId]
    if(cachedSemanticType)return cachedSemanticType

    SemanticType semanticType = new SemanticType()
    semanticType.identity{
      id = theId
      typeId = xmlSemanticType.semanticType
      description = xmlSemanticType.description
    }
    typeIdToSemanticType[theId] = semanticType

    return semanticType
  }


  
}
