package com.uzen.slimapk;

import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.FileSystems;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;

import com.uzen.slimapk.parser.ApkFileVisitor;
import com.uzen.slimapk.struct.AndroidConstants;
import com.uzen.slimapk.struct.ApkOptions;
import com.uzen.slimapk.struct.exception.SlimLog;
import com.uzen.slimapk.parser.FileXMLParser;
import com.uzen.slimapk.struct.ApkMeta;
import com.uzen.slimapk.utils.Utils;

public class SlimApk {
	protected ApkOptions Options;
	protected Path input, output, temp;
	protected FileXMLParser Parser = new FileXMLParser();
	
	protected static final SlimLog log = new SlimLog(SlimApk.class.getName());
	private static final StandardCopyOption COPY_OPTION = StandardCopyOption.COPY_ATTRIBUTES;
	
	private Map<String, String> List; //list of applications with versions
	
	public SlimApk(String input, String output, ApkOptions Options) throws IOException {
		this.input = FileSystems.getDefault().getPath(input);	
		if (Files.notExists(this.input)) 
			throw new IOException("No such file or directory: " + this.input);
		this.output = FileSystems.getDefault().getPath(output);
		this.Options = Options;		
		
		if(Options.isDebug()) {
			log.isLoggable();
			log.d("Input: {0}", this.input);		
			log.d("Output: {0}", this.output);
		}
		if(Options.getFilesList() != null) {	
			List = new HashMap<>();
		}
	}
	
	protected void createWorkingDir() throws IOException {
		Files.createDirectories(output);
		temp = Files.createTempDirectory("slim_");
	}
	
	protected Path createTempApp(final Path source) {
		if(!Options.isCache()){
			return source;
		}
		Path name = source.getFileName();
		Path slim_apk = temp.resolve(name);
		try{
			Files.copy(source, slim_apk, COPY_OPTION);
		} catch(IOException e) {
			log.e("Error occurred while copying to a temporary directory:\n {0}", e);
			slim_apk = source;
		}
		return slim_apk;
	}
	
   protected void addElementToList(String label, String version) {
		if(List != null) {
			List.put(label, version);
		}
	}
	
	public static void deleteDirectories(Path dir) throws IOException {
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

	protected void writeFileList() throws IOException {
		if(List == null) return;
		Path file = FileSystems.getDefault().getPath(Options.getFilesList());
		log.d("Record filelist to {0}", file);
	   Utils.writeToFile(file, queryString(List));
	}
	
	private static String queryString(Map <String, String> list) {
		StringBuilder sbuf = new StringBuilder();

		for (Map.Entry <String, String> entry: list.entrySet()) {
			sbuf.append(entry.getKey());
			sbuf.append(" #");
			sbuf.append(entry.getValue());
			sbuf.append('\n');
		}

		return sbuf.toString();
	}
}
