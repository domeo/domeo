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
package org.mindinformatics.domeo.grails.plugins.utils

import java.net.InetSocketAddress;
import java.net.Proxy;

import org.apache.http.HttpHost

/**
 * @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
 * 
 * This class centralizes the access to the Domeo configuration properties.
 */
class DomeoConfigAccessService {

    public static final String DEFAULT_EMAIL_ADDRESS = "paolo.ciccarese@gmail.com"
    public static final String DEFAULT_EMAIL_LABEL = "-please define instance administrator email-"
    
    def grailsApplication;
    
    public String getDomeoConfigAdminMissingMessage() {
        return '--->>> Please define the administratin properties'
    }
    
    public boolean isProxyDefined() {
        //grailsApplication.config.domeo.proxy.ip.isEmpty() ?????
        return (grailsApplication.config.domeo.proxy.ip!=null && grailsApplication.config.domeo.proxy.ip.size()>0 
            && grailsApplication.config.domeo.proxy.port!=null && grailsApplication.config.domeo.proxy.port.size()>0);
    }
    
    public HttpHost getProxyHttpHost() {
        if(isProxyDefined()) {
            log.info("proxy: " + getProxyIp() + "-" + getProxyPort()) ;
            return new HttpHost(getProxyIp(), getProxyPort(), "http");
        } else throw new RuntimeException("No proxy defined, check with isProxyDefined() first.");
    }
    
    public Proxy getProxy() {
        if(isProxyDefined()) {
            log.info("proxy: " + getProxyIp() + "-" + getProxyPort()) ;
            return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(getProxyIp(), getProxyPort()));
        } else throw new RuntimeException("No proxy defined, check with isProxyDefined() first.");
    }
    
    public String getProxyIp() {
        return grailsApplication.config.domeo.proxy.ip;
    }
    
    public Integer getProxyPort() {
        return new Integer(grailsApplication.config.domeo.proxy.port);
    }
    
    public String getAdministratorName() {
        try {
            return (grailsApplication.config.domeo.admin.name);
        } catch (Exception e) {
            log.error("Administrator name not defined");
            return "-Please define instance administrator name-";
        }
    }
    
    public String getAdministratorOrganization() {
        try {
            return (grailsApplication.config.domeo.admin.organization);
        } catch (Exception e) {
            log.error("Administrator organization not defined");
            return "-Please define instance administrator organization-";
        }
    }
    
    public boolean doesAdministratorEmailAddressExists() {
        try {
            return (grailsApplication.config.domeo.admin.email.address && grailsApplication.config.domeo.admin.email.address.length()>0);
        } catch (Exception e) {
            log.error("Administrator email address not defined");
            return false;
        }
    }
    
    public String getAdministratorEmailAddress() {
        if(doesAdministratorEmailAddressExists()) {
            return grailsApplication.config.domeo.admin.email.to;
        } else {
            log.warn("Administrator email address not defined");
            return DEFAULT_EMAIL_ADDRESS
        }
    } 
    
    public String getAdministratorEmailLabel() {
        try {
            return grailsApplication.config.domeo.admin.email.display;
        } catch (Exception e) {
            log.error("Administrator email label not defined");
            return DEFAULT_EMAIL_LABEL;
        }
    }
}
