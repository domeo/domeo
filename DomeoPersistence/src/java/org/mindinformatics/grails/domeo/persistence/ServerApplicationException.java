package org.mindinformatics.grails.domeo.persistence;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ServerApplicationException extends RuntimeException implements Serializable {
    public ServerApplicationException(){
        super();
    }
    public ServerApplicationException(String theMessage){
        super(theMessage);
    }
    public ServerApplicationException(String theMessage,Throwable theCause){
        super(theMessage,theCause);
    }
    public ServerApplicationException(Throwable e){
        super(e);
    }

}
