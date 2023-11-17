package uia.utils.file;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class DirectoryScannerTest {

    @Test
    public void test() throws IOException {

        long t1 = System.currentTimeMillis();
        final ExecutorService exec = Executors.newFixedThreadPool(1);
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
        scanner.accept((p) -> {
            exec.submit(() -> {
                System.out.println(p.toString());
                try {
                    Thread.sleep(10);
                }
                catch (Exception ex) {

                }
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
}
