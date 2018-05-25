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
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.TreeMap;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

/**
 * Simple HTTP client.
 *
 * @author Kyle K. Lin
 *
 */
public class SimpleHttpsClient extends AbstractHttpClient implements AutoCloseable {

    private int retryCount;

    private CloseableHttpClient client;

    /**
     * Constructor.
     * @param rootURL Root URL.
     */
    public SimpleHttpsClient(String rootURL) {
        this(rootURL, new TreeMap<String, String>());
    }

    /**
     * Constructor.
     * @param rootURL Root URL.
     * @param headersDefault Header information.
     */
    public SimpleHttpsClient(String rootURL, Map<String, String> headersDefault) {
        super(rootURL, headersDefault);
        this.retryCount = 3;
        this.client = createClient();
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
     * @param json JSON message.
     * @return Response.
     * @throws IOException IO failed.
     */
    public SimpleHttpClientResponse putJson(String action, String json) throws IOException {
        return putJson(action, json, null);
    }

    /**
     * Execute HTTP put.
     * @param action Action.
     * @param json JSON message.
     * @param headersOthers Header information.
     * @return Response.
     * @throws IOException IO failed.
     */
    public SimpleHttpClientResponse putJson(String action, String json, Map<String, String> headersOthers) throws IOException {
        StringEntity requestEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
        HttpPut putMethod = new HttpPut(this.rootURL + action);
        putMethod.setEntity(requestEntity);
        return execute(putMethod, headersOthers);
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

    private CloseableHttpClient createClient() {
        final SSLConnectionSocketFactory sslsf;
        try {
            sslsf = new SSLConnectionSocketFactory(
                    SSLContext.getDefault(),
                    NoopHostnameVerifier.INSTANCE);
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        final Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory> create()
                .register("http", new PlainConnectionSocketFactory())
                .register("https", sslsf)
                .build();

        final PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
        cm.setMaxTotal(100);

        HttpClientBuilder builder = HttpClients.custom()
                .setSSLSocketFactory(sslsf)
                .setConnectionManager(cm);

        return builder.build();
    }
}