package uia.utils.http;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManagerFactory;

import org.junit.Test;

public class SslTest {

    @Test
    public void test() throws Exception {
        String CA_FILE = "D:/skii/gtm/05.deploy/F5_172_20_100_245.pem";

        FileInputStream fis = new FileInputStream(CA_FILE);
        X509Certificate ca = (X509Certificate) CertificateFactory
                .getInstance("X.509")
                .generateCertificate(new BufferedInputStream(fis));

        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(null, null);
        ks.setCertificateEntry(Integer.toString(1), ca);
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(ks);
    }
}
