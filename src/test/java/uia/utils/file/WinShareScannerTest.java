package uia.utils.file;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class WinShareScannerTest {

	@Test
    public void test166() throws IOException {
        final ExecutorService exec = Executors.newFixedThreadPool(4);
        WinShareScanner scanner = new WinShareScanner("CP_Test_DATA_Archive", "\\CP测试1.69网盘数据备份\\XAC7061B");
        scanner.login("10.160.1.166", "ks015971_rw", "Qj015971", "HT-TECH");
        scanner.run((share, p, is) -> {
            exec.submit(() -> {
                System.out.println(p);
            });
            return true;
        });

        exec.shutdown();
        try {
            if (!exec.awaitTermination(120, TimeUnit.SECONDS)) {
                exec.shutdownNow();
            }
        }
        catch (InterruptedException e) {
            exec.shutdownNow();
        }
    }

	@Test
    public void test69() throws IOException {
        WinShareScanner scanner = new WinShareScanner("CP_Test", "\\TESTER\\XAC70A13B\\S00G04.1-WCP");
        scanner.login("10.160.1.69", "cptest", "administrator", "HT-TECH");
        scanner.run((share, p, is) -> {
            System.out.println(p.replace("\\TESTER", ""));
            return true;
        });
    }
}
