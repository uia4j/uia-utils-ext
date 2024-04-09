package uia.utils.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.TreeSet;

public class DirectoryScanner {
	
	private final String root;

    private final DirectoryStream<Path> ds;

    private final Set<String> ignoreFolders;

    private final Set<String> acceptExt;

    DirectoryScanner(Path path, Set<String> ignoreFolders, Set<String> acceptExt) throws IOException {
    	this.root = path.toString();
        this.ds = Files.newDirectoryStream(path);
        this.ignoreFolders = ignoreFolders;
        this.acceptExt = acceptExt;
    }

    public DirectoryScanner(String first, String... more) throws IOException {
    	Path path = Paths.get(first, more);
    	this.root = path.toString();
        this.ds = Files.newDirectoryStream(path);
        this.ignoreFolders = new TreeSet<>();
        this.acceptExt = new TreeSet<>();
    }

    public DirectoryScanner addIgnoreFolder(String folderName) {
        this.ignoreFolders.add(folderName);
        return this;
    }

    public DirectoryScanner addAcceptFileExt(String ext) {
        this.acceptExt.add(ext);
        return this;
    }

    public boolean run(FileHandler handler) throws IOException {
        for (Path p : this.ds) {
            String pathName = p.toString();
            if (Files.isDirectory(p)) {
                String folder = pathName.substring(pathName.lastIndexOf(File.separator) + 1);
                if (this.ignoreFolders.contains(folder)) {
                    continue;
                }

                if (!new DirectoryScanner(p, this.ignoreFolders, this.acceptExt).run(handler)) {
                    return false;
                }
            }
            else {
                String extName = pathName.substring(pathName.lastIndexOf(".") + 1);
                if (this.acceptExt.isEmpty() || this.acceptExt.contains(extName)) {
                    if (!handler.accept(p)) {
                        return false;
                    }
                }
            }
        }
     
        return true;
    }
    
    public void copyTo(FileTransfer dstServ, String dstRoot) throws IOException {
		run(srcPath -> {
    		String dstPath = dstRoot + relativeToParent(srcPath, dstServ.isWindows());
    		try {
    			dstServ.upload(Files.newInputStream(srcPath), dstPath, true);
				return true;
			} catch (Exception e) {
				return false;
			}
		});
    }
    
    public void moveTo(FileTransfer dstServ, String dstRoot) throws IOException {
		run(srcPath -> {
    		String dstPath = dstRoot + relativeToParent(srcPath, dstServ.isWindows());
    		try {
    			dstServ.upload(Files.newInputStream(srcPath), dstPath, true);
    			Files.delete(srcPath);
				return true;
			} catch (Exception e) {
				return false;
			}
		});
    }

    public String relativeToParent(Path path, boolean forWindows) {
    	return forWindows 
    			? path.toString().replace(this.root, "").replace('/', '\\')
		        : path.toString().replace(this.root, "").replace('\\', '/');
    }

    public static interface FileHandler {
    	
    	public boolean accept(Path path) throws IOException;
    }
}
