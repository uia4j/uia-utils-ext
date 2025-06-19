package uia.utils.file;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;

import org.junit.Test;

import uia.utils.file.FileTransfer.Progress;

public class WinShareFileTransferTest {

    @Test
    public void testMD5() throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(Files.readAllBytes(Paths.get("d:/temp/lyr.xls")));
        System.out.println(new BigInteger(md.digest()).toString(16));
    }

    @Test
    public void testPLP() throws Exception {
        WinShareFileTransfer trans = new WinShareFileTransfer("PLP_File");
        trans.login("10.170.110.85", "pg_plp_admin", "gMkVGw7G", "js");
        try (InputStream local = Files.newInputStream(Paths.get("d:/temp/P25530050001-A-P0SZ79PQ0003-Q2_FL1-IT.gbr"))) {
            trans.upload(local, "\\DSM_INPUT\\P25530050001-A-P0SZ79PQ0003-Q2_FL1-IT.gbr", true);
        }
        trans.close();
    }

    @Test
    public void testCP1() throws Exception {
        WinShareFileTransfer trans = new WinShareFileTransfer("CP_Test_DATA_Archive");
        trans.login("10.160.1.166", "ks015971_rw", "Qj015971", "HT-TECH");
        trans.list().forEach(System.out::println);
        trans.close();
    }

    @Test
    public void testCP2() throws Exception {
        WinShareFileTransfer trans = new WinShareFileTransfer("CP_Test_DATA_Archive");
        trans.setParentPath("\\CP测试1.69网盘数据备份");
        trans.login("10.160.1.166", "ks015971_rw", "Qj015971", "HT-TECH");
        trans.setZipEnabled(true);
        try (InputStream local = Files.newInputStream(Paths.get("d:/temp/lyr.xlsx"))) {
            trans.upload(local, "\\test1\\test2\\test3\\lyr.xls", true);
        }
        trans.close();
    }

    @Test
    public void testCopy1() throws IOException {
        WinShareFileTransfer trans = new WinShareFileTransfer("CP_Test_DATA_Archive");
        trans.setParentPath("\\CP测试1.69网盘数据备份");
        trans.login("10.160.1.166", "ks015971_rw", "Qj015971", "HT-TECH");
        trans.addListener(new Progress() {

            @Override
            public void start(String path) {
                System.out.println(path);
            }

            @Override
            public void uploading(String path, long count) {
                System.out.println("  " + count);
            }

            @Override
            public void error(String path, long count, Exception ex) {
                ex.printStackTrace();
            }

            @Override
            public void done(String path, long total) {
                System.out.println(path + ", total=" + total);
            }
        });

        WinShareScanner scanner = new WinShareScanner("CP_Test", "\\TESTER\\XAC70A13B\\S00G04.1-WCP");
        scanner.login("10.160.1.69", "cptest", "kscp@1234", "HT-TECH");
        scanner.run((share, p, smbFile) -> {
            trans.upload(smbFile.getInputStream(), p.replace("\\TESTER", ""), true);
            return true;
        });
    }

    @Test
    public void testCopy2() throws IOException {
        WinShareFileTransfer trans = new WinShareFileTransfer("CP_Test_DATA_Archive");
        trans.setParentPath("\\CP测试1.69网盘数据备份");
        trans.login("10.160.1.166", "ks015971_rw", "Qj015971", "HT-TECH");
        trans.addListener(new Progress() {

            @Override
            public void start(String path) {
                System.out.println(path);
            }

            @Override
            public void uploading(String path, long count) {
                System.out.println("  " + count);
            }

            @Override
            public void error(String path, long count, Exception ex) {
                ex.printStackTrace();
            }

            @Override
            public void done(String path, long total) {
                System.out.println(path + ", total=" + total);
            }
        });

        String syncFolder = "\\X0ZJ48BL0007\\HSA738-WCP";
        WinShareScanner scanner = new WinShareScanner("CP_Test", "\\TESTER" + syncFolder);
        scanner.login("10.160.1.69", "administrator", "bG~(7xd6Cyghw@H8", "HT-TECH");
        scanner.copyTo(trans, syncFolder);
    }

    @Test
    public void testMove1() throws IOException {
        WinShareFileTransfer trans = new WinShareFileTransfer("CP_Test_DATA_Archive");
        // trans.setZip(true);
        trans.setParentPath("\\CP测试1.69网盘数据备份");
        trans.login("10.160.1.166", "ks015971_rw", "Qj015971", "HT-TECH");

        String syncFolder = "\\EAP - Copy (2)";
        WinShareScanner scanner = new WinShareScanner("CP_Test", "\\TESTER" + syncFolder);
        scanner.login("10.160.1.69", "administrator", "bG~(7xd6Cyghw@H8", "HT-TECH");
        scanner.copyTo(trans, syncFolder);
    }
}
