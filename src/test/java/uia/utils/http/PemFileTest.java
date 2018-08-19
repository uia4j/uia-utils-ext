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

import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Test;

public class PemFileTest {

    @Test
    public void test() throws Exception {
        System.out.println(PemFile.x509("D:/skii/gtm/05.deploy/F5_172_20_100_245.pem").getVersion());
    }

    @Test
    public void test2() throws Exception {
        String file = "D:/skii/gtm/05.deploy/F5_172_20_100_245.pem";

        byte[] certAndKey = Files.readAllBytes(Paths.get(file));

        byte[] certBytes = PemFile.retriveCertification(certAndKey);
        java.security.cert.X509Certificate cert1 = PemFile.generateCertificateFromDER(certBytes);

        javax.security.cert.X509Certificate cert2 = PemFile.x509(file);

        System.out.println(cert1.getSerialNumber());
        System.out.println(cert2.getSerialNumber());
    }
}
