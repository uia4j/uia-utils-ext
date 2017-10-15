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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;

/**
 * HTTP client response.
 * 
 * @author Kyle K. Lin
 *
 */
public class HttpClientResponse {

    private final HttpResponse heepResponse;

    HttpClientResponse(HttpResponse httpResponse) {
        this.heepResponse = httpResponse;
    }

    /**
     * Get status code.
     * @return Status code.
     */
    public int getStatusCode() {
        return this.heepResponse.getStatusLine().getStatusCode();
    }

    /**
     * Get content.
     * @param charsetName Charset name.
     * @return Content
     * @throws IOException IO failed.
     */
    public String getContent(String charsetName) throws IOException {
        if (this.heepResponse.getEntity() == null) {
            return null;
        }

        InputStream inputStream = this.heepResponse.getEntity().getContent();

        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        // StandardCharsets.UTF_8.name() > JDK 7
        return result.toString(charsetName);
    }

    /**
     * Get content.
     * @return Content stream.
     * @throws IOException IO failed.
     */
    public InputStream getContentStream() throws IOException {
        if (this.heepResponse.getEntity() == null) {
            return null;
        }

        return this.heepResponse.getEntity().getContent();
    }
}
