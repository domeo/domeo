package org.mindinformatics.domeo.persistence.services

import org.mindinformatics.grails.domeo.persistence.AnnotationSetGroup
import org.mindinformatics.grails.domeo.persistence.AnnotationSetIndex
import org.mindinformatics.grails.domeo.persistence.AnnotationSetPermissions
import org.mindinformatics.grails.domeo.persistence.BibliographicIdMapping
import org.mindinformatics.grails.domeo.persistence.BibliographicSetIndex
import org.mindinformatics.grails.domeo.persistence.LastAnnotationSetIndex
import org.mindinformatics.grails.domeo.persistence.LastBibliographicSetIndex
import org.mindinformatics.grails.domeo.persistence.SavingItemRecovery
import org.mindinformatics.grails.domeo.persistence.ServerApplicationException
import org.mindinformatics.grails.domeo.persistence.services.PersistenceUtils
import org.mindinformatics.grails.domeo.persistence.services.ServiceUtils

class TransactionalPersistenceService {

	static transactional = true
	
	AnnotationSetIndex saveAnnotationSetIndex(AnnotationSetIndex annotationSetIndex) throws ServerApplicationException{
		ServiceUtils.wrapThrowableWithApplicationServerException log,{
			//PersistenceUtils.checkForNulls(docDTO, ['url', ['firstAccessor', 'id']])
			//SourceDocument doc = annotationModelPersistenceService.createSourceDocument(docDTO.url, docDTO.title, docDTO.contents, docDTO.firstAccessor.id, docDTO.source)
			PersistenceUtils.saveDomainObjectWithoutIndexing(annotationSetIndex)
			annotationSetIndex;
		}
	}	
	
	LastAnnotationSetIndex saveLastAnnotationSetIndex(LastAnnotationSetIndex lastAnnotationSetIndex) throws ServerApplicationException {
		ServiceUtils.wrapThrowableWithApplicationServerException log,{
			//PersistenceUtils.checkForNulls(docDTO, ['url', ['firstAccessor', 'id']])
			//SourceDocument doc = annotationModelPersistenceService.createSourceDocument(docDTO.url, docDTO.title, docDTO.contents, docDTO.firstAccessor.id, docDTO.source)
			PersistenceUtils.saveDomainObjectWithoutIndexing(lastAnnotationSetIndex)
			lastAnnotationSetIndex;
		}
	}
	
	AnnotationSetPermissions saveAnnotationSetPermissions(AnnotationSetPermissions annotationSetPermissions) throws ServerApplicationException{
		ServiceUtils.wrapThrowableWithApplicationServerException log,{
			//PersistenceUtils.checkForNulls(docDTO, ['url', ['firstAccessor', 'id']])
			//SourceDocument doc = annotationModelPersistenceService.createSourceDocument(docDTO.url, docDTO.title, docDTO.contents, docDTO.firstAccessor.id, docDTO.source)
			PersistenceUtils.saveDomainObjectWithoutIndexing(annotationSetPermissions)
			annotationSetPermissions;
		}
	}
	
	AnnotationSetGroup saveAnnotationSetGroup(AnnotationSetGroup annotationSetGroup) throws ServerApplicationException{
		ServiceUtils.wrapThrowableWithApplicationServerException log,{
			//PersistenceUtils.checkForNulls(docDTO, ['url', ['firstAccessor', 'id']])
			//SourceDocument doc = annotationModelPersistenceService.createSourceDocument(docDTO.url, docDTO.title, docDTO.contents, docDTO.firstAccessor.id, docDTO.source)
			PersistenceUtils.saveDomainObjectWithoutIndexing(annotationSetGroup)
			annotationSetGroup;
		}
	}
	
	SavingItemRecovery saveSavingItemRecovery(SavingItemRecovery savingItemRecovery) throws ServerApplicationException{
		ServiceUtils.wrapThrowableWithApplicationServerException log,{
			//PersistenceUtils.checkForNulls(docDTO, ['url', ['firstAccessor', 'id']])
			//SourceDocument doc = annotationModelPersistenceService.createSourceDocument(docDTO.url, docDTO.title, docDTO.contents, docDTO.firstAccessor.id, docDTO.source)
			PersistenceUtils.saveDomainObjectWithoutIndexing(savingItemRecovery)
			savingItemRecovery;
		}
	}
	
	BibliographicSetIndex saveBibliographicSetIndex(BibliographicSetIndex bibliographicSetIndex) throws ServerApplicationException{
		ServiceUtils.wrapThrowableWithApplicationServerException log,{
			//PersistenceUtils.checkForNulls(docDTO, ['url', ['firstAccessor', 'id']])
			//SourceDocument doc = annotationModelPersistenceService.createSourceDocument(docDTO.url, docDTO.title, docDTO.contents, docDTO.firstAccessor.id, docDTO.source)
			PersistenceUtils.saveDomainObjectWithoutIndexing(bibliographicSetIndex)
			bibliographicSetIndex;
		}
	}
	
	BibliographicIdMapping saveBibliographicIdMapping(BibliographicIdMapping bibliographicIdMapping) throws ServerApplicationException{
		ServiceUtils.wrapThrowableWithApplicationServerException log,{
			//PersistenceUtils.checkForNulls(docDTO, ['url', ['firstAccessor', 'id']])
			//SourceDocument doc = annotationModelPersistenceService.createSourceDocument(docDTO.url, docDTO.title, docDTO.contents, docDTO.firstAccessor.id, docDTO.source)
			PersistenceUtils.saveDomainObjectWithoutIndexing(bibliographicIdMapping)
			bibliographicIdMapping;
		}
	}
	
	LastBibliographicSetIndex saveLastBibliographicSetIndex(LastBibliographicSetIndex lastBibliographicSetIndex) throws ServerApplicationException {
		ServiceUtils.wrapThrowableWithApplicationServerException log,{
			//PersistenceUtils.checkForNulls(docDTO, ['url', ['firstAccessor', 'id']])
			//SourceDocument doc = annotationModelPersistenceService.createSourceDocument(docDTO.url, docDTO.title, docDTO.contents, docDTO.firstAccessor.id, docDTO.source)
			PersistenceUtils.saveDomainObjectWithoutIndexing(lastBibliographicSetIndex)
			lastBibliographicSetIndex;
		}
	}
}
