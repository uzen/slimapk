package com.uzen.slimapk;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.DirectoryStream;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.HashMap;
import java.net.URI;
import java.io.IOException;
import java.io.Closeable;

import com.uzen.slimapk.parser.NameParser;
import com.uzen.slimapk.parser.FileNameParser;
import com.uzen.slimapk.parser.FileXMLParser;
import com.uzen.slimapk.parser.ApkFileVisitor;
import com.uzen.slimapk.struct.AndroidConstants;
import com.uzen.slimapk.struct.ApkOptions;
import com.uzen.slimapk.struct.exception.ParserException;

public class SlimApk implements Closeable {
	private ApkOptions Options;
	private Path input, output, temp;
	private static final StandardCopyOption copyOption = StandardCopyOption.COPY_ATTRIBUTES;

	public SlimApk(String input, String output, ApkOptions Options) throws IOException {
		if (output == null) output = System.getProperty("user.dir");
		this.Options = Options;
		this.input = FileSystems.getDefault().getPath(input);
		this.output = FileSystems.getDefault().getPath(output);
		createWorkingDir();
		setType();
	}

	private void createWorkingDir() throws IOException {
		if (Files.notExists(input)) throw new IOException("No such file or directory: " + input.toString());
		Files.createDirectories(output);
		temp = Files.createTempDirectory("slim_");
	}

	private void setType() throws IOException {
		String abi, type = Options.getType();
		if (type == null) {
			throw new ParserException("Variable 'type' was null inside method getType.");
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

	private void unzipApk(Path apk) throws IOException {
		Path[] path = createWorkplace(apk);
		/*
		 path[0]: current apk file(tmp)
		 path[1]: output apk file
		*/
		Map < String, String > env = new HashMap < > ();

		URI uri = URI.create("jar:file:" + path[0].toAbsolutePath());

		try (FileSystem ApkFileSystem = FileSystems.newFileSystem(uri, env)) {
			final Path root = ApkFileSystem.getPath("/");
			String ApkName = getNameApk(Options.getPattern(), root, path[0]);
			path[1] = path[1].resolveSibling(ApkName);
			if (Files.exists(path[1])) deleteDirectories(path[1]);
			Files.createDirectories(path[1]);
			extractLibrary(root, path[1]);
			apk = path[1].resolve(ApkName + ".apk");
		} catch (IOException e) {
			System.err.println(e.getMessage());
			deleteDirectories(path[1]);
			return;
		}
		Files.move(path[0], apk);
		System.out.printf("save: %s\n", apk.toString());
	}

	/* get Name Apk of androidmanifest.xml or File Name */
	private static String getNameApk(String pattern, Path root, Path path) {

		NameParser ApkName;

		if (pattern != null) {
			ApkName = new FileNameParser(pattern);
			ApkName.setName(path);
		} else {
			ApkName = new FileXMLParser();
			ApkName.setName(root);
		}

		ApkName.parseData();
		String name = ApkName.getName();
		if (name == null) {
			name = path.getFileName().toString();
		}
		//if(list != null)
		//list.put(name, ApkName.getVersion());
		return name;
	}

	private Path[] createWorkplace(final Path source) throws IOException {

		Path[] path = new Path[2];

		Path name = source.getFileName();

		System.out.printf("ext.: %s\n", name);

		path[0] = Files.copy(source, temp.resolve(name), copyOption);

		if (path[0] == null) {
			throw new IOException("Error occurred while copying to a temporary directory");
		}

		if (Options.getKeepMode()) {
			path[1] = output.resolve(input.relativize(source));
		} else {
			path[1] = output.resolve(name);
		}

		return path;
	}

	private void extractLibrary(Path root, Path outPath) throws IOException {
		final Path libdir = root.resolve(AndroidConstants.LIB_PREFIX);
		outPath = outPath.resolve(AndroidConstants.LIB_DIR);
		if (Files.exists(libdir)) {
			if (parseLibrary(libdir, outPath)) {
				deleteDirectories(libdir);
			} else throw new ParserException("Incorrect application architecture");
		}
	}

	private boolean parseLibrary(Path libdir, Path outPath) throws IOException {

		final Path curPath = libdir.resolve(Options.getABI());
		final Path defPath = libdir.resolve(AndroidConstants.ABI_ARM);
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

	private static void deleteDirectories(Path dir) throws IOException {
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

	public void parseDirectories() {
		try {
			ApkFileVisitor ApkParser = new ApkFileVisitor() {
				@Override
				public void actionFile(Path file) {
					String name = file.toString();
					int c = name.length();
					if (c > 4) {
						name = name.substring(c - 4, c).toLowerCase();
						try {
							if (name.equals(".apk")) unzipApk(file);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			};
			Files.walkFileTree(input, ApkParser);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getConfig() {
		return "Input: " + input.toString() + "\nOutput: " + output.toString() +
			"\nArch: " + Options.getType();
	}

	@Override
	public void close() throws IOException {
		temp.toFile().deleteOnExit();
		System.out.println("Done.");
	}
}