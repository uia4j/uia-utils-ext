package uia.utils.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;

public class DirectoryScanner {

    private final DirectoryStream<Path> ds;

    private final Set<String> ignore;

    private final Set<String> accept;

    DirectoryScanner(Path path, Set<String> ignore, Set<String> accept) throws IOException {
        this.ds = Files.newDirectoryStream(path);
        this.ignore = ignore;
        this.accept = accept;
    }

    public DirectoryScanner(String first, String... more) throws IOException {
        this.ds = Files.newDirectoryStream(Paths.get(first, more));
        this.ignore = new TreeSet<>();
        this.accept = new TreeSet<>();
    }

    public DirectoryScanner addIgnoreFolder(String name) {
        this.ignore.add(name);
        return this;
    }

    public DirectoryScanner addAcceptFileExt(String name) {
        this.accept.add(name);
        return this;
    }

    public boolean accept(Function<Path, Boolean> f) throws IOException {
        for (Path p : this.ds) {
            String pathName = p.toString();
            if (Files.isDirectory(p)) {
                String folder = pathName.substring(pathName.lastIndexOf(File.separator) + 1);
                if (folder.equals(".plugins")) {
                    System.out.println(folder);
                }
                if (this.ignore.contains(folder)) {
                    continue;
                }

                if (!new DirectoryScanner(p, this.ignore, this.accept).accept(f)) {
                    return false;
                }
            }
            else {
                String extName = pathName.substring(pathName.lastIndexOf(".") + 1);
                if (this.accept.isEmpty() || this.accept.contains(extName)) {
                    if (!f.apply(p)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
