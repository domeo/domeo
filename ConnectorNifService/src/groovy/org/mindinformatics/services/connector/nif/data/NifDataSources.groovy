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
package org.mindinformatics.services.connector.nif.data

/**
 * @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
 * 
 * Supported NIF data sources
 */
enum NifDataSources {

    NIF_ANTIBODIES("nif-0000-07730-1", "Antibody registry"), 
    NIF_REGISTRY("nlx_144509-1", "NIF registry"),
    NIF_INTEGRATED_ANIMAL("nif-0000-08137-1", "NIF integrated animal view")
    
    final String identifier, label
    
    /**
     * Full constructor
     * @param identifier    The NIF identifier for the resource
     * @param label         The NIF label for the resource
     */
    public NifDataSources(String identifier, String label) {
        this.identifier = identifier
        this.label = label
    }

    String identifier() { return identifier }
    String label() { return label }
}
