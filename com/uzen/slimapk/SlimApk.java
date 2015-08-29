package com.uzen.slimapk;

import java.io.Closeable;
import java.nio.file.*;
import java.util.*;
import java.net.URI;
import java.io.IOException;
import com.uzen.slimapk.Parser.*;
import com.uzen.slimapk.Parser.struct.AndroidConstants;

public class SlimApk implements Closeable {
	
	private String TYPE;
	private String pattern;
	private Path input, output;
	private static Boolean sKeepDir = false;
	StandardCopyOption copyOption = StandardCopyOption.REPLACE_EXISTING;
	
	public SlimApk(String input, String output, String arch, Boolean keepDir, String pattern) {
		if(output == null) output = System.getProperty("user.dir");
		setWorkingDir(input, output);
		this.sKeepDir = keepDir;
		this.pattern = pattern;
		if (arch == null) arch = "arm";
		setType(arch);
	}
	
	public void setWorkingDir(String input, String output) {
		try {
			this.input = Paths.get(input);
			this.output = Paths.get(output);
			if (!Files.exists(this.input)) throw new IOException(input);
			if (!Files.exists(this.output)) {
				Files.createDirectories(this.output);
			}
			System.out.printf("Input file/directory: %s\nOutput file/directory: %s\n",
			this.input.toAbsolutePath(), this.output.toAbsolutePath());
		} catch (IOException e) {
			System.err.format("No such file or directory:\n %s%n", e.getMessage());
			System.exit(1);
		}
	}
	
	public void setType(String app_architecture) {
		switch (app_architecture) {
			case "arm64":
				this.TYPE = "arm64-v8a";
				break;
			case "x86":
				this.TYPE = "x86";
				break;
			default:
				this.TYPE = "armeabi-v7a";
		}
	}
	
	public void unzipApk(Path file) {
		
		Path apk = createWorkplace(file);
		Path apkHome = apk.getParent();
		String ApkName = null;

		Map < String, String > env = new HashMap < > ();
		URI uri = URI.create("jar:file:" + apk.toAbsolutePath());

		try (FileSystem ApkFileSystem = FileSystems.newFileSystem(uri, env)) {
			final Path root = ApkFileSystem.getPath("/");
			extractLibrary(root, apkHome);
			boolean smode = true;
			if (pattern != null || smode==true) {
				ApkName = getNameApk(new FileNameParser(pattern), apk);
			} else {
				ApkName = getNameApk(new FileXMLParser(), root);
			}			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
		try{
			Files.move(apk, apk.resolveSibling(ApkName + ".apk"), copyOption);
			Files.move(apkHome, apkHome.resolveSibling(ApkName), copyOption);
		} catch (IOException e) {
			e.printStackTrace();
		}
		}
	}
	private String getNameApk(NameParser ApkName, Path path){
		ApkName.setName(path);
		//get Name Apk of androidmanifest.xml or File Name
		ApkName.parseData();
		
		return ApkName.getName();
	}
	private Path createWorkplace(Path file) {

		Path ApkFileOut = file.getFileName();
		Path ApkDirOut = input.relativize(file);
		
		if(sKeepDir){
			ApkDirOut = ApkDirOut.resolveSibling(ApkFileOut);
			ApkDirOut = output.resolve(ApkDirOut);
			System.out.printf("processing...\n File: %s\n Directory: %s\n", ApkFileOut, ApkDirOut);
		} else {
			ApkDirOut = output.resolve(ApkFileOut);
			System.out.printf("processing...\n File: %s\n", ApkFileOut);
		}
		
		ApkFileOut = ApkDirOut.resolve(ApkFileOut);
		
		try{
			Files.createDirectories(ApkDirOut);
			Files.copy(file, ApkFileOut, copyOption);
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return ApkFileOut;
	}
	private void extractLibrary(Path root, Path outPath) throws IOException {
		Path libdir = root.resolve("lib");

		if (Files.exists(libdir)) {
			outPath = outPath.resolve(AndroidConstants.LIB_DIR);
			if (!Files.exists(outPath)) Files.createDirectories(outPath);
			parseLibrary(libdir, outPath);
			deleteLibrary(libdir);
		}

		System.out.println("Unpacked library is completed!");
	}

	private void parseLibrary(Path root, Path outPath) throws IOException {

		ApkFileVisitor ApkLibParser = new ApkFileVisitor(outPath) {
			public void actionDir(Path dir) {}
			public void actionFile(Path file) {
				try {
					Files.move(file, target.resolve(file.getFileName().toString()), copyOption);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};

		final Path curPath = root.resolve(TYPE);
		final Path defPath = root.resolve(AndroidConstants.TYPE);

		if (Files.exists(curPath)) {
			Files.walkFileTree(curPath, ApkLibParser);
		} else if (Files.exists(defPath)) {
			Files.walkFileTree(defPath, ApkLibParser);
		}
	}

	private void deleteLibrary(Path libdir) throws IOException {
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
		Files.walkFileTree(libdir, ApkLibDelParser);
	}

	public void unZipIt() {
		try {
			ApkFileVisitor ApkParser = new ApkFileVisitor() {
				public void actionDir(Path dir) {}
				private PathMatcher matcher = FileSystems.getDefault().getPathMatcher("regex:\\S+\\.apk");
				public void actionFile(Path file) {
					if (matcher.matches(file)) unzipApk(file);
				}
			};
			Files.walkFileTree(input, ApkParser);
			System.out.println("File search completed!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() throws IOException {
		System.out.println("bye-bye;)");
	}
}