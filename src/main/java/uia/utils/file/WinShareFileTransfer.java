package uia.utils.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.hierynomus.msdtyp.AccessMask;
import com.hierynomus.msfscc.fileinformation.FileIdBothDirectoryInformation;
import com.hierynomus.mssmb2.SMB2CreateDisposition;
import com.hierynomus.mssmb2.SMB2CreateOptions;
import com.hierynomus.mssmb2.SMB2ShareAccess;
import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.SmbConfig;
import com.hierynomus.smbj.auth.AuthenticationContext;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.session.Session;
import com.hierynomus.smbj.share.DiskShare;
import com.hierynomus.smbj.share.File;

public class WinShareFileTransfer implements FileTransfer {
	
	private final String shareFolder;
	
	private String ip;
	
	private String user;
	
	private String password;
	
	private String domain;

    private SMBClient client;

    private Connection connection;
 
    private Session session;
    
    private String parentPath;
    
    private Progress progress;
    
    private boolean zipEnabled;
    
    private int retry;
    
    public WinShareFileTransfer(String shareFolder) {
    	this.shareFolder = shareFolder;
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
		this.zipEnabled = false;
    }

    public String getParentPath() {
		return this.parentPath;
	}

	public void setParentPath(String parentPath) {
		this.parentPath = parentPath;
	}

	public int getRetry() {
		return this.retry;
	}

	public void setRetry(int retry) {
		this.retry = retry;
	}

	public boolean isZipEnabled() {
		return this.zipEnabled;
	}

	public void setZipEnabled(boolean zipEnabled) {
		this.zipEnabled = zipEnabled;
	}

	@Override
	public void addListener(Progress progress) {
		this.progress = progress;
	}

	@Override
	public boolean isWindows() {
		return true;
	}
    
    public List<String> list() {
		List<String> result = new ArrayList<>();
		try (DiskShare share = (DiskShare) session.connectShare(this.shareFolder)) {
			for (FileIdBothDirectoryInformation f : share.list("\\")) {
				result.add(f.getFileName());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
    
	public void login(String ip, String user, String pwd, String domain) throws IOException {
		this.ip = ip;
		this.user = user;
		this.password = pwd;
		this.domain = domain;
		connect();
    }

	@Override
	public boolean upload(InputStream local, String dst, boolean overwrite) throws IOException {
		int count = Math.max(0, this.retry);
		while(count >= 0) {
        	count--;
        	String[] folders = dst.split("\\\\"); 
        	String dstPath = this.parentPath == null ? "" : this.parentPath;
        	int size = 0;
	        try (final DiskShare share = (DiskShare) this.session.connectShare(this.shareFolder)) {
	        	// create folder
	        	for(int i = 0; i < folders.length - 1; i++) {
	        		if(folders[i].trim().length() > 0) {
	            		dstPath += ("\\" + folders[i]);
	            		if(!share.folderExists(dstPath)) {
	            			share.mkdir(dstPath);
	            		}
	        		}
	        	}
	        	dstPath += ("\\" + folders[folders.length - 1]);

	    		this.progress.start(dstPath);
        		if(this.zipEnabled) {
        			String fileName = dstPath.substring(dstPath.lastIndexOf("\\"));
        	   		final File dstFile = share.openFile(
            				dstPath + ".zip", 
            				EnumSet.of(AccessMask.FILE_ADD_FILE), 
            				null, 
            				SMB2ShareAccess.ALL,
            				SMB2CreateDisposition.FILE_OVERWRITE_IF, 
            				EnumSet.of(SMB2CreateOptions.FILE_NON_DIRECTORY_FILE, SMB2CreateOptions.FILE_WRITE_THROUGH));

        	   		OutputStream out = dstFile.getOutputStream();
        	   		ZipOutputStream zo = new ZipOutputStream(out);
    	    		zo.putNextEntry(new ZipEntry(fileName));
        	   		
    	    		byte[] buf = new byte[1024 * 1024];
    	    		int length = 0;
    			    while ((length = local.read(buf)) != -1) {
    			        zo.write(buf, 0, length);
    			    	size += length;
    		    		this.progress.uploading(dstPath, size);
    			    }
    			    zo.close();
    			    out.close();
    			    this.progress.done(dstPath, size);

    			    return true;
        		}
        		else {
        	   		final File dstFile = share.openFile(
            				dstPath, 
            				EnumSet.of(AccessMask.FILE_ADD_FILE), 
            				null, 
            				SMB2ShareAccess.ALL,
            				SMB2CreateDisposition.FILE_OVERWRITE_IF, 
            				EnumSet.of(SMB2CreateOptions.FILE_NON_DIRECTORY_FILE, SMB2CreateOptions.FILE_WRITE_THROUGH));
         
        	   		MessageDigest md = MessageDigest.getInstance("MD5");
    	    		OutputStream out = dstFile.getOutputStream();
    	    		byte[] buf = new byte[1024 * 1024];
    	    		int length = 0;
    			    while ((length = local.read(buf)) != -1) {
    			        out.write(buf, 0, length);
    			    	size += length;
    		    		this.progress.uploading(dstPath, size);
    		    		md.update(buf, 0, length);
    			    }
    			    out.flush();
    			    out.close();
    			    this.progress.done(dstPath, size);

    			    return verify(share, dstPath, new BigInteger(md.digest()).toString(16));
        		}
	        }
	        catch(Exception ex) {
        		this.progress.error(dstPath, size, ex);
	        	close();
	        	connect();
	        }
		}
		return false;
	}
	
	private void connect() throws IOException {
        SmbConfig smbConfig = SmbConfig.builder()
                .withTimeout(120, TimeUnit.SECONDS)
                .withSoTimeout(360, TimeUnit.SECONDS)
                .withReadTimeout(360, TimeUnit.SECONDS)
                .build();
        this.client = new SMBClient(smbConfig);
        this.connection = this.client.connect(ip);
        this.session = this.connection.authenticate(new AuthenticationContext(user, this.password.toCharArray(), domain));
	}
	
	private boolean verify(DiskShare share, String filePath, String md5) throws Exception {
		final File smbFile = share.openFile(
				filePath, 
                EnumSet.of(AccessMask.GENERIC_READ),
                null,
                SMB2ShareAccess.ALL,
                SMB2CreateDisposition.FILE_OPEN,
				null);
		
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] buf = new byte[1024 * 1024];
		int length = 0;
		InputStream is = smbFile.getInputStream();
		while((length = is.read(buf)) != -1) {
			md.update(buf, 0, length);
		}
		String _md5 = new BigInteger(md.digest()).toString(16);
		return md5.equals(_md5);
	}

	@Override
	public void close() {
		try {
			this.connection.close();
			this.client.close();
		}
		catch(Exception ex) {
		}
	}
}
