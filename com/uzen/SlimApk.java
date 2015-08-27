package com.uzen;

import java.io.Closeable;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.net.URI;
import java.io.IOException;
import com.uzen.Parser.*;
import com.uzen.Parser.struct.AndroidConstants;

public class UnzipApk implements Closeable {
	private String TYPE;
	private String pattern;
	private Path input, output;
	private static Boolean sKeepDir = false;
	public static Boolean flashMode = false;
	public UnzipApk(String input, String output, String arch, Boolean keepDir, String pattern) {
		setWorkingDir(input, output);
		this.sKeepDir = keepDir;
		this.pattern = pattern;
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
	public void setType(String arch) {
		if (arch == null) arch = "arm";
		switch (arch) {
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
	public void unZipArch(Path apkFileName) {
		ParseName ApkName;
		//Files.createDirectories(this.output);
		//Files.copy(apkFileName,output.resolve(apkFileName));
		Map < String, String > env = new HashMap < > ();
		URI uri = URI.create("jar:file:" + apkFileName.toUri().getPath());

		try (FileSystem apkFileSystem = FileSystems.newFileSystem(uri, env)) {
			final Path root = apkFileSystem.getPath("/");

			extractLibrary(root, output);

			if (pattern == null && flashMode == false) {
				ApkName = new ParseFileXML();
			} else {
				ApkName = new ParseFileName(pattern);
			}
			ApkName.setName(apkFileName);
			//get Name Apk of androidmanifest.xml or File Name
			ApkName.parseData();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			//Files.move(path, path.resolveSibling(ApkName)));
		}
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

		ApkFileMethod method = new ApkFileMethod(outPath) {
			public void actionDir(Path dir) {};
			public void actionFile(Path file) {
				try {
					Files.move(file, target.resolve(file.getFileName().toString()), StandardCopyOption.REPLACE_EXISTING);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};

		final Path curPath = root.resolve(TYPE);
		final Path defPath = root.resolve(AndroidConstants.TYPE);

		if (Files.exists(curPath)) {
			Files.walkFileTree(curPath, new ApkFileVisitor(method));
		} else if (Files.exists(defPath)) {
			Files.walkFileTree(defPath, new ApkFileVisitor(method));
		}
	}

	private void deleteLibrary(Path libdir) throws IOException {
		ApkFileMethod method = new ApkFileMethod() {
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
		Files.walkFileTree(libdir, new ApkFileVisitor(method));
	}

	public void unZipIt() {
		try {
			ApkFileMethod method = new ApkFileMethod() {
				private PathMatcher matcher = FileSystems.getDefault().getPathMatcher("regex:\\S+\\.apk");
				public void actionDir(Path dir) {};
				public void actionFile(Path file) {
					if (matcher.matches(file)) unZipArch(file);
				}
			};
			Files.walkFileTree(input, new ApkFileVisitor(method));
			System.out.println("File search completed!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private abstract class ApkFileMethod {
		Path target;

		ApkFileMethod() {
			//nothing   	
		};

		ApkFileMethod(Path t) {
			target = t;
		}

		abstract void actionDir(Path dir);
		abstract void actionFile(Path file);
	}

	class ApkFileVisitor extends SimpleFileVisitor < Path > {

		private ApkFileMethod m;

		public ApkFileVisitor(ApkFileMethod method) {
			m = method;
		}@Override
		public FileVisitResult visitFile(Path path, BasicFileAttributes fileAttributes) {
			m.actionFile(path);
			return FileVisitResult.CONTINUE;
		}@Override
		public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
			m.actionDir(dir);
			return FileVisitResult.CONTINUE;
		}
	}

	@Override
	public void close() throws IOException {
		//убиваем ссылку на список 
	}

}