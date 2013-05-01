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
package org.mindinformatics.domeo.grails.plugins.utils;

import java.text.SimpleDateFormat;

/**
* @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
*/
public class MiscUtils {

    public static final String DEFAULT_ENCODING = "UTF-8";
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss Z";
    
    public SimpleDateFormat getDefaultDateFormatter() {
        return new SimpleDateFormat(DEFAULT_DATE_FORMAT);
    }
}
