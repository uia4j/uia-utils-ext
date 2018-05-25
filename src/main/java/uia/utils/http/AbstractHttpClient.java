/*******************************************************************************
 * Copyright 2017 UIA
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package uia.utils.http;

import java.util.Base64;
import java.util.Map;
import java.util.TreeMap;

import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpUriRequest;

/**
 * Simple HTTP client.
 *
 * @author Kyle K. Lin
 *
 */
public abstract class AbstractHttpClient {

    protected final String rootURL;

    protected final Map<String, String> headersDefault;

    protected String user;

    protected String password;

    /**
     * Constructor.
     * @param rootURL Root URL.
     */
    public AbstractHttpClient(String rootURL) {
        this(rootURL, new TreeMap<String, String>());
    }

    /**
     * Constructor.
     * @param rootURL Root URL.
     * @param headersDefault Header information.
     */
    public AbstractHttpClient(String rootURL, Map<String, String> headersDefault) {
        this.rootURL = rootURL;
        this.headersDefault = headersDefault;
    }

    public void addDefaultProperty(String key, String value) {
        this.headersDefault.put(key, value);
    }

    public String getUser() {
        return this.user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    protected void applyBasicAuth(HttpUriRequest request) {
        if (this.user == null) {
            return;
        }

        String auth = this.password == null
                ? this.user + ":"
                : this.user + ":" + this.password;
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes());
        String authHeader = "Basic " + new String(encodedAuth);
        request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
    }
}
