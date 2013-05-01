package org.mindinformatics.grails.domeo

import org.mindinformatics.grails.domeo.persistence.AnnotationSetIndex

class WebController { 
	
	def domeo = {

		// Url specified as parameter of the annotator url
		if(params.url && !params.lineageId) { 
			render (view:'domeo', model:[documentUrl: params.url])
			return;
		} else if(params.url && params.lineageId) {
			// If the lineageId is specified, the annotationId is ignored
			render (view:'domeo', model:[documentUrl: params.url, lineageId: params.lineageId])
			return;
		} else if(params.url && params.annotationId) {
			render (view:'domeo', model:[documentUrl: params.url, annotationId: params.annotationId])
			return;
		} else if(params.annotationId) {
			def url = AnnotationSetIndex.findByIndividualUri(params.annotationId).annotatesUrl;
			render (view:'domeo', model:[documentUrl: url, annotationId: params.annotationId])
			return;
		}
		render(view:'domeo');
	}
}
