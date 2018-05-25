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
