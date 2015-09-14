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

import com.uzen.slimapk.parser.ApkFileVisitor;
import com.uzen.slimapk.struct.AndroidConstants;
import com.uzen.slimapk.struct.ApkOptions;
import com.uzen.slimapk.struct.NameParserEntity;
import com.uzen.slimapk.struct.exception.ParserException;
import com.uzen.slimapk.utils.ResourceNote;

public class SlimApk implements Closeable {
	private ApkOptions Options;
	private Path input, output, temp;
	private NameParserEntity metaParser;
	
	private static final StandardCopyOption copyOption = StandardCopyOption.COPY_ATTRIBUTES;
	
	private Map<String, String> List; //list of applications with versions

	public SlimApk(String input, String output, ApkOptions Options) throws IOException {
		this.input = FileSystems.getDefault().getPath(input);
		this.output = FileSystems.getDefault().getPath(output);
		this.Options = Options;
		createWorkingDir();
		setType();
		initListFiles();
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

	private void initListFiles() {
		metaParser = new NameParserEntity(Options.getPattern());
		
		if(Options.getFilesList() != null) {	
			List = new HashMap<>();
		}
	}

	private Path unzipApk(Path apk) throws IOException {
		Path[] path = createWorkplace(apk);
		/*
		 path[0]: current apk file(tmp)
		 path[1]: output apk file
		*/
		Map<String, String> env = new HashMap<>();
		env.put("encoding", "UTF-8");
		
		/* replace spaces in directory names */
		
		String apkName, encodePath = path[0].toString().replaceAll("\\s+", "%20");	
		URI uri = URI.create("jar:file:" + encodePath);
		
		try (FileSystem ApkFileSystem = FileSystems.newFileSystem(uri, env)) {
			final Path root = ApkFileSystem.getPath("/");
			apkName = addElementToList(metaParser.parse(root, path[0]));
			path[1] = path[1].resolveSibling(apkName);
			deleteDirectories(path[1]);
			Files.createDirectories(path[1]);
			extractLibrary(root, path[1]);
		} catch (IOException e) {
			deleteDirectories(path[1]);
			return null;
		}
		apk = Files.move(path[0], path[1].resolve(apkName + AndroidConstants.EXTENSION));
		
		return apk;
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
	
   private String addElementToList(String[] data) {
		if(List != null) {
			List.put(data[0], data[1]); // label, version
		}
		
		return data[0];
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
		if (Files.exists(dir)) {
			Files.walkFileTree(dir, ApkLibDelParser);
		}
	}

	private void writeFileList() throws IOException {
		if(List == null) return;
		
		Path file = FileSystems.getDefault().getPath(Options.getFilesList());
	   ResourceNote.writeToFile(file, List);
	}
	
	public void parse() {
		try {
			ApkFileVisitor ApkParser = new ApkFileVisitor() {
				@Override
				public void actionFile(Path apk) {
					if(apk.toString().toLowerCase().endsWith(AndroidConstants.EXTENSION)){
						try {
							Path apkSlim = unzipApk(apk);				
							System.out.printf("save: %s\n", apkSlim.toString());
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

	@Override
   public String toString() {
		return "Input: " + input.toString() + "\nOutput: " + output.toString() +
			"\nArch: " + Options.getType();
	}

	@Override
	public void close() throws IOException {
		temp.toFile().deleteOnExit();
		writeFileList();
	}
}