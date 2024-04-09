package uia.utils.file;

import java.io.IOException;
import java.io.InputStream;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpProgressMonitor;

public class LinuxSFtpFileTransfer implements FileTransfer {
	
	private Session session;
	
	private ChannelSftp sftp;
	
	private Progress progress;
	
	public LinuxSFtpFileTransfer() {
		this.progress = new Progress() {

			@Override
			public void start(String path) {
			}

			@Override
			public void uploading(String path, long count) {
			}

			@Override
			public void error(String path, long count, Exception ex) {
			}

			@Override
			public void done(String path, long total) {
			}
		};
	}

	public void login(String host, int port, String user, String pwd) throws JSchException {
		JSch jsch = new JSch(); 
		this.session = jsch.getSession(user, host, port); 
		this.session.setConfig("StrictHostKeyChecking", "no");
		this.session.setPassword(pwd);
		this.session.connect(10000);
		this.sftp = (ChannelSftp)this.session.openChannel("sftp");
		this.sftp.connect(5000);
	}

	@Override
	public void addListener(Progress progress) {
		this.progress = progress;
	}

	@Override
	public boolean isWindows() {
		return false;
	}

	@Override
	public boolean upload(InputStream src, String dst, boolean overwrite) throws IOException {
		String path = dst.substring(0, dst.lastIndexOf("/"));
        try {
             this.sftp.stat(path);
        } catch(Exception e) {
        	mkdir(path.split("/"));
        }
        
        try {
			if(overwrite) {
				this.sftp.put(src, dst, new SftpProgressMonitor() {

					@Override
					public void init(int op, String src, String dest, long max) {
						LinuxSFtpFileTransfer.this.progress.start(dst);
					}

					@Override
					public boolean count(long count) {
						LinuxSFtpFileTransfer.this.progress.uploading(dst, count);
						return true;
					}

					@Override
					public void end() {
						LinuxSFtpFileTransfer.this.progress.done(dst, 0);
					}
				}, ChannelSftp.OVERWRITE);

			}
			else {
				this.sftp.put(src, dst, new SftpProgressMonitor() {

					@Override
					public void init(int op, String src, String dest, long max) {
						LinuxSFtpFileTransfer.this.progress.start(dst);
					}

					@Override
					public boolean count(long count) {
						LinuxSFtpFileTransfer.this.progress.uploading(dst, count);
						return true;
					}

					@Override
					public void end() {
						LinuxSFtpFileTransfer.this.progress.done(dst, 0);
					}
				});
			}
        	this.progress.done(dst, 0);
            return true;
		}
		catch(Exception ex) {
			this.progress.error(dst, 0, ex);
	        return false;
		}
        
    }
	
	@Override
	public void close() {
		try {
			this.sftp.exit();
			this.session.disconnect();
		}
		catch(Exception ex) {
			
		}
	}
	
	private void mkdir(String[] fs) throws IOException {
		try {
			this.sftp.cd("/");
	
			String path = "";
			for(int i = 0; i < fs.length; i++) {
				path += ("/" + fs[i]);
		        try {
		             this.sftp.stat(path);
		        } catch(Exception e) {
		        	 this.sftp.mkdir(fs[i]);
		        }
				this.sftp.cd(path);
			}
		}
		catch(Exception ex) {
			throw new IOException(ex);
		}
	}
}
