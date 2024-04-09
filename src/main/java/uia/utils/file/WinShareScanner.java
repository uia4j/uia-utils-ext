package uia.utils.file;

import java.io.IOException;
import java.io.InputStream;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import com.hierynomus.msdtyp.AccessMask;
import com.hierynomus.msfscc.FileAttributes;
import com.hierynomus.msfscc.fileinformation.FileIdBothDirectoryInformation;
import com.hierynomus.mssmb2.SMB2CreateDisposition;
import com.hierynomus.mssmb2.SMB2ShareAccess;
import com.hierynomus.protocol.commons.EnumWithValue.EnumUtils;
import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.SmbConfig;
import com.hierynomus.smbj.auth.AuthenticationContext;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.session.Session;
import com.hierynomus.smbj.share.DiskShare;
import com.hierynomus.smbj.share.File;

public class WinShareScanner {

	private final String shareFolder;
    
    private final String parentPath;
	
    private SMBClient client;

    private Connection connection;
 
    private Session session;

    private final Set<String> ignoreFolders;

    private final Set<String> acceptExt;

    public WinShareScanner(String shareFolder) throws IOException {
    	this(shareFolder, null);
    }

    public WinShareScanner(String shareFolder, String parentPath) throws IOException {
    	this.shareFolder = shareFolder;
    	this.parentPath = parentPath;
        this.ignoreFolders = new TreeSet<>();
        this.acceptExt = new TreeSet<>();
        
        this.ignoreFolders.add(".");
        this.ignoreFolders.add("..");
    }

    WinShareScanner(String shareFolder, String parentPath, Set<String> ignoreFolders, Set<String> acceptExt) throws IOException {
    	this.shareFolder = shareFolder;
    	this.parentPath = parentPath;
        this.ignoreFolders = ignoreFolders;
        this.acceptExt = acceptExt;

        this.ignoreFolders.add(".");
        this.ignoreFolders.add("..");
    }

    public WinShareScanner addIgnoreFolder(String name) {
        this.ignoreFolders.add(name);
        return this;
    }

    public WinShareScanner addAcceptFileExt(String name) {
        this.acceptExt.add(name);
        return this;
    }
    
	public void login(String ip, String user, String pwd, String domain) throws IOException {
        SmbConfig smbConfig = SmbConfig.builder()
                .withTimeout(120, TimeUnit.SECONDS)
                .withSoTimeout(180, TimeUnit.SECONDS)
                .withReadTimeout(180, TimeUnit.SECONDS)
                .build();
        this.client = new SMBClient(smbConfig);
        this.connection = this.client.connect(ip);
        this.session = this.connection.authenticate(new AuthenticationContext(user, pwd.toCharArray(), domain));
    }
    
    public boolean run(FileHandler handler) throws IOException {
		try (DiskShare share = (DiskShare) session.connectShare(this.shareFolder)) {
			if(this.parentPath == null) {
				scan(share, "\\", handler);
			}
			else {
				scan(share, this.parentPath, handler);
			}
		}
        return true;
    }
    
    public String relativeToParent(String filePath, boolean forWindows) {
    	if(this.parentPath != null) {
        	return forWindows 
        			? filePath.replace(this.parentPath, "").replace('/', '\\')
    		        : filePath.replace(this.parentPath, "").replace('\\', '/');
    	}
    	else {
        	return forWindows 
        			? filePath.replace('/', '\\')
    		        : filePath.replace('\\', '/');
    	}
    }
    
    public void copyTo(FileTransfer transfer, String dstRoot) throws IOException {
		run((share, srcPath, smbFile) -> {
    		String dstPath = dstRoot + relativeToParent(srcPath, transfer.isWindows());
    		try {
    			InputStream is = smbFile.getInputStream();
    			transfer.upload(is, dstPath, true);
    			is.close();
    			smbFile.close();
				return true;
			} catch (Exception e) {
				return false;
			}
		});
    }
    
    public void moveTo(FileTransfer transfer, String dstRoot) throws IOException {
		run((share, srcPath, smbFile) -> {
    		String dstPath = dstRoot + relativeToParent(srcPath, transfer.isWindows());
    		try {
    			InputStream is = smbFile.getInputStream();
    			transfer.upload(is, dstPath, true);
    			is.close();
    			smbFile.close();
    			share.rm(srcPath);
				return true;
			} catch (Exception e) {
				return false;
			}
		});
    }
	
    private void scan(DiskShare share, String path, FileHandler handler) throws IOException {
		for (FileIdBothDirectoryInformation f : share.list(path)) {
			if(EnumUtils.isSet(f.getFileAttributes(), FileAttributes.FILE_ATTRIBUTE_DIRECTORY)) {
				if(this.ignoreFolders.contains(f.getFileName())) {
					continue;
				}
				scan(share, path + "\\" + f.getFileName(), handler);
			}
			else {
                String extName = f.getFileName().substring(f.getFileName().lastIndexOf(".") + 1);
                if (this.acceptExt.isEmpty() || this.acceptExt.contains(extName)) {
                    String filePath = path + "\\" + f.getFileName();
    	    		final File smbFile = share.openFile(
    	    				filePath, 
    	                    EnumSet.of(AccessMask.GENERIC_READ),
    	                    null,
    	                    SMB2ShareAccess.ALL,
    	                    SMB2CreateDisposition.FILE_OPEN,
    	    				null);
    	    		handler.accept(share, filePath, smbFile);
                }

			}
		}
    }

    public static interface FileHandler {
    	
    	public boolean accept(DiskShare share, String filePath, File smbFile) throws IOException;
    	
    }
}
