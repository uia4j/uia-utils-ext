package uia.utils.file;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class DirectoryScannerTest {

	@Test
    public void testSimple() throws IOException {

        long t1 = System.currentTimeMillis();
        final ExecutorService exec = Executors.newFixedThreadPool(4);
        DirectoryScanner scanner = new DirectoryScanner("d:/workspace")
                .addIgnoreFolder(".git")
                .addIgnoreFolder(".metadata")
                .addIgnoreFolder(".plugins")
                .addIgnoreFolder(".project")
                .addIgnoreFolder(".settings")
                .addIgnoreFolder(".vscode")
                .addIgnoreFolder(".mars")
                .addIgnoreFolder(".eclipse")
                .addIgnoreFolder("target")
                .addIgnoreFolder("bin")
                .addIgnoreFolder("dist")
                .addIgnoreFolder("out")
                .addIgnoreFolder("node_modules");
        scanner.run((p) -> {
            exec.submit(() -> {
                System.out.println(p.toString());
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
        long t2 = System.currentTimeMillis();
        System.out.println(t2 - t1);
    }
    
	@Test
	public void testCopy() throws Exception {
		LinuxSFtpFileTransfer dstServ = new LinuxSFtpFileTransfer();
		dstServ.login("10.160.240.85", 22, "root", "tmC3M6DF988avsy%");

		DirectoryScanner srcScanner = new DirectoryScanner("d:/workspace/htks/trek/03.code/uia-trek-enter/dist");
		srcScanner.copyTo(dstServ, "/opt/test");

		dstServ.close();
	}
    
	@Test
	public void testMove() throws Exception {
		LinuxSFtpFileTransfer dstServ = new LinuxSFtpFileTransfer();
		dstServ.login("10.160.240.85", 22, "root", "tmC3M6DF988avsy%");

		DirectoryScanner srcScanner = new DirectoryScanner("d:/workspace/htks/trek/03.code/uia-trek-enter/dist");
		srcScanner.moveTo(dstServ, "/opt/test");

		dstServ.close();
	}
}
