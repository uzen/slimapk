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
	private Path input, output, temp;
	private static final StandardCopyOption copyOption = StandardCopyOption.COPY_ATTRIBUTES;
	public SlimApk(String input, String output, ApkOptions Options) {
		if (output == null) output = System.getProperty("user.dir");
		this.input = Paths.get(input);
		this.output = Paths.get(output);
		this.Options = Options;
		try {
			createWorkingDir();
			setType();
		} catch (IOException e) {
			debug("No such file or directory:\n>> " + e.getMessage());
		} catch (NullPointerException d) {
			debug(d.getMessage());
		}
	}
	
	private void createWorkingDir() throws IOException {
		if (Files.notExists(input)) throw new IOException(input.toString());
		Files.createDirectories(output);
		temp = Files.createTempDirectory("slim_");
	}
	
	private void setType() throws NullPointerException {
		String abi, type = Options.getType();
		if (type == null) {
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
	private void unzipApk(Path apk) {
		Path[] path = createWorkplace(apk);
		/*
		 path[0]: current apk file(tmp)
		 path[1]: output apk file
		*/
		Map < String, String > env = new HashMap < > ();
		URI uri = URI.create("jar:file:" + path[0].toAbsolutePath());

		try (FileSystem ApkFileSystem = FileSystems.newFileSystem(uri, env)) {
			final Path root = ApkFileSystem.getPath("/");
			String ApkName, pattern = Options.getPattern();

			if (pattern != null || Options.getSpeedMode() == true) {
				ApkName = getNameApk(new FileNameParser(pattern), path[0]);
			} else {
				ApkName = getNameApk(new FileXMLParser(), root);
			}

			path[1] = path[1].resolveSibling(ApkName);
			if (Files.exists(path[1])) deleteDirectories(path[1]);
			Files.createDirectories(path[1]);
			extractLibrary(root, path[1]);
			path[1] = path[1].resolve(ApkName + ".apk");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				Files.move(path[0], path[1]);
			} catch (IOException e) {
				debug("No such file or directory:\n>> " + e.getMessage());
			}
		}
	}

	/* get Name Apk of androidmanifest.xml or File Name */
	private String getNameApk(NameParser ApkName, Path path) {
		ApkName.setName(path);
		ApkName.parseData();

		return ApkName.getName();
	}

	private Path[] createWorkplace(final Path source) {

		Path[] path = new Path[] {
			source, input.relativize(source)
		};

		path[0] = source.getFileName();

		if (Options.getKeepMode()) {
			path[1] = output.resolve(path[1]);
			System.out.printf("processing...\n>> %s\nto: %s\n", path[0], path[1]);
		} else {
			path[1] = output.resolve(path[0]);
			System.out.printf("processing...\n>> %s\n", path[0]);
		}

		path[0] = temp.resolve(path[0]);

		try {
			Files.copy(source, path[0], copyOption);
		} catch (IOException e) {
			debug("error occurred while copying to a temporary directory:\n" + e.getMessage());
		}
		return path;
	}

	private void extractLibrary(Path root, Path outPath) throws IOException {
		final Path libdir = root.resolve(AndroidConstants.LIB_PREFIX);

		outPath = outPath.resolve(AndroidConstants.LIB_DIR);
		if (parseLibrary(libdir, outPath)) deleteDirectories(libdir);
	}

	private boolean parseLibrary(Path root, Path outPath) throws IOException {

		final Path curPath = root.resolve(Options.getType());
		final Path defPath = root.resolve(AndroidConstants.ABI_ARM);
		final Path dirPath = (Files.exists(curPath)) ? curPath : (Files.exists(defPath)) ? defPath : null;

		if (dirPath == null) return false;
		Files.createDirectories(outPath);
		try (DirectoryStream < Path > stream = Files.newDirectoryStream(dirPath)) {
			for (Path file: stream) {
				Files.copy(file, outPath.resolve(file.getFileName().toString()), copyOption);
			}
		}
		return true;
	}

	private void deleteDirectories(Path dir) throws IOException {
		ApkFileVisitor ApkLibDelParser = new ApkFileVisitor() {
			@Override
			public void actionDir(Path dir) {
				try {
					Files.delete(dir);
				} catch (IOException e) {
					e.printStackTrace();
				}
			};
			@Override
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
				@Override
				public void actionFile(Path file) {
					String name = file.toString();
					int c = name.length();
					if (c > 4) {
						name = name.substring(c - 4, c).toLowerCase();
						if (name.equals(".apk")) unzipApk(file);
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
		temp.toFile().deleteOnExit();
		System.out.println("bye-bye;)");
	}
}