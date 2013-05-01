package org.mindinformatics.grails.domeo.persistence.services

import grails.validation.ValidationException

class PersistenceUtils {

	static <T extends Object> T saveDomainObjectWithoutIndexing(T domainObject) {
		if (!domainObject.save(flush: true)) {
			throw new ValidationException("$domainObject could not be saved", domainObject.errors)
		} else {
			return domainObject
		}
	}
	
	static void checkForNulls(def theObject, List beanPropertyPaths) throws RuntimeException{
		beanPropertyPaths.each {path ->
			def execPath = null
			if (!(path instanceof List)) {
				execPath = [path]
			} else {
				execPath = path
			}
			def currentObject = theObject
			execPath.each {propName ->
				currentObject = currentObject[propName]
				if (currentObject == null) throw new RuntimeException("Property path ${execPath.join('.')} in object of type ${theObject.class} cannot be null")
			}
		}
	}
}
