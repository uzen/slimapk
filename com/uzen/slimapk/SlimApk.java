package com.uzen.slimapk;

import java.io.Closeable;
import java.nio.file.*;
import java.util.Map;
import java.util.HashMap;
import java.net.URI;
import java.io.IOException;
import com.uzen.slimapk.Parser.*;
import com.uzen.slimapk.struct.*;

public class SlimApk implements Closeable {
    
    private ApkOptions Options;
    private Path input, output;
    private StandardCopyOption copyOption = StandardCopyOption.REPLACE_EXISTING;

    private SlimApk(String input, String output) {
        if (output == null) output = System.getProperty("user.dir");
        this.input = Paths.get(input);
        this.output = Paths.get(output);
    }

    public SlimApk(String input, String output, ApkOptions Options){
        this(input, output);
        this.Options = Options;
        try{
            createWorkDir();
            setType();
        } catch (IOException e) {
            debug("No such file or directory:\n>> " + e.getMessage());
        } catch (NullPointerException d) {
        		debug(d.getMessage());
        } 
    }

    private void createWorkDir() throws IOException {
            if (Files.notExists(input)) throw new IOException(input.toString());
            if (Files.notExists(output)) {
                Files.createDirectories(output);
            }
    }

    private void setType() throws NullPointerException {
        String abi, type = Options.getType();
        if(type == null) {
            throw new NullPointerException("Variable 'type' was null inside method getType.");
        }
        switch (type) {
            case "arm":
                abi = AndroidConstants.ABI_ARMv7;
                break;
            case "arm64":
                abi = AndroidConstants.ABI_ARMv8;
                break;
            case "x86":
                abi = AndroidConstants.ABI_X86;
                break;
            default:
                abi = AndroidConstants.ABI_ARM;
        }
        Options.setABI(abi);
    }

    public void unzipApk(Path file) {

        Path apk = createWorkplace(file);
        Path nadir = apk.getParent();
        String ApkName = null, pattern = Options.getPattern();

        Map < String, String > env = new HashMap < > ();
        URI uri = URI.create("jar:file:" + apk.toAbsolutePath());

        try (FileSystem ApkFileSystem = FileSystems.newFileSystem(uri, env)) {
            final Path root = ApkFileSystem.getPath("/");
            extractLibrary(root, nadir);
            
            if (pattern != null || Options.getSpeedMode() == true) {
                ApkName = getNameApk(new FileNameParser(pattern), apk);
            } else {
                ApkName = getNameApk(new FileXMLParser(), root);
            }
            nadir = nadir.resolveSibling(ApkName);
            if(Files.exists(nadir)) deleteDirectories(nadir);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                Files.move(apk, apk.resolveSibling(ApkName + ".apk"), copyOption);
                Files.move(apk.getParent(), nadir, copyOption);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /* get Name Apk of androidmanifest.xml or File Name */
    private String getNameApk(NameParser ApkName, Path path) {
        ApkName.setName(path);
        ApkName.parseData();

        return ApkName.getName();
    }

    private Path createWorkplace(Path file) {

        Path ApkFileOut = file.getFileName();
        Path ApkDirOut = input.relativize(file);

        if (Options.getKeepMode()) {
            ApkDirOut = ApkDirOut.resolveSibling(ApkFileOut);
            ApkDirOut = output.resolve(ApkDirOut);
            System.out.printf("processing...\n >>%s\n to: %s\n", ApkFileOut, ApkDirOut);
        } else {
            ApkDirOut = output.resolve(ApkFileOut);
            System.out.printf("processing...\n >>%s\n", ApkFileOut);
        }

        ApkFileOut = ApkDirOut.resolve(ApkFileOut);

        try {
            Files.createDirectories(ApkDirOut);
            Files.copy(file, ApkFileOut, copyOption);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ApkFileOut;
    }
    
    private void extractLibrary(Path root, Path outPath) throws IOException {
        final Path libdir = root.resolve(AndroidConstants.LIB_PREFIX);

        if (Files.exists(libdir)) {
            outPath = outPath.resolve(AndroidConstants.LIB_DIR);
            if (!Files.exists(outPath)) Files.createDirectories(outPath);
            parseLibrary(libdir, outPath);
            deleteDirectories(libdir);
        }

    }

    private void parseLibrary(Path root, Path outPath) throws IOException {

        final Path curPath = root.resolve(Options.getType());
        final Path defPath = root.resolve(AndroidConstants.ABI_ARM);
        final Path dirPath = (Files.exists(curPath)) ? curPath: defPath;
        
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath)) {
            for (Path file: stream) {
           Files.move(file, outPath.resolve(file.getFileName().toString()), copyOption);
            }
        }
    }

    private void deleteDirectories(Path dir) throws IOException {
        ApkFileVisitor ApkLibDelParser = new ApkFileVisitor() {
            public void actionDir(Path dir) {
                try {
                    Files.delete(dir);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            };
            public void actionFile(Path file) {
                try {
                    Files.delete(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        Files.walkFileTree(dir, ApkLibDelParser);
    }

    public void unZipIt() {
        try {
            ApkFileVisitor ApkParser = new ApkFileVisitor() {
                public void actionFile(Path file) {
                	String name = file.toString();
                	int c;
                	if((c = name.length()) > 4) {
                		name = name.substring(c-4,c).toUpperCase();
                    	if (name.equals(".APK")) unzipApk(file);
                  }
                }
            };
            Files.walkFileTree(input, ApkParser);
            System.out.println("File search completed!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void debug(String msg) {
        System.out.println(msg);
        System.exit(1);
    }
    	
    @Override
    public void close() throws IOException {
        System.out.println("bye-bye;)");
    }
}