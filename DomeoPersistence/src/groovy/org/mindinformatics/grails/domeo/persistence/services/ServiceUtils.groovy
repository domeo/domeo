package org.mindinformatics.grails.domeo.persistence.services

import org.mindinformatics.grails.domeo.persistence.ServerApplicationException;

class ServiceUtils {
	public static <T extends Object> T wrapThrowableWithApplicationServerException(def log,Closure closure){
		try{
			return closure()
		}catch(Throwable e){
			log.error("Got a throwable executing service")
			log.error(e.getClass().getName()+":"+e.getMessage())
			log.error(e.printStackTrace())
			throw new ServerApplicationException(e.getClass().getName()+":"+e.getMessage(), e)
		}
	}
}
