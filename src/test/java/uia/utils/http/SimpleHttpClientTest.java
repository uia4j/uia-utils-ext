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

import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.junit.Test;

/**
*
* Remember to setup JVM arguments
* -Djavax.net.ssl.trustStore=C:\Progra~1\Java\jre1.8.0_131\lib\security\cacerts
*
* @author gazer
*
*/
public class SimpleHttpClientTest {

    @Test
    public void test() throws Exception {
        final SSLConnectionSocketFactory sslsf;
        try {
            sslsf = new SSLConnectionSocketFactory(SSLContext.getDefault(),
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

        try (SimpleHttpClient client = new SimpleHttpClient("https://172.20.100.245/", builder)) {
            client.addDefaultProperty("Content-Type", "application/json");
            client.setUser("gazer2kanlin");
            client.setPassword("5683F5");
            SimpleHttpClientResponse resp = client.get("mgmt/tm/ltm/pool");
            System.out.println(resp.getStatusCode());
            System.out.println(resp.getContent("UTF-8"));
        }
    }
}
