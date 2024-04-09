package uia.utils.file;

import java.nio.file.Files;

import org.junit.Test;

public class LinuxSFtpFileTransferTest {

	@Test
	public void testConnect() throws Exception {
		LinuxSFtpFileTransfer trans = new LinuxSFtpFileTransfer();
		trans.login("10.160.240.85", 22, "root", "tmC3M6DF988avsy%");
		trans.close();
	}

	@Test
	public void testUpload() throws Exception {
		LinuxSFtpFileTransfer trans = new LinuxSFtpFileTransfer();
		trans.login("10.160.240.85", 22, "root", "tmC3M6DF988avsy%");

		DirectoryScanner scanner = new DirectoryScanner("d:/workspace/htks/trek/03.code/uia-trek-enter/dist");
        scanner.run((p) -> {
    		String dst = "/opt/test" + scanner.relativeToParent(p, trans.isWindows());
        	try {
	        	trans.upload(Files.newInputStream(p), dst, true);
	        	return true;
        	}
        	catch(Exception ex) {
        		System.out.println(dst + " failed, " + ex.getMessage());
        		return false;
        	}
        });
        
        trans.close();
	}
}
