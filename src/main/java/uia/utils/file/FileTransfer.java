package uia.utils.file;

import java.io.IOException;
import java.io.InputStream;

public interface FileTransfer {
	
	public boolean isWindows();

	public boolean upload(InputStream local, String dst, boolean overwrite) throws IOException;
	
	public void close();
	
	public void addListener(Progress progress);
	
	public static interface Progress {
		
		public void start(String path);

		public void uploading(String path, long size);

		public void error(String path, long size, Exception ex);

		public void done(String path, long total);
	}
}
