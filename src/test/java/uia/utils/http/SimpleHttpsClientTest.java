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

import java.util.Map;

import org.junit.Test;

/**
 *
 * Remember to setup JVM arguments
 * -Djavax.net.ssl.trustStore=C:\Progra~1\Java\jre1.8.0_131\lib\security\cacerts
 *
 * @author gazer
 *
 */
public class SimpleHttpsClientTest {

    @Test
    public void testReadAllFiles() throws Exception {
        try (SimpleHttpsClient client = new SimpleHttpsClient("https://172.20.100.245/")) {
            client.addDefaultProperty("Content-Type", "application/json");
            client.setUser("admin");
            client.setPassword("1qaz@WSX");

            SimpleHttpClientResponse resp = client.get("mgmt/tm/ltm/rule");
            System.out.println(resp.getStatusCode());
            System.out.println(Json.format(resp.getContent("UTF-8")));
        }
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void testReadOneFile() throws Exception {
        try (SimpleHttpsClient client = new SimpleHttpsClient("https://172.20.100.245/")) {
            client.addDefaultProperty("Content-Type", "application/json");
            client.setUser("admin");
            client.setPassword("1qaz@WSX");

            //SimpleHttpClientResponse resp1 = client.get("mgmt/tm/ltm/rule");
            //System.out.println(resp1.getStatusCode());
            //System.out.println(Json.format(resp1.getContent("UTF-8")));

            SimpleHttpClientResponse resp2 = client.get("mgmt/tm/ltm/rule/TEST");
            System.out.println(resp2.getStatusCode());
            Map data = Json.toMap(resp2.getContent("UTF-8"));
            System.out.println(data.get("apiAnonymous"));

            /**
            client.putJson(
                    "mgmt/tm/ltm/rule/TEST",
                    Json.toString(new IRule("TEST", "# COMMENTS\nwhen HTTP_REQUEST {}")));
            
            client.postJson(
                    "mgmt/tm/ltm/rule",
                    Json.toString(new IRule("TEST9", "# COMMENTS\nwhen HTTP_REQUEST {}")));
            */
        }
    }

    public static class IRule {

        private String name;

        private String apiAnonymous;

        public IRule() {
        }

        public IRule(String name, String apiAnonymous) {
            this.name = name;
            this.apiAnonymous = apiAnonymous;
        }

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getApiAnonymous() {
            return this.apiAnonymous;
        }

        public void setApiAnonymous(String apiAnonymous) {
            this.apiAnonymous = apiAnonymous;
        }

    }
}
