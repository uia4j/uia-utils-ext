/*******************************************************************************
 * Copyright 2018 UIA
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
import java.util.TreeMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

/**
 * Simple HTTP client.
 *
 * @author Kyle K. Lin
 *
 */
public class SimpleHttpClient extends AbstractHttpClient implements AutoCloseable {

    private int retryCount;

    private CloseableHttpClient client;

    /**
     * Constructor.
     * @param rootURL Root URL.
     */
    public SimpleHttpClient(String rootURL) {
        this(rootURL, new TreeMap<String, String>());
    }

    /**
     * Constructor.
     * @param rootURL Root URL.
     * @param headersDefault Header information.
     */
    public SimpleHttpClient(String rootURL, Map<String, String> headersDefault) {
        super(rootURL, headersDefault);
        this.retryCount = 3;
        HttpClientBuilder builder = HttpClientBuilder.create();
        this.client = builder.build();
    }

    /**
     * Constructor.
     * @param rootURL Root URL.
     * @param builder Apache client builder.
     */
    public SimpleHttpClient(String rootURL, HttpClientBuilder builder) {
        this(rootURL, builder, new TreeMap<String, String>());
    }

    /**
     * Constructor.
     * @param rootURL Root URL.
     * @param builder Apache client builder.
     * @param headersDefault Header information.
     */
    public SimpleHttpClient(String rootURL, HttpClientBuilder builder, Map<String, String> headersDefault) {
        super(rootURL, headersDefault);
        this.retryCount = 3;
        this.client = builder.build();
    }

    @Override
    public void close() throws Exception {
        this.client.close();
    }

    /**
     * Get retry count.
     * @return Retry count.
     */
    public int getRetryCount() {
        return this.retryCount;
    }

    /**
     * Set retry count.
     * @param retryCount Retry count
     */
    public void setRetryCount(int retryCount) {
        this.retryCount = Math.max(1, retryCount);
    }

    /**
     * Execute HTTP get.
     * @param action Action.
     * @return Response.
     * @throws IOException IO failed.
     */
    public SimpleHttpClientResponse get(String action) throws IOException {
        return get(action, null);
    }

    /**
     * Execute HTTP get.
     * @param action Action.
     * @param headersOthers Header information.
     * @return Response.
     * @throws IOException IO failed.
     */
    public SimpleHttpClientResponse get(String action, Map<String, String> headersOthers) throws IOException {
        HttpGet getMethod = new HttpGet(this.rootURL + action);
        return execute(getMethod, headersOthers);
    }

    /**
     * Execute HTTP post.
     * @param action Action.
     * @param json JSON message.
     * @return Response.
     * @throws IOException IO failed.
     */
    public SimpleHttpClientResponse postJson(String action, String json) throws IOException {
        return postJson(action, json, null);
    }

    /**
     * Execute HTTP post.
     * @param action Action.
     * @param json JSON message.
     * @param headersOthers Header information.
     * @return Response.
     * @throws IOException IO failed.
     */
    public SimpleHttpClientResponse postJson(String action, String json, Map<String, String> headersOthers) throws IOException {
        StringEntity requestEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
        HttpPost postMethod = new HttpPost(this.rootURL + action);
        postMethod.setEntity(requestEntity);
        return execute(postMethod, headersOthers);
    }

    /**
     * Execute HTTP post.
     * @param action Action.
     * @param xml XML message.
     * @return Response.
     * @throws IOException IO failed.
     */
    public SimpleHttpClientResponse postXml(String action, String xml) throws IOException {
        return postXml(action, xml, null);
    }

    /**
     * Execute HTTP post.
     * @param action Action.
     * @param xml XML message.
     * @param headersOthers Header information.
     * @return Response.
     * @throws IOException IO failed.
     */
    public SimpleHttpClientResponse postXml(String action, String xml, Map<String, String> headersOthers) throws IOException {
        StringEntity requestEntity = new StringEntity(xml, ContentType.APPLICATION_XML);
        HttpPost postMethod = new HttpPost(this.rootURL + action);
        postMethod.setEntity(requestEntity);

        return execute(postMethod, headersOthers);
    }

    /**
     * Execute HTTP delete.
     * @param action Action.
     * @return Response.
     * @throws IOException IO failed.
     */
    public SimpleHttpClientResponse delete(String action) throws IOException {
        return delete(action, null);
    }

    /**
     * Execute HTTP delete.
     * @param action Action.
     * @param headersOthers Header information.
     * @return Response.
     * @throws IOException IO failed.
     */
    public SimpleHttpClientResponse delete(String action, Map<String, String> headersOthers) throws IOException {
        HttpDelete deleteMethod = new HttpDelete(this.rootURL + action);
        return execute(deleteMethod, headersOthers);
    }

    private SimpleHttpClientResponse execute(HttpUriRequest request, Map<String, String> headersOthers) throws IOException {
        if (this.headersDefault != null) {
            this.headersDefault.forEach(request::addHeader);
        }
        if (headersOthers != null) {
            headersOthers.forEach(request::addHeader);
        }
        applyBasicAuth(request);

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

        return new SimpleHttpClientResponse(response);
    }
}
