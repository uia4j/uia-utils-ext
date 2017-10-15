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

import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

public class HttpClient {

    private int retryCount;

    private String url;

    private CloseableHttpClient client;

    private Map<String, String> headersDefault;

    public HttpClient(String url) {
        this(url, (Map<String, String>) null);
    }

    public HttpClient(String url, Map<String, String> headersDefault) {
        this.retryCount = 3;
        this.url = url;
        HttpClientBuilder builder = HttpClientBuilder.create();
        this.client = builder.build();
        this.headersDefault = headersDefault;
    }

    public HttpClient(String url, HttpClientBuilder builder) {
        this(url, builder, null);
    }

    public HttpClient(String url, HttpClientBuilder builder, Map<String, String> headersDefault) {
        this.url = url;
        this.client = builder.build();
        this.headersDefault = headersDefault;
    }

    public void shutdown() throws IOException {
        this.client.close();
    }

    public int getRetryCount() {
        return this.retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public HttpClientResponse get(String action) throws IOException {
        return get(action, null);
    }

    public HttpClientResponse get(String action, Map<String, String> headersOthers) throws IOException {
        HttpGet getMethod = new HttpGet(this.url + action);
        return execute(getMethod, headersOthers);
    }

    public HttpClientResponse postJson(String action, String json) throws IOException {
        return postJson(action, json, null);
    }

    public HttpClientResponse postJson(String action, String json, Map<String, String> headersOthers) throws IOException {
        StringEntity requestEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
        HttpPost postMethod = new HttpPost(this.url + action);
        postMethod.setEntity(requestEntity);
        return execute(postMethod, headersOthers);
    }

    public HttpClientResponse postXml(String action, String json) throws IOException {
        return postXml(action, json, null);
    }

    public HttpClientResponse postXml(String action, String json, Map<String, String> headersOthers) throws IOException {
        StringEntity requestEntity = new StringEntity(json, ContentType.APPLICATION_XML);
        HttpPost postMethod = new HttpPost(this.url + action);
        postMethod.setEntity(requestEntity);

        return execute(postMethod, headersOthers);
    }

    public HttpClientResponse delete(String action) throws IOException {
        return delete(action, null);
    }

    public HttpClientResponse delete(String action, Map<String, String> headersOthers) throws IOException {
        HttpDelete deleteMethod = new HttpDelete(this.url + action);
        return execute(deleteMethod, headersOthers);
    }

    private HttpClientResponse execute(HttpUriRequest request, Map<String, String> headersOthers) throws IOException {
        if (this.headersDefault != null) {
            this.headersDefault.forEach(request::addHeader);
        }
        if (headersOthers != null) {
            headersOthers.forEach(request::addHeader);
        }

        HttpResponse response;
        int rc = this.retryCount;
        while (true) {
            rc--;
            try {
                response = this.client.execute(request);
                break;
            }
            catch (IOException ex) {
                if (rc < 1) {
                    throw ex;
                }
            }
        }

        return new HttpClientResponse(response);
    }
}
